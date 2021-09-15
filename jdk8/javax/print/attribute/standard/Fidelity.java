package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Fidelity extends EnumSyntax implements PrintJobAttribute, PrintRequestAttribute {
   private static final long serialVersionUID = 6320827847329172308L;
   public static final Fidelity FIDELITY_TRUE = new Fidelity(0);
   public static final Fidelity FIDELITY_FALSE = new Fidelity(1);
   private static final String[] myStringTable = new String[]{"true", "false"};
   private static final Fidelity[] myEnumValueTable;

   protected Fidelity(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   public final Class<? extends Attribute> getCategory() {
      return Fidelity.class;
   }

   public final String getName() {
      return "ipp-attribute-fidelity";
   }

   static {
      myEnumValueTable = new Fidelity[]{FIDELITY_TRUE, FIDELITY_FALSE};
   }
}
