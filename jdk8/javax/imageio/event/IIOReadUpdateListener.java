package javax.imageio.event;

import java.awt.image.BufferedImage;
import java.util.EventListener;
import javax.imageio.ImageReader;

public interface IIOReadUpdateListener extends EventListener {
   void passStarted(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int[] var10);

   void imageUpdate(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9);

   void passComplete(ImageReader var1, BufferedImage var2);

   void thumbnailPassStarted(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int[] var10);

   void thumbnailUpdate(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9);

   void thumbnailPassComplete(ImageReader var1, BufferedImage var2);
}
