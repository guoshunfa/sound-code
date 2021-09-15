package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;

public class RepaintManager {
   static final boolean HANDLE_TOP_LEVEL_PAINT;
   private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
   private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
   private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
   private static final short BUFFER_STRATEGY_TYPE;
   private Map<GraphicsConfiguration, VolatileImage> volatileMap;
   private Map<Container, Rectangle> hwDirtyComponents;
   private Map<Component, Rectangle> dirtyComponents;
   private Map<Component, Rectangle> tmpDirtyComponents;
   private List<Component> invalidComponents;
   private List<Runnable> runnableList;
   boolean doubleBufferingEnabled;
   private Dimension doubleBufferMaxSize;
   RepaintManager.DoubleBufferInfo standardDoubleBuffer;
   private RepaintManager.PaintManager paintManager;
   private static final Object repaintManagerKey = RepaintManager.class;
   static boolean volatileImageBufferEnabled = true;
   private static final int volatileBufferType;
   private static boolean nativeDoubleBuffering;
   private static final int VOLATILE_LOOP_MAX = 2;
   private int paintDepth;
   private short bufferStrategyType;
   private boolean painting;
   private JComponent repaintRoot;
   private Thread paintThread;
   private final RepaintManager.ProcessingRunnable processingRunnable;
   private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
   private static final DisplayChangedListener displayChangedHandler = new RepaintManager.DisplayChangedHandler();
   Rectangle tmp;
   private List<SwingUtilities2.RepaintListener> repaintListeners;

   public static RepaintManager currentManager(Component var0) {
      return currentManager(AppContext.getAppContext());
   }

   static RepaintManager currentManager(AppContext var0) {
      RepaintManager var1 = (RepaintManager)var0.get(repaintManagerKey);
      if (var1 == null) {
         var1 = new RepaintManager(BUFFER_STRATEGY_TYPE);
         var0.put(repaintManagerKey, var1);
      }

      return var1;
   }

   public static RepaintManager currentManager(JComponent var0) {
      return currentManager((Component)var0);
   }

   public static void setCurrentManager(RepaintManager var0) {
      if (var0 != null) {
         SwingUtilities.appContextPut(repaintManagerKey, var0);
      } else {
         SwingUtilities.appContextRemove(repaintManagerKey);
      }

   }

   public RepaintManager() {
      this((short)2);
   }

   private RepaintManager(short var1) {
      this.volatileMap = new HashMap(1);
      this.doubleBufferingEnabled = true;
      this.paintDepth = 0;
      this.tmp = new Rectangle();
      this.repaintListeners = new ArrayList(1);
      this.doubleBufferingEnabled = !nativeDoubleBuffering;
      synchronized(this) {
         this.dirtyComponents = new IdentityHashMap();
         this.tmpDirtyComponents = new IdentityHashMap();
         this.bufferStrategyType = var1;
         this.hwDirtyComponents = new IdentityHashMap();
      }

      this.processingRunnable = new RepaintManager.ProcessingRunnable();
   }

   private void displayChanged() {
      this.clearImages();
   }

   public synchronized void addInvalidComponent(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         var2.addInvalidComponent(var1);
      } else {
         Container var3 = SwingUtilities.getValidateRoot(var1, true);
         if (var3 != null) {
            if (this.invalidComponents == null) {
               this.invalidComponents = new ArrayList();
            } else {
               int var4 = this.invalidComponents.size();

               for(int var5 = 0; var5 < var4; ++var5) {
                  if (var3 == this.invalidComponents.get(var5)) {
                     return;
                  }
               }
            }

            this.invalidComponents.add(var3);
            this.scheduleProcessingRunnable(SunToolkit.targetToAppContext(var1));
         }
      }
   }

   public synchronized void removeInvalidComponent(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         var2.removeInvalidComponent(var1);
      } else {
         if (this.invalidComponents != null) {
            int var3 = this.invalidComponents.indexOf(var1);
            if (var3 != -1) {
               this.invalidComponents.remove(var3);
            }
         }

      }
   }

   private void addDirtyRegion0(Container var1, int var2, int var3, int var4, int var5) {
      if (var4 > 0 && var5 > 0 && var1 != null) {
         if (var1.getWidth() > 0 && var1.getHeight() > 0) {
            if (!this.extendDirtyRegion(var1, var2, var3, var4, var5)) {
               Container var6 = null;
               Container var7 = var1;

               while(var7 != null) {
                  if (var7.isVisible() && var7.getPeer() != null) {
                     if (!(var7 instanceof Window) && !(var7 instanceof Applet)) {
                        var7 = var7.getParent();
                        continue;
                     }

                     if (var7 instanceof Frame && (((Frame)var7).getExtendedState() & 1) == 1) {
                        return;
                     }

                     var6 = var7;
                     break;
                  }

                  return;
               }

               if (var6 != null) {
                  synchronized(this) {
                     if (this.extendDirtyRegion(var1, var2, var3, var4, var5)) {
                        return;
                     }

                     this.dirtyComponents.put(var1, new Rectangle(var2, var3, var4, var5));
                  }

                  this.scheduleProcessingRunnable(SunToolkit.targetToAppContext(var1));
               }
            }
         }
      }
   }

   public void addDirtyRegion(JComponent var1, int var2, int var3, int var4, int var5) {
      RepaintManager var6 = this.getDelegate(var1);
      if (var6 != null) {
         var6.addDirtyRegion(var1, var2, var3, var4, var5);
      } else {
         this.addDirtyRegion0(var1, var2, var3, var4, var5);
      }
   }

   public void addDirtyRegion(Window var1, int var2, int var3, int var4, int var5) {
      this.addDirtyRegion0(var1, var2, var3, var4, var5);
   }

   public void addDirtyRegion(Applet var1, int var2, int var3, int var4, int var5) {
      this.addDirtyRegion0(var1, var2, var3, var4, var5);
   }

   void scheduleHeavyWeightPaints() {
      Map var1;
      synchronized(this) {
         if (this.hwDirtyComponents.size() == 0) {
            return;
         }

         var1 = this.hwDirtyComponents;
         this.hwDirtyComponents = new IdentityHashMap();
      }

      Iterator var2 = var1.keySet().iterator();

      while(var2.hasNext()) {
         Container var3 = (Container)var2.next();
         Rectangle var4 = (Rectangle)var1.get(var3);
         if (var3 instanceof Window) {
            this.addDirtyRegion((Window)var3, var4.x, var4.y, var4.width, var4.height);
         } else if (var3 instanceof Applet) {
            this.addDirtyRegion((Applet)var3, var4.x, var4.y, var4.width, var4.height);
         } else {
            this.addDirtyRegion0(var3, var4.x, var4.y, var4.width, var4.height);
         }
      }

   }

   void nativeAddDirtyRegion(AppContext var1, Container var2, int var3, int var4, int var5, int var6) {
      if (var5 > 0 && var6 > 0) {
         synchronized(this) {
            Rectangle var8 = (Rectangle)this.hwDirtyComponents.get(var2);
            if (var8 == null) {
               this.hwDirtyComponents.put(var2, new Rectangle(var3, var4, var5, var6));
            } else {
               this.hwDirtyComponents.put(var2, SwingUtilities.computeUnion(var3, var4, var5, var6, var8));
            }
         }

         this.scheduleProcessingRunnable(var1);
      }

   }

   void nativeQueueSurfaceDataRunnable(AppContext var1, final Component var2, final Runnable var3) {
      synchronized(this) {
         if (this.runnableList == null) {
            this.runnableList = new LinkedList();
         }

         this.runnableList.add(new Runnable() {
            public void run() {
               AccessControlContext var1 = AccessController.getContext();
               AccessControlContext var2x = AWTAccessor.getComponentAccessor().getAccessControlContext(var2);
               RepaintManager.javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
                  public Void run() {
                     var3.run();
                     return null;
                  }
               }, var1, var2x);
            }
         });
      }

      this.scheduleProcessingRunnable(var1);
   }

   private synchronized boolean extendDirtyRegion(Component var1, int var2, int var3, int var4, int var5) {
      Rectangle var6 = (Rectangle)this.dirtyComponents.get(var1);
      if (var6 != null) {
         SwingUtilities.computeUnion(var2, var3, var4, var5, var6);
         return true;
      } else {
         return false;
      }
   }

   public Rectangle getDirtyRegion(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         return var2.getDirtyRegion(var1);
      } else {
         Rectangle var3;
         synchronized(this) {
            var3 = (Rectangle)this.dirtyComponents.get(var1);
         }

         return var3 == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(var3);
      }
   }

   public void markCompletelyDirty(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         var2.markCompletelyDirty(var1);
      } else {
         this.addDirtyRegion((JComponent)var1, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
      }
   }

   public void markCompletelyClean(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         var2.markCompletelyClean(var1);
      } else {
         synchronized(this) {
            this.dirtyComponents.remove(var1);
         }
      }
   }

   public boolean isCompletelyDirty(JComponent var1) {
      RepaintManager var2 = this.getDelegate(var1);
      if (var2 != null) {
         return var2.isCompletelyDirty(var1);
      } else {
         Rectangle var3 = this.getDirtyRegion(var1);
         return var3.width == Integer.MAX_VALUE && var3.height == Integer.MAX_VALUE;
      }
   }

   public void validateInvalidComponents() {
      List var1;
      synchronized(this) {
         if (this.invalidComponents == null) {
            return;
         }

         var1 = this.invalidComponents;
         this.invalidComponents = null;
      }

      int var2 = var1.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         final Component var4 = (Component)var1.get(var3);
         AccessControlContext var5 = AccessController.getContext();
         AccessControlContext var6 = AWTAccessor.getComponentAccessor().getAccessControlContext(var4);
         javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
               var4.validate();
               return null;
            }
         }, var5, var6);
      }

   }

   private void prePaintDirtyRegions() {
      Map var1;
      List var2;
      synchronized(this) {
         var1 = this.dirtyComponents;
         var2 = this.runnableList;
         this.runnableList = null;
      }

      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Runnable var4 = (Runnable)var3.next();
            var4.run();
         }
      }

      this.paintDirtyRegions();
      if (var1.size() > 0) {
         this.paintDirtyRegions(var1);
      }

   }

   private void updateWindows(Map<Component, Rectangle> var1) {
      Toolkit var2 = Toolkit.getDefaultToolkit();
      if (var2 instanceof SunToolkit && ((SunToolkit)var2).needUpdateWindow()) {
         HashSet var3 = new HashSet();
         Set var4 = var1.keySet();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Component var6 = (Component)var5.next();
            Window var7 = var6 instanceof Window ? (Window)var6 : SwingUtilities.getWindowAncestor(var6);
            if (var7 != null && !var7.isOpaque()) {
               var3.add(var7);
            }
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            Window var8 = (Window)var5.next();
            AWTAccessor.getWindowAccessor().updateWindow(var8);
         }

      }
   }

   boolean isPainting() {
      return this.painting;
   }

   public void paintDirtyRegions() {
      synchronized(this) {
         Map var2 = this.tmpDirtyComponents;
         this.tmpDirtyComponents = this.dirtyComponents;
         this.dirtyComponents = var2;
         this.dirtyComponents.clear();
      }

      this.paintDirtyRegions(this.tmpDirtyComponents);
   }

   private void paintDirtyRegions(final Map<Component, Rectangle> var1) {
      if (!var1.isEmpty()) {
         final ArrayList var2 = new ArrayList(var1.size());
         Iterator var3 = var1.keySet().iterator();

         while(var3.hasNext()) {
            Component var4 = (Component)var3.next();
            this.collectDirtyComponents(var1, var4, var2);
         }

         final AtomicInteger var12 = new AtomicInteger(var2.size());
         this.painting = true;

         try {
            for(final int var13 = 0; var13 < var12.get(); ++var13) {
               final Component var6 = (Component)var2.get(var13);
               AccessControlContext var7 = AccessController.getContext();
               AccessControlContext var8 = AWTAccessor.getComponentAccessor().getAccessControlContext(var6);
               javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
                  public Void run() {
                     Rectangle var1x = (Rectangle)var1.get(var6);
                     if (var1x == null) {
                        return null;
                     } else {
                        int var2x = var6.getHeight();
                        int var3 = var6.getWidth();
                        SwingUtilities.computeIntersection(0, 0, var3, var2x, var1x);
                        if (var6 instanceof JComponent) {
                           ((JComponent)var6).paintImmediately(var1x.x, var1x.y, var1x.width, var1x.height);
                        } else if (var6.isShowing()) {
                           Graphics var4 = JComponent.safelyGetGraphics(var6, var6);
                           if (var4 != null) {
                              var4.setClip(var1x.x, var1x.y, var1x.width, var1x.height);

                              try {
                                 var6.paint(var4);
                              } finally {
                                 var4.dispose();
                              }
                           }
                        }

                        if (RepaintManager.this.repaintRoot != null) {
                           RepaintManager.this.adjustRoots(RepaintManager.this.repaintRoot, var2, var13 + 1);
                           var12.set(var2.size());
                           RepaintManager.this.paintManager.isRepaintingRoot = true;
                           RepaintManager.this.repaintRoot.paintImmediately(0, 0, RepaintManager.this.repaintRoot.getWidth(), RepaintManager.this.repaintRoot.getHeight());
                           RepaintManager.this.paintManager.isRepaintingRoot = false;
                           RepaintManager.this.repaintRoot = null;
                        }

                        return null;
                     }
                  }
               }, var7, var8);
            }
         } finally {
            this.painting = false;
         }

         this.updateWindows(var1);
         var1.clear();
      }
   }

   private void adjustRoots(JComponent var1, List<Component> var2, int var3) {
      for(int var4 = var2.size() - 1; var4 >= var3; --var4) {
         Object var5;
         for(var5 = (Component)var2.get(var4); var5 != var1 && var5 != null && var5 instanceof JComponent; var5 = ((Component)var5).getParent()) {
         }

         if (var5 == var1) {
            var2.remove(var4);
         }
      }

   }

   void collectDirtyComponents(Map<Component, Rectangle> var1, Component var2, List<Component> var3) {
      Object var9 = var2;
      Object var8 = var2;
      int var12 = var2.getX();
      int var13 = var2.getY();
      int var14 = var2.getWidth();
      int var15 = var2.getHeight();
      int var6 = 0;
      int var4 = 0;
      int var7 = 0;
      int var5 = 0;
      this.tmp.setBounds((Rectangle)var1.get(var2));
      SwingUtilities.computeIntersection(0, 0, var14, var15, this.tmp);
      if (!this.tmp.isEmpty()) {
         while(var8 instanceof JComponent) {
            Container var10 = ((Component)var8).getParent();
            if (var10 == null) {
               break;
            }

            var8 = var10;
            var4 += var12;
            var5 += var13;
            this.tmp.setLocation(this.tmp.x + var12, this.tmp.y + var13);
            var12 = var10.getX();
            var13 = var10.getY();
            var14 = var10.getWidth();
            var15 = var10.getHeight();
            this.tmp = SwingUtilities.computeIntersection(0, 0, var14, var15, this.tmp);
            if (this.tmp.isEmpty()) {
               return;
            }

            if (var1.get(var10) != null) {
               var9 = var10;
               var6 = var4;
               var7 = var5;
            }
         }

         if (var2 != var9) {
            this.tmp.setLocation(this.tmp.x + var6 - var4, this.tmp.y + var7 - var5);
            Rectangle var16 = (Rectangle)var1.get(var9);
            SwingUtilities.computeUnion(this.tmp.x, this.tmp.y, this.tmp.width, this.tmp.height, var16);
         }

         if (!var3.contains(var9)) {
            var3.add(var9);
         }

      }
   }

   public synchronized String toString() {
      StringBuffer var1 = new StringBuffer();
      if (this.dirtyComponents != null) {
         var1.append("" + this.dirtyComponents);
      }

      return var1.toString();
   }

   public Image getOffscreenBuffer(Component var1, int var2, int var3) {
      RepaintManager var4 = this.getDelegate(var1);
      return var4 != null ? var4.getOffscreenBuffer(var1, var2, var3) : this._getOffscreenBuffer(var1, var2, var3);
   }

   public Image getVolatileOffscreenBuffer(Component var1, int var2, int var3) {
      RepaintManager var4 = this.getDelegate(var1);
      if (var4 != null) {
         return var4.getVolatileOffscreenBuffer(var1, var2, var3);
      } else {
         Window var5 = var1 instanceof Window ? (Window)var1 : SwingUtilities.getWindowAncestor(var1);
         if (!var5.isOpaque()) {
            Toolkit var6 = Toolkit.getDefaultToolkit();
            if (var6 instanceof SunToolkit && ((SunToolkit)var6).needUpdateWindow()) {
               return null;
            }
         }

         GraphicsConfiguration var11 = var1.getGraphicsConfiguration();
         if (var11 == null) {
            var11 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
         }

         Dimension var7 = this.getDoubleBufferMaximumSize();
         int var8 = var2 < 1 ? 1 : (var2 > var7.width ? var7.width : var2);
         int var9 = var3 < 1 ? 1 : (var3 > var7.height ? var7.height : var3);
         VolatileImage var10 = (VolatileImage)this.volatileMap.get(var11);
         if (var10 == null || var10.getWidth() < var8 || var10.getHeight() < var9) {
            if (var10 != null) {
               var10.flush();
            }

            var10 = var11.createCompatibleVolatileImage(var8, var9, volatileBufferType);
            this.volatileMap.put(var11, var10);
         }

         return var10;
      }
   }

   private Image _getOffscreenBuffer(Component var1, int var2, int var3) {
      Dimension var4 = this.getDoubleBufferMaximumSize();
      Window var8 = var1 instanceof Window ? (Window)var1 : SwingUtilities.getWindowAncestor(var1);
      if (!var8.isOpaque()) {
         Toolkit var9 = Toolkit.getDefaultToolkit();
         if (var9 instanceof SunToolkit && ((SunToolkit)var9).needUpdateWindow()) {
            return null;
         }
      }

      if (this.standardDoubleBuffer == null) {
         this.standardDoubleBuffer = new RepaintManager.DoubleBufferInfo();
      }

      RepaintManager.DoubleBufferInfo var5 = this.standardDoubleBuffer;
      int var6 = var2 < 1 ? 1 : (var2 > var4.width ? var4.width : var2);
      int var7 = var3 < 1 ? 1 : (var3 > var4.height ? var4.height : var3);
      if (var5.needsReset || var5.image != null && (var5.size.width < var6 || var5.size.height < var7)) {
         var5.needsReset = false;
         if (var5.image != null) {
            var5.image.flush();
            var5.image = null;
         }

         var6 = Math.max(var5.size.width, var6);
         var7 = Math.max(var5.size.height, var7);
      }

      Image var10 = var5.image;
      if (var5.image == null) {
         var10 = var1.createImage(var6, var7);
         var5.size = new Dimension(var6, var7);
         if (var1 instanceof JComponent) {
            ((JComponent)var1).setCreatedDoubleBuffer(true);
            var5.image = var10;
         }
      }

      return var10;
   }

   public void setDoubleBufferMaximumSize(Dimension var1) {
      this.doubleBufferMaxSize = var1;
      if (this.doubleBufferMaxSize == null) {
         this.clearImages();
      } else {
         this.clearImages(var1.width, var1.height);
      }

   }

   private void clearImages() {
      this.clearImages(0, 0);
   }

   private void clearImages(int var1, int var2) {
      if (this.standardDoubleBuffer != null && this.standardDoubleBuffer.image != null && (this.standardDoubleBuffer.image.getWidth((ImageObserver)null) > var1 || this.standardDoubleBuffer.image.getHeight((ImageObserver)null) > var2)) {
         this.standardDoubleBuffer.image.flush();
         this.standardDoubleBuffer.image = null;
      }

      Iterator var3 = this.volatileMap.keySet().iterator();

      while(true) {
         VolatileImage var5;
         do {
            if (!var3.hasNext()) {
               return;
            }

            GraphicsConfiguration var4 = (GraphicsConfiguration)var3.next();
            var5 = (VolatileImage)this.volatileMap.get(var4);
         } while(var5.getWidth() <= var1 && var5.getHeight() <= var2);

         var5.flush();
         var3.remove();
      }
   }

   public Dimension getDoubleBufferMaximumSize() {
      if (this.doubleBufferMaxSize == null) {
         try {
            Rectangle var1 = new Rectangle();
            GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] var3 = var2.getScreenDevices();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               GraphicsDevice var6 = var3[var5];
               GraphicsConfiguration var7 = var6.getDefaultConfiguration();
               var1 = var1.union(var7.getBounds());
            }

            this.doubleBufferMaxSize = new Dimension(var1.width, var1.height);
         } catch (HeadlessException var8) {
            this.doubleBufferMaxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
         }
      }

      return this.doubleBufferMaxSize;
   }

   public void setDoubleBufferingEnabled(boolean var1) {
      this.doubleBufferingEnabled = var1;
      RepaintManager.PaintManager var2 = this.getPaintManager();
      if (!var1 && var2.getClass() != RepaintManager.PaintManager.class) {
         this.setPaintManager(new RepaintManager.PaintManager());
      }

   }

   public boolean isDoubleBufferingEnabled() {
      return this.doubleBufferingEnabled;
   }

   void resetDoubleBuffer() {
      if (this.standardDoubleBuffer != null) {
         this.standardDoubleBuffer.needsReset = true;
      }

   }

   void resetVolatileDoubleBuffer(GraphicsConfiguration var1) {
      Image var2 = (Image)this.volatileMap.remove(var1);
      if (var2 != null) {
         var2.flush();
      }

   }

   boolean useVolatileDoubleBuffer() {
      return volatileImageBufferEnabled;
   }

   private synchronized boolean isPaintingThread() {
      return Thread.currentThread() == this.paintThread;
   }

   void paint(JComponent var1, JComponent var2, Graphics var3, int var4, int var5, int var6, int var7) {
      RepaintManager.PaintManager var8 = this.getPaintManager();
      if (!this.isPaintingThread() && var8.getClass() != RepaintManager.PaintManager.class) {
         var8 = new RepaintManager.PaintManager();
         var8.repaintManager = this;
      }

      if (!var8.paint(var1, var2, var3, var4, var5, var6, var7)) {
         var3.setClip(var4, var5, var6, var7);
         var1.paintToOffscreen(var3, var4, var5, var6, var7, var4 + var6, var5 + var7);
      }

   }

   void copyArea(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      this.getPaintManager().copyArea(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private void addRepaintListener(SwingUtilities2.RepaintListener var1) {
      this.repaintListeners.add(var1);
   }

   private void removeRepaintListener(SwingUtilities2.RepaintListener var1) {
      this.repaintListeners.remove(var1);
   }

   void notifyRepaintPerformed(JComponent var1, int var2, int var3, int var4, int var5) {
      Iterator var6 = this.repaintListeners.iterator();

      while(var6.hasNext()) {
         SwingUtilities2.RepaintListener var7 = (SwingUtilities2.RepaintListener)var6.next();
         var7.repaintPerformed(var1, var2, var3, var4, var5);
      }

   }

   void beginPaint() {
      boolean var1 = false;
      Thread var3 = Thread.currentThread();
      int var2;
      synchronized(this) {
         var2 = this.paintDepth;
         if (this.paintThread != null && var3 != this.paintThread) {
            var1 = true;
         } else {
            this.paintThread = var3;
            ++this.paintDepth;
         }
      }

      if (!var1 && var2 == 0) {
         this.getPaintManager().beginPaint();
      }

   }

   void endPaint() {
      if (this.isPaintingThread()) {
         RepaintManager.PaintManager var1 = null;
         synchronized(this) {
            if (--this.paintDepth == 0) {
               var1 = this.getPaintManager();
            }
         }

         if (var1 != null) {
            var1.endPaint();
            synchronized(this) {
               this.paintThread = null;
            }
         }
      }

   }

   boolean show(Container var1, int var2, int var3, int var4, int var5) {
      return this.getPaintManager().show(var1, var2, var3, var4, var5);
   }

   void doubleBufferingChanged(JRootPane var1) {
      this.getPaintManager().doubleBufferingChanged(var1);
   }

   void setPaintManager(RepaintManager.PaintManager var1) {
      if (var1 == null) {
         var1 = new RepaintManager.PaintManager();
      }

      RepaintManager.PaintManager var2;
      synchronized(this) {
         var2 = this.paintManager;
         this.paintManager = var1;
         var1.repaintManager = this;
      }

      if (var2 != null) {
         var2.dispose();
      }

   }

   private synchronized RepaintManager.PaintManager getPaintManager() {
      if (this.paintManager == null) {
         BufferStrategyPaintManager var1 = null;
         if (this.doubleBufferingEnabled && !nativeDoubleBuffering) {
            switch(this.bufferStrategyType) {
            case 0:
               Toolkit var2 = Toolkit.getDefaultToolkit();
               if (var2 instanceof SunToolkit) {
                  SunToolkit var3 = (SunToolkit)var2;
                  if (var3.useBufferPerWindow()) {
                     var1 = new BufferStrategyPaintManager();
                  }
               }
               break;
            case 1:
               var1 = new BufferStrategyPaintManager();
            }
         }

         this.setPaintManager(var1);
      }

      return this.paintManager;
   }

   private void scheduleProcessingRunnable(AppContext var1) {
      if (this.processingRunnable.markPending()) {
         Toolkit var2 = Toolkit.getDefaultToolkit();
         if (var2 instanceof SunToolkit) {
            SunToolkit.getSystemEventQueueImplPP(var1).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
         } else {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
         }
      }

   }

   private RepaintManager getDelegate(Component var1) {
      RepaintManager var2 = SwingUtilities3.getDelegateRepaintManager(var1);
      if (this == var2) {
         var2 = null;
      }

      return var2;
   }

   static {
      SwingAccessor.setRepaintManagerAccessor(new SwingAccessor.RepaintManagerAccessor() {
         public void addRepaintListener(RepaintManager var1, SwingUtilities2.RepaintListener var2) {
            var1.addRepaintListener(var2);
         }

         public void removeRepaintListener(RepaintManager var1, SwingUtilities2.RepaintListener var2) {
            var1.removeRepaintListener(var2);
         }
      });
      volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.volatileImageBufferEnabled", "true"))));
      boolean var0 = GraphicsEnvironment.isHeadless();
      if (volatileImageBufferEnabled && var0) {
         volatileImageBufferEnabled = false;
      }

      nativeDoubleBuffering = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.nativeDoubleBuffering"))));
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.bufferPerWindow")));
      if (var0) {
         BUFFER_STRATEGY_TYPE = 2;
      } else if (var1 == null) {
         BUFFER_STRATEGY_TYPE = 0;
      } else if ("true".equals(var1)) {
         BUFFER_STRATEGY_TYPE = 1;
      } else {
         BUFFER_STRATEGY_TYPE = 2;
      }

      HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.handleTopLevelPaint", "true"))));
      GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (var2 instanceof SunGraphicsEnvironment) {
         ((SunGraphicsEnvironment)var2).addDisplayChangedListener(displayChangedHandler);
      }

      Toolkit var3 = Toolkit.getDefaultToolkit();
      if (var3 instanceof SunToolkit && ((SunToolkit)var3).isSwingBackbufferTranslucencySupported()) {
         volatileBufferType = 3;
      } else {
         volatileBufferType = 1;
      }

   }

   private final class ProcessingRunnable implements Runnable {
      private boolean pending;

      private ProcessingRunnable() {
      }

      public synchronized boolean markPending() {
         if (!this.pending) {
            this.pending = true;
            return true;
         } else {
            return false;
         }
      }

      public void run() {
         synchronized(this) {
            this.pending = false;
         }

         RepaintManager.this.scheduleHeavyWeightPaints();
         RepaintManager.this.validateInvalidComponents();
         RepaintManager.this.prePaintDirtyRegions();
      }

      // $FF: synthetic method
      ProcessingRunnable(Object var2) {
         this();
      }
   }

   private static final class DisplayChangedRunnable implements Runnable {
      private DisplayChangedRunnable() {
      }

      public void run() {
         RepaintManager.currentManager((JComponent)null).displayChanged();
      }

      // $FF: synthetic method
      DisplayChangedRunnable(Object var1) {
         this();
      }
   }

   private static final class DisplayChangedHandler implements DisplayChangedListener {
      DisplayChangedHandler() {
      }

      public void displayChanged() {
         scheduleDisplayChanges();
      }

      public void paletteChanged() {
      }

      private static void scheduleDisplayChanges() {
         Iterator var0 = AppContext.getAppContexts().iterator();

         while(var0.hasNext()) {
            AppContext var1 = (AppContext)var0.next();
            synchronized(var1) {
               if (!var1.isDisposed()) {
                  EventQueue var3 = (EventQueue)var1.get(AppContext.EVENT_QUEUE_KEY);
                  if (var3 != null) {
                     var3.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new RepaintManager.DisplayChangedRunnable()));
                  }
               }
            }
         }

      }
   }

   private class DoubleBufferInfo {
      public Image image;
      public Dimension size;
      public boolean needsReset;

      private DoubleBufferInfo() {
         this.needsReset = false;
      }

      // $FF: synthetic method
      DoubleBufferInfo(Object var2) {
         this();
      }
   }

   static class PaintManager {
      protected RepaintManager repaintManager;
      boolean isRepaintingRoot;

      public boolean paint(JComponent var1, JComponent var2, Graphics var3, int var4, int var5, int var6, int var7) {
         boolean var8 = false;
         Image var9;
         if (this.repaintManager.useVolatileDoubleBuffer() && (var9 = this.getValidImage(this.repaintManager.getVolatileOffscreenBuffer(var2, var6, var7))) != null) {
            VolatileImage var10 = (VolatileImage)var9;
            GraphicsConfiguration var11 = var2.getGraphicsConfiguration();

            for(int var12 = 0; !var8 && var12 < 2; ++var12) {
               if (var10.validate(var11) == 2) {
                  this.repaintManager.resetVolatileDoubleBuffer(var11);
                  var9 = this.repaintManager.getVolatileOffscreenBuffer(var2, var6, var7);
                  var10 = (VolatileImage)var9;
               }

               this.paintDoubleBuffered(var1, var10, var3, var4, var5, var6, var7);
               var8 = !var10.contentsLost();
            }
         }

         if (!var8 && (var9 = this.getValidImage(this.repaintManager.getOffscreenBuffer(var2, var6, var7))) != null) {
            this.paintDoubleBuffered(var1, var9, var3, var4, var5, var6, var7);
            var8 = true;
         }

         return var8;
      }

      public void copyArea(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
         var2.copyArea(var3, var4, var5, var6, var7, var8);
      }

      public void beginPaint() {
      }

      public void endPaint() {
      }

      public boolean show(Container var1, int var2, int var3, int var4, int var5) {
         return false;
      }

      public void doubleBufferingChanged(JRootPane var1) {
      }

      protected void paintDoubleBuffered(JComponent var1, Image var2, Graphics var3, int var4, int var5, int var6, int var7) {
         Graphics var8 = var2.getGraphics();
         int var9 = Math.min(var6, var2.getWidth((ImageObserver)null));
         int var10 = Math.min(var7, var2.getHeight((ImageObserver)null));

         try {
            int var11 = var4;

            for(int var13 = var4 + var6; var11 < var13; var11 += var9) {
               int var12 = var5;

               for(int var14 = var5 + var7; var12 < var14; var12 += var10) {
                  var8.translate(-var11, -var12);
                  var8.setClip(var11, var12, var9, var10);
                  Graphics2D var15;
                  if (RepaintManager.volatileBufferType != 1 && var8 instanceof Graphics2D) {
                     var15 = (Graphics2D)var8;
                     Color var16 = var15.getBackground();
                     var15.setBackground(var1.getBackground());
                     var15.clearRect(var11, var12, var9, var10);
                     var15.setBackground(var16);
                  }

                  var1.paintToOffscreen(var8, var11, var12, var9, var10, var13, var14);
                  var3.setClip(var11, var12, var9, var10);
                  if (RepaintManager.volatileBufferType != 1 && var3 instanceof Graphics2D) {
                     var15 = (Graphics2D)var3;
                     Composite var20 = var15.getComposite();
                     var15.setComposite(AlphaComposite.Src);
                     var15.drawImage(var2, var11, var12, var1);
                     var15.setComposite(var20);
                  } else {
                     var3.drawImage(var2, var11, var12, var1);
                  }

                  var8.translate(var11, var12);
               }
            }
         } finally {
            var8.dispose();
         }

      }

      private Image getValidImage(Image var1) {
         return var1 != null && var1.getWidth((ImageObserver)null) > 0 && var1.getHeight((ImageObserver)null) > 0 ? var1 : null;
      }

      protected void repaintRoot(JComponent var1) {
         assert this.repaintManager.repaintRoot == null;

         if (this.repaintManager.painting) {
            this.repaintManager.repaintRoot = var1;
         } else {
            var1.repaint();
         }

      }

      protected boolean isRepaintingRoot() {
         return this.isRepaintingRoot;
      }

      protected void dispose() {
      }
   }
}
