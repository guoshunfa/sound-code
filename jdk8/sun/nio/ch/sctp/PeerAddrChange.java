package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import java.net.SocketAddress;

public class PeerAddrChange extends PeerAddressChangeNotification implements SctpNotification {
   private static final int SCTP_ADDR_AVAILABLE = 1;
   private static final int SCTP_ADDR_UNREACHABLE = 2;
   private static final int SCTP_ADDR_REMOVED = 3;
   private static final int SCTP_ADDR_ADDED = 4;
   private static final int SCTP_ADDR_MADE_PRIM = 5;
   private static final int SCTP_ADDR_CONFIRMED = 6;
   private Association association;
   private int assocId;
   private SocketAddress address;
   private PeerAddressChangeNotification.AddressChangeEvent event;

   private PeerAddrChange(int var1, SocketAddress var2, int var3) {
      switch(var3) {
      case 1:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_AVAILABLE;
         break;
      case 2:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_UNREACHABLE;
         break;
      case 3:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_REMOVED;
         break;
      case 4:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_ADDED;
         break;
      case 5:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_MADE_PRIMARY;
         break;
      case 6:
         this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_CONFIRMED;
         break;
      default:
         throw new AssertionError("Unknown event type");
      }

      this.assocId = var1;
      this.address = var2;
   }

   public int assocId() {
      return this.assocId;
   }

   public void setAssociation(Association var1) {
      this.association = var1;
   }

   public SocketAddress address() {
      assert this.address != null;

      return this.address;
   }

   public Association association() {
      assert this.association != null;

      return this.association;
   }

   public PeerAddressChangeNotification.AddressChangeEvent event() {
      assert this.event != null;

      return this.event;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString()).append(" [");
      var1.append("Address: ").append((Object)this.address);
      var1.append(", Association:").append((Object)this.association);
      var1.append(", Event: ").append((Object)this.event).append("]");
      return var1.toString();
   }
}
