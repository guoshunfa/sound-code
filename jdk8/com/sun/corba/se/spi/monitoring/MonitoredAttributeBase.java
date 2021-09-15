package com.sun.corba.se.spi.monitoring;

public abstract class MonitoredAttributeBase implements MonitoredAttribute {
   String name;
   MonitoredAttributeInfo attributeInfo;

   public MonitoredAttributeBase(String var1, MonitoredAttributeInfo var2) {
      this.name = var1;
      this.attributeInfo = var2;
   }

   MonitoredAttributeBase(String var1) {
      this.name = var1;
   }

   void setMonitoredAttributeInfo(MonitoredAttributeInfo var1) {
      this.attributeInfo = var1;
   }

   public void clearState() {
   }

   public abstract Object getValue();

   public void setValue(Object var1) {
      if (!this.attributeInfo.isWritable()) {
         throw new IllegalStateException("The Attribute " + this.name + " is not Writable...");
      } else {
         throw new IllegalStateException("The method implementation is not provided for the attribute " + this.name);
      }
   }

   public MonitoredAttributeInfo getAttributeInfo() {
      return this.attributeInfo;
   }

   public String getName() {
      return this.name;
   }
}
