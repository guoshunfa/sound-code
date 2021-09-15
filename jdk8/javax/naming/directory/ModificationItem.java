package javax.naming.directory;

import java.io.Serializable;

public class ModificationItem implements Serializable {
   private int mod_op;
   private Attribute attr;
   private static final long serialVersionUID = 7573258562534746850L;

   public ModificationItem(int var1, Attribute var2) {
      switch(var1) {
      case 1:
      case 2:
      case 3:
         if (var2 == null) {
            throw new IllegalArgumentException("Must specify non-null attribute for modification");
         }

         this.mod_op = var1;
         this.attr = var2;
         return;
      default:
         throw new IllegalArgumentException("Invalid modification code " + var1);
      }
   }

   public int getModificationOp() {
      return this.mod_op;
   }

   public Attribute getAttribute() {
      return this.attr;
   }

   public String toString() {
      switch(this.mod_op) {
      case 1:
         return "Add attribute: " + this.attr.toString();
      case 2:
         return "Replace attribute: " + this.attr.toString();
      case 3:
         return "Remove attribute: " + this.attr.toString();
      default:
         return "";
      }
   }
}
