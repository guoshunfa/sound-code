package sun.tracing;

import com.sun.tracing.Probe;
import java.lang.reflect.Field;

public abstract class ProbeSkeleton implements Probe {
   protected Class<?>[] parameters;

   protected ProbeSkeleton(Class<?>[] var1) {
      this.parameters = var1;
   }

   public abstract boolean isEnabled();

   public abstract void uncheckedTrigger(Object[] var1);

   private static boolean isAssignable(Object var0, Class<?> var1) {
      if (var0 != null && !var1.isInstance(var0)) {
         if (var1.isPrimitive()) {
            try {
               Field var2 = var0.getClass().getField("TYPE");
               return var1.isAssignableFrom((Class)var2.get((Object)null));
            } catch (Exception var3) {
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void trigger(Object... var1) {
      if (var1.length != this.parameters.length) {
         throw new IllegalArgumentException("Wrong number of arguments");
      } else {
         for(int var2 = 0; var2 < this.parameters.length; ++var2) {
            if (!isAssignable(var1[var2], this.parameters[var2])) {
               throw new IllegalArgumentException("Wrong type of argument at position " + var2);
            }
         }

         this.uncheckedTrigger(var1);
      }
   }
}
