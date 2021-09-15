package javax.management.openmbean;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.management.Descriptor;
import javax.management.DescriptorRead;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class OpenMBeanAttributeInfoSupport extends MBeanAttributeInfo implements OpenMBeanAttributeInfo {
   static final long serialVersionUID = -4867215622149721849L;
   private OpenType<?> openType;
   private final Object defaultValue;
   private final Set<?> legalValues;
   private final Comparable<?> minValue;
   private final Comparable<?> maxValue;
   private transient Integer myHashCode;
   private transient String myToString;

   public OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<?> var3, boolean var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, var5, var6, (Descriptor)null);
   }

   public OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<?> var3, boolean var4, boolean var5, boolean var6, Descriptor var7) {
      super(var1, var3 == null ? null : var3.getClassName(), var2, var4, var5, var6, ImmutableDescriptor.union(var7, var3 == null ? null : var3.getDescriptor()));
      this.myHashCode = null;
      this.myToString = null;
      this.openType = var3;
      var7 = this.getDescriptor();
      this.defaultValue = valueFrom(var7, "defaultValue", var3);
      this.legalValues = valuesFrom(var7, "legalValues", var3);
      this.minValue = comparableValueFrom(var7, "minValue", var3);
      this.maxValue = comparableValueFrom(var7, "maxValue", var3);

      try {
         check(this);
      } catch (OpenDataException var9) {
         throw new IllegalArgumentException(var9.getMessage(), var9);
      }
   }

   public <T> OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<T> var3, boolean var4, boolean var5, boolean var6, T var7) throws OpenDataException {
      this(var1, var2, var3, var4, var5, var6, var7, (Object[])null);
   }

   public <T> OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<T> var3, boolean var4, boolean var5, boolean var6, T var7, T[] var8) throws OpenDataException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, (Comparable)null, (Comparable)null);
   }

   public <T> OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<T> var3, boolean var4, boolean var5, boolean var6, T var7, Comparable<T> var8, Comparable<T> var9) throws OpenDataException {
      this(var1, var2, var3, var4, var5, var6, var7, (Object[])null, var8, var9);
   }

   private <T> OpenMBeanAttributeInfoSupport(String var1, String var2, OpenType<T> var3, boolean var4, boolean var5, boolean var6, T var7, T[] var8, Comparable<T> var9, Comparable<T> var10) throws OpenDataException {
      super(var1, var3 == null ? null : var3.getClassName(), var2, var4, var5, var6, makeDescriptor(var3, var7, var8, var9, var10));
      this.myHashCode = null;
      this.myToString = null;
      this.openType = var3;
      Descriptor var11 = this.getDescriptor();
      this.defaultValue = var7;
      this.minValue = var9;
      this.maxValue = var10;
      this.legalValues = (Set)var11.getFieldValue("legalValues");
      check(this);
   }

   private Object readResolve() {
      if (this.getDescriptor().getFieldNames().length == 0) {
         OpenType var1 = (OpenType)cast(this.openType);
         Set var2 = (Set)cast(this.legalValues);
         Comparable var3 = (Comparable)cast(this.minValue);
         Comparable var4 = (Comparable)cast(this.maxValue);
         return new OpenMBeanAttributeInfoSupport(this.name, this.description, this.openType, this.isReadable(), this.isWritable(), this.isIs(), makeDescriptor(var1, this.defaultValue, var2, var3, var4));
      } else {
         return this;
      }
   }

   static void check(OpenMBeanParameterInfo var0) throws OpenDataException {
      OpenType var1 = var0.getOpenType();
      if (var1 == null) {
         throw new IllegalArgumentException("OpenType cannot be null");
      } else if (var0.getName() != null && !var0.getName().trim().equals("")) {
         if (var0.getDescription() != null && !var0.getDescription().trim().equals("")) {
            String var6;
            if (var0.hasDefaultValue()) {
               if (var1.isArray() || var1 instanceof TabularType) {
                  throw new OpenDataException("Default value not supported for ArrayType and TabularType");
               }

               if (!var1.isValue(var0.getDefaultValue())) {
                  var6 = "Argument defaultValue's class [\"" + var0.getDefaultValue().getClass().getName() + "\"] does not match the one defined in openType[\"" + var1.getClassName() + "\"]";
                  throw new OpenDataException(var6);
               }
            }

            if (!var0.hasLegalValues() || !var0.hasMinValue() && !var0.hasMaxValue()) {
               if (var0.hasMinValue() && !var1.isValue(var0.getMinValue())) {
                  var6 = "Type of minValue [" + var0.getMinValue().getClass().getName() + "] does not match OpenType [" + var1.getClassName() + "]";
                  throw new OpenDataException(var6);
               } else if (var0.hasMaxValue() && !var1.isValue(var0.getMaxValue())) {
                  var6 = "Type of maxValue [" + var0.getMaxValue().getClass().getName() + "] does not match OpenType [" + var1.getClassName() + "]";
                  throw new OpenDataException(var6);
               } else {
                  if (var0.hasDefaultValue()) {
                     Object var2 = var0.getDefaultValue();
                     if (var0.hasLegalValues() && !var0.getLegalValues().contains(var2)) {
                        throw new OpenDataException("defaultValue is not contained in legalValues");
                     }

                     if (var0.hasMinValue() && compare(var0.getMinValue(), var2) > 0) {
                        throw new OpenDataException("minValue cannot be greater than defaultValue");
                     }

                     if (var0.hasMaxValue() && compare(var0.getMaxValue(), var2) < 0) {
                        throw new OpenDataException("maxValue cannot be less than defaultValue");
                     }
                  }

                  if (var0.hasLegalValues()) {
                     if (var1 instanceof TabularType || var1.isArray()) {
                        throw new OpenDataException("Legal values not supported for TabularType and arrays");
                     }

                     Iterator var5 = var0.getLegalValues().iterator();

                     while(var5.hasNext()) {
                        Object var3 = var5.next();
                        if (!var1.isValue(var3)) {
                           String var4 = "Element of legalValues [" + var3 + "] is not a valid value for the specified openType [" + var1.toString() + "]";
                           throw new OpenDataException(var4);
                        }
                     }
                  }

                  if (var0.hasMinValue() && var0.hasMaxValue() && compare(var0.getMinValue(), var0.getMaxValue()) > 0) {
                     throw new OpenDataException("minValue cannot be greater than maxValue");
                  }
               }
            } else {
               throw new OpenDataException("cannot have both legalValue and minValue or maxValue");
            }
         } else {
            throw new IllegalArgumentException("Description cannot be null or empty");
         }
      } else {
         throw new IllegalArgumentException("Name cannot be null or empty");
      }
   }

   static int compare(Object var0, Object var1) {
      return ((Comparable)var0).compareTo(var1);
   }

   static <T> Descriptor makeDescriptor(OpenType<T> var0, T var1, T[] var2, Comparable<T> var3, Comparable<T> var4) {
      HashMap var5 = new HashMap();
      if (var1 != null) {
         var5.put("defaultValue", var1);
      }

      if (var2 != null) {
         HashSet var6 = new HashSet();
         Object[] var7 = var2;
         int var8 = var2.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Object var10 = var7[var9];
            var6.add(var10);
         }

         Set var11 = Collections.unmodifiableSet(var6);
         var5.put("legalValues", var11);
      }

      if (var3 != null) {
         var5.put("minValue", var3);
      }

      if (var4 != null) {
         var5.put("maxValue", var4);
      }

      if (var5.isEmpty()) {
         return var0.getDescriptor();
      } else {
         var5.put("openType", var0);
         return new ImmutableDescriptor(var5);
      }
   }

   static <T> Descriptor makeDescriptor(OpenType<T> var0, T var1, Set<T> var2, Comparable<T> var3, Comparable<T> var4) {
      Object[] var5;
      if (var2 == null) {
         var5 = null;
      } else {
         var5 = (Object[])cast(new Object[var2.size()]);
         var2.toArray(var5);
      }

      return makeDescriptor(var0, var1, var5, var3, var4);
   }

   static <T> T valueFrom(Descriptor var0, String var1, OpenType<T> var2) {
      Object var3 = var0.getFieldValue(var1);
      if (var3 == null) {
         return null;
      } else {
         try {
            return convertFrom(var3, var2);
         } catch (Exception var6) {
            String var5 = "Cannot convert descriptor field " + var1 + "  to " + var2.getTypeName();
            throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(var5), var6);
         }
      }
   }

   static <T> Set<T> valuesFrom(Descriptor var0, String var1, OpenType<T> var2) {
      Object var3 = var0.getFieldValue(var1);
      if (var3 == null) {
         return null;
      } else {
         Object var4;
         if (var3 instanceof Set) {
            Set var5 = (Set)var3;
            boolean var6 = true;
            Iterator var7 = var5.iterator();

            while(var7.hasNext()) {
               Object var8 = var7.next();
               if (!var2.isValue(var8)) {
                  var6 = false;
                  break;
               }
            }

            if (var6) {
               return (Set)cast(var5);
            }

            var4 = var5;
         } else {
            if (!(var3 instanceof Object[])) {
               String var10 = "Descriptor value for " + var1 + " must be a Set or an array: " + var3.getClass().getName();
               throw new IllegalArgumentException(var10);
            }

            var4 = Arrays.asList((Object[])((Object[])var3));
         }

         HashSet var9 = new HashSet();
         Iterator var11 = ((Collection)var4).iterator();

         while(var11.hasNext()) {
            Object var12 = var11.next();
            var9.add(convertFrom(var12, var2));
         }

         return var9;
      }
   }

   static <T> Comparable<?> comparableValueFrom(Descriptor var0, String var1, OpenType<T> var2) {
      Object var3 = valueFrom(var0, var1, var2);
      if (var3 != null && !(var3 instanceof Comparable)) {
         String var4 = "Descriptor field " + var1 + " with value " + var3 + " is not Comparable";
         throw new IllegalArgumentException(var4);
      } else {
         return (Comparable)var3;
      }
   }

   private static <T> T convertFrom(Object var0, OpenType<T> var1) {
      if (var1.isValue(var0)) {
         Object var2 = cast(var0);
         return var2;
      } else {
         return convertFromStrings(var0, var1);
      }
   }

   private static <T> T convertFromStrings(Object var0, OpenType<T> var1) {
      if (var1 instanceof ArrayType) {
         return convertFromStringArray(var0, var1);
      } else if (var0 instanceof String) {
         return convertFromString((String)var0, var1);
      } else {
         String var2 = "Cannot convert value " + var0 + " of type " + var0.getClass().getName() + " to type " + var1.getTypeName();
         throw new IllegalArgumentException(var2);
      }
   }

   private static <T> T convertFromString(String var0, OpenType<T> var1) {
      Class var2;
      try {
         String var3 = var1.safeGetClassName();
         ReflectUtil.checkPackageAccess(var3);
         var2 = (Class)cast(Class.forName(var3));
      } catch (ClassNotFoundException var10) {
         throw new NoClassDefFoundError(var10.toString());
      }

      Method var12;
      try {
         var12 = var2.getMethod("valueOf", String.class);
         if (!Modifier.isStatic(var12.getModifiers()) || var12.getReturnType() != var2) {
            var12 = null;
         }
      } catch (NoSuchMethodException var11) {
         var12 = null;
      }

      if (var12 != null) {
         try {
            return var2.cast(MethodUtil.invoke(var12, (Object)null, new Object[]{var0}));
         } catch (Exception var7) {
            String var5 = "Could not convert \"" + var0 + "\" using method: " + var12;
            throw new IllegalArgumentException(var5, var7);
         }
      } else {
         Constructor var4;
         try {
            var4 = var2.getConstructor(String.class);
         } catch (NoSuchMethodException var9) {
            var4 = null;
         }

         if (var4 != null) {
            try {
               return var4.newInstance(var0);
            } catch (Exception var8) {
               String var6 = "Could not convert \"" + var0 + "\" using constructor: " + var4;
               throw new IllegalArgumentException(var6, var8);
            }
         } else {
            throw new IllegalArgumentException("Don't know how to convert string to " + var1.getTypeName());
         }
      }
   }

   private static <T> T convertFromStringArray(Object var0, OpenType<T> var1) {
      ArrayType var2 = (ArrayType)var1;
      OpenType var3 = var2.getElementOpenType();
      int var4 = var2.getDimension();
      String var5 = "[";

      for(int var6 = 1; var6 < var4; ++var6) {
         var5 = var5 + "[";
      }

      Class var7;
      String var8;
      Class var16;
      try {
         var8 = var3.safeGetClassName();
         ReflectUtil.checkPackageAccess(var8);
         var16 = Class.forName(var5 + "Ljava.lang.String;");
         var7 = Class.forName(var5 + "L" + var8 + ";");
      } catch (ClassNotFoundException var15) {
         throw new NoClassDefFoundError(var15.toString());
      }

      if (!var16.isInstance(var0)) {
         var8 = "Value for " + var4 + "-dimensional array of " + var3.getTypeName() + " must be same type or a String array with same dimensions";
         throw new IllegalArgumentException(var8);
      } else {
         Object var17;
         if (var4 == 1) {
            var17 = var3;
         } else {
            try {
               var17 = new ArrayType(var4 - 1, var3);
            } catch (OpenDataException var14) {
               throw new IllegalArgumentException(var14.getMessage(), var14);
            }
         }

         int var9 = Array.getLength(var0);
         Object[] var10 = (Object[])((Object[])Array.newInstance(var7.getComponentType(), var9));

         for(int var11 = 0; var11 < var9; ++var11) {
            Object var12 = Array.get(var0, var11);
            Object var13 = convertFromStrings(var12, (OpenType)var17);
            Array.set(var10, var11, var13);
         }

         return cast(var10);
      }
   }

   static <T> T cast(Object var0) {
      return var0;
   }

   public OpenType<?> getOpenType() {
      return this.openType;
   }

   public Object getDefaultValue() {
      return this.defaultValue;
   }

   public Set<?> getLegalValues() {
      return this.legalValues;
   }

   public Comparable<?> getMinValue() {
      return this.minValue;
   }

   public Comparable<?> getMaxValue() {
      return this.maxValue;
   }

   public boolean hasDefaultValue() {
      return this.defaultValue != null;
   }

   public boolean hasLegalValues() {
      return this.legalValues != null;
   }

   public boolean hasMinValue() {
      return this.minValue != null;
   }

   public boolean hasMaxValue() {
      return this.maxValue != null;
   }

   public boolean isValue(Object var1) {
      return isValue(this, var1);
   }

   static boolean isValue(OpenMBeanParameterInfo var0, Object var1) {
      if (var0.hasDefaultValue() && var1 == null) {
         return true;
      } else {
         return var0.getOpenType().isValue(var1) && (!var0.hasLegalValues() || var0.getLegalValues().contains(var1)) && (!var0.hasMinValue() || var0.getMinValue().compareTo(var1) <= 0) && (!var0.hasMaxValue() || var0.getMaxValue().compareTo(var1) >= 0);
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof OpenMBeanAttributeInfo)) {
         return false;
      } else {
         OpenMBeanAttributeInfo var2 = (OpenMBeanAttributeInfo)var1;
         return this.isReadable() == var2.isReadable() && this.isWritable() == var2.isWritable() && this.isIs() == var2.isIs() && equal(this, var2);
      }
   }

   static boolean equal(OpenMBeanParameterInfo var0, OpenMBeanParameterInfo var1) {
      if (var0 instanceof DescriptorRead) {
         if (!(var1 instanceof DescriptorRead)) {
            return false;
         }

         Descriptor var2 = ((DescriptorRead)var0).getDescriptor();
         Descriptor var3 = ((DescriptorRead)var1).getDescriptor();
         if (!var2.equals(var3)) {
            return false;
         }
      } else if (var1 instanceof DescriptorRead) {
         return false;
      }

      boolean var10000;
      label73: {
         if (var0.getName().equals(var1.getName()) && var0.getOpenType().equals(var1.getOpenType())) {
            label67: {
               if (var0.hasDefaultValue()) {
                  if (!var0.getDefaultValue().equals(var1.getDefaultValue())) {
                     break label67;
                  }
               } else if (var1.hasDefaultValue()) {
                  break label67;
               }

               if (var0.hasMinValue()) {
                  if (!var0.getMinValue().equals(var1.getMinValue())) {
                     break label67;
                  }
               } else if (var1.hasMinValue()) {
                  break label67;
               }

               if (var0.hasMaxValue()) {
                  if (!var0.getMaxValue().equals(var1.getMaxValue())) {
                     break label67;
                  }
               } else if (var1.hasMaxValue()) {
                  break label67;
               }

               if (var0.hasLegalValues()) {
                  if (var0.getLegalValues().equals(var1.getLegalValues())) {
                     break label73;
                  }
               } else if (!var1.hasLegalValues()) {
                  break label73;
               }
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         this.myHashCode = hashCode(this);
      }

      return this.myHashCode;
   }

   static int hashCode(OpenMBeanParameterInfo var0) {
      byte var1 = 0;
      int var2 = var1 + var0.getName().hashCode();
      var2 += var0.getOpenType().hashCode();
      if (var0.hasDefaultValue()) {
         var2 += var0.getDefaultValue().hashCode();
      }

      if (var0.hasMinValue()) {
         var2 += var0.getMinValue().hashCode();
      }

      if (var0.hasMaxValue()) {
         var2 += var0.getMaxValue().hashCode();
      }

      if (var0.hasLegalValues()) {
         var2 += var0.getLegalValues().hashCode();
      }

      if (var0 instanceof DescriptorRead) {
         var2 += ((DescriptorRead)var0).getDescriptor().hashCode();
      }

      return var2;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = toString(this);
      }

      return this.myToString;
   }

   static String toString(OpenMBeanParameterInfo var0) {
      Descriptor var1 = var0 instanceof DescriptorRead ? ((DescriptorRead)var0).getDescriptor() : null;
      return var0.getClass().getName() + "(name=" + var0.getName() + ",openType=" + var0.getOpenType() + ",default=" + var0.getDefaultValue() + ",minValue=" + var0.getMinValue() + ",maxValue=" + var0.getMaxValue() + ",legalValues=" + var0.getLegalValues() + (var1 == null ? "" : ",descriptor=" + var1) + ")";
   }
}
