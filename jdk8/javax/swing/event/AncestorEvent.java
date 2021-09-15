package javax.swing.event;

import java.awt.AWTEvent;
import java.awt.Container;
import javax.swing.JComponent;

public class AncestorEvent extends AWTEvent {
   public static final int ANCESTOR_ADDED = 1;
   public static final int ANCESTOR_REMOVED = 2;
   public static final int ANCESTOR_MOVED = 3;
   Container ancestor;
   Container ancestorParent;

   public AncestorEvent(JComponent var1, int var2, Container var3, Container var4) {
      super(var1, var2);
      this.ancestor = var3;
      this.ancestorParent = var4;
   }

   public Container getAncestor() {
      return this.ancestor;
   }

   public Container getAncestorParent() {
      return this.ancestorParent;
   }

   public JComponent getComponent() {
      return (JComponent)this.getSource();
   }
}
