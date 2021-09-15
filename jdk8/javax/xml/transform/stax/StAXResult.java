package javax.xml.transform.stax;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class StAXResult implements Result {
   public static final String FEATURE = "http://javax.xml.transform.stax.StAXResult/feature";
   private XMLEventWriter xmlEventWriter = null;
   private XMLStreamWriter xmlStreamWriter = null;
   private String systemId = null;

   public StAXResult(XMLEventWriter xmlEventWriter) {
      if (xmlEventWriter == null) {
         throw new IllegalArgumentException("StAXResult(XMLEventWriter) with XMLEventWriter == null");
      } else {
         this.xmlEventWriter = xmlEventWriter;
      }
   }

   public StAXResult(XMLStreamWriter xmlStreamWriter) {
      if (xmlStreamWriter == null) {
         throw new IllegalArgumentException("StAXResult(XMLStreamWriter) with XMLStreamWriter == null");
      } else {
         this.xmlStreamWriter = xmlStreamWriter;
      }
   }

   public XMLEventWriter getXMLEventWriter() {
      return this.xmlEventWriter;
   }

   public XMLStreamWriter getXMLStreamWriter() {
      return this.xmlStreamWriter;
   }

   public void setSystemId(String systemId) {
      throw new UnsupportedOperationException("StAXResult#setSystemId(systemId) cannot set the system identifier for a StAXResult");
   }

   public String getSystemId() {
      return null;
   }
}
