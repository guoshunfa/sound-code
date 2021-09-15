package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;

public class StreamSerializer implements XmlSerializer {
   private final SaxSerializer serializer;
   private final XMLWriter writer;

   public StreamSerializer(OutputStream out) {
      this(createWriter(out));
   }

   public StreamSerializer(OutputStream out, String encoding) throws UnsupportedEncodingException {
      this(createWriter(out, encoding));
   }

   public StreamSerializer(Writer out) {
      this(new StreamResult(out));
   }

   public StreamSerializer(StreamResult streamResult) {
      final OutputStream[] autoClose = new OutputStream[1];
      if (streamResult.getWriter() != null) {
         this.writer = createWriter(streamResult.getWriter());
      } else if (streamResult.getOutputStream() != null) {
         this.writer = createWriter(streamResult.getOutputStream());
      } else {
         if (streamResult.getSystemId() == null) {
            throw new IllegalArgumentException();
         }

         String fileURL = streamResult.getSystemId();
         fileURL = this.convertURL(fileURL);

         try {
            FileOutputStream fos = new FileOutputStream(fileURL);
            autoClose[0] = fos;
            this.writer = createWriter((OutputStream)fos);
         } catch (IOException var5) {
            throw new TxwException(var5);
         }
      }

      this.serializer = new SaxSerializer(this.writer, this.writer, false) {
         public void endDocument() {
            super.endDocument();
            if (autoClose[0] != null) {
               try {
                  autoClose[0].close();
               } catch (IOException var2) {
                  throw new TxwException(var2);
               }

               autoClose[0] = null;
            }

         }
      };
   }

   private StreamSerializer(XMLWriter writer) {
      this.writer = writer;
      this.serializer = new SaxSerializer(writer, writer, false);
   }

   private String convertURL(String url) {
      url = url.replace('\\', '/');
      url = url.replaceAll("//", "/");
      url = url.replaceAll("//", "/");
      if (url.startsWith("file:/")) {
         if (url.substring(6).indexOf(":") > 0) {
            url = url.substring(6);
         } else {
            url = url.substring(5);
         }
      }

      return url;
   }

   public void startDocument() {
      this.serializer.startDocument();
   }

   public void beginStartTag(String uri, String localName, String prefix) {
      this.serializer.beginStartTag(uri, localName, prefix);
   }

   public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
      this.serializer.writeAttribute(uri, localName, prefix, value);
   }

   public void writeXmlns(String prefix, String uri) {
      this.serializer.writeXmlns(prefix, uri);
   }

   public void endStartTag(String uri, String localName, String prefix) {
      this.serializer.endStartTag(uri, localName, prefix);
   }

   public void endTag() {
      this.serializer.endTag();
   }

   public void text(StringBuilder text) {
      this.serializer.text(text);
   }

   public void cdata(StringBuilder text) {
      this.serializer.cdata(text);
   }

   public void comment(StringBuilder comment) {
      this.serializer.comment(comment);
   }

   public void endDocument() {
      this.serializer.endDocument();
   }

   public void flush() {
      this.serializer.flush();

      try {
         this.writer.flush();
      } catch (IOException var2) {
         throw new TxwException(var2);
      }
   }

   private static XMLWriter createWriter(Writer w) {
      DataWriter dw = new DataWriter(new BufferedWriter(w));
      dw.setIndentStep("  ");
      return dw;
   }

   private static XMLWriter createWriter(OutputStream os, String encoding) throws UnsupportedEncodingException {
      XMLWriter writer = createWriter((Writer)(new OutputStreamWriter(os, encoding)));
      writer.setEncoding(encoding);
      return writer;
   }

   private static XMLWriter createWriter(OutputStream os) {
      try {
         return createWriter(os, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new Error(var2);
      }
   }
}
