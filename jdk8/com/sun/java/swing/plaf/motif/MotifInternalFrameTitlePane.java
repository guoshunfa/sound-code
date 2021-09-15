package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.awt.AWTAccessor;

public class MotifInternalFrameTitlePane extends BasicInternalFrameTitlePane implements LayoutManager, ActionListener, PropertyChangeListener {
   MotifInternalFrameTitlePane.SystemButton systemButton;
   MotifInternalFrameTitlePane.MinimizeButton minimizeButton;
   MotifInternalFrameTitlePane.MaximizeButton maximizeButton;
   JPopupMenu systemMenu;
   MotifInternalFrameTitlePane.Title title;
   Color color;
   Color highlight;
   Color shadow;
   public static final int BUTTON_SIZE = 19;
   static Dimension buttonDimension = new Dimension(19, 19);

   public MotifInternalFrameTitlePane(JInternalFrame var1) {
      super(var1);
   }

   protected void installDefaults() {
      this.setFont(UIManager.getFont("InternalFrame.titleFont"));
      this.setPreferredSize(new Dimension(100, 19));
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this;
   }

   protected LayoutManager createLayout() {
      return this;
   }

   JPopupMenu getSystemMenu() {
      return this.systemMenu;
   }

   protected void assembleSystemMenu() {
      this.systemMenu = new JPopupMenu();
      JMenuItem var1 = this.systemMenu.add(this.restoreAction);
      var1.setMnemonic(getButtonMnemonic("restore"));
      var1 = this.systemMenu.add(this.moveAction);
      var1.setMnemonic(getButtonMnemonic("move"));
      var1 = this.systemMenu.add(this.sizeAction);
      var1.setMnemonic(getButtonMnemonic("size"));
      var1 = this.systemMenu.add(this.iconifyAction);
      var1.setMnemonic(getButtonMnemonic("minimize"));
      var1 = this.systemMenu.add(this.maximizeAction);
      var1.setMnemonic(getButtonMnemonic("maximize"));
      this.systemMenu.add((Component)(new JSeparator()));
      var1 = this.systemMenu.add(this.closeAction);
      var1.setMnemonic(getButtonMnemonic("close"));
      this.systemButton = new MotifInternalFrameTitlePane.SystemButton();
      this.systemButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MotifInternalFrameTitlePane.this.systemMenu.show(MotifInternalFrameTitlePane.this.systemButton, 0, 19);
         }
      });
      this.systemButton.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1) {
            try {
               MotifInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException var3) {
            }

            if (var1.getClickCount() == 2) {
               MotifInternalFrameTitlePane.this.closeAction.actionPerformed(new ActionEvent(var1.getSource(), 1001, (String)null, var1.getWhen(), 0));
               MotifInternalFrameTitlePane.this.systemMenu.setVisible(false);
            }

         }
      });
   }

   private static int getButtonMnemonic(String var0) {
      try {
         return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + var0 + "Button.mnemonic"));
      } catch (NumberFormatException var2) {
         return -1;
      }
   }

   protected void createButtons() {
      this.minimizeButton = new MotifInternalFrameTitlePane.MinimizeButton();
      this.minimizeButton.addActionListener(this.iconifyAction);
      this.maximizeButton = new MotifInternalFrameTitlePane.MaximizeButton();
      this.maximizeButton.addActionListener(this.maximizeAction);
   }

   protected void addSubComponents() {
      this.title = new MotifInternalFrameTitlePane.Title(this.frame.getTitle());
      this.title.setFont(this.getFont());
      this.add(this.systemButton);
      this.add(this.title);
      this.add(this.minimizeButton);
      this.add(this.maximizeButton);
   }

   public void paintComponent(Graphics var1) {
   }

   void setColors(Color var1, Color var2, Color var3) {
      this.color = var1;
      this.highlight = var2;
      this.shadow = var3;
   }

   public void actionPerformed(ActionEvent var1) {
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      JInternalFrame var3 = (JInternalFrame)var1.getSource();
      boolean var4 = false;
      if ("selected".equals(var2)) {
         this.repaint();
      } else if (var2.equals("maximizable")) {
         if ((Boolean)var1.getNewValue() == Boolean.TRUE) {
            this.add(this.maximizeButton);
         } else {
            this.remove(this.maximizeButton);
         }

         this.revalidate();
         this.repaint();
      } else if (var2.equals("iconable")) {
         if ((Boolean)var1.getNewValue() == Boolean.TRUE) {
            this.add(this.minimizeButton);
         } else {
            this.remove(this.minimizeButton);
         }

         this.revalidate();
         this.repaint();
      } else if (var2.equals("title")) {
         this.repaint();
      }

      this.enableActions();
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      return this.minimumLayoutSize(var1);
   }

   public Dimension minimumLayoutSize(Container var1) {
      return new Dimension(100, 19);
   }

   public void layoutContainer(Container var1) {
      int var2 = this.getWidth();
      this.systemButton.setBounds(0, 0, 19, 19);
      int var3 = var2 - 19;
      if (this.frame.isMaximizable()) {
         this.maximizeButton.setBounds(var3, 0, 19, 19);
         var3 -= 19;
      } else if (this.maximizeButton.getParent() != null) {
         this.maximizeButton.getParent().remove(this.maximizeButton);
      }

      if (this.frame.isIconifiable()) {
         this.minimizeButton.setBounds(var3, 0, 19, 19);
         var3 -= 19;
      } else if (this.minimizeButton.getParent() != null) {
         this.minimizeButton.getParent().remove(this.minimizeButton);
      }

      this.title.setBounds(19, 0, var3, 19);
   }

   protected void showSystemMenu() {
      this.systemMenu.show(this.systemButton, 0, 19);
   }

   protected void hideSystemMenu() {
      this.systemMenu.setVisible(false);
   }

   private class Title extends MotifInternalFrameTitlePane.FrameButton {
      Title(String var2) {
         super();
         this.setText(var2);
         this.setHorizontalAlignment(0);
         this.setBorder(BorderFactory.createBevelBorder(0, UIManager.getColor("activeCaptionBorder"), UIManager.getColor("inactiveCaptionBorder")));
         this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }

            public void mouseMoved(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }
         });
         this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }

            public void mousePressed(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }

            public void mouseReleased(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }

            public void mouseEntered(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }

            public void mouseExited(MouseEvent var1) {
               Title.this.forwardEventToParent(var1);
            }
         });
      }

      void forwardEventToParent(MouseEvent var1) {
         MouseEvent var2 = new MouseEvent(this.getParent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
         AWTAccessor.MouseEventAccessor var3 = AWTAccessor.getMouseEventAccessor();
         var3.setCausedByTouchEvent(var2, var3.isCausedByTouchEvent(var1));
         this.getParent().dispatchEvent(var2);
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         if (MotifInternalFrameTitlePane.this.frame.isSelected()) {
            var1.setColor(UIManager.getColor("activeCaptionText"));
         } else {
            var1.setColor(UIManager.getColor("inactiveCaptionText"));
         }

         Dimension var2 = this.getSize();
         String var3 = MotifInternalFrameTitlePane.this.frame.getTitle();
         if (var3 != null) {
            MotifGraphicsUtils.drawStringInRect(MotifInternalFrameTitlePane.this.frame, var1, var3, 0, 0, var2.width, var2.height, 0);
         }

      }
   }

   private class SystemButton extends MotifInternalFrameTitlePane.FrameButton {
      private SystemButton() {
         super();
      }

      public boolean isFocusTraversable() {
         return false;
      }

      public void requestFocus() {
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         var1.setColor(MotifInternalFrameTitlePane.this.highlight);
         var1.drawLine(4, 8, 4, 11);
         var1.drawLine(4, 8, 14, 8);
         var1.setColor(MotifInternalFrameTitlePane.this.shadow);
         var1.drawLine(5, 11, 14, 11);
         var1.drawLine(14, 9, 14, 11);
      }

      // $FF: synthetic method
      SystemButton(Object var2) {
         this();
      }
   }

   private class MaximizeButton extends MotifInternalFrameTitlePane.FrameButton {
      private MaximizeButton() {
         super();
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         byte var2 = 14;
         boolean var3 = MotifInternalFrameTitlePane.this.frame.isMaximum();
         var1.setColor(var3 ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
         var1.drawLine(4, 4, 4, var2);
         var1.drawLine(4, 4, var2, 4);
         var1.setColor(var3 ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
         var1.drawLine(5, var2, var2, var2);
         var1.drawLine(var2, 5, var2, var2);
      }

      // $FF: synthetic method
      MaximizeButton(Object var2) {
         this();
      }
   }

   private class MinimizeButton extends MotifInternalFrameTitlePane.FrameButton {
      private MinimizeButton() {
         super();
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         var1.setColor(MotifInternalFrameTitlePane.this.highlight);
         var1.drawLine(7, 8, 7, 11);
         var1.drawLine(7, 8, 10, 8);
         var1.setColor(MotifInternalFrameTitlePane.this.shadow);
         var1.drawLine(8, 11, 10, 11);
         var1.drawLine(11, 9, 11, 11);
      }

      // $FF: synthetic method
      MinimizeButton(Object var2) {
         this();
      }
   }

   private abstract class FrameButton extends JButton {
      FrameButton() {
         this.setFocusPainted(false);
         this.setBorderPainted(false);
      }

      public boolean isFocusTraversable() {
         return false;
      }

      public void requestFocus() {
      }

      public Dimension getMinimumSize() {
         return MotifInternalFrameTitlePane.buttonDimension;
      }

      public Dimension getPreferredSize() {
         return MotifInternalFrameTitlePane.buttonDimension;
      }

      public void paintComponent(Graphics var1) {
         Dimension var2 = this.getSize();
         int var3 = var2.width - 1;
         int var4 = var2.height - 1;
         var1.setColor(MotifInternalFrameTitlePane.this.color);
         var1.fillRect(1, 1, var2.width, var2.height);
         boolean var5 = this.getModel().isPressed();
         var1.setColor(var5 ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
         var1.drawLine(0, 0, var3, 0);
         var1.drawLine(0, 0, 0, var4);
         var1.setColor(var5 ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
         var1.drawLine(1, var4, var3, var4);
         var1.drawLine(var3, 1, var3, var4);
      }
   }
}
