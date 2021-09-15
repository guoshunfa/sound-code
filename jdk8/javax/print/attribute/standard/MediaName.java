package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public class MediaName extends Media implements Attribute {
   private static final long serialVersionUID = 4653117714524155448L;
   public static final MediaName NA_LETTER_WHITE = new MediaName(0);
   public static final MediaName NA_LETTER_TRANSPARENT = new MediaName(1);
   public static final MediaName ISO_A4_WHITE = new MediaName(2);
   public static final MediaName ISO_A4_TRANSPARENT = new MediaName(3);
   private static final String[] myStringTable = new String[]{"na-letter-white", "na-letter-transparent", "iso-a4-white", "iso-a4-transparent"};
   private static final MediaName[] myEnumValueTable;

   protected MediaName(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   static {
      myEnumValueTable = new MediaName[]{NA_LETTER_WHITE, NA_LETTER_TRANSPARENT, ISO_A4_WHITE, ISO_A4_TRANSPARENT};
   }
}
