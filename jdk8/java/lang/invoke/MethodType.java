package java.lang.invoke;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;

public final class MethodType implements Serializable {
   private static final long serialVersionUID = 292L;
   private final Class<?> rtype;
   private final Class<?>[] ptypes;
   @Stable
   private MethodTypeForm form;
   @Stable
   private MethodType wrapAlt;
   @Stable
   private Invokers invokers;
   @Stable
   private String methodDescriptor;
   static final int MAX_JVM_ARITY = 255;
   static final int MAX_MH_ARITY = 254;
   static final int MAX_MH_INVOKER_ARITY = 253;
   static final MethodType.ConcurrentWeakInternSet<MethodType> internTable = new MethodType.ConcurrentWeakInternSet();
   static final Class<?>[] NO_PTYPES = new Class[0];
   private static final MethodType[] objectOnlyTypes = new MethodType[20];
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
   private static final long rtypeOffset;
   private static final long ptypesOffset;

   private MethodType(Class<?> var1, Class<?>[] var2, boolean var3) {
      checkRtype(var1);
      checkPtypes(var2);
      this.rtype = var1;
      this.ptypes = var3 ? var2 : (Class[])Arrays.copyOf((Object[])var2, var2.length);
   }

   private MethodType(Class<?>[] var1, Class<?> var2) {
      this.rtype = var2;
      this.ptypes = var1;
   }

   MethodTypeForm form() {
      return this.form;
   }

   Class<?> rtype() {
      return this.rtype;
   }

   Class<?>[] ptypes() {
      return this.ptypes;
   }

   void setForm(MethodTypeForm var1) {
      this.form = var1;
   }

   private static void checkRtype(Class<?> var0) {
      Objects.requireNonNull(var0);
   }

   private static void checkPtype(Class<?> var0) {
      Objects.requireNonNull(var0);
      if (var0 == Void.TYPE) {
         throw MethodHandleStatics.newIllegalArgumentException("parameter type cannot be void");
      }
   }

   private static int checkPtypes(Class<?>[] var0) {
      int var1 = 0;
      Class[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class var5 = var2[var4];
         checkPtype(var5);
         if (var5 == Double.TYPE || var5 == Long.TYPE) {
            ++var1;
         }
      }

      checkSlotCount(var0.length + var1);
      return var1;
   }

   static void checkSlotCount(int var0) {
      if ((var0 & 255) != var0) {
         throw MethodHandleStatics.newIllegalArgumentException("bad parameter count " + var0);
      }
   }

   private static IndexOutOfBoundsException newIndexOutOfBoundsException(Object var0) {
      if (var0 instanceof Integer) {
         var0 = "bad index: " + var0;
      }

      return new IndexOutOfBoundsException(var0.toString());
   }

   public static MethodType methodType(Class<?> var0, Class<?>[] var1) {
      return makeImpl(var0, var1, false);
   }

   public static MethodType methodType(Class<?> var0, List<Class<?>> var1) {
      boolean var2 = false;
      return makeImpl(var0, listToArray(var1), var2);
   }

   private static Class<?>[] listToArray(List<Class<?>> var0) {
      checkSlotCount(var0.size());
      return (Class[])var0.toArray(NO_PTYPES);
   }

   public static MethodType methodType(Class<?> var0, Class<?> var1, Class<?>... var2) {
      Class[] var3 = new Class[1 + var2.length];
      var3[0] = var1;
      System.arraycopy(var2, 0, var3, 1, var2.length);
      return makeImpl(var0, var3, true);
   }

   public static MethodType methodType(Class<?> var0) {
      return makeImpl(var0, NO_PTYPES, true);
   }

   public static MethodType methodType(Class<?> var0, Class<?> var1) {
      return makeImpl(var0, new Class[]{var1}, true);
   }

   public static MethodType methodType(Class<?> var0, MethodType var1) {
      return makeImpl(var0, var1.ptypes, true);
   }

   static MethodType makeImpl(Class<?> var0, Class<?>[] var1, boolean var2) {
      MethodType var3 = (MethodType)internTable.get(new MethodType(var1, var0));
      if (var3 != null) {
         return var3;
      } else {
         if (var1.length == 0) {
            var1 = NO_PTYPES;
            var2 = true;
         }

         var3 = new MethodType(var0, var1, var2);
         var3.form = MethodTypeForm.findForm(var3);
         return (MethodType)internTable.add(var3);
      }
   }

   public static MethodType genericMethodType(int var0, boolean var1) {
      checkSlotCount(var0);
      int var3 = !var1 ? 0 : 1;
      int var4 = var0 * 2 + var3;
      MethodType var2;
      if (var4 < objectOnlyTypes.length) {
         var2 = objectOnlyTypes[var4];
         if (var2 != null) {
            return var2;
         }
      }

      Class[] var5 = new Class[var0 + var3];
      Arrays.fill(var5, Object.class);
      if (var3 != 0) {
         var5[var0] = Object[].class;
      }

      var2 = makeImpl(Object.class, var5, true);
      if (var4 < objectOnlyTypes.length) {
         objectOnlyTypes[var4] = var2;
      }

      return var2;
   }

   public static MethodType genericMethodType(int var0) {
      return genericMethodType(var0, false);
   }

   public MethodType changeParameterType(int var1, Class<?> var2) {
      if (this.parameterType(var1) == var2) {
         return this;
      } else {
         checkPtype(var2);
         Class[] var3 = (Class[])this.ptypes.clone();
         var3[var1] = var2;
         return makeImpl(this.rtype, var3, true);
      }
   }

   public MethodType insertParameterTypes(int var1, Class<?>... var2) {
      int var3 = this.ptypes.length;
      if (var1 >= 0 && var1 <= var3) {
         int var4 = checkPtypes(var2);
         checkSlotCount(this.parameterSlotCount() + var2.length + var4);
         int var5 = var2.length;
         if (var5 == 0) {
            return this;
         } else {
            Class[] var6 = (Class[])Arrays.copyOfRange((Object[])this.ptypes, 0, var3 + var5);
            System.arraycopy(var6, var1, var6, var1 + var5, var3 - var1);
            System.arraycopy(var2, 0, var6, var1, var5);
            return makeImpl(this.rtype, var6, true);
         }
      } else {
         throw newIndexOutOfBoundsException(var1);
      }
   }

   public MethodType appendParameterTypes(Class<?>... var1) {
      return this.insertParameterTypes(this.parameterCount(), var1);
   }

   public MethodType insertParameterTypes(int var1, List<Class<?>> var2) {
      return this.insertParameterTypes(var1, listToArray(var2));
   }

   public MethodType appendParameterTypes(List<Class<?>> var1) {
      return this.insertParameterTypes(this.parameterCount(), var1);
   }

   MethodType replaceParameterTypes(int var1, int var2, Class<?>... var3) {
      if (var1 == var2) {
         return this.insertParameterTypes(var1, var3);
      } else {
         int var4 = this.ptypes.length;
         if (0 <= var1 && var1 <= var2 && var2 <= var4) {
            int var5 = var3.length;
            return var5 == 0 ? this.dropParameterTypes(var1, var2) : this.dropParameterTypes(var1, var2).insertParameterTypes(var1, var3);
         } else {
            throw newIndexOutOfBoundsException("start=" + var1 + " end=" + var2);
         }
      }
   }

   MethodType asSpreaderType(Class<?> var1, int var2) {
      assert this.parameterCount() >= var2;

      int var3 = this.ptypes.length - var2;
      if (var2 == 0) {
         return this;
      } else {
         if (var1 == Object[].class) {
            if (this.isGeneric()) {
               return this;
            }

            if (var3 == 0) {
               MethodType var7 = genericMethodType(var2);
               if (this.rtype != Object.class) {
                  var7 = var7.changeReturnType(this.rtype);
               }

               return var7;
            }
         }

         Class var4 = var1.getComponentType();

         assert var4 != null;

         for(int var5 = var3; var5 < this.ptypes.length; ++var5) {
            if (this.ptypes[var5] != var4) {
               Class[] var6 = (Class[])this.ptypes.clone();
               Arrays.fill(var6, var5, this.ptypes.length, var4);
               return methodType(this.rtype, var6);
            }
         }

         return this;
      }
   }

   Class<?> leadingReferenceParameter() {
      Class var1;
      if (this.ptypes.length != 0 && !(var1 = this.ptypes[0]).isPrimitive()) {
         return var1;
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("no leading reference parameter");
      }
   }

   MethodType asCollectorType(Class<?> var1, int var2) {
      assert this.parameterCount() >= 1;

      assert this.lastParameterType().isAssignableFrom(var1);

      MethodType var3;
      if (var1 == Object[].class) {
         var3 = genericMethodType(var2);
         if (this.rtype != Object.class) {
            var3 = var3.changeReturnType(this.rtype);
         }
      } else {
         Class var4 = var1.getComponentType();

         assert var4 != null;

         var3 = methodType(this.rtype, Collections.nCopies(var2, var4));
      }

      return this.ptypes.length == 1 ? var3 : var3.insertParameterTypes(0, (List)this.parameterList().subList(0, this.ptypes.length - 1));
   }

   public MethodType dropParameterTypes(int var1, int var2) {
      int var3 = this.ptypes.length;
      if (0 <= var1 && var1 <= var2 && var2 <= var3) {
         if (var1 == var2) {
            return this;
         } else {
            Class[] var4;
            if (var1 == 0) {
               if (var2 == var3) {
                  var4 = NO_PTYPES;
               } else {
                  var4 = (Class[])Arrays.copyOfRange((Object[])this.ptypes, var2, var3);
               }
            } else if (var2 == var3) {
               var4 = (Class[])Arrays.copyOfRange((Object[])this.ptypes, 0, var1);
            } else {
               int var5 = var3 - var2;
               var4 = (Class[])Arrays.copyOfRange((Object[])this.ptypes, 0, var1 + var5);
               System.arraycopy(this.ptypes, var2, var4, var1, var5);
            }

            return makeImpl(this.rtype, var4, true);
         }
      } else {
         throw newIndexOutOfBoundsException("start=" + var1 + " end=" + var2);
      }
   }

   public MethodType changeReturnType(Class<?> var1) {
      return this.returnType() == var1 ? this : makeImpl(var1, this.ptypes, true);
   }

   public boolean hasPrimitives() {
      return this.form.hasPrimitives();
   }

   public boolean hasWrappers() {
      return this.unwrap() != this;
   }

   public MethodType erase() {
      return this.form.erasedType();
   }

   MethodType basicType() {
      return this.form.basicType();
   }

   MethodType invokerType() {
      return this.insertParameterTypes(0, (Class[])(MethodHandle.class));
   }

   public MethodType generic() {
      return genericMethodType(this.parameterCount());
   }

   boolean isGeneric() {
      return this == this.erase() && !this.hasPrimitives();
   }

   public MethodType wrap() {
      return this.hasPrimitives() ? wrapWithPrims(this) : this;
   }

   public MethodType unwrap() {
      MethodType var1 = !this.hasPrimitives() ? this : wrapWithPrims(this);
      return unwrapWithNoPrims(var1);
   }

   private static MethodType wrapWithPrims(MethodType var0) {
      assert var0.hasPrimitives();

      MethodType var1 = var0.wrapAlt;
      if (var1 == null) {
         var1 = MethodTypeForm.canonicalize(var0, 2, 2);

         assert var1 != null;

         var0.wrapAlt = var1;
      }

      return var1;
   }

   private static MethodType unwrapWithNoPrims(MethodType var0) {
      assert !var0.hasPrimitives();

      MethodType var1 = var0.wrapAlt;
      if (var1 == null) {
         var1 = MethodTypeForm.canonicalize(var0, 3, 3);
         if (var1 == null) {
            var1 = var0;
         }

         var0.wrapAlt = var1;
      }

      return var1;
   }

   public Class<?> parameterType(int var1) {
      return this.ptypes[var1];
   }

   public int parameterCount() {
      return this.ptypes.length;
   }

   public Class<?> returnType() {
      return this.rtype;
   }

   public List<Class<?>> parameterList() {
      return Collections.unmodifiableList(Arrays.asList((Object[])this.ptypes.clone()));
   }

   Class<?> lastParameterType() {
      int var1 = this.ptypes.length;
      return var1 == 0 ? Void.TYPE : this.ptypes[var1 - 1];
   }

   public Class<?>[] parameterArray() {
      return (Class[])this.ptypes.clone();
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof MethodType && this.equals((MethodType)var1);
   }

   private boolean equals(MethodType var1) {
      return this.rtype == var1.rtype && Arrays.equals((Object[])this.ptypes, (Object[])var1.ptypes);
   }

   public int hashCode() {
      int var1 = 31 + this.rtype.hashCode();
      Class[] var2 = this.ptypes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class var5 = var2[var4];
         var1 = 31 * var1 + var5.hashCode();
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("(");

      for(int var2 = 0; var2 < this.ptypes.length; ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(this.ptypes[var2].getSimpleName());
      }

      var1.append(")");
      var1.append(this.rtype.getSimpleName());
      return var1.toString();
   }

   boolean isViewableAs(MethodType var1, boolean var2) {
      return !VerifyType.isNullConversion(this.returnType(), var1.returnType(), var2) ? false : this.parametersAreViewableAs(var1, var2);
   }

   boolean parametersAreViewableAs(MethodType var1, boolean var2) {
      if (this.form == var1.form && this.form.erasedType == this) {
         return true;
      } else if (this.ptypes == var1.ptypes) {
         return true;
      } else {
         int var3 = this.parameterCount();
         if (var3 != var1.parameterCount()) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (!VerifyType.isNullConversion(var1.parameterType(var4), this.parameterType(var4), var2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   boolean isConvertibleTo(MethodType var1) {
      MethodTypeForm var2 = this.form();
      MethodTypeForm var3 = var1.form();
      if (var2 == var3) {
         return true;
      } else if (!canConvert(this.returnType(), var1.returnType())) {
         return false;
      } else {
         Class[] var4 = var1.ptypes;
         Class[] var5 = this.ptypes;
         if (var4 == var5) {
            return true;
         } else {
            int var6;
            if ((var6 = var4.length) != var5.length) {
               return false;
            } else if (var6 <= 1) {
               return var6 != 1 || canConvert(var4[0], var5[0]);
            } else if (var2.primitiveParameterCount() == 0 && var2.erasedType == this || var3.primitiveParameterCount() == 0 && var3.erasedType == var1) {
               assert this.canConvertParameters(var4, var5);

               return true;
            } else {
               return this.canConvertParameters(var4, var5);
            }
         }
      }
   }

   boolean explicitCastEquivalentToAsType(MethodType var1) {
      if (this == var1) {
         return true;
      } else if (!explicitCastEquivalentToAsType(this.rtype, var1.rtype)) {
         return false;
      } else {
         Class[] var2 = var1.ptypes;
         Class[] var3 = this.ptypes;
         if (var3 == var2) {
            return true;
         } else {
            assert var3.length == var2.length;

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (!explicitCastEquivalentToAsType(var2[var4], var3[var4])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean explicitCastEquivalentToAsType(Class<?> var0, Class<?> var1) {
      if (var0 != var1 && var1 != Object.class && var1 != Void.TYPE) {
         if (var0.isPrimitive()) {
            return canConvert(var0, var1);
         } else if (var1.isPrimitive()) {
            return false;
         } else {
            return !var1.isInterface() || var1.isAssignableFrom(var0);
         }
      } else {
         return true;
      }
   }

   private boolean canConvertParameters(Class<?>[] var1, Class<?>[] var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (!canConvert(var1[var3], var2[var3])) {
            return false;
         }
      }

      return true;
   }

   static boolean canConvert(Class<?> var0, Class<?> var1) {
      if (var0 != var1 && var0 != Object.class && var1 != Object.class) {
         Wrapper var2;
         if (var0.isPrimitive()) {
            if (var0 == Void.TYPE) {
               return true;
            } else {
               var2 = Wrapper.forPrimitiveType(var0);
               return var1.isPrimitive() ? Wrapper.forPrimitiveType(var1).isConvertibleFrom(var2) : var1.isAssignableFrom(var2.wrapperType());
            }
         } else if (var1.isPrimitive()) {
            if (var1 == Void.TYPE) {
               return true;
            } else {
               var2 = Wrapper.forPrimitiveType(var1);
               if (var0.isAssignableFrom(var2.wrapperType())) {
                  return true;
               } else {
                  return Wrapper.isWrapperType(var0) && var2.isConvertibleFrom(Wrapper.forWrapperType(var0));
               }
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   int parameterSlotCount() {
      return this.form.parameterSlotCount();
   }

   Invokers invokers() {
      Invokers var1 = this.invokers;
      if (var1 != null) {
         return var1;
      } else {
         this.invokers = var1 = new Invokers(this);
         return var1;
      }
   }

   int parameterSlotDepth(int var1) {
      if (var1 < 0 || var1 > this.ptypes.length) {
         this.parameterType(var1);
      }

      return this.form.parameterToArgSlot(var1 - 1);
   }

   int returnSlotCount() {
      return this.form.returnSlotCount();
   }

   public static MethodType fromMethodDescriptorString(String var0, ClassLoader var1) throws IllegalArgumentException, TypeNotPresentException {
      if (var0.startsWith("(") && var0.indexOf(41) >= 0 && var0.indexOf(46) < 0) {
         List var2 = BytecodeDescriptor.parseMethod(var0, var1);
         Class var3 = (Class)var2.remove(var2.size() - 1);
         checkSlotCount(var2.size());
         Class[] var4 = listToArray(var2);
         return makeImpl(var3, var4, true);
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("not a method descriptor: " + var0);
      }
   }

   public String toMethodDescriptorString() {
      String var1 = this.methodDescriptor;
      if (var1 == null) {
         var1 = BytecodeDescriptor.unparse(this);
         this.methodDescriptor = var1;
      }

      return var1;
   }

   static String toFieldDescriptorString(Class<?> var0) {
      return BytecodeDescriptor.unparse(var0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.returnType());
      var1.writeObject(this.parameterArray());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.MethodType_init(Void.TYPE, NO_PTYPES);
      var1.defaultReadObject();
      Class var2 = (Class)var1.readObject();
      Class[] var3 = (Class[])((Class[])var1.readObject());
      var3 = (Class[])var3.clone();
      this.MethodType_init(var2, var3);
   }

   private void MethodType_init(Class<?> var1, Class<?>[] var2) {
      checkRtype(var1);
      checkPtypes(var2);
      MethodHandleStatics.UNSAFE.putObject(this, rtypeOffset, var1);
      MethodHandleStatics.UNSAFE.putObject(this, ptypesOffset, var2);
   }

   private Object readResolve() {
      MethodType var1;
      try {
         var1 = methodType(this.rtype, this.ptypes);
      } finally {
         this.MethodType_init(Void.TYPE, NO_PTYPES);
      }

      return var1;
   }

   static {
      try {
         rtypeOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("rtype"));
         ptypesOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("ptypes"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   private static class ConcurrentWeakInternSet<T> {
      private final ConcurrentMap<MethodType.ConcurrentWeakInternSet.WeakEntry<T>, MethodType.ConcurrentWeakInternSet.WeakEntry<T>> map = new ConcurrentHashMap();
      private final ReferenceQueue<T> stale = new ReferenceQueue();

      public ConcurrentWeakInternSet() {
      }

      public T get(T var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.expungeStaleElements();
            MethodType.ConcurrentWeakInternSet.WeakEntry var2 = (MethodType.ConcurrentWeakInternSet.WeakEntry)this.map.get(new MethodType.ConcurrentWeakInternSet.WeakEntry(var1));
            if (var2 != null) {
               Object var3 = var2.get();
               if (var3 != null) {
                  return var3;
               }
            }

            return null;
         }
      }

      public T add(T var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            MethodType.ConcurrentWeakInternSet.WeakEntry var3 = new MethodType.ConcurrentWeakInternSet.WeakEntry(var1, this.stale);

            Object var2;
            do {
               this.expungeStaleElements();
               MethodType.ConcurrentWeakInternSet.WeakEntry var4 = (MethodType.ConcurrentWeakInternSet.WeakEntry)this.map.putIfAbsent(var3, var3);
               var2 = var4 == null ? var1 : var4.get();
            } while(var2 == null);

            return var2;
         }
      }

      private void expungeStaleElements() {
         Reference var1;
         while((var1 = this.stale.poll()) != null) {
            this.map.remove(var1);
         }

      }

      private static class WeakEntry<T> extends WeakReference<T> {
         public final int hashcode;

         public WeakEntry(T var1, ReferenceQueue<T> var2) {
            super(var1, var2);
            this.hashcode = var1.hashCode();
         }

         public WeakEntry(T var1) {
            super(var1);
            this.hashcode = var1.hashCode();
         }

         public boolean equals(Object var1) {
            if (!(var1 instanceof MethodType.ConcurrentWeakInternSet.WeakEntry)) {
               return false;
            } else {
               Object var2 = ((MethodType.ConcurrentWeakInternSet.WeakEntry)var1).get();
               Object var3 = this.get();
               return var2 != null && var3 != null ? var3.equals(var2) : this == var1;
            }
         }

         public int hashCode() {
            return this.hashcode;
         }
      }
   }
}
