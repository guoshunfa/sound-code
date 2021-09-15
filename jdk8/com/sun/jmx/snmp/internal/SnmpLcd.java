package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUnknownModelLcdException;
import com.sun.jmx.snmp.SnmpUnknownSubSystemException;
import java.util.Hashtable;

public abstract class SnmpLcd {
   private Hashtable<SnmpSubSystem, SnmpLcd.SubSysLcdManager> subs = new Hashtable();

   public abstract int getEngineBoots();

   public abstract String getEngineId();

   public abstract void storeEngineBoots(int var1);

   public abstract void storeEngineId(SnmpEngineId var1);

   public void addModelLcd(SnmpSubSystem var1, int var2, SnmpModelLcd var3) {
      SnmpLcd.SubSysLcdManager var4 = (SnmpLcd.SubSysLcdManager)this.subs.get(var1);
      if (var4 == null) {
         var4 = new SnmpLcd.SubSysLcdManager();
         this.subs.put(var1, var4);
      }

      var4.addModelLcd(var2, var3);
   }

   public void removeModelLcd(SnmpSubSystem var1, int var2) throws SnmpUnknownModelLcdException, SnmpUnknownSubSystemException {
      SnmpLcd.SubSysLcdManager var3 = (SnmpLcd.SubSysLcdManager)this.subs.get(var1);
      if (var3 != null) {
         SnmpModelLcd var4 = var3.removeModelLcd(var2);
         if (var4 == null) {
            throw new SnmpUnknownModelLcdException("Model : " + var2);
         }
      } else {
         throw new SnmpUnknownSubSystemException(var1.toString());
      }
   }

   public SnmpModelLcd getModelLcd(SnmpSubSystem var1, int var2) {
      SnmpLcd.SubSysLcdManager var3 = (SnmpLcd.SubSysLcdManager)this.subs.get(var1);
      return var3 == null ? null : var3.getModelLcd(var2);
   }

   class SubSysLcdManager {
      private Hashtable<Integer, SnmpModelLcd> models = new Hashtable();

      public void addModelLcd(int var1, SnmpModelLcd var2) {
         this.models.put(new Integer(var1), var2);
      }

      public SnmpModelLcd getModelLcd(int var1) {
         return (SnmpModelLcd)this.models.get(new Integer(var1));
      }

      public SnmpModelLcd removeModelLcd(int var1) {
         return (SnmpModelLcd)this.models.remove(new Integer(var1));
      }
   }
}
