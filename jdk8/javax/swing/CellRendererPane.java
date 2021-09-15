package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class CellRendererPane extends Container implements Accessible {
   protected AccessibleContext accessibleContext = null;

   public CellRendererPane() {
      this.setLayout((LayoutManager)null);
      this.setVisible(false);
   }

   public void invalidate() {
   }

   public void paint(Graphics var1) {
   }

   public void update(Graphics var1) {
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (var1.getParent() != this) {
         super.addImpl(var1, var2, var3);
      }
   }

   public void paintComponent(Graphics var1, Component var2, Container var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (var2 == null) {
         if (var3 != null) {
            Color var14 = var1.getColor();
            var1.setColor(var3.getBackground());
            var1.fillRect(var4, var5, var6, var7);
            var1.setColor(var14);
         }

      } else {
         if (var2.getParent() != this) {
            this.add(var2);
         }

         var2.setBounds(var4, var5, var6, var7);
         if (var8) {
            var2.validate();
         }

         boolean var9 = false;
         if (var2 instanceof JComponent && ((JComponent)var2).isDoubleBuffered()) {
            var9 = true;
            ((JComponent)var2).setDoubleBuffered(false);
         }

         Graphics var10 = var1.create(var4, var5, var6, var7);

         try {
            var2.paint(var10);
         } finally {
            var10.dispose();
         }

         if (var9 && var2 instanceof JComponent) {
            ((JComponent)var2).setDoubleBuffered(true);
         }

         var2.setBounds(-var6, -var7, 0, 0);
      }
   }

   public void paintComponent(Graphics var1, Component var2, Container var3, int var4, int var5, int var6, int var7) {
      this.paintComponent(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public void paintComponent(Graphics var1, Component var2, Container var3, Rectangle var4) {
      this.paintComponent(var1, var2, var3, var4.x, var4.y, var4.width, var4.height);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.removeAll();
      var1.defaultWriteObject();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new CellRendererPane.AccessibleCellRendererPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleCellRendererPane extends Container.AccessibleAWTContainer {
      protected AccessibleCellRendererPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PANEL;
      }
   }
}
