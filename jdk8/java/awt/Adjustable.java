package java.awt;

import java.awt.event.AdjustmentListener;

public interface Adjustable {
   int HORIZONTAL = 0;
   int VERTICAL = 1;
   int NO_ORIENTATION = 2;

   int getOrientation();

   void setMinimum(int var1);

   int getMinimum();

   void setMaximum(int var1);

   int getMaximum();

   void setUnitIncrement(int var1);

   int getUnitIncrement();

   void setBlockIncrement(int var1);

   int getBlockIncrement();

   void setVisibleAmount(int var1);

   int getVisibleAmount();

   void setValue(int var1);

   int getValue();

   void addAdjustmentListener(AdjustmentListener var1);

   void removeAdjustmentListener(AdjustmentListener var1);
}
