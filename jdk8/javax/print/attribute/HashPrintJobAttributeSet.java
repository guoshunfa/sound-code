package javax.print.attribute;

import java.io.Serializable;

public class HashPrintJobAttributeSet extends HashAttributeSet implements PrintJobAttributeSet, Serializable {
   private static final long serialVersionUID = -4204473656070350348L;

   public HashPrintJobAttributeSet() {
      super(PrintJobAttribute.class);
   }

   public HashPrintJobAttributeSet(PrintJobAttribute var1) {
      super((Attribute)var1, PrintJobAttribute.class);
   }

   public HashPrintJobAttributeSet(PrintJobAttribute[] var1) {
      super((Attribute[])var1, PrintJobAttribute.class);
   }

   public HashPrintJobAttributeSet(PrintJobAttributeSet var1) {
      super((AttributeSet)var1, PrintJobAttribute.class);
   }
}
