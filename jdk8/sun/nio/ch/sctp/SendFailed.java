package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.SendFailedNotification;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class SendFailed extends SendFailedNotification implements SctpNotification {
   private Association association;
   private int assocId;
   private SocketAddress address;
   private ByteBuffer buffer;
   private int errorCode;
   private int streamNumber;

   private SendFailed(int var1, SocketAddress var2, ByteBuffer var3, int var4, int var5) {
      this.assocId = var1;
      this.errorCode = var4;
      this.streamNumber = var5;
      this.address = var2;
      this.buffer = var3;
   }

   public int assocId() {
      return this.assocId;
   }

   public void setAssociation(Association var1) {
      this.association = var1;
   }

   public Association association() {
      return this.association;
   }

   public SocketAddress address() {
      assert this.address != null;

      return this.address;
   }

   public ByteBuffer buffer() {
      assert this.buffer != null;

      return this.buffer;
   }

   public int errorCode() {
      return this.errorCode;
   }

   public int streamNumber() {
      return this.streamNumber;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString()).append(" [");
      var1.append("Association:").append((Object)this.association);
      var1.append(", Address: ").append((Object)this.address);
      var1.append(", buffer: ").append((Object)this.buffer);
      var1.append(", errorCode: ").append(this.errorCode);
      var1.append(", streamNumber: ").append(this.streamNumber);
      var1.append("]");
      return var1.toString();
   }
}
