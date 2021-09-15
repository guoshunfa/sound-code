package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

public class PICurrent extends LocalObject implements Current {
   private int slotCounter;
   private ORB myORB;
   private OMGSystemException wrapper;
   private boolean orbInitializing;
   private ThreadLocal threadLocalSlotTable = new ThreadLocal() {
      protected Object initialValue() {
         SlotTable var1 = new SlotTable(PICurrent.this.myORB, PICurrent.this.slotCounter);
         return new SlotTableStack(PICurrent.this.myORB, var1);
      }
   };

   PICurrent(ORB var1) {
      this.myORB = var1;
      this.wrapper = OMGSystemException.get(var1, "rpc.protocol");
      this.orbInitializing = true;
      this.slotCounter = 0;
   }

   int allocateSlotId() {
      int var1 = this.slotCounter++;
      return var1;
   }

   SlotTable getSlotTable() {
      SlotTable var1 = ((SlotTableStack)this.threadLocalSlotTable.get()).peekSlotTable();
      return var1;
   }

   void pushSlotTable() {
      SlotTableStack var1 = (SlotTableStack)this.threadLocalSlotTable.get();
      var1.pushSlotTable();
   }

   void popSlotTable() {
      SlotTableStack var1 = (SlotTableStack)this.threadLocalSlotTable.get();
      var1.popSlotTable();
   }

   public void set_slot(int var1, Any var2) throws InvalidSlot {
      if (this.orbInitializing) {
         throw this.wrapper.invalidPiCall3();
      } else {
         this.getSlotTable().set_slot(var1, var2);
      }
   }

   public Any get_slot(int var1) throws InvalidSlot {
      if (this.orbInitializing) {
         throw this.wrapper.invalidPiCall4();
      } else {
         return this.getSlotTable().get_slot(var1);
      }
   }

   void resetSlotTable() {
      this.getSlotTable().resetSlots();
   }

   void setORBInitializing(boolean var1) {
      this.orbInitializing = var1;
   }
}
