package javax.swing;

import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;

public interface ButtonModel extends ItemSelectable {
   boolean isArmed();

   boolean isSelected();

   boolean isEnabled();

   boolean isPressed();

   boolean isRollover();

   void setArmed(boolean var1);

   void setSelected(boolean var1);

   void setEnabled(boolean var1);

   void setPressed(boolean var1);

   void setRollover(boolean var1);

   void setMnemonic(int var1);

   int getMnemonic();

   void setActionCommand(String var1);

   String getActionCommand();

   void setGroup(ButtonGroup var1);

   void addActionListener(ActionListener var1);

   void removeActionListener(ActionListener var1);

   void addItemListener(ItemListener var1);

   void removeItemListener(ItemListener var1);

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);
}
