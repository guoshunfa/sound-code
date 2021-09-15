package javax.imageio;

import javax.imageio.metadata.IIOMetadata;

public interface ImageTranscoder {
   IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2);

   IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3);
}
