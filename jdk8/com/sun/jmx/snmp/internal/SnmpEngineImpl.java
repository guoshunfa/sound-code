package com.sun.jmx.snmp.internal;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpEngineFactory;
import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUsmKeyHandler;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class SnmpEngineImpl implements SnmpEngine, Serializable {
   private static final long serialVersionUID = -2564301391365614725L;
   public static final int noAuthNoPriv = 0;
   public static final int authNoPriv = 1;
   public static final int authPriv = 3;
   public static final int reportableFlag = 4;
   public static final int authMask = 1;
   public static final int privMask = 2;
   public static final int authPrivMask = 3;
   private SnmpEngineId engineid = null;
   private SnmpEngineFactory factory = null;
   private long startTime = 0L;
   private int boot = 0;
   private boolean checkOid = false;
   private transient SnmpUsmKeyHandler usmKeyHandler = null;
   private transient SnmpLcd lcd = null;
   private transient SnmpSecuritySubSystem securitySub = null;
   private transient SnmpMsgProcessingSubSystem messageSub = null;
   private transient SnmpAccessControlSubSystem accessSub = null;

   public synchronized int getEngineTime() {
      long var1 = System.currentTimeMillis() / 1000L - this.startTime;
      if (var1 > 2147483647L) {
         this.startTime = System.currentTimeMillis() / 1000L;
         if (this.boot != Integer.MAX_VALUE) {
            ++this.boot;
         }

         this.storeNBBoots(this.boot);
      }

      return (int)(System.currentTimeMillis() / 1000L - this.startTime);
   }

   public SnmpEngineId getEngineId() {
      return this.engineid;
   }

   public SnmpUsmKeyHandler getUsmKeyHandler() {
      return this.usmKeyHandler;
   }

   public SnmpLcd getLcd() {
      return this.lcd;
   }

   public int getEngineBoots() {
      return this.boot;
   }

   public SnmpEngineImpl(SnmpEngineFactory var1, SnmpLcd var2, SnmpEngineId var3) throws UnknownHostException {
      this.init(var2, var1);
      this.initEngineID();
      if (this.engineid == null) {
         if (var3 != null) {
            this.engineid = var3;
         } else {
            this.engineid = SnmpEngineId.createEngineId();
         }
      }

      var2.storeEngineId(this.engineid);
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,SnmpEngineId)", "LOCAL ENGINE ID: " + this.engineid);
      }

   }

   public SnmpEngineImpl(SnmpEngineFactory var1, SnmpLcd var2, InetAddress var3, int var4) throws UnknownHostException {
      this.init(var2, var1);
      this.initEngineID();
      if (this.engineid == null) {
         this.engineid = SnmpEngineId.createEngineId(var3, var4);
      }

      var2.storeEngineId(this.engineid);
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,InetAddress,int)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + this.getEngineTime());
      }

   }

   public SnmpEngineImpl(SnmpEngineFactory var1, SnmpLcd var2, int var3) throws UnknownHostException {
      this.init(var2, var1);
      this.initEngineID();
      if (this.engineid == null) {
         this.engineid = SnmpEngineId.createEngineId(var3);
      }

      var2.storeEngineId(this.engineid);
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,int)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + this.getEngineTime());
      }

   }

   public SnmpEngineImpl(SnmpEngineFactory var1, SnmpLcd var2) throws UnknownHostException {
      this.init(var2, var1);
      this.initEngineID();
      if (this.engineid == null) {
         this.engineid = SnmpEngineId.createEngineId();
      }

      var2.storeEngineId(this.engineid);
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + this.getEngineTime());
      }

   }

   public synchronized void activateCheckOid() {
      this.checkOid = true;
   }

   public synchronized void deactivateCheckOid() {
      this.checkOid = false;
   }

   public synchronized boolean isCheckOidActivated() {
      return this.checkOid;
   }

   private void storeNBBoots(int var1) {
      if (var1 >= 0 && var1 != Integer.MAX_VALUE) {
         this.lcd.storeEngineBoots(var1 + 1);
      } else {
         var1 = Integer.MAX_VALUE;
         this.lcd.storeEngineBoots(var1);
      }

   }

   private void init(SnmpLcd var1, SnmpEngineFactory var2) {
      this.factory = var2;
      this.lcd = var1;
      this.boot = var1.getEngineBoots();
      if (this.boot == -1 || this.boot == 0) {
         this.boot = 1;
      }

      this.storeNBBoots(this.boot);
      this.startTime = System.currentTimeMillis() / 1000L;
   }

   void setUsmKeyHandler(SnmpUsmKeyHandler var1) {
      this.usmKeyHandler = var1;
   }

   private void initEngineID() throws UnknownHostException {
      String var1 = this.lcd.getEngineId();
      if (var1 != null) {
         this.engineid = SnmpEngineId.createEngineId(var1);
      }

   }

   public SnmpMsgProcessingSubSystem getMsgProcessingSubSystem() {
      return this.messageSub;
   }

   public void setMsgProcessingSubSystem(SnmpMsgProcessingSubSystem var1) {
      this.messageSub = var1;
   }

   public SnmpSecuritySubSystem getSecuritySubSystem() {
      return this.securitySub;
   }

   public void setSecuritySubSystem(SnmpSecuritySubSystem var1) {
      this.securitySub = var1;
   }

   public void setAccessControlSubSystem(SnmpAccessControlSubSystem var1) {
      this.accessSub = var1;
   }

   public SnmpAccessControlSubSystem getAccessControlSubSystem() {
      return this.accessSub;
   }

   public static void checkSecurityLevel(byte var0) throws SnmpBadSecurityLevelException {
      int var1 = var0 & 3;
      if ((var1 & 2) != 0 && (var1 & 1) == 0) {
         throw new SnmpBadSecurityLevelException("Security level: noAuthPriv!!!");
      }
   }
}
