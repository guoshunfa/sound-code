package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public abstract class SnmpMibGroup extends SnmpMibOid implements Serializable {
   protected Hashtable<Long, Long> subgroups = null;

   public abstract boolean isTable(long var1);

   public abstract boolean isVariable(long var1);

   public abstract boolean isReadable(long var1);

   public abstract SnmpMibTable getTable(long var1);

   public void validateVarId(long var1, Object var3) throws SnmpStatusException {
      if (!this.isVariable(var1)) {
         throw new SnmpStatusException(225);
      }
   }

   public boolean isNestedArc(long var1) {
      if (this.subgroups == null) {
         return false;
      } else {
         Object var3 = this.subgroups.get(new Long(var1));
         return var3 != null;
      }
   }

   public abstract void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public void getRootOid(Vector<Integer> var1) {
   }

   void registerNestedArc(long var1) {
      Long var3 = new Long(var1);
      if (this.subgroups == null) {
         this.subgroups = new Hashtable();
      }

      this.subgroups.put(var3, var3);
   }

   protected void registerObject(long var1) throws IllegalAccessException {
      long[] var3 = new long[]{var1};
      super.registerNode(var3, 0, (SnmpMibNode)null);
   }

   void registerNode(long[] var1, int var2, SnmpMibNode var3) throws IllegalAccessException {
      super.registerNode(var1, var2, var3);
      if (var2 >= 0) {
         if (var2 < var1.length) {
            this.registerNestedArc(var1[var2]);
         }
      }
   }

   void findHandlingNode(SnmpVarBind var1, long[] var2, int var3, SnmpRequestTree var4) throws SnmpStatusException {
      int var5 = var2.length;
      if (var4 == null) {
         throw new SnmpStatusException(5);
      } else {
         Object var6 = var4.getUserData();
         if (var3 >= var5) {
            throw new SnmpStatusException(6);
         } else {
            long var7 = var2[var3];
            if (this.isNestedArc(var7)) {
               super.findHandlingNode(var1, var2, var3, var4);
            } else if (this.isTable(var7)) {
               SnmpMibTable var9 = this.getTable(var7);
               var9.findHandlingNode(var1, var2, var3 + 1, var4);
            } else {
               this.validateVarId(var7, var6);
               if (var3 + 2 > var5) {
                  throw new SnmpStatusException(224);
               }

               if (var3 + 2 < var5) {
                  throw new SnmpStatusException(224);
               }

               if (var2[var3 + 1] != 0L) {
                  throw new SnmpStatusException(224);
               }

               var4.add(this, var3, var1);
            }

         }
      }
   }

   long[] findNextHandlingNode(SnmpVarBind var1, long[] var2, int var3, int var4, SnmpRequestTree var5, AcmChecker var6) throws SnmpStatusException {
      int var7 = var2.length;
      Object var8 = null;
      if (var5 == null) {
         throw new SnmpStatusException(225);
      } else {
         Object var9 = var5.getUserData();
         int var10 = var5.getRequestPduVersion();
         if (var3 >= var7) {
            return super.findNextHandlingNode(var1, var2, var3, var4, var5, var6);
         } else {
            long var11 = var2[var3];
            Object var13 = null;

            long[] var15;
            try {
               long[] var43;
               if (this.isTable(var11)) {
                  SnmpMibTable var44 = this.getTable(var11);
                  var6.add(var4, var11);

                  try {
                     var43 = var44.findNextHandlingNode(var1, var2, var3 + 1, var4 + 1, var5, var6);
                  } catch (SnmpStatusException var37) {
                     throw new SnmpStatusException(225);
                  } finally {
                     var6.remove(var4);
                  }

                  var43[var4] = var11;
                  return var43;
               } else {
                  if (this.isReadable(var11)) {
                     if (var3 == var7 - 1) {
                        var43 = new long[var4 + 2];
                        var43[var4 + 1] = 0L;
                        var43[var4] = var11;
                        var6.add(var4, var43, var4, 2);

                        try {
                           var6.checkCurrentOid();
                        } catch (SnmpStatusException var39) {
                           throw new SnmpStatusException(225);
                        } finally {
                           var6.remove(var4, 2);
                        }

                        var5.add(this, var4, var1);
                        return var43;
                     }
                  } else if (this.isNestedArc(var11)) {
                     SnmpMibNode var14 = this.getChild(var11);
                     if (var14 != null) {
                        var6.add(var4, var11);

                        try {
                           var43 = var14.findNextHandlingNode(var1, var2, var3 + 1, var4 + 1, var5, var6);
                           var43[var4] = var11;
                           var15 = var43;
                        } finally {
                           var6.remove(var4);
                        }

                        return var15;
                     }
                  }

                  throw new SnmpStatusException(225);
               }
            } catch (SnmpStatusException var42) {
               var15 = new long[]{this.getNextVarId(var11, var9, var10)};
               return this.findNextHandlingNode(var1, var15, 0, var4, var5, var6);
            }
         }
      }
   }
}
