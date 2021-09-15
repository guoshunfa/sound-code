package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionMenuListener implements ItemListener {
   private ToolDialog td;

   PermissionMenuListener(ToolDialog var1) {
      this.td = var1;
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() != 2) {
         JComboBox var2 = (JComboBox)this.td.getComponent(1);
         JComboBox var3 = (JComboBox)this.td.getComponent(3);
         JComboBox var4 = (JComboBox)this.td.getComponent(5);
         JTextField var5 = (JTextField)this.td.getComponent(4);
         JTextField var6 = (JTextField)this.td.getComponent(6);
         JTextField var7 = (JTextField)this.td.getComponent(2);
         JTextField var8 = (JTextField)this.td.getComponent(8);
         var2.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)var1.getItem()));
         Perm var9;
         if (PolicyTool.collator.compare((String)var1.getItem(), ToolDialog.PERM) == 0) {
            if (var7.getText() != null && var7.getText().length() > 0) {
               var9 = ToolDialog.getPerm(var7.getText(), true);
               if (var9 != null) {
                  var2.setSelectedItem(var9.CLASS);
               }
            }

         } else {
            if (var7.getText().indexOf((String)var1.getItem()) == -1) {
               var5.setText("");
               var6.setText("");
               var8.setText("");
            }

            var9 = ToolDialog.getPerm((String)var1.getItem(), false);
            if (var9 == null) {
               var7.setText("");
            } else {
               var7.setText(var9.FULL_CLASS);
            }

            this.td.setPermissionNames(var9, var3, var5);
            this.td.setPermissionActions(var9, var4, var6);
         }
      }
   }
}
