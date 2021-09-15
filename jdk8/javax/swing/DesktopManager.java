package javax.swing;

public interface DesktopManager {
   void openFrame(JInternalFrame var1);

   void closeFrame(JInternalFrame var1);

   void maximizeFrame(JInternalFrame var1);

   void minimizeFrame(JInternalFrame var1);

   void iconifyFrame(JInternalFrame var1);

   void deiconifyFrame(JInternalFrame var1);

   void activateFrame(JInternalFrame var1);

   void deactivateFrame(JInternalFrame var1);

   void beginDraggingFrame(JComponent var1);

   void dragFrame(JComponent var1, int var2, int var3);

   void endDraggingFrame(JComponent var1);

   void beginResizingFrame(JComponent var1, int var2);

   void resizeFrame(JComponent var1, int var2, int var3, int var4, int var5);

   void endResizingFrame(JComponent var1);

   void setBoundsForFrame(JComponent var1, int var2, int var3, int var4, int var5);
}
