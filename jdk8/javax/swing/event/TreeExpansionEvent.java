package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeExpansionEvent extends EventObject {
   protected TreePath path;

   public TreeExpansionEvent(Object var1, TreePath var2) {
      super(var1);
      this.path = var2;
   }

   public TreePath getPath() {
      return this.path;
   }
}
