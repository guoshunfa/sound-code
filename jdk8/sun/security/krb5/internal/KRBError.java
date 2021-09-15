package sun.security.krb5.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBError implements Serializable {
   static final long serialVersionUID = 3643809337475284503L;
   private int pvno;
   private int msgType;
   private KerberosTime cTime;
   private Integer cuSec;
   private KerberosTime sTime;
   private Integer suSec;
   private int errorCode;
   private PrincipalName cname;
   private PrincipalName sname;
   private String eText;
   private byte[] eData;
   private Checksum eCksum;
   private PAData[] pa;
   private static boolean DEBUG;

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      try {
         this.init(new DerValue((byte[])((byte[])var1.readObject())));
         this.parseEData(this.eData);
      } catch (Exception var3) {
         throw new IOException(var3);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      try {
         var1.writeObject(this.asn1Encode());
      } catch (Exception var3) {
         throw new IOException(var3);
      }
   }

   public KRBError(APOptions var1, KerberosTime var2, Integer var3, KerberosTime var4, Integer var5, int var6, PrincipalName var7, PrincipalName var8, String var9, byte[] var10) throws IOException, Asn1Exception {
      this.pvno = 5;
      this.msgType = 30;
      this.cTime = var2;
      this.cuSec = var3;
      this.sTime = var4;
      this.suSec = var5;
      this.errorCode = var6;
      this.cname = var7;
      this.sname = var8;
      this.eText = var9;
      this.eData = var10;
      this.parseEData(this.eData);
   }

   public KRBError(APOptions var1, KerberosTime var2, Integer var3, KerberosTime var4, Integer var5, int var6, PrincipalName var7, PrincipalName var8, String var9, byte[] var10, Checksum var11) throws IOException, Asn1Exception {
      this.pvno = 5;
      this.msgType = 30;
      this.cTime = var2;
      this.cuSec = var3;
      this.sTime = var4;
      this.suSec = var5;
      this.errorCode = var6;
      this.cname = var7;
      this.sname = var8;
      this.eText = var9;
      this.eData = var10;
      this.eCksum = var11;
      this.parseEData(this.eData);
   }

   public KRBError(byte[] var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(new DerValue(var1));
      this.parseEData(this.eData);
   }

   public KRBError(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1);
      this.showDebug();
      this.parseEData(this.eData);
   }

   private void parseEData(byte[] var1) throws IOException {
      if (var1 != null) {
         if (this.errorCode != 25 && this.errorCode != 24) {
            if (DEBUG) {
               System.out.println("Unknown eData field of KRB-ERROR:\n" + (new HexDumpEncoder()).encodeBuffer(var1));
            }
         } else {
            try {
               this.parsePAData(var1);
            } catch (Exception var4) {
               if (DEBUG) {
                  System.out.println("Unable to parse eData field of KRB-ERROR:\n" + (new HexDumpEncoder()).encodeBuffer(var1));
               }

               IOException var3 = new IOException("Unable to parse eData field of KRB-ERROR");
               var3.initCause(var4);
               throw var3;
            }
         }

      }
   }

   private void parsePAData(byte[] var1) throws IOException, Asn1Exception {
      DerValue var2 = new DerValue(var1);
      ArrayList var3 = new ArrayList();

      while(var2.data.available() > 0) {
         DerValue var4 = var2.data.getDerValue();
         PAData var5 = new PAData(var4);
         var3.add(var5);
         if (DEBUG) {
            System.out.println((Object)var5);
         }
      }

      this.pa = (PAData[])var3.toArray(new PAData[var3.size()]);
   }

   public final KerberosTime getServerTime() {
      return this.sTime;
   }

   public final KerberosTime getClientTime() {
      return this.cTime;
   }

   public final Integer getServerMicroSeconds() {
      return this.suSec;
   }

   public final Integer getClientMicroSeconds() {
      return this.cuSec;
   }

   public final int getErrorCode() {
      return this.errorCode;
   }

   public final PAData[] getPA() {
      return this.pa;
   }

   public final String getErrorString() {
      return this.eText;
   }

   private void init(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      if ((var1.getTag() & 31) == 30 && var1.isApplication() && var1.isConstructed()) {
         DerValue var2 = var1.getData().getDerValue();
         if (var2.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var2.getData().getDerValue();
            if ((var3.getTag() & 31) == 0) {
               this.pvno = var3.getData().getBigInteger().intValue();
               if (this.pvno != 5) {
                  throw new KrbApErrException(39);
               } else {
                  var3 = var2.getData().getDerValue();
                  if ((var3.getTag() & 31) == 1) {
                     this.msgType = var3.getData().getBigInteger().intValue();
                     if (this.msgType != 30) {
                        throw new KrbApErrException(40);
                     } else {
                        this.cTime = KerberosTime.parse(var2.getData(), (byte)2, true);
                        if ((var2.getData().peekByte() & 31) == 3) {
                           var3 = var2.getData().getDerValue();
                           this.cuSec = new Integer(var3.getData().getBigInteger().intValue());
                        } else {
                           this.cuSec = null;
                        }

                        this.sTime = KerberosTime.parse(var2.getData(), (byte)4, false);
                        var3 = var2.getData().getDerValue();
                        if ((var3.getTag() & 31) == 5) {
                           this.suSec = new Integer(var3.getData().getBigInteger().intValue());
                           var3 = var2.getData().getDerValue();
                           if ((var3.getTag() & 31) == 6) {
                              this.errorCode = var3.getData().getBigInteger().intValue();
                              Realm var4 = Realm.parse(var2.getData(), (byte)7, true);
                              this.cname = PrincipalName.parse(var2.getData(), (byte)8, true, var4);
                              Realm var5 = Realm.parse(var2.getData(), (byte)9, false);
                              this.sname = PrincipalName.parse(var2.getData(), (byte)10, false, var5);
                              this.eText = null;
                              this.eData = null;
                              this.eCksum = null;
                              if (var2.getData().available() > 0 && (var2.getData().peekByte() & 31) == 11) {
                                 var3 = var2.getData().getDerValue();
                                 this.eText = (new KerberosString(var3.getData().getDerValue())).toString();
                              }

                              if (var2.getData().available() > 0 && (var2.getData().peekByte() & 31) == 12) {
                                 var3 = var2.getData().getDerValue();
                                 this.eData = var3.getData().getOctetString();
                              }

                              if (var2.getData().available() > 0) {
                                 this.eCksum = Checksum.parse(var2.getData(), (byte)13, true);
                              }

                              if (var2.getData().available() > 0) {
                                 throw new Asn1Exception(906);
                              }
                           } else {
                              throw new Asn1Exception(906);
                           }
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
      } else {
         throw new Asn1Exception(906);
      }
   }

   private void showDebug() {
      if (DEBUG) {
         System.out.println(">>>KRBError:");
         if (this.cTime != null) {
            System.out.println("\t cTime is " + this.cTime.toDate().toString() + " " + this.cTime.toDate().getTime());
         }

         if (this.cuSec != null) {
            System.out.println("\t cuSec is " + this.cuSec);
         }

         System.out.println("\t sTime is " + this.sTime.toDate().toString() + " " + this.sTime.toDate().getTime());
         System.out.println("\t suSec is " + this.suSec);
         System.out.println("\t error code is " + this.errorCode);
         System.out.println("\t error Message is " + Krb5.getErrorMessage(this.errorCode));
         if (this.cname != null) {
            System.out.println("\t cname is " + this.cname.toString());
         }

         if (this.sname != null) {
            System.out.println("\t sname is " + this.sname.toString());
         }

         if (this.eData != null) {
            System.out.println("\t eData provided.");
         }

         if (this.eCksum != null) {
            System.out.println("\t checksum provided.");
         }

         System.out.println("\t msgType is " + this.msgType);
      }

   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.pvno));
      var2.write(DerValue.createTag((byte)-128, true, (byte)0), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.msgType));
      var2.write(DerValue.createTag((byte)-128, true, (byte)1), var1);
      if (this.cTime != null) {
         var2.write(DerValue.createTag((byte)-128, true, (byte)2), this.cTime.asn1Encode());
      }

      if (this.cuSec != null) {
         var1 = new DerOutputStream();
         var1.putInteger(BigInteger.valueOf((long)this.cuSec));
         var2.write(DerValue.createTag((byte)-128, true, (byte)3), var1);
      }

      var2.write(DerValue.createTag((byte)-128, true, (byte)4), this.sTime.asn1Encode());
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.suSec));
      var2.write(DerValue.createTag((byte)-128, true, (byte)5), var1);
      var1 = new DerOutputStream();
      var1.putInteger(BigInteger.valueOf((long)this.errorCode));
      var2.write(DerValue.createTag((byte)-128, true, (byte)6), var1);
      if (this.cname != null) {
         var2.write(DerValue.createTag((byte)-128, true, (byte)7), this.cname.getRealm().asn1Encode());
         var2.write(DerValue.createTag((byte)-128, true, (byte)8), this.cname.asn1Encode());
      }

      var2.write(DerValue.createTag((byte)-128, true, (byte)9), this.sname.getRealm().asn1Encode());
      var2.write(DerValue.createTag((byte)-128, true, (byte)10), this.sname.asn1Encode());
      if (this.eText != null) {
         var1 = new DerOutputStream();
         var1.putDerValue((new KerberosString(this.eText)).toDerValue());
         var2.write(DerValue.createTag((byte)-128, true, (byte)11), var1);
      }

      if (this.eData != null) {
         var1 = new DerOutputStream();
         var1.putOctetString(this.eData);
         var2.write(DerValue.createTag((byte)-128, true, (byte)12), var1);
      }

      if (this.eCksum != null) {
         var2.write(DerValue.createTag((byte)-128, true, (byte)13), this.eCksum.asn1Encode());
      }

      var1 = new DerOutputStream();
      var1.write((byte)48, (DerOutputStream)var2);
      var2 = new DerOutputStream();
      var2.write(DerValue.createTag((byte)64, true, (byte)30), var1);
      return var2.toByteArray();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KRBError)) {
         return false;
      } else {
         KRBError var2 = (KRBError)var1;
         return this.pvno == var2.pvno && this.msgType == var2.msgType && isEqual(this.cTime, var2.cTime) && isEqual(this.cuSec, var2.cuSec) && isEqual(this.sTime, var2.sTime) && isEqual(this.suSec, var2.suSec) && this.errorCode == var2.errorCode && isEqual(this.cname, var2.cname) && isEqual(this.sname, var2.sname) && isEqual(this.eText, var2.eText) && Arrays.equals(this.eData, var2.eData) && isEqual(this.eCksum, var2.eCksum);
      }
   }

   private static boolean isEqual(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 37 * var1 + this.pvno;
      var2 = 37 * var2 + this.msgType;
      if (this.cTime != null) {
         var2 = 37 * var2 + this.cTime.hashCode();
      }

      if (this.cuSec != null) {
         var2 = 37 * var2 + this.cuSec.hashCode();
      }

      if (this.sTime != null) {
         var2 = 37 * var2 + this.sTime.hashCode();
      }

      if (this.suSec != null) {
         var2 = 37 * var2 + this.suSec.hashCode();
      }

      var2 = 37 * var2 + this.errorCode;
      if (this.cname != null) {
         var2 = 37 * var2 + this.cname.hashCode();
      }

      if (this.sname != null) {
         var2 = 37 * var2 + this.sname.hashCode();
      }

      if (this.eText != null) {
         var2 = 37 * var2 + this.eText.hashCode();
      }

      var2 = 37 * var2 + Arrays.hashCode(this.eData);
      if (this.eCksum != null) {
         var2 = 37 * var2 + this.eCksum.hashCode();
      }

      return var2;
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
