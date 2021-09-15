package sun.security.jgss;

import com.sun.security.auth.callback.TextCallbackHandler;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.spnego.NegotiateCallbackHandler;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.SpNegoCredElement;

public class GSSUtil {
   public static final Oid GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
   public static final Oid GSS_KRB5_MECH_OID2 = createOid("1.3.5.1.5.2");
   public static final Oid GSS_KRB5_MECH_OID_MS = createOid("1.2.840.48018.1.2.2");
   public static final Oid GSS_SPNEGO_MECH_OID = createOid("1.3.6.1.5.5.2");
   public static final Oid NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
   private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
   static final boolean DEBUG = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.jgss.debug")));

   static void debug(String var0) {
      if (DEBUG) {
         assert var0 != null;

         System.out.println(var0);
      }

   }

   public static Oid createOid(String var0) {
      try {
         return new Oid(var0);
      } catch (GSSException var2) {
         debug("Ignored invalid OID: " + var0);
         return null;
      }
   }

   public static boolean isSpNegoMech(Oid var0) {
      return GSS_SPNEGO_MECH_OID.equals(var0);
   }

   public static boolean isKerberosMech(Oid var0) {
      return GSS_KRB5_MECH_OID.equals(var0) || GSS_KRB5_MECH_OID2.equals(var0) || GSS_KRB5_MECH_OID_MS.equals(var0);
   }

   public static String getMechStr(Oid var0) {
      if (isSpNegoMech(var0)) {
         return "SPNEGO";
      } else {
         return isKerberosMech(var0) ? "Kerberos V5" : var0.toString();
      }
   }

   public static Subject getSubject(GSSName var0, GSSCredential var1) {
      HashSet var2 = null;
      HashSet var3 = new HashSet();
      Set var4 = null;
      HashSet var5 = new HashSet();
      if (var0 instanceof GSSNameImpl) {
         try {
            GSSNameSpi var6 = ((GSSNameImpl)var0).getElement(GSS_KRB5_MECH_OID);
            String var7 = var6.toString();
            if (var6 instanceof Krb5NameElement) {
               var7 = ((Krb5NameElement)var6).getKrb5PrincipalName().getName();
            }

            KerberosPrincipal var8 = new KerberosPrincipal(var7);
            var5.add(var8);
         } catch (GSSException var9) {
            debug("Skipped name " + var0 + " due to " + var9);
         }
      }

      if (var1 instanceof GSSCredentialImpl) {
         var4 = ((GSSCredentialImpl)var1).getElements();
         var2 = new HashSet(var4.size());
         populateCredentials(var2, var4);
      } else {
         var2 = new HashSet();
      }

      debug("Created Subject with the following");
      debug("principals=" + var5);
      debug("public creds=" + var3);
      debug("private creds=" + var2);
      return new Subject(false, var5, var3, var2);
   }

   private static void populateCredentials(Set<Object> var0, Set<?> var1) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var2 = var3.next();
         if (var2 instanceof SpNegoCredElement) {
            var2 = ((SpNegoCredElement)var2).getInternalCred();
         }

         if (var2 instanceof KerberosTicket) {
            if (!var2.getClass().getName().equals("javax.security.auth.kerberos.KerberosTicket")) {
               KerberosTicket var4 = (KerberosTicket)var2;
               var2 = new KerberosTicket(var4.getEncoded(), var4.getClient(), var4.getServer(), var4.getSessionKey().getEncoded(), var4.getSessionKeyType(), var4.getFlags(), var4.getAuthTime(), var4.getStartTime(), var4.getEndTime(), var4.getRenewTill(), var4.getClientAddresses());
            }

            var0.add(var2);
         } else if (var2 instanceof KerberosKey) {
            if (!var2.getClass().getName().equals("javax.security.auth.kerberos.KerberosKey")) {
               KerberosKey var5 = (KerberosKey)var2;
               var2 = new KerberosKey(var5.getPrincipal(), var5.getEncoded(), var5.getKeyType(), var5.getVersionNumber());
            }

            var0.add(var2);
         } else {
            debug("Skipped cred element: " + var2);
         }
      }

   }

   public static Subject login(GSSCaller var0, Oid var1) throws LoginException {
      Object var2 = null;
      if (var0 instanceof HttpCaller) {
         var2 = new NegotiateCallbackHandler(((HttpCaller)var0).info());
      } else {
         String var3 = Security.getProperty("auth.login.defaultCallbackHandler");
         if (var3 != null && var3.length() != 0) {
            var2 = null;
         } else {
            var2 = new TextCallbackHandler();
         }
      }

      LoginContext var4 = new LoginContext("", (Subject)null, (CallbackHandler)var2, new LoginConfigImpl(var0, var1));
      var4.login();
      return var4.getSubject();
   }

   public static boolean useSubjectCredsOnly(GSSCaller var0) {
      String var1 = GetPropertyAction.privilegedGetProperty("javax.security.auth.useSubjectCredsOnly");
      if (var0 instanceof HttpCaller) {
         return "true".equalsIgnoreCase(var1);
      } else {
         return !"false".equalsIgnoreCase(var1);
      }
   }

   public static boolean useMSInterop() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.spnego.msinterop", "true")));
      return !var0.equalsIgnoreCase("false");
   }

   public static <T extends GSSCredentialSpi> Vector<T> searchSubject(final GSSNameSpi var0, final Oid var1, final boolean var2, final Class<? extends T> var3) {
      debug("Search Subject for " + getMechStr(var1) + (var2 ? " INIT" : " ACCEPT") + " cred (" + (var0 == null ? "<<DEF>>" : var0.toString()) + ", " + var3.getName() + ")");
      final AccessControlContext var4 = AccessController.getContext();

      try {
         Vector var5 = (Vector)AccessController.doPrivileged(new PrivilegedExceptionAction<Vector<T>>() {
            public Vector<T> run() throws Exception {
               Subject var1x = Subject.getSubject(var4);
               Vector var2x = null;
               if (var1x != null) {
                  var2x = new Vector();
                  Iterator var3x = var1x.getPrivateCredentials(GSSCredentialImpl.class).iterator();

                  while(var3x.hasNext()) {
                     GSSCredentialImpl var4x = (GSSCredentialImpl)var3x.next();
                     GSSUtil.debug("...Found cred" + var4x);

                     try {
                        GSSCredentialSpi var5 = var4x.getElement(var1, var2);
                        GSSUtil.debug("......Found element: " + var5);
                        if (!var5.getClass().equals(var3) || var0 != null && !var0.equals((Object)var5.getName())) {
                           GSSUtil.debug("......Discard element");
                        } else {
                           var2x.add(var3.cast(var5));
                        }
                     } catch (GSSException var6) {
                        GSSUtil.debug("...Discard cred (" + var6 + ")");
                     }
                  }
               } else {
                  GSSUtil.debug("No Subject");
               }

               return var2x;
            }
         });
         return var5;
      } catch (PrivilegedActionException var6) {
         debug("Unexpected exception when searching Subject:");
         if (DEBUG) {
            var6.printStackTrace();
         }

         return null;
      }
   }
}
