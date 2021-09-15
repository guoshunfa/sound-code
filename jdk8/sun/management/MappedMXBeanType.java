package sun.management;

import com.sun.management.VMOption;
import java.io.InvalidObjectException;
import java.lang.management.LockInfo;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public abstract class MappedMXBeanType {
   private static final WeakHashMap<Type, MappedMXBeanType> convertedTypes = new WeakHashMap();
   boolean isBasicType = false;
   OpenType<?> openType;
   Class<?> mappedTypeClass;
   private static final String KEY = "key";
   private static final String VALUE = "value";
   private static final String[] mapIndexNames = new String[]{"key"};
   private static final String[] mapItemNames = new String[]{"key", "value"};
   private static final Class<?> COMPOSITE_DATA_CLASS = CompositeData.class;
   private static final OpenType<?> inProgress;
   private static final OpenType[] simpleTypes;

   public MappedMXBeanType() {
      this.openType = inProgress;
   }

   static synchronized MappedMXBeanType newMappedType(Type var0) throws OpenDataException {
      Object var1 = null;
      if (var0 instanceof Class) {
         Class var2 = (Class)var0;
         if (var2.isEnum()) {
            var1 = new MappedMXBeanType.EnumMXBeanType(var2);
         } else if (var2.isArray()) {
            var1 = new MappedMXBeanType.ArrayMXBeanType(var2);
         } else {
            var1 = new MappedMXBeanType.CompositeDataMXBeanType(var2);
         }
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var5 = (ParameterizedType)var0;
         Type var3 = var5.getRawType();
         if (var3 instanceof Class) {
            Class var4 = (Class)var3;
            if (var4 == List.class) {
               var1 = new MappedMXBeanType.ListMXBeanType(var5);
            } else if (var4 == Map.class) {
               var1 = new MappedMXBeanType.MapMXBeanType(var5);
            }
         }
      } else if (var0 instanceof GenericArrayType) {
         GenericArrayType var6 = (GenericArrayType)var0;
         var1 = new MappedMXBeanType.GenericArrayMXBeanType(var6);
      }

      if (var1 == null) {
         throw new OpenDataException(var0 + " is not a supported MXBean type.");
      } else {
         convertedTypes.put(var0, var1);
         return (MappedMXBeanType)var1;
      }
   }

   static synchronized MappedMXBeanType newBasicType(Class<?> var0, OpenType<?> var1) throws OpenDataException {
      MappedMXBeanType.BasicMXBeanType var2 = new MappedMXBeanType.BasicMXBeanType(var0, var1);
      convertedTypes.put(var0, var2);
      return var2;
   }

   static synchronized MappedMXBeanType getMappedType(Type var0) throws OpenDataException {
      MappedMXBeanType var1 = (MappedMXBeanType)convertedTypes.get(var0);
      if (var1 == null) {
         var1 = newMappedType(var0);
      }

      if (var1.getOpenType() instanceof MappedMXBeanType.InProgress) {
         throw new OpenDataException("Recursive data structure");
      } else {
         return var1;
      }
   }

   public static synchronized OpenType<?> toOpenType(Type var0) throws OpenDataException {
      MappedMXBeanType var1 = getMappedType(var0);
      return var1.getOpenType();
   }

   public static Object toJavaTypeData(Object var0, Type var1) throws OpenDataException, InvalidObjectException {
      if (var0 == null) {
         return null;
      } else {
         MappedMXBeanType var2 = getMappedType(var1);
         return var2.toJavaTypeData(var0);
      }
   }

   public static Object toOpenTypeData(Object var0, Type var1) throws OpenDataException {
      if (var0 == null) {
         return null;
      } else {
         MappedMXBeanType var2 = getMappedType(var1);
         return var2.toOpenTypeData(var0);
      }
   }

   OpenType<?> getOpenType() {
      return this.openType;
   }

   boolean isBasicType() {
      return this.isBasicType;
   }

   String getTypeName() {
      return this.getMappedTypeClass().getName();
   }

   Class<?> getMappedTypeClass() {
      return this.mappedTypeClass;
   }

   abstract Type getJavaType();

   abstract String getName();

   abstract Object toOpenTypeData(Object var1) throws OpenDataException;

   abstract Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException;

   private static String decapitalize(String var0) {
      if (var0 != null && var0.length() != 0) {
         if (var0.length() > 1 && Character.isUpperCase(var0.charAt(1)) && Character.isUpperCase(var0.charAt(0))) {
            return var0;
         } else {
            char[] var1 = var0.toCharArray();
            var1[0] = Character.toLowerCase(var1[0]);
            return new String(var1);
         }
      } else {
         return var0;
      }
   }

   static {
      MappedMXBeanType.InProgress var0;
      try {
         var0 = new MappedMXBeanType.InProgress();
      } catch (OpenDataException var9) {
         throw new AssertionError(var9);
      }

      inProgress = var0;
      simpleTypes = new OpenType[]{SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID};

      try {
         for(int var11 = 0; var11 < simpleTypes.length; ++var11) {
            OpenType var1 = simpleTypes[var11];

            Class var2;
            try {
               var2 = Class.forName(var1.getClassName(), false, MappedMXBeanType.class.getClassLoader());
               newBasicType(var2, var1);
            } catch (ClassNotFoundException var7) {
               throw new AssertionError(var7);
            } catch (OpenDataException var8) {
               throw new AssertionError(var8);
            }

            if (var2.getName().startsWith("java.lang.")) {
               try {
                  Field var3 = var2.getField("TYPE");
                  Class var4 = (Class)var3.get((Object)null);
                  newBasicType(var4, var1);
               } catch (NoSuchFieldException var5) {
               } catch (IllegalAccessException var6) {
                  throw new AssertionError(var6);
               }
            }
         }

      } catch (OpenDataException var10) {
         throw new AssertionError(var10);
      }
   }

   private static class InProgress extends OpenType {
      private static final String description = "Marker to detect recursive type use -- internal use only!";
      private static final long serialVersionUID = -3413063475064374490L;

      InProgress() throws OpenDataException {
         super("java.lang.String", "java.lang.String", "Marker to detect recursive type use -- internal use only!");
      }

      public String toString() {
         return "Marker to detect recursive type use -- internal use only!";
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         return false;
      }

      public boolean isValue(Object var1) {
         return false;
      }
   }

   static class CompositeDataMXBeanType extends MappedMXBeanType {
      final Class<?> javaClass;
      final boolean isCompositeData;
      Method fromMethod = null;

      CompositeDataMXBeanType(Class<?> var1) throws OpenDataException {
         this.javaClass = var1;
         this.mappedTypeClass = MappedMXBeanType.COMPOSITE_DATA_CLASS;

         try {
            this.fromMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
               public Method run() throws NoSuchMethodException {
                  return CompositeDataMXBeanType.this.javaClass.getMethod("from", MappedMXBeanType.COMPOSITE_DATA_CLASS);
               }
            });
         } catch (PrivilegedActionException var10) {
         }

         if (MappedMXBeanType.COMPOSITE_DATA_CLASS.isAssignableFrom(var1)) {
            this.isCompositeData = true;
            this.openType = null;
         } else {
            this.isCompositeData = false;
            Method[] var2 = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
               public Method[] run() {
                  return CompositeDataMXBeanType.this.javaClass.getMethods();
               }
            });
            ArrayList var3 = new ArrayList();
            ArrayList var4 = new ArrayList();

            for(int var5 = 0; var5 < var2.length; ++var5) {
               Method var6 = var2[var5];
               String var7 = var6.getName();
               Type var8 = var6.getGenericReturnType();
               String var9;
               if (var7.startsWith("get")) {
                  var9 = var7.substring(3);
               } else {
                  if (!var7.startsWith("is") || !(var8 instanceof Class) || (Class)var8 != Boolean.TYPE) {
                     continue;
                  }

                  var9 = var7.substring(2);
               }

               if (!var9.equals("") && var6.getParameterTypes().length <= 0 && var8 != Void.TYPE && !var9.equals("Class")) {
                  var3.add(MappedMXBeanType.decapitalize(var9));
                  var4.add(toOpenType(var8));
               }
            }

            String[] var11 = (String[])var3.toArray(new String[0]);
            this.openType = new CompositeType(var1.getName(), var1.getName(), var11, var11, (OpenType[])var4.toArray(new OpenType[0]));
         }

      }

      Type getJavaType() {
         return this.javaClass;
      }

      String getName() {
         return this.javaClass.getName();
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         if (var1 instanceof MemoryUsage) {
            return MemoryUsageCompositeData.toCompositeData((MemoryUsage)var1);
         } else if (var1 instanceof ThreadInfo) {
            return ThreadInfoCompositeData.toCompositeData((ThreadInfo)var1);
         } else if (var1 instanceof LockInfo) {
            return var1 instanceof MonitorInfo ? MonitorInfoCompositeData.toCompositeData((MonitorInfo)var1) : LockInfoCompositeData.toCompositeData((LockInfo)var1);
         } else if (var1 instanceof MemoryNotificationInfo) {
            return MemoryNotifInfoCompositeData.toCompositeData((MemoryNotificationInfo)var1);
         } else if (var1 instanceof VMOption) {
            return VMOptionCompositeData.toCompositeData((VMOption)var1);
         } else if (this.isCompositeData) {
            CompositeData var2 = (CompositeData)var1;
            CompositeType var3 = var2.getCompositeType();
            String[] var4 = (String[])var3.keySet().toArray(new String[0]);
            Object[] var5 = var2.getAll(var4);
            return new CompositeDataSupport(var3, var4, var5);
         } else {
            throw new OpenDataException(this.javaClass.getName() + " is not supported for platform MXBeans");
         }
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         if (this.fromMethod == null) {
            throw new AssertionError("Does not support data conversion");
         } else {
            try {
               return this.fromMethod.invoke((Object)null, var1);
            } catch (IllegalAccessException var4) {
               throw new AssertionError(var4);
            } catch (InvocationTargetException var5) {
               OpenDataException var3 = new OpenDataException("Failed to invoke " + this.fromMethod.getName() + " to convert CompositeData  to " + this.javaClass.getName());
               var3.initCause(var5);
               throw var3;
            }
         }
      }
   }

   static class MapMXBeanType extends MappedMXBeanType {
      final ParameterizedType javaType;
      final MappedMXBeanType keyType;
      final MappedMXBeanType valueType;
      final String typeName;

      MapMXBeanType(ParameterizedType var1) throws OpenDataException {
         this.javaType = var1;
         Type[] var2 = var1.getActualTypeArguments();

         assert var2.length == 2;

         this.keyType = getMappedType(var2[0]);
         this.valueType = getMappedType(var2[1]);
         this.typeName = "Map<" + this.keyType.getName() + "," + this.valueType.getName() + ">";
         OpenType[] var3 = new OpenType[]{this.keyType.getOpenType(), this.valueType.getOpenType()};
         CompositeType var4 = new CompositeType(this.typeName, this.typeName, MappedMXBeanType.mapItemNames, MappedMXBeanType.mapItemNames, var3);
         this.openType = new TabularType(this.typeName, this.typeName, var4, MappedMXBeanType.mapIndexNames);
         this.mappedTypeClass = TabularData.class;
      }

      Type getJavaType() {
         return this.javaType;
      }

      String getName() {
         return this.typeName;
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         Map var2 = (Map)var1;
         TabularType var3 = (TabularType)this.openType;
         TabularDataSupport var4 = new TabularDataSupport(var3);
         CompositeType var5 = var3.getRowType();
         Iterator var6 = var2.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            Object var8 = this.keyType.toOpenTypeData(var7.getKey());
            Object var9 = this.valueType.toOpenTypeData(var7.getValue());
            CompositeDataSupport var10 = new CompositeDataSupport(var5, MappedMXBeanType.mapItemNames, new Object[]{var8, var9});
            var4.put(var10);
         }

         return var4;
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         TabularData var2 = (TabularData)var1;
         HashMap var3 = new HashMap();
         Iterator var4 = var2.values().iterator();

         while(var4.hasNext()) {
            CompositeData var5 = (CompositeData)var4.next();
            Object var6 = this.keyType.toJavaTypeData(var5.get("key"));
            Object var7 = this.valueType.toJavaTypeData(var5.get("value"));
            var3.put(var6, var7);
         }

         return var3;
      }
   }

   static class ListMXBeanType extends MappedMXBeanType {
      final ParameterizedType javaType;
      final MappedMXBeanType paramType;
      final String typeName;

      ListMXBeanType(ParameterizedType var1) throws OpenDataException {
         this.javaType = var1;
         Type[] var2 = var1.getActualTypeArguments();

         assert var2.length == 1;

         if (!(var2[0] instanceof Class)) {
            throw new OpenDataException("Element Type for " + var1 + " not supported");
         } else {
            Class var3 = (Class)var2[0];
            if (var3.isArray()) {
               throw new OpenDataException("Element Type for " + var1 + " not supported");
            } else {
               this.paramType = getMappedType(var3);
               this.typeName = "List<" + this.paramType.getName() + ">";

               try {
                  this.mappedTypeClass = Class.forName("[L" + this.paramType.getTypeName() + ";");
               } catch (ClassNotFoundException var6) {
                  OpenDataException var5 = new OpenDataException("Array class not found");
                  var5.initCause(var6);
                  throw var5;
               }

               this.openType = new ArrayType(1, this.paramType.getOpenType());
            }
         }
      }

      Type getJavaType() {
         return this.javaType;
      }

      String getName() {
         return this.typeName;
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         List var2 = (List)var1;
         Object[] var3 = (Object[])((Object[])Array.newInstance(this.paramType.getMappedTypeClass(), var2.size()));
         int var4 = 0;

         Object var6;
         for(Iterator var5 = var2.iterator(); var5.hasNext(); var3[var4++] = this.paramType.toOpenTypeData(var6)) {
            var6 = var5.next();
         }

         return var3;
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         Object[] var2 = (Object[])((Object[])var1);
         ArrayList var3 = new ArrayList(var2.length);
         Object[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var4[var6];
            var3.add(this.paramType.toJavaTypeData(var7));
         }

         return var3;
      }
   }

   static class GenericArrayMXBeanType extends MappedMXBeanType.ArrayMXBeanType {
      final GenericArrayType gtype;

      GenericArrayMXBeanType(GenericArrayType var1) throws OpenDataException {
         this.gtype = var1;
         this.componentType = getMappedType(var1.getGenericComponentType());
         StringBuilder var2 = new StringBuilder();
         Object var3 = var1;

         int var4;
         for(var4 = 0; var3 instanceof GenericArrayType; ++var4) {
            var2.append('[');
            GenericArrayType var5 = (GenericArrayType)var3;
            var3 = var5.getGenericComponentType();
         }

         this.baseElementType = getMappedType((Type)var3);
         if (var3 instanceof Class && ((Class)var3).isPrimitive()) {
            var2 = new StringBuilder(var1.toString());
         } else {
            var2.append("L" + this.baseElementType.getTypeName() + ";");
         }

         try {
            this.mappedTypeClass = Class.forName(var2.toString());
         } catch (ClassNotFoundException var7) {
            OpenDataException var6 = new OpenDataException("Cannot obtain array class");
            var6.initCause(var7);
            throw var6;
         }

         this.openType = new ArrayType(var4, this.baseElementType.getOpenType());
      }

      Type getJavaType() {
         return this.gtype;
      }

      String getName() {
         return this.gtype.toString();
      }
   }

   static class ArrayMXBeanType extends MappedMXBeanType {
      final Class<?> arrayClass;
      protected MappedMXBeanType componentType;
      protected MappedMXBeanType baseElementType;

      ArrayMXBeanType(Class<?> var1) throws OpenDataException {
         this.arrayClass = var1;
         this.componentType = getMappedType(var1.getComponentType());
         StringBuilder var2 = new StringBuilder();
         Class var3 = var1;

         int var4;
         for(var4 = 0; var3.isArray(); ++var4) {
            var2.append('[');
            var3 = var3.getComponentType();
         }

         this.baseElementType = getMappedType(var3);
         if (var3.isPrimitive()) {
            var2 = new StringBuilder(var1.getName());
         } else {
            var2.append("L" + this.baseElementType.getTypeName() + ";");
         }

         try {
            this.mappedTypeClass = Class.forName(var2.toString());
         } catch (ClassNotFoundException var7) {
            OpenDataException var6 = new OpenDataException("Cannot obtain array class");
            var6.initCause(var7);
            throw var6;
         }

         this.openType = new ArrayType(var4, this.baseElementType.getOpenType());
      }

      protected ArrayMXBeanType() {
         this.arrayClass = null;
      }

      Type getJavaType() {
         return this.arrayClass;
      }

      String getName() {
         return this.arrayClass.getName();
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         if (this.baseElementType.isBasicType()) {
            return var1;
         } else {
            Object[] var2 = (Object[])((Object[])var1);
            Object[] var3 = (Object[])((Object[])Array.newInstance(this.componentType.getMappedTypeClass(), var2.length));
            int var4 = 0;
            Object[] var5 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Object var8 = var5[var7];
               if (var8 == null) {
                  var3[var4] = null;
               } else {
                  var3[var4] = this.componentType.toOpenTypeData(var8);
               }

               ++var4;
            }

            return var3;
         }
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         if (this.baseElementType.isBasicType()) {
            return var1;
         } else {
            Object[] var2 = (Object[])((Object[])var1);
            Object[] var3 = (Object[])((Object[])Array.newInstance((Class)this.componentType.getJavaType(), var2.length));
            int var4 = 0;
            Object[] var5 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Object var8 = var5[var7];
               if (var8 == null) {
                  var3[var4] = null;
               } else {
                  var3[var4] = this.componentType.toJavaTypeData(var8);
               }

               ++var4;
            }

            return var3;
         }
      }
   }

   static class EnumMXBeanType extends MappedMXBeanType {
      final Class enumClass;

      EnumMXBeanType(Class<?> var1) {
         this.enumClass = var1;
         this.openType = SimpleType.STRING;
         this.mappedTypeClass = String.class;
      }

      Type getJavaType() {
         return this.enumClass;
      }

      String getName() {
         return this.enumClass.getName();
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         return ((Enum)var1).name();
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         try {
            return Enum.valueOf(this.enumClass, (String)var1);
         } catch (IllegalArgumentException var4) {
            InvalidObjectException var3 = new InvalidObjectException("Enum constant named " + (String)var1 + " is missing");
            var3.initCause(var4);
            throw var3;
         }
      }
   }

   static class BasicMXBeanType extends MappedMXBeanType {
      final Class<?> basicType;

      BasicMXBeanType(Class<?> var1, OpenType<?> var2) {
         this.basicType = var1;
         this.openType = var2;
         this.mappedTypeClass = var1;
         this.isBasicType = true;
      }

      Type getJavaType() {
         return this.basicType;
      }

      String getName() {
         return this.basicType.getName();
      }

      Object toOpenTypeData(Object var1) throws OpenDataException {
         return var1;
      }

      Object toJavaTypeData(Object var1) throws OpenDataException, InvalidObjectException {
         return var1;
      }
   }
}
