package sun.management.counter;

import java.io.Serializable;

public interface Counter extends Serializable {
   String getName();

   Units getUnits();

   Variability getVariability();

   boolean isVector();

   int getVectorLength();

   Object getValue();

   boolean isInternal();

   int getFlags();
}
