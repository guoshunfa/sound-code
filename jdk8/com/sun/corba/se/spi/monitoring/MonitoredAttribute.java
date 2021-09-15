package com.sun.corba.se.spi.monitoring;

public interface MonitoredAttribute {
   MonitoredAttributeInfo getAttributeInfo();

   void setValue(Object var1);

   Object getValue();

   String getName();

   void clearState();
}
