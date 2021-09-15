package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclEntryImpl implements AclEntry, Serializable {
   private static final long serialVersionUID = -5047185131260073216L;
   private Principal princ = null;
   private boolean neg = false;
   private Vector<Permission> permList = null;
   private Vector<String> commList = null;

   private AclEntryImpl(AclEntryImpl var1) throws UnknownHostException {
      this.setPrincipal(var1.getPrincipal());
      this.permList = new Vector();
      this.commList = new Vector();
      Enumeration var2 = var1.communities();

      while(var2.hasMoreElements()) {
         this.addCommunity((String)var2.nextElement());
      }

      var2 = var1.permissions();

      while(var2.hasMoreElements()) {
         this.addPermission((Permission)var2.nextElement());
      }

      if (var1.isNegative()) {
         this.setNegativePermissions();
      }

   }

   public AclEntryImpl() {
      this.princ = null;
      this.permList = new Vector();
      this.commList = new Vector();
   }

   public AclEntryImpl(Principal var1) throws UnknownHostException {
      this.princ = var1;
      this.permList = new Vector();
      this.commList = new Vector();
   }

   public Object clone() {
      AclEntryImpl var1;
      try {
         var1 = new AclEntryImpl(this);
      } catch (UnknownHostException var3) {
         var1 = null;
      }

      return var1;
   }

   public boolean isNegative() {
      return this.neg;
   }

   public boolean addPermission(Permission var1) {
      if (this.permList.contains(var1)) {
         return false;
      } else {
         this.permList.addElement(var1);
         return true;
      }
   }

   public boolean removePermission(Permission var1) {
      if (!this.permList.contains(var1)) {
         return false;
      } else {
         this.permList.removeElement(var1);
         return true;
      }
   }

   public boolean checkPermission(Permission var1) {
      return this.permList.contains(var1);
   }

   public Enumeration<Permission> permissions() {
      return this.permList.elements();
   }

   public void setNegativePermissions() {
      this.neg = true;
   }

   public Principal getPrincipal() {
      return this.princ;
   }

   public boolean setPrincipal(Principal var1) {
      if (this.princ != null) {
         return false;
      } else {
         this.princ = var1;
         return true;
      }
   }

   public String toString() {
      return "AclEntry:" + this.princ.toString();
   }

   public Enumeration<String> communities() {
      return this.commList.elements();
   }

   public boolean addCommunity(String var1) {
      if (this.commList.contains(var1)) {
         return false;
      } else {
         this.commList.addElement(var1);
         return true;
      }
   }

   public boolean removeCommunity(String var1) {
      if (!this.commList.contains(var1)) {
         return false;
      } else {
         this.commList.removeElement(var1);
         return true;
      }
   }

   public boolean checkCommunity(String var1) {
      return this.commList.contains(var1);
   }
}
