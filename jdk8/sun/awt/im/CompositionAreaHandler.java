package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.lang.ref.WeakReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

class CompositionAreaHandler implements InputMethodListener, InputMethodRequests {
   private static CompositionArea compositionArea;
   private static Object compositionAreaLock = new Object();
   private static CompositionAreaHandler compositionAreaOwner;
   private AttributedCharacterIterator composedText;
   private TextHitInfo caret = null;
   private WeakReference<Component> clientComponent = new WeakReference((Object)null);
   private InputMethodContext inputMethodContext;
   private static final AttributedCharacterIterator.Attribute[] IM_ATTRIBUTES;
   private static final AttributedCharacterIterator EMPTY_TEXT;

   CompositionAreaHandler(InputMethodContext var1) {
      this.inputMethodContext = var1;
   }

   private void createCompositionArea() {
      synchronized(compositionAreaLock) {
         compositionArea = new CompositionArea();
         if (compositionAreaOwner != null) {
            compositionArea.setHandlerInfo(compositionAreaOwner, this.inputMethodContext);
         }

         Component var2 = (Component)this.clientComponent.get();
         if (var2 != null) {
            InputMethodRequests var3 = var2.getInputMethodRequests();
            if (var3 != null && this.inputMethodContext.useBelowTheSpotInput()) {
               this.setCompositionAreaUndecorated(true);
            }
         }

      }
   }

   void setClientComponent(Component var1) {
      this.clientComponent = new WeakReference(var1);
   }

   void grabCompositionArea(boolean var1) {
      synchronized(compositionAreaLock) {
         if (compositionAreaOwner != this) {
            compositionAreaOwner = this;
            if (compositionArea != null) {
               compositionArea.setHandlerInfo(this, this.inputMethodContext);
            }

            if (var1) {
               if (this.composedText != null && compositionArea == null) {
                  this.createCompositionArea();
               }

               if (compositionArea != null) {
                  compositionArea.setText(this.composedText, this.caret);
               }
            }
         }

      }
   }

   void releaseCompositionArea() {
      synchronized(compositionAreaLock) {
         if (compositionAreaOwner == this) {
            compositionAreaOwner = null;
            if (compositionArea != null) {
               compositionArea.setHandlerInfo((CompositionAreaHandler)null, (InputContext)null);
               compositionArea.setText((AttributedCharacterIterator)null, (TextHitInfo)null);
            }
         }

      }
   }

   static void closeCompositionArea() {
      if (compositionArea != null) {
         synchronized(compositionAreaLock) {
            compositionAreaOwner = null;
            compositionArea.setHandlerInfo((CompositionAreaHandler)null, (InputContext)null);
            compositionArea.setText((AttributedCharacterIterator)null, (TextHitInfo)null);
         }
      }

   }

   boolean isCompositionAreaVisible() {
      return compositionArea != null ? compositionArea.isCompositionAreaVisible() : false;
   }

   void setCompositionAreaVisible(boolean var1) {
      if (compositionArea != null) {
         compositionArea.setCompositionAreaVisible(var1);
      }

   }

   void processInputMethodEvent(InputMethodEvent var1) {
      if (var1.getID() == 1100) {
         this.inputMethodTextChanged(var1);
      } else {
         this.caretPositionChanged(var1);
      }

   }

   void setCompositionAreaUndecorated(boolean var1) {
      if (compositionArea != null) {
         compositionArea.setCompositionAreaUndecorated(var1);
      }

   }

   public void inputMethodTextChanged(InputMethodEvent var1) {
      AttributedCharacterIterator var2 = var1.getText();
      int var3 = var1.getCommittedCharacterCount();
      this.composedText = null;
      this.caret = null;
      if (var2 != null && var3 < var2.getEndIndex() - var2.getBeginIndex()) {
         if (compositionArea == null) {
            this.createCompositionArea();
         }

         AttributedString var4 = new AttributedString(var2, var2.getBeginIndex() + var3, var2.getEndIndex(), IM_ATTRIBUTES);
         var4.addAttribute(TextAttribute.FONT, compositionArea.getFont());
         this.composedText = var4.getIterator();
         this.caret = var1.getCaret();
      }

      if (compositionArea != null) {
         compositionArea.setText(this.composedText, this.caret);
      }

      if (var3 > 0) {
         this.inputMethodContext.dispatchCommittedText((Component)var1.getSource(), var2, var3);
         if (this.isCompositionAreaVisible()) {
            compositionArea.updateWindowLocation();
         }
      }

      var1.consume();
   }

   public void caretPositionChanged(InputMethodEvent var1) {
      if (compositionArea != null) {
         compositionArea.setCaret(var1.getCaret());
      }

      var1.consume();
   }

   InputMethodRequests getClientInputMethodRequests() {
      Component var1 = (Component)this.clientComponent.get();
      return var1 != null ? var1.getInputMethodRequests() : null;
   }

   public Rectangle getTextLocation(TextHitInfo var1) {
      synchronized(compositionAreaLock) {
         if (compositionAreaOwner == this && this.isCompositionAreaVisible()) {
            return compositionArea.getTextLocation(var1);
         } else if (this.composedText != null) {
            return new Rectangle(0, 0, 0, 10);
         } else {
            InputMethodRequests var3 = this.getClientInputMethodRequests();
            return var3 != null ? var3.getTextLocation(var1) : new Rectangle(0, 0, 0, 10);
         }
      }
   }

   public TextHitInfo getLocationOffset(int var1, int var2) {
      synchronized(compositionAreaLock) {
         return compositionAreaOwner == this && this.isCompositionAreaVisible() ? compositionArea.getLocationOffset(var1, var2) : null;
      }
   }

   public int getInsertPositionOffset() {
      InputMethodRequests var1 = this.getClientInputMethodRequests();
      return var1 != null ? var1.getInsertPositionOffset() : 0;
   }

   public AttributedCharacterIterator getCommittedText(int var1, int var2, AttributedCharacterIterator.Attribute[] var3) {
      InputMethodRequests var4 = this.getClientInputMethodRequests();
      return var4 != null ? var4.getCommittedText(var1, var2, var3) : EMPTY_TEXT;
   }

   public int getCommittedTextLength() {
      InputMethodRequests var1 = this.getClientInputMethodRequests();
      return var1 != null ? var1.getCommittedTextLength() : 0;
   }

   public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] var1) {
      InputMethodRequests var2 = this.getClientInputMethodRequests();
      return var2 != null ? var2.cancelLatestCommittedText(var1) : null;
   }

   public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] var1) {
      InputMethodRequests var2 = this.getClientInputMethodRequests();
      return var2 != null ? var2.getSelectedText(var1) : EMPTY_TEXT;
   }

   static {
      IM_ATTRIBUTES = new AttributedCharacterIterator.Attribute[]{TextAttribute.INPUT_METHOD_HIGHLIGHT};
      EMPTY_TEXT = (new AttributedString("")).getIterator();
   }
}
