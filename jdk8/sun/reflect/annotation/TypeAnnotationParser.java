package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.ConstantPool;

public final class TypeAnnotationParser {
   private static final TypeAnnotation[] EMPTY_TYPE_ANNOTATION_ARRAY = new TypeAnnotation[0];
   private static final byte CLASS_TYPE_PARAMETER = 0;
   private static final byte METHOD_TYPE_PARAMETER = 1;
   private static final byte CLASS_EXTENDS = 16;
   private static final byte CLASS_TYPE_PARAMETER_BOUND = 17;
   private static final byte METHOD_TYPE_PARAMETER_BOUND = 18;
   private static final byte FIELD = 19;
   private static final byte METHOD_RETURN = 20;
   private static final byte METHOD_RECEIVER = 21;
   private static final byte METHOD_FORMAL_PARAMETER = 22;
   private static final byte THROWS = 23;
   private static final byte LOCAL_VARIABLE = 64;
   private static final byte RESOURCE_VARIABLE = 65;
   private static final byte EXCEPTION_PARAMETER = 66;
   private static final byte INSTANCEOF = 67;
   private static final byte NEW = 68;
   private static final byte CONSTRUCTOR_REFERENCE = 69;
   private static final byte METHOD_REFERENCE = 70;
   private static final byte CAST = 71;
   private static final byte CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
   private static final byte METHOD_INVOCATION_TYPE_ARGUMENT = 73;
   private static final byte CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
   private static final byte METHOD_REFERENCE_TYPE_ARGUMENT = 75;

   public static AnnotatedType buildAnnotatedType(byte[] var0, ConstantPool var1, AnnotatedElement var2, Class<?> var3, Type var4, TypeAnnotation.TypeAnnotationTarget var5) {
      TypeAnnotation[] var6 = parseTypeAnnotations(var0, var1, var2, var3);
      ArrayList var7 = new ArrayList(var6.length);
      TypeAnnotation[] var8 = var6;
      int var9 = var6.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         TypeAnnotation var11 = var8[var10];
         TypeAnnotation.TypeAnnotationTargetInfo var12 = var11.getTargetInfo();
         if (var12.getTarget() == var5) {
            var7.add(var11);
         }
      }

      var8 = (TypeAnnotation[])var7.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
      return AnnotatedTypeFactory.buildAnnotatedType(var4, TypeAnnotation.LocationInfo.BASE_LOCATION, var8, var8, var2);
   }

   public static AnnotatedType[] buildAnnotatedTypes(byte[] var0, ConstantPool var1, AnnotatedElement var2, Class<?> var3, Type[] var4, TypeAnnotation.TypeAnnotationTarget var5) {
      int var6 = var4.length;
      AnnotatedType[] var7 = new AnnotatedType[var6];
      Arrays.fill(var7, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
      ArrayList[] var8 = new ArrayList[var6];
      TypeAnnotation[] var9 = parseTypeAnnotations(var0, var1, var2, var3);
      TypeAnnotation[] var10 = var9;
      int var11 = var9.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         TypeAnnotation var13 = var10[var12];
         TypeAnnotation.TypeAnnotationTargetInfo var14 = var13.getTargetInfo();
         if (var14.getTarget() == var5) {
            int var15 = var14.getCount();
            ArrayList var16;
            if (var8[var15] == null) {
               var16 = new ArrayList(var9.length);
               var8[var15] = var16;
            }

            var16 = var8[var15];
            var16.add(var13);
         }
      }

      for(int var17 = 0; var17 < var6; ++var17) {
         ArrayList var18 = var8[var17];
         TypeAnnotation[] var19;
         if (var18 != null) {
            var19 = (TypeAnnotation[])var18.toArray(new TypeAnnotation[var18.size()]);
         } else {
            var19 = EMPTY_TYPE_ANNOTATION_ARRAY;
         }

         var7[var17] = AnnotatedTypeFactory.buildAnnotatedType(var4[var17], TypeAnnotation.LocationInfo.BASE_LOCATION, var19, var19, var2);
      }

      return var7;
   }

   public static AnnotatedType buildAnnotatedSuperclass(byte[] var0, ConstantPool var1, Class<?> var2) {
      Type var3 = var2.getGenericSuperclass();
      return var3 == null ? AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE : buildAnnotatedType(var0, var1, var2, var2, var3, TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
   }

   public static AnnotatedType[] buildAnnotatedInterfaces(byte[] var0, ConstantPool var1, Class<?> var2) {
      return var2 != Object.class && !var2.isArray() && !var2.isPrimitive() && var2 != Void.TYPE ? buildAnnotatedTypes(var0, var1, var2, var2, var2.getGenericInterfaces(), TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS) : AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE_ARRAY;
   }

   public static <D extends GenericDeclaration> Annotation[] parseTypeVariableAnnotations(D var0, int var1) {
      Object var2;
      TypeAnnotation.TypeAnnotationTarget var3;
      if (var0 instanceof Class) {
         var2 = (Class)var0;
         var3 = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER;
      } else {
         if (!(var0 instanceof Executable)) {
            throw new AssertionError("Unknown GenericDeclaration " + var0 + "\nthis should not happen.");
         }

         var2 = (Executable)var0;
         var3 = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER;
      }

      List var4 = TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)var2), var3);
      ArrayList var5 = new ArrayList(var4.size());
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         TypeAnnotation var7 = (TypeAnnotation)var6.next();
         if (var7.getTargetInfo().getCount() == var1) {
            var5.add(var7.getAnnotation());
         }
      }

      return (Annotation[])var5.toArray(new Annotation[0]);
   }

   public static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] var0, D var1, int var2) {
      return parseAnnotatedBounds(var0, var1, var2, TypeAnnotation.LocationInfo.BASE_LOCATION);
   }

   private static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] var0, D var1, int var2, TypeAnnotation.LocationInfo var3) {
      List var4 = fetchBounds(var1);
      if (var0 == null) {
         return new AnnotatedType[0];
      } else {
         byte var5 = 0;
         AnnotatedType[] var6 = new AnnotatedType[var0.length];
         if (var0.length > 0) {
            Type var7 = var0[0];
            if (!(var7 instanceof Class)) {
               var5 = 1;
            } else {
               Class var8 = (Class)var7;
               if (var8.isInterface()) {
                  var5 = 1;
               }
            }
         }

         for(int var12 = 0; var12 < var0.length; ++var12) {
            ArrayList var13 = new ArrayList(var4.size());
            Iterator var9 = var4.iterator();

            while(var9.hasNext()) {
               TypeAnnotation var10 = (TypeAnnotation)var9.next();
               TypeAnnotation.TypeAnnotationTargetInfo var11 = var10.getTargetInfo();
               if (var11.getSecondaryIndex() == var12 + var5 && var11.getCount() == var2) {
                  var13.add(var10);
               }
            }

            var6[var12] = AnnotatedTypeFactory.buildAnnotatedType(var0[var12], var3, (TypeAnnotation[])var13.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), (TypeAnnotation[])var4.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), var1);
         }

         return var6;
      }
   }

   private static <D extends GenericDeclaration> List<TypeAnnotation> fetchBounds(D var0) {
      Object var1;
      TypeAnnotation.TypeAnnotationTarget var2;
      if (var0 instanceof Class) {
         var2 = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND;
         var1 = (Class)var0;
      } else {
         var2 = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND;
         var1 = (Executable)var0;
      }

      return TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)var1), var2);
   }

   static TypeAnnotation[] parseAllTypeAnnotations(AnnotatedElement var0) {
      JavaLangAccess var3 = SharedSecrets.getJavaLangAccess();
      Class var1;
      byte[] var2;
      if (var0 instanceof Class) {
         var1 = (Class)var0;
         var2 = var3.getRawClassTypeAnnotations(var1);
      } else {
         if (!(var0 instanceof Executable)) {
            return EMPTY_TYPE_ANNOTATION_ARRAY;
         }

         var1 = ((Executable)var0).getDeclaringClass();
         var2 = var3.getRawExecutableTypeAnnotations((Executable)var0);
      }

      return parseTypeAnnotations(var2, var3.getConstantPool(var1), var0, var1);
   }

   private static TypeAnnotation[] parseTypeAnnotations(byte[] var0, ConstantPool var1, AnnotatedElement var2, Class<?> var3) {
      if (var0 == null) {
         return EMPTY_TYPE_ANNOTATION_ARRAY;
      } else {
         ByteBuffer var4 = ByteBuffer.wrap(var0);
         int var5 = var4.getShort() & '\uffff';
         ArrayList var6 = new ArrayList(var5);

         for(int var7 = 0; var7 < var5; ++var7) {
            TypeAnnotation var8 = parseTypeAnnotation(var4, var1, var2, var3);
            if (var8 != null) {
               var6.add(var8);
            }
         }

         return (TypeAnnotation[])var6.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
      }
   }

   static Map<Class<? extends Annotation>, Annotation> mapTypeAnnotations(TypeAnnotation[] var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      TypeAnnotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TypeAnnotation var5 = var2[var4];
         Annotation var6 = var5.getAnnotation();
         Class var7 = var6.annotationType();
         AnnotationType var8 = AnnotationType.getInstance(var7);
         if (var8.retention() == RetentionPolicy.RUNTIME && var1.put(var7, var6) != null) {
            throw new AnnotationFormatError("Duplicate annotation for class: " + var7 + ": " + var6);
         }
      }

      return var1;
   }

   private static TypeAnnotation parseTypeAnnotation(ByteBuffer var0, ConstantPool var1, AnnotatedElement var2, Class<?> var3) {
      try {
         TypeAnnotation.TypeAnnotationTargetInfo var4 = parseTargetInfo(var0);
         TypeAnnotation.LocationInfo var5 = TypeAnnotation.LocationInfo.parseLocationInfo(var0);
         Annotation var6 = AnnotationParser.parseAnnotation(var0, var1, var3, false);
         return var4 == null ? null : new TypeAnnotation(var4, var5, var6, var2);
      } catch (BufferUnderflowException | IllegalArgumentException var7) {
         throw new AnnotationFormatError(var7);
      }
   }

   private static TypeAnnotation.TypeAnnotationTargetInfo parseTargetInfo(ByteBuffer var0) {
      int var1 = var0.get() & 255;
      short var2;
      short var3;
      int var7;
      TypeAnnotation.TypeAnnotationTargetInfo var11;
      switch(var1) {
      case 0:
      case 1:
         var7 = var0.get() & 255;
         if (var1 == 0) {
            var11 = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER, var7);
         } else {
            var11 = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER, var7);
         }

         return var11;
      case 16:
         var2 = var0.getShort();
         if (var2 == -1) {
            return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
         } else if (var2 >= 0) {
            var11 = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS, var2);
            return var11;
         }
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      default:
         throw new AnnotationFormatError("Could not parse bytes for type annotations");
      case 17:
         return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND, var0);
      case 18:
         return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND, var0);
      case 19:
         return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.FIELD);
      case 20:
         return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
      case 21:
         return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
      case 22:
         var7 = var0.get() & 255;
         return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER, var7);
      case 23:
         return parseShortTarget(TypeAnnotation.TypeAnnotationTarget.THROWS, var0);
      case 64:
      case 65:
         var2 = var0.getShort();

         for(int var9 = 0; var9 < var2; ++var9) {
            short var10 = var0.getShort();
            short var5 = var0.getShort();
            short var6 = var0.getShort();
         }

         return null;
      case 66:
         byte var8 = var0.get();
         return null;
      case 67:
      case 68:
      case 69:
      case 70:
         var3 = var0.getShort();
         return null;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
         var3 = var0.getShort();
         byte var4 = var0.get();
         return null;
      }
   }

   private static TypeAnnotation.TypeAnnotationTargetInfo parseShortTarget(TypeAnnotation.TypeAnnotationTarget var0, ByteBuffer var1) {
      int var2 = var1.getShort() & '\uffff';
      return new TypeAnnotation.TypeAnnotationTargetInfo(var0, var2);
   }

   private static TypeAnnotation.TypeAnnotationTargetInfo parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget var0, ByteBuffer var1) {
      int var2 = var1.get() & 255;
      int var3 = var1.get() & 255;
      return new TypeAnnotation.TypeAnnotationTargetInfo(var0, var2, var3);
   }
}
