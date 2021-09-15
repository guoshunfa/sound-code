package sun.awt.image;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.StateTrackable;
import sun.java2d.StateTrackableDelegate;
import sun.java2d.SurfaceData;

public class SunWritableRaster extends WritableRaster {
   private static SunWritableRaster.DataStealer stealer;
   private StateTrackableDelegate theTrackable;

   public static void setDataStealer(SunWritableRaster.DataStealer var0) {
      if (stealer != null) {
         throw new InternalError("Attempt to set DataStealer twice");
      } else {
         stealer = var0;
      }
   }

   public static byte[] stealData(DataBufferByte var0, int var1) {
      return stealer.getData(var0, var1);
   }

   public static short[] stealData(DataBufferUShort var0, int var1) {
      return stealer.getData(var0, var1);
   }

   public static int[] stealData(DataBufferInt var0, int var1) {
      return stealer.getData(var0, var1);
   }

   public static StateTrackableDelegate stealTrackable(DataBuffer var0) {
      return stealer.getTrackable(var0);
   }

   public static void setTrackable(DataBuffer var0, StateTrackableDelegate var1) {
      stealer.setTrackable(var0, var1);
   }

   public static void makeTrackable(DataBuffer var0) {
      stealer.setTrackable(var0, StateTrackableDelegate.createInstance(StateTrackable.State.STABLE));
   }

   public static void markDirty(DataBuffer var0) {
      stealer.getTrackable(var0).markDirty();
   }

   public static void markDirty(WritableRaster var0) {
      if (var0 instanceof SunWritableRaster) {
         ((SunWritableRaster)var0).markDirty();
      } else {
         markDirty(var0.getDataBuffer());
      }

   }

   public static void markDirty(Image var0) {
      SurfaceData.getPrimarySurfaceData(var0).markDirty();
   }

   public SunWritableRaster(SampleModel var1, Point var2) {
      super(var1, var2);
      this.theTrackable = stealTrackable(this.dataBuffer);
   }

   public SunWritableRaster(SampleModel var1, DataBuffer var2, Point var3) {
      super(var1, var2, var3);
      this.theTrackable = stealTrackable(var2);
   }

   public SunWritableRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, WritableRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.theTrackable = stealTrackable(var2);
   }

   public final void markDirty() {
      this.theTrackable.markDirty();
   }

   public interface DataStealer {
      byte[] getData(DataBufferByte var1, int var2);

      short[] getData(DataBufferUShort var1, int var2);

      int[] getData(DataBufferInt var1, int var2);

      StateTrackableDelegate getTrackable(DataBuffer var1);

      void setTrackable(DataBuffer var1, StateTrackableDelegate var2);
   }
}
