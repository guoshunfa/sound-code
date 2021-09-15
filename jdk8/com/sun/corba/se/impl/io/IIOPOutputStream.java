package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.Bridge;

public class IIOPOutputStream extends OutputStreamHook {
   private UtilSystemException wrapper = UtilSystemException.get("rpc.encoding");
   private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {
         return Bridge.get();
      }
   });
   private OutputStream orbStream;
   private Object currentObject = null;
   private ObjectStreamClass currentClassDesc = null;
   private int recursionDepth = 0;
   private int simpleWriteDepth = 0;
   private IOException abortIOException = null;
   private Stack classDescStack = new Stack();
   private Object[] writeObjectArgList = new Object[]{this};

   public IIOPOutputStream() throws IOException {
   }

   protected void beginOptionalCustomData() {
      if (this.streamFormatVersion == 2) {
         ValueOutputStream var1 = (ValueOutputStream)this.orbStream;
         var1.start_value(this.currentClassDesc.getRMIIIOPOptionalDataRepId());
      }

   }

   final void setOrbStream(OutputStream var1) {
      this.orbStream = var1;
   }

   final OutputStream getOrbStream() {
      return this.orbStream;
   }

   final void increaseRecursionDepth() {
      ++this.recursionDepth;
   }

   final int decreaseRecursionDepth() {
      return --this.recursionDepth;
   }

   public final void writeObjectOverride(Object var1) throws IOException {
      this.writeObjectState.writeData(this);
      Util.writeAbstractObject(this.orbStream, var1);
   }

   public final void simpleWriteObject(Object var1, byte var2) {
      byte var3 = this.streamFormatVersion;
      this.streamFormatVersion = var2;
      Object var4 = this.currentObject;
      ObjectStreamClass var5 = this.currentClassDesc;
      ++this.simpleWriteDepth;

      try {
         this.outputObject(var1);
      } catch (IOException var10) {
         if (this.abortIOException == null) {
            this.abortIOException = var10;
         }
      } finally {
         this.streamFormatVersion = var3;
         --this.simpleWriteDepth;
         this.currentObject = var4;
         this.currentClassDesc = var5;
      }

      IOException var6 = this.abortIOException;
      if (this.simpleWriteDepth == 0) {
         this.abortIOException = null;
      }

      if (var6 != null) {
         bridge.throwException(var6);
      }

   }

   ObjectStreamField[] getFieldsNoCopy() {
      return this.currentClassDesc.getFieldsNoCopy();
   }

   public final void defaultWriteObjectDelegate() {
      try {
         if (this.currentObject == null || this.currentClassDesc == null) {
            throw new NotActiveException("defaultWriteObjectDelegate");
         }

         ObjectStreamField[] var1 = this.currentClassDesc.getFieldsNoCopy();
         if (var1.length > 0) {
            this.outputClassFields(this.currentObject, this.currentClassDesc.forClass(), var1);
         }
      } catch (IOException var2) {
         bridge.throwException(var2);
      }

   }

   public final boolean enableReplaceObjectDelegate(boolean var1) {
      return false;
   }

   protected final void annotateClass(Class<?> var1) throws IOException {
      throw new IOException("Method annotateClass not supported");
   }

   public final void close() throws IOException {
   }

   protected final void drain() throws IOException {
   }

   public final void flush() throws IOException {
      try {
         this.orbStream.flush();
      } catch (Error var3) {
         IOException var2 = new IOException(var3.getMessage());
         var2.initCause(var3);
         throw var2;
      }
   }

   protected final Object replaceObject(Object var1) throws IOException {
      throw new IOException("Method replaceObject not supported");
   }

   public final void reset() throws IOException {
      try {
         if (this.currentObject == null && this.currentClassDesc == null) {
            this.abortIOException = null;
            if (this.classDescStack == null) {
               this.classDescStack = new Stack();
            } else {
               this.classDescStack.setSize(0);
            }

         } else {
            throw new IOException("Illegal call to reset");
         }
      } catch (Error var3) {
         IOException var2 = new IOException(var3.getMessage());
         var2.initCause(var3);
         throw var2;
      }
   }

   public final void write(byte[] var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_octet_array(var1, 0, var1.length);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void write(byte[] var1, int var2, int var3) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_octet_array(var1, var2, var3);
      } catch (Error var6) {
         IOException var5 = new IOException(var6.getMessage());
         var5.initCause(var6);
         throw var5;
      }
   }

   public final void write(int var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_octet((byte)(var1 & 255));
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeBoolean(boolean var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_boolean(var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeByte(int var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_octet((byte)var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeBytes(String var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         byte[] var2 = var1.getBytes();
         this.orbStream.write_octet_array(var2, 0, var2.length);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeChar(int var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_wchar((char)var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeChars(String var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         char[] var2 = var1.toCharArray();
         this.orbStream.write_wchar_array(var2, 0, var2.length);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeDouble(double var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_double(var1);
      } catch (Error var5) {
         IOException var4 = new IOException(var5.getMessage());
         var4.initCause(var5);
         throw var4;
      }
   }

   public final void writeFloat(float var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_float(var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeInt(int var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_long(var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public final void writeLong(long var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_longlong(var1);
      } catch (Error var5) {
         IOException var4 = new IOException(var5.getMessage());
         var4.initCause(var5);
         throw var4;
      }
   }

   public final void writeShort(int var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.orbStream.write_short((short)var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   protected final void writeStreamHeader() throws IOException {
   }

   protected void internalWriteUTF(org.omg.CORBA.portable.OutputStream var1, String var2) {
      var1.write_wstring(var2);
   }

   public final void writeUTF(String var1) throws IOException {
      try {
         this.writeObjectState.writeData(this);
         this.internalWriteUTF(this.orbStream, var1);
      } catch (Error var4) {
         IOException var3 = new IOException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   private boolean checkSpecialClasses(Object var1) throws IOException {
      if (var1 instanceof ObjectStreamClass) {
         throw new IOException("Serialization of ObjectStreamClass not supported");
      } else {
         return false;
      }
   }

   private boolean checkSubstitutableSpecialClasses(Object var1) throws IOException {
      if (var1 instanceof String) {
         this.orbStream.write_value((Serializable)var1);
         return true;
      } else {
         return false;
      }
   }

   private void outputObject(Object var1) throws IOException {
      this.currentObject = var1;
      Class var2 = var1.getClass();
      this.currentClassDesc = ObjectStreamClass.lookup(var2);
      if (this.currentClassDesc == null) {
         throw new NotSerializableException(var2.getName());
      } else {
         if (this.currentClassDesc.isExternalizable()) {
            this.orbStream.write_octet(this.streamFormatVersion);
            Externalizable var3 = (Externalizable)var1;
            var3.writeExternal(this);
         } else {
            if (this.currentClassDesc.forClass().getName().equals("java.lang.String")) {
               this.writeUTF((String)var1);
               return;
            }

            int var14 = this.classDescStack.size();

            try {
               ObjectStreamClass var4;
               while((var4 = this.currentClassDesc.getSuperclass()) != null) {
                  this.classDescStack.push(this.currentClassDesc);
                  this.currentClassDesc = var4;
               }

               do {
                  OutputStreamHook.WriteObjectState var5 = this.writeObjectState;

                  try {
                     this.setState(NOT_IN_WRITE_OBJECT);
                     if (this.currentClassDesc.hasWriteObject()) {
                        this.invokeObjectWriter(this.currentClassDesc, var1);
                     } else {
                        this.defaultWriteObjectDelegate();
                     }
                  } finally {
                     this.setState(var5);
                  }
               } while(this.classDescStack.size() > var14 && (this.currentClassDesc = (ObjectStreamClass)this.classDescStack.pop()) != null);
            } finally {
               this.classDescStack.setSize(var14);
            }
         }

      }
   }

   private void invokeObjectWriter(ObjectStreamClass var1, Object var2) throws IOException {
      Class var3 = var1.forClass();

      try {
         this.orbStream.write_octet(this.streamFormatVersion);
         this.writeObjectState.enterWriteObject(this);
         var1.writeObjectMethod.invoke(var2, this.writeObjectArgList);
         this.writeObjectState.exitWriteObject(this);
      } catch (InvocationTargetException var6) {
         Throwable var5 = var6.getTargetException();
         if (var5 instanceof IOException) {
            throw (IOException)var5;
         }

         if (var5 instanceof RuntimeException) {
            throw (RuntimeException)var5;
         }

         if (var5 instanceof Error) {
            throw (Error)var5;
         }

         throw new Error("invokeObjectWriter internal error", var6);
      } catch (IllegalAccessException var7) {
      }

   }

   void writeField(ObjectStreamField var1, Object var2) throws IOException {
      switch(var1.getTypeCode()) {
      case 'B':
         if (var2 == null) {
            this.orbStream.write_octet((byte)0);
         } else {
            this.orbStream.write_octet((Byte)var2);
         }
         break;
      case 'C':
         if (var2 == null) {
            this.orbStream.write_wchar('\u0000');
         } else {
            this.orbStream.write_wchar((Character)var2);
         }
         break;
      case 'D':
         if (var2 == null) {
            this.orbStream.write_double(0.0D);
         } else {
            this.orbStream.write_double((Double)var2);
         }
         break;
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      default:
         throw new InvalidClassException(this.currentClassDesc.getName());
      case 'F':
         if (var2 == null) {
            this.orbStream.write_float(0.0F);
         } else {
            this.orbStream.write_float((Float)var2);
         }
         break;
      case 'I':
         if (var2 == null) {
            this.orbStream.write_long(0);
         } else {
            this.orbStream.write_long((Integer)var2);
         }
         break;
      case 'J':
         if (var2 == null) {
            this.orbStream.write_longlong(0L);
         } else {
            this.orbStream.write_longlong((Long)var2);
         }
         break;
      case 'L':
      case '[':
         this.writeObjectField(var1, var2);
         break;
      case 'S':
         if (var2 == null) {
            this.orbStream.write_short((short)0);
         } else {
            this.orbStream.write_short((Short)var2);
         }
         break;
      case 'Z':
         if (var2 == null) {
            this.orbStream.write_boolean(false);
         } else {
            this.orbStream.write_boolean((Boolean)var2);
         }
      }

   }

   private void writeObjectField(ObjectStreamField var1, Object var2) throws IOException {
      if (ObjectStreamClassCorbaExt.isAny(var1.getTypeString())) {
         Util.writeAny(this.orbStream, var2);
      } else {
         Class var3 = var1.getType();
         byte var4 = 2;
         if (var3.isInterface()) {
            String var5 = var3.getName();
            if (Remote.class.isAssignableFrom(var3)) {
               var4 = 0;
            } else if (org.omg.CORBA.Object.class.isAssignableFrom(var3)) {
               var4 = 0;
            } else if (RepositoryId.isAbstractBase(var3)) {
               var4 = 1;
            } else if (ObjectStreamClassCorbaExt.isAbstractInterface(var3)) {
               var4 = 1;
            }
         }

         switch(var4) {
         case 0:
            Util.writeRemoteObject(this.orbStream, var2);
            break;
         case 1:
            Util.writeAbstractObject(this.orbStream, var2);
            break;
         case 2:
            try {
               this.orbStream.write_value((Serializable)var2, var3);
            } catch (ClassCastException var6) {
               if (var2 instanceof Serializable) {
                  throw var6;
               }

               Utility.throwNotSerializableForCorba(var2.getClass().getName());
            }
         }
      }

   }

   private void outputClassFields(Object var1, Class var2, ObjectStreamField[] var3) throws IOException, InvalidClassException {
      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].getField() == null) {
            throw new InvalidClassException(var2.getName(), "Nonexistent field " + var3[var4].getName());
         }

         try {
            switch(var3[var4].getTypeCode()) {
            case 'B':
               byte var5 = var3[var4].getField().getByte(var1);
               this.orbStream.write_octet(var5);
               break;
            case 'C':
               char var6 = var3[var4].getField().getChar(var1);
               this.orbStream.write_wchar(var6);
               break;
            case 'D':
               double var8 = var3[var4].getField().getDouble(var1);
               this.orbStream.write_double(var8);
               break;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
               throw new InvalidClassException(var2.getName());
            case 'F':
               float var7 = var3[var4].getField().getFloat(var1);
               this.orbStream.write_float(var7);
               break;
            case 'I':
               int var10 = var3[var4].getField().getInt(var1);
               this.orbStream.write_long(var10);
               break;
            case 'J':
               long var11 = var3[var4].getField().getLong(var1);
               this.orbStream.write_longlong(var11);
               break;
            case 'L':
            case '[':
               Object var15 = var3[var4].getField().get(var1);
               this.writeObjectField(var3[var4], var15);
               break;
            case 'S':
               short var13 = var3[var4].getField().getShort(var1);
               this.orbStream.write_short(var13);
               break;
            case 'Z':
               boolean var14 = var3[var4].getField().getBoolean(var1);
               this.orbStream.write_boolean(var14);
            }
         } catch (IllegalAccessException var16) {
            throw this.wrapper.illegalFieldAccess((Throwable)var16, var3[var4].getName());
         }
      }

   }
}
