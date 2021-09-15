package javax.xml.bind.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class AbstractMarshallerImpl implements Marshaller {
   private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
   private String encoding = "UTF-8";
   private String schemaLocation = null;
   private String noNSSchemaLocation = null;
   private boolean formattedOutput = false;
   private boolean fragment = false;
   static String[] aliases = new String[]{"UTF-8", "UTF8", "UTF-16", "Unicode", "UTF-16BE", "UnicodeBigUnmarked", "UTF-16LE", "UnicodeLittleUnmarked", "US-ASCII", "ASCII", "TIS-620", "TIS620", "ISO-10646-UCS-2", "Unicode", "EBCDIC-CP-US", "cp037", "EBCDIC-CP-CA", "cp037", "EBCDIC-CP-NL", "cp037", "EBCDIC-CP-WT", "cp037", "EBCDIC-CP-DK", "cp277", "EBCDIC-CP-NO", "cp277", "EBCDIC-CP-FI", "cp278", "EBCDIC-CP-SE", "cp278", "EBCDIC-CP-IT", "cp280", "EBCDIC-CP-ES", "cp284", "EBCDIC-CP-GB", "cp285", "EBCDIC-CP-FR", "cp297", "EBCDIC-CP-AR1", "cp420", "EBCDIC-CP-HE", "cp424", "EBCDIC-CP-BE", "cp500", "EBCDIC-CP-CH", "cp500", "EBCDIC-CP-ROECE", "cp870", "EBCDIC-CP-YU", "cp870", "EBCDIC-CP-IS", "cp871", "EBCDIC-CP-AR2", "cp918"};

   public final void marshal(Object obj, OutputStream os) throws JAXBException {
      this.checkNotNull(obj, "obj", os, "os");
      this.marshal(obj, (Result)(new StreamResult(os)));
   }

   public void marshal(Object jaxbElement, File output) throws JAXBException {
      this.checkNotNull(jaxbElement, "jaxbElement", output, "output");

      try {
         BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(output));

         try {
            this.marshal(jaxbElement, (Result)(new StreamResult(os)));
         } finally {
            os.close();
         }

      } catch (IOException var8) {
         throw new JAXBException(var8);
      }
   }

   public final void marshal(Object obj, Writer w) throws JAXBException {
      this.checkNotNull(obj, "obj", w, "writer");
      this.marshal(obj, (Result)(new StreamResult(w)));
   }

   public final void marshal(Object obj, ContentHandler handler) throws JAXBException {
      this.checkNotNull(obj, "obj", handler, "handler");
      this.marshal(obj, (Result)(new SAXResult(handler)));
   }

   public final void marshal(Object obj, Node node) throws JAXBException {
      this.checkNotNull(obj, "obj", node, "node");
      this.marshal(obj, (Result)(new DOMResult(node)));
   }

   public Node getNode(Object obj) throws JAXBException {
      this.checkNotNull(obj, "obj", Boolean.TRUE, "foo");
      throw new UnsupportedOperationException();
   }

   protected String getEncoding() {
      return this.encoding;
   }

   protected void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   protected String getSchemaLocation() {
      return this.schemaLocation;
   }

   protected void setSchemaLocation(String location) {
      this.schemaLocation = location;
   }

   protected String getNoNSSchemaLocation() {
      return this.noNSSchemaLocation;
   }

   protected void setNoNSSchemaLocation(String location) {
      this.noNSSchemaLocation = location;
   }

   protected boolean isFormattedOutput() {
      return this.formattedOutput;
   }

   protected void setFormattedOutput(boolean v) {
      this.formattedOutput = v;
   }

   protected boolean isFragment() {
      return this.fragment;
   }

   protected void setFragment(boolean v) {
      this.fragment = v;
   }

   protected String getJavaEncoding(String encoding) throws UnsupportedEncodingException {
      try {
         "1".getBytes(encoding);
         return encoding;
      } catch (UnsupportedEncodingException var4) {
         for(int i = 0; i < aliases.length; i += 2) {
            if (encoding.equals(aliases[i])) {
               "1".getBytes(aliases[i + 1]);
               return aliases[i + 1];
            }
         }

         throw new UnsupportedEncodingException(encoding);
      }
   }

   public void setProperty(String name, Object value) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"name"));
      } else if ("jaxb.encoding".equals(name)) {
         this.checkString(name, value);
         this.setEncoding((String)value);
      } else if ("jaxb.formatted.output".equals(name)) {
         this.checkBoolean(name, value);
         this.setFormattedOutput((Boolean)value);
      } else if ("jaxb.noNamespaceSchemaLocation".equals(name)) {
         this.checkString(name, value);
         this.setNoNSSchemaLocation((String)value);
      } else if ("jaxb.schemaLocation".equals(name)) {
         this.checkString(name, value);
         this.setSchemaLocation((String)value);
      } else if ("jaxb.fragment".equals(name)) {
         this.checkBoolean(name, value);
         this.setFragment((Boolean)value);
      } else {
         throw new PropertyException(name, value);
      }
   }

   public Object getProperty(String name) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)"name"));
      } else if ("jaxb.encoding".equals(name)) {
         return this.getEncoding();
      } else if ("jaxb.formatted.output".equals(name)) {
         return this.isFormattedOutput() ? Boolean.TRUE : Boolean.FALSE;
      } else if ("jaxb.noNamespaceSchemaLocation".equals(name)) {
         return this.getNoNSSchemaLocation();
      } else if ("jaxb.schemaLocation".equals(name)) {
         return this.getSchemaLocation();
      } else if ("jaxb.fragment".equals(name)) {
         return this.isFragment() ? Boolean.TRUE : Boolean.FALSE;
      } else {
         throw new PropertyException(name);
      }
   }

   public ValidationEventHandler getEventHandler() throws JAXBException {
      return this.eventHandler;
   }

   public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
      if (handler == null) {
         this.eventHandler = new DefaultValidationEventHandler();
      } else {
         this.eventHandler = handler;
      }

   }

   private void checkBoolean(String name, Object value) throws PropertyException {
      if (!(value instanceof Boolean)) {
         throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeBoolean", (Object)name));
      }
   }

   private void checkString(String name, Object value) throws PropertyException {
      if (!(value instanceof String)) {
         throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeString", (Object)name));
      }
   }

   private void checkNotNull(Object o1, String o1Name, Object o2, String o2Name) {
      if (o1 == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)o1Name));
      } else if (o2 == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", (Object)o2Name));
      }
   }

   public void marshal(Object obj, XMLEventWriter writer) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public void marshal(Object obj, XMLStreamWriter writer) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public void setSchema(Schema schema) {
      throw new UnsupportedOperationException();
   }

   public Schema getSchema() {
      throw new UnsupportedOperationException();
   }

   public void setAdapter(XmlAdapter adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException();
      } else {
         this.setAdapter(adapter.getClass(), adapter);
      }
   }

   public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
      throw new UnsupportedOperationException();
   }

   public <A extends XmlAdapter> A getAdapter(Class<A> type) {
      throw new UnsupportedOperationException();
   }

   public void setAttachmentMarshaller(AttachmentMarshaller am) {
      throw new UnsupportedOperationException();
   }

   public AttachmentMarshaller getAttachmentMarshaller() {
      throw new UnsupportedOperationException();
   }

   public void setListener(Marshaller.Listener listener) {
      throw new UnsupportedOperationException();
   }

   public Marshaller.Listener getListener() {
      throw new UnsupportedOperationException();
   }
}
