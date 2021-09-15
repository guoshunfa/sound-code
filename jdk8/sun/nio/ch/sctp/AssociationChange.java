package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.AssociationChangeNotification;

public class AssociationChange extends AssociationChangeNotification implements SctpNotification {
   private static final int SCTP_COMM_UP = 1;
   private static final int SCTP_COMM_LOST = 2;
   private static final int SCTP_RESTART = 3;
   private static final int SCTP_SHUTDOWN = 4;
   private static final int SCTP_CANT_START = 5;
   private Association association;
   private int assocId;
   private AssociationChangeNotification.AssocChangeEvent event;
   private int maxOutStreams;
   private int maxInStreams;

   private AssociationChange(int var1, int var2, int var3, int var4) {
      switch(var2) {
      case 1:
         this.event = AssociationChangeNotification.AssocChangeEvent.COMM_UP;
         break;
      case 2:
         this.event = AssociationChangeNotification.AssocChangeEvent.COMM_LOST;
         break;
      case 3:
         this.event = AssociationChangeNotification.AssocChangeEvent.RESTART;
         break;
      case 4:
         this.event = AssociationChangeNotification.AssocChangeEvent.SHUTDOWN;
         break;
      case 5:
         this.event = AssociationChangeNotification.AssocChangeEvent.CANT_START;
         break;
      default:
         throw new AssertionError("Unknown Association Change Event type: " + var2);
      }

      this.assocId = var1;
      this.maxOutStreams = var3;
      this.maxInStreams = var4;
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

   public AssociationChangeNotification.AssocChangeEvent event() {
      return this.event;
   }

   int maxOutStreams() {
      return this.maxOutStreams;
   }

   int maxInStreams() {
      return this.maxInStreams;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString()).append(" [");
      var1.append("Association:").append((Object)this.association);
      var1.append(", Event: ").append((Object)this.event).append("]");
      return var1.toString();
   }
}
