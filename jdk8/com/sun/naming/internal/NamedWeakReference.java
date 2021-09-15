package com.sun.naming.internal;

import java.lang.ref.WeakReference;

class NamedWeakReference<T> extends WeakReference<T> {
   private final String name;

   NamedWeakReference(T var1, String var2) {
      super(var1);
      this.name = var2;
   }

   String getName() {
      return this.name;
   }
}
