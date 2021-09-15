package java.util;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import sun.misc.SharedSecrets;

public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, Serializable {
   final Class<E> elementType;
   final Enum<?>[] universe;
   private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];

   EnumSet(Class<E> var1, Enum<?>[] var2) {
      this.elementType = var1;
      this.universe = var2;
   }

   public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> var0) {
      Enum[] var1 = getUniverse(var0);
      if (var1 == null) {
         throw new ClassCastException(var0 + " not an enum");
      } else {
         return (EnumSet)(var1.length <= 64 ? new RegularEnumSet(var0, var1) : new JumboEnumSet(var0, var1));
      }
   }

   public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> var0) {
      EnumSet var1 = noneOf(var0);
      var1.addAll();
      return var1;
   }

   abstract void addAll();

   public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> var0) {
      return var0.clone();
   }

   public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> var0) {
      if (var0 instanceof EnumSet) {
         return ((EnumSet)var0).clone();
      } else if (var0.isEmpty()) {
         throw new IllegalArgumentException("Collection is empty");
      } else {
         Iterator var1 = var0.iterator();
         Enum var2 = (Enum)var1.next();
         EnumSet var3 = of(var2);

         while(var1.hasNext()) {
            var3.add(var1.next());
         }

         return var3;
      }
   }

   public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> var0) {
      EnumSet var1 = copyOf(var0);
      var1.complement();
      return var1;
   }

   public static <E extends Enum<E>> EnumSet<E> of(E var0) {
      EnumSet var1 = noneOf(var0.getDeclaringClass());
      var1.add(var0);
      return var1;
   }

   public static <E extends Enum<E>> EnumSet<E> of(E var0, E var1) {
      EnumSet var2 = noneOf(var0.getDeclaringClass());
      var2.add(var0);
      var2.add(var1);
      return var2;
   }

   public static <E extends Enum<E>> EnumSet<E> of(E var0, E var1, E var2) {
      EnumSet var3 = noneOf(var0.getDeclaringClass());
      var3.add(var0);
      var3.add(var1);
      var3.add(var2);
      return var3;
   }

   public static <E extends Enum<E>> EnumSet<E> of(E var0, E var1, E var2, E var3) {
      EnumSet var4 = noneOf(var0.getDeclaringClass());
      var4.add(var0);
      var4.add(var1);
      var4.add(var2);
      var4.add(var3);
      return var4;
   }

   public static <E extends Enum<E>> EnumSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      EnumSet var5 = noneOf(var0.getDeclaringClass());
      var5.add(var0);
      var5.add(var1);
      var5.add(var2);
      var5.add(var3);
      var5.add(var4);
      return var5;
   }

   @SafeVarargs
   public static <E extends Enum<E>> EnumSet<E> of(E var0, E... var1) {
      EnumSet var2 = noneOf(var0.getDeclaringClass());
      var2.add(var0);
      Enum[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Enum var6 = var3[var5];
         var2.add(var6);
      }

      return var2;
   }

   public static <E extends Enum<E>> EnumSet<E> range(E var0, E var1) {
      if (var0.compareTo(var1) > 0) {
         throw new IllegalArgumentException(var0 + " > " + var1);
      } else {
         EnumSet var2 = noneOf(var0.getDeclaringClass());
         var2.addRange(var0, var1);
         return var2;
      }
   }

   abstract void addRange(E var1, E var2);

   public EnumSet<E> clone() {
      try {
         return (EnumSet)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError(var2);
      }
   }

   abstract void complement();

   final void typeCheck(E var1) {
      Class var2 = var1.getClass();
      if (var2 != this.elementType && var2.getSuperclass() != this.elementType) {
         throw new ClassCastException(var2 + " != " + this.elementType);
      }
   }

   private static <E extends Enum<E>> E[] getUniverse(Class<E> var0) {
      return SharedSecrets.getJavaLangAccess().getEnumConstantsShared(var0);
   }

   Object writeReplace() {
      return new EnumSet.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static class SerializationProxy<E extends Enum<E>> implements Serializable {
      private final Class<E> elementType;
      private final Enum<?>[] elements;
      private static final long serialVersionUID = 362491234563181265L;

      SerializationProxy(EnumSet<E> var1) {
         this.elementType = var1.elementType;
         this.elements = (Enum[])var1.toArray(EnumSet.ZERO_LENGTH_ENUM_ARRAY);
      }

      private Object readResolve() {
         EnumSet var1 = EnumSet.noneOf(this.elementType);
         Enum[] var2 = this.elements;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Enum var5 = var2[var4];
            var1.add(var5);
         }

         return var1;
      }
   }
}
