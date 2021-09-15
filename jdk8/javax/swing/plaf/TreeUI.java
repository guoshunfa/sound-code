package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public abstract class TreeUI extends ComponentUI {
   public abstract Rectangle getPathBounds(JTree var1, TreePath var2);

   public abstract TreePath getPathForRow(JTree var1, int var2);

   public abstract int getRowForPath(JTree var1, TreePath var2);

   public abstract int getRowCount(JTree var1);

   public abstract TreePath getClosestPathForLocation(JTree var1, int var2, int var3);

   public abstract boolean isEditing(JTree var1);

   public abstract boolean stopEditing(JTree var1);

   public abstract void cancelEditing(JTree var1);

   public abstract void startEditingAtPath(JTree var1, TreePath var2);

   public abstract TreePath getEditingPath(JTree var1);
}
