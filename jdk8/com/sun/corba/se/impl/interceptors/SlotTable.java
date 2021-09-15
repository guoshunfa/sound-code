package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.PortableInterceptor.InvalidSlot;

public class SlotTable {
   private Any[] theSlotData;
   private ORB orb;
   private boolean dirtyFlag = false;

   SlotTable(ORB var1, int var2) {
      this.orb = var1;
      this.theSlotData = new Any[var2];
   }

   public void set_slot(int var1, Any var2) throws InvalidSlot {
      if (var1 >= this.theSlotData.length) {
         throw new InvalidSlot();
      } else {
         this.dirtyFlag = true;
         this.theSlotData[var1] = var2;
      }
   }

   public Any get_slot(int var1) throws InvalidSlot {
      if (var1 >= this.theSlotData.length) {
         throw new InvalidSlot();
      } else {
         if (this.theSlotData[var1] == null) {
            this.theSlotData[var1] = new AnyImpl(this.orb);
         }

         return this.theSlotData[var1];
      }
   }

   void resetSlots() {
      if (this.dirtyFlag) {
         for(int var1 = 0; var1 < this.theSlotData.length; ++var1) {
            this.theSlotData[var1] = null;
         }
      }

   }

   int getSize() {
      return this.theSlotData.length;
   }
}
