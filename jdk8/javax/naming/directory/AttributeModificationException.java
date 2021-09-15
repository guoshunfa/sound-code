package javax.naming.directory;

import javax.naming.NamingException;

public class AttributeModificationException extends NamingException {
   private ModificationItem[] unexecs = null;
   private static final long serialVersionUID = 8060676069678710186L;

   public AttributeModificationException(String var1) {
      super(var1);
   }

   public AttributeModificationException() {
   }

   public void setUnexecutedModifications(ModificationItem[] var1) {
      this.unexecs = var1;
   }

   public ModificationItem[] getUnexecutedModifications() {
      return this.unexecs;
   }

   public String toString() {
      String var1 = super.toString();
      if (this.unexecs != null) {
         var1 = var1 + "First unexecuted modification: " + this.unexecs[0].toString();
      }

      return var1;
   }
}
