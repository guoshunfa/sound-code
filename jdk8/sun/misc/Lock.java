package sun.misc;

public class Lock {
   private boolean locked = false;

   public final synchronized void lock() throws InterruptedException {
      while(this.locked) {
         this.wait();
      }

      this.locked = true;
   }

   public final synchronized void unlock() {
      this.locked = false;
      this.notifyAll();
   }
}
