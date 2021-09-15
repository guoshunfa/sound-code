package java.awt.peer;

import java.awt.Dialog;

public interface WindowPeer extends ContainerPeer {
   void toFront();

   void toBack();

   void updateAlwaysOnTopState();

   void updateFocusableWindowState();

   void setModalBlocked(Dialog var1, boolean var2);

   void updateMinimumSize();

   void updateIconImages();

   void setOpacity(float var1);

   void setOpaque(boolean var1);

   void updateWindow();

   void repositionSecurityWarning();
}
