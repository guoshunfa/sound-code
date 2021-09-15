package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane {
   private Color selectedTitleGradientColor;
   private Color notSelectedTitleGradientColor;
   private JPopupMenu systemPopupMenu;
   private JLabel systemLabel;
   private Font titleFont;
   private int titlePaneHeight;
   private int buttonWidth;
   private int buttonHeight;
   private boolean hotTrackingOn;

   public WindowsInternalFrameTitlePane(JInternalFrame var1) {
      super(var1);
   }

   protected void addSubComponents() {
      this.add(this.systemLabel);
      this.add(this.iconButton);
      this.add(this.maxButton);
      this.add(this.closeButton);
   }

   protected void installDefaults() {
      super.installDefaults();
      this.titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
      this.buttonWidth = UIManager.getInt("InternalFrame.titleButtonWidth") - 4;
      this.buttonHeight = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
      Object var1 = UIManager.get("InternalFrame.titleButtonToolTipsOn");
      this.hotTrackingOn = var1 instanceof Boolean ? (Boolean)var1 : true;
      if (XPStyle.getXP() != null) {
         this.buttonWidth = this.buttonHeight;
         Dimension var2 = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
         if (var2 != null && var2.width != 0 && var2.height != 0) {
            this.buttonWidth = (int)((float)this.buttonWidth * (float)var2.width / (float)var2.height);
         }
      } else {
         this.buttonWidth += 2;
         Color var3 = UIManager.getColor("InternalFrame.activeBorderColor");
         this.setBorder(BorderFactory.createLineBorder(var3, 1));
      }

      this.selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
      this.notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
   }

   protected void createButtons() {
      super.createButtons();
      if (XPStyle.getXP() != null) {
         this.iconButton.setContentAreaFilled(false);
         this.maxButton.setContentAreaFilled(false);
         this.closeButton.setContentAreaFilled(false);
      }

   }

   protected void setButtonIcons() {
      super.setButtonIcons();
      if (!this.hotTrackingOn) {
         this.iconButton.setToolTipText((String)null);
         this.maxButton.setToolTipText((String)null);
         this.closeButton.setToolTipText((String)null);
      }

   }

   public void paintComponent(Graphics var1) {
      XPStyle var2 = XPStyle.getXP();
      this.paintTitleBackground(var1);
      String var3 = this.frame.getTitle();
      if (var3 != null) {
         boolean var4 = this.frame.isSelected();
         Font var5 = var1.getFont();
         Font var6 = this.titleFont != null ? this.titleFont : this.getFont();
         var1.setFont(var6);
         FontMetrics var7 = SwingUtilities2.getFontMetrics(this.frame, var1, var6);
         int var8 = (this.getHeight() + var7.getAscent() - var7.getLeading() - var7.getDescent()) / 2;
         Rectangle var9 = new Rectangle(0, 0, 0, 0);
         if (this.frame.isIconifiable()) {
            var9 = this.iconButton.getBounds();
         } else if (this.frame.isMaximizable()) {
            var9 = this.maxButton.getBounds();
         } else if (this.frame.isClosable()) {
            var9 = this.closeButton.getBounds();
         }

         byte var12 = 2;
         int var10;
         int var11;
         if (WindowsGraphicsUtils.isLeftToRight(this.frame)) {
            if (var9.x == 0) {
               var9.x = this.frame.getWidth() - this.frame.getInsets().right;
            }

            var10 = this.systemLabel.getX() + this.systemLabel.getWidth() + var12;
            if (var2 != null) {
               var10 += 2;
            }

            var11 = var9.x - var10 - var12;
         } else {
            if (var9.x == 0) {
               var9.x = this.frame.getInsets().left;
            }

            var11 = SwingUtilities2.stringWidth(this.frame, var7, var3);
            int var13 = var9.x + var9.width + var12;
            if (var2 != null) {
               var13 += 2;
            }

            int var14 = this.systemLabel.getX() - var12 - var13;
            if (var14 > var11) {
               var10 = this.systemLabel.getX() - var12 - var11;
            } else {
               var10 = var13;
               var11 = var14;
            }
         }

         var3 = this.getTitle(this.frame.getTitle(), var7, var11);
         if (var2 != null) {
            String var16 = null;
            if (var4) {
               var16 = var2.getString(this, TMSchema.Part.WP_CAPTION, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWTYPE);
            }

            if ("single".equalsIgnoreCase(var16)) {
               Point var17 = var2.getPoint(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWOFFSET);
               Color var15 = var2.getColor(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWCOLOR, (Color)null);
               if (var17 != null && var15 != null) {
                  var1.setColor(var15);
                  SwingUtilities2.drawString(this.frame, var1, (String)var3, var10 + var17.x, var8 + var17.y);
               }
            }
         }

         var1.setColor(var4 ? this.selectedTextColor : this.notSelectedTextColor);
         SwingUtilities2.drawString(this.frame, var1, (String)var3, var10, var8);
         var1.setFont(var5);
      }

   }

   public Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public Dimension getMinimumSize() {
      Dimension var1 = new Dimension(super.getMinimumSize());
      var1.height = this.titlePaneHeight + 2;
      XPStyle var2 = XPStyle.getXP();
      if (var2 != null) {
         if (this.frame.isMaximum()) {
            --var1.height;
         } else {
            var1.height += 3;
         }
      }

      return var1;
   }

   protected void paintTitleBackground(Graphics var1) {
      XPStyle var2 = XPStyle.getXP();
      if (var2 != null) {
         TMSchema.Part var3 = this.frame.isIcon() ? TMSchema.Part.WP_MINCAPTION : (this.frame.isMaximum() ? TMSchema.Part.WP_MAXCAPTION : TMSchema.Part.WP_CAPTION);
         TMSchema.State var4 = this.frame.isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
         XPStyle.Skin var5 = var2.getSkin(this, var3);
         var5.paintSkin(var1, 0, 0, this.getWidth(), this.getHeight(), var4);
      } else {
         Boolean var9 = (Boolean)LookAndFeel.getDesktopPropertyValue("win.frame.captionGradientsOn", false);
         if (var9 && var1 instanceof Graphics2D) {
            Graphics2D var10 = (Graphics2D)var1;
            Paint var11 = var10.getPaint();
            boolean var6 = this.frame.isSelected();
            int var7 = this.getWidth();
            GradientPaint var8;
            if (var6) {
               var8 = new GradientPaint(0.0F, 0.0F, this.selectedTitleColor, (float)((int)((double)var7 * 0.75D)), 0.0F, this.selectedTitleGradientColor);
               var10.setPaint(var8);
            } else {
               var8 = new GradientPaint(0.0F, 0.0F, this.notSelectedTitleColor, (float)((int)((double)var7 * 0.75D)), 0.0F, this.notSelectedTitleGradientColor);
               var10.setPaint(var8);
            }

            var10.fillRect(0, 0, this.getWidth(), this.getHeight());
            var10.setPaint(var11);
         } else {
            super.paintTitleBackground(var1);
         }
      }

   }

   protected void assembleSystemMenu() {
      this.systemPopupMenu = new JPopupMenu();
      this.addSystemMenuItems(this.systemPopupMenu);
      this.enableActions();
      this.systemLabel = new JLabel(this.frame.getFrameIcon()) {
         protected void paintComponent(Graphics var1) {
            int var2 = 0;
            int var3 = 0;
            int var4 = this.getWidth();
            int var5 = this.getHeight();
            var1 = var1.create();
            if (this.isOpaque()) {
               var1.setColor(this.getBackground());
               var1.fillRect(0, 0, var4, var5);
            }

            Icon var6 = this.getIcon();
            int var7;
            int var8;
            if (var6 != null && (var7 = var6.getIconWidth()) > 0 && (var8 = var6.getIconHeight()) > 0) {
               double var9;
               if (var7 > var8) {
                  var3 = (var5 - var4 * var8 / var7) / 2;
                  var9 = (double)var4 / (double)var7;
               } else {
                  var2 = (var4 - var5 * var7 / var8) / 2;
                  var9 = (double)var5 / (double)var8;
               }

               ((Graphics2D)var1).translate(var2, var3);
               ((Graphics2D)var1).scale(var9, var9);
               var6.paintIcon(this, var1, 0, 0);
            }

            var1.dispose();
         }
      };
      this.systemLabel.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            if (var1.getClickCount() == 2 && WindowsInternalFrameTitlePane.this.frame.isClosable() && !WindowsInternalFrameTitlePane.this.frame.isIcon()) {
               WindowsInternalFrameTitlePane.this.systemPopupMenu.setVisible(false);
               WindowsInternalFrameTitlePane.this.frame.doDefaultCloseAction();
            } else {
               super.mouseClicked(var1);
            }

         }

         public void mousePressed(MouseEvent var1) {
            try {
               WindowsInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException var3) {
            }

            WindowsInternalFrameTitlePane.this.showSystemPopupMenu(var1.getComponent());
         }
      });
   }

   protected void addSystemMenuItems(JPopupMenu var1) {
      JMenuItem var2 = var1.add(this.restoreAction);
      var2.setMnemonic(getButtonMnemonic("restore"));
      var2 = var1.add(this.moveAction);
      var2.setMnemonic(getButtonMnemonic("move"));
      var2 = var1.add(this.sizeAction);
      var2.setMnemonic(getButtonMnemonic("size"));
      var2 = var1.add(this.iconifyAction);
      var2.setMnemonic(getButtonMnemonic("minimize"));
      var2 = var1.add(this.maximizeAction);
      var2.setMnemonic(getButtonMnemonic("maximize"));
      var1.add((Component)(new JSeparator()));
      var2 = var1.add(this.closeAction);
      var2.setMnemonic(getButtonMnemonic("close"));
   }

   private static int getButtonMnemonic(String var0) {
      try {
         return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + var0 + "Button.mnemonic"));
      } catch (NumberFormatException var2) {
         return -1;
      }
   }

   protected void showSystemMenu() {
      this.showSystemPopupMenu(this.systemLabel);
   }

   private void showSystemPopupMenu(Component var1) {
      Dimension var2 = new Dimension();
      Border var3 = this.frame.getBorder();
      if (var3 != null) {
         var2.width += var3.getBorderInsets(this.frame).left + var3.getBorderInsets(this.frame).right;
         var2.height += var3.getBorderInsets(this.frame).bottom + var3.getBorderInsets(this.frame).top;
      }

      if (!this.frame.isIcon()) {
         this.systemPopupMenu.show(var1, this.getX() - var2.width, this.getY() + this.getHeight() - var2.height);
      } else {
         this.systemPopupMenu.show(var1, this.getX() - var2.width, this.getY() - this.systemPopupMenu.getPreferredSize().height - var2.height);
      }

   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new WindowsInternalFrameTitlePane.WindowsPropertyChangeHandler();
   }

   protected LayoutManager createLayout() {
      return new WindowsInternalFrameTitlePane.WindowsTitlePaneLayout();
   }

   public static class ScalableIconUIResource implements Icon, UIResource {
      private static final int SIZE = 16;
      private Icon[] icons;

      public ScalableIconUIResource(Object[] var1) {
         this.icons = new Icon[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] instanceof UIDefaults.LazyValue) {
               this.icons[var2] = (Icon)((UIDefaults.LazyValue)var1[var2]).createValue((UIDefaults)null);
            } else {
               this.icons[var2] = (Icon)var1[var2];
            }
         }

      }

      protected Icon getBestIcon(int var1) {
         if (this.icons != null && this.icons.length > 0) {
            int var2 = 0;
            int var3 = Integer.MAX_VALUE;

            for(int var4 = 0; var4 < this.icons.length; ++var4) {
               Icon var5 = this.icons[var4];
               int var6;
               if (var5 != null && (var6 = var5.getIconWidth()) > 0) {
                  int var7 = Math.abs(var6 - var1);
                  if (var7 < var3) {
                     var3 = var7;
                     var2 = var4;
                  }
               }
            }

            return this.icons[var2];
         } else {
            return null;
         }
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         Graphics2D var5 = (Graphics2D)var2.create();
         int var6 = this.getIconWidth();
         double var7 = var5.getTransform().getScaleX();
         Icon var9 = this.getBestIcon((int)((double)var6 * var7));
         int var10;
         if (var9 != null && (var10 = var9.getIconWidth()) > 0) {
            double var11 = (double)var6 / (double)var10;
            var5.translate(var3, var4);
            var5.scale(var11, var11);
            var9.paintIcon(var1, var5, 0, 0);
         }

         var5.dispose();
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 16;
      }
   }

   public class WindowsPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler {
      public WindowsPropertyChangeHandler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("frameIcon".equals(var2) && WindowsInternalFrameTitlePane.this.systemLabel != null) {
            WindowsInternalFrameTitlePane.this.systemLabel.setIcon(WindowsInternalFrameTitlePane.this.frame.getFrameIcon());
         }

         super.propertyChange(var1);
      }
   }

   public class WindowsTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout {
      private Insets captionMargin = null;
      private Insets contentMargin = null;
      private XPStyle xp = XPStyle.getXP();

      WindowsTitlePaneLayout() {
         super();
         if (this.xp != null) {
            this.captionMargin = this.xp.getMargin(WindowsInternalFrameTitlePane.this, TMSchema.Part.WP_CAPTION, (TMSchema.State)null, TMSchema.Prop.CAPTIONMARGINS);
            this.contentMargin = this.xp.getMargin(WindowsInternalFrameTitlePane.this, TMSchema.Part.WP_CAPTION, (TMSchema.State)null, TMSchema.Prop.CONTENTMARGINS);
         }

         if (this.captionMargin == null) {
            this.captionMargin = new Insets(0, 2, 0, 2);
         }

         if (this.contentMargin == null) {
            this.contentMargin = new Insets(0, 0, 0, 0);
         }

      }

      private int layoutButton(JComponent var1, TMSchema.Part var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
         if (!var8) {
            var3 -= var5;
         }

         var1.setBounds(var3, var4, var5, var6);
         if (var8) {
            var3 += var5 + 2;
         } else {
            var3 -= 2;
         }

         return var3;
      }

      public void layoutContainer(Container var1) {
         boolean var2 = WindowsGraphicsUtils.isLeftToRight(WindowsInternalFrameTitlePane.this.frame);
         int var5 = WindowsInternalFrameTitlePane.this.getWidth();
         int var6 = WindowsInternalFrameTitlePane.this.getHeight();
         int var7 = this.xp != null ? (var6 - 2) * 6 / 10 : var6 - 4;
         int var3;
         if (this.xp != null) {
            var3 = var2 ? this.captionMargin.left + 2 : var5 - this.captionMargin.right - 2;
         } else {
            var3 = var2 ? this.captionMargin.left : var5 - this.captionMargin.right;
         }

         int var4 = (var6 - var7) / 2;
         this.layoutButton(WindowsInternalFrameTitlePane.this.systemLabel, TMSchema.Part.WP_SYSBUTTON, var3, var4, var7, var7, 0, var2);
         if (this.xp != null) {
            var3 = var2 ? var5 - this.captionMargin.right - 2 : this.captionMargin.left + 2;
            byte var8 = 1;
            if (WindowsInternalFrameTitlePane.this.frame.isMaximum()) {
               var4 = var8 + 1;
            } else {
               var4 = var8 + 5;
            }
         } else {
            var3 = var2 ? var5 - this.captionMargin.right : this.captionMargin.left;
            var4 = (var6 - WindowsInternalFrameTitlePane.this.buttonHeight) / 2;
         }

         if (WindowsInternalFrameTitlePane.this.frame.isClosable()) {
            var3 = this.layoutButton(WindowsInternalFrameTitlePane.this.closeButton, TMSchema.Part.WP_CLOSEBUTTON, var3, var4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 2, !var2);
         }

         if (WindowsInternalFrameTitlePane.this.frame.isMaximizable()) {
            var3 = this.layoutButton(WindowsInternalFrameTitlePane.this.maxButton, TMSchema.Part.WP_MAXBUTTON, var3, var4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, this.xp != null ? 2 : 0, !var2);
         }

         if (WindowsInternalFrameTitlePane.this.frame.isIconifiable()) {
            this.layoutButton(WindowsInternalFrameTitlePane.this.iconButton, TMSchema.Part.WP_MINBUTTON, var3, var4, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 0, !var2);
         }

      }
   }
}
