package javax.naming.spi;

import java.io.Serializable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class ResolveResult implements Serializable {
   protected Object resolvedObj;
   protected Name remainingName;
   private static final long serialVersionUID = -4552108072002407559L;

   protected ResolveResult() {
      this.resolvedObj = null;
      this.remainingName = null;
   }

   public ResolveResult(Object var1, String var2) {
      this.resolvedObj = var1;

      try {
         this.remainingName = new CompositeName(var2);
      } catch (InvalidNameException var4) {
      }

   }

   public ResolveResult(Object var1, Name var2) {
      this.resolvedObj = var1;
      this.setRemainingName(var2);
   }

   public Name getRemainingName() {
      return this.remainingName;
   }

   public Object getResolvedObj() {
      return this.resolvedObj;
   }

   public void setRemainingName(Name var1) {
      if (var1 != null) {
         this.remainingName = (Name)((Name)var1.clone());
      } else {
         this.remainingName = null;
      }

   }

   public void appendRemainingName(Name var1) {
      if (var1 != null) {
         if (this.remainingName != null) {
            try {
               this.remainingName.addAll(var1);
            } catch (InvalidNameException var3) {
            }
         } else {
            this.remainingName = (Name)((Name)var1.clone());
         }
      }

   }

   public void appendRemainingComponent(String var1) {
      if (var1 != null) {
         CompositeName var2 = new CompositeName();

         try {
            var2.add(var1);
         } catch (InvalidNameException var4) {
         }

         this.appendRemainingName(var2);
      }

   }

   public void setResolvedObj(Object var1) {
      this.resolvedObj = var1;
   }
}
