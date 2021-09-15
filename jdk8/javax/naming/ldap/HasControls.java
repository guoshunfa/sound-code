package javax.naming.ldap;

import javax.naming.NamingException;

public interface HasControls {
   Control[] getControls() throws NamingException;
}
