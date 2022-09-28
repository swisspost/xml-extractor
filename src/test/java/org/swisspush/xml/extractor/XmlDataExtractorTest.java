package org.swisspush.xml.extractor;

import ch.galinet.xmlns.example._1.Header;
import ch.galinet.xmlns.example._1.ObjectFactory;
import ch.galinet.xmlns.example._1.Person;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlDataExtractorTest {

    @Test
    public void processFile() throws JAXBException, XMLStreamException, FileNotFoundException {
        URL xsdLocation = this.getClass().getResource("/xsd/example.xsd");
        Assert.assertNotNull(xsdLocation);

        XmlDataExtractor extractor = new XmlDataExtractor("src/test/resources/example.xml", xsdLocation, ObjectFactory.class);

        Iterable<Person> iterable = extractor.process("/delivery/persons/person", Person.class);

        Flux<Person> flux = Flux.fromIterable(iterable);

        Assert.assertEquals(2, flux.count().block().longValue());
    }

    @Test
    public void processStream() throws JAXBException, XMLStreamException, FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/example.xml");
        XmlDataExtractor extractor = new XmlDataExtractor(fis, null, ObjectFactory.class);

        Iterable<Person> iterable = extractor.process("/delivery/persons/person", Person.class);

        Flux<Person> flux = Flux.fromIterable(iterable);

        Assert.assertEquals(2, flux.count().block().longValue());
    }

    @Test
    public void processPush() throws JAXBException, XMLStreamException, FileNotFoundException {
        AtomicInteger nbHeader = new AtomicInteger(0);
        AtomicInteger nbVoter = new AtomicInteger(0);
        XmlDataExtractor extractor = new XmlDataExtractor("src/test/resources/example.xml", null, ObjectFactory.class);

        extractor.addElementListener("/delivery/header", Header.class, h -> nbHeader.incrementAndGet());
        extractor.addElementListener("/delivery/persons/person", Person.class, v -> nbVoter.incrementAndGet());
        extractor.process();

        Assert.assertEquals(1, nbHeader.get());
        Assert.assertEquals(2, nbVoter.get());
    }
}