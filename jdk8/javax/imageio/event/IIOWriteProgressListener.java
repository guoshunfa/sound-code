package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public interface IIOWriteProgressListener extends EventListener {
   void imageStarted(ImageWriter var1, int var2);

   void imageProgress(ImageWriter var1, float var2);

   void imageComplete(ImageWriter var1);

   void thumbnailStarted(ImageWriter var1, int var2, int var3);

   void thumbnailProgress(ImageWriter var1, float var2);

   void thumbnailComplete(ImageWriter var1);

   void writeAborted(ImageWriter var1);
}
