# xml-extractor

## Data Pull :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

Iterable\<T> it = extractor.process(String xPath, Class\<T> clazz)


## Data Push :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

extractor.addElementListener(String xPath, Class\<T> clazz, Consumer\<T> consumer)
  
extractor.process()
