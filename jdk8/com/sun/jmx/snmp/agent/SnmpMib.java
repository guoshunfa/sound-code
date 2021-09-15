package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public abstract class SnmpMib extends SnmpMibAgent implements Serializable {
   protected SnmpMibOid root = new SnmpMibOid();
   private transient long[] rootOid = null;

   protected String getGroupOid(String var1, String var2) {
      return var2;
   }

   protected ObjectName getGroupObjectName(String var1, String var2, String var3) throws MalformedObjectNameException {
      return new ObjectName(var3);
   }

   protected void registerGroupNode(String var1, String var2, ObjectName var3, SnmpMibNode var4, Object var5, MBeanServer var6) throws NotCompliantMBeanException, MBeanRegistrationException, InstanceAlreadyExistsException, IllegalAccessException {
      this.root.registerNode(var2, var4);
      if (var6 != null && var3 != null && var5 != null) {
         var6.registerMBean(var5, var3);
      }

   }

   public abstract void registerTableMeta(String var1, SnmpMibTable var2);

   public abstract SnmpMibTable getRegisteredTableMeta(String var1);

   public void get(SnmpMibRequest var1) throws SnmpStatusException {
      SnmpRequestTree var3 = this.getHandlers(var1, false, false, 160);
      SnmpRequestTree.Handler var4 = null;
      SnmpMibNode var5 = null;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "get", "Processing handlers for GET... ");
      }

      Enumeration var6 = var3.getHandlers();

      while(var6.hasMoreElements()) {
         var4 = (SnmpRequestTree.Handler)var6.nextElement();
         var5 = var3.getMetaNode(var4);
         int var7 = var3.getOidDepth(var4);
         Enumeration var8 = var3.getSubRequests(var4);

         while(var8.hasMoreElements()) {
            var5.get((SnmpMibSubRequest)var8.nextElement(), var7);
         }
      }

   }

   public void set(SnmpMibRequest var1) throws SnmpStatusException {
      SnmpRequestTree var2 = null;
      if (var1 instanceof SnmpMibRequestImpl) {
         var2 = ((SnmpMibRequestImpl)var1).getRequestTree();
      }

      if (var2 == null) {
         var2 = this.getHandlers(var1, false, true, 163);
      }

      var2.switchCreationFlag(false);
      var2.setPduType(163);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "set", "Processing handlers for SET... ");
      }

      Enumeration var6 = var2.getHandlers();

      while(var6.hasMoreElements()) {
         SnmpRequestTree.Handler var4 = (SnmpRequestTree.Handler)var6.nextElement();
         SnmpMibNode var5 = var2.getMetaNode(var4);
         int var7 = var2.getOidDepth(var4);
         Enumeration var8 = var2.getSubRequests(var4);

         while(var8.hasMoreElements()) {
            var5.set((SnmpMibSubRequest)var8.nextElement(), var7);
         }
      }

   }

   public void check(SnmpMibRequest var1) throws SnmpStatusException {
      SnmpRequestTree var3 = this.getHandlers(var1, true, true, 253);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "check", "Processing handlers for CHECK... ");
      }

      Enumeration var6 = var3.getHandlers();

      while(var6.hasMoreElements()) {
         SnmpRequestTree.Handler var4 = (SnmpRequestTree.Handler)var6.nextElement();
         SnmpMibNode var5 = var3.getMetaNode(var4);
         int var7 = var3.getOidDepth(var4);
         Enumeration var8 = var3.getSubRequests(var4);

         while(var8.hasMoreElements()) {
            var5.check((SnmpMibSubRequest)var8.nextElement(), var7);
         }
      }

      if (var1 instanceof SnmpMibRequestImpl) {
         ((SnmpMibRequestImpl)var1).setRequestTree(var3);
      }

   }

   public void getNext(SnmpMibRequest var1) throws SnmpStatusException {
      SnmpRequestTree var2 = this.getGetNextHandlers(var1);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getNext", "Processing handlers for GET-NEXT... ");
      }

      Enumeration var5 = var2.getHandlers();

      while(var5.hasMoreElements()) {
         SnmpRequestTree.Handler var3 = (SnmpRequestTree.Handler)var5.nextElement();
         SnmpMibNode var4 = var2.getMetaNode(var3);
         int var6 = var2.getOidDepth(var3);
         Enumeration var7 = var2.getSubRequests(var3);

         while(var7.hasMoreElements()) {
            var4.get((SnmpMibSubRequest)var7.nextElement(), var6);
         }
      }

   }

   public void getBulk(SnmpMibRequest var1, int var2, int var3) throws SnmpStatusException {
      this.getBulkWithGetNext(var1, var2, var3);
   }

   public long[] getRootOid() {
      if (this.rootOid == null) {
         Vector var1 = new Vector(10);
         this.root.getRootOid(var1);
         this.rootOid = new long[var1.size()];
         int var2 = 0;

         Integer var4;
         for(Enumeration var3 = var1.elements(); var3.hasMoreElements(); this.rootOid[var2++] = var4.longValue()) {
            var4 = (Integer)var3.nextElement();
         }
      }

      return (long[])this.rootOid.clone();
   }

   private SnmpRequestTree getHandlers(SnmpMibRequest var1, boolean var2, boolean var3, int var4) throws SnmpStatusException {
      SnmpRequestTree var5 = new SnmpRequestTree(var1, var2, var4);
      int var6 = 0;
      int var8 = var1.getVersion();

      for(Enumeration var9 = var1.getElements(); var9.hasMoreElements(); ++var6) {
         SnmpVarBind var7 = (SnmpVarBind)var9.nextElement();

         try {
            this.root.findHandlingNode(var7, var7.oid.longValue(false), 0, var5);
         } catch (SnmpStatusException var13) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "Couldn't find a handling node for " + var7.oid.toString());
            }

            SnmpStatusException var14;
            if (var8 == 0) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tV1: Throwing exception");
               }

               var14 = new SnmpStatusException(var13, var6 + 1);
               var14.initCause(var13);
               throw var14;
            }

            int var11;
            SnmpStatusException var12;
            if (var4 == 253 || var4 == 163) {
               var11 = SnmpRequestTree.mapSetException(var13.getStatus(), var8);
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tSET: Throwing exception");
               }

               var12 = new SnmpStatusException(var11, var6 + 1);
               var12.initCause(var13);
               throw var12;
            }

            if (var3) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tATOMIC: Throwing exception");
               }

               var14 = new SnmpStatusException(var13, var6 + 1);
               var14.initCause(var13);
               throw var14;
            }

            var11 = SnmpRequestTree.mapGetException(var13.getStatus(), var8);
            if (var11 == 224) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchInstance");
               }

               var7.value = SnmpVarBind.noSuchInstance;
            } else {
               if (var11 != 225) {
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering global error: " + var11);
                  }

                  var12 = new SnmpStatusException(var11, var6 + 1);
                  var12.initCause(var13);
                  throw var12;
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchObject");
               }

               var7.value = SnmpVarBind.noSuchObject;
            }
         }
      }

      return var5;
   }

   private SnmpRequestTree getGetNextHandlers(SnmpMibRequest var1) throws SnmpStatusException {
      SnmpRequestTree var2 = new SnmpRequestTree(var1, false, 161);
      var2.setGetNextFlag();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Received MIB request : " + var1);
      }

      AcmChecker var3 = new AcmChecker(var1);
      int var4 = 0;
      SnmpVarBind var5 = null;
      int var6 = var1.getVersion();
      Object var7 = null;

      for(Enumeration var8 = var1.getElements(); var8.hasMoreElements(); ++var4) {
         var5 = (SnmpVarBind)var8.nextElement();

         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " Next OID of : " + var5.oid);
            }

            SnmpOid var9 = new SnmpOid(this.root.findNextHandlingNode(var5, var5.oid.longValue(false), 0, 0, var2, var3));
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " is : " + var9);
            }

            var5.oid = var9;
         } catch (SnmpStatusException var11) {
            if (var6 == 0) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "\tThrowing exception " + var11.toString());
               }

               throw new SnmpStatusException(var11, var4 + 1);
            }

            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Exception : " + var11.getStatus());
            }

            var5.setSnmpValue(SnmpVarBind.endOfMibView);
         }
      }

      return var2;
   }
}
