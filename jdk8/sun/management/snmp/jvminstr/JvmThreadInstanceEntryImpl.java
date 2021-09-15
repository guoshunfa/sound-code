package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpOidRecord;
import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;
import sun.management.snmp.jvmmib.JvmThreadInstanceEntryMBean;
import sun.management.snmp.util.MibLogger;

public class JvmThreadInstanceEntryImpl implements JvmThreadInstanceEntryMBean, Serializable {
   static final long serialVersionUID = 910173589985461347L;
   private final ThreadInfo info;
   private final Byte[] index;
   private static String jvmThreadInstIndexOid = null;
   static final MibLogger log = new MibLogger(JvmThreadInstanceEntryImpl.class);

   public JvmThreadInstanceEntryImpl(ThreadInfo var1, Byte[] var2) {
      this.info = var1;
      this.index = var2;
   }

   public static String getJvmThreadInstIndexOid() throws SnmpStatusException {
      if (jvmThreadInstIndexOid == null) {
         JVM_MANAGEMENT_MIBOidTable var0 = new JVM_MANAGEMENT_MIBOidTable();
         SnmpOidRecord var1 = var0.resolveVarName("jvmThreadInstIndex");
         jvmThreadInstIndexOid = var1.getOid();
      }

      return jvmThreadInstIndexOid;
   }

   public String getJvmThreadInstLockOwnerPtr() throws SnmpStatusException {
      long var1 = this.info.getLockOwnerId();
      if (var1 == -1L) {
         return new String("0.0");
      } else {
         SnmpOid var3 = JvmThreadInstanceTableMetaImpl.makeOid(var1);
         return getJvmThreadInstIndexOid() + "." + var3.toString();
      }
   }

   private String validDisplayStringTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(var1);
   }

   private String validJavaObjectNameTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(var1);
   }

   private String validPathElementTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(var1);
   }

   public String getJvmThreadInstLockName() throws SnmpStatusException {
      return this.validJavaObjectNameTC(this.info.getLockName());
   }

   public String getJvmThreadInstName() throws SnmpStatusException {
      return this.validJavaObjectNameTC(this.info.getThreadName());
   }

   public Long getJvmThreadInstCpuTimeNs() throws SnmpStatusException {
      long var1 = 0L;
      ThreadMXBean var3 = JvmThreadingImpl.getThreadMXBean();

      try {
         if (var3.isThreadCpuTimeSupported()) {
            var1 = var3.getThreadCpuTime(this.info.getThreadId());
            log.debug("getJvmThreadInstCpuTimeNs", "Cpu time ns : " + var1);
            if (var1 == -1L) {
               var1 = 0L;
            }
         }
      } catch (UnsatisfiedLinkError var5) {
         log.debug("getJvmThreadInstCpuTimeNs", "Operation not supported: " + var5);
      }

      return new Long(var1);
   }

   public Long getJvmThreadInstBlockTimeMs() throws SnmpStatusException {
      long var1 = 0L;
      ThreadMXBean var3 = JvmThreadingImpl.getThreadMXBean();
      if (var3.isThreadContentionMonitoringSupported()) {
         var1 = this.info.getBlockedTime();
         if (var1 == -1L) {
            var1 = 0L;
         }
      }

      return new Long(var1);
   }

   public Long getJvmThreadInstBlockCount() throws SnmpStatusException {
      return new Long(this.info.getBlockedCount());
   }

   public Long getJvmThreadInstWaitTimeMs() throws SnmpStatusException {
      long var1 = 0L;
      ThreadMXBean var3 = JvmThreadingImpl.getThreadMXBean();
      if (var3.isThreadContentionMonitoringSupported()) {
         var1 = this.info.getWaitedTime();
         if (var1 == -1L) {
            var1 = 0L;
         }
      }

      return new Long(var1);
   }

   public Long getJvmThreadInstWaitCount() throws SnmpStatusException {
      return new Long(this.info.getWaitedCount());
   }

   public Byte[] getJvmThreadInstState() throws SnmpStatusException {
      return JvmThreadInstanceEntryImpl.ThreadStateMap.getState(this.info);
   }

   public Long getJvmThreadInstId() throws SnmpStatusException {
      return new Long(this.info.getThreadId());
   }

   public Byte[] getJvmThreadInstIndex() throws SnmpStatusException {
      return this.index;
   }

   private String getJvmThreadInstStackTrace() throws SnmpStatusException {
      StackTraceElement[] var1 = this.info.getStackTrace();
      StringBuffer var2 = new StringBuffer();
      int var3 = var1.length;
      log.debug("getJvmThreadInstStackTrace", "Stack size : " + var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         log.debug("getJvmThreadInstStackTrace", "Append " + var1[var4].toString());
         var2.append(var1[var4].toString());
         if (var4 < var3) {
            var2.append("\n");
         }
      }

      return this.validPathElementTC(var2.toString());
   }

   public static final class ThreadStateMap {
      public static final byte mask0 = 63;
      public static final byte mask1 = -128;

      private static void setBit(byte[] var0, int var1, byte var2) {
         var0[var1] |= var2;
      }

      public static void setNative(byte[] var0) {
         setBit(var0, 0, (byte)-128);
      }

      public static void setSuspended(byte[] var0) {
         setBit(var0, 0, (byte)64);
      }

      public static void setState(byte[] var0, Thread.State var1) {
         switch(var1) {
         case BLOCKED:
            setBit(var0, 0, (byte)8);
            return;
         case NEW:
            setBit(var0, 0, (byte)32);
            return;
         case RUNNABLE:
            setBit(var0, 0, (byte)16);
            return;
         case TERMINATED:
            setBit(var0, 0, (byte)4);
            return;
         case TIMED_WAITING:
            setBit(var0, 0, (byte)1);
            return;
         case WAITING:
            setBit(var0, 0, (byte)2);
            return;
         default:
         }
      }

      public static void checkOther(byte[] var0) {
         if ((var0[0] & 63) == 0 && (var0[1] & -128) == 0) {
            setBit(var0, 1, (byte)-128);
         }

      }

      public static Byte[] getState(ThreadInfo var0) {
         byte[] var1 = new byte[]{0, 0};

         try {
            Thread.State var2 = var0.getThreadState();
            boolean var3 = var0.isInNative();
            boolean var4 = var0.isSuspended();
            JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", "[State=" + var2 + ",isInNative=" + var3 + ",isSuspended=" + var4 + "]");
            setState(var1, var2);
            if (var3) {
               setNative(var1);
            }

            if (var4) {
               setSuspended(var1);
            }

            checkOther(var1);
         } catch (RuntimeException var5) {
            var1[0] = 0;
            var1[1] = -128;
            JvmThreadInstanceEntryImpl.log.trace("getJvmThreadInstState", "Unexpected exception: " + var5);
            JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", (Throwable)var5);
         }

         Byte[] var6 = new Byte[]{new Byte(var1[0]), new Byte(var1[1])};
         return var6;
      }

      public static final class Byte1 {
         public static final byte other = -128;
         public static final byte reserved10 = 64;
         public static final byte reserved11 = 32;
         public static final byte reserved12 = 16;
         public static final byte reserved13 = 8;
         public static final byte reserved14 = 4;
         public static final byte reserved15 = 2;
         public static final byte reserved16 = 1;
      }

      public static final class Byte0 {
         public static final byte inNative = -128;
         public static final byte suspended = 64;
         public static final byte newThread = 32;
         public static final byte runnable = 16;
         public static final byte blocked = 8;
         public static final byte terminated = 4;
         public static final byte waiting = 2;
         public static final byte timedWaiting = 1;
      }
   }
}
