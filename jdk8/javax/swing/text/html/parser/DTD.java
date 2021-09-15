package javax.swing.text.html.parser;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.awt.AppContext;

public class DTD implements DTDConstants {
   public String name;
   public Vector<Element> elements = new Vector();
   public Hashtable<String, Element> elementHash = new Hashtable();
   public Hashtable<Object, Entity> entityHash = new Hashtable();
   public final Element pcdata = this.getElement("#pcdata");
   public final Element html = this.getElement("html");
   public final Element meta = this.getElement("meta");
   public final Element base = this.getElement("base");
   public final Element isindex = this.getElement("isindex");
   public final Element head = this.getElement("head");
   public final Element body = this.getElement("body");
   public final Element applet = this.getElement("applet");
   public final Element param = this.getElement("param");
   public final Element p = this.getElement("p");
   public final Element title = this.getElement("title");
   final Element style = this.getElement("style");
   final Element link = this.getElement("link");
   final Element script = this.getElement("script");
   public static final int FILE_VERSION = 1;
   private static final Object DTD_HASH_KEY = new Object();

   protected DTD(String var1) {
      this.name = var1;
      this.defEntity("#RE", 65536, 13);
      this.defEntity("#RS", 65536, 10);
      this.defEntity("#SPACE", 65536, 32);
      this.defineElement("unknown", 17, false, true, (ContentModel)null, (BitSet)null, (BitSet)null, (AttributeList)null);
   }

   public String getName() {
      return this.name;
   }

   public Entity getEntity(String var1) {
      return (Entity)this.entityHash.get(var1);
   }

   public Entity getEntity(int var1) {
      return (Entity)this.entityHash.get(var1);
   }

   boolean elementExists(String var1) {
      return !"unknown".equals(var1) && this.elementHash.get(var1) != null;
   }

   public Element getElement(String var1) {
      Element var2 = (Element)this.elementHash.get(var1);
      if (var2 == null) {
         var2 = new Element(var1, this.elements.size());
         this.elements.addElement(var2);
         this.elementHash.put(var1, var2);
      }

      return var2;
   }

   public Element getElement(int var1) {
      return (Element)this.elements.elementAt(var1);
   }

   public Entity defineEntity(String var1, int var2, char[] var3) {
      Entity var4 = (Entity)this.entityHash.get(var1);
      if (var4 == null) {
         var4 = new Entity(var1, var2, var3);
         this.entityHash.put(var1, var4);
         if ((var2 & 65536) != 0 && var3.length == 1) {
            switch(var2 & -65537) {
            case 1:
            case 11:
               this.entityHash.put(Integer.valueOf(var3[0]), var4);
            }
         }
      }

      return var4;
   }

   public Element defineElement(String var1, int var2, boolean var3, boolean var4, ContentModel var5, BitSet var6, BitSet var7, AttributeList var8) {
      Element var9 = this.getElement(var1);
      var9.type = var2;
      var9.oStart = var3;
      var9.oEnd = var4;
      var9.content = var5;
      var9.exclusions = var6;
      var9.inclusions = var7;
      var9.atts = var8;
      return var9;
   }

   public void defineAttributes(String var1, AttributeList var2) {
      Element var3 = this.getElement(var1);
      var3.atts = var2;
   }

   public Entity defEntity(String var1, int var2, int var3) {
      char[] var4 = new char[]{(char)var3};
      return this.defineEntity(var1, var2, var4);
   }

   protected Entity defEntity(String var1, int var2, String var3) {
      int var4 = var3.length();
      char[] var5 = new char[var4];
      var3.getChars(0, var4, var5, 0);
      return this.defineEntity(var1, var2, var5);
   }

   protected Element defElement(String var1, int var2, boolean var3, boolean var4, ContentModel var5, String[] var6, String[] var7, AttributeList var8) {
      BitSet var9 = null;
      int var12;
      if (var6 != null && var6.length > 0) {
         var9 = new BitSet();
         String[] var10 = var6;
         int var11 = var6.length;

         for(var12 = 0; var12 < var11; ++var12) {
            String var13 = var10[var12];
            if (var13.length() > 0) {
               var9.set(this.getElement(var13).getIndex());
            }
         }
      }

      BitSet var15 = null;
      if (var7 != null && var7.length > 0) {
         var15 = new BitSet();
         String[] var16 = var7;
         var12 = var7.length;

         for(int var17 = 0; var17 < var12; ++var17) {
            String var14 = var16[var17];
            if (var14.length() > 0) {
               var15.set(this.getElement(var14).getIndex());
            }
         }
      }

      return this.defineElement(var1, var2, var3, var4, var5, var9, var15, var8);
   }

   protected AttributeList defAttributeList(String var1, int var2, int var3, String var4, String var5, AttributeList var6) {
      Vector var7 = null;
      if (var5 != null) {
         var7 = new Vector();
         StringTokenizer var8 = new StringTokenizer(var5, "|");

         while(var8.hasMoreTokens()) {
            String var9 = var8.nextToken();
            if (var9.length() > 0) {
               var7.addElement(var9);
            }
         }
      }

      return new AttributeList(var1, var2, var3, var4, var7, var6);
   }

   protected ContentModel defContentModel(int var1, Object var2, ContentModel var3) {
      return new ContentModel(var1, var2, var3);
   }

   public String toString() {
      return this.name;
   }

   public static void putDTDHash(String var0, DTD var1) {
      getDtdHash().put(var0, var1);
   }

   public static DTD getDTD(String var0) throws IOException {
      var0 = var0.toLowerCase();
      DTD var1 = (DTD)getDtdHash().get(var0);
      if (var1 == null) {
         var1 = new DTD(var0);
      }

      return var1;
   }

   private static Hashtable<String, DTD> getDtdHash() {
      AppContext var0 = AppContext.getAppContext();
      Hashtable var1 = (Hashtable)var0.get(DTD_HASH_KEY);
      if (var1 == null) {
         var1 = new Hashtable();
         var0.put(DTD_HASH_KEY, var1);
      }

      return var1;
   }

   public void read(DataInputStream var1) throws IOException {
      if (var1.readInt() != 1) {
      }

      String[] var2 = new String[var1.readShort()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var1.readUTF();
      }

      short var12 = var1.readShort();

      int var4;
      short var5;
      byte var6;
      for(var4 = 0; var4 < var12; ++var4) {
         var5 = var1.readShort();
         var6 = var1.readByte();
         String var7 = var1.readUTF();
         this.defEntity(var2[var5], var6 | 65536, var7);
      }

      var12 = var1.readShort();

      for(var4 = 0; var4 < var12; ++var4) {
         var5 = var1.readShort();
         var6 = var1.readByte();
         byte var13 = var1.readByte();
         ContentModel var8 = this.readContentModel(var1, var2);
         String[] var9 = this.readNameArray(var1, var2);
         String[] var10 = this.readNameArray(var1, var2);
         AttributeList var11 = this.readAttributeList(var1, var2);
         this.defElement(var2[var5], var6, (var13 & 1) != 0, (var13 & 2) != 0, var8, var9, var10, var11);
      }

   }

   private ContentModel readContentModel(DataInputStream var1, String[] var2) throws IOException {
      byte var3 = var1.readByte();
      byte var4;
      ContentModel var6;
      switch(var3) {
      case 0:
         return null;
      case 1:
         var4 = var1.readByte();
         ContentModel var7 = this.readContentModel(var1, var2);
         var6 = this.readContentModel(var1, var2);
         return this.defContentModel(var4, var7, var6);
      case 2:
         var4 = var1.readByte();
         Element var5 = this.getElement(var2[var1.readShort()]);
         var6 = this.readContentModel(var1, var2);
         return this.defContentModel(var4, var5, var6);
      default:
         throw new IOException("bad bdtd");
      }
   }

   private String[] readNameArray(DataInputStream var1, String[] var2) throws IOException {
      short var3 = var1.readShort();
      if (var3 == 0) {
         return null;
      } else {
         String[] var4 = new String[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var2[var1.readShort()];
         }

         return var4;
      }
   }

   private AttributeList readAttributeList(DataInputStream var1, String[] var2) throws IOException {
      AttributeList var3 = null;

      for(int var4 = var1.readByte(); var4 > 0; --var4) {
         short var5 = var1.readShort();
         byte var6 = var1.readByte();
         byte var7 = var1.readByte();
         short var8 = var1.readShort();
         String var9 = var8 == -1 ? null : var2[var8];
         Vector var10 = null;
         short var11 = var1.readShort();
         if (var11 > 0) {
            var10 = new Vector(var11);

            for(int var12 = 0; var12 < var11; ++var12) {
               var10.addElement(var2[var1.readShort()]);
            }
         }

         var3 = new AttributeList(var2[var5], var6, var7, var9, var10, var3);
      }

      return var3;
   }
}
