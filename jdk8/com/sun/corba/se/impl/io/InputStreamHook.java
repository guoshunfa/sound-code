package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class InputStreamHook extends ObjectInputStream {
   static final OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
   static final UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
   protected InputStreamHook.ReadObjectState readObjectState;
   protected static final InputStreamHook.ReadObjectState DEFAULT_STATE = new InputStreamHook.DefaultState();
   protected static final InputStreamHook.ReadObjectState IN_READ_OBJECT_OPT_DATA = new InputStreamHook.InReadObjectOptionalDataState();
   protected static final InputStreamHook.ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA = new InputStreamHook.InReadObjectNoMoreOptionalDataState();
   protected static final InputStreamHook.ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT = new InputStreamHook.InReadObjectDefaultsSentState();
   protected static final InputStreamHook.ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT = new InputStreamHook.NoReadObjectDefaultsSentState();
   protected static final InputStreamHook.ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED = new InputStreamHook.InReadObjectRemoteDidNotUseWriteObjectState();
   protected static final InputStreamHook.ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM = new InputStreamHook.InReadObjectPastDefaultsRemoteDidNotUseWOState();

   public InputStreamHook() throws IOException {
      this.readObjectState = DEFAULT_STATE;
   }

   public void defaultReadObject() throws IOException, ClassNotFoundException, NotActiveException {
      this.readObjectState.beginDefaultReadObject(this);
      this.defaultReadObjectDelegate();
      this.readObjectState.endDefaultReadObject(this);
   }

   abstract void defaultReadObjectDelegate();

   abstract void readFields(Map var1) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException;

   public ObjectInputStream.GetField readFields() throws IOException, ClassNotFoundException, NotActiveException {
      HashMap var1 = new HashMap();
      this.readFields(var1);
      this.readObjectState.endDefaultReadObject(this);
      return new InputStreamHook.HookGetFields(var1);
   }

   protected void setState(InputStreamHook.ReadObjectState var1) {
      this.readObjectState = var1;
   }

   protected abstract byte getStreamFormatVersion();

   abstract InputStream getOrbStream();

   protected void throwOptionalDataIncompatibleException() {
      throw omgWrapper.rmiiiopOptionalDataIncompatible2();
   }

   protected static class NoReadObjectDefaultsSentState extends InputStreamHook.ReadObjectState {
      public void endUnmarshalCustomValue(InputStreamHook var1) throws IOException {
         if (var1.getStreamFormatVersion() == 2) {
            ((ValueInputStream)var1.getOrbStream()).start_value();
            ((ValueInputStream)var1.getOrbStream()).end_value();
         }

         var1.setState(InputStreamHook.DEFAULT_STATE);
      }
   }

   protected static class InReadObjectNoMoreOptionalDataState extends InputStreamHook.InReadObjectOptionalDataState {
      public void readData(InputStreamHook var1) throws IOException {
         var1.throwOptionalDataIncompatibleException();
      }
   }

   protected static class InReadObjectOptionalDataState extends InputStreamHook.ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) {
         throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
      }

      public void endUnmarshalCustomValue(InputStreamHook var1) throws IOException {
         if (var1.getStreamFormatVersion() == 2) {
            ((ValueInputStream)var1.getOrbStream()).end_value();
         }

         var1.setState(InputStreamHook.DEFAULT_STATE);
      }

      public void beginDefaultReadObject(InputStreamHook var1) throws IOException {
         throw new StreamCorruptedException("Default data not sent or already read/passed");
      }
   }

   protected static class InReadObjectDefaultsSentState extends InputStreamHook.ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) {
         throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
      }

      public void endUnmarshalCustomValue(InputStreamHook var1) {
         if (var1.getStreamFormatVersion() == 2) {
            ((ValueInputStream)var1.getOrbStream()).start_value();
            ((ValueInputStream)var1.getOrbStream()).end_value();
         }

         var1.setState(InputStreamHook.DEFAULT_STATE);
      }

      public void endDefaultReadObject(InputStreamHook var1) throws IOException {
         if (var1.getStreamFormatVersion() == 2) {
            ((ValueInputStream)var1.getOrbStream()).start_value();
         }

         var1.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
      }

      public void readData(InputStreamHook var1) throws IOException {
         ORB var2 = var1.getOrbStream().orb();
         if (var2 != null && var2 instanceof com.sun.corba.se.spi.orb.ORB) {
            ORBVersion var3 = ((com.sun.corba.se.spi.orb.ORB)var2).getORBVersion();
            if (ORBVersionFactory.getPEORB().compareTo(var3) <= 0 || var3.equals(ORBVersionFactory.getFOREIGN())) {
               throw new StreamCorruptedException("Default data must be read first");
            }
         } else {
            throw new StreamCorruptedException("Default data must be read first");
         }
      }
   }

   protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState extends InputStreamHook.ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) {
         throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
      }

      public void beginDefaultReadObject(InputStreamHook var1) throws IOException {
         throw new StreamCorruptedException("Default data already read");
      }

      public void readData(InputStreamHook var1) {
         var1.throwOptionalDataIncompatibleException();
      }
   }

   protected static class InReadObjectRemoteDidNotUseWriteObjectState extends InputStreamHook.ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) {
         throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
      }

      public void endDefaultReadObject(InputStreamHook var1) {
         var1.setState(InputStreamHook.IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM);
      }

      public void readData(InputStreamHook var1) {
         var1.throwOptionalDataIncompatibleException();
      }
   }

   protected static class DefaultState extends InputStreamHook.ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) throws IOException {
         if (var3) {
            if (var2) {
               var1.setState(InputStreamHook.IN_READ_OBJECT_DEFAULTS_SENT);
            } else {
               try {
                  if (var1.getStreamFormatVersion() == 2) {
                     ((ValueInputStream)var1.getOrbStream()).start_value();
                  }
               } catch (Exception var5) {
               }

               var1.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
            }
         } else {
            if (!var2) {
               throw new StreamCorruptedException("No default data sent");
            }

            var1.setState(InputStreamHook.NO_READ_OBJECT_DEFAULTS_SENT);
         }

      }
   }

   protected static class ReadObjectState {
      public void beginUnmarshalCustomValue(InputStreamHook var1, boolean var2, boolean var3) throws IOException {
      }

      public void endUnmarshalCustomValue(InputStreamHook var1) throws IOException {
      }

      public void beginDefaultReadObject(InputStreamHook var1) throws IOException {
      }

      public void endDefaultReadObject(InputStreamHook var1) throws IOException {
      }

      public void readData(InputStreamHook var1) throws IOException {
      }
   }

   private class HookGetFields extends ObjectInputStream.GetField {
      private Map fields = null;

      HookGetFields(Map var2) {
         this.fields = var2;
      }

      public java.io.ObjectStreamClass getObjectStreamClass() {
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
}
