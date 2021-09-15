package javax.swing.event;

import java.util.EventListener;
import javax.swing.tree.ExpandVetoException;

public interface TreeWillExpandListener extends EventListener {
   void treeWillExpand(TreeExpansionEvent var1) throws ExpandVetoException;

   void treeWillCollapse(TreeExpansionEvent var1) throws ExpandVetoException;
}
