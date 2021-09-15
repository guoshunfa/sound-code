package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.internal.messaging.saaj.util.ParserPool;
import com.sun.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;

public class EnvelopeFactory {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   private static ContextClassloaderLocal<ParserPool> parserPool = new ContextClassloaderLocal<ParserPool>() {
      protected ParserPool initialValue() throws Exception {
         return new ParserPool(5);
      }
   };

   public static Envelope createEnvelope(Source src, SOAPPartImpl soapPart) throws SOAPException {
      SAXParser saxParser = null;
      if (src instanceof StreamSource) {
         if (src instanceof JAXMStreamSource) {
            try {
               if (!SOAPPartImpl.lazyContentLength) {
                  ((JAXMStreamSource)src).reset();
               }
            } catch (IOException var15) {
               log.severe("SAAJ0515.source.reset.exception");
               throw new SOAPExceptionImpl(var15);
            }
         }

         try {
            saxParser = ((ParserPool)parserPool.get()).get();
         } catch (Exception var14) {
            log.severe("SAAJ0601.util.newSAXParser.exception");
            throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", var14);
         }

         InputSource is = SAXSource.sourceToInputSource((Source)src);
         if (is.getEncoding() == null && soapPart.getSourceCharsetEncoding() != null) {
            is.setEncoding(soapPart.getSourceCharsetEncoding());
         }

         RejectDoctypeSaxFilter rejectFilter;
         try {
            rejectFilter = new RejectDoctypeSaxFilter(saxParser);
         } catch (Exception var13) {
            log.severe("SAAJ0510.soap.cannot.create.envelope");
            throw new SOAPExceptionImpl("Unable to create envelope from given source: ", var13);
         }

         src = new SAXSource(rejectFilter, is);
      }

      Envelope var6;
      try {
         Transformer transformer = EfficientStreamingTransformer.newTransformer();
         DOMResult result = new DOMResult(soapPart);
         transformer.transform((Source)src, result);
         Envelope env = (Envelope)soapPart.getEnvelope();
         var6 = env;
      } catch (Exception var16) {
         if (var16 instanceof SOAPVersionMismatchException) {
            throw (SOAPVersionMismatchException)var16;
         }

         log.severe("SAAJ0511.soap.cannot.create.envelope");
         throw new SOAPExceptionImpl("Unable to create envelope from given source: ", var16);
      } finally {
         if (saxParser != null) {
            ((ParserPool)parserPool.get()).returnParser(saxParser);
         }

      }

      return var6;
   }
}
