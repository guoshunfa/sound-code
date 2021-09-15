package javax.swing.tree;

import java.util.Enumeration;

public interface TreeNode {
   TreeNode getChildAt(int var1);

   int getChildCount();

   TreeNode getParent();

   int getIndex(TreeNode var1);

   boolean getAllowsChildren();

   boolean isLeaf();

   Enumeration children();
}
