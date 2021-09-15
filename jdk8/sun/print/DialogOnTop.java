package sun.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;

public class DialogOnTop implements PrintRequestAttribute {
   private static final long serialVersionUID = -1901909867156076547L;
   long id;

   public DialogOnTop() {
   }

   public DialogOnTop(long var1) {
      this.id = var1;
   }

   public final Class<? extends Attribute> getCategory() {
      return DialogOnTop.class;
   }

   public long getID() {
      return this.id;
   }

   public final String getName() {
      return "dialog-on-top";
   }

   public String toString() {
      return "dialog-on-top";
   }
}
