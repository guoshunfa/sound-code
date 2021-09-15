package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Vector;

public abstract class SnmpMibNode implements Serializable {
   protected int[] varList;

   public long getNextVarId(long var1, Object var3) throws SnmpStatusException {
      return (long)getNextIdentifier(this.varList, var1);
   }

   public long getNextVarId(long var1, Object var3, int var4) throws SnmpStatusException {
      long var5 = var1;

      do {
         var5 = this.getNextVarId(var5, var3);
      } while(this.skipVariable(var5, var3, var4));

      return var5;
   }

   protected boolean skipVariable(long var1, Object var3, int var4) {
      return false;
   }

   void findHandlingNode(SnmpVarBind var1, long[] var2, int var3, SnmpRequestTree var4) throws SnmpStatusException {
      throw new SnmpStatusException(225);
   }

   long[] findNextHandlingNode(SnmpVarBind var1, long[] var2, int var3, int var4, SnmpRequestTree var5, AcmChecker var6) throws SnmpStatusException {
      throw new SnmpStatusException(225);
   }

   public abstract void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public abstract void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException;

   public static void sort(int[] var0) {
      QuickSort(var0, 0, var0.length - 1);
   }

   public void getRootOid(Vector<Integer> var1) {
   }

   static void QuickSort(int[] var0, int var1, int var2) {
      int var3 = var1;
      int var4 = var2;
      if (var2 > var1) {
         int var5 = var0[(var1 + var2) / 2];

         while(var3 <= var4) {
            while(var3 < var2 && var0[var3] < var5) {
               ++var3;
            }

            while(var4 > var1 && var0[var4] > var5) {
               --var4;
            }

            if (var3 <= var4) {
               swap(var0, var3, var4);
               ++var3;
               --var4;
            }
         }

         if (var1 < var4) {
            QuickSort(var0, var1, var4);
         }

         if (var3 < var2) {
            QuickSort(var0, var3, var2);
         }
      }

   }

   protected static final int getNextIdentifier(int[] var0, long var1) throws SnmpStatusException {
      int[] var3 = var0;
      int var4 = (int)var1;
      if (var0 == null) {
         throw new SnmpStatusException(225);
      } else {
         int var5 = 0;
         int var6 = var0.length;
         int var7 = var5 + (var6 - var5) / 2;
         boolean var8 = false;
         if (var6 < 1) {
            throw new SnmpStatusException(225);
         } else if (var0[var6 - 1] <= var4) {
            throw new SnmpStatusException(225);
         } else {
            for(; var5 <= var6; var7 = var5 + (var6 - var5) / 2) {
               int var9 = var3[var7];
               if (var4 == var9) {
                  ++var7;
                  return var3[var7];
               }

               if (var9 < var4) {
                  var5 = var7 + 1;
               } else {
                  var6 = var7 - 1;
               }
            }

            return var3[var7];
         }
      }
   }

   private static final void swap(int[] var0, int var1, int var2) {
      int var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }
}
