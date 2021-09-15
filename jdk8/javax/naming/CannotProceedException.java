package javax.naming;

import java.util.Hashtable;

public class CannotProceedException extends NamingException {
   protected Name remainingNewName = null;
   protected Hashtable<?, ?> environment = null;
   protected Name altName = null;
   protected Context altNameCtx = null;
   private static final long serialVersionUID = 1219724816191576813L;

   public CannotProceedException(String var1) {
      super(var1);
   }

   public CannotProceedException() {
   }

   public Hashtable<?, ?> getEnvironment() {
      return this.environment;
   }

   public void setEnvironment(Hashtable<?, ?> var1) {
      this.environment = var1;
   }

   public Name getRemainingNewName() {
      return this.remainingNewName;
   }

   public void setRemainingNewName(Name var1) {
      if (var1 != null) {
         this.remainingNewName = (Name)((Name)var1.clone());
      } else {
         this.remainingNewName = null;
      }

   }

   public Name getAltName() {
      return this.altName;
   }

   public void setAltName(Name var1) {
      this.altName = var1;
   }

   public Context getAltNameCtx() {
      return this.altNameCtx;
   }

   public void setAltNameCtx(Context var1) {
      this.altNameCtx = var1;
   }
}
