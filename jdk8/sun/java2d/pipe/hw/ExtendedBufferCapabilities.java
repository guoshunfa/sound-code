package sun.java2d.pipe.hw;

import java.awt.BufferCapabilities;
import java.awt.ImageCapabilities;

public class ExtendedBufferCapabilities extends BufferCapabilities {
   private ExtendedBufferCapabilities.VSyncType vsync;

   public ExtendedBufferCapabilities(BufferCapabilities var1) {
      super(var1.getFrontBufferCapabilities(), var1.getBackBufferCapabilities(), var1.getFlipContents());
      this.vsync = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
   }

   public ExtendedBufferCapabilities(ImageCapabilities var1, ImageCapabilities var2, BufferCapabilities.FlipContents var3) {
      super(var1, var2, var3);
      this.vsync = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
   }

   public ExtendedBufferCapabilities(ImageCapabilities var1, ImageCapabilities var2, BufferCapabilities.FlipContents var3, ExtendedBufferCapabilities.VSyncType var4) {
      super(var1, var2, var3);
      this.vsync = var4;
   }

   public ExtendedBufferCapabilities(BufferCapabilities var1, ExtendedBufferCapabilities.VSyncType var2) {
      super(var1.getFrontBufferCapabilities(), var1.getBackBufferCapabilities(), var1.getFlipContents());
      this.vsync = var2;
   }

   public ExtendedBufferCapabilities derive(ExtendedBufferCapabilities.VSyncType var1) {
      return new ExtendedBufferCapabilities(this, var1);
   }

   public ExtendedBufferCapabilities.VSyncType getVSync() {
      return this.vsync;
   }

   public final boolean isPageFlipping() {
      return true;
   }

   public static enum VSyncType {
      VSYNC_DEFAULT(0),
      VSYNC_ON(1),
      VSYNC_OFF(2);

      private int id;

      public int id() {
         return this.id;
      }

      private VSyncType(int var3) {
         this.id = var3;
      }
   }
}
