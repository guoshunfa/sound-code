package com.sun.corba.se.spi.monitoring;

import java.util.Collection;

public interface MonitoredObject {
   String getName();

   String getDescription();

   void addChild(MonitoredObject var1);

   void removeChild(String var1);

   MonitoredObject getChild(String var1);

   Collection getChildren();

   void setParent(MonitoredObject var1);

   MonitoredObject getParent();

   void addAttribute(MonitoredAttribute var1);

   void removeAttribute(String var1);

   MonitoredAttribute getAttribute(String var1);

   Collection getAttributes();

   void clearState();
}
