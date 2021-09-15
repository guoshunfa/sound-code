package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.DesktopPaneUI;

public class JDesktopPane extends JLayeredPane implements Accessible {
   private static final String uiClassID = "DesktopPaneUI";
   transient DesktopManager desktopManager;
   private transient JInternalFrame selectedFrame = null;
   public static final int LIVE_DRAG_MODE = 0;
   public static final int OUTLINE_DRAG_MODE = 1;
   private int dragMode = 0;
   private boolean dragModeSet = false;
   private transient List<JInternalFrame> framesCache;
   private boolean componentOrderCheckingEnabled = true;
   private boolean componentOrderChanged = false;

   public JDesktopPane() {
      this.setUIProperty("opaque", Boolean.TRUE);
      this.setFocusCycleRoot(true);
      this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
         public Component getDefaultComponent(Container var1) {
            JInternalFrame[] var2 = JDesktopPane.this.getAllFrames();
            Component var3 = null;
            JInternalFrame[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               JInternalFrame var7 = var4[var6];
               var3 = var7.getFocusTraversalPolicy().getDefaultComponent(var7);
               if (var3 != null) {
                  break;
               }
            }

            return var3;
         }
      });
      this.updateUI();
   }

   public DesktopPaneUI getUI() {
      return (DesktopPaneUI)this.ui;
   }

   public void setUI(DesktopPaneUI var1) {
      super.setUI(var1);
   }

   public void setDragMode(int var1) {
      int var2 = this.dragMode;
      this.dragMode = var1;
      this.firePropertyChange("dragMode", var2, this.dragMode);
      this.dragModeSet = true;
   }

   public int getDragMode() {
      return this.dragMode;
   }

   public DesktopManager getDesktopManager() {
      return this.desktopManager;
   }

   public void setDesktopManager(DesktopManager var1) {
      DesktopManager var2 = this.desktopManager;
      this.desktopManager = var1;
      this.firePropertyChange("desktopManager", var2, this.desktopManager);
   }

   public void updateUI() {
      this.setUI((DesktopPaneUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "DesktopPaneUI";
   }

   public JInternalFrame[] getAllFrames() {
      return (JInternalFrame[])getAllFrames(this).toArray(new JInternalFrame[0]);
   }

   private static Collection<JInternalFrame> getAllFrames(Container var0) {
      LinkedHashSet var3 = new LinkedHashSet();
      int var2 = var0.getComponentCount();

      for(int var1 = 0; var1 < var2; ++var1) {
         Component var4 = var0.getComponent(var1);
         if (var4 instanceof JInternalFrame) {
            var3.add((JInternalFrame)var4);
         } else if (var4 instanceof JInternalFrame.JDesktopIcon) {
            JInternalFrame var5 = ((JInternalFrame.JDesktopIcon)var4).getInternalFrame();
            if (var5 != null) {
               var3.add(var5);
            }
         } else if (var4 instanceof Container) {
            var3.addAll(getAllFrames((Container)var4));
         }
      }

      return var3;
   }

   public JInternalFrame getSelectedFrame() {
      return this.selectedFrame;
   }

   public void setSelectedFrame(JInternalFrame var1) {
      this.selectedFrame = var1;
   }

   public JInternalFrame[] getAllFramesInLayer(int var1) {
      Collection var2 = getAllFrames(this);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         if (((JInternalFrame)var3.next()).getLayer() != var1) {
            var3.remove();
         }
      }

      return (JInternalFrame[])var2.toArray(new JInternalFrame[0]);
   }

   private List<JInternalFrame> getFrames() {
      TreeSet var2 = new TreeSet();

      for(int var3 = 0; var3 < this.getComponentCount(); ++var3) {
         Component var1 = this.getComponent(var3);
         if (var1 instanceof JInternalFrame) {
            var2.add(new JDesktopPane.ComponentPosition((JInternalFrame)var1, this.getLayer(var1), var3));
         } else if (var1 instanceof JInternalFrame.JDesktopIcon) {
            JInternalFrame var6 = ((JInternalFrame.JDesktopIcon)var1).getInternalFrame();
            var2.add(new JDesktopPane.ComponentPosition((JInternalFrame)var6, this.getLayer(var6), var3));
         }
      }

      ArrayList var7 = new ArrayList(var2.size());
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         JDesktopPane.ComponentPosition var5 = (JDesktopPane.ComponentPosition)var4.next();
         var7.add(var5.component);
      }

      return var7;
   }

   private JInternalFrame getNextFrame(JInternalFrame var1, boolean var2) {
      this.verifyFramesCache();
      if (var1 == null) {
         return this.getTopInternalFrame();
      } else {
         int var3 = this.framesCache.indexOf(var1);
         if (var3 != -1 && this.framesCache.size() != 1) {
            if (var2) {
               ++var3;
               if (var3 == this.framesCache.size()) {
                  var3 = 0;
               }
            } else {
               --var3;
               if (var3 == -1) {
                  var3 = this.framesCache.size() - 1;
               }
            }

            return (JInternalFrame)this.framesCache.get(var3);
         } else {
            return null;
         }
      }
   }

   JInternalFrame getNextFrame(JInternalFrame var1) {
      return this.getNextFrame(var1, true);
   }

   private JInternalFrame getTopInternalFrame() {
      return this.framesCache.size() == 0 ? null : (JInternalFrame)this.framesCache.get(0);
   }

   private void updateFramesCache() {
      this.framesCache = this.getFrames();
   }

   private void verifyFramesCache() {
      if (this.componentOrderChanged) {
         this.componentOrderChanged = false;
         this.updateFramesCache();
      }

   }

   public void remove(Component var1) {
      super.remove(var1);
      this.updateFramesCache();
   }

   public JInternalFrame selectFrame(boolean var1) {
      JInternalFrame var2 = this.getSelectedFrame();
      JInternalFrame var3 = this.getNextFrame(var2, var1);
      if (var3 == null) {
         return null;
      } else {
         this.setComponentOrderCheckingEnabled(false);
         if (var1 && var2 != null) {
            var2.moveToBack();
         }

         try {
            var3.setSelected(true);
         } catch (PropertyVetoException var5) {
         }

         this.setComponentOrderCheckingEnabled(true);
         return var3;
      }
   }

   void setComponentOrderCheckingEnabled(boolean var1) {
      this.componentOrderCheckingEnabled = var1;
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      super.addImpl(var1, var2, var3);
      if (this.componentOrderCheckingEnabled && (var1 instanceof JInternalFrame || var1 instanceof JInternalFrame.JDesktopIcon)) {
         this.componentOrderChanged = true;
      }

   }

   public void remove(int var1) {
      if (this.componentOrderCheckingEnabled) {
         Component var2 = this.getComponent(var1);
         if (var2 instanceof JInternalFrame || var2 instanceof JInternalFrame.JDesktopIcon) {
            this.componentOrderChanged = true;
         }
      }

      super.remove(var1);
   }

   public void removeAll() {
      if (this.componentOrderCheckingEnabled) {
         int var1 = this.getComponentCount();

         for(int var2 = 0; var2 < var1; ++var2) {
            Component var3 = this.getComponent(var2);
            if (var3 instanceof JInternalFrame || var3 instanceof JInternalFrame.JDesktopIcon) {
               this.componentOrderChanged = true;
               break;
            }
         }
      }

      super.removeAll();
   }

   public void setComponentZOrder(Component var1, int var2) {
      super.setComponentZOrder(var1, var2);
      if (this.componentOrderCheckingEnabled && (var1 instanceof JInternalFrame || var1 instanceof JInternalFrame.JDesktopIcon)) {
         this.componentOrderChanged = true;
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("DesktopPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "dragMode") {
         if (!this.dragModeSet) {
            this.setDragMode((Integer)var2);
            this.dragModeSet = false;
         }
      } else {
         super.setUIProperty(var1, var2);
      }

   }

   protected String paramString() {
      String var1 = this.desktopManager != null ? this.desktopManager.toString() : "";
      return super.paramString() + ",desktopManager=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JDesktopPane.AccessibleJDesktopPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJDesktopPane extends JComponent.AccessibleJComponent {
      protected AccessibleJDesktopPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.DESKTOP_PANE;
      }
   }

   private static class ComponentPosition implements Comparable<JDesktopPane.ComponentPosition> {
      private final JInternalFrame component;
      private final int layer;
      private final int zOrder;

      ComponentPosition(JInternalFrame var1, int var2, int var3) {
         this.component = var1;
         this.layer = var2;
         this.zOrder = var3;
      }

      public int compareTo(JDesktopPane.ComponentPosition var1) {
         int var2 = var1.layer - this.layer;
         return var2 == 0 ? this.zOrder - var1.zOrder : var2;
      }
   }
}
