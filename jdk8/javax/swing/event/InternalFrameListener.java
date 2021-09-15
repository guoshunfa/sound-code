package javax.swing.event;

import java.util.EventListener;

public interface InternalFrameListener extends EventListener {
   void internalFrameOpened(InternalFrameEvent var1);

   void internalFrameClosing(InternalFrameEvent var1);

   void internalFrameClosed(InternalFrameEvent var1);

   void internalFrameIconified(InternalFrameEvent var1);

   void internalFrameDeiconified(InternalFrameEvent var1);

   void internalFrameActivated(InternalFrameEvent var1);

   void internalFrameDeactivated(InternalFrameEvent var1);
}
