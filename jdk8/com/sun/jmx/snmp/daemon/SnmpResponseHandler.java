package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import java.net.DatagramPacket;
import java.util.logging.Level;

class SnmpResponseHandler {
   SnmpAdaptorServer adaptor = null;
   SnmpQManager snmpq = null;

   public SnmpResponseHandler(SnmpAdaptorServer var1, SnmpQManager var2) {
      this.adaptor = var1;
      this.snmpq = var2;
   }

   public synchronized void processDatagram(DatagramPacket var1) {
      byte[] var2 = var1.getData();
      int var3 = var1.getLength();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpResponseHandler.class.getName(), "action", (String)"processDatagram", (Object)("Received from " + var1.getAddress().toString() + " Length = " + var3 + "\nDump : \n" + SnmpMessage.dumpHexBuffer(var2, 0, var3)));
      }

      try {
         SnmpMessage var4 = new SnmpMessage();
         var4.decodeMessage(var2, var3);
         var4.address = var1.getAddress();
         var4.port = var1.getPort();
         SnmpPduFactory var5 = this.adaptor.getPduFactory();
         if (var5 == null) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find the pdu factory of the SNMP adaptor server");
            }
         } else {
            SnmpPduPacket var6 = (SnmpPduPacket)var5.decodeSnmpPdu(var4);
            if (var6 == null) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Pdu factory returned a null value");
               }
            } else if (var6 instanceof SnmpPduRequest) {
               SnmpPduRequest var7 = (SnmpPduRequest)var6;
               SnmpInformRequest var8 = this.snmpq.removeRequest((long)var7.requestId);
               if (var8 != null) {
                  var8.invokeOnResponse(var7);
               } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find corresponding for InformRequestId = " + var7.requestId);
               }
            } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. The packet does not contain an inform response");
            }

            var6 = null;
         }
      } catch (Exception var9) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", (String)"Exception while processsing", (Throwable)var9);
         }
      }

   }
}
