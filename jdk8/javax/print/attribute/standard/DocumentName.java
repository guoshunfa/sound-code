package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.TextSyntax;

public final class DocumentName extends TextSyntax implements DocAttribute {
   private static final long serialVersionUID = 7883105848533280430L;

   public DocumentName(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof DocumentName;
   }

   public final Class<? extends Attribute> getCategory() {
      return DocumentName.class;
   }

   public final String getName() {
      return "document-name";
   }
}
