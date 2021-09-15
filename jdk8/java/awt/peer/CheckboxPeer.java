package java.awt.peer;

import java.awt.CheckboxGroup;

public interface CheckboxPeer extends ComponentPeer {
   void setState(boolean var1);

   void setCheckboxGroup(CheckboxGroup var1);

   void setLabel(String var1);
}
