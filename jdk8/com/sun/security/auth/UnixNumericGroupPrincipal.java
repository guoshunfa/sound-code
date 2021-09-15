package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericGroupPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = 3941535899328403223L;
   private String name;
   private boolean primaryGroup;

   public UnixNumericGroupPrincipal(String var1, boolean var2) {
      if (var1 == null) {
         MessageFormat var3 = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
         Object[] var4 = new Object[]{"name"};
         throw new NullPointerException(var3.format(var4));
      } else {
         this.name = var1;
         this.primaryGroup = var2;
      }
   }

   public UnixNumericGroupPrincipal(long var1, boolean var3) {
      this.name = (new Long(var1)).toString();
      this.primaryGroup = var3;
   }

   public String getName() {
      return this.name;
   }

   public long longValue() {
      return new Long(this.name);
   }

   public boolean isPrimaryGroup() {
      return this.primaryGroup;
   }

   public String toString() {
      MessageFormat var1;
      Object[] var2;
      if (this.primaryGroup) {
         var1 = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Primary.Group.name", "sun.security.util.AuthResources"));
         var2 = new Object[]{this.name};
         return var1.format(var2);
      } else {
         var1 = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Supplementary.Group.name", "sun.security.util.AuthResources"));
         var2 = new Object[]{this.name};
         return var1.format(var2);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof UnixNumericGroupPrincipal)) {
         return false;
      } else {
         UnixNumericGroupPrincipal var2 = (UnixNumericGroupPrincipal)var1;
         return this.getName().equals(var2.getName()) && this.isPrimaryGroup() == var2.isPrimaryGroup();
      }
   }

   public int hashCode() {
      return this.toString().hashCode();
   }
}
