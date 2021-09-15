package javax.print.attribute;

import java.io.Serializable;

public class HashPrintServiceAttributeSet extends HashAttributeSet implements PrintServiceAttributeSet, Serializable {
   private static final long serialVersionUID = 6642904616179203070L;

   public HashPrintServiceAttributeSet() {
      super(PrintServiceAttribute.class);
   }

   public HashPrintServiceAttributeSet(PrintServiceAttribute var1) {
      super((Attribute)var1, PrintServiceAttribute.class);
   }

   public HashPrintServiceAttributeSet(PrintServiceAttribute[] var1) {
      super((Attribute[])var1, PrintServiceAttribute.class);
   }

   public HashPrintServiceAttributeSet(PrintServiceAttributeSet var1) {
      super((AttributeSet)var1, PrintServiceAttribute.class);
   }
}
