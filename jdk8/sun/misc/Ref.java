package sun.misc;

import java.lang.ref.SoftReference;

/** @deprecated */
@Deprecated
public abstract class Ref {
   private SoftReference soft = null;

   public synchronized Object get() {
      Object var1 = this.check();
      if (var1 == null) {
         var1 = this.reconstitute();
         this.setThing(var1);
      }

      return var1;
   }

   public abstract Object reconstitute();

   public synchronized void flush() {
      SoftReference var1 = this.soft;
      if (var1 != null) {
         var1.clear();
      }

      this.soft = null;
   }

   public synchronized void setThing(Object var1) {
      this.flush();
      this.soft = new SoftReference(var1);
   }

   public synchronized Object check() {
      SoftReference var1 = this.soft;
      return var1 == null ? null : var1.get();
   }

   public Ref() {
   }

   public Ref(Object var1) {
      this.setThing(var1);
   }
}
