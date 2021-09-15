package java.awt.font;

import java.awt.Font;

public interface MultipleMaster {
   int getNumDesignAxes();

   float[] getDesignAxisRanges();

   float[] getDesignAxisDefaults();

   String[] getDesignAxisNames();

   Font deriveMMFont(float[] var1);

   Font deriveMMFont(float[] var1, float var2, float var3, float var4, float var5);
}
