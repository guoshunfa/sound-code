package javax.swing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import sun.awt.ModalExclude;

public class Popup {
   private Component component;

   protected Popup(Component var1, Component var2, int var3, int var4) {
      this();
      if (var2 == null) {
         throw new IllegalArgumentException("Contents must be non-null");
      } else {
         this.reset(var1, var2, var3, var4);
      }
   }

   protected Popup() {
   }

   public void show() {
      Component var1 = this.getComponent();
      if (var1 != null) {
         var1.show();
      }

   }

   public void hide() {
      Component var1 = this.getComponent();
      if (var1 instanceof JWindow) {
         var1.hide();
         ((JWindow)var1).getContentPane().removeAll();
      }

      this.dispose();
   }

   void dispose() {
      Component var1 = this.getComponent();
      Window var2 = SwingUtilities.getWindowAncestor(var1);
      if (var1 instanceof JWindow) {
         ((Window)var1).dispose();
         var1 = null;
      }

      if (var2 instanceof Popup.DefaultFrame) {
         var2.dispose();
      }

   }

   void reset(Component var1, Component var2, int var3, int var4) {
      if (this.getComponent() == null) {
         this.component = this.createComponent(var1);
      }

      Component var5 = this.getComponent();
      if (var5 instanceof JWindow) {
         JWindow var6 = (JWindow)this.getComponent();
         var6.setLocation(var3, var4);
         var6.getContentPane().add((Component)var2, (Object)"Center");
         var6.invalidate();
         var6.validate();
         if (var6.isVisible()) {
            this.pack();
         }
      }

   }

   void pack() {
      Component var1 = this.getComponent();
      if (var1 instanceof Window) {
         ((Window)var1).pack();
      }

   }

   private Window getParentWindow(Component var1) {
      Object var2 = null;
      if (var1 instanceof Window) {
         var2 = (Window)var1;
      } else if (var1 != null) {
         var2 = SwingUtilities.getWindowAncestor(var1);
      }

      if (var2 == null) {
         var2 = new Popup.DefaultFrame();
      }

      return (Window)var2;
   }

   Component createComponent(Component var1) {
      return GraphicsEnvironment.isHeadless() ? null : new Popup.HeavyWeightWindow(this.getParentWindow(var1));
   }

   Component getComponent() {
      return this.component;
   }

   static class DefaultFrame extends Frame {
   }

   static class HeavyWeightWindow extends JWindow implements ModalExclude {
      HeavyWeightWindow(Window var1) {
         super(var1);
         this.setFocusableWindowState(false);
         this.setType(Window.Type.POPUP);
         this.getRootPane().setUseTrueDoubleBuffering(false);

         try {
            this.setAlwaysOnTop(true);
         } catch (SecurityException var3) {
         }

      }

      public void update(Graphics var1) {
         this.paint(var1);
      }

      public void show() {
         this.pack();
         if (this.getWidth() > 0 && this.getHeight() > 0) {
            super.show();
         }

      }
   }
}
