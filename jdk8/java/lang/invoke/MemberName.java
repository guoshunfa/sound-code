package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyAccess;

final class MemberName implements Member, Cloneable {
   private Class<?> clazz;
   private String name;
   private Object type;
   private int flags;
   private Object resolution;
   private static final int MH_INVOKE_MODS = 273;
   static final int BRIDGE = 64;
   static final int VARARGS = 128;
   static final int SYNTHETIC = 4096;
   static final int ANNOTATION = 8192;
   static final int ENUM = 16384;
   static final String CONSTRUCTOR_NAME = "<init>";
   static final int RECOGNIZED_MODIFIERS = 65535;
   static final int IS_METHOD = 65536;
   static final int IS_CONSTRUCTOR = 131072;
   static final int IS_FIELD = 262144;
   static final int IS_TYPE = 524288;
   static final int CALLER_SENSITIVE = 1048576;
   static final int ALL_ACCESS = 7;
   static final int ALL_KINDS = 983040;
   static final int IS_INVOCABLE = 196608;
   static final int IS_FIELD_OR_METHOD = 327680;
   static final int SEARCH_ALL_SUPERS = 3145728;

   public Class<?> getDeclaringClass() {
      return this.clazz;
   }

   public ClassLoader getClassLoader() {
      return this.clazz.getClassLoader();
   }

   public String getName() {
      if (this.name == null) {
         this.expandFromVM();
         if (this.name == null) {
            return null;
         }
      }

      return this.name;
   }

   public MethodType getMethodOrFieldType() {
      if (this.isInvocable()) {
         return this.getMethodType();
      } else if (this.isGetter()) {
         return MethodType.methodType(this.getFieldType());
      } else if (this.isSetter()) {
         return MethodType.methodType(Void.TYPE, this.getFieldType());
      } else {
         throw new InternalError("not a method or field: " + this);
      }
   }

   public MethodType getMethodType() {
      if (this.type == null) {
         this.expandFromVM();
         if (this.type == null) {
            return null;
         }
      }

      if (!this.isInvocable()) {
         throw MethodHandleStatics.newIllegalArgumentException("not invocable, no method type");
      } else {
         Object var1 = this.type;
         if (var1 instanceof MethodType) {
            return (MethodType)var1;
         } else {
            synchronized(this) {
               if (this.type instanceof String) {
                  String var2 = (String)this.type;
                  MethodType var3 = MethodType.fromMethodDescriptorString(var2, this.getClassLoader());
                  this.type = var3;
               } else if (this.type instanceof Object[]) {
                  Object[] var8 = (Object[])((Object[])this.type);
                  Class[] var9 = (Class[])((Class[])var8[1]);
                  Class var4 = (Class)var8[0];
                  MethodType var5 = MethodType.methodType(var4, var9);
                  this.type = var5;
               }

               assert this.type instanceof MethodType : "bad method type " + this.type;
            }

            return (MethodType)this.type;
         }
      }
   }

   public MethodType getInvocationType() {
      MethodType var1 = this.getMethodOrFieldType();
      if (this.isConstructor() && this.getReferenceKind() == 8) {
         return var1.changeReturnType(this.clazz);
      } else {
         return !this.isStatic() ? var1.insertParameterTypes(0, (Class[])(this.clazz)) : var1;
      }
   }

   public Class<?>[] getParameterTypes() {
      return this.getMethodType().parameterArray();
   }

   public Class<?> getReturnType() {
      return this.getMethodType().returnType();
   }

   public Class<?> getFieldType() {
      if (this.type == null) {
         this.expandFromVM();
         if (this.type == null) {
            return null;
         }
      }

      if (this.isInvocable()) {
         throw MethodHandleStatics.newIllegalArgumentException("not a field or nested class, no simple type");
      } else {
         Object var1 = this.type;
         if (var1 instanceof Class) {
            return (Class)var1;
         } else {
            synchronized(this) {
               if (this.type instanceof String) {
                  String var2 = (String)this.type;
                  MethodType var3 = MethodType.fromMethodDescriptorString("()" + var2, this.getClassLoader());
                  Class var4 = var3.returnType();
                  this.type = var4;
               }

               assert this.type instanceof Class : "bad field type " + this.type;
            }

            return (Class)this.type;
         }
      }
   }

   public Object getType() {
      return this.isInvocable() ? this.getMethodType() : this.getFieldType();
   }

   public String getSignature() {
      if (this.type == null) {
         this.expandFromVM();
         if (this.type == null) {
            return null;
         }
      }

      return this.isInvocable() ? BytecodeDescriptor.unparse(this.getMethodType()) : BytecodeDescriptor.unparse(this.getFieldType());
   }

   public int getModifiers() {
      return this.flags & '\uffff';
   }

   public byte getReferenceKind() {
      return (byte)(this.flags >>> 24 & 15);
   }

   private boolean referenceKindIsConsistent() {
      byte var1 = this.getReferenceKind();
      if (var1 == 0) {
         return this.isType();
      } else {
         if (this.isField()) {
            assert this.staticIsConsistent();

            assert MethodHandleNatives.refKindIsField(var1);
         } else if (this.isConstructor()) {
            assert var1 == 8 || var1 == 7;
         } else if (this.isMethod()) {
            assert this.staticIsConsistent();

            assert MethodHandleNatives.refKindIsMethod(var1);

            assert !this.clazz.isInterface() || var1 == 9 || var1 == 6 || var1 == 7 || var1 == 5 && this.isObjectPublicMethod();
         } else {
            assert false;
         }

         return true;
      }
   }

   private boolean isObjectPublicMethod() {
      if (this.clazz == Object.class) {
         return true;
      } else {
         MethodType var1 = this.getMethodType();
         if (this.name.equals("toString") && var1.returnType() == String.class && var1.parameterCount() == 0) {
            return true;
         } else if (this.name.equals("hashCode") && var1.returnType() == Integer.TYPE && var1.parameterCount() == 0) {
            return true;
         } else {
            return this.name.equals("equals") && var1.returnType() == Boolean.TYPE && var1.parameterCount() == 1 && var1.parameterType(0) == Object.class;
         }
      }
   }

   boolean referenceKindIsConsistentWith(int var1) {
      byte var2 = this.getReferenceKind();
      if (var2 == var1) {
         return true;
      } else {
         switch(var1) {
         case 5:
         case 8:
            assert var2 == 7 : this;

            return true;
         case 6:
         case 7:
         default:
            assert false : this + " != " + MethodHandleNatives.refKindName((byte)var1);

            return true;
         case 9:
            assert var2 == 5 || var2 == 7 : this;

            return true;
         }
      }
   }

   private boolean staticIsConsistent() {
      byte var1 = this.getReferenceKind();
      return MethodHandleNatives.refKindIsStatic(var1) == this.isStatic() || this.getModifiers() == 0;
   }

   private boolean vminfoIsConsistent() {
      byte var1 = this.getReferenceKind();

      assert this.isResolved();

      Object var2 = MethodHandleNatives.getMemberVMInfo(this);

      assert var2 instanceof Object[];

      long var3 = (Long)((Object[])((Object[])var2))[0];
      Object var5 = ((Object[])((Object[])var2))[1];
      if (MethodHandleNatives.refKindIsField(var1)) {
         assert var5 instanceof Class;

         assert var5 instanceof Class;
      } else {
         if (MethodHandleNatives.refKindDoesDispatch(var1)) {
            assert var3 >= 0L : var3 + ":" + this;
         } else {
            assert var3 < 0L : var3;
         }

         assert var5 instanceof MemberName : var5 + " in " + this;
      }

      return true;
   }

   private MemberName changeReferenceKind(byte var1, byte var2) {
      assert this.getReferenceKind() == var2;

      assert MethodHandleNatives.refKindIsValid(var1);

      this.flags += var1 - var2 << 24;
      return this;
   }

   private boolean testFlags(int var1, int var2) {
      return (this.flags & var1) == var2;
   }

   private boolean testAllFlags(int var1) {
      return this.testFlags(var1, var1);
   }

   private boolean testAnyFlags(int var1) {
      return !this.testFlags(var1, 0);
   }

   public boolean isMethodHandleInvoke() {
      return this.testFlags(280, 272) && this.clazz == MethodHandle.class ? isMethodHandleInvokeName(this.name) : false;
   }

   public static boolean isMethodHandleInvokeName(String var0) {
      byte var2 = -1;
      switch(var0.hashCode()) {
      case -1183693704:
         if (var0.equals("invoke")) {
            var2 = 0;
         }
         break;
      case 941760871:
         if (var0.equals("invokeExact")) {
            var2 = 1;
         }
      }

      switch(var2) {
      case 0:
      case 1:
         return true;
      default:
         return false;
      }
   }

   public boolean isStatic() {
      return Modifier.isStatic(this.flags);
   }

   public boolean isPublic() {
      return Modifier.isPublic(this.flags);
   }

   public boolean isPrivate() {
      return Modifier.isPrivate(this.flags);
   }

   public boolean isProtected() {
      return Modifier.isProtected(this.flags);
   }

   public boolean isFinal() {
      return Modifier.isFinal(this.flags);
   }

   public boolean canBeStaticallyBound() {
      return Modifier.isFinal(this.flags | this.clazz.getModifiers());
   }

   public boolean isVolatile() {
      return Modifier.isVolatile(this.flags);
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this.flags);
   }

   public boolean isNative() {
      return Modifier.isNative(this.flags);
   }

   public boolean isBridge() {
      return this.testAllFlags(65600);
   }

   public boolean isVarargs() {
      return this.testAllFlags(128) && this.isInvocable();
   }

   public boolean isSynthetic() {
      return this.testAllFlags(4096);
   }

   public boolean isInvocable() {
      return this.testAnyFlags(196608);
   }

   public boolean isFieldOrMethod() {
      return this.testAnyFlags(327680);
   }

   public boolean isMethod() {
      return this.testAllFlags(65536);
   }

   public boolean isConstructor() {
      return this.testAllFlags(131072);
   }

   public boolean isField() {
      return this.testAllFlags(262144);
   }

   public boolean isType() {
      return this.testAllFlags(524288);
   }

   public boolean isPackage() {
      return !this.testAnyFlags(7);
   }

   public boolean isCallerSensitive() {
      return this.testAllFlags(1048576);
   }

   public boolean isAccessibleFrom(Class<?> var1) {
      return VerifyAccess.isMemberAccessible(this.getDeclaringClass(), this.getDeclaringClass(), this.flags, var1, 15);
   }

   private void init(Class<?> var1, String var2, Object var3, int var4) {
      this.clazz = var1;
      this.name = var2;
      this.type = var3;
      this.flags = var4;

      assert this.testAnyFlags(983040);

      assert this.resolution == null;

   }

   private void expandFromVM() {
      if (this.type == null) {
         if (this.isResolved()) {
            MethodHandleNatives.expand(this);
         }
      }
   }

   private static int flagsMods(int var0, int var1, byte var2) {
      assert (var0 & '\uffff') == 0;

      assert (var1 & -65536) == 0;

      assert (var2 & -16) == 0;

      return var0 | var1 | var2 << 24;
   }

   public MemberName(Method var1) {
      this(var1, false);
   }

   public MemberName(Method var1, boolean var2) {
      var1.getClass();
      MethodHandleNatives.init(this, var1);
      if (this.clazz == null) {
         if (var1.getDeclaringClass() == MethodHandle.class && isMethodHandleInvokeName(var1.getName())) {
            MethodType var3 = MethodType.methodType(var1.getReturnType(), var1.getParameterTypes());
            int var4 = flagsMods(65536, var1.getModifiers(), (byte)5);
            this.init(MethodHandle.class, var1.getName(), var3, var4);
            if (this.isMethodHandleInvoke()) {
               return;
            }
         }

         throw new LinkageError(var1.toString());
      } else {
         assert this.isResolved() && this.clazz != null;

         this.name = var1.getName();
         if (this.type == null) {
            this.type = new Object[]{var1.getReturnType(), var1.getParameterTypes()};
         }

         if (var2) {
            if (this.isAbstract()) {
               throw new AbstractMethodError(this.toString());
            }

            if (this.getReferenceKind() == 5) {
               this.changeReferenceKind((byte)7, (byte)5);
            } else if (this.getReferenceKind() == 9) {
               this.changeReferenceKind((byte)7, (byte)9);
            }
         }

      }
   }

   public MemberName asSpecial() {
      switch(this.getReferenceKind()) {
      case 5:
         return this.clone().changeReferenceKind((byte)7, (byte)5);
      case 6:
      default:
         throw new IllegalArgumentException(this.toString());
      case 7:
         return this;
      case 8:
         return this.clone().changeReferenceKind((byte)7, (byte)8);
      case 9:
         return this.clone().changeReferenceKind((byte)7, (byte)9);
      }
   }

   public MemberName asConstructor() {
      switch(this.getReferenceKind()) {
      case 7:
         return this.clone().changeReferenceKind((byte)8, (byte)7);
      case 8:
         return this;
      default:
         throw new IllegalArgumentException(this.toString());
      }
   }

   public MemberName asNormalOriginal() {
      int var1 = this.clazz.isInterface() ? 9 : 5;
      byte var2 = this.getReferenceKind();
      int var3 = var2;
      switch(var2) {
      case 5:
      case 7:
      case 9:
         var3 = var1;
      case 6:
      case 8:
      default:
         if (var3 == var2) {
            return this;
         } else {
            MemberName var4 = this.clone().changeReferenceKind((byte)var3, var2);

            assert this.referenceKindIsConsistentWith(var4.getReferenceKind());

            return var4;
         }
      }
   }

   public MemberName(Constructor<?> var1) {
      var1.getClass();
      MethodHandleNatives.init(this, var1);

      assert this.isResolved() && this.clazz != null;

      this.name = "<init>";
      if (this.type == null) {
         this.type = new Object[]{Void.TYPE, var1.getParameterTypes()};
      }

   }

   public MemberName(Field var1) {
      this(var1, false);
   }

   public MemberName(Field var1, boolean var2) {
      var1.getClass();
      MethodHandleNatives.init(this, var1);

      assert this.isResolved() && this.clazz != null;

      this.name = var1.getName();
      this.type = var1.getType();
      byte var3 = this.getReferenceKind();

      assert var3 == (this.isStatic() ? 2 : 1);

      if (var2) {
         this.changeReferenceKind((byte)(var3 + 2), var3);
      }

   }

   public boolean isGetter() {
      return MethodHandleNatives.refKindIsGetter(this.getReferenceKind());
   }

   public boolean isSetter() {
      return MethodHandleNatives.refKindIsSetter(this.getReferenceKind());
   }

   public MemberName asSetter() {
      byte var1 = this.getReferenceKind();

      assert MethodHandleNatives.refKindIsGetter(var1);

      byte var2 = (byte)(var1 + 2);
      return this.clone().changeReferenceKind(var2, var1);
   }

   public MemberName(Class<?> var1) {
      this.init(var1.getDeclaringClass(), var1.getSimpleName(), var1, flagsMods(524288, var1.getModifiers(), (byte)0));
      this.initResolved(true);
   }

   static MemberName makeMethodHandleInvoke(String var0, MethodType var1) {
      return makeMethodHandleInvoke(var0, var1, 4369);
   }

   static MemberName makeMethodHandleInvoke(String var0, MethodType var1, int var2) {
      MemberName var3 = new MemberName(MethodHandle.class, var0, var1, (byte)5);
      var3.flags |= var2;

      assert var3.isMethodHandleInvoke() : var3;

      return var3;
   }

   MemberName() {
   }

   protected MemberName clone() {
      try {
         return (MemberName)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw MethodHandleStatics.newInternalError((Throwable)var2);
      }
   }

   public MemberName getDefinition() {
      if (!this.isResolved()) {
         throw new IllegalStateException("must be resolved: " + this);
      } else if (this.isType()) {
         return this;
      } else {
         MemberName var1 = this.clone();
         var1.clazz = null;
         var1.type = null;
         var1.name = null;
         var1.resolution = var1;
         var1.expandFromVM();

         assert var1.getName().equals(this.getName());

         return var1;
      }
   }

   public int hashCode() {
      return Objects.hash(this.clazz, this.getReferenceKind(), this.name, this.getType());
   }

   public boolean equals(Object var1) {
      return var1 instanceof MemberName && this.equals((MemberName)var1);
   }

   public boolean equals(MemberName var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         return this.clazz == var1.clazz && this.getReferenceKind() == var1.getReferenceKind() && Objects.equals(this.name, var1.name) && Objects.equals(this.getType(), var1.getType());
      }
   }

   public MemberName(Class<?> var1, String var2, Class<?> var3, byte var4) {
      this.init(var1, var2, var3, flagsMods(262144, 0, var4));
      this.initResolved(false);
   }

   public MemberName(Class<?> var1, String var2, MethodType var3, byte var4) {
      int var5 = var2 != null && var2.equals("<init>") ? 131072 : 65536;
      this.init(var1, var2, var3, flagsMods(var5, 0, var4));
      this.initResolved(false);
   }

   public MemberName(byte var1, Class<?> var2, String var3, Object var4) {
      int var5;
      if (MethodHandleNatives.refKindIsField(var1)) {
         var5 = 262144;
         if (!(var4 instanceof Class)) {
            throw MethodHandleStatics.newIllegalArgumentException("not a field type");
         }
      } else if (MethodHandleNatives.refKindIsMethod(var1)) {
         var5 = 65536;
         if (!(var4 instanceof MethodType)) {
            throw MethodHandleStatics.newIllegalArgumentException("not a method type");
         }
      } else {
         if (var1 != 8) {
            throw MethodHandleStatics.newIllegalArgumentException("bad reference kind " + var1);
         }

         var5 = 131072;
         if (!(var4 instanceof MethodType) || !"<init>".equals(var3)) {
            throw MethodHandleStatics.newIllegalArgumentException("not a constructor type or name");
         }
      }

      this.init(var2, var3, var4, flagsMods(var5, 0, var1));
      this.initResolved(false);
   }

   public boolean hasReceiverTypeDispatch() {
      return MethodHandleNatives.refKindDoesDispatch(this.getReferenceKind());
   }

   public boolean isResolved() {
      return this.resolution == null;
   }

   private void initResolved(boolean var1) {
      assert this.resolution == null;

      if (!var1) {
         this.resolution = this;
      }

      assert this.isResolved() == var1;

   }

   void checkForTypeAlias(Class<?> var1) {
      if (this.isInvocable()) {
         MethodType var3;
         if (this.type instanceof MethodType) {
            var3 = (MethodType)this.type;
         } else {
            this.type = var3 = this.getMethodType();
         }

         if (var3.erase() != var3) {
            if (!VerifyAccess.isTypeVisible(var3, var1)) {
               throw new LinkageError("bad method type alias: " + var3 + " not visible from " + var1);
            }
         }
      } else {
         Class var2;
         if (this.type instanceof Class) {
            var2 = (Class)this.type;
         } else {
            this.type = var2 = this.getFieldType();
         }

         if (!VerifyAccess.isTypeVisible(var2, var1)) {
            throw new LinkageError("bad field type alias: " + var2 + " not visible from " + var1);
         }
      }
   }

   public String toString() {
      if (this.isType()) {
         return this.type.toString();
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.getDeclaringClass() != null) {
            var1.append(getName(this.clazz));
            var1.append('.');
         }

         String var2 = this.getName();
         var1.append(var2 == null ? "*" : var2);
         Object var3 = this.getType();
         if (!this.isInvocable()) {
            var1.append('/');
            var1.append(var3 == null ? "*" : getName(var3));
         } else {
            var1.append(var3 == null ? "(*)*" : getName(var3));
         }

         byte var4 = this.getReferenceKind();
         if (var4 != 0) {
            var1.append('/');
            var1.append(MethodHandleNatives.refKindName(var4));
         }

         return var1.toString();
      }
   }

   private static String getName(Object var0) {
      return var0 instanceof Class ? ((Class)var0).getName() : String.valueOf(var0);
   }

   public IllegalAccessException makeAccessException(String var1, Object var2) {
      var1 = var1 + ": " + this.toString();
      if (var2 != null) {
         var1 = var1 + ", from " + var2;
      }

      return new IllegalAccessException(var1);
   }

   private String message() {
      if (this.isResolved()) {
         return "no access";
      } else if (this.isConstructor()) {
         return "no such constructor";
      } else {
         return this.isMethod() ? "no such method" : "no such field";
      }
   }

   public ReflectiveOperationException makeAccessException() {
      String var1 = this.message() + ": " + this.toString();
      Object var2;
      if (this.isResolved() || !(this.resolution instanceof NoSuchMethodError) && !(this.resolution instanceof NoSuchFieldError)) {
         var2 = new IllegalAccessException(var1);
      } else if (this.isConstructor()) {
         var2 = new NoSuchMethodException(var1);
      } else if (this.isMethod()) {
         var2 = new NoSuchMethodException(var1);
      } else {
         var2 = new NoSuchFieldException(var1);
      }

      if (this.resolution instanceof Throwable) {
         ((ReflectiveOperationException)var2).initCause((Throwable)this.resolution);
      }

      return (ReflectiveOperationException)var2;
   }

   static MemberName.Factory getFactory() {
      return MemberName.Factory.INSTANCE;
   }

   static class Factory {
      static MemberName.Factory INSTANCE = new MemberName.Factory();
      private static int ALLOWED_FLAGS = 983040;

      private Factory() {
      }

      List<MemberName> getMembers(Class<?> var1, String var2, Object var3, int var4, Class<?> var5) {
         var4 &= ALLOWED_FLAGS;
         String var6 = null;
         if (var3 != null) {
            var6 = BytecodeDescriptor.unparse(var3);
            if (var6.startsWith("(")) {
               var4 &= -786433;
            } else {
               var4 &= -720897;
            }
         }

         int var8 = var2 == null ? 10 : (var3 == null ? 4 : 1);
         MemberName[] var9 = newMemberBuffer(var8);
         int var10 = 0;
         ArrayList var11 = null;
         boolean var12 = false;

         while(true) {
            int var16 = MethodHandleNatives.getMembers(var1, var2, var6, var4, var5, var10, var9);
            if (var16 <= var9.length) {
               if (var16 < 0) {
                  var16 = 0;
               }

               var10 += var16;
               ArrayList var17 = new ArrayList(var10);
               Iterator var18;
               if (var11 != null) {
                  var18 = var11.iterator();

                  while(var18.hasNext()) {
                     MemberName[] var15 = (MemberName[])var18.next();
                     Collections.addAll(var17, var15);
                  }
               }

               var17.addAll(Arrays.asList(var9).subList(0, var16));
               if (var3 != null && var3 != var6) {
                  var18 = var17.iterator();

                  while(var18.hasNext()) {
                     MemberName var19 = (MemberName)var18.next();
                     if (!var3.equals(var19.getType())) {
                        var18.remove();
                     }
                  }
               }

               return var17;
            }

            var10 += var9.length;
            int var13 = var16 - var9.length;
            if (var11 == null) {
               var11 = new ArrayList(1);
            }

            var11.add(var9);
            int var14 = var9.length;
            var14 = Math.max(var14, var13);
            var14 = Math.max(var14, var10 / 4);
            var9 = newMemberBuffer(Math.min(8192, var14));
         }
      }

      private MemberName resolve(byte var1, MemberName var2, Class<?> var3) {
         MemberName var4 = var2.clone();

         assert var1 == var4.getReferenceKind();

         try {
            var4 = MethodHandleNatives.resolve(var4, var3);
            var4.checkForTypeAlias(var4.getDeclaringClass());
            var4.resolution = null;
         } catch (LinkageError | ClassNotFoundException var6) {
            assert !var4.isResolved();

            var4.resolution = var6;
            return var4;
         }

         assert var4.referenceKindIsConsistent();

         var4.initResolved(true);

         assert var4.vminfoIsConsistent();

         return var4;
      }

      public <NoSuchMemberException extends ReflectiveOperationException> MemberName resolveOrFail(byte var1, MemberName var2, Class<?> var3, Class<NoSuchMemberException> var4) throws IllegalAccessException, NoSuchMemberException {
         MemberName var5 = this.resolve(var1, var2, var3);
         if (var5.isResolved()) {
            return var5;
         } else {
            ReflectiveOperationException var6 = var5.makeAccessException();
            if (var6 instanceof IllegalAccessException) {
               throw (IllegalAccessException)var6;
            } else {
               throw (ReflectiveOperationException)var4.cast(var6);
            }
         }
      }

      public MemberName resolveOrNull(byte var1, MemberName var2, Class<?> var3) {
         MemberName var4 = this.resolve(var1, var2, var3);
         return var4.isResolved() ? var4 : null;
      }

      public List<MemberName> getMethods(Class<?> var1, boolean var2, Class<?> var3) {
         return this.getMethods(var1, var2, (String)null, (MethodType)null, var3);
      }

      public List<MemberName> getMethods(Class<?> var1, boolean var2, String var3, MethodType var4, Class<?> var5) {
         int var6 = 65536 | (var2 ? 3145728 : 0);
         return this.getMembers(var1, var3, var4, var6, var5);
      }

      public List<MemberName> getConstructors(Class<?> var1, Class<?> var2) {
         return this.getMembers(var1, (String)null, (Object)null, 131072, var2);
      }

      public List<MemberName> getFields(Class<?> var1, boolean var2, Class<?> var3) {
         return this.getFields(var1, var2, (String)null, (Class)null, var3);
      }

      public List<MemberName> getFields(Class<?> var1, boolean var2, String var3, Class<?> var4, Class<?> var5) {
         int var6 = 262144 | (var2 ? 3145728 : 0);
         return this.getMembers(var1, var3, var4, var6, var5);
      }

      public List<MemberName> getNestedTypes(Class<?> var1, boolean var2, Class<?> var3) {
         int var4 = 524288 | (var2 ? 3145728 : 0);
         return this.getMembers(var1, (String)null, (Object)null, var4, var3);
      }

      private static MemberName[] newMemberBuffer(int var0) {
         MemberName[] var1 = new MemberName[var0];

         for(int var2 = 0; var2 < var0; ++var2) {
            var1[var2] = new MemberName();
         }

         return var1;
      }
   }
}
