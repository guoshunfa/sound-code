package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class JobSheets extends EnumSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -4735258056132519759L;
   public static final JobSheets NONE = new JobSheets(0);
   public static final JobSheets STANDARD = new JobSheets(1);
   private static final String[] myStringTable = new String[]{"none", "standard"};
   private static final JobSheets[] myEnumValueTable;

   protected JobSheets(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   public final Class<? extends Attribute> getCategory() {
      return JobSheets.class;
   }

   public final String getName() {
      return "job-sheets";
   }

   static {
      myEnumValueTable = new JobSheets[]{NONE, STANDARD};
   }
}
