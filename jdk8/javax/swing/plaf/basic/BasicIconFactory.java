package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

public class BasicIconFactory implements Serializable {
   private static Icon frame_icon;
   private static Icon checkBoxIcon;
   private static Icon radioButtonIcon;
   private static Icon checkBoxMenuItemIcon;
   private static Icon radioButtonMenuItemIcon;
   private static Icon menuItemCheckIcon;
   private static Icon menuItemArrowIcon;
   private static Icon menuArrowIcon;

   public static Icon getMenuItemCheckIcon() {
      if (menuItemCheckIcon == null) {
         menuItemCheckIcon = new BasicIconFactory.MenuItemCheckIcon();
      }

      return menuItemCheckIcon;
   }

   public static Icon getMenuItemArrowIcon() {
      if (menuItemArrowIcon == null) {
         menuItemArrowIcon = new BasicIconFactory.MenuItemArrowIcon();
      }

      return menuItemArrowIcon;
   }

   public static Icon getMenuArrowIcon() {
      if (menuArrowIcon == null) {
         menuArrowIcon = new BasicIconFactory.MenuArrowIcon();
      }

      return menuArrowIcon;
   }

   public static Icon getCheckBoxIcon() {
      if (checkBoxIcon == null) {
         checkBoxIcon = new BasicIconFactory.CheckBoxIcon();
      }

      return checkBoxIcon;
   }

   public static Icon getRadioButtonIcon() {
      if (radioButtonIcon == null) {
         radioButtonIcon = new BasicIconFactory.RadioButtonIcon();
      }

      return radioButtonIcon;
   }

   public static Icon getCheckBoxMenuItemIcon() {
      if (checkBoxMenuItemIcon == null) {
         checkBoxMenuItemIcon = new BasicIconFactory.CheckBoxMenuItemIcon();
      }

      return checkBoxMenuItemIcon;
   }

   public static Icon getRadioButtonMenuItemIcon() {
      if (radioButtonMenuItemIcon == null) {
         radioButtonMenuItemIcon = new BasicIconFactory.RadioButtonMenuItemIcon();
      }

      return radioButtonMenuItemIcon;
   }

   public static Icon createEmptyFrameIcon() {
      if (frame_icon == null) {
         frame_icon = new BasicIconFactory.EmptyFrameIcon();
      }

      return frame_icon;
   }

   private static class MenuArrowIcon implements Icon, UIResource, Serializable {
      private MenuArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         Polygon var5 = new Polygon();
         var5.addPoint(var3, var4);
         var5.addPoint(var3 + this.getIconWidth(), var4 + this.getIconHeight() / 2);
         var5.addPoint(var3, var4 + this.getIconHeight());
         var2.fillPolygon(var5);
      }

      public int getIconWidth() {
         return 4;
      }

      public int getIconHeight() {
         return 8;
      }

      // $FF: synthetic method
      MenuArrowIcon(Object var1) {
         this();
      }
   }

   private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
      private MenuItemArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return 4;
      }

      public int getIconHeight() {
         return 8;
      }

      // $FF: synthetic method
      MenuItemArrowIcon(Object var1) {
         this();
      }
   }

   private static class MenuItemCheckIcon implements Icon, UIResource, Serializable {
      private MenuItemCheckIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return 9;
      }

      public int getIconHeight() {
         return 9;
      }

      // $FF: synthetic method
      MenuItemCheckIcon(Object var1) {
         this();
      }
   }

   private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
      private RadioButtonMenuItemIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         if (var5.isSelected()) {
            var2.fillOval(var3 + 1, var4 + 1, this.getIconWidth(), this.getIconHeight());
         }

      }

      public int getIconWidth() {
         return 6;
      }

      public int getIconHeight() {
         return 6;
      }

      // $FF: synthetic method
      RadioButtonMenuItemIcon(Object var1) {
         this();
      }
   }

   private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {
      private CheckBoxMenuItemIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         boolean var7 = var6.isSelected();
         if (var7) {
            var2.drawLine(var3 + 7, var4 + 1, var3 + 7, var4 + 3);
            var2.drawLine(var3 + 6, var4 + 2, var3 + 6, var4 + 4);
            var2.drawLine(var3 + 5, var4 + 3, var3 + 5, var4 + 5);
            var2.drawLine(var3 + 4, var4 + 4, var3 + 4, var4 + 6);
            var2.drawLine(var3 + 3, var4 + 5, var3 + 3, var4 + 7);
            var2.drawLine(var3 + 2, var4 + 4, var3 + 2, var4 + 6);
            var2.drawLine(var3 + 1, var4 + 3, var3 + 1, var4 + 5);
         }

      }

      public int getIconWidth() {
         return 9;
      }

      public int getIconHeight() {
         return 9;
      }

      // $FF: synthetic method
      CheckBoxMenuItemIcon(Object var1) {
         this();
      }
   }

   private static class RadioButtonIcon implements Icon, UIResource, Serializable {
      private RadioButtonIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
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

   private static class CheckBoxIcon implements Icon, Serializable {
      static final int csize = 13;

      private CheckBoxIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return 13;
      }

      public int getIconHeight() {
         return 13;
      }

      // $FF: synthetic method
      CheckBoxIcon(Object var1) {
         this();
      }
   }

   private static class EmptyFrameIcon implements Icon, Serializable {
      int height;
      int width;

      private EmptyFrameIcon() {
         this.height = 16;
         this.width = 14;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return this.width;
      }

      public int getIconHeight() {
         return this.height;
      }

      // $FF: synthetic method
      EmptyFrameIcon(Object var1) {
         this();
      }
   }
}
