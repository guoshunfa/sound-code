package java.security;

/** @deprecated */
@Deprecated
public abstract class Signer extends Identity {
   private static final long serialVersionUID = -1763464102261361480L;
   private PrivateKey privateKey;

   protected Signer() {
   }

   public Signer(String var1) {
      super(var1);
   }

   public Signer(String var1, IdentityScope var2) throws KeyManagementException {
      super(var1, var2);
   }

   public PrivateKey getPrivateKey() {
      check("getSignerPrivateKey");
      return this.privateKey;
   }

   public final void setKeyPair(KeyPair var1) throws InvalidParameterException, KeyException {
      check("setSignerKeyPair");
      final PublicKey var2 = var1.getPublic();
      PrivateKey var3 = var1.getPrivate();
      if (var2 != null && var3 != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws KeyManagementException {
                  Signer.this.setPublicKey(var2);
                  return null;
               }
            });
         } catch (PrivilegedActionException var5) {
            throw (KeyManagementException)var5.getException();
         }

         this.privateKey = var3;
      } else {
         throw new InvalidParameterException();
      }
   }

   String printKeys() {
      String var1 = "";
      PublicKey var2 = this.getPublicKey();
      if (var2 != null && this.privateKey != null) {
         var1 = "\tpublic and private keys initialized";
      } else {
         var1 = "\tno keys";
      }

      return var1;
   }

   public String toString() {
      return "[Signer]" + super.toString();
   }

   private static void check(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSecurityAccess(var0);
      }

   }
}
