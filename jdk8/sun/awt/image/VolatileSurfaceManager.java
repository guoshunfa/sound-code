package sun.awt.image;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import sun.awt.DisplayChangedListener;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceData;

public abstract class VolatileSurfaceManager extends SurfaceManager implements DisplayChangedListener {
   protected SunVolatileImage vImg;
   protected SurfaceData sdAccel;
   protected SurfaceData sdBackup;
   protected SurfaceData sdCurrent;
   protected SurfaceData sdPrevious;
   protected boolean lostSurface;
   protected Object context;

   protected VolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      this.vImg = var1;
      this.context = var2;
      GraphicsEnvironment var3 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (var3 instanceof SunGraphicsEnvironment) {
         ((SunGraphicsEnvironment)var3).addDisplayChangedListener(this);
      }

   }

   public void initialize() {
      if (this.isAccelerationEnabled()) {
         this.sdAccel = this.initAcceleratedSurface();
         if (this.sdAccel != null) {
            this.sdCurrent = this.sdAccel;
         }
      }

      if (this.sdCurrent == null && this.vImg.getForcedAccelSurfaceType() == 0) {
         this.sdCurrent = this.getBackupSurface();
      }

   }

   public SurfaceData getPrimarySurfaceData() {
      return this.sdCurrent;
   }

   protected abstract boolean isAccelerationEnabled();

   public int validate(GraphicsConfiguration var1) {
      byte var2 = 0;
      boolean var3 = this.lostSurface;
      this.lostSurface = false;
      if (this.isAccelerationEnabled()) {
         if (!this.isConfigValid(var1)) {
            var2 = 2;
         } else if (this.sdAccel == null) {
            this.sdAccel = this.initAcceleratedSurface();
            if (this.sdAccel != null) {
               this.sdCurrent = this.sdAccel;
               this.sdBackup = null;
               var2 = 1;
            } else {
               this.sdCurrent = this.getBackupSurface();
            }
         } else if (this.sdAccel.isSurfaceLost()) {
            try {
               this.restoreAcceleratedSurface();
               this.sdCurrent = this.sdAccel;
               this.sdAccel.setSurfaceLost(false);
               this.sdBackup = null;
               var2 = 1;
            } catch (InvalidPipeException var5) {
               this.sdCurrent = this.getBackupSurface();
            }
         } else if (var3) {
            var2 = 1;
         }
      } else if (this.sdAccel != null) {
         this.sdCurrent = this.getBackupSurface();
         this.sdAccel = null;
         var2 = 1;
      }

      if (var2 != 2 && this.sdCurrent != this.sdPrevious) {
         this.sdPrevious = this.sdCurrent;
         var2 = 1;
      }

      if (var2 == 1) {
         this.initContents();
      }

      return var2;
   }

   public boolean contentsLost() {
      return this.lostSurface;
   }

   protected abstract SurfaceData initAcceleratedSurface();

   protected SurfaceData getBackupSurface() {
      if (this.sdBackup == null) {
         BufferedImage var1 = this.vImg.getBackupImage();
         SunWritableRaster.stealTrackable(var1.getRaster().getDataBuffer()).setUntrackable();
         this.sdBackup = BufImgSurfaceData.createData(var1);
      }

      return this.sdBackup;
   }

   public void initContents() {
      if (this.sdCurrent != null) {
         Graphics2D var1 = this.vImg.createGraphics();
         var1.clearRect(0, 0, this.vImg.getWidth(), this.vImg.getHeight());
         var1.dispose();
      }

   }

   public SurfaceData restoreContents() {
      return this.getBackupSurface();
   }

   public void acceleratedSurfaceLost() {
      if (this.isAccelerationEnabled() && this.sdCurrent == this.sdAccel) {
         this.lostSurface = true;
      }

   }

   protected void restoreAcceleratedSurface() {
   }

   public void displayChanged() {
      if (this.isAccelerationEnabled()) {
         this.lostSurface = true;
         if (this.sdAccel != null) {
            this.sdBackup = null;
            SurfaceData var1 = this.sdAccel;
            this.sdAccel = null;
            var1.invalidate();
            this.sdCurrent = this.getBackupSurface();
         }

         this.vImg.updateGraphicsConfig();
      }
   }

   public void paletteChanged() {
      this.lostSurface = true;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return var1 == null || var1.getDevice() == this.vImg.getGraphicsConfig().getDevice();
   }

   public ImageCapabilities getCapabilities(GraphicsConfiguration var1) {
      if (this.isConfigValid(var1)) {
         return (ImageCapabilities)(this.isAccelerationEnabled() ? new VolatileSurfaceManager.AcceleratedImageCapabilities() : new ImageCapabilities(false));
      } else {
         return super.getCapabilities(var1);
      }
   }

   public void flush() {
      this.lostSurface = true;
      SurfaceData var1 = this.sdAccel;
      this.sdAccel = null;
      if (var1 != null) {
         var1.flush();
      }

   }

   private class AcceleratedImageCapabilities extends ImageCapabilities {
      AcceleratedImageCapabilities() {
         super(false);
      }

      public boolean isAccelerated() {
         return VolatileSurfaceManager.this.sdCurrent == VolatileSurfaceManager.this.sdAccel;
      }

      public boolean isTrueVolatile() {
         return this.isAccelerated();
      }
   }
}
