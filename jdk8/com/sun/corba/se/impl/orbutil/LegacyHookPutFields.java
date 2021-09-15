package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

class LegacyHookPutFields extends ObjectOutputStream.PutField {
   private Hashtable fields = new Hashtable();

   public void put(String var1, boolean var2) {
      this.fields.put(var1, new Boolean(var2));
   }

   public void put(String var1, char var2) {
      this.fields.put(var1, new Character(var2));
   }

   public void put(String var1, byte var2) {
      this.fields.put(var1, new Byte(var2));
   }

   public void put(String var1, short var2) {
      this.fields.put(var1, new Short(var2));
   }

   public void put(String var1, int var2) {
      this.fields.put(var1, new Integer(var2));
   }

   public void put(String var1, long var2) {
      this.fields.put(var1, new Long(var2));
   }

   public void put(String var1, float var2) {
      this.fields.put(var1, new Float(var2));
   }

   public void put(String var1, double var2) {
      this.fields.put(var1, new Double(var2));
   }

   public void put(String var1, Object var2) {
      this.fields.put(var1, var2);
   }

   public void write(ObjectOutput var1) throws IOException {
      var1.writeObject(this.fields);
   }
}
