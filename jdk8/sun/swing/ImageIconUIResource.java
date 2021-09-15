package sun.swing;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.plaf.UIResource;

public class ImageIconUIResource extends ImageIcon implements UIResource {
   public ImageIconUIResource(byte[] var1) {
      super(var1);
   }

   public ImageIconUIResource(Image var1) {
      super(var1);
   }
}
