package sun.lwawt;

import java.awt.AWTEvent;
import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.MouseWheelEvent;
import java.awt.peer.ScrollPanePeer;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class LWScrollPanePeer extends LWContainerPeer<ScrollPane, JScrollPane> implements ScrollPanePeer, ChangeListener {
   LWScrollPanePeer(ScrollPane var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JScrollPane createDelegate() {
      JScrollPane var1 = new JScrollPane();
      JPanel var2 = new JPanel();
      var2.setOpaque(false);
      var2.setVisible(false);
      var1.getViewport().setView(var2);
      var1.setBorder(BorderFactory.createEmptyBorder());
      var1.getViewport().addChangeListener(this);
      return var1;
   }

   public void handleEvent(AWTEvent var1) {
      if (var1 instanceof MouseWheelEvent) {
         MouseWheelEvent var2 = (MouseWheelEvent)var1;
         if (((ScrollPane)this.getTarget()).isWheelScrollingEnabled() && var2.isConsumed()) {
            this.sendEventToDelegate(var2);
         }
      } else {
         super.handleEvent(var1);
      }

   }

   public void stateChanged(ChangeEvent var1) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            LWComponentPeer var1 = LWScrollPanePeer.this.getViewPeer();
            if (var1 != null) {
               Rectangle var2;
               synchronized(LWScrollPanePeer.this.getDelegateLock()) {
                  var2 = ((JScrollPane)LWScrollPanePeer.this.getDelegate()).getViewport().getView().getBounds();
               }

               var1.setBounds(var2.x, var2.y, var2.width, var2.height, 3, true, true);
            }

         }
      });
   }

   void initializeImpl() {
      super.initializeImpl();
      int var1 = ((ScrollPane)this.getTarget()).getScrollbarDisplayPolicy();
      synchronized(this.getDelegateLock()) {
         ((JScrollPane)this.getDelegate()).getViewport().setScrollMode(0);
         ((JScrollPane)this.getDelegate()).setVerticalScrollBarPolicy(convertVPolicy(var1));
         ((JScrollPane)this.getDelegate()).setHorizontalScrollBarPolicy(convertHPolicy(var1));
      }
   }

   LWComponentPeer<?, ?> getViewPeer() {
      List var1 = this.getChildren();
      return var1.isEmpty() ? null : (LWComponentPeer)var1.get(0);
   }

   Rectangle getContentSize() {
      Rectangle var1 = ((JScrollPane)this.getDelegate()).getViewport().getViewRect();
      return new Rectangle(var1.width, var1.height);
   }

   public void layout() {
      super.layout();
      synchronized(this.getDelegateLock()) {
         LWComponentPeer var2 = this.getViewPeer();
         if (var2 != null) {
            Component var3 = ((JScrollPane)this.getDelegate()).getViewport().getView();
            var3.setBounds(var2.getBounds());
            var3.setPreferredSize(var2.getPreferredSize());
            var3.setMinimumSize(var2.getMinimumSize());
            ((JScrollPane)this.getDelegate()).invalidate();
            ((JScrollPane)this.getDelegate()).validate();
            var2.setBounds(var3.getBounds());
         }

      }
   }

   public void setScrollPosition(int var1, int var2) {
   }

   public int getHScrollbarHeight() {
      synchronized(this.getDelegateLock()) {
         return ((JScrollPane)this.getDelegate()).getHorizontalScrollBar().getHeight();
      }
   }

   public int getVScrollbarWidth() {
      synchronized(this.getDelegateLock()) {
         return ((JScrollPane)this.getDelegate()).getVerticalScrollBar().getWidth();
      }
   }

   public void childResized(int var1, int var2) {
      synchronized(this.getDelegateLock()) {
         ((JScrollPane)this.getDelegate()).invalidate();
         ((JScrollPane)this.getDelegate()).validate();
      }
   }

   public void setUnitIncrement(Adjustable var1, int var2) {
   }

   public void setValue(Adjustable var1, int var2) {
   }

   private static int convertHPolicy(int var0) {
      switch(var0) {
      case 1:
         return 32;
      case 2:
         return 31;
      default:
         return 30;
      }
   }

   private static int convertVPolicy(int var0) {
      switch(var0) {
      case 1:
         return 22;
      case 2:
         return 21;
      default:
         return 20;
      }
   }
}
