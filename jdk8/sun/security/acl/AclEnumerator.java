package sun.security.acl;

import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

final class AclEnumerator implements Enumeration<AclEntry> {
   Acl acl;
   Enumeration<AclEntry> u1;
   Enumeration<AclEntry> u2;
   Enumeration<AclEntry> g1;
   Enumeration<AclEntry> g2;

   AclEnumerator(Acl var1, Hashtable<?, AclEntry> var2, Hashtable<?, AclEntry> var3, Hashtable<?, AclEntry> var4, Hashtable<?, AclEntry> var5) {
      this.acl = var1;
      this.u1 = var2.elements();
      this.u2 = var4.elements();
      this.g1 = var3.elements();
      this.g2 = var5.elements();
   }

   public boolean hasMoreElements() {
      return this.u1.hasMoreElements() || this.u2.hasMoreElements() || this.g1.hasMoreElements() || this.g2.hasMoreElements();
   }

   public AclEntry nextElement() {
      synchronized(this.acl) {
         if (this.u1.hasMoreElements()) {
            return (AclEntry)this.u1.nextElement();
         }

         if (this.u2.hasMoreElements()) {
            return (AclEntry)this.u2.nextElement();
         }

         if (this.g1.hasMoreElements()) {
            return (AclEntry)this.g1.nextElement();
         }

         if (this.g2.hasMoreElements()) {
            return (AclEntry)this.g2.nextElement();
         }
      }

      throw new NoSuchElementException("Acl Enumerator");
   }
}
