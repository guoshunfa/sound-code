package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public class SimpleLocator implements XMLLocator {
   String lsid;
   String esid;
   int line;
   int column;
   int charOffset;

   public SimpleLocator() {
   }

   public SimpleLocator(String lsid, String esid, int line, int column) {
      this(lsid, esid, line, column, -1);
   }

   public void setValues(String lsid, String esid, int line, int column) {
      this.setValues(lsid, esid, line, column, -1);
   }

   public SimpleLocator(String lsid, String esid, int line, int column, int offset) {
      this.line = line;
      this.column = column;
      this.lsid = lsid;
      this.esid = esid;
      this.charOffset = offset;
   }

   public void setValues(String lsid, String esid, int line, int column, int offset) {
      this.line = line;
      this.column = column;
      this.lsid = lsid;
      this.esid = esid;
      this.charOffset = offset;
   }

   public int getLineNumber() {
      return this.line;
   }

   public int getColumnNumber() {
      return this.column;
   }

   public int getCharacterOffset() {
      return this.charOffset;
   }

   public String getPublicId() {
      return null;
   }

   public String getExpandedSystemId() {
      return this.esid;
   }

   public String getLiteralSystemId() {
      return this.lsid;
   }

   public String getBaseSystemId() {
      return null;
   }

   public void setColumnNumber(int col) {
      this.column = col;
   }

   public void setLineNumber(int line) {
      this.line = line;
   }

   public void setCharacterOffset(int offset) {
      this.charOffset = offset;
   }

   public void setBaseSystemId(String systemId) {
   }

   public void setExpandedSystemId(String systemId) {
      this.esid = systemId;
   }

   public void setLiteralSystemId(String systemId) {
      this.lsid = systemId;
   }

   public void setPublicId(String publicId) {
   }

   public String getEncoding() {
      return null;
   }

   public String getXMLVersion() {
      return null;
   }
}
