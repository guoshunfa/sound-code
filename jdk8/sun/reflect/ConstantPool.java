package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

public class ConstantPool {
   private Object constantPoolOop;

   public int getSize() {
      return this.getSize0(this.constantPoolOop);
   }

   public Class<?> getClassAt(int var1) {
      return this.getClassAt0(this.constantPoolOop, var1);
   }

   public Class<?> getClassAtIfLoaded(int var1) {
      return this.getClassAtIfLoaded0(this.constantPoolOop, var1);
   }

   public Member getMethodAt(int var1) {
      return this.getMethodAt0(this.constantPoolOop, var1);
   }

   public Member getMethodAtIfLoaded(int var1) {
      return this.getMethodAtIfLoaded0(this.constantPoolOop, var1);
   }

   public Field getFieldAt(int var1) {
      return this.getFieldAt0(this.constantPoolOop, var1);
   }

   public Field getFieldAtIfLoaded(int var1) {
      return this.getFieldAtIfLoaded0(this.constantPoolOop, var1);
   }

   public String[] getMemberRefInfoAt(int var1) {
      return this.getMemberRefInfoAt0(this.constantPoolOop, var1);
   }

   public int getIntAt(int var1) {
      return this.getIntAt0(this.constantPoolOop, var1);
   }

   public long getLongAt(int var1) {
      return this.getLongAt0(this.constantPoolOop, var1);
   }

   public float getFloatAt(int var1) {
      return this.getFloatAt0(this.constantPoolOop, var1);
   }

   public double getDoubleAt(int var1) {
      return this.getDoubleAt0(this.constantPoolOop, var1);
   }

   public String getStringAt(int var1) {
      return this.getStringAt0(this.constantPoolOop, var1);
   }

   public String getUTF8At(int var1) {
      return this.getUTF8At0(this.constantPoolOop, var1);
   }

   private native int getSize0(Object var1);

   private native Class<?> getClassAt0(Object var1, int var2);

   private native Class<?> getClassAtIfLoaded0(Object var1, int var2);

   private native Member getMethodAt0(Object var1, int var2);

   private native Member getMethodAtIfLoaded0(Object var1, int var2);

   private native Field getFieldAt0(Object var1, int var2);

   private native Field getFieldAtIfLoaded0(Object var1, int var2);

   private native String[] getMemberRefInfoAt0(Object var1, int var2);

   private native int getIntAt0(Object var1, int var2);

   private native long getLongAt0(Object var1, int var2);

   private native float getFloatAt0(Object var1, int var2);

   private native double getDoubleAt0(Object var1, int var2);

   private native String getStringAt0(Object var1, int var2);

   private native String getUTF8At0(Object var1, int var2);

   static {
      Reflection.registerFieldsToFilter(ConstantPool.class, "constantPoolOop");
   }
}
