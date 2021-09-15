package sun.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AclImpl extends OwnerImpl implements Acl {
   private Hashtable<Principal, AclEntry> allowedUsersTable = new Hashtable(23);
   private Hashtable<Principal, AclEntry> allowedGroupsTable = new Hashtable(23);
   private Hashtable<Principal, AclEntry> deniedUsersTable = new Hashtable(23);
   private Hashtable<Principal, AclEntry> deniedGroupsTable = new Hashtable(23);
   private String aclName = null;
   private Vector<Permission> zeroSet = new Vector(1, 1);

   public AclImpl(Principal var1, String var2) {
      super(var1);

      try {
         this.setName(var1, var2);
      } catch (Exception var4) {
      }

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

   public synchronized boolean addEntry(Principal var1, AclEntry var2) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else {
         Hashtable var3 = this.findTable(var2);
         Principal var4 = var2.getPrincipal();
         if (var3.get(var4) != null) {
            return false;
         } else {
            var3.put(var4, var2);
            return true;
         }
      }
   }

   public synchronized boolean removeEntry(Principal var1, AclEntry var2) throws NotOwnerException {
      if (!this.isOwner(var1)) {
         throw new NotOwnerException();
      } else {
         Hashtable var3 = this.findTable(var2);
         Principal var4 = var2.getPrincipal();
         AclEntry var5 = (AclEntry)var3.remove(var4);
         return var5 != null;
      }
   }

   public synchronized Enumeration<Permission> getPermissions(Principal var1) {
      Enumeration var4 = this.subtract(this.getGroupPositive(var1), this.getGroupNegative(var1));
      Enumeration var5 = this.subtract(this.getGroupNegative(var1), this.getGroupPositive(var1));
      Enumeration var2 = this.subtract(this.getIndividualPositive(var1), this.getIndividualNegative(var1));
      Enumeration var3 = this.subtract(this.getIndividualNegative(var1), this.getIndividualPositive(var1));
      Enumeration var6 = this.subtract(var4, var3);
      Enumeration var7 = union(var2, var6);
      var2 = this.subtract(this.getIndividualPositive(var1), this.getIndividualNegative(var1));
      var3 = this.subtract(this.getIndividualNegative(var1), this.getIndividualPositive(var1));
      var6 = this.subtract(var5, var2);
      Enumeration var8 = union(var3, var6);
      return this.subtract(var7, var8);
   }

   public boolean checkPermission(Principal var1, Permission var2) {
      Enumeration var3 = this.getPermissions(var1);

      Permission var4;
      do {
         if (!var3.hasMoreElements()) {
            return false;
         }

         var4 = (Permission)var3.nextElement();
      } while(!var4.equals(var2));

      return true;
   }

   public synchronized Enumeration<AclEntry> entries() {
      return new AclEnumerator(this, this.allowedUsersTable, this.allowedGroupsTable, this.deniedUsersTable, this.deniedGroupsTable);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      Enumeration var2 = this.entries();

      while(var2.hasMoreElements()) {
         AclEntry var3 = (AclEntry)var2.nextElement();
         var1.append(var3.toString().trim());
         var1.append("\n");
      }

      return var1.toString();
   }

   private Hashtable<Principal, AclEntry> findTable(AclEntry var1) {
      Hashtable var2 = null;
      Principal var3 = var1.getPrincipal();
      if (var3 instanceof Group) {
         if (var1.isNegative()) {
            var2 = this.deniedGroupsTable;
         } else {
            var2 = this.allowedGroupsTable;
         }
      } else if (var1.isNegative()) {
         var2 = this.deniedUsersTable;
      } else {
         var2 = this.allowedUsersTable;
      }

      return var2;
   }

   private static Enumeration<Permission> union(Enumeration<Permission> var0, Enumeration<Permission> var1) {
      Vector var2 = new Vector(20, 20);

      while(var0.hasMoreElements()) {
         var2.addElement(var0.nextElement());
      }

      while(var1.hasMoreElements()) {
         Permission var3 = (Permission)var1.nextElement();
         if (!var2.contains(var3)) {
            var2.addElement(var3);
         }
      }

      return var2.elements();
   }

   private Enumeration<Permission> subtract(Enumeration<Permission> var1, Enumeration<Permission> var2) {
      Vector var3 = new Vector(20, 20);

      while(var1.hasMoreElements()) {
         var3.addElement(var1.nextElement());
      }

      while(var2.hasMoreElements()) {
         Permission var4 = (Permission)var2.nextElement();
         if (var3.contains(var4)) {
            var3.removeElement(var4);
         }
      }

      return var3.elements();
   }

   private Enumeration<Permission> getGroupPositive(Principal var1) {
      Enumeration var2 = this.zeroSet.elements();
      Enumeration var3 = this.allowedGroupsTable.keys();

      while(var3.hasMoreElements()) {
         Group var4 = (Group)var3.nextElement();
         if (var4.isMember(var1)) {
            AclEntry var5 = (AclEntry)this.allowedGroupsTable.get(var4);
            var2 = union(var5.permissions(), var2);
         }
      }

      return var2;
   }

   private Enumeration<Permission> getGroupNegative(Principal var1) {
      Enumeration var2 = this.zeroSet.elements();
      Enumeration var3 = this.deniedGroupsTable.keys();

      while(var3.hasMoreElements()) {
         Group var4 = (Group)var3.nextElement();
         if (var4.isMember(var1)) {
            AclEntry var5 = (AclEntry)this.deniedGroupsTable.get(var4);
            var2 = union(var5.permissions(), var2);
         }
      }

      return var2;
   }

   private Enumeration<Permission> getIndividualPositive(Principal var1) {
      Enumeration var2 = this.zeroSet.elements();
      AclEntry var3 = (AclEntry)this.allowedUsersTable.get(var1);
      if (var3 != null) {
         var2 = var3.permissions();
      }

      return var2;
   }

   private Enumeration<Permission> getIndividualNegative(Principal var1) {
      Enumeration var2 = this.zeroSet.elements();
      AclEntry var3 = (AclEntry)this.deniedUsersTable.get(var1);
      if (var3 != null) {
         var2 = var3.permissions();
      }

      return var2;
   }
}
