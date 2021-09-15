package javax.swing.text;

import java.io.Serializable;
import javax.swing.JFormattedTextField;

public class DefaultFormatterFactory extends JFormattedTextField.AbstractFormatterFactory implements Serializable {
   private JFormattedTextField.AbstractFormatter defaultFormat;
   private JFormattedTextField.AbstractFormatter displayFormat;
   private JFormattedTextField.AbstractFormatter editFormat;
   private JFormattedTextField.AbstractFormatter nullFormat;

   public DefaultFormatterFactory() {
   }

   public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter var1) {
      this(var1, (JFormattedTextField.AbstractFormatter)null);
   }

   public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter var1, JFormattedTextField.AbstractFormatter var2) {
      this(var1, var2, (JFormattedTextField.AbstractFormatter)null);
   }

   public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter var1, JFormattedTextField.AbstractFormatter var2, JFormattedTextField.AbstractFormatter var3) {
      this(var1, var2, var3, (JFormattedTextField.AbstractFormatter)null);
   }

   public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter var1, JFormattedTextField.AbstractFormatter var2, JFormattedTextField.AbstractFormatter var3, JFormattedTextField.AbstractFormatter var4) {
      this.defaultFormat = var1;
      this.displayFormat = var2;
      this.editFormat = var3;
      this.nullFormat = var4;
   }

   public void setDefaultFormatter(JFormattedTextField.AbstractFormatter var1) {
      this.defaultFormat = var1;
   }

   public JFormattedTextField.AbstractFormatter getDefaultFormatter() {
      return this.defaultFormat;
   }

   public void setDisplayFormatter(JFormattedTextField.AbstractFormatter var1) {
      this.displayFormat = var1;
   }

   public JFormattedTextField.AbstractFormatter getDisplayFormatter() {
      return this.displayFormat;
   }

   public void setEditFormatter(JFormattedTextField.AbstractFormatter var1) {
      this.editFormat = var1;
   }

   public JFormattedTextField.AbstractFormatter getEditFormatter() {
      return this.editFormat;
   }

   public void setNullFormatter(JFormattedTextField.AbstractFormatter var1) {
      this.nullFormat = var1;
   }

   public JFormattedTextField.AbstractFormatter getNullFormatter() {
      return this.nullFormat;
   }

   public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField var1) {
      JFormattedTextField.AbstractFormatter var2 = null;
      if (var1 == null) {
         return null;
      } else {
         Object var3 = var1.getValue();
         if (var3 == null) {
            var2 = this.getNullFormatter();
         }

         if (var2 == null) {
            if (var1.hasFocus()) {
               var2 = this.getEditFormatter();
            } else {
               var2 = this.getDisplayFormatter();
            }

            if (var2 == null) {
               var2 = this.getDefaultFormatter();
            }
         }

         return var2;
      }
   }
}
