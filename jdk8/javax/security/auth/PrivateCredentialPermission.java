package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.util.ResourcesMgr;

public final class PrivateCredentialPermission extends Permission {
   private static final long serialVersionUID = 5284372143517237068L;
   private static final PrivateCredentialPermission.CredOwner[] EMPTY_PRINCIPALS = new PrivateCredentialPermission.CredOwner[0];
   private String credentialClass;
   private Set<Principal> principals;
   private transient PrivateCredentialPermission.CredOwner[] credOwners;
   private boolean testing = false;

   PrivateCredentialPermission(String var1, Set<Principal> var2) {
      super(var1);
      this.credentialClass = var1;
      synchronized(var2) {
         if (var2.size() == 0) {
            this.credOwners = EMPTY_PRINCIPALS;
         } else {
            this.credOwners = new PrivateCredentialPermission.CredOwner[var2.size()];
            int var4 = 0;

            Principal var6;
            for(Iterator var5 = var2.iterator(); var5.hasNext(); this.credOwners[var4++] = new PrivateCredentialPermission.CredOwner(var6.getClass().getName(), var6.getName())) {
               var6 = (Principal)var5.next();
            }
         }

      }
   }

   public PrivateCredentialPermission(String var1, String var2) {
      super(var1);
      if (!"read".equalsIgnoreCase(var2)) {
         throw new IllegalArgumentException(ResourcesMgr.getString("actions.can.only.be.read."));
      } else {
         this.init(var1);
      }
   }

   public String getCredentialClass() {
      return this.credentialClass;
   }

   public String[][] getPrincipals() {
      if (this.credOwners != null && this.credOwners.length != 0) {
         String[][] var1 = new String[this.credOwners.length][2];

         for(int var2 = 0; var2 < this.credOwners.length; ++var2) {
            var1[var2][0] = this.credOwners[var2].principalClass;
            var1[var2][1] = this.credOwners[var2].principalName;
         }

         return var1;
      } else {
         return new String[0][0];
      }
   }

   public boolean implies(Permission var1) {
      if (var1 != null && var1 instanceof PrivateCredentialPermission) {
         PrivateCredentialPermission var2 = (PrivateCredentialPermission)var1;
         return !this.impliesCredentialClass(this.credentialClass, var2.credentialClass) ? false : this.impliesPrincipalSet(this.credOwners, var2.credOwners);
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PrivateCredentialPermission)) {
         return false;
      } else {
         PrivateCredentialPermission var2 = (PrivateCredentialPermission)var1;
         return this.implies(var2) && var2.implies(this);
      }
   }

   public int hashCode() {
      return this.credentialClass.hashCode();
   }

   public String getActions() {
      return "read";
   }

   public PermissionCollection newPermissionCollection() {
      return null;
   }

   private void init(String var1) {
      if (var1 != null && var1.trim().length() != 0) {
         ArrayList var2 = new ArrayList();
         StringTokenizer var3 = new StringTokenizer(var1, " ", true);
         String var4 = null;
         String var5 = null;
         if (this.testing) {
            System.out.println("whole name = " + var1);
         }

         this.credentialClass = var3.nextToken();
         if (this.testing) {
            System.out.println("Credential Class = " + this.credentialClass);
         }

         MessageFormat var6;
         Object[] var7;
         if (!var3.hasMoreTokens()) {
            var6 = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
            var7 = new Object[]{var1};
            throw new IllegalArgumentException(var6.format(var7) + ResourcesMgr.getString("Credential.Class.not.followed.by.a.Principal.Class.and.Name"));
         } else {
            for(; var3.hasMoreTokens(); var2.add(new PrivateCredentialPermission.CredOwner(var4, var5))) {
               var3.nextToken();
               var4 = var3.nextToken();
               if (this.testing) {
                  System.out.println("    Principal Class = " + var4);
               }

               if (!var3.hasMoreTokens()) {
                  var6 = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
                  var7 = new Object[]{var1};
                  throw new IllegalArgumentException(var6.format(var7) + ResourcesMgr.getString("Principal.Class.not.followed.by.a.Principal.Name"));
               }

               var3.nextToken();
               var5 = var3.nextToken();
               if (!var5.startsWith("\"")) {
                  var6 = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
                  var7 = new Object[]{var1};
                  throw new IllegalArgumentException(var6.format(var7) + ResourcesMgr.getString("Principal.Name.must.be.surrounded.by.quotes"));
               }

               if (!var5.endsWith("\"")) {
                  while(var3.hasMoreTokens()) {
                     var5 = var5 + var3.nextToken();
                     if (var5.endsWith("\"")) {
                        break;
                     }
                  }

                  if (!var5.endsWith("\"")) {
                     var6 = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
                     var7 = new Object[]{var1};
                     throw new IllegalArgumentException(var6.format(var7) + ResourcesMgr.getString("Principal.Name.missing.end.quote"));
                  }
               }

               if (this.testing) {
                  System.out.println("\tprincipalName = '" + var5 + "'");
               }

               var5 = var5.substring(1, var5.length() - 1);
               if (var4.equals("*") && !var5.equals("*")) {
                  throw new IllegalArgumentException(ResourcesMgr.getString("PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value"));
               }

               if (this.testing) {
                  System.out.println("\tprincipalName = '" + var5 + "'");
               }
            }

            this.credOwners = new PrivateCredentialPermission.CredOwner[var2.size()];
            var2.toArray(this.credOwners);
         }
      } else {
         throw new IllegalArgumentException("invalid empty name");
      }
   }

   private boolean impliesCredentialClass(String var1, String var2) {
      if (var1 != null && var2 != null) {
         if (this.testing) {
            System.out.println("credential class comparison: " + var1 + "/" + var2);
         }

         return var1.equals("*") ? true : var1.equals(var2);
      } else {
         return false;
      }
   }

   private boolean impliesPrincipalSet(PrivateCredentialPermission.CredOwner[] var1, PrivateCredentialPermission.CredOwner[] var2) {
      if (var1 != null && var2 != null) {
         if (var2.length == 0) {
            return true;
         } else if (var1.length == 0) {
            return false;
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               boolean var4 = false;

               for(int var5 = 0; var5 < var2.length; ++var5) {
                  if (var1[var3].implies(var2[var5])) {
                     var4 = true;
                     break;
                  }
               }

               if (!var4) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.getName().indexOf(" ") == -1 && this.getName().indexOf("\"") == -1) {
         this.credentialClass = this.getName();
         this.credOwners = EMPTY_PRINCIPALS;
      } else {
         this.init(this.getName());
      }

   }

   static class CredOwner implements Serializable {
      private static final long serialVersionUID = -5607449830436408266L;
      String principalClass;
      String principalName;

      CredOwner(String var1, String var2) {
         this.principalClass = var1;
         this.principalName = var2;
      }

      public boolean implies(Object var1) {
         if (var1 != null && var1 instanceof PrivateCredentialPermission.CredOwner) {
            PrivateCredentialPermission.CredOwner var2 = (PrivateCredentialPermission.CredOwner)var1;
            return (this.principalClass.equals("*") || this.principalClass.equals(var2.principalClass)) && (this.principalName.equals("*") || this.principalName.equals(var2.principalName));
         } else {
            return false;
         }
      }

      public String toString() {
         MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("CredOwner.Principal.Class.class.Principal.Name.name"));
         Object[] var2 = new Object[]{this.principalClass, this.principalName};
         return var1.format(var2);
      }
   }
}
