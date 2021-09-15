package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;

public abstract class SnmpMibEntry extends SnmpMibNode implements Serializable {
   public abstract boolean isVariable(long var1);

   public abstract boolean isReadable(long var1);

   public long getNextVarId(long var1, Object var3) throws SnmpStatusException {
      long var4;
      for(var4 = super.getNextVarId(var1, var3); !this.isReadable(var4); var4 = super.getNextVarId(var4, var3)) {
      }

      return var4;
   }

   public void validateVarId(long var1, Object var3) throws SnmpStatusException {
      if (!this.isVariable(var1)) {
         throw new SnmpStatusException(2);
      }
   }

   public abstract void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;
}
