package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class PrintQuality extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -3072341285225858365L;
   public static final PrintQuality DRAFT = new PrintQuality(3);
   public static final PrintQuality NORMAL = new PrintQuality(4);
   public static final PrintQuality HIGH = new PrintQuality(5);
   private static final String[] myStringTable = new String[]{"draft", "normal", "high"};
   private static final PrintQuality[] myEnumValueTable;

   protected PrintQuality(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   protected int getOffset() {
      return 3;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrintQuality.class;
   }

   public final String getName() {
      return "print-quality";
   }

   static {
      myEnumValueTable = new PrintQuality[]{DRAFT, NORMAL, HIGH};
   }
}
