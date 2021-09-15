package java.awt.image;

import java.util.Hashtable;

public class ImageFilter implements ImageConsumer, Cloneable {
   protected ImageConsumer consumer;

   public ImageFilter getFilterInstance(ImageConsumer var1) {
      ImageFilter var2 = (ImageFilter)this.clone();
      var2.consumer = var1;
      return var2;
   }

   public void setDimensions(int var1, int var2) {
      this.consumer.setDimensions(var1, var2);
   }

   public void setProperties(Hashtable<?, ?> var1) {
      Hashtable var2 = (Hashtable)var1.clone();
      Object var3 = var2.get("filters");
      if (var3 == null) {
         var2.put("filters", this.toString());
      } else if (var3 instanceof String) {
         var2.put("filters", (String)var3 + this.toString());
      }

      this.consumer.setProperties(var2);
   }

   public void setColorModel(ColorModel var1) {
      this.consumer.setColorModel(var1);
   }

   public void setHints(int var1) {
      this.consumer.setHints(var1);
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      this.consumer.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      this.consumer.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void imageComplete(int var1) {
      this.consumer.imageComplete(var1);
   }

   public void resendTopDownLeftRight(ImageProducer var1) {
      var1.requestTopDownLeftRightResend(this);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
