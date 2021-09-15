package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Vector;

public abstract class SnmpMsg implements SnmpDefinitions {
   public int version = 0;
   public byte[] data = null;
   public int dataLength = 0;
   public InetAddress address = null;
   public int port = 0;
   public SnmpSecurityParameters securityParameters = null;

   public static int getProtocolVersion(byte[] var0) throws SnmpStatusException {
      boolean var1 = false;
      BerDecoder var2 = null;

      int var6;
      try {
         var2 = new BerDecoder(var0);
         var2.openSequence();
         var6 = var2.fetchInteger();
      } catch (BerException var5) {
         throw new SnmpStatusException("Invalid encoding");
      }

      try {
         var2.closeSequence();
      } catch (BerException var4) {
      }

      return var6;
   }

   public abstract int getRequestId(byte[] var1) throws SnmpStatusException;

   public abstract int encodeMessage(byte[] var1) throws SnmpTooBigException;

   public abstract void decodeMessage(byte[] var1, int var2) throws SnmpStatusException;

   public abstract void encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException;

   public abstract SnmpPdu decodeSnmpPdu() throws SnmpStatusException;

   public static String dumpHexBuffer(byte[] var0, int var1, int var2) {
      StringBuffer var3 = new StringBuffer(var2 << 1);
      int var4 = 1;
      int var5 = var1 + var2;

      for(int var6 = var1; var6 < var5; ++var6) {
         int var7 = var0[var6] & 255;
         var3.append(Character.forDigit(var7 >>> 4, 16));
         var3.append(Character.forDigit(var7 & 15, 16));
         ++var4;
         if (var4 % 16 == 0) {
            var3.append('\n');
            var4 = 1;
         } else {
            var3.append(' ');
         }
      }

      return var3.toString();
   }

   public String printMessage() {
      StringBuffer var1 = new StringBuffer();
      var1.append("Version: ");
      var1.append(this.version);
      var1.append("\n");
      if (this.data == null) {
         var1.append("Data: null");
      } else {
         var1.append("Data: {\n");
         var1.append(dumpHexBuffer(this.data, 0, this.dataLength));
         var1.append("\n}\n");
      }

      return var1.toString();
   }

   public void encodeVarBindList(BerEncoder var1, SnmpVarBind[] var2) throws SnmpStatusException, SnmpTooBigException {
      int var3 = 0;

      try {
         var1.openSequence();
         if (var2 != null) {
            for(int var4 = var2.length - 1; var4 >= 0; --var4) {
               SnmpVarBind var5 = var2[var4];
               if (var5 != null) {
                  var1.openSequence();
                  this.encodeVarBindValue(var1, var5.value);
                  var1.putOid(var5.oid.longValue());
                  var1.closeSequence();
                  ++var3;
               }
            }
         }

         var1.closeSequence();
      } catch (ArrayIndexOutOfBoundsException var6) {
         throw new SnmpTooBigException(var3);
      }
   }

   void encodeVarBindValue(BerEncoder var1, SnmpValue var2) throws SnmpStatusException {
      if (var2 == null) {
         var1.putNull();
      } else if (var2 instanceof SnmpIpAddress) {
         var1.putOctetString(((SnmpIpAddress)var2).byteValue(), 64);
      } else if (var2 instanceof SnmpCounter) {
         var1.putInteger(((SnmpCounter)var2).longValue(), 65);
      } else if (var2 instanceof SnmpGauge) {
         var1.putInteger(((SnmpGauge)var2).longValue(), 66);
      } else if (var2 instanceof SnmpTimeticks) {
         var1.putInteger(((SnmpTimeticks)var2).longValue(), 67);
      } else if (var2 instanceof SnmpOpaque) {
         var1.putOctetString(((SnmpOpaque)var2).byteValue(), 68);
      } else if (var2 instanceof SnmpInt) {
         var1.putInteger(((SnmpInt)var2).intValue());
      } else if (var2 instanceof SnmpString) {
         var1.putOctetString(((SnmpString)var2).byteValue());
      } else if (var2 instanceof SnmpOid) {
         var1.putOid(((SnmpOid)var2).longValue());
      } else if (var2 instanceof SnmpCounter64) {
         if (this.version == 0) {
            throw new SnmpStatusException("Invalid value for SNMP v1 : " + var2);
         }

         var1.putInteger(((SnmpCounter64)var2).longValue(), 70);
      } else {
         if (!(var2 instanceof SnmpNull)) {
            throw new SnmpStatusException("Invalid value " + var2);
         }

         int var3 = ((SnmpNull)var2).getTag();
         if (this.version == 0 && var3 != 5) {
            throw new SnmpStatusException("Invalid value for SNMP v1 : " + var2);
         }

         if (this.version == 1 && var3 != 5 && var3 != 128 && var3 != 129 && var3 != 130) {
            throw new SnmpStatusException("Invalid value " + var2);
         }

         var1.putNull(var3);
      }

   }

   public SnmpVarBind[] decodeVarBindList(BerDecoder var1) throws BerException {
      var1.openSequence();
      Vector var2 = new Vector();

      while(var1.cannotCloseSequence()) {
         SnmpVarBind var3 = new SnmpVarBind();
         var1.openSequence();
         var3.oid = new SnmpOid(var1.fetchOid());
         var3.setSnmpValue(this.decodeVarBindValue(var1));
         var1.closeSequence();
         var2.addElement(var3);
      }

      var1.closeSequence();
      SnmpVarBind[] var4 = new SnmpVarBind[var2.size()];
      var2.copyInto(var4);
      return var4;
   }

   SnmpValue decodeVarBindValue(BerDecoder var1) throws BerException {
      Object var2 = null;
      int var3 = var1.getTag();
      switch(var3) {
      case 2:
         try {
            var2 = new SnmpInt(var1.fetchInteger());
            break;
         } catch (RuntimeException var14) {
            throw new BerException();
         }
      case 4:
         try {
            var2 = new SnmpString(var1.fetchOctetString());
            break;
         } catch (RuntimeException var13) {
            throw new BerException();
         }
      case 5:
         var1.fetchNull();

         try {
            var2 = new SnmpNull();
            break;
         } catch (RuntimeException var11) {
            throw new BerException();
         }
      case 6:
         try {
            var2 = new SnmpOid(var1.fetchOid());
            break;
         } catch (RuntimeException var12) {
            throw new BerException();
         }
      case 64:
         try {
            var2 = new SnmpIpAddress(var1.fetchOctetString(var3));
            break;
         } catch (RuntimeException var10) {
            throw new BerException();
         }
      case 65:
         try {
            var2 = new SnmpCounter(var1.fetchIntegerAsLong(var3));
            break;
         } catch (RuntimeException var9) {
            throw new BerException();
         }
      case 66:
         try {
            var2 = new SnmpGauge(var1.fetchIntegerAsLong(var3));
            break;
         } catch (RuntimeException var8) {
            throw new BerException();
         }
      case 67:
         try {
            var2 = new SnmpTimeticks(var1.fetchIntegerAsLong(var3));
            break;
         } catch (RuntimeException var7) {
            throw new BerException();
         }
      case 68:
         try {
            var2 = new SnmpOpaque(var1.fetchOctetString(var3));
            break;
         } catch (RuntimeException var6) {
            throw new BerException();
         }
      case 70:
         if (this.version == 0) {
            throw new BerException(1);
         }

         try {
            var2 = new SnmpCounter64(var1.fetchIntegerAsLong(var3));
            break;
         } catch (RuntimeException var5) {
            throw new BerException();
         }
      case 128:
         if (this.version == 0) {
            throw new BerException(1);
         }

         var1.fetchNull(var3);
         var2 = SnmpVarBind.noSuchObject;
         break;
      case 129:
         if (this.version == 0) {
            throw new BerException(1);
         }

         var1.fetchNull(var3);
         var2 = SnmpVarBind.noSuchInstance;
         break;
      case 130:
         if (this.version == 0) {
            throw new BerException(1);
         }

         var1.fetchNull(var3);
         var2 = SnmpVarBind.endOfMibView;
         break;
      default:
         throw new BerException();
      }

      return (SnmpValue)var2;
   }
}
