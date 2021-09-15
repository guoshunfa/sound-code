package sun.reflect.generics.reflectiveObjects;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.misc.ReflectUtil;

public class TypeVariableImpl<D extends GenericDeclaration> extends LazyReflectiveObjectGenerator implements TypeVariable<D> {
   D genericDeclaration;
   private String name;
   private Type[] bounds;
   private FieldTypeSignature[] boundASTs;
   private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

   private TypeVariableImpl(D var1, String var2, FieldTypeSignature[] var3, GenericsFactory var4) {
      super(var4);
      this.genericDeclaration = var1;
      this.name = var2;
      this.boundASTs = var3;
   }

   private FieldTypeSignature[] getBoundASTs() {
      assert this.bounds == null;

      return this.boundASTs;
   }

   public static <T extends GenericDeclaration> TypeVariableImpl<T> make(T var0, String var1, FieldTypeSignature[] var2, GenericsFactory var3) {
      if (!(var0 instanceof Class) && !(var0 instanceof Method) && !(var0 instanceof Constructor)) {
         throw new AssertionError("Unexpected kind of GenericDeclaration" + var0.getClass().toString());
      } else {
         return new TypeVariableImpl(var0, var1, var2, var3);
      }
   }

   public Type[] getBounds() {
      if (this.bounds == null) {
         FieldTypeSignature[] var1 = this.getBoundASTs();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Reifier var4 = this.getReifier();
            var1[var3].accept(var4);
            var2[var3] = var4.getResult();
         }

         this.bounds = var2;
      }

      return (Type[])this.bounds.clone();
   }

   public D getGenericDeclaration() {
      if (this.genericDeclaration instanceof Class) {
         ReflectUtil.checkPackageAccess((Class)this.genericDeclaration);
      } else {
         if (!(this.genericDeclaration instanceof Method) && !(this.genericDeclaration instanceof Constructor)) {
            throw new AssertionError("Unexpected kind of GenericDeclaration");
         }

         ReflectUtil.conservativeCheckMemberAccess((Member)this.genericDeclaration);
      }

      return this.genericDeclaration;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getName();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof TypeVariable && var1.getClass() == TypeVariableImpl.class) {
         TypeVariable var2 = (TypeVariable)var1;
         GenericDeclaration var3 = var2.getGenericDeclaration();
         String var4 = var2.getName();
         return Objects.equals(this.genericDeclaration, var3) && Objects.equals(this.name, var4);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.genericDeclaration.hashCode() ^ this.name.hashCode();
   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)mapAnnotations(this.getAnnotations()).get(var1);
   }

   public <T extends Annotation> T getDeclaredAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      return this.getAnnotation(var1);
   }

   public <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(mapAnnotations(this.getAnnotations()), var1);
   }

   public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return this.getAnnotationsByType(var1);
   }

   public Annotation[] getAnnotations() {
      int var1 = this.typeVarIndex();
      if (var1 < 0) {
         throw new AssertionError("Index must be non-negative.");
      } else {
         return TypeAnnotationParser.parseTypeVariableAnnotations(this.getGenericDeclaration(), var1);
      }
   }

   public Annotation[] getDeclaredAnnotations() {
      return this.getAnnotations();
   }

   public AnnotatedType[] getAnnotatedBounds() {
      return TypeAnnotationParser.parseAnnotatedBounds(this.getBounds(), this.getGenericDeclaration(), this.typeVarIndex());
   }

   private int typeVarIndex() {
      TypeVariable[] var1 = this.getGenericDeclaration().getTypeParameters();
      int var2 = -1;
      TypeVariable[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TypeVariable var6 = var3[var5];
         ++var2;
         if (this.equals(var6)) {
            return var2;
         }
      }

      return -1;
   }

   private static Map<Class<? extends Annotation>, Annotation> mapAnnotations(Annotation[] var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      Annotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation var5 = var2[var4];
         Class var6 = var5.annotationType();
         AnnotationType var7 = AnnotationType.getInstance(var6);
         if (var7.retention() == RetentionPolicy.RUNTIME && var1.put(var6, var5) != null) {
            throw new AnnotationFormatError("Duplicate annotation for class: " + var6 + ": " + var5);
         }
      }

      return var1;
   }
}
