package javax.swing.event;

import java.util.EventListener;

public interface TreeExpansionListener extends EventListener {
   void treeExpanded(TreeExpansionEvent var1);

   void treeCollapsed(TreeExpansionEvent var1);
}
