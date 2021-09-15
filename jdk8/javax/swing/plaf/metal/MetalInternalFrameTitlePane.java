package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

public class MetalInternalFrameTitlePane extends BasicInternalFrameTitlePane {
   protected boolean isPalette = false;
   protected Icon paletteCloseIcon;
   protected int paletteTitleHeight;
   private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
   private String selectedBackgroundKey;
   private String selectedForegroundKey;
   private String selectedShadowKey;
   private boolean wasClosable;
   int buttonsWidth = 0;
   MetalBumps activeBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.get("InternalFrame.activeTitleGradient") != null ? null : MetalLookAndFeel.getPrimaryControl());
   MetalBumps inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.get("InternalFrame.inactiveTitleGradient") != null ? null : MetalLookAndFeel.getControl());
   MetalBumps paletteBumps;
   private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
   private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();

   public MetalInternalFrameTitlePane(JInternalFrame var1) {
      super(var1);
   }

   public void addNotify() {
      super.addNotify();
      this.updateOptionPaneState();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.setFont(UIManager.getFont("InternalFrame.titleFont"));
      this.paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
      this.paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
      this.wasClosable = this.frame.isClosable();
      this.selectedForegroundKey = this.selectedBackgroundKey = null;
      if (MetalLookAndFeel.usingOcean()) {
         this.setOpaque(true);
      }

   }

   protected void uninstallDefaults() {
      super.uninstallDefaults();
      if (this.wasClosable != this.frame.isClosable()) {
         this.frame.setClosable(this.wasClosable);
      }

   }

   protected void createButtons() {
      super.createButtons();
      Boolean var1 = this.frame.isSelected() ? Boolean.TRUE : Boolean.FALSE;
      this.iconButton.putClientProperty("paintActive", var1);
      this.iconButton.setBorder(handyEmptyBorder);
      this.maxButton.putClientProperty("paintActive", var1);
      this.maxButton.setBorder(handyEmptyBorder);
      this.closeButton.putClientProperty("paintActive", var1);
      this.closeButton.setBorder(handyEmptyBorder);
      this.closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
      if (MetalLookAndFeel.usingOcean()) {
         this.iconButton.setContentAreaFilled(false);
         this.maxButton.setContentAreaFilled(false);
         this.closeButton.setContentAreaFilled(false);
      }

   }

   protected void assembleSystemMenu() {
   }

   protected void addSystemMenuItems(JMenu var1) {
   }

   protected void showSystemMenu() {
   }

   protected void addSubComponents() {
      this.add(this.iconButton);
      this.add(this.maxButton);
      this.add(this.closeButton);
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new MetalInternalFrameTitlePane.MetalPropertyChangeHandler();
   }

   protected LayoutManager createLayout() {
      return new MetalInternalFrameTitlePane.MetalTitlePaneLayout();
   }

   public void paintPalette(Graphics var1) {
      boolean var2 = MetalUtils.isLeftToRight(this.frame);
      int var3 = this.getWidth();
      int var4 = this.getHeight();
      if (this.paletteBumps == null) {
         this.paletteBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlShadow());
      }

      ColorUIResource var5 = MetalLookAndFeel.getPrimaryControlShadow();
      ColorUIResource var6 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      var1.setColor(var5);
      var1.fillRect(0, 0, var3, var4);
      var1.setColor(var6);
      var1.drawLine(0, var4 - 1, var3, var4 - 1);
      int var7 = var2 ? 4 : this.buttonsWidth + 4;
      int var8 = var3 - this.buttonsWidth - 8;
      int var9 = this.getHeight() - 4;
      this.paletteBumps.setBumpArea(var8, var9);
      this.paletteBumps.paintIcon(this, var1, var7, 2);
   }

   public void paintComponent(Graphics var1) {
      if (this.isPalette) {
         this.paintPalette(var1);
      } else {
         boolean var2 = MetalUtils.isLeftToRight(this.frame);
         boolean var3 = this.frame.isSelected();
         int var4 = this.getWidth();
         int var5 = this.getHeight();
         Object var6 = null;
         Object var7 = null;
         Object var8 = null;
         MetalBumps var9;
         String var10;
         if (var3) {
            if (!MetalLookAndFeel.usingOcean()) {
               this.closeButton.setContentAreaFilled(true);
               this.maxButton.setContentAreaFilled(true);
               this.iconButton.setContentAreaFilled(true);
            }

            if (this.selectedBackgroundKey != null) {
               var6 = UIManager.getColor(this.selectedBackgroundKey);
            }

            if (var6 == null) {
               var6 = MetalLookAndFeel.getWindowTitleBackground();
            }

            if (this.selectedForegroundKey != null) {
               var7 = UIManager.getColor(this.selectedForegroundKey);
            }

            if (this.selectedShadowKey != null) {
               var8 = UIManager.getColor(this.selectedShadowKey);
            }

            if (var8 == null) {
               var8 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            }

            if (var7 == null) {
               var7 = MetalLookAndFeel.getWindowTitleForeground();
            }

            this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, (Color)(UIManager.get("InternalFrame.activeTitleGradient") != null ? null : var6));
            var9 = this.activeBumps;
            var10 = "InternalFrame.activeTitleGradient";
         } else {
            if (!MetalLookAndFeel.usingOcean()) {
               this.closeButton.setContentAreaFilled(false);
               this.maxButton.setContentAreaFilled(false);
               this.iconButton.setContentAreaFilled(false);
            }

            var6 = MetalLookAndFeel.getWindowTitleInactiveBackground();
            var7 = MetalLookAndFeel.getWindowTitleInactiveForeground();
            var8 = MetalLookAndFeel.getControlDarkShadow();
            var9 = this.inactiveBumps;
            var10 = "InternalFrame.inactiveTitleGradient";
         }

         if (!MetalUtils.drawGradient(this, var1, var10, 0, 0, var4, var5, true)) {
            var1.setColor((Color)var6);
            var1.fillRect(0, 0, var4, var5);
         }

         var1.setColor((Color)var8);
         var1.drawLine(0, var5 - 1, var4, var5 - 1);
         var1.drawLine(0, 0, 0, 0);
         var1.drawLine(var4 - 1, 0, var4 - 1, 0);
         int var12 = var2 ? 5 : var4 - 5;
         String var13 = this.frame.getTitle();
         Icon var14 = this.frame.getFrameIcon();
         int var15;
         if (var14 != null) {
            if (!var2) {
               var12 -= var14.getIconWidth();
            }

            var15 = var5 / 2 - var14.getIconHeight() / 2;
            var14.paintIcon(this.frame, var1, var12, var15);
            var12 += var2 ? var14.getIconWidth() + 5 : -5;
         }

         int var18;
         if (var13 != null) {
            Font var21 = this.getFont();
            var1.setFont(var21);
            FontMetrics var16 = SwingUtilities2.getFontMetrics(this.frame, var1, var21);
            int var17 = var16.getHeight();
            var1.setColor((Color)var7);
            var18 = (var5 - var16.getHeight()) / 2 + var16.getAscent();
            Rectangle var19 = new Rectangle(0, 0, 0, 0);
            if (this.frame.isIconifiable()) {
               var19 = this.iconButton.getBounds();
            } else if (this.frame.isMaximizable()) {
               var19 = this.maxButton.getBounds();
            } else if (this.frame.isClosable()) {
               var19 = this.closeButton.getBounds();
            }

            int var20;
            if (var2) {
               if (var19.x == 0) {
                  var19.x = this.frame.getWidth() - this.frame.getInsets().right - 2;
               }

               var20 = var19.x - var12 - 4;
               var13 = this.getTitle(var13, var16, var20);
            } else {
               var20 = var12 - var19.x - var19.width - 4;
               var13 = this.getTitle(var13, var16, var20);
               var12 -= SwingUtilities2.stringWidth(this.frame, var16, var13);
            }

            int var11 = SwingUtilities2.stringWidth(this.frame, var16, var13);
            SwingUtilities2.drawString(this.frame, var1, (String)var13, var12, var18);
            var12 += var2 ? var11 + 5 : -5;
         }

         int var22;
         if (var2) {
            var22 = var4 - this.buttonsWidth - var12 - 5;
            var15 = var12;
         } else {
            var22 = var12 - this.buttonsWidth - 5;
            var15 = this.buttonsWidth + 5;
         }

         byte var23 = 3;
         var18 = this.getHeight() - 2 * var23;
         var9.setBumpArea(var22, var18);
         var9.paintIcon(this, var1, var15, var23);
      }
   }

   public void setPalette(boolean var1) {
      this.isPalette = var1;
      if (this.isPalette) {
         this.closeButton.setIcon(this.paletteCloseIcon);
         if (this.frame.isMaximizable()) {
            this.remove(this.maxButton);
         }

         if (this.frame.isIconifiable()) {
            this.remove(this.iconButton);
         }
      } else {
         this.closeButton.setIcon(this.closeIcon);
         if (this.frame.isMaximizable()) {
            this.add(this.maxButton);
         }

         if (this.frame.isIconifiable()) {
            this.add(this.iconButton);
         }
      }

      this.revalidate();
      this.repaint();
   }

   private void updateOptionPaneState() {
      int var1 = -2;
      boolean var2 = this.wasClosable;
      Object var3 = this.frame.getClientProperty("JInternalFrame.messageType");
      if (var3 != null) {
         if (var3 instanceof Integer) {
            var1 = (Integer)var3;
         }

         switch(var1) {
         case -1:
         case 1:
            this.selectedBackgroundKey = this.selectedForegroundKey = this.selectedShadowKey = null;
            var2 = false;
            break;
         case 0:
            this.selectedBackgroundKey = "OptionPane.errorDialog.titlePane.background";
            this.selectedForegroundKey = "OptionPane.errorDialog.titlePane.foreground";
            this.selectedShadowKey = "OptionPane.errorDialog.titlePane.shadow";
            var2 = false;
            break;
         case 2:
            this.selectedBackgroundKey = "OptionPane.warningDialog.titlePane.background";
            this.selectedForegroundKey = "OptionPane.warningDialog.titlePane.foreground";
            this.selectedShadowKey = "OptionPane.warningDialog.titlePane.shadow";
            var2 = false;
            break;
         case 3:
            this.selectedBackgroundKey = "OptionPane.questionDialog.titlePane.background";
            this.selectedForegroundKey = "OptionPane.questionDialog.titlePane.foreground";
            this.selectedShadowKey = "OptionPane.questionDialog.titlePane.shadow";
            var2 = false;
            break;
         default:
            this.selectedBackgroundKey = this.selectedForegroundKey = this.selectedShadowKey = null;
         }

         if (var2 != this.frame.isClosable()) {
            this.frame.setClosable(var2);
         }

      }
   }

   class MetalTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout {
      MetalTitlePaneLayout() {
         super();
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         int var2 = 30;
         if (MetalInternalFrameTitlePane.this.frame.isClosable()) {
            var2 += 21;
         }

         if (MetalInternalFrameTitlePane.this.frame.isMaximizable()) {
            var2 += 16 + (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4);
         }

         if (MetalInternalFrameTitlePane.this.frame.isIconifiable()) {
            var2 += 16 + (MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4));
         }

         FontMetrics var3 = MetalInternalFrameTitlePane.this.frame.getFontMetrics(MetalInternalFrameTitlePane.this.getFont());
         String var4 = MetalInternalFrameTitlePane.this.frame.getTitle();
         int var5 = var4 != null ? SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, var3, var4) : 0;
         int var6 = var4 != null ? var4.length() : 0;
         int var7;
         if (var6 > 2) {
            var7 = SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, var3, MetalInternalFrameTitlePane.this.frame.getTitle().substring(0, 2) + "...");
            var2 += var5 < var7 ? var5 : var7;
         } else {
            var2 += var5;
         }

         if (MetalInternalFrameTitlePane.this.isPalette) {
            var7 = MetalInternalFrameTitlePane.this.paletteTitleHeight;
         } else {
            int var8 = var3.getHeight();
            var8 += 7;
            Icon var9 = MetalInternalFrameTitlePane.this.frame.getFrameIcon();
            int var10 = 0;
            if (var9 != null) {
               var10 = Math.min(var9.getIconHeight(), 16);
            }

            var10 += 5;
            var7 = Math.max(var8, var10);
         }

         return new Dimension(var2, var7);
      }

      public void layoutContainer(Container var1) {
         boolean var2 = MetalUtils.isLeftToRight(MetalInternalFrameTitlePane.this.frame);
         int var3 = MetalInternalFrameTitlePane.this.getWidth();
         int var4 = var2 ? var3 : 0;
         byte var5 = 2;
         int var7 = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
         int var8 = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconWidth();
         if (MetalInternalFrameTitlePane.this.frame.isClosable()) {
            byte var6;
            if (MetalInternalFrameTitlePane.this.isPalette) {
               var6 = 3;
               var4 += var2 ? -var6 - (var8 + 2) : var6;
               MetalInternalFrameTitlePane.this.closeButton.setBounds(var4, var5, var8 + 2, MetalInternalFrameTitlePane.this.getHeight() - 4);
               if (!var2) {
                  var4 += var8 + 2;
               }
            } else {
               var6 = 4;
               var4 += var2 ? -var6 - var8 : var6;
               MetalInternalFrameTitlePane.this.closeButton.setBounds(var4, var5, var8, var7);
               if (!var2) {
                  var4 += var8;
               }
            }
         }

         int var9;
         if (MetalInternalFrameTitlePane.this.frame.isMaximizable() && !MetalInternalFrameTitlePane.this.isPalette) {
            var9 = MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4;
            var4 += var2 ? -var9 - var8 : var9;
            MetalInternalFrameTitlePane.this.maxButton.setBounds(var4, var5, var8, var7);
            if (!var2) {
               var4 += var8;
            }
         }

         if (MetalInternalFrameTitlePane.this.frame.isIconifiable() && !MetalInternalFrameTitlePane.this.isPalette) {
            var9 = MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4);
            var4 += var2 ? -var9 - var8 : var9;
            MetalInternalFrameTitlePane.this.iconButton.setBounds(var4, var5, var8, var7);
            if (!var2) {
               var4 += var8;
            }
         }

         MetalInternalFrameTitlePane.this.buttonsWidth = var2 ? var3 - var4 : var4;
      }
   }

   class MetalPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler {
      MetalPropertyChangeHandler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("selected")) {
            Boolean var3 = (Boolean)var1.getNewValue();
            MetalInternalFrameTitlePane.this.iconButton.putClientProperty("paintActive", var3);
            MetalInternalFrameTitlePane.this.closeButton.putClientProperty("paintActive", var3);
            MetalInternalFrameTitlePane.this.maxButton.putClientProperty("paintActive", var3);
         } else if ("JInternalFrame.messageType".equals(var2)) {
            MetalInternalFrameTitlePane.this.updateOptionPaneState();
            MetalInternalFrameTitlePane.this.frame.repaint();
         }

         super.propertyChange(var1);
      }
   }
}
