package ch.galinet.xml.extractor;

import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import org.codehaus.stax2.validation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public class XmlDataExtractor {
    private XMLStreamReader2 reader;
    private Unmarshaller unmarshaller;
    private CustomValidationHandler validationHandler;

    private Class iteratorClass;
    private String iteratorPath;

    private HashMap<String, Consumer> elements = new HashMap<>();
    private HashMap<String, Class> classes = new HashMap<>();


    private LinkedList<String> actualParsingThroughTags = new LinkedList<>();

    public XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass) throws XMLStreamException, JAXBException, FileNotFoundException {
        this(new FileInputStream(xmlFileLocation), xsdLocation, jaxbFactoryClass);
    }

    public XmlDataExtractor(InputStream input, URL xsdLocation, Class jaxbFactoryClass)
            throws XMLStreamException, JAXBException {

        unmarshaller = JAXBContext.newInstance(jaxbFactoryClass).createUnmarshaller();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        
        reader = Stax2ReaderAdapter.wrapIfNecessary(xmlInputFactory.createXMLStreamReader(input, "UTF-8"));
        if (xsdLocation != null) {
            XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
            XMLValidationSchema sv = sf.createSchema(xsdLocation);
            validationHandler = new CustomValidationHandler();
            reader.setValidationProblemHandler(validationHandler);
            reader.validateAgainst(sv);
        }
    }

    public <T> Iterable<T> process(String path, Class<T> clazz) {
        iteratorPath = path;
        iteratorClass = clazz;
        return new XmlDataExtractorIterable<>();
    }

    public <T> void addElementListener(String path, Class<T> clazz, Consumer<T> consumer) {
        elements.put(path, consumer);
        classes.put(path, clazz);
    }

    public void addValidationProblemListener(Consumer<XmlValidationProblem> consumer) {
        if (validationHandler != null) {
            validationHandler.addValidationProblemConsumer(consumer);
        } else {
            throw new RuntimeException("No XSD defined in constructor, unable to process any validation");
        }
    }

    public void process() throws JAXBException, XMLStreamException {
        process(null);
    }

    private Object process(String stopProcessingPath) throws JAXBException, XMLStreamException {
        while (reader.hasNext()) {
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                actualParsingThroughTags.add(reader.getLocalName());

                String actualPath = "/" + String.join("/", actualParsingThroughTags);

                boolean elementStep = elements.containsKey(actualPath);
                boolean stopProcessingStep = actualPath.equals(stopProcessingPath);

                if (elementStep || stopProcessingStep) {
                    actualParsingThroughTags.removeLast();

                    //When unmarshalling, we don't have to call the next() on reader because the unmarshaller already call it
                    Object o = unmarshaller.unmarshal(reader, elementStep ? classes.get(actualPath) : iteratorClass).getValue();
                    if (elementStep) {
                        elements.get(actualPath).accept(o);
                    }
                    if (stopProcessingStep) {
                        return o;
                    }
                } else {
                    reader.next();
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (actualParsingThroughTags.getLast().equals(reader.getLocalName())) {
                    actualParsingThroughTags.removeLast();
                }
                reader.next();
            } else {
                reader.next();
            }
        }
        return null;
    }


    class XmlDataExtractorIterable<T> implements Iterable<T> {
        XmlDataExtractorIterator it = new XmlDataExtractorIterator<>();

        @Override
        public Iterator<T> iterator() {
            return it;
        }
    }

    class XmlDataExtractorIterator<T> implements Iterator<T> {

        private Boolean hasNext = null;
        private T next = null;

        public XmlDataExtractorIterator() {
            //process until firstElement to be iterated
            hasNext();
        }

        @Override
        public boolean hasNext() {
            try {
                if (hasNext == null) {
                    next = (T) process(iteratorPath);
                    hasNext = (next != null);
                }
                return hasNext;
            } catch (JAXBException | XMLStreamException e) {
                throw new RuntimeException("Error while processing XMLStream", e);
            }
        }

        @Override
        public T next() {
            try {
                //ensure hasNext is called
                hasNext();
                return next;
            } finally {
                hasNext = null;
            }
        }
    }

    class CustomValidationHandler implements ValidationProblemHandler {

        private ArrayList<Consumer<XmlValidationProblem>> validationProblemConsumers = new ArrayList<>();

        public void addValidationProblemConsumer(Consumer<XmlValidationProblem> consumer) {
            validationProblemConsumers.add(consumer);
        }

        @Override
        public void reportProblem(XMLValidationProblem problem) throws XMLValidationException {
            if (validationProblemConsumers.size() > 0) {
                XmlValidationProblem dto = new XmlValidationProblem(problem.getLocation(),
                        problem.getMessage(), problem.getSeverity(), problem.getType());
                validationProblemConsumers.forEach(c -> c.accept(dto));
            } else {
                throw XMLValidationException.createException(problem);
            }
        }
    }
}
