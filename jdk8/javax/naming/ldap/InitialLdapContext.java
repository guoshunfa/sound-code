package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;
import javax.naming.directory.InitialDirContext;

public class InitialLdapContext extends InitialDirContext implements LdapContext {
   private static final String BIND_CONTROLS_PROPERTY = "java.naming.ldap.control.connect";

   public InitialLdapContext() throws NamingException {
      super((Hashtable)null);
   }

   public InitialLdapContext(Hashtable<?, ?> var1, Control[] var2) throws NamingException {
      super(true);
      Hashtable var3 = var1 == null ? new Hashtable(11) : (Hashtable)var1.clone();
      if (var2 != null) {
         Control[] var4 = new Control[var2.length];
         System.arraycopy(var2, 0, var4, 0, var2.length);
         var3.put("java.naming.ldap.control.connect", var4);
      }

      var3.put("java.naming.ldap.version", "3");
      this.init(var3);
   }

   private LdapContext getDefaultLdapInitCtx() throws NamingException {
      Context var1 = this.getDefaultInitCtx();
      if (!(var1 instanceof LdapContext)) {
         if (var1 == null) {
            throw new NoInitialContextException();
         } else {
            throw new NotContextException("Not an instance of LdapContext");
         }
      } else {
         return (LdapContext)var1;
      }
   }

   public ExtendedResponse extendedOperation(ExtendedRequest var1) throws NamingException {
      return this.getDefaultLdapInitCtx().extendedOperation(var1);
   }

   public LdapContext newInstance(Control[] var1) throws NamingException {
      return this.getDefaultLdapInitCtx().newInstance(var1);
   }

   public void reconnect(Control[] var1) throws NamingException {
      this.getDefaultLdapInitCtx().reconnect(var1);
   }

   public Control[] getConnectControls() throws NamingException {
      return this.getDefaultLdapInitCtx().getConnectControls();
   }

   public void setRequestControls(Control[] var1) throws NamingException {
      this.getDefaultLdapInitCtx().setRequestControls(var1);
   }

   public Control[] getRequestControls() throws NamingException {
      return this.getDefaultLdapInitCtx().getRequestControls();
   }

   public Control[] getResponseControls() throws NamingException {
      return this.getDefaultLdapInitCtx().getResponseControls();
   }
}
