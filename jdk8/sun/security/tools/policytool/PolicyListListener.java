package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PolicyListListener extends MouseAdapter implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;

   PolicyListListener(PolicyTool var1, ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      ToolDialog var2 = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
      var2.displayPolicyEntryDialog(true);
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 2) {
         this.actionPerformed((ActionEvent)null);
      }

   }
}
