package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public final class Severity extends EnumSyntax implements Attribute {
   private static final long serialVersionUID = 8781881462717925380L;
   public static final Severity REPORT = new Severity(0);
   public static final Severity WARNING = new Severity(1);
   public static final Severity ERROR = new Severity(2);
   private static final String[] myStringTable = new String[]{"report", "warning", "error"};
   private static final Severity[] myEnumValueTable;

   protected Severity(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   public final Class<? extends Attribute> getCategory() {
      return Severity.class;
   }

   public final String getName() {
      return "severity";
   }

   static {
      myEnumValueTable = new Severity[]{REPORT, WARNING, ERROR};
   }
}
