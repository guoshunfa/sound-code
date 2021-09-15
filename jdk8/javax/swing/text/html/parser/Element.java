package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Hashtable;
import sun.awt.AppContext;

public final class Element implements DTDConstants, Serializable {
   public int index;
   public String name;
   public boolean oStart;
   public boolean oEnd;
   public BitSet inclusions;
   public BitSet exclusions;
   public int type = 19;
   public ContentModel content;
   public AttributeList atts;
   public Object data;
   private static final Object MAX_INDEX_KEY = new Object();
   static Hashtable<String, Integer> contentTypes = new Hashtable();

   Element() {
   }

   Element(String var1, int var2) {
      this.name = var1;
      this.index = var2;
      if (var2 > getMaxIndex()) {
         AppContext.getAppContext().put(MAX_INDEX_KEY, var2);
      }

   }

   static int getMaxIndex() {
      Integer var0 = (Integer)AppContext.getAppContext().get(MAX_INDEX_KEY);
      return var0 != null ? var0 : 0;
   }

   public String getName() {
      return this.name;
   }

   public boolean omitStart() {
      return this.oStart;
   }

   public boolean omitEnd() {
      return this.oEnd;
   }

   public int getType() {
      return this.type;
   }

   public ContentModel getContent() {
      return this.content;
   }

   public AttributeList getAttributes() {
      return this.atts;
   }

   public int getIndex() {
      return this.index;
   }

   public boolean isEmpty() {
      return this.type == 17;
   }

   public String toString() {
      return this.name;
   }

   public AttributeList getAttribute(String var1) {
      for(AttributeList var2 = this.atts; var2 != null; var2 = var2.next) {
         if (var2.name.equals(var1)) {
            return var2;
         }
      }

      return null;
   }

   public AttributeList getAttributeByValue(String var1) {
      for(AttributeList var2 = this.atts; var2 != null; var2 = var2.next) {
         if (var2.values != null && var2.values.contains(var1)) {
            return var2;
         }
      }

      return null;
   }

   public static int name2type(String var0) {
      Integer var1 = (Integer)contentTypes.get(var0);
      return var1 != null ? var1 : 0;
   }

   static {
      contentTypes.put("CDATA", 1);
      contentTypes.put("RCDATA", 16);
      contentTypes.put("EMPTY", 17);
      contentTypes.put("ANY", 19);
   }
}
