package sun.awt.image;

import java.awt.Image;
import java.util.List;

public interface MultiResolutionImage {
   Image getResolutionVariant(int var1, int var2);

   List<Image> getResolutionVariants();
}
