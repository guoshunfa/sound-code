package javax.swing.tree;

import javax.swing.event.TreeExpansionEvent;

public class ExpandVetoException extends Exception {
   protected TreeExpansionEvent event;

   public ExpandVetoException(TreeExpansionEvent var1) {
      this(var1, (String)null);
   }

   public ExpandVetoException(TreeExpansionEvent var1, String var2) {
      super(var2);
      this.event = var1;
   }
}
