package com.sun.xml.internal.bind;

import javax.xml.bind.Marshaller;

public interface CycleRecoverable {
   Object onCycleDetected(CycleRecoverable.Context var1);

   public interface Context {
      Marshaller getMarshaller();
   }
}
