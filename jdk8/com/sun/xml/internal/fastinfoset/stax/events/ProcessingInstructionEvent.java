package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends EventBase implements ProcessingInstruction {
   private String targetName;
   private String _data;

   public ProcessingInstructionEvent() {
      this.init();
   }

   public ProcessingInstructionEvent(String targetName, String data) {
      this.targetName = targetName;
      this._data = data;
      this.init();
   }

   protected void init() {
      this.setEventType(3);
   }

   public String getTarget() {
      return this.targetName;
   }

   public void setTarget(String targetName) {
      this.targetName = targetName;
   }

   public void setData(String data) {
      this._data = data;
   }

   public String getData() {
      return this._data;
   }

   public String toString() {
      if (this._data != null && this.targetName != null) {
         return "<?" + this.targetName + " " + this._data + "?>";
      } else if (this.targetName != null) {
         return "<?" + this.targetName + "?>";
      } else {
         return this._data != null ? "<?" + this._data + "?>" : "<??>";
      }
   }
}
