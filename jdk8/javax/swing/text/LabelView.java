package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Toolkit;
import javax.swing.event.DocumentEvent;

public class LabelView extends GlyphView implements TabableView {
   private Font font;
   private Color fg;
   private Color bg;
   private boolean underline;
   private boolean strike;
   private boolean superscript;
   private boolean subscript;

   public LabelView(Element var1) {
      super(var1);
   }

   final void sync() {
      if (this.font == null) {
         this.setPropertiesFromAttributes();
      }

   }

   protected void setUnderline(boolean var1) {
      this.underline = var1;
   }

   protected void setStrikeThrough(boolean var1) {
      this.strike = var1;
   }

   protected void setSuperscript(boolean var1) {
      this.superscript = var1;
   }

   protected void setSubscript(boolean var1) {
      this.subscript = var1;
   }

   protected void setBackground(Color var1) {
      this.bg = var1;
   }

   protected void setPropertiesFromAttributes() {
      AttributeSet var1 = this.getAttributes();
      if (var1 != null) {
         Document var2 = this.getDocument();
         if (!(var2 instanceof StyledDocument)) {
            throw new StateInvariantError("LabelView needs StyledDocument");
         }

         StyledDocument var3 = (StyledDocument)var2;
         this.font = var3.getFont(var1);
         this.fg = var3.getForeground(var1);
         if (var1.isDefined(StyleConstants.Background)) {
            this.bg = var3.getBackground(var1);
         } else {
            this.bg = null;
         }

         this.setUnderline(StyleConstants.isUnderline(var1));
         this.setStrikeThrough(StyleConstants.isStrikeThrough(var1));
         this.setSuperscript(StyleConstants.isSuperscript(var1));
         this.setSubscript(StyleConstants.isSubscript(var1));
      }

   }

   /** @deprecated */
   @Deprecated
   protected FontMetrics getFontMetrics() {
      this.sync();
      Container var1 = this.getContainer();
      return var1 != null ? var1.getFontMetrics(this.font) : Toolkit.getDefaultToolkit().getFontMetrics(this.font);
   }

   public Color getBackground() {
      this.sync();
      return this.bg;
   }

   public Color getForeground() {
      this.sync();
      return this.fg;
   }

   public Font getFont() {
      this.sync();
      return this.font;
   }

   public boolean isUnderline() {
      this.sync();
      return this.underline;
   }

   public boolean isStrikeThrough() {
      this.sync();
      return this.strike;
   }

   public boolean isSubscript() {
      this.sync();
      return this.subscript;
   }

   public boolean isSuperscript() {
      this.sync();
      return this.superscript;
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.font = null;
      super.changedUpdate(var1, var2, var3);
   }
}
