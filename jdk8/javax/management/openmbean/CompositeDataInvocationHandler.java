package javax.management.openmbean;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import com.sun.jmx.mbeanserver.MXBeanLookup;
import com.sun.jmx.mbeanserver.MXBeanMapping;
import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CompositeDataInvocationHandler implements InvocationHandler {
   private final CompositeData compositeData;
   private final MXBeanLookup lookup;

   public CompositeDataInvocationHandler(CompositeData var1) {
      this(var1, (MXBeanLookup)null);
   }

   CompositeDataInvocationHandler(CompositeData var1, MXBeanLookup var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("compositeData");
      } else {
         this.compositeData = var1;
         this.lookup = var2;
      }
   }

   public CompositeData getCompositeData() {
      assert this.compositeData != null;

      return this.compositeData;
   }

   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      String var4 = var2.getName();
      if (var2.getDeclaringClass() == Object.class) {
         if (var4.equals("toString") && var3 == null) {
            return "Proxy[" + this.compositeData + "]";
         } else if (var4.equals("hashCode") && var3 == null) {
            return this.compositeData.hashCode() + 1128548680;
         } else {
            return var4.equals("equals") && var3.length == 1 && var2.getParameterTypes()[0] == Object.class ? this.equals(var1, var3[0]) : var2.invoke(this, var3);
         }
      } else {
         String var5 = DefaultMXBeanMappingFactory.propertyName(var2);
         if (var5 == null) {
            throw new IllegalArgumentException("Method is not getter: " + var2.getName());
         } else {
            Object var6;
            if (this.compositeData.containsKey(var5)) {
               var6 = this.compositeData.get(var5);
            } else {
               String var7 = DefaultMXBeanMappingFactory.decapitalize(var5);
               if (!this.compositeData.containsKey(var7)) {
                  String var8 = "No CompositeData item " + var5 + (var7.equals(var5) ? "" : " or " + var7) + " to match " + var4;
                  throw new IllegalArgumentException(var8);
               }

               var6 = this.compositeData.get(var7);
            }

            MXBeanMapping var9 = MXBeanMappingFactory.DEFAULT.mappingForType(var2.getGenericReturnType(), MXBeanMappingFactory.DEFAULT);
            return var9.fromOpenValue(var6);
         }
      }
   }

   private boolean equals(Object var1, Object var2) {
      if (var2 == null) {
         return false;
      } else {
         Class var3 = var1.getClass();
         Class var4 = var2.getClass();
         if (var3 != var4) {
            return false;
         } else {
            InvocationHandler var5 = Proxy.getInvocationHandler(var2);
            if (!(var5 instanceof CompositeDataInvocationHandler)) {
               return false;
            } else {
               CompositeDataInvocationHandler var6 = (CompositeDataInvocationHandler)var5;
               return this.compositeData.equals(var6.compositeData);
            }
         }
      }
   }
}
