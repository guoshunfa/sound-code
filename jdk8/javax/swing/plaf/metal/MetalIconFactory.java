package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import sun.swing.CachedPainter;

public class MetalIconFactory implements Serializable {
   private static Icon fileChooserDetailViewIcon;
   private static Icon fileChooserHomeFolderIcon;
   private static Icon fileChooserListViewIcon;
   private static Icon fileChooserNewFolderIcon;
   private static Icon fileChooserUpFolderIcon;
   private static Icon internalFrameAltMaximizeIcon;
   private static Icon internalFrameCloseIcon;
   private static Icon internalFrameDefaultMenuIcon;
   private static Icon internalFrameMaximizeIcon;
   private static Icon internalFrameMinimizeIcon;
   private static Icon radioButtonIcon;
   private static Icon treeComputerIcon;
   private static Icon treeFloppyDriveIcon;
   private static Icon treeHardDriveIcon;
   private static Icon menuArrowIcon;
   private static Icon menuItemArrowIcon;
   private static Icon checkBoxMenuItemIcon;
   private static Icon radioButtonMenuItemIcon;
   private static Icon checkBoxIcon;
   private static Icon oceanHorizontalSliderThumb;
   private static Icon oceanVerticalSliderThumb;
   public static final boolean DARK = false;
   public static final boolean LIGHT = true;
   private static final Dimension folderIcon16Size = new Dimension(16, 16);
   private static final Dimension fileIcon16Size = new Dimension(16, 16);
   private static final Dimension treeControlSize = new Dimension(18, 18);
   private static final Dimension menuArrowIconSize = new Dimension(4, 8);
   private static final Dimension menuCheckIconSize = new Dimension(10, 10);
   private static final int xOff = 4;

   public static Icon getFileChooserDetailViewIcon() {
      if (fileChooserDetailViewIcon == null) {
         fileChooserDetailViewIcon = new MetalIconFactory.FileChooserDetailViewIcon();
      }

      return fileChooserDetailViewIcon;
   }

   public static Icon getFileChooserHomeFolderIcon() {
      if (fileChooserHomeFolderIcon == null) {
         fileChooserHomeFolderIcon = new MetalIconFactory.FileChooserHomeFolderIcon();
      }

      return fileChooserHomeFolderIcon;
   }

   public static Icon getFileChooserListViewIcon() {
      if (fileChooserListViewIcon == null) {
         fileChooserListViewIcon = new MetalIconFactory.FileChooserListViewIcon();
      }

      return fileChooserListViewIcon;
   }

   public static Icon getFileChooserNewFolderIcon() {
      if (fileChooserNewFolderIcon == null) {
         fileChooserNewFolderIcon = new MetalIconFactory.FileChooserNewFolderIcon();
      }

      return fileChooserNewFolderIcon;
   }

   public static Icon getFileChooserUpFolderIcon() {
      if (fileChooserUpFolderIcon == null) {
         fileChooserUpFolderIcon = new MetalIconFactory.FileChooserUpFolderIcon();
      }

      return fileChooserUpFolderIcon;
   }

   public static Icon getInternalFrameAltMaximizeIcon(int var0) {
      return new MetalIconFactory.InternalFrameAltMaximizeIcon(var0);
   }

   public static Icon getInternalFrameCloseIcon(int var0) {
      return new MetalIconFactory.InternalFrameCloseIcon(var0);
   }

   public static Icon getInternalFrameDefaultMenuIcon() {
      if (internalFrameDefaultMenuIcon == null) {
         internalFrameDefaultMenuIcon = new MetalIconFactory.InternalFrameDefaultMenuIcon();
      }

      return internalFrameDefaultMenuIcon;
   }

   public static Icon getInternalFrameMaximizeIcon(int var0) {
      return new MetalIconFactory.InternalFrameMaximizeIcon(var0);
   }

   public static Icon getInternalFrameMinimizeIcon(int var0) {
      return new MetalIconFactory.InternalFrameMinimizeIcon(var0);
   }

   public static Icon getRadioButtonIcon() {
      if (radioButtonIcon == null) {
         radioButtonIcon = new MetalIconFactory.RadioButtonIcon();
      }

      return radioButtonIcon;
   }

   public static Icon getCheckBoxIcon() {
      if (checkBoxIcon == null) {
         checkBoxIcon = new MetalIconFactory.CheckBoxIcon();
      }

      return checkBoxIcon;
   }

   public static Icon getTreeComputerIcon() {
      if (treeComputerIcon == null) {
         treeComputerIcon = new MetalIconFactory.TreeComputerIcon();
      }

      return treeComputerIcon;
   }

   public static Icon getTreeFloppyDriveIcon() {
      if (treeFloppyDriveIcon == null) {
         treeFloppyDriveIcon = new MetalIconFactory.TreeFloppyDriveIcon();
      }

      return treeFloppyDriveIcon;
   }

   public static Icon getTreeFolderIcon() {
      return new MetalIconFactory.TreeFolderIcon();
   }

   public static Icon getTreeHardDriveIcon() {
      if (treeHardDriveIcon == null) {
         treeHardDriveIcon = new MetalIconFactory.TreeHardDriveIcon();
      }

      return treeHardDriveIcon;
   }

   public static Icon getTreeLeafIcon() {
      return new MetalIconFactory.TreeLeafIcon();
   }

   public static Icon getTreeControlIcon(boolean var0) {
      return new MetalIconFactory.TreeControlIcon(var0);
   }

   public static Icon getMenuArrowIcon() {
      if (menuArrowIcon == null) {
         menuArrowIcon = new MetalIconFactory.MenuArrowIcon();
      }

      return menuArrowIcon;
   }

   public static Icon getMenuItemCheckIcon() {
      return null;
   }

   public static Icon getMenuItemArrowIcon() {
      if (menuItemArrowIcon == null) {
         menuItemArrowIcon = new MetalIconFactory.MenuItemArrowIcon();
      }

      return menuItemArrowIcon;
   }

   public static Icon getCheckBoxMenuItemIcon() {
      if (checkBoxMenuItemIcon == null) {
         checkBoxMenuItemIcon = new MetalIconFactory.CheckBoxMenuItemIcon();
      }

      return checkBoxMenuItemIcon;
   }

   public static Icon getRadioButtonMenuItemIcon() {
      if (radioButtonMenuItemIcon == null) {
         radioButtonMenuItemIcon = new MetalIconFactory.RadioButtonMenuItemIcon();
      }

      return radioButtonMenuItemIcon;
   }

   public static Icon getHorizontalSliderThumbIcon() {
      if (MetalLookAndFeel.usingOcean()) {
         if (oceanHorizontalSliderThumb == null) {
            oceanHorizontalSliderThumb = new MetalIconFactory.OceanHorizontalSliderThumbIcon();
         }

         return oceanHorizontalSliderThumb;
      } else {
         return new MetalIconFactory.HorizontalSliderThumbIcon();
      }
   }

   public static Icon getVerticalSliderThumbIcon() {
      if (MetalLookAndFeel.usingOcean()) {
         if (oceanVerticalSliderThumb == null) {
            oceanVerticalSliderThumb = new MetalIconFactory.OceanVerticalSliderThumbIcon();
         }

         return oceanVerticalSliderThumb;
      } else {
         return new MetalIconFactory.VerticalSliderThumbIcon();
      }
   }

   private static class OceanHorizontalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource {
      private static Polygon THUMB_SHAPE = new Polygon(new int[]{0, 14, 14, 7, 0}, new int[]{0, 0, 8, 15, 8}, 5);

      OceanHorizontalSliderThumbIcon() {
         super(3);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (var2 instanceof Graphics2D) {
            this.paint(var1, var2, var3, var4, this.getIconWidth(), this.getIconHeight(), new Object[]{var1.hasFocus(), var1.isEnabled(), MetalLookAndFeel.getCurrentTheme()});
         }
      }

      protected Image createImage(Component var1, int var2, int var3, GraphicsConfiguration var4, Object[] var5) {
         return var4 == null ? new BufferedImage(var2, var3, 2) : var4.createCompatibleImage(var2, var3, 2);
      }

      protected void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6) {
         Graphics2D var7 = (Graphics2D)var3;
         boolean var8 = (Boolean)var6[0];
         boolean var9 = (Boolean)var6[1];
         Rectangle var10 = var7.getClipBounds();
         var7.clip(THUMB_SHAPE);
         if (!var9) {
            var7.setColor(MetalLookAndFeel.getControl());
            var7.fillRect(1, 1, 13, 14);
         } else if (var8) {
            MetalUtils.drawGradient(var1, var7, "Slider.focusGradient", 1, 1, 13, 14, true);
         } else {
            MetalUtils.drawGradient(var1, var7, "Slider.gradient", 1, 1, 13, 14, true);
         }

         var7.setClip(var10);
         if (var8) {
            var7.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         } else {
            var7.setColor(var9 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
         }

         var7.drawLine(1, 0, 13, 0);
         var7.drawLine(0, 1, 0, 8);
         var7.drawLine(14, 1, 14, 8);
         var7.drawLine(1, 9, 7, 15);
         var7.drawLine(7, 15, 14, 8);
         if (var8 && var9) {
            var7.setColor(MetalLookAndFeel.getPrimaryControl());
            var7.fillRect(1, 1, 13, 1);
            var7.fillRect(1, 2, 1, 7);
            var7.fillRect(13, 2, 1, 7);
            var7.drawLine(2, 9, 7, 14);
            var7.drawLine(8, 13, 12, 9);
         }

      }

      public int getIconWidth() {
         return 15;
      }

      public int getIconHeight() {
         return 16;
      }
   }

   private static class OceanVerticalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource {
      private static Polygon LTR_THUMB_SHAPE = new Polygon(new int[]{0, 8, 15, 8, 0}, new int[]{0, 0, 7, 14, 14}, 5);
      private static Polygon RTL_THUMB_SHAPE = new Polygon(new int[]{15, 15, 7, 0, 7}, new int[]{0, 14, 14, 7, 0}, 5);

      OceanVerticalSliderThumbIcon() {
         super(3);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (var2 instanceof Graphics2D) {
            this.paint(var1, var2, var3, var4, this.getIconWidth(), this.getIconHeight(), new Object[]{MetalUtils.isLeftToRight(var1), var1.hasFocus(), var1.isEnabled(), MetalLookAndFeel.getCurrentTheme()});
         }
      }

      protected void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6) {
         Graphics2D var7 = (Graphics2D)var3;
         boolean var8 = (Boolean)var6[0];
         boolean var9 = (Boolean)var6[1];
         boolean var10 = (Boolean)var6[2];
         Rectangle var11 = var7.getClipBounds();
         if (var8) {
            var7.clip(LTR_THUMB_SHAPE);
         } else {
            var7.clip(RTL_THUMB_SHAPE);
         }

         if (!var10) {
            var7.setColor(MetalLookAndFeel.getControl());
            var7.fillRect(1, 1, 14, 14);
         } else if (var9) {
            MetalUtils.drawGradient(var1, var7, "Slider.focusGradient", 1, 1, 14, 14, false);
         } else {
            MetalUtils.drawGradient(var1, var7, "Slider.gradient", 1, 1, 14, 14, false);
         }

         var7.setClip(var11);
         if (var9) {
            var7.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         } else {
            var7.setColor(var10 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
         }

         if (var8) {
            var7.drawLine(1, 0, 8, 0);
            var7.drawLine(0, 1, 0, 13);
            var7.drawLine(1, 14, 8, 14);
            var7.drawLine(9, 1, 15, 7);
            var7.drawLine(9, 13, 15, 7);
         } else {
            var7.drawLine(7, 0, 14, 0);
            var7.drawLine(15, 1, 15, 13);
            var7.drawLine(7, 14, 14, 14);
            var7.drawLine(0, 7, 6, 1);
            var7.drawLine(0, 7, 6, 13);
         }

         if (var9 && var10) {
            var7.setColor(MetalLookAndFeel.getPrimaryControl());
            if (var8) {
               var7.drawLine(1, 1, 8, 1);
               var7.drawLine(1, 1, 1, 13);
               var7.drawLine(1, 13, 8, 13);
               var7.drawLine(9, 2, 14, 7);
               var7.drawLine(9, 12, 14, 7);
            } else {
               var7.drawLine(7, 1, 14, 1);
               var7.drawLine(14, 1, 14, 13);
               var7.drawLine(7, 13, 14, 13);
               var7.drawLine(1, 7, 7, 1);
               var7.drawLine(1, 7, 7, 13);
            }
         }

      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 15;
      }

      protected Image createImage(Component var1, int var2, int var3, GraphicsConfiguration var4, Object[] var5) {
         return var4 == null ? new BufferedImage(var2, var3, 2) : var4.createCompatibleImage(var2, var3, 2);
      }
   }

   private static class HorizontalSliderThumbIcon implements Icon, Serializable, UIResource {
      protected static MetalBumps controlBumps;
      protected static MetalBumps primaryBumps;

      public HorizontalSliderThumbIcon() {
         controlBumps = new MetalBumps(10, 6, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
         primaryBumps = new MetalBumps(10, 6, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         if (var1.hasFocus()) {
            var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         } else {
            var2.setColor(var1.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
         }

         var2.drawLine(1, 0, 13, 0);
         var2.drawLine(0, 1, 0, 8);
         var2.drawLine(14, 1, 14, 8);
         var2.drawLine(1, 9, 7, 15);
         var2.drawLine(7, 15, 14, 8);
         if (var1.hasFocus()) {
            var2.setColor(var1.getForeground());
         } else {
            var2.setColor(MetalLookAndFeel.getControl());
         }

         var2.fillRect(1, 1, 13, 8);
         var2.drawLine(2, 9, 12, 9);
         var2.drawLine(3, 10, 11, 10);
         var2.drawLine(4, 11, 10, 11);
         var2.drawLine(5, 12, 9, 12);
         var2.drawLine(6, 13, 8, 13);
         var2.drawLine(7, 14, 7, 14);
         if (var1.isEnabled()) {
            if (var1.hasFocus()) {
               primaryBumps.paintIcon(var1, var2, 2, 2);
            } else {
               controlBumps.paintIcon(var1, var2, 2, 2);
            }
         }

         if (var1.isEnabled()) {
            var2.setColor(var1.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
            var2.drawLine(1, 1, 13, 1);
            var2.drawLine(1, 1, 1, 8);
         }

         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 15;
      }

      public int getIconHeight() {
         return 16;
      }
   }

   private static class VerticalSliderThumbIcon implements Icon, Serializable, UIResource {
      protected static MetalBumps controlBumps;
      protected static MetalBumps primaryBumps;

      public VerticalSliderThumbIcon() {
         controlBumps = new MetalBumps(6, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
         primaryBumps = new MetalBumps(6, 10, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         boolean var5 = MetalUtils.isLeftToRight(var1);
         var2.translate(var3, var4);
         if (var1.hasFocus()) {
            var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         } else {
            var2.setColor(var1.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
         }

         if (var5) {
            var2.drawLine(1, 0, 8, 0);
            var2.drawLine(0, 1, 0, 13);
            var2.drawLine(1, 14, 8, 14);
            var2.drawLine(9, 1, 15, 7);
            var2.drawLine(9, 13, 15, 7);
         } else {
            var2.drawLine(7, 0, 14, 0);
            var2.drawLine(15, 1, 15, 13);
            var2.drawLine(7, 14, 14, 14);
            var2.drawLine(0, 7, 6, 1);
            var2.drawLine(0, 7, 6, 13);
         }

         if (var1.hasFocus()) {
            var2.setColor(var1.getForeground());
         } else {
            var2.setColor(MetalLookAndFeel.getControl());
         }

         if (var5) {
            var2.fillRect(1, 1, 8, 13);
            var2.drawLine(9, 2, 9, 12);
            var2.drawLine(10, 3, 10, 11);
            var2.drawLine(11, 4, 11, 10);
            var2.drawLine(12, 5, 12, 9);
            var2.drawLine(13, 6, 13, 8);
            var2.drawLine(14, 7, 14, 7);
         } else {
            var2.fillRect(7, 1, 8, 13);
            var2.drawLine(6, 3, 6, 12);
            var2.drawLine(5, 4, 5, 11);
            var2.drawLine(4, 5, 4, 10);
            var2.drawLine(3, 6, 3, 9);
            var2.drawLine(2, 7, 2, 8);
         }

         int var6 = var5 ? 2 : 8;
         if (var1.isEnabled()) {
            if (var1.hasFocus()) {
               primaryBumps.paintIcon(var1, var2, var6, 2);
            } else {
               controlBumps.paintIcon(var1, var2, var6, 2);
            }
         }

         if (var1.isEnabled()) {
            var2.setColor(var1.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
            if (var5) {
               var2.drawLine(1, 1, 8, 1);
               var2.drawLine(1, 1, 1, 13);
            } else {
               var2.drawLine(8, 1, 14, 1);
               var2.drawLine(1, 7, 7, 1);
            }
         }

         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 15;
      }
   }

   private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
      private RadioButtonMenuItemIcon() {
      }

      public void paintOceanIcon(Component var1, Graphics var2, int var3, int var4) {
         ButtonModel var5 = ((JMenuItem)var1).getModel();
         boolean var6 = var5.isSelected();
         boolean var7 = var5.isEnabled();
         boolean var8 = var5.isPressed();
         boolean var9 = var5.isArmed();
         var2.translate(var3, var4);
         if (var7) {
            MetalUtils.drawGradient(var1, var2, "RadioButtonMenuItem.gradient", 1, 1, 7, 7, true);
            if (!var8 && !var9) {
               var2.setColor(MetalLookAndFeel.getControlHighlight());
            } else {
               var2.setColor(MetalLookAndFeel.getPrimaryControl());
            }

            var2.drawLine(2, 9, 7, 9);
            var2.drawLine(9, 2, 9, 7);
            var2.drawLine(8, 8, 8, 8);
            if (!var8 && !var9) {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            } else {
               var2.setColor(MetalLookAndFeel.getControlInfo());
            }
         } else {
            var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
         }

         var2.drawLine(2, 0, 6, 0);
         var2.drawLine(2, 8, 6, 8);
         var2.drawLine(0, 2, 0, 6);
         var2.drawLine(8, 2, 8, 6);
         var2.drawLine(1, 1, 1, 1);
         var2.drawLine(7, 1, 7, 1);
         var2.drawLine(1, 7, 1, 7);
         var2.drawLine(7, 7, 7, 7);
         if (var6) {
            if (!var7) {
               var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            } else if (!var9 && (!(var1 instanceof JMenu) || !var5.isSelected())) {
               var2.setColor(MetalLookAndFeel.getControlInfo());
            } else {
               var2.setColor(MetalLookAndFeel.getMenuSelectedForeground());
            }

            var2.drawLine(3, 2, 5, 2);
            var2.drawLine(2, 3, 6, 3);
            var2.drawLine(2, 4, 6, 4);
            var2.drawLine(2, 5, 6, 5);
            var2.drawLine(3, 6, 5, 6);
         }

         var2.translate(-var3, -var4);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (MetalLookAndFeel.usingOcean()) {
            this.paintOceanIcon(var1, var2, var3, var4);
         } else {
            JMenuItem var5 = (JMenuItem)var1;
            ButtonModel var6 = var5.getModel();
            boolean var7 = var6.isSelected();
            boolean var8 = var6.isEnabled();
            boolean var9 = var6.isPressed();
            boolean var10 = var6.isArmed();
            var2.translate(var3, var4);
            if (var8) {
               if (!var9 && !var10) {
                  var2.setColor(MetalLookAndFeel.getControlHighlight());
                  var2.drawLine(3, 1, 8, 1);
                  var2.drawLine(2, 9, 7, 9);
                  var2.drawLine(1, 3, 1, 8);
                  var2.drawLine(9, 2, 9, 7);
                  var2.drawLine(2, 2, 2, 2);
                  var2.drawLine(8, 8, 8, 8);
                  var2.setColor(MetalLookAndFeel.getControlDarkShadow());
                  var2.drawLine(2, 0, 6, 0);
                  var2.drawLine(2, 8, 6, 8);
                  var2.drawLine(0, 2, 0, 6);
                  var2.drawLine(8, 2, 8, 6);
                  var2.drawLine(1, 1, 1, 1);
                  var2.drawLine(7, 1, 7, 1);
                  var2.drawLine(1, 7, 1, 7);
                  var2.drawLine(7, 7, 7, 7);
               } else {
                  var2.setColor(MetalLookAndFeel.getPrimaryControl());
                  var2.drawLine(3, 1, 8, 1);
                  var2.drawLine(2, 9, 7, 9);
                  var2.drawLine(1, 3, 1, 8);
                  var2.drawLine(9, 2, 9, 7);
                  var2.drawLine(2, 2, 2, 2);
                  var2.drawLine(8, 8, 8, 8);
                  var2.setColor(MetalLookAndFeel.getControlInfo());
                  var2.drawLine(2, 0, 6, 0);
                  var2.drawLine(2, 8, 6, 8);
                  var2.drawLine(0, 2, 0, 6);
                  var2.drawLine(8, 2, 8, 6);
                  var2.drawLine(1, 1, 1, 1);
                  var2.drawLine(7, 1, 7, 1);
                  var2.drawLine(1, 7, 1, 7);
                  var2.drawLine(7, 7, 7, 7);
               }
            } else {
               var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
               var2.drawLine(2, 0, 6, 0);
               var2.drawLine(2, 8, 6, 8);
               var2.drawLine(0, 2, 0, 6);
               var2.drawLine(8, 2, 8, 6);
               var2.drawLine(1, 1, 1, 1);
               var2.drawLine(7, 1, 7, 1);
               var2.drawLine(1, 7, 1, 7);
               var2.drawLine(7, 7, 7, 7);
            }

            if (var7) {
               if (!var8) {
                  var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
               } else if (!var6.isArmed() && (!(var1 instanceof JMenu) || !var6.isSelected())) {
                  var2.setColor(var5.getForeground());
               } else {
                  var2.setColor(MetalLookAndFeel.getMenuSelectedForeground());
               }

               var2.drawLine(3, 2, 5, 2);
               var2.drawLine(2, 3, 6, 3);
               var2.drawLine(2, 4, 6, 4);
               var2.drawLine(2, 5, 6, 5);
               var2.drawLine(3, 6, 5, 6);
            }

            var2.translate(-var3, -var4);
         }
      }

      public int getIconWidth() {
         return MetalIconFactory.menuCheckIconSize.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.menuCheckIconSize.height;
      }

      // $FF: synthetic method
      RadioButtonMenuItemIcon(Object var1) {
         this();
      }
   }

   private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {
      private CheckBoxMenuItemIcon() {
      }

      public void paintOceanIcon(Component var1, Graphics var2, int var3, int var4) {
         ButtonModel var5 = ((JMenuItem)var1).getModel();
         boolean var6 = var5.isSelected();
         boolean var7 = var5.isEnabled();
         boolean var8 = var5.isPressed();
         boolean var9 = var5.isArmed();
         var2.translate(var3, var4);
         if (var7) {
            MetalUtils.drawGradient(var1, var2, "CheckBoxMenuItem.gradient", 1, 1, 7, 7, true);
            if (!var8 && !var9) {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawLine(0, 0, 8, 0);
               var2.drawLine(0, 0, 0, 8);
               var2.drawLine(8, 2, 8, 8);
               var2.drawLine(2, 8, 8, 8);
               var2.setColor(MetalLookAndFeel.getControlHighlight());
               var2.drawLine(9, 1, 9, 9);
               var2.drawLine(1, 9, 9, 9);
            } else {
               var2.setColor(MetalLookAndFeel.getControlInfo());
               var2.drawLine(0, 0, 8, 0);
               var2.drawLine(0, 0, 0, 8);
               var2.drawLine(8, 2, 8, 8);
               var2.drawLine(2, 8, 8, 8);
               var2.setColor(MetalLookAndFeel.getPrimaryControl());
               var2.drawLine(9, 1, 9, 9);
               var2.drawLine(1, 9, 9, 9);
            }
         } else {
            var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            var2.drawRect(0, 0, 8, 8);
         }

         if (var6) {
            if (!var7) {
               var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            } else if (!var9 && (!(var1 instanceof JMenu) || !var6)) {
               var2.setColor(MetalLookAndFeel.getControlInfo());
            } else {
               var2.setColor(MetalLookAndFeel.getMenuSelectedForeground());
            }

            var2.drawLine(2, 2, 2, 6);
            var2.drawLine(3, 2, 3, 6);
            var2.drawLine(4, 4, 8, 0);
            var2.drawLine(4, 5, 9, 0);
         }

         var2.translate(-var3, -var4);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (MetalLookAndFeel.usingOcean()) {
            this.paintOceanIcon(var1, var2, var3, var4);
         } else {
            JMenuItem var5 = (JMenuItem)var1;
            ButtonModel var6 = var5.getModel();
            boolean var7 = var6.isSelected();
            boolean var8 = var6.isEnabled();
            boolean var9 = var6.isPressed();
            boolean var10 = var6.isArmed();
            var2.translate(var3, var4);
            if (var8) {
               if (!var9 && !var10) {
                  var2.setColor(MetalLookAndFeel.getControlDarkShadow());
                  var2.drawLine(0, 0, 8, 0);
                  var2.drawLine(0, 0, 0, 8);
                  var2.drawLine(8, 2, 8, 8);
                  var2.drawLine(2, 8, 8, 8);
                  var2.setColor(MetalLookAndFeel.getControlHighlight());
                  var2.drawLine(1, 1, 7, 1);
                  var2.drawLine(1, 1, 1, 7);
                  var2.drawLine(9, 1, 9, 9);
                  var2.drawLine(1, 9, 9, 9);
               } else {
                  var2.setColor(MetalLookAndFeel.getControlInfo());
                  var2.drawLine(0, 0, 8, 0);
                  var2.drawLine(0, 0, 0, 8);
                  var2.drawLine(8, 2, 8, 8);
                  var2.drawLine(2, 8, 8, 8);
                  var2.setColor(MetalLookAndFeel.getPrimaryControl());
                  var2.drawLine(1, 1, 7, 1);
                  var2.drawLine(1, 1, 1, 7);
                  var2.drawLine(9, 1, 9, 9);
                  var2.drawLine(1, 9, 9, 9);
               }
            } else {
               var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
               var2.drawRect(0, 0, 8, 8);
            }

            if (var7) {
               if (!var8) {
                  var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
               } else if (!var6.isArmed() && (!(var1 instanceof JMenu) || !var6.isSelected())) {
                  var2.setColor(var5.getForeground());
               } else {
                  var2.setColor(MetalLookAndFeel.getMenuSelectedForeground());
               }

               var2.drawLine(2, 2, 2, 6);
               var2.drawLine(3, 2, 3, 6);
               var2.drawLine(4, 4, 8, 0);
               var2.drawLine(4, 5, 9, 0);
            }

            var2.translate(-var3, -var4);
         }
      }

      public int getIconWidth() {
         return MetalIconFactory.menuCheckIconSize.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.menuCheckIconSize.height;
      }

      // $FF: synthetic method
      CheckBoxMenuItemIcon(Object var1) {
         this();
      }
   }

   private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
      private MenuItemArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return MetalIconFactory.menuArrowIconSize.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.menuArrowIconSize.height;
      }

      // $FF: synthetic method
      MenuItemArrowIcon(Object var1) {
         this();
      }
   }

   private static class MenuArrowIcon implements Icon, UIResource, Serializable {
      private MenuArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JMenuItem var5 = (JMenuItem)var1;
         ButtonModel var6 = var5.getModel();
         var2.translate(var3, var4);
         if (!var6.isEnabled()) {
            var2.setColor(MetalLookAndFeel.getMenuDisabledForeground());
         } else if (!var6.isArmed() && (!(var1 instanceof JMenu) || !var6.isSelected())) {
            var2.setColor(var5.getForeground());
         } else {
            var2.setColor(MetalLookAndFeel.getMenuSelectedForeground());
         }

         if (MetalUtils.isLeftToRight(var5)) {
            var2.drawLine(0, 0, 0, 7);
            var2.drawLine(1, 1, 1, 6);
            var2.drawLine(2, 2, 2, 5);
            var2.drawLine(3, 3, 3, 4);
         } else {
            var2.drawLine(4, 0, 4, 7);
            var2.drawLine(3, 1, 3, 6);
            var2.drawLine(2, 2, 2, 5);
            var2.drawLine(1, 3, 1, 4);
         }

         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return MetalIconFactory.menuArrowIconSize.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.menuArrowIconSize.height;
      }

      // $FF: synthetic method
      MenuArrowIcon(Object var1) {
         this();
      }
   }

   public static class TreeControlIcon implements Icon, Serializable {
      protected boolean isLight;
      MetalIconFactory.ImageCacher imageCacher;
      transient boolean cachedOrientation = true;

      public TreeControlIcon(boolean var1) {
         this.isLight = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         GraphicsConfiguration var5 = var1.getGraphicsConfiguration();
         if (this.imageCacher == null) {
            this.imageCacher = new MetalIconFactory.ImageCacher();
         }

         Object var6 = this.imageCacher.getImage(var5);
         if (var6 == null || this.cachedOrientation != MetalUtils.isLeftToRight(var1)) {
            this.cachedOrientation = MetalUtils.isLeftToRight(var1);
            if (var5 != null) {
               var6 = var5.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
            } else {
               var6 = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
            }

            Graphics var7 = ((Image)var6).getGraphics();
            this.paintMe(var1, var7, var3, var4);
            var7.dispose();
            this.imageCacher.cacheImage((Image)var6, var5);
         }

         if (MetalUtils.isLeftToRight(var1)) {
            if (this.isLight) {
               var2.drawImage((Image)var6, var3 + 5, var4 + 3, var3 + 18, var4 + 13, 4, 3, 17, 13, (ImageObserver)null);
            } else {
               var2.drawImage((Image)var6, var3 + 5, var4 + 3, var3 + 18, var4 + 17, 4, 3, 17, 17, (ImageObserver)null);
            }
         } else if (this.isLight) {
            var2.drawImage((Image)var6, var3 + 3, var4 + 3, var3 + 16, var4 + 13, 4, 3, 17, 13, (ImageObserver)null);
         } else {
            var2.drawImage((Image)var6, var3 + 3, var4 + 3, var3 + 16, var4 + 17, 4, 3, 17, 17, (ImageObserver)null);
         }

      }

      public void paintMe(Component var1, Graphics var2, int var3, int var4) {
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         int var5 = MetalUtils.isLeftToRight(var1) ? 0 : 4;
         var2.drawLine(var5 + 4, 6, var5 + 4, 9);
         var2.drawLine(var5 + 5, 5, var5 + 5, 5);
         var2.drawLine(var5 + 6, 4, var5 + 9, 4);
         var2.drawLine(var5 + 10, 5, var5 + 10, 5);
         var2.drawLine(var5 + 11, 6, var5 + 11, 9);
         var2.drawLine(var5 + 10, 10, var5 + 10, 10);
         var2.drawLine(var5 + 6, 11, var5 + 9, 11);
         var2.drawLine(var5 + 5, 10, var5 + 5, 10);
         var2.drawLine(var5 + 7, 7, var5 + 8, 7);
         var2.drawLine(var5 + 7, 8, var5 + 8, 8);
         if (this.isLight) {
            if (MetalUtils.isLeftToRight(var1)) {
               var2.drawLine(12, 7, 15, 7);
               var2.drawLine(12, 8, 15, 8);
            } else {
               var2.drawLine(4, 7, 7, 7);
               var2.drawLine(4, 8, 7, 8);
            }
         } else {
            var2.drawLine(var5 + 7, 12, var5 + 7, 15);
            var2.drawLine(var5 + 8, 12, var5 + 8, 15);
         }

         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawLine(var5 + 5, 6, var5 + 5, 9);
         var2.drawLine(var5 + 6, 5, var5 + 9, 5);
         var2.setColor(MetalLookAndFeel.getPrimaryControlShadow());
         var2.drawLine(var5 + 6, 6, var5 + 6, 6);
         var2.drawLine(var5 + 9, 6, var5 + 9, 6);
         var2.drawLine(var5 + 6, 9, var5 + 6, 9);
         var2.drawLine(var5 + 10, 6, var5 + 10, 9);
         var2.drawLine(var5 + 6, 10, var5 + 9, 10);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.drawLine(var5 + 6, 7, var5 + 6, 8);
         var2.drawLine(var5 + 7, 6, var5 + 8, 6);
         var2.drawLine(var5 + 9, 7, var5 + 9, 7);
         var2.drawLine(var5 + 7, 9, var5 + 7, 9);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(var5 + 8, 9, var5 + 9, 9);
         var2.drawLine(var5 + 9, 8, var5 + 9, 8);
      }

      public int getIconWidth() {
         return MetalIconFactory.treeControlSize.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.treeControlSize.height;
      }
   }

   public static class TreeLeafIcon extends MetalIconFactory.FileIcon16 {
      public int getShift() {
         return 2;
      }

      public int getAdditionalHeight() {
         return 4;
      }
   }

   public static class FileIcon16 implements Icon, Serializable {
      MetalIconFactory.ImageCacher imageCacher;

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         GraphicsConfiguration var5 = var1.getGraphicsConfiguration();
         if (this.imageCacher == null) {
            this.imageCacher = new MetalIconFactory.ImageCacher();
         }

         Object var6 = this.imageCacher.getImage(var5);
         if (var6 == null) {
            if (var5 != null) {
               var6 = var5.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
            } else {
               var6 = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
            }

            Graphics var7 = ((Image)var6).getGraphics();
            this.paintMe(var1, var7);
            var7.dispose();
            this.imageCacher.cacheImage((Image)var6, var5);
         }

         var2.drawImage((Image)var6, var3, var4 + this.getShift(), (ImageObserver)null);
      }

      private void paintMe(Component var1, Graphics var2) {
         int var3 = MetalIconFactory.fileIcon16Size.width - 1;
         int var4 = MetalIconFactory.fileIcon16Size.height - 1;
         var2.setColor(MetalLookAndFeel.getWindowBackground());
         var2.fillRect(4, 2, 9, 12);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(2, 0, 2, var4);
         var2.drawLine(2, 0, var3 - 4, 0);
         var2.drawLine(2, var4, var3 - 1, var4);
         var2.drawLine(var3 - 1, 6, var3 - 1, var4);
         var2.drawLine(var3 - 6, 2, var3 - 2, 6);
         var2.drawLine(var3 - 5, 1, var3 - 4, 1);
         var2.drawLine(var3 - 3, 2, var3 - 3, 3);
         var2.drawLine(var3 - 2, 4, var3 - 2, 5);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.drawLine(3, 1, 3, var4 - 1);
         var2.drawLine(3, 1, var3 - 6, 1);
         var2.drawLine(var3 - 2, 7, var3 - 2, var4 - 1);
         var2.drawLine(var3 - 5, 2, var3 - 3, 4);
         var2.drawLine(3, var4 - 1, var3 - 2, var4 - 1);
      }

      public int getShift() {
         return 0;
      }

      public int getAdditionalHeight() {
         return 0;
      }

      public int getIconWidth() {
         return MetalIconFactory.fileIcon16Size.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.fileIcon16Size.height + this.getAdditionalHeight();
      }
   }

   public static class TreeFolderIcon extends MetalIconFactory.FolderIcon16 {
      public int getShift() {
         return -1;
      }

      public int getAdditionalHeight() {
         return 2;
      }
   }

   public static class FolderIcon16 implements Icon, Serializable {
      MetalIconFactory.ImageCacher imageCacher;

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         GraphicsConfiguration var5 = var1.getGraphicsConfiguration();
         if (this.imageCacher == null) {
            this.imageCacher = new MetalIconFactory.ImageCacher();
         }

         Object var6 = this.imageCacher.getImage(var5);
         if (var6 == null) {
            if (var5 != null) {
               var6 = var5.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
            } else {
               var6 = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
            }

            Graphics var7 = ((Image)var6).getGraphics();
            this.paintMe(var1, var7);
            var7.dispose();
            this.imageCacher.cacheImage((Image)var6, var5);
         }

         var2.drawImage((Image)var6, var3, var4 + this.getShift(), (ImageObserver)null);
      }

      private void paintMe(Component var1, Graphics var2) {
         int var3 = MetalIconFactory.folderIcon16Size.width - 1;
         int var4 = MetalIconFactory.folderIcon16Size.height - 1;
         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawLine(var3 - 5, 3, var3, 3);
         var2.drawLine(var3 - 6, 4, var3, 4);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.fillRect(2, 7, 13, 8);
         var2.setColor(MetalLookAndFeel.getPrimaryControlShadow());
         var2.drawLine(var3 - 6, 5, var3 - 1, 5);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(0, 6, 0, var4);
         var2.drawLine(1, 5, var3 - 7, 5);
         var2.drawLine(var3 - 6, 6, var3 - 1, 6);
         var2.drawLine(var3, 5, var3, var4);
         var2.drawLine(0, var4, var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(1, 6, 1, var4 - 1);
         var2.drawLine(1, 6, var3 - 7, 6);
         var2.drawLine(var3 - 6, 7, var3 - 1, 7);
      }

      public int getShift() {
         return 0;
      }

      public int getAdditionalHeight() {
         return 0;
      }

      public int getIconWidth() {
         return MetalIconFactory.folderIcon16Size.width;
      }

      public int getIconHeight() {
         return MetalIconFactory.folderIcon16Size.height + this.getAdditionalHeight();
      }
   }

   static class ImageCacher {
      Vector<MetalIconFactory.ImageCacher.ImageGcPair> images = new Vector(1, 1);
      MetalIconFactory.ImageCacher.ImageGcPair currentImageGcPair;

      Image getImage(GraphicsConfiguration var1) {
         if (this.currentImageGcPair != null && this.currentImageGcPair.hasSameConfiguration(var1)) {
            return this.currentImageGcPair.image;
         } else {
            Iterator var2 = this.images.iterator();

            MetalIconFactory.ImageCacher.ImageGcPair var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (MetalIconFactory.ImageCacher.ImageGcPair)var2.next();
            } while(!var3.hasSameConfiguration(var1));

            this.currentImageGcPair = var3;
            return var3.image;
         }
      }

      void cacheImage(Image var1, GraphicsConfiguration var2) {
         MetalIconFactory.ImageCacher.ImageGcPair var3 = new MetalIconFactory.ImageCacher.ImageGcPair(var1, var2);
         this.images.addElement(var3);
         this.currentImageGcPair = var3;
      }

      class ImageGcPair {
         Image image;
         GraphicsConfiguration gc;

         ImageGcPair(Image var2, GraphicsConfiguration var3) {
            this.image = var2;
            this.gc = var3;
         }

         boolean hasSameConfiguration(GraphicsConfiguration var1) {
            return var1 != null && var1.equals(this.gc) || var1 == null && this.gc == null;
         }
      }
   }

   private static class TreeFloppyDriveIcon implements Icon, UIResource, Serializable {
      private TreeFloppyDriveIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.fillRect(2, 2, 12, 12);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(1, 1, 13, 1);
         var2.drawLine(14, 2, 14, 14);
         var2.drawLine(1, 14, 14, 14);
         var2.drawLine(1, 1, 1, 14);
         var2.setColor(MetalLookAndFeel.getControlDarkShadow());
         var2.fillRect(5, 2, 6, 5);
         var2.drawLine(4, 8, 11, 8);
         var2.drawLine(3, 9, 3, 13);
         var2.drawLine(12, 9, 12, 13);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.fillRect(8, 3, 2, 3);
         var2.fillRect(4, 9, 8, 5);
         var2.setColor(MetalLookAndFeel.getPrimaryControlShadow());
         var2.drawLine(5, 10, 9, 10);
         var2.drawLine(5, 12, 8, 12);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 16;
      }

      // $FF: synthetic method
      TreeFloppyDriveIcon(Object var1) {
         this();
      }
   }

   private static class TreeHardDriveIcon implements Icon, UIResource, Serializable {
      private TreeHardDriveIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(1, 4, 1, 5);
         var2.drawLine(2, 3, 3, 3);
         var2.drawLine(4, 2, 11, 2);
         var2.drawLine(12, 3, 13, 3);
         var2.drawLine(14, 4, 14, 5);
         var2.drawLine(12, 6, 13, 6);
         var2.drawLine(4, 7, 11, 7);
         var2.drawLine(2, 6, 3, 6);
         var2.drawLine(1, 7, 1, 8);
         var2.drawLine(2, 9, 3, 9);
         var2.drawLine(4, 10, 11, 10);
         var2.drawLine(12, 9, 13, 9);
         var2.drawLine(14, 7, 14, 8);
         var2.drawLine(1, 10, 1, 11);
         var2.drawLine(2, 12, 3, 12);
         var2.drawLine(4, 13, 11, 13);
         var2.drawLine(12, 12, 13, 12);
         var2.drawLine(14, 10, 14, 11);
         var2.setColor(MetalLookAndFeel.getControlShadow());
         var2.drawLine(7, 6, 7, 6);
         var2.drawLine(9, 6, 9, 6);
         var2.drawLine(10, 5, 10, 5);
         var2.drawLine(11, 6, 11, 6);
         var2.drawLine(12, 5, 13, 5);
         var2.drawLine(13, 4, 13, 4);
         var2.drawLine(7, 9, 7, 9);
         var2.drawLine(9, 9, 9, 9);
         var2.drawLine(10, 8, 10, 8);
         var2.drawLine(11, 9, 11, 9);
         var2.drawLine(12, 8, 13, 8);
         var2.drawLine(13, 7, 13, 7);
         var2.drawLine(7, 12, 7, 12);
         var2.drawLine(9, 12, 9, 12);
         var2.drawLine(10, 11, 10, 11);
         var2.drawLine(11, 12, 11, 12);
         var2.drawLine(12, 11, 13, 11);
         var2.drawLine(13, 10, 13, 10);
         var2.setColor(MetalLookAndFeel.getControlHighlight());
         var2.drawLine(4, 3, 5, 3);
         var2.drawLine(7, 3, 9, 3);
         var2.drawLine(11, 3, 11, 3);
         var2.drawLine(2, 4, 6, 4);
         var2.drawLine(8, 4, 8, 4);
         var2.drawLine(2, 5, 3, 5);
         var2.drawLine(4, 6, 4, 6);
         var2.drawLine(2, 7, 3, 7);
         var2.drawLine(2, 8, 3, 8);
         var2.drawLine(4, 9, 4, 9);
         var2.drawLine(2, 10, 3, 10);
         var2.drawLine(2, 11, 3, 11);
         var2.drawLine(4, 12, 4, 12);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 16;
      }

      // $FF: synthetic method
      TreeHardDriveIcon(Object var1) {
         this();
      }
   }

   private static class TreeComputerIcon implements Icon, UIResource, Serializable {
      private TreeComputerIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.fillRect(5, 4, 6, 4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(2, 2, 2, 8);
         var2.drawLine(13, 2, 13, 8);
         var2.drawLine(3, 1, 12, 1);
         var2.drawLine(12, 9, 12, 9);
         var2.drawLine(3, 9, 3, 9);
         var2.drawLine(4, 4, 4, 7);
         var2.drawLine(5, 3, 10, 3);
         var2.drawLine(11, 4, 11, 7);
         var2.drawLine(5, 8, 10, 8);
         var2.drawLine(1, 10, 14, 10);
         var2.drawLine(14, 10, 14, 14);
         var2.drawLine(1, 14, 14, 14);
         var2.drawLine(1, 10, 1, 14);
         var2.setColor(MetalLookAndFeel.getControlDarkShadow());
         var2.drawLine(6, 12, 8, 12);
         var2.drawLine(10, 12, 12, 12);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 16;
      }

      // $FF: synthetic method
      TreeComputerIcon(Object var1) {
         this();
      }
   }

   private static class RadioButtonIcon implements Icon, UIResource, Serializable {
      private RadioButtonIcon() {
      }

      public void paintOceanIcon(Component var1, Graphics var2, int var3, int var4) {
         ButtonModel var5 = ((JRadioButton)var1).getModel();
         boolean var6 = var5.isEnabled();
         boolean var7 = var6 && var5.isPressed() && var5.isArmed();
         boolean var8 = var6 && var5.isRollover();
         var2.translate(var3, var4);
         if (var6 && !var7) {
            MetalUtils.drawGradient(var1, var2, "RadioButton.gradient", 1, 1, 10, 10, true);
            var2.setColor(var1.getBackground());
            var2.fillRect(1, 1, 1, 1);
            var2.fillRect(10, 1, 1, 1);
            var2.fillRect(1, 10, 1, 1);
            var2.fillRect(10, 10, 1, 1);
         } else if (var7 || !var6) {
            if (var7) {
               var2.setColor(MetalLookAndFeel.getPrimaryControl());
            } else {
               var2.setColor(MetalLookAndFeel.getControl());
            }

            var2.fillRect(2, 2, 8, 8);
            var2.fillRect(4, 1, 4, 1);
            var2.fillRect(4, 10, 4, 1);
            var2.fillRect(1, 4, 1, 4);
            var2.fillRect(10, 4, 1, 4);
         }

         if (!var6) {
            var2.setColor(MetalLookAndFeel.getInactiveControlTextColor());
         } else {
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
         }

         var2.drawLine(4, 0, 7, 0);
         var2.drawLine(8, 1, 9, 1);
         var2.drawLine(10, 2, 10, 3);
         var2.drawLine(11, 4, 11, 7);
         var2.drawLine(10, 8, 10, 9);
         var2.drawLine(9, 10, 8, 10);
         var2.drawLine(7, 11, 4, 11);
         var2.drawLine(3, 10, 2, 10);
         var2.drawLine(1, 9, 1, 8);
         var2.drawLine(0, 7, 0, 4);
         var2.drawLine(1, 3, 1, 2);
         var2.drawLine(2, 1, 3, 1);
         if (var7) {
            var2.fillRect(1, 4, 1, 4);
            var2.fillRect(2, 2, 1, 2);
            var2.fillRect(3, 2, 1, 1);
            var2.fillRect(4, 1, 4, 1);
         } else if (var8) {
            var2.setColor(MetalLookAndFeel.getPrimaryControl());
            var2.fillRect(4, 1, 4, 2);
            var2.fillRect(8, 2, 2, 2);
            var2.fillRect(9, 4, 2, 4);
            var2.fillRect(8, 8, 2, 2);
            var2.fillRect(4, 9, 4, 2);
            var2.fillRect(2, 8, 2, 2);
            var2.fillRect(1, 4, 2, 4);
            var2.fillRect(2, 2, 2, 2);
         }

         if (var5.isSelected()) {
            if (var6) {
               var2.setColor(MetalLookAndFeel.getControlInfo());
            } else {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            }

            var2.fillRect(4, 4, 4, 4);
            var2.drawLine(4, 3, 7, 3);
            var2.drawLine(8, 4, 8, 7);
            var2.drawLine(7, 8, 4, 8);
            var2.drawLine(3, 7, 3, 4);
         }

         var2.translate(-var3, -var4);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (MetalLookAndFeel.usingOcean()) {
            this.paintOceanIcon(var1, var2, var3, var4);
         } else {
            JRadioButton var5 = (JRadioButton)var1;
            ButtonModel var6 = var5.getModel();
            boolean var7 = var6.isSelected();
            Color var8 = var1.getBackground();
            Object var9 = var1.getForeground();
            ColorUIResource var10 = MetalLookAndFeel.getControlShadow();
            ColorUIResource var11 = MetalLookAndFeel.getControlDarkShadow();
            Object var12 = MetalLookAndFeel.getControlHighlight();
            Object var13 = MetalLookAndFeel.getControlHighlight();
            Object var14 = var8;
            if (!var6.isEnabled()) {
               var13 = var8;
               var12 = var8;
               var9 = var10;
               var11 = var10;
            } else if (var6.isPressed() && var6.isArmed()) {
               var14 = var10;
               var12 = var10;
            }

            var2.translate(var3, var4);
            var2.setColor((Color)var14);
            var2.fillRect(2, 2, 9, 9);
            var2.setColor(var11);
            var2.drawLine(4, 0, 7, 0);
            var2.drawLine(8, 1, 9, 1);
            var2.drawLine(10, 2, 10, 3);
            var2.drawLine(11, 4, 11, 7);
            var2.drawLine(10, 8, 10, 9);
            var2.drawLine(9, 10, 8, 10);
            var2.drawLine(7, 11, 4, 11);
            var2.drawLine(3, 10, 2, 10);
            var2.drawLine(1, 9, 1, 8);
            var2.drawLine(0, 7, 0, 4);
            var2.drawLine(1, 3, 1, 2);
            var2.drawLine(2, 1, 3, 1);
            var2.setColor((Color)var12);
            var2.drawLine(2, 9, 2, 8);
            var2.drawLine(1, 7, 1, 4);
            var2.drawLine(2, 2, 2, 3);
            var2.drawLine(2, 2, 3, 2);
            var2.drawLine(4, 1, 7, 1);
            var2.drawLine(8, 2, 9, 2);
            var2.setColor((Color)var13);
            var2.drawLine(10, 1, 10, 1);
            var2.drawLine(11, 2, 11, 3);
            var2.drawLine(12, 4, 12, 7);
            var2.drawLine(11, 8, 11, 9);
            var2.drawLine(10, 10, 10, 10);
            var2.drawLine(9, 11, 8, 11);
            var2.drawLine(7, 12, 4, 12);
            var2.drawLine(3, 11, 2, 11);
            if (var7) {
               var2.setColor((Color)var9);
               var2.fillRect(4, 4, 4, 4);
               var2.drawLine(4, 3, 7, 3);
               var2.drawLine(8, 4, 8, 7);
               var2.drawLine(7, 8, 4, 8);
               var2.drawLine(3, 7, 3, 4);
            }

            var2.translate(-var3, -var4);
         }
      }

      public int getIconWidth() {
         return 13;
      }

      public int getIconHeight() {
         return 13;
      }

      // $FF: synthetic method
      RadioButtonIcon(Object var1) {
         this();
      }
   }

   private static class CheckBoxIcon implements Icon, UIResource, Serializable {
      private CheckBoxIcon() {
      }

      protected int getControlSize() {
         return 13;
      }

      private void paintOceanIcon(Component var1, Graphics var2, int var3, int var4) {
         ButtonModel var5 = ((JCheckBox)var1).getModel();
         var2.translate(var3, var4);
         int var6 = this.getIconWidth();
         int var7 = this.getIconHeight();
         if (var5.isEnabled()) {
            if (var5.isPressed() && var5.isArmed()) {
               var2.setColor(MetalLookAndFeel.getControlShadow());
               var2.fillRect(0, 0, var6, var7);
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.fillRect(0, 0, var6, 2);
               var2.fillRect(0, 2, 2, var7 - 2);
               var2.fillRect(var6 - 1, 1, 1, var7 - 1);
               var2.fillRect(1, var7 - 1, var6 - 2, 1);
            } else if (var5.isRollover()) {
               MetalUtils.drawGradient(var1, var2, "CheckBox.gradient", 0, 0, var6, var7, true);
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(0, 0, var6 - 1, var7 - 1);
               var2.setColor(MetalLookAndFeel.getPrimaryControl());
               var2.drawRect(1, 1, var6 - 3, var7 - 3);
               var2.drawRect(2, 2, var6 - 5, var7 - 5);
            } else {
               MetalUtils.drawGradient(var1, var2, "CheckBox.gradient", 0, 0, var6, var7, true);
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(0, 0, var6 - 1, var7 - 1);
            }

            var2.setColor(MetalLookAndFeel.getControlInfo());
         } else {
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            var2.drawRect(0, 0, var6 - 1, var7 - 1);
         }

         var2.translate(-var3, -var4);
         if (var5.isSelected()) {
            this.drawCheck(var1, var2, var3, var4);
         }

      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (MetalLookAndFeel.usingOcean()) {
            this.paintOceanIcon(var1, var2, var3, var4);
         } else {
            ButtonModel var5 = ((JCheckBox)var1).getModel();
            int var6 = this.getControlSize();
            if (var5.isEnabled()) {
               if (var5.isPressed() && var5.isArmed()) {
                  var2.setColor(MetalLookAndFeel.getControlShadow());
                  var2.fillRect(var3, var4, var6 - 1, var6 - 1);
                  MetalUtils.drawPressed3DBorder(var2, var3, var4, var6, var6);
               } else {
                  MetalUtils.drawFlush3DBorder(var2, var3, var4, var6, var6);
               }

               var2.setColor(var1.getForeground());
            } else {
               var2.setColor(MetalLookAndFeel.getControlShadow());
               var2.drawRect(var3, var4, var6 - 2, var6 - 2);
            }

            if (var5.isSelected()) {
               this.drawCheck(var1, var2, var3, var4);
            }

         }
      }

      protected void drawCheck(Component var1, Graphics var2, int var3, int var4) {
         int var5 = this.getControlSize();
         var2.fillRect(var3 + 3, var4 + 5, 2, var5 - 8);
         var2.drawLine(var3 + (var5 - 4), var4 + 3, var3 + 5, var4 + (var5 - 6));
         var2.drawLine(var3 + (var5 - 4), var4 + 4, var3 + 5, var4 + (var5 - 5));
      }

      public int getIconWidth() {
         return this.getControlSize();
      }

      public int getIconHeight() {
         return this.getControlSize();
      }

      // $FF: synthetic method
      CheckBoxIcon(Object var1) {
         this();
      }
   }

   private static class InternalFrameMinimizeIcon implements Icon, UIResource, Serializable {
      int iconSize = 16;

      public InternalFrameMinimizeIcon(int var1) {
         this.iconSize = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JButton var5 = (JButton)var1;
         ButtonModel var6 = var5.getModel();
         ColorUIResource var7 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var8 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var9 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         ColorUIResource var10 = MetalLookAndFeel.getBlack();
         ColorUIResource var11 = MetalLookAndFeel.getWhite();
         ColorUIResource var12 = MetalLookAndFeel.getWhite();
         if (var5.getClientProperty("paintActive") != Boolean.TRUE) {
            var7 = MetalLookAndFeel.getControl();
            var8 = var7;
            var9 = MetalLookAndFeel.getControlDarkShadow();
            if (var6.isPressed() && var6.isArmed()) {
               var8 = MetalLookAndFeel.getControlShadow();
               var11 = var8;
               var9 = var10;
            }
         } else if (var6.isPressed() && var6.isArmed()) {
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var11 = var8;
            var9 = var10;
         }

         var2.translate(var3, var4);
         var2.setColor(var7);
         var2.fillRect(0, 0, this.iconSize, this.iconSize);
         var2.setColor(var8);
         var2.fillRect(4, 11, this.iconSize - 13, this.iconSize - 13);
         var2.setColor(var12);
         var2.drawRect(2, 10, this.iconSize - 10, this.iconSize - 11);
         var2.setColor(var11);
         var2.drawRect(3, 10, this.iconSize - 12, this.iconSize - 12);
         var2.setColor(var10);
         var2.drawRect(1, 8, this.iconSize - 10, this.iconSize - 10);
         var2.drawRect(2, 9, this.iconSize - 12, this.iconSize - 12);
         var2.setColor(var9);
         var2.drawRect(2, 9, this.iconSize - 11, this.iconSize - 11);
         var2.drawLine(this.iconSize - 10, 10, this.iconSize - 10, 10);
         var2.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
         var2.setColor(var9);
         var2.fillRect(this.iconSize - 7, 3, 3, 5);
         var2.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
         var2.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
         var2.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
         var2.setColor(var10);
         var2.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
         var2.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
         var2.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
         var2.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
         var2.setColor(var12);
         var2.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
         var2.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
         var2.drawLine(this.iconSize - 7, 8, this.iconSize - 3, 8);
         var2.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return this.iconSize;
      }

      public int getIconHeight() {
         return this.iconSize;
      }
   }

   private static class InternalFrameMaximizeIcon implements Icon, UIResource, Serializable {
      protected int iconSize = 16;

      public InternalFrameMaximizeIcon(int var1) {
         this.iconSize = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JButton var5 = (JButton)var1;
         ButtonModel var6 = var5.getModel();
         ColorUIResource var7 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var8 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var9 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         ColorUIResource var10 = MetalLookAndFeel.getBlack();
         ColorUIResource var11 = MetalLookAndFeel.getWhite();
         ColorUIResource var12 = MetalLookAndFeel.getWhite();
         if (var5.getClientProperty("paintActive") != Boolean.TRUE) {
            var7 = MetalLookAndFeel.getControl();
            var8 = var7;
            var9 = MetalLookAndFeel.getControlDarkShadow();
            if (var6.isPressed() && var6.isArmed()) {
               var8 = MetalLookAndFeel.getControlShadow();
               var11 = var8;
               var9 = var10;
            }
         } else if (var6.isPressed() && var6.isArmed()) {
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var11 = var8;
            var9 = var10;
         }

         var2.translate(var3, var4);
         var2.setColor(var7);
         var2.fillRect(0, 0, this.iconSize, this.iconSize);
         var2.setColor(var8);
         var2.fillRect(3, 7, this.iconSize - 10, this.iconSize - 10);
         var2.setColor(var11);
         var2.drawRect(3, 7, this.iconSize - 10, this.iconSize - 10);
         var2.setColor(var12);
         var2.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
         var2.setColor(var10);
         var2.drawRect(1, 5, this.iconSize - 7, this.iconSize - 7);
         var2.drawRect(2, 6, this.iconSize - 9, this.iconSize - 9);
         var2.setColor(var9);
         var2.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
         var2.setColor(var10);
         var2.drawLine(3, this.iconSize - 5, this.iconSize - 9, 7);
         var2.drawLine(this.iconSize - 6, 4, this.iconSize - 5, 3);
         var2.drawLine(this.iconSize - 7, 1, this.iconSize - 7, 2);
         var2.drawLine(this.iconSize - 6, 1, this.iconSize - 2, 1);
         var2.setColor(var11);
         var2.drawLine(5, this.iconSize - 4, this.iconSize - 8, 9);
         var2.setColor(var12);
         var2.drawLine(this.iconSize - 6, 3, this.iconSize - 4, 5);
         var2.drawLine(this.iconSize - 4, 5, this.iconSize - 4, 6);
         var2.drawLine(this.iconSize - 2, 7, this.iconSize - 1, 7);
         var2.drawLine(this.iconSize - 1, 2, this.iconSize - 1, 6);
         var2.setColor(var9);
         var2.drawLine(3, this.iconSize - 4, this.iconSize - 3, 2);
         var2.drawLine(3, this.iconSize - 3, this.iconSize - 2, 2);
         var2.drawLine(4, this.iconSize - 3, 5, this.iconSize - 3);
         var2.drawLine(this.iconSize - 7, 8, this.iconSize - 7, 9);
         var2.drawLine(this.iconSize - 6, 2, this.iconSize - 4, 2);
         var2.drawRect(this.iconSize - 3, 3, 1, 3);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return this.iconSize;
      }

      public int getIconHeight() {
         return this.iconSize;
      }
   }

   private static class InternalFrameDefaultMenuIcon implements Icon, UIResource, Serializable {
      private InternalFrameDefaultMenuIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         ColorUIResource var5 = MetalLookAndFeel.getWindowBackground();
         ColorUIResource var6 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var7 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         var2.translate(var3, var4);
         var2.setColor(var6);
         var2.fillRect(0, 0, 16, 16);
         var2.setColor(var5);
         var2.fillRect(2, 6, 13, 9);
         var2.drawLine(2, 2, 2, 2);
         var2.drawLine(5, 2, 5, 2);
         var2.drawLine(8, 2, 8, 2);
         var2.drawLine(11, 2, 11, 2);
         var2.setColor(var7);
         var2.drawRect(1, 1, 13, 13);
         var2.drawLine(1, 0, 14, 0);
         var2.drawLine(15, 1, 15, 14);
         var2.drawLine(1, 15, 14, 15);
         var2.drawLine(0, 1, 0, 14);
         var2.drawLine(2, 5, 13, 5);
         var2.drawLine(3, 3, 3, 3);
         var2.drawLine(6, 3, 6, 3);
         var2.drawLine(9, 3, 9, 3);
         var2.drawLine(12, 3, 12, 3);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 16;
      }

      public int getIconHeight() {
         return 16;
      }

      // $FF: synthetic method
      InternalFrameDefaultMenuIcon(Object var1) {
         this();
      }
   }

   private static class InternalFrameAltMaximizeIcon implements Icon, UIResource, Serializable {
      int iconSize = 16;

      public InternalFrameAltMaximizeIcon(int var1) {
         this.iconSize = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JButton var5 = (JButton)var1;
         ButtonModel var6 = var5.getModel();
         ColorUIResource var7 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var8 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var9 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         ColorUIResource var10 = MetalLookAndFeel.getBlack();
         ColorUIResource var11 = MetalLookAndFeel.getWhite();
         ColorUIResource var12 = MetalLookAndFeel.getWhite();
         if (var5.getClientProperty("paintActive") != Boolean.TRUE) {
            var7 = MetalLookAndFeel.getControl();
            var8 = var7;
            var9 = MetalLookAndFeel.getControlDarkShadow();
            if (var6.isPressed() && var6.isArmed()) {
               var8 = MetalLookAndFeel.getControlShadow();
               var11 = var8;
               var9 = var10;
            }
         } else if (var6.isPressed() && var6.isArmed()) {
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var11 = var8;
            var9 = var10;
         }

         var2.translate(var3, var4);
         var2.setColor(var7);
         var2.fillRect(0, 0, this.iconSize, this.iconSize);
         var2.setColor(var8);
         var2.fillRect(3, 6, this.iconSize - 9, this.iconSize - 9);
         var2.setColor(var10);
         var2.drawRect(1, 5, this.iconSize - 8, this.iconSize - 8);
         var2.drawLine(1, this.iconSize - 2, 1, this.iconSize - 2);
         var2.setColor(var12);
         var2.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
         var2.setColor(var11);
         var2.drawRect(3, 7, this.iconSize - 9, this.iconSize - 9);
         var2.setColor(var9);
         var2.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
         var2.setColor(var11);
         var2.drawLine(this.iconSize - 6, 8, this.iconSize - 6, 8);
         var2.drawLine(this.iconSize - 9, 6, this.iconSize - 7, 8);
         var2.setColor(var9);
         var2.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
         var2.setColor(var10);
         var2.drawLine(this.iconSize - 6, 9, this.iconSize - 6, 9);
         var2.setColor(var7);
         var2.drawLine(this.iconSize - 9, 5, this.iconSize - 9, 5);
         var2.setColor(var9);
         var2.fillRect(this.iconSize - 7, 3, 3, 5);
         var2.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
         var2.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
         var2.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
         var2.setColor(var10);
         var2.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
         var2.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
         var2.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
         var2.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
         var2.setColor(var12);
         var2.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
         var2.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
         var2.drawLine(this.iconSize - 4, 8, this.iconSize - 3, 8);
         var2.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return this.iconSize;
      }

      public int getIconHeight() {
         return this.iconSize;
      }
   }

   private static class InternalFrameCloseIcon implements Icon, UIResource, Serializable {
      int iconSize = 16;

      public InternalFrameCloseIcon(int var1) {
         this.iconSize = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JButton var5 = (JButton)var1;
         ButtonModel var6 = var5.getModel();
         ColorUIResource var7 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var8 = MetalLookAndFeel.getPrimaryControl();
         ColorUIResource var9 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         ColorUIResource var10 = MetalLookAndFeel.getBlack();
         ColorUIResource var11 = MetalLookAndFeel.getWhite();
         ColorUIResource var12 = MetalLookAndFeel.getWhite();
         if (var5.getClientProperty("paintActive") != Boolean.TRUE) {
            var7 = MetalLookAndFeel.getControl();
            var8 = var7;
            var9 = MetalLookAndFeel.getControlDarkShadow();
            if (var6.isPressed() && var6.isArmed()) {
               var8 = MetalLookAndFeel.getControlShadow();
               var11 = var8;
               var9 = var10;
            }
         } else if (var6.isPressed() && var6.isArmed()) {
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var11 = var8;
            var9 = var10;
         }

         int var13 = this.iconSize / 2;
         var2.translate(var3, var4);
         var2.setColor(var7);
         var2.fillRect(0, 0, this.iconSize, this.iconSize);
         var2.setColor(var8);
         var2.fillRect(3, 3, this.iconSize - 6, this.iconSize - 6);
         var2.setColor(var10);
         var2.drawRect(1, 1, this.iconSize - 3, this.iconSize - 3);
         var2.drawRect(2, 2, this.iconSize - 5, this.iconSize - 5);
         var2.setColor(var12);
         var2.drawRect(2, 2, this.iconSize - 3, this.iconSize - 3);
         var2.setColor(var9);
         var2.drawRect(2, 2, this.iconSize - 4, this.iconSize - 4);
         var2.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
         var2.drawLine(this.iconSize - 3, 3, this.iconSize - 3, 3);
         var2.setColor(var10);
         var2.drawLine(4, 5, 5, 4);
         var2.drawLine(4, this.iconSize - 6, this.iconSize - 6, 4);
         var2.setColor(var11);
         var2.drawLine(6, this.iconSize - 5, this.iconSize - 5, 6);
         var2.drawLine(var13, var13 + 2, var13 + 2, var13);
         var2.drawLine(this.iconSize - 5, this.iconSize - 5, this.iconSize - 4, this.iconSize - 5);
         var2.drawLine(this.iconSize - 5, this.iconSize - 4, this.iconSize - 5, this.iconSize - 4);
         var2.setColor(var9);
         var2.drawLine(5, 5, this.iconSize - 6, this.iconSize - 6);
         var2.drawLine(6, 5, this.iconSize - 5, this.iconSize - 6);
         var2.drawLine(5, 6, this.iconSize - 6, this.iconSize - 5);
         var2.drawLine(5, this.iconSize - 5, this.iconSize - 5, 5);
         var2.drawLine(5, this.iconSize - 6, this.iconSize - 6, 5);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return this.iconSize;
      }

      public int getIconHeight() {
         return this.iconSize;
      }
   }

   public static class PaletteCloseIcon implements Icon, UIResource, Serializable {
      int iconSize = 7;

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JButton var5 = (JButton)var1;
         ButtonModel var6 = var5.getModel();
         ColorUIResource var8 = MetalLookAndFeel.getPrimaryControlHighlight();
         ColorUIResource var9 = MetalLookAndFeel.getPrimaryControlInfo();
         ColorUIResource var7;
         if (var6.isPressed() && var6.isArmed()) {
            var7 = var9;
         } else {
            var7 = MetalLookAndFeel.getPrimaryControlDarkShadow();
         }

         var2.translate(var3, var4);
         var2.setColor(var7);
         var2.drawLine(0, 1, 5, 6);
         var2.drawLine(1, 0, 6, 5);
         var2.drawLine(1, 1, 6, 6);
         var2.drawLine(6, 1, 1, 6);
         var2.drawLine(5, 0, 0, 5);
         var2.drawLine(5, 1, 1, 5);
         var2.setColor(var8);
         var2.drawLine(6, 2, 5, 3);
         var2.drawLine(2, 6, 3, 5);
         var2.drawLine(6, 6, 6, 6);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return this.iconSize;
      }

      public int getIconHeight() {
         return this.iconSize;
      }
   }

   private static class FileChooserUpFolderIcon implements Icon, UIResource, Serializable {
      private FileChooserUpFolderIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.fillRect(3, 5, 12, 9);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(1, 6, 1, 14);
         var2.drawLine(2, 14, 15, 14);
         var2.drawLine(15, 13, 15, 5);
         var2.drawLine(2, 5, 9, 5);
         var2.drawLine(10, 6, 14, 6);
         var2.drawLine(8, 13, 8, 16);
         var2.drawLine(8, 9, 8, 9);
         var2.drawLine(7, 10, 9, 10);
         var2.drawLine(6, 11, 10, 11);
         var2.drawLine(5, 12, 11, 12);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(2, 6, 2, 13);
         var2.drawLine(3, 6, 9, 6);
         var2.drawLine(10, 7, 14, 7);
         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawLine(11, 3, 15, 3);
         var2.drawLine(10, 4, 15, 4);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 18;
      }

      public int getIconHeight() {
         return 18;
      }

      // $FF: synthetic method
      FileChooserUpFolderIcon(Object var1) {
         this();
      }
   }

   private static class FileChooserNewFolderIcon implements Icon, UIResource, Serializable {
      private FileChooserNewFolderIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.fillRect(3, 5, 12, 9);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(1, 6, 1, 14);
         var2.drawLine(2, 14, 15, 14);
         var2.drawLine(15, 13, 15, 5);
         var2.drawLine(2, 5, 9, 5);
         var2.drawLine(10, 6, 14, 6);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(2, 6, 2, 13);
         var2.drawLine(3, 6, 9, 6);
         var2.drawLine(10, 7, 14, 7);
         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawLine(11, 3, 15, 3);
         var2.drawLine(10, 4, 15, 4);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 18;
      }

      public int getIconHeight() {
         return 18;
      }

      // $FF: synthetic method
      FileChooserNewFolderIcon(Object var1) {
         this();
      }
   }

   private static class FileChooserListViewIcon implements Icon, UIResource, Serializable {
      private FileChooserListViewIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(2, 2, 5, 2);
         var2.drawLine(2, 3, 2, 7);
         var2.drawLine(3, 7, 6, 7);
         var2.drawLine(6, 6, 6, 3);
         var2.drawLine(10, 2, 13, 2);
         var2.drawLine(10, 3, 10, 7);
         var2.drawLine(11, 7, 14, 7);
         var2.drawLine(14, 6, 14, 3);
         var2.drawLine(2, 10, 5, 10);
         var2.drawLine(2, 11, 2, 15);
         var2.drawLine(3, 15, 6, 15);
         var2.drawLine(6, 14, 6, 11);
         var2.drawLine(10, 10, 13, 10);
         var2.drawLine(10, 11, 10, 15);
         var2.drawLine(11, 15, 14, 15);
         var2.drawLine(14, 14, 14, 11);
         var2.drawLine(8, 5, 8, 5);
         var2.drawLine(16, 5, 16, 5);
         var2.drawLine(8, 13, 8, 13);
         var2.drawLine(16, 13, 16, 13);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.drawRect(3, 3, 2, 3);
         var2.drawRect(11, 3, 2, 3);
         var2.drawRect(3, 11, 2, 3);
         var2.drawRect(11, 11, 2, 3);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(4, 4, 4, 5);
         var2.drawLine(12, 4, 12, 5);
         var2.drawLine(4, 12, 4, 13);
         var2.drawLine(12, 12, 12, 13);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 18;
      }

      public int getIconHeight() {
         return 18;
      }

      // $FF: synthetic method
      FileChooserListViewIcon(Object var1) {
         this();
      }
   }

   private static class FileChooserHomeFolderIcon implements Icon, UIResource, Serializable {
      private FileChooserHomeFolderIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(8, 1, 1, 8);
         var2.drawLine(8, 1, 15, 8);
         var2.drawLine(11, 2, 11, 3);
         var2.drawLine(12, 2, 12, 4);
         var2.drawLine(3, 7, 3, 15);
         var2.drawLine(13, 7, 13, 15);
         var2.drawLine(4, 15, 12, 15);
         var2.drawLine(6, 9, 6, 14);
         var2.drawLine(10, 9, 10, 14);
         var2.drawLine(7, 9, 9, 9);
         var2.setColor(MetalLookAndFeel.getControlDarkShadow());
         var2.fillRect(8, 2, 1, 1);
         var2.fillRect(7, 3, 3, 1);
         var2.fillRect(6, 4, 5, 1);
         var2.fillRect(5, 5, 7, 1);
         var2.fillRect(4, 6, 9, 2);
         var2.drawLine(9, 12, 9, 12);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.drawLine(4, 8, 12, 8);
         var2.fillRect(4, 9, 2, 6);
         var2.fillRect(11, 9, 2, 6);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 18;
      }

      public int getIconHeight() {
         return 18;
      }

      // $FF: synthetic method
      FileChooserHomeFolderIcon(Object var1) {
         this();
      }
   }

   private static class FileChooserDetailViewIcon implements Icon, UIResource, Serializable {
      private FileChooserDetailViewIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlInfo());
         var2.drawLine(2, 2, 5, 2);
         var2.drawLine(2, 3, 2, 7);
         var2.drawLine(3, 7, 6, 7);
         var2.drawLine(6, 6, 6, 3);
         var2.drawLine(2, 10, 5, 10);
         var2.drawLine(2, 11, 2, 15);
         var2.drawLine(3, 15, 6, 15);
         var2.drawLine(6, 14, 6, 11);
         var2.drawLine(8, 5, 15, 5);
         var2.drawLine(8, 13, 15, 13);
         var2.setColor(MetalLookAndFeel.getPrimaryControl());
         var2.drawRect(3, 3, 2, 3);
         var2.drawRect(3, 11, 2, 3);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(4, 4, 4, 5);
         var2.drawLine(4, 12, 4, 13);
         var2.translate(-var3, -var4);
      }

      public int getIconWidth() {
         return 18;
      }

      public int getIconHeight() {
         return 18;
      }

      // $FF: synthetic method
      FileChooserDetailViewIcon(Object var1) {
         this();
      }
   }
}
