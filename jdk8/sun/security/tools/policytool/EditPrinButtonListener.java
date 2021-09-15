package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class EditPrinButtonListener extends MouseAdapter implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog td;
   private boolean editPolicyEntry;

   EditPrinButtonListener(PolicyTool var1, ToolWindow var2, ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.editPolicyEntry = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      TaggedList var2 = (TaggedList)this.td.getComponent(6);
      int var3 = var2.getSelectedIndex();
      if (var3 < 0) {
         this.tw.displayErrorDialog(this.td, (Throwable)(new Exception(PolicyTool.getMessage("No.principal.selected"))));
      } else {
         this.td.displayPrincipalDialog(this.editPolicyEntry, true);
      }
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 2) {
         this.actionPerformed((ActionEvent)null);
      }

   }
}
