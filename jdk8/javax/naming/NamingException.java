package javax.naming;

public class NamingException extends Exception {
   protected Name resolvedName;
   protected Object resolvedObj;
   protected Name remainingName;
   protected Throwable rootException = null;
   private static final long serialVersionUID = -1299181962103167177L;

   public NamingException(String var1) {
      super(var1);
      this.resolvedName = this.remainingName = null;
      this.resolvedObj = null;
   }

   public NamingException() {
      this.resolvedName = this.remainingName = null;
      this.resolvedObj = null;
   }

   public Name getResolvedName() {
      return this.resolvedName;
   }

   public Name getRemainingName() {
      return this.remainingName;
   }

   public Object getResolvedObj() {
      return this.resolvedObj;
   }

   public String getExplanation() {
      return this.getMessage();
   }

   public void setResolvedName(Name var1) {
      if (var1 != null) {
         this.resolvedName = (Name)((Name)var1.clone());
      } else {
         this.resolvedName = null;
      }

   }

   public void setRemainingName(Name var1) {
      if (var1 != null) {
         this.remainingName = (Name)((Name)var1.clone());
      } else {
         this.remainingName = null;
      }

   }

   public void setResolvedObj(Object var1) {
      this.resolvedObj = var1;
   }

   public void appendRemainingComponent(String var1) {
      if (var1 != null) {
         try {
            if (this.remainingName == null) {
               this.remainingName = new CompositeName();
            }

            this.remainingName.add(var1);
         } catch (NamingException var3) {
            throw new IllegalArgumentException(var3.toString());
         }
      }

   }

   public void appendRemainingName(Name var1) {
      if (var1 != null) {
         if (this.remainingName != null) {
            try {
               this.remainingName.addAll(var1);
            } catch (NamingException var3) {
               throw new IllegalArgumentException(var3.toString());
            }
         } else {
            this.remainingName = (Name)((Name)var1.clone());
         }

      }
   }

   public Throwable getRootCause() {
      return this.rootException;
   }

   public void setRootCause(Throwable var1) {
      if (var1 != this) {
         this.rootException = var1;
      }

   }

   public Throwable getCause() {
      return this.getRootCause();
   }

   public Throwable initCause(Throwable var1) {
      super.initCause(var1);
      this.setRootCause(var1);
      return this;
   }

   public String toString() {
      String var1 = super.toString();
      if (this.rootException != null) {
         var1 = var1 + " [Root exception is " + this.rootException + "]";
      }

      if (this.remainingName != null) {
         var1 = var1 + "; remaining name '" + this.remainingName + "'";
      }

      return var1;
   }

   public String toString(boolean var1) {
      return var1 && this.resolvedObj != null ? this.toString() + "; resolved object " + this.resolvedObj : this.toString();
   }
}
