package javax.swing;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import sun.awt.SunToolkit;

public class JApplet extends Applet implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
   protected JRootPane rootPane;
   protected boolean rootPaneCheckingEnabled = false;
   private TransferHandler transferHandler;
   protected AccessibleContext accessibleContext = null;

   public JApplet() throws HeadlessException {
      TimerQueue var1 = TimerQueue.sharedInstance();
      if (var1 != null) {
         var1.startIfNeeded();
      }

      this.setForeground(Color.black);
      this.setBackground(Color.white);
      this.setLocale(JComponent.getDefaultLocale());
      this.setLayout(new BorderLayout());
      this.setRootPane(this.createRootPane());
      this.setRootPaneCheckingEnabled(true);
      this.setFocusTraversalPolicyProvider(true);
      SunToolkit.checkAndSetPolicy(this);
      this.enableEvents(8L);
   }

   protected JRootPane createRootPane() {
      JRootPane var1 = new JRootPane();
      var1.setOpaque(true);
      return var1;
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

   public void setJMenuBar(JMenuBar var1) {
      this.getRootPane().setMenuBar(var1);
   }

   public JMenuBar getJMenuBar() {
      return this.getRootPane().getMenuBar();
   }

   protected boolean isRootPaneCheckingEnabled() {
      return this.rootPaneCheckingEnabled;
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
         RepaintManager.currentManager((Component)this).addDirtyRegion((Applet)this, var3, var4, var5, var6);
      } else {
         super.repaint(var1, var3, var4, var5, var6);
      }

   }

   protected String paramString() {
      String var1 = this.rootPane != null ? this.rootPane.toString() : "";
      String var2 = this.rootPaneCheckingEnabled ? "true" : "false";
      return super.paramString() + ",rootPane=" + var1 + ",rootPaneCheckingEnabled=" + var2;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JApplet.AccessibleJApplet();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJApplet extends Applet.AccessibleApplet {
      protected AccessibleJApplet() {
         super();
      }
   }
}
