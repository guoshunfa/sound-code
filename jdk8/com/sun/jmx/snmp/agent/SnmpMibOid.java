package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpMibOid extends SnmpMibNode implements Serializable {
   private static final long serialVersionUID = 5012254771107446812L;
   private SnmpMibOid.NonSyncVector<SnmpMibNode> children = new SnmpMibOid.NonSyncVector(1);
   private int nbChildren = 0;

   public void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      Enumeration var3 = var1.getElements();

      while(var3.hasMoreElements()) {
         SnmpVarBind var4 = (SnmpVarBind)var3.nextElement();
         SnmpStatusException var5 = new SnmpStatusException(225);
         var1.registerGetException(var4, var5);
      }

   }

   public void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      Enumeration var3 = var1.getElements();

      while(var3.hasMoreElements()) {
         SnmpVarBind var4 = (SnmpVarBind)var3.nextElement();
         SnmpStatusException var5 = new SnmpStatusException(6);
         var1.registerSetException(var4, var5);
      }

   }

   public void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      Enumeration var3 = var1.getElements();

      while(var3.hasMoreElements()) {
         SnmpVarBind var4 = (SnmpVarBind)var3.nextElement();
         SnmpStatusException var5 = new SnmpStatusException(6);
         var1.registerCheckException(var4, var5);
      }

   }

   void findHandlingNode(SnmpVarBind var1, long[] var2, int var3, SnmpRequestTree var4) throws SnmpStatusException {
      int var5 = var2.length;
      Object var6 = null;
      if (var4 == null) {
         throw new SnmpStatusException(5);
      } else if (var3 > var5) {
         throw new SnmpStatusException(225);
      } else if (var3 == var5) {
         throw new SnmpStatusException(224);
      } else {
         SnmpMibNode var7 = this.getChild(var2[var3]);
         if (var7 == null) {
            var4.add(this, var3, var1);
         } else {
            var7.findHandlingNode(var1, var2, var3 + 1, var4);
         }

      }
   }

   long[] findNextHandlingNode(SnmpVarBind var1, long[] var2, int var3, int var4, SnmpRequestTree var5, AcmChecker var6) throws SnmpStatusException {
      int var7 = var2.length;
      Object var8 = null;
      Object var9 = null;
      if (var5 == null) {
         throw new SnmpStatusException(225);
      } else {
         Object var10 = var5.getUserData();
         int var11 = var5.getRequestPduVersion();
         long[] var12;
         long[] var21;
         if (var3 >= var7) {
            var12 = new long[]{this.getNextVarId(-1L, var10, var11)};
            var21 = this.findNextHandlingNode(var1, var12, 0, var4, var5, var6);
            return var21;
         } else {
            var12 = new long[1];
            long var13 = var2[var3];

            while(true) {
               try {
                  SnmpMibNode var15 = this.getChild(var13);
                  if (var15 == null) {
                     throw new SnmpStatusException(225);
                  }

                  var6.add(var4, var13);

                  try {
                     var21 = var15.findNextHandlingNode(var1, var2, var3 + 1, var4 + 1, var5, var6);
                  } finally {
                     var6.remove(var4);
                  }

                  var21[var4] = var13;
                  return var21;
               } catch (SnmpStatusException var20) {
                  var13 = this.getNextVarId(var13, var10, var11);
                  var12[0] = var13;
                  var3 = 1;
                  var2 = var12;
               }
            }
         }
      }
   }

   public void getRootOid(Vector<Integer> var1) {
      if (this.nbChildren == 1) {
         var1.addElement(this.varList[0]);
         ((SnmpMibNode)this.children.firstElement()).getRootOid(var1);
      }
   }

   public void registerNode(String var1, SnmpMibNode var2) throws IllegalAccessException {
      SnmpOid var3 = new SnmpOid(var1);
      this.registerNode(var3.longValue(), 0, var2);
   }

   void registerNode(long[] var1, int var2, SnmpMibNode var3) throws IllegalAccessException {
      if (var2 >= var1.length) {
         throw new IllegalAccessException();
      } else {
         long var4 = var1[var2];
         int var6 = this.retrieveIndex(var4);
         if (var6 == this.nbChildren) {
            ++this.nbChildren;
            this.varList = new int[this.nbChildren];
            this.varList[0] = (int)var4;
            byte var10 = 0;
            if (var2 + 1 == var1.length) {
               this.children.insertElementAt(var3, var10);
            } else {
               SnmpMibOid var12 = new SnmpMibOid();
               this.children.insertElementAt(var12, var10);
               var12.registerNode(var1, var2 + 1, var3);
            }
         } else {
            if (var6 == -1) {
               int[] var7 = new int[this.nbChildren + 1];
               var7[this.nbChildren] = (int)var4;
               System.arraycopy(this.varList, 0, var7, 0, this.nbChildren);
               this.varList = var7;
               ++this.nbChildren;
               SnmpMibNode.sort(this.varList);
               int var8 = this.retrieveIndex(var4);
               this.varList[var8] = (int)var4;
               if (var2 + 1 == var1.length) {
                  this.children.insertElementAt(var3, var8);
                  return;
               }

               SnmpMibOid var9 = new SnmpMibOid();
               this.children.insertElementAt(var9, var8);
               var9.registerNode(var1, var2 + 1, var3);
            } else {
               SnmpMibNode var11 = (SnmpMibNode)this.children.elementAt(var6);
               if (var2 + 1 == var1.length) {
                  if (var11 == var3) {
                     return;
                  }

                  if (var11 != null && var3 != null) {
                     if (var3 instanceof SnmpMibGroup) {
                        ((SnmpMibOid)var11).exportChildren((SnmpMibOid)var3);
                        this.children.setElementAt(var3, var6);
                        return;
                     }

                     if (var3 instanceof SnmpMibOid && var11 instanceof SnmpMibGroup) {
                        ((SnmpMibOid)var3).exportChildren((SnmpMibOid)var11);
                        return;
                     }

                     if (var3 instanceof SnmpMibOid) {
                        ((SnmpMibOid)var11).exportChildren((SnmpMibOid)var3);
                        this.children.setElementAt(var3, var6);
                        return;
                     }
                  }

                  this.children.setElementAt(var3, var6);
               } else {
                  if (var11 == null) {
                     throw new IllegalAccessException();
                  }

                  ((SnmpMibOid)var11).registerNode(var1, var2 + 1, var3);
               }
            }

         }
      }
   }

   void exportChildren(SnmpMibOid var1) throws IllegalAccessException {
      if (var1 != null) {
         long[] var2 = new long[1];

         for(int var3 = 0; var3 < this.nbChildren; ++var3) {
            SnmpMibNode var4 = (SnmpMibNode)this.children.elementAt(var3);
            if (var4 != null) {
               var2[0] = (long)this.varList[var3];
               var1.registerNode(var2, 0, var4);
            }
         }

      }
   }

   SnmpMibNode getChild(long var1) throws SnmpStatusException {
      int var3 = this.getInsertAt(var1);
      if (var3 >= this.nbChildren) {
         throw new SnmpStatusException(225);
      } else if (this.varList[var3] != (int)var1) {
         throw new SnmpStatusException(225);
      } else {
         SnmpMibNode var4 = null;

         try {
            var4 = (SnmpMibNode)this.children.elementAtNonSync(var3);
         } catch (ArrayIndexOutOfBoundsException var6) {
            throw new SnmpStatusException(225);
         }

         if (var4 == null) {
            throw new SnmpStatusException(224);
         } else {
            return var4;
         }
      }
   }

   private int retrieveIndex(long var1) {
      int var3 = 0;
      int var4 = (int)var1;
      if (this.varList != null && this.varList.length >= 1) {
         int var5 = this.varList.length - 1;

         for(int var6 = var3 + (var5 - var3) / 2; var3 <= var5; var6 = var3 + (var5 - var3) / 2) {
            int var7 = this.varList[var6];
            if (var4 == var7) {
               return var6;
            }

            if (var7 < var4) {
               var3 = var6 + 1;
            } else {
               var5 = var6 - 1;
            }
         }

         return -1;
      } else {
         return this.nbChildren;
      }
   }

   private int getInsertAt(long var1) {
      int var3 = 0;
      int var4 = (int)var1;
      if (this.varList == null) {
         return -1;
      } else {
         int var5 = this.varList.length - 1;

         int var7;
         for(var7 = var3 + (var5 - var3) / 2; var3 <= var5; var7 = var3 + (var5 - var3) / 2) {
            int var6 = this.varList[var7];
            if (var4 == var6) {
               return var7;
            }

            if (var6 < var4) {
               var3 = var7 + 1;
            } else {
               var5 = var7 - 1;
            }
         }

         return var7;
      }
   }

   class NonSyncVector<E> extends Vector<E> {
      public NonSyncVector(int var2) {
         super(var2);
      }

      final void addNonSyncElement(E var1) {
         this.ensureCapacity(this.elementCount + 1);
         this.elementData[this.elementCount++] = var1;
      }

      final E elementAtNonSync(int var1) {
         return this.elementData[var1];
      }
   }
}
