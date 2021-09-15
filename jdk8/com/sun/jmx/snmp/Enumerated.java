package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Enumerated implements Serializable {
   protected int value;

   public Enumerated() throws IllegalArgumentException {
      Enumeration var1 = this.getIntTable().keys();
      if (var1.hasMoreElements()) {
         this.value = (Integer)var1.nextElement();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Enumerated(int var1) throws IllegalArgumentException {
      if (this.getIntTable().get(new Integer(var1)) == null) {
         throw new IllegalArgumentException();
      } else {
         this.value = var1;
      }
   }

   public Enumerated(Integer var1) throws IllegalArgumentException {
      if (this.getIntTable().get(var1) == null) {
         throw new IllegalArgumentException();
      } else {
         this.value = var1;
      }
   }

   public Enumerated(String var1) throws IllegalArgumentException {
      Integer var2 = (Integer)this.getStringTable().get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException();
      } else {
         this.value = var2;
      }
   }

   public int intValue() {
      return this.value;
   }

   public Enumeration<Integer> valueIndexes() {
      return this.getIntTable().keys();
   }

   public Enumeration<String> valueStrings() {
      return this.getStringTable().keys();
   }

   public boolean equals(Object var1) {
      return var1 != null && this.getClass() == var1.getClass() && this.value == ((Enumerated)var1).value;
   }

   public int hashCode() {
      String var1 = this.getClass().getName() + String.valueOf(this.value);
      return var1.hashCode();
   }

   public String toString() {
      return (String)this.getIntTable().get(new Integer(this.value));
   }

   protected abstract Hashtable<Integer, String> getIntTable();

   protected abstract Hashtable<String, Integer> getStringTable();
}
