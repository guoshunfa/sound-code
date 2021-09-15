package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;

public class SnmpStandardObjectServer implements Serializable {
   private static final long serialVersionUID = -4641068116505308488L;

   public void get(SnmpStandardMetaServer var1, SnmpMibSubRequest var2, int var3) throws SnmpStatusException {
      Object var4 = var2.getUserData();
      Enumeration var5 = var2.getElements();

      while(var5.hasMoreElements()) {
         SnmpVarBind var6 = (SnmpVarBind)var5.nextElement();

         try {
            long var7 = var6.oid.getOidArc(var3);
            var6.value = var1.get(var7, var4);
         } catch (SnmpStatusException var9) {
            var2.registerGetException(var6, var9);
         }
      }

   }

   public void set(SnmpStandardMetaServer var1, SnmpMibSubRequest var2, int var3) throws SnmpStatusException {
      Object var4 = var2.getUserData();
      Enumeration var5 = var2.getElements();

      while(var5.hasMoreElements()) {
         SnmpVarBind var6 = (SnmpVarBind)var5.nextElement();

         try {
            long var7 = var6.oid.getOidArc(var3);
            var6.value = var1.set(var6.value, var7, var4);
         } catch (SnmpStatusException var9) {
            var2.registerSetException(var6, var9);
         }
      }

   }

   public void check(SnmpStandardMetaServer var1, SnmpMibSubRequest var2, int var3) throws SnmpStatusException {
      Object var4 = var2.getUserData();
      Enumeration var5 = var2.getElements();

      while(var5.hasMoreElements()) {
         SnmpVarBind var6 = (SnmpVarBind)var5.nextElement();

         try {
            long var7 = var6.oid.getOidArc(var3);
            var1.check(var6.value, var7, var4);
         } catch (SnmpStatusException var9) {
            var2.registerCheckException(var6, var9);
         }
      }

   }
}
