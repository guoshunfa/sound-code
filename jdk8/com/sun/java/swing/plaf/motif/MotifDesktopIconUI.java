package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.EventListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class MotifDesktopIconUI extends BasicDesktopIconUI {
   protected MotifDesktopIconUI.DesktopIconActionListener desktopIconActionListener;
   protected MotifDesktopIconUI.DesktopIconMouseListener desktopIconMouseListener;
   protected Icon defaultIcon;
   protected MotifDesktopIconUI.IconButton iconButton;
   protected MotifDesktopIconUI.IconLabel iconLabel;
   private MotifInternalFrameTitlePane sysMenuTitlePane;
   JPopupMenu systemMenu;
   EventListener mml;
   static final int LABEL_HEIGHT = 18;
   static final int LABEL_DIVIDER = 4;
   static final Font defaultTitleFont = new Font("SansSerif", 0, 12);

   public static ComponentUI createUI(JComponent var0) {
      return new MotifDesktopIconUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.setDefaultIcon(UIManager.getIcon("DesktopIcon.icon"));
      this.iconButton = this.createIconButton(this.defaultIcon);
      this.sysMenuTitlePane = new MotifInternalFrameTitlePane(this.frame);
      this.systemMenu = this.sysMenuTitlePane.getSystemMenu();
      MotifBorders.FrameBorder var1 = new MotifBorders.FrameBorder(this.desktopIcon);
      this.desktopIcon.setLayout(new BorderLayout());
      this.iconButton.setBorder(var1);
      this.desktopIcon.add(this.iconButton, "Center");
      this.iconLabel = this.createIconLabel(this.frame);
      this.iconLabel.setBorder(var1);
      this.desktopIcon.add(this.iconLabel, "South");
      this.desktopIcon.setSize(this.desktopIcon.getPreferredSize());
      this.desktopIcon.validate();
      JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer((JComponent)this.frame));
   }

   protected void installComponents() {
   }

   protected void uninstallComponents() {
   }

   protected void installListeners() {
      super.installListeners();
      this.desktopIconActionListener = this.createDesktopIconActionListener();
      this.desktopIconMouseListener = this.createDesktopIconMouseListener();
      this.iconButton.addActionListener(this.desktopIconActionListener);
      this.iconButton.addMouseListener(this.desktopIconMouseListener);
      this.iconLabel.addMouseListener(this.desktopIconMouseListener);
   }

   JInternalFrame.JDesktopIcon getDesktopIcon() {
      return this.desktopIcon;
   }

   void setDesktopIcon(JInternalFrame.JDesktopIcon var1) {
      this.desktopIcon = var1;
   }

   JInternalFrame getFrame() {
      return this.frame;
   }

   void setFrame(JInternalFrame var1) {
      this.frame = var1;
   }

   protected void showSystemMenu() {
      this.systemMenu.show(this.iconButton, 0, this.getDesktopIcon().getHeight());
   }

   protected void hideSystemMenu() {
      this.systemMenu.setVisible(false);
   }

   protected MotifDesktopIconUI.IconLabel createIconLabel(JInternalFrame var1) {
      return new MotifDesktopIconUI.IconLabel(var1);
   }

   protected MotifDesktopIconUI.IconButton createIconButton(Icon var1) {
      return new MotifDesktopIconUI.IconButton(var1);
   }

   protected MotifDesktopIconUI.DesktopIconActionListener createDesktopIconActionListener() {
      return new MotifDesktopIconUI.DesktopIconActionListener();
   }

   protected MotifDesktopIconUI.DesktopIconMouseListener createDesktopIconMouseListener() {
      return new MotifDesktopIconUI.DesktopIconMouseListener();
   }

   protected void uninstallDefaults() {
      super.uninstallDefaults();
      this.desktopIcon.setLayout((LayoutManager)null);
      this.desktopIcon.remove(this.iconButton);
      this.desktopIcon.remove(this.iconLabel);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.iconButton.removeActionListener(this.desktopIconActionListener);
      this.iconButton.removeMouseListener(this.desktopIconMouseListener);
      this.sysMenuTitlePane.uninstallListeners();
   }

   public Dimension getMinimumSize(JComponent var1) {
      JInternalFrame var2 = this.desktopIcon.getInternalFrame();
      int var3 = this.defaultIcon.getIconWidth();
      int var4 = this.defaultIcon.getIconHeight() + 18 + 4;
      Border var5 = var2.getBorder();
      if (var5 != null) {
         var3 += var5.getBorderInsets(var2).left + var5.getBorderInsets(var2).right;
         var4 += var5.getBorderInsets(var2).bottom + var5.getBorderInsets(var2).top;
      }

      return new Dimension(var3, var4);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   public Icon getDefaultIcon() {
      return this.defaultIcon;
   }

   public void setDefaultIcon(Icon var1) {
      this.defaultIcon = var1;
   }

   protected class DesktopIconMouseListener extends MouseAdapter {
      public void mousePressed(MouseEvent var1) {
         if (var1.getClickCount() > 1) {
            try {
               MotifDesktopIconUI.this.getFrame().setIcon(false);
            } catch (PropertyVetoException var3) {
            }

            MotifDesktopIconUI.this.systemMenu.setVisible(false);
            MotifDesktopIconUI.this.getFrame().getDesktopPane().getDesktopManager().endDraggingFrame((JComponent)var1.getSource());
         }

      }
   }

   protected class DesktopIconActionListener implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         MotifDesktopIconUI.this.systemMenu.show(MotifDesktopIconUI.this.iconButton, 0, MotifDesktopIconUI.this.getDesktopIcon().getHeight());
      }
   }

   protected class IconButton extends JButton {
      Icon icon;

      IconButton(Icon var2) {
         super(var2);
         this.icon = var2;
         this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }

            public void mouseMoved(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }
         });
         this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }

            public void mousePressed(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }

            public void mouseReleased(MouseEvent var1) {
               if (!MotifDesktopIconUI.this.systemMenu.isShowing()) {
                  IconButton.this.forwardEventToParent(var1);
               }

            }

            public void mouseEntered(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }

            public void mouseExited(MouseEvent var1) {
               IconButton.this.forwardEventToParent(var1);
            }
         });
      }

      void forwardEventToParent(MouseEvent var1) {
         MouseEvent var2 = new MouseEvent(this.getParent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
         AWTAccessor.MouseEventAccessor var3 = AWTAccessor.getMouseEventAccessor();
         var3.setCausedByTouchEvent(var2, var3.isCausedByTouchEvent(var1));
         this.getParent().dispatchEvent(var2);
      }

      public boolean isFocusTraversable() {
         return false;
      }
   }

   protected class IconLabel extends JPanel {
      JInternalFrame frame;

      IconLabel(JInternalFrame var2) {
         this.frame = var2;
         this.setFont(MotifDesktopIconUI.defaultTitleFont);
         this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }

            public void mouseMoved(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }
         });
         this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }

            public void mousePressed(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }

            public void mouseReleased(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }

            public void mouseEntered(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }

            public void mouseExited(MouseEvent var1) {
               IconLabel.this.forwardEventToParent(var1);
            }
         });
      }

      void forwardEventToParent(MouseEvent var1) {
         MouseEvent var2 = new MouseEvent(this.getParent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
         AWTAccessor.MouseEventAccessor var3 = AWTAccessor.getMouseEventAccessor();
         var3.setCausedByTouchEvent(var2, var3.isCausedByTouchEvent(var1));
         this.getParent().dispatchEvent(var2);
      }

      public boolean isFocusTraversable() {
         return false;
      }

      public Dimension getMinimumSize() {
         return new Dimension(MotifDesktopIconUI.this.defaultIcon.getIconWidth() + 1, 22);
      }

      public Dimension getPreferredSize() {
         String var1 = this.frame.getTitle();
         FontMetrics var2 = this.frame.getFontMetrics(MotifDesktopIconUI.defaultTitleFont);
         int var3 = 4;
         if (var1 != null) {
            var3 += SwingUtilities2.stringWidth(this.frame, var2, var1);
         }

         return new Dimension(var3, 22);
      }

      public void paint(Graphics var1) {
         super.paint(var1);
         int var2 = this.getWidth() - 1;
         Color var3 = UIManager.getColor("inactiveCaptionBorder").darker().darker();
         var1.setColor(var3);
         var1.setClip(0, 0, this.getWidth(), this.getHeight());
         var1.drawLine(var2 - 1, 1, var2 - 1, 1);
         var1.drawLine(var2, 0, var2, 0);
         var1.setColor(UIManager.getColor("inactiveCaption"));
         var1.fillRect(2, 1, var2 - 3, 19);
         var1.setClip(2, 1, var2 - 4, 18);
         int var4 = 18 - SwingUtilities2.getFontMetrics(this.frame, (Graphics)var1).getDescent();
         var1.setColor(UIManager.getColor("inactiveCaptionText"));
         String var5 = this.frame.getTitle();
         if (var5 != null) {
            SwingUtilities2.drawString(this.frame, var1, (String)var5, 4, var4);
         }

      }
   }
}
