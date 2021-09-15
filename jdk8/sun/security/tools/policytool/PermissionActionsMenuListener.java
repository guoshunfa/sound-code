package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionActionsMenuListener implements ItemListener {
   private ToolDialog td;

   PermissionActionsMenuListener(ToolDialog var1) {
      this.td = var1;
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() != 2) {
         JComboBox var2 = (JComboBox)this.td.getComponent(5);
         var2.getAccessibleContext().setAccessibleName((String)var1.getItem());
         if (((String)var1.getItem()).indexOf(ToolDialog.PERM_ACTIONS) == -1) {
            JTextField var3 = (JTextField)this.td.getComponent(6);
            if (var3.getText() != null && !var3.getText().equals("")) {
               if (var3.getText().indexOf((String)var1.getItem()) == -1) {
                  var3.setText(var3.getText() + ", " + (String)var1.getItem());
               }
            } else {
               var3.setText((String)var1.getItem());
            }

         }
      }
   }
}
