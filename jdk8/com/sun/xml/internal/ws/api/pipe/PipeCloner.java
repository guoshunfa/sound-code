package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

/** @deprecated */
public abstract class PipeCloner extends TubeCloner {
   public static Pipe clone(Pipe p) {
      return (new PipeClonerImpl()).copy(p);
   }

   PipeCloner(Map<Object, Object> master2copy) {
      super(master2copy);
   }

   public abstract <T extends Pipe> T copy(T var1);

   public abstract void add(Pipe var1, Pipe var2);
}
