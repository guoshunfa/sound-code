package javax.print.attribute;

import java.io.Serializable;
import java.util.Date;

public abstract class DateTimeSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = -1400819079791208582L;
   private Date value;

   protected DateTimeSyntax(Date var1) {
      if (var1 == null) {
         throw new NullPointerException("value is null");
      } else {
         this.value = var1;
      }
   }

   public Date getValue() {
      return new Date(this.value.getTime());
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof DateTimeSyntax && this.value.equals(((DateTimeSyntax)var1).value);
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public String toString() {
      return "" + this.value;
   }
}
