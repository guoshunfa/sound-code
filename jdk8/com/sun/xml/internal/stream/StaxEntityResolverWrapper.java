package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StaxEntityResolverWrapper {
   XMLResolver fStaxResolver;

   public StaxEntityResolverWrapper(XMLResolver resolver) {
      this.fStaxResolver = resolver;
   }

   public void setStaxEntityResolver(XMLResolver resolver) {
      this.fStaxResolver = resolver;
   }

   public XMLResolver getStaxEntityResolver() {
      return this.fStaxResolver;
   }

   public StaxXMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
      Object object = null;

      try {
         object = this.fStaxResolver.resolveEntity(resourceIdentifier.getPublicId(), resourceIdentifier.getLiteralSystemId(), resourceIdentifier.getBaseSystemId(), (String)null);
         return this.getStaxInputSource(object);
      } catch (XMLStreamException var4) {
         throw new XNIException(var4);
      }
   }

   StaxXMLInputSource getStaxInputSource(Object object) {
      if (object == null) {
         return null;
      } else if (object instanceof InputStream) {
         return new StaxXMLInputSource(new XMLInputSource((String)null, (String)null, (String)null, (InputStream)object, (String)null));
      } else if (object instanceof XMLStreamReader) {
         return new StaxXMLInputSource((XMLStreamReader)object);
      } else {
         return object instanceof XMLEventReader ? new StaxXMLInputSource((XMLEventReader)object) : null;
      }
   }
}
