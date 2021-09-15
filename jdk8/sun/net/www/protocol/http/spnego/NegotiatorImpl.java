package sun.net.www.protocol.http.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.net.www.protocol.http.Negotiator;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.HttpCaller;

public class NegotiatorImpl extends Negotiator {
   private static final boolean DEBUG = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.krb5.debug")));
   private GSSContext context;
   private byte[] oneToken;

   private void init(HttpCallerInfo var1) throws GSSException {
      Oid var2;
      if (var1.scheme.equalsIgnoreCase("Kerberos")) {
         var2 = GSSUtil.GSS_KRB5_MECH_OID;
      } else {
         String var3 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("http.auth.preference", "spnego");
            }
         });
         if (var3.equalsIgnoreCase("kerberos")) {
            var2 = GSSUtil.GSS_KRB5_MECH_OID;
         } else {
            var2 = GSSUtil.GSS_SPNEGO_MECH_OID;
         }
      }

      GSSManagerImpl var6 = new GSSManagerImpl(new HttpCaller(var1));
      String var4 = "HTTP@" + var1.host.toLowerCase();
      GSSName var5 = var6.createName(var4, GSSName.NT_HOSTBASED_SERVICE);
      this.context = var6.createContext(var5, var2, (GSSCredential)null, 0);
      if (this.context instanceof ExtendedGSSContext) {
         ((ExtendedGSSContext)this.context).requestDelegPolicy(true);
      }

      this.oneToken = this.context.initSecContext(new byte[0], 0, 0);
   }

   public NegotiatorImpl(HttpCallerInfo var1) throws IOException {
      try {
         this.init(var1);
      } catch (GSSException var4) {
         if (DEBUG) {
            System.out.println("Negotiate support not initiated, will fallback to other scheme if allowed. Reason:");
            var4.printStackTrace();
         }

         IOException var3 = new IOException("Negotiate support not initiated");
         var3.initCause(var4);
         throw var3;
      }
   }

   public byte[] firstToken() {
      return this.oneToken;
   }

   public byte[] nextToken(byte[] var1) throws IOException {
      try {
         return this.context.initSecContext(var1, 0, var1.length);
      } catch (GSSException var4) {
         if (DEBUG) {
            System.out.println("Negotiate support cannot continue. Reason:");
            var4.printStackTrace();
         }

         IOException var3 = new IOException("Negotiate support cannot continue");
         var3.initCause(var4);
         throw var3;
      }
   }
}
