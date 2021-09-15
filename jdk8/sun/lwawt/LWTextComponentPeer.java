package sun.lwawt;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.TextComponent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.TextEvent;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextComponentPeer;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import sun.awt.AWTAccessor;

abstract class LWTextComponentPeer<T extends TextComponent, D extends JComponent> extends LWComponentPeer<T, D> implements DocumentListener, TextComponentPeer, InputMethodListener {
   private volatile boolean firstChangeSkipped;

   LWTextComponentPeer(T var1, PlatformComponent var2) {
      super(var1, var2);
      if (!((TextComponent)this.getTarget()).isBackgroundSet()) {
         ((TextComponent)this.getTarget()).setBackground(SystemColor.text);
      }

   }

   void initializeImpl() {
      super.initializeImpl();
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().getDocument().addDocumentListener(this);
      }

      this.setEditable(((TextComponent)this.getTarget()).isEditable());
      this.setText(((TextComponent)this.getTarget()).getText());
      this.setCaretPosition(((TextComponent)this.getTarget()).getCaretPosition());
      ((TextComponent)this.getTarget()).addInputMethodListener(this);
      int var1 = ((TextComponent)this.getTarget()).getSelectionStart();
      int var2 = ((TextComponent)this.getTarget()).getSelectionEnd();
      if (var2 > var1) {
         this.select(var1, var2);
      }

      this.firstChangeSkipped = true;
   }

   protected final void disposeImpl() {
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().getCaret().setVisible(false);
      }

      super.disposeImpl();
   }

   abstract JTextComponent getTextComponent();

   public Dimension getMinimumSize(int var1, int var2) {
      Insets var3;
      synchronized(this.getDelegateLock()) {
         var3 = this.getTextComponent().getInsets();
      }

      int var4 = var3.top + var3.bottom;
      int var5 = var3.left + var3.right;
      FontMetrics var6 = this.getFontMetrics(this.getFont());
      return new Dimension(var6.charWidth('0') * var2 + var5, var6.getHeight() * var1 + var4);
   }

   public final void setEditable(boolean var1) {
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().setEditable(var1);
      }
   }

   public final String getText() {
      synchronized(this.getDelegateLock()) {
         return this.getTextComponent().getText();
      }
   }

   public final void setText(String var1) {
      synchronized(this.getDelegateLock()) {
         Document var3 = this.getTextComponent().getDocument();
         var3.removeDocumentListener(this);
         this.getTextComponent().setText(var1);
         this.revalidate();
         if (this.firstChangeSkipped) {
            this.postEvent(new TextEvent(this.getTarget(), 900));
         }

         var3.addDocumentListener(this);
      }

      this.repaintPeer();
   }

   public final int getSelectionStart() {
      synchronized(this.getDelegateLock()) {
         return this.getTextComponent().getSelectionStart();
      }
   }

   public final int getSelectionEnd() {
      synchronized(this.getDelegateLock()) {
         return this.getTextComponent().getSelectionEnd();
      }
   }

   public final void select(int var1, int var2) {
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().select(var1, var2);
      }

      this.repaintPeer();
   }

   public final void setCaretPosition(int var1) {
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().setCaretPosition(var1);
      }

      this.repaintPeer();
   }

   public final int getCaretPosition() {
      synchronized(this.getDelegateLock()) {
         return this.getTextComponent().getCaretPosition();
      }
   }

   public final InputMethodRequests getInputMethodRequests() {
      synchronized(this.getDelegateLock()) {
         return this.getTextComponent().getInputMethodRequests();
      }
   }

   public final boolean isFocusable() {
      return ((TextComponent)this.getTarget()).isFocusable();
   }

   protected final void revalidate() {
      synchronized(this.getDelegateLock()) {
         this.getTextComponent().invalidate();
         this.getDelegate().validate();
      }
   }

   protected final void postTextEvent() {
      this.postEvent(new TextEvent(this.getTarget(), 900));
      synchronized(this.getDelegateLock()) {
         this.revalidate();
      }
   }

   public final void changedUpdate(DocumentEvent var1) {
      this.postTextEvent();
   }

   public final void insertUpdate(DocumentEvent var1) {
      this.postTextEvent();
   }

   public final void removeUpdate(DocumentEvent var1) {
      this.postTextEvent();
   }

   public void inputMethodTextChanged(InputMethodEvent var1) {
      synchronized(this.getDelegateLock()) {
         AWTAccessor.getComponentAccessor().processEvent(this.getTextComponent(), var1);
      }
   }

   public void caretPositionChanged(InputMethodEvent var1) {
      synchronized(this.getDelegateLock()) {
         AWTAccessor.getComponentAccessor().processEvent(this.getTextComponent(), var1);
      }
   }
}
