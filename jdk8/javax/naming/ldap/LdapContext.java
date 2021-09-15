package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface LdapContext extends DirContext {
   String CONTROL_FACTORIES = "java.naming.factory.control";

   ExtendedResponse extendedOperation(ExtendedRequest var1) throws NamingException;

   LdapContext newInstance(Control[] var1) throws NamingException;

   void reconnect(Control[] var1) throws NamingException;

   Control[] getConnectControls() throws NamingException;

   void setRequestControls(Control[] var1) throws NamingException;

   Control[] getRequestControls() throws NamingException;

   Control[] getResponseControls() throws NamingException;
}
