package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent extends DummyEvent implements StartDocument {
   protected String fSystemId;
   protected String fEncodingScheam;
   protected boolean fStandalone;
   protected String fVersion;
   private boolean fEncodingSchemeSet = false;
   private boolean fStandaloneSet = false;
   private boolean nestedCall = false;

   public StartDocumentEvent() {
      this.init("UTF-8", "1.0", true, (Location)null);
   }

   public StartDocumentEvent(String encoding) {
      this.init(encoding, "1.0", true, (Location)null);
   }

   public StartDocumentEvent(String encoding, String version) {
      this.init(encoding, version, true, (Location)null);
   }

   public StartDocumentEvent(String encoding, String version, boolean standalone) {
      this.fStandaloneSet = true;
      this.init(encoding, version, standalone, (Location)null);
   }

   public StartDocumentEvent(String encoding, String version, boolean standalone, Location loc) {
      this.fStandaloneSet = true;
      this.init(encoding, version, standalone, loc);
   }

   protected void init(String encoding, String version, boolean standalone, Location loc) {
      this.setEventType(7);
      this.fEncodingScheam = encoding;
      this.fVersion = version;
      this.fStandalone = standalone;
      if (encoding != null && !encoding.equals("")) {
         this.fEncodingSchemeSet = true;
      } else {
         this.fEncodingSchemeSet = false;
         this.fEncodingScheam = "UTF-8";
      }

      this.fLocation = loc;
   }

   public String getSystemId() {
      return this.fLocation == null ? "" : this.fLocation.getSystemId();
   }

   public String getCharacterEncodingScheme() {
      return this.fEncodingScheam;
   }

   public boolean isStandalone() {
      return this.fStandalone;
   }

   public String getVersion() {
      return this.fVersion;
   }

   public void setStandalone(boolean flag) {
      this.fStandaloneSet = true;
      this.fStandalone = flag;
   }

   public void setStandalone(String s) {
      this.fStandaloneSet = true;
      if (s == null) {
         this.fStandalone = true;
      } else {
         if (s.equals("yes")) {
            this.fStandalone = true;
         } else {
            this.fStandalone = false;
         }

      }
   }

   public boolean encodingSet() {
      return this.fEncodingSchemeSet;
   }

   public boolean standaloneSet() {
      return this.fStandaloneSet;
   }

   public void setEncoding(String encoding) {
      this.fEncodingScheam = encoding;
   }

   void setDeclaredEncoding(boolean value) {
      this.fEncodingSchemeSet = value;
   }

   public void setVersion(String s) {
      this.fVersion = s;
   }

   void clear() {
      this.fEncodingScheam = "UTF-8";
      this.fStandalone = true;
      this.fVersion = "1.0";
      this.fEncodingSchemeSet = false;
      this.fStandaloneSet = false;
   }

   public String toString() {
      String s = "<?xml version=\"" + this.fVersion + "\"";
      s = s + " encoding='" + this.fEncodingScheam + "'";
      if (this.fStandaloneSet) {
         if (this.fStandalone) {
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

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(this.toString());
   }
}
