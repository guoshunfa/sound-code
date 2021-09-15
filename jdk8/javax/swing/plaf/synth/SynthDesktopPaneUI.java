package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class SynthDesktopPaneUI extends BasicDesktopPaneUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthDesktopPaneUI.TaskBar taskBar;
   private DesktopManager oldDesktopManager;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthDesktopPaneUI();
   }

   protected void installListeners() {
      super.installListeners();
      this.desktop.addPropertyChangeListener(this);
      if (this.taskBar != null) {
         this.desktop.addComponentListener(this.taskBar);
         this.desktop.addContainerListener(this.taskBar);
      }

   }

   protected void installDefaults() {
      this.updateStyle(this.desktop);
      if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
         this.taskBar = new SynthDesktopPaneUI.TaskBar();
         Component[] var1 = this.desktop.getComponents();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Component var4 = var1[var3];
            JInternalFrame.JDesktopIcon var5;
            if (var4 instanceof JInternalFrame.JDesktopIcon) {
               var5 = (JInternalFrame.JDesktopIcon)var4;
            } else {
               if (!(var4 instanceof JInternalFrame)) {
                  continue;
               }

               var5 = ((JInternalFrame)var4).getDesktopIcon();
            }

            if (var5.getParent() == this.desktop) {
               this.desktop.remove(var5);
            }

            if (var5.getParent() != this.taskBar) {
               this.taskBar.add(var5);
               var5.getInternalFrame().addComponentListener(this.taskBar);
            }
         }

         this.taskBar.setBackground(this.desktop.getBackground());
         this.desktop.add(this.taskBar, JLayeredPane.PALETTE_LAYER + 1);
         if (this.desktop.isShowing()) {
            this.taskBar.adjustSize();
         }
      }

   }

   private void updateStyle(JDesktopPane var1) {
      SynthStyle var2 = this.style;
      SynthContext var3 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var3, this);
      if (var2 != null) {
         this.uninstallKeyboardActions();
         this.installKeyboardActions();
      }

      var3.dispose();
   }

   protected void uninstallListeners() {
      if (this.taskBar != null) {
         this.desktop.removeComponentListener(this.taskBar);
         this.desktop.removeContainerListener(this.taskBar);
      }

      this.desktop.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.desktop, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      if (this.taskBar != null) {
         Component[] var2 = this.taskBar.getComponents();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            JInternalFrame.JDesktopIcon var6 = (JInternalFrame.JDesktopIcon)var5;
            this.taskBar.remove(var6);
            var6.setPreferredSize((Dimension)null);
            JInternalFrame var7 = var6.getInternalFrame();
            if (var7.isIcon()) {
               this.desktop.add(var6);
            }

            var7.removeComponentListener(this.taskBar);
         }

         this.desktop.remove(this.taskBar);
         this.taskBar = null;
      }

   }

   protected void installDesktopManager() {
      if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
         this.desktopManager = this.oldDesktopManager = this.desktop.getDesktopManager();
         if (!(this.desktopManager instanceof SynthDesktopPaneUI.SynthDesktopManager)) {
            this.desktopManager = new SynthDesktopPaneUI.SynthDesktopManager();
            this.desktop.setDesktopManager(this.desktopManager);
         }
      } else {
         super.installDesktopManager();
      }

   }

   protected void uninstallDesktopManager() {
      if (this.oldDesktopManager != null && !(this.oldDesktopManager instanceof UIResource)) {
         this.desktopManager = this.desktop.getDesktopManager();
         if (this.desktopManager == null || this.desktopManager instanceof UIResource) {
            this.desktop.setDesktopManager(this.oldDesktopManager);
         }
      }

      this.oldDesktopManager = null;
      super.uninstallDesktopManager();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      return SynthLookAndFeel.getComponentState(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintDesktopPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintDesktopPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JDesktopPane)var1.getSource());
      }

      if (var1.getPropertyName() == "ancestor" && this.taskBar != null) {
         this.taskBar.adjustSize();
      }

   }

   class SynthDesktopManager extends DefaultDesktopManager implements UIResource {
      public void maximizeFrame(JInternalFrame var1) {
         if (var1.isIcon()) {
            try {
               var1.setIcon(false);
            } catch (PropertyVetoException var4) {
            }
         } else {
            var1.setNormalBounds(var1.getBounds());
            Container var2 = var1.getParent();
            this.setBoundsForFrame(var1, 0, 0, var2.getWidth(), var2.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight());
         }

         try {
            var1.setSelected(true);
         } catch (PropertyVetoException var3) {
         }

      }

      public void iconifyFrame(JInternalFrame var1) {
         Container var3 = var1.getParent();
         JDesktopPane var4 = var1.getDesktopPane();
         boolean var5 = var1.isSelected();
         if (var3 != null) {
            JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
            if (!var1.isMaximum()) {
               var1.setNormalBounds(var1.getBounds());
            }

            var3.remove(var1);
            var3.repaint(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());

            try {
               var1.setSelected(false);
            } catch (PropertyVetoException var12) {
            }

            if (var5) {
               Component[] var6 = var3.getComponents();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Component var9 = var6[var8];
                  if (var9 instanceof JInternalFrame) {
                     try {
                        ((JInternalFrame)var9).setSelected(true);
                     } catch (PropertyVetoException var11) {
                     }

                     ((JInternalFrame)var9).moveToFront();
                     return;
                  }
               }
            }

         }
      }

      public void deiconifyFrame(JInternalFrame var1) {
         JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
         Container var3 = var2.getParent();
         if (var3 != null) {
            var3 = var3.getParent();
            if (var3 != null) {
               var3.add(var1);
               if (var1.isMaximum()) {
                  int var4 = var3.getWidth();
                  int var5 = var3.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight();
                  if (var1.getWidth() != var4 || var1.getHeight() != var5) {
                     this.setBoundsForFrame(var1, 0, 0, var4, var5);
                  }
               }

               if (var1.isSelected()) {
                  var1.moveToFront();
               } else {
                  try {
                     var1.setSelected(true);
                  } catch (PropertyVetoException var6) {
                  }
               }
            }
         }

      }

      protected void removeIconFor(JInternalFrame var1) {
         super.removeIconFor(var1);
         SynthDesktopPaneUI.this.taskBar.validate();
      }

      public void setBoundsForFrame(JComponent var1, int var2, int var3, int var4, int var5) {
         super.setBoundsForFrame(var1, var2, var3, var4, var5);
         if (SynthDesktopPaneUI.this.taskBar != null && var3 >= SynthDesktopPaneUI.this.taskBar.getY()) {
            var1.setLocation(var1.getX(), SynthDesktopPaneUI.this.taskBar.getY() - var1.getInsets().top);
         }

      }
   }

   static class TaskBar extends JPanel implements ComponentListener, ContainerListener {
      TaskBar() {
         this.setOpaque(true);
         this.setLayout(new FlowLayout(0, 0, 0) {
            public void layoutContainer(Container var1) {
               Component[] var2 = var1.getComponents();
               int var3 = var2.length;
               if (var3 > 0) {
                  int var4 = 0;
                  Component[] var5 = var2;
                  int var6 = var2.length;

                  int var7;
                  for(var7 = 0; var7 < var6; ++var7) {
                     Component var8 = var5[var7];
                     var8.setPreferredSize((Dimension)null);
                     Dimension var9 = var8.getPreferredSize();
                     if (var9.width > var4) {
                        var4 = var9.width;
                     }
                  }

                  Insets var13 = var1.getInsets();
                  var6 = var1.getWidth() - var13.left - var13.right;
                  var7 = Math.min(var4, Math.max(10, var6 / var3));
                  Component[] var14 = var2;
                  int var15 = var2.length;

                  for(int var10 = 0; var10 < var15; ++var10) {
                     Component var11 = var14[var10];
                     Dimension var12 = var11.getPreferredSize();
                     var11.setPreferredSize(new Dimension(var7, var12.height));
                  }
               }

               super.layoutContainer(var1);
            }
         });
         this.setBorder(new BevelBorder(0) {
            protected void paintRaisedBevel(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
               Color var7 = var2.getColor();
               var2.translate(var3, var4);
               var2.setColor(this.getHighlightOuterColor(var1));
               var2.drawLine(0, 0, 0, var6 - 2);
               var2.drawLine(1, 0, var5 - 2, 0);
               var2.setColor(this.getShadowOuterColor(var1));
               var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
               var2.drawLine(var5 - 1, 0, var5 - 1, var6 - 2);
               var2.translate(-var3, -var4);
               var2.setColor(var7);
            }
         });
      }

      void adjustSize() {
         JDesktopPane var1 = (JDesktopPane)this.getParent();
         if (var1 != null) {
            int var2 = this.getPreferredSize().height;
            Insets var3 = this.getInsets();
            if (var2 == var3.top + var3.bottom) {
               if (this.getHeight() <= var2) {
                  var2 += 21;
               } else {
                  var2 = this.getHeight();
               }
            }

            this.setBounds(0, var1.getHeight() - var2, var1.getWidth(), var2);
            this.revalidate();
            this.repaint();
         }

      }

      public void componentResized(ComponentEvent var1) {
         if (var1.getSource() instanceof JDesktopPane) {
            this.adjustSize();
         }

      }

      public void componentMoved(ComponentEvent var1) {
      }

      public void componentShown(ComponentEvent var1) {
         if (var1.getSource() instanceof JInternalFrame) {
            this.adjustSize();
         }

      }

      public void componentHidden(ComponentEvent var1) {
         if (var1.getSource() instanceof JInternalFrame) {
            ((JInternalFrame)var1.getSource()).getDesktopIcon().setVisible(false);
            this.revalidate();
         }

      }

      public void componentAdded(ContainerEvent var1) {
         if (var1.getChild() instanceof JInternalFrame) {
            JDesktopPane var2 = (JDesktopPane)var1.getSource();
            JInternalFrame var3 = (JInternalFrame)var1.getChild();
            JInternalFrame.JDesktopIcon var4 = var3.getDesktopIcon();
            Component[] var5 = this.getComponents();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Component var8 = var5[var7];
               if (var8 == var4) {
                  return;
               }
            }

            this.add(var4);
            var3.addComponentListener(this);
            if (this.getComponentCount() == 1) {
               this.adjustSize();
            }
         }

      }

      public void componentRemoved(ContainerEvent var1) {
         if (var1.getChild() instanceof JInternalFrame) {
            JInternalFrame var2 = (JInternalFrame)var1.getChild();
            if (!var2.isIcon()) {
               this.remove(var2.getDesktopIcon());
               var2.removeComponentListener(this);
               this.revalidate();
               this.repaint();
            }
         }

      }
   }
}
