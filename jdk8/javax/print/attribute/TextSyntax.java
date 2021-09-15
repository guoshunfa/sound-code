package javax.print.attribute;

import java.io.Serializable;
import java.util.Locale;

public abstract class TextSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = -8130648736378144102L;
   private String value;
   private Locale locale;

   protected TextSyntax(String var1, Locale var2) {
      this.value = verify(var1);
      this.locale = verify(var2);
   }

   private static String verify(String var0) {
      if (var0 == null) {
         throw new NullPointerException(" value is null");
      } else {
         return var0;
      }
   }

   private static Locale verify(Locale var0) {
      return var0 == null ? Locale.getDefault() : var0;
   }

   public String getValue() {
      return this.value;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public int hashCode() {
      return this.value.hashCode() ^ this.locale.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof TextSyntax && this.value.equals(((TextSyntax)var1).value) && this.locale.equals(((TextSyntax)var1).locale);
   }

   public String toString() {
      return this.value;
   }
}
