package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenInit extends SpNegoToken {
   private byte[] mechTypes = null;
   private Oid[] mechTypeList = null;
   private BitArray reqFlags = null;
   private byte[] mechToken = null;
   private byte[] mechListMIC = null;

   NegTokenInit(byte[] var1, BitArray var2, byte[] var3, byte[] var4) {
      super(0);
      this.mechTypes = var1;
      this.reqFlags = var2;
      this.mechToken = var3;
      this.mechListMIC = var4;
   }

   public NegTokenInit(byte[] var1) throws GSSException {
      super(0);
      this.parseToken(var1);
   }

   final byte[] encode() throws GSSException {
      try {
         DerOutputStream var1 = new DerOutputStream();
         if (this.mechTypes != null) {
            var1.write(DerValue.createTag((byte)-128, true, (byte)0), this.mechTypes);
         }

         DerOutputStream var2;
         if (this.reqFlags != null) {
            var2 = new DerOutputStream();
            var2.putUnalignedBitString(this.reqFlags);
            var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
         }

         if (this.mechToken != null) {
            var2 = new DerOutputStream();
            var2.putOctetString(this.mechToken);
            var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
         }

         if (this.mechListMIC != null) {
            if (DEBUG) {
               System.out.println("SpNegoToken NegTokenInit: sending MechListMIC");
            }

            var2 = new DerOutputStream();
            var2.putOctetString(this.mechListMIC);
            var1.write(DerValue.createTag((byte)-128, true, (byte)3), var2);
         }

         var2 = new DerOutputStream();
         var2.write((byte)48, (DerOutputStream)var1);
         return var2.toByteArray();
      } catch (IOException var3) {
         throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + var3.getMessage());
      }
   }

   private void parseToken(byte[] var1) throws GSSException {
      try {
         DerValue var2 = new DerValue(var1);
         if (!var2.isContextSpecific((byte)0)) {
            throw new IOException("SPNEGO NegoTokenInit : did not have right token type");
         } else {
            DerValue var3 = var2.data.getDerValue();
            if (var3.tag != 48) {
               throw new IOException("SPNEGO NegoTokenInit : did not have the Sequence tag");
            } else {
               int var4 = -1;

               while(true) {
                  while(var3.data.available() > 0) {
                     DerValue var5 = var3.data.getDerValue();
                     if (var5.isContextSpecific((byte)0)) {
                        var4 = checkNextField(var4, 0);
                        DerInputStream var6 = var5.data;
                        this.mechTypes = var6.toByteArray();
                        DerValue[] var7 = var6.getSequence(0);
                        this.mechTypeList = new Oid[var7.length];
                        ObjectIdentifier var8 = null;

                        for(int var9 = 0; var9 < var7.length; ++var9) {
                           var8 = var7[var9].getOID();
                           if (DEBUG) {
                              System.out.println("SpNegoToken NegTokenInit: reading Mechanism Oid = " + var8);
                           }

                           this.mechTypeList[var9] = new Oid(var8.toString());
                        }
                     } else if (var5.isContextSpecific((byte)1)) {
                        var4 = checkNextField(var4, 1);
                     } else if (var5.isContextSpecific((byte)2)) {
                        var4 = checkNextField(var4, 2);
                        if (DEBUG) {
                           System.out.println("SpNegoToken NegTokenInit: reading Mech Token");
                        }

                        this.mechToken = var5.data.getOctetString();
                     } else if (var5.isContextSpecific((byte)3)) {
                        var4 = checkNextField(var4, 3);
                        if (!GSSUtil.useMSInterop()) {
                           this.mechListMIC = var5.data.getOctetString();
                           if (DEBUG) {
                              System.out.println("SpNegoToken NegTokenInit: MechListMIC Token = " + getHexBytes(this.mechListMIC));
                           }
                        }
                     }
                  }

                  return;
               }
            }
         }
      } catch (IOException var10) {
         throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + var10.getMessage());
      }
   }

   byte[] getMechTypes() {
      return this.mechTypes;
   }

   public Oid[] getMechTypeList() {
      return this.mechTypeList;
   }

   BitArray getReqFlags() {
      return this.reqFlags;
   }

   public byte[] getMechToken() {
      return this.mechToken;
   }

   byte[] getMechListMIC() {
      return this.mechListMIC;
   }
}
