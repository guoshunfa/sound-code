package javax.security.auth;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.DomainCombiner;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;

public class SubjectDomainCombiner implements DomainCombiner {
   private Subject subject;
   private SubjectDomainCombiner.WeakKeyValueMap<ProtectionDomain, ProtectionDomain> cachedPDs = new SubjectDomainCombiner.WeakKeyValueMap();
   private Set<Principal> principalSet;
   private Principal[] principals;
   private static final Debug debug = Debug.getInstance("combiner", "\t[SubjectDomainCombiner]");
   private static final boolean useJavaxPolicy;
   private static final boolean allowCaching;
   private static final JavaSecurityProtectionDomainAccess pdAccess;

   public SubjectDomainCombiner(Subject var1) {
      this.subject = var1;
      if (var1.isReadOnly()) {
         this.principalSet = var1.getPrincipals();
         this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
      }

   }

   public Subject getSubject() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AuthPermission("getSubjectFromDomainCombiner"));
      }

      return this.subject;
   }

   public ProtectionDomain[] combine(ProtectionDomain[] var1, ProtectionDomain[] var2) {
      if (debug != null) {
         if (this.subject == null) {
            debug.println("null subject");
         } else {
            final Subject var3 = this.subject;
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  SubjectDomainCombiner.debug.println(var3.toString());
                  return null;
               }
            });
         }

         printInputDomains(var1, var2);
      }

      if (var1 != null && var1.length != 0) {
         var1 = optimize(var1);
         if (debug != null) {
            debug.println("after optimize");
            printInputDomains(var1, var2);
         }

         if (var1 == null && var2 == null) {
            return null;
         } else if (useJavaxPolicy) {
            return this.combineJavaxPolicy(var1, var2);
         } else {
            int var14 = var1 == null ? 0 : var1.length;
            int var4 = var2 == null ? 0 : var2.length;
            ProtectionDomain[] var5 = new ProtectionDomain[var14 + var4];
            boolean var6 = true;
            synchronized(this.cachedPDs) {
               if (!this.subject.isReadOnly() && !this.subject.getPrincipals().equals(this.principalSet)) {
                  Set var8 = this.subject.getPrincipals();
                  synchronized(var8) {
                     this.principalSet = new HashSet(var8);
                  }

                  this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
                  this.cachedPDs.clear();
                  if (debug != null) {
                     debug.println("Subject mutated - clearing cache");
                  }
               }

               for(int var9 = 0; var9 < var14; ++var9) {
                  ProtectionDomain var10 = var1[var9];
                  ProtectionDomain var15 = (ProtectionDomain)this.cachedPDs.getValue(var10);
                  if (var15 == null) {
                     if (pdAccess.getStaticPermissionsField(var10)) {
                        var15 = new ProtectionDomain(var10.getCodeSource(), var10.getPermissions());
                     } else {
                        var15 = new ProtectionDomain(var10.getCodeSource(), var10.getPermissions(), var10.getClassLoader(), this.principals);
                     }

                     this.cachedPDs.putValue(var10, var15);
                  } else {
                     var6 = false;
                  }

                  var5[var9] = var15;
               }
            }

            int var7;
            if (debug != null) {
               debug.println("updated current: ");

               for(var7 = 0; var7 < var14; ++var7) {
                  debug.println("\tupdated[" + var7 + "] = " + printDomain(var5[var7]));
               }
            }

            if (var4 > 0) {
               System.arraycopy(var2, 0, var5, var14, var4);
               if (!var6) {
                  var5 = optimize(var5);
               }
            }

            if (debug != null) {
               if (var5 != null && var5.length != 0) {
                  debug.println("combinedDomains: ");

                  for(var7 = 0; var7 < var5.length; ++var7) {
                     debug.println("newDomain " + var7 + ": " + printDomain(var5[var7]));
                  }
               } else {
                  debug.println("returning null");
               }
            }

            return var5 != null && var5.length != 0 ? var5 : null;
         }
      } else {
         return var2;
      }
   }

   private ProtectionDomain[] combineJavaxPolicy(ProtectionDomain[] var1, ProtectionDomain[] var2) {
      if (!allowCaching) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Policy.getPolicy().refresh();
               return null;
            }
         });
      }

      int var3 = var1 == null ? 0 : var1.length;
      int var4 = var2 == null ? 0 : var2.length;
      ProtectionDomain[] var5 = new ProtectionDomain[var3 + var4];
      synchronized(this.cachedPDs) {
         if (!this.subject.isReadOnly() && !this.subject.getPrincipals().equals(this.principalSet)) {
            Set var7 = this.subject.getPrincipals();
            synchronized(var7) {
               this.principalSet = new HashSet(var7);
            }

            this.principals = (Principal[])this.principalSet.toArray(new Principal[this.principalSet.size()]);
            this.cachedPDs.clear();
            if (debug != null) {
               debug.println("Subject mutated - clearing cache");
            }
         }

         for(int var24 = 0; var24 < var3; ++var24) {
            ProtectionDomain var8 = var1[var24];
            ProtectionDomain var9 = (ProtectionDomain)this.cachedPDs.getValue(var8);
            if (var9 == null) {
               if (pdAccess.getStaticPermissionsField(var8)) {
                  var9 = new ProtectionDomain(var8.getCodeSource(), var8.getPermissions());
               } else {
                  Permissions var10 = new Permissions();
                  PermissionCollection var11 = var8.getPermissions();
                  Enumeration var12;
                  if (var11 != null) {
                     synchronized(var11) {
                        var12 = var11.elements();

                        while(var12.hasMoreElements()) {
                           Permission var14 = (Permission)var12.nextElement();
                           var10.add(var14);
                        }
                     }
                  }

                  final CodeSource var13 = var8.getCodeSource();
                  final Subject var25 = this.subject;
                  PermissionCollection var15 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
                     public PermissionCollection run() {
                        return Policy.getPolicy().getPermissions(var25, var13);
                     }
                  });
                  synchronized(var15) {
                     var12 = var15.elements();

                     while(true) {
                        if (!var12.hasMoreElements()) {
                           break;
                        }

                        Permission var17 = (Permission)var12.nextElement();
                        if (!var10.implies(var17)) {
                           var10.add(var17);
                           if (debug != null) {
                              debug.println("Adding perm " + var17 + "\n");
                           }
                        }
                     }
                  }

                  var9 = new ProtectionDomain(var13, var10, var8.getClassLoader(), this.principals);
               }

               if (allowCaching) {
                  this.cachedPDs.putValue(var8, var9);
               }
            }

            var5[var24] = var9;
         }
      }

      int var6;
      if (debug != null) {
         debug.println("updated current: ");

         for(var6 = 0; var6 < var3; ++var6) {
            debug.println("\tupdated[" + var6 + "] = " + var5[var6]);
         }
      }

      if (var4 > 0) {
         System.arraycopy(var2, 0, var5, var3, var4);
      }

      if (debug != null) {
         if (var5 != null && var5.length != 0) {
            debug.println("combinedDomains: ");

            for(var6 = 0; var6 < var5.length; ++var6) {
               debug.println("newDomain " + var6 + ": " + var5[var6].toString());
            }
         } else {
            debug.println("returning null");
         }
      }

      return var5 != null && var5.length != 0 ? var5 : null;
   }

   private static ProtectionDomain[] optimize(ProtectionDomain[] var0) {
      if (var0 != null && var0.length != 0) {
         ProtectionDomain[] var1 = new ProtectionDomain[var0.length];
         int var3 = 0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            ProtectionDomain var2;
            if ((var2 = var0[var4]) != null) {
               boolean var5 = false;

               for(int var6 = 0; var6 < var3 && !var5; ++var6) {
                  var5 = var1[var6] == var2;
               }

               if (!var5) {
                  var1[var3++] = var2;
               }
            }
         }

         if (var3 > 0 && var3 < var0.length) {
            ProtectionDomain[] var7 = new ProtectionDomain[var3];
            System.arraycopy(var1, 0, var7, 0, var7.length);
            var1 = var7;
         }

         return var3 != 0 && var1.length != 0 ? var1 : null;
      } else {
         return null;
      }
   }

   private static boolean cachePolicy() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("cache.auth.policy");
         }
      });
      return var0 != null ? Boolean.parseBoolean(var0) : true;
   }

   private static void printInputDomains(ProtectionDomain[] var0, ProtectionDomain[] var1) {
      int var2;
      if (var0 != null && var0.length != 0) {
         for(var2 = 0; var0 != null && var2 < var0.length; ++var2) {
            if (var0[var2] == null) {
               debug.println("currentDomain " + var2 + ": SystemDomain");
            } else {
               debug.println("currentDomain " + var2 + ": " + printDomain(var0[var2]));
            }
         }
      } else {
         debug.println("currentDomains null or 0 length");
      }

      if (var1 != null && var1.length != 0) {
         debug.println("assignedDomains = ");

         for(var2 = 0; var1 != null && var2 < var1.length; ++var2) {
            if (var1[var2] == null) {
               debug.println("assignedDomain " + var2 + ": SystemDomain");
            } else {
               debug.println("assignedDomain " + var2 + ": " + printDomain(var1[var2]));
            }
         }
      } else {
         debug.println("assignedDomains null or 0 length");
      }

   }

   private static String printDomain(final ProtectionDomain var0) {
      return var0 == null ? "null" : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return var0.toString();
         }
      });
   }

   static {
      useJavaxPolicy = Policy.isCustomPolicySet(debug);
      allowCaching = useJavaxPolicy && cachePolicy();
      pdAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
   }

   private static class WeakKeyValueMap<K, V> extends WeakHashMap<K, WeakReference<V>> {
      private WeakKeyValueMap() {
      }

      public V getValue(K var1) {
         WeakReference var2 = (WeakReference)super.get(var1);
         return var2 != null ? var2.get() : null;
      }

      public V putValue(K var1, V var2) {
         WeakReference var3 = (WeakReference)super.put(var1, new WeakReference(var2));
         return var3 != null ? var3.get() : null;
      }

      // $FF: synthetic method
      WeakKeyValueMap(Object var1) {
         this();
      }
   }
}
