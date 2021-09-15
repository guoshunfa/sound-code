package com.sun.tracing;

public interface Probe {
   boolean isEnabled();

   void trigger(Object... var1);
}
