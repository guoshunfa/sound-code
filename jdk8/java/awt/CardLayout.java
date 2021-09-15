package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CardLayout implements LayoutManager2, Serializable {
   private static final long serialVersionUID = -4328196481005934313L;
   Vector<CardLayout.Card> vector;
   int currentCard;
   int hgap;
   int vgap;
   private static final ObjectStreamField[] serialPersistentFields;

   public CardLayout() {
      this(0, 0);
   }

   public CardLayout(int var1, int var2) {
      this.vector = new Vector();
      this.currentCard = 0;
      this.hgap = var1;
      this.vgap = var2;
   }

   public int getHgap() {
      return this.hgap;
   }

   public void setHgap(int var1) {
      this.hgap = var1;
   }

   public int getVgap() {
      return this.vgap;
   }

   public void setVgap(int var1) {
      this.vgap = var1;
   }

   public void addLayoutComponent(Component var1, Object var2) {
      synchronized(var1.getTreeLock()) {
         if (var2 == null) {
            var2 = "";
         }

         if (var2 instanceof String) {
            this.addLayoutComponent((String)var2, var1);
         } else {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addLayoutComponent(String var1, Component var2) {
      synchronized(var2.getTreeLock()) {
         if (!this.vector.isEmpty()) {
            var2.setVisible(false);
         }

         for(int var4 = 0; var4 < this.vector.size(); ++var4) {
            if (((CardLayout.Card)this.vector.get(var4)).name.equals(var1)) {
               ((CardLayout.Card)this.vector.get(var4)).comp = var2;
               return;
            }
         }

         this.vector.add(new CardLayout.Card(var1, var2));
      }
   }

   public void removeLayoutComponent(Component var1) {
      synchronized(var1.getTreeLock()) {
         for(int var3 = 0; var3 < this.vector.size(); ++var3) {
            if (((CardLayout.Card)this.vector.get(var3)).comp == var1) {
               if (var1.isVisible() && var1.getParent() != null) {
                  this.next(var1.getParent());
               }

               this.vector.remove(var3);
               if (this.currentCard > var3) {
                  --this.currentCard;
               }
               break;
            }
         }

      }
   }

   public Dimension preferredLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         int var5 = 0;
         int var6 = 0;

         for(int var7 = 0; var7 < var4; ++var7) {
            Component var8 = var1.getComponent(var7);
            Dimension var9 = var8.getPreferredSize();
            if (var9.width > var5) {
               var5 = var9.width;
            }

            if (var9.height > var6) {
               var6 = var9.height;
            }
         }

         return new Dimension(var3.left + var3.right + var5 + this.hgap * 2, var3.top + var3.bottom + var6 + this.vgap * 2);
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         int var5 = 0;
         int var6 = 0;

         for(int var7 = 0; var7 < var4; ++var7) {
            Component var8 = var1.getComponent(var7);
            Dimension var9 = var8.getMinimumSize();
            if (var9.width > var5) {
               var5 = var9.width;
            }

            if (var9.height > var6) {
               var6 = var9.height;
            }
         }

         return new Dimension(var3.left + var3.right + var5 + this.hgap * 2, var3.top + var3.bottom + var6 + this.vgap * 2);
      }
   }

   public Dimension maximumLayoutSize(Container var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public float getLayoutAlignmentX(Container var1) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container var1) {
      return 0.5F;
   }

   public void invalidateLayout(Container var1) {
   }

   public void layoutContainer(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         Component var5 = null;
         boolean var6 = false;

         for(int var7 = 0; var7 < var4; ++var7) {
            var5 = var1.getComponent(var7);
            var5.setBounds(this.hgap + var3.left, this.vgap + var3.top, var1.width - (this.hgap * 2 + var3.left + var3.right), var1.height - (this.vgap * 2 + var3.top + var3.bottom));
            if (var5.isVisible()) {
               var6 = true;
            }
         }

         if (!var6 && var4 > 0) {
            var1.getComponent(0).setVisible(true);
         }

      }
   }

   void checkLayout(Container var1) {
      if (var1.getLayout() != this) {
         throw new IllegalArgumentException("wrong parent for CardLayout");
      }
   }

   public void first(Container var1) {
      synchronized(var1.getTreeLock()) {
         this.checkLayout(var1);
         int var3 = var1.getComponentCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var1.getComponent(var4);
            if (var5.isVisible()) {
               var5.setVisible(false);
               break;
            }
         }

         if (var3 > 0) {
            this.currentCard = 0;
            var1.getComponent(0).setVisible(true);
            var1.validate();
         }

      }
   }

   public void next(Container var1) {
      synchronized(var1.getTreeLock()) {
         this.checkLayout(var1);
         int var3 = var1.getComponentCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var1.getComponent(var4);
            if (var5.isVisible()) {
               var5.setVisible(false);
               this.currentCard = (var4 + 1) % var3;
               var5 = var1.getComponent(this.currentCard);
               var5.setVisible(true);
               var1.validate();
               return;
            }
         }

         this.showDefaultComponent(var1);
      }
   }

   public void previous(Container var1) {
      synchronized(var1.getTreeLock()) {
         this.checkLayout(var1);
         int var3 = var1.getComponentCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var1.getComponent(var4);
            if (var5.isVisible()) {
               var5.setVisible(false);
               this.currentCard = var4 > 0 ? var4 - 1 : var3 - 1;
               var5 = var1.getComponent(this.currentCard);
               var5.setVisible(true);
               var1.validate();
               return;
            }
         }

         this.showDefaultComponent(var1);
      }
   }

   void showDefaultComponent(Container var1) {
      if (var1.getComponentCount() > 0) {
         this.currentCard = 0;
         var1.getComponent(0).setVisible(true);
         var1.validate();
      }

   }

   public void last(Container var1) {
      synchronized(var1.getTreeLock()) {
         this.checkLayout(var1);
         int var3 = var1.getComponentCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var1.getComponent(var4);
            if (var5.isVisible()) {
               var5.setVisible(false);
               break;
            }
         }

         if (var3 > 0) {
            this.currentCard = var3 - 1;
            var1.getComponent(this.currentCard).setVisible(true);
            var1.validate();
         }

      }
   }

   public void show(Container var1, String var2) {
      synchronized(var1.getTreeLock()) {
         this.checkLayout(var1);
         Component var4 = null;
         int var5 = this.vector.size();

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            CardLayout.Card var7 = (CardLayout.Card)this.vector.get(var6);
            if (var7.name.equals(var2)) {
               var4 = var7.comp;
               this.currentCard = var6;
               break;
            }
         }

         if (var4 != null && !var4.isVisible()) {
            var5 = var1.getComponentCount();

            for(var6 = 0; var6 < var5; ++var6) {
               Component var10 = var1.getComponent(var6);
               if (var10.isVisible()) {
                  var10.setVisible(false);
                  break;
               }
            }

            var4.setVisible(true);
            var1.validate();
         }

      }
   }

   public String toString() {
      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.hgap = var2.get("hgap", (int)0);
      this.vgap = var2.get("vgap", (int)0);
      if (var2.defaulted("vector")) {
         Hashtable var3 = (Hashtable)var2.get("tab", (Object)null);
         this.vector = new Vector();
         if (var3 != null && !var3.isEmpty()) {
            Enumeration var4 = var3.keys();

            while(var4.hasMoreElements()) {
               String var5 = (String)var4.nextElement();
               Component var6 = (Component)var3.get(var5);
               this.vector.add(new CardLayout.Card(var5, var6));
               if (var6.isVisible()) {
                  this.currentCard = this.vector.size() - 1;
               }
            }
         }
      } else {
         this.vector = (Vector)var2.get("vector", (Object)null);
         this.currentCard = var2.get("currentCard", (int)0);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = new Hashtable();
      int var3 = this.vector.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         CardLayout.Card var5 = (CardLayout.Card)this.vector.get(var4);
         var2.put(var5.name, var5.comp);
      }

      ObjectOutputStream.PutField var6 = var1.putFields();
      var6.put("hgap", this.hgap);
      var6.put("vgap", this.vgap);
      var6.put("vector", this.vector);
      var6.put("currentCard", this.currentCard);
      var6.put("tab", var2);
      var1.writeFields();
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("tab", Hashtable.class), new ObjectStreamField("hgap", Integer.TYPE), new ObjectStreamField("vgap", Integer.TYPE), new ObjectStreamField("vector", Vector.class), new ObjectStreamField("currentCard", Integer.TYPE)};
   }

   class Card implements Serializable {
      static final long serialVersionUID = 6640330810709497518L;
      public String name;
      public Component comp;

      public Card(String var2, Component var3) {
         this.name = var2;
         this.comp = var3;
      }
   }
}
