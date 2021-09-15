package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.IconUIResource;
import sun.swing.PrintColorUIResource;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class OceanTheme extends DefaultMetalTheme {
   private static final ColorUIResource PRIMARY1 = new ColorUIResource(6521535);
   private static final ColorUIResource PRIMARY2 = new ColorUIResource(10729676);
   private static final ColorUIResource PRIMARY3 = new ColorUIResource(12111845);
   private static final ColorUIResource SECONDARY1 = new ColorUIResource(8030873);
   private static final ColorUIResource SECONDARY2 = new ColorUIResource(12111845);
   private static final ColorUIResource SECONDARY3 = new ColorUIResource(15658734);
   private static final ColorUIResource CONTROL_TEXT_COLOR;
   private static final ColorUIResource INACTIVE_CONTROL_TEXT_COLOR;
   private static final ColorUIResource MENU_DISABLED_FOREGROUND;
   private static final ColorUIResource OCEAN_BLACK;
   private static final ColorUIResource OCEAN_DROP;

   public void addCustomEntriesToTable(UIDefaults var1) {
      SwingLazyValue var2 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{this.getPrimary1()});
      List var3 = Arrays.asList(new Float(0.3F), new Float(0.0F), new ColorUIResource(14543091), this.getWhite(), this.getSecondary2());
      ColorUIResource var4 = new ColorUIResource(13421772);
      ColorUIResource var5 = new ColorUIResource(14342874);
      ColorUIResource var6 = new ColorUIResource(13164018);
      Object var7 = this.getIconResource("icons/ocean/directory.gif");
      Object var8 = this.getIconResource("icons/ocean/file.gif");
      List var9 = Arrays.asList(new Float(0.3F), new Float(0.2F), var6, this.getWhite(), new ColorUIResource(SECONDARY2));
      Object[] var10 = new Object[]{"Button.gradient", var3, "Button.rollover", Boolean.TRUE, "Button.toolBarBorderBackground", INACTIVE_CONTROL_TEXT_COLOR, "Button.disabledToolBarBorderBackground", var4, "Button.rolloverIconType", "ocean", "CheckBox.rollover", Boolean.TRUE, "CheckBox.gradient", var3, "CheckBoxMenuItem.gradient", var3, "FileChooser.homeFolderIcon", this.getIconResource("icons/ocean/homeFolder.gif"), "FileChooser.newFolderIcon", this.getIconResource("icons/ocean/newFolder.gif"), "FileChooser.upFolderIcon", this.getIconResource("icons/ocean/upFolder.gif"), "FileView.computerIcon", this.getIconResource("icons/ocean/computer.gif"), "FileView.directoryIcon", var7, "FileView.hardDriveIcon", this.getIconResource("icons/ocean/hardDrive.gif"), "FileView.fileIcon", var8, "FileView.floppyDriveIcon", this.getIconResource("icons/ocean/floppy.gif"), "Label.disabledForeground", this.getInactiveControlTextColor(), "Menu.opaque", Boolean.FALSE, "MenuBar.gradient", Arrays.asList(new Float(1.0F), new Float(0.0F), this.getWhite(), var5, new ColorUIResource(var5)), "MenuBar.borderColor", var4, "InternalFrame.activeTitleGradient", var3, "InternalFrame.closeIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/close.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/close-pressed.gif", var1));
         }
      }, "InternalFrame.iconifyIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/iconify.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/iconify-pressed.gif", var1));
         }
      }, "InternalFrame.minimizeIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/minimize.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/minimize-pressed.gif", var1));
         }
      }, "InternalFrame.icon", this.getIconResource("icons/ocean/menu.gif"), "InternalFrame.maximizeIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/maximize.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/maximize-pressed.gif", var1));
         }
      }, "InternalFrame.paletteCloseIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/paletteClose.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/paletteClose-pressed.gif", var1));
         }
      }, "List.focusCellHighlightBorder", var2, "MenuBarUI", "javax.swing.plaf.metal.MetalMenuBarUI", "OptionPane.errorIcon", this.getIconResource("icons/ocean/error.png"), "OptionPane.informationIcon", this.getIconResource("icons/ocean/info.png"), "OptionPane.questionIcon", this.getIconResource("icons/ocean/question.png"), "OptionPane.warningIcon", this.getIconResource("icons/ocean/warning.png"), "RadioButton.gradient", var3, "RadioButton.rollover", Boolean.TRUE, "RadioButtonMenuItem.gradient", var3, "ScrollBar.gradient", var3, "Slider.altTrackColor", new ColorUIResource(13820655), "Slider.gradient", var9, "Slider.focusGradient", var9, "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE, "SplitPane.dividerFocusColor", var6, "TabbedPane.borderHightlightColor", this.getPrimary1(), "TabbedPane.contentAreaColor", var6, "TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3), "TabbedPane.selected", var6, "TabbedPane.tabAreaBackground", var5, "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6), "TabbedPane.unselectedBackground", SECONDARY3, "Table.focusCellHighlightBorder", var2, "Table.gridColor", SECONDARY1, "TableHeader.focusCellBackground", var6, "ToggleButton.gradient", var3, "ToolBar.borderColor", var4, "ToolBar.isRollover", Boolean.TRUE, "Tree.closedIcon", var7, "Tree.collapsedIcon", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new OceanTheme.COIcon(OceanTheme.this.getHastenedIcon("icons/ocean/collapsed.gif", var1), OceanTheme.this.getHastenedIcon("icons/ocean/collapsed-rtl.gif", var1));
         }
      }, "Tree.expandedIcon", this.getIconResource("icons/ocean/expanded.gif"), "Tree.leafIcon", var8, "Tree.openIcon", var7, "Tree.selectionBorderColor", this.getPrimary1(), "Tree.dropLineColor", this.getPrimary1(), "Table.dropLineColor", this.getPrimary1(), "Table.dropLineShortColor", OCEAN_BLACK, "Table.dropCellBackground", OCEAN_DROP, "Tree.dropCellBackground", OCEAN_DROP, "List.dropCellBackground", OCEAN_DROP, "List.dropLineColor", this.getPrimary1()};
      var1.putDefaults(var10);
   }

   boolean isSystemTheme() {
      return true;
   }

   public String getName() {
      return "Ocean";
   }

   protected ColorUIResource getPrimary1() {
      return PRIMARY1;
   }

   protected ColorUIResource getPrimary2() {
      return PRIMARY2;
   }

   protected ColorUIResource getPrimary3() {
      return PRIMARY3;
   }

   protected ColorUIResource getSecondary1() {
      return SECONDARY1;
   }

   protected ColorUIResource getSecondary2() {
      return SECONDARY2;
   }

   protected ColorUIResource getSecondary3() {
      return SECONDARY3;
   }

   protected ColorUIResource getBlack() {
      return OCEAN_BLACK;
   }

   public ColorUIResource getDesktopColor() {
      return MetalTheme.white;
   }

   public ColorUIResource getInactiveControlTextColor() {
      return INACTIVE_CONTROL_TEXT_COLOR;
   }

   public ColorUIResource getControlTextColor() {
      return CONTROL_TEXT_COLOR;
   }

   public ColorUIResource getMenuDisabledForeground() {
      return MENU_DISABLED_FOREGROUND;
   }

   private Object getIconResource(String var1) {
      return SwingUtilities2.makeIcon(this.getClass(), OceanTheme.class, var1);
   }

   private Icon getHastenedIcon(String var1, UIDefaults var2) {
      Object var3 = this.getIconResource(var1);
      return (Icon)((UIDefaults.LazyValue)var3).createValue(var2);
   }

   static {
      CONTROL_TEXT_COLOR = new PrintColorUIResource(3355443, Color.BLACK);
      INACTIVE_CONTROL_TEXT_COLOR = new ColorUIResource(10066329);
      MENU_DISABLED_FOREGROUND = new ColorUIResource(10066329);
      OCEAN_BLACK = new PrintColorUIResource(3355443, Color.BLACK);
      OCEAN_DROP = new ColorUIResource(13822463);
   }

   private static class IFIcon extends IconUIResource {
      private Icon pressed;

      public IFIcon(Icon var1, Icon var2) {
         super(var1);
         this.pressed = var2;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         ButtonModel var5 = ((AbstractButton)var1).getModel();
         if (var5.isPressed() && var5.isArmed()) {
            this.pressed.paintIcon(var1, var2, var3, var4);
         } else {
            super.paintIcon(var1, var2, var3, var4);
         }

      }
   }

   private static class COIcon extends IconUIResource {
      private Icon rtl;

      public COIcon(Icon var1, Icon var2) {
         super(var1);
         this.rtl = var2;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (MetalUtils.isLeftToRight(var1)) {
            super.paintIcon(var1, var2, var3, var4);
         } else {
            this.rtl.paintIcon(var1, var2, var3, var4);
         }

      }
   }
}
