package sun.awt.image;

import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufImgSurfaceManager extends SurfaceManager {
   protected BufferedImage bImg;
   protected SurfaceData sdDefault;

   public BufImgSurfaceManager(BufferedImage var1) {
      this.bImg = var1;
      this.sdDefault = BufImgSurfaceData.createData(var1);
   }

   public SurfaceData getPrimarySurfaceData() {
      return this.sdDefault;
   }

   public SurfaceData restoreContents() {
      return this.sdDefault;
   }
}
