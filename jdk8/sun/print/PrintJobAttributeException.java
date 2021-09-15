package sun.print;

import javax.print.AttributeException;
import javax.print.PrintException;
import javax.print.attribute.Attribute;

class PrintJobAttributeException extends PrintException implements AttributeException {
   private Attribute attr;
   private Class category;

   PrintJobAttributeException(String var1, Class var2, Attribute var3) {
      super(var1);
      this.attr = var3;
      this.category = var2;
   }

   public Class[] getUnsupportedAttributes() {
      if (this.category == null) {
         return null;
      } else {
         Class[] var1 = new Class[]{this.category};
         return var1;
      }
   }

   public Attribute[] getUnsupportedValues() {
      if (this.attr == null) {
         return null;
      } else {
         Attribute[] var1 = new Attribute[]{this.attr};
         return var1;
      }
   }
}
