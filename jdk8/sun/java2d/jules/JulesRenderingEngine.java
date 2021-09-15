package sun.java2d.jules;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.pisces.PiscesRenderingEngine;

public class JulesRenderingEngine extends PiscesRenderingEngine {
   public AATileGenerator getAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7) {
      return (AATileGenerator)(JulesPathBuf.isCairoAvailable() ? new JulesAATileGenerator(var1, var2, var3, var4, var5, var6, var7) : super.getAATileGenerator(var1, var2, var3, var4, var5, var6, var7));
   }

   public float getMinimumAAPenSize() {
      return 0.5F;
   }
}
