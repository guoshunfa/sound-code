package com.sun.jmx.snmp;

import com.sun.jmx.mbeanserver.Util;
import java.util.Vector;

public class SnmpOidDatabaseSupport implements SnmpOidDatabase {
   private Vector<SnmpOidTable> tables = new Vector();

   public SnmpOidDatabaseSupport() {
   }

   public SnmpOidDatabaseSupport(SnmpOidTable var1) {
      this.tables.addElement(var1);
   }

   public void add(SnmpOidTable var1) {
      if (!this.tables.contains(var1)) {
         this.tables.addElement(var1);
      }

   }

   public void remove(SnmpOidTable var1) throws SnmpStatusException {
      if (!this.tables.contains(var1)) {
         throw new SnmpStatusException("The specified SnmpOidTable does not exist in this SnmpOidDatabase");
      } else {
         this.tables.removeElement(var1);
      }
   }

   public SnmpOidRecord resolveVarName(String var1) throws SnmpStatusException {
      int var2 = 0;

      while(var2 < this.tables.size()) {
         try {
            return ((SnmpOidTable)this.tables.elementAt(var2)).resolveVarName(var1);
         } catch (SnmpStatusException var4) {
            if (var2 == this.tables.size() - 1) {
               throw new SnmpStatusException(var4.getMessage());
            }

            ++var2;
         }
      }

      return null;
   }

   public SnmpOidRecord resolveVarOid(String var1) throws SnmpStatusException {
      int var2 = 0;

      while(var2 < this.tables.size()) {
         try {
            return ((SnmpOidTable)this.tables.elementAt(var2)).resolveVarOid(var1);
         } catch (SnmpStatusException var4) {
            if (var2 == this.tables.size() - 1) {
               throw new SnmpStatusException(var4.getMessage());
            }

            ++var2;
         }
      }

      return null;
   }

   public Vector<?> getAllEntries() {
      Vector var1 = new Vector();

      for(int var2 = 0; var2 < this.tables.size(); ++var2) {
         Vector var3 = (Vector)Util.cast(((SnmpOidTable)this.tables.elementAt(var2)).getAllEntries());
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.size(); ++var4) {
               var1.addElement(var3.elementAt(var4));
            }
         }
      }

      return var1;
   }

   public void removeAll() {
      this.tables.removeAllElements();
   }
}
