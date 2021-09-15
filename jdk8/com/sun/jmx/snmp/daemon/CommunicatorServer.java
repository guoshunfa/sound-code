package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.MBeanServerForwarder;

public abstract class CommunicatorServer implements Runnable, MBeanRegistration, NotificationBroadcaster, CommunicatorServerMBean {
   public static final int ONLINE = 0;
   public static final int OFFLINE = 1;
   public static final int STOPPING = 2;
   public static final int STARTING = 3;
   public static final int SNMP_TYPE = 4;
   transient volatile int state = 1;
   ObjectName objectName;
   MBeanServer topMBS;
   MBeanServer bottomMBS;
   transient String dbgTag = null;
   int maxActiveClientCount = 1;
   transient int servedClientCount = 0;
   String host = null;
   int port = -1;
   private transient Object stateLock = new Object();
   private transient Vector<ClientHandler> clientHandlerVector = new Vector();
   private transient Thread mainThread = null;
   private volatile boolean stopRequested = false;
   private boolean interrupted = false;
   private transient Exception startException = null;
   private transient long notifCount = 0L;
   private transient NotificationBroadcasterSupport notifBroadcaster = new NotificationBroadcasterSupport();
   private transient MBeanNotificationInfo[] notifInfos = null;

   public CommunicatorServer(int var1) throws IllegalArgumentException {
      switch(var1) {
      case 4:
         this.dbgTag = this.makeDebugTag();
         return;
      default:
         throw new IllegalArgumentException("Invalid connector Type");
      }
   }

   protected Thread createMainThread() {
      return new Thread(this, this.makeThreadName());
   }

   public void start(long var1) throws CommunicationException, InterruptedException {
      boolean var3;
      synchronized(this.stateLock) {
         if (this.state == 2) {
            this.waitState(1, 60000L);
         }

         var3 = this.state == 1;
         if (var3) {
            this.changeState(3);
            this.stopRequested = false;
            this.interrupted = false;
            this.startException = null;
         }
      }

      if (!var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", "Connector is not OFFLINE");
         }

      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", "--> Start connector ");
         }

         this.mainThread = this.createMainThread();
         this.mainThread.start();
         if (var1 > 0L) {
            this.waitForStart(var1);
         }

      }
   }

   public void start() {
      try {
         this.start(0L);
      } catch (InterruptedException var2) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", (String)"interrupted", (Throwable)var2);
         }
      }

   }

   public void stop() {
      synchronized(this.stateLock) {
         if (this.state == 1 || this.state == 2) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Connector is not ONLINE");
            }

            return;
         }

         this.changeState(2);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Interrupt main thread");
         }

         this.stopRequested = true;
         if (!this.interrupted) {
            this.interrupted = true;
            this.mainThread.interrupt();
         }
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "terminateAllClient");
      }

      this.terminateAllClient();
      synchronized(this.stateLock) {
         if (this.state == 3) {
            this.changeState(1);
         }

      }
   }

   public boolean isActive() {
      synchronized(this.stateLock) {
         return this.state == 0;
      }
   }

   public boolean waitState(int var1, long var2) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", var1 + "(0on,1off,2st) TO=" + var2 + " ; current state = " + this.getStateString());
      }

      long var4 = 0L;
      if (var2 > 0L) {
         var4 = System.currentTimeMillis() + var2;
      }

      synchronized(this.stateLock) {
         while(this.state != var1) {
            if (var2 < 0L) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "timeOut < 0, return without wait");
               }

               return false;
            }

            try {
               if (var2 > 0L) {
                  long var7 = var4 - System.currentTimeMillis();
                  if (var7 <= 0L) {
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "timed out");
                     }

                     boolean var10000 = false;
                     return var10000;
                  }

                  this.stateLock.wait(var7);
               } else {
                  this.stateLock.wait();
               }
            } catch (InterruptedException var10) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "wait interrupted");
               }

               return this.state == var1;
            }
         }

         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "returning in desired state");
         }

         return true;
      }
   }

   private void waitForStart(long var1) throws CommunicationException, InterruptedException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "Timeout=" + var1 + " ; current state = " + this.getStateString());
      }

      long var3 = System.currentTimeMillis();
      synchronized(this.stateLock) {
         while(this.state == 3) {
            long var6 = System.currentTimeMillis() - var3;
            long var8 = var1 - var6;
            if (var8 < 0L) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "timeout < 0, return without wait");
               }

               throw new InterruptedException("Timeout expired");
            }

            try {
               this.stateLock.wait(var8);
            } catch (InterruptedException var12) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "wait interrupted");
               }

               if (this.state != 0) {
                  throw var12;
               }
            }
         }

         if (this.state == 0) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "started");
            }

         } else if (this.startException instanceof CommunicationException) {
            throw (CommunicationException)this.startException;
         } else if (this.startException instanceof InterruptedException) {
            throw (InterruptedException)this.startException;
         } else if (this.startException != null) {
            throw new CommunicationException(this.startException, "Failed to start: " + this.startException);
         } else {
            throw new CommunicationException("Failed to start: state is " + getStringForState(this.state));
         }
      }
   }

   public int getState() {
      synchronized(this.stateLock) {
         return this.state;
      }
   }

   public String getStateString() {
      return getStringForState(this.state);
   }

   public String getHost() {
      try {
         this.host = InetAddress.getLocalHost().getHostName();
      } catch (Exception var2) {
         this.host = "Unknown host";
      }

      return this.host;
   }

   public int getPort() {
      synchronized(this.stateLock) {
         return this.port;
      }
   }

   public void setPort(int var1) throws IllegalStateException {
      synchronized(this.stateLock) {
         if (this.state != 0 && this.state != 3) {
            this.port = var1;
            this.dbgTag = this.makeDebugTag();
         } else {
            throw new IllegalStateException("Stop server before carrying out this operation");
         }
      }
   }

   public abstract String getProtocol();

   int getServedClientCount() {
      return this.servedClientCount;
   }

   int getActiveClientCount() {
      int var1 = this.clientHandlerVector.size();
      return var1;
   }

   int getMaxActiveClientCount() {
      return this.maxActiveClientCount;
   }

   void setMaxActiveClientCount(int var1) throws IllegalStateException {
      synchronized(this.stateLock) {
         if (this.state != 0 && this.state != 3) {
            this.maxActiveClientCount = var1;
         } else {
            throw new IllegalStateException("Stop server before carrying out this operation");
         }
      }
   }

   void notifyClientHandlerCreated(ClientHandler var1) {
      this.clientHandlerVector.addElement(var1);
   }

   synchronized void notifyClientHandlerDeleted(ClientHandler var1) {
      this.clientHandlerVector.removeElement(var1);
      this.notifyAll();
   }

   protected int getBindTries() {
      return 50;
   }

   protected long getBindSleepTime() {
      return 100L;
   }

   public void run() {
      int var1 = 0;
      boolean var2 = false;

      try {
         int var46 = this.getBindTries();
         long var4 = this.getBindSleepTime();

         while(var1 < var46 && !var2) {
            try {
               this.doBind();
               var2 = true;
            } catch (CommunicationException var37) {
               ++var1;

               try {
                  Thread.sleep(var4);
               } catch (InterruptedException var36) {
                  throw var36;
               }
            }
         }

         if (!var2) {
            this.doBind();
         }
      } catch (Exception var45) {
         Exception var3 = var45;
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var45);
         }

         synchronized(this.stateLock) {
            this.startException = var3;
            this.changeState(1);
         }

         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE");
         }

         this.doError(var45);
         return;
      }

      boolean var30 = false;

      label390: {
         label391: {
            try {
               var30 = true;
               this.changeState(0);
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is ONLINE");
               }

               while(!this.stopRequested) {
                  ++this.servedClientCount;
                  this.doReceive();
                  this.waitIfTooManyClients();
                  this.doProcess();
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "Stop has been requested");
                  var30 = false;
               } else {
                  var30 = false;
               }
               break label390;
            } catch (InterruptedException var42) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", "Interrupt caught");
               }

               this.changeState(2);
               var30 = false;
               break label391;
            } catch (Exception var43) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var43);
               }

               this.changeState(2);
               var30 = false;
            } finally {
               if (var30) {
                  synchronized(this.stateLock) {
                     this.interrupted = true;
                     Thread.interrupted();
                  }

                  try {
                     this.doUnbind();
                     this.waitClientTermination();
                     this.changeState(1);
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE");
                     }
                  } catch (Exception var38) {
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var38);
                     }

                     this.changeState(1);
                  }

               }
            }

            synchronized(this.stateLock) {
               this.interrupted = true;
               Thread.interrupted();
            }

            try {
               this.doUnbind();
               this.waitClientTermination();
               this.changeState(1);
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE");
                  return;
               }
            } catch (Exception var39) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var39);
               }

               this.changeState(1);
            }

            return;
         }

         synchronized(this.stateLock) {
            this.interrupted = true;
            Thread.interrupted();
         }

         try {
            this.doUnbind();
            this.waitClientTermination();
            this.changeState(1);
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE");
               return;
            }
         } catch (Exception var40) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var40);
            }

            this.changeState(1);
         }

         return;
      }

      synchronized(this.stateLock) {
         this.interrupted = true;
         Thread.interrupted();
      }

      try {
         this.doUnbind();
         this.waitClientTermination();
         this.changeState(1);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE");
         }
      } catch (Exception var41) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", (String)"Got unexpected exception", (Throwable)var41);
         }

         this.changeState(1);
      }

   }

   protected abstract void doError(Exception var1) throws CommunicationException;

   protected abstract void doBind() throws CommunicationException, InterruptedException;

   protected abstract void doReceive() throws CommunicationException, InterruptedException;

   protected abstract void doProcess() throws CommunicationException, InterruptedException;

   protected abstract void doUnbind() throws CommunicationException, InterruptedException;

   public synchronized MBeanServer getMBeanServer() {
      return this.topMBS;
   }

   public synchronized void setMBeanServer(MBeanServer var1) throws IllegalArgumentException, IllegalStateException {
      synchronized(this.stateLock) {
         if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException("Stop server before carrying out this operation");
         }
      }

      Vector var3 = new Vector();

      for(MBeanServer var4 = var1; var4 != this.bottomMBS; var4 = ((MBeanServerForwarder)var4).getMBeanServer()) {
         if (!(var4 instanceof MBeanServerForwarder)) {
            throw new IllegalArgumentException("MBeanServer argument must be MBean server where this server is registered, or an MBeanServerForwarder leading to that server");
         }

         if (var3.contains(var4)) {
            throw new IllegalArgumentException("MBeanServerForwarder loop");
         }

         var3.addElement(var4);
      }

      this.topMBS = var1;
   }

   ObjectName getObjectName() {
      return this.objectName;
   }

   void changeState(int var1) {
      int var2;
      synchronized(this.stateLock) {
         if (this.state == var1) {
            return;
         }

         var2 = this.state;
         this.state = var1;
         this.stateLock.notifyAll();
      }

      this.sendStateChangeNotification(var2, var1);
   }

   String makeDebugTag() {
      return "CommunicatorServer[" + this.getProtocol() + ":" + this.getPort() + "]";
   }

   String makeThreadName() {
      String var1;
      if (this.objectName == null) {
         var1 = "CommunicatorServer";
      } else {
         var1 = this.objectName.toString();
      }

      return var1;
   }

   private synchronized void waitIfTooManyClients() throws InterruptedException {
      for(; this.getActiveClientCount() >= this.maxActiveClientCount; this.wait()) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitIfTooManyClients", "Waiting for a client to terminate");
         }
      }

   }

   private void waitClientTermination() {
      int var1 = this.clientHandlerVector.size();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && var1 >= 1) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", "waiting for " + var1 + " clients to terminate");
      }

      while(!this.clientHandlerVector.isEmpty()) {
         try {
            ((ClientHandler)this.clientHandlerVector.firstElement()).join();
         } catch (NoSuchElementException var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", (String)"No elements left", (Throwable)var3);
            }
         }
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && var1 >= 1) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", "Ok, let's go...");
      }

   }

   private void terminateAllClient() {
      int var1 = this.clientHandlerVector.size();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && var1 >= 1) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "terminateAllClient", "Interrupting " + var1 + " clients");
      }

      ClientHandler[] var2 = (ClientHandler[])this.clientHandlerVector.toArray(new ClientHandler[0]);
      ClientHandler[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ClientHandler var6 = var3[var5];

         try {
            var6.interrupt();
         } catch (Exception var8) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "terminateAllClient", (String)"Failed to interrupt pending request. Ignore the exception.", (Throwable)var8);
            }
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.stateLock = new Object();
      this.state = 1;
      this.stopRequested = false;
      this.servedClientCount = 0;
      this.clientHandlerVector = new Vector();
      this.mainThread = null;
      this.notifCount = 0L;
      this.notifInfos = null;
      this.notifBroadcaster = new NotificationBroadcasterSupport();
      this.dbgTag = this.makeDebugTag();
   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws IllegalArgumentException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "addNotificationListener", "Adding listener " + var1 + " with filter " + var2 + " and handback " + var3);
      }

      this.notifBroadcaster.addNotificationListener(var1, var2, var3);
   }

   public void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "removeNotificationListener", "Removing listener " + var1);
      }

      this.notifBroadcaster.removeNotificationListener(var1);
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      if (this.notifInfos == null) {
         this.notifInfos = new MBeanNotificationInfo[1];
         String[] var1 = new String[]{"jmx.attribute.change"};
         this.notifInfos[0] = new MBeanNotificationInfo(var1, AttributeChangeNotification.class.getName(), "Sent to notify that the value of the State attribute of this CommunicatorServer instance has changed.");
      }

      return (MBeanNotificationInfo[])this.notifInfos.clone();
   }

   private void sendStateChangeNotification(int var1, int var2) {
      String var3 = getStringForState(var1);
      String var4 = getStringForState(var2);
      String var5 = this.dbgTag + " The value of attribute State has changed from " + var1 + " (" + var3 + ") to " + var2 + " (" + var4 + ").";
      ++this.notifCount;
      AttributeChangeNotification var6 = new AttributeChangeNotification(this, this.notifCount, System.currentTimeMillis(), var5, "State", "int", new Integer(var1), new Integer(var2));
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendStateChangeNotification", "Sending AttributeChangeNotification #" + this.notifCount + " with message: " + var5);
      }

      this.notifBroadcaster.sendNotification(var6);
   }

   private static String getStringForState(int var0) {
      switch(var0) {
      case 0:
         return "ONLINE";
      case 1:
         return "OFFLINE";
      case 2:
         return "STOPPING";
      case 3:
         return "STARTING";
      default:
         return "UNDEFINED";
      }
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.objectName = var2;
      synchronized(this) {
         if (this.bottomMBS != null) {
            throw new IllegalArgumentException("connector already registered in an MBean server");
         }

         this.topMBS = this.bottomMBS = var1;
      }

      this.dbgTag = this.makeDebugTag();
      return var2;
   }

   public void postRegister(Boolean var1) {
      if (!var1) {
         synchronized(this) {
            this.topMBS = this.bottomMBS = null;
         }
      }

   }

   public void preDeregister() throws Exception {
      synchronized(this) {
         this.topMBS = this.bottomMBS = null;
      }

      this.objectName = null;
      int var1 = this.getState();
      if (var1 == 0 || var1 == 3) {
         this.stop();
      }

   }

   public void postDeregister() {
   }
}
