package javax.swing.text.html.parser;

import java.util.Hashtable;

public final class Entity implements DTDConstants {
   public String name;
   public int type;
   public char[] data;
   static Hashtable<String, Integer> entityTypes = new Hashtable();

   public Entity(String var1, int var2, char[] var3) {
      this.name = var1;
      this.type = var2;
      this.data = var3;
   }

   public String getName() {
      return this.name;
   }

   public int getType() {
      return this.type & '\uffff';
   }

   public boolean isParameter() {
      return (this.type & 262144) != 0;
   }

   public boolean isGeneral() {
      return (this.type & 65536) != 0;
   }

   public char[] getData() {
      return this.data;
   }

   public String getString() {
      return new String(this.data, 0, this.data.length);
   }

   public static int name2type(String var0) {
      Integer var1 = (Integer)entityTypes.get(var0);
      return var1 == null ? 1 : var1;
   }

   static {
      entityTypes.put("PUBLIC", 10);
      entityTypes.put("CDATA", 1);
      entityTypes.put("SDATA", 11);
      entityTypes.put("PI", 12);
      entityTypes.put("STARTTAG", 13);
      entityTypes.put("ENDTAG", 14);
      entityTypes.put("MS", 15);
      entityTypes.put("MD", 16);
      entityTypes.put("SYSTEM", 17);
   }
}
