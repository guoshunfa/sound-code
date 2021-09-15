package sun.java2d.xr;

import java.awt.Color;
import java.util.ArrayList;

public class MaskTileManager {
   public static final int MASK_SIZE = 256;
   MaskTile mainTile = new MaskTile();
   ArrayList<MaskTile> tileList = new ArrayList();
   int allocatedTiles = 0;
   int xTiles;
   int yTiles;
   XRCompositeManager xrMgr;
   XRBackend con;
   int maskPixmap;
   int maskPicture;
   long maskGC;

   public MaskTileManager(XRCompositeManager var1, int var2) {
      this.xrMgr = var1;
      this.con = var1.getBackend();
      this.maskPixmap = this.con.createPixmap(var2, 8, 256, 256);
      this.maskPicture = this.con.createPicture(this.maskPixmap, 2);
      this.con.renderRectangle(this.maskPicture, (byte)0, new XRColor(Color.black), 0, 0, 256, 256);
      this.maskGC = this.con.createGC(this.maskPixmap);
      this.con.setGCExposures(this.maskGC, false);
   }

   public void fillMask(XRSurfaceData var1) {
      boolean var2 = this.xrMgr.maskRequired();
      boolean var3 = XRUtils.isMaskEvaluated(this.xrMgr.compRule);
      if (var2 && var3) {
         this.mainTile.calculateDirtyAreas();
         DirtyRegion var4 = this.mainTile.getDirtyArea().cloneRegion();
         this.mainTile.translate(-var4.x, -var4.y);
         XRColor var5 = this.xrMgr.getMaskColor();
         if (var4.getWidth() <= 256 && var4.getHeight() <= 256) {
            this.compositeSingleTile(var1, this.mainTile, var4, var2, 0, 0, var5);
         } else {
            this.allocTiles(var4);
            this.tileRects();

            for(int var6 = 0; var6 < this.yTiles; ++var6) {
               for(int var7 = 0; var7 < this.xTiles; ++var7) {
                  MaskTile var8 = (MaskTile)this.tileList.get(var6 * this.xTiles + var7);
                  int var9 = var7 * 256;
                  int var10 = var6 * 256;
                  this.compositeSingleTile(var1, var8, var4, var2, var9, var10, var5);
               }
            }
         }
      } else if (this.xrMgr.isSolidPaintActive()) {
         this.xrMgr.XRRenderRectangles(var1, this.mainTile.getRects());
      } else {
         this.xrMgr.XRCompositeRectangles(var1, this.mainTile.getRects());
      }

      this.mainTile.reset();
   }

   public int uploadMask(int var1, int var2, int var3, int var4, byte[] var5) {
      int var6 = 0;
      if (var5 != null) {
         float var7 = this.xrMgr.isTexturePaintActive() ? this.xrMgr.getExtraAlpha() : 1.0F;
         this.con.putMaskImage(this.maskPixmap, this.maskGC, var5, 0, 0, 0, 0, var1, var2, var4, var3, var7);
         var6 = this.maskPicture;
      } else if (this.xrMgr.isTexturePaintActive()) {
         var6 = this.xrMgr.getExtraAlphaMask();
      }

      return var6;
   }

   public void clearUploadMask(int var1, int var2, int var3) {
      if (var1 == this.maskPicture) {
         this.con.renderRectangle(this.maskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, var2, var3);
      }

   }

   protected void compositeSingleTile(XRSurfaceData var1, MaskTile var2, DirtyRegion var3, boolean var4, int var5, int var6, XRColor var7) {
      if (var2.rects.getSize() > 0) {
         DirtyRegion var8 = var2.getDirtyArea();
         int var9 = var8.x + var5 + var3.x;
         int var10 = var8.y + var6 + var3.y;
         int var11 = var8.x2 - var8.x;
         int var12 = var8.y2 - var8.y;
         var11 = Math.min(var11, 256);
         var12 = Math.min(var12, 256);
         int var13 = var2.rects.getSize();
         if (var4) {
            int var14 = 0;
            if (var13 > 1) {
               this.con.renderRectangles(this.maskPicture, (byte)1, var7, var2.rects);
               var14 = this.maskPicture;
            } else if (this.xrMgr.isTexturePaintActive()) {
               var14 = this.xrMgr.getExtraAlphaMask();
            }

            this.xrMgr.XRComposite(0, var14, var1.getPicture(), var9, var10, var8.x, var8.y, var9, var10, var11, var12);
            if (var13 > 1) {
               this.con.renderRectangle(this.maskPicture, (byte)0, XRColor.NO_ALPHA, var8.x, var8.y, var11, var12);
            }

            var2.reset();
         } else if (var13 > 0) {
            var2.rects.translateRects(var5 + var3.x, var6 + var3.y);
            this.xrMgr.XRRenderRectangles(var1, var2.rects);
         }
      }

   }

   protected void allocTiles(DirtyRegion var1) {
      this.xTiles = var1.getWidth() / 256 + 1;
      this.yTiles = var1.getHeight() / 256 + 1;
      int var2 = this.xTiles * this.yTiles;
      if (var2 > this.allocatedTiles) {
         for(int var3 = 0; var3 < var2; ++var3) {
            if (var3 < this.allocatedTiles) {
               ((MaskTile)this.tileList.get(var3)).reset();
            } else {
               this.tileList.add(new MaskTile());
            }
         }

         this.allocatedTiles = var2;
      }

   }

   protected void tileRects() {
      GrowableRectArray var1 = this.mainTile.rects;

      for(int var2 = 0; var2 < var1.getSize(); ++var2) {
         int var3 = var1.getX(var2) / 256;
         int var4 = var1.getY(var2) / 256;
         int var5 = (var1.getX(var2) + var1.getWidth(var2)) / 256 + 1 - var3;
         int var6 = (var1.getY(var2) + var1.getHeight(var2)) / 256 + 1 - var4;

         for(int var7 = 0; var7 < var6; ++var7) {
            for(int var8 = 0; var8 < var5; ++var8) {
               int var9 = this.xTiles * (var4 + var7) + var3 + var8;
               MaskTile var10 = (MaskTile)this.tileList.get(var9);
               GrowableRectArray var11 = var10.getRects();
               int var12 = var11.getNextIndex();
               int var13 = (var3 + var8) * 256;
               int var14 = (var4 + var7) * 256;
               var11.setX(var12, var1.getX(var2) - var13);
               var11.setY(var12, var1.getY(var2) - var14);
               var11.setWidth(var12, var1.getWidth(var2));
               var11.setHeight(var12, var1.getHeight(var2));
               this.limitRectCoords(var11, var12);
               var10.getDirtyArea().growDirtyRegion(var11.getX(var12), var11.getY(var12), var11.getWidth(var12) + var11.getX(var12), var11.getHeight(var12) + var11.getY(var12));
            }
         }
      }

   }

   private void limitRectCoords(GrowableRectArray var1, int var2) {
      if (var1.getX(var2) + var1.getWidth(var2) > 256) {
         var1.setWidth(var2, 256 - var1.getX(var2));
      }

      if (var1.getY(var2) + var1.getHeight(var2) > 256) {
         var1.setHeight(var2, 256 - var1.getY(var2));
      }

      if (var1.getX(var2) < 0) {
         var1.setWidth(var2, var1.getWidth(var2) + var1.getX(var2));
         var1.setX(var2, 0);
      }

      if (var1.getY(var2) < 0) {
         var1.setHeight(var2, var1.getHeight(var2) + var1.getY(var2));
         var1.setY(var2, 0);
      }

   }

   public MaskTile getMainTile() {
      return this.mainTile;
   }
}
