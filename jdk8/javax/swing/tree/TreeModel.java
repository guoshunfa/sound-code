package javax.swing.tree;

import javax.swing.event.TreeModelListener;

public interface TreeModel {
   Object getRoot();

   Object getChild(Object var1, int var2);

   int getChildCount(Object var1);

   boolean isLeaf(Object var1);

   void valueForPathChanged(TreePath var1, Object var2);

   int getIndexOfChild(Object var1, Object var2);

   void addTreeModelListener(TreeModelListener var1);

   void removeTreeModelListener(TreeModelListener var1);
}
