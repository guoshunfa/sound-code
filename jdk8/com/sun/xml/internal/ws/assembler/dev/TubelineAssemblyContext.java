package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;

public interface TubelineAssemblyContext {
   Pipe getAdaptedTubelineHead();

   <T> T getImplementation(Class<T> var1);

   Tube getTubelineHead();
}
