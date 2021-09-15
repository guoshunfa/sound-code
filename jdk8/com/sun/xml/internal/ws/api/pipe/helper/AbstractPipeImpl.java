package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractPipeImpl implements Pipe {
   protected AbstractPipeImpl() {
   }

   protected AbstractPipeImpl(Pipe that, PipeCloner cloner) {
      cloner.add(that, this);
   }

   public void preDestroy() {
   }
}
