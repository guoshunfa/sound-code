package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class AnnotatedTypeFactory {
   static final AnnotatedType EMPTY_ANNOTATED_TYPE;
   static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY;

   public static AnnotatedType buildAnnotatedType(Type var0, TypeAnnotation.LocationInfo var1, TypeAnnotation[] var2, TypeAnnotation[] var3, AnnotatedElement var4) {
      if (var0 == null) {
         return EMPTY_ANNOTATED_TYPE;
      } else if (isArray(var0)) {
         return new AnnotatedTypeFactory.AnnotatedArrayTypeImpl(var0, var1, var2, var3, var4);
      } else if (var0 instanceof Class) {
         return new AnnotatedTypeFactory.AnnotatedTypeBaseImpl(var0, addNesting(var0, var1), var2, var3, var4);
      } else if (var0 instanceof TypeVariable) {
         return new AnnotatedTypeFactory.AnnotatedTypeVariableImpl((TypeVariable)var0, var1, var2, var3, var4);
      } else if (var0 instanceof ParameterizedType) {
         return new AnnotatedTypeFactory.AnnotatedParameterizedTypeImpl((ParameterizedType)var0, addNesting(var0, var1), var2, var3, var4);
      } else if (var0 instanceof WildcardType) {
         return new AnnotatedTypeFactory.AnnotatedWildcardTypeImpl((WildcardType)var0, var1, var2, var3, var4);
      } else {
         throw new AssertionError("Unknown instance of Type: " + var0 + "\nThis should not happen.");
      }
   }

   private static TypeAnnotation.LocationInfo addNesting(Type var0, TypeAnnotation.LocationInfo var1) {
      if (isArray(var0)) {
         return var1;
      } else if (var0 instanceof Class) {
         Class var3 = (Class)var0;
         if (var3.getEnclosingClass() == null) {
            return var1;
         } else {
            return Modifier.isStatic(var3.getModifiers()) ? addNesting(var3.getEnclosingClass(), var1) : addNesting(var3.getEnclosingClass(), var1.pushInner());
         }
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var2 = (ParameterizedType)var0;
         return var2.getOwnerType() == null ? var1 : addNesting(var2.getOwnerType(), var1.pushInner());
      } else {
         return var1;
      }
   }

   private static boolean isArray(Type var0) {
      if (var0 instanceof Class) {
         Class var1 = (Class)var0;
         if (var1.isArray()) {
            return true;
         }
      } else if (var0 instanceof GenericArrayType) {
         return true;
      }

      return false;
   }

   static {
      EMPTY_ANNOTATED_TYPE = new AnnotatedTypeFactory.AnnotatedTypeBaseImpl((Type)null, TypeAnnotation.LocationInfo.BASE_LOCATION, new TypeAnnotation[0], new TypeAnnotation[0], (AnnotatedElement)null);
      EMPTY_ANNOTATED_TYPE_ARRAY = new AnnotatedType[0];
   }

   private static final class AnnotatedWildcardTypeImpl extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl implements AnnotatedWildcardType {
      private final boolean hasUpperBounds;

      AnnotatedWildcardTypeImpl(WildcardType var1, TypeAnnotation.LocationInfo var2, TypeAnnotation[] var3, TypeAnnotation[] var4, AnnotatedElement var5) {
         super(var1, var2, var3, var4, var5);
         this.hasUpperBounds = var1.getLowerBounds().length == 0;
      }

      public AnnotatedType[] getAnnotatedUpperBounds() {
         return !this.hasUpperBounds() ? new AnnotatedType[0] : this.getAnnotatedBounds(this.getWildcardType().getUpperBounds());
      }

      public AnnotatedType[] getAnnotatedLowerBounds() {
         return this.hasUpperBounds ? new AnnotatedType[0] : this.getAnnotatedBounds(this.getWildcardType().getLowerBounds());
      }

      private AnnotatedType[] getAnnotatedBounds(Type[] var1) {
         AnnotatedType[] var2 = new AnnotatedType[var1.length];
         Arrays.fill(var2, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
         TypeAnnotation.LocationInfo var3 = this.getLocation().pushWildcard();
         int var4 = this.getTypeAnnotations().length;

         for(int var5 = 0; var5 < var2.length; ++var5) {
            ArrayList var6 = new ArrayList(var4);
            TypeAnnotation[] var7 = this.getTypeAnnotations();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               TypeAnnotation var10 = var7[var9];
               if (var10.getLocationInfo().isSameLocationInfo(var3)) {
                  var6.add(var10);
               }
            }

            var2[var5] = AnnotatedTypeFactory.buildAnnotatedType(var1[var5], var3, (TypeAnnotation[])var6.toArray(new TypeAnnotation[0]), this.getTypeAnnotations(), this.getDecl());
         }

         return var2;
      }

      private WildcardType getWildcardType() {
         return (WildcardType)this.getType();
      }

      private boolean hasUpperBounds() {
         return this.hasUpperBounds;
      }
   }

   private static final class AnnotatedParameterizedTypeImpl extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl implements AnnotatedParameterizedType {
      AnnotatedParameterizedTypeImpl(ParameterizedType var1, TypeAnnotation.LocationInfo var2, TypeAnnotation[] var3, TypeAnnotation[] var4, AnnotatedElement var5) {
         super(var1, var2, var3, var4, var5);
      }

      public AnnotatedType[] getAnnotatedActualTypeArguments() {
         Type[] var1 = this.getParameterizedType().getActualTypeArguments();
         AnnotatedType[] var2 = new AnnotatedType[var1.length];
         Arrays.fill(var2, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
         int var3 = this.getTypeAnnotations().length;

         for(int var4 = 0; var4 < var2.length; ++var4) {
            ArrayList var5 = new ArrayList(var3);
            TypeAnnotation.LocationInfo var6 = this.getLocation().pushTypeArg((short)((byte)var4));
            TypeAnnotation[] var7 = this.getTypeAnnotations();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               TypeAnnotation var10 = var7[var9];
               if (var10.getLocationInfo().isSameLocationInfo(var6)) {
                  var5.add(var10);
               }
            }

            var2[var4] = AnnotatedTypeFactory.buildAnnotatedType(var1[var4], var6, (TypeAnnotation[])var5.toArray(new TypeAnnotation[0]), this.getTypeAnnotations(), this.getDecl());
         }

         return var2;
      }

      private ParameterizedType getParameterizedType() {
         return (ParameterizedType)this.getType();
      }
   }

   private static final class AnnotatedTypeVariableImpl extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl implements AnnotatedTypeVariable {
      AnnotatedTypeVariableImpl(TypeVariable<?> var1, TypeAnnotation.LocationInfo var2, TypeAnnotation[] var3, TypeAnnotation[] var4, AnnotatedElement var5) {
         super(var1, var2, var3, var4, var5);
      }

      public AnnotatedType[] getAnnotatedBounds() {
         return this.getTypeVariable().getAnnotatedBounds();
      }

      private TypeVariable<?> getTypeVariable() {
         return (TypeVariable)this.getType();
      }
   }

   private static final class AnnotatedArrayTypeImpl extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl implements AnnotatedArrayType {
      AnnotatedArrayTypeImpl(Type var1, TypeAnnotation.LocationInfo var2, TypeAnnotation[] var3, TypeAnnotation[] var4, AnnotatedElement var5) {
         super(var1, var2, var3, var4, var5);
      }

      public AnnotatedType getAnnotatedGenericComponentType() {
         return AnnotatedTypeFactory.buildAnnotatedType(this.getComponentType(), this.getLocation().pushArray(), this.getTypeAnnotations(), this.getTypeAnnotations(), this.getDecl());
      }

      private Type getComponentType() {
         Type var1 = this.getType();
         if (var1 instanceof Class) {
            Class var2 = (Class)var1;
            return var2.getComponentType();
         } else {
            return ((GenericArrayType)var1).getGenericComponentType();
         }
      }
   }

   private static class AnnotatedTypeBaseImpl implements AnnotatedType {
      private final Type type;
      private final AnnotatedElement decl;
      private final TypeAnnotation.LocationInfo location;
      private final TypeAnnotation[] allOnSameTargetTypeAnnotations;
      private final Map<Class<? extends Annotation>, Annotation> annotations;

      AnnotatedTypeBaseImpl(Type var1, TypeAnnotation.LocationInfo var2, TypeAnnotation[] var3, TypeAnnotation[] var4, AnnotatedElement var5) {
         this.type = var1;
         this.decl = var5;
         this.location = var2;
         this.allOnSameTargetTypeAnnotations = var4;
         this.annotations = TypeAnnotationParser.mapTypeAnnotations(var2.filter(var3));
      }

      public final Annotation[] getAnnotations() {
         return this.getDeclaredAnnotations();
      }

      public final <T extends Annotation> T getAnnotation(Class<T> var1) {
         return this.getDeclaredAnnotation(var1);
      }

      public final <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
         return this.getDeclaredAnnotationsByType(var1);
      }

      public final Annotation[] getDeclaredAnnotations() {
         return (Annotation[])this.annotations.values().toArray(new Annotation[0]);
      }

      public final <T extends Annotation> T getDeclaredAnnotation(Class<T> var1) {
         return (Annotation)this.annotations.get(var1);
      }

      public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> var1) {
         return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.annotations, var1);
      }

      public final Type getType() {
         return this.type;
      }

      final TypeAnnotation.LocationInfo getLocation() {
         return this.location;
      }

      final TypeAnnotation[] getTypeAnnotations() {
         return this.allOnSameTargetTypeAnnotations;
      }

      final AnnotatedElement getDecl() {
         return this.decl;
      }
   }
}
