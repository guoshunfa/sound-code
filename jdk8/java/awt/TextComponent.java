package java.awt;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.text.AttributeSet;
import sun.awt.InputMethodSupport;
import sun.security.util.SecurityConstants;

public class TextComponent extends Component implements Accessible {
   String text;
   boolean editable = true;
   int selectionStart;
   int selectionEnd;
   boolean backgroundSetByClientCode = false;
   protected transient TextListener textListener;
   private static final long serialVersionUID = -2214773872412987419L;
   private int textComponentSerializedDataVersion = 1;
   private boolean checkForEnableIM = true;

   TextComponent(String var1) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      this.text = var1 != null ? var1 : "";
      this.setCursor(Cursor.getPredefinedCursor(2));
   }

   private void enableInputMethodsIfNecessary() {
      if (this.checkForEnableIM) {
         this.checkForEnableIM = false;

         try {
            Toolkit var1 = Toolkit.getDefaultToolkit();
            boolean var2 = false;
            if (var1 instanceof InputMethodSupport) {
               var2 = ((InputMethodSupport)var1).enableInputMethodsForTextComponent();
            }

            this.enableInputMethods(var2);
         } catch (Exception var3) {
         }
      }

   }

   public void enableInputMethods(boolean var1) {
      this.checkForEnableIM = false;
      super.enableInputMethods(var1);
   }

   boolean areInputMethodsEnabled() {
      if (this.checkForEnableIM) {
         this.enableInputMethodsIfNecessary();
      }

      return (this.eventMask & 4096L) != 0L;
   }

   public InputMethodRequests getInputMethodRequests() {
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      return var1 != null ? var1.getInputMethodRequests() : null;
   }

   public void addNotify() {
      super.addNotify();
      this.enableInputMethodsIfNecessary();
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         TextComponentPeer var2 = (TextComponentPeer)this.peer;
         if (var2 != null) {
            this.text = var2.getText();
            this.selectionStart = var2.getSelectionStart();
            this.selectionEnd = var2.getSelectionEnd();
         }

         super.removeNotify();
      }
   }

   public synchronized void setText(String var1) {
      boolean var2 = (this.text == null || this.text.isEmpty()) && (var1 == null || var1.isEmpty());
      this.text = var1 != null ? var1 : "";
      TextComponentPeer var3 = (TextComponentPeer)this.peer;
      if (var3 != null && !var2) {
         var3.setText(this.text);
      }

   }

   public synchronized String getText() {
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      if (var1 != null) {
         this.text = var1.getText();
      }

      return this.text;
   }

   public synchronized String getSelectedText() {
      return this.getText().substring(this.getSelectionStart(), this.getSelectionEnd());
   }

   public boolean isEditable() {
      return this.editable;
   }

   public synchronized void setEditable(boolean var1) {
      if (this.editable != var1) {
         this.editable = var1;
         TextComponentPeer var2 = (TextComponentPeer)this.peer;
         if (var2 != null) {
            var2.setEditable(var1);
         }

      }
   }

   public Color getBackground() {
      return (Color)(!this.editable && !this.backgroundSetByClientCode ? SystemColor.control : super.getBackground());
   }

   public void setBackground(Color var1) {
      this.backgroundSetByClientCode = true;
      super.setBackground(var1);
   }

   public synchronized int getSelectionStart() {
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      if (var1 != null) {
         this.selectionStart = var1.getSelectionStart();
      }

      return this.selectionStart;
   }

   public synchronized void setSelectionStart(int var1) {
      this.select(var1, this.getSelectionEnd());
   }

   public synchronized int getSelectionEnd() {
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      if (var1 != null) {
         this.selectionEnd = var1.getSelectionEnd();
      }

      return this.selectionEnd;
   }

   public synchronized void setSelectionEnd(int var1) {
      this.select(this.getSelectionStart(), var1);
   }

   public synchronized void select(int var1, int var2) {
      String var3 = this.getText();
      if (var1 < 0) {
         var1 = 0;
      }

      if (var1 > var3.length()) {
         var1 = var3.length();
      }

      if (var2 > var3.length()) {
         var2 = var3.length();
      }

      if (var2 < var1) {
         var2 = var1;
      }

      this.selectionStart = var1;
      this.selectionEnd = var2;
      TextComponentPeer var4 = (TextComponentPeer)this.peer;
      if (var4 != null) {
         var4.select(var1, var2);
      }

   }

   public synchronized void selectAll() {
      this.selectionStart = 0;
      this.selectionEnd = this.getText().length();
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      if (var1 != null) {
         var1.select(this.selectionStart, this.selectionEnd);
      }

   }

   public synchronized void setCaretPosition(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("position less than zero.");
      } else {
         int var2 = this.getText().length();
         if (var1 > var2) {
            var1 = var2;
         }

         TextComponentPeer var3 = (TextComponentPeer)this.peer;
         if (var3 != null) {
            var3.setCaretPosition(var1);
         } else {
            this.select(var1, var1);
         }

      }
   }

   public synchronized int getCaretPosition() {
      TextComponentPeer var1 = (TextComponentPeer)this.peer;
      boolean var2 = false;
      int var4;
      if (var1 != null) {
         var4 = var1.getCaretPosition();
      } else {
         var4 = this.selectionStart;
      }

      int var3 = this.getText().length();
      if (var4 > var3) {
         var4 = var3;
      }

      return var4;
   }

   public synchronized void addTextListener(TextListener var1) {
      if (var1 != null) {
         this.textListener = AWTEventMulticaster.add(this.textListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeTextListener(TextListener var1) {
      if (var1 != null) {
         this.textListener = AWTEventMulticaster.remove(this.textListener, var1);
      }
   }

   public synchronized TextListener[] getTextListeners() {
      return (TextListener[])this.getListeners(TextListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      TextListener var2 = null;
      if (var1 == TextListener.class) {
         var2 = this.textListener;
         return AWTEventMulticaster.getListeners(var2, var1);
      } else {
         return super.getListeners(var1);
      }
   }

   boolean eventEnabled(AWTEvent var1) {
      if (var1.id == 900) {
         return (this.eventMask & 1024L) != 0L || this.textListener != null;
      } else {
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof TextEvent) {
         this.processTextEvent((TextEvent)var1);
      } else {
         super.processEvent(var1);
      }
   }

   protected void processTextEvent(TextEvent var1) {
      TextListener var2 = this.textListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 900:
            var2.textValueChanged(var1);
         }
      }

   }

   protected String paramString() {
      String var1 = super.paramString() + ",text=" + this.getText();
      if (this.editable) {
         var1 = var1 + ",editable";
      }

      return var1 + ",selection=" + this.getSelectionStart() + "-" + this.getSelectionEnd();
   }

   private boolean canAccessClipboard() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 == null) {
         return true;
      } else {
         try {
            var1.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
            return true;
         } catch (SecurityException var3) {
            return false;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      TextComponentPeer var2 = (TextComponentPeer)this.peer;
      if (var2 != null) {
         this.text = var2.getText();
         this.selectionStart = var2.getSelectionStart();
         this.selectionEnd = var2.getSelectionEnd();
      }

      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "textL", this.textListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      var1.defaultReadObject();
      this.text = this.text != null ? this.text : "";
      this.select(this.selectionStart, this.selectionEnd);

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("textL" == var3) {
            this.addTextListener((TextListener)((TextListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

      this.enableInputMethodsIfNecessary();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new TextComponent.AccessibleAWTTextComponent();
      }

      return this.accessibleContext;
   }

   protected class AccessibleAWTTextComponent extends Component.AccessibleAWTComponent implements AccessibleText, TextListener {
      private static final long serialVersionUID = 3631432373506317811L;
      private static final boolean NEXT = true;
      private static final boolean PREVIOUS = false;

      public AccessibleAWTTextComponent() {
         super();
         TextComponent.this.addTextListener(this);
      }

      public void textValueChanged(TextEvent var1) {
         Integer var2 = TextComponent.this.getCaretPosition();
         this.firePropertyChange("AccessibleText", (Object)null, var2);
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (TextComponent.this.isEditable()) {
            var1.add(AccessibleState.EDITABLE);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TEXT;
      }

      public AccessibleText getAccessibleText() {
         return this;
      }

      public int getIndexAtPoint(Point var1) {
         return -1;
      }

      public Rectangle getCharacterBounds(int var1) {
         return null;
      }

      public int getCharCount() {
         return TextComponent.this.getText().length();
      }

      public int getCaretPosition() {
         return TextComponent.this.getCaretPosition();
      }

      public AttributeSet getCharacterAttribute(int var1) {
         return null;
      }

      public int getSelectionStart() {
         return TextComponent.this.getSelectionStart();
      }

      public int getSelectionEnd() {
         return TextComponent.this.getSelectionEnd();
      }

      public String getSelectedText() {
         String var1 = TextComponent.this.getSelectedText();
         return var1 != null && !var1.equals("") ? var1 : null;
      }

      public String getAtIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < TextComponent.this.getText().length()) {
            String var3;
            BreakIterator var4;
            int var5;
            switch(var1) {
            case 1:
               return TextComponent.this.getText().substring(var2, var2 + 1);
            case 2:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getWordInstance();
               var4.setText(var3);
               var5 = var4.following(var2);
               return var3.substring(var4.previous(), var5);
            case 3:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getSentenceInstance();
               var4.setText(var3);
               var5 = var4.following(var2);
               return var3.substring(var4.previous(), var5);
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      private int findWordLimit(int var1, BreakIterator var2, boolean var3, String var4) {
         int var5 = var3 ? var2.following(var1) : var2.preceding(var1);

         for(int var6 = var3 ? var2.next() : var2.previous(); var6 != -1; var6 = var3 ? var2.next() : var2.previous()) {
            for(int var7 = Math.min(var5, var6); var7 < Math.max(var5, var6); ++var7) {
               if (Character.isLetter(var4.charAt(var7))) {
                  return var5;
               }
            }

            var5 = var6;
         }

         return -1;
      }

      public String getAfterIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < TextComponent.this.getText().length()) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 + 1 >= TextComponent.this.getText().length()) {
                  return null;
               }

               return TextComponent.this.getText().substring(var2 + 1, var2 + 2);
            case 2:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getWordInstance();
               var4.setText(var3);
               var5 = this.findWordLimit(var2, var4, true, var3);
               if (var5 != -1 && var5 < var3.length()) {
                  var6 = var4.following(var5);
                  if (var6 != -1 && var6 < var3.length()) {
                     return var3.substring(var5, var6);
                  }

                  return null;
               }

               return null;
            case 3:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getSentenceInstance();
               var4.setText(var3);
               var5 = var4.following(var2);
               if (var5 != -1 && var5 < var3.length()) {
                  var6 = var4.following(var5);
                  if (var6 != -1 && var6 < var3.length()) {
                     return var3.substring(var5, var6);
                  }

                  return null;
               }

               return null;
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public String getBeforeIndex(int var1, int var2) {
         if (var2 >= 0 && var2 <= TextComponent.this.getText().length() - 1) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 == 0) {
                  return null;
               }

               return TextComponent.this.getText().substring(var2 - 1, var2);
            case 2:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getWordInstance();
               var4.setText(var3);
               var5 = this.findWordLimit(var2, var4, false, var3);
               if (var5 == -1) {
                  return null;
               } else {
                  var6 = var4.preceding(var5);
                  if (var6 == -1) {
                     return null;
                  }

                  return var3.substring(var6, var5);
               }
            case 3:
               var3 = TextComponent.this.getText();
               var4 = BreakIterator.getSentenceInstance();
               var4.setText(var3);
               var4.following(var2);
               var5 = var4.previous();
               var6 = var4.previous();
               if (var6 == -1) {
                  return null;
               }

               return var3.substring(var6, var5);
            default:
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
