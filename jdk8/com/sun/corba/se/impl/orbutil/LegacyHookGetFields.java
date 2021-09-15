package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Hashtable;

class LegacyHookGetFields extends ObjectInputStream.GetField {
   private Hashtable fields = null;

   LegacyHookGetFields(Hashtable var1) {
      this.fields = var1;
   }

   public ObjectStreamClass getObjectStreamClass() {
      return null;
   }

   public boolean defaulted(String var1) throws IOException, IllegalArgumentException {
      return !this.fields.containsKey(var1);
   }

   public boolean get(String var1, boolean var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Boolean)this.fields.get(var1);
   }

   public char get(String var1, char var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Character)this.fields.get(var1);
   }

   public byte get(String var1, byte var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Byte)this.fields.get(var1);
   }

   public short get(String var1, short var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Short)this.fields.get(var1);
   }

   public int get(String var1, int var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Integer)this.fields.get(var1);
   }

   public long get(String var1, long var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Long)this.fields.get(var1);
   }

   public float get(String var1, float var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Float)this.fields.get(var1);
   }

   public double get(String var1, double var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : (Double)this.fields.get(var1);
   }

   public Object get(String var1, Object var2) throws IOException, IllegalArgumentException {
      return this.defaulted(var1) ? var2 : this.fields.get(var1);
   }

   public String toString() {
      return this.fields.toString();
   }
}
