package sun.lwawt;

import java.awt.Rectangle;

public interface PlatformEventNotifier {
   void notifyIconify(boolean var1);

   void notifyZoom(boolean var1);

   void notifyExpose(Rectangle var1);

   void notifyReshape(int var1, int var2, int var3, int var4);

   void notifyUpdateCursor();

   void notifyActivation(boolean var1, LWWindowPeer var2);

   void notifyNCMouseDown();

   void notifyMouseEvent(int var1, long var2, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, byte[] var12);

   void notifyMouseWheelEvent(long var1, int var3, int var4, int var5, int var6, int var7, int var8, double var9, byte[] var11);

   void notifyKeyEvent(int var1, long var2, int var4, int var5, char var6, int var7);
}
