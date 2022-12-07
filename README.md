<p align='right'>A <a href="https://developer.post.ch/">swisspost</a> project <a href="https://developer.post.ch/" border=0><img align="top"  src='https://avatars.githubusercontent.com/u/92710854?s=32&v=4'></a></p>

<p align='center'><img src='https://user-images.githubusercontent.com/692124/54452860-6512c800-4756-11e9-8e7f-6033f2b77c54.png' /></p>

# xml-extractor

## Data Pull :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

Iterable\<T> it = extractor.process(String xPath, Class\<T> clazz)


## Data Push :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

extractor.addElementListener(String xPath, Class\<T> clazz, Consumer\<T> consumer)
  
extractor.process()
