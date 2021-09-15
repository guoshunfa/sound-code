package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.SendingContext.RunTime;

public final class ValueHandlerImpl implements ValueHandlerMultiFormat {
   public static final String FORMAT_VERSION_PROPERTY = "com.sun.CORBA.MaxStreamFormatVersion";
   private static final byte MAX_SUPPORTED_FORMAT_VERSION = 2;
   private static final byte STREAM_FORMAT_VERSION_1 = 1;
   private static final byte MAX_STREAM_FORMAT_VERSION = getMaxStreamFormatVersion();
   public static final short kRemoteType = 0;
   public static final short kAbstractType = 1;
   public static final short kValueType = 2;
   private Hashtable inputStreamPairs;
   private Hashtable outputStreamPairs;
   private CodeBase codeBase;
   private boolean useHashtables;
   private boolean isInputStream;
   private IIOPOutputStream outputStreamBridge;
   private IIOPInputStream inputStreamBridge;
   private OMGSystemException omgWrapper;
   private UtilSystemException utilWrapper;

   private static byte getMaxStreamFormatVersion() {
      try {
         String var0 = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return System.getProperty("com.sun.CORBA.MaxStreamFormatVersion");
            }
         });
         if (var0 == null) {
            return 2;
         } else {
            byte var3 = Byte.parseByte(var0);
            if (var3 >= 1 && var3 <= 2) {
               return var3;
            } else {
               throw new ExceptionInInitializerError("Invalid stream format version: " + var3 + ".  Valid range is 1 through " + 2);
            }
         }
      } catch (Exception var2) {
         ExceptionInInitializerError var1 = new ExceptionInInitializerError(var2);
         var1.initCause(var2);
         throw var1;
      }
   }

   public byte getMaximumStreamFormatVersion() {
      return MAX_STREAM_FORMAT_VERSION;
   }

   public void writeValue(OutputStream var1, Serializable var2, byte var3) {
      if (var3 == 2) {
         if (!(var1 instanceof ValueOutputStream)) {
            throw this.omgWrapper.notAValueoutputstream();
         }
      } else if (var3 != 1) {
         throw this.omgWrapper.invalidStreamFormatVersion(new Integer(var3));
      }

      this.writeValueWithVersion(var1, var2, var3);
   }

   private ValueHandlerImpl() {
      this.inputStreamPairs = null;
      this.outputStreamPairs = null;
      this.codeBase = null;
      this.useHashtables = true;
      this.isInputStream = true;
      this.outputStreamBridge = null;
      this.inputStreamBridge = null;
      this.omgWrapper = OMGSystemException.get("rpc.encoding");
      this.utilWrapper = UtilSystemException.get("rpc.encoding");
   }

   private ValueHandlerImpl(boolean var1) {
      this();
      this.useHashtables = false;
      this.isInputStream = var1;
   }

   static ValueHandlerImpl getInstance() {
      return new ValueHandlerImpl();
   }

   static ValueHandlerImpl getInstance(boolean var0) {
      return new ValueHandlerImpl(var0);
   }

   public void writeValue(OutputStream var1, Serializable var2) {
      this.writeValueWithVersion(var1, var2, (byte)1);
   }

   private void writeValueWithVersion(OutputStream var1, Serializable var2, byte var3) {
      org.omg.CORBA_2_3.portable.OutputStream var4 = (org.omg.CORBA_2_3.portable.OutputStream)var1;
      if (!this.useHashtables) {
         if (this.outputStreamBridge == null) {
            this.outputStreamBridge = this.createOutputStream();
            this.outputStreamBridge.setOrbStream(var4);
         }

         try {
            this.outputStreamBridge.increaseRecursionDepth();
            this.writeValueInternal(this.outputStreamBridge, var4, var2, var3);
         } finally {
            this.outputStreamBridge.decreaseRecursionDepth();
         }

      } else {
         IIOPOutputStream var5 = null;
         if (this.outputStreamPairs == null) {
            this.outputStreamPairs = new Hashtable();
         }

         var5 = (IIOPOutputStream)this.outputStreamPairs.get(var1);
         if (var5 == null) {
            var5 = this.createOutputStream();
            var5.setOrbStream(var4);
            this.outputStreamPairs.put(var1, var5);
         }

         try {
            var5.increaseRecursionDepth();
            this.writeValueInternal(var5, var4, var2, var3);
         } finally {
            if (var5.decreaseRecursionDepth() == 0) {
               this.outputStreamPairs.remove(var1);
            }

         }

      }
   }

   private void writeValueInternal(IIOPOutputStream var1, org.omg.CORBA_2_3.portable.OutputStream var2, Serializable var3, byte var4) {
      Class var5 = var3.getClass();
      if (var5.isArray()) {
         this.write_Array(var2, var3, var5.getComponentType());
      } else {
         var1.simpleWriteObject(var3, var4);
      }

   }

   public Serializable readValue(InputStream var1, int var2, Class var3, String var4, RunTime var5) {
      CodeBase var6 = CodeBaseHelper.narrow(var5);
      org.omg.CORBA_2_3.portable.InputStream var7 = (org.omg.CORBA_2_3.portable.InputStream)var1;
      Serializable var8;
      if (this.useHashtables) {
         var8 = null;
         if (this.inputStreamPairs == null) {
            this.inputStreamPairs = new Hashtable();
         }

         IIOPInputStream var17 = (IIOPInputStream)this.inputStreamPairs.get(var1);
         if (var17 == null) {
            var17 = this.createInputStream();
            var17.setOrbStream(var7);
            var17.setSender(var6);
            var17.setValueHandler(this);
            this.inputStreamPairs.put(var1, var17);
         }

         Serializable var9 = null;

         try {
            var17.increaseRecursionDepth();
            var9 = this.readValueInternal(var17, var7, var2, var3, var4, var6);
         } finally {
            if (var17.decreaseRecursionDepth() == 0) {
               this.inputStreamPairs.remove(var1);
            }

         }

         return var9;
      } else {
         if (this.inputStreamBridge == null) {
            this.inputStreamBridge = this.createInputStream();
            this.inputStreamBridge.setOrbStream(var7);
            this.inputStreamBridge.setSender(var6);
            this.inputStreamBridge.setValueHandler(this);
         }

         var8 = null;

         try {
            this.inputStreamBridge.increaseRecursionDepth();
            var8 = this.readValueInternal(this.inputStreamBridge, var7, var2, var3, var4, var6);
         } finally {
            if (this.inputStreamBridge.decreaseRecursionDepth() == 0) {
            }

         }

         return var8;
      }
   }

   private Serializable readValueInternal(IIOPInputStream var1, org.omg.CORBA_2_3.portable.InputStream var2, int var3, Class var4, String var5, CodeBase var6) {
      Serializable var7 = null;
      if (var4 == null) {
         if (this.isArray(var5)) {
            this.read_Array(var1, var2, (Class)null, var6, var3);
         } else {
            var1.simpleSkipObject(var5, var6);
         }

         return var7;
      } else {
         if (var4.isArray()) {
            var7 = (Serializable)this.read_Array(var1, var2, var4, var6, var3);
         } else {
            var7 = (Serializable)var1.simpleReadObject(var4, var5, var6, var3);
         }

         return var7;
      }
   }

   public String getRMIRepositoryID(Class var1) {
      return RepositoryId.createForJavaType(var1);
   }

   public boolean isCustomMarshaled(Class var1) {
      return ObjectStreamClass.lookup(var1).isCustomMarshaled();
   }

   public RunTime getRunTimeCodeBase() {
      if (this.codeBase != null) {
         return this.codeBase;
      } else {
         this.codeBase = new FVDCodeBaseImpl();
         FVDCodeBaseImpl var1 = (FVDCodeBaseImpl)this.codeBase;
         var1.setValueHandler(this);
         return this.codeBase;
      }
   }

   public boolean useFullValueDescription(Class var1, String var2) throws IOException {
      return RepositoryId.useFullValueDescription(var1, var2);
   }

   public String getClassName(String var1) {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getClassName();
   }

   public Class getClassFromType(String var1) throws ClassNotFoundException {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getClassFromType();
   }

   public Class getAnyClassFromType(String var1) throws ClassNotFoundException {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getAnyClassFromType();
   }

   public String createForAnyType(Class var1) {
      return RepositoryId.createForAnyType(var1);
   }

   public String getDefinedInId(String var1) {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getDefinedInId();
   }

   public String getUnqualifiedName(String var1) {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getUnqualifiedName();
   }

   public String getSerialVersionUID(String var1) {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.getSerialVersionUID();
   }

   public boolean isAbstractBase(Class var1) {
      return RepositoryId.isAbstractBase(var1);
   }

   public boolean isSequence(String var1) {
      RepositoryId var2 = RepositoryId.cache.getId(var1);
      return var2.isSequence();
   }

   public Serializable writeReplace(Serializable var1) {
      return ObjectStreamClass.lookup(var1.getClass()).writeReplace(var1);
   }

   private void writeCharArray(org.omg.CORBA_2_3.portable.OutputStream var1, char[] var2, int var3, int var4) {
      var1.write_wchar_array(var2, var3, var4);
   }

   private void write_Array(org.omg.CORBA_2_3.portable.OutputStream var1, Serializable var2, Class var3) {
      int var5;
      if (var3.isPrimitive()) {
         if (var3 == Integer.TYPE) {
            int[] var10 = (int[])var2;
            var5 = var10.length;
            var1.write_ulong(var5);
            var1.write_long_array(var10, 0, var5);
         } else if (var3 == Byte.TYPE) {
            byte[] var11 = (byte[])var2;
            var5 = var11.length;
            var1.write_ulong(var5);
            var1.write_octet_array(var11, 0, var5);
         } else if (var3 == Long.TYPE) {
            long[] var12 = (long[])var2;
            var5 = var12.length;
            var1.write_ulong(var5);
            var1.write_longlong_array(var12, 0, var5);
         } else if (var3 == Float.TYPE) {
            float[] var13 = (float[])var2;
            var5 = var13.length;
            var1.write_ulong(var5);
            var1.write_float_array(var13, 0, var5);
         } else if (var3 == Double.TYPE) {
            double[] var14 = (double[])var2;
            var5 = var14.length;
            var1.write_ulong(var5);
            var1.write_double_array(var14, 0, var5);
         } else if (var3 == Short.TYPE) {
            short[] var15 = (short[])var2;
            var5 = var15.length;
            var1.write_ulong(var5);
            var1.write_short_array(var15, 0, var5);
         } else if (var3 == Character.TYPE) {
            char[] var16 = (char[])var2;
            var5 = var16.length;
            var1.write_ulong(var5);
            this.writeCharArray(var1, var16, 0, var5);
         } else {
            if (var3 != Boolean.TYPE) {
               throw new Error("Invalid primitive type : " + var2.getClass().getName());
            }

            boolean[] var17 = (boolean[])var2;
            var5 = var17.length;
            var1.write_ulong(var5);
            var1.write_boolean_array(var17, 0, var5);
         }
      } else {
         int var4;
         Object[] var6;
         if (var3 == Object.class) {
            var6 = (Object[])var2;
            var5 = var6.length;
            var1.write_ulong(var5);

            for(var4 = 0; var4 < var5; ++var4) {
               Util.writeAny(var1, var6[var4]);
            }
         } else {
            var6 = (Object[])var2;
            var5 = var6.length;
            var1.write_ulong(var5);
            byte var7 = 2;
            if (var3.isInterface()) {
               String var8 = var3.getName();
               if (Remote.class.isAssignableFrom(var3)) {
                  var7 = 0;
               } else if (org.omg.CORBA.Object.class.isAssignableFrom(var3)) {
                  var7 = 0;
               } else if (RepositoryId.isAbstractBase(var3)) {
                  var7 = 1;
               } else if (ObjectStreamClassCorbaExt.isAbstractInterface(var3)) {
                  var7 = 1;
               }
            }

            for(var4 = 0; var4 < var5; ++var4) {
               switch(var7) {
               case 0:
                  Util.writeRemoteObject(var1, var6[var4]);
                  break;
               case 1:
                  Util.writeAbstractObject(var1, var6[var4]);
                  break;
               case 2:
                  try {
                     var1.write_value((Serializable)var6[var4]);
                  } catch (ClassCastException var9) {
                     if (var6[var4] instanceof Serializable) {
                        throw var9;
                     }

                     Utility.throwNotSerializableForCorba(var6[var4].getClass().getName());
                  }
               }
            }
         }
      }

   }

   private void readCharArray(org.omg.CORBA_2_3.portable.InputStream var1, char[] var2, int var3, int var4) {
      var1.read_wchar_array(var2, var3, var4);
   }

   private Object read_Array(IIOPInputStream var1, org.omg.CORBA_2_3.portable.InputStream var2, Class var3, CodeBase var4, int var5) {
      Serializable var11;
      try {
         int var6 = var2.read_ulong();
         int var7;
         Class var8;
         if (var3 == null) {
            for(var7 = 0; var7 < var6; ++var7) {
               var2.read_value();
            }

            var8 = null;
            return var8;
         }

         var8 = var3.getComponentType();
         Class var9 = var8;
         if (!var8.isPrimitive()) {
            Object[] var38;
            if (var8 != Object.class) {
               var38 = (Object[])((Object[])Array.newInstance(var8, var6));
               var1.activeRecursionMgr.addObject(var5, var38);
               byte var42 = 2;
               boolean var36 = false;
               if (var8.isInterface()) {
                  boolean var13 = false;
                  if (Remote.class.isAssignableFrom(var8)) {
                     var42 = 0;
                     var13 = true;
                  } else if (org.omg.CORBA.Object.class.isAssignableFrom(var8)) {
                     var42 = 0;
                     var13 = true;
                  } else if (RepositoryId.isAbstractBase(var8)) {
                     var42 = 1;
                     var13 = true;
                  } else if (ObjectStreamClassCorbaExt.isAbstractInterface(var8)) {
                     var42 = 1;
                  }

                  if (var13) {
                     try {
                        String var14 = Util.getCodebase(var8);
                        String var15 = RepositoryId.createForAnyType(var8);
                        Class var16 = Utility.loadStubClass(var15, var14, var8);
                        var9 = var16;
                     } catch (ClassNotFoundException var28) {
                        var36 = true;
                     }
                  } else {
                     var36 = true;
                  }
               }

               for(var7 = 0; var7 < var6; ++var7) {
                  try {
                     switch(var42) {
                     case 0:
                        if (!var36) {
                           var38[var7] = var2.read_Object(var9);
                        } else {
                           var38[var7] = Utility.readObjectAndNarrow(var2, var9);
                        }
                        break;
                     case 1:
                        if (!var36) {
                           var38[var7] = var2.read_abstract_interface(var9);
                        } else {
                           var38[var7] = Utility.readAbstractAndNarrow(var2, var9);
                        }
                        break;
                     case 2:
                        var38[var7] = var2.read_value(var9);
                     }
                  } catch (IndirectionException var27) {
                     IndirectionException var40 = var27;

                     try {
                        var38[var7] = var1.activeRecursionMgr.getObject(var40.offset);
                     } catch (IOException var26) {
                        throw this.utilWrapper.invalidIndirection((Throwable)var26, new Integer(var27.offset));
                     }
                  }
               }

               Serializable var41 = (Serializable)var38;
               return var41;
            }

            var38 = (Object[])((Object[])Array.newInstance(var8, var6));
            var1.activeRecursionMgr.addObject(var5, var38);

            for(var7 = 0; var7 < var6; ++var7) {
               var11 = null;

               Object var39;
               try {
                  var39 = Util.readAny(var2);
               } catch (IndirectionException var25) {
                  IndirectionException var12 = var25;

                  try {
                     var39 = var1.activeRecursionMgr.getObject(var12.offset);
                  } catch (IOException var24) {
                     throw this.utilWrapper.invalidIndirection((Throwable)var24, new Integer(var25.offset));
                  }
               }

               var38[var7] = var39;
            }

            var11 = (Serializable)var38;
            return var11;
         }

         if (var8 != Integer.TYPE) {
            if (var8 == Byte.TYPE) {
               byte[] var37 = new byte[var6];
               var2.read_octet_array(var37, 0, var6);
               var11 = (Serializable)var37;
               return var11;
            }

            if (var8 == Long.TYPE) {
               long[] var35 = new long[var6];
               var2.read_longlong_array(var35, 0, var6);
               var11 = (Serializable)var35;
               return var11;
            }

            if (var8 == Float.TYPE) {
               float[] var34 = new float[var6];
               var2.read_float_array(var34, 0, var6);
               var11 = (Serializable)var34;
               return var11;
            }

            if (var8 == Double.TYPE) {
               double[] var33 = new double[var6];
               var2.read_double_array(var33, 0, var6);
               var11 = (Serializable)var33;
               return var11;
            }

            if (var8 == Short.TYPE) {
               short[] var32 = new short[var6];
               var2.read_short_array(var32, 0, var6);
               var11 = (Serializable)var32;
               return var11;
            }

            if (var8 == Character.TYPE) {
               char[] var31 = new char[var6];
               this.readCharArray(var2, var31, 0, var6);
               var11 = (Serializable)var31;
               return var11;
            }

            if (var8 == Boolean.TYPE) {
               boolean[] var30 = new boolean[var6];
               var2.read_boolean_array(var30, 0, var6);
               var11 = (Serializable)var30;
               return var11;
            }

            throw new Error("Invalid primitive componentType : " + var3.getName());
         }

         int[] var10 = new int[var6];
         var2.read_long_array(var10, 0, var6);
         var11 = (Serializable)var10;
      } finally {
         var1.activeRecursionMgr.removeObject(var5);
      }

      return var11;
   }

   private boolean isArray(String var1) {
      return RepositoryId.cache.getId(var1).isSequence();
   }

   private String getOutputStreamClassName() {
      return "com.sun.corba.se.impl.io.IIOPOutputStream";
   }

   private IIOPOutputStream createOutputStream() {
      String var1 = this.getOutputStreamClassName();

      try {
         IIOPOutputStream var2 = this.createOutputStreamBuiltIn(var1);
         return var2 != null ? var2 : (IIOPOutputStream)this.createCustom(IIOPOutputStream.class, var1);
      } catch (Throwable var4) {
         InternalError var3 = new InternalError("Error loading " + var1);
         var3.initCause(var4);
         throw var3;
      }
   }

   private IIOPOutputStream createOutputStreamBuiltIn(final String var1) throws Throwable {
      try {
         return (IIOPOutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<IIOPOutputStream>() {
            public IIOPOutputStream run() throws IOException {
               return ValueHandlerImpl.this.createOutputStreamBuiltInNoPriv(var1);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw var3.getCause();
      }
   }

   private IIOPOutputStream createOutputStreamBuiltInNoPriv(String var1) throws IOException {
      return var1.equals(IIOPOutputStream.class.getName()) ? new IIOPOutputStream() : null;
   }

   private String getInputStreamClassName() {
      return "com.sun.corba.se.impl.io.IIOPInputStream";
   }

   private IIOPInputStream createInputStream() {
      String var1 = this.getInputStreamClassName();

      try {
         IIOPInputStream var2 = this.createInputStreamBuiltIn(var1);
         return var2 != null ? var2 : (IIOPInputStream)this.createCustom(IIOPInputStream.class, var1);
      } catch (Throwable var4) {
         InternalError var3 = new InternalError("Error loading " + var1);
         var3.initCause(var4);
         throw var3;
      }
   }

   private IIOPInputStream createInputStreamBuiltIn(final String var1) throws Throwable {
      try {
         return (IIOPInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<IIOPInputStream>() {
            public IIOPInputStream run() throws IOException {
               return ValueHandlerImpl.this.createInputStreamBuiltInNoPriv(var1);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw var3.getCause();
      }
   }

   private IIOPInputStream createInputStreamBuiltInNoPriv(String var1) throws IOException {
      return var1.equals(IIOPInputStream.class.getName()) ? new IIOPInputStream() : null;
   }

   private <T> T createCustom(Class<T> var1, String var2) throws Throwable {
      ClassLoader var3 = Thread.currentThread().getContextClassLoader();
      if (var3 == null) {
         var3 = ClassLoader.getSystemClassLoader();
      }

      Class var4 = var3.loadClass(var2);
      Class var5 = var4.asSubclass(var1);
      return var5.newInstance();
   }

   TCKind getJavaCharTCKind() {
      return TCKind.tk_wchar;
   }
}
