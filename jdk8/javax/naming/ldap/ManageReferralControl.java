package javax.naming.ldap;

public final class ManageReferralControl extends BasicControl {
   public static final String OID = "2.16.840.1.113730.3.4.2";
   private static final long serialVersionUID = 3017756160149982566L;

   public ManageReferralControl() {
      super("2.16.840.1.113730.3.4.2", true, (byte[])null);
   }

   public ManageReferralControl(boolean var1) {
      super("2.16.840.1.113730.3.4.2", var1, (byte[])null);
   }
}
