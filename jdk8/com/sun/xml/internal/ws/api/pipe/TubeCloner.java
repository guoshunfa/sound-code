package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

public abstract class TubeCloner {
   public final Map<Object, Object> master2copy;

   public static Tube clone(Tube p) {
      return (new PipeClonerImpl()).copy(p);
   }

   TubeCloner(Map<Object, Object> master2copy) {
      this.master2copy = master2copy;
   }

   public abstract <T extends Tube> T copy(T var1);

   public abstract void add(Tube var1, Tube var2);
}
