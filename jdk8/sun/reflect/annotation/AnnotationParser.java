package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import sun.reflect.ConstantPool;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class AnnotationParser {
   private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
   private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

   public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(byte[] var0, ConstantPool var1, Class<?> var2) {
      if (var0 == null) {
         return Collections.emptyMap();
      } else {
         try {
            return parseAnnotations2(var0, var1, var2, (Class[])null);
         } catch (BufferUnderflowException var4) {
            throw new AnnotationFormatError("Unexpected end of annotations.");
         } catch (IllegalArgumentException var5) {
            throw new AnnotationFormatError(var5);
         }
      }
   }

   @SafeVarargs
   static Map<Class<? extends Annotation>, Annotation> parseSelectAnnotations(byte[] var0, ConstantPool var1, Class<?> var2, Class<? extends Annotation>... var3) {
      if (var0 == null) {
         return Collections.emptyMap();
      } else {
         try {
            return parseAnnotations2(var0, var1, var2, var3);
         } catch (BufferUnderflowException var5) {
            throw new AnnotationFormatError("Unexpected end of annotations.");
         } catch (IllegalArgumentException var6) {
            throw new AnnotationFormatError(var6);
         }
      }
   }

   private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(byte[] var0, ConstantPool var1, Class<?> var2, Class<? extends Annotation>[] var3) {
      LinkedHashMap var4 = new LinkedHashMap();
      ByteBuffer var5 = ByteBuffer.wrap(var0);
      int var6 = var5.getShort() & '\uffff';

      for(int var7 = 0; var7 < var6; ++var7) {
         Annotation var8 = parseAnnotation2(var5, var1, var2, false, var3);
         if (var8 != null) {
            Class var9 = var8.annotationType();
            if (AnnotationType.getInstance(var9).retention() == RetentionPolicy.RUNTIME && var4.put(var9, var8) != null) {
               throw new AnnotationFormatError("Duplicate annotation for class: " + var9 + ": " + var8);
            }
         }
      }

      return var4;
   }

   public static Annotation[][] parseParameterAnnotations(byte[] var0, ConstantPool var1, Class<?> var2) {
      try {
         return parseParameterAnnotations2(var0, var1, var2);
      } catch (BufferUnderflowException var4) {
         throw new AnnotationFormatError("Unexpected end of parameter annotations.");
      } catch (IllegalArgumentException var5) {
         throw new AnnotationFormatError(var5);
      }
   }

   private static Annotation[][] parseParameterAnnotations2(byte[] var0, ConstantPool var1, Class<?> var2) {
      ByteBuffer var3 = ByteBuffer.wrap(var0);
      int var4 = var3.get() & 255;
      Annotation[][] var5 = new Annotation[var4][];

      for(int var6 = 0; var6 < var4; ++var6) {
         int var7 = var3.getShort() & '\uffff';
         ArrayList var8 = new ArrayList(var7);

         for(int var9 = 0; var9 < var7; ++var9) {
            Annotation var10 = parseAnnotation(var3, var1, var2, false);
            if (var10 != null) {
               AnnotationType var11 = AnnotationType.getInstance(var10.annotationType());
               if (var11.retention() == RetentionPolicy.RUNTIME) {
                  var8.add(var10);
               }
            }
         }

         var5[var6] = (Annotation[])var8.toArray(EMPTY_ANNOTATIONS_ARRAY);
      }

      return var5;
   }

   static Annotation parseAnnotation(ByteBuffer var0, ConstantPool var1, Class<?> var2, boolean var3) {
      return parseAnnotation2(var0, var1, var2, var3, (Class[])null);
   }

   private static Annotation parseAnnotation2(ByteBuffer var0, ConstantPool var1, Class<?> var2, boolean var3, Class<? extends Annotation>[] var4) {
      int var5 = var0.getShort() & '\uffff';
      Class var6 = null;
      String var7 = "[unknown]";

      try {
         try {
            var7 = var1.getUTF8At(var5);
            var6 = parseSig(var7, var2);
         } catch (IllegalArgumentException var18) {
            var6 = var1.getClassAt(var5);
         }
      } catch (NoClassDefFoundError var19) {
         if (var3) {
            throw new TypeNotPresentException(var7, var19);
         }

         skipAnnotation(var0, false);
         return null;
      } catch (TypeNotPresentException var20) {
         if (var3) {
            throw var20;
         }

         skipAnnotation(var0, false);
         return null;
      }

      if (var4 != null && !contains(var4, var6)) {
         skipAnnotation(var0, false);
         return null;
      } else {
         AnnotationType var8 = null;

         try {
            var8 = AnnotationType.getInstance(var6);
         } catch (IllegalArgumentException var17) {
            skipAnnotation(var0, false);
            return null;
         }

         Map var9 = var8.memberTypes();
         LinkedHashMap var10 = new LinkedHashMap(var8.memberDefaults());
         int var11 = var0.getShort() & '\uffff';

         for(int var12 = 0; var12 < var11; ++var12) {
            int var13 = var0.getShort() & '\uffff';
            String var14 = var1.getUTF8At(var13);
            Class var15 = (Class)var9.get(var14);
            if (var15 == null) {
               skipMemberValue(var0);
            } else {
               Object var16 = parseMemberValue(var15, var0, var1, var2);
               if (var16 instanceof AnnotationTypeMismatchExceptionProxy) {
                  ((AnnotationTypeMismatchExceptionProxy)var16).setMember((Method)var8.members().get(var14));
               }

               var10.put(var14, var16);
            }
         }

         return annotationForMap(var6, var10);
      }
   }

   public static Annotation annotationForMap(final Class<? extends Annotation> var0, final Map<String, Object> var1) {
      return (Annotation)AccessController.doPrivileged(new PrivilegedAction<Annotation>() {
         public Annotation run() {
            return (Annotation)Proxy.newProxyInstance(var0.getClassLoader(), new Class[]{var0}, new AnnotationInvocationHandler(var0, var1));
         }
      });
   }

   public static Object parseMemberValue(Class<?> var0, ByteBuffer var1, ConstantPool var2, Class<?> var3) {
      Object var4 = null;
      byte var5 = var1.get();
      switch(var5) {
      case 64:
         var4 = parseAnnotation(var1, var2, var3, true);
         break;
      case 91:
         return parseArray(var0, var1, var2, var3);
      case 99:
         var4 = parseClassValue(var1, var2, var3);
         break;
      case 101:
         return parseEnumValue(var0, var1, var2, var3);
      default:
         var4 = parseConst(var5, var1, var2);
      }

      if (!(var4 instanceof ExceptionProxy) && !var0.isInstance(var4)) {
         var4 = new AnnotationTypeMismatchExceptionProxy(var4.getClass() + "[" + var4 + "]");
      }

      return var4;
   }

   private static Object parseConst(int var0, ByteBuffer var1, ConstantPool var2) {
      int var3 = var1.getShort() & '\uffff';
      switch(var0) {
      case 66:
         return (byte)var2.getIntAt(var3);
      case 67:
         return (char)var2.getIntAt(var3);
      case 68:
         return var2.getDoubleAt(var3);
      case 70:
         return var2.getFloatAt(var3);
      case 73:
         return var2.getIntAt(var3);
      case 74:
         return var2.getLongAt(var3);
      case 83:
         return (short)var2.getIntAt(var3);
      case 90:
         return var2.getIntAt(var3) != 0;
      case 115:
         return var2.getUTF8At(var3);
      default:
         throw new AnnotationFormatError("Invalid member-value tag in annotation: " + var0);
      }
   }

   private static Object parseClassValue(ByteBuffer var0, ConstantPool var1, Class<?> var2) {
      int var3 = var0.getShort() & '\uffff';

      try {
         try {
            String var4 = var1.getUTF8At(var3);
            return parseSig(var4, var2);
         } catch (IllegalArgumentException var5) {
            return var1.getClassAt(var3);
         }
      } catch (NoClassDefFoundError var6) {
         return new TypeNotPresentExceptionProxy("[unknown]", var6);
      } catch (TypeNotPresentException var7) {
         return new TypeNotPresentExceptionProxy(var7.typeName(), var7.getCause());
      }
   }

   private static Class<?> parseSig(String var0, Class<?> var1) {
      if (var0.equals("V")) {
         return Void.TYPE;
      } else {
         SignatureParser var2 = SignatureParser.make();
         TypeSignature var3 = var2.parseTypeSig(var0);
         CoreReflectionFactory var4 = CoreReflectionFactory.make(var1, ClassScope.make(var1));
         Reifier var5 = Reifier.make(var4);
         var3.accept(var5);
         Type var6 = var5.getResult();
         return toClass(var6);
      }
   }

   static Class<?> toClass(Type var0) {
      return var0 instanceof GenericArrayType ? Array.newInstance(toClass(((GenericArrayType)var0).getGenericComponentType()), 0).getClass() : (Class)var0;
   }

   private static Object parseEnumValue(Class<? extends Enum> var0, ByteBuffer var1, ConstantPool var2, Class<?> var3) {
      int var4 = var1.getShort() & '\uffff';
      String var5 = var2.getUTF8At(var4);
      int var6 = var1.getShort() & '\uffff';
      String var7 = var2.getUTF8At(var6);
      if (!var5.endsWith(";")) {
         if (!var0.getName().equals(var5)) {
            return new AnnotationTypeMismatchExceptionProxy(var5 + "." + var7);
         }
      } else if (var0 != parseSig(var5, var3)) {
         return new AnnotationTypeMismatchExceptionProxy(var5 + "." + var7);
      }

      try {
         return Enum.valueOf(var0, var7);
      } catch (IllegalArgumentException var9) {
         return new EnumConstantNotPresentExceptionProxy(var0, var7);
      }
   }

   private static Object parseArray(Class<?> var0, ByteBuffer var1, ConstantPool var2, Class<?> var3) {
      int var4 = var1.getShort() & '\uffff';
      Class var5 = var0.getComponentType();
      if (var5 == Byte.TYPE) {
         return parseByteArray(var4, var1, var2);
      } else if (var5 == Character.TYPE) {
         return parseCharArray(var4, var1, var2);
      } else if (var5 == Double.TYPE) {
         return parseDoubleArray(var4, var1, var2);
      } else if (var5 == Float.TYPE) {
         return parseFloatArray(var4, var1, var2);
      } else if (var5 == Integer.TYPE) {
         return parseIntArray(var4, var1, var2);
      } else if (var5 == Long.TYPE) {
         return parseLongArray(var4, var1, var2);
      } else if (var5 == Short.TYPE) {
         return parseShortArray(var4, var1, var2);
      } else if (var5 == Boolean.TYPE) {
         return parseBooleanArray(var4, var1, var2);
      } else if (var5 == String.class) {
         return parseStringArray(var4, var1, var2);
      } else if (var5 == Class.class) {
         return parseClassArray(var4, var1, var2, var3);
      } else if (var5.isEnum()) {
         return parseEnumArray(var4, var5, var1, var2, var3);
      } else {
         assert var5.isAnnotation();

         return parseAnnotationArray(var4, var5, var1, var2, var3);
      }
   }

   private static Object parseByteArray(int var0, ByteBuffer var1, ConstantPool var2) {
      byte[] var3 = new byte[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 66) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = (byte)var2.getIntAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseCharArray(int var0, ByteBuffer var1, ConstantPool var2) {
      char[] var3 = new char[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 67) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = (char)var2.getIntAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseDoubleArray(int var0, ByteBuffer var1, ConstantPool var2) {
      double[] var3 = new double[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 68) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getDoubleAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseFloatArray(int var0, ByteBuffer var1, ConstantPool var2) {
      float[] var3 = new float[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 70) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getFloatAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseIntArray(int var0, ByteBuffer var1, ConstantPool var2) {
      int[] var3 = new int[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 73) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getIntAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseLongArray(int var0, ByteBuffer var1, ConstantPool var2) {
      long[] var3 = new long[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 74) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getLongAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseShortArray(int var0, ByteBuffer var1, ConstantPool var2) {
      short[] var3 = new short[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 83) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = (short)var2.getIntAt(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseBooleanArray(int var0, ByteBuffer var1, ConstantPool var2) {
      boolean[] var3 = new boolean[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 90) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getIntAt(var7) != 0;
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseStringArray(int var0, ByteBuffer var1, ConstantPool var2) {
      String[] var3 = new String[var0];
      boolean var4 = false;
      byte var5 = 0;

      for(int var6 = 0; var6 < var0; ++var6) {
         var5 = var1.get();
         if (var5 == 115) {
            int var7 = var1.getShort() & '\uffff';
            var3[var6] = var2.getUTF8At(var7);
         } else {
            skipMemberValue(var5, var1);
            var4 = true;
         }
      }

      return var4 ? exceptionProxy(var5) : var3;
   }

   private static Object parseClassArray(int var0, ByteBuffer var1, ConstantPool var2, Class<?> var3) {
      Class[] var4 = new Class[var0];
      boolean var5 = false;
      byte var6 = 0;

      for(int var7 = 0; var7 < var0; ++var7) {
         var6 = var1.get();
         if (var6 == 99) {
            var4[var7] = (Class)parseClassValue(var1, var2, var3);
         } else {
            skipMemberValue(var6, var1);
            var5 = true;
         }
      }

      return var5 ? exceptionProxy(var6) : var4;
   }

   private static Object parseEnumArray(int var0, Class<? extends Enum<?>> var1, ByteBuffer var2, ConstantPool var3, Class<?> var4) {
      Object[] var5 = (Object[])((Object[])Array.newInstance(var1, var0));
      boolean var6 = false;
      byte var7 = 0;

      for(int var8 = 0; var8 < var0; ++var8) {
         var7 = var2.get();
         if (var7 == 101) {
            var5[var8] = parseEnumValue(var1, var2, var3, var4);
         } else {
            skipMemberValue(var7, var2);
            var6 = true;
         }
      }

      return var6 ? exceptionProxy(var7) : var5;
   }

   private static Object parseAnnotationArray(int var0, Class<? extends Annotation> var1, ByteBuffer var2, ConstantPool var3, Class<?> var4) {
      Object[] var5 = (Object[])((Object[])Array.newInstance(var1, var0));
      boolean var6 = false;
      byte var7 = 0;

      for(int var8 = 0; var8 < var0; ++var8) {
         var7 = var2.get();
         if (var7 == 64) {
            var5[var8] = parseAnnotation(var2, var3, var4, true);
         } else {
            skipMemberValue(var7, var2);
            var6 = true;
         }
      }

      return var6 ? exceptionProxy(var7) : var5;
   }

   private static ExceptionProxy exceptionProxy(int var0) {
      return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + var0);
   }

   private static void skipAnnotation(ByteBuffer var0, boolean var1) {
      if (var1) {
         var0.getShort();
      }

      int var2 = var0.getShort() & '\uffff';

      for(int var3 = 0; var3 < var2; ++var3) {
         var0.getShort();
         skipMemberValue(var0);
      }

   }

   private static void skipMemberValue(ByteBuffer var0) {
      byte var1 = var0.get();
      skipMemberValue(var1, var0);
   }

   private static void skipMemberValue(int var0, ByteBuffer var1) {
      switch(var0) {
      case 64:
         skipAnnotation(var1, true);
         break;
      case 91:
         skipArray(var1);
         break;
      case 101:
         var1.getInt();
         break;
      default:
         var1.getShort();
      }

   }

   private static void skipArray(ByteBuffer var0) {
      int var1 = var0.getShort() & '\uffff';

      for(int var2 = 0; var2 < var1; ++var2) {
         skipMemberValue(var0);
      }

   }

   private static boolean contains(Object[] var0, Object var1) {
      Object[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 == var1) {
            return true;
         }
      }

      return false;
   }

   public static Annotation[] toArray(Map<Class<? extends Annotation>, Annotation> var0) {
      return (Annotation[])var0.values().toArray(EMPTY_ANNOTATION_ARRAY);
   }

   static Annotation[] getEmptyAnnotationArray() {
      return EMPTY_ANNOTATION_ARRAY;
   }
}
