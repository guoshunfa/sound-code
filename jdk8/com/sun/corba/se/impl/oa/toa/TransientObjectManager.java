package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;

public final class TransientObjectManager {
   private ORB orb;
   private int maxSize = 128;
   private Element[] elementArray;
   private Element freeList;

   void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   public TransientObjectManager(ORB var1) {
      this.orb = var1;
      this.elementArray = new Element[this.maxSize];
      this.elementArray[this.maxSize - 1] = new Element(this.maxSize - 1, (Object)null);

      for(int var2 = this.maxSize - 2; var2 >= 0; --var2) {
         this.elementArray[var2] = new Element(var2, this.elementArray[var2 + 1]);
      }

      this.freeList = this.elementArray[0];
   }

   public synchronized byte[] storeServant(Object var1, Object var2) {
      if (this.freeList == null) {
         this.doubleSize();
      }

      Element var3 = this.freeList;
      this.freeList = (Element)this.freeList.servant;
      byte[] var4 = var3.getKey(var1, var2);
      if (this.orb.transientObjectManagerDebugFlag) {
         this.dprint("storeServant returns key for element " + var3);
      }

      return var4;
   }

   public synchronized Object lookupServant(byte[] var1) {
      int var2 = ORBUtility.bytesToInt(var1, 0);
      int var3 = ORBUtility.bytesToInt(var1, 4);
      if (this.orb.transientObjectManagerDebugFlag) {
         this.dprint("lookupServant called with index=" + var2 + ", counter=" + var3);
      }

      if (this.elementArray[var2].counter == var3 && this.elementArray[var2].valid) {
         if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is valid");
         }

         return this.elementArray[var2].servant;
      } else {
         if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is invalid");
         }

         return null;
      }
   }

   public synchronized Object lookupServantData(byte[] var1) {
      int var2 = ORBUtility.bytesToInt(var1, 0);
      int var3 = ORBUtility.bytesToInt(var1, 4);
      if (this.orb.transientObjectManagerDebugFlag) {
         this.dprint("lookupServantData called with index=" + var2 + ", counter=" + var3);
      }

      if (this.elementArray[var2].counter == var3 && this.elementArray[var2].valid) {
         if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is valid");
         }

         return this.elementArray[var2].servantData;
      } else {
         if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is invalid");
         }

         return null;
      }
   }

   public synchronized void deleteServant(byte[] var1) {
      int var2 = ORBUtility.bytesToInt(var1, 0);
      if (this.orb.transientObjectManagerDebugFlag) {
         this.dprint("deleting servant at index=" + var2);
      }

      this.elementArray[var2].delete(this.freeList);
      this.freeList = this.elementArray[var2];
   }

   public synchronized byte[] getKey(Object var1) {
      for(int var2 = 0; var2 < this.maxSize; ++var2) {
         if (this.elementArray[var2].valid && this.elementArray[var2].servant == var1) {
            return this.elementArray[var2].toBytes();
         }
      }

      return null;
   }

   private void doubleSize() {
      Element[] var1 = this.elementArray;
      int var2 = this.maxSize;
      this.maxSize *= 2;
      this.elementArray = new Element[this.maxSize];

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.elementArray[var3] = var1[var3];
      }

      this.elementArray[this.maxSize - 1] = new Element(this.maxSize - 1, (Object)null);

      for(var3 = this.maxSize - 2; var3 >= var2; --var3) {
         this.elementArray[var3] = new Element(var3, this.elementArray[var3 + 1]);
      }

      this.freeList = this.elementArray[var2];
   }
}
