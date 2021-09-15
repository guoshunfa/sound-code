package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddPermButtonListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog td;
   private boolean editPolicyEntry;

   AddPermButtonListener(PolicyTool var1, ToolWindow var2, ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.editPolicyEntry = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.td.displayPermissionDialog(this.editPolicyEntry, false);
   }
}
