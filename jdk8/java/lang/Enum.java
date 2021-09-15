package java.lang;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable {
   private final String name;
   private final int ordinal;

   public final String name() {
      return this.name;
   }

   public final int ordinal() {
      return this.ordinal;
   }

   protected Enum(String var1, int var2) {
      this.name = var1;
      this.ordinal = var2;
   }

   public String toString() {
      return this.name;
   }

   public final boolean equals(Object var1) {
      return this == var1;
   }

   public final int hashCode() {
      return super.hashCode();
   }

   protected final Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }

   public final int compareTo(E var1) {
      if (this.getClass() != var1.getClass() && this.getDeclaringClass() != var1.getDeclaringClass()) {
         throw new ClassCastException();
      } else {
         return this.ordinal - var1.ordinal;
      }
   }

   public final Class<E> getDeclaringClass() {
      Class var1 = this.getClass();
      Class var2 = var1.getSuperclass();
      return var2 == Enum.class ? var1 : var2;
   }

   public static <T extends Enum<T>> T valueOf(Class<T> var0, String var1) {
      Enum var2 = (Enum)var0.enumConstantDirectory().get(var1);
      if (var2 != null) {
         return var2;
      } else if (var1 == null) {
         throw new NullPointerException("Name is null");
      } else {
         throw new IllegalArgumentException("No enum constant " + var0.getCanonicalName() + "." + var1);
      }
   }

   protected final void finalize() {
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      throw new InvalidObjectException("can't deserialize enum");
   }

   private void readObjectNoData() throws ObjectStreamException {
      throw new InvalidObjectException("can't deserialize enum");
   }
}
