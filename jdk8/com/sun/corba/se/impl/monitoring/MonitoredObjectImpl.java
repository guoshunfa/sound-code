package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MonitoredObjectImpl implements MonitoredObject {
   private final String name;
   private final String description;
   private Map children = new HashMap();
   private Map monitoredAttributes = new HashMap();
   private MonitoredObject parent = null;

   MonitoredObjectImpl(String var1, String var2) {
      this.name = var1;
      this.description = var2;
   }

   public MonitoredObject getChild(String var1) {
      synchronized(this) {
         return (MonitoredObject)this.children.get(var1);
      }
   }

   public Collection getChildren() {
      synchronized(this) {
         return this.children.values();
      }
   }

   public void addChild(MonitoredObject var1) {
      if (var1 != null) {
         synchronized(this) {
            this.children.put(var1.getName(), var1);
            var1.setParent(this);
         }
      }

   }

   public void removeChild(String var1) {
      if (var1 != null) {
         synchronized(this) {
            this.children.remove(var1);
         }
      }

   }

   public synchronized MonitoredObject getParent() {
      return this.parent;
   }

   public synchronized void setParent(MonitoredObject var1) {
      this.parent = var1;
   }

   public MonitoredAttribute getAttribute(String var1) {
      synchronized(this) {
         return (MonitoredAttribute)this.monitoredAttributes.get(var1);
      }
   }

   public Collection getAttributes() {
      synchronized(this) {
         return this.monitoredAttributes.values();
      }
   }

   public void addAttribute(MonitoredAttribute var1) {
      if (var1 != null) {
         synchronized(this) {
            this.monitoredAttributes.put(var1.getName(), var1);
         }
      }

   }

   public void removeAttribute(String var1) {
      if (var1 != null) {
         synchronized(this) {
            this.monitoredAttributes.remove(var1);
         }
      }

   }

   public void clearState() {
      synchronized(this) {
         Iterator var2 = this.monitoredAttributes.values().iterator();

         while(var2.hasNext()) {
            ((MonitoredAttribute)var2.next()).clearState();
         }

         var2 = this.children.values().iterator();

         while(var2.hasNext()) {
            ((MonitoredObject)var2.next()).clearState();
         }

      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }
}
