package javax.swing;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.awt.EmbeddedFrame;
import sun.awt.OSInfo;

public class PopupFactory {
   private static final Object SharedInstanceKey = new StringBuffer("PopupFactory.SharedInstanceKey");
   private static final int MAX_CACHE_SIZE = 5;
   static final int LIGHT_WEIGHT_POPUP = 0;
   static final int MEDIUM_WEIGHT_POPUP = 1;
   static final int HEAVY_WEIGHT_POPUP = 2;
   private int popupType = 0;

   public static void setSharedInstance(PopupFactory var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("PopupFactory can not be null");
      } else {
         SwingUtilities.appContextPut(SharedInstanceKey, var0);
      }
   }

   public static PopupFactory getSharedInstance() {
      PopupFactory var0 = (PopupFactory)SwingUtilities.appContextGet(SharedInstanceKey);
      if (var0 == null) {
         var0 = new PopupFactory();
         setSharedInstance(var0);
      }

      return var0;
   }

   void setPopupType(int var1) {
      this.popupType = var1;
   }

   int getPopupType() {
      return this.popupType;
   }

   public Popup getPopup(Component var1, Component var2, int var3, int var4) throws IllegalArgumentException {
      if (var2 == null) {
         throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
      } else {
         int var5 = this.getPopupType(var1, var2, var3, var4);
         Popup var6 = this.getPopup(var1, var2, var3, var4, var5);
         if (var6 == null) {
            var6 = this.getPopup(var1, var2, var3, var4, 2);
         }

         return var6;
      }
   }

   private int getPopupType(Component var1, Component var2, int var3, int var4) {
      int var5 = this.getPopupType();
      if (var1 != null && !this.invokerInHeavyWeightPopup(var1)) {
         if (var5 == 0 && !(var2 instanceof JToolTip) && !(var2 instanceof JPopupMenu)) {
            var5 = 1;
         }
      } else {
         var5 = 2;
      }

      for(Object var6 = var1; var6 != null; var6 = ((Component)var6).getParent()) {
         if (var6 instanceof JComponent && ((JComponent)var6).getClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP) == Boolean.TRUE) {
            var5 = 2;
            break;
         }
      }

      return var5;
   }

   private Popup getPopup(Component var1, Component var2, int var3, int var4, int var5) {
      if (GraphicsEnvironment.isHeadless()) {
         return this.getHeadlessPopup(var1, var2, var3, var4);
      } else {
         switch(var5) {
         case 0:
            return this.getLightWeightPopup(var1, var2, var3, var4);
         case 1:
            return this.getMediumWeightPopup(var1, var2, var3, var4);
         case 2:
            Popup var6 = this.getHeavyWeightPopup(var1, var2, var3, var4);
            if (AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.MACOSX && var1 != null && EmbeddedFrame.getAppletIfAncestorOf(var1) != null) {
               ((PopupFactory.HeavyWeightPopup)var6).setCacheEnabled(false);
            }

            return var6;
         default:
            return null;
         }
      }
   }

   private Popup getHeadlessPopup(Component var1, Component var2, int var3, int var4) {
      return PopupFactory.HeadlessPopup.getHeadlessPopup(var1, var2, var3, var4);
   }

   private Popup getLightWeightPopup(Component var1, Component var2, int var3, int var4) {
      return PopupFactory.LightWeightPopup.getLightWeightPopup(var1, var2, var3, var4);
   }

   private Popup getMediumWeightPopup(Component var1, Component var2, int var3, int var4) {
      return PopupFactory.MediumWeightPopup.getMediumWeightPopup(var1, var2, var3, var4);
   }

   private Popup getHeavyWeightPopup(Component var1, Component var2, int var3, int var4) {
      return GraphicsEnvironment.isHeadless() ? this.getMediumWeightPopup(var1, var2, var3, var4) : PopupFactory.HeavyWeightPopup.getHeavyWeightPopup(var1, var2, var3, var4);
   }

   private boolean invokerInHeavyWeightPopup(Component var1) {
      if (var1 != null) {
         for(Container var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
            if (var2 instanceof Popup.HeavyWeightWindow) {
               return true;
            }
         }
      }

      return false;
   }

   private static class MediumWeightPopup extends PopupFactory.ContainerPopup {
      private static final Object mediumWeightPopupCacheKey = new StringBuffer("PopupFactory.mediumPopupCache");
      private JRootPane rootPane;

      private MediumWeightPopup() {
         super(null);
      }

      static Popup getMediumWeightPopup(Component var0, Component var1, int var2, int var3) {
         PopupFactory.MediumWeightPopup var4 = getRecycledMediumWeightPopup();
         if (var4 == null) {
            var4 = new PopupFactory.MediumWeightPopup();
         }

         var4.reset(var0, var1, var2, var3);
         if (var4.fitsOnScreen() && !var4.overlappedByOwnedWindow()) {
            return var4;
         } else {
            var4.hide();
            return null;
         }
      }

      private static List<PopupFactory.MediumWeightPopup> getMediumWeightPopupCache() {
         Object var0 = (List)SwingUtilities.appContextGet(mediumWeightPopupCacheKey);
         if (var0 == null) {
            var0 = new ArrayList();
            SwingUtilities.appContextPut(mediumWeightPopupCacheKey, var0);
         }

         return (List)var0;
      }

      private static void recycleMediumWeightPopup(PopupFactory.MediumWeightPopup var0) {
         Class var1 = PopupFactory.MediumWeightPopup.class;
         synchronized(PopupFactory.MediumWeightPopup.class) {
            List var2 = getMediumWeightPopupCache();
            if (var2.size() < 5) {
               var2.add(var0);
            }

         }
      }

      private static PopupFactory.MediumWeightPopup getRecycledMediumWeightPopup() {
         Class var0 = PopupFactory.MediumWeightPopup.class;
         synchronized(PopupFactory.MediumWeightPopup.class) {
            List var1 = getMediumWeightPopupCache();
            if (var1.size() > 0) {
               PopupFactory.MediumWeightPopup var2 = (PopupFactory.MediumWeightPopup)var1.get(0);
               var1.remove(0);
               return var2;
            } else {
               return null;
            }
         }
      }

      public void hide() {
         super.hide();
         this.rootPane.getContentPane().removeAll();
         recycleMediumWeightPopup(this);
      }

      public void show() {
         Component var1 = this.getComponent();
         Container var2 = null;
         if (this.owner != null) {
            var2 = this.owner.getParent();
         }

         while(!(var2 instanceof Window) && !(var2 instanceof Applet) && var2 != null) {
            var2 = var2.getParent();
         }

         Point var3;
         if (var2 instanceof RootPaneContainer) {
            JLayeredPane var4 = ((RootPaneContainer)var2).getLayeredPane();
            var3 = SwingUtilities.convertScreenLocationToParent(var4, this.x, this.y);
            var1.setVisible(false);
            var1.setLocation(var3.x, var3.y);
            var4.add(var1, JLayeredPane.POPUP_LAYER, 0);
         } else {
            var3 = SwingUtilities.convertScreenLocationToParent(var2, this.x, this.y);
            var1.setLocation(var3.x, var3.y);
            var1.setVisible(false);
            var2.add(var1);
         }

         var1.setVisible(true);
      }

      Component createComponent(Component var1) {
         PopupFactory.MediumWeightPopup.MediumWeightComponent var2 = new PopupFactory.MediumWeightPopup.MediumWeightComponent();
         this.rootPane = new JRootPane();
         this.rootPane.setOpaque(true);
         var2.add(this.rootPane, "Center");
         return var2;
      }

      void reset(Component var1, Component var2, int var3, int var4) {
         super.reset(var1, var2, var3, var4);
         Component var5 = this.getComponent();
         var5.setLocation(var3, var4);
         this.rootPane.getContentPane().add((Component)var2, (Object)"Center");
         var2.invalidate();
         var5.validate();
         this.pack();
      }

      private static class MediumWeightComponent extends Panel implements SwingHeavyWeight {
         MediumWeightComponent() {
            super(new BorderLayout());
         }
      }
   }

   private static class LightWeightPopup extends PopupFactory.ContainerPopup {
      private static final Object lightWeightPopupCacheKey = new StringBuffer("PopupFactory.lightPopupCache");

      private LightWeightPopup() {
         super(null);
      }

      static Popup getLightWeightPopup(Component var0, Component var1, int var2, int var3) {
         PopupFactory.LightWeightPopup var4 = getRecycledLightWeightPopup();
         if (var4 == null) {
            var4 = new PopupFactory.LightWeightPopup();
         }

         var4.reset(var0, var1, var2, var3);
         if (var4.fitsOnScreen() && !var4.overlappedByOwnedWindow()) {
            return var4;
         } else {
            var4.hide();
            return null;
         }
      }

      private static List<PopupFactory.LightWeightPopup> getLightWeightPopupCache() {
         Object var0 = (List)SwingUtilities.appContextGet(lightWeightPopupCacheKey);
         if (var0 == null) {
            var0 = new ArrayList();
            SwingUtilities.appContextPut(lightWeightPopupCacheKey, var0);
         }

         return (List)var0;
      }

      private static void recycleLightWeightPopup(PopupFactory.LightWeightPopup var0) {
         Class var1 = PopupFactory.LightWeightPopup.class;
         synchronized(PopupFactory.LightWeightPopup.class) {
            List var2 = getLightWeightPopupCache();
            if (var2.size() < 5) {
               var2.add(var0);
            }

         }
      }

      private static PopupFactory.LightWeightPopup getRecycledLightWeightPopup() {
         Class var0 = PopupFactory.LightWeightPopup.class;
         synchronized(PopupFactory.LightWeightPopup.class) {
            List var1 = getLightWeightPopupCache();
            if (var1.size() > 0) {
               PopupFactory.LightWeightPopup var2 = (PopupFactory.LightWeightPopup)var1.get(0);
               var1.remove(0);
               return var2;
            } else {
               return null;
            }
         }
      }

      public void hide() {
         super.hide();
         Container var1 = (Container)this.getComponent();
         var1.removeAll();
         recycleLightWeightPopup(this);
      }

      public void show() {
         Object var1 = null;
         if (this.owner != null) {
            var1 = this.owner instanceof Container ? (Container)this.owner : this.owner.getParent();
         }

         for(Object var2 = var1; var2 != null; var2 = ((Container)var2).getParent()) {
            if (var2 instanceof JRootPane) {
               if (!(((Container)var2).getParent() instanceof JInternalFrame)) {
                  var1 = ((JRootPane)var2).getLayeredPane();
               }
            } else {
               if (var2 instanceof Window) {
                  if (var1 == null) {
                     var1 = var2;
                  }
                  break;
               }

               if (var2 instanceof JApplet) {
                  break;
               }
            }
         }

         Point var4 = SwingUtilities.convertScreenLocationToParent((Container)var1, this.x, this.y);
         Component var3 = this.getComponent();
         var3.setLocation(var4.x, var4.y);
         if (var1 instanceof JLayeredPane) {
            ((Container)var1).add(var3, JLayeredPane.POPUP_LAYER, 0);
         } else {
            ((Container)var1).add(var3);
         }

      }

      Component createComponent(Component var1) {
         JPanel var2 = new JPanel(new BorderLayout(), true);
         var2.setOpaque(true);
         return var2;
      }

      void reset(Component var1, Component var2, int var3, int var4) {
         super.reset(var1, var2, var3, var4);
         JComponent var5 = (JComponent)this.getComponent();
         var5.setOpaque(var2.isOpaque());
         var5.setLocation(var3, var4);
         var5.add(var2, "Center");
         var2.invalidate();
         this.pack();
      }
   }

   private static class HeadlessPopup extends PopupFactory.ContainerPopup {
      private HeadlessPopup() {
         super(null);
      }

      static Popup getHeadlessPopup(Component var0, Component var1, int var2, int var3) {
         PopupFactory.HeadlessPopup var4 = new PopupFactory.HeadlessPopup();
         var4.reset(var0, var1, var2, var3);
         return var4;
      }

      Component createComponent(Component var1) {
         return new Panel(new BorderLayout());
      }

      public void show() {
      }

      public void hide() {
      }
   }

   private static class ContainerPopup extends Popup {
      Component owner;
      int x;
      int y;

      private ContainerPopup() {
      }

      public void hide() {
         Component var1 = this.getComponent();
         if (var1 != null) {
            Container var2 = var1.getParent();
            if (var2 != null) {
               Rectangle var3 = var1.getBounds();
               var2.remove(var1);
               var2.repaint(var3.x, var3.y, var3.width, var3.height);
            }
         }

         this.owner = null;
      }

      public void pack() {
         Component var1 = this.getComponent();
         if (var1 != null) {
            var1.setSize(var1.getPreferredSize());
         }

      }

      void reset(Component var1, Component var2, int var3, int var4) {
         if (var1 instanceof JFrame || var1 instanceof JDialog || var1 instanceof JWindow) {
            var1 = ((RootPaneContainer)var1).getLayeredPane();
         }

         super.reset((Component)var1, var2, var3, var4);
         this.x = var3;
         this.y = var4;
         this.owner = (Component)var1;
      }

      boolean overlappedByOwnedWindow() {
         Component var1 = this.getComponent();
         if (this.owner != null && var1 != null) {
            Window var2 = SwingUtilities.getWindowAncestor(this.owner);
            if (var2 == null) {
               return false;
            }

            Window[] var3 = var2.getOwnedWindows();
            if (var3 != null) {
               Rectangle var4 = var1.getBounds();
               Window[] var5 = var3;
               int var6 = var3.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Window var8 = var5[var7];
                  if (var8.isVisible() && var4.intersects(var8.getBounds())) {
                     return true;
                  }
               }
            }
         }

         return false;
      }

      boolean fitsOnScreen() {
         boolean var1 = false;
         Component var2 = this.getComponent();
         if (this.owner != null && var2 != null) {
            int var3 = var2.getWidth();
            int var4 = var2.getHeight();
            Container var5 = (Container)SwingUtilities.getRoot(this.owner);
            Rectangle var6;
            if (!(var5 instanceof JFrame) && !(var5 instanceof JDialog) && !(var5 instanceof JWindow)) {
               if (var5 instanceof JApplet) {
                  var6 = var5.getBounds();
                  Point var10 = var5.getLocationOnScreen();
                  var6.x = var10.x;
                  var6.y = var10.y;
                  var1 = var6.contains(this.x, this.y, var3, var4);
               }
            } else {
               var6 = var5.getBounds();
               Insets var7 = var5.getInsets();
               var6.x += var7.left;
               var6.y += var7.top;
               var6.width -= var7.left + var7.right;
               var6.height -= var7.top + var7.bottom;
               if (JPopupMenu.canPopupOverlapTaskBar()) {
                  GraphicsConfiguration var8 = var5.getGraphicsConfiguration();
                  Rectangle var9 = this.getContainerPopupArea(var8);
                  var1 = var6.intersection(var9).contains(this.x, this.y, var3, var4);
               } else {
                  var1 = var6.contains(this.x, this.y, var3, var4);
               }
            }
         }

         return var1;
      }

      Rectangle getContainerPopupArea(GraphicsConfiguration var1) {
         Toolkit var3 = Toolkit.getDefaultToolkit();
         Rectangle var2;
         Insets var4;
         if (var1 != null) {
            var2 = var1.getBounds();
            var4 = var3.getScreenInsets(var1);
         } else {
            var2 = new Rectangle(var3.getScreenSize());
            var4 = new Insets(0, 0, 0, 0);
         }

         var2.x += var4.left;
         var2.y += var4.top;
         var2.width -= var4.left + var4.right;
         var2.height -= var4.top + var4.bottom;
         return var2;
      }

      // $FF: synthetic method
      ContainerPopup(Object var1) {
         this();
      }
   }

   private static class HeavyWeightPopup extends Popup {
      private static final Object heavyWeightPopupCacheKey = new StringBuffer("PopupFactory.heavyWeightPopupCache");
      private volatile boolean isCacheEnabled = true;

      static Popup getHeavyWeightPopup(Component var0, Component var1, int var2, int var3) {
         Window var4 = var0 != null ? SwingUtilities.getWindowAncestor(var0) : null;
         PopupFactory.HeavyWeightPopup var5 = null;
         if (var4 != null) {
            var5 = getRecycledHeavyWeightPopup(var4);
         }

         boolean var6 = false;
         if (var1 != null && var1.isFocusable() && var1 instanceof JPopupMenu) {
            JPopupMenu var7 = (JPopupMenu)var1;
            Component[] var8 = var7.getComponents();
            Component[] var9 = var8;
            int var10 = var8.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               Component var12 = var9[var11];
               if (!(var12 instanceof MenuElement) && !(var12 instanceof JSeparator)) {
                  var6 = true;
                  break;
               }
            }
         }

         if (var5 == null || ((JWindow)var5.getComponent()).getFocusableWindowState() != var6) {
            if (var5 != null) {
               var5._dispose();
            }

            var5 = new PopupFactory.HeavyWeightPopup();
         }

         var5.reset(var0, var1, var2, var3);
         if (var6) {
            JWindow var13 = (JWindow)var5.getComponent();
            var13.setFocusableWindowState(true);
            var13.setName("###focusableSwingPopup###");
         }

         return var5;
      }

      private static PopupFactory.HeavyWeightPopup getRecycledHeavyWeightPopup(Window var0) {
         Class var1 = PopupFactory.HeavyWeightPopup.class;
         synchronized(PopupFactory.HeavyWeightPopup.class) {
            Map var3 = getHeavyWeightPopupCache();
            if (var3.containsKey(var0)) {
               List var2 = (List)var3.get(var0);
               if (var2.size() > 0) {
                  PopupFactory.HeavyWeightPopup var4 = (PopupFactory.HeavyWeightPopup)var2.get(0);
                  var2.remove(0);
                  return var4;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      }

      private static Map<Window, List<PopupFactory.HeavyWeightPopup>> getHeavyWeightPopupCache() {
         Class var0 = PopupFactory.HeavyWeightPopup.class;
         synchronized(PopupFactory.HeavyWeightPopup.class) {
            Object var1 = (Map)SwingUtilities.appContextGet(heavyWeightPopupCacheKey);
            if (var1 == null) {
               var1 = new HashMap(2);
               SwingUtilities.appContextPut(heavyWeightPopupCacheKey, var1);
            }

            return (Map)var1;
         }
      }

      private static void recycleHeavyWeightPopup(PopupFactory.HeavyWeightPopup var0) {
         Class var1 = PopupFactory.HeavyWeightPopup.class;
         synchronized(PopupFactory.HeavyWeightPopup.class) {
            final Window var3 = SwingUtilities.getWindowAncestor(var0.getComponent());
            Map var4 = getHeavyWeightPopupCache();
            if (!(var3 instanceof Popup.DefaultFrame) && var3.isVisible()) {
               Object var2;
               if (var4.containsKey(var3)) {
                  var2 = (List)var4.get(var3);
               } else {
                  var2 = new ArrayList();
                  var4.put(var3, var2);
                  var3.addWindowListener(new WindowAdapter() {
                     public void windowClosed(WindowEvent var1) {
                        Class var3x = PopupFactory.HeavyWeightPopup.class;
                        List var2;
                        synchronized(PopupFactory.HeavyWeightPopup.class) {
                           Map var4 = PopupFactory.HeavyWeightPopup.getHeavyWeightPopupCache();
                           var2 = (List)var4.remove(var3);
                        }

                        if (var2 != null) {
                           for(int var7 = var2.size() - 1; var7 >= 0; --var7) {
                              ((PopupFactory.HeavyWeightPopup)var2.get(var7))._dispose();
                           }
                        }

                     }
                  });
               }

               if (((List)var2).size() < 5) {
                  ((List)var2).add(var0);
               } else {
                  var0._dispose();
               }

            } else {
               var0._dispose();
            }
         }
      }

      void setCacheEnabled(boolean var1) {
         this.isCacheEnabled = var1;
      }

      public void hide() {
         super.hide();
         if (this.isCacheEnabled) {
            recycleHeavyWeightPopup(this);
         } else {
            this._dispose();
         }

      }

      void dispose() {
      }

      void _dispose() {
         super.dispose();
      }
   }
}
