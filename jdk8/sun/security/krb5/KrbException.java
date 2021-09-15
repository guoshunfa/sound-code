package sun.security.krb5;

import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;

public class KrbException extends Exception {
   private static final long serialVersionUID = -4993302876451928596L;
   private int returnCode;
   private KRBError error;

   public KrbException(String var1) {
      super(var1);
   }

   public KrbException(Throwable var1) {
      super(var1);
   }

   public KrbException(int var1) {
      this.returnCode = var1;
   }

   public KrbException(int var1, String var2) {
      this(var2);
      this.returnCode = var1;
   }

   public KrbException(KRBError var1) {
      this.returnCode = var1.getErrorCode();
      this.error = var1;
   }

   public KrbException(KRBError var1, String var2) {
      this(var2);
      this.returnCode = var1.getErrorCode();
      this.error = var1;
   }

   public KRBError getError() {
      return this.error;
   }

   public int returnCode() {
      return this.returnCode;
   }

   public String returnCodeSymbol() {
      return returnCodeSymbol(this.returnCode);
   }

   public static String returnCodeSymbol(int var0) {
      return "not yet implemented";
   }

   public String returnCodeMessage() {
      return Krb5.getErrorMessage(this.returnCode);
   }

   public static String errorMessage(int var0) {
      return Krb5.getErrorMessage(var0);
   }

   public String krbErrorMessage() {
      StringBuffer var1 = new StringBuffer("krb_error " + this.returnCode);
      String var2 = this.getMessage();
      if (var2 != null) {
         var1.append(" ");
         var1.append(var2);
      }

      return var1.toString();
   }

   public String getMessage() {
      StringBuffer var1 = new StringBuffer();
      int var2 = this.returnCode();
      if (var2 != 0) {
         var1.append(this.returnCodeMessage());
         var1.append(" (").append(this.returnCode()).append(')');
      }

      String var3 = super.getMessage();
      if (var3 != null && var3.length() != 0) {
         if (var2 != 0) {
            var1.append(" - ");
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   public String toString() {
      return "KrbException: " + this.getMessage();
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 37 * var1 + this.returnCode;
      if (this.error != null) {
         var2 = 37 * var2 + this.error.hashCode();
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KrbException)) {
         return false;
      } else {
         KrbException var2 = (KrbException)var1;
         if (this.returnCode != var2.returnCode) {
            return false;
         } else {
            return this.error == null ? var2.error == null : this.error.equals(var2.error);
         }
      }
   }
}
