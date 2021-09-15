package sun.lwawt.macosx;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.peer.TrayIconPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class CTrayIcon extends CFRetainedResource implements TrayIconPeer {
   private TrayIcon target;
   private PopupMenu popup;
   private JDialog messageDialog = null;
   private CTrayIcon.DialogEventHandler handler = null;
   private final Frame dummyFrame;
   private static int mouseClickButtons = 0;

   CTrayIcon(TrayIcon var1) {
      super(0L, true);
      this.target = var1;
      this.popup = var1.getPopupMenu();
      this.dummyFrame = new Frame();
      this.setPtr(this.createModel());
      this.checkAndCreatePopupPeer();
      this.updateImage();
   }

   private CPopupMenu checkAndCreatePopupPeer() {
      CPopupMenu var1 = null;
      if (this.popup != null) {
         try {
            var1 = (CPopupMenu)this.popup.getPeer();
            if (var1 == null) {
               this.popup.addNotify();
               var1 = (CPopupMenu)this.popup.getPeer();
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      return var1;
   }

   private long createModel() {
      return this.nativeCreate();
   }

   private native long nativeCreate();

   public long getPopupMenuModel() {
      if (this.popup == null) {
         PopupMenu var1 = this.target.getPopupMenu();
         if (var1 == null) {
            return 0L;
         }

         this.popup = var1;
      }

      return this.checkAndCreatePopupPeer().ptr;
   }

   public void displayMessage(final String var1, final String var2, final String var3) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.displayMessageOnEDT(var1, var2, var3);
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  CTrayIcon.this.displayMessageOnEDT(var1, var2, var3);
               }
            });
         } catch (Exception var5) {
            throw new AssertionError(var5);
         }
      }

   }

   public void dispose() {
      if (this.messageDialog != null) {
         this.disposeMessageDialog();
      }

      this.dummyFrame.dispose();
      if (this.popup != null) {
         this.popup.removeNotify();
      }

      LWCToolkit.targetDisposedPeer(this.target, this);
      this.target = null;
      super.dispose();
   }

   public void setToolTip(String var1) {
      this.execute((var2) -> {
         this.nativeSetToolTip(var2, var1);
      });
   }

   private native void nativeSetToolTip(long var1, String var3);

   public void showPopupMenu(int var1, int var2) {
   }

   public void updateImage() {
      Image var1 = this.target.getImage();
      if (var1 != null) {
         MediaTracker var2 = new MediaTracker(new Button(""));
         var2.addImage(var1, 0);

         try {
            var2.waitForAll();
         } catch (InterruptedException var5) {
         }

         if (var1.getWidth((ImageObserver)null) > 0 && var1.getHeight((ImageObserver)null) > 0) {
            CImage var3 = CImage.getCreator().createFromImage(var1);
            boolean var4 = this.target.isImageAutoSize();
            var3.execute((var2x) -> {
               this.execute((var4x) -> {
                  this.setNativeImage(var4x, var2x, var4);
               });
            });
         }
      }
   }

   private native void setNativeImage(long var1, long var3, boolean var5);

   private void postEvent(final AWTEvent var1) {
      SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
         public void run() {
            SunToolkit.postEvent(SunToolkit.targetToAppContext(CTrayIcon.this.target), var1);
         }
      });
   }

   private void handleMouseEvent(NSEvent var1) {
      int var2 = var1.getButtonNumber();
      SunToolkit var3 = (SunToolkit)Toolkit.getDefaultToolkit();
      if ((var2 <= 2 || var3.areExtraMouseButtonsEnabled()) && var2 <= var3.getNumberOfButtons() - 1) {
         int var4 = NSEvent.nsToJavaEventType(var1.getType());
         int var5 = 0;
         int var6 = 0;
         if (var4 != 503) {
            var5 = NSEvent.nsToJavaButton(var2);
            var6 = var1.getClickCount();
         }

         int var7 = NSEvent.nsToJavaMouseModifiers(var2, var1.getModifierFlags());
         boolean var8 = NSEvent.isPopupTrigger(var7);
         int var9 = var5 > 0 ? MouseEvent.getMaskForButton(var5) : 0;
         long var10 = System.currentTimeMillis();
         if (var4 == 501) {
            mouseClickButtons |= var9;
         } else if (var4 == 506) {
            mouseClickButtons = 0;
         }

         int var12 = var1.getAbsX();
         int var13 = var1.getAbsY();
         MouseEvent var14 = new MouseEvent(this.dummyFrame, var4, var10, var7, var12, var13, var12, var13, var6, var8, var5);
         var14.setSource(this.target);
         this.postEvent(var14);
         if (var4 == 501 && var8) {
            String var15 = this.target.getActionCommand();
            ActionEvent var16 = new ActionEvent(this.target, 1001, var15);
            this.postEvent(var16);
         }

         if (var4 == 502) {
            if ((mouseClickButtons & var9) != 0) {
               MouseEvent var17 = new MouseEvent(this.dummyFrame, 500, var10, var7, var12, var13, var12, var13, var6, var8, var5);
               var17.setSource(this.target);
               this.postEvent(var17);
            }

            mouseClickButtons &= ~var9;
         }

      }
   }

   private native Point2D nativeGetIconLocation(long var1);

   public void displayMessageOnEDT(String var1, String var2, String var3) {
      if (this.messageDialog != null) {
         this.disposeMessageDialog();
      }

      Object var4 = getIconForMessageType(var3);
      if (var4 != null) {
         var4 = new ImageIcon(scaleIcon((Icon)var4, 0.75D));
      }

      Dimension var5 = Toolkit.getDefaultToolkit().getScreenSize();
      int var6 = var5.width / 8;
      this.messageDialog = this.createMessageDialog(var1, var2, var6, (Icon)var4);
      this.showMessageDialog();
   }

   private JDialog createMessageDialog(String var1, String var2, int var3, Icon var4) {
      this.handler = new CTrayIcon.DialogEventHandler();
      JTextArea var6 = null;
      if (var1 != null) {
         var6 = createTextArea(var1, var3, false, true);
      }

      JTextArea var7 = null;
      if (var2 != null) {
         var7 = createTextArea(var2, var3, true, false);
      }

      Object[] var8 = null;
      if (var6 != null) {
         if (var7 != null) {
            var8 = new Object[]{var6, new JLabel(), var7};
         } else {
            var8 = new Object[]{var6};
         }
      } else if (var7 != null) {
         var8 = new Object[]{var7};
      }

      JOptionPane var9 = new JOptionPane(var8);
      var9.setIcon(var4);
      var9.addPropertyChangeListener(this.handler);

      try {
         JPanel var10 = (JPanel)var9.getComponent(1);
         JButton var11 = (JButton)var10.getComponent(0);
         var11.putClientProperty("JComponent.sizeVariant", "small");
      } catch (Throwable var12) {
      }

      JDialog var5 = new JDialog((Dialog)null);
      JRootPane var13 = var5.getRootPane();
      var13.putClientProperty("Window.style", "small");
      var13.putClientProperty("Window.zoomable", "false");
      var5.setDefaultCloseOperation(0);
      var5.setModal(false);
      var5.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
      var5.setAlwaysOnTop(true);
      var5.setAutoRequestFocus(false);
      var5.setResizable(false);
      var5.setContentPane(var9);
      var5.addWindowListener(this.handler);
      AWTAccessor.getWindowAccessor().setTrayIconWindow(var5, true);
      var5.pack();
      return var5;
   }

   private void showMessageDialog() {
      Dimension var1 = Toolkit.getDefaultToolkit().getScreenSize();
      AtomicReference var2 = new AtomicReference();
      this.execute((var2x) -> {
         var2.set(this.nativeGetIconLocation(var2x));
      });
      Point2D var3 = (Point2D)var2.get();
      if (var3 != null) {
         int var4 = (int)var3.getY();
         int var5 = (int)var3.getX();
         if (var5 + this.messageDialog.getWidth() > var1.width) {
            var5 = var1.width - this.messageDialog.getWidth();
         }

         this.messageDialog.setLocation(var5, var4);
         this.messageDialog.setVisible(true);
      }
   }

   private void disposeMessageDialog() {
      if (SwingUtilities.isEventDispatchThread()) {
         this.disposeMessageDialogOnEDT();
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  CTrayIcon.this.disposeMessageDialogOnEDT();
               }
            });
         } catch (Exception var2) {
            throw new AssertionError(var2);
         }
      }

   }

   private void disposeMessageDialogOnEDT() {
      if (this.messageDialog != null) {
         this.messageDialog.removeWindowListener(this.handler);
         this.messageDialog.removePropertyChangeListener(this.handler);
         this.messageDialog.dispose();
         this.messageDialog = null;
         this.handler = null;
      }

   }

   private static BufferedImage scaleIcon(Icon var0, double var1) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var0.getIconWidth();
         int var4 = var0.getIconHeight();
         GraphicsEnvironment var5 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var6 = var5.getDefaultScreenDevice();
         GraphicsConfiguration var7 = var6.getDefaultConfiguration();
         BufferedImage var8 = var7.createCompatibleImage(var3, var4, 3);
         Graphics2D var9 = var8.createGraphics();
         var0.paintIcon((Component)null, var9, 0, 0);
         var9.dispose();
         int var10 = (int)((double)var3 * var1);
         int var11 = (int)((double)var4 * var1);
         BufferedImage var12 = var7.createCompatibleImage(var10, var11, 3);
         var9 = var12.createGraphics();
         var9.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var9.drawImage(var8, 0, 0, var10, var11, (ImageObserver)null);
         var9.dispose();
         return var12;
      }
   }

   private static Icon getIconForMessageType(String var0) {
      if (var0.equals("ERROR")) {
         return UIManager.getIcon("OptionPane.errorIcon");
      } else {
         return var0.equals("WARNING") ? UIManager.getIcon("OptionPane.warningIcon") : UIManager.getIcon("OptionPane.informationIcon");
      }
   }

   private static JTextArea createTextArea(String var0, int var1, boolean var2, boolean var3) {
      JTextArea var4 = new JTextArea(var0);
      var4.setLineWrap(true);
      var4.setWrapStyleWord(true);
      var4.setEditable(false);
      var4.setFocusable(false);
      var4.setBorder((Border)null);
      var4.setBackground((new JLabel()).getBackground());
      if (var2) {
         var4.putClientProperty("JComponent.sizeVariant", "small");
      }

      if (var3) {
         Font var5 = var4.getFont();
         Font var6 = new Font(var5.getName(), 1, var5.getSize());
         var4.setFont(var6);
      }

      var4.setSize(var1, 1);
      return var4;
   }

   private final class DialogEventHandler extends WindowAdapter implements PropertyChangeListener {
      private DialogEventHandler() {
      }

      public void windowClosing(WindowEvent var1) {
         CTrayIcon.this.disposeMessageDialog();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (CTrayIcon.this.messageDialog != null) {
            String var2 = var1.getPropertyName();
            Container var3 = CTrayIcon.this.messageDialog.getContentPane();
            if (CTrayIcon.this.messageDialog.isVisible() && var1.getSource() == var3 && var2.equals("value")) {
               CTrayIcon.this.disposeMessageDialog();
            }

         }
      }

      // $FF: synthetic method
      DialogEventHandler(Object var2) {
         this();
      }
   }
}
