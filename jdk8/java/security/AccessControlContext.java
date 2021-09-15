package java.security;

import java.util.ArrayList;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public final class AccessControlContext {
   private ProtectionDomain[] context;
   private boolean isPrivileged;
   private boolean isAuthorized;
   private AccessControlContext privilegedContext;
   private DomainCombiner combiner;
   private Permission[] permissions;
   private AccessControlContext parent;
   private boolean isWrapped;
   private boolean isLimited;
   private ProtectionDomain[] limitedContext;
   private static boolean debugInit = false;
   private static Debug debug = null;

   static Debug getDebug() {
      if (debugInit) {
         return debug;
      } else {
         if (Policy.isSet()) {
            debug = Debug.getInstance("access");
            debugInit = true;
         }

         return debug;
      }
   }

   public AccessControlContext(ProtectionDomain[] var1) {
      this.isAuthorized = false;
      this.combiner = null;
      if (var1.length == 0) {
         this.context = null;
      } else if (var1.length == 1) {
         if (var1[0] != null) {
            this.context = (ProtectionDomain[])var1.clone();
         } else {
            this.context = null;
         }
      } else {
         ArrayList var2 = new ArrayList(var1.length);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != null && !var2.contains(var1[var3])) {
               var2.add(var1[var3]);
            }
         }

         if (!var2.isEmpty()) {
            this.context = new ProtectionDomain[var2.size()];
            this.context = (ProtectionDomain[])var2.toArray(this.context);
         }
      }

   }

   public AccessControlContext(AccessControlContext var1, DomainCombiner var2) {
      this(var1, var2, false);
   }

   AccessControlContext(AccessControlContext var1, DomainCombiner var2, boolean var3) {
      this.isAuthorized = false;
      this.combiner = null;
      if (!var3) {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            var4.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
            this.isAuthorized = true;
         }
      } else {
         this.isAuthorized = true;
      }

      this.context = var1.context;
      this.combiner = var2;
   }

   AccessControlContext(ProtectionDomain var1, DomainCombiner var2, AccessControlContext var3, AccessControlContext var4, Permission[] var5) {
      this.isAuthorized = false;
      this.combiner = null;
      ProtectionDomain[] var6 = null;
      if (var1 != null) {
         var6 = new ProtectionDomain[]{var1};
      }

      if (var4 != null) {
         if (var2 != null) {
            this.context = var2.combine(var6, var4.context);
         } else {
            this.context = combine(var6, var4.context);
         }
      } else if (var2 != null) {
         this.context = var2.combine(var6, (ProtectionDomain[])null);
      } else {
         this.context = combine(var6, (ProtectionDomain[])null);
      }

      this.combiner = var2;
      Permission[] var7 = null;
      if (var5 != null) {
         var7 = new Permission[var5.length];

         for(int var8 = 0; var8 < var5.length; ++var8) {
            if (var5[var8] == null) {
               throw new NullPointerException("permission can't be null");
            }

            if (var5[var8].getClass() == AllPermission.class) {
               var3 = null;
            }

            var7[var8] = var5[var8];
         }
      }

      if (var3 != null) {
         this.limitedContext = combine(var3.context, var3.limitedContext);
         this.isLimited = true;
         this.isWrapped = true;
         this.permissions = var7;
         this.parent = var3;
         this.privilegedContext = var4;
      }

      this.isAuthorized = true;
   }

   AccessControlContext(ProtectionDomain[] var1, boolean var2) {
      this.isAuthorized = false;
      this.combiner = null;
      this.context = var1;
      this.isPrivileged = var2;
      this.isAuthorized = true;
   }

   AccessControlContext(ProtectionDomain[] var1, AccessControlContext var2) {
      this.isAuthorized = false;
      this.combiner = null;
      this.context = var1;
      this.privilegedContext = var2;
      this.isPrivileged = true;
   }

   ProtectionDomain[] getContext() {
      return this.context;
   }

   boolean isPrivileged() {
      return this.isPrivileged;
   }

   DomainCombiner getAssignedCombiner() {
      AccessControlContext var1;
      if (this.isPrivileged) {
         var1 = this.privilegedContext;
      } else {
         var1 = AccessController.getInheritedAccessControlContext();
      }

      return var1 != null ? var1.combiner : null;
   }

   public DomainCombiner getDomainCombiner() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION);
      }

      return this.getCombiner();
   }

   DomainCombiner getCombiner() {
      return this.combiner;
   }

   boolean isAuthorized() {
      return this.isAuthorized;
   }

   public void checkPermission(Permission var1) throws AccessControlException {
      boolean var2 = false;
      if (var1 == null) {
         throw new NullPointerException("permission can't be null");
      } else {
         int var3;
         if (getDebug() != null) {
            var2 = !Debug.isOn("codebase=");
            if (!var2) {
               for(var3 = 0; this.context != null && var3 < this.context.length; ++var3) {
                  if (this.context[var3].getCodeSource() != null && this.context[var3].getCodeSource().getLocation() != null && Debug.isOn("codebase=" + this.context[var3].getCodeSource().getLocation().toString())) {
                     var2 = true;
                     break;
                  }
               }
            }

            var2 &= !Debug.isOn("permission=") || Debug.isOn("permission=" + var1.getClass().getCanonicalName());
            if (var2 && Debug.isOn("stack")) {
               Thread.dumpStack();
            }

            if (var2 && Debug.isOn("domain")) {
               if (this.context == null) {
                  debug.println("domain (context is null)");
               } else {
                  for(var3 = 0; var3 < this.context.length; ++var3) {
                     debug.println("domain " + var3 + " " + this.context[var3]);
                  }
               }
            }
         }

         if (this.context == null) {
            this.checkPermission2(var1);
         } else {
            for(var3 = 0; var3 < this.context.length; ++var3) {
               if (this.context[var3] != null && !this.context[var3].implies(var1)) {
                  if (var2) {
                     debug.println("access denied " + var1);
                  }

                  if (Debug.isOn("failure") && debug != null) {
                     if (!var2) {
                        debug.println("access denied " + var1);
                     }

                     Thread.dumpStack();
                     final ProtectionDomain var4 = this.context[var3];
                     final Debug var5 = debug;
                     AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                           var5.println("domain that failed " + var4);
                           return null;
                        }
                     });
                  }

                  throw new AccessControlException("access denied " + var1, var1);
               }
            }

            if (var2) {
               debug.println("access allowed " + var1);
            }

            this.checkPermission2(var1);
         }
      }
   }

   private void checkPermission2(Permission var1) {
      if (this.isLimited) {
         if (this.privilegedContext != null) {
            this.privilegedContext.checkPermission2(var1);
         }

         if (!this.isWrapped) {
            if (this.permissions != null) {
               Class var2 = var1.getClass();

               for(int var3 = 0; var3 < this.permissions.length; ++var3) {
                  Permission var4 = this.permissions[var3];
                  if (var4.getClass().equals(var2) && var4.implies(var1)) {
                     return;
                  }
               }
            }

            if (this.parent != null) {
               if (this.permissions == null) {
                  this.parent.checkPermission2(var1);
               } else {
                  this.parent.checkPermission(var1);
               }
            }

         }
      }
   }

   AccessControlContext optimize() {
      DomainCombiner var2 = null;
      AccessControlContext var3 = null;
      Permission[] var4 = null;
      AccessControlContext var1;
      if (this.isPrivileged) {
         var1 = this.privilegedContext;
         if (var1 != null && var1.isWrapped) {
            var4 = var1.permissions;
            var3 = var1.parent;
         }
      } else {
         var1 = AccessController.getInheritedAccessControlContext();
         if (var1 != null && var1.isLimited) {
            var3 = var1;
         }
      }

      boolean var5 = this.context == null;
      boolean var6 = var1 == null || var1.context == null;
      ProtectionDomain[] var7 = var6 ? null : var1.context;
      boolean var9 = (var1 == null || !var1.isWrapped) && var3 == null;
      ProtectionDomain[] var8;
      if (var1 != null && var1.combiner != null) {
         if (getDebug() != null) {
            debug.println("AccessControlContext invoking the Combiner");
         }

         var2 = var1.combiner;
         var8 = var2.combine(this.context, var7);
      } else {
         if (var5) {
            if (var6) {
               this.calculateFields(var1, var3, var4);
               return this;
            }

            if (var9) {
               return var1;
            }
         } else if (var7 != null && var9 && this.context.length == 1 && this.context[0] == var7[0]) {
            return var1;
         }

         var8 = combine(this.context, var7);
         if (var9 && !var6 && var8 == var7) {
            return var1;
         }

         if (var6 && var8 == this.context) {
            this.calculateFields(var1, var3, var4);
            return this;
         }
      }

      this.context = var8;
      this.combiner = var2;
      this.isPrivileged = false;
      this.calculateFields(var1, var3, var4);
      return this;
   }

   private static ProtectionDomain[] combine(ProtectionDomain[] var0, ProtectionDomain[] var1) {
      boolean var2 = var0 == null;
      boolean var3 = var1 == null;
      int var4 = var2 ? 0 : var0.length;
      if (var3 && var4 <= 2) {
         return var0;
      } else {
         int var5 = var3 ? 0 : var1.length;
         ProtectionDomain[] var6 = new ProtectionDomain[var4 + var5];
         if (!var3) {
            System.arraycopy(var1, 0, var6, 0, var5);
         }

         label73:
         for(int var7 = 0; var7 < var4; ++var7) {
            ProtectionDomain var8 = var0[var7];
            if (var8 != null) {
               for(int var9 = 0; var9 < var5; ++var9) {
                  if (var8 == var6[var9]) {
                     continue label73;
                  }
               }

               var6[var5++] = var8;
            }
         }

         if (var5 != var6.length) {
            if (!var3 && var5 == var1.length) {
               return var1;
            }

            if (var3 && var5 == var4) {
               return var0;
            }

            ProtectionDomain[] var10 = new ProtectionDomain[var5];
            System.arraycopy(var6, 0, var10, 0, var5);
            var6 = var10;
         }

         return var6;
      }
   }

   private void calculateFields(AccessControlContext var1, AccessControlContext var2, Permission[] var3) {
      ProtectionDomain[] var4 = null;
      ProtectionDomain[] var5 = null;
      var4 = var2 != null ? var2.limitedContext : null;
      var5 = var1 != null ? var1.limitedContext : null;
      ProtectionDomain[] var6 = combine(var4, var5);
      if (var6 != null && (this.context == null || !containsAllPDs(var6, this.context))) {
         this.limitedContext = var6;
         this.permissions = var3;
         this.parent = var2;
         this.isLimited = true;
      }

   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AccessControlContext)) {
         return false;
      } else {
         AccessControlContext var2 = (AccessControlContext)var1;
         if (!this.equalContext(var2)) {
            return false;
         } else {
            return this.equalLimitedContext(var2);
         }
      }
   }

   private boolean equalContext(AccessControlContext var1) {
      if (!this.equalPDs(this.context, var1.context)) {
         return false;
      } else if (this.combiner == null && var1.combiner != null) {
         return false;
      } else {
         return this.combiner == null || this.combiner.equals(var1.combiner);
      }
   }

   private boolean equalPDs(ProtectionDomain[] var1, ProtectionDomain[] var2) {
      if (var1 == null) {
         return var2 == null;
      } else if (var2 == null) {
         return false;
      } else {
         return containsAllPDs(var1, var2) && containsAllPDs(var2, var1);
      }
   }

   private boolean equalLimitedContext(AccessControlContext var1) {
      if (var1 == null) {
         return false;
      } else if (!this.isLimited && !var1.isLimited) {
         return true;
      } else if (this.isLimited && var1.isLimited) {
         if (this.isWrapped && !var1.isWrapped || !this.isWrapped && var1.isWrapped) {
            return false;
         } else if (this.permissions == null && var1.permissions != null) {
            return false;
         } else if (this.permissions != null && var1.permissions == null) {
            return false;
         } else if (this.containsAllLimits(var1) && var1.containsAllLimits(this)) {
            AccessControlContext var2 = getNextPC(this);
            AccessControlContext var3 = getNextPC(var1);
            if (var2 == null && var3 != null && var3.isLimited) {
               return false;
            } else if (var2 != null && !var2.equalLimitedContext(var3)) {
               return false;
            } else if (this.parent == null && var1.parent != null) {
               return false;
            } else {
               return this.parent == null || this.parent.equals(var1.parent);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static AccessControlContext getNextPC(AccessControlContext var0) {
      while(true) {
         if (var0 != null && var0.privilegedContext != null) {
            var0 = var0.privilegedContext;
            if (var0.isWrapped) {
               continue;
            }

            return var0;
         }

         return null;
      }
   }

   private static boolean containsAllPDs(ProtectionDomain[] var0, ProtectionDomain[] var1) {
      boolean var2 = false;

      for(int var4 = 0; var4 < var0.length; ++var4) {
         var2 = false;
         ProtectionDomain var3;
         if ((var3 = var0[var4]) == null) {
            for(int var8 = 0; var8 < var1.length && !var2; ++var8) {
               var2 = var1[var8] == null;
            }
         } else {
            Class var5 = var3.getClass();

            for(int var7 = 0; var7 < var1.length && !var2; ++var7) {
               ProtectionDomain var6 = var1[var7];
               var2 = var6 != null && var5 == var6.getClass() && var3.equals(var6);
            }
         }

         if (!var2) {
            return false;
         }
      }

      return var2;
   }

   private boolean containsAllLimits(AccessControlContext var1) {
      boolean var2 = false;
      if (this.permissions == null && var1.permissions == null) {
         return true;
      } else {
         for(int var4 = 0; var4 < this.permissions.length; ++var4) {
            Permission var5 = this.permissions[var4];
            Class var6 = var5.getClass();
            var2 = false;

            for(int var7 = 0; var7 < var1.permissions.length && !var2; ++var7) {
               Permission var8 = var1.permissions[var7];
               var2 = var6.equals(var8.getClass()) && var5.equals(var8);
            }

            if (!var2) {
               return false;
            }
         }

         return var2;
      }
   }

   public int hashCode() {
      int var1 = 0;
      if (this.context == null) {
         return var1;
      } else {
         for(int var2 = 0; var2 < this.context.length; ++var2) {
            if (this.context[var2] != null) {
               var1 ^= this.context[var2].hashCode();
            }
         }

         return var1;
      }
   }
}
