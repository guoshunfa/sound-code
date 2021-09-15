package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobOriginatingUserName extends TextSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = -8052537926362933477L;

   public JobOriginatingUserName(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobOriginatingUserName;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobOriginatingUserName.class;
   }

   public final String getName() {
      return "job-originating-user-name";
   }
}
