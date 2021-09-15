package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import java.net.SocketAddress;

public class MessageInfoImpl extends MessageInfo {
   private final SocketAddress address;
   private final int bytes;
   private Association association;
   private int assocId;
   private int streamNumber;
   private boolean complete = true;
   private boolean unordered;
   private long timeToLive;
   private int ppid;

   public MessageInfoImpl(Association var1, SocketAddress var2, int var3) {
      this.association = var1;
      this.address = var2;
      this.streamNumber = var3;
      this.bytes = 0;
   }

   private MessageInfoImpl(int var1, SocketAddress var2, int var3, int var4, boolean var5, boolean var6, int var7) {
      this.assocId = var1;
      this.address = var2;
      this.bytes = var3;
      this.streamNumber = var4;
      this.complete = var5;
      this.unordered = var6;
      this.ppid = var7;
   }

   public Association association() {
      return this.association;
   }

   void setAssociation(Association var1) {
      this.association = var1;
   }

   int associationID() {
      return this.assocId;
   }

   public SocketAddress address() {
      return this.address;
   }

   public int bytes() {
      return this.bytes;
   }

   public int streamNumber() {
      return this.streamNumber;
   }

   public MessageInfo streamNumber(int var1) {
      if (var1 >= 0 && var1 <= 65536) {
         this.streamNumber = var1;
         return this;
      } else {
         throw new IllegalArgumentException("Invalid stream number");
      }
   }

   public int payloadProtocolID() {
      return this.ppid;
   }

   public MessageInfo payloadProtocolID(int var1) {
      this.ppid = var1;
      return this;
   }

   public boolean isComplete() {
      return this.complete;
   }

   public MessageInfo complete(boolean var1) {
      this.complete = var1;
      return this;
   }

   public boolean isUnordered() {
      return this.unordered;
   }

   public MessageInfo unordered(boolean var1) {
      this.unordered = var1;
      return this;
   }

   public long timeToLive() {
      return this.timeToLive;
   }

   public MessageInfo timeToLive(long var1) {
      this.timeToLive = var1;
      return this;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString());
      var1.append("[Address: ").append((Object)this.address).append(", Association: ").append((Object)this.association).append(", Assoc ID: ").append(this.assocId).append(", Bytes: ").append(this.bytes).append(", Stream Number: ").append(this.streamNumber).append(", Complete: ").append(this.complete).append(", isUnordered: ").append(this.unordered).append("]");
      return var1.toString();
   }
}
