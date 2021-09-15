package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JDialog extends Dialog implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
   private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JDialog.defaultLookAndFeelDecorated");
   private int defaultCloseOperation;
   protected JRootPane rootPane;
   protected boolean rootPaneCheckingEnabled;
   private TransferHandler transferHandler;
   protected AccessibleContext accessibleContext;

   public JDialog() {
      this((Frame)null, false);
   }

   public JDialog(Frame var1) {
      this(var1, false);
   }

   public JDialog(Frame var1, boolean var2) {
      this(var1, "", var2);
   }

   public JDialog(Frame var1, String var2) {
      this(var1, var2, false);
   }

   public JDialog(Frame var1, String var2, boolean var3) {
      super(var1 == null ? SwingUtilities.getSharedOwnerFrame() : var1, var2, var3);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      if (var1 == null) {
         WindowListener var4 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         this.addWindowListener(var4);
      }

      this.dialogInit();
   }

   public JDialog(Frame var1, String var2, boolean var3, GraphicsConfiguration var4) {
      super(var1 == null ? SwingUtilities.getSharedOwnerFrame() : var1, var2, var3, var4);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      if (var1 == null) {
         WindowListener var5 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         this.addWindowListener(var5);
      }

      this.dialogInit();
   }

   public JDialog(Dialog var1) {
      this(var1, false);
   }

   public JDialog(Dialog var1, boolean var2) {
      this(var1, "", var2);
   }

   public JDialog(Dialog var1, String var2) {
      this(var1, var2, false);
   }

   public JDialog(Dialog var1, String var2, boolean var3) {
      super(var1, var2, var3);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      this.dialogInit();
   }

   public JDialog(Dialog var1, String var2, boolean var3, GraphicsConfiguration var4) {
      super(var1, var2, var3, var4);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      this.dialogInit();
   }

   public JDialog(Window var1) {
      this(var1, Dialog.ModalityType.MODELESS);
   }

   public JDialog(Window var1, Dialog.ModalityType var2) {
      this(var1, "", var2);
   }

   public JDialog(Window var1, String var2) {
      this(var1, var2, Dialog.ModalityType.MODELESS);
   }

   public JDialog(Window var1, String var2, Dialog.ModalityType var3) {
      super(var1, var2, var3);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      this.dialogInit();
   }

   public JDialog(Window var1, String var2, Dialog.ModalityType var3, GraphicsConfiguration var4) {
      super(var1, var2, var3, var4);
      this.defaultCloseOperation = 1;
      this.rootPaneCheckingEnabled = false;
      this.accessibleContext = null;
      this.dialogInit();
   }

   protected void dialogInit() {
      this.enableEvents(72L);
      this.setLocale(JComponent.getDefaultLocale());
      this.setRootPane(this.createRootPane());
      this.setBackground(UIManager.getColor("control"));
      this.setRootPaneCheckingEnabled(true);
      if (isDefaultLookAndFeelDecorated()) {
         boolean var1 = UIManager.getLookAndFeel().getSupportsWindowDecorations();
         if (var1) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(2);
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
         }
      }

   }

   public void setDefaultCloseOperation(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, or DISPOSE_ON_CLOSE");
      } else {
         int var2 = this.defaultCloseOperation;
         this.defaultCloseOperation = var1;
         this.firePropertyChange("defaultCloseOperation", var2, var1);
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
      } else {
         var1 = "";
      }

      String var2 = this.rootPane != null ? this.rootPane.toString() : "";
      String var3 = this.rootPaneCheckingEnabled ? "true" : "false";
      return super.paramString() + ",defaultCloseOperation=" + var1 + ",rootPane=" + var2 + ",rootPaneCheckingEnabled=" + var3;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JDialog.AccessibleJDialog();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJDialog extends Dialog.AccessibleAWTDialog {
      protected AccessibleJDialog() {
         super();
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return JDialog.this.getTitle() == null ? super.getAccessibleName() : JDialog.this.getTitle();
         }
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JDialog.this.isResizable()) {
            var1.add(AccessibleState.RESIZABLE);
         }

         if (JDialog.this.getFocusOwner() != null) {
            var1.add(AccessibleState.ACTIVE);
         }

         if (JDialog.this.isModal()) {
            var1.add(AccessibleState.MODAL);
         }

         return var1;
      }
   }
}
