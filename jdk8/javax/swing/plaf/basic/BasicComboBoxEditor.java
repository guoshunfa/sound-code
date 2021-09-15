package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import sun.reflect.misc.MethodUtil;

public class BasicComboBoxEditor implements ComboBoxEditor, FocusListener {
   protected JTextField editor = this.createEditorComponent();
   private Object oldValue;

   public Component getEditorComponent() {
      return this.editor;
   }

   protected JTextField createEditorComponent() {
      BasicComboBoxEditor.BorderlessTextField var1 = new BasicComboBoxEditor.BorderlessTextField("", 9);
      var1.setBorder((Border)null);
      return var1;
   }

   public void setItem(Object var1) {
      String var2;
      if (var1 != null) {
         var2 = var1.toString();
         if (var2 == null) {
            var2 = "";
         }

         this.oldValue = var1;
      } else {
         var2 = "";
      }

      if (!var2.equals(this.editor.getText())) {
         this.editor.setText(var2);
      }

   }

   public Object getItem() {
      Object var1 = this.editor.getText();
      if (this.oldValue != null && !(this.oldValue instanceof String)) {
         if (var1.equals(this.oldValue.toString())) {
            return this.oldValue;
         }

         Class var2 = this.oldValue.getClass();

         try {
            Method var3 = MethodUtil.getMethod(var2, "valueOf", new Class[]{String.class});
            var1 = MethodUtil.invoke(var3, this.oldValue, new Object[]{this.editor.getText()});
         } catch (Exception var4) {
         }
      }

      return var1;
   }

   public void selectAll() {
      this.editor.selectAll();
      this.editor.requestFocus();
   }

   public void focusGained(FocusEvent var1) {
   }

   public void focusLost(FocusEvent var1) {
   }

   public void addActionListener(ActionListener var1) {
      this.editor.addActionListener(var1);
   }

   public void removeActionListener(ActionListener var1) {
      this.editor.removeActionListener(var1);
   }

   public static class UIResource extends BasicComboBoxEditor implements javax.swing.plaf.UIResource {
   }

   static class BorderlessTextField extends JTextField {
      public BorderlessTextField(String var1, int var2) {
         super(var1, var2);
      }

      public void setText(String var1) {
         if (!this.getText().equals(var1)) {
            super.setText(var1);
         }
      }

      public void setBorder(Border var1) {
         if (!(var1 instanceof BasicComboBoxEditor.UIResource)) {
            super.setBorder(var1);
         }

      }
   }
}
