package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public abstract class SnmpMibTable extends SnmpMibNode implements NotificationBroadcaster, Serializable {
   protected int nodeId = 1;
   protected SnmpMib theMib;
   protected boolean creationEnabled = false;
   protected SnmpTableEntryFactory factory = null;
   private int size = 0;
   private static final int Delta = 16;
   private int tablecount = 0;
   private int tablesize = 16;
   private SnmpOid[] tableoids;
   private final Vector<Object> entries;
   private final Vector<ObjectName> entrynames;
   private Hashtable<NotificationListener, Vector<Object>> handbackTable;
   private Hashtable<NotificationListener, Vector<NotificationFilter>> filterTable;
   transient long sequenceNumber;

   public SnmpMibTable(SnmpMib var1) {
      this.tableoids = new SnmpOid[this.tablesize];
      this.entries = new Vector();
      this.entrynames = new Vector();
      this.handbackTable = new Hashtable();
      this.filterTable = new Hashtable();
      this.sequenceNumber = 0L;
      this.theMib = var1;
      this.setCreationEnabled(false);
   }

   public abstract void createNewEntry(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException;

   public abstract boolean isRegistrationRequired();

   public boolean isCreationEnabled() {
      return this.creationEnabled;
   }

   public void setCreationEnabled(boolean var1) {
      this.creationEnabled = var1;
   }

   public boolean hasRowStatus() {
      return false;
   }

   public void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      boolean var3 = var1.isNewEntry();
      SnmpMibSubRequest var4 = var1;
      if (var3) {
         Enumeration var6 = var1.getElements();

         while(var6.hasMoreElements()) {
            SnmpVarBind var5 = (SnmpVarBind)var6.nextElement();
            var4.registerGetException(var5, new SnmpStatusException(224));
         }
      }

      SnmpOid var7 = var4.getEntryOid();
      this.get(var1, var7, var2 + 1);
   }

   public void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      SnmpOid var3 = var1.getEntryOid();
      int var4 = this.getRowAction(var1, var3, var2 + 1);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "Calling beginRowAction");
      }

      this.beginRowAction(var1, var3, var2 + 1, var4);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "Calling check for " + var1.getSize() + " varbinds");
      }

      this.check(var1, var3, var2 + 1);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "check finished");
      }

   }

   public void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Entering set");
      }

      SnmpOid var3 = var1.getEntryOid();
      int var4 = this.getRowAction(var1, var3, var2 + 1);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Calling set for " + var1.getSize() + " varbinds");
      }

      this.set(var1, var3, var2 + 1);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Calling endRowAction");
      }

      this.endRowAction(var1, var3, var2 + 1, var4);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "RowAction finished");
      }

   }

   public void addEntry(SnmpOid var1, Object var2) throws SnmpStatusException {
      this.addEntry(var1, (ObjectName)null, var2);
   }

   public synchronized void addEntry(SnmpOid var1, ObjectName var2, Object var3) throws SnmpStatusException {
      if (this.isRegistrationRequired() && var2 == null) {
         throw new SnmpStatusException(3);
      } else if (this.size != 0) {
         boolean var4 = false;
         int var9 = this.getInsertionPoint(var1, true);
         if (var9 == this.size) {
            this.insertOid(this.tablecount, var1);
            if (this.entries != null) {
               this.entries.addElement(var3);
            }

            if (this.entrynames != null) {
               this.entrynames.addElement(var2);
            }

            ++this.size;
         } else {
            try {
               this.insertOid(var9, var1);
               if (this.entries != null) {
                  this.entries.insertElementAt(var3, var9);
               }

               if (this.entrynames != null) {
                  this.entrynames.insertElementAt(var2, var9);
               }

               ++this.size;
            } catch (ArrayIndexOutOfBoundsException var6) {
            }
         }

         if (this.factory != null) {
            try {
               this.factory.addEntryCb(var9, var1, var2, var3, this);
            } catch (SnmpStatusException var7) {
               this.removeOid(var9);
               if (this.entries != null) {
                  this.entries.removeElementAt(var9);
               }

               if (this.entrynames != null) {
                  this.entrynames.removeElementAt(var9);
               }

               throw var7;
            }
         }

         this.sendNotification("jmx.snmp.table.entry.added", (new Date()).getTime(), var3, var2);
      } else {
         this.insertOid(0, var1);
         if (this.entries != null) {
            this.entries.addElement(var3);
         }

         if (this.entrynames != null) {
            this.entrynames.addElement(var2);
         }

         ++this.size;
         if (this.factory != null) {
            try {
               this.factory.addEntryCb(0, var1, var2, var3, this);
            } catch (SnmpStatusException var8) {
               this.removeOid(0);
               if (this.entries != null) {
                  this.entries.removeElementAt(0);
               }

               if (this.entrynames != null) {
                  this.entrynames.removeElementAt(0);
               }

               throw var8;
            }
         }

         this.sendNotification("jmx.snmp.table.entry.added", (new Date()).getTime(), var3, var2);
      }
   }

   public synchronized void removeEntry(SnmpOid var1, Object var2) throws SnmpStatusException {
      int var3 = this.findObject(var1);
      if (var3 != -1) {
         this.removeEntry(var3, var2);
      }
   }

   public void removeEntry(SnmpOid var1) throws SnmpStatusException {
      int var2 = this.findObject(var1);
      if (var2 != -1) {
         this.removeEntry(var2, (Object)null);
      }
   }

   public synchronized void removeEntry(int var1, Object var2) throws SnmpStatusException {
      if (var1 != -1) {
         if (var1 < this.size) {
            Object var3 = var2;
            if (this.entries != null && this.entries.size() > var1) {
               var3 = this.entries.elementAt(var1);
               this.entries.removeElementAt(var1);
            }

            ObjectName var4 = null;
            if (this.entrynames != null && this.entrynames.size() > var1) {
               var4 = (ObjectName)this.entrynames.elementAt(var1);
               this.entrynames.removeElementAt(var1);
            }

            SnmpOid var5 = this.tableoids[var1];
            this.removeOid(var1);
            --this.size;
            if (var3 == null) {
               var3 = var2;
            }

            if (this.factory != null) {
               this.factory.removeEntryCb(var1, var5, var4, var3, this);
            }

            this.sendNotification("jmx.snmp.table.entry.removed", (new Date()).getTime(), var3, var4);
         }
      }
   }

   public synchronized Object getEntry(SnmpOid var1) throws SnmpStatusException {
      int var2 = this.findObject(var1);
      if (var2 == -1) {
         throw new SnmpStatusException(224);
      } else {
         return this.entries.elementAt(var2);
      }
   }

   public synchronized ObjectName getEntryName(SnmpOid var1) throws SnmpStatusException {
      int var2 = this.findObject(var1);
      if (this.entrynames == null) {
         return null;
      } else if (var2 != -1 && var2 < this.entrynames.size()) {
         return (ObjectName)this.entrynames.elementAt(var2);
      } else {
         throw new SnmpStatusException(224);
      }
   }

   public Object[] getBasicEntries() {
      Object[] var1 = new Object[this.size];
      this.entries.copyInto(var1);
      return var1;
   }

   public int getSize() {
      return this.size;
   }

   public synchronized void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Listener can't be null");
      } else {
         Vector var4 = (Vector)this.handbackTable.get(var1);
         Vector var5 = (Vector)this.filterTable.get(var1);
         if (var4 == null) {
            var4 = new Vector();
            var5 = new Vector();
            this.handbackTable.put(var1, var4);
            this.filterTable.put(var1, var5);
         }

         var4.addElement(var3);
         var5.addElement(var2);
      }
   }

   public synchronized void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      Vector var2 = (Vector)this.handbackTable.get(var1);
      if (var2 == null) {
         throw new ListenerNotFoundException("listener");
      } else {
         this.handbackTable.remove(var1);
         this.filterTable.remove(var1);
      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      String[] var1 = new String[]{"jmx.snmp.table.entry.added", "jmx.snmp.table.entry.removed"};
      MBeanNotificationInfo[] var2 = new MBeanNotificationInfo[]{new MBeanNotificationInfo(var1, "com.sun.jmx.snmp.agent.SnmpTableEntryNotification", "Notifications sent by the SnmpMibTable")};
      return var2;
   }

   public void registerEntryFactory(SnmpTableEntryFactory var1) {
      this.factory = var1;
   }

   protected boolean isRowStatus(SnmpOid var1, long var2, Object var4) {
      return false;
   }

   protected int getRowAction(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      boolean var4 = var1.isNewEntry();
      SnmpVarBind var5 = var1.getRowStatusVarBind();
      if (var5 == null) {
         return var4 && !this.hasRowStatus() ? 4 : 0;
      } else {
         try {
            return this.mapRowStatus(var2, var5, var1.getUserData());
         } catch (SnmpStatusException var7) {
            checkRowStatusFail(var1, var7.getStatus());
            return 0;
         }
      }
   }

   protected int mapRowStatus(SnmpOid var1, SnmpVarBind var2, Object var3) throws SnmpStatusException {
      SnmpValue var4 = var2.value;
      if (var4 instanceof SnmpInt) {
         return ((SnmpInt)var4).intValue();
      } else {
         throw new SnmpStatusException(12);
      }
   }

   protected SnmpValue setRowStatus(SnmpOid var1, int var2, Object var3) throws SnmpStatusException {
      return null;
   }

   protected boolean isRowReady(SnmpOid var1, Object var2) throws SnmpStatusException {
      return true;
   }

   protected void checkRowStatusChange(SnmpMibSubRequest var1, SnmpOid var2, int var3, int var4) throws SnmpStatusException {
   }

   protected void checkRemoveTableRow(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
   }

   protected void removeTableRow(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      this.removeEntry(var2);
   }

   protected synchronized void beginRowAction(SnmpMibSubRequest var1, SnmpOid var2, int var3, int var4) throws SnmpStatusException {
      boolean var5 = var1.isNewEntry();
      switch(var4) {
      case 0:
         if (var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Failed to create row[" + var2 + "] : RowStatus = unspecified");
            }

            checkRowStatusFail(var1, 6);
         }
         break;
      case 1:
      case 2:
         if (var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't switch state of row[" + var2 + "] : specified RowStatus = active | notInService but row does not exist");
            }

            checkRowStatusFail(var1, 12);
         }

         this.checkRowStatusChange(var1, var2, var3, var4);
         break;
      case 3:
      default:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Invalid RowStatus value for row[" + var2 + "] : specified RowStatus = " + var4);
         }

         checkRowStatusFail(var1, 12);
         break;
      case 4:
      case 5:
         if (var5) {
            if (this.isCreationEnabled()) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Creating row[" + var2 + "] : RowStatus = createAndGo | createAndWait");
               }

               this.createNewEntry(var1, var2, var3);
            } else {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't create row[" + var2 + "] : RowStatus = createAndGo | createAndWait but creation is disabled");
               }

               checkRowStatusFail(var1, 6);
            }
         } else {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't create row[" + var2 + "] : RowStatus = createAndGo | createAndWait but row already exists");
            }

            checkRowStatusFail(var1, 12);
         }
         break;
      case 6:
         if (var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Warning: can't destroy row[" + var2 + "] : RowStatus = destroy but row does not exist");
            }
         } else if (!this.isCreationEnabled()) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't destroy row[" + var2 + "] : RowStatus = destroy but creation is disabled");
            }

            checkRowStatusFail(var1, 6);
         }

         this.checkRemoveTableRow(var1, var2, var3);
      }

   }

   protected void endRowAction(SnmpMibSubRequest var1, SnmpOid var2, int var3, int var4) throws SnmpStatusException {
      boolean var5 = var1.isNewEntry();
      Object var8 = var1.getUserData();
      SnmpValue var9 = null;
      switch(var4) {
      case 0:
         break;
      case 1:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'active' for row[" + var2 + "] : requested RowStatus = active");
         }

         var9 = this.setRowStatus(var2, 1, var8);
         break;
      case 2:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notInService' for row[" + var2 + "] : requested RowStatus = notInService");
         }

         var9 = this.setRowStatus(var2, 2, var8);
         break;
      case 3:
      default:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Invalid RowStatus value for row[" + var2 + "] : specified RowStatus = " + var4);
         }

         setRowStatusFail(var1, 12);
         break;
      case 4:
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'active' for row[" + var2 + "] : requested RowStatus = createAndGo");
         }

         var9 = this.setRowStatus(var2, 1, var8);
         break;
      case 5:
         if (this.isRowReady(var2, var8)) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notInService' for row[" + var2 + "] : requested RowStatus = createAndWait");
            }

            var9 = this.setRowStatus(var2, 2, var8);
         } else {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notReady' for row[" + var2 + "] : requested RowStatus = createAndWait");
            }

            var9 = this.setRowStatus(var2, 3, var8);
         }
         break;
      case 6:
         if (var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Warning: requested RowStatus = destroy, but row[" + var2 + "] does not exist");
            }
         } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Destroying row[" + var2 + "] : requested RowStatus = destroy");
         }

         this.removeTableRow(var1, var2, var3);
      }

      if (var9 != null) {
         SnmpVarBind var10 = var1.getRowStatusVarBind();
         if (var10 != null) {
            var10.value = var9;
         }
      }

   }

   protected long getNextVarEntryId(SnmpOid var1, long var2, Object var4, int var5) throws SnmpStatusException {
      long var6 = var2;

      do {
         var6 = this.getNextVarEntryId(var1, var6, var4);
      } while(this.skipEntryVariable(var1, var6, var4, var5));

      return var6;
   }

   protected boolean skipEntryVariable(SnmpOid var1, long var2, Object var4, int var5) {
      return false;
   }

   protected SnmpOid getNextOid(SnmpOid var1, Object var2) throws SnmpStatusException {
      if (this.size == 0) {
         throw new SnmpStatusException(224);
      } else {
         SnmpOid var4 = this.tableoids[this.tablecount - 1];
         if (var4.equals(var1)) {
            throw new SnmpStatusException(224);
         } else {
            int var5 = this.getInsertionPoint(var1, false);
            if (var5 > -1 && var5 < this.size) {
               try {
                  var4 = this.tableoids[var5];
                  return var4;
               } catch (ArrayIndexOutOfBoundsException var7) {
                  throw new SnmpStatusException(224);
               }
            } else {
               throw new SnmpStatusException(224);
            }
         }
      }
   }

   protected SnmpOid getNextOid(Object var1) throws SnmpStatusException {
      if (this.size == 0) {
         throw new SnmpStatusException(224);
      } else {
         return this.tableoids[0];
      }
   }

   protected abstract long getNextVarEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException;

   protected abstract void validateVarEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException;

   protected abstract boolean isReadableEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException;

   protected abstract void get(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException;

   protected abstract void check(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException;

   protected abstract void set(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException;

   SnmpOid getNextOid(long[] var1, int var2, Object var3) throws SnmpStatusException {
      SnmpEntryOid var4 = new SnmpEntryOid(var1, var2);
      return this.getNextOid(var4, var3);
   }

   static void checkRowStatusFail(SnmpMibSubRequest var0, int var1) throws SnmpStatusException {
      SnmpVarBind var2 = var0.getRowStatusVarBind();
      SnmpStatusException var3 = new SnmpStatusException(var1);
      var0.registerCheckException(var2, var3);
   }

   static void setRowStatusFail(SnmpMibSubRequest var0, int var1) throws SnmpStatusException {
      SnmpVarBind var2 = var0.getRowStatusVarBind();
      SnmpStatusException var3 = new SnmpStatusException(var1);
      var0.registerSetException(var2, var3);
   }

   final synchronized void findHandlingNode(SnmpVarBind var1, long[] var2, int var3, SnmpRequestTree var4) throws SnmpStatusException {
      int var5 = var2.length;
      if (var4 == null) {
         throw new SnmpStatusException(5);
      } else if (var3 >= var5) {
         throw new SnmpStatusException(6);
      } else if (var2[var3] != (long)this.nodeId) {
         throw new SnmpStatusException(6);
      } else if (var3 + 2 >= var5) {
         throw new SnmpStatusException(6);
      } else {
         SnmpEntryOid var6 = new SnmpEntryOid(var2, var3 + 2);
         Object var7 = var4.getUserData();
         boolean var8 = this.contains(var6, var7);
         if (!var8) {
            if (!var4.isCreationAllowed()) {
               throw new SnmpStatusException(224);
            }

            if (!this.isCreationEnabled()) {
               throw new SnmpStatusException(6);
            }
         }

         long var9 = var2[var3 + 1];
         if (var8) {
            this.validateVarEntryId(var6, var9, var7);
         }

         if (var4.isSetRequest() && this.isRowStatus(var6, var9, var7)) {
            var4.add(this, var3, var6, var1, !var8, var1);
         } else {
            var4.add(this, var3, var6, var1, !var8);
         }

      }
   }

   final synchronized long[] findNextHandlingNode(SnmpVarBind var1, long[] var2, int var3, int var4, SnmpRequestTree var5, AcmChecker var6) throws SnmpStatusException {
      int var7 = var2.length;
      if (var5 == null) {
         throw new SnmpStatusException(225);
      } else {
         Object var8 = var5.getUserData();
         int var9 = var5.getRequestPduVersion();
         long var10 = -1L;
         if (var3 >= var7) {
            var2 = new long[]{(long)this.nodeId};
            var3 = 0;
            var7 = 1;
         } else {
            if (var2[var3] > (long)this.nodeId) {
               throw new SnmpStatusException(225);
            }

            if (var2[var3] < (long)this.nodeId) {
               var2 = new long[]{(long)this.nodeId};
               var3 = 0;
               var7 = 0;
            } else if (var3 + 1 < var7) {
               var10 = var2[var3 + 1];
            }
         }

         SnmpOid var12;
         if (var3 == var7 - 1) {
            var12 = this.getNextOid(var8);
            var10 = this.getNextVarEntryId(var12, var10, var8, var9);
         } else if (var3 == var7 - 2) {
            var12 = this.getNextOid(var8);
            if (this.skipEntryVariable(var12, var10, var8, var9)) {
               var10 = this.getNextVarEntryId(var12, var10, var8, var9);
            }
         } else {
            try {
               var12 = this.getNextOid(var2, var3 + 2, var8);
               if (this.skipEntryVariable(var12, var10, var8, var9)) {
                  throw new SnmpStatusException(225);
               }
            } catch (SnmpStatusException var14) {
               var12 = this.getNextOid(var8);
               var10 = this.getNextVarEntryId(var12, var10, var8, var9);
            }
         }

         return this.findNextAccessibleOid(var12, var1, var2, var4, var5, var6, var8, var10);
      }
   }

   private long[] findNextAccessibleOid(SnmpOid var1, SnmpVarBind var2, long[] var3, int var4, SnmpRequestTree var5, AcmChecker var6, Object var7, long var8) throws SnmpStatusException {
      int var10 = var5.getRequestPduVersion();

      while(true) {
         if (var1 != null && var8 != -1L) {
            try {
               label91: {
                  if (!this.isReadableEntryId(var1, var8, var7)) {
                     throw new SnmpStatusException(225);
                  }

                  long[] var11 = var1.longValue(false);
                  int var12 = var11.length;
                  long[] var13 = new long[var4 + 2 + var12];
                  var13[0] = -1L;
                  System.arraycopy(var11, 0, var13, var4 + 2, var12);
                  var13[var4] = (long)this.nodeId;
                  var13[var4 + 1] = var8;
                  var6.add(var4, var13, var4, var12 + 2);

                  long[] var14;
                  try {
                     var6.checkCurrentOid();
                     var5.add(this, var4, var1, var2, false);
                     var14 = var13;
                  } catch (SnmpStatusException var19) {
                     var1 = this.getNextOid(var1, var7);
                     break label91;
                  } finally {
                     var6.remove(var4, var12 + 2);
                  }

                  return var14;
               }
            } catch (SnmpStatusException var21) {
               var1 = this.getNextOid(var7);
               var8 = this.getNextVarEntryId(var1, var8, var7, var10);
            }

            if (var1 != null && var8 != -1L) {
               continue;
            }

            throw new SnmpStatusException(225);
         }

         throw new SnmpStatusException(225);
      }
   }

   final void validateOid(long[] var1, int var2) throws SnmpStatusException {
      int var3 = var1.length;
      if (var2 + 2 >= var3) {
         throw new SnmpStatusException(224);
      } else if (var1[var2] != (long)this.nodeId) {
         throw new SnmpStatusException(225);
      }
   }

   private synchronized void sendNotification(Notification var1) {
      Enumeration var2 = this.handbackTable.keys();

      label27:
      while(var2.hasMoreElements()) {
         NotificationListener var3 = (NotificationListener)var2.nextElement();
         Vector var4 = (Vector)this.handbackTable.get(var3);
         Vector var5 = (Vector)this.filterTable.get(var3);
         Enumeration var6 = var5.elements();
         Enumeration var7 = var4.elements();

         while(true) {
            Object var8;
            NotificationFilter var9;
            do {
               if (!var7.hasMoreElements()) {
                  continue label27;
               }

               var8 = var7.nextElement();
               var9 = (NotificationFilter)var6.nextElement();
            } while(var9 != null && !var9.isNotificationEnabled(var1));

            var3.handleNotification(var1, var8);
         }
      }

   }

   private void sendNotification(String var1, long var2, Object var4, ObjectName var5) {
      synchronized(this) {
         ++this.sequenceNumber;
      }

      SnmpTableEntryNotification var6 = new SnmpTableEntryNotification(var1, this, this.sequenceNumber, var2, var4, var5);
      this.sendNotification(var6);
   }

   protected boolean contains(SnmpOid var1, Object var2) {
      return this.findObject(var1) > -1;
   }

   private int findObject(SnmpOid var1) {
      int var2 = 0;
      int var3 = this.size - 1;

      for(int var6 = var2 + (var3 - var2) / 2; var2 <= var3; var6 = var2 + (var3 - var2) / 2) {
         SnmpOid var4 = this.tableoids[var6];
         int var5 = var1.compareTo(var4);
         if (var5 == 0) {
            return var6;
         }

         if (var1.equals(var4)) {
            return var6;
         }

         if (var5 > 0) {
            var2 = var6 + 1;
         } else {
            var3 = var6 - 1;
         }
      }

      return -1;
   }

   private int getInsertionPoint(SnmpOid var1, boolean var2) throws SnmpStatusException {
      int var4 = 0;
      int var5 = this.size - 1;

      int var8;
      for(var8 = var4 + (var5 - var4) / 2; var4 <= var5; var8 = var4 + (var5 - var4) / 2) {
         SnmpOid var6 = this.tableoids[var8];
         int var7 = var1.compareTo(var6);
         if (var7 == 0) {
            if (var2) {
               throw new SnmpStatusException(17, var8);
            }

            return var8 + 1;
         }

         if (var7 > 0) {
            var4 = var8 + 1;
         } else {
            var5 = var8 - 1;
         }
      }

      return var8;
   }

   private void removeOid(int var1) {
      if (var1 < this.tablecount) {
         if (var1 >= 0) {
            int var2 = --this.tablecount - var1;
            this.tableoids[var1] = null;
            if (var2 > 0) {
               System.arraycopy(this.tableoids, var1 + 1, this.tableoids, var1, var2);
            }

            this.tableoids[this.tablecount] = null;
         }
      }
   }

   private void insertOid(int var1, SnmpOid var2) {
      if (var1 < this.tablesize && this.tablecount != this.tablesize) {
         if (var1 < this.tablecount) {
            System.arraycopy(this.tableoids, var1, this.tableoids, var1 + 1, this.tablecount - var1);
         }
      } else {
         SnmpOid[] var3 = this.tableoids;
         this.tablesize += 16;
         this.tableoids = new SnmpOid[this.tablesize];
         if (var1 > this.tablecount) {
            var1 = this.tablecount;
         }

         if (var1 < 0) {
            var1 = 0;
         }

         int var5 = this.tablecount - var1;
         if (var1 > 0) {
            System.arraycopy(var3, 0, this.tableoids, 0, var1);
         }

         if (var5 > 0) {
            System.arraycopy(var3, var1, this.tableoids, var1 + 1, var5);
         }
      }

      this.tableoids[var1] = var2;
      ++this.tablecount;
   }
}
