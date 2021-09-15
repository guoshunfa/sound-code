package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePermButtonListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog td;
   private boolean edit;

   RemovePermButtonListener(PolicyTool var1, ToolWindow var2, ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.edit = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      TaggedList var2 = (TaggedList)this.td.getComponent(8);
      int var3 = var2.getSelectedIndex();
      if (var3 < 0) {
         this.tw.displayErrorDialog(this.td, (Throwable)(new Exception(PolicyTool.getMessage("No.permission.selected"))));
      } else {
         var2.removeTaggedItem(var3);
      }
   }
}
