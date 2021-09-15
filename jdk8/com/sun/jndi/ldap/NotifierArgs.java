package com.sun.jndi.ldap;

import javax.naming.directory.SearchControls;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;

final class NotifierArgs {
   static final int ADDED_MASK = 1;
   static final int REMOVED_MASK = 2;
   static final int CHANGED_MASK = 4;
   static final int RENAMED_MASK = 8;
   String name;
   String filter;
   SearchControls controls;
   int mask;
   private int sum;

   NotifierArgs(String var1, int var2, NamingListener var3) {
      this(var1, "(objectclass=*)", (SearchControls)null, var3);
      if (var2 != 1) {
         this.controls = new SearchControls();
         this.controls.setSearchScope(var2);
      }

   }

   NotifierArgs(String var1, String var2, SearchControls var3, NamingListener var4) {
      this.sum = -1;
      this.name = var1;
      this.filter = var2;
      this.controls = var3;
      if (var4 instanceof NamespaceChangeListener) {
         this.mask |= 11;
      }

      if (var4 instanceof ObjectChangeListener) {
         this.mask |= 4;
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof NotifierArgs)) {
         return false;
      } else {
         NotifierArgs var2 = (NotifierArgs)var1;
         return this.mask == var2.mask && this.name.equals(var2.name) && this.filter.equals(var2.filter) && this.checkControls(var2.controls);
      }
   }

   private boolean checkControls(SearchControls var1) {
      if (this.controls != null && var1 != null) {
         return this.controls.getSearchScope() == var1.getSearchScope() && this.controls.getTimeLimit() == var1.getTimeLimit() && this.controls.getDerefLinkFlag() == var1.getDerefLinkFlag() && this.controls.getReturningObjFlag() == var1.getReturningObjFlag() && this.controls.getCountLimit() == var1.getCountLimit() && checkStringArrays(this.controls.getReturningAttributes(), var1.getReturningAttributes());
      } else {
         return var1 == this.controls;
      }
   }

   private static boolean checkStringArrays(String[] var0, String[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (!var0[var2].equals(var1[var2])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0 == var1;
      }
   }

   public int hashCode() {
      if (this.sum == -1) {
         this.sum = this.mask + this.name.hashCode() + this.filter.hashCode() + this.controlsCode();
      }

      return this.sum;
   }

   private int controlsCode() {
      if (this.controls == null) {
         return 0;
      } else {
         int var1 = this.controls.getTimeLimit() + (int)this.controls.getCountLimit() + (this.controls.getDerefLinkFlag() ? 1 : 0) + (this.controls.getReturningObjFlag() ? 1 : 0);
         String[] var2 = this.controls.getReturningAttributes();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               var1 += var2[var3].hashCode();
            }
         }

         return var1;
      }
   }
}
