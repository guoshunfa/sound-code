package javax.swing.event;

import java.util.EventListener;

public interface TreeModelListener extends EventListener {
   void treeNodesChanged(TreeModelEvent var1);

   void treeNodesInserted(TreeModelEvent var1);

   void treeNodesRemoved(TreeModelEvent var1);

   void treeStructureChanged(TreeModelEvent var1);
}
