package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclImpl extends OwnerImpl implements Acl, Serializable {
   private static final long serialVersionUID = -2250957591085270029L;
   private Vector<AclEntry> entryList = null;
   private String aclName = null;

   public AclImpl(PrincipalImpl var1, String var2) {
      super(var1);
      this.entryList = new Vector();
      this.aclName = var2;
   }

   public void setName(Principal var1, String var2) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else {
         this.aclName = var2;
      }
   }

   public String getName() {
      return this.aclName;
   }

   public boolean addEntry(Principal var1, AclEntry var2) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else if (this.entryList.contains(var2)) {
         return false;
      } else {
         this.entryList.addElement(var2);
         return true;
      }
   }

   public boolean removeEntry(Principal var1, AclEntry var2) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else {
         return this.entryList.removeElement(var2);
      }
   }

   public void removeAll(Principal var1) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else {
         this.entryList.removeAllElements();
      }
   }

   public Enumeration<Permission> getPermissions(Principal var1) {
      Vector var2 = new Vector();
      Enumeration var3 = this.entryList.elements();

      AclEntry var4;
      do {
         if (!var3.hasMoreElements()) {
            return var2.elements();
         }

         var4 = (AclEntry)var3.nextElement();
      } while(!var4.getPrincipal().equals(var1));

      return var4.permissions();
   }

   public Enumeration<AclEntry> entries() {
      return this.entryList.elements();
   }

   public boolean checkPermission(Principal var1, Permission var2) {
      Enumeration var3 = this.entryList.elements();

      AclEntry var4;
      do {
         if (!var3.hasMoreElements()) {
            return false;
         }

         var4 = (AclEntry)var3.nextElement();
      } while(!var4.getPrincipal().equals(var1) || !var4.checkPermission(var2));

      return true;
   }

   public boolean checkPermission(Principal var1, String var2, Permission var3) {
      Enumeration var4 = this.entryList.elements();

      AclEntryImpl var5;
      do {
         if (!var4.hasMoreElements()) {
            return false;
         }

         var5 = (AclEntryImpl)var4.nextElement();
      } while(!var5.getPrincipal().equals(var1) || !var5.checkPermission(var3) || !var5.checkCommunity(var2));

      return true;
   }

   public boolean checkCommunity(String var1) {
      Enumeration var2 = this.entryList.elements();

      AclEntryImpl var3;
      do {
         if (!var2.hasMoreElements()) {
            return false;
         }

         var3 = (AclEntryImpl)var2.nextElement();
      } while(!var3.checkCommunity(var1));

      return true;
   }

   public String toString() {
      return "AclImpl: " + this.getName();
   }
}
