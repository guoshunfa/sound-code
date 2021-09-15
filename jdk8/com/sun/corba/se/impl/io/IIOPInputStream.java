package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.io.StreamCorruptedException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.Bridge;

public class IIOPInputStream extends InputStreamHook {
   private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {
         return Bridge.get();
      }
   });
   private static OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
   private static UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
   private ValueMember[] defaultReadObjectFVDMembers = null;
   private InputStream orbStream;
   private CodeBase cbSender;
   private ValueHandlerImpl vhandler;
   private Object currentObject = null;
   private ObjectStreamClass currentClassDesc = null;
   private Class currentClass = null;
   private int recursionDepth = 0;
   private int simpleReadDepth = 0;
   IIOPInputStream.ActiveRecursionManager activeRecursionMgr = new IIOPInputStream.ActiveRecursionManager();
   private IOException abortIOException = null;
   private ClassNotFoundException abortClassNotFoundException = null;
   private Vector callbacks;
   ObjectStreamClass[] classdesc;
   Class[] classes;
   int spClass;
   private static final String kEmptyStr = "";
   public static final TypeCode kRemoteTypeCode;
   public static final TypeCode kValueTypeCode;
   private static final boolean useFVDOnly = false;
   private byte streamFormatVersion;
   private static final Constructor OPT_DATA_EXCEPTION_CTOR;
   private Object[] readObjectArgList = new Object[]{this};

   private static Constructor getOptDataExceptionCtor() {
      try {
         Constructor var0 = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws NoSuchMethodException, SecurityException {
               Constructor var1 = java.io.OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
               var1.setAccessible(true);
               return var1;
            }
         });
         if (var0 == null) {
            throw new Error("Unable to find OptionalDataException constructor");
         } else {
            return var0;
         }
      } catch (Exception var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }

   private java.io.OptionalDataException createOptionalDataException() {
      try {
         java.io.OptionalDataException var1 = (java.io.OptionalDataException)OPT_DATA_EXCEPTION_CTOR.newInstance(Boolean.TRUE);
         if (var1 == null) {
            throw new Error("Created null OptionalDataException");
         } else {
            return var1;
         }
      } catch (Exception var2) {
         throw new Error("Couldn't create OptionalDataException", var2);
      }
   }

   protected byte getStreamFormatVersion() {
      return this.streamFormatVersion;
   }

   private void readFormatVersion() throws IOException {
      this.streamFormatVersion = this.orbStream.read_octet();
      IOException var2;
      if (this.streamFormatVersion >= 1 && this.streamFormatVersion <= this.vhandler.getMaximumStreamFormatVersion()) {
         if (this.streamFormatVersion == 2 && !(this.orbStream instanceof ValueInputStream)) {
            BAD_PARAM var3 = omgWrapper.notAValueinputstream(CompletionStatus.COMPLETED_MAYBE);
            var2 = new IOException("Not a ValueInputStream");
            var2.initCause(var3);
            throw var2;
         }
      } else {
         MARSHAL var1 = omgWrapper.unsupportedFormatVersion(CompletionStatus.COMPLETED_MAYBE);
         var2 = new IOException("Unsupported format version: " + this.streamFormatVersion);
         var2.initCause(var1);
         throw var2;
      }
   }

   public static void setTestFVDFlag(boolean var0) {
   }

   public IIOPInputStream() throws IOException {
      this.resetStream();
   }

   final void setOrbStream(InputStream var1) {
      this.orbStream = var1;
   }

   final InputStream getOrbStream() {
      return this.orbStream;
   }

   public final void setSender(CodeBase var1) {
      this.cbSender = var1;
   }

   public final CodeBase getSender() {
      return this.cbSender;
   }

   public final void setValueHandler(ValueHandler var1) {
      this.vhandler = (ValueHandlerImpl)var1;
   }

   public final ValueHandler getValueHandler() {
      return this.vhandler;
   }

   final void increaseRecursionDepth() {
      ++this.recursionDepth;
   }

   final int decreaseRecursionDepth() {
      return --this.recursionDepth;
   }

   public final synchronized Object readObjectDelegate() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_abstract_interface();
      } catch (MARSHAL var2) {
         this.handleOptionalDataMarshalException(var2, true);
         throw var2;
      } catch (IndirectionException var3) {
         return this.activeRecursionMgr.getObject(var3.offset);
      }
   }

   final synchronized Object simpleReadObject(Class var1, String var2, CodeBase var3, int var4) {
      Object var5 = this.currentObject;
      ObjectStreamClass var6 = this.currentClassDesc;
      Class var7 = this.currentClass;
      byte var8 = this.streamFormatVersion;
      ++this.simpleReadDepth;
      Object var9 = null;

      ClassNotFoundException var11;
      label97: {
         try {
            if (this.vhandler.useFullValueDescription(var1, var2)) {
               var9 = this.inputObjectUsingFVD(var1, var2, var3, var4);
            } else {
               var9 = this.inputObject(var1, var2, var3, var4);
            }

            var9 = this.currentClassDesc.readResolve(var9);
            break label97;
         } catch (ClassNotFoundException var16) {
            bridge.throwException(var16);
            var11 = null;
            return var11;
         } catch (IOException var17) {
            bridge.throwException(var17);
            var11 = null;
         } finally {
            --this.simpleReadDepth;
            this.currentObject = var5;
            this.currentClassDesc = var6;
            this.currentClass = var7;
            this.streamFormatVersion = var8;
         }

         return var11;
      }

      IOException var10 = this.abortIOException;
      if (this.simpleReadDepth == 0) {
         this.abortIOException = null;
      }

      if (var10 != null) {
         bridge.throwException(var10);
         return null;
      } else {
         var11 = this.abortClassNotFoundException;
         if (this.simpleReadDepth == 0) {
            this.abortClassNotFoundException = null;
         }

         if (var11 != null) {
            bridge.throwException(var11);
            return null;
         } else {
            return var9;
         }
      }
   }

   public final synchronized void simpleSkipObject(String var1, CodeBase var2) {
      Object var3 = this.currentObject;
      ObjectStreamClass var4 = this.currentClassDesc;
      Class var5 = this.currentClass;
      byte var6 = this.streamFormatVersion;
      ++this.simpleReadDepth;
      Object var7 = null;

      label87: {
         try {
            this.skipObjectUsingFVD(var1, var2);
            break label87;
         } catch (ClassNotFoundException var13) {
            bridge.throwException(var13);
         } catch (IOException var14) {
            bridge.throwException(var14);
            return;
         } finally {
            --this.simpleReadDepth;
            this.streamFormatVersion = var6;
            this.currentObject = var3;
            this.currentClassDesc = var4;
            this.currentClass = var5;
         }

         return;
      }

      IOException var8 = this.abortIOException;
      if (this.simpleReadDepth == 0) {
         this.abortIOException = null;
      }

      if (var8 != null) {
         bridge.throwException(var8);
      } else {
         ClassNotFoundException var9 = this.abortClassNotFoundException;
         if (this.simpleReadDepth == 0) {
            this.abortClassNotFoundException = null;
         }

         if (var9 != null) {
            bridge.throwException(var9);
         }
      }
   }

   protected final Object readObjectOverride() throws java.io.OptionalDataException, ClassNotFoundException, IOException {
      return this.readObjectDelegate();
   }

   final synchronized void defaultReadObjectDelegate() {
      try {
         if (this.currentObject == null || this.currentClassDesc == null) {
            throw new NotActiveException("defaultReadObjectDelegate");
         }

         if (!this.currentClassDesc.forClass().isAssignableFrom(this.currentObject.getClass())) {
            throw new IOException("Object Type mismatch");
         }

         if (this.defaultReadObjectFVDMembers != null && this.defaultReadObjectFVDMembers.length > 0) {
            this.inputClassFields(this.currentObject, this.currentClass, this.currentClassDesc, this.defaultReadObjectFVDMembers, this.cbSender);
         } else {
            ObjectStreamField[] var1 = this.currentClassDesc.getFieldsNoCopy();
            if (var1.length > 0) {
               this.inputClassFields(this.currentObject, this.currentClass, var1, this.cbSender);
            }
         }
      } catch (NotActiveException var2) {
         bridge.throwException(var2);
      } catch (IOException var3) {
         bridge.throwException(var3);
      } catch (ClassNotFoundException var4) {
         bridge.throwException(var4);
      }

   }

   public final boolean enableResolveObjectDelegate(boolean var1) {
      return false;
   }

   public final void mark(int var1) {
      this.orbStream.mark(var1);
   }

   public final boolean markSupported() {
      return this.orbStream.markSupported();
   }

   public final void reset() throws IOException {
      try {
         this.orbStream.reset();
      } catch (Error var3) {
         IOException var2 = new IOException(var3.getMessage());
         var2.initCause(var3);
         throw var2;
      }
   }

   public final int available() throws IOException {
      return 0;
   }

   public final void close() throws IOException {
   }

   public final int read() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_octet() << 0 & 255;
      } catch (MARSHAL var3) {
         if (var3.minor == 1330446344) {
            this.setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
            return -1;
         } else {
            throw var3;
         }
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final int read(byte[] var1, int var2, int var3) throws IOException {
      try {
         this.readObjectState.readData(this);
         this.orbStream.read_octet_array(var1, var2, var3);
         return var3;
      } catch (MARSHAL var6) {
         if (var6.minor == 1330446344) {
            this.setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
            return -1;
         } else {
            throw var6;
         }
      } catch (Error var7) {
         IOException var5 = new IOException(var7.getMessage());
         var5.initCause(var7);
         throw var5;
      }
   }

   public final boolean readBoolean() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_boolean();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final byte readByte() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_octet();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final char readChar() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_wchar();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final double readDouble() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_double();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final float readFloat() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_float();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1, int var2, int var3) throws IOException {
      try {
         this.readObjectState.readData(this);
         this.orbStream.read_octet_array(var1, var2, var3);
      } catch (MARSHAL var6) {
         this.handleOptionalDataMarshalException(var6, false);
         throw var6;
      } catch (Error var7) {
         IOException var5 = new IOException(var7.getMessage());
         var5.initCause(var7);
         throw var5;
      }
   }

   public final int readInt() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_long();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final String readLine() throws IOException {
      throw new IOException("Method readLine not supported");
   }

   public final long readLong() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_longlong();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final short readShort() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_short();
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   protected final void readStreamHeader() throws IOException, StreamCorruptedException {
   }

   public final int readUnsignedByte() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_octet() << 0 & 255;
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   public final int readUnsignedShort() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.orbStream.read_ushort() << 0 & '\uffff';
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   protected String internalReadUTF(org.omg.CORBA.portable.InputStream var1) {
      return var1.read_wstring();
   }

   public final String readUTF() throws IOException {
      try {
         this.readObjectState.readData(this);
         return this.internalReadUTF(this.orbStream);
      } catch (MARSHAL var3) {
         this.handleOptionalDataMarshalException(var3, false);
         throw var3;
      } catch (Error var4) {
         IOException var2 = new IOException(var4.getMessage());
         var2.initCause(var4);
         throw var2;
      }
   }

   private void handleOptionalDataMarshalException(MARSHAL var1, boolean var2) throws IOException {
      if (var1.minor == 1330446344) {
         Object var3;
         if (!var2) {
            var3 = new EOFException("No more optional data");
         } else {
            var3 = this.createOptionalDataException();
         }

         ((IOException)var3).initCause(var1);
         this.setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
         throw var3;
      }
   }

   public final synchronized void registerValidation(ObjectInputValidation var1, int var2) throws NotActiveException, InvalidObjectException {
      throw new Error("Method registerValidation not supported");
   }

   protected final Class resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      throw new IOException("Method resolveClass not supported");
   }

   protected final Object resolveObject(Object var1) throws IOException {
      throw new IOException("Method resolveObject not supported");
   }

   public final int skipBytes(int var1) throws IOException {
      try {
         this.readObjectState.readData(this);
         byte[] var2 = new byte[var1];
         this.orbStream.read_octet_array(var2, 0, var1);
         return var1;
      } catch (MARSHAL var4) {
         this.handleOptionalDataMarshalException(var4, false);
         throw var4;
      } catch (Error var5) {
         IOException var3 = new IOException(var5.getMessage());
         var3.initCause(var5);
         throw var3;
      }
   }

   private synchronized Object inputObject(Class var1, String var2, CodeBase var3, int var4) throws IOException, ClassNotFoundException {
      this.currentClassDesc = ObjectStreamClass.lookup(var1);
      this.currentClass = this.currentClassDesc.forClass();
      if (this.currentClass == null) {
         throw new ClassNotFoundException(this.currentClassDesc.getName());
      } else {
         String var8;
         try {
            if (Enum.class.isAssignableFrom(var1)) {
               int var53 = this.orbStream.read_long();
               String var56 = (String)this.orbStream.read_value(String.class);
               Enum var55 = Enum.valueOf(var1, var56);
               return var55;
            }

            if (this.currentClassDesc.isExternalizable()) {
               InvalidClassException var54;
               try {
                  this.currentObject = this.currentClass == null ? null : this.currentClassDesc.newInstance();
                  if (this.currentObject != null) {
                     this.activeRecursionMgr.addObject(var4, this.currentObject);
                     this.readFormatVersion();
                     Externalizable var52 = (Externalizable)this.currentObject;
                     var52.readExternal(this);
                  }

                  return this.currentObject;
               } catch (InvocationTargetException var46) {
                  var54 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                  var54.initCause(var46);
                  throw var54;
               } catch (UnsupportedOperationException var47) {
                  var54 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                  var54.initCause(var47);
                  throw var54;
               } catch (InstantiationException var48) {
                  var54 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                  var54.initCause(var48);
                  throw var54;
               }
            }

            ObjectStreamClass var5 = this.currentClassDesc;
            Class var6 = this.currentClass;
            int var7 = this.spClass;
            if (!this.currentClass.getName().equals("java.lang.String")) {
               var5 = this.currentClassDesc;

               for(var6 = this.currentClass; var5 != null && var5.isSerializable(); var5 = var5.getSuperclass()) {
                  Class var57 = var5.forClass();

                  Class var9;
                  for(var9 = var6; var9 != null && var57 != var9; var9 = var9.getSuperclass()) {
                  }

                  ++this.spClass;
                  if (this.spClass >= this.classes.length) {
                     int var10 = this.classes.length * 2;
                     Class[] var11 = new Class[var10];
                     ObjectStreamClass[] var12 = new ObjectStreamClass[var10];
                     System.arraycopy(this.classes, 0, var11, 0, this.classes.length);
                     System.arraycopy(this.classdesc, 0, var12, 0, this.classes.length);
                     this.classes = var11;
                     this.classdesc = var12;
                  }

                  if (var9 == null) {
                     this.classdesc[this.spClass] = var5;
                     this.classes[this.spClass] = null;
                  } else {
                     this.classdesc[this.spClass] = var5;
                     this.classes[this.spClass] = var9;
                     var6 = var9.getSuperclass();
                  }
               }

               InvalidClassException var60;
               try {
                  this.currentObject = this.currentClass == null ? null : this.currentClassDesc.newInstance();
                  this.activeRecursionMgr.addObject(var4, this.currentObject);
               } catch (InvocationTargetException var43) {
                  var60 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                  var60.initCause(var43);
                  throw var60;
               } catch (UnsupportedOperationException var44) {
                  var60 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                  var60.initCause(var44);
                  throw var60;
               } catch (InstantiationException var45) {
                  var60 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                  var60.initCause(var45);
                  throw var60;
               }

               try {
                  for(this.spClass = this.spClass; this.spClass > var7; --this.spClass) {
                     this.currentClassDesc = this.classdesc[this.spClass];
                     this.currentClass = this.classes[this.spClass];
                     if (this.classes[this.spClass] != null) {
                        InputStreamHook.ReadObjectState var59 = this.readObjectState;
                        this.setState(DEFAULT_STATE);

                        try {
                           if (this.currentClassDesc.hasWriteObject()) {
                              this.readFormatVersion();
                              boolean var61 = this.readBoolean();
                              this.readObjectState.beginUnmarshalCustomValue(this, var61, this.currentClassDesc.readObjectMethod != null);
                           } else if (this.currentClassDesc.hasReadObject()) {
                              this.setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                           }

                           if (!this.invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass) || this.readObjectState == IN_READ_OBJECT_DEFAULTS_SENT) {
                              ObjectStreamField[] var62 = this.currentClassDesc.getFieldsNoCopy();
                              if (var62.length > 0) {
                                 this.inputClassFields(this.currentObject, this.currentClass, var62, var3);
                              }
                           }

                           if (this.currentClassDesc.hasWriteObject()) {
                              this.readObjectState.endUnmarshalCustomValue(this);
                           }
                        } finally {
                           this.setState(var59);
                        }
                     } else {
                        ObjectStreamField[] var58 = this.currentClassDesc.getFieldsNoCopy();
                        if (var58.length > 0) {
                           this.inputClassFields((Object)null, this.currentClass, var58, var3);
                        }
                     }
                  }

                  return this.currentObject;
               } finally {
                  this.spClass = var7;
               }
            }

            var8 = this.readUTF();
         } finally {
            this.activeRecursionMgr.removeObject(var4);
         }

         return var8;
      }
   }

   private Vector getOrderedDescriptions(String var1, CodeBase var2) {
      Vector var3 = new Vector();
      if (var2 == null) {
         return var3;
      } else {
         for(FullValueDescription var4 = var2.meta(var1); var4 != null; var4 = var2.meta(var4.base_value)) {
            var3.insertElementAt(var4, 0);
            if (var4.base_value == null || "".equals(var4.base_value)) {
               return var3;
            }
         }

         return var3;
      }
   }

   private synchronized Object inputObjectUsingFVD(Class var1, String var2, CodeBase var3, int var4) throws IOException, ClassNotFoundException {
      int var5 = this.spClass;

      Object var55;
      try {
         ObjectStreamClass var6 = this.currentClassDesc = ObjectStreamClass.lookup(var1);
         Class var7 = this.currentClass = var1;
         InvalidClassException var56;
         if (this.currentClassDesc.isExternalizable()) {
            try {
               this.currentObject = this.currentClass == null ? null : this.currentClassDesc.newInstance();
               if (this.currentObject != null) {
                  this.activeRecursionMgr.addObject(var4, this.currentObject);
                  this.readFormatVersion();
                  Externalizable var54 = (Externalizable)this.currentObject;
                  var54.readExternal(this);
               }
            } catch (InvocationTargetException var47) {
               var56 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
               var56.initCause(var47);
               throw var56;
            } catch (UnsupportedOperationException var48) {
               var56 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
               var56.initCause(var48);
               throw var56;
            } catch (InstantiationException var49) {
               var56 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
               var56.initCause(var49);
               throw var56;
            }
         } else {
            var6 = this.currentClassDesc;

            for(var7 = this.currentClass; var6 != null && var6.isSerializable(); var6 = var6.getSuperclass()) {
               Class var8 = var6.forClass();

               Class var9;
               for(var9 = var7; var9 != null && var8 != var9; var9 = var9.getSuperclass()) {
               }

               ++this.spClass;
               if (this.spClass >= this.classes.length) {
                  int var10 = this.classes.length * 2;
                  Class[] var11 = new Class[var10];
                  ObjectStreamClass[] var12 = new ObjectStreamClass[var10];
                  System.arraycopy(this.classes, 0, var11, 0, this.classes.length);
                  System.arraycopy(this.classdesc, 0, var12, 0, this.classes.length);
                  this.classes = var11;
                  this.classdesc = var12;
               }

               if (var9 == null) {
                  this.classdesc[this.spClass] = var6;
                  this.classes[this.spClass] = null;
               } else {
                  this.classdesc[this.spClass] = var6;
                  this.classes[this.spClass] = var9;
                  var7 = var9.getSuperclass();
               }
            }

            try {
               this.currentObject = this.currentClass == null ? null : this.currentClassDesc.newInstance();
               this.activeRecursionMgr.addObject(var4, this.currentObject);
            } catch (InvocationTargetException var44) {
               var56 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
               var56.initCause(var44);
               throw var56;
            } catch (UnsupportedOperationException var45) {
               var56 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
               var56.initCause(var45);
               throw var56;
            } catch (InstantiationException var46) {
               var56 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
               var56.initCause(var46);
               throw var56;
            }

            Enumeration var53 = this.getOrderedDescriptions(var2, var3).elements();

            label749:
            while(true) {
               FullValueDescription var58;
               if (var53.hasMoreElements() && this.spClass > var5) {
                  var58 = (FullValueDescription)var53.nextElement();
                  String var57 = this.vhandler.getClassName(var58.id);
                  String var59 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));

                  boolean var13;
                  while(this.spClass > var5 && !var57.equals(var59)) {
                     int var60 = this.findNextClass(var57, this.classes, this.spClass, var5);
                     if (var60 != -1) {
                        this.spClass = var60;
                        var7 = this.currentClass = this.classes[this.spClass];
                        var59 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));
                     } else {
                        if (var58.is_custom) {
                           this.readFormatVersion();
                           var13 = this.readBoolean();
                           if (var13) {
                              this.inputClassFields((Object)null, (Class)null, (ObjectStreamClass)null, var58.members, var3);
                           }

                           if (this.getStreamFormatVersion() == 2) {
                              ((ValueInputStream)this.getOrbStream()).start_value();
                              ((ValueInputStream)this.getOrbStream()).end_value();
                           }
                        } else {
                           this.inputClassFields((Object)null, this.currentClass, (ObjectStreamClass)null, var58.members, var3);
                        }

                        if (!var53.hasMoreElements()) {
                           Object var62 = this.currentObject;
                           return var62;
                        }

                        var58 = (FullValueDescription)var53.nextElement();
                        var57 = this.vhandler.getClassName(var58.id);
                     }
                  }

                  var6 = this.currentClassDesc = ObjectStreamClass.lookup(this.currentClass);
                  if (!var59.equals("java.lang.Object")) {
                     InputStreamHook.ReadObjectState var61 = this.readObjectState;
                     this.setState(DEFAULT_STATE);

                     try {
                        if (var58.is_custom) {
                           this.readFormatVersion();
                           var13 = this.readBoolean();
                           this.readObjectState.beginUnmarshalCustomValue(this, var13, this.currentClassDesc.readObjectMethod != null);
                        }

                        var13 = false;

                        try {
                           if (!var58.is_custom && this.currentClassDesc.hasReadObject()) {
                              this.setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                           }

                           this.defaultReadObjectFVDMembers = var58.members;
                           var13 = this.invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass);
                        } finally {
                           this.defaultReadObjectFVDMembers = null;
                        }

                        if (!var13 || this.readObjectState == IN_READ_OBJECT_DEFAULTS_SENT) {
                           this.inputClassFields(this.currentObject, this.currentClass, var6, var58.members, var3);
                        }

                        if (var58.is_custom) {
                           this.readObjectState.endUnmarshalCustomValue(this);
                        }
                     } finally {
                        this.setState(var61);
                     }

                     var7 = this.currentClass = this.classes[--this.spClass];
                     continue;
                  }

                  this.inputClassFields((Object)null, this.currentClass, (ObjectStreamClass)null, var58.members, var3);

                  while(true) {
                     if (!var53.hasMoreElements()) {
                        continue label749;
                     }

                     var58 = (FullValueDescription)var53.nextElement();
                     if (var58.is_custom) {
                        this.skipCustomUsingFVD(var58.members, var3);
                     } else {
                        this.inputClassFields((Object)null, this.currentClass, (ObjectStreamClass)null, var58.members, var3);
                     }
                  }
               }

               while(true) {
                  if (!var53.hasMoreElements()) {
                     break label749;
                  }

                  var58 = (FullValueDescription)var53.nextElement();
                  if (var58.is_custom) {
                     this.skipCustomUsingFVD(var58.members, var3);
                  } else {
                     this.throwAwayData(var58.members, var3);
                  }
               }
            }
         }

         var55 = this.currentObject;
      } finally {
         this.spClass = var5;
         this.activeRecursionMgr.removeObject(var4);
      }

      return var55;
   }

   private Object skipObjectUsingFVD(String var1, CodeBase var2) throws IOException, ClassNotFoundException {
      Enumeration var3 = this.getOrderedDescriptions(var1, var2).elements();

      while(var3.hasMoreElements()) {
         FullValueDescription var4 = (FullValueDescription)var3.nextElement();
         String var5 = this.vhandler.getClassName(var4.id);
         if (!var5.equals("java.lang.Object")) {
            if (var4.is_custom) {
               this.readFormatVersion();
               boolean var6 = this.readBoolean();
               if (var6) {
                  this.inputClassFields((Object)null, (Class)null, (ObjectStreamClass)null, var4.members, var2);
               }

               if (this.getStreamFormatVersion() == 2) {
                  ((ValueInputStream)this.getOrbStream()).start_value();
                  ((ValueInputStream)this.getOrbStream()).end_value();
               }
            } else {
               this.inputClassFields((Object)null, (Class)null, (ObjectStreamClass)null, var4.members, var2);
            }
         }
      }

      return null;
   }

   private int findNextClass(String var1, Class[] var2, int var3, int var4) {
      for(int var5 = var3; var5 > var4; --var5) {
         if (var1.equals(var2[var5].getName())) {
            return var5;
         }
      }

      return -1;
   }

   private boolean invokeObjectReader(ObjectStreamClass var1, Object var2, Class var3) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      if (var1.readObjectMethod == null) {
         return false;
      } else {
         try {
            var1.readObjectMethod.invoke(var2, this.readObjectArgList);
            return true;
         } catch (InvocationTargetException var6) {
            Throwable var5 = var6.getTargetException();
            if (var5 instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)var5;
            } else if (var5 instanceof IOException) {
               throw (IOException)var5;
            } else if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            } else if (var5 instanceof Error) {
               throw (Error)var5;
            } else {
               throw new Error("internal error");
            }
         } catch (IllegalAccessException var7) {
            return false;
         }
      }
   }

   private void resetStream() throws IOException {
      int var1;
      if (this.classes == null) {
         this.classes = new Class[20];
      } else {
         for(var1 = 0; var1 < this.classes.length; ++var1) {
            this.classes[var1] = null;
         }
      }

      if (this.classdesc == null) {
         this.classdesc = new ObjectStreamClass[20];
      } else {
         for(var1 = 0; var1 < this.classdesc.length; ++var1) {
            this.classdesc[var1] = null;
         }
      }

      this.spClass = 0;
      if (this.callbacks != null) {
         this.callbacks.setSize(0);
      }

   }

   private void inputPrimitiveField(Object var1, Class var2, ObjectStreamField var3) throws InvalidClassException, IOException {
      try {
         switch(var3.getTypeCode()) {
         case 'B':
            byte var4 = this.orbStream.read_octet();
            if (var3.getField() != null) {
               bridge.putByte(var1, var3.getFieldID(), var4);
            }
            break;
         case 'C':
            char var6 = this.orbStream.read_wchar();
            if (var3.getField() != null) {
               bridge.putChar(var1, var3.getFieldID(), var6);
            }
            break;
         case 'D':
            double var12 = this.orbStream.read_double();
            if (var3.getField() != null) {
               bridge.putDouble(var1, var3.getFieldID(), var12);
            }
            break;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
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
            float var11 = this.orbStream.read_float();
            if (var3.getField() != null) {
               bridge.putFloat(var1, var3.getFieldID(), var11);
            }
            break;
         case 'I':
            int var8 = this.orbStream.read_long();
            if (var3.getField() != null) {
               bridge.putInt(var1, var3.getFieldID(), var8);
            }
            break;
         case 'J':
            long var9 = this.orbStream.read_longlong();
            if (var3.getField() != null) {
               bridge.putLong(var1, var3.getFieldID(), var9);
            }
            break;
         case 'S':
            short var7 = this.orbStream.read_short();
            if (var3.getField() != null) {
               bridge.putShort(var1, var3.getFieldID(), var7);
            }
            break;
         case 'Z':
            boolean var15 = this.orbStream.read_boolean();
            if (var3.getField() != null) {
               bridge.putBoolean(var1, var3.getFieldID(), var15);
            }
         }

      } catch (IllegalArgumentException var14) {
         ClassCastException var5 = new ClassCastException("Assigning instance of class " + var3.getType().getName() + " to field " + this.currentClassDesc.getName() + '#' + var3.getField().getName());
         var5.initCause(var14);
         throw var5;
      }
   }

   private Object inputObjectField(ValueMember var1, CodeBase var2) throws IndirectionException, ClassNotFoundException, IOException, StreamCorruptedException {
      Object var3 = null;
      Class var4 = null;
      String var5 = var1.id;

      try {
         var4 = this.vhandler.getClassFromType(var5);
      } catch (ClassNotFoundException var8) {
         var4 = null;
      }

      String var6 = null;
      if (var4 != null) {
         var6 = ValueUtility.getSignature(var1);
      }

      if (var6 != null && (var6.equals("Ljava/lang/Object;") || var6.equals("Ljava/io/Serializable;") || var6.equals("Ljava/io/Externalizable;"))) {
         var3 = Util.readAny(this.orbStream);
      } else {
         byte var7 = 2;
         if (!this.vhandler.isSequence(var5)) {
            if (var1.type.kind().value() == kRemoteTypeCode.kind().value()) {
               var7 = 0;
            } else if (var4 != null && var4.isInterface() && (this.vhandler.isAbstractBase(var4) || ObjectStreamClassCorbaExt.isAbstractInterface(var4))) {
               var7 = 1;
            }
         }

         switch(var7) {
         case 0:
            if (var4 != null) {
               var3 = Utility.readObjectAndNarrow(this.orbStream, var4);
            } else {
               var3 = this.orbStream.read_Object();
            }
            break;
         case 1:
            if (var4 != null) {
               var3 = Utility.readAbstractAndNarrow(this.orbStream, var4);
            } else {
               var3 = this.orbStream.read_abstract_interface();
            }
            break;
         case 2:
            if (var4 != null) {
               var3 = this.orbStream.read_value(var4);
            } else {
               var3 = this.orbStream.read_value();
            }
            break;
         default:
            throw new StreamCorruptedException("Unknown callType: " + var7);
         }
      }

      return var3;
   }

   private Object inputObjectField(ObjectStreamField var1) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IndirectionException, IOException {
      if (ObjectStreamClassCorbaExt.isAny(var1.getTypeString())) {
         return Util.readAny(this.orbStream);
      } else {
         Object var2 = null;
         Class var3 = var1.getType();
         Class var4 = var3;
         byte var5 = 2;
         boolean var6 = false;
         if (var3.isInterface()) {
            boolean var7 = false;
            if (Remote.class.isAssignableFrom(var3)) {
               var5 = 0;
            } else if (org.omg.CORBA.Object.class.isAssignableFrom(var3)) {
               var5 = 0;
               var7 = true;
            } else if (this.vhandler.isAbstractBase(var3)) {
               var5 = 1;
               var7 = true;
            } else if (ObjectStreamClassCorbaExt.isAbstractInterface(var3)) {
               var5 = 1;
            }

            if (var7) {
               try {
                  String var8 = Util.getCodebase(var3);
                  String var9 = this.vhandler.createForAnyType(var3);
                  Class var10 = Utility.loadStubClass(var9, var8, var3);
                  var4 = var10;
               } catch (ClassNotFoundException var11) {
                  var6 = true;
               }
            } else {
               var6 = true;
            }
         }

         switch(var5) {
         case 0:
            if (!var6) {
               var2 = this.orbStream.read_Object(var4);
            } else {
               var2 = Utility.readObjectAndNarrow(this.orbStream, var4);
            }
            break;
         case 1:
            if (!var6) {
               var2 = this.orbStream.read_abstract_interface(var4);
            } else {
               var2 = Utility.readAbstractAndNarrow(this.orbStream, var4);
            }
            break;
         case 2:
            var2 = this.orbStream.read_value(var4);
            break;
         default:
            throw new StreamCorruptedException("Unknown callType: " + var5);
         }

         return var2;
      }
   }

   private final boolean mustUseRemoteValueMembers() {
      return this.defaultReadObjectFVDMembers != null;
   }

   void readFields(Map var1) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      if (this.mustUseRemoteValueMembers()) {
         this.inputRemoteMembersForReadFields(var1);
      } else {
         this.inputCurrentClassFieldsForReadFields(var1);
      }

   }

   private final void inputRemoteMembersForReadFields(Map var1) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      ValueMember[] var2 = this.defaultReadObjectFVDMembers;

      try {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            switch(var2[var3].type.kind().value()) {
            case 2:
               short var7 = this.orbStream.read_short();
               var1.put(var2[var3].name, new Short(var7));
               break;
            case 3:
               int var8 = this.orbStream.read_long();
               var1.put(var2[var3].name, new Integer(var8));
               break;
            case 4:
            case 5:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 24:
            case 25:
            case 27:
            case 28:
            default:
               throw new StreamCorruptedException("Unknown kind: " + var2[var3].type.kind().value());
            case 6:
               float var11 = this.orbStream.read_float();
               var1.put(var2[var3].name, new Float(var11));
               break;
            case 7:
               double var12 = this.orbStream.read_double();
               var1.put(var2[var3].name, new Double(var12));
               break;
            case 8:
               boolean var5 = this.orbStream.read_boolean();
               var1.put(var2[var3].name, new Boolean(var5));
               break;
            case 9:
            case 26:
               char var6 = this.orbStream.read_wchar();
               var1.put(var2[var3].name, new Character(var6));
               break;
            case 10:
               byte var18 = this.orbStream.read_octet();
               var1.put(var2[var3].name, new Byte(var18));
               break;
            case 14:
            case 29:
            case 30:
               Object var14 = null;

               try {
                  var14 = this.inputObjectField(var2[var3], this.cbSender);
               } catch (IndirectionException var16) {
                  var14 = this.activeRecursionMgr.getObject(var16.offset);
               }

               var1.put(var2[var3].name, var14);
               break;
            case 23:
               long var9 = this.orbStream.read_longlong();
               var1.put(var2[var3].name, new Long(var9));
            }
         }

      } catch (Throwable var17) {
         StreamCorruptedException var4 = new StreamCorruptedException(var17.getMessage());
         var4.initCause(var17);
         throw var4;
      }
   }

   private final void inputCurrentClassFieldsForReadFields(Map var1) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      ObjectStreamField[] var2 = this.currentClassDesc.getFieldsNoCopy();
      int var3 = var2.length - this.currentClassDesc.objFields;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         switch(var2[var4].getTypeCode()) {
         case 'B':
            byte var5 = this.orbStream.read_octet();
            var1.put(var2[var4].getName(), new Byte(var5));
            break;
         case 'C':
            char var7 = this.orbStream.read_wchar();
            var1.put(var2[var4].getName(), new Character(var7));
            break;
         case 'D':
            double var13 = this.orbStream.read_double();
            var1.put(var2[var4].getName(), new Double(var13));
            break;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
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
            float var12 = this.orbStream.read_float();
            var1.put(var2[var4].getName(), new Float(var12));
            break;
         case 'I':
            int var9 = this.orbStream.read_long();
            var1.put(var2[var4].getName(), new Integer(var9));
            break;
         case 'J':
            long var10 = this.orbStream.read_longlong();
            var1.put(var2[var4].getName(), new Long(var10));
            break;
         case 'S':
            short var8 = this.orbStream.read_short();
            var1.put(var2[var4].getName(), new Short(var8));
            break;
         case 'Z':
            boolean var6 = this.orbStream.read_boolean();
            var1.put(var2[var4].getName(), new Boolean(var6));
         }
      }

      if (this.currentClassDesc.objFields > 0) {
         for(var4 = var3; var4 < var2.length; ++var4) {
            Object var16 = null;

            try {
               var16 = this.inputObjectField(var2[var4]);
            } catch (IndirectionException var15) {
               var16 = this.activeRecursionMgr.getObject(var15.offset);
            }

            var1.put(var2[var4].getName(), var16);
         }
      }

   }

   private void inputClassFields(Object var1, Class<?> var2, ObjectStreamField[] var3, CodeBase var4) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      int var5 = var3.length - this.currentClassDesc.objFields;
      int var6;
      if (var1 != null) {
         for(var6 = 0; var6 < var5; ++var6) {
            this.inputPrimitiveField(var1, var2, var3[var6]);
         }
      }

      if (this.currentClassDesc.objFields > 0) {
         for(var6 = var5; var6 < var3.length; ++var6) {
            Object var7 = null;

            try {
               var7 = this.inputObjectField(var3[var6]);
            } catch (IndirectionException var13) {
               var7 = this.activeRecursionMgr.getObject(var13.offset);
            }

            if (var1 != null && var3[var6].getField() != null) {
               String var9;
               String var10;
               try {
                  Class var8 = var3[var6].getClazz();
                  if (var7 != null && !var8.isAssignableFrom(var7.getClass())) {
                     throw new IllegalArgumentException("Field mismatch");
                  }

                  var9 = null;
                  var10 = var3[var6].getName();

                  Field var19;
                  try {
                     var19 = getDeclaredField(var2, var10);
                  } catch (PrivilegedActionException var14) {
                     throw new IllegalArgumentException((NoSuchFieldException)var14.getException());
                  } catch (SecurityException var15) {
                     throw new IllegalArgumentException(var15);
                  } catch (NullPointerException var16) {
                     continue;
                  } catch (NoSuchFieldException var17) {
                     continue;
                  }

                  if (var19 != null) {
                     Class var20 = var19.getType();
                     if (!var20.isAssignableFrom(var8)) {
                        throw new IllegalArgumentException("Field Type mismatch");
                     }

                     if (var7 != null && !var8.isInstance(var7)) {
                        throw new IllegalArgumentException();
                     }

                     bridge.putObject(var1, var3[var6].getFieldID(), var7);
                  }
               } catch (IllegalArgumentException var18) {
                  var9 = "null";
                  var10 = "null";
                  String var11 = "null";
                  if (var7 != null) {
                     var9 = var7.getClass().getName();
                  }

                  if (this.currentClassDesc != null) {
                     var10 = this.currentClassDesc.getName();
                  }

                  if (var3[var6] != null && var3[var6].getField() != null) {
                     var11 = var3[var6].getField().getName();
                  }

                  ClassCastException var12 = new ClassCastException("Assigning instance of class " + var9 + " to field " + var10 + '#' + var11);
                  var12.initCause(var18);
                  throw var12;
               }
            }
         }
      }

   }

   private void inputClassFields(Object var1, Class var2, ObjectStreamClass var3, ValueMember[] var4, CodeBase var5) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      try {
         for(int var6 = 0; var6 < var4.length; ++var6) {
            try {
               switch(var4[var6].type.kind().value()) {
               case 2:
                  short var10 = this.orbStream.read_short();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setShortField(var1, var2, var4[var6].name, var10);
                  }
                  break;
               case 3:
                  int var11 = this.orbStream.read_long();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setIntField(var1, var2, var4[var6].name, var11);
                  }
                  break;
               case 4:
               case 5:
               case 11:
               case 12:
               case 13:
               case 15:
               case 16:
               case 17:
               case 18:
               case 19:
               case 20:
               case 21:
               case 22:
               case 24:
               case 25:
               case 27:
               case 28:
               default:
                  throw new StreamCorruptedException("Unknown kind: " + var4[var6].type.kind().value());
               case 6:
                  float var14 = this.orbStream.read_float();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setFloatField(var1, var2, var4[var6].name, var14);
                  }
                  break;
               case 7:
                  double var15 = this.orbStream.read_double();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setDoubleField(var1, var2, var4[var6].name, var15);
                  }
                  break;
               case 8:
                  boolean var25 = this.orbStream.read_boolean();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setBooleanField(var1, var2, var4[var6].name, var25);
                  }
                  break;
               case 9:
               case 26:
                  char var9 = this.orbStream.read_wchar();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setCharField(var1, var2, var4[var6].name, var9);
                  }
                  break;
               case 10:
                  byte var24 = this.orbStream.read_octet();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setByteField(var1, var2, var4[var6].name, var24);
                  }
                  break;
               case 14:
               case 29:
               case 30:
                  Object var17 = null;

                  try {
                     var17 = this.inputObjectField(var4[var6], var5);
                  } catch (IndirectionException var21) {
                     var17 = this.activeRecursionMgr.getObject(var21.offset);
                  }

                  if (var1 != null) {
                     try {
                        if (var3.hasField(var4[var6])) {
                           setObjectField(var1, var2, var4[var6].name, var17);
                        }
                     } catch (IllegalArgumentException var20) {
                        ClassCastException var19 = new ClassCastException("Assigning instance of class " + var17.getClass().getName() + " to field " + var4[var6].name);
                        var19.initCause(var20);
                        throw var19;
                     }
                  }
                  break;
               case 23:
                  long var12 = this.orbStream.read_longlong();
                  if (var1 != null && var3.hasField(var4[var6])) {
                     setLongField(var1, var2, var4[var6].name, var12);
                  }
               }
            } catch (IllegalArgumentException var22) {
               ClassCastException var8 = new ClassCastException("Assigning instance of class " + var4[var6].id + " to field " + this.currentClassDesc.getName() + '#' + var4[var6].name);
               var8.initCause(var22);
               throw var8;
            }
         }

      } catch (Throwable var23) {
         StreamCorruptedException var7 = new StreamCorruptedException(var23.getMessage());
         var7.initCause(var23);
         throw var7;
      }
   }

   private void skipCustomUsingFVD(ValueMember[] var1, CodeBase var2) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      this.readFormatVersion();
      boolean var3 = this.readBoolean();
      if (var3) {
         this.throwAwayData(var1, var2);
      }

      if (this.getStreamFormatVersion() == 2) {
         ((ValueInputStream)this.getOrbStream()).start_value();
         ((ValueInputStream)this.getOrbStream()).end_value();
      }

   }

   private void throwAwayData(ValueMember[] var1, CodeBase var2) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         try {
            switch(var1[var3].type.kind().value()) {
            case 2:
               this.orbStream.read_short();
               break;
            case 3:
               this.orbStream.read_long();
               break;
            case 4:
            case 5:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 24:
            case 25:
            case 27:
            case 28:
            default:
               throw new StreamCorruptedException("Unknown kind: " + var1[var3].type.kind().value());
            case 6:
               this.orbStream.read_float();
               break;
            case 7:
               this.orbStream.read_double();
               break;
            case 8:
               this.orbStream.read_boolean();
               break;
            case 9:
            case 26:
               this.orbStream.read_wchar();
               break;
            case 10:
               this.orbStream.read_octet();
               break;
            case 14:
            case 29:
            case 30:
               Class var4 = null;
               String var12 = var1[var3].id;

               try {
                  var4 = this.vhandler.getClassFromType(var12);
               } catch (ClassNotFoundException var9) {
                  var4 = null;
               }

               String var6 = null;
               if (var4 != null) {
                  var6 = ValueUtility.getSignature(var1[var3]);
               }

               try {
                  if (var6 == null || !var6.equals("Ljava/lang/Object;") && !var6.equals("Ljava/io/Serializable;") && !var6.equals("Ljava/io/Externalizable;")) {
                     byte var7 = 2;
                     if (!this.vhandler.isSequence(var12)) {
                        FullValueDescription var8 = var2.meta(var1[var3].id);
                        if (kRemoteTypeCode == var1[var3].type) {
                           var7 = 0;
                        } else if (var8.is_abstract) {
                           var7 = 1;
                        }
                     }

                     switch(var7) {
                     case 0:
                        this.orbStream.read_Object();
                        break;
                     case 1:
                        this.orbStream.read_abstract_interface();
                        break;
                     case 2:
                        if (var4 != null) {
                           this.orbStream.read_value(var4);
                        } else {
                           this.orbStream.read_value();
                        }
                        break;
                     default:
                        throw new StreamCorruptedException("Unknown callType: " + var7);
                     }
                  } else {
                     Util.readAny(this.orbStream);
                  }
               } catch (IndirectionException var10) {
               }
               break;
            case 23:
               this.orbStream.read_longlong();
            }
         } catch (IllegalArgumentException var11) {
            ClassCastException var5 = new ClassCastException("Assigning instance of class " + var1[var3].id + " to field " + this.currentClassDesc.getName() + '#' + var1[var3].name);
            var5.initCause(var11);
            throw var5;
         }
      }

   }

   private static void setObjectField(Object var0, Class<?> var1, String var2, Object var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         Class var5 = var4.getType();
         if (var3 != null && !var5.isInstance(var3)) {
            throw new Exception();
         } else {
            long var6 = bridge.objectFieldOffset(var4);
            bridge.putObject(var0, var6, var3);
         }
      } catch (Exception var8) {
         if (var0 != null) {
            throw utilWrapper.errorSetObjectField((Throwable)var8, var2, var0.toString(), var3.toString());
         } else {
            throw utilWrapper.errorSetObjectField((Throwable)var8, var2, "null " + var1.getName() + " object", var3.toString());
         }
      }
   }

   private static void setBooleanField(Object var0, Class<?> var1, String var2, boolean var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Boolean.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putBoolean(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetBooleanField((Throwable)var7, var2, var0.toString(), new Boolean(var3));
         } else {
            throw utilWrapper.errorSetBooleanField((Throwable)var7, var2, "null " + var1.getName() + " object", new Boolean(var3));
         }
      }
   }

   private static void setByteField(Object var0, Class<?> var1, String var2, byte var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Byte.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putByte(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetByteField((Throwable)var7, var2, var0.toString(), new Byte(var3));
         } else {
            throw utilWrapper.errorSetByteField((Throwable)var7, var2, "null " + var1.getName() + " object", new Byte(var3));
         }
      }
   }

   private static void setCharField(Object var0, Class<?> var1, String var2, char var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Character.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putChar(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetCharField((Throwable)var7, var2, var0.toString(), new Character(var3));
         } else {
            throw utilWrapper.errorSetCharField((Throwable)var7, var2, "null " + var1.getName() + " object", new Character(var3));
         }
      }
   }

   private static void setShortField(Object var0, Class<?> var1, String var2, short var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Short.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putShort(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetShortField((Throwable)var7, var2, var0.toString(), new Short(var3));
         } else {
            throw utilWrapper.errorSetShortField((Throwable)var7, var2, "null " + var1.getName() + " object", new Short(var3));
         }
      }
   }

   private static void setIntField(Object var0, Class<?> var1, String var2, int var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Integer.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putInt(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetIntField((Throwable)var7, var2, var0.toString(), new Integer(var3));
         } else {
            throw utilWrapper.errorSetIntField((Throwable)var7, var2, "null " + var1.getName() + " object", new Integer(var3));
         }
      }
   }

   private static void setLongField(Object var0, Class<?> var1, String var2, long var3) {
      try {
         Field var5 = getDeclaredField(var1, var2);
         if (var5 != null && var5.getType() == Long.TYPE) {
            long var6 = bridge.objectFieldOffset(var5);
            bridge.putLong(var0, var6, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var8) {
         if (var0 != null) {
            throw utilWrapper.errorSetLongField((Throwable)var8, var2, var0.toString(), new Long(var3));
         } else {
            throw utilWrapper.errorSetLongField((Throwable)var8, var2, "null " + var1.getName() + " object", new Long(var3));
         }
      }
   }

   private static void setFloatField(Object var0, Class<?> var1, String var2, float var3) {
      try {
         Field var4 = getDeclaredField(var1, var2);
         if (var4 != null && var4.getType() == Float.TYPE) {
            long var5 = bridge.objectFieldOffset(var4);
            bridge.putFloat(var0, var5, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var7) {
         if (var0 != null) {
            throw utilWrapper.errorSetFloatField((Throwable)var7, var2, var0.toString(), new Float(var3));
         } else {
            throw utilWrapper.errorSetFloatField((Throwable)var7, var2, "null " + var1.getName() + " object", new Float(var3));
         }
      }
   }

   private static void setDoubleField(Object var0, Class<?> var1, String var2, double var3) {
      try {
         Field var5 = getDeclaredField(var1, var2);
         if (var5 != null && var5.getType() == Double.TYPE) {
            long var6 = bridge.objectFieldOffset(var5);
            bridge.putDouble(var0, var6, var3);
         } else {
            throw new InvalidObjectException("Field Type mismatch");
         }
      } catch (Exception var8) {
         if (var0 != null) {
            throw utilWrapper.errorSetDoubleField((Throwable)var8, var2, var0.toString(), new Double(var3));
         } else {
            throw utilWrapper.errorSetDoubleField((Throwable)var8, var2, "null " + var1.getName() + " object", new Double(var3));
         }
      }
   }

   private static Field getDeclaredField(final Class<?> var0, final String var1) throws PrivilegedActionException, NoSuchFieldException, SecurityException {
      return System.getSecurityManager() == null ? var0.getDeclaredField(var1) : (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
         public Field run() throws NoSuchFieldException {
            return var0.getDeclaredField(var1);
         }
      });
   }

   static {
      kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
      kValueTypeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
      OPT_DATA_EXCEPTION_CTOR = getOptDataExceptionCtor();
   }

   static class ActiveRecursionManager {
      private Map<Integer, Object> offsetToObjectMap = new HashMap();

      public ActiveRecursionManager() {
      }

      public void addObject(int var1, Object var2) {
         this.offsetToObjectMap.put(new Integer(var1), var2);
      }

      public Object getObject(int var1) throws IOException {
         Integer var2 = new Integer(var1);
         if (!this.offsetToObjectMap.containsKey(var2)) {
            throw new IOException("Invalid indirection to offset " + var1);
         } else {
            return this.offsetToObjectMap.get(var2);
         }
      }

      public void removeObject(int var1) {
         this.offsetToObjectMap.remove(new Integer(var1));
      }

      public boolean containsObject(int var1) {
         return this.offsetToObjectMap.containsKey(new Integer(var1));
      }
   }
}
