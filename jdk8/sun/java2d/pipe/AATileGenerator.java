package sun.java2d.pipe;

public interface AATileGenerator {
   int getTileWidth();

   int getTileHeight();

   int getTypicalAlpha();

   void nextTile();

   void getAlpha(byte[] var1, int var2, int var3);

   void dispose();
}
