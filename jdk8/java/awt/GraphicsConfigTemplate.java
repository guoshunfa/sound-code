package java.awt;

import java.io.Serializable;

public abstract class GraphicsConfigTemplate implements Serializable {
   private static final long serialVersionUID = -8061369279557787079L;
   public static final int REQUIRED = 1;
   public static final int PREFERRED = 2;
   public static final int UNNECESSARY = 3;

   public abstract GraphicsConfiguration getBestConfiguration(GraphicsConfiguration[] var1);

   public abstract boolean isGraphicsConfigSupported(GraphicsConfiguration var1);
}
