package javax.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

interface GraphicsWrapper {
   Graphics subGraphics();

   boolean isClipIntersecting(Rectangle var1);

   int getClipX();

   int getClipY();

   int getClipWidth();

   int getClipHeight();
}
