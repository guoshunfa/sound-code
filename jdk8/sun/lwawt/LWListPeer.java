package sun.lwawt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.List;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ListPeer;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

final class LWListPeer extends LWComponentPeer<List, LWListPeer.ScrollableJList> implements ListPeer {
   private static final int DEFAULT_VISIBLE_ROWS = 4;
   private static final String TEXT = "0123456789abcde";

   LWListPeer(List var1, PlatformComponent var2) {
      super(var1, var2);
      if (!((List)this.getTarget()).isBackgroundSet()) {
         ((List)this.getTarget()).setBackground(SystemColor.text);
      }

   }

   LWListPeer.ScrollableJList createDelegate() {
      return new LWListPeer.ScrollableJList();
   }

   void initializeImpl() {
      super.initializeImpl();
      this.setMultipleMode(((List)this.getTarget()).isMultipleMode());
      int[] var1 = ((List)this.getTarget()).getSelectedIndexes();
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).setSkipStateChangedEvent(true);
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().setSelectedIndices(var1);
         ((LWListPeer.ScrollableJList)this.getDelegate()).setSkipStateChangedEvent(false);
      }
   }

   public boolean isFocusable() {
      return true;
   }

   Component getDelegateFocusOwner() {
      return ((LWListPeer.ScrollableJList)this.getDelegate()).getView();
   }

   public int[] getSelectedIndexes() {
      synchronized(this.getDelegateLock()) {
         return ((LWListPeer.ScrollableJList)this.getDelegate()).getView().getSelectedIndices();
      }
   }

   public void add(String var1, int var2) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getModel().add(var2, var1);
         this.revalidate();
      }
   }

   public void delItems(int var1, int var2) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getModel().removeRange(var1, var2);
         this.revalidate();
      }
   }

   public void removeAll() {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getModel().removeAllElements();
         this.revalidate();
      }
   }

   public void select(int var1) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).setSkipStateChangedEvent(true);
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().setSelectedIndex(var1);
         ((LWListPeer.ScrollableJList)this.getDelegate()).setSkipStateChangedEvent(false);
      }
   }

   public void deselect(int var1) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().getSelectionModel().removeSelectionInterval(var1, var1);
      }
   }

   public void makeVisible(int var1) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().ensureIndexIsVisible(var1);
      }
   }

   public void setMultipleMode(boolean var1) {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().setSelectionMode(var1 ? 2 : 0);
      }
   }

   public Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public Dimension getMinimumSize() {
      return this.getMinimumSize(4);
   }

   public Dimension getPreferredSize(int var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(int var1) {
      synchronized(this.getDelegateLock()) {
         Dimension var3 = this.getCellSize();
         var3.height *= var1;
         JScrollBar var4 = ((LWListPeer.ScrollableJList)this.getDelegate()).getVerticalScrollBar();
         var3.width += var4 != null ? var4.getMinimumSize().width : 0;
         Insets var5 = ((LWListPeer.ScrollableJList)this.getDelegate()).getInsets();
         Insets var6 = ((LWListPeer.ScrollableJList)this.getDelegate()).getView().getInsets();
         var3.width += var5.left + var5.right + var6.left + var6.right;
         var3.height += var5.top + var5.bottom + var6.top + var6.bottom;
         return var3;
      }
   }

   private Dimension getCellSize() {
      JList var1 = ((LWListPeer.ScrollableJList)this.getDelegate()).getView();
      ListCellRenderer var2 = var1.getCellRenderer();
      Component var3 = var2.getListCellRendererComponent(var1, "0123456789abcde", 0, false, false);
      return var3.getPreferredSize();
   }

   private void revalidate() {
      synchronized(this.getDelegateLock()) {
         ((LWListPeer.ScrollableJList)this.getDelegate()).getView().invalidate();
         ((LWListPeer.ScrollableJList)this.getDelegate()).validate();
      }
   }

   final class ScrollableJList extends JScrollPane implements ListSelectionListener {
      private boolean skipStateChangedEvent;
      private final DefaultListModel<String> model = new DefaultListModel<String>() {
         public void add(int var1, String var2) {
            if (var1 == -1) {
               this.addElement(var2);
            } else {
               super.add(var1, var2);
            }

         }
      };
      private int[] oldSelectedIndices = new int[0];

      ScrollableJList() {
         this.getViewport().setScrollMode(0);
         LWListPeer.ScrollableJList.JListDelegate var2 = new LWListPeer.ScrollableJList.JListDelegate();
         var2.addListSelectionListener(this);
         this.getViewport().setView(var2);
         String[] var3 = ((List)LWListPeer.this.getTarget()).getItems();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            this.model.add(var4, var3[var4]);
         }

      }

      public boolean isSkipStateChangedEvent() {
         return this.skipStateChangedEvent;
      }

      public void setSkipStateChangedEvent(boolean var1) {
         this.skipStateChangedEvent = var1;
      }

      public void valueChanged(ListSelectionEvent var1) {
         if (!var1.getValueIsAdjusting() && !this.isSkipStateChangedEvent()) {
            JList var2 = (JList)var1.getSource();

            for(int var3 = 0; var3 < var2.getModel().getSize(); ++var3) {
               boolean var4 = Arrays.binarySearch(this.oldSelectedIndices, var3) >= 0;
               boolean var5 = var2.isSelectedIndex(var3);
               if (var4 != var5) {
                  int var6 = !var4 && var5 ? 1 : 2;
                  LWListPeer.this.postEvent(new ItemEvent((ItemSelectable)LWListPeer.this.getTarget(), 701, var3, var6));
               }
            }

            this.oldSelectedIndices = var2.getSelectedIndices();
         }

      }

      public JList<String> getView() {
         return (JList)this.getViewport().getView();
      }

      public DefaultListModel<String> getModel() {
         return this.model;
      }

      public void setEnabled(boolean var1) {
         this.getView().setEnabled(var1);
         super.setEnabled(var1);
      }

      public void setOpaque(boolean var1) {
         super.setOpaque(var1);
         if (this.getView() != null) {
            this.getView().setOpaque(var1);
         }

      }

      public void setFont(Font var1) {
         super.setFont(var1);
         if (this.getView() != null) {
            this.getView().setFont(var1);
            LWListPeer.this.revalidate();
         }

      }

      private final class JListDelegate extends JList<String> {
         JListDelegate() {
            super((ListModel)ScrollableJList.this.model);
         }

         public boolean hasFocus() {
            return ((List)LWListPeer.this.getTarget()).hasFocus();
         }

         protected void processMouseEvent(MouseEvent var1) {
            super.processMouseEvent(var1);
            if (var1.getID() == 500 && var1.getClickCount() == 2) {
               int var2 = this.locationToIndex(var1.getPoint());
               if (0 <= var2 && var2 < this.getModel().getSize()) {
                  LWListPeer.this.postEvent(new ActionEvent(LWListPeer.this.getTarget(), 1001, (String)this.getModel().getElementAt(var2), var1.getWhen(), var1.getModifiers()));
               }
            }

         }

         protected void processKeyEvent(KeyEvent var1) {
            super.processKeyEvent(var1);
            if (var1.getID() == 401 && var1.getKeyCode() == 10) {
               String var2 = (String)this.getSelectedValue();
               if (var2 != null) {
                  LWListPeer.this.postEvent(new ActionEvent(LWListPeer.this.getTarget(), 1001, var2, var1.getWhen(), var1.getModifiers()));
               }
            }

         }

         public Point getLocationOnScreen() {
            return LWListPeer.this.getLocationOnScreen();
         }
      }
   }
}
