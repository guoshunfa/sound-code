package sun.reflect.generics.factory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;
import sun.reflect.generics.scope.Scope;
import sun.reflect.generics.tree.FieldTypeSignature;

public class CoreReflectionFactory implements GenericsFactory {
   private final GenericDeclaration decl;
   private final Scope scope;

   private CoreReflectionFactory(GenericDeclaration var1, Scope var2) {
      this.decl = var1;
      this.scope = var2;
   }

   private GenericDeclaration getDecl() {
      return this.decl;
   }

   private Scope getScope() {
      return this.scope;
   }

   private ClassLoader getDeclsLoader() {
      if (this.decl instanceof Class) {
         return ((Class)this.decl).getClassLoader();
      } else if (this.decl instanceof Method) {
         return ((Method)this.decl).getDeclaringClass().getClassLoader();
      } else {
         assert this.decl instanceof Constructor : "Constructor expected";

         return ((Constructor)this.decl).getDeclaringClass().getClassLoader();
      }
   }

   public static CoreReflectionFactory make(GenericDeclaration var0, Scope var1) {
      return new CoreReflectionFactory(var0, var1);
   }

   public TypeVariable<?> makeTypeVariable(String var1, FieldTypeSignature[] var2) {
      return TypeVariableImpl.make(this.getDecl(), var1, var2, this);
   }

   public WildcardType makeWildcard(FieldTypeSignature[] var1, FieldTypeSignature[] var2) {
      return WildcardTypeImpl.make(var1, var2, this);
   }

   public ParameterizedType makeParameterizedType(Type var1, Type[] var2, Type var3) {
      return ParameterizedTypeImpl.make((Class)var1, var2, var3);
   }

   public TypeVariable<?> findTypeVariable(String var1) {
      return this.getScope().lookup(var1);
   }

   public Type makeNamedType(String var1) {
      try {
         return Class.forName(var1, false, this.getDeclsLoader());
      } catch (ClassNotFoundException var3) {
         throw new TypeNotPresentException(var1, var3);
      }
   }

   public Type makeArrayType(Type var1) {
      return (Type)(var1 instanceof Class ? Array.newInstance((Class)var1, 0).getClass() : GenericArrayTypeImpl.make(var1));
   }

   public Type makeByte() {
      return Byte.TYPE;
   }

   public Type makeBool() {
      return Boolean.TYPE;
   }

   public Type makeShort() {
      return Short.TYPE;
   }

   public Type makeChar() {
      return Character.TYPE;
   }

   public Type makeInt() {
      return Integer.TYPE;
   }

   public Type makeLong() {
      return Long.TYPE;
   }

   public Type makeFloat() {
      return Float.TYPE;
   }

   public Type makeDouble() {
      return Double.TYPE;
   }

   public Type makeVoid() {
      return Void.TYPE;
   }
}
