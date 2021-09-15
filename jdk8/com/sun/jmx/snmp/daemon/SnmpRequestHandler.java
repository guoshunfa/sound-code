package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduBulk;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class SnmpRequestHandler extends ClientHandler implements SnmpDefinitions {
   private transient DatagramSocket socket = null;
   private transient DatagramPacket packet = null;
   private transient Vector<SnmpMibAgent> mibs = null;
   private transient Hashtable<SnmpMibAgent, SnmpSubRequestHandler> subs = null;
   private transient SnmpMibTree root;
   private transient InetAddressAcl ipacl = null;
   private transient SnmpPduFactory pduFactory = null;
   private transient SnmpUserDataFactory userDataFactory = null;
   private transient SnmpAdaptorServer adaptor = null;
   private static final String InterruptSysCallMsg = "Interrupted system call";

   public SnmpRequestHandler(SnmpAdaptorServer var1, int var2, DatagramSocket var3, DatagramPacket var4, SnmpMibTree var5, Vector<SnmpMibAgent> var6, InetAddressAcl var7, SnmpPduFactory var8, SnmpUserDataFactory var9, MBeanServer var10, ObjectName var11) {
      super(var1, var2, var10, var11);
      this.adaptor = var1;
      this.socket = var3;
      this.packet = var4;
      this.root = var5;
      this.mibs = new Vector(var6);
      this.subs = new Hashtable(this.mibs.size());
      this.ipacl = var7;
      this.pduFactory = var8;
      this.userDataFactory = var9;
   }

   public void doRun() {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet received:\n" + SnmpMessage.dumpHexBuffer(this.packet.getData(), 0, this.packet.getLength()));
      }

      DatagramPacket var1 = this.makeResponsePacket(this.packet);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && var1 != null) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet to be sent:\n" + SnmpMessage.dumpHexBuffer(var1.getData(), 0, var1.getLength()));
      }

      if (var1 != null) {
         try {
            this.socket.send(var1);
         } catch (SocketException var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               if (var3.getMessage().equals("Interrupted system call")) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted");
               } else {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", (String)"I/O exception", (Throwable)var3);
               }
            }
         } catch (InterruptedIOException var4) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted");
            }
         } catch (Exception var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", (String)"failure when sending response", (Throwable)var5);
            }
         }
      }

   }

   private DatagramPacket makeResponsePacket(DatagramPacket var1) {
      DatagramPacket var2 = null;
      SnmpMessage var3 = new SnmpMessage();

      try {
         var3.decodeMessage(var1.getData(), var1.getLength());
         var3.address = var1.getAddress();
         var3.port = var1.getPort();
      } catch (SnmpStatusException var9) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", (String)"packet decoding failed", (Throwable)var9);
         }

         var3 = null;
         ((SnmpAdaptorServer)this.adaptorServer).incSnmpInASNParseErrs(1);
      }

      SnmpMessage var4 = null;
      if (var3 != null) {
         var4 = this.makeResponseMessage(var3);
      }

      if (var4 != null) {
         try {
            var1.setLength(var4.encodeMessage(var1.getData()));
            var2 = var1;
         } catch (SnmpTooBigException var8) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "response message is too big");
            }

            try {
               var4 = this.newTooBigMessage(var3);
               var1.setLength(var4.encodeMessage(var1.getData()));
               var2 = var1;
            } catch (SnmpTooBigException var7) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "'too big' is 'too big' !!!");
               }

               this.adaptor.incSnmpSilentDrops(1);
            }
         }
      }

      return var2;
   }

   private SnmpMessage makeResponseMessage(SnmpMessage var1) {
      SnmpMessage var2 = null;
      Object var4 = null;

      SnmpPduPacket var3;
      try {
         var3 = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(var1);
         if (var3 != null && this.userDataFactory != null) {
            var4 = this.userDataFactory.allocateUserData(var3);
         }
      } catch (SnmpStatusException var19) {
         var3 = null;
         SnmpAdaptorServer var6 = (SnmpAdaptorServer)this.adaptorServer;
         var6.incSnmpInASNParseErrs(1);
         if (var19.getStatus() == 243) {
            var6.incSnmpInBadVersions(1);
         }

         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", (String)"message decoding failed", (Throwable)var19);
         }
      }

      SnmpPduPacket var5 = null;
      if (var3 != null) {
         var5 = this.makeResponsePdu(var3, var4);

         try {
            if (this.userDataFactory != null) {
               this.userDataFactory.releaseUserData(var4, var5);
            }
         } catch (SnmpStatusException var10) {
            var5 = null;
         }
      }

      if (var5 != null) {
         try {
            var2 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var5, this.packet.getData().length);
         } catch (SnmpStatusException var17) {
            var2 = null;
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", (String)"failure when encoding the response message", (Throwable)var17);
            }
         } catch (SnmpTooBigException var18) {
            SnmpTooBigException var20 = var18;
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is too big");
            }

            try {
               if (this.packet.getData().length <= 32) {
                  throw var20;
               }

               int var7 = var20.getVarBindCount();
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + var7);
               }

               while(true) {
                  try {
                     var5 = this.reduceResponsePdu(var3, var5, var7);
                     var2 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var5, this.packet.getData().length - 32);
                     break;
                  } catch (SnmpTooBigException var13) {
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is still too big");
                     }

                     int var8 = var7;
                     var7 = var13.getVarBindCount();
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + var7);
                     }

                     if (var7 == var8) {
                        throw var13;
                     }
                  }
               }
            } catch (SnmpStatusException var14) {
               var2 = null;
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", (String)"failure when encoding the response message", (Throwable)var14);
               }
            } catch (SnmpTooBigException var15) {
               try {
                  var5 = this.newTooBigPdu(var3);
                  var2 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var5, this.packet.getData().length);
               } catch (SnmpTooBigException var11) {
                  var2 = null;
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "'too big' is 'too big' !!!");
                  }

                  this.adaptor.incSnmpSilentDrops(1);
               } catch (Exception var12) {
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", (String)"Got unexpected exception", (Throwable)var12);
                  }

                  var2 = null;
               }
            } catch (Exception var16) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", (String)"Got unexpected exception", (Throwable)var16);
               }

               var2 = null;
            }
         }
      }

      return var2;
   }

   private SnmpPduPacket makeResponsePdu(SnmpPduPacket var1, Object var2) {
      SnmpAdaptorServer var3 = (SnmpAdaptorServer)this.adaptorServer;
      SnmpPduPacket var4 = null;
      var3.updateRequestCounters(var1.type);
      if (var1.varBindList != null) {
         var3.updateVarCounters(var1.type, var1.varBindList.length);
      }

      if (this.checkPduType(var1)) {
         var4 = this.checkAcl(var1);
         if (var4 == null) {
            if (this.mibs.size() < 1) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeResponsePdu", "Request " + var1.requestId + " received but no MIB registered.");
               }

               return this.makeNoMibErrorPdu((SnmpPduRequest)var1, var2);
            }

            switch(var1.type) {
            case 160:
            case 161:
            case 163:
               var4 = this.makeGetSetResponsePdu((SnmpPduRequest)var1, var2);
            case 162:
            case 164:
            default:
               break;
            case 165:
               var4 = this.makeGetBulkResponsePdu((SnmpPduBulk)var1, var2);
            }
         } else {
            if (!var3.getAuthRespEnabled()) {
               var4 = null;
            }

            if (var3.getAuthTrapEnabled()) {
               try {
                  var3.snmpV1Trap(4, 0, new SnmpVarBindList());
               } catch (Exception var6) {
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePdu", (String)"Failure when sending authentication trap", (Throwable)var6);
                  }
               }
            }
         }
      }

      return var4;
   }

   SnmpPduPacket makeErrorVarbindPdu(SnmpPduPacket var1, int var2) {
      SnmpVarBind[] var3 = var1.varBindList;
      int var4 = var3.length;
      int var5;
      switch(var2) {
      case 128:
         for(var5 = 0; var5 < var4; ++var5) {
            var3[var5].value = SnmpVarBind.noSuchObject;
         }

         return this.newValidResponsePdu(var1, var3);
      case 129:
         for(var5 = 0; var5 < var4; ++var5) {
            var3[var5].value = SnmpVarBind.noSuchInstance;
         }

         return this.newValidResponsePdu(var1, var3);
      case 130:
         for(var5 = 0; var5 < var4; ++var5) {
            var3[var5].value = SnmpVarBind.endOfMibView;
         }

         return this.newValidResponsePdu(var1, var3);
      default:
         return this.newErrorResponsePdu(var1, 5, 1);
      }
   }

   SnmpPduPacket makeNoMibErrorPdu(SnmpPduRequest var1, Object var2) {
      if (var1.version == 0) {
         return this.newErrorResponsePdu(var1, 2, 1);
      } else {
         if (var1.version == 1) {
            switch(var1.type) {
            case 160:
               return this.makeErrorVarbindPdu(var1, 128);
            case 161:
            case 165:
               return this.makeErrorVarbindPdu(var1, 130);
            case 163:
            case 253:
               return this.newErrorResponsePdu(var1, 6, 1);
            }
         }

         return this.newErrorResponsePdu(var1, 5, 1);
      }
   }

   private SnmpPduPacket makeGetSetResponsePdu(SnmpPduRequest var1, Object var2) {
      if (var1.varBindList == null) {
         return this.newValidResponsePdu(var1, (SnmpVarBind[])null);
      } else {
         this.splitRequest(var1);
         int var3 = this.subs.size();
         if (var3 == 1) {
            return this.turboProcessingGetSet(var1, var2);
         } else {
            SnmpPduPacket var4 = this.executeSubRequest(var1, var2);
            if (var4 != null) {
               return var4;
            } else {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeGetSetResponsePdu", "Build the unified response for request " + var1.requestId);
               }

               return this.mergeResponses(var1);
            }
         }
      }
   }

   private SnmpPduPacket executeSubRequest(SnmpPduPacket var1, Object var2) {
      byte var3 = 0;
      int var4;
      Enumeration var5;
      SnmpSubRequestHandler var6;
      if (var1.type == 163) {
         var4 = 0;

         for(var5 = this.subs.elements(); var5.hasMoreElements(); ++var4) {
            var6 = (SnmpSubRequestHandler)var5.nextElement();
            var6.setUserData(var2);
            var6.type = 253;
            var6.run();
            var6.type = 163;
            if (var6.getErrorStatus() != 0) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs");
               }

               return this.newErrorResponsePdu(var1, var3, var6.getErrorIndex() + 1);
            }
         }
      }

      var4 = 0;

      for(var5 = this.subs.elements(); var5.hasMoreElements(); ++var4) {
         var6 = (SnmpSubRequestHandler)var5.nextElement();
         var6.setUserData(var2);
         var6.run();
         if (var6.getErrorStatus() != 0) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs");
            }

            return this.newErrorResponsePdu(var1, var3, var6.getErrorIndex() + 1);
         }
      }

      return null;
   }

   private SnmpPduPacket turboProcessingGetSet(SnmpPduRequest var1, Object var2) {
      SnmpSubRequestHandler var4 = (SnmpSubRequestHandler)this.subs.elements().nextElement();
      var4.setUserData(var2);
      int var3;
      if (var1.type == 163) {
         var4.type = 253;
         var4.run();
         var4.type = 163;
         var3 = var4.getErrorStatus();
         if (var3 != 0) {
            return this.newErrorResponsePdu(var1, var3, var4.getErrorIndex() + 1);
         }
      }

      var4.run();
      var3 = var4.getErrorStatus();
      if (var3 != 0) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "turboProcessingGetSet", "an error occurs");
         }

         int var5 = var4.getErrorIndex() + 1;
         return this.newErrorResponsePdu(var1, var3, var5);
      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "turboProcessingGetSet", "build the unified response for request " + var1.requestId);
         }

         return this.mergeResponses(var1);
      }
   }

   private SnmpPduPacket makeGetBulkResponsePdu(SnmpPduBulk var1, Object var2) {
      int var4 = var1.varBindList.length;
      int var5 = Math.max(Math.min(var1.nonRepeaters, var4), 0);
      int var6 = Math.max(var1.maxRepetitions, 0);
      int var7 = var4 - var5;
      if (var1.varBindList == null) {
         return this.newValidResponsePdu(var1, (SnmpVarBind[])null);
      } else {
         this.splitBulkRequest(var1, var5, var6, var7);
         SnmpPduPacket var8 = this.executeSubRequest(var1, var2);
         if (var8 != null) {
            return var8;
         } else {
            SnmpVarBind[] var3 = this.mergeBulkResponses(var5 + var6 * var7);

            int var10;
            for(var10 = var3.length; var10 > var5 && var3[var10 - 1].value.equals(SnmpVarBind.endOfMibView); --var10) {
            }

            int var9;
            if (var10 == var5) {
               var9 = var5 + var7;
            } else {
               var9 = var5 + ((var10 - 1 - var5) / var7 + 2) * var7;
            }

            if (var9 < var3.length) {
               SnmpVarBind[] var11 = new SnmpVarBind[var9];

               for(int var12 = 0; var12 < var9; ++var12) {
                  var11[var12] = var3[var12];
               }

               var3 = var11;
            }

            return this.newValidResponsePdu(var1, var3);
         }
      }
   }

   private boolean checkPduType(SnmpPduPacket var1) {
      boolean var2;
      switch(var1.type) {
      case 160:
      case 161:
      case 163:
      case 165:
         var2 = true;
         break;
      case 162:
      case 164:
      default:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "checkPduType", "cannot respond to this kind of PDU");
         }

         var2 = false;
      }

      return var2;
   }

   private SnmpPduPacket checkAcl(SnmpPduPacket var1) {
      SnmpPduRequest var2 = null;
      String var3 = new String(var1.community);
      if (this.ipacl != null) {
         int var4;
         if (var1.type == 163) {
            if (!this.ipacl.checkWritePermission(var1.address, var3)) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + var1.address + " with " + var3 + ". Sender has no write permission");
               }

               var4 = SnmpSubRequestHandler.mapErrorStatus(16, var1.version, var1.type);
               var2 = this.newErrorResponsePdu(var1, var4, 0);
            } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + var1.address + " with " + var3 + ". Sender has write permission");
            }
         } else if (!this.ipacl.checkReadPermission(var1.address, var3)) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + var1.address + " with " + var3 + ". Sender has no read permission");
            }

            var4 = SnmpSubRequestHandler.mapErrorStatus(16, var1.version, var1.type);
            var2 = this.newErrorResponsePdu(var1, var4, 0);
            SnmpAdaptorServer var5 = (SnmpAdaptorServer)this.adaptorServer;
            var5.updateErrorCounters(2);
         } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + var1.address + " with " + var3 + ". Sender has read permission");
         }
      }

      if (var2 != null) {
         SnmpAdaptorServer var6 = (SnmpAdaptorServer)this.adaptorServer;
         var6.incSnmpInBadCommunityUses(1);
         if (!this.ipacl.checkCommunity(var3)) {
            var6.incSnmpInBadCommunityNames(1);
         }
      }

      return var2;
   }

   private SnmpPduRequest newValidResponsePdu(SnmpPduPacket var1, SnmpVarBind[] var2) {
      SnmpPduRequest var3 = new SnmpPduRequest();
      var3.address = var1.address;
      var3.port = var1.port;
      var3.version = var1.version;
      var3.community = var1.community;
      var3.type = 162;
      var3.requestId = var1.requestId;
      var3.errorStatus = 0;
      var3.errorIndex = 0;
      var3.varBindList = var2;
      ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(var3.errorStatus);
      return var3;
   }

   private SnmpPduRequest newErrorResponsePdu(SnmpPduPacket var1, int var2, int var3) {
      SnmpPduRequest var4 = this.newValidResponsePdu(var1, (SnmpVarBind[])null);
      var4.errorStatus = var2;
      var4.errorIndex = var3;
      var4.varBindList = var1.varBindList;
      ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(var4.errorStatus);
      return var4;
   }

   private SnmpMessage newTooBigMessage(SnmpMessage var1) throws SnmpTooBigException {
      SnmpMessage var2 = null;

      try {
         SnmpPduPacket var3 = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(var1);
         if (var3 != null) {
            SnmpPduPacket var4 = this.newTooBigPdu(var3);
            var2 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var4, this.packet.getData().length);
         }

         return var2;
      } catch (SnmpStatusException var5) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "newTooBigMessage", (String)"Internal error", (Throwable)var5);
         }

         throw new InternalError(var5);
      }
   }

   private SnmpPduPacket newTooBigPdu(SnmpPduPacket var1) {
      SnmpPduRequest var2 = this.newErrorResponsePdu(var1, 1, 0);
      var2.varBindList = null;
      return var2;
   }

   private SnmpPduPacket reduceResponsePdu(SnmpPduPacket var1, SnmpPduPacket var2, int var3) throws SnmpTooBigException {
      if (var1.type != 165) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything");
         }

         throw new SnmpTooBigException(var3);
      } else {
         int var4;
         if (var3 >= 3) {
            var4 = Math.min(var3 - 1, var2.varBindList.length);
         } else if (var3 == 1) {
            var4 = 1;
         } else {
            var4 = var2.varBindList.length / 2;
         }

         if (var4 < 1) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything");
            }

            throw new SnmpTooBigException(var3);
         } else {
            SnmpVarBind[] var5 = new SnmpVarBind[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = var2.varBindList[var6];
            }

            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", var2.varBindList.length - var5.length + " items have been removed");
            }

            var2.varBindList = var5;
            return var2;
         }
      }
   }

   private void splitRequest(SnmpPduRequest var1) {
      int var2 = this.mibs.size();
      SnmpMibAgent var3 = (SnmpMibAgent)this.mibs.firstElement();
      if (var2 == 1) {
         this.subs.put(var3, new SnmpSubRequestHandler(var3, var1, true));
      } else if (var1.type != 161) {
         int var8 = var1.varBindList.length;
         SnmpVarBind[] var9 = var1.varBindList;

         for(int var7 = 0; var7 < var8; ++var7) {
            var3 = this.root.getAgentMib(var9[var7].oid);
            SnmpSubRequestHandler var6 = (SnmpSubRequestHandler)this.subs.get(var3);
            if (var6 == null) {
               var6 = new SnmpSubRequestHandler(var3, var1);
               this.subs.put(var3, var6);
            }

            var6.updateRequest(var9[var7], var7);
         }

      } else {
         Enumeration var4 = this.mibs.elements();

         while(var4.hasMoreElements()) {
            SnmpMibAgent var5 = (SnmpMibAgent)var4.nextElement();
            this.subs.put(var5, new SnmpSubNextRequestHandler(this.adaptor, var5, var1));
         }

      }
   }

   private void splitBulkRequest(SnmpPduBulk var1, int var2, int var3, int var4) {
      SnmpMibAgent var6;
      for(Enumeration var5 = this.mibs.elements(); var5.hasMoreElements(); this.subs.put(var6, new SnmpSubBulkRequestHandler(this.adaptor, var6, var1, var2, var3, var4))) {
         var6 = (SnmpMibAgent)var5.nextElement();
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "splitBulkRequest", "Create a sub with : " + var6 + " " + var2 + " " + var3 + " " + var4);
         }
      }

   }

   private SnmpPduPacket mergeResponses(SnmpPduRequest var1) {
      if (var1.type == 161) {
         return this.mergeNextResponses(var1);
      } else {
         SnmpVarBind[] var2 = var1.varBindList;
         Enumeration var3 = this.subs.elements();

         while(var3.hasMoreElements()) {
            SnmpSubRequestHandler var4 = (SnmpSubRequestHandler)var3.nextElement();
            var4.updateResult(var2);
         }

         return this.newValidResponsePdu(var1, var2);
      }
   }

   private SnmpPduPacket mergeNextResponses(SnmpPduRequest var1) {
      int var2 = var1.varBindList.length;
      SnmpVarBind[] var3 = new SnmpVarBind[var2];
      Enumeration var4 = this.subs.elements();

      while(var4.hasMoreElements()) {
         SnmpSubRequestHandler var5 = (SnmpSubRequestHandler)var4.nextElement();
         var5.updateResult(var3);
      }

      if (var1.version == 1) {
         return this.newValidResponsePdu(var1, var3);
      } else {
         for(int var6 = 0; var6 < var2; ++var6) {
            SnmpValue var7 = var3[var6].value;
            if (var7 == SnmpVarBind.endOfMibView) {
               return this.newErrorResponsePdu(var1, 2, var6 + 1);
            }
         }

         return this.newValidResponsePdu(var1, var3);
      }
   }

   private SnmpVarBind[] mergeBulkResponses(int var1) {
      SnmpVarBind[] var2 = new SnmpVarBind[var1];

      for(int var3 = var1 - 1; var3 >= 0; --var3) {
         var2[var3] = new SnmpVarBind();
         var2[var3].value = SnmpVarBind.endOfMibView;
      }

      Enumeration var5 = this.subs.elements();

      while(var5.hasMoreElements()) {
         SnmpSubRequestHandler var4 = (SnmpSubRequestHandler)var5.nextElement();
         var4.updateResult(var2);
      }

      return var2;
   }

   protected String makeDebugTag() {
      return "SnmpRequestHandler[" + this.adaptorServer.getProtocol() + ":" + this.adaptorServer.getPort() + "]";
   }

   Thread createThread(Runnable var1) {
      return null;
   }
}
