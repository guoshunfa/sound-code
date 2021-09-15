package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.peer.TrayIconPeer;
import java.security.AccessControlContext;
import java.security.AccessController;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;

public class TrayIcon {
   private Image image;
   private String tooltip;
   private PopupMenu popup;
   private boolean autosize;
   private int id;
   private String actionCommand;
   private transient TrayIconPeer peer;
   transient MouseListener mouseListener;
   transient MouseMotionListener mouseMotionListener;
   transient ActionListener actionListener;
   private final AccessControlContext acc;

   final AccessControlContext getAccessControlContext() {
      if (this.acc == null) {
         throw new SecurityException("TrayIcon is missing AccessControlContext");
      } else {
         return this.acc;
      }
   }

   private TrayIcon() throws UnsupportedOperationException, HeadlessException, SecurityException {
      this.acc = AccessController.getContext();
      SystemTray.checkSystemTrayAllowed();
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (!SystemTray.isSupported()) {
         throw new UnsupportedOperationException();
      } else {
         SunToolkit.insertTargetMapping(this, AppContext.getAppContext());
      }
   }

   public TrayIcon(Image var1) {
      this();
      if (var1 == null) {
         throw new IllegalArgumentException("creating TrayIcon with null Image");
      } else {
         this.setImage(var1);
      }
   }

   public TrayIcon(Image var1, String var2) {
      this(var1);
      this.setToolTip(var2);
   }

   public TrayIcon(Image var1, String var2, PopupMenu var3) {
      this(var1, var2);
      this.setPopupMenu(var3);
   }

   public void setImage(Image var1) {
      if (var1 == null) {
         throw new NullPointerException("setting null Image");
      } else {
         this.image = var1;
         TrayIconPeer var2 = this.peer;
         if (var2 != null) {
            var2.updateImage();
         }

      }
   }

   public Image getImage() {
      return this.image;
   }

   public void setPopupMenu(PopupMenu var1) {
      if (var1 != this.popup) {
         Class var2 = TrayIcon.class;
         synchronized(TrayIcon.class) {
            if (var1 != null) {
               if (var1.isTrayIconPopup) {
                  throw new IllegalArgumentException("the PopupMenu is already set for another TrayIcon");
               }

               var1.isTrayIconPopup = true;
            }

            if (this.popup != null) {
               this.popup.isTrayIconPopup = false;
            }

            this.popup = var1;
         }
      }
   }

   public PopupMenu getPopupMenu() {
      return this.popup;
   }

   public void setToolTip(String var1) {
      this.tooltip = var1;
      TrayIconPeer var2 = this.peer;
      if (var2 != null) {
         var2.setToolTip(var1);
      }

   }

   public String getToolTip() {
      return this.tooltip;
   }

   public void setImageAutoSize(boolean var1) {
      this.autosize = var1;
      TrayIconPeer var2 = this.peer;
      if (var2 != null) {
         var2.updateImage();
      }

   }

   public boolean isImageAutoSize() {
      return this.autosize;
   }

   public synchronized void addMouseListener(MouseListener var1) {
      if (var1 != null) {
         this.mouseListener = AWTEventMulticaster.add(this.mouseListener, var1);
      }
   }

   public synchronized void removeMouseListener(MouseListener var1) {
      if (var1 != null) {
         this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, var1);
      }
   }

   public synchronized MouseListener[] getMouseListeners() {
      return (MouseListener[])AWTEventMulticaster.getListeners(this.mouseListener, MouseListener.class);
   }

   public synchronized void addMouseMotionListener(MouseMotionListener var1) {
      if (var1 != null) {
         this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, var1);
      }
   }

   public synchronized void removeMouseMotionListener(MouseMotionListener var1) {
      if (var1 != null) {
         this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, var1);
      }
   }

   public synchronized MouseMotionListener[] getMouseMotionListeners() {
      return (MouseMotionListener[])AWTEventMulticaster.getListeners(this.mouseMotionListener, MouseMotionListener.class);
   }

   public String getActionCommand() {
      return this.actionCommand;
   }

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public synchronized void addActionListener(ActionListener var1) {
      if (var1 != null) {
         this.actionListener = AWTEventMulticaster.add(this.actionListener, var1);
      }
   }

   public synchronized void removeActionListener(ActionListener var1) {
      if (var1 != null) {
         this.actionListener = AWTEventMulticaster.remove(this.actionListener, var1);
      }
   }

   public synchronized ActionListener[] getActionListeners() {
      return (ActionListener[])AWTEventMulticaster.getListeners(this.actionListener, ActionListener.class);
   }

   public void displayMessage(String var1, String var2, TrayIcon.MessageType var3) {
      if (var1 == null && var2 == null) {
         throw new NullPointerException("displaying the message with both caption and text being null");
      } else {
         TrayIconPeer var4 = this.peer;
         if (var4 != null) {
            var4.displayMessage(var1, var2, var3.name());
         }

      }
   }

   public Dimension getSize() {
      return SystemTray.getSystemTray().getTrayIconSize();
   }

   void addNotify() throws AWTException {
      synchronized(this) {
         if (this.peer == null) {
            Toolkit var2 = Toolkit.getDefaultToolkit();
            if (var2 instanceof SunToolkit) {
               this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
            } else if (var2 instanceof HeadlessToolkit) {
               this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
            }
         }
      }

      this.peer.setToolTip(this.tooltip);
   }

   void removeNotify() {
      TrayIconPeer var1 = null;
      synchronized(this) {
         var1 = this.peer;
         this.peer = null;
      }

      if (var1 != null) {
         var1.dispose();
      }

   }

   void setID(int var1) {
      this.id = var1;
   }

   int getID() {
      return this.id;
   }

   void dispatchEvent(AWTEvent var1) {
      EventQueue.setCurrentEventAndMostRecentTime(var1);
      Toolkit.getDefaultToolkit().notifyAWTEventListeners(var1);
      this.processEvent(var1);
   }

   void processEvent(AWTEvent var1) {
      if (var1 instanceof MouseEvent) {
         switch(var1.getID()) {
         case 500:
         case 501:
         case 502:
            this.processMouseEvent((MouseEvent)var1);
            break;
         case 503:
            this.processMouseMotionEvent((MouseEvent)var1);
            break;
         default:
            return;
         }
      } else if (var1 instanceof ActionEvent) {
         this.processActionEvent((ActionEvent)var1);
      }

   }

   void processMouseEvent(MouseEvent var1) {
      MouseListener var2 = this.mouseListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 500:
            var2.mouseClicked(var1);
            break;
         case 501:
            var2.mousePressed(var1);
            break;
         case 502:
            var2.mouseReleased(var1);
            break;
         default:
            return;
         }
      }

   }

   void processMouseMotionEvent(MouseEvent var1) {
      MouseMotionListener var2 = this.mouseMotionListener;
      if (var2 != null && var1.getID() == 503) {
         var2.mouseMoved(var1);
      }

   }

   void processActionEvent(ActionEvent var1) {
      ActionListener var2 = this.actionListener;
      if (var2 != null) {
         var2.actionPerformed(var1);
      }

   }

   private static native void initIDs();

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setTrayIconAccessor(new AWTAccessor.TrayIconAccessor() {
         public void addNotify(TrayIcon var1) throws AWTException {
            var1.addNotify();
         }

         public void removeNotify(TrayIcon var1) {
            var1.removeNotify();
         }
      });
   }

   public static enum MessageType {
      ERROR,
      WARNING,
      INFO,
      NONE;
   }
}
