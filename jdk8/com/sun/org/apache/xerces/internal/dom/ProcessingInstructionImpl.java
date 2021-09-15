package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends CharacterDataImpl implements ProcessingInstruction {
   static final long serialVersionUID = 7554435174099981510L;
   protected String target;

   public ProcessingInstructionImpl(CoreDocumentImpl ownerDoc, String target, String data) {
      super(ownerDoc, data);
      this.target = target;
   }

   public short getNodeType() {
      return 7;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.target;
   }

   public String getTarget() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.target;
   }

   public String getData() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.data;
   }

   public void setData(String data) {
      this.setNodeValue(data);
   }

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.ownerNode.getBaseURI();
   }
}
