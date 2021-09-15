package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

final class SerializerFactoryImpl extends SerializerFactory {
   private String _method;

   SerializerFactoryImpl(String method) {
      this._method = method;
      if (!this._method.equals("xml") && !this._method.equals("html") && !this._method.equals("xhtml") && !this._method.equals("text")) {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[]{method});
         throw new IllegalArgumentException(msg);
      }
   }

   public Serializer makeSerializer(OutputFormat format) {
      Serializer serializer = this.getSerializer(format);
      serializer.setOutputFormat(format);
      return serializer;
   }

   public Serializer makeSerializer(Writer writer, OutputFormat format) {
      Serializer serializer = this.getSerializer(format);
      serializer.setOutputCharStream(writer);
      return serializer;
   }

   public Serializer makeSerializer(OutputStream output, OutputFormat format) throws UnsupportedEncodingException {
      Serializer serializer = this.getSerializer(format);
      serializer.setOutputByteStream(output);
      return serializer;
   }

   private Serializer getSerializer(OutputFormat format) {
      if (this._method.equals("xml")) {
         return new XMLSerializer(format);
      } else if (this._method.equals("html")) {
         return new HTMLSerializer(format);
      } else if (this._method.equals("xhtml")) {
         return new XHTMLSerializer(format);
      } else if (this._method.equals("text")) {
         return new TextSerializer();
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[]{this._method});
         throw new IllegalStateException(msg);
      }
   }

   protected String getSupportedMethod() {
      return this._method;
   }
}
