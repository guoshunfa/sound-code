package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public abstract class SnmpTableSupport implements SnmpTableEntryFactory, SnmpTableCallbackHandler, Serializable {
   protected List<Object> entries;
   protected SnmpMibTable meta;
   protected SnmpMib theMib;
   private boolean registrationRequired = false;

   protected SnmpTableSupport(SnmpMib var1) {
      this.theMib = var1;
      this.meta = this.getRegisteredTableMeta(var1);
      this.bindWithTableMeta();
      this.entries = this.allocateTable();
   }

   public abstract void createNewEntry(SnmpMibSubRequest var1, SnmpOid var2, int var3, SnmpMibTable var4) throws SnmpStatusException;

   public Object getEntry(int var1) {
      return this.entries == null ? null : this.entries.get(var1);
   }

   public int getSize() {
      return this.meta.getSize();
   }

   public void setCreationEnabled(boolean var1) {
      this.meta.setCreationEnabled(var1);
   }

   public boolean isCreationEnabled() {
      return this.meta.isCreationEnabled();
   }

   public boolean isRegistrationRequired() {
      return this.registrationRequired;
   }

   public SnmpIndex buildSnmpIndex(SnmpOid var1) throws SnmpStatusException {
      return this.buildSnmpIndex(var1.longValue(false), 0);
   }

   public abstract SnmpOid buildOidFromIndex(SnmpIndex var1) throws SnmpStatusException;

   public abstract ObjectName buildNameFromIndex(SnmpIndex var1) throws SnmpStatusException;

   public void addEntryCb(int var1, SnmpOid var2, ObjectName var3, Object var4, SnmpMibTable var5) throws SnmpStatusException {
      try {
         if (this.entries != null) {
            this.entries.add(var1, var4);
         }

      } catch (Exception var7) {
         throw new SnmpStatusException(2);
      }
   }

   public void removeEntryCb(int var1, SnmpOid var2, ObjectName var3, Object var4, SnmpMibTable var5) throws SnmpStatusException {
      try {
         if (this.entries != null) {
            this.entries.remove(var1);
         }
      } catch (Exception var7) {
      }

   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      this.meta.addNotificationListener(var1, var2, var3);
   }

   public synchronized void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      this.meta.removeNotificationListener(var1);
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return this.meta.getNotificationInfo();
   }

   protected abstract SnmpIndex buildSnmpIndex(long[] var1, int var2) throws SnmpStatusException;

   protected abstract SnmpMibTable getRegisteredTableMeta(SnmpMib var1);

   protected List<Object> allocateTable() {
      return new ArrayList();
   }

   protected void addEntry(SnmpIndex var1, Object var2) throws SnmpStatusException {
      SnmpOid var3 = this.buildOidFromIndex(var1);
      ObjectName var4 = null;
      if (this.isRegistrationRequired()) {
         var4 = this.buildNameFromIndex(var1);
      }

      this.meta.addEntry(var3, var4, var2);
   }

   protected void addEntry(SnmpIndex var1, ObjectName var2, Object var3) throws SnmpStatusException {
      SnmpOid var4 = this.buildOidFromIndex(var1);
      this.meta.addEntry(var4, var2, var3);
   }

   protected void removeEntry(SnmpIndex var1, Object var2) throws SnmpStatusException {
      SnmpOid var3 = this.buildOidFromIndex(var1);
      this.meta.removeEntry(var3, var2);
   }

   protected Object[] getBasicEntries() {
      if (this.entries == null) {
         return null;
      } else {
         Object[] var1 = new Object[this.entries.size()];
         this.entries.toArray(var1);
         return var1;
      }
   }

   protected void bindWithTableMeta() {
      if (this.meta != null) {
         this.registrationRequired = this.meta.isRegistrationRequired();
         this.meta.registerEntryFactory(this);
      }
   }
}
