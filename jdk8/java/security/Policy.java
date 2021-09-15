package java.security;

import java.util.Enumeration;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import sun.security.jca.GetInstance;
import sun.security.provider.PolicyFile;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public abstract class Policy {
   public static final PermissionCollection UNSUPPORTED_EMPTY_COLLECTION = new Policy.UnsupportedEmptyCollection();
   private static AtomicReference<Policy.PolicyInfo> policy = new AtomicReference(new Policy.PolicyInfo((Policy)null, false));
   private static final Debug debug = Debug.getInstance("policy");
   private WeakHashMap<ProtectionDomain.Key, PermissionCollection> pdMapping;

   static boolean isSet() {
      Policy.PolicyInfo var0 = (Policy.PolicyInfo)policy.get();
      return var0.policy != null && var0.initialized;
   }

   private static void checkPermission(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new SecurityPermission("createPolicy." + var0));
      }

   }

   public static Policy getPolicy() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
      }

      return getPolicyNoCheck();
   }

   static Policy getPolicyNoCheck() {
      Policy.PolicyInfo var0 = (Policy.PolicyInfo)policy.get();
      if (var0.initialized && var0.policy != null) {
         return var0.policy;
      } else {
         Class var1 = Policy.class;
         synchronized(Policy.class) {
            Policy.PolicyInfo var2 = (Policy.PolicyInfo)policy.get();
            if (var2.policy == null) {
               final String var3 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                  public String run() {
                     return Security.getProperty("policy.provider");
                  }
               });
               if (var3 == null) {
                  var3 = "sun.security.provider.PolicyFile";
               }

               try {
                  var2 = new Policy.PolicyInfo((Policy)Class.forName(var3).newInstance(), true);
               } catch (Exception var9) {
                  PolicyFile var5 = new PolicyFile();
                  var2 = new Policy.PolicyInfo(var5, false);
                  policy.set(var2);
                  Policy var7 = (Policy)AccessController.doPrivileged(new PrivilegedAction<Policy>() {
                     public Policy run() {
                        try {
                           ClassLoader var1 = ClassLoader.getSystemClassLoader();

                           ClassLoader var2;
                           for(var2 = null; var1 != null; var1 = var1.getParent()) {
                              var2 = var1;
                           }

                           return var2 != null ? (Policy)Class.forName(var3, true, var2).newInstance() : null;
                        } catch (Exception var3x) {
                           if (Policy.debug != null) {
                              Policy.debug.println("policy provider " + var3 + " not available");
                              var3x.printStackTrace();
                           }

                           return null;
                        }
                     }
                  });
                  if (var7 != null) {
                     var2 = new Policy.PolicyInfo(var7, true);
                  } else {
                     if (debug != null) {
                        debug.println("using sun.security.provider.PolicyFile");
                     }

                     var2 = new Policy.PolicyInfo(var5, true);
                  }
               }

               policy.set(var2);
            }

            return var2.policy;
         }
      }
   }

   public static void setPolicy(Policy var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new SecurityPermission("setPolicy"));
      }

      if (var0 != null) {
         initPolicy(var0);
      }

      Class var2 = Policy.class;
      synchronized(Policy.class) {
         policy.set(new Policy.PolicyInfo(var0, var0 != null));
      }
   }

   private static void initPolicy(final Policy var0) {
      ProtectionDomain var1 = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
         public ProtectionDomain run() {
            return var0.getClass().getProtectionDomain();
         }
      });
      Object var2 = null;
      synchronized(var0) {
         if (var0.pdMapping == null) {
            var0.pdMapping = new WeakHashMap();
         }
      }

      if (var1.getCodeSource() != null) {
         Policy var3 = ((Policy.PolicyInfo)policy.get()).policy;
         if (var3 != null) {
            var2 = var3.getPermissions(var1);
         }

         if (var2 == null) {
            var2 = new Permissions();
            ((PermissionCollection)var2).add(SecurityConstants.ALL_PERMISSION);
         }

         synchronized(var0.pdMapping) {
            var0.pdMapping.put(var1.key, var2);
         }
      }

   }

   public static Policy getInstance(String var0, Policy.Parameters var1) throws NoSuchAlgorithmException {
      checkPermission(var0);

      try {
         GetInstance.Instance var2 = GetInstance.getInstance("Policy", PolicySpi.class, var0, (Object)var1);
         return new Policy.PolicyDelegate((PolicySpi)var2.impl, var2.provider, var0, var1);
      } catch (NoSuchAlgorithmException var3) {
         return handleException(var3);
      }
   }

   public static Policy getInstance(String var0, Policy.Parameters var1, String var2) throws NoSuchProviderException, NoSuchAlgorithmException {
      if (var2 != null && var2.length() != 0) {
         checkPermission(var0);

         try {
            GetInstance.Instance var3 = GetInstance.getInstance("Policy", PolicySpi.class, var0, var1, (String)var2);
            return new Policy.PolicyDelegate((PolicySpi)var3.impl, var3.provider, var0, var1);
         } catch (NoSuchAlgorithmException var4) {
            return handleException(var4);
         }
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static Policy getInstance(String var0, Policy.Parameters var1, Provider var2) throws NoSuchAlgorithmException {
      if (var2 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         checkPermission(var0);

         try {
            GetInstance.Instance var3 = GetInstance.getInstance("Policy", PolicySpi.class, var0, var1, (Provider)var2);
            return new Policy.PolicyDelegate((PolicySpi)var3.impl, var3.provider, var0, var1);
         } catch (NoSuchAlgorithmException var4) {
            return handleException(var4);
         }
      }
   }

   private static Policy handleException(NoSuchAlgorithmException var0) throws NoSuchAlgorithmException {
      Throwable var1 = var0.getCause();
      if (var1 instanceof IllegalArgumentException) {
         throw (IllegalArgumentException)var1;
      } else {
         throw var0;
      }
   }

   public Provider getProvider() {
      return null;
   }

   public String getType() {
      return null;
   }

   public Policy.Parameters getParameters() {
      return null;
   }

   public PermissionCollection getPermissions(CodeSource var1) {
      return UNSUPPORTED_EMPTY_COLLECTION;
   }

   public PermissionCollection getPermissions(ProtectionDomain var1) {
      PermissionCollection var2 = null;
      if (var1 == null) {
         return new Permissions();
      } else {
         if (this.pdMapping == null) {
            initPolicy(this);
         }

         synchronized(this.pdMapping) {
            var2 = (PermissionCollection)this.pdMapping.get(var1.key);
         }

         if (var2 != null) {
            Permissions var3 = new Permissions();
            synchronized(var2) {
               Enumeration var5 = var2.elements();

               while(var5.hasMoreElements()) {
                  var3.add((Permission)var5.nextElement());
               }

               return var3;
            }
         } else {
            Object var9 = this.getPermissions(var1.getCodeSource());
            if (var9 == null || var9 == UNSUPPORTED_EMPTY_COLLECTION) {
               var9 = new Permissions();
            }

            this.addStaticPerms((PermissionCollection)var9, var1.getPermissions());
            return (PermissionCollection)var9;
         }
      }
   }

   private void addStaticPerms(PermissionCollection var1, PermissionCollection var2) {
      if (var2 != null) {
         synchronized(var2) {
            Enumeration var4 = var2.elements();

            while(var4.hasMoreElements()) {
               var1.add((Permission)var4.nextElement());
            }
         }
      }

   }

   public boolean implies(ProtectionDomain var1, Permission var2) {
      if (this.pdMapping == null) {
         initPolicy(this);
      }

      PermissionCollection var3;
      synchronized(this.pdMapping) {
         var3 = (PermissionCollection)this.pdMapping.get(var1.key);
      }

      if (var3 != null) {
         return var3.implies(var2);
      } else {
         var3 = this.getPermissions(var1);
         if (var3 == null) {
            return false;
         } else {
            synchronized(this.pdMapping) {
               this.pdMapping.put(var1.key, var3);
            }

            return var3.implies(var2);
         }
      }
   }

   public void refresh() {
   }

   private static class UnsupportedEmptyCollection extends PermissionCollection {
      private static final long serialVersionUID = -8492269157353014774L;
      private Permissions perms = new Permissions();

      public UnsupportedEmptyCollection() {
         this.perms.setReadOnly();
      }

      public void add(Permission var1) {
         this.perms.add(var1);
      }

      public boolean implies(Permission var1) {
         return this.perms.implies(var1);
      }

      public Enumeration<Permission> elements() {
         return this.perms.elements();
      }
   }

   public interface Parameters {
   }

   private static class PolicyDelegate extends Policy {
      private PolicySpi spi;
      private Provider p;
      private String type;
      private Policy.Parameters params;

      private PolicyDelegate(PolicySpi var1, Provider var2, String var3, Policy.Parameters var4) {
         this.spi = var1;
         this.p = var2;
         this.type = var3;
         this.params = var4;
      }

      public String getType() {
         return this.type;
      }

      public Policy.Parameters getParameters() {
         return this.params;
      }

      public Provider getProvider() {
         return this.p;
      }

      public PermissionCollection getPermissions(CodeSource var1) {
         return this.spi.engineGetPermissions(var1);
      }

      public PermissionCollection getPermissions(ProtectionDomain var1) {
         return this.spi.engineGetPermissions(var1);
      }

      public boolean implies(ProtectionDomain var1, Permission var2) {
         return this.spi.engineImplies(var1, var2);
      }

      public void refresh() {
         this.spi.engineRefresh();
      }

      // $FF: synthetic method
      PolicyDelegate(PolicySpi var1, Provider var2, String var3, Policy.Parameters var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   private static class PolicyInfo {
      final Policy policy;
      final boolean initialized;

      PolicyInfo(Policy var1, boolean var2) {
         this.policy = var1;
         this.initialized = var2;
      }
   }
}
