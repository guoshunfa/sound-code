package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.List;

public class SlotTableStack {
   private List tableContainer;
   private int currentIndex;
   private SlotTableStack.SlotTablePool tablePool;
   private ORB orb;
   private InterceptorsSystemException wrapper;

   SlotTableStack(ORB var1, SlotTable var2) {
      this.orb = var1;
      this.wrapper = InterceptorsSystemException.get(var1, "rpc.protocol");
      this.currentIndex = 0;
      this.tableContainer = new ArrayList();
      this.tablePool = new SlotTableStack.SlotTablePool();
      this.tableContainer.add(this.currentIndex, var2);
      ++this.currentIndex;
   }

   void pushSlotTable() {
      SlotTable var1 = this.tablePool.getSlotTable();
      if (var1 == null) {
         SlotTable var2 = this.peekSlotTable();
         var1 = new SlotTable(this.orb, var2.getSize());
      }

      if (this.currentIndex == this.tableContainer.size()) {
         this.tableContainer.add(this.currentIndex, var1);
      } else {
         if (this.currentIndex > this.tableContainer.size()) {
            throw this.wrapper.slotTableInvariant(new Integer(this.currentIndex), new Integer(this.tableContainer.size()));
         }

         this.tableContainer.set(this.currentIndex, var1);
      }

      ++this.currentIndex;
   }

   void popSlotTable() {
      if (this.currentIndex <= 1) {
         throw this.wrapper.cantPopOnlyPicurrent();
      } else {
         --this.currentIndex;
         SlotTable var1 = (SlotTable)this.tableContainer.get(this.currentIndex);
         this.tableContainer.set(this.currentIndex, (Object)null);
         var1.resetSlots();
         this.tablePool.putSlotTable(var1);
      }
   }

   SlotTable peekSlotTable() {
      return (SlotTable)this.tableContainer.get(this.currentIndex - 1);
   }

   private class SlotTablePool {
      private SlotTable[] pool = new SlotTable[5];
      private final int HIGH_WATER_MARK = 5;
      private int currentIndex = 0;

      SlotTablePool() {
      }

      void putSlotTable(SlotTable var1) {
         if (this.currentIndex < 5) {
            this.pool[this.currentIndex] = var1;
            ++this.currentIndex;
         }
      }

      SlotTable getSlotTable() {
         if (this.currentIndex == 0) {
            return null;
         } else {
            --this.currentIndex;
            return this.pool[this.currentIndex];
         }
      }
   }
}
