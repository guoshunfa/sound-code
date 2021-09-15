package sun.lwawt.macosx;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import sun.awt.AWTAccessor;
import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;
import sun.security.action.GetBooleanAction;

class CFileDialog implements FileDialogPeer {
   private final FileDialog target;

   CFileDialog(FileDialog var1) {
      this.target = var1;
   }

   public void dispose() {
      LWCToolkit.targetDisposedPeer(this.target, this);
   }

   public void setVisible(boolean var1) {
      if (var1) {
         (new Thread(new CFileDialog.Task())).start();
      }

   }

   private boolean queryFilenameFilter(String var1) {
      boolean var2 = false;
      FilenameFilter var3 = this.target.getFilenameFilter();
      File var4 = new File(var1);
      if (!var4.isDirectory()) {
         File var5 = new File(var4.getParent());
         String var6 = var4.getName();
         var2 = var3.accept(var5, var6);
      }

      return var2;
   }

   private native String[] nativeRunFileDialog(String var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6, String var7, String var8);

   public void setDirectory(String var1) {
   }

   public void setFile(String var1) {
   }

   public void setFilenameFilter(FilenameFilter var1) {
   }

   public void blockWindows(List<Window> var1) {
   }

   public void setResizable(boolean var1) {
   }

   public void setTitle(String var1) {
   }

   public void repositionSecurityWarning() {
   }

   public void updateAlwaysOnTopState() {
   }

   public void setModalBlocked(Dialog var1, boolean var2) {
   }

   public void setOpacity(float var1) {
   }

   public void setOpaque(boolean var1) {
   }

   public void toBack() {
   }

   public void toFront() {
   }

   public void updateFocusableWindowState() {
   }

   public void updateIconImages() {
   }

   public void updateMinimumSize() {
   }

   public void updateWindow() {
   }

   public void beginLayout() {
   }

   public void beginValidate() {
   }

   public void endLayout() {
   }

   public void endValidate() {
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public void applyShape(Region var1) {
   }

   public boolean canDetermineObscurity() {
      return false;
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      return 0;
   }

   public void coalescePaintEvent(PaintEvent var1) {
   }

   public void createBuffers(int var1, BufferCapabilities var2) throws AWTException {
   }

   public Image createImage(ImageProducer var1) {
      return null;
   }

   public Image createImage(int var1, int var2) {
      return null;
   }

   public VolatileImage createVolatileImage(int var1, int var2) {
      return null;
   }

   public void destroyBuffers() {
   }

   public void flip(int var1, int var2, int var3, int var4, BufferCapabilities.FlipContents var5) {
   }

   public Image getBackBuffer() {
      return null;
   }

   public ColorModel getColorModel() {
      return null;
   }

   public FontMetrics getFontMetrics(Font var1) {
      return null;
   }

   public Graphics getGraphics() {
      return null;
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
   }

   public Point getLocationOnScreen() {
      return null;
   }

   public Dimension getMinimumSize() {
      return this.target.getSize();
   }

   public Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public void handleEvent(AWTEvent var1) {
   }

   public boolean handlesWheelScrolling() {
      return false;
   }

   public boolean isFocusable() {
      return false;
   }

   public boolean isObscured() {
      return false;
   }

   public boolean isReparentSupported() {
      return false;
   }

   public void layout() {
   }

   public void paint(Graphics var1) {
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      return false;
   }

   public void print(Graphics var1) {
   }

   public void reparent(ContainerPeer var1) {
   }

   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return false;
   }

   public void setBackground(Color var1) {
   }

   public void setForeground(Color var1) {
   }

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
   }

   public void setEnabled(boolean var1) {
   }

   public void setFont(Font var1) {
   }

   public void setZOrder(ComponentPeer var1) {
   }

   public void updateCursorImmediately() {
   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      return false;
   }

   private class Task implements Runnable {
      private Task() {
      }

      public void run() {
         try {
            boolean var1 = !(Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("apple.awt.use-file-dialog-packages")));
            boolean var2 = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("apple.awt.fileDialogForDirectories")));
            int var3 = CFileDialog.this.target.getMode();
            String var4 = CFileDialog.this.target.getTitle();
            if (var4 == null) {
               var4 = " ";
            }

            String[] var5 = CFileDialog.this.nativeRunFileDialog(var4, var3, CFileDialog.this.target.isMultipleMode(), var1, var2, CFileDialog.this.target.getFilenameFilter() != null, CFileDialog.this.target.getDirectory(), CFileDialog.this.target.getFile());
            String var6 = null;
            String var7 = null;
            File[] var8 = null;
            if (var5 != null) {
               int var9 = var5.length;
               var8 = new File[var9];
               int var10 = 0;

               while(true) {
                  if (var10 >= var9) {
                     var6 = var8[0].getParent();
                     if (!var6.endsWith(File.separator)) {
                        var6 = var6 + File.separator;
                     }

                     var7 = var8[0].getName();
                     break;
                  }

                  var8[var10] = new File(var5[var10]);
                  ++var10;
               }
            }

            AWTAccessor.FileDialogAccessor var14 = AWTAccessor.getFileDialogAccessor();
            var14.setDirectory(CFileDialog.this.target, var6);
            var14.setFile(CFileDialog.this.target, var7);
            var14.setFiles(CFileDialog.this.target, var8);
         } finally {
            CFileDialog.this.target.dispose();
         }

      }

      // $FF: synthetic method
      Task(Object var2) {
         this();
      }
   }
}
