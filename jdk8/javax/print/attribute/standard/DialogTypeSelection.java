package javax.print.attribute.standard;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintRequestAttribute;

public final class DialogTypeSelection extends EnumSyntax implements PrintRequestAttribute {
   private static final long serialVersionUID = 7518682952133256029L;
   public static final DialogTypeSelection NATIVE = new DialogTypeSelection(0);
   public static final DialogTypeSelection COMMON = new DialogTypeSelection(1);
   private static final String[] myStringTable = new String[]{"native", "common"};
   private static final DialogTypeSelection[] myEnumValueTable;

   protected DialogTypeSelection(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   public final Class getCategory() {
      return DialogTypeSelection.class;
   }

   public final String getName() {
      return "dialog-type-selection";
   }

   static {
      myEnumValueTable = new DialogTypeSelection[]{NATIVE, COMMON};
   }
}
