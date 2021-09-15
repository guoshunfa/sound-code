package sun.reflect;

abstract class FieldAccessorImpl extends MagicAccessorImpl implements FieldAccessor {
   public abstract Object get(Object var1) throws IllegalArgumentException;

   public abstract boolean getBoolean(Object var1) throws IllegalArgumentException;

   public abstract byte getByte(Object var1) throws IllegalArgumentException;

   public abstract char getChar(Object var1) throws IllegalArgumentException;

   public abstract short getShort(Object var1) throws IllegalArgumentException;

   public abstract int getInt(Object var1) throws IllegalArgumentException;

   public abstract long getLong(Object var1) throws IllegalArgumentException;

   public abstract float getFloat(Object var1) throws IllegalArgumentException;

   public abstract double getDouble(Object var1) throws IllegalArgumentException;

   public abstract void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException;

   public abstract void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException;
}
