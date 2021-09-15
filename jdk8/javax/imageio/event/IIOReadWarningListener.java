package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public interface IIOReadWarningListener extends EventListener {
   void warningOccurred(ImageReader var1, String var2);
}
