package javax.naming.ldap;

import com.sun.naming.internal.VersionHelper;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;

public class StartTlsRequest implements ExtendedRequest {
   public static final String OID = "1.3.6.1.4.1.1466.20037";
   private static final long serialVersionUID = 4441679576360753397L;

   public String getID() {
      return "1.3.6.1.4.1.1466.20037";
   }

   public byte[] getEncodedValue() {
      return null;
   }

   public ExtendedResponse createExtendedResponse(String var1, byte[] var2, int var3, int var4) throws NamingException {
      if (var1 != null && !var1.equals("1.3.6.1.4.1.1466.20037")) {
         throw new ConfigurationException("Start TLS received the following response instead of 1.3.6.1.4.1.1466.20037: " + var1);
      } else {
         StartTlsResponse var5 = null;
         ServiceLoader var6 = ServiceLoader.load(StartTlsResponse.class, this.getContextClassLoader());

         for(Iterator var7 = var6.iterator(); var5 == null && privilegedHasNext(var7); var5 = (StartTlsResponse)var7.next()) {
         }

         if (var5 != null) {
            return var5;
         } else {
            try {
               VersionHelper var8 = VersionHelper.getVersionHelper();
               Class var9 = var8.loadClass("com.sun.jndi.ldap.ext.StartTlsResponseImpl");
               var5 = (StartTlsResponse)var9.newInstance();
               return var5;
            } catch (IllegalAccessException var10) {
               throw this.wrapException(var10);
            } catch (InstantiationException var11) {
               throw this.wrapException(var11);
            } catch (ClassNotFoundException var12) {
               throw this.wrapException(var12);
            }
         }
      }
   }

   private ConfigurationException wrapException(Exception var1) {
      ConfigurationException var2 = new ConfigurationException("Cannot load implementation of javax.naming.ldap.StartTlsResponse");
      var2.setRootCause(var1);
      return var2;
   }

   private final ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   private static final boolean privilegedHasNext(final Iterator<StartTlsResponse> var0) {
      Boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return var0.hasNext();
         }
      });
      return var1;
   }
}
