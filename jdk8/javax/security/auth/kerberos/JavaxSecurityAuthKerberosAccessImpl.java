package javax.security.auth.kerberos;

import sun.security.krb5.JavaxSecurityAuthKerberosAccess;

class JavaxSecurityAuthKerberosAccessImpl implements JavaxSecurityAuthKerberosAccess {
   public sun.security.krb5.internal.ktab.KeyTab keyTabTakeSnapshot(KeyTab var1) {
      return var1.takeSnapshot();
   }
}
