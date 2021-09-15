package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.SunToolkit;

public class JLayeredPane extends JComponent implements Accessible {
   public static final Integer DEFAULT_LAYER = new Integer(0);
   public static final Integer PALETTE_LAYER = new Integer(100);
   public static final Integer MODAL_LAYER = new Integer(200);
   public static final Integer POPUP_LAYER = new Integer(300);
   public static final Integer DRAG_LAYER = new Integer(400);
   public static final Integer FRAME_CONTENT_LAYER = new Integer(-30000);
   public static final String LAYER_PROPERTY = "layeredContainerLayer";
   private Hashtable<Component, Integer> componentToLayer;
   private boolean optimizedDrawingPossible = true;

   public JLayeredPane() {
      this.setLayout((LayoutManager)null);
   }

   private void validateOptimizedDrawing() {
      boolean var1 = false;
      synchronized(this.getTreeLock()) {
         Component[] var4 = this.getComponents();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Component var7 = var4[var6];
            Integer var3 = null;
            if ((SunToolkit.isInstanceOf((Object)var7, "javax.swing.JInternalFrame") || var7 instanceof JComponent && (var3 = (Integer)((JComponent)var7).getClientProperty("layeredContainerLayer")) != null) && (var3 == null || !var3.equals(FRAME_CONTENT_LAYER))) {
               var1 = true;
               break;
            }
         }
      }

      if (var1) {
         this.optimizedDrawingPossible = false;
      } else {
         this.optimizedDrawingPossible = true;
      }

   }

   protected void addImpl(Component var1, Object var2, int var3) {
      int var4;
      if (var2 instanceof Integer) {
         var4 = (Integer)var2;
         this.setLayer(var1, var4);
      } else {
         var4 = this.getLayer(var1);
      }

      int var5 = this.insertIndexForLayer(var4, var3);
      super.addImpl(var1, var2, var5);
      var1.validate();
      var1.repaint();
      this.validateOptimizedDrawing();
   }

   public void remove(int var1) {
      Component var2 = this.getComponent(var1);
      super.remove(var1);
      if (var2 != null && !(var2 instanceof JComponent)) {
         this.getComponentToLayer().remove(var2);
      }

      this.validateOptimizedDrawing();
   }

   public void removeAll() {
      Component[] var1 = this.getComponents();
      Hashtable var2 = this.getComponentToLayer();

      for(int var3 = var1.length - 1; var3 >= 0; --var3) {
         Component var4 = var1[var3];
         if (var4 != null && !(var4 instanceof JComponent)) {
            var2.remove(var4);
         }
      }

      super.removeAll();
   }

   public boolean isOptimizedDrawingEnabled() {
      return this.optimizedDrawingPossible;
   }

   public static void putLayer(JComponent var0, int var1) {
      Integer var2 = new Integer(var1);
      var0.putClientProperty("layeredContainerLayer", var2);
   }

   public static int getLayer(JComponent var0) {
      Integer var1;
      return (var1 = (Integer)var0.getClientProperty("layeredContainerLayer")) != null ? var1 : DEFAULT_LAYER;
   }

   public static JLayeredPane getLayeredPaneAbove(Component var0) {
      if (var0 == null) {
         return null;
      } else {
         Container var1;
         for(var1 = var0.getParent(); var1 != null && !(var1 instanceof JLayeredPane); var1 = var1.getParent()) {
         }

         return (JLayeredPane)var1;
      }
   }

   public void setLayer(Component var1, int var2) {
      this.setLayer(var1, var2, -1);
   }

   public void setLayer(Component var1, int var2, int var3) {
      Integer var4 = this.getObjectForLayer(var2);
      if (var2 == this.getLayer(var1) && var3 == this.getPosition(var1)) {
         this.repaint(var1.getBounds());
      } else {
         if (var1 instanceof JComponent) {
            ((JComponent)var1).putClientProperty("layeredContainerLayer", var4);
         } else {
            this.getComponentToLayer().put(var1, var4);
         }

         if (var1.getParent() != null && var1.getParent() == this) {
            int var5 = this.insertIndexForLayer(var1, var2, var3);
            this.setComponentZOrder(var1, var5);
            this.repaint(var1.getBounds());
         } else {
            this.repaint(var1.getBounds());
         }
      }
   }

   public int getLayer(Component var1) {
      Integer var2;
      if (var1 instanceof JComponent) {
         var2 = (Integer)((JComponent)var1).getClientProperty("layeredContainerLayer");
      } else {
         var2 = (Integer)this.getComponentToLayer().get(var1);
      }

      return var2 == null ? DEFAULT_LAYER : var2;
   }

   public int getIndexOf(Component var1) {
      int var3 = this.getComponentCount();

      for(int var2 = 0; var2 < var3; ++var2) {
         if (var1 == this.getComponent(var2)) {
            return var2;
         }
      }

      return -1;
   }

   public void moveToFront(Component var1) {
      this.setPosition(var1, 0);
   }

   public void moveToBack(Component var1) {
      this.setPosition(var1, -1);
   }

   public void setPosition(Component var1, int var2) {
      this.setLayer(var1, this.getLayer(var1), var2);
   }

   public int getPosition(Component var1) {
      int var6 = 0;
      this.getComponentCount();
      int var5 = this.getIndexOf(var1);
      if (var5 == -1) {
         return -1;
      } else {
         int var3 = this.getLayer(var1);

         for(int var2 = var5 - 1; var2 >= 0; --var2) {
            int var4 = this.getLayer(this.getComponent(var2));
            if (var4 != var3) {
               return var6;
            }

            ++var6;
         }

         return var6;
      }
   }

   public int highestLayer() {
      return this.getComponentCount() > 0 ? this.getLayer(this.getComponent(0)) : 0;
   }

   public int lowestLayer() {
      int var1 = this.getComponentCount();
      return var1 > 0 ? this.getLayer(this.getComponent(var1 - 1)) : 0;
   }

   public int getComponentCountInLayer(int var1) {
      int var5 = 0;
      int var3 = this.getComponentCount();

      for(int var2 = 0; var2 < var3; ++var2) {
         int var4 = this.getLayer(this.getComponent(var2));
         if (var4 == var1) {
            ++var5;
         } else if (var5 > 0 || var4 < var1) {
            break;
         }
      }

      return var5;
   }

   public Component[] getComponentsInLayer(int var1) {
      int var5 = 0;
      Component[] var6 = new Component[this.getComponentCountInLayer(var1)];
      int var3 = this.getComponentCount();

      for(int var2 = 0; var2 < var3; ++var2) {
         int var4 = this.getLayer(this.getComponent(var2));
         if (var4 == var1) {
            var6[var5++] = this.getComponent(var2);
         } else if (var5 > 0 || var4 < var1) {
            break;
         }
      }

      return var6;
   }

   public void paint(Graphics var1) {
      if (this.isOpaque()) {
         Rectangle var2 = var1.getClipBounds();
         Color var3 = this.getBackground();
         if (var3 == null) {
            var3 = Color.lightGray;
         }

         var1.setColor(var3);
         if (var2 != null) {
            var1.fillRect(var2.x, var2.y, var2.width, var2.height);
         } else {
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
         }
      }

      super.paint(var1);
   }

   protected Hashtable<Component, Integer> getComponentToLayer() {
      if (this.componentToLayer == null) {
         this.componentToLayer = new Hashtable(4);
      }

      return this.componentToLayer;
   }

   protected Integer getObjectForLayer(int var1) {
      Integer var2;
      switch(var1) {
      case 0:
         var2 = DEFAULT_LAYER;
         break;
      case 100:
         var2 = PALETTE_LAYER;
         break;
      case 200:
         var2 = MODAL_LAYER;
         break;
      case 300:
         var2 = POPUP_LAYER;
         break;
      case 400:
         var2 = DRAG_LAYER;
         break;
      default:
         var2 = new Integer(var1);
      }

      return var2;
   }

   protected int insertIndexForLayer(int var1, int var2) {
      return this.insertIndexForLayer((Component)null, var1, var2);
   }

   private int insertIndexForLayer(Component var1, int var2, int var3) {
      int var7 = -1;
      int var8 = -1;
      int var9 = this.getComponentCount();
      ArrayList var10 = new ArrayList(var9);

      for(int var11 = 0; var11 < var9; ++var11) {
         if (this.getComponent(var11) != var1) {
            var10.add(this.getComponent(var11));
         }
      }

      int var5 = var10.size();

      for(int var4 = 0; var4 < var5; ++var4) {
         int var6 = this.getLayer((Component)var10.get(var4));
         if (var7 == -1 && var6 == var2) {
            var7 = var4;
         }

         if (var6 < var2) {
            if (var4 == 0) {
               var7 = 0;
               var8 = 0;
            } else {
               var8 = var4;
            }
            break;
         }
      }

      if (var7 == -1 && var8 == -1) {
         return var5;
      } else {
         if (var7 != -1 && var8 == -1) {
            var8 = var5;
         }

         if (var8 != -1 && var7 == -1) {
            var7 = var8;
         }

         if (var3 == -1) {
            return var8;
         } else {
            return var3 > -1 && var7 + var3 <= var8 ? var7 + var3 : var8;
         }
      }
   }

   protected String paramString() {
      String var1 = this.optimizedDrawingPossible ? "true" : "false";
      return super.paramString() + ",optimizedDrawingPossible=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JLayeredPane.AccessibleJLayeredPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJLayeredPane extends JComponent.AccessibleJComponent {
      protected AccessibleJLayeredPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.LAYERED_PANE;
      }
   }
}
