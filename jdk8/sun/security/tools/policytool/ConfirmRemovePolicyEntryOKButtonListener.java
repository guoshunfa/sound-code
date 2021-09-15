package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class ConfirmRemovePolicyEntryOKButtonListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog us;

   ConfirmRemovePolicyEntryOKButtonListener(PolicyTool var1, ToolWindow var2, ToolDialog var3) {
      this.tool = var1;
      this.tw = var2;
      this.us = var3;
   }

   public void actionPerformed(ActionEvent var1) {
      JList var2 = (JList)this.tw.getComponent(3);
      int var3 = var2.getSelectedIndex();
      PolicyEntry[] var4 = this.tool.getEntry();
      this.tool.removeEntry(var4[var3]);
      DefaultListModel var5 = new DefaultListModel();
      var2 = new JList(var5);
      var2.setVisibleRowCount(15);
      var2.setSelectionMode(0);
      var2.addMouseListener(new PolicyListListener(this.tool, this.tw));
      var4 = this.tool.getEntry();
      if (var4 != null) {
         for(int var6 = 0; var6 < var4.length; ++var6) {
            var5.addElement(var4[var6].headerToString());
         }
      }

      this.tw.replacePolicyList(var2);
      this.us.setVisible(false);
      this.us.dispose();
   }
}
