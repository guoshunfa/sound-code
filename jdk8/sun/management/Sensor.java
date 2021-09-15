package sun.management;

import java.lang.management.MemoryUsage;

public abstract class Sensor {
   private Object lock;
   private String name;
   private long count;
   private boolean on;

   public Sensor(String var1) {
      this.name = var1;
      this.count = 0L;
      this.on = false;
      this.lock = new Object();
   }

   public String getName() {
      return this.name;
   }

   public long getCount() {
      synchronized(this.lock) {
         return this.count;
      }
   }

   public boolean isOn() {
      synchronized(this.lock) {
         return this.on;
      }
   }

   public void trigger() {
      synchronized(this.lock) {
         this.on = true;
         ++this.count;
      }

      this.triggerAction();
   }

   public void trigger(int var1) {
      synchronized(this.lock) {
         this.on = true;
         this.count += (long)var1;
      }

      this.triggerAction();
   }

   public void trigger(int var1, MemoryUsage var2) {
      synchronized(this.lock) {
         this.on = true;
         this.count += (long)var1;
      }

      this.triggerAction(var2);
   }

   public void clear() {
      synchronized(this.lock) {
         this.on = false;
      }

      this.clearAction();
   }

   public void clear(int var1) {
      synchronized(this.lock) {
         this.on = false;
         this.count += (long)var1;
      }

      this.clearAction();
   }

   public String toString() {
      return "Sensor - " + this.getName() + (this.isOn() ? " on " : " off ") + " count = " + this.getCount();
   }

   abstract void triggerAction();

   abstract void triggerAction(MemoryUsage var1);

   abstract void clearAction();
}
