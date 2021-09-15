package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class MultiplexProbe extends ProbeSkeleton {
   private Set<Probe> probes = new HashSet();

   MultiplexProbe(Method var1, Set<Provider> var2) {
      super(var1.getParameterTypes());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Provider var4 = (Provider)var3.next();
         Probe var5 = var4.getProbe(var1);
         if (var5 != null) {
            this.probes.add(var5);
         }
      }

   }

   public boolean isEnabled() {
      Iterator var1 = this.probes.iterator();

      Probe var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Probe)var1.next();
      } while(!var2.isEnabled());

      return true;
   }

   public void uncheckedTrigger(Object[] var1) {
      Iterator var2 = this.probes.iterator();

      while(var2.hasNext()) {
         Probe var3 = (Probe)var2.next();

         try {
            ProbeSkeleton var4 = (ProbeSkeleton)var3;
            var4.uncheckedTrigger(var1);
         } catch (ClassCastException var7) {
            try {
               Method var5 = Probe.class.getMethod("trigger", Class.forName("[java.lang.Object"));
               var5.invoke(var3, var1);
            } catch (Exception var6) {
               assert false;
            }
         }
      }

   }
}
