package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import java.io.IOException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamReaderToXMLStreamWriter {
   private static final int BUF_SIZE = 4096;
   protected XMLStreamReader in;
   protected XMLStreamWriter out;
   private char[] buf;
   boolean optimizeBase64Data = false;
   AttachmentMarshaller mtomAttachmentMarshaller;

   public void bridge(XMLStreamReader in, XMLStreamWriter out) throws XMLStreamException {
      assert in != null && out != null;

      this.in = in;
      this.out = out;
      this.optimizeBase64Data = in instanceof XMLStreamReaderEx;
      if (out instanceof XMLStreamWriterEx && out instanceof MtomStreamWriter) {
         this.mtomAttachmentMarshaller = ((MtomStreamWriter)out).getAttachmentMarshaller();
      }

      int depth = 0;
      this.buf = new char[4096];
      int event = in.getEventType();
      if (event == 7) {
         while(!in.isStartElement()) {
            event = in.next();
            if (event == 5) {
               this.handleComment();
            }
         }
      }

      if (event != 1) {
         throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
      } else {
         do {
            switch(event) {
            case 1:
               ++depth;
               this.handleStartElement();
               break;
            case 2:
               this.handleEndElement();
               --depth;
               if (depth == 0) {
                  return;
               }
               break;
            case 3:
               this.handlePI();
               break;
            case 4:
               this.handleCharacters();
               break;
            case 5:
               this.handleComment();
               break;
            case 6:
               this.handleSpace();
               break;
            case 7:
            case 10:
            default:
               throw new XMLStreamException("Cannot process event: " + event);
            case 8:
               throw new XMLStreamException("Malformed XML at depth=" + depth + ", Reached EOF. Event=" + event);
            case 9:
               this.handleEntityReference();
               break;
            case 11:
               this.handleDTD();
               break;
            case 12:
               this.handleCDATA();
            }

            event = in.next();
         } while(depth != 0);

      }
   }

   protected void handlePI() throws XMLStreamException {
      this.out.writeProcessingInstruction(this.in.getPITarget(), this.in.getPIData());
   }

   protected void handleCharacters() throws XMLStreamException {
      CharSequence c = null;
      if (this.optimizeBase64Data) {
         c = ((XMLStreamReaderEx)this.in).getPCDATA();
      }

      if (c != null && c instanceof Base64Data) {
         if (this.mtomAttachmentMarshaller != null) {
            Base64Data b64d = (Base64Data)c;
            ((XMLStreamWriterEx)this.out).writeBinary(b64d.getDataHandler());
         } else {
            try {
               ((Base64Data)c).writeTo(this.out);
            } catch (IOException var4) {
               throw new XMLStreamException(var4);
            }
         }
      } else {
         int start = 0;

         for(int read = this.buf.length; read == this.buf.length; start += this.buf.length) {
            read = this.in.getTextCharacters(start, this.buf, 0, this.buf.length);
            this.out.writeCharacters(this.buf, 0, read);
         }
      }

   }

   protected void handleEndElement() throws XMLStreamException {
      this.out.writeEndElement();
   }

   protected void handleStartElement() throws XMLStreamException {
      String nsUri = this.in.getNamespaceURI();
      if (nsUri == null) {
         this.out.writeStartElement(this.in.getLocalName());
      } else {
         this.out.writeStartElement(fixNull(this.in.getPrefix()), this.in.getLocalName(), nsUri);
      }

      int nsCount = this.in.getNamespaceCount();

      int attCount;
      for(attCount = 0; attCount < nsCount; ++attCount) {
         this.out.writeNamespace(this.in.getNamespacePrefix(attCount), fixNull(this.in.getNamespaceURI(attCount)));
      }

      attCount = this.in.getAttributeCount();

      for(int i = 0; i < attCount; ++i) {
         this.handleAttribute(i);
      }

   }

   protected void handleAttribute(int i) throws XMLStreamException {
      String nsUri = this.in.getAttributeNamespace(i);
      String prefix = this.in.getAttributePrefix(i);
      if (!fixNull(nsUri).equals("http://www.w3.org/2000/xmlns/")) {
         if (nsUri != null && prefix != null && !prefix.equals("")) {
            this.out.writeAttribute(prefix, nsUri, this.in.getAttributeLocalName(i), this.in.getAttributeValue(i));
         } else {
            this.out.writeAttribute(this.in.getAttributeLocalName(i), this.in.getAttributeValue(i));
         }

      }
   }

   protected void handleDTD() throws XMLStreamException {
      this.out.writeDTD(this.in.getText());
   }

   protected void handleComment() throws XMLStreamException {
      this.out.writeComment(this.in.getText());
   }

   protected void handleEntityReference() throws XMLStreamException {
      this.out.writeEntityRef(this.in.getText());
   }

   protected void handleSpace() throws XMLStreamException {
      this.handleCharacters();
   }

   protected void handleCDATA() throws XMLStreamException {
      this.out.writeCData(this.in.getText());
   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }
}
