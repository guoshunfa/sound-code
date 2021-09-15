package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public abstract class Media extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -2823970704630722439L;

   protected Media(int var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof Media && var1.getClass() == this.getClass() && ((Media)var1).getValue() == this.getValue();
   }

   public final Class<? extends Attribute> getCategory() {
      return Media.class;
   }

   public final String getName() {
      return "media";
   }
}
