package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class StAXOutputFactory extends XMLOutputFactory {
   private StAXManager _manager = null;

   public StAXOutputFactory() {
      this._manager = new StAXManager(2);
   }

   public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
      return new StAXEventWriter(this.createXMLStreamWriter(result));
   }

   public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
      return new StAXEventWriter(this.createXMLStreamWriter(writer));
   }

   public XMLEventWriter createXMLEventWriter(OutputStream outputStream) throws XMLStreamException {
      return new StAXEventWriter(this.createXMLStreamWriter(outputStream));
   }

   public XMLEventWriter createXMLEventWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
      return new StAXEventWriter(this.createXMLStreamWriter(outputStream, encoding));
   }

   public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
      XMLStreamWriter streamWriter;
      if (result instanceof StreamResult) {
         StreamResult streamResult = (StreamResult)result;
         if (streamResult.getWriter() != null) {
            return this.createXMLStreamWriter(streamResult.getWriter());
         } else if (streamResult.getOutputStream() != null) {
            return this.createXMLStreamWriter(streamResult.getOutputStream());
         } else if (streamResult.getSystemId() != null) {
            FileWriter writer = null;
            boolean isError = true;

            XMLStreamWriter var6;
            try {
               writer = new FileWriter(new File(streamResult.getSystemId()));
               streamWriter = this.createXMLStreamWriter((Writer)writer);
               isError = false;
               var6 = streamWriter;
            } catch (IOException var29) {
               throw new XMLStreamException(var29);
            } finally {
               if (isError && writer != null) {
                  try {
                     writer.close();
                  } catch (IOException var27) {
                  }
               }

            }

            return var6;
         } else {
            throw new UnsupportedOperationException();
         }
      } else {
         FileWriter writer = null;
         boolean isError = true;

         try {
            writer = new FileWriter(new File(result.getSystemId()));
            XMLStreamWriter streamWriter = this.createXMLStreamWriter((Writer)writer);
            isError = false;
            streamWriter = streamWriter;
         } catch (IOException var30) {
            throw new XMLStreamException(var30);
         } finally {
            if (isError && writer != null) {
               try {
                  writer.close();
               } catch (IOException var28) {
               }
            }

         }

         return streamWriter;
      }
   }

   public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
      return new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
   }

   public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
      StAXDocumentSerializer serializer = new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
      serializer.setEncoding(encoding);
      return serializer;
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      if (name == null) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{null}));
      } else if (this._manager.containsProperty(name)) {
         return this._manager.getProperty(name);
      } else {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
      }
   }

   public boolean isPropertySupported(String name) {
      return name == null ? false : this._manager.containsProperty(name);
   }

   public void setProperty(String name, Object value) throws IllegalArgumentException {
      this._manager.setProperty(name, value);
   }
}
