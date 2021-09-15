package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import sun.awt.SunToolkit;

public class JWindow extends Window implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
   protected JRootPane rootPane;
   protected boolean rootPaneCheckingEnabled;
   private TransferHandler transferHandler;
   protected AccessibleContext accessibleContext;

   public JWindow() {
      this((Frame)null);
   }

   public JWindow(GraphicsConfiguration var1) {
      this((Window)null, var1);
      super.setFocusableWindowState(false);
   }

   public JWindow(Frame var1) {
      super(var1 == null ? SwingUtilities.getSharedOwnerFrame() : var1);
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      if (var1 == null) {
         WindowListener var2 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         this.addWindowListener(var2);
      }

      this.windowInit();
   }

   public JWindow(Window var1) {
      super((Window)(var1 == null ? SwingUtilities.getSharedOwnerFrame() : var1));
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      if (var1 == null) {
         WindowListener var2 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         this.addWindowListener(var2);
      }

      this.windowInit();
   }

   public JWindow(Window var1, GraphicsConfiguration var2) {
      super((Window)(var1 == null ? SwingUtilities.getSharedOwnerFrame() : var1), var2);
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      if (var1 == null) {
         WindowListener var3 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         this.addWindowListener(var3);
      }

      this.windowInit();
   }

   protected void windowInit() {
      this.setLocale(JComponent.getDefaultLocale());
      this.setRootPane(this.createRootPane());
      this.setRootPaneCheckingEnabled(true);
      SunToolkit.checkAndSetPolicy(this);
   }

   protected JRootPane createRootPane() {
      JRootPane var1 = new JRootPane();
      var1.setOpaque(true);
      return var1;
   }

   protected boolean isRootPaneCheckingEnabled() {
      return this.rootPaneCheckingEnabled;
   }

   public void setTransferHandler(TransferHandler var1) {
      TransferHandler var2 = this.transferHandler;
      this.transferHandler = var1;
      SwingUtilities.installSwingDropTargetAsNecessary(this, this.transferHandler);
      this.firePropertyChange("transferHandler", var2, var1);
   }

   public TransferHandler getTransferHandler() {
      return this.transferHandler;
   }

   public void update(Graphics var1) {
      this.paint(var1);
   }

   protected void setRootPaneCheckingEnabled(boolean var1) {
      this.rootPaneCheckingEnabled = var1;
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (this.isRootPaneCheckingEnabled()) {
         this.getContentPane().add(var1, var2, var3);
      } else {
         super.addImpl(var1, var2, var3);
      }

   }

   public void remove(Component var1) {
      if (var1 == this.rootPane) {
         super.remove(var1);
      } else {
         this.getContentPane().remove(var1);
      }

   }

   public void setLayout(LayoutManager var1) {
      if (this.isRootPaneCheckingEnabled()) {
         this.getContentPane().setLayout(var1);
      } else {
         super.setLayout(var1);
      }

   }

   public JRootPane getRootPane() {
      return this.rootPane;
   }

   protected void setRootPane(JRootPane var1) {
      if (this.rootPane != null) {
         this.remove(this.rootPane);
      }

      this.rootPane = var1;
      if (this.rootPane != null) {
         boolean var2 = this.isRootPaneCheckingEnabled();

         try {
            this.setRootPaneCheckingEnabled(false);
            this.add(this.rootPane, "Center");
         } finally {
            this.setRootPaneCheckingEnabled(var2);
         }
      }

   }

   public Container getContentPane() {
      return this.getRootPane().getContentPane();
   }

   public void setContentPane(Container var1) {
      this.getRootPane().setContentPane(var1);
   }

   public JLayeredPane getLayeredPane() {
      return this.getRootPane().getLayeredPane();
   }

   public void setLayeredPane(JLayeredPane var1) {
      this.getRootPane().setLayeredPane(var1);
   }

   public Component getGlassPane() {
      return this.getRootPane().getGlassPane();
   }

   public void setGlassPane(Component var1) {
      this.getRootPane().setGlassPane(var1);
   }

   public Graphics getGraphics() {
      JComponent.getGraphicsInvoked(this);
      return super.getGraphics();
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
      if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
         RepaintManager.currentManager((Component)this).addDirtyRegion((Window)this, var3, var4, var5, var6);
      } else {
         super.repaint(var1, var3, var4, var5, var6);
      }

   }

   protected String paramString() {
      String var1 = this.rootPaneCheckingEnabled ? "true" : "false";
      return super.paramString() + ",rootPaneCheckingEnabled=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JWindow.AccessibleJWindow();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJWindow extends Window.AccessibleAWTWindow {
      protected AccessibleJWindow() {
         super();
      }
   }
}
