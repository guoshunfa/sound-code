package javax.security.sasl;

import java.security.Provider;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.CallbackHandler;

public class Sasl {
   public static final String QOP = "javax.security.sasl.qop";
   public static final String STRENGTH = "javax.security.sasl.strength";
   public static final String SERVER_AUTH = "javax.security.sasl.server.authentication";
   public static final String BOUND_SERVER_NAME = "javax.security.sasl.bound.server.name";
   public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";
   public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
   public static final String REUSE = "javax.security.sasl.reuse";
   public static final String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
   public static final String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
   public static final String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
   public static final String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
   public static final String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
   public static final String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
   public static final String CREDENTIALS = "javax.security.sasl.credentials";

   private Sasl() {
   }

   public static SaslClient createSaslClient(String[] var0, String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException {
      SaslClient var6 = null;

      for(int var10 = 0; var10 < var0.length; ++var10) {
         String var9;
         if ((var9 = var0[var10]) == null) {
            throw new NullPointerException("Mechanism name cannot be null");
         }

         if (var9.length() != 0) {
            String var11 = "SaslClientFactory." + var9;
            Provider[] var12 = Security.getProviders(var11);

            for(int var13 = 0; var12 != null && var13 < var12.length; ++var13) {
               String var8 = var12[var13].getProperty(var11);
               if (var8 != null) {
                  SaslClientFactory var7 = (SaslClientFactory)loadFactory(var12[var13], var8);
                  if (var7 != null) {
                     var6 = var7.createSaslClient(new String[]{var0[var10]}, var1, var2, var3, var4, var5);
                     if (var6 != null) {
                        return var6;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private static Object loadFactory(Provider var0, String var1) throws SaslException {
      try {
         ClassLoader var2 = var0.getClass().getClassLoader();
         Class var3 = Class.forName(var1, true, var2);
         return var3.newInstance();
      } catch (ClassNotFoundException var4) {
         throw new SaslException("Cannot load class " + var1, var4);
      } catch (InstantiationException var5) {
         throw new SaslException("Cannot instantiate class " + var1, var5);
      } catch (IllegalAccessException var6) {
         throw new SaslException("Cannot access class " + var1, var6);
      } catch (SecurityException var7) {
         throw new SaslException("Cannot access class " + var1, var7);
      }
   }

   public static SaslServer createSaslServer(String var0, String var1, String var2, Map<String, ?> var3, CallbackHandler var4) throws SaslException {
      SaslServer var5 = null;
      if (var0 == null) {
         throw new NullPointerException("Mechanism name cannot be null");
      } else if (var0.length() == 0) {
         return null;
      } else {
         String var8 = "SaslServerFactory." + var0;
         Provider[] var9 = Security.getProviders(var8);

         for(int var10 = 0; var9 != null && var10 < var9.length; ++var10) {
            String var7 = var9[var10].getProperty(var8);
            if (var7 == null) {
               throw new SaslException("Provider does not support " + var8);
            }

            SaslServerFactory var6 = (SaslServerFactory)loadFactory(var9[var10], var7);
            if (var6 != null) {
               var5 = var6.createSaslServer(var0, var1, var2, var3, var4);
               if (var5 != null) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   public static Enumeration<SaslClientFactory> getSaslClientFactories() {
      Set var0 = getFactories("SaslClientFactory");
      final Iterator var1 = var0.iterator();
      return new Enumeration<SaslClientFactory>() {
         public boolean hasMoreElements() {
            return var1.hasNext();
         }

         public SaslClientFactory nextElement() {
            return (SaslClientFactory)var1.next();
         }
      };
   }

   public static Enumeration<SaslServerFactory> getSaslServerFactories() {
      Set var0 = getFactories("SaslServerFactory");
      final Iterator var1 = var0.iterator();
      return new Enumeration<SaslServerFactory>() {
         public boolean hasMoreElements() {
            return var1.hasNext();
         }

         public SaslServerFactory nextElement() {
            return (SaslServerFactory)var1.next();
         }
      };
   }

   private static Set<Object> getFactories(String var0) {
      HashSet var1 = new HashSet();
      if (var0 != null && var0.length() != 0 && !var0.endsWith(".")) {
         Provider[] var2 = Security.getProviders();
         HashSet var3 = new HashSet();

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var3.clear();
            Enumeration var6 = var2[var5].keys();

            while(var6.hasMoreElements()) {
               String var7 = (String)var6.nextElement();
               if (var7.startsWith(var0) && var7.indexOf(" ") < 0) {
                  String var8 = var2[var5].getProperty(var7);
                  if (!var3.contains(var8)) {
                     var3.add(var8);

                     try {
                        Object var4 = loadFactory(var2[var5], var8);
                        if (var4 != null) {
                           var1.add(var4);
                        }
                     } catch (Exception var10) {
                     }
                  }
               }
            }
         }

         return Collections.unmodifiableSet(var1);
      } else {
         return var1;
      }
   }
}
