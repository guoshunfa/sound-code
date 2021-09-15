package java.io;

import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetBooleanAction;

public class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants {
   private final ObjectOutputStream.BlockDataOutputStream bout;
   private final ObjectOutputStream.HandleTable handles;
   private final ObjectOutputStream.ReplaceTable subs;
   private int protocol = 2;
   private int depth;
   private byte[] primVals;
   private final boolean enableOverride;
   private boolean enableReplace;
   private SerialCallbackContext curContext;
   private ObjectOutputStream.PutFieldImpl curPut;
   private final ObjectOutputStream.DebugTraceInfoStack debugInfoStack;
   private static final boolean extendedDebugInfo = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.io.serialization.extendedDebugInfo")));

   public ObjectOutputStream(OutputStream var1) throws IOException {
      this.verifySubclass();
      this.bout = new ObjectOutputStream.BlockDataOutputStream(var1);
      this.handles = new ObjectOutputStream.HandleTable(10, 3.0F);
      this.subs = new ObjectOutputStream.ReplaceTable(10, 3.0F);
      this.enableOverride = false;
      this.writeStreamHeader();
      this.bout.setBlockDataMode(true);
      if (extendedDebugInfo) {
         this.debugInfoStack = new ObjectOutputStream.DebugTraceInfoStack();
      } else {
         this.debugInfoStack = null;
      }

   }

   protected ObjectOutputStream() throws IOException, SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
      }

      this.bout = null;
      this.handles = null;
      this.subs = null;
      this.enableOverride = true;
      this.debugInfoStack = null;
   }

   public void useProtocolVersion(int var1) throws IOException {
      if (this.handles.size() != 0) {
         throw new IllegalStateException("stream non-empty");
      } else {
         switch(var1) {
         case 1:
         case 2:
            this.protocol = var1;
            return;
         default:
            throw new IllegalArgumentException("unknown version: " + var1);
         }
      }
   }

   public final void writeObject(Object var1) throws IOException {
      if (this.enableOverride) {
         this.writeObjectOverride(var1);
      } else {
         try {
            this.writeObject0(var1, false);
         } catch (IOException var3) {
            if (this.depth == 0) {
               this.writeFatalException(var3);
            }

            throw var3;
         }
      }
   }

   protected void writeObjectOverride(Object var1) throws IOException {
   }

   public void writeUnshared(Object var1) throws IOException {
      try {
         this.writeObject0(var1, true);
      } catch (IOException var3) {
         if (this.depth == 0) {
            this.writeFatalException(var3);
         }

         throw var3;
      }
   }

   public void defaultWriteObject() throws IOException {
      SerialCallbackContext var1 = this.curContext;
      if (var1 == null) {
         throw new NotActiveException("not in call to writeObject");
      } else {
         Object var2 = var1.getObj();
         ObjectStreamClass var3 = var1.getDesc();
         this.bout.setBlockDataMode(false);
         this.defaultWriteFields(var2, var3);
         this.bout.setBlockDataMode(true);
      }
   }

   public ObjectOutputStream.PutField putFields() throws IOException {
      if (this.curPut == null) {
         SerialCallbackContext var1 = this.curContext;
         if (var1 == null) {
            throw new NotActiveException("not in call to writeObject");
         }

         Object var2 = var1.getObj();
         ObjectStreamClass var3 = var1.getDesc();
         this.curPut = new ObjectOutputStream.PutFieldImpl(var3);
      }

      return this.curPut;
   }

   public void writeFields() throws IOException {
      if (this.curPut == null) {
         throw new NotActiveException("no current PutField object");
      } else {
         this.bout.setBlockDataMode(false);
         this.curPut.writeFields();
         this.bout.setBlockDataMode(true);
      }
   }

   public void reset() throws IOException {
      if (this.depth != 0) {
         throw new IOException("stream active");
      } else {
         this.bout.setBlockDataMode(false);
         this.bout.writeByte(121);
         this.clear();
         this.bout.setBlockDataMode(true);
      }
   }

   protected void annotateClass(Class<?> var1) throws IOException {
   }

   protected void annotateProxyClass(Class<?> var1) throws IOException {
   }

   protected Object replaceObject(Object var1) throws IOException {
      return var1;
   }

   protected boolean enableReplaceObject(boolean var1) throws SecurityException {
      if (var1 == this.enableReplace) {
         return var1;
      } else {
         if (var1) {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkPermission(SUBSTITUTION_PERMISSION);
            }
         }

         this.enableReplace = var1;
         return !this.enableReplace;
      }
   }

   protected void writeStreamHeader() throws IOException {
      this.bout.writeShort(-21267);
      this.bout.writeShort(5);
   }

   protected void writeClassDescriptor(ObjectStreamClass var1) throws IOException {
      var1.writeNonProxy(this);
   }

   public void write(int var1) throws IOException {
      this.bout.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      this.bout.write(var1, 0, var1.length, false);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var4 = var2 + var3;
         if (var2 >= 0 && var3 >= 0 && var4 <= var1.length && var4 >= 0) {
            this.bout.write(var1, var2, var3, false);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void flush() throws IOException {
      this.bout.flush();
   }

   protected void drain() throws IOException {
      this.bout.drain();
   }

   public void close() throws IOException {
      this.flush();
      this.clear();
      this.bout.close();
   }

   public void writeBoolean(boolean var1) throws IOException {
      this.bout.writeBoolean(var1);
   }

   public void writeByte(int var1) throws IOException {
      this.bout.writeByte(var1);
   }

   public void writeShort(int var1) throws IOException {
      this.bout.writeShort(var1);
   }

   public void writeChar(int var1) throws IOException {
      this.bout.writeChar(var1);
   }

   public void writeInt(int var1) throws IOException {
      this.bout.writeInt(var1);
   }

   public void writeLong(long var1) throws IOException {
      this.bout.writeLong(var1);
   }

   public void writeFloat(float var1) throws IOException {
      this.bout.writeFloat(var1);
   }

   public void writeDouble(double var1) throws IOException {
      this.bout.writeDouble(var1);
   }

   public void writeBytes(String var1) throws IOException {
      this.bout.writeBytes(var1);
   }

   public void writeChars(String var1) throws IOException {
      this.bout.writeChars(var1);
   }

   public void writeUTF(String var1) throws IOException {
      this.bout.writeUTF(var1);
   }

   int getProtocolVersion() {
      return this.protocol;
   }

   void writeTypeString(String var1) throws IOException {
      if (var1 == null) {
         this.writeNull();
      } else {
         int var2;
         if ((var2 = this.handles.lookup(var1)) != -1) {
            this.writeHandle(var2);
         } else {
            this.writeString(var1, false);
         }
      }

   }

   private void verifySubclass() {
      Class var1 = this.getClass();
      if (var1 != ObjectOutputStream.class) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            ObjectStreamClass.processQueue(ObjectOutputStream.Caches.subclassAuditsQueue, ObjectOutputStream.Caches.subclassAudits);
            ObjectStreamClass.WeakClassKey var3 = new ObjectStreamClass.WeakClassKey(var1, ObjectOutputStream.Caches.subclassAuditsQueue);
            Boolean var4 = (Boolean)ObjectOutputStream.Caches.subclassAudits.get(var3);
            if (var4 == null) {
               var4 = auditSubclass(var1);
               ObjectOutputStream.Caches.subclassAudits.putIfAbsent(var3, var4);
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

            while(var1 != ObjectOutputStream.class) {
               try {
                  var1.getDeclaredMethod("writeUnshared", Object.class);
                  return Boolean.FALSE;
               } catch (NoSuchMethodException var4) {
                  try {
                     var1.getDeclaredMethod("putFields", (Class[])null);
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
      this.subs.clear();
      this.handles.clear();
   }

   private void writeObject0(Object var1, boolean var2) throws IOException {
      boolean var3 = this.bout.setBlockDataMode(false);
      ++this.depth;

      try {
         if ((var1 = this.subs.lookup(var1)) == null) {
            this.writeNull();
            return;
         }

         int var4;
         if (!var2 && (var4 = this.handles.lookup(var1)) != -1) {
            this.writeHandle(var4);
            return;
         }

         if (!(var1 instanceof Class)) {
            if (var1 instanceof ObjectStreamClass) {
               this.writeClassDesc((ObjectStreamClass)var1, var2);
               return;
            }

            Object var5 = var1;
            Class var6 = var1.getClass();

            while(true) {
               ObjectStreamClass var7 = ObjectStreamClass.lookup(var6, true);
               Class var8;
               if (!var7.hasWriteReplaceMethod() || (var1 = var7.invokeWriteReplace(var1)) == null || (var8 = var1.getClass()) == var6) {
                  if (this.enableReplace) {
                     Object var12 = this.replaceObject(var1);
                     if (var12 != var1 && var12 != null) {
                        var6 = var12.getClass();
                        var7 = ObjectStreamClass.lookup(var6, true);
                     }

                     var1 = var12;
                  }

                  if (var1 != var5) {
                     this.subs.assign(var5, var1);
                     if (var1 == null) {
                        this.writeNull();
                        return;
                     }

                     if (!var2 && (var4 = this.handles.lookup(var1)) != -1) {
                        this.writeHandle(var4);
                        return;
                     }

                     if (var1 instanceof Class) {
                        this.writeClass((Class)var1, var2);
                        return;
                     }

                     if (var1 instanceof ObjectStreamClass) {
                        this.writeClassDesc((ObjectStreamClass)var1, var2);
                        return;
                     }
                  }

                  if (var1 instanceof String) {
                     this.writeString((String)var1, var2);
                     return;
                  } else if (var6.isArray()) {
                     this.writeArray(var1, var7, var2);
                     return;
                  } else {
                     if (var1 instanceof Enum) {
                        this.writeEnum((Enum)var1, var7, var2);
                     } else {
                        if (!(var1 instanceof Serializable)) {
                           if (extendedDebugInfo) {
                              throw new NotSerializableException(var6.getName() + "\n" + this.debugInfoStack.toString());
                           }

                           throw new NotSerializableException(var6.getName());
                        }

                        this.writeOrdinaryObject(var1, var7, var2);
                     }

                     return;
                  }
               }

               var6 = var8;
            }
         }

         this.writeClass((Class)var1, var2);
      } finally {
         --this.depth;
         this.bout.setBlockDataMode(var3);
      }

   }

   private void writeNull() throws IOException {
      this.bout.writeByte(112);
   }

   private void writeHandle(int var1) throws IOException {
      this.bout.writeByte(113);
      this.bout.writeInt(8257536 + var1);
   }

   private void writeClass(Class<?> var1, boolean var2) throws IOException {
      this.bout.writeByte(118);
      this.writeClassDesc(ObjectStreamClass.lookup(var1, true), false);
      this.handles.assign(var2 ? null : var1);
   }

   private void writeClassDesc(ObjectStreamClass var1, boolean var2) throws IOException {
      if (var1 == null) {
         this.writeNull();
      } else {
         int var3;
         if (!var2 && (var3 = this.handles.lookup(var1)) != -1) {
            this.writeHandle(var3);
         } else if (var1.isProxy()) {
            this.writeProxyDesc(var1, var2);
         } else {
            this.writeNonProxyDesc(var1, var2);
         }
      }

   }

   private boolean isCustomSubclass() {
      return this.getClass().getClassLoader() != ObjectOutputStream.class.getClassLoader();
   }

   private void writeProxyDesc(ObjectStreamClass var1, boolean var2) throws IOException {
      this.bout.writeByte(125);
      this.handles.assign(var2 ? null : var1);
      Class var3 = var1.forClass();
      Class[] var4 = var3.getInterfaces();
      this.bout.writeInt(var4.length);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         this.bout.writeUTF(var4[var5].getName());
      }

      this.bout.setBlockDataMode(true);
      if (var3 != null && this.isCustomSubclass()) {
         ReflectUtil.checkPackageAccess(var3);
      }

      this.annotateProxyClass(var3);
      this.bout.setBlockDataMode(false);
      this.bout.writeByte(120);
      this.writeClassDesc(var1.getSuperDesc(), false);
   }

   private void writeNonProxyDesc(ObjectStreamClass var1, boolean var2) throws IOException {
      this.bout.writeByte(114);
      this.handles.assign(var2 ? null : var1);
      if (this.protocol == 1) {
         var1.writeNonProxy(this);
      } else {
         this.writeClassDescriptor(var1);
      }

      Class var3 = var1.forClass();
      this.bout.setBlockDataMode(true);
      if (var3 != null && this.isCustomSubclass()) {
         ReflectUtil.checkPackageAccess(var3);
      }

      this.annotateClass(var3);
      this.bout.setBlockDataMode(false);
      this.bout.writeByte(120);
      this.writeClassDesc(var1.getSuperDesc(), false);
   }

   private void writeString(String var1, boolean var2) throws IOException {
      this.handles.assign(var2 ? null : var1);
      long var3 = this.bout.getUTFLength(var1);
      if (var3 <= 65535L) {
         this.bout.writeByte(116);
         this.bout.writeUTF(var1, var3);
      } else {
         this.bout.writeByte(124);
         this.bout.writeLongUTF(var1, var3);
      }

   }

   private void writeArray(Object var1, ObjectStreamClass var2, boolean var3) throws IOException {
      this.bout.writeByte(117);
      this.writeClassDesc(var2, false);
      this.handles.assign(var3 ? null : var1);
      Class var4 = var2.forClass().getComponentType();
      if (var4.isPrimitive()) {
         if (var4 == Integer.TYPE) {
            int[] var5 = (int[])((int[])var1);
            this.bout.writeInt(var5.length);
            this.bout.writeInts(var5, 0, var5.length);
         } else if (var4 == Byte.TYPE) {
            byte[] var16 = (byte[])((byte[])var1);
            this.bout.writeInt(var16.length);
            this.bout.write(var16, 0, var16.length, true);
         } else if (var4 == Long.TYPE) {
            long[] var17 = (long[])((long[])var1);
            this.bout.writeInt(var17.length);
            this.bout.writeLongs(var17, 0, var17.length);
         } else if (var4 == Float.TYPE) {
            float[] var18 = (float[])((float[])var1);
            this.bout.writeInt(var18.length);
            this.bout.writeFloats(var18, 0, var18.length);
         } else if (var4 == Double.TYPE) {
            double[] var19 = (double[])((double[])var1);
            this.bout.writeInt(var19.length);
            this.bout.writeDoubles(var19, 0, var19.length);
         } else if (var4 == Short.TYPE) {
            short[] var20 = (short[])((short[])var1);
            this.bout.writeInt(var20.length);
            this.bout.writeShorts(var20, 0, var20.length);
         } else if (var4 == Character.TYPE) {
            char[] var21 = (char[])((char[])var1);
            this.bout.writeInt(var21.length);
            this.bout.writeChars(var21, 0, var21.length);
         } else {
            if (var4 != Boolean.TYPE) {
               throw new InternalError();
            }

            boolean[] var22 = (boolean[])((boolean[])var1);
            this.bout.writeInt(var22.length);
            this.bout.writeBooleans(var22, 0, var22.length);
         }
      } else {
         Object[] var23 = (Object[])((Object[])var1);
         int var6 = var23.length;
         this.bout.writeInt(var6);
         if (extendedDebugInfo) {
            this.debugInfoStack.push("array (class \"" + var1.getClass().getName() + "\", size: " + var6 + ")");
         }

         try {
            for(int var7 = 0; var7 < var6; ++var7) {
               if (extendedDebugInfo) {
                  this.debugInfoStack.push("element of array (index: " + var7 + ")");
               }

               try {
                  this.writeObject0(var23[var7], false);
               } finally {
                  if (extendedDebugInfo) {
                     this.debugInfoStack.pop();
                  }

               }
            }
         } finally {
            if (extendedDebugInfo) {
               this.debugInfoStack.pop();
            }

         }
      }

   }

   private void writeEnum(Enum<?> var1, ObjectStreamClass var2, boolean var3) throws IOException {
      this.bout.writeByte(126);
      ObjectStreamClass var4 = var2.getSuperDesc();
      this.writeClassDesc(var4.forClass() == Enum.class ? var2 : var4, false);
      this.handles.assign(var3 ? null : var1);
      this.writeString(var1.name(), false);
   }

   private void writeOrdinaryObject(Object var1, ObjectStreamClass var2, boolean var3) throws IOException {
      if (extendedDebugInfo) {
         this.debugInfoStack.push((this.depth == 1 ? "root " : "") + "object (class \"" + var1.getClass().getName() + "\", " + var1.toString() + ")");
      }

      try {
         var2.checkSerialize();
         this.bout.writeByte(115);
         this.writeClassDesc(var2, false);
         this.handles.assign(var3 ? null : var1);
         if (var2.isExternalizable() && !var2.isProxy()) {
            this.writeExternalData((Externalizable)var1);
         } else {
            this.writeSerialData(var1, var2);
         }
      } finally {
         if (extendedDebugInfo) {
            this.debugInfoStack.pop();
         }

      }

   }

   private void writeExternalData(Externalizable var1) throws IOException {
      ObjectOutputStream.PutFieldImpl var2 = this.curPut;
      this.curPut = null;
      if (extendedDebugInfo) {
         this.debugInfoStack.push("writeExternal data");
      }

      SerialCallbackContext var3 = this.curContext;

      try {
         this.curContext = null;
         if (this.protocol == 1) {
            var1.writeExternal(this);
         } else {
            this.bout.setBlockDataMode(true);
            var1.writeExternal(this);
            this.bout.setBlockDataMode(false);
            this.bout.writeByte(120);
         }
      } finally {
         this.curContext = var3;
         if (extendedDebugInfo) {
            this.debugInfoStack.pop();
         }

      }

      this.curPut = var2;
   }

   private void writeSerialData(Object var1, ObjectStreamClass var2) throws IOException {
      ObjectStreamClass.ClassDataSlot[] var3 = var2.getClassDataLayout();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ObjectStreamClass var5 = var3[var4].desc;
         if (var5.hasWriteObjectMethod()) {
            ObjectOutputStream.PutFieldImpl var6 = this.curPut;
            this.curPut = null;
            SerialCallbackContext var7 = this.curContext;
            if (extendedDebugInfo) {
               this.debugInfoStack.push("custom writeObject data (class \"" + var5.getName() + "\")");
            }

            try {
               this.curContext = new SerialCallbackContext(var1, var5);
               this.bout.setBlockDataMode(true);
               var5.invokeWriteObject(var1, this);
               this.bout.setBlockDataMode(false);
               this.bout.writeByte(120);
            } finally {
               this.curContext.setUsed();
               this.curContext = var7;
               if (extendedDebugInfo) {
                  this.debugInfoStack.pop();
               }

            }

            this.curPut = var6;
         } else {
            this.defaultWriteFields(var1, var5);
         }
      }

   }

   private void defaultWriteFields(Object var1, ObjectStreamClass var2) throws IOException {
      Class var3 = var2.forClass();
      if (var3 != null && var1 != null && !var3.isInstance(var1)) {
         throw new ClassCastException();
      } else {
         var2.checkDefaultSerialize();
         int var4 = var2.getPrimDataSize();
         if (this.primVals == null || this.primVals.length < var4) {
            this.primVals = new byte[var4];
         }

         var2.getPrimFieldValues(var1, this.primVals);
         this.bout.write(this.primVals, 0, var4, false);
         ObjectStreamField[] var5 = var2.getFields(false);
         Object[] var6 = new Object[var2.getNumObjFields()];
         int var7 = var5.length - var6.length;
         var2.getObjFieldValues(var1, var6);

         for(int var8 = 0; var8 < var6.length; ++var8) {
            if (extendedDebugInfo) {
               this.debugInfoStack.push("field (class \"" + var2.getName() + "\", name: \"" + var5[var7 + var8].getName() + "\", type: \"" + var5[var7 + var8].getType() + "\")");
            }

            try {
               this.writeObject0(var6[var8], var5[var7 + var8].isUnshared());
            } finally {
               if (extendedDebugInfo) {
                  this.debugInfoStack.pop();
               }

            }
         }

      }
   }

   private void writeFatalException(IOException var1) throws IOException {
      this.clear();
      boolean var2 = this.bout.setBlockDataMode(false);

      try {
         this.bout.writeByte(123);
         this.writeObject0(var1, false);
         this.clear();
      } finally {
         this.bout.setBlockDataMode(var2);
      }

   }

   private static native void floatsToBytes(float[] var0, int var1, byte[] var2, int var3, int var4);

   private static native void doublesToBytes(double[] var0, int var1, byte[] var2, int var3, int var4);

   private static class DebugTraceInfoStack {
      private final List<String> stack = new ArrayList();

      DebugTraceInfoStack() {
      }

      void clear() {
         this.stack.clear();
      }

      void pop() {
         this.stack.remove(this.stack.size() - 1);
      }

      void push(String var1) {
         this.stack.add("\t- " + var1);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (!this.stack.isEmpty()) {
            for(int var2 = this.stack.size(); var2 > 0; --var2) {
               var1.append((String)this.stack.get(var2 - 1) + (var2 != 1 ? "\n" : ""));
            }
         }

         return var1.toString();
      }
   }

   private static class ReplaceTable {
      private final ObjectOutputStream.HandleTable htab;
      private Object[] reps;

      ReplaceTable(int var1, float var2) {
         this.htab = new ObjectOutputStream.HandleTable(var1, var2);
         this.reps = new Object[var1];
      }

      void assign(Object var1, Object var2) {
         int var3 = this.htab.assign(var1);

         while(var3 >= this.reps.length) {
            this.grow();
         }

         this.reps[var3] = var2;
      }

      Object lookup(Object var1) {
         int var2 = this.htab.lookup(var1);
         return var2 >= 0 ? this.reps[var2] : var1;
      }

      void clear() {
         Arrays.fill(this.reps, 0, this.htab.size(), (Object)null);
         this.htab.clear();
      }

      int size() {
         return this.htab.size();
      }

      private void grow() {
         Object[] var1 = new Object[(this.reps.length << 1) + 1];
         System.arraycopy(this.reps, 0, var1, 0, this.reps.length);
         this.reps = var1;
      }
   }

   private static class HandleTable {
      private int size;
      private int threshold;
      private final float loadFactor;
      private int[] spine;
      private int[] next;
      private Object[] objs;

      HandleTable(int var1, float var2) {
         this.loadFactor = var2;
         this.spine = new int[var1];
         this.next = new int[var1];
         this.objs = new Object[var1];
         this.threshold = (int)((float)var1 * var2);
         this.clear();
      }

      int assign(Object var1) {
         if (this.size >= this.next.length) {
            this.growEntries();
         }

         if (this.size >= this.threshold) {
            this.growSpine();
         }

         this.insert(var1, this.size);
         return this.size++;
      }

      int lookup(Object var1) {
         if (this.size == 0) {
            return -1;
         } else {
            int var2 = this.hash(var1) % this.spine.length;

            for(int var3 = this.spine[var2]; var3 >= 0; var3 = this.next[var3]) {
               if (this.objs[var3] == var1) {
                  return var3;
               }
            }

            return -1;
         }
      }

      void clear() {
         Arrays.fill((int[])this.spine, (int)-1);
         Arrays.fill(this.objs, 0, this.size, (Object)null);
         this.size = 0;
      }

      int size() {
         return this.size;
      }

      private void insert(Object var1, int var2) {
         int var3 = this.hash(var1) % this.spine.length;
         this.objs[var2] = var1;
         this.next[var2] = this.spine[var3];
         this.spine[var3] = var2;
      }

      private void growSpine() {
         this.spine = new int[(this.spine.length << 1) + 1];
         this.threshold = (int)((float)this.spine.length * this.loadFactor);
         Arrays.fill((int[])this.spine, (int)-1);

         for(int var1 = 0; var1 < this.size; ++var1) {
            this.insert(this.objs[var1], var1);
         }

      }

      private void growEntries() {
         int var1 = (this.next.length << 1) + 1;
         int[] var2 = new int[var1];
         System.arraycopy(this.next, 0, var2, 0, this.size);
         this.next = var2;
         Object[] var3 = new Object[var1];
         System.arraycopy(this.objs, 0, var3, 0, this.size);
         this.objs = var3;
      }

      private int hash(Object var1) {
         return System.identityHashCode(var1) & Integer.MAX_VALUE;
      }
   }

   private static class BlockDataOutputStream extends OutputStream implements DataOutput {
      private static final int MAX_BLOCK_SIZE = 1024;
      private static final int MAX_HEADER_SIZE = 5;
      private static final int CHAR_BUF_SIZE = 256;
      private final byte[] buf = new byte[1024];
      private final byte[] hbuf = new byte[5];
      private final char[] cbuf = new char[256];
      private boolean blkmode = false;
      private int pos = 0;
      private final OutputStream out;
      private final DataOutputStream dout;

      BlockDataOutputStream(OutputStream var1) {
         this.out = var1;
         this.dout = new DataOutputStream(this);
      }

      boolean setBlockDataMode(boolean var1) throws IOException {
         if (this.blkmode == var1) {
            return this.blkmode;
         } else {
            this.drain();
            this.blkmode = var1;
            return !this.blkmode;
         }
      }

      boolean getBlockDataMode() {
         return this.blkmode;
      }

      public void write(int var1) throws IOException {
         if (this.pos >= 1024) {
            this.drain();
         }

         this.buf[this.pos++] = (byte)var1;
      }

      public void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length, false);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.write(var1, var2, var3, false);
      }

      public void flush() throws IOException {
         this.drain();
         this.out.flush();
      }

      public void close() throws IOException {
         this.flush();
         this.out.close();
      }

      void write(byte[] var1, int var2, int var3, boolean var4) throws IOException {
         if (!var4 && !this.blkmode) {
            this.drain();
            this.out.write(var1, var2, var3);
         } else {
            while(true) {
               while(var3 > 0) {
                  if (this.pos >= 1024) {
                     this.drain();
                  }

                  if (var3 >= 1024 && !var4 && this.pos == 0) {
                     this.writeBlockHeader(1024);
                     this.out.write(var1, var2, 1024);
                     var2 += 1024;
                     var3 -= 1024;
                  } else {
                     int var5 = Math.min(var3, 1024 - this.pos);
                     System.arraycopy(var1, var2, this.buf, this.pos, var5);
                     this.pos += var5;
                     var2 += var5;
                     var3 -= var5;
                  }
               }

               return;
            }
         }
      }

      void drain() throws IOException {
         if (this.pos != 0) {
            if (this.blkmode) {
               this.writeBlockHeader(this.pos);
            }

            this.out.write(this.buf, 0, this.pos);
            this.pos = 0;
         }
      }

      private void writeBlockHeader(int var1) throws IOException {
         if (var1 <= 255) {
            this.hbuf[0] = 119;
            this.hbuf[1] = (byte)var1;
            this.out.write(this.hbuf, 0, 2);
         } else {
            this.hbuf[0] = 122;
            Bits.putInt(this.hbuf, 1, var1);
            this.out.write(this.hbuf, 0, 5);
         }

      }

      public void writeBoolean(boolean var1) throws IOException {
         if (this.pos >= 1024) {
            this.drain();
         }

         Bits.putBoolean(this.buf, this.pos++, var1);
      }

      public void writeByte(int var1) throws IOException {
         if (this.pos >= 1024) {
            this.drain();
         }

         this.buf[this.pos++] = (byte)var1;
      }

      public void writeChar(int var1) throws IOException {
         if (this.pos + 2 <= 1024) {
            Bits.putChar(this.buf, this.pos, (char)var1);
            this.pos += 2;
         } else {
            this.dout.writeChar(var1);
         }

      }

      public void writeShort(int var1) throws IOException {
         if (this.pos + 2 <= 1024) {
            Bits.putShort(this.buf, this.pos, (short)var1);
            this.pos += 2;
         } else {
            this.dout.writeShort(var1);
         }

      }

      public void writeInt(int var1) throws IOException {
         if (this.pos + 4 <= 1024) {
            Bits.putInt(this.buf, this.pos, var1);
            this.pos += 4;
         } else {
            this.dout.writeInt(var1);
         }

      }

      public void writeFloat(float var1) throws IOException {
         if (this.pos + 4 <= 1024) {
            Bits.putFloat(this.buf, this.pos, var1);
            this.pos += 4;
         } else {
            this.dout.writeFloat(var1);
         }

      }

      public void writeLong(long var1) throws IOException {
         if (this.pos + 8 <= 1024) {
            Bits.putLong(this.buf, this.pos, var1);
            this.pos += 8;
         } else {
            this.dout.writeLong(var1);
         }

      }

      public void writeDouble(double var1) throws IOException {
         if (this.pos + 8 <= 1024) {
            Bits.putDouble(this.buf, this.pos, var1);
            this.pos += 8;
         } else {
            this.dout.writeDouble(var1);
         }

      }

      public void writeBytes(String var1) throws IOException {
         int var2 = var1.length();
         int var3 = 0;
         int var4 = 0;

         int var6;
         for(int var5 = 0; var5 < var2; var5 += var6) {
            if (var3 >= var4) {
               var3 = 0;
               var4 = Math.min(var2 - var5, 256);
               var1.getChars(var5, var5 + var4, this.cbuf, 0);
            }

            if (this.pos >= 1024) {
               this.drain();
            }

            var6 = Math.min(var4 - var3, 1024 - this.pos);

            for(int var7 = this.pos + var6; this.pos < var7; this.buf[this.pos++] = (byte)this.cbuf[var3++]) {
            }
         }

      }

      public void writeChars(String var1) throws IOException {
         int var2 = var1.length();

         int var4;
         for(int var3 = 0; var3 < var2; var3 += var4) {
            var4 = Math.min(var2 - var3, 256);
            var1.getChars(var3, var3 + var4, this.cbuf, 0);
            this.writeChars(this.cbuf, 0, var4);
         }

      }

      public void writeUTF(String var1) throws IOException {
         this.writeUTF(var1, this.getUTFLength(var1));
      }

      void writeBooleans(boolean[] var1, int var2, int var3) throws IOException {
         int var4 = var2 + var3;

         while(var2 < var4) {
            if (this.pos >= 1024) {
               this.drain();
            }

            int var5 = Math.min(var4, var2 + (1024 - this.pos));

            while(var2 < var5) {
               Bits.putBoolean(this.buf, this.pos++, var1[var2++]);
            }
         }

      }

      void writeChars(char[] var1, int var2, int var3) throws IOException {
         short var4 = 1022;
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               if (this.pos <= var4) {
                  int var6 = 1024 - this.pos >> 1;

                  for(int var7 = Math.min(var5, var2 + var6); var2 < var7; this.pos += 2) {
                     Bits.putChar(this.buf, this.pos, var1[var2++]);
                  }
               } else {
                  this.dout.writeChar(var1[var2++]);
               }
            }

            return;
         }
      }

      void writeShorts(short[] var1, int var2, int var3) throws IOException {
         short var4 = 1022;
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               if (this.pos <= var4) {
                  int var6 = 1024 - this.pos >> 1;

                  for(int var7 = Math.min(var5, var2 + var6); var2 < var7; this.pos += 2) {
                     Bits.putShort(this.buf, this.pos, var1[var2++]);
                  }
               } else {
                  this.dout.writeShort(var1[var2++]);
               }
            }

            return;
         }
      }

      void writeInts(int[] var1, int var2, int var3) throws IOException {
         short var4 = 1020;
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               if (this.pos <= var4) {
                  int var6 = 1024 - this.pos >> 2;

                  for(int var7 = Math.min(var5, var2 + var6); var2 < var7; this.pos += 4) {
                     Bits.putInt(this.buf, this.pos, var1[var2++]);
                  }
               } else {
                  this.dout.writeInt(var1[var2++]);
               }
            }

            return;
         }
      }

      void writeFloats(float[] var1, int var2, int var3) throws IOException {
         short var4 = 1020;
         int var5 = var2 + var3;

         while(var2 < var5) {
            if (this.pos <= var4) {
               int var6 = 1024 - this.pos >> 2;
               int var7 = Math.min(var5 - var2, var6);
               ObjectOutputStream.floatsToBytes(var1, var2, this.buf, this.pos, var7);
               var2 += var7;
               this.pos += var7 << 2;
            } else {
               this.dout.writeFloat(var1[var2++]);
            }
         }

      }

      void writeLongs(long[] var1, int var2, int var3) throws IOException {
         short var4 = 1016;
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               if (this.pos <= var4) {
                  int var6 = 1024 - this.pos >> 3;

                  for(int var7 = Math.min(var5, var2 + var6); var2 < var7; this.pos += 8) {
                     Bits.putLong(this.buf, this.pos, var1[var2++]);
                  }
               } else {
                  this.dout.writeLong(var1[var2++]);
               }
            }

            return;
         }
      }

      void writeDoubles(double[] var1, int var2, int var3) throws IOException {
         short var4 = 1016;
         int var5 = var2 + var3;

         while(var2 < var5) {
            if (this.pos <= var4) {
               int var6 = 1024 - this.pos >> 3;
               int var7 = Math.min(var5 - var2, var6);
               ObjectOutputStream.doublesToBytes(var1, var2, this.buf, this.pos, var7);
               var2 += var7;
               this.pos += var7 << 3;
            } else {
               this.dout.writeDouble(var1[var2++]);
            }
         }

      }

      long getUTFLength(String var1) {
         int var2 = var1.length();
         long var3 = 0L;

         int var6;
         for(int var5 = 0; var5 < var2; var5 += var6) {
            var6 = Math.min(var2 - var5, 256);
            var1.getChars(var5, var5 + var6, this.cbuf, 0);

            for(int var7 = 0; var7 < var6; ++var7) {
               char var8 = this.cbuf[var7];
               if (var8 >= 1 && var8 <= 127) {
                  ++var3;
               } else if (var8 > 2047) {
                  var3 += 3L;
               } else {
                  var3 += 2L;
               }
            }
         }

         return var3;
      }

      void writeUTF(String var1, long var2) throws IOException {
         if (var2 > 65535L) {
            throw new UTFDataFormatException();
         } else {
            this.writeShort((int)var2);
            if (var2 == (long)var1.length()) {
               this.writeBytes(var1);
            } else {
               this.writeUTFBody(var1);
            }

         }
      }

      void writeLongUTF(String var1) throws IOException {
         this.writeLongUTF(var1, this.getUTFLength(var1));
      }

      void writeLongUTF(String var1, long var2) throws IOException {
         this.writeLong(var2);
         if (var2 == (long)var1.length()) {
            this.writeBytes(var1);
         } else {
            this.writeUTFBody(var1);
         }

      }

      private void writeUTFBody(String var1) throws IOException {
         short var2 = 1021;
         int var3 = var1.length();

         int var5;
         for(int var4 = 0; var4 < var3; var4 += var5) {
            var5 = Math.min(var3 - var4, 256);
            var1.getChars(var4, var4 + var5, this.cbuf, 0);

            for(int var6 = 0; var6 < var5; ++var6) {
               char var7 = this.cbuf[var6];
               if (this.pos <= var2) {
                  if (var7 <= 127 && var7 != 0) {
                     this.buf[this.pos++] = (byte)var7;
                  } else if (var7 > 2047) {
                     this.buf[this.pos + 2] = (byte)(128 | var7 >> 0 & 63);
                     this.buf[this.pos + 1] = (byte)(128 | var7 >> 6 & 63);
                     this.buf[this.pos + 0] = (byte)(224 | var7 >> 12 & 15);
                     this.pos += 3;
                  } else {
                     this.buf[this.pos + 1] = (byte)(128 | var7 >> 0 & 63);
                     this.buf[this.pos + 0] = (byte)(192 | var7 >> 6 & 31);
                     this.pos += 2;
                  }
               } else if (var7 <= 127 && var7 != 0) {
                  this.write(var7);
               } else if (var7 > 2047) {
                  this.write(224 | var7 >> 12 & 15);
                  this.write(128 | var7 >> 6 & 63);
                  this.write(128 | var7 >> 0 & 63);
               } else {
                  this.write(192 | var7 >> 6 & 31);
                  this.write(128 | var7 >> 0 & 63);
               }
            }
         }

      }
   }

   private class PutFieldImpl extends ObjectOutputStream.PutField {
      private final ObjectStreamClass desc;
      private final byte[] primVals;
      private final Object[] objVals;

      PutFieldImpl(ObjectStreamClass var2) {
         this.desc = var2;
         this.primVals = new byte[var2.getPrimDataSize()];
         this.objVals = new Object[var2.getNumObjFields()];
      }

      public void put(String var1, boolean var2) {
         Bits.putBoolean(this.primVals, this.getFieldOffset(var1, Boolean.TYPE), var2);
      }

      public void put(String var1, byte var2) {
         this.primVals[this.getFieldOffset(var1, Byte.TYPE)] = var2;
      }

      public void put(String var1, char var2) {
         Bits.putChar(this.primVals, this.getFieldOffset(var1, Character.TYPE), var2);
      }

      public void put(String var1, short var2) {
         Bits.putShort(this.primVals, this.getFieldOffset(var1, Short.TYPE), var2);
      }

      public void put(String var1, int var2) {
         Bits.putInt(this.primVals, this.getFieldOffset(var1, Integer.TYPE), var2);
      }

      public void put(String var1, float var2) {
         Bits.putFloat(this.primVals, this.getFieldOffset(var1, Float.TYPE), var2);
      }

      public void put(String var1, long var2) {
         Bits.putLong(this.primVals, this.getFieldOffset(var1, Long.TYPE), var2);
      }

      public void put(String var1, double var2) {
         Bits.putDouble(this.primVals, this.getFieldOffset(var1, Double.TYPE), var2);
      }

      public void put(String var1, Object var2) {
         this.objVals[this.getFieldOffset(var1, Object.class)] = var2;
      }

      public void write(ObjectOutput var1) throws IOException {
         if (ObjectOutputStream.this != var1) {
            throw new IllegalArgumentException("wrong stream");
         } else {
            var1.write(this.primVals, 0, this.primVals.length);
            ObjectStreamField[] var2 = this.desc.getFields(false);
            int var3 = var2.length - this.objVals.length;

            for(int var4 = 0; var4 < this.objVals.length; ++var4) {
               if (var2[var3 + var4].isUnshared()) {
                  throw new IOException("cannot write unshared object");
               }

               var1.writeObject(this.objVals[var4]);
            }

         }
      }

      void writeFields() throws IOException {
         ObjectOutputStream.this.bout.write(this.primVals, 0, this.primVals.length, false);
         ObjectStreamField[] var1 = this.desc.getFields(false);
         int var2 = var1.length - this.objVals.length;

         for(int var3 = 0; var3 < this.objVals.length; ++var3) {
            if (ObjectOutputStream.extendedDebugInfo) {
               ObjectOutputStream.this.debugInfoStack.push("field (class \"" + this.desc.getName() + "\", name: \"" + var1[var2 + var3].getName() + "\", type: \"" + var1[var2 + var3].getType() + "\")");
            }

            try {
               ObjectOutputStream.this.writeObject0(this.objVals[var3], var1[var2 + var3].isUnshared());
            } finally {
               if (ObjectOutputStream.extendedDebugInfo) {
                  ObjectOutputStream.this.debugInfoStack.pop();
               }

            }
         }

      }

      private int getFieldOffset(String var1, Class<?> var2) {
         ObjectStreamField var3 = this.desc.getField(var1, var2);
         if (var3 == null) {
            throw new IllegalArgumentException("no such field " + var1 + " with type " + var2);
         } else {
            return var3.getOffset();
         }
      }
   }

   public abstract static class PutField {
      public abstract void put(String var1, boolean var2);

      public abstract void put(String var1, byte var2);

      public abstract void put(String var1, char var2);

      public abstract void put(String var1, short var2);

      public abstract void put(String var1, int var2);

      public abstract void put(String var1, long var2);

      public abstract void put(String var1, float var2);

      public abstract void put(String var1, double var2);

      public abstract void put(String var1, Object var2);

      /** @deprecated */
      @Deprecated
      public abstract void write(ObjectOutput var1) throws IOException;
   }

   private static class Caches {
      static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
      static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
   }
}
