package sun.rmi.transport;

import java.rmi.server.ObjID;

class ObjectEndpoint {
   private final ObjID id;
   private final Transport transport;

   ObjectEndpoint(ObjID var1, Transport var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         assert var2 != null || var1.equals(new ObjID(2));

         this.id = var1;
         this.transport = var2;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectEndpoint)) {
         return false;
      } else {
         ObjectEndpoint var2 = (ObjectEndpoint)var1;
         return this.id.equals(var2.id) && this.transport == var2.transport;
      }
   }

   public int hashCode() {
      return this.id.hashCode() ^ (this.transport != null ? this.transport.hashCode() : 0);
   }

   public String toString() {
      return this.id.toString();
   }
}
