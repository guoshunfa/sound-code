package sun.lwawt;

import java.awt.Choice;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

final class LWChoicePeer extends LWComponentPeer<Choice, JComboBox<String>> implements ChoicePeer, ItemListener {
   private boolean skipPostMessage;

   LWChoicePeer(Choice var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JComboBox<String> createDelegate() {
      return new LWChoicePeer.JComboBoxDelegate();
   }

   void initializeImpl() {
      super.initializeImpl();
      Choice var1 = (Choice)this.getTarget();
      JComboBox var2 = (JComboBox)this.getDelegate();
      synchronized(this.getDelegateLock()) {
         int var4 = var1.getItemCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            var2.addItem(var1.getItem(var5));
         }

         this.select(var1.getSelectedIndex());
         var2.addItemListener(this);
      }
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() == 1) {
         synchronized(this.getDelegateLock()) {
            if (this.skipPostMessage) {
               return;
            }

            ((Choice)this.getTarget()).select(((JComboBox)this.getDelegate()).getSelectedIndex());
         }

         this.postEvent(new ItemEvent((ItemSelectable)this.getTarget(), 701, var1.getItem(), 1));
      }

   }

   public void add(String var1, int var2) {
      synchronized(this.getDelegateLock()) {
         ((JComboBox)this.getDelegate()).insertItemAt(var1, var2);
      }
   }

   public void remove(int var1) {
      synchronized(this.getDelegateLock()) {
         this.skipPostMessage = true;
         ((JComboBox)this.getDelegate()).removeItemAt(var1);
         this.skipPostMessage = false;
      }
   }

   public void removeAll() {
      synchronized(this.getDelegateLock()) {
         ((JComboBox)this.getDelegate()).removeAllItems();
      }
   }

   public void select(int var1) {
      synchronized(this.getDelegateLock()) {
         if (var1 != ((JComboBox)this.getDelegate()).getSelectedIndex()) {
            this.skipPostMessage = true;
            ((JComboBox)this.getDelegate()).setSelectedIndex(var1);
            this.skipPostMessage = false;
         }

      }
   }

   public boolean isFocusable() {
      return true;
   }

   private final class JComboBoxDelegate extends JComboBox<String> {
      JComboBoxDelegate() {
      }

      public boolean hasFocus() {
         return ((Choice)LWChoicePeer.this.getTarget()).hasFocus();
      }

      public Point getLocationOnScreen() {
         return LWChoicePeer.this.getLocationOnScreen();
      }

      public void setSelectedItem(Object var1) {
         Object var2 = this.selectedItemReminder;
         if (var2 != null && var2.equals(var1)) {
            this.selectedItemChanged();
         }

         super.setSelectedItem(var1);
      }

      public void firePopupMenuWillBecomeVisible() {
         super.firePopupMenuWillBecomeVisible();
         SwingUtilities.invokeLater(() -> {
            JPopupMenu var1 = this.getPopupMenu();
            if (var1 != null && var1.isShowing() && var1.getInvoker() != LWChoicePeer.this.getTarget()) {
               Point var2 = var1.getLocationOnScreen();
               SwingUtilities.convertPointFromScreen(var2, this);
               var1.setVisible(false);
               var1.show(LWChoicePeer.this.getTarget(), var2.x, var2.y);
            }

         });
      }

      private JPopupMenu getPopupMenu() {
         for(int var1 = 0; var1 < this.getAccessibleContext().getAccessibleChildrenCount(); ++var1) {
            Accessible var2 = this.getAccessibleContext().getAccessibleChild(var1);
            if (var2 instanceof JPopupMenu) {
               return (JPopupMenu)var2;
            }
         }

         return null;
      }
   }
}
