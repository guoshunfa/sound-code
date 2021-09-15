package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.ExternalSubsetResolver;
import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class EntityResolver2Wrapper implements ExternalSubsetResolver {
   protected EntityResolver2 fEntityResolver;

   public EntityResolver2Wrapper() {
   }

   public EntityResolver2Wrapper(EntityResolver2 entityResolver) {
      this.setEntityResolver(entityResolver);
   }

   public void setEntityResolver(EntityResolver2 entityResolver) {
      this.fEntityResolver = entityResolver;
   }

   public EntityResolver2 getEntityResolver() {
      return this.fEntityResolver;
   }

   public XMLInputSource getExternalSubset(XMLDTDDescription grammarDescription) throws XNIException, IOException {
      if (this.fEntityResolver != null) {
         String name = grammarDescription.getRootName();
         String baseURI = grammarDescription.getBaseSystemId();

         try {
            InputSource inputSource = this.fEntityResolver.getExternalSubset(name, baseURI);
            return inputSource != null ? this.createXMLInputSource(inputSource, baseURI) : null;
         } catch (SAXException var6) {
            Exception ex = var6.getException();
            if (ex == null) {
               ex = var6;
            }

            throw new XNIException((Exception)ex);
         }
      } else {
         return null;
      }
   }

   public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
      if (this.fEntityResolver != null) {
         String pubId = resourceIdentifier.getPublicId();
         String sysId = resourceIdentifier.getLiteralSystemId();
         String baseURI = resourceIdentifier.getBaseSystemId();
         String name = null;
         if (resourceIdentifier instanceof XMLDTDDescription) {
            name = "[dtd]";
         } else if (resourceIdentifier instanceof XMLEntityDescription) {
            name = ((XMLEntityDescription)resourceIdentifier).getEntityName();
         }

         if (pubId == null && sysId == null) {
            return null;
         } else {
            try {
               InputSource inputSource = this.fEntityResolver.resolveEntity(name, pubId, baseURI, sysId);
               return inputSource != null ? this.createXMLInputSource(inputSource, baseURI) : null;
            } catch (SAXException var8) {
               Exception ex = var8.getException();
               if (ex == null) {
                  ex = var8;
               }

               throw new XNIException((Exception)ex);
            }
         }
      } else {
         return null;
      }
   }

   private XMLInputSource createXMLInputSource(InputSource source, String baseURI) {
      String publicId = source.getPublicId();
      String systemId = source.getSystemId();
      InputStream byteStream = source.getByteStream();
      Reader charStream = source.getCharacterStream();
      String encoding = source.getEncoding();
      XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, baseURI);
      xmlInputSource.setByteStream(byteStream);
      xmlInputSource.setCharacterStream(charStream);
      xmlInputSource.setEncoding(encoding);
      return xmlInputSource;
   }
}
