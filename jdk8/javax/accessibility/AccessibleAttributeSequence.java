package javax.accessibility;

import javax.swing.text.AttributeSet;

public class AccessibleAttributeSequence {
   public int startIndex;
   public int endIndex;
   public AttributeSet attributes;

   public AccessibleAttributeSequence(int var1, int var2, AttributeSet var3) {
      this.startIndex = var1;
      this.endIndex = var2;
      this.attributes = var3;
   }
}
