package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;

public class SnmpOidTableSupport implements SnmpOidTable {
   private Hashtable<String, SnmpOidRecord> oidStore = new Hashtable();
   private String myName;

   public SnmpOidTableSupport(String var1) {
      this.myName = var1;
   }

   public SnmpOidRecord resolveVarName(String var1) throws SnmpStatusException {
      SnmpOidRecord var2 = (SnmpOidRecord)this.oidStore.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         throw new SnmpStatusException("Variable name <" + var1 + "> not found in Oid repository");
      }
   }

   public SnmpOidRecord resolveVarOid(String var1) throws SnmpStatusException {
      int var2 = var1.indexOf(46);
      if (var2 < 0) {
         throw new SnmpStatusException("Variable oid <" + var1 + "> not found in Oid repository");
      } else {
         if (var2 == 0) {
            var1 = var1.substring(1, var1.length());
         }

         Enumeration var3 = this.oidStore.elements();

         SnmpOidRecord var4;
         do {
            if (!var3.hasMoreElements()) {
               throw new SnmpStatusException("Variable oid <" + var1 + "> not found in Oid repository");
            }

            var4 = (SnmpOidRecord)var3.nextElement();
         } while(!var4.getOid().equals(var1));

         return var4;
      }
   }

   public Vector<SnmpOidRecord> getAllEntries() {
      Vector var1 = new Vector();
      Enumeration var2 = this.oidStore.elements();

      while(var2.hasMoreElements()) {
         var1.addElement(var2.nextElement());
      }

      return var1;
   }

   public synchronized void loadMib(SnmpOidRecord[] var1) {
      try {
         int var2 = 0;

         while(true) {
            SnmpOidRecord var3 = var1[var2];
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpOidTableSupport.class.getName(), "loadMib", "Load " + var3.getName());
            }

            this.oidStore.put(var3.getName(), var3);
            ++var2;
         }
      } catch (ArrayIndexOutOfBoundsException var4) {
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof SnmpOidTableSupport)) {
         return false;
      } else {
         SnmpOidTableSupport var2 = (SnmpOidTableSupport)var1;
         return this.myName.equals(var2.getName());
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.myName);
   }

   public String getName() {
      return this.myName;
   }
}
