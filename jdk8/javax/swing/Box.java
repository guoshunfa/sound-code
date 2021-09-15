package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.beans.ConstructorProperties;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Box extends JComponent implements Accessible {
   public Box(int var1) {
      super.setLayout(new BoxLayout(this, var1));
   }

   public static Box createHorizontalBox() {
      return new Box(0);
   }

   public static Box createVerticalBox() {
      return new Box(1);
   }

   public static Component createRigidArea(Dimension var0) {
      return new Box.Filler(var0, var0, var0);
   }

   public static Component createHorizontalStrut(int var0) {
      return new Box.Filler(new Dimension(var0, 0), new Dimension(var0, 0), new Dimension(var0, 32767));
   }

   public static Component createVerticalStrut(int var0) {
      return new Box.Filler(new Dimension(0, var0), new Dimension(0, var0), new Dimension(32767, var0));
   }

   public static Component createGlue() {
      return new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
   }

   public static Component createHorizontalGlue() {
      return new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
   }

   public static Component createVerticalGlue() {
      return new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
   }

   public void setLayout(LayoutManager var1) {
      throw new AWTError("Illegal request");
   }

   protected void paintComponent(Graphics var1) {
      if (this.ui != null) {
         super.paintComponent(var1);
      } else if (this.isOpaque()) {
         var1.setColor(this.getBackground());
         var1.fillRect(0, 0, this.getWidth(), this.getHeight());
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Box.AccessibleBox();
      }

      return this.accessibleContext;
   }

   protected class AccessibleBox extends Container.AccessibleAWTContainer {
      protected AccessibleBox() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.FILLER;
      }
   }

   public static class Filler extends JComponent implements Accessible {
      @ConstructorProperties({"minimumSize", "preferredSize", "maximumSize"})
      public Filler(Dimension var1, Dimension var2, Dimension var3) {
         this.setMinimumSize(var1);
         this.setPreferredSize(var2);
         this.setMaximumSize(var3);
      }

      public void changeShape(Dimension var1, Dimension var2, Dimension var3) {
         this.setMinimumSize(var1);
         this.setPreferredSize(var2);
         this.setMaximumSize(var3);
         this.revalidate();
      }

      protected void paintComponent(Graphics var1) {
         if (this.ui != null) {
            super.paintComponent(var1);
         } else if (this.isOpaque()) {
            var1.setColor(this.getBackground());
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

      }

      public AccessibleContext getAccessibleContext() {
         if (this.accessibleContext == null) {
            this.accessibleContext = new Box.Filler.AccessibleBoxFiller();
         }

         return this.accessibleContext;
      }

      protected class AccessibleBoxFiller extends Component.AccessibleAWTComponent {
         protected AccessibleBoxFiller() {
            super();
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FILLER;
         }
      }
   }
}
