package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends DummyEvent implements ProcessingInstruction {
   private String fName;
   private String fContent;

   public ProcessingInstructionEvent() {
      this.init();
   }

   public ProcessingInstructionEvent(String targetName, String data) {
      this(targetName, data, (Location)null);
   }

   public ProcessingInstructionEvent(String targetName, String data, Location loc) {
      this.init();
      this.fName = targetName;
      this.fContent = data;
      this.setLocation(loc);
   }

   protected void init() {
      this.setEventType(3);
   }

   public String getTarget() {
      return this.fName;
   }

   public void setTarget(String targetName) {
      this.fName = targetName;
   }

   public void setData(String data) {
      this.fContent = data;
   }

   public String getData() {
      return this.fContent;
   }

   public String toString() {
      if (this.fContent != null && this.fName != null) {
         return "<?" + this.fName + " " + this.fContent + "?>";
      } else if (this.fName != null) {
         return "<?" + this.fName + "?>";
      } else {
         return this.fContent != null ? "<?" + this.fContent + "?>" : "<??>";
      }
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(this.toString());
   }
}
