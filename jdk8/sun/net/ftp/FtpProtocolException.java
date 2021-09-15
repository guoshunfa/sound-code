package sun.net.ftp;

public class FtpProtocolException extends Exception {
   private static final long serialVersionUID = 5978077070276545054L;
   private final FtpReplyCode code;

   public FtpProtocolException(String var1) {
      super(var1);
      this.code = FtpReplyCode.UNKNOWN_ERROR;
   }

   public FtpProtocolException(String var1, FtpReplyCode var2) {
      super(var1);
      this.code = var2;
   }

   public FtpReplyCode getReplyCode() {
      return this.code;
   }
}
