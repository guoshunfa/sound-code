package java.awt.image;

import java.awt.Point;

public interface WritableRenderedImage extends RenderedImage {
   void addTileObserver(TileObserver var1);

   void removeTileObserver(TileObserver var1);

   WritableRaster getWritableTile(int var1, int var2);

   void releaseWritableTile(int var1, int var2);

   boolean isTileWritable(int var1, int var2);

   Point[] getWritableTileIndices();

   boolean hasTileWriters();

   void setData(Raster var1);
}
