package javax.net.ssl;

public class SSLEngineResult {
   private final SSLEngineResult.Status status;
   private final SSLEngineResult.HandshakeStatus handshakeStatus;
   private final int bytesConsumed;
   private final int bytesProduced;

   public SSLEngineResult(SSLEngineResult.Status var1, SSLEngineResult.HandshakeStatus var2, int var3, int var4) {
      if (var1 != null && var2 != null && var3 >= 0 && var4 >= 0) {
         this.status = var1;
         this.handshakeStatus = var2;
         this.bytesConsumed = var3;
         this.bytesProduced = var4;
      } else {
         throw new IllegalArgumentException("Invalid Parameter(s)");
      }
   }

   public final SSLEngineResult.Status getStatus() {
      return this.status;
   }

   public final SSLEngineResult.HandshakeStatus getHandshakeStatus() {
      return this.handshakeStatus;
   }

   public final int bytesConsumed() {
      return this.bytesConsumed;
   }

   public final int bytesProduced() {
      return this.bytesProduced;
   }

   public String toString() {
      return "Status = " + this.status + " HandshakeStatus = " + this.handshakeStatus + "\nbytesConsumed = " + this.bytesConsumed + " bytesProduced = " + this.bytesProduced;
   }

   public static enum HandshakeStatus {
      NOT_HANDSHAKING,
      FINISHED,
      NEED_TASK,
      NEED_WRAP,
      NEED_UNWRAP;
   }

   public static enum Status {
      BUFFER_UNDERFLOW,
      BUFFER_OVERFLOW,
      OK,
      CLOSED;
   }
}
