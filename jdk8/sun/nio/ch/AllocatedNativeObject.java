package sun.nio.ch;

class AllocatedNativeObject extends NativeObject {
   AllocatedNativeObject(int var1, boolean var2) {
      super(var1, var2);
   }

   synchronized void free() {
      if (this.allocationAddress != 0L) {
         unsafe.freeMemory(this.allocationAddress);
         this.allocationAddress = 0L;
      }

   }
}
