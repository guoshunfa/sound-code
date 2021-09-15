package javax.naming;

import java.io.Serializable;

public abstract class RefAddr implements Serializable {
   protected String addrType;
   private static final long serialVersionUID = -1468165120479154358L;

   protected RefAddr(String var1) {
      this.addrType = var1;
   }

   public String getType() {
      return this.addrType;
   }

   public abstract Object getContent();

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof RefAddr) {
         RefAddr var2 = (RefAddr)var1;
         if (this.addrType.compareTo(var2.addrType) == 0) {
            Object var3 = this.getContent();
            Object var4 = var2.getContent();
            if (var3 == var4) {
               return true;
            }

            if (var3 != null) {
               return var3.equals(var4);
            }
         }
      }

      return false;
   }

   public int hashCode() {
      return this.getContent() == null ? this.addrType.hashCode() : this.addrType.hashCode() + this.getContent().hashCode();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("Type: " + this.addrType + "\n");
      var1.append("Content: " + this.getContent() + "\n");
      return var1.toString();
   }
}
