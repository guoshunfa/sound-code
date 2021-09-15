package javax.management.openmbean;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.ObjectName;

public final class SimpleType<T> extends OpenType<T> {
   static final long serialVersionUID = 2215577471957694503L;
   public static final SimpleType<Void> VOID = new SimpleType(Void.class);
   public static final SimpleType<Boolean> BOOLEAN = new SimpleType(Boolean.class);
   public static final SimpleType<Character> CHARACTER = new SimpleType(Character.class);
   public static final SimpleType<Byte> BYTE = new SimpleType(Byte.class);
   public static final SimpleType<Short> SHORT = new SimpleType(Short.class);
   public static final SimpleType<Integer> INTEGER = new SimpleType(Integer.class);
   public static final SimpleType<Long> LONG = new SimpleType(Long.class);
   public static final SimpleType<Float> FLOAT = new SimpleType(Float.class);
   public static final SimpleType<Double> DOUBLE = new SimpleType(Double.class);
   public static final SimpleType<String> STRING = new SimpleType(String.class);
   public static final SimpleType<BigDecimal> BIGDECIMAL = new SimpleType(BigDecimal.class);
   public static final SimpleType<BigInteger> BIGINTEGER = new SimpleType(BigInteger.class);
   public static final SimpleType<Date> DATE = new SimpleType(Date.class);
   public static final SimpleType<ObjectName> OBJECTNAME = new SimpleType(ObjectName.class);
   private static final SimpleType<?>[] typeArray;
   private transient Integer myHashCode = null;
   private transient String myToString = null;
   private static final Map<SimpleType<?>, SimpleType<?>> canonicalTypes;

   private SimpleType(Class<T> var1) {
      super(var1.getName(), var1.getName(), var1.getName(), false);
   }

   public boolean isValue(Object var1) {
      return var1 == null ? false : this.getClassName().equals(var1.getClass().getName());
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof SimpleType)) {
         return false;
      } else {
         SimpleType var2 = (SimpleType)var1;
         return this.getClassName().equals(var2.getClassName());
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         this.myHashCode = this.getClassName().hashCode();
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = this.getClass().getName() + "(name=" + this.getTypeName() + ")";
      }

      return this.myToString;
   }

   public Object readResolve() throws ObjectStreamException {
      SimpleType var1 = (SimpleType)canonicalTypes.get(this);
      if (var1 == null) {
         throw new InvalidObjectException("Invalid SimpleType: " + this);
      } else {
         return var1;
      }
   }

   static {
      typeArray = new SimpleType[]{VOID, BOOLEAN, CHARACTER, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, BIGDECIMAL, BIGINTEGER, DATE, OBJECTNAME};
      canonicalTypes = new HashMap();

      for(int var0 = 0; var0 < typeArray.length; ++var0) {
         SimpleType var1 = typeArray[var0];
         canonicalTypes.put(var1, var1);
      }

   }
}
