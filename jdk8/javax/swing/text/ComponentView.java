package javax.swing.text;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.SwingUtilities;

public class ComponentView extends View {
   private Component createdC;
   private ComponentView.Invalidator c;

   public ComponentView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      AttributeSet var1 = this.getElement().getAttributes();
      Component var2 = StyleConstants.getComponent(var1);
      return var2;
   }

   public final Component getComponent() {
      return this.createdC;
   }

   public void paint(Graphics var1, Shape var2) {
      if (this.c != null) {
         Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
         this.c.setBounds(var3.x, var3.y, var3.width, var3.height);
      }

   }

   public float getPreferredSpan(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("Invalid axis: " + var1);
      } else if (this.c != null) {
         Dimension var2 = this.c.getPreferredSize();
         return var1 == 0 ? (float)var2.width : (float)var2.height;
      } else {
         return 0.0F;
      }
   }

   public float getMinimumSpan(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("Invalid axis: " + var1);
      } else if (this.c != null) {
         Dimension var2 = this.c.getMinimumSize();
         return var1 == 0 ? (float)var2.width : (float)var2.height;
      } else {
         return 0.0F;
      }
   }

   public float getMaximumSpan(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("Invalid axis: " + var1);
      } else if (this.c != null) {
         Dimension var2 = this.c.getMaximumSize();
         return var1 == 0 ? (float)var2.width : (float)var2.height;
      } else {
         return 0.0F;
      }
   }

   public float getAlignment(int var1) {
      if (this.c != null) {
         switch(var1) {
         case 0:
            return this.c.getAlignmentX();
         case 1:
            return this.c.getAlignmentY();
         }
      }

      return super.getAlignment(var1);
   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (SwingUtilities.isEventDispatchThread()) {
         this.setComponentParent();
      } else {
         Runnable var2 = new Runnable() {
            public void run() {
               Document var1 = ComponentView.this.getDocument();

               try {
                  if (var1 instanceof AbstractDocument) {
                     ((AbstractDocument)var1).readLock();
                  }

                  ComponentView.this.setComponentParent();
                  Container var2 = ComponentView.this.getContainer();
                  if (var2 != null) {
                     ComponentView.this.preferenceChanged((View)null, true, true);
                     var2.repaint();
                  }
               } finally {
                  if (var1 instanceof AbstractDocument) {
                     ((AbstractDocument)var1).readUnlock();
                  }

               }

            }
         };
         SwingUtilities.invokeLater(var2);
      }

   }

   void setComponentParent() {
      View var1 = this.getParent();
      Container var2;
      if (var1 != null) {
         var2 = this.getContainer();
         if (var2 != null) {
            if (this.c == null) {
               Component var3 = this.createComponent();
               if (var3 != null) {
                  this.createdC = var3;
                  this.c = new ComponentView.Invalidator(var3);
               }
            }

            if (this.c != null && this.c.getParent() == null) {
               var2.add((Component)this.c, (Object)this);
               var2.addPropertyChangeListener("enabled", this.c);
            }
         }
      } else if (this.c != null) {
         var2 = this.c.getParent();
         if (var2 != null) {
            var2.remove(this.c);
            var2.removePropertyChangeListener("enabled", this.c);
         }
      }

   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      int var4 = this.getStartOffset();
      int var5 = this.getEndOffset();
      if (var1 >= var4 && var1 <= var5) {
         Rectangle var6 = var2.getBounds();
         if (var1 == var5) {
            var6.x += var6.width;
         }

         var6.width = 0;
         return var6;
      } else {
         throw new BadLocationException(var1 + " not in range " + var4 + "," + var5, var1);
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      Rectangle var5 = (Rectangle)var3;
      if (var1 < (float)(var5.x + var5.width / 2)) {
         var4[0] = Position.Bias.Forward;
         return this.getStartOffset();
      } else {
         var4[0] = Position.Bias.Backward;
         return this.getEndOffset();
      }
   }

   class Invalidator extends Container implements PropertyChangeListener {
      Dimension min;
      Dimension pref;
      Dimension max;
      float yalign;
      float xalign;

      Invalidator(Component var2) {
         this.setLayout((LayoutManager)null);
         this.add(var2);
         this.cacheChildSizes();
      }

      public void invalidate() {
         super.invalidate();
         if (this.getParent() != null) {
            ComponentView.this.preferenceChanged((View)null, true, true);
         }

      }

      public void doLayout() {
         this.cacheChildSizes();
      }

      public void setBounds(int var1, int var2, int var3, int var4) {
         super.setBounds(var1, var2, var3, var4);
         if (this.getComponentCount() > 0) {
            this.getComponent(0).setSize(var3, var4);
         }

         this.cacheChildSizes();
      }

      public void validateIfNecessary() {
         if (!this.isValid()) {
            this.validate();
         }

      }

      private void cacheChildSizes() {
         if (this.getComponentCount() > 0) {
            Component var1 = this.getComponent(0);
            this.min = var1.getMinimumSize();
            this.pref = var1.getPreferredSize();
            this.max = var1.getMaximumSize();
            this.yalign = var1.getAlignmentY();
            this.xalign = var1.getAlignmentX();
         } else {
            this.min = this.pref = this.max = new Dimension(0, 0);
         }

      }

      public void setVisible(boolean var1) {
         super.setVisible(var1);
         if (this.getComponentCount() > 0) {
            this.getComponent(0).setVisible(var1);
         }

      }

      public boolean isShowing() {
         return true;
      }

      public Dimension getMinimumSize() {
         this.validateIfNecessary();
         return this.min;
      }

      public Dimension getPreferredSize() {
         this.validateIfNecessary();
         return this.pref;
      }

      public Dimension getMaximumSize() {
         this.validateIfNecessary();
         return this.max;
      }

      public float getAlignmentX() {
         this.validateIfNecessary();
         return this.xalign;
      }

      public float getAlignmentY() {
         this.validateIfNecessary();
         return this.yalign;
      }

      public Set<AWTKeyStroke> getFocusTraversalKeys(int var1) {
         return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(var1);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         Boolean var2 = (Boolean)var1.getNewValue();
         if (this.getComponentCount() > 0) {
            this.getComponent(0).setEnabled(var2);
         }

      }
   }
}
