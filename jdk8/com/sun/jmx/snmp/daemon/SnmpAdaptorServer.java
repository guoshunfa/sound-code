package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpParameters;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduFactoryBER;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduTrap;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.IPAcl.SnmpAcl;
import com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import com.sun.jmx.snmp.tasks.ThreadService;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpAdaptorServer extends CommunicatorServer implements SnmpAdaptorServerMBean, MBeanRegistration, SnmpDefinitions, SnmpMibHandler {
   private int trapPort;
   private int informPort;
   InetAddress address;
   private InetAddressAcl ipacl;
   private SnmpPduFactory pduFactory;
   private SnmpUserDataFactory userDataFactory;
   private boolean authRespEnabled;
   private boolean authTrapEnabled;
   private SnmpOid enterpriseOid;
   int bufferSize;
   private transient long startUpTime;
   private transient DatagramSocket socket;
   transient DatagramSocket trapSocket;
   private transient SnmpSession informSession;
   private transient DatagramPacket packet;
   transient Vector<SnmpMibAgent> mibs;
   private transient SnmpMibTree root;
   private transient boolean useAcl;
   private int maxTries;
   private int timeout;
   int snmpOutTraps;
   private int snmpOutGetResponses;
   private int snmpOutGenErrs;
   private int snmpOutBadValues;
   private int snmpOutNoSuchNames;
   private int snmpOutTooBigs;
   int snmpOutPkts;
   private int snmpInASNParseErrs;
   private int snmpInBadCommunityUses;
   private int snmpInBadCommunityNames;
   private int snmpInBadVersions;
   private int snmpInGetRequests;
   private int snmpInGetNexts;
   private int snmpInSetRequests;
   private int snmpInPkts;
   private int snmpInTotalReqVars;
   private int snmpInTotalSetVars;
   private int snmpSilentDrops;
   private static final String InterruptSysCallMsg = "Interrupted system call";
   static final SnmpOid sysUpTimeOid = new SnmpOid("1.3.6.1.2.1.1.3.0");
   static final SnmpOid snmpTrapOidOid = new SnmpOid("1.3.6.1.6.3.1.1.4.1.0");
   private ThreadService threadService;
   private static int threadNumber = 6;

   public SnmpAdaptorServer() {
      this(true, (InetAddressAcl)null, 161, (InetAddress)null);
   }

   public SnmpAdaptorServer(int var1) {
      this(true, (InetAddressAcl)null, var1, (InetAddress)null);
   }

   public SnmpAdaptorServer(InetAddressAcl var1) {
      this(false, var1, 161, (InetAddress)null);
   }

   public SnmpAdaptorServer(InetAddress var1) {
      this(true, (InetAddressAcl)null, 161, var1);
   }

   public SnmpAdaptorServer(InetAddressAcl var1, int var2) {
      this(false, var1, var2, (InetAddress)null);
   }

   public SnmpAdaptorServer(int var1, InetAddress var2) {
      this(true, (InetAddressAcl)null, var1, var2);
   }

   public SnmpAdaptorServer(InetAddressAcl var1, InetAddress var2) {
      this(false, var1, 161, var2);
   }

   public SnmpAdaptorServer(InetAddressAcl var1, int var2, InetAddress var3) {
      this(false, var1, var2, var3);
   }

   public SnmpAdaptorServer(boolean var1, int var2, InetAddress var3) {
      this(var1, (InetAddressAcl)null, var2, var3);
   }

   private SnmpAdaptorServer(boolean var1, InetAddressAcl var2, int var3, InetAddress var4) {
      super(4);
      this.trapPort = 162;
      this.informPort = 162;
      this.address = null;
      this.ipacl = null;
      this.pduFactory = null;
      this.userDataFactory = null;
      this.authRespEnabled = true;
      this.authTrapEnabled = true;
      this.enterpriseOid = new SnmpOid("1.3.6.1.4.1.42");
      this.bufferSize = 1024;
      this.startUpTime = 0L;
      this.socket = null;
      this.trapSocket = null;
      this.informSession = null;
      this.packet = null;
      this.mibs = new Vector();
      this.useAcl = true;
      this.maxTries = 3;
      this.timeout = 3000;
      this.snmpOutTraps = 0;
      this.snmpOutGetResponses = 0;
      this.snmpOutGenErrs = 0;
      this.snmpOutBadValues = 0;
      this.snmpOutNoSuchNames = 0;
      this.snmpOutTooBigs = 0;
      this.snmpOutPkts = 0;
      this.snmpInASNParseErrs = 0;
      this.snmpInBadCommunityUses = 0;
      this.snmpInBadCommunityNames = 0;
      this.snmpInBadVersions = 0;
      this.snmpInGetRequests = 0;
      this.snmpInGetNexts = 0;
      this.snmpInSetRequests = 0;
      this.snmpInPkts = 0;
      this.snmpInTotalReqVars = 0;
      this.snmpInTotalSetVars = 0;
      this.snmpSilentDrops = 0;
      if (var2 == null && var1) {
         try {
            var2 = new SnmpAcl("SNMP protocol adaptor IP ACL");
         } catch (UnknownHostException var6) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "constructor", (String)"UnknowHostException when creating ACL", (Throwable)var6);
            }
         }
      } else {
         this.useAcl = var2 != null || var1;
      }

      this.init((InetAddressAcl)var2, var3, var4);
   }

   public int getServedClientCount() {
      return super.getServedClientCount();
   }

   public int getActiveClientCount() {
      return super.getActiveClientCount();
   }

   public int getMaxActiveClientCount() {
      return super.getMaxActiveClientCount();
   }

   public void setMaxActiveClientCount(int var1) throws IllegalStateException {
      super.setMaxActiveClientCount(var1);
   }

   public InetAddressAcl getInetAddressAcl() {
      return this.ipacl;
   }

   public Integer getTrapPort() {
      return new Integer(this.trapPort);
   }

   public void setTrapPort(Integer var1) {
      this.setTrapPort(var1);
   }

   public void setTrapPort(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Trap port cannot be a negative value");
      } else {
         this.trapPort = var1;
      }
   }

   public int getInformPort() {
      return this.informPort;
   }

   public void setInformPort(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Inform request port cannot be a negative value");
      } else {
         this.informPort = var1;
      }
   }

   public String getProtocol() {
      return "snmp";
   }

   public Integer getBufferSize() {
      return new Integer(this.bufferSize);
   }

   public void setBufferSize(Integer var1) throws IllegalStateException {
      if (this.state != 0 && this.state != 3) {
         this.bufferSize = var1;
      } else {
         throw new IllegalStateException("Stop server before carrying out this operation");
      }
   }

   public final int getMaxTries() {
      return this.maxTries;
   }

   public final synchronized void setMaxTries(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.maxTries = var1;
      }
   }

   public final int getTimeout() {
      return this.timeout;
   }

   public final synchronized void setTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.timeout = var1;
      }
   }

   public SnmpPduFactory getPduFactory() {
      return this.pduFactory;
   }

   public void setPduFactory(SnmpPduFactory var1) {
      if (var1 == null) {
         this.pduFactory = new SnmpPduFactoryBER();
      } else {
         this.pduFactory = var1;
      }

   }

   public void setUserDataFactory(SnmpUserDataFactory var1) {
      this.userDataFactory = var1;
   }

   public SnmpUserDataFactory getUserDataFactory() {
      return this.userDataFactory;
   }

   public boolean getAuthTrapEnabled() {
      return this.authTrapEnabled;
   }

   public void setAuthTrapEnabled(boolean var1) {
      this.authTrapEnabled = var1;
   }

   public boolean getAuthRespEnabled() {
      return this.authRespEnabled;
   }

   public void setAuthRespEnabled(boolean var1) {
      this.authRespEnabled = var1;
   }

   public String getEnterpriseOid() {
      return this.enterpriseOid.toString();
   }

   public void setEnterpriseOid(String var1) throws IllegalArgumentException {
      this.enterpriseOid = new SnmpOid(var1);
   }

   public String[] getMibs() {
      String[] var1 = new String[this.mibs.size()];
      int var2 = 0;

      SnmpMibAgent var4;
      for(Enumeration var3 = this.mibs.elements(); var3.hasMoreElements(); var1[var2++] = var4.getMibName()) {
         var4 = (SnmpMibAgent)var3.nextElement();
      }

      return var1;
   }

   public Long getSnmpOutTraps() {
      return new Long((long)this.snmpOutTraps);
   }

   public Long getSnmpOutGetResponses() {
      return new Long((long)this.snmpOutGetResponses);
   }

   public Long getSnmpOutGenErrs() {
      return new Long((long)this.snmpOutGenErrs);
   }

   public Long getSnmpOutBadValues() {
      return new Long((long)this.snmpOutBadValues);
   }

   public Long getSnmpOutNoSuchNames() {
      return new Long((long)this.snmpOutNoSuchNames);
   }

   public Long getSnmpOutTooBigs() {
      return new Long((long)this.snmpOutTooBigs);
   }

   public Long getSnmpInASNParseErrs() {
      return new Long((long)this.snmpInASNParseErrs);
   }

   public Long getSnmpInBadCommunityUses() {
      return new Long((long)this.snmpInBadCommunityUses);
   }

   public Long getSnmpInBadCommunityNames() {
      return new Long((long)this.snmpInBadCommunityNames);
   }

   public Long getSnmpInBadVersions() {
      return new Long((long)this.snmpInBadVersions);
   }

   public Long getSnmpOutPkts() {
      return new Long((long)this.snmpOutPkts);
   }

   public Long getSnmpInPkts() {
      return new Long((long)this.snmpInPkts);
   }

   public Long getSnmpInGetRequests() {
      return new Long((long)this.snmpInGetRequests);
   }

   public Long getSnmpInGetNexts() {
      return new Long((long)this.snmpInGetNexts);
   }

   public Long getSnmpInSetRequests() {
      return new Long((long)this.snmpInSetRequests);
   }

   public Long getSnmpInTotalSetVars() {
      return new Long((long)this.snmpInTotalSetVars);
   }

   public Long getSnmpInTotalReqVars() {
      return new Long((long)this.snmpInTotalReqVars);
   }

   public Long getSnmpSilentDrops() {
      return new Long((long)this.snmpSilentDrops);
   }

   public Long getSnmpProxyDrops() {
      return new Long(0L);
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      if (var2 == null) {
         var2 = new ObjectName(var1.getDefaultDomain() + ":" + "name=SnmpAdaptorServer");
      }

      return super.preRegister(var1, var2);
   }

   public void postRegister(Boolean var1) {
      super.postRegister(var1);
   }

   public void preDeregister() throws Exception {
      super.preDeregister();
   }

   public void postDeregister() {
      super.postDeregister();
   }

   public SnmpMibHandler addMib(SnmpMibAgent var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         if (!this.mibs.contains(var1)) {
            this.mibs.addElement(var1);
         }

         this.root.register(var1);
         return this;
      }
   }

   public SnmpMibHandler addMib(SnmpMibAgent var1, SnmpOid[] var2) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else if (var2 == null) {
         return this.addMib(var1);
      } else {
         if (!this.mibs.contains(var1)) {
            this.mibs.addElement(var1);
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.root.register(var1, var2[var3].longValue());
         }

         return this;
      }
   }

   public SnmpMibHandler addMib(SnmpMibAgent var1, String var2) throws IllegalArgumentException {
      return this.addMib(var1);
   }

   public SnmpMibHandler addMib(SnmpMibAgent var1, String var2, SnmpOid[] var3) throws IllegalArgumentException {
      return this.addMib(var1, var3);
   }

   public boolean removeMib(SnmpMibAgent var1, String var2) {
      return this.removeMib(var1);
   }

   public boolean removeMib(SnmpMibAgent var1) {
      this.root.unregister(var1);
      return this.mibs.removeElement(var1);
   }

   public boolean removeMib(SnmpMibAgent var1, SnmpOid[] var2) {
      this.root.unregister(var1, var2);
      return this.mibs.removeElement(var1);
   }

   public boolean removeMib(SnmpMibAgent var1, String var2, SnmpOid[] var3) {
      return this.removeMib(var1, var3);
   }

   protected void doBind() throws CommunicationException, InterruptedException {
      try {
         synchronized(this) {
            this.socket = new DatagramSocket(this.port, this.address);
         }

         this.dbgTag = this.makeDebugTag();
      } catch (SocketException var4) {
         if (var4.getMessage().equals("Interrupted system call")) {
            throw new InterruptedException(var4.toString());
         } else {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doBind", "cannot bind on port " + this.port);
            }

            throw new CommunicationException(var4);
         }
      }
   }

   public int getPort() {
      synchronized(this) {
         if (this.socket != null) {
            return this.socket.getLocalPort();
         }
      }

      return super.getPort();
   }

   protected void doUnbind() throws CommunicationException, InterruptedException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doUnbind", "Finally close the socket");
      }

      synchronized(this) {
         if (this.socket != null) {
            this.socket.close();
            this.socket = null;
         }
      }

      this.closeTrapSocketIfNeeded();
      this.closeInformSocketIfNeeded();
   }

   private void createSnmpRequestHandler(SnmpAdaptorServer var1, int var2, DatagramSocket var3, DatagramPacket var4, SnmpMibTree var5, Vector<SnmpMibAgent> var6, InetAddressAcl var7, SnmpPduFactory var8, SnmpUserDataFactory var9, MBeanServer var10, ObjectName var11) {
      SnmpRequestHandler var12 = new SnmpRequestHandler(this, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      this.threadService.submitTask((Runnable)var12);
   }

   protected void doReceive() throws CommunicationException, InterruptedException {
      try {
         this.packet = new DatagramPacket(new byte[this.bufferSize], this.bufferSize);
         this.socket.receive(this.packet);
         int var1 = this.getState();
         if (var1 != 0) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doReceive", "received a message but state not online, returning.");
            }

            return;
         }

         this.createSnmpRequestHandler(this, this.servedClientCount, this.socket, this.packet, this.root, this.mibs, this.ipacl, this.pduFactory, this.userDataFactory, this.topMBS, this.objectName);
      } catch (SocketException var2) {
         if (var2.getMessage().equals("Interrupted system call")) {
            throw new InterruptedException(var2.toString());
         }

         throw new CommunicationException(var2);
      } catch (InterruptedIOException var3) {
         throw new InterruptedException(var3.toString());
      } catch (CommunicationException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new CommunicationException(var5);
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doReceive", "received a message");
      }

   }

   protected void doError(Exception var1) throws CommunicationException {
   }

   protected void doProcess() throws CommunicationException, InterruptedException {
   }

   protected int getBindTries() {
      return 1;
   }

   public void stop() {
      int var1 = this.getPort();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Stopping: using port " + var1);
      }

      if (this.state == 0 || this.state == 3) {
         super.stop();

         try {
            DatagramSocket var2 = new DatagramSocket(0);

            try {
               byte[] var3 = new byte[1];
               DatagramPacket var4;
               if (this.address != null) {
                  var4 = new DatagramPacket(var3, 1, this.address, var1);
               } else {
                  var4 = new DatagramPacket(var3, 1, InetAddress.getLocalHost(), var1);
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Sending: using port " + var1);
               }

               var2.send(var4);
            } finally {
               var2.close();
            }
         } catch (Throwable var9) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "stop", "Got unexpected Throwable", var9);
            }
         }
      }

   }

   public void snmpV1Trap(int var1, int var2, SnmpVarBindList var3) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + var1 + ", specific=" + var2);
      }

      SnmpPduTrap var4 = new SnmpPduTrap();
      var4.address = null;
      var4.port = this.trapPort;
      var4.type = 164;
      var4.version = 0;
      var4.community = null;
      var4.enterprise = this.enterpriseOid;
      var4.genericTrap = var1;
      var4.specificTrap = var2;
      var4.timeStamp = this.getSysUpTime();
      if (var3 != null) {
         var4.varBindList = new SnmpVarBind[var3.size()];
         var3.copyInto(var4.varBindList);
      } else {
         var4.varBindList = null;
      }

      try {
         if (this.address != null) {
            var4.agentAddr = this.handleMultipleIpVersion(this.address.getAddress());
         } else {
            var4.agentAddr = this.handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
         }
      } catch (UnknownHostException var7) {
         byte[] var6 = new byte[4];
         var4.agentAddr = this.handleMultipleIpVersion(var6);
      }

      this.sendTrapPdu(var4);
   }

   private SnmpIpAddress handleMultipleIpVersion(byte[] var1) {
      if (var1.length == 4) {
         return new SnmpIpAddress(var1);
      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "handleMultipleIPVersion", "Not an IPv4 address, return null");
         }

         return null;
      }
   }

   public void snmpV1Trap(InetAddress var1, String var2, int var3, int var4, SnmpVarBindList var5) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + var3 + ", specific=" + var4);
      }

      SnmpPduTrap var6 = new SnmpPduTrap();
      var6.address = null;
      var6.port = this.trapPort;
      var6.type = 164;
      var6.version = 0;
      if (var2 != null) {
         var6.community = var2.getBytes();
      } else {
         var6.community = null;
      }

      var6.enterprise = this.enterpriseOid;
      var6.genericTrap = var3;
      var6.specificTrap = var4;
      var6.timeStamp = this.getSysUpTime();
      if (var5 != null) {
         var6.varBindList = new SnmpVarBind[var5.size()];
         var5.copyInto(var6.varBindList);
      } else {
         var6.varBindList = null;
      }

      try {
         if (this.address != null) {
            var6.agentAddr = this.handleMultipleIpVersion(this.address.getAddress());
         } else {
            var6.agentAddr = this.handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
         }
      } catch (UnknownHostException var9) {
         byte[] var8 = new byte[4];
         var6.agentAddr = this.handleMultipleIpVersion(var8);
      }

      if (var1 != null) {
         this.sendTrapPdu(var1, var6);
      } else {
         this.sendTrapPdu(var6);
      }

   }

   public void snmpV1Trap(InetAddress var1, SnmpIpAddress var2, String var3, SnmpOid var4, int var5, int var6, SnmpVarBindList var7, SnmpTimeticks var8) throws IOException, SnmpStatusException {
      this.snmpV1Trap(var1, this.trapPort, var2, var3, var4, var5, var6, var7, var8);
   }

   public void snmpV1Trap(SnmpPeer var1, SnmpIpAddress var2, SnmpOid var3, int var4, int var5, SnmpVarBindList var6, SnmpTimeticks var7) throws IOException, SnmpStatusException {
      SnmpParameters var8 = (SnmpParameters)var1.getParams();
      this.snmpV1Trap(var1.getDestAddr(), var1.getDestPort(), var2, var8.getRdCommunity(), var3, var4, var5, var6, var7);
   }

   private void snmpV1Trap(InetAddress var1, int var2, SnmpIpAddress var3, String var4, SnmpOid var5, int var6, int var7, SnmpVarBindList var8, SnmpTimeticks var9) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + var6 + ", specific=" + var7);
      }

      SnmpPduTrap var10 = new SnmpPduTrap();
      var10.address = null;
      var10.port = var2;
      var10.type = 164;
      var10.version = 0;
      if (var4 != null) {
         var10.community = var4.getBytes();
      } else {
         var10.community = null;
      }

      if (var5 != null) {
         var10.enterprise = var5;
      } else {
         var10.enterprise = this.enterpriseOid;
      }

      var10.genericTrap = var6;
      var10.specificTrap = var7;
      if (var9 != null) {
         var10.timeStamp = var9.longValue();
      } else {
         var10.timeStamp = this.getSysUpTime();
      }

      if (var8 != null) {
         var10.varBindList = new SnmpVarBind[var8.size()];
         var8.copyInto(var10.varBindList);
      } else {
         var10.varBindList = null;
      }

      if (var3 == null) {
         try {
            InetAddress var11 = this.address != null ? this.address : InetAddress.getLocalHost();
            var3 = this.handleMultipleIpVersion(var11.getAddress());
         } catch (UnknownHostException var13) {
            byte[] var12 = new byte[4];
            var3 = this.handleMultipleIpVersion(var12);
         }
      }

      var10.agentAddr = var3;
      if (var1 != null) {
         this.sendTrapPdu(var1, var10);
      } else {
         this.sendTrapPdu(var10);
      }

   }

   public void snmpV2Trap(SnmpPeer var1, SnmpOid var2, SnmpVarBindList var3, SnmpTimeticks var4) throws IOException, SnmpStatusException {
      SnmpParameters var5 = (SnmpParameters)var1.getParams();
      this.snmpV2Trap(var1.getDestAddr(), var1.getDestPort(), var5.getRdCommunity(), var2, var3, var4);
   }

   public void snmpV2Trap(SnmpOid var1, SnmpVarBindList var2) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", "trapOid=" + var1);
      }

      SnmpPduRequest var3 = new SnmpPduRequest();
      var3.address = null;
      var3.port = this.trapPort;
      var3.type = 167;
      var3.version = 1;
      var3.community = null;
      SnmpVarBindList var4;
      if (var2 != null) {
         var4 = var2.clone();
      } else {
         var4 = new SnmpVarBindList(2);
      }

      SnmpTimeticks var5 = new SnmpTimeticks(this.getSysUpTime());
      var4.insertElementAt(new SnmpVarBind(snmpTrapOidOid, var1), 0);
      var4.insertElementAt(new SnmpVarBind(sysUpTimeOid, var5), 0);
      var3.varBindList = new SnmpVarBind[var4.size()];
      var4.copyInto(var3.varBindList);
      this.sendTrapPdu(var3);
   }

   public void snmpV2Trap(InetAddress var1, String var2, SnmpOid var3, SnmpVarBindList var4) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", "trapOid=" + var3);
      }

      SnmpPduRequest var5 = new SnmpPduRequest();
      var5.address = null;
      var5.port = this.trapPort;
      var5.type = 167;
      var5.version = 1;
      if (var2 != null) {
         var5.community = var2.getBytes();
      } else {
         var5.community = null;
      }

      SnmpVarBindList var6;
      if (var4 != null) {
         var6 = var4.clone();
      } else {
         var6 = new SnmpVarBindList(2);
      }

      SnmpTimeticks var7 = new SnmpTimeticks(this.getSysUpTime());
      var6.insertElementAt(new SnmpVarBind(snmpTrapOidOid, var3), 0);
      var6.insertElementAt(new SnmpVarBind(sysUpTimeOid, var7), 0);
      var5.varBindList = new SnmpVarBind[var6.size()];
      var6.copyInto(var5.varBindList);
      if (var1 != null) {
         this.sendTrapPdu(var1, var5);
      } else {
         this.sendTrapPdu(var5);
      }

   }

   public void snmpV2Trap(InetAddress var1, String var2, SnmpOid var3, SnmpVarBindList var4, SnmpTimeticks var5) throws IOException, SnmpStatusException {
      this.snmpV2Trap(var1, this.trapPort, var2, var3, var4, var5);
   }

   private void snmpV2Trap(InetAddress var1, int var2, String var3, SnmpOid var4, SnmpVarBindList var5, SnmpTimeticks var6) throws IOException, SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var7 = (new StringBuilder()).append("trapOid=").append((Object)var4).append("\ncommunity=").append(var3).append("\naddr=").append((Object)var1).append("\nvarBindList=").append((Object)var5).append("\ntime=").append((Object)var6).append("\ntrapPort=").append(var2);
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", var7.toString());
      }

      SnmpPduRequest var10 = new SnmpPduRequest();
      var10.address = null;
      var10.port = var2;
      var10.type = 167;
      var10.version = 1;
      if (var3 != null) {
         var10.community = var3.getBytes();
      } else {
         var10.community = null;
      }

      SnmpVarBindList var8;
      if (var5 != null) {
         var8 = var5.clone();
      } else {
         var8 = new SnmpVarBindList(2);
      }

      SnmpTimeticks var9;
      if (var6 != null) {
         var9 = var6;
      } else {
         var9 = new SnmpTimeticks(this.getSysUpTime());
      }

      var8.insertElementAt(new SnmpVarBind(snmpTrapOidOid, var4), 0);
      var8.insertElementAt(new SnmpVarBind(sysUpTimeOid, var9), 0);
      var10.varBindList = new SnmpVarBind[var8.size()];
      var8.copyInto(var10.varBindList);
      if (var1 != null) {
         this.sendTrapPdu(var1, var10);
      } else {
         this.sendTrapPdu(var10);
      }

   }

   public void snmpPduTrap(InetAddress var1, SnmpPduPacket var2) throws IOException, SnmpStatusException {
      if (var1 != null) {
         this.sendTrapPdu(var1, var2);
      } else {
         this.sendTrapPdu(var2);
      }

   }

   public void snmpPduTrap(SnmpPeer var1, SnmpPduPacket var2) throws IOException, SnmpStatusException {
      if (var1 != null) {
         var2.port = var1.getDestPort();
         this.sendTrapPdu(var1.getDestAddr(), var2);
      } else {
         var2.port = this.getTrapPort();
         this.sendTrapPdu(var2);
      }

   }

   private void sendTrapPdu(SnmpPduPacket var1) throws SnmpStatusException, IOException {
      SnmpMessage var2 = null;

      try {
         var2 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var1, this.bufferSize);
         if (var2 == null) {
            throw new SnmpStatusException(16);
         }
      } catch (SnmpTooBigException var7) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to anyone");
         }

         throw new SnmpStatusException(1);
      }

      int var3 = 0;
      this.openTrapSocketIfNeeded();
      if (this.ipacl != null) {
         Enumeration var4 = this.ipacl.getTrapDestinations();

         while(var4.hasMoreElements()) {
            var2.address = (InetAddress)var4.nextElement();
            Enumeration var5 = this.ipacl.getTrapCommunities(var2.address);

            while(var5.hasMoreElements()) {
               var2.community = ((String)var5.nextElement()).getBytes();

               try {
                  this.sendTrapMessage(var2);
                  ++var3;
               } catch (SnmpTooBigException var10) {
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + var2.address);
                  }
               }
            }
         }
      }

      if (var3 == 0) {
         try {
            var2.address = InetAddress.getLocalHost();
            this.sendTrapMessage(var2);
         } catch (SnmpTooBigException var8) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent.");
            }
         } catch (UnknownHostException var9) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent.");
            }
         }
      }

      this.closeTrapSocketIfNeeded();
   }

   private void sendTrapPdu(InetAddress var1, SnmpPduPacket var2) throws SnmpStatusException, IOException {
      SnmpMessage var3 = null;

      try {
         var3 = (SnmpMessage)this.pduFactory.encodeSnmpPdu(var2, this.bufferSize);
         if (var3 == null) {
            throw new SnmpStatusException(16);
         }
      } catch (SnmpTooBigException var5) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to the specified host.");
         }

         throw new SnmpStatusException(1);
      }

      this.openTrapSocketIfNeeded();
      if (var1 != null) {
         var3.address = var1;

         try {
            this.sendTrapMessage(var3);
         } catch (SnmpTooBigException var6) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + var3.address);
            }
         }
      }

      this.closeTrapSocketIfNeeded();
   }

   private void sendTrapMessage(SnmpMessage var1) throws IOException, SnmpTooBigException {
      byte[] var2 = new byte[this.bufferSize];
      DatagramPacket var3 = new DatagramPacket(var2, var2.length);
      int var4 = var1.encodeMessage(var2);
      var3.setLength(var4);
      var3.setAddress(var1.address);
      var3.setPort(var1.port);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "sendTrapMessage", "sending trap to " + var1.address + ":" + var1.port);
      }

      this.trapSocket.send(var3);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "sendTrapMessage", "sent to " + var1.address + ":" + var1.port);
      }

      ++this.snmpOutTraps;
      ++this.snmpOutPkts;
   }

   synchronized void openTrapSocketIfNeeded() throws SocketException {
      if (this.trapSocket == null) {
         this.trapSocket = new DatagramSocket(0, this.address);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "openTrapSocketIfNeeded", "using port " + this.trapSocket.getLocalPort() + " to send traps");
         }
      }

   }

   synchronized void closeTrapSocketIfNeeded() {
      if (this.trapSocket != null && this.state != 0) {
         this.trapSocket.close();
         this.trapSocket = null;
      }

   }

   public Vector<SnmpInformRequest> snmpInformRequest(SnmpInformHandler var1, SnmpOid var2, SnmpVarBindList var3) throws IllegalStateException, IOException, SnmpStatusException {
      if (!this.isActive()) {
         throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation");
      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpInformRequest", "trapOid=" + var2);
         }

         SnmpVarBindList var4;
         if (var3 != null) {
            var4 = var3.clone();
         } else {
            var4 = new SnmpVarBindList(2);
         }

         SnmpTimeticks var5 = new SnmpTimeticks(this.getSysUpTime());
         var4.insertElementAt(new SnmpVarBind(snmpTrapOidOid, var2), 0);
         var4.insertElementAt(new SnmpVarBind(sysUpTimeOid, var5), 0);
         this.openInformSocketIfNeeded();
         Vector var6 = new Vector();
         if (this.ipacl != null) {
            Enumeration var9 = this.ipacl.getInformDestinations();

            while(var9.hasMoreElements()) {
               InetAddress var7 = (InetAddress)var9.nextElement();
               Enumeration var10 = this.ipacl.getInformCommunities(var7);

               while(var10.hasMoreElements()) {
                  String var8 = (String)var10.nextElement();
                  var6.addElement(this.informSession.makeAsyncRequest(var7, var8, var1, var4, this.getInformPort()));
               }
            }
         }

         return var6;
      }
   }

   public SnmpInformRequest snmpInformRequest(InetAddress var1, String var2, SnmpInformHandler var3, SnmpOid var4, SnmpVarBindList var5) throws IllegalStateException, IOException, SnmpStatusException {
      return this.snmpInformRequest(var1, this.getInformPort(), var2, var3, var4, var5);
   }

   public SnmpInformRequest snmpInformRequest(SnmpPeer var1, SnmpInformHandler var2, SnmpOid var3, SnmpVarBindList var4) throws IllegalStateException, IOException, SnmpStatusException {
      SnmpParameters var5 = (SnmpParameters)var1.getParams();
      return this.snmpInformRequest(var1.getDestAddr(), var1.getDestPort(), var5.getInformCommunity(), var2, var3, var4);
   }

   public static int mapErrorStatus(int var0, int var1, int var2) {
      return SnmpSubRequestHandler.mapErrorStatus(var0, var1, var2);
   }

   private SnmpInformRequest snmpInformRequest(InetAddress var1, int var2, String var3, SnmpInformHandler var4, SnmpOid var5, SnmpVarBindList var6) throws IllegalStateException, IOException, SnmpStatusException {
      if (!this.isActive()) {
         throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation");
      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpInformRequest", "trapOid=" + var5);
         }

         SnmpVarBindList var7;
         if (var6 != null) {
            var7 = var6.clone();
         } else {
            var7 = new SnmpVarBindList(2);
         }

         SnmpTimeticks var8 = new SnmpTimeticks(this.getSysUpTime());
         var7.insertElementAt(new SnmpVarBind(snmpTrapOidOid, var5), 0);
         var7.insertElementAt(new SnmpVarBind(sysUpTimeOid, var8), 0);
         this.openInformSocketIfNeeded();
         return this.informSession.makeAsyncRequest(var1, var3, var4, var7, var2);
      }
   }

   synchronized void openInformSocketIfNeeded() throws SocketException {
      if (this.informSession == null) {
         this.informSession = new SnmpSession(this);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "openInformSocketIfNeeded", "to send inform requests and receive inform responses");
         }
      }

   }

   synchronized void closeInformSocketIfNeeded() {
      if (this.informSession != null && this.state != 0) {
         this.informSession.destroySession();
         this.informSession = null;
      }

   }

   InetAddress getAddress() {
      return this.address;
   }

   protected void finalize() {
      try {
         if (this.socket != null) {
            this.socket.close();
            this.socket = null;
         }

         this.threadService.terminate();
      } catch (Exception var2) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "finalize", (String)"Exception in finalizer", (Throwable)var2);
         }
      }

   }

   String makeDebugTag() {
      return "SnmpAdaptorServer[" + this.getProtocol() + ":" + this.getPort() + "]";
   }

   void updateRequestCounters(int var1) {
      switch(var1) {
      case 160:
         ++this.snmpInGetRequests;
         break;
      case 161:
         ++this.snmpInGetNexts;
      case 162:
      default:
         break;
      case 163:
         ++this.snmpInSetRequests;
      }

      ++this.snmpInPkts;
   }

   void updateErrorCounters(int var1) {
      switch(var1) {
      case 0:
         ++this.snmpOutGetResponses;
         break;
      case 1:
         ++this.snmpOutTooBigs;
         break;
      case 2:
         ++this.snmpOutNoSuchNames;
         break;
      case 3:
         ++this.snmpOutBadValues;
      case 4:
      default:
         break;
      case 5:
         ++this.snmpOutGenErrs;
      }

      ++this.snmpOutPkts;
   }

   void updateVarCounters(int var1, int var2) {
      switch(var1) {
      case 160:
      case 161:
      case 165:
         this.snmpInTotalReqVars += var2;
      case 162:
      case 164:
      default:
         break;
      case 163:
         this.snmpInTotalSetVars += var2;
      }

   }

   void incSnmpInASNParseErrs(int var1) {
      this.snmpInASNParseErrs += var1;
   }

   void incSnmpInBadVersions(int var1) {
      this.snmpInBadVersions += var1;
   }

   void incSnmpInBadCommunityUses(int var1) {
      this.snmpInBadCommunityUses += var1;
   }

   void incSnmpInBadCommunityNames(int var1) {
      this.snmpInBadCommunityNames += var1;
   }

   void incSnmpSilentDrops(int var1) {
      this.snmpSilentDrops += var1;
   }

   long getSysUpTime() {
      return (System.currentTimeMillis() - this.startUpTime) / 10L;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.mibs = new Vector();
   }

   private void init(InetAddressAcl var1, int var2, InetAddress var3) {
      this.root = new SnmpMibTree();
      this.root.setDefaultAgent(new SnmpErrorHandlerAgent());
      this.startUpTime = System.currentTimeMillis();
      this.maxActiveClientCount = 10;
      this.pduFactory = new SnmpPduFactoryBER();
      this.port = var2;
      this.ipacl = var1;
      this.address = var3;
      if (this.ipacl == null && this.useAcl) {
         throw new IllegalArgumentException("ACL object cannot be null");
      } else {
         this.threadService = new ThreadService(threadNumber);
      }
   }

   SnmpMibAgent getAgentMib(SnmpOid var1) {
      return this.root.getAgentMib(var1);
   }

   protected Thread createMainThread() {
      Thread var1 = super.createMainThread();
      var1.setDaemon(true);
      return var1;
   }

   static {
      String var0 = System.getProperty("com.sun.jmx.snmp.threadnumber");
      if (var0 != null) {
         try {
            threadNumber = Integer.parseInt(System.getProperty(var0));
         } catch (Exception var2) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpAdaptorServer.class.getName(), "<static init>", "Got wrong value for com.sun.jmx.snmp.threadnumber: " + var0 + ". Use the default value: " + threadNumber);
         }
      }

   }
}
