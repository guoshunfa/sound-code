package com.sun.jmx.snmp;

import java.util.Enumeration;
import java.util.Vector;

public class SnmpVarBindList extends Vector<SnmpVarBind> {
   private static final long serialVersionUID = -7203997794636430321L;
   public String identity;
   Timestamp timestamp;

   public SnmpVarBindList() {
      super(5, 5);
      this.identity = "VarBindList ";
   }

   public SnmpVarBindList(int var1) {
      super(var1);
      this.identity = "VarBindList ";
   }

   public SnmpVarBindList(String var1) {
      super(5, 5);
      this.identity = "VarBindList ";
      this.identity = var1;
   }

   public SnmpVarBindList(SnmpVarBindList var1) {
      super(var1.size(), 5);
      this.identity = "VarBindList ";
      var1.copyInto(this.elementData);
      this.elementCount = var1.size();
   }

   public SnmpVarBindList(Vector<SnmpVarBind> var1) {
      super(var1.size(), 5);
      this.identity = "VarBindList ";
      Enumeration var2 = var1.elements();

      while(var2.hasMoreElements()) {
         SnmpVarBind var3 = (SnmpVarBind)var2.nextElement();
         this.addElement(var3.clone());
      }

   }

   public SnmpVarBindList(String var1, Vector<SnmpVarBind> var2) {
      this(var2);
      this.identity = var1;
   }

   public Timestamp getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(Timestamp var1) {
      this.timestamp = var1;
   }

   public final synchronized SnmpVarBind getVarBindAt(int var1) {
      return (SnmpVarBind)this.elementAt(var1);
   }

   public synchronized int getVarBindCount() {
      return this.size();
   }

   public synchronized Enumeration<SnmpVarBind> getVarBindList() {
      return this.elements();
   }

   public final synchronized void setVarBindList(Vector<SnmpVarBind> var1) {
      this.setVarBindList(var1, false);
   }

   public final synchronized void setVarBindList(Vector<SnmpVarBind> var1, boolean var2) {
      synchronized(var1) {
         int var4 = var1.size();
         this.setSize(var4);
         var1.copyInto(this.elementData);
         if (var2) {
            for(int var5 = 0; var5 < var4; ++var5) {
               SnmpVarBind var6 = (SnmpVarBind)this.elementData[var5];
               this.elementData[var5] = var6.clone();
            }
         }

      }
   }

   public synchronized void addVarBindList(SnmpVarBindList var1) {
      this.ensureCapacity(var1.size() + this.size());

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.addElement(var1.getVarBindAt(var2));
      }

   }

   public synchronized boolean removeVarBindList(SnmpVarBindList var1) {
      boolean var2 = true;

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2 = this.removeElement(var1.getVarBindAt(var3));
      }

      return var2;
   }

   public final synchronized void replaceVarBind(SnmpVarBind var1, int var2) {
      this.setElementAt(var1, var2);
   }

   public final synchronized void addVarBind(String[] var1, String var2) throws SnmpStatusException {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         SnmpVarBind var4 = new SnmpVarBind(var1[var3]);
         var4.addInstance(var2);
         this.addElement(var4);
      }

   }

   public synchronized boolean removeVarBind(String[] var1, String var2) throws SnmpStatusException {
      boolean var3 = true;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         SnmpVarBind var5 = new SnmpVarBind(var1[var4]);
         var5.addInstance(var2);
         int var6 = this.indexOfOid(var5);

         try {
            this.removeElementAt(var6);
         } catch (ArrayIndexOutOfBoundsException var8) {
            var3 = false;
         }
      }

      return var3;
   }

   public synchronized void addVarBind(String[] var1) throws SnmpStatusException {
      this.addVarBind(var1, (String)null);
   }

   public synchronized boolean removeVarBind(String[] var1) throws SnmpStatusException {
      return this.removeVarBind(var1, (String)null);
   }

   public synchronized void addVarBind(String var1) throws SnmpStatusException {
      SnmpVarBind var2 = new SnmpVarBind(var1);
      this.addVarBind(var2);
   }

   public synchronized boolean removeVarBind(String var1) throws SnmpStatusException {
      SnmpVarBind var2 = new SnmpVarBind(var1);
      int var3 = this.indexOfOid(var2);

      try {
         this.removeElementAt(var3);
         return true;
      } catch (ArrayIndexOutOfBoundsException var5) {
         return false;
      }
   }

   public synchronized void addVarBind(SnmpVarBind var1) {
      this.addElement(var1);
   }

   public synchronized boolean removeVarBind(SnmpVarBind var1) {
      return this.removeElement(var1);
   }

   public synchronized void addInstance(String var1) throws SnmpStatusException {
      int var2 = this.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         ((SnmpVarBind)this.elementData[var3]).addInstance(var1);
      }

   }

   public final synchronized void concat(Vector<SnmpVarBind> var1) {
      this.ensureCapacity(this.size() + var1.size());
      Enumeration var2 = var1.elements();

      while(var2.hasMoreElements()) {
         this.addElement(var2.nextElement());
      }

   }

   public synchronized boolean checkForValidValues() {
      int var1 = this.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         SnmpVarBind var3 = (SnmpVarBind)this.elementData[var2];
         if (!var3.isValidValue()) {
            return false;
         }
      }

      return true;
   }

   public synchronized boolean checkForUnspecifiedValue() {
      int var1 = this.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         SnmpVarBind var3 = (SnmpVarBind)this.elementData[var2];
         if (var3.isUnspecifiedValue()) {
            return true;
         }
      }

      return false;
   }

   public synchronized SnmpVarBindList splitAt(int var1) {
      SnmpVarBindList var2 = null;
      if (var1 > this.elementCount) {
         return var2;
      } else {
         var2 = new SnmpVarBindList();
         int var3 = this.size();

         for(int var4 = var1; var4 < var3; ++var4) {
            var2.addElement((SnmpVarBind)this.elementData[var4]);
         }

         this.elementCount = var1;
         this.trimToSize();
         return var2;
      }
   }

   public synchronized int indexOfOid(SnmpVarBind var1, int var2, int var3) {
      SnmpOid var4 = var1.getOid();

      for(int var5 = var2; var5 < var3; ++var5) {
         SnmpVarBind var6 = (SnmpVarBind)this.elementData[var5];
         if (var4.equals(var6.getOid())) {
            return var5;
         }
      }

      return -1;
   }

   public synchronized int indexOfOid(SnmpVarBind var1) {
      return this.indexOfOid(var1, 0, this.size());
   }

   public synchronized int indexOfOid(SnmpOid var1) {
      int var2 = this.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         SnmpVarBind var4 = (SnmpVarBind)this.elementData[var3];
         if (var1.equals(var4.getOid())) {
            return var3;
         }
      }

      return -1;
   }

   public synchronized SnmpVarBindList cloneWithValue() {
      SnmpVarBindList var1 = new SnmpVarBindList();
      var1.setTimestamp(this.getTimestamp());
      var1.ensureCapacity(this.size());

      for(int var2 = 0; var2 < this.size(); ++var2) {
         SnmpVarBind var3 = (SnmpVarBind)this.elementData[var2];
         var1.addElement(var3.clone());
      }

      return var1;
   }

   public synchronized SnmpVarBindList cloneWithoutValue() {
      SnmpVarBindList var1 = new SnmpVarBindList();
      int var2 = this.size();
      var1.ensureCapacity(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         SnmpVarBind var4 = (SnmpVarBind)this.elementData[var3];
         var1.addElement((SnmpVarBind)var4.cloneWithoutValue());
      }

      return var1;
   }

   public synchronized SnmpVarBindList clone() {
      return this.cloneWithValue();
   }

   public synchronized Vector<SnmpVarBind> toVector(boolean var1) {
      int var2 = this.elementCount;
      if (!var1) {
         return new Vector(this);
      } else {
         Vector var3 = new Vector(var2, 5);

         for(int var4 = 0; var4 < var2; ++var4) {
            SnmpVarBind var5 = (SnmpVarBind)this.elementData[var4];
            var3.addElement(var5.clone());
         }

         return var3;
      }
   }

   public String oidListToString() {
      StringBuilder var1 = new StringBuilder(300);

      for(int var2 = 0; var2 < this.elementCount; ++var2) {
         SnmpVarBind var3 = (SnmpVarBind)this.elementData[var2];
         var1.append(var3.getOid().toString()).append("\n");
      }

      return var1.toString();
   }

   public synchronized String varBindListToString() {
      StringBuilder var1 = new StringBuilder(300);

      for(int var2 = 0; var2 < this.elementCount; ++var2) {
         var1.append(this.elementData[var2].toString()).append("\n");
      }

      return var1.toString();
   }

   protected void finalize() {
      this.removeAllElements();
   }
}
