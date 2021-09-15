package com.sun.xml.internal.stream.events;

import javax.xml.stream.Location;

public class LocationImpl implements Location {
   String systemId;
   String publicId;
   int colNo;
   int lineNo;
   int charOffset;

   LocationImpl(Location loc) {
      this.systemId = loc.getSystemId();
      this.publicId = loc.getPublicId();
      this.lineNo = loc.getLineNumber();
      this.colNo = loc.getColumnNumber();
      this.charOffset = loc.getCharacterOffset();
   }

   public int getCharacterOffset() {
      return this.charOffset;
   }

   public int getColumnNumber() {
      return this.colNo;
   }

   public int getLineNumber() {
      return this.lineNo;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String toString() {
      StringBuffer sbuffer = new StringBuffer();
      sbuffer.append("Line number = " + this.getLineNumber());
      sbuffer.append("\n");
      sbuffer.append("Column number = " + this.getColumnNumber());
      sbuffer.append("\n");
      sbuffer.append("System Id = " + this.getSystemId());
      sbuffer.append("\n");
      sbuffer.append("Public Id = " + this.getPublicId());
      sbuffer.append("\n");
      sbuffer.append("CharacterOffset = " + this.getCharacterOffset());
      sbuffer.append("\n");
      return sbuffer.toString();
   }
}
