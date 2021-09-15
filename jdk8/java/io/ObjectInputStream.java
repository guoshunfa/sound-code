package java.io;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.JavaOISAccess;
import sun.misc.ObjectInputFilter;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.reflect.misc.ReflectUtil;
import sun.util.logging.PlatformLogger;

public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants {
   private static final int NULL_HANDLE = -1;
   private static final Object unsharedMarker = new Object();
   private static final HashMap<String, Class<?>> primClasses = new HashMap(8, 1.0F);
   private final ObjectInputStream.BlockDataInputStream bin;
   private final ObjectInputStream.ValidationList vlist;
   private long depth;
   private long totalObjectRefs;
   private boolean closed;
   private final ObjectInputStream.HandleTable handles;
   private int passHandle = -1;
   private boolean defaultDataEnd = false;
   private byte[] primVals;
   private final boolean enableOverride;
   private boolean enableResolve;
   private SerialCallbackContext curContext;
   private ObjectInputFilter serialFilter;
   private volatile ObjectStreamClassValidator validator;

   public ObjectInputStream(InputStream var1) throws IOException {
      this.verifySubclass();
      this.bin = new ObjectInputStream.BlockDataInputStream(var1);
      this.handles = new ObjectInputStream.HandleTable(10);
      this.vlist = new ObjectInputStream.ValidationList();
      this.serialFilter = ObjectInputFilter.Config.getSerialFilter();
      this.enableOverride = false;
      this.readStreamHeader();
      this.bin.setBlockDataMode(true);
   }

   protected ObjectInputStream() throws IOException, SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
      }

      this.bin = null;
      this.handles = null;
      this.vlist = null;
      this.serialFilter = ObjectInputFilter.Config.getSerialFilter();
      this.enableOverride = true;
   }

   public final Object readObject() throws IOException, ClassNotFoundException {
      if (this.enableOverride) {
         return this.readObjectOverride();
      } else {
         int var1 = this.passHandle;

         Object var4;
         try {
            Object var2 = this.readObject0(false);
            this.handles.markDependency(var1, this.passHandle);
            ClassNotFoundException var3 = this.handles.lookupException(this.passHandle);
            if (var3 != null) {
               throw var3;
            }

            if (this.depth == 0L) {
               this.vlist.doCallbacks();
            }

            var4 = var2;
         } finally {
            this.passHandle = var1;
            if (this.closed && this.depth == 0L) {
               this.clear();
            }

         }

         return var4;
      }
   }

   protected Object readObjectOverride() throws IOException, ClassNotFoundException {
      return null;
   }

   public Object readUnshared() throws IOException, ClassNotFoundException {
      int var1 = this.passHandle;

      Object var4;
      try {
         Object var2 = this.readObject0(true);
         this.handles.markDependency(var1, this.passHandle);
         ClassNotFoundException var3 = this.handles.lookupException(this.passHandle);
         if (var3 != null) {
            throw var3;
         }

         if (this.depth == 0L) {
            this.vlist.doCallbacks();
         }

         var4 = var2;
      } finally {
         this.passHandle = var1;
         if (this.closed && this.depth == 0L) {
            this.clear();
         }

      }

      return var4;
   }

   public void defaultReadObject() throws IOException, ClassNotFoundException {
      SerialCallbackContext var1 = this.curContext;
      if (var1 == null) {
         throw new NotActiveException("not in call to readObject");
      } else {
         Object var2 = var1.getObj();
         ObjectStreamClass var3 = var1.getDesc();
         this.bin.setBlockDataMode(false);
         this.defaultReadFields(var2, var3);
         this.bin.setBlockDataMode(true);
         if (!var3.hasWriteObjectData()) {
            this.defaultDataEnd = true;
         }

         ClassNotFoundException var4 = this.handles.lookupException(this.passHandle);
         if (var4 != null) {
            throw var4;
         }
      }
   }

   public ObjectInputStream.GetField readFields() throws IOException, ClassNotFoundException {
      SerialCallbackContext var1 = this.curContext;
      if (var1 == null) {
         throw new NotActiveException("not in call to readObject");
      } else {
         Object var2 = var1.getObj();
         ObjectStreamClass var3 = var1.getDesc();
         this.bin.setBlockDataMode(false);
         ObjectInputStream.GetFieldImpl var4 = new ObjectInputStream.GetFieldImpl(var3);
         var4.readFields();
         this.bin.setBlockDataMode(true);
         if (!var3.hasWriteObjectData()) {
            this.defaultDataEnd = true;
         }

         return var4;
      }
   }

   public void registerValidation(ObjectInputValidation var1, int var2) throws NotActiveException, InvalidObjectException {
      if (this.depth == 0L) {
         throw new NotActiveException("stream inactive");
      } else {
         this.vlist.register(var1, var2);
      }
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      String var2 = var1.getName();

      try {
         return Class.forName(var2, false, latestUserDefinedLoader());
      } catch (ClassNotFoundException var5) {
         Class var4 = (Class)primClasses.get(var2);
         if (var4 != null) {
            return var4;
         } else {
            throw var5;
         }
      }
   }

   protected Class<?> resolveProxyClass(String[] var1) throws IOException, ClassNotFoundException {
      ClassLoader var2 = latestUserDefinedLoader();
      ClassLoader var3 = null;
      boolean var4 = false;
      Class[] var5 = new Class[var1.length];

      for(int var6 = 0; var6 < var1.length; ++var6) {
         Class var7 = Class.forName(var1[var6], false, var2);
         if ((var7.getModifiers() & 1) == 0) {
            if (var4) {
               if (var3 != var7.getClassLoader()) {
                  throw new IllegalAccessError("conflicting non-public interface class loaders");
               }
            } else {
               var3 = var7.getClassLoader();
               var4 = true;
            }
         }

         var5[var6] = var7;
      }

      try {
         return Proxy.getProxyClass(var4 ? var3 : var2, var5);
      } catch (IllegalArgumentException var8) {
         throw new ClassNotFoundException((String)null, var8);
      }
   }

   protected Object resolveObject(Object var1) throws IOException {
      return var1;
   }

   protected boolean enableResolveObject(boolean var1) throws SecurityException {
      if (var1 == this.enableResolve) {
         return var1;
      } else {
         if (var1) {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkPermission(SUBSTITUTION_PERMISSION);
            }
         }

         this.enableResolve = var1;
         return !this.enableResolve;
      }
   }

   protected void readStreamHeader() throws IOException, StreamCorruptedException {
      short var1 = this.bin.readShort();
      short var2 = this.bin.readShort();
      if (var1 != -21267 || var2 != 5) {
         throw new StreamCorruptedException(String.format("invalid stream header: %04X%04X", var1, var2));
      }
   }

   protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
      ObjectStreamClass var1 = new ObjectStreamClass();
      var1.readNonProxy(this);
      return var1;
   }

   public int read() throws IOException {
      return this.bin.read();
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var4 = var2 + var3;
         if (var2 >= 0 && var3 >= 0 && var4 <= var1.length && var4 >= 0) {
            return this.bin.read(var1, var2, var3, false);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int available() throws IOException {
      return this.bin.available();
   }

   public void close() throws IOException {
      this.closed = true;
      if (this.depth == 0L) {
         this.clear();
      }

      this.bin.close();
   }

   public boolean readBoolean() throws IOException {
      return this.bin.readBoolean();
   }

   public byte readByte() throws IOException {
      return this.bin.readByte();
   }

   public int readUnsignedByte() throws IOException {
      return this.bin.readUnsignedByte();
   }

   public char readChar() throws IOException {
      return this.bin.readChar();
   }

   public short readShort() throws IOException {
      return this.bin.readShort();
   }

   public int readUnsignedShort() throws IOException {
      return this.bin.readUnsignedShort();
   }

   public int readInt() throws IOException {
      return this.bin.readInt();
   }

   public long readLong() throws IOException {
      return this.bin.readLong();
   }

   public float readFloat() throws IOException {
      return this.bin.readFloat();
   }

   public double readDouble() throws IOException {
      return this.bin.readDouble();
   }

   public void readFully(byte[] var1) throws IOException {
      this.bin.readFully(var1, 0, var1.length, false);
   }

   public void readFully(byte[] var1, int var2, int var3) throws IOException {
      int var4 = var2 + var3;
      if (var2 >= 0 && var3 >= 0 && var4 <= var1.length && var4 >= 0) {
         this.bin.readFully(var1, var2, var3, false);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int skipBytes(int var1) throws IOException {
      return this.bin.skipBytes(var1);
   }

   /** @deprecated */
   @Deprecated
   public String readLine() throws IOException {
      return this.bin.readLine();
   }

   public String readUTF() throws IOException {
      return this.bin.readUTF();
   }

   private final ObjectInputFilter getInternalObjectInputFilter() {
      return this.serialFilter;
   }

   private final void setInternalObjectInputFilter(ObjectInputFilter var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new SerializablePermission("serialFilter"));
      }

      if (this.serialFilter != null && this.serialFilter != ObjectInputFilter.Config.getSerialFilter()) {
         throw new IllegalStateException("filter can not be set more than once");
      } else {
         this.serialFilter = var1;
      }
   }

   private void filterCheck(Class<?> var1, int var2) throws InvalidClassException {
      if (this.serialFilter != null) {
         RuntimeException var3 = null;
         long var5 = this.bin == null ? 0L : this.bin.getBytesRead();

         ObjectInputFilter.Status var4;
         try {
            var4 = this.serialFilter.checkInput(new ObjectInputStream.FilterValues(var1, (long)var2, this.totalObjectRefs, this.depth, var5));
         } catch (RuntimeException var8) {
            var4 = ObjectInputFilter.Status.REJECTED;
            var3 = var8;
         }

         if (var4 == null || var4 == ObjectInputFilter.Status.REJECTED) {
            if (ObjectInputStream.Logging.infoLogger != null) {
               ObjectInputStream.Logging.infoLogger.info("ObjectInputFilter {0}: {1}, array length: {2}, nRefs: {3}, depth: {4}, bytes: {5}, ex: {6}", var4, var1, var2, this.totalObjectRefs, this.depth, var5, Objects.toString(var3, "n/a"));
            }

            InvalidClassException var7 = new InvalidClassException("filter status: " + var4);
            var7.initCause(var3);
            throw var7;
         }

         if (ObjectInputStream.Logging.traceLogger != null) {
            ObjectInputStream.Logging.traceLogger.finer("ObjectInputFilter {0}: {1}, array length: {2}, nRefs: {3}, depth: {4}, bytes: {5}, ex: {6}", var4, var1, var2, this.totalObjectRefs, this.depth, var5, Objects.toString(var3, "n/a"));
         }
      }

   }

   private void checkArray(Class<?> var1, int var2) throws InvalidClassException {
      Objects.requireNonNull(var1);
      if (!var1.isArray()) {
         throw new IllegalArgumentException("not an array type");
      } else if (var2 < 0) {
         throw new NegativeArraySizeException();
      } else {
         this.filterCheck(var1, var2);
      }
   }

   private void verifySubclass() {
      Class var1 = this.getClass();
      if (var1 != ObjectInputStream.class) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            ObjectStreamClass.processQueue(ObjectInputStream.Caches.subclassAuditsQueue, ObjectInputStream.Caches.subclassAudits);
            ObjectStreamClass.WeakClassKey var3 = new ObjectStreamClass.WeakClassKey(var1, ObjectInputStream.Caches.subclassAuditsQueue);
            Boolean var4 = (Boolean)ObjectInputStream.Caches.subclassAudits.get(var3);
            if (var4 == null) {
               var4 = auditSubclass(var1);
               ObjectInputStream.Caches.subclassAudits.putIfAbsent(var3, var4);
            }

            if (!var4) {
               var2.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
         }
      }
   }

   private static boolean auditSubclass(final Class<?> var0) {
      Boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            Class var1 = var0;

            while(var1 != ObjectInputStream.class) {
               try {
                  var1.getDeclaredMethod("readUnshared", (Class[])null);
                  return Boolean.FALSE;
               } catch (NoSuchMethodException var4) {
                  try {
                     var1.getDeclaredMethod("readFields", (Class[])null);
                     return Boolean.FALSE;
                  } catch (NoSuchMethodException var3) {
                     var1 = var1.getSuperclass();
                  }
               }
            }

            return Boolean.TRUE;
         }
      });
      return var1;
   }

   private void clear() {
      this.handles.clear();
      this.vlist.clear();
   }

   private Object readObject0(boolean var1) throws IOException {
      boolean var2 = this.bin.getBlockDataMode();
      if (var2) {
         int var3 = this.bin.currentBlockRemaining();
         if (var3 > 0) {
            throw new OptionalDataException(var3);
         }

         if (this.defaultDataEnd) {
            throw new OptionalDataException(true);
         }

         this.bin.setBlockDataMode(false);
      }

      byte var8;
      while((var8 = this.bin.peekByte()) == 121) {
         this.bin.readByte();
         this.handleReset();
      }

      ++this.depth;
      ++this.totalObjectRefs;

      Object var4;
      try {
         switch(var8) {
         case 112:
            var4 = this.readNull();
            return var4;
         case 113:
            var4 = this.readHandle(var1);
            return var4;
         case 114:
         case 125:
            ObjectStreamClass var11 = this.readClassDesc(var1);
            return var11;
         case 115:
            var4 = this.checkResolve(this.readOrdinaryObject(var1));
            return var4;
         case 116:
         case 124:
            var4 = this.checkResolve(this.readString(var1));
            return var4;
         case 117:
            var4 = this.checkResolve(this.readArray(var1));
            return var4;
         case 118:
            Class var10 = this.readClass(var1);
            return var10;
         case 119:
         case 122:
            if (var2) {
               this.bin.setBlockDataMode(true);
               this.bin.peek();
               throw new OptionalDataException(this.bin.currentBlockRemaining());
            }

            throw new StreamCorruptedException("unexpected block data");
         case 120:
            if (var2) {
               throw new OptionalDataException(true);
            }

            throw new StreamCorruptedException("unexpected end of block data");
         case 121:
         default:
            throw new StreamCorruptedException(String.format("invalid type code: %02X", var8));
         case 123:
            IOException var9 = this.readFatalException();
            throw new WriteAbortedException("writing aborted", var9);
         case 126:
            var4 = this.checkResolve(this.readEnum(var1));
         }
      } finally {
         --this.depth;
         this.bin.setBlockDataMode(var2);
      }

      return var4;
   }

   private Object checkResolve(Object var1) throws IOException {
      if (this.enableResolve && this.handles.lookupException(this.passHandle) == null) {
         Object var2 = this.resolveObject(var1);
         if (var2 != var1) {
            if (var2 != null) {
               if (var2.getClass().isArray()) {
                  this.filterCheck(var2.getClass(), Array.getLength(var2));
               } else {
                  this.filterCheck(var2.getClass(), -1);
               }
            }

            this.handles.setObject(this.passHandle, var2);
         }

         return var2;
      } else {
         return var1;
      }
   }

   String readTypeString() throws IOException {
      int var1 = this.passHandle;

      try {
         byte var2 = this.bin.peekByte();
         String var3;
         switch(var2) {
         case 112:
            var3 = (String)this.readNull();
            return var3;
         case 113:
            var3 = (String)this.readHandle(false);
            return var3;
         case 116:
         case 124:
            var3 = this.readString(false);
            return var3;
         default:
            throw new StreamCorruptedException(String.format("invalid type code: %02X", var2));
         }
      } finally {
         this.passHandle = var1;
      }
   }

   private Object readNull() throws IOException {
      if (this.bin.readByte() != 112) {
         throw new InternalError();
      } else {
         this.passHandle = -1;
         return null;
      }
   }

   private Object readHandle(boolean var1) throws IOException {
      if (this.bin.readByte() != 113) {
         throw new InternalError();
      } else {
         this.passHandle = this.bin.readInt() - 8257536;
         if (this.passHandle >= 0 && this.passHandle < this.handles.size()) {
            if (var1) {
               throw new InvalidObjectException("cannot read back reference as unshared");
            } else {
               Object var2 = this.handles.lookupObject(this.passHandle);
               if (var2 == unsharedMarker) {
                  throw new InvalidObjectException("cannot read back reference to unshared object");
               } else {
                  this.filterCheck((Class)null, -1);
                  return var2;
               }
            }
         } else {
            throw new StreamCorruptedException(String.format("invalid handle value: %08X", this.passHandle + 8257536));
         }
      }
   }

   private Class<?> readClass(boolean var1) throws IOException {
      if (this.bin.readByte() != 118) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = this.readClassDesc(false);
         Class var3 = var2.forClass();
         this.passHandle = this.handles.assign(var1 ? unsharedMarker : var3);
         ClassNotFoundException var4 = var2.getResolveException();
         if (var4 != null) {
            this.handles.markException(this.passHandle, var4);
         }

         this.handles.finish(this.passHandle);
         return var3;
      }
   }

   private ObjectStreamClass readClassDesc(boolean var1) throws IOException {
      byte var2 = this.bin.peekByte();
      ObjectStreamClass var3;
      switch(var2) {
      case 112:
         var3 = (ObjectStreamClass)this.readNull();
         break;
      case 113:
         var3 = (ObjectStreamClass)this.readHandle(var1);
         break;
      case 114:
         var3 = this.readNonProxyDesc(var1);
         break;
      case 125:
         var3 = this.readProxyDesc(var1);
         break;
      default:
         throw new StreamCorruptedException(String.format("invalid type code: %02X", var2));
      }

      if (var3 != null) {
         this.validateDescriptor(var3);
      }

      return var3;
   }

   private boolean isCustomSubclass() {
      return this.getClass().getClassLoader() != ObjectInputStream.class.getClassLoader();
   }

   private ObjectStreamClass readProxyDesc(boolean var1) throws IOException {
      if (this.bin.readByte() != 125) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = new ObjectStreamClass();
         int var3 = this.handles.assign(var1 ? unsharedMarker : var2);
         this.passHandle = -1;
         int var4 = this.bin.readInt();
         if (var4 > 65535) {
            throw new InvalidObjectException("interface limit exceeded: " + var4);
         } else {
            String[] var5 = new String[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = this.bin.readUTF();
            }

            Class var17 = null;
            ClassNotFoundException var7 = null;
            this.bin.setBlockDataMode(true);

            try {
               if ((var17 = this.resolveProxyClass(var5)) == null) {
                  var7 = new ClassNotFoundException("null class");
               } else {
                  if (!Proxy.isProxyClass(var17)) {
                     throw new InvalidClassException("Not a proxy");
                  }

                  ReflectUtil.checkProxyPackageAccess(this.getClass().getClassLoader(), var17.getInterfaces());
                  Class[] var8 = var17.getInterfaces();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     Class var11 = var8[var10];
                     this.filterCheck(var11, -1);
                  }
               }
            } catch (ClassNotFoundException var16) {
               var7 = var16;
            }

            this.filterCheck(var17, -1);
            this.skipCustomData();

            try {
               ++this.totalObjectRefs;
               ++this.depth;
               var2.initProxy(var17, var7, this.readClassDesc(false));
            } finally {
               --this.depth;
            }

            this.handles.finish(var3);
            this.passHandle = var3;
            return var2;
         }
      }
   }

   private ObjectStreamClass readNonProxyDesc(boolean var1) throws IOException {
      if (this.bin.readByte() != 114) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = new ObjectStreamClass();
         int var3 = this.handles.assign(var1 ? unsharedMarker : var2);
         this.passHandle = -1;
         ObjectStreamClass var4 = null;

         try {
            var4 = this.readClassDescriptor();
         } catch (ClassNotFoundException var15) {
            throw (IOException)(new InvalidClassException("failed to read class descriptor")).initCause(var15);
         }

         Class var5 = null;
         ClassNotFoundException var6 = null;
         this.bin.setBlockDataMode(true);
         boolean var7 = this.isCustomSubclass();

         try {
            if ((var5 = this.resolveClass(var4)) == null) {
               var6 = new ClassNotFoundException("null class");
            } else if (var7) {
               ReflectUtil.checkPackageAccess(var5);
            }
         } catch (ClassNotFoundException var14) {
            var6 = var14;
         }

         this.filterCheck(var5, -1);
         this.skipCustomData();

         try {
            ++this.totalObjectRefs;
            ++this.depth;
            var2.initNonProxy(var4, var5, var6, this.readClassDesc(false));
         } finally {
            --this.depth;
         }

         this.handles.finish(var3);
         this.passHandle = var3;
         return var2;
      }
   }

   private String readString(boolean var1) throws IOException {
      byte var3 = this.bin.readByte();
      String var2;
      switch(var3) {
      case 116:
         var2 = this.bin.readUTF();
         break;
      case 124:
         var2 = this.bin.readLongUTF();
         break;
      default:
         throw new StreamCorruptedException(String.format("invalid type code: %02X", var3));
      }

      this.passHandle = this.handles.assign(var1 ? unsharedMarker : var2);
      this.handles.finish(this.passHandle);
      return var2;
   }

   private Object readArray(boolean var1) throws IOException {
      if (this.bin.readByte() != 117) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = this.readClassDesc(false);
         int var3 = this.bin.readInt();
         this.filterCheck(var2.forClass(), var3);
         Object var4 = null;
         Class var6 = null;
         Class var5;
         if ((var5 = var2.forClass()) != null) {
            var6 = var5.getComponentType();
            var4 = Array.newInstance(var6, var3);
         }

         int var7 = this.handles.assign(var1 ? unsharedMarker : var4);
         ClassNotFoundException var8 = var2.getResolveException();
         if (var8 != null) {
            this.handles.markException(var7, var8);
         }

         if (var6 == null) {
            for(int var9 = 0; var9 < var3; ++var9) {
               this.readObject0(false);
            }
         } else if (var6.isPrimitive()) {
            if (var6 == Integer.TYPE) {
               this.bin.readInts((int[])((int[])var4), 0, var3);
            } else if (var6 == Byte.TYPE) {
               this.bin.readFully((byte[])((byte[])var4), 0, var3, true);
            } else if (var6 == Long.TYPE) {
               this.bin.readLongs((long[])((long[])var4), 0, var3);
            } else if (var6 == Float.TYPE) {
               this.bin.readFloats((float[])((float[])var4), 0, var3);
            } else if (var6 == Double.TYPE) {
               this.bin.readDoubles((double[])((double[])var4), 0, var3);
            } else if (var6 == Short.TYPE) {
               this.bin.readShorts((short[])((short[])var4), 0, var3);
            } else if (var6 == Character.TYPE) {
               this.bin.readChars((char[])((char[])var4), 0, var3);
            } else {
               if (var6 != Boolean.TYPE) {
                  throw new InternalError();
               }

               this.bin.readBooleans((boolean[])((boolean[])var4), 0, var3);
            }
         } else {
            Object[] var11 = (Object[])((Object[])var4);

            for(int var10 = 0; var10 < var3; ++var10) {
               var11[var10] = this.readObject0(false);
               this.handles.markDependency(var7, this.passHandle);
            }
         }

         this.handles.finish(var7);
         this.passHandle = var7;
         return var4;
      }
   }

   private Enum<?> readEnum(boolean var1) throws IOException {
      if (this.bin.readByte() != 126) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = this.readClassDesc(false);
         if (!var2.isEnum()) {
            throw new InvalidClassException("non-enum class: " + var2);
         } else {
            int var3 = this.handles.assign(var1 ? unsharedMarker : null);
            ClassNotFoundException var4 = var2.getResolveException();
            if (var4 != null) {
               this.handles.markException(var3, var4);
            }

            String var5 = this.readString(false);
            Enum var6 = null;
            Class var7 = var2.forClass();
            if (var7 != null) {
               try {
                  Enum var8 = Enum.valueOf(var7, var5);
                  var6 = var8;
               } catch (IllegalArgumentException var9) {
                  throw (IOException)(new InvalidObjectException("enum constant " + var5 + " does not exist in " + var7)).initCause(var9);
               }

               if (!var1) {
                  this.handles.setObject(var3, var6);
               }
            }

            this.handles.finish(var3);
            this.passHandle = var3;
            return var6;
         }
      }
   }

   private Object readOrdinaryObject(boolean var1) throws IOException {
      if (this.bin.readByte() != 115) {
         throw new InternalError();
      } else {
         ObjectStreamClass var2 = this.readClassDesc(false);
         var2.checkDeserialize();
         Class var3 = var2.forClass();
         if (var3 != String.class && var3 != Class.class && var3 != ObjectStreamClass.class) {
            Object var4;
            try {
               var4 = var2.isInstantiable() ? var2.newInstance() : null;
            } catch (Exception var7) {
               throw (IOException)(new InvalidClassException(var2.forClass().getName(), "unable to create instance")).initCause(var7);
            }

            this.passHandle = this.handles.assign(var1 ? unsharedMarker : var4);
            ClassNotFoundException var5 = var2.getResolveException();
            if (var5 != null) {
               this.handles.markException(this.passHandle, var5);
            }

            if (var2.isExternalizable()) {
               this.readExternalData((Externalizable)var4, var2);
            } else {
               this.readSerialData(var4, var2);
            }

            this.handles.finish(this.passHandle);
            if (var4 != null && this.handles.lookupException(this.passHandle) == null && var2.hasReadResolveMethod()) {
               Object var6 = var2.invokeReadResolve(var4);
               if (var1 && var6.getClass().isArray()) {
                  var6 = cloneArray(var6);
               }

               if (var6 != var4) {
                  if (var6 != null) {
                     if (var6.getClass().isArray()) {
                        this.filterCheck(var6.getClass(), Array.getLength(var6));
                     } else {
                        this.filterCheck(var6.getClass(), -1);
                     }
                  }

                  var4 = var6;
                  this.handles.setObject(this.passHandle, var6);
               }
            }

            return var4;
         } else {
            throw new InvalidClassException("invalid class descriptor");
         }
      }
   }

   private void readExternalData(Externalizable var1, ObjectStreamClass var2) throws IOException {
      SerialCallbackContext var3 = this.curContext;
      if (var3 != null) {
         var3.check();
      }

      this.curContext = null;

      try {
         boolean var4 = var2.hasBlockExternalData();
         if (var4) {
            this.bin.setBlockDataMode(true);
         }

         if (var1 != null) {
            try {
               var1.readExternal(this);
            } catch (ClassNotFoundException var9) {
               this.handles.markException(this.passHandle, var9);
            }
         }

         if (var4) {
            this.skipCustomData();
         }
      } finally {
         if (var3 != null) {
            var3.check();
         }

         this.curContext = var3;
      }

   }

   private void readSerialData(Object var1, ObjectStreamClass var2) throws IOException {
      ObjectStreamClass.ClassDataSlot[] var3 = var2.getClassDataLayout();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ObjectStreamClass var5 = var3[var4].desc;
         if (!var3[var4].hasData) {
            if (var1 != null && var5.hasReadObjectNoDataMethod() && this.handles.lookupException(this.passHandle) == null) {
               var5.invokeReadObjectNoData(var1);
            }
         } else {
            if (var1 != null && this.handles.lookupException(this.passHandle) == null) {
               if (var5.hasReadObjectMethod()) {
                  ThreadDeath var6 = null;
                  boolean var7 = false;
                  SerialCallbackContext var8 = this.curContext;
                  if (var8 != null) {
                     var8.check();
                  }

                  try {
                     this.curContext = new SerialCallbackContext(var1, var5);
                     this.bin.setBlockDataMode(true);
                     var5.invokeReadObject(var1, this);
                  } catch (ClassNotFoundException var18) {
                     this.handles.markException(this.passHandle, var18);
                  } finally {
                     do {
                        try {
                           this.curContext.setUsed();
                           if (var8 != null) {
                              var8.check();
                           }

                           this.curContext = var8;
                           var7 = true;
                        } catch (ThreadDeath var17) {
                           var6 = var17;
                        }
                     } while(!var7);

                     if (var6 != null) {
                        throw var6;
                     }

                  }

                  this.defaultDataEnd = false;
               } else {
                  this.defaultReadFields(var1, var5);
               }
            } else {
               this.defaultReadFields((Object)null, var5);
            }

            if (var5.hasWriteObjectData()) {
               this.skipCustomData();
            } else {
               this.bin.setBlockDataMode(false);
            }
         }
      }

   }

   private void skipCustomData() throws IOException {
      int var1 = this.passHandle;

      while(true) {
         if (this.bin.getBlockDataMode()) {
            this.bin.skipBlockData();
            this.bin.setBlockDataMode(false);
         }

         switch(this.bin.peekByte()) {
         case 119:
         case 122:
            this.bin.setBlockDataMode(true);
            break;
         case 120:
            this.bin.readByte();
            this.passHandle = var1;
            return;
         case 121:
         default:
            this.readObject0(false);
         }
      }
   }

   private void defaultReadFields(Object var1, ObjectStreamClass var2) throws IOException {
      Class var3 = var2.forClass();
      if (var3 != null && var1 != null && !var3.isInstance(var1)) {
         throw new ClassCastException();
      } else {
         int var4 = var2.getPrimDataSize();
         if (this.primVals == null || this.primVals.length < var4) {
            this.primVals = new byte[var4];
         }

         this.bin.readFully(this.primVals, 0, var4, false);
         if (var1 != null) {
            var2.setPrimFieldValues(var1, this.primVals);
         }

         int var5 = this.passHandle;
         ObjectStreamField[] var6 = var2.getFields(false);
         Object[] var7 = new Object[var2.getNumObjFields()];
         int var8 = var6.length - var7.length;

         for(int var9 = 0; var9 < var7.length; ++var9) {
            ObjectStreamField var10 = var6[var8 + var9];
            var7[var9] = this.readObject0(var10.isUnshared());
            if (var10.getField() != null) {
               this.handles.markDependency(var5, this.passHandle);
            }
         }

         if (var1 != null) {
            var2.setObjFieldValues(var1, var7);
         }

         this.passHandle = var5;
      }
   }

   private IOException readFatalException() throws IOException {
      if (this.bin.readByte() != 123) {
         throw new InternalError();
      } else {
         this.clear();
         return (IOException)this.readObject0(false);
      }
   }

   private void handleReset() throws StreamCorruptedException {
      if (this.depth > 0L) {
         throw new StreamCorruptedException("unexpected reset; recursion depth: " + this.depth);
      } else {
         this.clear();
      }
   }

   private static native void bytesToFloats(byte[] var0, int var1, float[] var2, int var3, int var4);

   private static native void bytesToDoubles(byte[] var0, int var1, double[] var2, int var3, int var4);

   private static ClassLoader latestUserDefinedLoader() {
      return VM.latestUserDefinedLoader();
   }

   private static Object cloneArray(Object var0) {
      if (var0 instanceof Object[]) {
         return ((Object[])((Object[])var0)).clone();
      } else if (var0 instanceof boolean[]) {
         return ((boolean[])((boolean[])var0)).clone();
      } else if (var0 instanceof byte[]) {
         return ((byte[])((byte[])var0)).clone();
      } else if (var0 instanceof char[]) {
         return ((char[])((char[])var0)).clone();
      } else if (var0 instanceof double[]) {
         return ((double[])((double[])var0)).clone();
      } else if (var0 instanceof float[]) {
         return ((float[])((float[])var0)).clone();
      } else if (var0 instanceof int[]) {
         return ((int[])((int[])var0)).clone();
      } else if (var0 instanceof long[]) {
         return ((long[])((long[])var0)).clone();
      } else if (var0 instanceof short[]) {
         return ((short[])((short[])var0)).clone();
      } else {
         throw new AssertionError();
      }
   }

   private void validateDescriptor(ObjectStreamClass var1) {
      ObjectStreamClassValidator var2 = this.validator;
      if (var2 != null) {
         var2.validateDescriptor(var1);
      }

   }

   private static void setValidator(ObjectInputStream var0, ObjectStreamClassValidator var1) {
      var0.validator = var1;
   }

   static {
      primClasses.put("boolean", Boolean.TYPE);
      primClasses.put("byte", Byte.TYPE);
      primClasses.put("char", Character.TYPE);
      primClasses.put("short", Short.TYPE);
      primClasses.put("int", Integer.TYPE);
      primClasses.put("long", Long.TYPE);
      primClasses.put("float", Float.TYPE);
      primClasses.put("double", Double.TYPE);
      primClasses.put("void", Void.TYPE);
      JavaOISAccess var0 = new JavaOISAccess() {
         public void setObjectInputFilter(ObjectInputStream var1, ObjectInputFilter var2) {
            var1.setInternalObjectInputFilter(var2);
         }

         public ObjectInputFilter getObjectInputFilter(ObjectInputStream var1) {
            return var1.getInternalObjectInputFilter();
         }

         public void checkArray(ObjectInputStream var1, Class<?> var2, int var3) throws InvalidClassException {
            var1.checkArray(var2, var3);
         }
      };
      SharedSecrets.setJavaOISAccess(var0);
      SharedSecrets.setJavaObjectInputStreamAccess(ObjectInputStream::setValidator);
   }

   private static class HandleTable {
      private static final byte STATUS_OK = 1;
      private static final byte STATUS_UNKNOWN = 2;
      private static final byte STATUS_EXCEPTION = 3;
      byte[] status;
      Object[] entries;
      ObjectInputStream.HandleTable.HandleList[] deps;
      int lowDep = -1;
      int size = 0;

      HandleTable(int var1) {
         this.status = new byte[var1];
         this.entries = new Object[var1];
         this.deps = new ObjectInputStream.HandleTable.HandleList[var1];
      }

      int assign(Object var1) {
         if (this.size >= this.entries.length) {
            this.grow();
         }

         this.status[this.size] = 2;
         this.entries[this.size] = var1;
         return this.size++;
      }

      void markDependency(int var1, int var2) {
         if (var1 != -1 && var2 != -1) {
            switch(this.status[var1]) {
            case 2:
               switch(this.status[var2]) {
               case 1:
                  break;
               case 2:
                  if (this.deps[var2] == null) {
                     this.deps[var2] = new ObjectInputStream.HandleTable.HandleList();
                  }

                  this.deps[var2].add(var1);
                  if (this.lowDep < 0 || this.lowDep > var2) {
                     this.lowDep = var2;
                  }
                  break;
               case 3:
                  this.markException(var1, (ClassNotFoundException)this.entries[var2]);
                  break;
               default:
                  throw new InternalError();
               }
            case 3:
               return;
            default:
               throw new InternalError();
            }
         }
      }

      void markException(int var1, ClassNotFoundException var2) {
         switch(this.status[var1]) {
         case 2:
            this.status[var1] = 3;
            this.entries[var1] = var2;
            ObjectInputStream.HandleTable.HandleList var3 = this.deps[var1];
            if (var3 != null) {
               int var4 = var3.size();

               for(int var5 = 0; var5 < var4; ++var5) {
                  this.markException(var3.get(var5), var2);
               }

               this.deps[var1] = null;
            }
         case 3:
            return;
         default:
            throw new InternalError();
         }
      }

      void finish(int var1) {
         int var2;
         if (this.lowDep < 0) {
            var2 = var1 + 1;
         } else {
            if (this.lowDep < var1) {
               return;
            }

            var2 = this.size;
            this.lowDep = -1;
         }

         int var3 = var1;

         while(var3 < var2) {
            switch(this.status[var3]) {
            case 2:
               this.status[var3] = 1;
               this.deps[var3] = null;
            case 1:
            case 3:
               ++var3;
               break;
            default:
               throw new InternalError();
            }
         }

      }

      void setObject(int var1, Object var2) {
         switch(this.status[var1]) {
         case 1:
         case 2:
            this.entries[var1] = var2;
         case 3:
            return;
         default:
            throw new InternalError();
         }
      }

      Object lookupObject(int var1) {
         return var1 != -1 && this.status[var1] != 3 ? this.entries[var1] : null;
      }

      ClassNotFoundException lookupException(int var1) {
         return var1 != -1 && this.status[var1] == 3 ? (ClassNotFoundException)this.entries[var1] : null;
      }

      void clear() {
         Arrays.fill((byte[])this.status, 0, this.size, (byte)0);
         Arrays.fill(this.entries, 0, this.size, (Object)null);
         Arrays.fill(this.deps, 0, this.size, (Object)null);
         this.lowDep = -1;
         this.size = 0;
      }

      int size() {
         return this.size;
      }

      private void grow() {
         int var1 = (this.entries.length << 1) + 1;
         byte[] var2 = new byte[var1];
         Object[] var3 = new Object[var1];
         ObjectInputStream.HandleTable.HandleList[] var4 = new ObjectInputStream.HandleTable.HandleList[var1];
         System.arraycopy(this.status, 0, var2, 0, this.size);
         System.arraycopy(this.entries, 0, var3, 0, this.size);
         System.arraycopy(this.deps, 0, var4, 0, this.size);
         this.status = var2;
         this.entries = var3;
         this.deps = var4;
      }

      private static class HandleList {
         private int[] list = new int[4];
         private int size = 0;

         public HandleList() {
         }

         public void add(int var1) {
            if (this.size >= this.list.length) {
               int[] var2 = new int[this.list.length << 1];
               System.arraycopy(this.list, 0, var2, 0, this.list.length);
               this.list = var2;
            }

            this.list[this.size++] = var1;
         }

         public int get(int var1) {
            if (var1 >= this.size) {
               throw new ArrayIndexOutOfBoundsException();
            } else {
               return this.list[var1];
            }
         }

         public int size() {
            return this.size;
         }
      }
   }

   private class BlockDataInputStream extends InputStream implements DataInput {
      private static final int MAX_BLOCK_SIZE = 1024;
      private static final int MAX_HEADER_SIZE = 5;
      private static final int CHAR_BUF_SIZE = 256;
      private static final int HEADER_BLOCKED = -2;
      private final byte[] buf = new byte[1024];
      private final byte[] hbuf = new byte[5];
      private final char[] cbuf = new char[256];
      private boolean blkmode = false;
      private int pos = 0;
      private int end = -1;
      private int unread = 0;
      private final ObjectInputStream.PeekInputStream in;
      private final DataInputStream din;

      BlockDataInputStream(InputStream var2) {
         this.in = new ObjectInputStream.PeekInputStream(var2);
         this.din = new DataInputStream(this);
      }

      boolean setBlockDataMode(boolean var1) throws IOException {
         if (this.blkmode == var1) {
            return this.blkmode;
         } else {
            if (var1) {
               this.pos = 0;
               this.end = 0;
               this.unread = 0;
            } else if (this.pos < this.end) {
               throw new IllegalStateException("unread block data");
            }

            this.blkmode = var1;
            return !this.blkmode;
         }
      }

      boolean getBlockDataMode() {
         return this.blkmode;
      }

      void skipBlockData() throws IOException {
         if (!this.blkmode) {
            throw new IllegalStateException("not in block data mode");
         } else {
            while(this.end >= 0) {
               this.refill();
            }

         }
      }

      private int readBlockHeader(boolean var1) throws IOException {
         if (ObjectInputStream.this.defaultDataEnd) {
            return -1;
         } else {
            try {
               while(true) {
                  int var2 = var1 ? Integer.MAX_VALUE : this.in.available();
                  if (var2 == 0) {
                     return -2;
                  }

                  int var3 = this.in.peek();
                  switch(var3) {
                  case 119:
                     if (var2 < 2) {
                        return -2;
                     }

                     this.in.readFully(this.hbuf, 0, 2);
                     return this.hbuf[1] & 255;
                  case 120:
                  default:
                     if (var3 < 0 || var3 >= 112 && var3 <= 126) {
                        return -1;
                     }

                     throw new StreamCorruptedException(String.format("invalid type code: %02X", var3));
                  case 121:
                     this.in.read();
                     ObjectInputStream.this.handleReset();
                     break;
                  case 122:
                     if (var2 < 5) {
                        return -2;
                     }

                     this.in.readFully(this.hbuf, 0, 5);
                     int var4 = Bits.getInt(this.hbuf, 1);
                     if (var4 < 0) {
                        throw new StreamCorruptedException("illegal block data header length: " + var4);
                     }

                     return var4;
                  }
               }
            } catch (EOFException var5) {
               throw new StreamCorruptedException("unexpected EOF while reading block data header");
            }
         }
      }

      private void refill() throws IOException {
         try {
            do {
               this.pos = 0;
               int var1;
               if (this.unread > 0) {
                  var1 = this.in.read(this.buf, 0, Math.min(this.unread, 1024));
                  if (var1 < 0) {
                     throw new StreamCorruptedException("unexpected EOF in middle of data block");
                  }

                  this.end = var1;
                  this.unread -= var1;
               } else {
                  var1 = this.readBlockHeader(true);
                  if (var1 >= 0) {
                     this.end = 0;
                     this.unread = var1;
                  } else {
                     this.end = -1;
                     this.unread = 0;
                  }
               }
            } while(this.pos == this.end);

         } catch (IOException var2) {
            this.pos = 0;
            this.end = -1;
            this.unread = 0;
            throw var2;
         }
      }

      int currentBlockRemaining() {
         if (this.blkmode) {
            return this.end >= 0 ? this.end - this.pos + this.unread : 0;
         } else {
            throw new IllegalStateException();
         }
      }

      int peek() throws IOException {
         if (this.blkmode) {
            if (this.pos == this.end) {
               this.refill();
            }

            return this.end >= 0 ? this.buf[this.pos] & 255 : -1;
         } else {
            return this.in.peek();
         }
      }

      byte peekByte() throws IOException {
         int var1 = this.peek();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            return (byte)var1;
         }
      }

      public int read() throws IOException {
         if (this.blkmode) {
            if (this.pos == this.end) {
               this.refill();
            }

            return this.end >= 0 ? this.buf[this.pos++] & 255 : -1;
         } else {
            return this.in.read();
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         return this.read(var1, var2, var3, false);
      }

      public long skip(long var1) throws IOException {
         long var3 = var1;

         while(var3 > 0L) {
            int var5;
            if (this.blkmode) {
               if (this.pos == this.end) {
                  this.refill();
               }

               if (this.end < 0) {
                  break;
               }

               var5 = (int)Math.min(var3, (long)(this.end - this.pos));
               var3 -= (long)var5;
               this.pos += var5;
            } else {
               var5 = (int)Math.min(var3, 1024L);
               if ((var5 = this.in.read(this.buf, 0, var5)) < 0) {
                  break;
               }

               var3 -= (long)var5;
            }
         }

         return var1 - var3;
      }

      public int available() throws IOException {
         if (!this.blkmode) {
            return this.in.available();
         } else {
            int var1;
            if (this.pos == this.end && this.unread == 0) {
               label31:
               while(true) {
                  if ((var1 = this.readBlockHeader(false)) != 0) {
                     switch(var1) {
                     case -2:
                        break label31;
                     case -1:
                        this.pos = 0;
                        this.end = -1;
                        break label31;
                     default:
                        this.pos = 0;
                        this.end = 0;
                        this.unread = var1;
                        break label31;
                     }
                  }
               }
            }

            var1 = this.unread > 0 ? Math.min(this.in.available(), this.unread) : 0;
            return this.end >= 0 ? this.end - this.pos + var1 : 0;
         }
      }

      public void close() throws IOException {
         if (this.blkmode) {
            this.pos = 0;
            this.end = -1;
            this.unread = 0;
         }

         this.in.close();
      }

      int read(byte[] var1, int var2, int var3, boolean var4) throws IOException {
         if (var3 == 0) {
            return 0;
         } else {
            int var5;
            if (this.blkmode) {
               if (this.pos == this.end) {
                  this.refill();
               }

               if (this.end < 0) {
                  return -1;
               } else {
                  var5 = Math.min(var3, this.end - this.pos);
                  System.arraycopy(this.buf, this.pos, var1, var2, var5);
                  this.pos += var5;
                  return var5;
               }
            } else if (var4) {
               var5 = this.in.read(this.buf, 0, Math.min(var3, 1024));
               if (var5 > 0) {
                  System.arraycopy(this.buf, 0, var1, var2, var5);
               }

               return var5;
            } else {
               return this.in.read(var1, var2, var3);
            }
         }
      }

      public void readFully(byte[] var1) throws IOException {
         this.readFully(var1, 0, var1.length, false);
      }

      public void readFully(byte[] var1, int var2, int var3) throws IOException {
         this.readFully(var1, var2, var3, false);
      }

      public void readFully(byte[] var1, int var2, int var3, boolean var4) throws IOException {
         while(var3 > 0) {
            int var5 = this.read(var1, var2, var3, var4);
            if (var5 < 0) {
               throw new EOFException();
            }

            var2 += var5;
            var3 -= var5;
         }

      }

      public int skipBytes(int var1) throws IOException {
         return this.din.skipBytes(var1);
      }

      public boolean readBoolean() throws IOException {
         int var1 = this.read();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            return var1 != 0;
         }
      }

      public byte readByte() throws IOException {
         int var1 = this.read();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            return (byte)var1;
         }
      }

      public int readUnsignedByte() throws IOException {
         int var1 = this.read();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            return var1;
         }
      }

      public char readChar() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 2);
         } else if (this.end - this.pos < 2) {
            return this.din.readChar();
         }

         char var1 = Bits.getChar(this.buf, this.pos);
         this.pos += 2;
         return var1;
      }

      public short readShort() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 2);
         } else if (this.end - this.pos < 2) {
            return this.din.readShort();
         }

         short var1 = Bits.getShort(this.buf, this.pos);
         this.pos += 2;
         return var1;
      }

      public int readUnsignedShort() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 2);
         } else if (this.end - this.pos < 2) {
            return this.din.readUnsignedShort();
         }

         int var1 = Bits.getShort(this.buf, this.pos) & '\uffff';
         this.pos += 2;
         return var1;
      }

      public int readInt() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 4);
         } else if (this.end - this.pos < 4) {
            return this.din.readInt();
         }

         int var1 = Bits.getInt(this.buf, this.pos);
         this.pos += 4;
         return var1;
      }

      public float readFloat() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 4);
         } else if (this.end - this.pos < 4) {
            return this.din.readFloat();
         }

         float var1 = Bits.getFloat(this.buf, this.pos);
         this.pos += 4;
         return var1;
      }

      public long readLong() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 8);
         } else if (this.end - this.pos < 8) {
            return this.din.readLong();
         }

         long var1 = Bits.getLong(this.buf, this.pos);
         this.pos += 8;
         return var1;
      }

      public double readDouble() throws IOException {
         if (!this.blkmode) {
            this.pos = 0;
            this.in.readFully(this.buf, 0, 8);
         } else if (this.end - this.pos < 8) {
            return this.din.readDouble();
         }

         double var1 = Bits.getDouble(this.buf, this.pos);
         this.pos += 8;
         return var1;
      }

      public String readUTF() throws IOException {
         return this.readUTFBody((long)this.readUnsignedShort());
      }

      public String readLine() throws IOException {
         return this.din.readLine();
      }

      void readBooleans(boolean[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  int var6 = Math.min(var5 - var2, 1024);
                  this.in.readFully(this.buf, 0, var6);
                  var4 = var2 + var6;
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 1) {
                     var1[var2++] = this.din.readBoolean();
                     continue;
                  }

                  var4 = Math.min(var5, var2 + this.end - this.pos);
               }

               while(var2 < var4) {
                  var1[var2++] = Bits.getBoolean(this.buf, this.pos++);
               }
            }

            return;
         }
      }

      void readChars(char[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  int var6 = Math.min(var5 - var2, 512);
                  this.in.readFully(this.buf, 0, var6 << 1);
                  var4 = var2 + var6;
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 2) {
                     var1[var2++] = this.din.readChar();
                     continue;
                  }

                  var4 = Math.min(var5, var2 + (this.end - this.pos >> 1));
               }

               while(var2 < var4) {
                  var1[var2++] = Bits.getChar(this.buf, this.pos);
                  this.pos += 2;
               }
            }

            return;
         }
      }

      void readShorts(short[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  int var6 = Math.min(var5 - var2, 512);
                  this.in.readFully(this.buf, 0, var6 << 1);
                  var4 = var2 + var6;
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 2) {
                     var1[var2++] = this.din.readShort();
                     continue;
                  }

                  var4 = Math.min(var5, var2 + (this.end - this.pos >> 1));
               }

               while(var2 < var4) {
                  var1[var2++] = Bits.getShort(this.buf, this.pos);
                  this.pos += 2;
               }
            }

            return;
         }
      }

      void readInts(int[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  int var6 = Math.min(var5 - var2, 256);
                  this.in.readFully(this.buf, 0, var6 << 2);
                  var4 = var2 + var6;
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 4) {
                     var1[var2++] = this.din.readInt();
                     continue;
                  }

                  var4 = Math.min(var5, var2 + (this.end - this.pos >> 2));
               }

               while(var2 < var4) {
                  var1[var2++] = Bits.getInt(this.buf, this.pos);
                  this.pos += 4;
               }
            }

            return;
         }
      }

      void readFloats(float[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  var4 = Math.min(var5 - var2, 256);
                  this.in.readFully(this.buf, 0, var4 << 2);
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 4) {
                     var1[var2++] = this.din.readFloat();
                     continue;
                  }

                  var4 = Math.min(var5 - var2, this.end - this.pos >> 2);
               }

               ObjectInputStream.bytesToFloats(this.buf, this.pos, var1, var2, var4);
               var2 += var4;
               this.pos += var4 << 2;
            }

            return;
         }
      }

      void readLongs(long[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  int var6 = Math.min(var5 - var2, 128);
                  this.in.readFully(this.buf, 0, var6 << 3);
                  var4 = var2 + var6;
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 8) {
                     var1[var2++] = this.din.readLong();
                     continue;
                  }

                  var4 = Math.min(var5, var2 + (this.end - this.pos >> 3));
               }

               while(var2 < var4) {
                  var1[var2++] = Bits.getLong(this.buf, this.pos);
                  this.pos += 8;
               }
            }

            return;
         }
      }

      void readDoubles(double[] var1, int var2, int var3) throws IOException {
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               int var4;
               if (!this.blkmode) {
                  var4 = Math.min(var5 - var2, 128);
                  this.in.readFully(this.buf, 0, var4 << 3);
                  this.pos = 0;
               } else {
                  if (this.end - this.pos < 8) {
                     var1[var2++] = this.din.readDouble();
                     continue;
                  }

                  var4 = Math.min(var5 - var2, this.end - this.pos >> 3);
               }

               ObjectInputStream.bytesToDoubles(this.buf, this.pos, var1, var2, var4);
               var2 += var4;
               this.pos += var4 << 3;
            }

            return;
         }
      }

      String readLongUTF() throws IOException {
         return this.readUTFBody(this.readLong());
      }

      private String readUTFBody(long var1) throws IOException {
         StringBuilder var3 = new StringBuilder();
         if (!this.blkmode) {
            this.end = this.pos = 0;
         }

         while(true) {
            while(var1 > 0L) {
               int var4 = this.end - this.pos;
               if (var4 < 3 && (long)var4 != var1) {
                  if (this.blkmode) {
                     var1 -= (long)this.readUTFChar(var3, var1);
                  } else {
                     if (var4 > 0) {
                        System.arraycopy(this.buf, this.pos, this.buf, 0, var4);
                     }

                     this.pos = 0;
                     this.end = (int)Math.min(1024L, var1);
                     this.in.readFully(this.buf, var4, this.end - var4);
                  }
               } else {
                  var1 -= this.readUTFSpan(var3, var1);
               }
            }

            return var3.toString();
         }
      }

      private long readUTFSpan(StringBuilder var1, long var2) throws IOException {
         int var4 = 0;
         int var5 = this.pos;
         int var6 = Math.min(this.end - this.pos, 256);
         int var7 = this.pos + (var2 > (long)var6 ? var6 - 2 : (int)var2);
         boolean var8 = false;

         while(true) {
            try {
               if (this.pos < var7) {
                  int var9 = this.buf[this.pos++] & 255;
                  byte var10;
                  switch(var9 >> 4) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                     this.cbuf[var4++] = (char)var9;
                     continue;
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  default:
                     throw new UTFDataFormatException();
                  case 12:
                  case 13:
                     var10 = this.buf[this.pos++];
                     if ((var10 & 192) != 128) {
                        throw new UTFDataFormatException();
                     }

                     this.cbuf[var4++] = (char)((var9 & 31) << 6 | (var10 & 63) << 0);
                     continue;
                  case 14:
                     byte var11 = this.buf[this.pos + 1];
                     var10 = this.buf[this.pos + 0];
                     this.pos += 2;
                     if ((var10 & 192) == 128 && (var11 & 192) == 128) {
                        this.cbuf[var4++] = (char)((var9 & 15) << 12 | (var10 & 63) << 6 | (var11 & 63) << 0);
                        continue;
                     }

                     throw new UTFDataFormatException();
                  }
               }
            } catch (ArrayIndexOutOfBoundsException var15) {
               var8 = true;
            } finally {
               if (var8 || (long)(this.pos - var5) > var2) {
                  this.pos = var5 + (int)var2;
                  throw new UTFDataFormatException();
               }

            }

            var1.append((char[])this.cbuf, 0, var4);
            return (long)(this.pos - var5);
         }
      }

      private int readUTFChar(StringBuilder var1, long var2) throws IOException {
         int var4 = this.readByte() & 255;
         byte var5;
         switch(var4 >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            var1.append((char)var4);
            return 1;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw new UTFDataFormatException();
         case 12:
         case 13:
            if (var2 < 2L) {
               throw new UTFDataFormatException();
            } else {
               var5 = this.readByte();
               if ((var5 & 192) != 128) {
                  throw new UTFDataFormatException();
               }

               var1.append((char)((var4 & 31) << 6 | (var5 & 63) << 0));
               return 2;
            }
         case 14:
            if (var2 < 3L) {
               if (var2 == 2L) {
                  this.readByte();
               }

               throw new UTFDataFormatException();
            } else {
               var5 = this.readByte();
               byte var6 = this.readByte();
               if ((var5 & 192) == 128 && (var6 & 192) == 128) {
                  var1.append((char)((var4 & 15) << 12 | (var5 & 63) << 6 | (var6 & 63) << 0));
                  return 3;
               } else {
                  throw new UTFDataFormatException();
               }
            }
         }
      }

      long getBytesRead() {
         return this.in.getBytesRead();
      }
   }

   private static class PeekInputStream extends InputStream {
      private final InputStream in;
      private int peekb = -1;
      private long totalBytesRead = 0L;

      PeekInputStream(InputStream var1) {
         this.in = var1;
      }

      int peek() throws IOException {
         if (this.peekb >= 0) {
            return this.peekb;
         } else {
            this.peekb = this.in.read();
            this.totalBytesRead += this.peekb >= 0 ? 1L : 0L;
            return this.peekb;
         }
      }

      public int read() throws IOException {
         int var1;
         if (this.peekb >= 0) {
            var1 = this.peekb;
            this.peekb = -1;
            return var1;
         } else {
            var1 = this.in.read();
            this.totalBytesRead += var1 >= 0 ? 1L : 0L;
            return var1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 == 0) {
            return 0;
         } else {
            int var4;
            if (this.peekb < 0) {
               var4 = this.in.read(var1, var2, var3);
               this.totalBytesRead += var4 >= 0 ? (long)var4 : 0L;
               return var4;
            } else {
               var1[var2++] = (byte)this.peekb;
               --var3;
               this.peekb = -1;
               var4 = this.in.read(var1, var2, var3);
               this.totalBytesRead += var4 >= 0 ? (long)var4 : 0L;
               return var4 >= 0 ? var4 + 1 : 1;
            }
         }
      }

      void readFully(byte[] var1, int var2, int var3) throws IOException {
         int var5;
         for(int var4 = 0; var4 < var3; var4 += var5) {
            var5 = this.read(var1, var2 + var4, var3 - var4);
            if (var5 < 0) {
               throw new EOFException();
            }
         }

      }

      public long skip(long var1) throws IOException {
         if (var1 <= 0L) {
            return 0L;
         } else {
            int var3 = 0;
            if (this.peekb >= 0) {
               this.peekb = -1;
               ++var3;
               --var1;
            }

            var1 = (long)var3 + this.in.skip(var1);
            this.totalBytesRead += var1;
            return var1;
         }
      }

      public int available() throws IOException {
         return this.in.available() + (this.peekb >= 0 ? 1 : 0);
      }

      public void close() throws IOException {
         this.in.close();
      }

      public long getBytesRead() {
         return this.totalBytesRead;
      }
   }

   static class FilterValues implements ObjectInputFilter.FilterInfo {
      final Class<?> clazz;
      final long arrayLength;
      final long totalObjectRefs;
      final long depth;
      final long streamBytes;

      public FilterValues(Class<?> var1, long var2, long var4, long var6, long var8) {
         this.clazz = var1;
         this.arrayLength = var2;
         this.totalObjectRefs = var4;
         this.depth = var6;
         this.streamBytes = var8;
      }

      public Class<?> serialClass() {
         return this.clazz;
      }

      public long arrayLength() {
         return this.arrayLength;
      }

      public long references() {
         return this.totalObjectRefs;
      }

      public long depth() {
         return this.depth;
      }

      public long streamBytes() {
         return this.streamBytes;
      }
   }

   private static class ValidationList {
      private ObjectInputStream.ValidationList.Callback list;

      ValidationList() {
      }

      void register(ObjectInputValidation var1, int var2) throws InvalidObjectException {
         if (var1 == null) {
            throw new InvalidObjectException("null callback");
         } else {
            ObjectInputStream.ValidationList.Callback var3 = null;

            ObjectInputStream.ValidationList.Callback var4;
            for(var4 = this.list; var4 != null && var2 < var4.priority; var4 = var4.next) {
               var3 = var4;
            }

            AccessControlContext var5 = AccessController.getContext();
            if (var3 != null) {
               var3.next = new ObjectInputStream.ValidationList.Callback(var1, var2, var4, var5);
            } else {
               this.list = new ObjectInputStream.ValidationList.Callback(var1, var2, this.list, var5);
            }

         }
      }

      void doCallbacks() throws InvalidObjectException {
         try {
            while(this.list != null) {
               AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() throws InvalidObjectException {
                     ValidationList.this.list.obj.validateObject();
                     return null;
                  }
               }, this.list.acc);
               this.list = this.list.next;
            }

         } catch (PrivilegedActionException var2) {
            this.list = null;
            throw (InvalidObjectException)var2.getException();
         }
      }

      public void clear() {
         this.list = null;
      }

      private static class Callback {
         final ObjectInputValidation obj;
         final int priority;
         ObjectInputStream.ValidationList.Callback next;
         final AccessControlContext acc;

         Callback(ObjectInputValidation var1, int var2, ObjectInputStream.ValidationList.Callback var3, AccessControlContext var4) {
            this.obj = var1;
            this.priority = var2;
            this.next = var3;
            this.acc = var4;
         }
      }
   }

   private class GetFieldImpl extends ObjectInputStream.GetField {
      private final ObjectStreamClass desc;
      private final byte[] primVals;
      private final Object[] objVals;
      private final int[] objHandles;

      GetFieldImpl(ObjectStreamClass var2) {
         this.desc = var2;
         this.primVals = new byte[var2.getPrimDataSize()];
         this.objVals = new Object[var2.getNumObjFields()];
         this.objHandles = new int[this.objVals.length];
      }

      public ObjectStreamClass getObjectStreamClass() {
         return this.desc;
      }

      public boolean defaulted(String var1) throws IOException {
         return this.getFieldOffset(var1, (Class)null) < 0;
      }

      public boolean get(String var1, boolean var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Boolean.TYPE);
         return var3 >= 0 ? Bits.getBoolean(this.primVals, var3) : var2;
      }

      public byte get(String var1, byte var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Byte.TYPE);
         return var3 >= 0 ? this.primVals[var3] : var2;
      }

      public char get(String var1, char var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Character.TYPE);
         return var3 >= 0 ? Bits.getChar(this.primVals, var3) : var2;
      }

      public short get(String var1, short var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Short.TYPE);
         return var3 >= 0 ? Bits.getShort(this.primVals, var3) : var2;
      }

      public int get(String var1, int var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Integer.TYPE);
         return var3 >= 0 ? Bits.getInt(this.primVals, var3) : var2;
      }

      public float get(String var1, float var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Float.TYPE);
         return var3 >= 0 ? Bits.getFloat(this.primVals, var3) : var2;
      }

      public long get(String var1, long var2) throws IOException {
         int var4 = this.getFieldOffset(var1, Long.TYPE);
         return var4 >= 0 ? Bits.getLong(this.primVals, var4) : var2;
      }

      public double get(String var1, double var2) throws IOException {
         int var4 = this.getFieldOffset(var1, Double.TYPE);
         return var4 >= 0 ? Bits.getDouble(this.primVals, var4) : var2;
      }

      public Object get(String var1, Object var2) throws IOException {
         int var3 = this.getFieldOffset(var1, Object.class);
         if (var3 >= 0) {
            int var4 = this.objHandles[var3];
            ObjectInputStream.this.handles.markDependency(ObjectInputStream.this.passHandle, var4);
            return ObjectInputStream.this.handles.lookupException(var4) == null ? this.objVals[var3] : null;
         } else {
            return var2;
         }
      }

      void readFields() throws IOException {
         ObjectInputStream.this.bin.readFully(this.primVals, 0, this.primVals.length, false);
         int var1 = ObjectInputStream.this.passHandle;
         ObjectStreamField[] var2 = this.desc.getFields(false);
         int var3 = var2.length - this.objVals.length;

         for(int var4 = 0; var4 < this.objVals.length; ++var4) {
            this.objVals[var4] = ObjectInputStream.this.readObject0(var2[var3 + var4].isUnshared());
            this.objHandles[var4] = ObjectInputStream.this.passHandle;
         }

         ObjectInputStream.this.passHandle = var1;
      }

      private int getFieldOffset(String var1, Class<?> var2) {
         ObjectStreamField var3 = this.desc.getField(var1, var2);
         if (var3 != null) {
            return var3.getOffset();
         } else if (this.desc.getLocalDesc().getField(var1, var2) != null) {
            return -1;
         } else {
            throw new IllegalArgumentException("no such field " + var1 + " with type " + var2);
         }
      }
   }

   public abstract static class GetField {
      public abstract ObjectStreamClass getObjectStreamClass();

      public abstract boolean defaulted(String var1) throws IOException;

      public abstract boolean get(String var1, boolean var2) throws IOException;

      public abstract byte get(String var1, byte var2) throws IOException;

      public abstract char get(String var1, char var2) throws IOException;

      public abstract short get(String var1, short var2) throws IOException;

      public abstract int get(String var1, int var2) throws IOException;

      public abstract long get(String var1, long var2) throws IOException;

      public abstract float get(String var1, float var2) throws IOException;

      public abstract double get(String var1, double var2) throws IOException;

      public abstract Object get(String var1, Object var2) throws IOException;
   }

   private static class Logging {
      private static final PlatformLogger traceLogger;
      private static final PlatformLogger infoLogger;

      static {
         PlatformLogger var0 = PlatformLogger.getLogger("java.io.serialization");
         infoLogger = var0 != null && var0.isLoggable(PlatformLogger.Level.INFO) ? var0 : null;
         traceLogger = var0 != null && var0.isLoggable(PlatformLogger.Level.FINER) ? var0 : null;
      }
   }

   private static class Caches {
      static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
      static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
   }
}
