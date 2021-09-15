package com.sun.jmx.snmp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SnmpPeer implements Serializable {
   private static final long serialVersionUID = -5554565062847175999L;
   public static final int defaultSnmpRequestPktSize = 2048;
   public static final int defaultSnmpResponsePktSize = 8192;
   private int maxVarBindLimit;
   private int portNum;
   private int maxTries;
   private int timeout;
   private SnmpPduFactory pduFactory;
   private long _maxrtt;
   private long _minrtt;
   private long _avgrtt;
   private SnmpParams _snmpParameter;
   private InetAddress _devAddr;
   private int maxSnmpPacketSize;
   InetAddress[] _devAddrList;
   int _addrIndex;
   private boolean customPduFactory;

   public SnmpPeer(String var1) throws UnknownHostException {
      this((String)var1, 161);
   }

   public SnmpPeer(InetAddress var1, int var2) {
      this.maxVarBindLimit = 25;
      this.portNum = 161;
      this.maxTries = 3;
      this.timeout = 3000;
      this.pduFactory = new SnmpPduFactoryBER();
      this._snmpParameter = new SnmpParameters();
      this._devAddr = null;
      this.maxSnmpPacketSize = 2048;
      this._devAddrList = null;
      this._addrIndex = 0;
      this.customPduFactory = false;
      this._devAddr = var1;
      this.portNum = var2;
   }

   public SnmpPeer(InetAddress var1) {
      this.maxVarBindLimit = 25;
      this.portNum = 161;
      this.maxTries = 3;
      this.timeout = 3000;
      this.pduFactory = new SnmpPduFactoryBER();
      this._snmpParameter = new SnmpParameters();
      this._devAddr = null;
      this.maxSnmpPacketSize = 2048;
      this._devAddrList = null;
      this._addrIndex = 0;
      this.customPduFactory = false;
      this._devAddr = var1;
   }

   public SnmpPeer(String var1, int var2) throws UnknownHostException {
      this.maxVarBindLimit = 25;
      this.portNum = 161;
      this.maxTries = 3;
      this.timeout = 3000;
      this.pduFactory = new SnmpPduFactoryBER();
      this._snmpParameter = new SnmpParameters();
      this._devAddr = null;
      this.maxSnmpPacketSize = 2048;
      this._devAddrList = null;
      this._addrIndex = 0;
      this.customPduFactory = false;
      this.useIPAddress(var1);
      this.portNum = var2;
   }

   public final synchronized void useIPAddress(String var1) throws UnknownHostException {
      this._devAddr = InetAddress.getByName(var1);
   }

   public final synchronized String ipAddressInUse() {
      byte[] var1 = this._devAddr.getAddress();
      return (var1[0] & 255) + "." + (var1[1] & 255) + "." + (var1[2] & 255) + "." + (var1[3] & 255);
   }

   public final synchronized void useAddressList(InetAddress[] var1) {
      this._devAddrList = var1 != null ? (InetAddress[])var1.clone() : null;
      this._addrIndex = 0;
      this.useNextAddress();
   }

   public final synchronized void useNextAddress() {
      if (this._devAddrList != null) {
         if (this._addrIndex > this._devAddrList.length - 1) {
            this._addrIndex = 0;
         }

         this._devAddr = this._devAddrList[this._addrIndex++];
      }
   }

   public boolean allowSnmpSets() {
      return this._snmpParameter.allowSnmpSets();
   }

   public final InetAddress[] getDestAddrList() {
      return this._devAddrList == null ? null : (InetAddress[])this._devAddrList.clone();
   }

   public final InetAddress getDestAddr() {
      return this._devAddr;
   }

   public final int getDestPort() {
      return this.portNum;
   }

   public final synchronized void setDestPort(int var1) {
      this.portNum = var1;
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

   public final String getDevName() {
      return this.getDestAddr().getHostName();
   }

   public String toString() {
      return "Peer/Port : " + this.getDestAddr().getHostAddress() + "/" + this.getDestPort();
   }

   public final synchronized int getVarBindLimit() {
      return this.maxVarBindLimit;
   }

   public final synchronized void setVarBindLimit(int var1) {
      this.maxVarBindLimit = var1;
   }

   public void setParams(SnmpParams var1) {
      this._snmpParameter = var1;
   }

   public SnmpParams getParams() {
      return this._snmpParameter;
   }

   public final int getMaxSnmpPktSize() {
      return this.maxSnmpPacketSize;
   }

   public final synchronized void setMaxSnmpPktSize(int var1) {
      this.maxSnmpPacketSize = var1;
   }

   boolean isCustomPduFactory() {
      return this.customPduFactory;
   }

   protected void finalize() {
      this._devAddr = null;
      this._devAddrList = null;
      this._snmpParameter = null;
   }

   public long getMinRtt() {
      return this._minrtt;
   }

   public long getMaxRtt() {
      return this._maxrtt;
   }

   public long getAvgRtt() {
      return this._avgrtt;
   }

   private void updateRttStats(long var1) {
      if (this._minrtt > var1) {
         this._minrtt = var1;
      } else if (this._maxrtt < var1) {
         this._maxrtt = var1;
      } else {
         this._avgrtt = var1;
      }

   }
}
