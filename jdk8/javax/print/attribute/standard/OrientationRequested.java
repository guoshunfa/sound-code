package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class OrientationRequested extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -4447437289862822276L;
   public static final OrientationRequested PORTRAIT = new OrientationRequested(3);
   public static final OrientationRequested LANDSCAPE = new OrientationRequested(4);
   public static final OrientationRequested REVERSE_LANDSCAPE = new OrientationRequested(5);
   public static final OrientationRequested REVERSE_PORTRAIT = new OrientationRequested(6);
   private static final String[] myStringTable = new String[]{"portrait", "landscape", "reverse-landscape", "reverse-portrait"};
   private static final OrientationRequested[] myEnumValueTable;

   protected OrientationRequested(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return myStringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return myEnumValueTable;
   }

   protected int getOffset() {
      return 3;
   }

   public final Class<? extends Attribute> getCategory() {
      return OrientationRequested.class;
   }

   public final String getName() {
      return "orientation-requested";
   }

   static {
      myEnumValueTable = new OrientationRequested[]{PORTRAIT, LANDSCAPE, REVERSE_LANDSCAPE, REVERSE_PORTRAIT};
   }
}
