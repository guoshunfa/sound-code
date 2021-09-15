package java.rmi.activation;

import java.io.Serializable;
import java.rmi.server.UID;

public class ActivationGroupID implements Serializable {
   private ActivationSystem system;
   private UID uid = new UID();
   private static final long serialVersionUID = -1648432278909740833L;

   public ActivationGroupID(ActivationSystem var1) {
      this.system = var1;
   }

   public ActivationSystem getSystem() {
      return this.system;
   }

   public int hashCode() {
      return this.uid.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ActivationGroupID)) {
         return false;
      } else {
         ActivationGroupID var2 = (ActivationGroupID)var1;
         return this.uid.equals(var2.uid) && this.system.equals(var2.system);
      }
   }
}
