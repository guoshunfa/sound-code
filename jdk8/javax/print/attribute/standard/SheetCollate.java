package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class SheetCollate extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 7080587914259873003L;
   public static final SheetCollate UNCOLLATED = new SheetCollate(0);
   public static final SheetCollate COLLATED = new SheetCollate(1);
   private static final String[] myStringTable = new String[]{"uncollated", "collated"};
   private static final SheetCollate[] myEnumValueTable;

   protected SheetCollate(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   public final Class<? extends Attribute> getCategory() {
      return SheetCollate.class;
   }

   public final String getName() {
      return "sheet-collate";
   }

   static {
      myEnumValueTable = new SheetCollate[]{UNCOLLATED, COLLATED};
   }
}
