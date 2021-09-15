package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.JMX;

public class DescriptorCache {
   private static final DescriptorCache instance = new DescriptorCache();
   private final WeakHashMap<ImmutableDescriptor, WeakReference<ImmutableDescriptor>> map = new WeakHashMap();

   private DescriptorCache() {
   }

   static DescriptorCache getInstance() {
      return instance;
   }

   public static DescriptorCache getInstance(JMX var0) {
      return var0 != null ? instance : null;
   }

   public ImmutableDescriptor get(ImmutableDescriptor var1) {
      WeakReference var2 = (WeakReference)this.map.get(var1);
      ImmutableDescriptor var3 = var2 == null ? null : (ImmutableDescriptor)var2.get();
      if (var3 != null) {
         return var3;
      } else {
         this.map.put(var1, new WeakReference(var1));
         return var1;
      }
   }

   public ImmutableDescriptor union(Descriptor... var1) {
      return this.get(ImmutableDescriptor.union(var1));
   }
}
