package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StreamWriterBufferProcessor extends AbstractProcessor {
   public StreamWriterBufferProcessor() {
   }

   /** @deprecated */
   public StreamWriterBufferProcessor(XMLStreamBuffer buffer) {
      this.setXMLStreamBuffer(buffer, buffer.isFragment());
   }

   public StreamWriterBufferProcessor(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
      this.setXMLStreamBuffer(buffer, produceFragmentEvent);
   }

   public final void process(XMLStreamBuffer buffer, XMLStreamWriter writer) throws XMLStreamException {
      this.setXMLStreamBuffer(buffer, buffer.isFragment());
      this.process(writer);
   }

   public void process(XMLStreamWriter writer) throws XMLStreamException {
      if (this._fragmentMode) {
         this.writeFragment(writer);
      } else {
         this.write(writer);
      }

   }

   /** @deprecated */
   public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
      this.setBuffer(buffer);
   }

   public void setXMLStreamBuffer(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
      this.setBuffer(buffer, produceFragmentEvent);
   }

   public void write(XMLStreamWriter writer) throws XMLStreamException {
      if (!this._fragmentMode) {
         if (this._treeCount > 1) {
            throw new IllegalStateException("forest cannot be written as a full infoset");
         }

         writer.writeStartDocument();
      }

      while(true) {
         int item = getEIIState(this.peekStructure());
         writer.flush();
         int start;
         String comment;
         int length;
         switch(item) {
         case 1:
            this.readStructure();
            break;
         case 2:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 15:
         default:
            throw new XMLStreamException("Invalid State " + item);
         case 3:
         case 4:
         case 5:
         case 6:
            this.writeFragment(writer);
            break;
         case 12:
            this.readStructure();
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 13:
            this.readStructure();
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 14:
            this.readStructure();
            char[] ch = this.readContentCharactersCopy();
            writer.writeComment(new String(ch));
            break;
         case 16:
            this.readStructure();
            writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
            break;
         case 17:
            this.readStructure();
            writer.writeEndDocument();
            return;
         }
      }
   }

   public void writeFragment(XMLStreamWriter writer) throws XMLStreamException {
      if (writer instanceof XMLStreamWriterEx) {
         this.writeFragmentEx((XMLStreamWriterEx)writer);
      } else {
         this.writeFragmentNoEx(writer);
      }

   }

   public void writeFragmentEx(XMLStreamWriterEx writer) throws XMLStreamException {
      int depth = 0;
      int item = getEIIState(this.peekStructure());
      if (item == 1) {
         this.readStructure();
      }

      do {
         item = this.readEiiState();
         char[] ch;
         int start;
         String comment;
         int length;
         String s;
         String localName;
         switch(item) {
         case 1:
            throw new AssertionError();
         case 2:
         case 15:
         default:
            throw new XMLStreamException("Invalid State " + item);
         case 3:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            comment = this.getPrefixFromQName(this.readStructureString());
            writer.writeStartElement(comment, localName, s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 4:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            comment = this.readStructureString();
            writer.writeStartElement(s, comment, localName);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 5:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            writer.writeStartElement("", localName, s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 6:
            ++depth;
            s = this.readStructureString();
            writer.writeStartElement(s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 7:
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            writer.writeCharacters(this._contentCharactersBuffer, start, length);
            break;
         case 8:
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            writer.writeCharacters(this._contentCharactersBuffer, start, length);
            break;
         case 9:
            ch = this.readContentCharactersCopy();
            writer.writeCharacters(ch, 0, ch.length);
            break;
         case 10:
            s = this.readContentString();
            writer.writeCharacters(s);
            break;
         case 11:
            CharSequence c = (CharSequence)this.readContentObject();
            writer.writePCDATA(c);
            break;
         case 12:
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 13:
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 14:
            ch = this.readContentCharactersCopy();
            writer.writeComment(new String(ch));
            break;
         case 16:
            writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
            break;
         case 17:
            writer.writeEndElement();
            --depth;
            if (depth == 0) {
               --this._treeCount;
            }
         }
      } while(depth > 0 || this._treeCount > 0);

   }

   public void writeFragmentNoEx(XMLStreamWriter writer) throws XMLStreamException {
      int depth = 0;
      int item = getEIIState(this.peekStructure());
      if (item == 1) {
         this.readStructure();
      }

      do {
         item = this.readEiiState();
         char[] ch;
         int start;
         String comment;
         int length;
         String s;
         String localName;
         switch(item) {
         case 1:
            throw new AssertionError();
         case 2:
         case 15:
         default:
            throw new XMLStreamException("Invalid State " + item);
         case 3:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            comment = this.getPrefixFromQName(this.readStructureString());
            writer.writeStartElement(comment, localName, s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 4:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            comment = this.readStructureString();
            writer.writeStartElement(s, comment, localName);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 5:
            ++depth;
            s = this.readStructureString();
            localName = this.readStructureString();
            writer.writeStartElement("", localName, s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 6:
            ++depth;
            s = this.readStructureString();
            writer.writeStartElement(s);
            this.writeAttributes(writer, this.isInscope(depth));
            break;
         case 7:
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            writer.writeCharacters(this._contentCharactersBuffer, start, length);
            break;
         case 8:
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            writer.writeCharacters(this._contentCharactersBuffer, start, length);
            break;
         case 9:
            ch = this.readContentCharactersCopy();
            writer.writeCharacters(ch, 0, ch.length);
            break;
         case 10:
            s = this.readContentString();
            writer.writeCharacters(s);
            break;
         case 11:
            CharSequence c = (CharSequence)this.readContentObject();
            if (c instanceof Base64Data) {
               try {
                  Base64Data bd = (Base64Data)c;
                  bd.writeTo(writer);
               } catch (IOException var7) {
                  throw new XMLStreamException(var7);
               }
            } else {
               writer.writeCharacters(c.toString());
            }
            break;
         case 12:
            length = this.readStructure();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 13:
            length = this.readStructure16();
            start = this.readContentCharactersBuffer(length);
            comment = new String(this._contentCharactersBuffer, start, length);
            writer.writeComment(comment);
            break;
         case 14:
            ch = this.readContentCharactersCopy();
            writer.writeComment(new String(ch));
            break;
         case 16:
            writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
            break;
         case 17:
            writer.writeEndElement();
            --depth;
            if (depth == 0) {
               --this._treeCount;
            }
         }
      } while(depth > 0 || this._treeCount > 0);

   }

   private boolean isInscope(int depth) {
      return this._buffer.getInscopeNamespaces().size() > 0 && depth == 1;
   }

   private void writeAttributes(XMLStreamWriter writer, boolean inscope) throws XMLStreamException {
      Set<String> prefixSet = inscope ? new HashSet() : Collections.emptySet();
      int item = this.peekStructure();
      if ((item & 240) == 64) {
         item = this.writeNamespaceAttributes(item, writer, inscope, (Set)prefixSet);
      }

      if (inscope) {
         this.writeInscopeNamespaces(writer, (Set)prefixSet);
      }

      if ((item & 240) == 48) {
         this.writeAttributes(item, writer);
      }

   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   private void writeInscopeNamespaces(XMLStreamWriter writer, Set<String> prefixSet) throws XMLStreamException {
      Iterator var3 = this._buffer.getInscopeNamespaces().entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, String> e = (Map.Entry)var3.next();
         String key = fixNull((String)e.getKey());
         if (!prefixSet.contains(key)) {
            writer.writeNamespace(key, (String)e.getValue());
         }
      }

   }

   private int writeNamespaceAttributes(int item, XMLStreamWriter writer, boolean collectPrefixes, Set<String> prefixSet) throws XMLStreamException {
      do {
         String prefix;
         switch(getNIIState(item)) {
         case 1:
            writer.writeDefaultNamespace("");
            if (collectPrefixes) {
               prefixSet.add("");
            }
            break;
         case 2:
            prefix = this.readStructureString();
            writer.writeNamespace(prefix, "");
            if (collectPrefixes) {
               prefixSet.add(prefix);
            }
            break;
         case 3:
            prefix = this.readStructureString();
            writer.writeNamespace(prefix, this.readStructureString());
            if (collectPrefixes) {
               prefixSet.add(prefix);
            }
            break;
         case 4:
            writer.writeDefaultNamespace(this.readStructureString());
            if (collectPrefixes) {
               prefixSet.add("");
            }
         }

         this.readStructure();
         item = this.peekStructure();
      } while((item & 240) == 64);

      return item;
   }

   private void writeAttributes(int item, XMLStreamWriter writer) throws XMLStreamException {
      do {
         switch(getAIIState(item)) {
         case 1:
            String uri = this.readStructureString();
            String localName = this.readStructureString();
            String prefix = this.getPrefixFromQName(this.readStructureString());
            writer.writeAttribute(prefix, uri, localName, this.readContentString());
            break;
         case 2:
            writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         case 3:
            writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         case 4:
            writer.writeAttribute(this.readStructureString(), this.readContentString());
         }

         this.readStructureString();
         this.readStructure();
         item = this.peekStructure();
      } while((item & 240) == 48);

   }
}
