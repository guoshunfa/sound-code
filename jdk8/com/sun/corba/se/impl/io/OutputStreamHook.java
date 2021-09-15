package com.sun.corba.se.impl.io;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class OutputStreamHook extends ObjectOutputStream {
   private OutputStreamHook.HookPutFields putFields = null;
   protected byte streamFormatVersion = 1;
   protected OutputStreamHook.WriteObjectState writeObjectState;
   protected static final OutputStreamHook.WriteObjectState NOT_IN_WRITE_OBJECT = new OutputStreamHook.DefaultState();
   protected static final OutputStreamHook.WriteObjectState IN_WRITE_OBJECT = new OutputStreamHook.InWriteObjectState();
   protected static final OutputStreamHook.WriteObjectState WROTE_DEFAULT_DATA = new OutputStreamHook.WroteDefaultDataState();
   protected static final OutputStreamHook.WriteObjectState WROTE_CUSTOM_DATA = new OutputStreamHook.WroteCustomDataState();

   abstract void writeField(ObjectStreamField var1, Object var2) throws IOException;

   public OutputStreamHook() throws IOException {
      this.writeObjectState = NOT_IN_WRITE_OBJECT;
   }

   public void defaultWriteObject() throws IOException {
      this.writeObjectState.defaultWriteObject(this);
      this.defaultWriteObjectDelegate();
   }

   public abstract void defaultWriteObjectDelegate();

   public ObjectOutputStream.PutField putFields() throws IOException {
      if (this.putFields == null) {
         this.putFields = new OutputStreamHook.HookPutFields();
      }

      return this.putFields;
   }

   public byte getStreamFormatVersion() {
      return this.streamFormatVersion;
   }

   abstract ObjectStreamField[] getFieldsNoCopy();

   public void writeFields() throws IOException {
      this.writeObjectState.defaultWriteObject(this);
      if (this.putFields != null) {
         this.putFields.write(this);
      } else {
         throw new NotActiveException("no current PutField object");
      }
   }

   abstract OutputStream getOrbStream();

   protected abstract void beginOptionalCustomData();

   protected void setState(OutputStreamHook.WriteObjectState var1) {
      this.writeObjectState = var1;
   }

   protected static class WroteCustomDataState extends OutputStreamHook.InWriteObjectState {
      public void exitWriteObject(OutputStreamHook var1) throws IOException {
         if (var1.getStreamFormatVersion() == 2) {
            ((ValueOutputStream)var1.getOrbStream()).end_value();
         }

         var1.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
      }

      public void defaultWriteObject(OutputStreamHook var1) throws IOException {
         throw new IOException("Cannot call defaultWriteObject/writeFields after writing custom data in RMI-IIOP");
      }

      public void writeData(OutputStreamHook var1) throws IOException {
      }
   }

   protected static class WroteDefaultDataState extends OutputStreamHook.InWriteObjectState {
      public void exitWriteObject(OutputStreamHook var1) throws IOException {
         if (var1.getStreamFormatVersion() == 2) {
            var1.getOrbStream().write_long(0);
         }

         var1.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
      }

      public void defaultWriteObject(OutputStreamHook var1) throws IOException {
         throw new IOException("Called defaultWriteObject/writeFields twice");
      }

      public void writeData(OutputStreamHook var1) throws IOException {
         var1.beginOptionalCustomData();
         var1.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
      }
   }

   protected static class InWriteObjectState extends OutputStreamHook.WriteObjectState {
      public void enterWriteObject(OutputStreamHook var1) throws IOException {
         throw new IOException("Internal state failure: Entered writeObject twice");
      }

      public void exitWriteObject(OutputStreamHook var1) throws IOException {
         var1.getOrbStream().write_boolean(false);
         if (var1.getStreamFormatVersion() == 2) {
            var1.getOrbStream().write_long(0);
         }

         var1.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
      }

      public void defaultWriteObject(OutputStreamHook var1) throws IOException {
         var1.getOrbStream().write_boolean(true);
         var1.setState(OutputStreamHook.WROTE_DEFAULT_DATA);
      }

      public void writeData(OutputStreamHook var1) throws IOException {
         var1.getOrbStream().write_boolean(false);
         var1.beginOptionalCustomData();
         var1.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
      }
   }

   protected static class DefaultState extends OutputStreamHook.WriteObjectState {
      public void enterWriteObject(OutputStreamHook var1) throws IOException {
         var1.setState(OutputStreamHook.IN_WRITE_OBJECT);
      }
   }

   protected static class WriteObjectState {
      public void enterWriteObject(OutputStreamHook var1) throws IOException {
      }

      public void exitWriteObject(OutputStreamHook var1) throws IOException {
      }

      public void defaultWriteObject(OutputStreamHook var1) throws IOException {
      }

      public void writeData(OutputStreamHook var1) throws IOException {
      }
   }

   private class HookPutFields extends ObjectOutputStream.PutField {
      private Map<String, Object> fields;

      private HookPutFields() {
         this.fields = new HashMap();
      }

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
         OutputStreamHook var2 = (OutputStreamHook)var1;
         ObjectStreamField[] var3 = var2.getFieldsNoCopy();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Object var5 = this.fields.get(var3[var4].getName());
            var2.writeField(var3[var4], var5);
         }

      }

      // $FF: synthetic method
      HookPutFields(Object var2) {
         this();
      }
   }
}
