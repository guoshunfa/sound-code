package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public interface IIOWriteWarningListener extends EventListener {
   void warningOccurred(ImageWriter var1, int var2, String var3);
}
