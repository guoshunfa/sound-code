package javax.swing.text.html;

import java.awt.Color;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class InlineView extends LabelView {
   private boolean nowrap;
   private AttributeSet attr;

   public InlineView(Element var1) {
      super(var1);
      StyleSheet var2 = this.getStyleSheet();
      this.attr = var2.getViewAttributes(this);
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.insertUpdate(var1, var2, var3);
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.removeUpdate(var1, var2, var3);
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.changedUpdate(var1, var2, var3);
      StyleSheet var4 = this.getStyleSheet();
      this.attr = var4.getViewAttributes(this);
      this.preferenceChanged((View)null, true, true);
   }

   public AttributeSet getAttributes() {
      return this.attr;
   }

   public int getBreakWeight(int var1, float var2, float var3) {
      return this.nowrap ? 0 : super.getBreakWeight(var1, var2, var3);
   }

   public View breakView(int var1, int var2, float var3, float var4) {
      return super.breakView(var1, var2, var3, var4);
   }

   protected void setPropertiesFromAttributes() {
      super.setPropertiesFromAttributes();
      AttributeSet var1 = this.getAttributes();
      Object var2 = var1.getAttribute(CSS.Attribute.TEXT_DECORATION);
      boolean var3 = var2 != null ? var2.toString().indexOf("underline") >= 0 : false;
      this.setUnderline(var3);
      boolean var4 = var2 != null ? var2.toString().indexOf("line-through") >= 0 : false;
      this.setStrikeThrough(var4);
      Object var5 = var1.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
      var4 = var5 != null ? var5.toString().indexOf("sup") >= 0 : false;
      this.setSuperscript(var4);
      var4 = var5 != null ? var5.toString().indexOf("sub") >= 0 : false;
      this.setSubscript(var4);
      Object var6 = var1.getAttribute(CSS.Attribute.WHITE_SPACE);
      if (var6 != null && var6.equals("nowrap")) {
         this.nowrap = true;
      } else {
         this.nowrap = false;
      }

      HTMLDocument var7 = (HTMLDocument)this.getDocument();
      Color var8 = var7.getBackground(var1);
      if (var8 != null) {
         this.setBackground(var8);
      }

   }

   protected StyleSheet getStyleSheet() {
      HTMLDocument var1 = (HTMLDocument)this.getDocument();
      return var1.getStyleSheet();
   }
}
