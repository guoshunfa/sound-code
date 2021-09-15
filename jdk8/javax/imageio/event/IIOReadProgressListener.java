package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public interface IIOReadProgressListener extends EventListener {
   void sequenceStarted(ImageReader var1, int var2);

   void sequenceComplete(ImageReader var1);

   void imageStarted(ImageReader var1, int var2);

   void imageProgress(ImageReader var1, float var2);

   void imageComplete(ImageReader var1);

   void thumbnailStarted(ImageReader var1, int var2, int var3);

   void thumbnailProgress(ImageReader var1, float var2);

   void thumbnailComplete(ImageReader var1);

   void readAborted(ImageReader var1);
}
