package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpOidTable;
import com.sun.jmx.snmp.SnmpParameters;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIB;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;
import sun.management.snmp.jvmmib.JvmCompilationMeta;
import sun.management.snmp.jvmmib.JvmMemoryMeta;
import sun.management.snmp.jvmmib.JvmRuntimeMeta;
import sun.management.snmp.jvmmib.JvmThreadingMeta;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableHandler;

public class JVM_MANAGEMENT_MIB_IMPL extends JVM_MANAGEMENT_MIB {
   private static final long serialVersionUID = -8104825586888859831L;
   private static final MibLogger log = new MibLogger(JVM_MANAGEMENT_MIB_IMPL.class);
   private static WeakReference<SnmpOidTable> tableRef;
   private ArrayList<NotificationTarget> notificationTargets = new ArrayList();
   private final NotificationEmitter emitter = (NotificationEmitter)ManagementFactory.getMemoryMXBean();
   private final JVM_MANAGEMENT_MIB_IMPL.NotificationHandler handler = new JVM_MANAGEMENT_MIB_IMPL.NotificationHandler();
   private static final int DISPLAY_STRING_MAX_LENGTH = 255;
   private static final int JAVA_OBJECT_NAME_MAX_LENGTH = 1023;
   private static final int PATH_ELEMENT_MAX_LENGTH = 1023;
   private static final int ARG_VALUE_MAX_LENGTH = 1023;
   private static final int DEFAULT_CACHE_VALIDITY_PERIOD = 1000;

   public static SnmpOidTable getOidTable() {
      Object var0 = null;
      if (tableRef == null) {
         JVM_MANAGEMENT_MIBOidTable var1 = new JVM_MANAGEMENT_MIBOidTable();
         tableRef = new WeakReference(var1);
         return var1;
      } else {
         var0 = (SnmpOidTable)tableRef.get();
         if (var0 == null) {
            var0 = new JVM_MANAGEMENT_MIBOidTable();
            tableRef = new WeakReference(var0);
         }

         return (SnmpOidTable)var0;
      }
   }

   public JVM_MANAGEMENT_MIB_IMPL() {
      this.emitter.addNotificationListener(this.handler, (NotificationFilter)null, (Object)null);
   }

   private synchronized void sendTrap(SnmpOid var1, SnmpVarBindList var2) {
      Iterator var3 = this.notificationTargets.iterator();
      SnmpAdaptorServer var4 = (SnmpAdaptorServer)this.getSnmpAdaptor();
      if (var4 == null) {
         log.error("sendTrap", "Cannot send trap: adaptor is null.");
      } else if (!var4.isActive()) {
         log.config("sendTrap", "Adaptor is not active: trap not sent.");
      } else {
         while(var3.hasNext()) {
            NotificationTarget var5 = null;

            try {
               var5 = (NotificationTarget)var3.next();
               SnmpPeer var6 = new SnmpPeer(var5.getAddress(), var5.getPort());
               SnmpParameters var7 = new SnmpParameters();
               var7.setRdCommunity(var5.getCommunity());
               var6.setParams(var7);
               log.debug("handleNotification", "Sending trap to " + var5.getAddress() + ":" + var5.getPort());
               var4.snmpV2Trap((SnmpPeer)var6, (SnmpOid)var1, (SnmpVarBindList)var2, (SnmpTimeticks)null);
            } catch (Exception var8) {
               log.error("sendTrap", "Exception occurred while sending trap to [" + var5 + "]. Exception : " + var8);
               log.debug("sendTrap", (Throwable)var8);
            }
         }

      }
   }

   public synchronized void addTarget(NotificationTarget var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Target is null");
      } else {
         this.notificationTargets.add(var1);
      }
   }

   public void terminate() {
      try {
         this.emitter.removeNotificationListener(this.handler);
      } catch (ListenerNotFoundException var2) {
         log.error("terminate", "Listener Not found : " + var2);
      }

   }

   public synchronized void addTargets(List<NotificationTarget> var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Target list is null");
      } else {
         this.notificationTargets.addAll(var1);
      }
   }

   protected Object createJvmMemoryMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmMemoryImpl(this, var4) : new JvmMemoryImpl(this);
   }

   protected JvmMemoryMeta createJvmMemoryMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmMemoryMetaImpl(this, this.objectserver);
   }

   protected JvmThreadingMeta createJvmThreadingMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmThreadingMetaImpl(this, this.objectserver);
   }

   protected Object createJvmThreadingMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmThreadingImpl(this, var4) : new JvmThreadingImpl(this);
   }

   protected JvmRuntimeMeta createJvmRuntimeMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmRuntimeMetaImpl(this, this.objectserver);
   }

   protected Object createJvmRuntimeMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmRuntimeImpl(this, var4) : new JvmRuntimeImpl(this);
   }

   protected JvmCompilationMeta createJvmCompilationMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return ManagementFactory.getCompilationMXBean() == null ? null : super.createJvmCompilationMetaNode(var1, var2, var3, var4);
   }

   protected Object createJvmCompilationMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmCompilationImpl(this, var4) : new JvmCompilationImpl(this);
   }

   protected Object createJvmOSMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmOSImpl(this, var4) : new JvmOSImpl(this);
   }

   protected Object createJvmClassLoadingMBean(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return var4 != null ? new JvmClassLoadingImpl(this, var4) : new JvmClassLoadingImpl(this);
   }

   static String validDisplayStringTC(String var0) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > 255 ? var0.substring(0, 255) : var0;
      }
   }

   static String validJavaObjectNameTC(String var0) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > 1023 ? var0.substring(0, 1023) : var0;
      }
   }

   static String validPathElementTC(String var0) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > 1023 ? var0.substring(0, 1023) : var0;
      }
   }

   static String validArgValueTC(String var0) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > 1023 ? var0.substring(0, 1023) : var0;
      }
   }

   private SnmpTableHandler getJvmMemPoolTableHandler(Object var1) {
      SnmpMibTable var2 = this.getRegisteredTableMeta("JvmMemPoolTable");
      if (!(var2 instanceof JvmMemPoolTableMetaImpl)) {
         String var4 = var2 == null ? "No metadata for JvmMemPoolTable" : "Bad metadata class for JvmMemPoolTable: " + var2.getClass().getName();
         log.error("getJvmMemPoolTableHandler", var4);
         return null;
      } else {
         JvmMemPoolTableMetaImpl var3 = (JvmMemPoolTableMetaImpl)var2;
         return var3.getHandler(var1);
      }
   }

   private int findInCache(SnmpTableHandler var1, String var2) {
      if (!(var1 instanceof SnmpCachedData)) {
         if (var1 != null) {
            String var7 = "Bad class for JvmMemPoolTable datas: " + var1.getClass().getName();
            log.error("getJvmMemPoolEntry", var7);
         }

         return -1;
      } else {
         SnmpCachedData var3 = (SnmpCachedData)var1;
         int var4 = var3.datas.length;

         for(int var5 = 0; var5 < var3.datas.length; ++var5) {
            MemoryPoolMXBean var6 = (MemoryPoolMXBean)var3.datas[var5];
            if (var2.equals(var6.getName())) {
               return var5;
            }
         }

         return -1;
      }
   }

   private SnmpOid getJvmMemPoolEntryIndex(SnmpTableHandler var1, String var2) {
      int var3 = this.findInCache(var1, var2);
      return var3 < 0 ? null : ((SnmpCachedData)var1).indexes[var3];
   }

   private SnmpOid getJvmMemPoolEntryIndex(String var1) {
      return this.getJvmMemPoolEntryIndex(this.getJvmMemPoolTableHandler((Object)null), var1);
   }

   public long validity() {
      return 1000L;
   }

   private class NotificationHandler implements NotificationListener {
      private NotificationHandler() {
      }

      public void handleNotification(Notification var1, Object var2) {
         JVM_MANAGEMENT_MIB_IMPL.log.debug("handleNotification", "Received notification [ " + var1.getType() + "]");
         String var3 = var1.getType();
         if (var3.equals("java.management.memory.threshold.exceeded") || var3.equals("java.management.memory.collection.threshold.exceeded")) {
            MemoryNotificationInfo var4 = MemoryNotificationInfo.from((CompositeData)var1.getUserData());
            SnmpCounter64 var5 = new SnmpCounter64(var4.getCount());
            SnmpCounter64 var6 = new SnmpCounter64(var4.getUsage().getUsed());
            SnmpString var7 = new SnmpString(var4.getPoolName());
            SnmpOid var8 = JVM_MANAGEMENT_MIB_IMPL.this.getJvmMemPoolEntryIndex(var4.getPoolName());
            if (var8 == null) {
               JVM_MANAGEMENT_MIB_IMPL.log.error("handleNotification", "Error: Can't find entry index for Memory Pool: " + var4.getPoolName() + ": No trap emitted for " + var3);
               return;
            }

            SnmpOid var9 = null;
            SnmpOidTable var10 = JVM_MANAGEMENT_MIB_IMPL.getOidTable();

            try {
               SnmpOid var11 = null;
               SnmpOid var12 = null;
               if (var3.equals("java.management.memory.threshold.exceeded")) {
                  var9 = new SnmpOid(var10.resolveVarName("jvmLowMemoryPoolUsageNotif").getOid());
                  var11 = new SnmpOid(var10.resolveVarName("jvmMemPoolUsed").getOid() + "." + var8);
                  var12 = new SnmpOid(var10.resolveVarName("jvmMemPoolThreshdCount").getOid() + "." + var8);
               } else if (var3.equals("java.management.memory.collection.threshold.exceeded")) {
                  var9 = new SnmpOid(var10.resolveVarName("jvmLowMemoryPoolCollectNotif").getOid());
                  var11 = new SnmpOid(var10.resolveVarName("jvmMemPoolCollectUsed").getOid() + "." + var8);
                  var12 = new SnmpOid(var10.resolveVarName("jvmMemPoolCollectThreshdCount").getOid() + "." + var8);
               }

               SnmpVarBindList var13 = new SnmpVarBindList();
               SnmpOid var14 = new SnmpOid(var10.resolveVarName("jvmMemPoolName").getOid() + "." + var8);
               SnmpVarBind var15 = new SnmpVarBind(var12, var5);
               SnmpVarBind var16 = new SnmpVarBind(var11, var6);
               SnmpVarBind var17 = new SnmpVarBind(var14, var7);
               var13.add(var17);
               var13.add(var15);
               var13.add(var16);
               JVM_MANAGEMENT_MIB_IMPL.this.sendTrap(var9, var13);
            } catch (Exception var18) {
               JVM_MANAGEMENT_MIB_IMPL.log.error("handleNotification", "Exception occurred : " + var18);
            }
         }

      }

      // $FF: synthetic method
      NotificationHandler(Object var2) {
         this();
      }
   }
}
