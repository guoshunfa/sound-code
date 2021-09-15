package sun.security.acl;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

public class GroupImpl implements Group {
   private Vector<Principal> groupMembers = new Vector(50, 100);
   private String group;

   public GroupImpl(String var1) {
      this.group = var1;
   }

   public boolean addMember(Principal var1) {
      if (this.groupMembers.contains(var1)) {
         return false;
      } else if (this.group.equals(var1.toString())) {
         throw new IllegalArgumentException();
      } else {
         this.groupMembers.addElement(var1);
         return true;
      }
   }

   public boolean removeMember(Principal var1) {
      return this.groupMembers.removeElement(var1);
   }

   public Enumeration<? extends Principal> members() {
      return this.groupMembers.elements();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Group)) {
         return false;
      } else {
         Group var2 = (Group)var1;
         return this.group.equals(var2.toString());
      }
   }

   public boolean equals(Group var1) {
      return this.equals((Object)var1);
   }

   public String toString() {
      return this.group;
   }

   public int hashCode() {
      return this.group.hashCode();
   }

   public boolean isMember(Principal var1) {
      if (this.groupMembers.contains(var1)) {
         return true;
      } else {
         Vector var2 = new Vector(10);
         return this.isMemberRecurse(var1, var2);
      }
   }

   public String getName() {
      return this.group;
   }

   boolean isMemberRecurse(Principal var1, Vector<Group> var2) {
      Enumeration var3 = this.members();

      boolean var4;
      do {
         if (!var3.hasMoreElements()) {
            return false;
         }

         var4 = false;
         Principal var5 = (Principal)var3.nextElement();
         if (var5.equals(var1)) {
            return true;
         }

         if (var5 instanceof GroupImpl) {
            GroupImpl var6 = (GroupImpl)var5;
            var2.addElement(this);
            if (!var2.contains(var6)) {
               var4 = var6.isMemberRecurse(var1, var2);
            }
         } else if (var5 instanceof Group) {
            Group var7 = (Group)var5;
            if (!var2.contains(var7)) {
               var4 = var7.isMember(var1);
            }
         }
      } while(!var4);

      return var4;
   }
}
