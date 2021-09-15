package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowEvent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JFrame extends Frame implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
   public static final int EXIT_ON_CLOSE = 3;
   private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JFrame.defaultLookAndFeelDecorated");
   private int defaultCloseOperation = 1;
   private TransferHandler transferHandler;
   protected JRootPane rootPane;
   protected boolean rootPaneCheckingEnabled = false;
   protected AccessibleContext accessibleContext = null;

   public JFrame() throws HeadlessException {
      this.frameInit();
   }

   public JFrame(GraphicsConfiguration var1) {
      super(var1);
      this.frameInit();
   }

   public JFrame(String var1) throws HeadlessException {
      super(var1);
      this.frameInit();
   }

   public JFrame(String var1, GraphicsConfiguration var2) {
      super(var1, var2);
      this.frameInit();
   }

   protected void frameInit() {
      this.enableEvents(72L);
      this.setLocale(JComponent.getDefaultLocale());
      this.setRootPane(this.createRootPane());
      this.setBackground(UIManager.getColor("control"));
      this.setRootPaneCheckingEnabled(true);
      if (isDefaultLookAndFeelDecorated()) {
         boolean var1 = UIManager.getLookAndFeel().getSupportsWindowDecorations();
         if (var1) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(1);
         }
      }

      SunToolkit.checkAndSetPolicy(this);
   }

   protected JRootPane createRootPane() {
      JRootPane var1 = new JRootPane();
      var1.setOpaque(true);
      return var1;
   }

   protected void processWindowEvent(WindowEvent var1) {
      super.processWindowEvent(var1);
      if (var1.getID() == 201) {
         switch(this.defaultCloseOperation) {
         case 0:
         default:
            break;
         case 1:
            this.setVisible(false);
            break;
         case 2:
            this.dispose();
            break;
         case 3:
            System.exit(0);
         }
      }

   }

   public void setDefaultCloseOperation(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
      } else {
         if (var1 == 3) {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkExit(0);
            }
         }

         if (this.defaultCloseOperation != var1) {
            int var3 = this.defaultCloseOperation;
            this.defaultCloseOperation = var1;
            this.firePropertyChange("defaultCloseOperation", var3, var1);
         }

      }
   }

   public int getDefaultCloseOperation() {
      return this.defaultCloseOperation;
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

   public void setIconImage(Image var1) {
      super.setIconImage(var1);
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

   public static void setDefaultLookAndFeelDecorated(boolean var0) {
      if (var0) {
         SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.TRUE);
      } else {
         SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.FALSE);
      }

   }

   public static boolean isDefaultLookAndFeelDecorated() {
      Boolean var0 = (Boolean)SwingUtilities.appContextGet(defaultLookAndFeelDecoratedKey);
      if (var0 == null) {
         var0 = Boolean.FALSE;
      }

      return var0;
   }

   protected String paramString() {
      String var1;
      if (this.defaultCloseOperation == 1) {
         var1 = "HIDE_ON_CLOSE";
      } else if (this.defaultCloseOperation == 2) {
         var1 = "DISPOSE_ON_CLOSE";
      } else if (this.defaultCloseOperation == 0) {
         var1 = "DO_NOTHING_ON_CLOSE";
      } else if (this.defaultCloseOperation == 3) {
         var1 = "EXIT_ON_CLOSE";
      } else {
         var1 = "";
      }

      String var2 = this.rootPane != null ? this.rootPane.toString() : "";
      String var3 = this.rootPaneCheckingEnabled ? "true" : "false";
      return super.paramString() + ",defaultCloseOperation=" + var1 + ",rootPane=" + var2 + ",rootPaneCheckingEnabled=" + var3;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JFrame.AccessibleJFrame();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJFrame extends Frame.AccessibleAWTFrame {
      protected AccessibleJFrame() {
         super();
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return JFrame.this.getTitle() == null ? super.getAccessibleName() : JFrame.this.getTitle();
         }
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JFrame.this.isResizable()) {
            var1.add(AccessibleState.RESIZABLE);
         }

         if (JFrame.this.getFocusOwner() != null) {
            var1.add(AccessibleState.ACTIVE);
         }

         return var1;
      }
   }
}
