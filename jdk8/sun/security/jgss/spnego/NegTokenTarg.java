package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenTarg extends SpNegoToken {
   private int negResult = 0;
   private Oid supportedMech = null;
   private byte[] responseToken = null;
   private byte[] mechListMIC = null;

   NegTokenTarg(int var1, Oid var2, byte[] var3, byte[] var4) {
      super(1);
      this.negResult = var1;
      this.supportedMech = var2;
      this.responseToken = var3;
      this.mechListMIC = var4;
   }

   public NegTokenTarg(byte[] var1) throws GSSException {
      super(1);
      this.parseToken(var1);
   }

   final byte[] encode() throws GSSException {
      try {
         DerOutputStream var1 = new DerOutputStream();
         DerOutputStream var2 = new DerOutputStream();
         var2.putEnumerated(this.negResult);
         var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
         DerOutputStream var3;
         if (this.supportedMech != null) {
            var3 = new DerOutputStream();
            byte[] var4 = this.supportedMech.getDER();
            var3.write(var4);
            var1.write(DerValue.createTag((byte)-128, true, (byte)1), var3);
         }

         if (this.responseToken != null) {
            var3 = new DerOutputStream();
            var3.putOctetString(this.responseToken);
            var1.write(DerValue.createTag((byte)-128, true, (byte)2), var3);
         }

         if (this.mechListMIC != null) {
            if (DEBUG) {
               System.out.println("SpNegoToken NegTokenTarg: sending MechListMIC");
            }

            var3 = new DerOutputStream();
            var3.putOctetString(this.mechListMIC);
            var1.write(DerValue.createTag((byte)-128, true, (byte)3), var3);
         } else if (GSSUtil.useMSInterop() && this.responseToken != null) {
            if (DEBUG) {
               System.out.println("SpNegoToken NegTokenTarg: sending additional token for MS Interop");
            }

            var3 = new DerOutputStream();
            var3.putOctetString(this.responseToken);
            var1.write(DerValue.createTag((byte)-128, true, (byte)3), var3);
         }

         var3 = new DerOutputStream();
         var3.write((byte)48, (DerOutputStream)var1);
         return var3.toByteArray();
      } catch (IOException var5) {
         throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + var5.getMessage());
      }
   }

   private void parseToken(byte[] var1) throws GSSException {
      try {
         DerValue var2 = new DerValue(var1);
         if (!var2.isContextSpecific((byte)1)) {
            throw new IOException("SPNEGO NegoTokenTarg : did not have the right token type");
         } else {
            DerValue var3 = var2.data.getDerValue();
            if (var3.tag != 48) {
               throw new IOException("SPNEGO NegoTokenTarg : did not have the Sequence tag");
            } else {
               int var4 = -1;

               while(var3.data.available() > 0) {
                  DerValue var5 = var3.data.getDerValue();
                  if (var5.isContextSpecific((byte)0)) {
                     var4 = checkNextField(var4, 0);
                     this.negResult = var5.data.getEnumerated();
                     if (DEBUG) {
                        System.out.println("SpNegoToken NegTokenTarg: negotiated result = " + getNegoResultString(this.negResult));
                     }
                  } else if (var5.isContextSpecific((byte)1)) {
                     var4 = checkNextField(var4, 1);
                     ObjectIdentifier var6 = var5.data.getOID();
                     this.supportedMech = new Oid(var6.toString());
                     if (DEBUG) {
                        System.out.println("SpNegoToken NegTokenTarg: supported mechanism = " + this.supportedMech);
                     }
                  } else if (var5.isContextSpecific((byte)2)) {
                     var4 = checkNextField(var4, 2);
                     this.responseToken = var5.data.getOctetString();
                  } else if (var5.isContextSpecific((byte)3)) {
                     var4 = checkNextField(var4, 3);
                     if (!GSSUtil.useMSInterop()) {
                        this.mechListMIC = var5.data.getOctetString();
                        if (DEBUG) {
                           System.out.println("SpNegoToken NegTokenTarg: MechListMIC Token = " + getHexBytes(this.mechListMIC));
                        }
                     }
                  }
               }

            }
         }
      } catch (IOException var7) {
         throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + var7.getMessage());
      }
   }

   int getNegotiatedResult() {
      return this.negResult;
   }

   public Oid getSupportedMech() {
      return this.supportedMech;
   }

   byte[] getResponseToken() {
      return this.responseToken;
   }

   byte[] getMechListMIC() {
      return this.mechListMIC;
   }
}
