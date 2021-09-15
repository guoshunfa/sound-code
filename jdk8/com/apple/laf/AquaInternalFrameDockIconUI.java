package com.apple.laf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;
import sun.swing.SwingUtilities2;

public class AquaInternalFrameDockIconUI extends DesktopIconUI implements MouseListener, MouseMotionListener, ComponentListener {
   private static final String CACHED_FRAME_ICON_KEY = "apple.laf.internal.frameIcon";
   protected JInternalFrame.JDesktopIcon fDesktopIcon;
   protected JInternalFrame fFrame;
   protected AquaInternalFrameDockIconUI.ScaledImageLabel fIconPane;
   protected AquaInternalFrameDockIconUI.DockLabel fDockLabel;
   protected boolean fTrackingIcon = false;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaInternalFrameDockIconUI();
   }

   public void installUI(JComponent var1) {
      this.fDesktopIcon = (JInternalFrame.JDesktopIcon)var1;
      this.installComponents();
      this.installListeners();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallComponents();
      this.uninstallListeners();
      this.fDesktopIcon = null;
      this.fFrame = null;
   }

   protected void installComponents() {
      this.fFrame = this.fDesktopIcon.getInternalFrame();
      this.fIconPane = new AquaInternalFrameDockIconUI.ScaledImageLabel();
      this.fDesktopIcon.setLayout(new BorderLayout());
      this.fDesktopIcon.add(this.fIconPane, "Center");
   }

   protected void uninstallComponents() {
      this.fDesktopIcon.setLayout((LayoutManager)null);
      this.fDesktopIcon.remove(this.fIconPane);
   }

   protected void installListeners() {
      this.fDesktopIcon.addMouseListener(this);
      this.fDesktopIcon.addMouseMotionListener(this);
      this.fFrame.addComponentListener(this);
   }

   protected void uninstallListeners() {
      this.fFrame.removeComponentListener(this);
      this.fDesktopIcon.removeMouseMotionListener(this);
      this.fDesktopIcon.removeMouseListener(this);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(32, 32);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(128, 128);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return new Dimension(64, 64);
   }

   public Insets getInsets(JComponent var1) {
      return new Insets(0, 0, 0, 0);
   }

   void updateIcon() {
      this.fIconPane.updateIcon();
   }

   public void mousePressed(MouseEvent var1) {
      this.fTrackingIcon = this.fIconPane.mouseInIcon(var1);
      if (this.fTrackingIcon) {
         this.fIconPane.repaint();
      }

   }

   public void mouseReleased(MouseEvent var1) {
      if (this.fFrame.isIconifiable() && this.fFrame.isIcon() && this.fTrackingIcon) {
         this.fTrackingIcon = false;
         if (this.fIconPane.mouseInIcon(var1)) {
            if (this.fDockLabel != null) {
               this.fDockLabel.hide();
            }

            try {
               this.fFrame.setIcon(false);
            } catch (PropertyVetoException var3) {
            }
         } else {
            this.fIconPane.repaint();
         }
      }

      if (this.fDockLabel != null && !this.fIconPane.getBounds().contains(var1.getX(), var1.getY())) {
         this.fDockLabel.hide();
      }

   }

   public void mouseEntered(MouseEvent var1) {
      if ((var1.getModifiers() & 16) == 0) {
         String var2 = this.fFrame.getTitle();
         if (var2 == null || var2.equals("")) {
            var2 = "Untitled";
         }

         this.fDockLabel = new AquaInternalFrameDockIconUI.DockLabel(var2);
         this.fDockLabel.show(this.fDesktopIcon);
      }
   }

   public void mouseExited(MouseEvent var1) {
      if (this.fDockLabel != null && (var1.getModifiers() & 16) == 0) {
         this.fDockLabel.hide();
      }

   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mouseDragged(MouseEvent var1) {
   }

   public void mouseMoved(MouseEvent var1) {
   }

   public void componentHidden(ComponentEvent var1) {
   }

   public void componentMoved(ComponentEvent var1) {
   }

   public void componentResized(ComponentEvent var1) {
      this.fFrame.putClientProperty("apple.laf.internal.frameIcon", (Object)null);
   }

   public void componentShown(ComponentEvent var1) {
      this.fFrame.putClientProperty("apple.laf.internal.frameIcon", (Object)null);
   }

   class DockLabel extends JLabel {
      static final int NUB_HEIGHT = 7;
      static final int ROUND_ADDITIONAL_HEIGHT = 8;
      static final int ROUND_ADDITIONAL_WIDTH = 12;

      DockLabel(String var2) {
         super(var2);
         this.setBorder((Border)null);
         this.setOpaque(false);
         this.setFont(AquaFonts.getDockIconFont());
         FontMetrics var3 = this.getFontMetrics(this.getFont());
         this.setSize(SwingUtilities.computeStringWidth(var3, this.getText()) + 24, var3.getAscent() + 7 + 8);
      }

      public void paint(Graphics var1) {
         int var2 = this.getWidth();
         int var3 = this.getHeight();
         Font var4 = this.getFont();
         FontMetrics var5 = this.getFontMetrics(var4);
         var1.setFont(var4);
         String var6 = this.getText().trim();
         int var7 = var5.getAscent();
         Rectangle2D var8 = var5.getStringBounds(var6, var1);
         int var9 = var2 / 2;
         int var10 = var9 - (int)var8.getWidth() / 2;
         Graphics2D var11 = var1 instanceof Graphics2D ? (Graphics2D)var1 : null;
         if (var11 != null) {
            var1.setColor(UIManager.getColor("DesktopIcon.labelBackground"));
            Object var12 = var11.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            var11.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int var13 = var3 - 8 + 1;
            var1.fillRoundRect(0, 0, var2, var13, var13, var13);
            int[] var14 = new int[]{var9, var9 + 7, var9 - 7};
            int[] var15 = new int[]{var3, var3 - 7, var3 - 7};
            var1.fillPolygon(var14, var15, 3);
            var11.setRenderingHint(RenderingHints.KEY_ANTIALIASING, var12);
         }

         var1.setColor(Color.black);
         SwingUtilities2.drawString(this, var1, (String)var6, var10, 2 + var7);
         var1.setColor(Color.white);
         SwingUtilities2.drawString(this, var1, (String)var6, var10, 1 + var7);
      }

      public void show(Component var1) {
         int var2 = (var1.getWidth() - this.getWidth()) / 2;
         int var3 = -(this.getHeight() + 6);
         Object var4 = var1.getParent();

         label32:
         for(Object var5 = var4; var5 != null; var5 = ((Container)var5).getParent()) {
            if (var5 instanceof JRootPane && !(((Container)var5).getParent() instanceof JInternalFrame)) {
               var4 = ((JRootPane)var5).getLayeredPane();
               Container var6 = ((Container)var4).getParent();

               while(true) {
                  if (var6 == null || var6 instanceof Window) {
                     break label32;
                  }

                  var6 = var6.getParent();
               }
            }
         }

         Point var7 = SwingUtilities.convertPoint(var1, var2, var3, (Component)var4);
         this.setLocation(var7.x, var7.y);
         if (var4 instanceof JLayeredPane) {
            ((JLayeredPane)var4).add(this, JLayeredPane.POPUP_LAYER, 0);
         }

      }

      public void hide() {
         Container var1 = this.getParent();
         Rectangle var2 = this.getBounds();
         if (var1 != null) {
            var1.remove(this);
            var1.repaint(var2.x, var2.y, var2.width, var2.height);
         }
      }
   }

   class ScaledImageLabel extends JLabel {
      ScaledImageLabel() {
         super((String)null, (Icon)null, 0);
      }

      void updateIcon() {
         Object var1 = AquaInternalFrameDockIconUI.this.fFrame.getClientProperty("apple.laf.internal.frameIcon");
         if (var1 instanceof ImageIcon) {
            this.setIcon((ImageIcon)var1);
         } else {
            int var2 = AquaInternalFrameDockIconUI.this.fFrame.getWidth();
            int var3 = AquaInternalFrameDockIconUI.this.fFrame.getHeight();
            if (var2 <= 0 || var3 <= 0) {
               var2 = 128;
               var3 = 128;
            }

            BufferedImage var4 = new BufferedImage(var2, var3, 3);
            Graphics var5 = var4.getGraphics();
            AquaInternalFrameDockIconUI.this.fFrame.paint(var5);
            var5.dispose();
            float var6 = (float)AquaInternalFrameDockIconUI.this.fDesktopIcon.getWidth() / (float)Math.max(var2, var3) * 0.89F;
            ImageIcon var7 = new ImageIcon(var4.getScaledInstance((int)((float)var2 * var6), -1, 4));
            AquaInternalFrameDockIconUI.this.fFrame.putClientProperty("apple.laf.internal.frameIcon", var7);
            this.setIcon(var7);
         }
      }

      public void paint(Graphics var1) {
         if (this.getIcon() == null) {
            this.updateIcon();
         }

         var1.translate(0, 2);
         if (!AquaInternalFrameDockIconUI.this.fTrackingIcon) {
            super.paint(var1);
         } else {
            ImageIcon var2 = (ImageIcon)this.getIcon();
            ImageIcon var3 = new ImageIcon(AquaUtils.generateSelectedDarkImage(var2.getImage()));
            this.setIcon(var3);
            super.paint(var1);
            this.setIcon(var2);
         }
      }

      boolean mouseInIcon(MouseEvent var1) {
         return this.getBounds().contains(var1.getX(), var1.getY());
      }

      public Dimension getPreferredSize() {
         return new Dimension(64, 64);
      }
   }
}
