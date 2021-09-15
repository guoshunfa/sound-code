package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class IndentingUTF8XmlOutput extends UTF8XmlOutput {
   private final Encoded indent8;
   private final int unitLen;
   private int depth = 0;
   private boolean seenText = false;

   public IndentingUTF8XmlOutput(OutputStream out, String indentStr, Encoded[] localNames, CharacterEscapeHandler escapeHandler) {
      super(out, localNames, escapeHandler);
      if (indentStr != null) {
         Encoded e = new Encoded(indentStr);
         this.indent8 = new Encoded();
         this.indent8.ensureSize(e.len * 8);
         this.unitLen = e.len;

         for(int i = 0; i < 8; ++i) {
            System.arraycopy(e.buf, 0, this.indent8.buf, this.unitLen * i, this.unitLen);
         }
      } else {
         this.indent8 = null;
         this.unitLen = 0;
      }

   }

   public void beginStartTag(int prefix, String localName) throws IOException {
      this.indentStartTag();
      super.beginStartTag(prefix, localName);
   }

   public void beginStartTag(Name name) throws IOException {
      this.indentStartTag();
      super.beginStartTag(name);
   }

   private void indentStartTag() throws IOException {
      this.closeStartTag();
      if (!this.seenText) {
         this.printIndent();
      }

      ++this.depth;
      this.seenText = false;
   }

   public void endTag(Name name) throws IOException {
      this.indentEndTag();
      super.endTag(name);
   }

   public void endTag(int prefix, String localName) throws IOException {
      this.indentEndTag();
      super.endTag(prefix, localName);
   }

   private void indentEndTag() throws IOException {
      --this.depth;
      if (!this.closeStartTagPending && !this.seenText) {
         this.printIndent();
      }

      this.seenText = false;
   }

   private void printIndent() throws IOException {
      this.write(10);
      int i = this.depth % 8;
      this.write(this.indent8.buf, 0, i * this.unitLen);

      for(i >>= 3; i > 0; --i) {
         this.indent8.write(this);
      }

   }

   public void text(String value, boolean needSP) throws IOException {
      this.seenText = true;
      super.text(value, needSP);
   }

   public void text(Pcdata value, boolean needSP) throws IOException {
      this.seenText = true;
      super.text(value, needSP);
   }

   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
      this.write(10);
      super.endDocument(fragment);
   }
}
