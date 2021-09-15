package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedFloatFieldAccessorImpl extends UnsafeQualifiedFieldAccessorImpl {
   UnsafeQualifiedFloatFieldAccessorImpl(Field var1, boolean var2) {
      super(var1, var2);
   }

   public Object get(Object var1) throws IllegalArgumentException {
      return new Float(this.getFloat(var1));
   }

   public boolean getBoolean(Object var1) throws IllegalArgumentException {
      throw this.newGetBooleanIllegalArgumentException();
   }

   public byte getByte(Object var1) throws IllegalArgumentException {
      throw this.newGetByteIllegalArgumentException();
   }

   public char getChar(Object var1) throws IllegalArgumentException {
      throw this.newGetCharIllegalArgumentException();
   }

   public short getShort(Object var1) throws IllegalArgumentException {
      throw this.newGetShortIllegalArgumentException();
   }

   public int getInt(Object var1) throws IllegalArgumentException {
      throw this.newGetIntIllegalArgumentException();
   }

   public long getLong(Object var1) throws IllegalArgumentException {
      throw this.newGetLongIllegalArgumentException();
   }

   public float getFloat(Object var1) throws IllegalArgumentException {
      this.ensureObj(var1);
      return unsafe.getFloatVolatile(var1, this.fieldOffset);
   }

   public double getDouble(Object var1) throws IllegalArgumentException {
      return (double)this.getFloat(var1);
   }

   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isReadOnly) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      if (var2 == null) {
         this.throwSetIllegalArgumentException(var2);
      }

      if (var2 instanceof Byte) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (float)(Byte)var2);
      } else if (var2 instanceof Short) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (float)(Short)var2);
      } else if (var2 instanceof Character) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (float)(Character)var2);
      } else if (var2 instanceof Integer) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (float)(Integer)var2);
      } else if (var2 instanceof Long) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (float)(Long)var2);
      } else if (var2 instanceof Float) {
         unsafe.putFloatVolatile(var1, this.fieldOffset, (Float)var2);
      } else {
         this.throwSetIllegalArgumentException(var2);
      }
   }

   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      this.setFloat(var1, (float)var2);
   }

   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      this.setFloat(var1, (float)var2);
   }

   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      this.setFloat(var1, (float)var2);
   }

   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      this.setFloat(var1, (float)var2);
   }

   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      this.setFloat(var1, (float)var2);
   }

   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isReadOnly) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      unsafe.putFloatVolatile(var1, this.fieldOffset, var2);
   }

   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }
}
