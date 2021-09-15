package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ToolWindowListener implements WindowListener {
   private PolicyTool tool;
   private ToolWindow tw;

   ToolWindowListener(PolicyTool var1, ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void windowOpened(WindowEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
      ToolDialog var2 = new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
      var2.displayUserSave(1);
   }

   public void windowClosed(WindowEvent var1) {
      System.exit(0);
   }

   public void windowIconified(WindowEvent var1) {
   }

   public void windowDeiconified(WindowEvent var1) {
   }

   public void windowActivated(WindowEvent var1) {
   }

   public void windowDeactivated(WindowEvent var1) {
   }
}
