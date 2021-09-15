package javax.print.attribute;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class EnumSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = -2739521845085831642L;
   private int value;

   protected EnumSyntax(int var1) {
      this.value = var1;
   }

   public int getValue() {
      return this.value;
   }

   public Object clone() {
      return this;
   }

   public int hashCode() {
      return this.value;
   }

   public String toString() {
      String[] var1 = this.getStringTable();
      int var2 = this.value - this.getOffset();
      return var1 != null && var2 >= 0 && var2 < var1.length ? var1[var2] : Integer.toString(this.value);
   }

   protected Object readResolve() throws ObjectStreamException {
      EnumSyntax[] var1 = this.getEnumValueTable();
      if (var1 == null) {
         throw new InvalidObjectException("Null enumeration value table for class " + this.getClass());
      } else {
         int var2 = this.getOffset();
         int var3 = this.value - var2;
         if (0 <= var3 && var3 < var1.length) {
            EnumSyntax var4 = var1[var3];
            if (var4 == null) {
               throw new InvalidObjectException("No enumeration value for integer value = " + this.value + "for class " + this.getClass());
            } else {
               return var4;
            }
         } else {
            throw new InvalidObjectException("Integer value = " + this.value + " not in valid range " + var2 + ".." + (var2 + var1.length - 1) + "for class " + this.getClass());
         }
      }
   }

   protected String[] getStringTable() {
      return null;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return null;
   }

   protected int getOffset() {
      return 0;
   }
}
