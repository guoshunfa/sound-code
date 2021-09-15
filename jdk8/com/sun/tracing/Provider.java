package com.sun.tracing;

import java.lang.reflect.Method;

public interface Provider {
   Probe getProbe(Method var1);

   void dispose();
}
