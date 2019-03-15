# xml-extractor

<p align='right'>A <a href="http://www.swisspush.org">swisspush</a> project <a href="http://www.swisspush.org" border=0><img align="top"  src='https://1.gravatar.com/avatar/cf7292487846085732baf808def5685a?s=32'></a></p>

<p align='center'><img src='https://user-images.githubusercontent.com/692124/54452860-6512c800-4756-11e9-8e7f-6033f2b77c54.png' /></p>

## Data Pull :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

Iterable\<T> it = extractor.process(String xPath, Class\<T> clazz)


## Data Push :

extractor = new XmlDataExtractor(String xmlFileLocation, URL xsdLocation, Class jaxbFactoryClass)

extractor.addElementListener(String xPath, Class\<T> clazz, Consumer\<T> consumer)
  
extractor.process()
