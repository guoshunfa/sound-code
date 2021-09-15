package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.ShutdownNotification;

public class Shutdown extends ShutdownNotification implements SctpNotification {
   private Association association;
   private int assocId;

   private Shutdown(int var1) {
      this.assocId = var1;
   }

   public int assocId() {
      return this.assocId;
   }

   public void setAssociation(Association var1) {
      this.association = var1;
   }

   public Association association() {
      assert this.association != null;

      return this.association;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString()).append(" [");
      var1.append("Association:").append((Object)this.association).append("]");
      return var1.toString();
   }
}
