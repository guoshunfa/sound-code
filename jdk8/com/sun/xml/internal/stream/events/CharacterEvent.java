package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Characters;

public class CharacterEvent extends DummyEvent implements Characters {
   private String fData;
   private boolean fIsCData;
   private boolean fIsIgnorableWhitespace;
   private boolean fIsSpace = false;
   private boolean fCheckIfSpaceNeeded = true;

   public CharacterEvent() {
      this.fIsCData = false;
      this.init();
   }

   public CharacterEvent(String data) {
      this.fIsCData = false;
      this.init();
      this.fData = data;
   }

   public CharacterEvent(String data, boolean flag) {
      this.init();
      this.fData = data;
      this.fIsCData = flag;
   }

   public CharacterEvent(String data, boolean flag, boolean isIgnorableWhiteSpace) {
      this.init();
      this.fData = data;
      this.fIsCData = flag;
      this.fIsIgnorableWhitespace = isIgnorableWhiteSpace;
   }

   protected void init() {
      this.setEventType(4);
   }

   public String getData() {
      return this.fData;
   }

   public void setData(String data) {
      this.fData = data;
      this.fCheckIfSpaceNeeded = true;
   }

   public boolean isCData() {
      return this.fIsCData;
   }

   public String toString() {
      return this.fIsCData ? "<![CDATA[" + this.getData() + "]]>" : this.fData;
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      if (this.fIsCData) {
         writer.write("<![CDATA[" + this.getData() + "]]>");
      } else {
         this.charEncode(writer, this.fData);
      }

   }

   public boolean isIgnorableWhiteSpace() {
      return this.fIsIgnorableWhitespace;
   }

   public boolean isWhiteSpace() {
      if (this.fCheckIfSpaceNeeded) {
         this.checkWhiteSpace();
         this.fCheckIfSpaceNeeded = false;
      }

      return this.fIsSpace;
   }

   private void checkWhiteSpace() {
      if (this.fData != null && this.fData.length() > 0) {
         this.fIsSpace = true;

         for(int i = 0; i < this.fData.length(); ++i) {
            if (!XMLChar.isSpace(this.fData.charAt(i))) {
               this.fIsSpace = false;
               break;
            }
         }
      }

   }
}
