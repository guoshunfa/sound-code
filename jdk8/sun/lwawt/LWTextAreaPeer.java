package sun.lwawt;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.TextEvent;
import java.awt.peer.TextAreaPeer;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

final class LWTextAreaPeer extends LWTextComponentPeer<TextArea, LWTextAreaPeer.ScrollableJTextArea> implements TextAreaPeer {
   private static final int DEFAULT_COLUMNS = 60;
   private static final int DEFAULT_ROWS = 10;

   LWTextAreaPeer(TextArea var1, PlatformComponent var2) {
      super(var1, var2);
   }

   LWTextAreaPeer.ScrollableJTextArea createDelegate() {
      return new LWTextAreaPeer.ScrollableJTextArea();
   }

   void initializeImpl() {
      super.initializeImpl();
      int var1 = ((TextArea)this.getTarget()).getScrollbarVisibility();
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().setWrapStyleWord(true);
         this.setScrollBarVisibility(var1);
      }
   }

   JTextArea getTextComponent() {
      return ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getView();
   }

   Cursor getCursor(Point var1) {
      boolean var2;
      synchronized(this.getDelegateLock()) {
         var2 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getViewport().getBounds().contains(var1);
      }

      return var2 ? super.getCursor(var1) : null;
   }

   Component getDelegateFocusOwner() {
      return this.getTextComponent();
   }

   public Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public Dimension getMinimumSize() {
      return this.getMinimumSize(10, 60);
   }

   public Dimension getPreferredSize(int var1, int var2) {
      return this.getMinimumSize(var1, var2);
   }

   public Dimension getMinimumSize(int var1, int var2) {
      Dimension var3 = super.getMinimumSize(var1, var2);
      synchronized(this.getDelegateLock()) {
         Insets var5 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getInsets();
         var3.width += var5.left + var5.right;
         var3.height += var5.top + var5.bottom;
         int var6 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getVerticalScrollBarPolicy();
         if (var6 == 22) {
            JScrollBar var7 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getVerticalScrollBar();
            var3.width += var7 != null ? var7.getMinimumSize().width : 0;
         }

         int var11 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getHorizontalScrollBarPolicy();
         if (var11 == 32) {
            JScrollBar var8 = ((LWTextAreaPeer.ScrollableJTextArea)this.getDelegate()).getHorizontalScrollBar();
            var3.height += var8 != null ? var8.getMinimumSize().height : 0;
         }

         return var3;
      }
   }

   public void insert(String var1, int var2) {
      LWTextAreaPeer.ScrollableJTextArea var3 = (LWTextAreaPeer.ScrollableJTextArea)this.getDelegate();
      synchronized(this.getDelegateLock()) {
         JTextArea var5 = var3.getView();
         boolean var6 = var2 >= var5.getDocument().getLength() && var5.getDocument().getLength() != 0;
         var5.insert(var1, var2);
         this.revalidate();
         if (var6) {
            JScrollBar var7 = var3.getVerticalScrollBar();
            if (var7 != null) {
               var7.setValue(var7.getMaximum() - var7.getVisibleAmount());
            }
         }
      }

      this.repaintPeer();
   }

   public void replaceRange(String var1, int var2, int var3) {
      synchronized(this.getDelegateLock()) {
         Document var5 = this.getTextComponent().getDocument();
         var5.removeDocumentListener(this);
         this.getTextComponent().replaceRange(var1, var2, var3);
         this.revalidate();
         this.postEvent(new TextEvent(this.getTarget(), 900));
         var5.addDocumentListener(this);
      }

      this.repaintPeer();
   }

   private void setScrollBarVisibility(int var1) {
      LWTextAreaPeer.ScrollableJTextArea var2 = (LWTextAreaPeer.ScrollableJTextArea)this.getDelegate();
      JTextArea var3 = var2.getView();
      var3.setLineWrap(false);
      switch(var1) {
      case 1:
         var2.setHorizontalScrollBarPolicy(31);
         var2.setVerticalScrollBarPolicy(22);
         var3.setLineWrap(true);
         break;
      case 2:
         var2.setVerticalScrollBarPolicy(21);
         var2.setHorizontalScrollBarPolicy(32);
         break;
      case 3:
         var2.setHorizontalScrollBarPolicy(31);
         var2.setVerticalScrollBarPolicy(21);
         var3.setLineWrap(true);
         break;
      default:
         var2.setHorizontalScrollBarPolicy(32);
         var2.setVerticalScrollBarPolicy(22);
      }

   }

   final class ScrollableJTextArea extends JScrollPane {
      ScrollableJTextArea() {
         this.getViewport().setView(new LWTextAreaPeer.ScrollableJTextArea.JTextAreaDelegate());
      }

      public JTextArea getView() {
         return (JTextArea)this.getViewport().getView();
      }

      public void setEnabled(boolean var1) {
         this.getViewport().getView().setEnabled(var1);
         super.setEnabled(var1);
      }

      private final class JTextAreaDelegate extends JTextArea {
         JTextAreaDelegate() {
         }

         public void replaceSelection(String var1) {
            this.getDocument().removeDocumentListener(LWTextAreaPeer.this);
            super.replaceSelection(var1);
            LWTextAreaPeer.this.postTextEvent();
            this.getDocument().addDocumentListener(LWTextAreaPeer.this);
         }

         public boolean hasFocus() {
            return ((TextArea)LWTextAreaPeer.this.getTarget()).hasFocus();
         }

         public Point getLocationOnScreen() {
            return LWTextAreaPeer.this.getLocationOnScreen();
         }
      }
   }
}
