package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ReferralException;

public abstract class LdapReferralException extends ReferralException {
   private static final long serialVersionUID = -1668992791764950804L;

   protected LdapReferralException(String var1) {
      super(var1);
   }

   protected LdapReferralException() {
   }

   public abstract Context getReferralContext() throws NamingException;

   public abstract Context getReferralContext(Hashtable<?, ?> var1) throws NamingException;

   public abstract Context getReferralContext(Hashtable<?, ?> var1, Control[] var2) throws NamingException;
}
