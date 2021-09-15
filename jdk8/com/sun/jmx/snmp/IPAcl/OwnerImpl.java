package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Owner;
import java.util.Vector;

class OwnerImpl implements Owner, Serializable {
   private static final long serialVersionUID = -576066072046319874L;
   private Vector<Principal> ownerList = null;

   public OwnerImpl() {
      this.ownerList = new Vector();
   }

   public OwnerImpl(PrincipalImpl var1) {
      this.ownerList = new Vector();
      this.ownerList.addElement(var1);
   }

   public boolean addOwner(Principal var1, Principal var2) throws NotOwnerException {
      if (!this.ownerList.contains(var1)) {
         throw new NotOwnerException();
      } else if (this.ownerList.contains(var2)) {
         return false;
      } else {
         this.ownerList.addElement(var2);
         return true;
      }
   }

   public boolean deleteOwner(Principal var1, Principal var2) throws NotOwnerException, LastOwnerException {
      if (!this.ownerList.contains(var1)) {
         throw new NotOwnerException();
      } else if (!this.ownerList.contains(var2)) {
         return false;
      } else if (this.ownerList.size() == 1) {
         throw new LastOwnerException();
      } else {
         this.ownerList.removeElement(var2);
         return true;
      }
   }

   public boolean isOwner(Principal var1) {
      return this.ownerList.contains(var1);
   }
}
