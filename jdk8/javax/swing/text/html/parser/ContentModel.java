package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Vector;

public final class ContentModel implements Serializable {
   public int type;
   public Object content;
   public ContentModel next;
   private boolean[] valSet;
   private boolean[] val;

   public ContentModel() {
   }

   public ContentModel(Element var1) {
      this(0, var1, (ContentModel)null);
   }

   public ContentModel(int var1, ContentModel var2) {
      this(var1, var2, (ContentModel)null);
   }

   public ContentModel(int var1, Object var2, ContentModel var3) {
      this.type = var1;
      this.content = var2;
      this.next = var3;
   }

   public boolean empty() {
      ContentModel var1;
      switch(this.type) {
      case 38:
      case 44:
         for(var1 = (ContentModel)this.content; var1 != null; var1 = var1.next) {
            if (!var1.empty()) {
               return false;
            }
         }

         return true;
      case 42:
      case 63:
         return true;
      case 43:
      case 124:
         for(var1 = (ContentModel)this.content; var1 != null; var1 = var1.next) {
            if (var1.empty()) {
               return true;
            }
         }

         return false;
      default:
         return false;
      }
   }

   public void getElements(Vector<Element> var1) {
      switch(this.type) {
      case 38:
      case 44:
      case 124:
         for(ContentModel var2 = (ContentModel)this.content; var2 != null; var2 = var2.next) {
            var2.getElements(var1);
         }

         return;
      case 42:
      case 43:
      case 63:
         ((ContentModel)this.content).getElements(var1);
         break;
      default:
         var1.addElement((Element)this.content);
      }

   }

   public boolean first(Object var1) {
      switch(this.type) {
      case 38:
      case 124:
         Element var4 = (Element)var1;
         if (this.valSet == null || this.valSet.length <= Element.getMaxIndex()) {
            this.valSet = new boolean[Element.getMaxIndex() + 1];
            this.val = new boolean[this.valSet.length];
         }

         if (this.valSet[var4.index]) {
            return this.val[var4.index];
         } else {
            for(ContentModel var3 = (ContentModel)this.content; var3 != null; var3 = var3.next) {
               if (var3.first(var1)) {
                  this.val[var4.index] = true;
                  break;
               }
            }

            this.valSet[var4.index] = true;
            return this.val[var4.index];
         }
      case 42:
      case 43:
      case 63:
         return ((ContentModel)this.content).first(var1);
      case 44:
         for(ContentModel var2 = (ContentModel)this.content; var2 != null; var2 = var2.next) {
            if (var2.first(var1)) {
               return true;
            }

            if (!var2.empty()) {
               return false;
            }
         }

         return false;
      default:
         return this.content == var1;
      }
   }

   public Element first() {
      switch(this.type) {
      case 38:
      case 42:
      case 63:
      case 124:
         return null;
      case 43:
      case 44:
         return ((ContentModel)this.content).first();
      default:
         return (Element)this.content;
      }
   }

   public String toString() {
      switch(this.type) {
      case 38:
      case 44:
      case 124:
         char[] var1 = new char[]{' ', (char)this.type, ' '};
         String var2 = "";

         for(ContentModel var3 = (ContentModel)this.content; var3 != null; var3 = var3.next) {
            var2 = var2 + var3;
            if (var3.next != null) {
               var2 = var2 + new String(var1);
            }
         }

         return "(" + var2 + ")";
      case 42:
         return this.content + "*";
      case 43:
         return this.content + "+";
      case 63:
         return this.content + "?";
      default:
         return this.content.toString();
      }
   }
}
