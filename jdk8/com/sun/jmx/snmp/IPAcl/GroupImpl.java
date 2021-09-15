package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

class GroupImpl extends PrincipalImpl implements Group, Serializable {
   private static final long serialVersionUID = -7777387035032541168L;

   public GroupImpl() throws UnknownHostException {
   }

   public GroupImpl(String var1) throws UnknownHostException {
      super(var1);
   }

   public boolean addMember(Principal var1) {
      return true;
   }

   public int hashCode() {
      return super.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PrincipalImpl) && !(var1 instanceof GroupImpl)) {
         return false;
      } else {
         return (super.hashCode() & var1.hashCode()) == var1.hashCode();
      }
   }

   public boolean isMember(Principal var1) {
      return (var1.hashCode() & super.hashCode()) == var1.hashCode();
   }

   public Enumeration<? extends Principal> members() {
      Vector var1 = new Vector(1);
      var1.addElement(this);
      return var1.elements();
   }

   public boolean removeMember(Principal var1) {
      return true;
   }

   public String toString() {
      return "GroupImpl :" + super.getAddress().toString();
   }
}
