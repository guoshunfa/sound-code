package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.FieldAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.FieldRepository;
import sun.reflect.generics.scope.ClassScope;

public final class Field extends AccessibleObject implements Member {
   private Class<?> clazz;
   private int slot;
   private String name;
   private Class<?> type;
   private int modifiers;
   private transient String signature;
   private transient FieldRepository genericInfo;
   private byte[] annotations;
   private FieldAccessor fieldAccessor;
   private FieldAccessor overrideFieldAccessor;
   private Field root;
   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

   private String getGenericSignature() {
      return this.signature;
   }

   private GenericsFactory getFactory() {
      Class var1 = this.getDeclaringClass();
      return CoreReflectionFactory.make(var1, ClassScope.make(var1));
   }

   private FieldRepository getGenericInfo() {
      if (this.genericInfo == null) {
         this.genericInfo = FieldRepository.make(this.getGenericSignature(), this.getFactory());
      }

      return this.genericInfo;
   }

   Field(Class<?> var1, String var2, Class<?> var3, int var4, int var5, String var6, byte[] var7) {
      this.clazz = var1;
      this.name = var2;
      this.type = var3;
      this.modifiers = var4;
      this.slot = var5;
      this.signature = var6;
      this.annotations = var7;
   }

   Field copy() {
      if (this.root != null) {
         throw new IllegalArgumentException("Can not copy a non-root Field");
      } else {
         Field var1 = new Field(this.clazz, this.name, this.type, this.modifiers, this.slot, this.signature, this.annotations);
         var1.root = this;
         var1.fieldAccessor = this.fieldAccessor;
         var1.overrideFieldAccessor = this.overrideFieldAccessor;
         return var1;
      }
   }

   public Class<?> getDeclaringClass() {
      return this.clazz;
   }

   public String getName() {
      return this.name;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public boolean isEnumConstant() {
      return (this.getModifiers() & 16384) != 0;
   }

   public boolean isSynthetic() {
      return Modifier.isSynthetic(this.getModifiers());
   }

   public Class<?> getType() {
      return this.type;
   }

   public Type getGenericType() {
      return (Type)(this.getGenericSignature() != null ? this.getGenericInfo().getGenericType() : this.getType());
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Field) {
         Field var2 = (Field)var1;
         return this.getDeclaringClass() == var2.getDeclaringClass() && this.getName() == var2.getName() && this.getType() == var2.getType();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getDeclaringClass().getName().hashCode() ^ this.getName().hashCode();
   }

   public String toString() {
      int var1 = this.getModifiers();
      return (var1 == 0 ? "" : Modifier.toString(var1) + " ") + this.getType().getTypeName() + " " + this.getDeclaringClass().getTypeName() + "." + this.getName();
   }

   public String toGenericString() {
      int var1 = this.getModifiers();
      Type var2 = this.getGenericType();
      return (var1 == 0 ? "" : Modifier.toString(var1) + " ") + var2.getTypeName() + " " + this.getDeclaringClass().getTypeName() + "." + this.getName();
   }

   @CallerSensitive
   public Object get(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).get(var1);
   }

   @CallerSensitive
   public boolean getBoolean(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getBoolean(var1);
   }

   @CallerSensitive
   public byte getByte(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getByte(var1);
   }

   @CallerSensitive
   public char getChar(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getChar(var1);
   }

   @CallerSensitive
   public short getShort(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getShort(var1);
   }

   @CallerSensitive
   public int getInt(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getInt(var1);
   }

   @CallerSensitive
   public long getLong(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getLong(var1);
   }

   @CallerSensitive
   public float getFloat(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getFloat(var1);
   }

   @CallerSensitive
   public double getDouble(Object var1) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, var1, this.modifiers);
      }

      return this.getFieldAccessor(var1).getDouble(var1);
   }

   @CallerSensitive
   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).set(var1, var2);
   }

   @CallerSensitive
   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setBoolean(var1, var2);
   }

   @CallerSensitive
   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setByte(var1, var2);
   }

   @CallerSensitive
   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setChar(var1, var2);
   }

   @CallerSensitive
   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setShort(var1, var2);
   }

   @CallerSensitive
   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setInt(var1, var2);
   }

   @CallerSensitive
   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var4 = Reflection.getCallerClass();
         this.checkAccess(var4, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setLong(var1, var2);
   }

   @CallerSensitive
   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setFloat(var1, var2);
   }

   @CallerSensitive
   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var4 = Reflection.getCallerClass();
         this.checkAccess(var4, this.clazz, var1, this.modifiers);
      }

      this.getFieldAccessor(var1).setDouble(var1, var2);
   }

   private FieldAccessor getFieldAccessor(Object var1) throws IllegalAccessException {
      boolean var2 = this.override;
      FieldAccessor var3 = var2 ? this.overrideFieldAccessor : this.fieldAccessor;
      return var3 != null ? var3 : this.acquireFieldAccessor(var2);
   }

   private FieldAccessor acquireFieldAccessor(boolean var1) {
      FieldAccessor var2 = null;
      if (this.root != null) {
         var2 = this.root.getFieldAccessor(var1);
      }

      if (var2 != null) {
         if (var1) {
            this.overrideFieldAccessor = var2;
         } else {
            this.fieldAccessor = var2;
         }
      } else {
         var2 = reflectionFactory.newFieldAccessor(this, var1);
         this.setFieldAccessor(var2, var1);
      }

      return var2;
   }

   private FieldAccessor getFieldAccessor(boolean var1) {
      return var1 ? this.overrideFieldAccessor : this.fieldAccessor;
   }

   private void setFieldAccessor(FieldAccessor var1, boolean var2) {
      if (var2) {
         this.overrideFieldAccessor = var1;
      } else {
         this.fieldAccessor = var1;
      }

      if (this.root != null) {
         this.root.setFieldAccessor(var1, var2);
      }

   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)var1.cast(this.declaredAnnotations().get(var1));
   }

   public <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.declaredAnnotations(), var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return AnnotationParser.toArray(this.declaredAnnotations());
   }

   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
      if (this.declaredAnnotations == null) {
         Field var1 = this.root;
         if (var1 != null) {
            this.declaredAnnotations = var1.declaredAnnotations();
         } else {
            this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this.getDeclaringClass());
         }
      }

      return this.declaredAnnotations;
   }

   private native byte[] getTypeAnnotationBytes0();

   public AnnotatedType getAnnotatedType() {
      return TypeAnnotationParser.buildAnnotatedType(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), this.getGenericType(), TypeAnnotation.TypeAnnotationTarget.FIELD);
   }
}
