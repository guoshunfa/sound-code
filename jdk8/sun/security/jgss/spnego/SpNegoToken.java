package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSToken;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

abstract class SpNegoToken extends GSSToken {
   static final int NEG_TOKEN_INIT_ID = 0;
   static final int NEG_TOKEN_TARG_ID = 1;
   private int tokenType;
   static final boolean DEBUG;
   public static ObjectIdentifier OID;

   protected SpNegoToken(int var1) {
      this.tokenType = var1;
   }

   abstract byte[] encode() throws GSSException;

   byte[] getEncoded() throws IOException, GSSException {
      DerOutputStream var1 = new DerOutputStream();
      var1.write(this.encode());
      switch(this.tokenType) {
      case 0:
         DerOutputStream var2 = new DerOutputStream();
         var2.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
         return var2.toByteArray();
      case 1:
         DerOutputStream var3 = new DerOutputStream();
         var3.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
         return var3.toByteArray();
      default:
         return var1.toByteArray();
      }
   }

   final int getType() {
      return this.tokenType;
   }

   static String getTokenName(int var0) {
      switch(var0) {
      case 0:
         return "SPNEGO NegTokenInit";
      case 1:
         return "SPNEGO NegTokenTarg";
      default:
         return "SPNEGO Mechanism Token";
      }
   }

   static SpNegoToken.NegoResult getNegoResultType(int var0) {
      switch(var0) {
      case 0:
         return SpNegoToken.NegoResult.ACCEPT_COMPLETE;
      case 1:
         return SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
      case 2:
         return SpNegoToken.NegoResult.REJECT;
      default:
         return SpNegoToken.NegoResult.ACCEPT_COMPLETE;
      }
   }

   static String getNegoResultString(int var0) {
      switch(var0) {
      case 0:
         return "Accept Complete";
      case 1:
         return "Accept InComplete";
      case 2:
         return "Reject";
      default:
         return "Unknown Negotiated Result: " + var0;
      }
   }

   static int checkNextField(int var0, int var1) throws GSSException {
      if (var0 < var1) {
         return var1;
      } else {
         throw new GSSException(10, -1, "Invalid SpNegoToken token : wrong order");
      }
   }

   static {
      DEBUG = SpNegoContext.DEBUG;

      try {
         OID = new ObjectIdentifier(SpNegoMechFactory.GSS_SPNEGO_MECH_OID.toString());
      } catch (IOException var1) {
      }

   }

   static enum NegoResult {
      ACCEPT_COMPLETE,
      ACCEPT_INCOMPLETE,
      REJECT;
   }
}
