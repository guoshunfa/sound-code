package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;

public class XMLStreamWriterOutput extends XmlOutputAbstractImpl {
   private final XMLStreamWriter out;
   private final CharacterEscapeHandler escapeHandler;
   private final XMLStreamWriterOutput.XmlStreamOutWriterAdapter writerWrapper;
   protected final char[] buf = new char[256];
   private static final Class FI_STAX_WRITER_CLASS = initFIStAXWriterClass();
   private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR = initFastInfosetOutputClass();
   private static final Class STAXEX_WRITER_CLASS = initStAXExWriterClass();
   private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR = initStAXExOutputClass();

   public static XmlOutput create(XMLStreamWriter out, JAXBContextImpl context, CharacterEscapeHandler escapeHandler) {
      Class writerClass = out.getClass();
      if (writerClass == FI_STAX_WRITER_CLASS) {
         try {
            return (XmlOutput)FI_OUTPUT_CTOR.newInstance(out, context);
         } catch (Exception var6) {
         }
      }

      if (STAXEX_WRITER_CLASS != null && STAXEX_WRITER_CLASS.isAssignableFrom(writerClass)) {
         try {
            return (XmlOutput)STAXEX_OUTPUT_CTOR.newInstance(out);
         } catch (Exception var5) {
         }
      }

      CharacterEscapeHandler xmlStreamEscapeHandler = escapeHandler != null ? escapeHandler : NoEscapeHandler.theInstance;
      return new XMLStreamWriterOutput(out, (CharacterEscapeHandler)xmlStreamEscapeHandler);
   }

   protected XMLStreamWriterOutput(XMLStreamWriter out, CharacterEscapeHandler escapeHandler) {
      this.out = out;
      this.escapeHandler = escapeHandler;
      this.writerWrapper = new XMLStreamWriterOutput.XmlStreamOutWriterAdapter(out);
   }

   public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
      super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
      if (!fragment) {
         this.out.writeStartDocument();
      }

   }

   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
      if (!fragment) {
         this.out.writeEndDocument();
         this.out.flush();
      }

      super.endDocument(fragment);
   }

   public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
      this.out.writeStartElement(this.nsContext.getPrefix(prefix), localName, this.nsContext.getNamespaceURI(prefix));
      NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
      if (nse.count() > 0) {
         for(int i = nse.count() - 1; i >= 0; --i) {
            String uri = nse.getNsUri(i);
            if (uri.length() != 0 || nse.getBase() != 1) {
               this.out.writeNamespace(nse.getPrefix(i), uri);
            }
         }
      }

   }

   public void attribute(int prefix, String localName, String value) throws IOException, XMLStreamException {
      if (prefix == -1) {
         this.out.writeAttribute(localName, value);
      } else {
         this.out.writeAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName, value);
      }

   }

   public void endStartTag() throws IOException, SAXException {
   }

   public void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException {
      this.out.writeEndElement();
   }

   public void text(String value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      if (needsSeparatingWhitespace) {
         this.out.writeCharacters(" ");
      }

      this.escapeHandler.escape(value.toCharArray(), 0, value.length(), false, this.writerWrapper);
   }

   public void text(Pcdata value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
      if (needsSeparatingWhitespace) {
         this.out.writeCharacters(" ");
      }

      int len = value.length();
      if (len < this.buf.length) {
         value.writeTo(this.buf, 0);
         this.out.writeCharacters(this.buf, 0, len);
      } else {
         this.out.writeCharacters(value.toString());
      }

   }

   private static Class initFIStAXWriterClass() {
      try {
         Class<?> llfisw = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter");
         Class<?> sds = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer");
         return llfisw.isAssignableFrom(sds) ? sds : null;
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Constructor<? extends XmlOutput> initFastInfosetOutputClass() {
      try {
         if (FI_STAX_WRITER_CLASS == null) {
            return null;
         } else {
            Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.FastInfosetStreamWriterOutput");
            return c.getConstructor(FI_STAX_WRITER_CLASS, JAXBContextImpl.class);
         }
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Class initStAXExWriterClass() {
      try {
         return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx");
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Constructor<? extends XmlOutput> initStAXExOutputClass() {
      try {
         Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput");
         return c.getConstructor(STAXEX_WRITER_CLASS);
      } catch (Throwable var1) {
         return null;
      }
   }

   private static final class XmlStreamOutWriterAdapter extends Writer {
      private final XMLStreamWriter writer;

      private XmlStreamOutWriterAdapter(XMLStreamWriter writer) {
         this.writer = writer;
      }

      public void write(char[] cbuf, int off, int len) throws IOException {
         try {
            this.writer.writeCharacters(cbuf, off, len);
         } catch (XMLStreamException var5) {
            throw new IOException("Error writing XML stream", var5);
         }
      }

      public void writeEntityRef(String entityReference) throws XMLStreamException {
         this.writer.writeEntityRef(entityReference);
      }

      public void flush() throws IOException {
         try {
            this.writer.flush();
         } catch (XMLStreamException var2) {
            throw new IOException("Error flushing XML stream", var2);
         }
      }

      public void close() throws IOException {
         try {
            this.writer.close();
         } catch (XMLStreamException var2) {
            throw new IOException("Error closing XML stream", var2);
         }
      }

      // $FF: synthetic method
      XmlStreamOutWriterAdapter(XMLStreamWriter x0, Object x1) {
         this(x0);
      }
   }
}
