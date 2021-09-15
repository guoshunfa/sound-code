package javax.swing.colorchooser;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;

final class ValueFormatter extends JFormattedTextField.AbstractFormatter implements FocusListener, Runnable {
   private final DocumentFilter filter = new DocumentFilter() {
      public void remove(DocumentFilter.FilterBypass var1, int var2, int var3) throws BadLocationException {
         if (ValueFormatter.this.isValid(var1.getDocument().getLength() - var3)) {
            var1.remove(var2, var3);
         }

      }

      public void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
         if (ValueFormatter.this.isValid(var1.getDocument().getLength() + var4.length() - var3) && ValueFormatter.this.isValid(var4)) {
            var1.replace(var2, var3, var4.toUpperCase(Locale.ENGLISH), var5);
         }

      }

      public void insertString(DocumentFilter.FilterBypass var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
         if (ValueFormatter.this.isValid(var1.getDocument().getLength() + var3.length()) && ValueFormatter.this.isValid(var3)) {
            var1.insertString(var2, var3.toUpperCase(Locale.ENGLISH), var4);
         }

      }
   };
   private final int length;
   private final int radix;
   private JFormattedTextField text;

   static void init(int var0, boolean var1, JFormattedTextField var2) {
      ValueFormatter var3 = new ValueFormatter(var0, var1);
      var2.setColumns(var0);
      var2.setFormatterFactory(new DefaultFormatterFactory(var3));
      var2.setHorizontalAlignment(4);
      var2.setMinimumSize(var2.getPreferredSize());
      var2.addFocusListener(var3);
   }

   ValueFormatter(int var1, boolean var2) {
      this.length = var1;
      this.radix = var2 ? 16 : 10;
   }

   public Object stringToValue(String var1) throws ParseException {
      try {
         return Integer.valueOf(var1, this.radix);
      } catch (NumberFormatException var4) {
         ParseException var3 = new ParseException("illegal format", 0);
         var3.initCause(var4);
         throw var3;
      }
   }

   public String valueToString(Object var1) throws ParseException {
      if (!(var1 instanceof Integer)) {
         throw new ParseException("illegal object", 0);
      } else if (this.radix == 10) {
         return var1.toString();
      } else {
         int var2 = (Integer)var1;
         int var3 = this.length;

         char[] var4;
         for(var4 = new char[var3]; 0 < var3--; var2 >>= 4) {
            var4[var3] = Character.forDigit(var2 & 15, this.radix);
         }

         return (new String(var4)).toUpperCase(Locale.ENGLISH);
      }
   }

   protected DocumentFilter getDocumentFilter() {
      return this.filter;
   }

   public void focusGained(FocusEvent var1) {
      Object var2 = var1.getSource();
      if (var2 instanceof JFormattedTextField) {
         this.text = (JFormattedTextField)var2;
         SwingUtilities.invokeLater(this);
      }

   }

   public void focusLost(FocusEvent var1) {
   }

   public void run() {
      if (this.text != null) {
         this.text.selectAll();
      }

   }

   private boolean isValid(int var1) {
      return 0 <= var1 && var1 <= this.length;
   }

   private boolean isValid(String var1) {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         if (Character.digit(var4, this.radix) < 0) {
            return false;
         }
      }

      return true;
   }
}
