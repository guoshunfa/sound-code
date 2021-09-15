package sun.security.jgss.wrapper;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import org.ietf.jgss.Oid;
import sun.security.action.PutAllAction;

public final class SunNativeProvider extends Provider {
   private static final long serialVersionUID = -238911724858694204L;
   private static final String NAME = "SunNativeGSS";
   private static final String INFO = "Sun Native GSS provider";
   private static final String MF_CLASS = "sun.security.jgss.wrapper.NativeGSSFactory";
   private static final String LIB_PROP = "sun.security.jgss.lib";
   private static final String DEBUG_PROP = "sun.security.nativegss.debug";
   private static HashMap<String, String> MECH_MAP = (HashMap)AccessController.doPrivileged(new PrivilegedAction<HashMap<String, String>>() {
      public HashMap<String, String> run() {
         SunNativeProvider.DEBUG = Boolean.parseBoolean(System.getProperty("sun.security.nativegss.debug"));

         try {
            System.loadLibrary("j2gss");
         } catch (Error var10) {
            SunNativeProvider.debug("No j2gss library found!");
            if (SunNativeProvider.DEBUG) {
               var10.printStackTrace();
            }

            return null;
         }

         String[] var1 = new String[0];
         String var2 = System.getProperty("sun.security.jgss.lib");
         if (var2 != null && !var2.trim().equals("")) {
            var1 = new String[]{var2};
         } else {
            String var3 = System.getProperty("os.name");
            if (var3.startsWith("SunOS")) {
               var1 = new String[]{"libgss.so"};
            } else if (var3.startsWith("Linux")) {
               var1 = new String[]{"libgssapi.so", "libgssapi_krb5.so", "libgssapi_krb5.so.2"};
            } else if (var3.contains("OS X")) {
               var1 = new String[]{"libgssapi_krb5.dylib", "/usr/lib/sasl2/libgssapiv2.2.so"};
            }
         }

         String[] var11 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var11[var5];
            if (GSSLibStub.init(var6, SunNativeProvider.DEBUG)) {
               SunNativeProvider.debug("Loaded GSS library: " + var6);
               Oid[] var7 = GSSLibStub.indicateMechs();
               HashMap var8 = new HashMap();

               for(int var9 = 0; var9 < var7.length; ++var9) {
                  SunNativeProvider.debug("Native MF for " + var7[var9]);
                  var8.put("GssApiMechanism." + var7[var9], "sun.security.jgss.wrapper.NativeGSSFactory");
               }

               return var8;
            }
         }

         return null;
      }
   });
   static final Provider INSTANCE = new SunNativeProvider();
   static boolean DEBUG;

   static void debug(String var0) {
      if (DEBUG) {
         if (var0 == null) {
            throw new NullPointerException();
         }

         System.out.println("SunNativeGSS: " + var0);
      }

   }

   public SunNativeProvider() {
      super("SunNativeGSS", 1.8D, "Sun Native GSS provider");
      if (MECH_MAP != null) {
         AccessController.doPrivileged((PrivilegedAction)(new PutAllAction(this, MECH_MAP)));
      }

   }
}
