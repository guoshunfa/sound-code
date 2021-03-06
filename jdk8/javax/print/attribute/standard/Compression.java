package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public class Compression extends EnumSyntax implements DocAttribute {
   private static final long serialVersionUID = -5716748913324997674L;
   public static final Compression NONE = new Compression(0);
   public static final Compression DEFLATE = new Compression(1);
   public static final Compression GZIP = new Compression(2);
   public static final Compression COMPRESS = new Compression(3);
   private static final String[] myStringTable = new String[]{"none", "deflate", "gzip", "compress"};
   private static final Compression[] myEnumValueTable;

   protected Compression(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   public final Class<? extends Attribute> getCategory() {
      return Compression.class;
   }

   public final String getName() {
      return "compression";
   }

   static {
      myEnumValueTable = new Compression[]{NONE, DEFLATE, GZIP, COMPRESS};
   }
}
