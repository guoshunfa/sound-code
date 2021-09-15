package sun.awt.im;

import javax.swing.JFrame;

public class InputMethodJFrame extends JFrame implements InputMethodWindow {
   InputContext inputContext = null;
   private static final long serialVersionUID = -4705856747771842549L;

   public InputMethodJFrame(String var1, InputContext var2) {
      super(var1);
      if (JFrame.isDefaultLookAndFeelDecorated()) {
         this.setUndecorated(true);
         this.getRootPane().setWindowDecorationStyle(0);
      }

      if (var2 != null) {
         this.inputContext = var2;
      }

      this.setFocusableWindowState(false);
   }

   public void setInputContext(InputContext var1) {
      this.inputContext = var1;
   }

   public java.awt.im.InputContext getInputContext() {
      return (java.awt.im.InputContext)(this.inputContext != null ? this.inputContext : super.getInputContext());
   }
}
