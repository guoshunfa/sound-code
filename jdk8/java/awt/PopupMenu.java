package java.awt;

import java.awt.peer.PopupMenuPeer;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class PopupMenu extends Menu {
   private static final String base = "popup";
   static int nameCounter = 0;
   transient boolean isTrayIconPopup;
   private static final long serialVersionUID = -4620452533522760060L;

   public PopupMenu() throws HeadlessException {
      this("");
   }

   public PopupMenu(String var1) throws HeadlessException {
      super(var1);
      this.isTrayIconPopup = false;
   }

   public MenuContainer getParent() {
      return this.isTrayIconPopup ? null : super.getParent();
   }

   String constructComponentName() {
      Class var1 = PopupMenu.class;
      synchronized(PopupMenu.class) {
         return "popup" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.parent != null && !(this.parent instanceof Component)) {
            super.addNotify();
         } else {
            if (this.peer == null) {
               this.peer = Toolkit.getDefaultToolkit().createPopupMenu(this);
            }

            int var2 = this.getItemCount();

            for(int var3 = 0; var3 < var2; ++var3) {
               MenuItem var4 = this.getItem(var3);
               var4.parent = this;
               var4.addNotify();
            }
         }

      }
   }

   public void show(Component var1, int var2, int var3) {
      MenuContainer var4 = this.parent;
      if (var4 == null) {
         throw new NullPointerException("parent is null");
      } else if (!(var4 instanceof Component)) {
         throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown");
      } else {
         Component var5 = (Component)var4;
         if (var5 != var1) {
            if (!(var5 instanceof Container)) {
               throw new IllegalArgumentException("origin not in parent's hierarchy");
            }

            if (!((Container)var5).isAncestorOf(var1)) {
               throw new IllegalArgumentException("origin not in parent's hierarchy");
            }
         }

         if (var5.getPeer() != null && var5.isShowing()) {
            if (this.peer == null) {
               this.addNotify();
            }

            synchronized(this.getTreeLock()) {
               if (this.peer != null) {
                  ((PopupMenuPeer)this.peer).show(new Event(var1, 0L, 501, var2, var3, 0, 0));
               }

            }
         } else {
            throw new RuntimeException("parent not showing on screen");
         }
      }
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new PopupMenu.AccessibleAWTPopupMenu();
      }

      return this.accessibleContext;
   }

   static {
      AWTAccessor.setPopupMenuAccessor(new AWTAccessor.PopupMenuAccessor() {
         public boolean isTrayIconPopup(PopupMenu var1) {
            return var1.isTrayIconPopup;
         }
      });
   }

   protected class AccessibleAWTPopupMenu extends Menu.AccessibleAWTMenu {
      private static final long serialVersionUID = -4282044795947239955L;

      protected AccessibleAWTPopupMenu() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.POPUP_MENU;
      }
   }
}
