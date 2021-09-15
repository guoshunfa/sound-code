package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class RequestingUserName extends TextSyntax implements PrintRequestAttribute {
   private static final long serialVersionUID = -2683049894310331454L;

   public RequestingUserName(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof RequestingUserName;
   }

   public final Class<? extends Attribute> getCategory() {
      return RequestingUserName.class;
   }

   public final String getName() {
      return "requesting-user-name";
   }
}
