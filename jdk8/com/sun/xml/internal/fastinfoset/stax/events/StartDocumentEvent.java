package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends EventBase implements StartDocument {
   protected String _systemId;
   protected String _encoding;
   protected boolean _standalone;
   protected String _version;
   private boolean _encodingSet;
   private boolean _standaloneSet;

   public void reset() {
      this._encoding = "UTF-8";
      this._standalone = true;
      this._version = "1.0";
      this._encodingSet = false;
      this._standaloneSet = false;
   }

   public StartDocumentEvent() {
      this((String)null, (String)null);
   }

   public StartDocumentEvent(String encoding) {
      this(encoding, (String)null);
   }

   public StartDocumentEvent(String encoding, String version) {
      this._encoding = "UTF-8";
      this._standalone = true;
      this._version = "1.0";
      this._encodingSet = false;
      this._standaloneSet = false;
      if (encoding != null) {
         this._encoding = encoding;
         this._encodingSet = true;
      }

      if (version != null) {
         this._version = version;
      }

      this.setEventType(7);
   }

   public String getSystemId() {
      return super.getSystemId();
   }

   public String getCharacterEncodingScheme() {
      return this._encoding;
   }

   public boolean encodingSet() {
      return this._encodingSet;
   }

   public boolean isStandalone() {
      return this._standalone;
   }

   public boolean standaloneSet() {
      return this._standaloneSet;
   }

   public String getVersion() {
      return this._version;
   }

   public void setStandalone(boolean standalone) {
      this._standaloneSet = true;
      this._standalone = standalone;
   }

   public void setStandalone(String s) {
      this._standaloneSet = true;
      if (s == null) {
         this._standalone = true;
      } else {
         if (s.equals("yes")) {
            this._standalone = true;
         } else {
            this._standalone = false;
         }

      }
   }

   public void setEncoding(String encoding) {
      this._encoding = encoding;
      this._encodingSet = true;
   }

   void setDeclaredEncoding(boolean value) {
      this._encodingSet = value;
   }

   public void setVersion(String s) {
      this._version = s;
   }

   void clear() {
      this._encoding = "UTF-8";
      this._standalone = true;
      this._version = "1.0";
      this._encodingSet = false;
      this._standaloneSet = false;
   }

   public String toString() {
      String s = "<?xml version=\"" + this._version + "\"";
      s = s + " encoding='" + this._encoding + "'";
      if (this._standaloneSet) {
         if (this._standalone) {
            s = s + " standalone='yes'?>";
         } else {
            s = s + " standalone='no'?>";
         }
      } else {
         s = s + "?>";
      }

      return s;
   }

   public boolean isStartDocument() {
      return true;
   }
}
