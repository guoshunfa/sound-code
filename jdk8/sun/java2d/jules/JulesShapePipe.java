package sun.java2d.jules;

import java.awt.BasicStroke;
import java.awt.Shape;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.xr.XRCompositeManager;
import sun.java2d.xr.XRSurfaceData;

public class JulesShapePipe implements ShapeDrawPipe {
   XRCompositeManager compMan;
   JulesPathBuf buf = new JulesPathBuf();

   public JulesShapePipe(XRCompositeManager var1) {
      this.compMan = var1;
   }

   private final void validateSurface(SunGraphics2D var1) {
      XRSurfaceData var2 = (XRSurfaceData)var1.surfaceData;
      var2.validateAsDestination(var1, var1.getCompClip());
      var2.maskBuffer.validateCompositeState(var1.composite, var1.transform, var1.paint, var1);
   }

   public void draw(SunGraphics2D var1, Shape var2) {
      try {
         SunToolkit.awtLock();
         this.validateSurface(var1);
         XRSurfaceData var3 = (XRSurfaceData)var1.surfaceData;
         BasicStroke var4;
         if (var1.stroke instanceof BasicStroke) {
            var4 = (BasicStroke)var1.stroke;
         } else {
            var2 = var1.stroke.createStrokedShape(var2);
            var4 = null;
         }

         boolean var5 = var4 != null && var1.strokeHint != 2;
         boolean var6 = var1.strokeState <= 1;
         TrapezoidList var7 = this.buf.tesselateStroke(var2, var4, var6, var5, true, var1.transform, var1.getCompClip());
         this.compMan.XRCompositeTraps(var3.picture, var1.transX, var1.transY, var7);
         this.buf.clear();
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      try {
         SunToolkit.awtLock();
         this.validateSurface(var1);
         XRSurfaceData var3 = (XRSurfaceData)var1.surfaceData;
         TrapezoidList var4 = this.buf.tesselateFill(var2, var1.transform, var1.getCompClip());
         this.compMan.XRCompositeTraps(var3.picture, 0, 0, var4);
         this.buf.clear();
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
