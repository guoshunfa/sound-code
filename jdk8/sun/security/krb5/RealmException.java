package sun.security.krb5;

public class RealmException extends KrbException {
   private static final long serialVersionUID = -9100385213693792864L;

   public RealmException(int var1) {
      super(var1);
   }

   public RealmException(String var1) {
      super(var1);
   }

   public RealmException(int var1, String var2) {
      super(var1, var2);
   }

   public RealmException(Throwable var1) {
      super(var1);
   }
}
