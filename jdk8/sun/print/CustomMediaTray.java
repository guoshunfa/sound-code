package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;

class CustomMediaTray extends MediaTray {
   private static ArrayList customStringTable = new ArrayList();
   private static ArrayList customEnumTable = new ArrayList();
   private String choiceName;
   private static final long serialVersionUID = 1019451298193987013L;

   private CustomMediaTray(int var1) {
      super(var1);
   }

   private static synchronized int nextValue(String var0) {
      customStringTable.add(var0);
      return customStringTable.size() - 1;
   }

   public CustomMediaTray(String var1, String var2) {
      super(nextValue(var1));
      this.choiceName = var2;
      customEnumTable.add(this);
   }

   public String getChoiceName() {
      return this.choiceName;
   }

   public Media[] getSuperEnumTable() {
      return (Media[])((Media[])super.getEnumValueTable());
   }

   protected String[] getStringTable() {
      String[] var1 = new String[customStringTable.size()];
      return (String[])((String[])customStringTable.toArray(var1));
   }

   protected EnumSyntax[] getEnumValueTable() {
      MediaTray[] var1 = new MediaTray[customEnumTable.size()];
      return (MediaTray[])((MediaTray[])customEnumTable.toArray(var1));
   }
}
