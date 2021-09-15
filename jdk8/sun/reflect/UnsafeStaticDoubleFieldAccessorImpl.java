package sun.reflect;

import java.lang.reflect.Field;

class UnsafeStaticDoubleFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl {
   UnsafeStaticDoubleFieldAccessorImpl(Field var1) {
      super(var1);
   }

   public Object get(Object var1) throws IllegalArgumentException {
      return new Double(this.getDouble(var1));
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
      throw this.newGetFloatIllegalArgumentException();
   }

   public double getDouble(Object var1) throws IllegalArgumentException {
      return unsafe.getDouble(this.base, this.fieldOffset);
   }

   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      if (var2 == null) {
         this.throwSetIllegalArgumentException(var2);
      }

      if (var2 instanceof Byte) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Byte)var2);
      } else if (var2 instanceof Short) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Short)var2);
      } else if (var2 instanceof Character) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Character)var2);
      } else if (var2 instanceof Integer) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Integer)var2);
      } else if (var2 instanceof Long) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Long)var2);
      } else if (var2 instanceof Float) {
         unsafe.putDouble(this.base, this.fieldOffset, (double)(Float)var2);
      } else if (var2 instanceof Double) {
         unsafe.putDouble(this.base, this.fieldOffset, (Double)var2);
      } else {
         this.throwSetIllegalArgumentException(var2);
      }
   }

   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      unsafe.putDouble(this.base, this.fieldOffset, var2);
   }
}
