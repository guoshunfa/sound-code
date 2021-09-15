package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class Monitor extends NotificationBroadcasterSupport implements MonitorMBean, MBeanRegistration {
   private String observedAttribute;
   private long granularityPeriod = 10000L;
   private boolean isActive = false;
   private final AtomicLong sequenceNumber = new AtomicLong();
   private boolean isComplexTypeAttribute = false;
   private String firstAttribute;
   private final List<String> remainingAttributes = new CopyOnWriteArrayList();
   private static final AccessControlContext noPermissionsACC = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, (PermissionCollection)null)});
   private volatile AccessControlContext acc;
   private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new Monitor.DaemonThreadFactory("Scheduler"));
   private static final Map<ThreadPoolExecutor, Void> executors = new WeakHashMap();
   private static final Object executorsLock = new Object();
   private static final int maximumPoolSize;
   private Future<?> monitorFuture;
   private final Monitor.SchedulerTask schedulerTask;
   private ScheduledFuture<?> schedulerFuture;
   protected static final int capacityIncrement = 16;
   protected int elementCount;
   /** @deprecated */
   @Deprecated
   protected int alreadyNotified;
   protected int[] alreadyNotifieds;
   protected MBeanServer server;
   protected static final int RESET_FLAGS_ALREADY_NOTIFIED = 0;
   protected static final int OBSERVED_OBJECT_ERROR_NOTIFIED = 1;
   protected static final int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED = 2;
   protected static final int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED = 4;
   protected static final int RUNTIME_ERROR_NOTIFIED = 8;
   /** @deprecated */
   @Deprecated
   protected String dbgTag;
   final List<Monitor.ObservedObject> observedObjects;
   static final int THRESHOLD_ERROR_NOTIFIED = 16;
   static final Integer INTEGER_ZERO;

   public Monitor() {
      this.acc = noPermissionsACC;
      this.schedulerTask = new Monitor.SchedulerTask();
      this.elementCount = 0;
      this.alreadyNotified = 0;
      this.alreadyNotifieds = new int[16];
      this.dbgTag = Monitor.class.getName();
      this.observedObjects = new CopyOnWriteArrayList();
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preRegister(MBeanServer, ObjectName)", "initialize the reference on the MBean server");
      this.server = var1;
      return var2;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preDeregister()", "stop the monitor");
      this.stop();
   }

   public void postDeregister() {
   }

   public abstract void start();

   public abstract void stop();

   /** @deprecated */
   @Deprecated
   public synchronized ObjectName getObservedObject() {
      return this.observedObjects.isEmpty() ? null : ((Monitor.ObservedObject)this.observedObjects.get(0)).getObservedObject();
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setObservedObject(ObjectName var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null observed object");
      } else if (this.observedObjects.size() != 1 || !this.containsObservedObject(var1)) {
         this.observedObjects.clear();
         this.addObservedObject(var1);
      }
   }

   public synchronized void addObservedObject(ObjectName var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null observed object");
      } else if (!this.containsObservedObject(var1)) {
         Monitor.ObservedObject var2 = this.createObservedObject(var1);
         var2.setAlreadyNotified(0);
         var2.setDerivedGauge(INTEGER_ZERO);
         var2.setDerivedGaugeTimeStamp(System.currentTimeMillis());
         this.observedObjects.add(var2);
         this.createAlreadyNotified();
      }
   }

   public synchronized void removeObservedObject(ObjectName var1) {
      if (var1 != null) {
         Monitor.ObservedObject var2 = this.getObservedObject(var1);
         if (var2 != null) {
            this.observedObjects.remove(var2);
            this.createAlreadyNotified();
         }

      }
   }

   public synchronized boolean containsObservedObject(ObjectName var1) {
      return this.getObservedObject(var1) != null;
   }

   public synchronized ObjectName[] getObservedObjects() {
      ObjectName[] var1 = new ObjectName[this.observedObjects.size()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = ((Monitor.ObservedObject)this.observedObjects.get(var2)).getObservedObject();
      }

      return var1;
   }

   public synchronized String getObservedAttribute() {
      return this.observedAttribute;
   }

   public void setObservedAttribute(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null observed attribute");
      } else {
         synchronized(this) {
            if (this.observedAttribute == null || !this.observedAttribute.equals(var1)) {
               this.observedAttribute = var1;
               this.cleanupIsComplexTypeAttribute();
               int var3 = 0;
               Iterator var4 = this.observedObjects.iterator();

               while(var4.hasNext()) {
                  Monitor.ObservedObject var5 = (Monitor.ObservedObject)var4.next();
                  this.resetAlreadyNotified(var5, var3++, 6);
               }

            }
         }
      }
   }

   public synchronized long getGranularityPeriod() {
      return this.granularityPeriod;
   }

   public synchronized void setGranularityPeriod(long var1) throws IllegalArgumentException {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("Nonpositive granularity period");
      } else if (this.granularityPeriod != var1) {
         this.granularityPeriod = var1;
         if (this.isActive()) {
            this.cleanupFutures();
            this.schedulerFuture = scheduler.schedule((Runnable)this.schedulerTask, var1, TimeUnit.MILLISECONDS);
         }

      }
   }

   public synchronized boolean isActive() {
      return this.isActive;
   }

   void doStart() {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "start the monitor");
      synchronized(this) {
         if (this.isActive()) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "the monitor is already active");
         } else {
            this.isActive = true;
            this.cleanupIsComplexTypeAttribute();
            this.acc = AccessController.getContext();
            this.cleanupFutures();
            this.schedulerTask.setMonitorTask(new Monitor.MonitorTask());
            this.schedulerFuture = scheduler.schedule((Runnable)this.schedulerTask, this.getGranularityPeriod(), TimeUnit.MILLISECONDS);
         }
      }
   }

   void doStop() {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "stop the monitor");
      synchronized(this) {
         if (!this.isActive()) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "the monitor is not active");
         } else {
            this.isActive = false;
            this.cleanupFutures();
            this.acc = noPermissionsACC;
            this.cleanupIsComplexTypeAttribute();
         }
      }
   }

   synchronized Object getDerivedGauge(ObjectName var1) {
      Monitor.ObservedObject var2 = this.getObservedObject(var1);
      return var2 == null ? null : var2.getDerivedGauge();
   }

   synchronized long getDerivedGaugeTimeStamp(ObjectName var1) {
      Monitor.ObservedObject var2 = this.getObservedObject(var1);
      return var2 == null ? 0L : var2.getDerivedGaugeTimeStamp();
   }

   Object getAttribute(MBeanServerConnection var1, ObjectName var2, String var3) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
      boolean var4;
      synchronized(this) {
         if (!this.isActive()) {
            throw new IllegalArgumentException("The monitor has been stopped");
         }

         if (!var3.equals(this.getObservedAttribute())) {
            throw new IllegalArgumentException("The observed attribute has been changed");
         }

         var4 = this.firstAttribute == null && var3.indexOf(46) != -1;
      }

      MBeanInfo var5;
      if (var4) {
         try {
            var5 = var1.getMBeanInfo(var2);
         } catch (IntrospectionException var14) {
            throw new IllegalArgumentException(var14);
         }
      } else {
         var5 = null;
      }

      String var6;
      synchronized(this) {
         if (!this.isActive()) {
            throw new IllegalArgumentException("The monitor has been stopped");
         }

         if (!var3.equals(this.getObservedAttribute())) {
            throw new IllegalArgumentException("The observed attribute has been changed");
         }

         if (this.firstAttribute == null) {
            if (var3.indexOf(46) == -1) {
               this.firstAttribute = var3;
            } else {
               MBeanAttributeInfo[] var8 = var5.getAttributes();
               MBeanAttributeInfo[] var9 = var8;
               int var10 = var8.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  MBeanAttributeInfo var12 = var9[var11];
                  if (var3.equals(var12.getName())) {
                     this.firstAttribute = var3;
                     break;
                  }
               }

               if (this.firstAttribute == null) {
                  String[] var17 = var3.split("\\.", -1);
                  this.firstAttribute = var17[0];
                  var10 = 1;

                  while(true) {
                     if (var10 >= var17.length) {
                        this.isComplexTypeAttribute = true;
                        break;
                     }

                     this.remainingAttributes.add(var17[var10]);
                     ++var10;
                  }
               }
            }
         }

         var6 = this.firstAttribute;
      }

      return var1.getAttribute(var2, var6);
   }

   Comparable<?> getComparableFromAttribute(ObjectName var1, String var2, Object var3) throws AttributeNotFoundException {
      if (!this.isComplexTypeAttribute) {
         return (Comparable)var3;
      } else {
         Object var4 = var3;

         String var6;
         for(Iterator var5 = this.remainingAttributes.iterator(); var5.hasNext(); var4 = Introspector.elementFromComplex(var4, var6)) {
            var6 = (String)var5.next();
         }

         return (Comparable)var4;
      }
   }

   boolean isComparableTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      return true;
   }

   String buildErrorNotification(ObjectName var1, String var2, Comparable<?> var3) {
      return null;
   }

   void onErrorNotification(MonitorNotification var1) {
   }

   Comparable<?> getDerivedGaugeFromComparable(ObjectName var1, String var2, Comparable<?> var3) {
      return var3;
   }

   MonitorNotification buildAlarmNotification(ObjectName var1, String var2, Comparable<?> var3) {
      return null;
   }

   boolean isThresholdTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      return true;
   }

   static Class<? extends Number> classForType(Monitor.NumericalType var0) {
      switch(var0) {
      case BYTE:
         return Byte.class;
      case SHORT:
         return Short.class;
      case INTEGER:
         return Integer.class;
      case LONG:
         return Long.class;
      case FLOAT:
         return Float.class;
      case DOUBLE:
         return Double.class;
      default:
         throw new IllegalArgumentException("Unsupported numerical type");
      }
   }

   static boolean isValidForType(Object var0, Class<? extends Number> var1) {
      return var0 == INTEGER_ZERO || var1.isInstance(var0);
   }

   synchronized Monitor.ObservedObject getObservedObject(ObjectName var1) {
      Iterator var2 = this.observedObjects.iterator();

      Monitor.ObservedObject var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Monitor.ObservedObject)var2.next();
      } while(!var3.getObservedObject().equals(var1));

      return var3;
   }

   Monitor.ObservedObject createObservedObject(ObjectName var1) {
      return new Monitor.ObservedObject(var1);
   }

   synchronized void createAlreadyNotified() {
      this.elementCount = this.observedObjects.size();
      this.alreadyNotifieds = new int[this.elementCount];

      for(int var1 = 0; var1 < this.elementCount; ++var1) {
         this.alreadyNotifieds[var1] = ((Monitor.ObservedObject)this.observedObjects.get(var1)).getAlreadyNotified();
      }

      this.updateDeprecatedAlreadyNotified();
   }

   synchronized void updateDeprecatedAlreadyNotified() {
      if (this.elementCount > 0) {
         this.alreadyNotified = this.alreadyNotifieds[0];
      } else {
         this.alreadyNotified = 0;
      }

   }

   synchronized void updateAlreadyNotified(Monitor.ObservedObject var1, int var2) {
      this.alreadyNotifieds[var2] = var1.getAlreadyNotified();
      if (var2 == 0) {
         this.updateDeprecatedAlreadyNotified();
      }

   }

   synchronized boolean isAlreadyNotified(Monitor.ObservedObject var1, int var2) {
      return (var1.getAlreadyNotified() & var2) != 0;
   }

   synchronized void setAlreadyNotified(Monitor.ObservedObject var1, int var2, int var3, int[] var4) {
      int var5 = this.computeAlreadyNotifiedIndex(var1, var2, var4);
      if (var5 != -1) {
         var1.setAlreadyNotified(var1.getAlreadyNotified() | var3);
         this.updateAlreadyNotified(var1, var5);
      }
   }

   synchronized void resetAlreadyNotified(Monitor.ObservedObject var1, int var2, int var3) {
      var1.setAlreadyNotified(var1.getAlreadyNotified() & ~var3);
      this.updateAlreadyNotified(var1, var2);
   }

   synchronized void resetAllAlreadyNotified(Monitor.ObservedObject var1, int var2, int[] var3) {
      int var4 = this.computeAlreadyNotifiedIndex(var1, var2, var3);
      if (var4 != -1) {
         var1.setAlreadyNotified(0);
         this.updateAlreadyNotified(var1, var2);
      }
   }

   synchronized int computeAlreadyNotifiedIndex(Monitor.ObservedObject var1, int var2, int[] var3) {
      return var3 == this.alreadyNotifieds ? var2 : this.observedObjects.indexOf(var1);
   }

   private void sendNotification(String var1, long var2, String var4, Object var5, Object var6, ObjectName var7, boolean var8) {
      if (this.isActive()) {
         if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "sendNotification", "send notification: \n\tNotification observed object = " + var7 + "\n\tNotification observed attribute = " + this.observedAttribute + "\n\tNotification derived gauge = " + var5);
         }

         long var9 = this.sequenceNumber.getAndIncrement();
         MonitorNotification var11 = new MonitorNotification(var1, this, var9, var2, var4, var7, this.observedAttribute, var5, var6);
         if (var8) {
            this.onErrorNotification(var11);
         }

         this.sendNotification(var11);
      }
   }

   private void monitor(Monitor.ObservedObject var1, int var2, int[] var3) {
      String var5 = null;
      String var6 = null;
      Comparable var7 = null;
      Object var8 = null;
      Comparable var10 = null;
      MonitorNotification var11 = null;
      if (this.isActive()) {
         String var4;
         ObjectName var9;
         synchronized(this) {
            var9 = var1.getObservedObject();
            var4 = this.getObservedAttribute();
            if (var9 == null || var4 == null) {
               return;
            }
         }

         Object var12 = null;

         try {
            var12 = this.getAttribute(this.server, var9, var4);
            if (var12 == null) {
               if (this.isAlreadyNotified(var1, 4)) {
                  return;
               }

               var5 = "jmx.monitor.error.type";
               this.setAlreadyNotified(var1, var2, 4, var3);
               var6 = "The observed attribute value is null.";
               JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            }
         } catch (NullPointerException var20) {
            if (this.isAlreadyNotified(var1, 8)) {
               return;
            }

            var5 = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(var1, var2, 8, var3);
            var6 = "The monitor must be registered in the MBean server or an MBeanServerConnection must be explicitly supplied.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var20.toString());
         } catch (InstanceNotFoundException var21) {
            if (this.isAlreadyNotified(var1, 1)) {
               return;
            }

            var5 = "jmx.monitor.error.mbean";
            this.setAlreadyNotified(var1, var2, 1, var3);
            var6 = "The observed object must be accessible in the MBeanServerConnection.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var21.toString());
         } catch (AttributeNotFoundException var22) {
            if (this.isAlreadyNotified(var1, 2)) {
               return;
            }

            var5 = "jmx.monitor.error.attribute";
            this.setAlreadyNotified(var1, var2, 2, var3);
            var6 = "The observed attribute must be accessible in the observed object.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var22.toString());
         } catch (MBeanException var23) {
            if (this.isAlreadyNotified(var1, 8)) {
               return;
            }

            var5 = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(var1, var2, 8, var3);
            var6 = var23.getMessage() == null ? "" : var23.getMessage();
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var23.toString());
         } catch (ReflectionException var24) {
            if (this.isAlreadyNotified(var1, 8)) {
               return;
            }

            var5 = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(var1, var2, 8, var3);
            var6 = var24.getMessage() == null ? "" : var24.getMessage();
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var24.toString());
         } catch (IOException var25) {
            if (this.isAlreadyNotified(var1, 8)) {
               return;
            }

            var5 = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(var1, var2, 8, var3);
            var6 = var25.getMessage() == null ? "" : var25.getMessage();
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var25.toString());
         } catch (RuntimeException var26) {
            if (this.isAlreadyNotified(var1, 8)) {
               return;
            }

            var5 = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(var1, var2, 8, var3);
            var6 = var26.getMessage() == null ? "" : var26.getMessage();
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var26.toString());
         }

         synchronized(this) {
            if (!this.isActive()) {
               return;
            }

            if (!var4.equals(this.getObservedAttribute())) {
               return;
            }

            if (var6 == null) {
               try {
                  var10 = this.getComparableFromAttribute(var9, var4, var12);
               } catch (ClassCastException var16) {
                  if (this.isAlreadyNotified(var1, 4)) {
                     return;
                  }

                  var5 = "jmx.monitor.error.type";
                  this.setAlreadyNotified(var1, var2, 4, var3);
                  var6 = "The observed attribute value does not implement the Comparable interface.";
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var16.toString());
               } catch (AttributeNotFoundException var17) {
                  if (this.isAlreadyNotified(var1, 2)) {
                     return;
                  }

                  var5 = "jmx.monitor.error.attribute";
                  this.setAlreadyNotified(var1, var2, 2, var3);
                  var6 = "The observed attribute must be accessible in the observed object.";
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var17.toString());
               } catch (RuntimeException var18) {
                  if (this.isAlreadyNotified(var1, 8)) {
                     return;
                  }

                  var5 = "jmx.monitor.error.runtime";
                  this.setAlreadyNotified(var1, var2, 8, var3);
                  var6 = var18.getMessage() == null ? "" : var18.getMessage();
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var18.toString());
               }
            }

            if (var6 == null && !this.isComparableTypeValid(var9, var4, var10)) {
               if (this.isAlreadyNotified(var1, 4)) {
                  return;
               }

               var5 = "jmx.monitor.error.type";
               this.setAlreadyNotified(var1, var2, 4, var3);
               var6 = "The observed attribute type is not valid.";
               JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            }

            if (var6 == null && !this.isThresholdTypeValid(var9, var4, var10)) {
               if (this.isAlreadyNotified(var1, 16)) {
                  return;
               }

               var5 = "jmx.monitor.error.threshold";
               this.setAlreadyNotified(var1, var2, 16, var3);
               var6 = "The threshold type is not valid.";
               JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
            }

            if (var6 == null) {
               var6 = this.buildErrorNotification(var9, var4, var10);
               if (var6 != null) {
                  if (this.isAlreadyNotified(var1, 8)) {
                     return;
                  }

                  var5 = "jmx.monitor.error.runtime";
                  this.setAlreadyNotified(var1, var2, 8, var3);
                  JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", var6);
               }
            }

            if (var6 == null) {
               this.resetAllAlreadyNotified(var1, var2, var3);
               var7 = this.getDerivedGaugeFromComparable(var9, var4, var10);
               var1.setDerivedGauge(var7);
               var1.setDerivedGaugeTimeStamp(System.currentTimeMillis());
               var11 = this.buildAlarmNotification(var9, var4, (Comparable)var7);
            }
         }

         if (var6 != null) {
            this.sendNotification(var5, System.currentTimeMillis(), var6, var7, var8, var9, true);
         }

         if (var11 != null && var11.getType() != null) {
            this.sendNotification(var11.getType(), System.currentTimeMillis(), var11.getMessage(), var7, var11.getTrigger(), var9, false);
         }

      }
   }

   private synchronized void cleanupFutures() {
      if (this.schedulerFuture != null) {
         this.schedulerFuture.cancel(false);
         this.schedulerFuture = null;
      }

      if (this.monitorFuture != null) {
         this.monitorFuture.cancel(false);
         this.monitorFuture = null;
      }

   }

   private synchronized void cleanupIsComplexTypeAttribute() {
      this.firstAttribute = null;
      this.remainingAttributes.clear();
      this.isComplexTypeAttribute = false;
   }

   static {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jmx.x.monitor.maximum.pool.size")));
      if (var1 != null && var1.trim().length() != 0) {
         boolean var2 = true;

         int var5;
         try {
            var5 = Integer.parseInt(var1);
         } catch (NumberFormatException var4) {
            if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", (String)"Wrong value for jmx.x.monitor.maximum.pool.size system property", (Throwable)var4);
               JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "jmx.x.monitor.maximum.pool.size defaults to 10");
            }

            var5 = 10;
         }

         if (var5 < 1) {
            maximumPoolSize = 1;
         } else {
            maximumPoolSize = var5;
         }
      } else {
         maximumPoolSize = 10;
      }

      INTEGER_ZERO = 0;
   }

   private static class DaemonThreadFactory implements ThreadFactory {
      final ThreadGroup group;
      final AtomicInteger threadNumber = new AtomicInteger(1);
      final String namePrefix;
      static final String nameSuffix = "]";

      public DaemonThreadFactory(String var1) {
         SecurityManager var2 = System.getSecurityManager();
         this.group = var2 != null ? var2.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "JMX Monitor " + var1 + " Pool [Thread-";
      }

      public DaemonThreadFactory(String var1, ThreadGroup var2) {
         this.group = var2;
         this.namePrefix = "JMX Monitor " + var1 + " Pool [Thread-";
      }

      public ThreadGroup getThreadGroup() {
         return this.group;
      }

      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(this.group, var1, this.namePrefix + this.threadNumber.getAndIncrement() + "]", 0L);
         var2.setDaemon(true);
         if (var2.getPriority() != 5) {
            var2.setPriority(5);
         }

         return var2;
      }
   }

   private class MonitorTask implements Runnable {
      private ThreadPoolExecutor executor;

      public MonitorTask() {
         SecurityManager var2 = System.getSecurityManager();
         ThreadGroup var3 = var2 != null ? var2.getThreadGroup() : Thread.currentThread().getThreadGroup();
         synchronized(Monitor.executorsLock) {
            Iterator var5 = Monitor.executors.keySet().iterator();

            while(var5.hasNext()) {
               ThreadPoolExecutor var6 = (ThreadPoolExecutor)var5.next();
               Monitor.DaemonThreadFactory var7 = (Monitor.DaemonThreadFactory)var6.getThreadFactory();
               ThreadGroup var8 = var7.getThreadGroup();
               if (var8 == var3) {
                  this.executor = var6;
                  break;
               }
            }

            if (this.executor == null) {
               this.executor = new ThreadPoolExecutor(Monitor.maximumPoolSize, Monitor.maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new Monitor.DaemonThreadFactory("ThreadGroup<" + var3.getName() + "> Executor", var3));
               this.executor.allowCoreThreadTimeOut(true);
               Monitor.executors.put(this.executor, (Object)null);
            }

         }
      }

      public Future<?> submit() {
         return this.executor.submit(this);
      }

      public void run() {
         ScheduledFuture var1;
         AccessControlContext var2;
         synchronized(Monitor.this) {
            var1 = Monitor.this.schedulerFuture;
            var2 = Monitor.this.acc;
         }

         PrivilegedAction var3 = new PrivilegedAction<Void>() {
            public Void run() {
               if (Monitor.this.isActive()) {
                  int[] var1 = Monitor.this.alreadyNotifieds;
                  int var2 = 0;
                  Iterator var3 = Monitor.this.observedObjects.iterator();

                  while(var3.hasNext()) {
                     Monitor.ObservedObject var4 = (Monitor.ObservedObject)var3.next();
                     if (Monitor.this.isActive()) {
                        Monitor.this.monitor(var4, var2++, var1);
                     }
                  }
               }

               return null;
            }
         };
         if (var2 == null) {
            throw new SecurityException("AccessControlContext cannot be null");
         } else {
            AccessController.doPrivileged(var3, var2);
            synchronized(Monitor.this) {
               if (Monitor.this.isActive() && Monitor.this.schedulerFuture == var1) {
                  Monitor.this.monitorFuture = null;
                  Monitor.this.schedulerFuture = Monitor.scheduler.schedule((Runnable)Monitor.this.schedulerTask, Monitor.this.getGranularityPeriod(), TimeUnit.MILLISECONDS);
               }

            }
         }
      }
   }

   private class SchedulerTask implements Runnable {
      private Monitor.MonitorTask task;

      public SchedulerTask() {
      }

      public void setMonitorTask(Monitor.MonitorTask var1) {
         this.task = var1;
      }

      public void run() {
         synchronized(Monitor.this) {
            Monitor.this.monitorFuture = this.task.submit();
         }
      }
   }

   static enum NumericalType {
      BYTE,
      SHORT,
      INTEGER,
      LONG,
      FLOAT,
      DOUBLE;
   }

   static class ObservedObject {
      private final ObjectName observedObject;
      private int alreadyNotified;
      private Object derivedGauge;
      private long derivedGaugeTimeStamp;

      public ObservedObject(ObjectName var1) {
         this.observedObject = var1;
      }

      public final ObjectName getObservedObject() {
         return this.observedObject;
      }

      public final synchronized int getAlreadyNotified() {
         return this.alreadyNotified;
      }

      public final synchronized void setAlreadyNotified(int var1) {
         this.alreadyNotified = var1;
      }

      public final synchronized Object getDerivedGauge() {
         return this.derivedGauge;
      }

      public final synchronized void setDerivedGauge(Object var1) {
         this.derivedGauge = var1;
      }

      public final synchronized long getDerivedGaugeTimeStamp() {
         return this.derivedGaugeTimeStamp;
      }

      public final synchronized void setDerivedGaugeTimeStamp(long var1) {
         this.derivedGaugeTimeStamp = var1;
      }
   }
}
