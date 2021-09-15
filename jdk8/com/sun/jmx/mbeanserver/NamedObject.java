package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

public class NamedObject {
   private final ObjectName name;
   private final DynamicMBean object;

   public NamedObject(ObjectName var1, DynamicMBean var2) {
      if (var1.isPattern()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + var1.toString()));
      } else {
         this.name = var1;
         this.object = var2;
      }
   }

   public NamedObject(String var1, DynamicMBean var2) throws MalformedObjectNameException {
      ObjectName var3 = new ObjectName(var1);
      if (var3.isPattern()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + var3.toString()));
      } else {
         this.name = var3;
         this.object = var2;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof NamedObject)) {
         return false;
      } else {
         NamedObject var2 = (NamedObject)var1;
         return this.name.equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public ObjectName getName() {
      return this.name;
   }

   public DynamicMBean getObject() {
      return this.object;
   }
}
