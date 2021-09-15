package java.awt.peer;

import java.awt.Adjustable;

public interface ScrollPanePeer extends ContainerPeer {
   int getHScrollbarHeight();

   int getVScrollbarWidth();

   void setScrollPosition(int var1, int var2);

   void childResized(int var1, int var2);

   void setUnitIncrement(Adjustable var1, int var2);

   void setValue(Adjustable var1, int var2);
}
