package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class MultipartDataContentHandler implements DataContentHandler {
   private ActivationDataFlavor myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.myDF};
   }

   public Object getTransferData(DataFlavor df, DataSource ds) {
      return this.myDF.equals(df) ? this.getContent(ds) : null;
   }

   public Object getContent(DataSource ds) {
      try {
         return new MimeMultipart(ds, new ContentType(ds.getContentType()));
      } catch (Exception var3) {
         return null;
      }
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (obj instanceof MimeMultipart) {
         try {
            ByteOutputStream baos = null;
            if (!(os instanceof ByteOutputStream)) {
               throw new IOException("Input Stream expected to be a com.sun.xml.internal.messaging.saaj.util.ByteOutputStream, but found " + os.getClass().getName());
            }

            baos = (ByteOutputStream)os;
            ((MimeMultipart)obj).writeTo(baos);
         } catch (Exception var5) {
            throw new IOException(var5.toString());
         }
      }

   }
}
