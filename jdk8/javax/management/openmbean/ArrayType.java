package javax.management.openmbean;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;

public class ArrayType<T> extends OpenType<T> {
   static final long serialVersionUID = 720504429830309770L;
   private int dimension;
   private OpenType<?> elementType;
   private boolean primitiveArray;
   private transient Integer myHashCode = null;
   private transient String myToString = null;
   private static final int PRIMITIVE_WRAPPER_NAME_INDEX = 0;
   private static final int PRIMITIVE_TYPE_NAME_INDEX = 1;
   private static final int PRIMITIVE_TYPE_KEY_INDEX = 2;
   private static final int PRIMITIVE_OPEN_TYPE_INDEX = 3;
   private static final Object[][] PRIMITIVE_ARRAY_TYPES;

   static boolean isPrimitiveContentType(String var0) {
      Object[][] var1 = PRIMITIVE_ARRAY_TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object[] var4 = var1[var3];
         if (var4[2].equals(var0)) {
            return true;
         }
      }

      return false;
   }

   static String getPrimitiveTypeKey(String var0) {
      Object[][] var1 = PRIMITIVE_ARRAY_TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object[] var4 = var1[var3];
         if (var0.equals(var4[0])) {
            return (String)var4[2];
         }
      }

      return null;
   }

   static String getPrimitiveTypeName(String var0) {
      Object[][] var1 = PRIMITIVE_ARRAY_TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object[] var4 = var1[var3];
         if (var0.equals(var4[0])) {
            return (String)var4[1];
         }
      }

      return null;
   }

   static SimpleType<?> getPrimitiveOpenType(String var0) {
      Object[][] var1 = PRIMITIVE_ARRAY_TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object[] var4 = var1[var3];
         if (var0.equals(var4[1])) {
            return (SimpleType)var4[3];
         }
      }

      return null;
   }

   public ArrayType(int var1, OpenType<?> var2) throws OpenDataException {
      super(buildArrayClassName(var1, var2), buildArrayClassName(var1, var2), buildArrayDescription(var1, var2));
      if (var2.isArray()) {
         ArrayType var3 = (ArrayType)var2;
         this.dimension = var3.getDimension() + var1;
         this.elementType = var3.getElementOpenType();
         this.primitiveArray = var3.isPrimitiveArray();
      } else {
         this.dimension = var1;
         this.elementType = var2;
         this.primitiveArray = false;
      }

   }

   public ArrayType(SimpleType<?> var1, boolean var2) throws OpenDataException {
      super(buildArrayClassName(1, var1, var2), buildArrayClassName(1, var1, var2), buildArrayDescription(1, var1, var2), true);
      this.dimension = 1;
      this.elementType = var1;
      this.primitiveArray = var2;
   }

   ArrayType(String var1, String var2, String var3, int var4, OpenType<?> var5, boolean var6) {
      super(var1, var2, var3, true);
      this.dimension = var4;
      this.elementType = var5;
      this.primitiveArray = var6;
   }

   private static String buildArrayClassName(int var0, OpenType<?> var1) throws OpenDataException {
      boolean var2 = false;
      if (var1.isArray()) {
         var2 = ((ArrayType)var1).isPrimitiveArray();
      }

      return buildArrayClassName(var0, var1, var2);
   }

   private static String buildArrayClassName(int var0, OpenType<?> var1, boolean var2) throws OpenDataException {
      if (var0 < 1) {
         throw new IllegalArgumentException("Value of argument dimension must be greater than 0");
      } else {
         StringBuilder var3 = new StringBuilder();
         String var4 = var1.getClassName();

         for(int var5 = 1; var5 <= var0; ++var5) {
            var3.append('[');
         }

         if (var1.isArray()) {
            var3.append(var4);
         } else if (var2) {
            String var6 = getPrimitiveTypeKey(var4);
            if (var6 == null) {
               throw new OpenDataException("Element type is not primitive: " + var4);
            }

            var3.append(var6);
         } else {
            var3.append("L");
            var3.append(var4);
            var3.append(';');
         }

         return var3.toString();
      }
   }

   private static String buildArrayDescription(int var0, OpenType<?> var1) throws OpenDataException {
      boolean var2 = false;
      if (var1.isArray()) {
         var2 = ((ArrayType)var1).isPrimitiveArray();
      }

      return buildArrayDescription(var0, var1, var2);
   }

   private static String buildArrayDescription(int var0, OpenType<?> var1, boolean var2) throws OpenDataException {
      if (var1.isArray()) {
         ArrayType var3 = (ArrayType)var1;
         var0 += var3.getDimension();
         var1 = var3.getElementOpenType();
         var2 = var3.isPrimitiveArray();
      }

      StringBuilder var6 = new StringBuilder(var0 + "-dimension array of ");
      String var4 = var1.getClassName();
      if (var2) {
         String var5 = getPrimitiveTypeName(var4);
         if (var5 == null) {
            throw new OpenDataException("Element is not a primitive type: " + var4);
         }

         var6.append(var5);
      } else {
         var6.append(var4);
      }

      return var6.toString();
   }

   public int getDimension() {
      return this.dimension;
   }

   public OpenType<?> getElementOpenType() {
      return this.elementType;
   }

   public boolean isPrimitiveArray() {
      return this.primitiveArray;
   }

   public boolean isValue(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         String var3 = var2.getName();
         if (!var2.isArray()) {
            return false;
         } else if (this.getClassName().equals(var3)) {
            return true;
         } else if (!this.elementType.getClassName().equals(TabularData.class.getName()) && !this.elementType.getClassName().equals(CompositeData.class.getName())) {
            return false;
         } else {
            boolean var4 = this.elementType.getClassName().equals(TabularData.class.getName());
            int[] var5 = new int[this.getDimension()];
            Class var6 = var4 ? TabularData.class : CompositeData.class;
            Class var7 = Array.newInstance(var6, var5).getClass();
            if (!var7.isAssignableFrom(var2)) {
               return false;
            } else {
               return this.checkElementsType((Object[])((Object[])var1), this.dimension);
            }
         }
      }
   }

   private boolean checkElementsType(Object[] var1, int var2) {
      int var3;
      if (var2 > 1) {
         for(var3 = 0; var3 < var1.length; ++var3) {
            if (!this.checkElementsType((Object[])((Object[])var1[var3]), var2 - 1)) {
               return false;
            }
         }

         return true;
      } else {
         for(var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != null && !this.getElementOpenType().isValue(var1[var3])) {
               return false;
            }
         }

         return true;
      }
   }

   boolean isAssignableFrom(OpenType<?> var1) {
      if (!(var1 instanceof ArrayType)) {
         return false;
      } else {
         ArrayType var2 = (ArrayType)var1;
         return var2.getDimension() == this.getDimension() && var2.isPrimitiveArray() == this.isPrimitiveArray() && var2.getElementOpenType().isAssignableFrom(this.getElementOpenType());
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ArrayType)) {
         return false;
      } else {
         ArrayType var2 = (ArrayType)var1;
         if (this.dimension != var2.dimension) {
            return false;
         } else if (!this.elementType.equals(var2.elementType)) {
            return false;
         } else {
            return this.primitiveArray == var2.primitiveArray;
         }
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         byte var1 = 0;
         int var2 = var1 + this.dimension;
         var2 += this.elementType.hashCode();
         var2 += Boolean.valueOf(this.primitiveArray).hashCode();
         this.myHashCode = var2;
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = this.getClass().getName() + "(name=" + this.getTypeName() + ",dimension=" + this.dimension + ",elementType=" + this.elementType + ",primitiveArray=" + this.primitiveArray + ")";
      }

      return this.myToString;
   }

   public static <E> ArrayType<E[]> getArrayType(OpenType<E> var0) throws OpenDataException {
      return new ArrayType(1, var0);
   }

   public static <T> ArrayType<T> getPrimitiveArrayType(Class<T> var0) {
      if (!var0.isArray()) {
         throw new IllegalArgumentException("arrayClass must be an array");
      } else {
         int var1 = 1;

         Class var2;
         for(var2 = var0.getComponentType(); var2.isArray(); var2 = var2.getComponentType()) {
            ++var1;
         }

         String var3 = var2.getName();
         if (!var2.isPrimitive()) {
            throw new IllegalArgumentException("component type of the array must be a primitive type");
         } else {
            SimpleType var4 = getPrimitiveOpenType(var3);

            try {
               ArrayType var5 = new ArrayType(var4, true);
               if (var1 > 1) {
                  var5 = new ArrayType(var1 - 1, var5);
               }

               return var5;
            } catch (OpenDataException var6) {
               throw new IllegalArgumentException(var6);
            }
         }
      }
   }

   private Object readResolve() throws ObjectStreamException {
      return this.primitiveArray ? this.convertFromWrapperToPrimitiveTypes() : this;
   }

   private <T> ArrayType<T> convertFromWrapperToPrimitiveTypes() {
      String var1 = this.getClassName();
      String var2 = this.getTypeName();
      String var3 = this.getDescription();
      Object[][] var4 = PRIMITIVE_ARRAY_TYPES;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object[] var7 = var4[var6];
         if (var1.indexOf((String)var7[0]) != -1) {
            var1 = var1.replaceFirst("L" + var7[0] + ";", (String)var7[2]);
            var2 = var2.replaceFirst("L" + var7[0] + ";", (String)var7[2]);
            var3 = var3.replaceFirst((String)var7[0], (String)var7[1]);
            break;
         }
      }

      return new ArrayType(var1, var2, var3, this.dimension, this.elementType, this.primitiveArray);
   }

   private Object writeReplace() throws ObjectStreamException {
      return this.primitiveArray ? this.convertFromPrimitiveToWrapperTypes() : this;
   }

   private <T> ArrayType<T> convertFromPrimitiveToWrapperTypes() {
      String var1 = this.getClassName();
      String var2 = this.getTypeName();
      String var3 = this.getDescription();
      Object[][] var4 = PRIMITIVE_ARRAY_TYPES;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object[] var7 = var4[var6];
         if (var1.indexOf((String)var7[2]) != -1) {
            var1 = var1.replaceFirst((String)var7[2], "L" + var7[0] + ";");
            var2 = var2.replaceFirst((String)var7[2], "L" + var7[0] + ";");
            var3 = var3.replaceFirst((String)var7[1], (String)var7[0]);
            break;
         }
      }

      return new ArrayType(var1, var2, var3, this.dimension, this.elementType, this.primitiveArray);
   }

   static {
      PRIMITIVE_ARRAY_TYPES = new Object[][]{{Boolean.class.getName(), Boolean.TYPE.getName(), "Z", SimpleType.BOOLEAN}, {Character.class.getName(), Character.TYPE.getName(), "C", SimpleType.CHARACTER}, {Byte.class.getName(), Byte.TYPE.getName(), "B", SimpleType.BYTE}, {Short.class.getName(), Short.TYPE.getName(), "S", SimpleType.SHORT}, {Integer.class.getName(), Integer.TYPE.getName(), "I", SimpleType.INTEGER}, {Long.class.getName(), Long.TYPE.getName(), "J", SimpleType.LONG}, {Float.class.getName(), Float.TYPE.getName(), "F", SimpleType.FLOAT}, {Double.class.getName(), Double.TYPE.getName(), "D", SimpleType.DOUBLE}};
   }
}
