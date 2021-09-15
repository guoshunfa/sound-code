package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;

public class FastInfosetDataContentHandler implements DataContentHandler {
   public static final String STR_SRC = "com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource";

   public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] flavors = new DataFlavor[]{new ActivationDataFlavor(FastInfosetReflection.getFastInfosetSource_class(), "application/fastinfoset", "Fast Infoset")};
      return flavors;
   }

   public Object getTransferData(DataFlavor flavor, DataSource dataSource) throws IOException {
      if (flavor.getMimeType().startsWith("application/fastinfoset")) {
         try {
            if (flavor.getRepresentationClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource")) {
               return FastInfosetReflection.FastInfosetSource_new(dataSource.getInputStream());
            }
         } catch (Exception var4) {
            throw new IOException(var4.getMessage());
         }
      }

      return null;
   }

   public Object getContent(DataSource dataSource) throws IOException {
      try {
         return FastInfosetReflection.FastInfosetSource_new(dataSource.getInputStream());
      } catch (Exception var3) {
         throw new IOException(var3.getMessage());
      }
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (!mimeType.equals("application/fastinfoset")) {
         throw new IOException("Invalid content type \"" + mimeType + "\" for FastInfosetDCH");
      } else {
         try {
            InputStream is = FastInfosetReflection.FastInfosetSource_getInputStream((Source)obj);
            byte[] buffer = new byte[4096];

            int n;
            while((n = is.read(buffer)) != -1) {
               os.write(buffer, 0, n);
            }

         } catch (Exception var7) {
            throw new IOException("Error copying FI source to output stream " + var7.getMessage());
         }
      }
   }
}
