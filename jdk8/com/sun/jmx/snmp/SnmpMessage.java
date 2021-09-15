package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

public class SnmpMessage extends SnmpMsg implements SnmpDefinitions {
   public byte[] community;

   public int encodeMessage(byte[] var1) throws SnmpTooBigException {
      boolean var2 = false;
      if (this.data == null) {
         throw new IllegalArgumentException("Data field is null");
      } else {
         try {
            BerEncoder var3 = new BerEncoder(var1);
            var3.openSequence();
            var3.putAny(this.data, this.dataLength);
            var3.putOctetString(this.community != null ? this.community : new byte[0]);
            var3.putInteger(this.version);
            var3.closeSequence();
            int var5 = var3.trim();
            return var5;
         } catch (ArrayIndexOutOfBoundsException var4) {
            throw new SnmpTooBigException();
         }
      }
   }

   public int getRequestId(byte[] var1) throws SnmpStatusException {
      boolean var2 = false;
      BerDecoder var3 = null;
      BerDecoder var4 = null;
      Object var5 = null;

      int var10;
      try {
         var3 = new BerDecoder(var1);
         var3.openSequence();
         var3.fetchInteger();
         var3.fetchOctetString();
         byte[] var11 = var3.fetchAny();
         var4 = new BerDecoder(var11);
         int var6 = var4.getTag();
         var4.openSequence(var6);
         var10 = var4.fetchInteger();
      } catch (BerException var9) {
         throw new SnmpStatusException("Invalid encoding");
      }

      try {
         var3.closeSequence();
      } catch (BerException var8) {
      }

      try {
         var4.closeSequence();
      } catch (BerException var7) {
      }

      return var10;
   }

   public void decodeMessage(byte[] var1, int var2) throws SnmpStatusException {
      try {
         BerDecoder var3 = new BerDecoder(var1);
         var3.openSequence();
         this.version = var3.fetchInteger();
         this.community = var3.fetchOctetString();
         this.data = var3.fetchAny();
         this.dataLength = this.data.length;
         var3.closeSequence();
      } catch (BerException var4) {
         throw new SnmpStatusException("Invalid encoding");
      }
   }

   public void encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException {
      SnmpPduPacket var3 = (SnmpPduPacket)var1;
      this.version = var3.version;
      this.community = var3.community;
      this.address = var3.address;
      this.port = var3.port;
      this.data = new byte[var2];

      try {
         BerEncoder var4 = new BerEncoder(this.data);
         var4.openSequence();
         this.encodeVarBindList(var4, var3.varBindList);
         switch(var3.type) {
         case 160:
         case 161:
         case 162:
         case 163:
         case 166:
         case 167:
         case 168:
            SnmpPduRequest var5 = (SnmpPduRequest)var3;
            var4.putInteger(var5.errorIndex);
            var4.putInteger(var5.errorStatus);
            var4.putInteger(var5.requestId);
            break;
         case 164:
            SnmpPduTrap var7 = (SnmpPduTrap)var3;
            var4.putInteger(var7.timeStamp, 67);
            var4.putInteger(var7.specificTrap);
            var4.putInteger(var7.genericTrap);
            if (var7.agentAddr != null) {
               var4.putOctetString(var7.agentAddr.byteValue(), 64);
            } else {
               var4.putOctetString(new byte[0], 64);
            }

            var4.putOid(var7.enterprise.longValue());
            break;
         case 165:
            SnmpPduBulk var6 = (SnmpPduBulk)var3;
            var4.putInteger(var6.maxRepetitions);
            var4.putInteger(var6.nonRepeaters);
            var4.putInteger(var6.requestId);
            break;
         default:
            throw new SnmpStatusException("Invalid pdu type " + String.valueOf(var3.type));
         }

         var4.closeSequence(var3.type);
         this.dataLength = var4.trim();
      } catch (ArrayIndexOutOfBoundsException var8) {
         throw new SnmpTooBigException();
      }
   }

   public SnmpPdu decodeSnmpPdu() throws SnmpStatusException {
      Object var1 = null;
      BerDecoder var2 = new BerDecoder(this.data);

      try {
         int var3 = var2.getTag();
         var2.openSequence(var3);
         switch(var3) {
         case 160:
         case 161:
         case 162:
         case 163:
         case 166:
         case 167:
         case 168:
            SnmpPduRequest var4 = new SnmpPduRequest();
            var4.requestId = var2.fetchInteger();
            var4.errorStatus = var2.fetchInteger();
            var4.errorIndex = var2.fetchInteger();
            var1 = var4;
            break;
         case 164:
            SnmpPduTrap var6 = new SnmpPduTrap();
            var6.enterprise = new SnmpOid(var2.fetchOid());
            byte[] var7 = var2.fetchOctetString(64);
            if (var7.length != 0) {
               var6.agentAddr = new SnmpIpAddress(var7);
            } else {
               var6.agentAddr = null;
            }

            var6.genericTrap = var2.fetchInteger();
            var6.specificTrap = var2.fetchInteger();
            var6.timeStamp = (long)var2.fetchInteger(67);
            var1 = var6;
            break;
         case 165:
            SnmpPduBulk var5 = new SnmpPduBulk();
            var5.requestId = var2.fetchInteger();
            var5.nonRepeaters = var2.fetchInteger();
            var5.maxRepetitions = var2.fetchInteger();
            var1 = var5;
            break;
         default:
            throw new SnmpStatusException(9);
         }

         ((SnmpPduPacket)var1).type = var3;
         ((SnmpPduPacket)var1).varBindList = this.decodeVarBindList(var2);
         var2.closeSequence();
      } catch (BerException var8) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", (String)"BerException", (Throwable)var8);
         }

         throw new SnmpStatusException(9);
      } catch (IllegalArgumentException var9) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", (String)"IllegalArgumentException", (Throwable)var9);
         }

         throw new SnmpStatusException(9);
      }

      ((SnmpPduPacket)var1).version = this.version;
      ((SnmpPduPacket)var1).community = this.community;
      ((SnmpPduPacket)var1).address = this.address;
      ((SnmpPduPacket)var1).port = this.port;
      return (SnmpPdu)var1;
   }

   public String printMessage() {
      StringBuffer var1 = new StringBuffer();
      if (this.community == null) {
         var1.append("Community: null");
      } else {
         var1.append("Community: {\n");
         var1.append(dumpHexBuffer(this.community, 0, this.community.length));
         var1.append("\n}\n");
      }

      return var1.append(super.printMessage()).toString();
   }
}
