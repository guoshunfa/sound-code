package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import sun.security.provider.PolicyParser;

class NewPolicyPermOKButtonListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog listDialog;
   private ToolDialog infoDialog;
   private boolean edit;

   NewPolicyPermOKButtonListener(PolicyTool var1, ToolWindow var2, ToolDialog var3, ToolDialog var4, boolean var5) {
      this.tool = var1;
      this.tw = var2;
      this.listDialog = var3;
      this.infoDialog = var4;
      this.edit = var5;
   }

   public void actionPerformed(ActionEvent var1) {
      try {
         PolicyParser.PermissionEntry var2 = this.infoDialog.getPermFromDialog();

         try {
            this.tool.verifyPermission(var2.permission, var2.name, var2.action);
         } catch (ClassNotFoundException var6) {
            MessageFormat var4 = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
            Object[] var5 = new Object[]{var2.permission};
            this.tool.warnings.addElement(var4.format(var5));
            this.tw.displayStatusDialog(this.infoDialog, var4.format(var5));
         }

         TaggedList var3 = (TaggedList)this.listDialog.getComponent(8);
         String var9 = ToolDialog.PermissionEntryToUserFriendlyString(var2);
         if (this.edit) {
            int var10 = var3.getSelectedIndex();
            var3.replaceTaggedItem(var9, var2, var10);
         } else {
            var3.addTaggedItem(var9, var2);
         }

         this.infoDialog.dispose();
      } catch (InvocationTargetException var7) {
         this.tw.displayErrorDialog(this.infoDialog, (Throwable)var7.getTargetException());
      } catch (Exception var8) {
         this.tw.displayErrorDialog(this.infoDialog, (Throwable)var8);
      }

   }
}
