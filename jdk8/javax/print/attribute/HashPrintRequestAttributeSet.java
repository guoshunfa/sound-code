package javax.print.attribute;

import java.io.Serializable;

public class HashPrintRequestAttributeSet extends HashAttributeSet implements PrintRequestAttributeSet, Serializable {
   private static final long serialVersionUID = 2364756266107751933L;

   public HashPrintRequestAttributeSet() {
      super(PrintRequestAttribute.class);
   }

   public HashPrintRequestAttributeSet(PrintRequestAttribute var1) {
      super((Attribute)var1, PrintRequestAttribute.class);
   }

   public HashPrintRequestAttributeSet(PrintRequestAttribute[] var1) {
      super((Attribute[])var1, PrintRequestAttribute.class);
   }

   public HashPrintRequestAttributeSet(PrintRequestAttributeSet var1) {
      super((AttributeSet)var1, PrintRequestAttribute.class);
   }
}
