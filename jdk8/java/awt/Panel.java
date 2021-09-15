package java.awt;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Panel extends Container implements Accessible {
   private static final String base = "panel";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -2728009084054400034L;

   public Panel() {
      this(new FlowLayout());
   }

   public Panel(LayoutManager var1) {
      this.setLayout(var1);
   }

   String constructComponentName() {
      Class var1 = Panel.class;
      synchronized(Panel.class) {
         return "panel" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createPanel(this);
         }

         super.addNotify();
      }
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Panel.AccessibleAWTPanel();
      }

      return this.accessibleContext;
   }

   protected class AccessibleAWTPanel extends Container.AccessibleAWTContainer {
      private static final long serialVersionUID = -6409552226660031050L;

      protected AccessibleAWTPanel() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PANEL;
      }
   }
}
