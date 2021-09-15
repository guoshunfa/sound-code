package java.util;

public class Observable {
   private boolean changed = false;
   private Vector<Observer> obs = new Vector();

   public synchronized void addObserver(Observer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (!this.obs.contains(var1)) {
            this.obs.addElement(var1);
         }

      }
   }

   public synchronized void deleteObserver(Observer var1) {
      this.obs.removeElement(var1);
   }

   public void notifyObservers() {
      this.notifyObservers((Object)null);
   }

   public void notifyObservers(Object var1) {
      Object[] var2;
      synchronized(this) {
         if (!this.changed) {
            return;
         }

         var2 = this.obs.toArray();
         this.clearChanged();
      }

      for(int var3 = var2.length - 1; var3 >= 0; --var3) {
         ((Observer)var2[var3]).update(this, var1);
      }

   }

   public synchronized void deleteObservers() {
      this.obs.removeAllElements();
   }

   protected synchronized void setChanged() {
      this.changed = true;
   }

   protected synchronized void clearChanged() {
      this.changed = false;
   }

   public synchronized boolean hasChanged() {
      return this.changed;
   }

   public synchronized int countObservers() {
      return this.obs.size();
   }
}
