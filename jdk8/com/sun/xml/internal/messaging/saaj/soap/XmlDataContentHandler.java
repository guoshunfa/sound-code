package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler implements DataContentHandler {
   public static final String STR_SRC = "javax.xml.transform.stream.StreamSource";
   private static Class streamSourceClass = null;

   public XmlDataContentHandler() throws ClassNotFoundException {
      if (streamSourceClass == null) {
         streamSourceClass = Class.forName("javax.xml.transform.stream.StreamSource");
      }

   }

   public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] flavors = new DataFlavor[]{new ActivationDataFlavor(streamSourceClass, "text/xml", "XML"), new ActivationDataFlavor(streamSourceClass, "application/xml", "XML")};
      return flavors;
   }

   public Object getTransferData(DataFlavor flavor, DataSource dataSource) throws IOException {
      return (flavor.getMimeType().startsWith("text/xml") || flavor.getMimeType().startsWith("application/xml")) && flavor.getRepresentationClass().getName().equals("javax.xml.transform.stream.StreamSource") ? new StreamSource(dataSource.getInputStream()) : null;
   }

   public Object getContent(DataSource dataSource) throws IOException {
      return new StreamSource(dataSource.getInputStream());
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (!mimeType.startsWith("text/xml") && !mimeType.startsWith("application/xml")) {
         throw new IOException("Invalid content type \"" + mimeType + "\" for XmlDCH");
      } else {
         try {
            Transformer transformer = EfficientStreamingTransformer.newTransformer();
            StreamResult result = new StreamResult(os);
            if (obj instanceof DataSource) {
               transformer.transform((Source)this.getContent((DataSource)obj), result);
            } else {
               Source src = null;
               if (obj instanceof String) {
                  src = new StreamSource(new StringReader((String)obj));
               } else {
                  src = (Source)obj;
               }

               transformer.transform((Source)src, result);
            }

         } catch (Exception var7) {
            throw new IOException("Unable to run the JAXP transformer on a stream " + var7.getMessage());
         }
      }
   }
}
