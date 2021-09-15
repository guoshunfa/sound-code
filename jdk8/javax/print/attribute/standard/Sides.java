package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Sides extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -6890309414893262822L;
   public static final Sides ONE_SIDED = new Sides(0);
   public static final Sides TWO_SIDED_LONG_EDGE = new Sides(1);
   public static final Sides TWO_SIDED_SHORT_EDGE = new Sides(2);
   public static final Sides DUPLEX;
   public static final Sides TUMBLE;
   private static final String[] myStringTable;
   private static final Sides[] myEnumValueTable;

   protected Sides(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   public final Class<? extends Attribute> getCategory() {
      return Sides.class;
   }

   public final String getName() {
      return "sides";
   }

   static {
      DUPLEX = TWO_SIDED_LONG_EDGE;
      TUMBLE = TWO_SIDED_SHORT_EDGE;
      myStringTable = new String[]{"one-sided", "two-sided-long-edge", "two-sided-short-edge"};
      myEnumValueTable = new Sides[]{ONE_SIDED, TWO_SIDED_LONG_EDGE, TWO_SIDED_SHORT_EDGE};
   }
}
