package javax.swing.text;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class DefaultFormatter extends JFormattedTextField.AbstractFormatter implements Cloneable, Serializable {
   private boolean allowsInvalid = true;
   private boolean overwriteMode = true;
   private boolean commitOnEdit;
   private Class<?> valueClass;
   private NavigationFilter navigationFilter;
   private DocumentFilter documentFilter;
   transient DefaultFormatter.ReplaceHolder replaceHolder;

   public void install(JFormattedTextField var1) {
      super.install(var1);
      this.positionCursorAtInitialLocation();
   }

   public void setCommitsOnValidEdit(boolean var1) {
      this.commitOnEdit = var1;
   }

   public boolean getCommitsOnValidEdit() {
      return this.commitOnEdit;
   }

   public void setOverwriteMode(boolean var1) {
      this.overwriteMode = var1;
   }

   public boolean getOverwriteMode() {
      return this.overwriteMode;
   }

   public void setAllowsInvalid(boolean var1) {
      this.allowsInvalid = var1;
   }

   public boolean getAllowsInvalid() {
      return this.allowsInvalid;
   }

   public void setValueClass(Class<?> var1) {
      this.valueClass = var1;
   }

   public Class<?> getValueClass() {
      return this.valueClass;
   }

   public Object stringToValue(String var1) throws ParseException {
      Class var2 = this.getValueClass();
      JFormattedTextField var3 = this.getFormattedTextField();
      if (var2 == null && var3 != null) {
         Object var4 = var3.getValue();
         if (var4 != null) {
            var2 = var4.getClass();
         }
      }

      if (var2 != null) {
         Constructor var8;
         try {
            ReflectUtil.checkPackageAccess(var2);
            SwingUtilities2.checkAccess(var2.getModifiers());
            var8 = var2.getConstructor(String.class);
         } catch (NoSuchMethodException var7) {
            var8 = null;
         }

         if (var8 != null) {
            try {
               SwingUtilities2.checkAccess(var8.getModifiers());
               return var8.newInstance(var1);
            } catch (Throwable var6) {
               throw new ParseException("Error creating instance", 0);
            }
         }
      }

      return var1;
   }

   public String valueToString(Object var1) throws ParseException {
      return var1 == null ? "" : var1.toString();
   }

   protected DocumentFilter getDocumentFilter() {
      if (this.documentFilter == null) {
         this.documentFilter = new DefaultFormatter.DefaultDocumentFilter();
      }

      return this.documentFilter;
   }

   protected NavigationFilter getNavigationFilter() {
      if (this.navigationFilter == null) {
         this.navigationFilter = new DefaultFormatter.DefaultNavigationFilter();
      }

      return this.navigationFilter;
   }

   public Object clone() throws CloneNotSupportedException {
      DefaultFormatter var1 = (DefaultFormatter)super.clone();
      var1.navigationFilter = null;
      var1.documentFilter = null;
      var1.replaceHolder = null;
      return var1;
   }

   void positionCursorAtInitialLocation() {
      JFormattedTextField var1 = this.getFormattedTextField();
      if (var1 != null) {
         var1.setCaretPosition(this.getInitialVisualPosition());
      }

   }

   int getInitialVisualPosition() {
      return this.getNextNavigatableChar(0, 1);
   }

   boolean isNavigatable(int var1) {
      return true;
   }

   boolean isLegalInsertText(String var1) {
      return true;
   }

   private int getNextNavigatableChar(int var1, int var2) {
      for(int var3 = this.getFormattedTextField().getDocument().getLength(); var1 >= 0 && var1 < var3; var1 += var2) {
         if (this.isNavigatable(var1)) {
            return var1;
         }
      }

      return var1;
   }

   String getReplaceString(int var1, int var2, String var3) {
      String var4 = this.getFormattedTextField().getText();
      String var5 = var4.substring(0, var1);
      if (var3 != null) {
         var5 = var5 + var3;
      }

      if (var1 + var2 < var4.length()) {
         var5 = var5 + var4.substring(var1 + var2);
      }

      return var5;
   }

   boolean isValidEdit(DefaultFormatter.ReplaceHolder var1) {
      if (!this.getAllowsInvalid()) {
         String var2 = this.getReplaceString(var1.offset, var1.length, var1.text);

         try {
            var1.value = this.stringToValue(var2);
            return true;
         } catch (ParseException var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   void commitEdit() throws ParseException {
      JFormattedTextField var1 = this.getFormattedTextField();
      if (var1 != null) {
         var1.commitEdit();
      }

   }

   void updateValue() {
      this.updateValue((Object)null);
   }

   void updateValue(Object var1) {
      try {
         if (var1 == null) {
            String var2 = this.getFormattedTextField().getText();
            this.stringToValue(var2);
         }

         if (this.getCommitsOnValidEdit()) {
            this.commitEdit();
         }

         this.setEditValid(true);
      } catch (ParseException var3) {
         this.setEditValid(false);
      }

   }

   int getNextCursorPosition(int var1, int var2) {
      int var3 = this.getNextNavigatableChar(var1, var2);
      int var4 = this.getFormattedTextField().getDocument().getLength();
      if (!this.getAllowsInvalid()) {
         if (var2 == -1 && var1 == var3) {
            var3 = this.getNextNavigatableChar(var3, 1);
            if (var3 >= var4) {
               var3 = var1;
            }
         } else if (var2 == 1 && var3 >= var4) {
            var3 = this.getNextNavigatableChar(var4 - 1, -1);
            if (var3 < var4) {
               ++var3;
            }
         }
      }

      return var3;
   }

   void repositionCursor(int var1, int var2) {
      this.getFormattedTextField().getCaret().setDot(this.getNextCursorPosition(var1, var2));
   }

   int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException {
      int var6 = var1.getUI().getNextVisualPositionFrom(var1, var2, var3, var4, var5);
      if (var6 == -1) {
         return -1;
      } else {
         if (!this.getAllowsInvalid() && (var4 == 3 || var4 == 7)) {
            int var7;
            for(var7 = -1; !this.isNavigatable(var6) && var6 != var7; var6 = var1.getUI().getNextVisualPositionFrom(var1, var6, var3, var4, var5)) {
               var7 = var6;
            }

            int var8 = this.getFormattedTextField().getDocument().getLength();
            if (var7 == var6 || var6 == var8) {
               if (var6 == 0) {
                  var5[0] = Position.Bias.Forward;
                  var6 = this.getInitialVisualPosition();
               }

               if (var6 >= var8 && var8 > 0) {
                  var5[0] = Position.Bias.Forward;
                  var6 = this.getNextNavigatableChar(var8 - 1, -1) + 1;
               }
            }
         }

         return var6;
      }
   }

   boolean canReplace(DefaultFormatter.ReplaceHolder var1) {
      return this.isValidEdit(var1);
   }

   void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
      DefaultFormatter.ReplaceHolder var6 = this.getReplaceHolder(var1, var2, var3, var4, var5);
      this.replace(var6);
   }

   boolean replace(DefaultFormatter.ReplaceHolder var1) throws BadLocationException {
      boolean var2 = true;
      byte var3 = 1;
      if (var1.length > 0 && (var1.text == null || var1.text.length() == 0) && (this.getFormattedTextField().getSelectionStart() != var1.offset || var1.length > 1)) {
         var3 = -1;
      }

      if (this.getOverwriteMode() && var1.text != null && this.getFormattedTextField().getSelectedText() == null) {
         var1.length = Math.min(Math.max(var1.length, var1.text.length()), var1.fb.getDocument().getLength() - var1.offset);
      }

      if (var1.text != null && !this.isLegalInsertText(var1.text) || !this.canReplace(var1) || var1.length == 0 && (var1.text == null || var1.text.length() == 0)) {
         var2 = false;
      }

      if (var2) {
         int var4 = var1.cursorPosition;
         var1.fb.replace(var1.offset, var1.length, var1.text, var1.attrs);
         if (var4 == -1) {
            var4 = var1.offset;
            if (var3 == 1 && var1.text != null) {
               var4 = var1.offset + var1.text.length();
            }
         }

         this.updateValue(var1.value);
         this.repositionCursor(var4, var3);
         return true;
      } else {
         this.invalidEdit();
         return false;
      }
   }

   void setDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
      var1.setDot(var2, var3);
   }

   void moveDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
      var1.moveDot(var2, var3);
   }

   DefaultFormatter.ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) {
      if (this.replaceHolder == null) {
         this.replaceHolder = new DefaultFormatter.ReplaceHolder();
      }

      this.replaceHolder.reset(var1, var2, var3, var4, var5);
      return this.replaceHolder;
   }

   private class DefaultDocumentFilter extends DocumentFilter implements Serializable {
      private DefaultDocumentFilter() {
      }

      public void remove(DocumentFilter.FilterBypass var1, int var2, int var3) throws BadLocationException {
         JFormattedTextField var4 = DefaultFormatter.this.getFormattedTextField();
         if (var4.composedTextExists()) {
            var1.remove(var2, var3);
         } else {
            DefaultFormatter.this.replace(var1, var2, var3, (String)null, (AttributeSet)null);
         }

      }

      public void insertString(DocumentFilter.FilterBypass var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
         JFormattedTextField var5 = DefaultFormatter.this.getFormattedTextField();
         if (!var5.composedTextExists() && !Utilities.isComposedTextAttributeDefined(var4)) {
            DefaultFormatter.this.replace(var1, var2, 0, var3, var4);
         } else {
            var1.insertString(var2, var3, var4);
         }

      }

      public void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
         JFormattedTextField var6 = DefaultFormatter.this.getFormattedTextField();
         if (!var6.composedTextExists() && !Utilities.isComposedTextAttributeDefined(var5)) {
            DefaultFormatter.this.replace(var1, var2, var3, var4, var5);
         } else {
            var1.replace(var2, var3, var4, var5);
         }

      }

      // $FF: synthetic method
      DefaultDocumentFilter(Object var2) {
         this();
      }
   }

   private class DefaultNavigationFilter extends NavigationFilter implements Serializable {
      private DefaultNavigationFilter() {
      }

      public void setDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
         JFormattedTextField var4 = DefaultFormatter.this.getFormattedTextField();
         if (var4.composedTextExists()) {
            var1.setDot(var2, var3);
         } else {
            DefaultFormatter.this.setDot(var1, var2, var3);
         }

      }

      public void moveDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
         JFormattedTextField var4 = DefaultFormatter.this.getFormattedTextField();
         if (var4.composedTextExists()) {
            var1.moveDot(var2, var3);
         } else {
            DefaultFormatter.this.moveDot(var1, var2, var3);
         }

      }

      public int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException {
         return var1.composedTextExists() ? var1.getUI().getNextVisualPositionFrom(var1, var2, var3, var4, var5) : DefaultFormatter.this.getNextVisualPositionFrom(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      DefaultNavigationFilter(Object var2) {
         this();
      }
   }

   static class ReplaceHolder {
      DocumentFilter.FilterBypass fb;
      int offset;
      int length;
      String text;
      AttributeSet attrs;
      Object value;
      int cursorPosition;

      void reset(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) {
         this.fb = var1;
         this.offset = var2;
         this.length = var3;
         this.text = var4;
         this.attrs = var5;
         this.value = null;
         this.cursorPosition = -1;
      }
   }
}
