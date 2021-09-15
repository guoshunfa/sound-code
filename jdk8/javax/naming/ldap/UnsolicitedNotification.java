package javax.naming.ldap;

import javax.naming.NamingException;

public interface UnsolicitedNotification extends ExtendedResponse, HasControls {
   String[] getReferrals();

   NamingException getException();
}
