package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

public interface SnmpAdaptorServerMBean extends CommunicatorServerMBean {
   InetAddressAcl getInetAddressAcl();

   Integer getTrapPort();

   void setTrapPort(Integer var1);

   int getInformPort();

   void setInformPort(int var1);

   int getServedClientCount();

   int getActiveClientCount();

   int getMaxActiveClientCount();

   void setMaxActiveClientCount(int var1) throws IllegalStateException;

   String getProtocol();

   Integer getBufferSize();

   void setBufferSize(Integer var1) throws IllegalStateException;

   int getMaxTries();

   void setMaxTries(int var1);

   int getTimeout();

   void setTimeout(int var1);

   SnmpPduFactory getPduFactory();

   void setPduFactory(SnmpPduFactory var1);

   void setUserDataFactory(SnmpUserDataFactory var1);

   SnmpUserDataFactory getUserDataFactory();

   boolean getAuthTrapEnabled();

   void setAuthTrapEnabled(boolean var1);

   boolean getAuthRespEnabled();

   void setAuthRespEnabled(boolean var1);

   String getEnterpriseOid();

   void setEnterpriseOid(String var1) throws IllegalArgumentException;

   String[] getMibs();

   Long getSnmpOutTraps();

   Long getSnmpOutGetResponses();

   Long getSnmpOutGenErrs();

   Long getSnmpOutBadValues();

   Long getSnmpOutNoSuchNames();

   Long getSnmpOutTooBigs();

   Long getSnmpInASNParseErrs();

   Long getSnmpInBadCommunityUses();

   Long getSnmpInBadCommunityNames();

   Long getSnmpInBadVersions();

   Long getSnmpOutPkts();

   Long getSnmpInPkts();

   Long getSnmpInGetRequests();

   Long getSnmpInGetNexts();

   Long getSnmpInSetRequests();

   Long getSnmpInTotalSetVars();

   Long getSnmpInTotalReqVars();

   Long getSnmpSilentDrops();

   Long getSnmpProxyDrops();

   SnmpMibHandler addMib(SnmpMibAgent var1) throws IllegalArgumentException;

   SnmpMibHandler addMib(SnmpMibAgent var1, SnmpOid[] var2) throws IllegalArgumentException;

   boolean removeMib(SnmpMibAgent var1);

   void snmpV1Trap(int var1, int var2, SnmpVarBindList var3) throws IOException, SnmpStatusException;

   void snmpV1Trap(InetAddress var1, String var2, int var3, int var4, SnmpVarBindList var5) throws IOException, SnmpStatusException;

   void snmpV1Trap(SnmpPeer var1, SnmpIpAddress var2, SnmpOid var3, int var4, int var5, SnmpVarBindList var6, SnmpTimeticks var7) throws IOException, SnmpStatusException;

   void snmpV2Trap(SnmpPeer var1, SnmpOid var2, SnmpVarBindList var3, SnmpTimeticks var4) throws IOException, SnmpStatusException;

   void snmpV2Trap(SnmpOid var1, SnmpVarBindList var2) throws IOException, SnmpStatusException;

   void snmpV2Trap(InetAddress var1, String var2, SnmpOid var3, SnmpVarBindList var4) throws IOException, SnmpStatusException;

   void snmpPduTrap(InetAddress var1, SnmpPduPacket var2) throws IOException, SnmpStatusException;

   void snmpPduTrap(SnmpPeer var1, SnmpPduPacket var2) throws IOException, SnmpStatusException;

   Vector<?> snmpInformRequest(SnmpInformHandler var1, SnmpOid var2, SnmpVarBindList var3) throws IllegalStateException, IOException, SnmpStatusException;

   SnmpInformRequest snmpInformRequest(InetAddress var1, String var2, SnmpInformHandler var3, SnmpOid var4, SnmpVarBindList var5) throws IllegalStateException, IOException, SnmpStatusException;

   SnmpInformRequest snmpInformRequest(SnmpPeer var1, SnmpInformHandler var2, SnmpOid var3, SnmpVarBindList var4) throws IllegalStateException, IOException, SnmpStatusException;
}
