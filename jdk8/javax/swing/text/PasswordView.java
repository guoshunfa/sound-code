package javax.swing.text;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JPasswordField;
import sun.swing.SwingUtilities2;

public class PasswordView extends FieldView {
   static char[] ONE = new char[1];

   public PasswordView(Element var1) {
      super(var1);
   }

   protected int drawUnselectedText(Graphics var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      Container var6 = this.getContainer();
      if (var6 instanceof JPasswordField) {
         JPasswordField var7 = (JPasswordField)var6;
         if (!var7.echoCharIsSet()) {
            return super.drawUnselectedText(var1, var2, var3, var4, var5);
         }

         if (var7.isEnabled()) {
            var1.setColor(var7.getForeground());
         } else {
            var1.setColor(var7.getDisabledTextColor());
         }

         char var8 = var7.getEchoChar();
         int var9 = var5 - var4;

         for(int var10 = 0; var10 < var9; ++var10) {
            var2 = this.drawEchoCharacter(var1, var2, var3, var8);
         }
      }

      return var2;
   }

   protected int drawSelectedText(Graphics var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      var1.setColor(this.selected);
      Container var6 = this.getContainer();
      if (var6 instanceof JPasswordField) {
         JPasswordField var7 = (JPasswordField)var6;
         if (!var7.echoCharIsSet()) {
            return super.drawSelectedText(var1, var2, var3, var4, var5);
         }

         char var8 = var7.getEchoChar();
         int var9 = var5 - var4;

         for(int var10 = 0; var10 < var9; ++var10) {
            var2 = this.drawEchoCharacter(var1, var2, var3, var8);
         }
      }

      return var2;
   }

   protected int drawEchoCharacter(Graphics var1, int var2, int var3, char var4) {
      ONE[0] = var4;
      SwingUtilities2.drawChars(Utilities.getJComponent(this), var1, ONE, 0, 1, var2, var3);
      return var2 + var1.getFontMetrics().charWidth(var4);
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      Container var4 = this.getContainer();
      if (var4 instanceof JPasswordField) {
         JPasswordField var5 = (JPasswordField)var4;
         if (!var5.echoCharIsSet()) {
            return super.modelToView(var1, var2, var3);
         } else {
            char var6 = var5.getEchoChar();
            FontMetrics var7 = var5.getFontMetrics(var5.getFont());
            Rectangle var8 = this.adjustAllocation(var2).getBounds();
            int var9 = (var1 - this.getStartOffset()) * var7.charWidth(var6);
            var8.x += var9;
            var8.width = 1;
            return var8;
         }
      } else {
         return null;
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      var4[0] = Position.Bias.Forward;
      int var5 = 0;
      Container var6 = this.getContainer();
      if (var6 instanceof JPasswordField) {
         JPasswordField var7 = (JPasswordField)var6;
         if (!var7.echoCharIsSet()) {
            return super.viewToModel(var1, var2, var3, var4);
         }

         char var8 = var7.getEchoChar();
         int var9 = var7.getFontMetrics(var7.getFont()).charWidth(var8);
         var3 = this.adjustAllocation(var3);
         Rectangle var10 = var3 instanceof Rectangle ? (Rectangle)var3 : var3.getBounds();
         var5 = var9 > 0 ? ((int)var1 - var10.x) / var9 : Integer.MAX_VALUE;
         if (var5 < 0) {
            var5 = 0;
         } else if (var5 > this.getStartOffset() + this.getDocument().getLength()) {
            var5 = this.getDocument().getLength() - this.getStartOffset();
         }
      }

      return this.getStartOffset() + var5;
   }

   public float getPreferredSpan(int var1) {
      switch(var1) {
      case 0:
         Container var2 = this.getContainer();
         if (var2 instanceof JPasswordField) {
            JPasswordField var3 = (JPasswordField)var2;
            if (var3.echoCharIsSet()) {
               char var4 = var3.getEchoChar();
               FontMetrics var5 = var3.getFontMetrics(var3.getFont());
               Document var6 = this.getDocument();
               return (float)(var5.charWidth(var4) * this.getDocument().getLength());
            }
         }
      default:
         return super.getPreferredSpan(var1);
      }
   }
}
