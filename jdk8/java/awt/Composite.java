package java.awt;

import java.awt.image.ColorModel;

public interface Composite {
   CompositeContext createContext(ColorModel var1, ColorModel var2, RenderingHints var3);
}
