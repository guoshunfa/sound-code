package sun.lwawt;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.security.AccessController;
import java.util.Properties;
import sun.awt.AWTAccessor;
import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.LightweightFrame;
import sun.awt.SunToolkit;
import sun.misc.ThreadGroupUtils;
import sun.print.PrintJob2D;
import sun.security.util.SecurityConstants;

public abstract class LWToolkit extends SunToolkit implements Runnable {
   private static final int STATE_NONE = 0;
   private static final int STATE_INIT = 1;
   private static final int STATE_MESSAGELOOP = 2;
   private static final int STATE_SHUTDOWN = 3;
   private static final int STATE_CLEANUP = 4;
   private static final int STATE_DONE = 5;
   private int runState = 0;
   private Clipboard clipboard;
   private MouseInfoPeer mouseInfoPeer;
   private volatile boolean dynamicLayoutSetting = true;

   protected LWToolkit() {
   }

   protected final void init() {
      AWTAutoShutdown.notifyToolkitThreadBusy();
      ThreadGroup var1 = (ThreadGroup)AccessController.doPrivileged(ThreadGroupUtils::getRootThreadGroup);
      Runtime.getRuntime().addShutdownHook(new Thread(var1, () -> {
         this.shutdown();
         this.waitForRunState(4);
      }));
      Thread var2 = new Thread(var1, this, "AWT-LW");
      var2.setDaemon(true);
      var2.setPriority(6);
      var2.start();
      this.waitForRunState(2);
   }

   protected abstract void platformInit();

   public final void shutdown() {
      this.setRunState(3);
      this.platformShutdown();
   }

   protected abstract void platformShutdown();

   protected abstract void platformCleanup();

   private synchronized int getRunState() {
      return this.runState;
   }

   private synchronized void setRunState(int var1) {
      this.runState = var1;
      this.notifyAll();
   }

   public final boolean isTerminating() {
      return this.getRunState() >= 3;
   }

   private void waitForRunState(int var1) {
      while(true) {
         if (this.getRunState() < var1) {
            try {
               synchronized(this) {
                  this.wait();
                  continue;
               }
            } catch (InterruptedException var5) {
            }
         }

         return;
      }
   }

   public final void run() {
      this.setRunState(1);
      this.platformInit();
      AWTAutoShutdown.notifyToolkitThreadFree();
      this.setRunState(2);

      while(this.getRunState() < 3) {
         try {
            this.platformRunMessage();
            if (Thread.currentThread().isInterrupted() && AppContext.getAppContext().isDisposed()) {
               break;
            }
         } catch (ThreadDeath var2) {
            break;
         } catch (Throwable var3) {
            System.err.println("Exception on the toolkit thread");
            var3.printStackTrace(System.err);
         }
      }

      this.setRunState(4);
      AWTAutoShutdown.notifyToolkitThreadFree();
      this.platformCleanup();
      this.setRunState(5);
   }

   protected abstract void platformRunMessage();

   public static LWToolkit getLWToolkit() {
      return (LWToolkit)Toolkit.getDefaultToolkit();
   }

   protected LWWindowPeer createDelegatedPeer(Window var1, PlatformComponent var2, PlatformWindow var3, LWWindowPeer.PeerType var4) {
      LWWindowPeer var5 = new LWWindowPeer(var1, var2, var3, var4);
      targetCreatedPeer(var1, var5);
      var5.initialize();
      return var5;
   }

   public final FramePeer createLightweightFrame(LightweightFrame var1) {
      PlatformComponent var2 = this.createLwPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.LW_FRAME);
      LWLightweightFramePeer var4 = new LWLightweightFramePeer(var1, var2, var3);
      targetCreatedPeer(var1, var4);
      var4.initialize();
      return var4;
   }

   public final WindowPeer createWindow(Window var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.SIMPLEWINDOW);
      return this.createDelegatedPeer(var1, var2, var3, LWWindowPeer.PeerType.SIMPLEWINDOW);
   }

   public final FramePeer createFrame(Frame var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.FRAME);
      return this.createDelegatedPeer(var1, var2, var3, LWWindowPeer.PeerType.FRAME);
   }

   public DialogPeer createDialog(Dialog var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.DIALOG);
      return this.createDelegatedPeer(var1, var2, var3, LWWindowPeer.PeerType.DIALOG);
   }

   public final FileDialogPeer createFileDialog(FileDialog var1) {
      FileDialogPeer var2 = this.createFileDialogPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public final ButtonPeer createButton(Button var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWButtonPeer var3 = new LWButtonPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final CheckboxPeer createCheckbox(Checkbox var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWCheckboxPeer var3 = new LWCheckboxPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final ChoicePeer createChoice(Choice var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWChoicePeer var3 = new LWChoicePeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final LabelPeer createLabel(Label var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWLabelPeer var3 = new LWLabelPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final CanvasPeer createCanvas(Canvas var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWCanvasPeer var3 = new LWCanvasPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final ListPeer createList(List var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWListPeer var3 = new LWListPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final PanelPeer createPanel(Panel var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWPanelPeer var3 = new LWPanelPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final ScrollPanePeer createScrollPane(ScrollPane var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWScrollPanePeer var3 = new LWScrollPanePeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final ScrollbarPeer createScrollbar(Scrollbar var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWScrollBarPeer var3 = new LWScrollBarPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final TextAreaPeer createTextArea(TextArea var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWTextAreaPeer var3 = new LWTextAreaPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final TextFieldPeer createTextField(TextField var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      LWTextFieldPeer var3 = new LWTextFieldPeer(var1, var2);
      targetCreatedPeer(var1, var3);
      var3.initialize();
      return var3;
   }

   public final ColorModel getColorModel() throws HeadlessException {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel();
   }

   public final boolean isDesktopSupported() {
      return true;
   }

   public final KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
      return LWKeyboardFocusManagerPeer.getInstance();
   }

   public final synchronized MouseInfoPeer getMouseInfoPeer() {
      if (this.mouseInfoPeer == null) {
         this.mouseInfoPeer = this.createMouseInfoPeerImpl();
      }

      return this.mouseInfoPeer;
   }

   protected final MouseInfoPeer createMouseInfoPeerImpl() {
      return new LWMouseInfoPeer();
   }

   protected abstract PlatformWindow getPlatformWindowUnderMouse();

   public final PrintJob getPrintJob(Frame var1, String var2, Properties var3) {
      return this.getPrintJob(var1, var2, (JobAttributes)null, (PageAttributes)null);
   }

   public final PrintJob getPrintJob(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      if (GraphicsEnvironment.isHeadless()) {
         throw new IllegalArgumentException();
      } else {
         PrintJob2D var5 = new PrintJob2D(var1, var2, var3, var4);
         if (!var5.printDialog()) {
            var5 = null;
         }

         return var5;
      }
   }

   public final Clipboard getSystemClipboard() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
      }

      synchronized(this) {
         if (this.clipboard == null) {
            this.clipboard = this.createPlatformClipboard();
         }
      }

      return this.clipboard;
   }

   protected abstract SecurityWarningWindow createSecurityWarning(Window var1, LWWindowPeer var2);

   public abstract Clipboard createPlatformClipboard();

   protected abstract PlatformWindow createPlatformWindow(LWWindowPeer.PeerType var1);

   protected abstract PlatformComponent createPlatformComponent();

   protected abstract PlatformComponent createLwPlatformComponent();

   protected abstract FileDialogPeer createFileDialogPeer(FileDialog var1);

   public static final Object targetToPeer(Object var0) {
      return SunToolkit.targetToPeer(var0);
   }

   public static final void targetDisposedPeer(Object var0, Object var1) {
      SunToolkit.targetDisposedPeer(var0, var1);
   }

   public abstract LWCursorManager getCursorManager();

   public static void postEvent(AWTEvent var0) {
      postEvent(targetToAppContext(var0.getSource()), var0);
   }

   public final void grab(Window var1) {
      ComponentPeer var2 = AWTAccessor.getComponentAccessor().getPeer(var1);
      if (var2 != null) {
         ((LWWindowPeer)var2).grab();
      }

   }

   public final void ungrab(Window var1) {
      ComponentPeer var2 = AWTAccessor.getComponentAccessor().getPeer(var1);
      if (var2 != null) {
         ((LWWindowPeer)var2).ungrab(false);
      }

   }

   protected final Object lazilyLoadDesktopProperty(String var1) {
      return var1.equals("awt.dynamicLayoutSupported") ? this.isDynamicLayoutSupported() : super.lazilyLoadDesktopProperty(var1);
   }

   public final void setDynamicLayout(boolean var1) {
      this.dynamicLayoutSetting = var1;
   }

   protected final boolean isDynamicLayoutSet() {
      return this.dynamicLayoutSetting;
   }

   public final boolean isDynamicLayoutActive() {
      return this.isDynamicLayoutSupported();
   }

   protected final boolean isDynamicLayoutSupported() {
      return true;
   }
}
