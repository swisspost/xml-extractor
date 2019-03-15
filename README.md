# xml-extractor

## For pulling :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

Iterable\<T> it = extractor.process(String xPath, Class\<T> clazz)


## For pushing :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

extractor.addElementListener(String xPath, Class\<T> clazz, Consumer\<T> consumer)
  
extractor.process()
