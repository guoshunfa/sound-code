package javax.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.TooManyListenersException;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class TransferHandler implements Serializable {
   public static final int NONE = 0;
   public static final int COPY = 1;
   public static final int MOVE = 2;
   public static final int COPY_OR_MOVE = 3;
   public static final int LINK = 1073741824;
   private Image dragImage;
   private Point dragImageOffset;
   private String propertyName;
   private static TransferHandler.SwingDragGestureRecognizer recognizer = null;
   static final Action cutAction = new TransferHandler.TransferAction("cut");
   static final Action copyAction = new TransferHandler.TransferAction("copy");
   static final Action pasteAction = new TransferHandler.TransferAction("paste");

   public static Action getCutAction() {
      return cutAction;
   }

   public static Action getCopyAction() {
      return copyAction;
   }

   public static Action getPasteAction() {
      return pasteAction;
   }

   public TransferHandler(String var1) {
      this.propertyName = var1;
   }

   protected TransferHandler() {
      this((String)null);
   }

   public void setDragImage(Image var1) {
      this.dragImage = var1;
   }

   public Image getDragImage() {
      return this.dragImage;
   }

   public void setDragImageOffset(Point var1) {
      this.dragImageOffset = new Point(var1);
   }

   public Point getDragImageOffset() {
      return this.dragImageOffset == null ? new Point(0, 0) : new Point(this.dragImageOffset);
   }

   public void exportAsDrag(JComponent var1, InputEvent var2, int var3) {
      int var4 = this.getSourceActions(var1);
      if (!(var2 instanceof MouseEvent) || var3 != 1 && var3 != 2 && var3 != 1073741824 || (var4 & var3) == 0) {
         var3 = 0;
      }

      if (var3 != 0 && !GraphicsEnvironment.isHeadless()) {
         if (recognizer == null) {
            recognizer = new TransferHandler.SwingDragGestureRecognizer(new TransferHandler.DragHandler());
         }

         recognizer.gestured(var1, (MouseEvent)var2, var4, var3);
      } else {
         this.exportDone(var1, (Transferable)null, 0);
      }

   }

   public void exportToClipboard(JComponent var1, Clipboard var2, int var3) throws IllegalStateException {
      if ((var3 == 1 || var3 == 2) && (this.getSourceActions(var1) & var3) != 0) {
         Transferable var4 = this.createTransferable(var1);
         if (var4 != null) {
            try {
               var2.setContents(var4, (ClipboardOwner)null);
               this.exportDone(var1, var4, var3);
               return;
            } catch (IllegalStateException var6) {
               this.exportDone(var1, var4, 0);
               throw var6;
            }
         }
      }

      this.exportDone(var1, (Transferable)null, 0);
   }

   public boolean importData(TransferHandler.TransferSupport var1) {
      return var1.getComponent() instanceof JComponent ? this.importData((JComponent)var1.getComponent(), var1.getTransferable()) : false;
   }

   public boolean importData(JComponent var1, Transferable var2) {
      PropertyDescriptor var3 = this.getPropertyDescriptor(var1);
      if (var3 != null) {
         Method var4 = var3.getWriteMethod();
         if (var4 == null) {
            return false;
         }

         Class[] var5 = var4.getParameterTypes();
         if (var5.length != 1) {
            return false;
         }

         DataFlavor var6 = this.getPropertyDataFlavor(var5[0], var2.getTransferDataFlavors());
         if (var6 != null) {
            try {
               Object var7 = var2.getTransferData(var6);
               Object[] var8 = new Object[]{var7};
               MethodUtil.invoke(var4, var1, var8);
               return true;
            } catch (Exception var9) {
               System.err.println("Invocation failed");
            }
         }
      }

      return false;
   }

   public boolean canImport(TransferHandler.TransferSupport var1) {
      return var1.getComponent() instanceof JComponent ? this.canImport((JComponent)var1.getComponent(), var1.getDataFlavors()) : false;
   }

   public boolean canImport(JComponent var1, DataFlavor[] var2) {
      PropertyDescriptor var3 = this.getPropertyDescriptor(var1);
      if (var3 != null) {
         Method var4 = var3.getWriteMethod();
         if (var4 == null) {
            return false;
         }

         Class[] var5 = var4.getParameterTypes();
         if (var5.length != 1) {
            return false;
         }

         DataFlavor var6 = this.getPropertyDataFlavor(var5[0], var2);
         if (var6 != null) {
            return true;
         }
      }

      return false;
   }

   public int getSourceActions(JComponent var1) {
      PropertyDescriptor var2 = this.getPropertyDescriptor(var1);
      return var2 != null ? 1 : 0;
   }

   public Icon getVisualRepresentation(Transferable var1) {
      return null;
   }

   protected Transferable createTransferable(JComponent var1) {
      PropertyDescriptor var2 = this.getPropertyDescriptor(var1);
      return var2 != null ? new TransferHandler.PropertyTransferable(var2, var1) : null;
   }

   protected void exportDone(JComponent var1, Transferable var2, int var3) {
   }

   private PropertyDescriptor getPropertyDescriptor(JComponent var1) {
      if (this.propertyName == null) {
         return null;
      } else {
         Class var2 = var1.getClass();

         BeanInfo var3;
         try {
            var3 = Introspector.getBeanInfo(var2);
         } catch (IntrospectionException var8) {
            return null;
         }

         PropertyDescriptor[] var4 = var3.getPropertyDescriptors();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (this.propertyName.equals(var4[var5].getName())) {
               Method var6 = var4[var5].getReadMethod();
               if (var6 != null) {
                  Class[] var7 = var6.getParameterTypes();
                  if (var7 == null || var7.length == 0) {
                     return var4[var5];
                  }
               }
            }
         }

         return null;
      }
   }

   private DataFlavor getPropertyDataFlavor(Class<?> var1, DataFlavor[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         DataFlavor var4 = var2[var3];
         if ("application".equals(var4.getPrimaryType()) && "x-java-jvm-local-objectref".equals(var4.getSubType()) && var1.isAssignableFrom(var4.getRepresentationClass())) {
            return var4;
         }
      }

      return null;
   }

   private static DropTargetListener getDropTargetListener() {
      Class var0 = TransferHandler.DropHandler.class;
      synchronized(TransferHandler.DropHandler.class) {
         TransferHandler.DropHandler var1 = (TransferHandler.DropHandler)AppContext.getAppContext().get(TransferHandler.DropHandler.class);
         if (var1 == null) {
            var1 = new TransferHandler.DropHandler();
            AppContext.getAppContext().put(TransferHandler.DropHandler.class, var1);
         }

         return var1;
      }
   }

   static class TransferAction extends UIAction implements UIResource {
      private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
      private static Object SandboxClipboardKey = new Object();

      TransferAction(String var1) {
         super(var1);
      }

      public boolean isEnabled(Object var1) {
         return !(var1 instanceof JComponent) || ((JComponent)var1).getTransferHandler() != null;
      }

      public void actionPerformed(final ActionEvent var1) {
         Object var2 = var1.getSource();
         final PrivilegedAction var3 = new PrivilegedAction<Void>() {
            public Void run() {
               TransferAction.this.actionPerformedImpl(var1);
               return null;
            }
         };
         AccessControlContext var4 = AccessController.getContext();
         AccessControlContext var5 = AWTAccessor.getComponentAccessor().getAccessControlContext((Component)var2);
         final AccessControlContext var6 = AWTAccessor.getAWTEventAccessor().getAccessControlContext(var1);
         if (var5 == null) {
            javaSecurityAccess.doIntersectionPrivilege(var3, var4, var6);
         } else {
            javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
               public Void run() {
                  TransferHandler.TransferAction.javaSecurityAccess.doIntersectionPrivilege(var3, var6);
                  return null;
               }
            }, var4, var5);
         }

      }

      private void actionPerformedImpl(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof JComponent) {
            JComponent var3 = (JComponent)var2;
            TransferHandler var4 = var3.getTransferHandler();
            Clipboard var5 = this.getClipboard(var3);
            String var6 = (String)this.getValue("Name");
            Transferable var7 = null;

            try {
               if (var5 != null && var4 != null && var6 != null) {
                  if ("cut".equals(var6)) {
                     var4.exportToClipboard(var3, var5, 2);
                  } else if ("copy".equals(var6)) {
                     var4.exportToClipboard(var3, var5, 1);
                  } else if ("paste".equals(var6)) {
                     var7 = var5.getContents((Object)null);
                  }
               }
            } catch (IllegalStateException var9) {
               UIManager.getLookAndFeel().provideErrorFeedback(var3);
               return;
            }

            if (var7 != null) {
               var4.importData(new TransferHandler.TransferSupport(var3, var7));
            }
         }

      }

      private Clipboard getClipboard(JComponent var1) {
         if (SwingUtilities2.canAccessSystemClipboard()) {
            return var1.getToolkit().getSystemClipboard();
         } else {
            Clipboard var2 = (Clipboard)AppContext.getAppContext().get(SandboxClipboardKey);
            if (var2 == null) {
               var2 = new Clipboard("Sandboxed Component Clipboard");
               AppContext.getAppContext().put(SandboxClipboardKey, var2);
            }

            return var2;
         }
      }
   }

   private static class SwingDragGestureRecognizer extends DragGestureRecognizer {
      SwingDragGestureRecognizer(DragGestureListener var1) {
         super(DragSource.getDefaultDragSource(), (Component)null, 0, var1);
      }

      void gestured(JComponent var1, MouseEvent var2, int var3, int var4) {
         this.setComponent(var1);
         this.setSourceActions(var3);
         this.appendEvent(var2);
         this.fireDragGestureRecognized(var4, var2.getPoint());
      }

      protected void registerListeners() {
      }

      protected void unregisterListeners() {
      }
   }

   private static class DragHandler implements DragGestureListener, DragSourceListener {
      private boolean scrolls;

      private DragHandler() {
      }

      public void dragGestureRecognized(DragGestureEvent var1) {
         JComponent var2 = (JComponent)var1.getComponent();
         TransferHandler var3 = var2.getTransferHandler();
         Transferable var4 = var3.createTransferable(var2);
         if (var4 != null) {
            this.scrolls = var2.getAutoscrolls();
            var2.setAutoscrolls(false);

            try {
               Image var5 = var3.getDragImage();
               if (var5 == null) {
                  var1.startDrag((Cursor)null, var4, this);
               } else {
                  var1.startDrag((Cursor)null, var5, var3.getDragImageOffset(), var4, this);
               }

               return;
            } catch (RuntimeException var6) {
               var2.setAutoscrolls(this.scrolls);
            }
         }

         var3.exportDone(var2, var4, 0);
      }

      public void dragEnter(DragSourceDragEvent var1) {
      }

      public void dragOver(DragSourceDragEvent var1) {
      }

      public void dragExit(DragSourceEvent var1) {
      }

      public void dragDropEnd(DragSourceDropEvent var1) {
         DragSourceContext var2 = var1.getDragSourceContext();
         JComponent var3 = (JComponent)var2.getComponent();
         if (var1.getDropSuccess()) {
            var3.getTransferHandler().exportDone(var3, var2.getTransferable(), var1.getDropAction());
         } else {
            var3.getTransferHandler().exportDone(var3, var2.getTransferable(), 0);
         }

         var3.setAutoscrolls(this.scrolls);
      }

      public void dropActionChanged(DragSourceDragEvent var1) {
      }

      // $FF: synthetic method
      DragHandler(Object var1) {
         this();
      }
   }

   private static class DropHandler implements DropTargetListener, Serializable, ActionListener {
      private Timer timer;
      private Point lastPosition;
      private Rectangle outer;
      private Rectangle inner;
      private int hysteresis;
      private Component component;
      private Object state;
      private TransferHandler.TransferSupport support;
      private static final int AUTOSCROLL_INSET = 10;

      private DropHandler() {
         this.outer = new Rectangle();
         this.inner = new Rectangle();
         this.hysteresis = 10;
         this.support = new TransferHandler.TransferSupport((Component)null, (DropTargetEvent)null);
      }

      private void updateAutoscrollRegion(JComponent var1) {
         Rectangle var2 = var1.getVisibleRect();
         this.outer.setBounds(var2.x, var2.y, var2.width, var2.height);
         Insets var3 = new Insets(0, 0, 0, 0);
         if (var1 instanceof Scrollable) {
            byte var4 = 20;
            if (var2.width >= var4) {
               var3.left = var3.right = 10;
            }

            if (var2.height >= var4) {
               var3.top = var3.bottom = 10;
            }
         }

         this.inner.setBounds(var2.x + var3.left, var2.y + var3.top, var2.width - (var3.left + var3.right), var2.height - (var3.top + var3.bottom));
      }

      private void autoscroll(JComponent var1, Point var2) {
         if (var1 instanceof Scrollable) {
            Scrollable var3 = (Scrollable)var1;
            int var4;
            Rectangle var5;
            if (var2.y < this.inner.y) {
               var4 = var3.getScrollableUnitIncrement(this.outer, 1, -1);
               var5 = new Rectangle(this.inner.x, this.outer.y - var4, this.inner.width, var4);
               var1.scrollRectToVisible(var5);
            } else if (var2.y > this.inner.y + this.inner.height) {
               var4 = var3.getScrollableUnitIncrement(this.outer, 1, 1);
               var5 = new Rectangle(this.inner.x, this.outer.y + this.outer.height, this.inner.width, var4);
               var1.scrollRectToVisible(var5);
            }

            if (var2.x < this.inner.x) {
               var4 = var3.getScrollableUnitIncrement(this.outer, 0, -1);
               var5 = new Rectangle(this.outer.x - var4, this.inner.y, var4, this.inner.height);
               var1.scrollRectToVisible(var5);
            } else if (var2.x > this.inner.x + this.inner.width) {
               var4 = var3.getScrollableUnitIncrement(this.outer, 0, 1);
               var5 = new Rectangle(this.outer.x + this.outer.width, this.inner.y, var4, this.inner.height);
               var1.scrollRectToVisible(var5);
            }
         }

      }

      private void initPropertiesIfNecessary() {
         if (this.timer == null) {
            Toolkit var1 = Toolkit.getDefaultToolkit();
            Integer var2 = (Integer)var1.getDesktopProperty("DnD.Autoscroll.interval");
            this.timer = new Timer(var2 == null ? 100 : var2, this);
            var2 = (Integer)var1.getDesktopProperty("DnD.Autoscroll.initialDelay");
            this.timer.setInitialDelay(var2 == null ? 100 : var2);
            var2 = (Integer)var1.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
            if (var2 != null) {
               this.hysteresis = var2;
            }
         }

      }

      public void actionPerformed(ActionEvent var1) {
         this.updateAutoscrollRegion((JComponent)this.component);
         if (this.outer.contains(this.lastPosition) && !this.inner.contains(this.lastPosition)) {
            this.autoscroll((JComponent)this.component, this.lastPosition);
         }

      }

      private void setComponentDropLocation(TransferHandler.TransferSupport var1, boolean var2) {
         TransferHandler.DropLocation var3 = var1 == null ? null : var1.getDropLocation();
         if (SunToolkit.isInstanceOf((Object)this.component, "javax.swing.text.JTextComponent")) {
            this.state = SwingAccessor.getJTextComponentAccessor().setDropLocation((JTextComponent)this.component, var3, this.state, var2);
         } else if (this.component instanceof JComponent) {
            this.state = ((JComponent)this.component).setDropLocation(var3, this.state, var2);
         }

      }

      private void handleDrag(DropTargetDragEvent var1) {
         TransferHandler var2 = ((TransferHandler.HasGetTransferHandler)this.component).getTransferHandler();
         if (var2 == null) {
            var1.rejectDrag();
            this.setComponentDropLocation((TransferHandler.TransferSupport)null, false);
         } else {
            this.support.setDNDVariables(this.component, var1);
            boolean var3 = var2.canImport(this.support);
            if (var3) {
               var1.acceptDrag(this.support.getDropAction());
            } else {
               var1.rejectDrag();
            }

            boolean var4 = this.support.showDropLocationIsSet ? this.support.showDropLocation : var3;
            this.setComponentDropLocation(var4 ? this.support : null, false);
         }
      }

      public void dragEnter(DropTargetDragEvent var1) {
         this.state = null;
         this.component = var1.getDropTargetContext().getComponent();
         this.handleDrag(var1);
         if (this.component instanceof JComponent) {
            this.lastPosition = var1.getLocation();
            this.updateAutoscrollRegion((JComponent)this.component);
            this.initPropertiesIfNecessary();
         }

      }

      public void dragOver(DropTargetDragEvent var1) {
         this.handleDrag(var1);
         if (this.component instanceof JComponent) {
            Point var2 = var1.getLocation();
            if (Math.abs(var2.x - this.lastPosition.x) <= this.hysteresis && Math.abs(var2.y - this.lastPosition.y) <= this.hysteresis) {
               if (!this.timer.isRunning()) {
                  this.timer.start();
               }
            } else if (this.timer.isRunning()) {
               this.timer.stop();
            }

            this.lastPosition = var2;
         }
      }

      public void dragExit(DropTargetEvent var1) {
         this.cleanup(false);
      }

      public void drop(DropTargetDropEvent var1) {
         TransferHandler var2 = ((TransferHandler.HasGetTransferHandler)this.component).getTransferHandler();
         if (var2 == null) {
            var1.rejectDrop();
            this.cleanup(false);
         } else {
            this.support.setDNDVariables(this.component, var1);
            boolean var3 = var2.canImport(this.support);
            if (var3) {
               var1.acceptDrop(this.support.getDropAction());
               boolean var4 = this.support.showDropLocationIsSet ? this.support.showDropLocation : var3;
               this.setComponentDropLocation(var4 ? this.support : null, false);

               boolean var5;
               try {
                  var5 = var2.importData(this.support);
               } catch (RuntimeException var7) {
                  var5 = false;
               }

               var1.dropComplete(var5);
               this.cleanup(var5);
            } else {
               var1.rejectDrop();
               this.cleanup(false);
            }

         }
      }

      public void dropActionChanged(DropTargetDragEvent var1) {
         if (this.component != null) {
            this.handleDrag(var1);
         }
      }

      private void cleanup(boolean var1) {
         this.setComponentDropLocation((TransferHandler.TransferSupport)null, var1);
         if (this.component instanceof JComponent) {
            ((JComponent)this.component).dndDone();
         }

         if (this.timer != null) {
            this.timer.stop();
         }

         this.state = null;
         this.component = null;
         this.lastPosition = null;
      }

      // $FF: synthetic method
      DropHandler(Object var1) {
         this();
      }
   }

   static class SwingDropTarget extends DropTarget implements UIResource {
      private EventListenerList listenerList;

      SwingDropTarget(Component var1) {
         super(var1, 1073741827, (DropTargetListener)null);

         try {
            super.addDropTargetListener(TransferHandler.getDropTargetListener());
         } catch (TooManyListenersException var3) {
         }

      }

      public void addDropTargetListener(DropTargetListener var1) throws TooManyListenersException {
         if (this.listenerList == null) {
            this.listenerList = new EventListenerList();
         }

         this.listenerList.add(DropTargetListener.class, var1);
      }

      public void removeDropTargetListener(DropTargetListener var1) {
         if (this.listenerList != null) {
            this.listenerList.remove(DropTargetListener.class, var1);
         }

      }

      public void dragEnter(DropTargetDragEvent var1) {
         super.dragEnter(var1);
         if (this.listenerList != null) {
            Object[] var2 = this.listenerList.getListenerList();

            for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
               if (var2[var3] == DropTargetListener.class) {
                  ((DropTargetListener)var2[var3 + 1]).dragEnter(var1);
               }
            }
         }

      }

      public void dragOver(DropTargetDragEvent var1) {
         super.dragOver(var1);
         if (this.listenerList != null) {
            Object[] var2 = this.listenerList.getListenerList();

            for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
               if (var2[var3] == DropTargetListener.class) {
                  ((DropTargetListener)var2[var3 + 1]).dragOver(var1);
               }
            }
         }

      }

      public void dragExit(DropTargetEvent var1) {
         super.dragExit(var1);
         if (this.listenerList != null) {
            Object[] var2 = this.listenerList.getListenerList();

            for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
               if (var2[var3] == DropTargetListener.class) {
                  ((DropTargetListener)var2[var3 + 1]).dragExit(var1);
               }
            }
         }

         if (!this.isActive()) {
            DropTargetListener var4 = TransferHandler.getDropTargetListener();
            if (var4 != null && var4 instanceof TransferHandler.DropHandler) {
               ((TransferHandler.DropHandler)var4).cleanup(false);
            }
         }

      }

      public void drop(DropTargetDropEvent var1) {
         super.drop(var1);
         if (this.listenerList != null) {
            Object[] var2 = this.listenerList.getListenerList();

            for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
               if (var2[var3] == DropTargetListener.class) {
                  ((DropTargetListener)var2[var3 + 1]).drop(var1);
               }
            }
         }

      }

      public void dropActionChanged(DropTargetDragEvent var1) {
         super.dropActionChanged(var1);
         if (this.listenerList != null) {
            Object[] var2 = this.listenerList.getListenerList();

            for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
               if (var2[var3] == DropTargetListener.class) {
                  ((DropTargetListener)var2[var3 + 1]).dropActionChanged(var1);
               }
            }
         }

      }
   }

   static class PropertyTransferable implements Transferable {
      JComponent component;
      PropertyDescriptor property;

      PropertyTransferable(PropertyDescriptor var1, JComponent var2) {
         this.property = var1;
         this.component = var2;
      }

      public DataFlavor[] getTransferDataFlavors() {
         DataFlavor[] var1 = new DataFlavor[1];
         Class var2 = this.property.getPropertyType();
         String var3 = "application/x-java-jvm-local-objectref;class=" + var2.getName();

         try {
            var1[0] = new DataFlavor(var3);
         } catch (ClassNotFoundException var5) {
            var1 = new DataFlavor[0];
         }

         return var1;
      }

      public boolean isDataFlavorSupported(DataFlavor var1) {
         Class var2 = this.property.getPropertyType();
         return "application".equals(var1.getPrimaryType()) && "x-java-jvm-local-objectref".equals(var1.getSubType()) && var1.getRepresentationClass().isAssignableFrom(var2);
      }

      public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
         if (!this.isDataFlavorSupported(var1)) {
            throw new UnsupportedFlavorException(var1);
         } else {
            Method var2 = this.property.getReadMethod();
            Object var3 = null;

            try {
               var3 = MethodUtil.invoke(var2, this.component, (Object[])null);
               return var3;
            } catch (Exception var5) {
               throw new IOException("Property read failed: " + this.property.getName());
            }
         }
      }
   }

   public static final class TransferSupport {
      private boolean isDrop;
      private Component component;
      private boolean showDropLocationIsSet;
      private boolean showDropLocation;
      private int dropAction;
      private Object source;
      private TransferHandler.DropLocation dropLocation;

      private TransferSupport(Component var1, DropTargetEvent var2) {
         this.dropAction = -1;
         this.isDrop = true;
         this.setDNDVariables(var1, var2);
      }

      public TransferSupport(Component var1, Transferable var2) {
         this.dropAction = -1;
         if (var1 == null) {
            throw new NullPointerException("component is null");
         } else if (var2 == null) {
            throw new NullPointerException("transferable is null");
         } else {
            this.isDrop = false;
            this.component = var1;
            this.source = var2;
         }
      }

      private void setDNDVariables(Component var1, DropTargetEvent var2) {
         assert this.isDrop;

         this.component = var1;
         this.source = var2;
         this.dropLocation = null;
         this.dropAction = -1;
         this.showDropLocationIsSet = false;
         if (this.source != null) {
            assert this.source instanceof DropTargetDragEvent || this.source instanceof DropTargetDropEvent;

            Point var3 = this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation();
            if (SunToolkit.isInstanceOf((Object)var1, "javax.swing.text.JTextComponent")) {
               this.dropLocation = SwingAccessor.getJTextComponentAccessor().dropLocationForPoint((JTextComponent)var1, var3);
            } else if (var1 instanceof JComponent) {
               this.dropLocation = ((JComponent)var1).dropLocationForPoint(var3);
            }

         }
      }

      public boolean isDrop() {
         return this.isDrop;
      }

      public Component getComponent() {
         return this.component;
      }

      private void assureIsDrop() {
         if (!this.isDrop) {
            throw new IllegalStateException("Not a drop");
         }
      }

      public TransferHandler.DropLocation getDropLocation() {
         this.assureIsDrop();
         if (this.dropLocation == null) {
            Point var1 = this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation();
            this.dropLocation = new TransferHandler.DropLocation(var1);
         }

         return this.dropLocation;
      }

      public void setShowDropLocation(boolean var1) {
         this.assureIsDrop();
         this.showDropLocation = var1;
         this.showDropLocationIsSet = true;
      }

      public void setDropAction(int var1) {
         this.assureIsDrop();
         int var2 = var1 & this.getSourceDropActions();
         if (var2 != 1 && var2 != 2 && var2 != 1073741824) {
            throw new IllegalArgumentException("unsupported drop action: " + var1);
         } else {
            this.dropAction = var1;
         }
      }

      public int getDropAction() {
         return this.dropAction == -1 ? this.getUserDropAction() : this.dropAction;
      }

      public int getUserDropAction() {
         this.assureIsDrop();
         return this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getDropAction() : ((DropTargetDropEvent)this.source).getDropAction();
      }

      public int getSourceDropActions() {
         this.assureIsDrop();
         return this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getSourceActions() : ((DropTargetDropEvent)this.source).getSourceActions();
      }

      public DataFlavor[] getDataFlavors() {
         if (this.isDrop) {
            return this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getCurrentDataFlavors() : ((DropTargetDropEvent)this.source).getCurrentDataFlavors();
         } else {
            return ((Transferable)this.source).getTransferDataFlavors();
         }
      }

      public boolean isDataFlavorSupported(DataFlavor var1) {
         if (this.isDrop) {
            return this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).isDataFlavorSupported(var1) : ((DropTargetDropEvent)this.source).isDataFlavorSupported(var1);
         } else {
            return ((Transferable)this.source).isDataFlavorSupported(var1);
         }
      }

      public Transferable getTransferable() {
         if (this.isDrop) {
            return this.source instanceof DropTargetDragEvent ? ((DropTargetDragEvent)this.source).getTransferable() : ((DropTargetDropEvent)this.source).getTransferable();
         } else {
            return (Transferable)this.source;
         }
      }

      // $FF: synthetic method
      TransferSupport(Component var1, DropTargetEvent var2, Object var3) {
         this(var1, var2);
      }
   }

   public static class DropLocation {
      private final Point dropPoint;

      protected DropLocation(Point var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("Point cannot be null");
         } else {
            this.dropPoint = new Point(var1);
         }
      }

      public final Point getDropPoint() {
         return new Point(this.dropPoint);
      }

      public String toString() {
         return this.getClass().getName() + "[dropPoint=" + this.dropPoint + "]";
      }
   }

   interface HasGetTransferHandler {
      TransferHandler getTransferHandler();
   }
}
