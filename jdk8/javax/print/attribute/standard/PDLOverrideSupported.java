package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintServiceAttribute;

public class PDLOverrideSupported extends EnumSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = -4393264467928463934L;
   public static final PDLOverrideSupported NOT_ATTEMPTED = new PDLOverrideSupported(0);
   public static final PDLOverrideSupported ATTEMPTED = new PDLOverrideSupported(1);
   private static final String[] myStringTable = new String[]{"not-attempted", "attempted"};
   private static final PDLOverrideSupported[] myEnumValueTable;

   protected PDLOverrideSupported(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   public final Class<? extends Attribute> getCategory() {
      return PDLOverrideSupported.class;
   }

   public final String getName() {
      return "pdl-override-supported";
   }

   static {
      myEnumValueTable = new PDLOverrideSupported[]{NOT_ATTEMPTED, ATTEMPTED};
   }
}
