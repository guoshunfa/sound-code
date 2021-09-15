package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReq {
   public KDCReqBody reqBody;
   private int pvno;
   private int msgType;
   private PAData[] pAData = null;

   public KDCReq(PAData[] var1, KDCReqBody var2, int var3) throws IOException {
      this.pvno = 5;
      this.msgType = var3;
      if (var1 != null) {
         this.pAData = new PAData[var1.length];

         for(int var4 = 0; var4 < var1.length; ++var4) {
            if (var1[var4] == null) {
               throw new IOException("Cannot create a KDCRep");
            }

            this.pAData[var4] = (PAData)var1[var4].clone();
         }
      }

      this.reqBody = var2;
   }

   public KDCReq() {
   }

   public KDCReq(byte[] var1, int var2) throws Asn1Exception, IOException, KrbException {
      this.init(new DerValue(var1), var2);
   }

   public KDCReq(DerValue var1, int var2) throws Asn1Exception, IOException, KrbException {
      this.init(var1, var2);
   }

   protected void init(DerValue var1, int var2) throws Asn1Exception, IOException, KrbException {
      if ((var1.getTag() & 31) != var2) {
         throw new Asn1Exception(906);
      } else {
         DerValue var3 = var1.getData().getDerValue();
         if (var3.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            if ((var4.getTag() & 31) == 1) {
               BigInteger var5 = var4.getData().getBigInteger();
               this.pvno = var5.intValue();
               if (this.pvno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  var4 = var3.getData().getDerValue();
                  if ((var4.getTag() & 31) == 2) {
                     var5 = var4.getData().getBigInteger();
                     this.msgType = var5.intValue();
                     if (this.msgType != var2) {
                        throw new KrbApErrException(40);
                     } else {
                        DerValue var6;
                        if ((var3.getData().peekByte() & 31) == 3) {
                           var4 = var3.getData().getDerValue();
                           var6 = var4.getData().getDerValue();
                           if (var6.getTag() != 48) {
                              throw new Asn1Exception(906);
                           }

                           Vector var7 = new Vector();

                           while(var6.getData().available() > 0) {
                              var7.addElement(new PAData(var6.getData().getDerValue()));
                           }

                           if (var7.size() > 0) {
                              this.pAData = new PAData[var7.size()];
                              var7.copyInto(this.pAData);
                           }
                        } else {
                           this.pAData = null;
                        }

                        var4 = var3.getData().getDerValue();
                        if ((var4.getTag() & 31) == 4) {
                           var6 = var4.getData().getDerValue();
                           this.reqBody = new KDCReqBody(var6, this.msgType);
                        } else {
                           throw new Asn1Exception(906);
                        }
                     }
                  } else {
                     throw new Asn1Exception(906);
                  }
               }
            } else {
               throw new Asn1Exception(906);
            }
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.pvno));
      DerOutputStream var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.msgType));
      var3.write(DerValue.createTag((byte)-128, true, (byte)2), var1);
      DerOutputStream var2;
      if (this.pAData != null && this.pAData.length > 0) {
         var1 = new DerOutputStream();

         for(int var4 = 0; var4 < this.pAData.length; ++var4) {
            var1.write(this.pAData[var4].asn1Encode());
         }

         var2 = new DerOutputStream();
         var2.write((byte)48, (DerOutputStream)var1);
         var3.write(DerValue.createTag((byte)-128, true, (byte)3), var2);
      }

      var3.write(DerValue.createTag((byte)-128, true, (byte)4), this.reqBody.asn1Encode(this.msgType));
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var3);
      var3 = new DerOutputStream();
      var3.write(DerValue.createTag((byte)64, true, (byte)this.msgType), var2);
      return var3.toByteArray();
   }

   public byte[] asn1EncodeReqBody() throws Asn1Exception, IOException {
      return this.reqBody.asn1Encode(this.msgType);
   }
}
