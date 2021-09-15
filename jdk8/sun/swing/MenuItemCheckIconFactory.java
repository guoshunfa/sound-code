package sun.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public interface MenuItemCheckIconFactory {
   Icon getIcon(JMenuItem var1);

   boolean isCompatible(Object var1, String var2);
}
