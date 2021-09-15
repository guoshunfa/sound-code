package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

public class SnmpV3Message extends SnmpMsg {
   public int msgId = 0;
   public int msgMaxSize = 0;
   public byte msgFlags = 0;
   public int msgSecurityModel = 0;
   public byte[] msgSecurityParameters = null;
   public byte[] contextEngineId = null;
   public byte[] contextName = null;
   public byte[] encryptedPdu = null;

   public int encodeMessage(byte[] var1) throws SnmpTooBigException {
      boolean var2 = false;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeMessage", "Can't encode directly V3Message! Need a SecuritySubSystem");
      }

      throw new IllegalArgumentException("Can't encode");
   }

   public void decodeMessage(byte[] var1, int var2) throws SnmpStatusException {
      try {
         BerDecoder var3 = new BerDecoder(var1);
         var3.openSequence();
         this.version = var3.fetchInteger();
         var3.openSequence();
         this.msgId = var3.fetchInteger();
         this.msgMaxSize = var3.fetchInteger();
         this.msgFlags = var3.fetchOctetString()[0];
         this.msgSecurityModel = var3.fetchInteger();
         var3.closeSequence();
         this.msgSecurityParameters = var3.fetchOctetString();
         if ((this.msgFlags & 2) == 0) {
            var3.openSequence();
            this.contextEngineId = var3.fetchOctetString();
            this.contextName = var3.fetchOctetString();
            this.data = var3.fetchAny();
            this.dataLength = this.data.length;
            var3.closeSequence();
         } else {
            this.encryptedPdu = var3.fetchOctetString();
         }

         var3.closeSequence();
      } catch (BerException var4) {
         var4.printStackTrace();
         throw new SnmpStatusException("Invalid encoding");
      }

      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var5 = (new StringBuilder()).append("Unmarshalled message : \n").append("version : ").append(this.version).append("\n").append("msgId : ").append(this.msgId).append("\n").append("msgMaxSize : ").append(this.msgMaxSize).append("\n").append("msgFlags : ").append((int)this.msgFlags).append("\n").append("msgSecurityModel : ").append(this.msgSecurityModel).append("\n").append("contextEngineId : ").append((Object)(this.contextEngineId == null ? null : SnmpEngineId.createEngineId(this.contextEngineId))).append("\n").append("contextName : ").append((Object)this.contextName).append("\n").append("data : ").append((Object)this.data).append("\n").append("dat len : ").append(this.data == null ? 0 : this.data.length).append("\n").append("encryptedPdu : ").append((Object)this.encryptedPdu).append("\n");
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeMessage", var5.toString());
      }

   }

   public int getRequestId(byte[] var1) throws SnmpStatusException {
      BerDecoder var2 = null;
      boolean var3 = false;

      int var7;
      try {
         var2 = new BerDecoder(var1);
         var2.openSequence();
         var2.fetchInteger();
         var2.openSequence();
         var7 = var2.fetchInteger();
      } catch (BerException var6) {
         throw new SnmpStatusException("Invalid encoding");
      }

      try {
         var2.closeSequence();
      } catch (BerException var5) {
      }

      return var7;
   }

   public void encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException {
      SnmpScopedPduPacket var3 = (SnmpScopedPduPacket)var1;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var4 = (new StringBuilder()).append("PDU to marshall: \n").append("security parameters : ").append((Object)var3.securityParameters).append("\n").append("type : ").append(var3.type).append("\n").append("version : ").append(var3.version).append("\n").append("requestId : ").append(var3.requestId).append("\n").append("msgId : ").append(var3.msgId).append("\n").append("msgMaxSize : ").append(var3.msgMaxSize).append("\n").append("msgFlags : ").append((int)var3.msgFlags).append("\n").append("msgSecurityModel : ").append(var3.msgSecurityModel).append("\n").append("contextEngineId : ").append((Object)var3.contextEngineId).append("\n").append("contextName : ").append((Object)var3.contextName).append("\n");
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeSnmpPdu", var4.toString());
      }

      this.version = var3.version;
      this.address = var3.address;
      this.port = var3.port;
      this.msgId = var3.msgId;
      this.msgMaxSize = var3.msgMaxSize;
      this.msgFlags = var3.msgFlags;
      this.msgSecurityModel = var3.msgSecurityModel;
      this.contextEngineId = var3.contextEngineId;
      this.contextName = var3.contextName;
      this.securityParameters = var3.securityParameters;
      this.data = new byte[var2];

      try {
         BerEncoder var8 = new BerEncoder(this.data);
         var8.openSequence();
         this.encodeVarBindList(var8, var3.varBindList);
         switch(var3.type) {
         case 160:
         case 161:
         case 162:
         case 163:
         case 166:
         case 167:
         case 168:
            SnmpPduRequestType var5 = (SnmpPduRequestType)var3;
            var8.putInteger(var5.getErrorIndex());
            var8.putInteger(var5.getErrorStatus());
            var8.putInteger(var3.requestId);
            break;
         case 164:
         default:
            throw new SnmpStatusException("Invalid pdu type " + String.valueOf(var3.type));
         case 165:
            SnmpPduBulkType var6 = (SnmpPduBulkType)var3;
            var8.putInteger(var6.getMaxRepetitions());
            var8.putInteger(var6.getNonRepeaters());
            var8.putInteger(var3.requestId);
         }

         var8.closeSequence(var3.type);
         this.dataLength = var8.trim();
      } catch (ArrayIndexOutOfBoundsException var7) {
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
            SnmpScopedPduRequest var4 = new SnmpScopedPduRequest();
            var4.requestId = var2.fetchInteger();
            var4.setErrorStatus(var2.fetchInteger());
            var4.setErrorIndex(var2.fetchInteger());
            var1 = var4;
            break;
         case 164:
         default:
            throw new SnmpStatusException(9);
         case 165:
            SnmpScopedPduBulk var5 = new SnmpScopedPduBulk();
            var5.requestId = var2.fetchInteger();
            var5.setNonRepeaters(var2.fetchInteger());
            var5.setMaxRepetitions(var2.fetchInteger());
            var1 = var5;
         }

         ((SnmpScopedPduPacket)var1).type = var3;
         ((SnmpScopedPduPacket)var1).varBindList = this.decodeVarBindList(var2);
         var2.closeSequence();
      } catch (BerException var6) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpV3Message.class.getName(), "decodeSnmpPdu", (String)"BerException", (Throwable)var6);
         }

         throw new SnmpStatusException(9);
      }

      ((SnmpScopedPduPacket)var1).address = this.address;
      ((SnmpScopedPduPacket)var1).port = this.port;
      ((SnmpScopedPduPacket)var1).msgFlags = this.msgFlags;
      ((SnmpScopedPduPacket)var1).version = this.version;
      ((SnmpScopedPduPacket)var1).msgId = this.msgId;
      ((SnmpScopedPduPacket)var1).msgMaxSize = this.msgMaxSize;
      ((SnmpScopedPduPacket)var1).msgSecurityModel = this.msgSecurityModel;
      ((SnmpScopedPduPacket)var1).contextEngineId = this.contextEngineId;
      ((SnmpScopedPduPacket)var1).contextName = this.contextName;
      ((SnmpScopedPduPacket)var1).securityParameters = this.securityParameters;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var7 = (new StringBuilder()).append("Unmarshalled PDU : \n").append("type : ").append(((SnmpScopedPduPacket)var1).type).append("\n").append("version : ").append(((SnmpScopedPduPacket)var1).version).append("\n").append("requestId : ").append(((SnmpScopedPduPacket)var1).requestId).append("\n").append("msgId : ").append(((SnmpScopedPduPacket)var1).msgId).append("\n").append("msgMaxSize : ").append(((SnmpScopedPduPacket)var1).msgMaxSize).append("\n").append("msgFlags : ").append((int)((SnmpScopedPduPacket)var1).msgFlags).append("\n").append("msgSecurityModel : ").append(((SnmpScopedPduPacket)var1).msgSecurityModel).append("\n").append("contextEngineId : ").append((Object)((SnmpScopedPduPacket)var1).contextEngineId).append("\n").append("contextName : ").append((Object)((SnmpScopedPduPacket)var1).contextName).append("\n");
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeSnmpPdu", var7.toString());
      }

      return (SnmpPdu)var1;
   }

   public String printMessage() {
      StringBuffer var1 = new StringBuffer();
      var1.append("msgId : " + this.msgId + "\n");
      var1.append("msgMaxSize : " + this.msgMaxSize + "\n");
      var1.append("msgFlags : " + this.msgFlags + "\n");
      var1.append("msgSecurityModel : " + this.msgSecurityModel + "\n");
      if (this.contextEngineId == null) {
         var1.append("contextEngineId : null");
      } else {
         var1.append("contextEngineId : {\n");
         var1.append(dumpHexBuffer(this.contextEngineId, 0, this.contextEngineId.length));
         var1.append("\n}\n");
      }

      if (this.contextName == null) {
         var1.append("contextName : null");
      } else {
         var1.append("contextName : {\n");
         var1.append(dumpHexBuffer(this.contextName, 0, this.contextName.length));
         var1.append("\n}\n");
      }

      return var1.append(super.printMessage()).toString();
   }
}
