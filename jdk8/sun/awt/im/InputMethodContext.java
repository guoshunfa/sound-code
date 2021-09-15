package sun.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.JFrame;
import sun.awt.InputMethodSupport;
import sun.security.action.GetPropertyAction;

public class InputMethodContext extends InputContext implements java.awt.im.spi.InputMethodContext {
   private boolean dispatchingCommittedText;
   private CompositionAreaHandler compositionAreaHandler;
   private Object compositionAreaHandlerLock = new Object();
   private static boolean belowTheSpotInputRequested;
   private boolean inputMethodSupportsBelowTheSpot;

   void setInputMethodSupportsBelowTheSpot(boolean var1) {
      this.inputMethodSupportsBelowTheSpot = var1;
   }

   boolean useBelowTheSpotInput() {
      return belowTheSpotInputRequested && this.inputMethodSupportsBelowTheSpot;
   }

   private boolean haveActiveClient() {
      Component var1 = this.getClientComponent();
      return var1 != null && var1.getInputMethodRequests() != null;
   }

   public void dispatchInputMethodEvent(int var1, AttributedCharacterIterator var2, int var3, TextHitInfo var4, TextHitInfo var5) {
      Component var6 = this.getClientComponent();
      if (var6 != null) {
         InputMethodEvent var7 = new InputMethodEvent(var6, var1, var2, var3, var4, var5);
         if (this.haveActiveClient() && !this.useBelowTheSpotInput()) {
            var6.dispatchEvent(var7);
         } else {
            this.getCompositionAreaHandler(true).processInputMethodEvent(var7);
         }
      }

   }

   synchronized void dispatchCommittedText(Component var1, AttributedCharacterIterator var2, int var3) {
      if (var3 != 0 && var2.getEndIndex() > var2.getBeginIndex()) {
         long var4 = System.currentTimeMillis();
         this.dispatchingCommittedText = true;

         try {
            InputMethodRequests var6 = var1.getInputMethodRequests();
            if (var6 != null) {
               int var7 = var2.getBeginIndex();
               AttributedCharacterIterator var8 = (new AttributedString(var2, var7, var7 + var3)).getIterator();
               InputMethodEvent var9 = new InputMethodEvent(var1, 1100, var8, var3, (TextHitInfo)null, (TextHitInfo)null);
               var1.dispatchEvent(var9);
            } else {
               for(char var13 = var2.first(); var3-- > 0 && var13 != '\uffff'; var13 = var2.next()) {
                  KeyEvent var14 = new KeyEvent(var1, 400, var4, 0, 0, var13);
                  var1.dispatchEvent(var14);
               }
            }
         } finally {
            this.dispatchingCommittedText = false;
         }

      }
   }

   public void dispatchEvent(AWTEvent var1) {
      if (var1 instanceof InputMethodEvent) {
         if (((Component)var1.getSource()).getInputMethodRequests() == null || this.useBelowTheSpotInput() && !this.dispatchingCommittedText) {
            this.getCompositionAreaHandler(true).processInputMethodEvent((InputMethodEvent)var1);
         }
      } else if (!this.dispatchingCommittedText) {
         super.dispatchEvent(var1);
      }

   }

   private CompositionAreaHandler getCompositionAreaHandler(boolean var1) {
      synchronized(this.compositionAreaHandlerLock) {
         if (this.compositionAreaHandler == null) {
            this.compositionAreaHandler = new CompositionAreaHandler(this);
         }

         this.compositionAreaHandler.setClientComponent(this.getClientComponent());
         if (var1) {
            this.compositionAreaHandler.grabCompositionArea(false);
         }

         return this.compositionAreaHandler;
      }
   }

   void grabCompositionArea(boolean var1) {
      synchronized(this.compositionAreaHandlerLock) {
         if (this.compositionAreaHandler != null) {
            this.compositionAreaHandler.grabCompositionArea(var1);
         } else {
            CompositionAreaHandler.closeCompositionArea();
         }

      }
   }

   void releaseCompositionArea() {
      synchronized(this.compositionAreaHandlerLock) {
         if (this.compositionAreaHandler != null) {
            this.compositionAreaHandler.releaseCompositionArea();
         }

      }
   }

   boolean isCompositionAreaVisible() {
      return this.compositionAreaHandler != null ? this.compositionAreaHandler.isCompositionAreaVisible() : false;
   }

   void setCompositionAreaVisible(boolean var1) {
      if (this.compositionAreaHandler != null) {
         this.compositionAreaHandler.setCompositionAreaVisible(var1);
      }

   }

   public Rectangle getTextLocation(TextHitInfo var1) {
      return this.getReq().getTextLocation(var1);
   }

   public TextHitInfo getLocationOffset(int var1, int var2) {
      return this.getReq().getLocationOffset(var1, var2);
   }

   public int getInsertPositionOffset() {
      return this.getReq().getInsertPositionOffset();
   }

   public AttributedCharacterIterator getCommittedText(int var1, int var2, AttributedCharacterIterator.Attribute[] var3) {
      return this.getReq().getCommittedText(var1, var2, var3);
   }

   public int getCommittedTextLength() {
      return this.getReq().getCommittedTextLength();
   }

   public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] var1) {
      return this.getReq().cancelLatestCommittedText(var1);
   }

   public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] var1) {
      return this.getReq().getSelectedText(var1);
   }

   private InputMethodRequests getReq() {
      return (InputMethodRequests)(this.haveActiveClient() && !this.useBelowTheSpotInput() ? this.getClientComponent().getInputMethodRequests() : this.getCompositionAreaHandler(false));
   }

   public Window createInputMethodWindow(String var1, boolean var2) {
      InputMethodContext var3 = var2 ? this : null;
      return createInputMethodWindow(var1, var3, false);
   }

   public JFrame createInputMethodJFrame(String var1, boolean var2) {
      InputMethodContext var3 = var2 ? this : null;
      return (JFrame)createInputMethodWindow(var1, var3, true);
   }

   static Window createInputMethodWindow(String var0, InputContext var1, boolean var2) {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (var2) {
         return new InputMethodJFrame(var0, var1);
      } else {
         Toolkit var3 = Toolkit.getDefaultToolkit();
         if (var3 instanceof InputMethodSupport) {
            return ((InputMethodSupport)var3).createInputMethodWindow(var0, var1);
         } else {
            throw new InternalError("Input methods must be supported");
         }
      }
   }

   public void enableClientWindowNotification(InputMethod var1, boolean var2) {
      super.enableClientWindowNotification(var1, var2);
   }

   void setCompositionAreaUndecorated(boolean var1) {
      if (this.compositionAreaHandler != null) {
         this.compositionAreaHandler.setCompositionAreaUndecorated(var1);
      }

   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.awt.im.style", (String)null)));
      if (var0 == null) {
         var0 = Toolkit.getProperty("java.awt.im.style", (String)null);
      }

      belowTheSpotInputRequested = "below-the-spot".equals(var0);
   }
}
