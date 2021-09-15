package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

abstract class UnsafeFieldAccessorImpl extends FieldAccessorImpl {
   static final Unsafe unsafe = Unsafe.getUnsafe();
   protected final Field field;
   protected final long fieldOffset;
   protected final boolean isFinal;

   UnsafeFieldAccessorImpl(Field var1) {
      this.field = var1;
      if (Modifier.isStatic(var1.getModifiers())) {
         this.fieldOffset = unsafe.staticFieldOffset(var1);
      } else {
         this.fieldOffset = unsafe.objectFieldOffset(var1);
      }

      this.isFinal = Modifier.isFinal(var1.getModifiers());
   }

   protected void ensureObj(Object var1) {
      if (!this.field.getDeclaringClass().isAssignableFrom(var1.getClass())) {
         this.throwSetIllegalArgumentException(var1);
      }

   }

   private String getQualifiedFieldName() {
      return this.field.getDeclaringClass().getName() + "." + this.field.getName();
   }

   protected IllegalArgumentException newGetIllegalArgumentException(String var1) {
      return new IllegalArgumentException("Attempt to get " + this.field.getType().getName() + " field \"" + this.getQualifiedFieldName() + "\" with illegal data type conversion to " + var1);
   }

   protected void throwFinalFieldIllegalAccessException(String var1, String var2) throws IllegalAccessException {
      throw new IllegalAccessException(this.getSetMessage(var1, var2));
   }

   protected void throwFinalFieldIllegalAccessException(Object var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException(var1 != null ? var1.getClass().getName() : "", "");
   }

   protected void throwFinalFieldIllegalAccessException(boolean var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("boolean", Boolean.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(char var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("char", Character.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(byte var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("byte", Byte.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(short var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("short", Short.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(int var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("int", Integer.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(long var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("long", Long.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(float var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("float", Float.toString(var1));
   }

   protected void throwFinalFieldIllegalAccessException(double var1) throws IllegalAccessException {
      this.throwFinalFieldIllegalAccessException("double", Double.toString(var1));
   }

   protected IllegalArgumentException newGetBooleanIllegalArgumentException() {
      return this.newGetIllegalArgumentException("boolean");
   }

   protected IllegalArgumentException newGetByteIllegalArgumentException() {
      return this.newGetIllegalArgumentException("byte");
   }

   protected IllegalArgumentException newGetCharIllegalArgumentException() {
      return this.newGetIllegalArgumentException("char");
   }

   protected IllegalArgumentException newGetShortIllegalArgumentException() {
      return this.newGetIllegalArgumentException("short");
   }

   protected IllegalArgumentException newGetIntIllegalArgumentException() {
      return this.newGetIllegalArgumentException("int");
   }

   protected IllegalArgumentException newGetLongIllegalArgumentException() {
      return this.newGetIllegalArgumentException("long");
   }

   protected IllegalArgumentException newGetFloatIllegalArgumentException() {
      return this.newGetIllegalArgumentException("float");
   }

   protected IllegalArgumentException newGetDoubleIllegalArgumentException() {
      return this.newGetIllegalArgumentException("double");
   }

   protected String getSetMessage(String var1, String var2) {
      String var3 = "Can not set";
      if (Modifier.isStatic(this.field.getModifiers())) {
         var3 = var3 + " static";
      }

      if (this.isFinal) {
         var3 = var3 + " final";
      }

      var3 = var3 + " " + this.field.getType().getName() + " field " + this.getQualifiedFieldName() + " to ";
      if (var2.length() > 0) {
         var3 = var3 + "(" + var1 + ")" + var2;
      } else if (var1.length() > 0) {
         var3 = var3 + var1;
      } else {
         var3 = var3 + "null value";
      }

      return var3;
   }

   protected void throwSetIllegalArgumentException(String var1, String var2) {
      throw new IllegalArgumentException(this.getSetMessage(var1, var2));
   }

   protected void throwSetIllegalArgumentException(Object var1) {
      this.throwSetIllegalArgumentException(var1 != null ? var1.getClass().getName() : "", "");
   }

   protected void throwSetIllegalArgumentException(boolean var1) {
      this.throwSetIllegalArgumentException("boolean", Boolean.toString(var1));
   }

   protected void throwSetIllegalArgumentException(byte var1) {
      this.throwSetIllegalArgumentException("byte", Byte.toString(var1));
   }

   protected void throwSetIllegalArgumentException(char var1) {
      this.throwSetIllegalArgumentException("char", Character.toString(var1));
   }

   protected void throwSetIllegalArgumentException(short var1) {
      this.throwSetIllegalArgumentException("short", Short.toString(var1));
   }

   protected void throwSetIllegalArgumentException(int var1) {
      this.throwSetIllegalArgumentException("int", Integer.toString(var1));
   }

   protected void throwSetIllegalArgumentException(long var1) {
      this.throwSetIllegalArgumentException("long", Long.toString(var1));
   }

   protected void throwSetIllegalArgumentException(float var1) {
      this.throwSetIllegalArgumentException("float", Float.toString(var1));
   }

   protected void throwSetIllegalArgumentException(double var1) {
      this.throwSetIllegalArgumentException("double", Double.toString(var1));
   }
}
