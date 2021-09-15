package sun.nio.ch.sctp;

public class ResultContainer {
   static final int NOTHING = 0;
   static final int MESSAGE = 1;
   static final int SEND_FAILED = 2;
   static final int ASSOCIATION_CHANGED = 3;
   static final int PEER_ADDRESS_CHANGED = 4;
   static final int SHUTDOWN = 5;
   private Object value;
   private int type;

   int type() {
      return this.type;
   }

   boolean hasSomething() {
      return this.type() != 0;
   }

   boolean isNotification() {
      return this.type() != 1 && this.type() != 0;
   }

   void clear() {
      this.type = 0;
      this.value = null;
   }

   SctpNotification notification() {
      assert this.type() != 1 && this.type() != 0;

      return (SctpNotification)this.value;
   }

   MessageInfoImpl getMessageInfo() {
      assert this.type() == 1;

      return this.value instanceof MessageInfoImpl ? (MessageInfoImpl)this.value : null;
   }

   SendFailed getSendFailed() {
      assert this.type() == 2;

      return this.value instanceof SendFailed ? (SendFailed)this.value : null;
   }

   AssociationChange getAssociationChanged() {
      assert this.type() == 3;

      return this.value instanceof AssociationChange ? (AssociationChange)this.value : null;
   }

   PeerAddrChange getPeerAddressChanged() {
      assert this.type() == 4;

      return this.value instanceof PeerAddrChange ? (PeerAddrChange)this.value : null;
   }

   Shutdown getShutdown() {
      assert this.type() == 5;

      return this.value instanceof Shutdown ? (Shutdown)this.value : null;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Type: ");
      switch(this.type) {
      case 0:
         var1.append("NOTHING");
         break;
      case 1:
         var1.append("MESSAGE");
         break;
      case 2:
         var1.append("SEND FAILED");
         break;
      case 3:
         var1.append("ASSOCIATION CHANGE");
         break;
      case 4:
         var1.append("PEER ADDRESS CHANGE");
         break;
      case 5:
         var1.append("SHUTDOWN");
         break;
      default:
         var1.append("Unknown result type");
      }

      var1.append(", Value: ");
      var1.append(this.value == null ? "null" : this.value.toString());
      return var1.toString();
   }
}
