package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.text.AttributeSet;

public class Option implements Serializable {
   private boolean selected;
   private String label;
   private AttributeSet attr;

   public Option(AttributeSet var1) {
      this.attr = var1.copyAttributes();
      this.selected = var1.getAttribute(HTML.Attribute.SELECTED) != null;
   }

   public void setLabel(String var1) {
      this.label = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public AttributeSet getAttributes() {
      return this.attr;
   }

   public String toString() {
      return this.label;
   }

   protected void setSelection(boolean var1) {
      this.selected = var1;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public String getValue() {
      String var1 = (String)this.attr.getAttribute(HTML.Attribute.VALUE);
      if (var1 == null) {
         var1 = this.label;
      }

      return var1;
   }
}
