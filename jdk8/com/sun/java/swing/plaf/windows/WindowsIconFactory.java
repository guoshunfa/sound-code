package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemCheckIconFactory;

public class WindowsIconFactory implements Serializable {
   private static Icon frame_closeIcon;
   private static Icon frame_iconifyIcon;
   private static Icon frame_maxIcon;
   private static Icon frame_minIcon;
   private static Icon frame_resizeIcon;
   private static Icon checkBoxIcon;
   private static Icon radioButtonIcon;
   private static Icon checkBoxMenuItemIcon;
   private static Icon radioButtonMenuItemIcon;
   private static Icon menuItemCheckIcon;
   private static Icon menuItemArrowIcon;
   private static Icon menuArrowIcon;
   private static WindowsIconFactory.VistaMenuItemCheckIconFactory menuItemCheckIconFactory;

   public static Icon getMenuItemCheckIcon() {
      if (menuItemCheckIcon == null) {
         menuItemCheckIcon = new WindowsIconFactory.MenuItemCheckIcon();
      }

      return menuItemCheckIcon;
   }

   public static Icon getMenuItemArrowIcon() {
      if (menuItemArrowIcon == null) {
         menuItemArrowIcon = new WindowsIconFactory.MenuItemArrowIcon();
      }

      return menuItemArrowIcon;
   }

   public static Icon getMenuArrowIcon() {
      if (menuArrowIcon == null) {
         menuArrowIcon = new WindowsIconFactory.MenuArrowIcon();
      }

      return menuArrowIcon;
   }

   public static Icon getCheckBoxIcon() {
      if (checkBoxIcon == null) {
         checkBoxIcon = new WindowsIconFactory.CheckBoxIcon();
      }

      return checkBoxIcon;
   }

   public static Icon getRadioButtonIcon() {
      if (radioButtonIcon == null) {
         radioButtonIcon = new WindowsIconFactory.RadioButtonIcon();
      }

      return radioButtonIcon;
   }

   public static Icon getCheckBoxMenuItemIcon() {
      if (checkBoxMenuItemIcon == null) {
         checkBoxMenuItemIcon = new WindowsIconFactory.CheckBoxMenuItemIcon();
      }

      return checkBoxMenuItemIcon;
   }

   public static Icon getRadioButtonMenuItemIcon() {
      if (radioButtonMenuItemIcon == null) {
         radioButtonMenuItemIcon = new WindowsIconFactory.RadioButtonMenuItemIcon();
      }

      return radioButtonMenuItemIcon;
   }

   static synchronized WindowsIconFactory.VistaMenuItemCheckIconFactory getMenuItemCheckIconFactory() {
      if (menuItemCheckIconFactory == null) {
         menuItemCheckIconFactory = new WindowsIconFactory.VistaMenuItemCheckIconFactory();
      }

      return menuItemCheckIconFactory;
   }

   public static Icon createFrameCloseIcon() {
      if (frame_closeIcon == null) {
         frame_closeIcon = new WindowsIconFactory.FrameButtonIcon(TMSchema.Part.WP_CLOSEBUTTON);
      }

      return frame_closeIcon;
   }

   public static Icon createFrameIconifyIcon() {
      if (frame_iconifyIcon == null) {
         frame_iconifyIcon = new WindowsIconFactory.FrameButtonIcon(TMSchema.Part.WP_MINBUTTON);
      }

      return frame_iconifyIcon;
   }

   public static Icon createFrameMaximizeIcon() {
      if (frame_maxIcon == null) {
         frame_maxIcon = new WindowsIconFactory.FrameButtonIcon(TMSchema.Part.WP_MAXBUTTON);
      }

      return frame_maxIcon;
   }

   public static Icon createFrameMinimizeIcon() {
      if (frame_minIcon == null) {
         frame_minIcon = new WindowsIconFactory.FrameButtonIcon(TMSchema.Part.WP_RESTOREBUTTON);
      }

      return frame_minIcon;
   }

   public static Icon createFrameResizeIcon() {
      if (frame_resizeIcon == null) {
         frame_resizeIcon = new WindowsIconFactory.ResizeIcon();
      }

      return frame_resizeIcon;
   }

   static class VistaMenuItemCheckIconFactory implements MenuItemCheckIconFactory {
      private static final int OFFSET = 3;

      public Icon getIcon(JMenuItem var1) {
         return new WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon(var1);
      }

      public boolean isCompatible(Object var1, String var2) {
         return var1 instanceof WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon && ((WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon)var1).type == getType(var2);
      }

      public Icon getIcon(String var1) {
         return new WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon(var1);
      }

      static int getIconWidth() {
         XPStyle var0 = XPStyle.getXP();
         return (var0 != null ? var0.getSkin((Component)null, TMSchema.Part.MP_POPUPCHECK).getWidth() : 16) + 6;
      }

      private static Class<? extends JMenuItem> getType(Component var0) {
         Class var1 = null;
         if (var0 instanceof JCheckBoxMenuItem) {
            var1 = JCheckBoxMenuItem.class;
         } else if (var0 instanceof JRadioButtonMenuItem) {
            var1 = JRadioButtonMenuItem.class;
         } else if (var0 instanceof JMenu) {
            var1 = JMenu.class;
         } else if (var0 instanceof JMenuItem) {
            var1 = JMenuItem.class;
         }

         return var1;
      }

      private static Class<? extends JMenuItem> getType(String var0) {
         Class var1 = null;
         if (var0 == "CheckBoxMenuItem") {
            var1 = JCheckBoxMenuItem.class;
         } else if (var0 == "RadioButtonMenuItem") {
            var1 = JRadioButtonMenuItem.class;
         } else if (var0 == "Menu") {
            var1 = JMenu.class;
         } else if (var0 == "MenuItem") {
            var1 = JMenuItem.class;
         } else {
            var1 = JMenuItem.class;
         }

         return var1;
      }

      private static class VistaMenuItemCheckIcon implements Icon, UIResource, Serializable {
         private final JMenuItem menuItem;
         private final Class<? extends JMenuItem> type;

         VistaMenuItemCheckIcon(JMenuItem var1) {
            this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType((Component)var1);
            this.menuItem = var1;
         }

         VistaMenuItemCheckIcon(String var1) {
            this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType(var1);
            this.menuItem = null;
         }

         public int getIconHeight() {
            Icon var1 = this.getLaFIcon();
            if (var1 != null) {
               return var1.getIconHeight();
            } else {
               Icon var2 = this.getIcon();
               boolean var3 = false;
               int var6;
               if (var2 != null) {
                  var6 = var2.getIconHeight();
               } else {
                  XPStyle var4 = XPStyle.getXP();
                  if (var4 != null) {
                     XPStyle.Skin var5 = var4.getSkin((Component)null, TMSchema.Part.MP_POPUPCHECK);
                     var6 = var5.getHeight();
                  } else {
                     var6 = 16;
                  }
               }

               var6 += 6;
               return var6;
            }
         }

         public int getIconWidth() {
            Icon var1 = this.getLaFIcon();
            if (var1 != null) {
               return var1.getIconWidth();
            } else {
               Icon var2 = this.getIcon();
               boolean var3 = false;
               int var4;
               if (var2 != null) {
                  var4 = var2.getIconWidth() + 6;
               } else {
                  var4 = WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth();
               }

               return var4;
            }
         }

         public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
            Icon var5 = this.getLaFIcon();
            if (var5 != null) {
               var5.paintIcon(var1, var2, var3, var4);
            } else {
               assert this.menuItem == null || var1 == this.menuItem;

               Icon var6 = this.getIcon();
               if (this.type == JCheckBoxMenuItem.class || this.type == JRadioButtonMenuItem.class) {
                  AbstractButton var7 = (AbstractButton)var1;
                  if (var7.isSelected()) {
                     TMSchema.Part var8 = TMSchema.Part.MP_POPUPCHECKBACKGROUND;
                     TMSchema.Part var9 = TMSchema.Part.MP_POPUPCHECK;
                     TMSchema.State var10;
                     TMSchema.State var11;
                     if (isEnabled(var1, (TMSchema.State)null)) {
                        var10 = var6 != null ? TMSchema.State.BITMAP : TMSchema.State.NORMAL;
                        var11 = this.type == JRadioButtonMenuItem.class ? TMSchema.State.BULLETNORMAL : TMSchema.State.CHECKMARKNORMAL;
                     } else {
                        var10 = TMSchema.State.DISABLEDPUSHED;
                        var11 = this.type == JRadioButtonMenuItem.class ? TMSchema.State.BULLETDISABLED : TMSchema.State.CHECKMARKDISABLED;
                     }

                     XPStyle var12 = XPStyle.getXP();
                     if (var12 != null) {
                        XPStyle.Skin var13 = var12.getSkin(var1, var8);
                        var13.paintSkin(var2, var3, var4, this.getIconWidth(), this.getIconHeight(), var10);
                        if (var6 == null) {
                           var13 = var12.getSkin(var1, var9);
                           var13.paintSkin(var2, var3 + 3, var4 + 3, var11);
                        }
                     }
                  }
               }

               if (var6 != null) {
                  var6.paintIcon(var1, var2, var3 + 3, var4 + 3);
               }

            }
         }

         private static WindowsMenuItemUIAccessor getAccessor(JMenuItem var0) {
            WindowsMenuItemUIAccessor var1 = null;
            ButtonUI var2 = var0 != null ? var0.getUI() : null;
            if (var2 instanceof WindowsMenuItemUI) {
               var1 = ((WindowsMenuItemUI)var2).accessor;
            } else if (var2 instanceof WindowsMenuUI) {
               var1 = ((WindowsMenuUI)var2).accessor;
            } else if (var2 instanceof WindowsCheckBoxMenuItemUI) {
               var1 = ((WindowsCheckBoxMenuItemUI)var2).accessor;
            } else if (var2 instanceof WindowsRadioButtonMenuItemUI) {
               var1 = ((WindowsRadioButtonMenuItemUI)var2).accessor;
            }

            return var1;
         }

         private static boolean isEnabled(Component var0, TMSchema.State var1) {
            if (var1 == null && var0 instanceof JMenuItem) {
               WindowsMenuItemUIAccessor var2 = getAccessor((JMenuItem)var0);
               if (var2 != null) {
                  var1 = var2.getState((JMenuItem)var0);
               }
            }

            if (var1 == null) {
               return var0 != null ? var0.isEnabled() : true;
            } else {
               return var1 != TMSchema.State.DISABLED && var1 != TMSchema.State.DISABLEDHOT && var1 != TMSchema.State.DISABLEDPUSHED;
            }
         }

         private Icon getIcon() {
            Icon var1 = null;
            if (this.menuItem == null) {
               return var1;
            } else {
               WindowsMenuItemUIAccessor var2 = getAccessor(this.menuItem);
               TMSchema.State var3 = var2 != null ? var2.getState(this.menuItem) : null;
               if (isEnabled(this.menuItem, (TMSchema.State)null)) {
                  if (var3 == TMSchema.State.PUSHED) {
                     var1 = this.menuItem.getPressedIcon();
                  } else {
                     var1 = this.menuItem.getIcon();
                  }
               } else {
                  var1 = this.menuItem.getDisabledIcon();
               }

               return var1;
            }
         }

         private Icon getLaFIcon() {
            Icon var1 = (Icon)UIManager.getDefaults().get(typeToString(this.type));
            if (var1 instanceof WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon && ((WindowsIconFactory.VistaMenuItemCheckIconFactory.VistaMenuItemCheckIcon)var1).type == this.type) {
               var1 = null;
            }

            return var1;
         }

         private static String typeToString(Class<? extends JMenuItem> var0) {
            assert var0 == JMenuItem.class || var0 == JMenu.class || var0 == JCheckBoxMenuItem.class || var0 == JRadioButtonMenuItem.class;

            StringBuilder var1 = new StringBuilder(var0.getName());
            var1.delete(0, var1.lastIndexOf("J") + 1);
            var1.append(".checkIcon");
            return var1.toString();
         }
      }
   }

   private static class MenuArrowIcon implements Icon, UIResource, Serializable {
      private MenuArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         XPStyle var5 = XPStyle.getXP();
         if (WindowsMenuItemUI.isVistaPainting(var5)) {
            TMSchema.State var6 = TMSchema.State.NORMAL;
            if (var1 instanceof JMenuItem) {
               var6 = ((JMenuItem)var1).getModel().isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
            }

            XPStyle.Skin var7 = var5.getSkin(var1, TMSchema.Part.MP_POPUPSUBMENU);
            if (WindowsGraphicsUtils.isLeftToRight(var1)) {
               var7.paintSkin(var2, var3, var4, var6);
            } else {
               Graphics2D var8 = (Graphics2D)var2.create();
               var8.translate(var3 + var7.getWidth(), var4);
               var8.scale(-1.0D, 1.0D);
               var7.paintSkin(var8, 0, 0, var6);
               var8.dispose();
            }
         } else {
            var2.translate(var3, var4);
            if (WindowsGraphicsUtils.isLeftToRight(var1)) {
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

      }

      public int getIconWidth() {
         XPStyle var1 = XPStyle.getXP();
         if (WindowsMenuItemUI.isVistaPainting(var1)) {
            XPStyle.Skin var2 = var1.getSkin((Component)null, TMSchema.Part.MP_POPUPSUBMENU);
            return var2.getWidth();
         } else {
            return 4;
         }
      }

      public int getIconHeight() {
         XPStyle var1 = XPStyle.getXP();
         if (WindowsMenuItemUI.isVistaPainting(var1)) {
            XPStyle.Skin var2 = var1.getSkin((Component)null, TMSchema.Part.MP_POPUPSUBMENU);
            return var2.getHeight();
         } else {
            return 8;
         }
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
            var2.fillRoundRect(var3 + 3, var4 + 3, this.getIconWidth() - 6, this.getIconHeight() - 6, 4, 4);
         }

      }

      public int getIconWidth() {
         return 12;
      }

      public int getIconHeight() {
         return 12;
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
            var4 -= this.getIconHeight() / 2;
            var2.drawLine(var3 + 9, var4 + 3, var3 + 9, var4 + 3);
            var2.drawLine(var3 + 8, var4 + 4, var3 + 9, var4 + 4);
            var2.drawLine(var3 + 7, var4 + 5, var3 + 9, var4 + 5);
            var2.drawLine(var3 + 6, var4 + 6, var3 + 8, var4 + 6);
            var2.drawLine(var3 + 3, var4 + 7, var3 + 7, var4 + 7);
            var2.drawLine(var3 + 4, var4 + 8, var3 + 6, var4 + 8);
            var2.drawLine(var3 + 5, var4 + 9, var3 + 5, var4 + 9);
            var2.drawLine(var3 + 3, var4 + 5, var3 + 3, var4 + 5);
            var2.drawLine(var3 + 3, var4 + 6, var3 + 4, var4 + 6);
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
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         XPStyle var7 = XPStyle.getXP();
         if (var7 != null) {
            TMSchema.Part var8 = TMSchema.Part.BP_RADIOBUTTON;
            XPStyle.Skin var9 = var7.getSkin(var5, var8);
            boolean var11 = false;
            TMSchema.State var10;
            if (var6.isSelected()) {
               var10 = TMSchema.State.CHECKEDNORMAL;
               if (!var6.isEnabled()) {
                  var10 = TMSchema.State.CHECKEDDISABLED;
               } else if (var6.isPressed() && var6.isArmed()) {
                  var10 = TMSchema.State.CHECKEDPRESSED;
               } else if (var6.isRollover()) {
                  var10 = TMSchema.State.CHECKEDHOT;
               }
            } else {
               var10 = TMSchema.State.UNCHECKEDNORMAL;
               if (!var6.isEnabled()) {
                  var10 = TMSchema.State.UNCHECKEDDISABLED;
               } else if (var6.isPressed() && var6.isArmed()) {
                  var10 = TMSchema.State.UNCHECKEDPRESSED;
               } else if (var6.isRollover()) {
                  var10 = TMSchema.State.UNCHECKEDHOT;
               }
            }

            var9.paintSkin(var2, var3, var4, var10);
         } else {
            if ((!var6.isPressed() || !var6.isArmed()) && var6.isEnabled()) {
               var2.setColor(UIManager.getColor("RadioButton.interiorBackground"));
            } else {
               var2.setColor(UIManager.getColor("RadioButton.background"));
            }

            var2.fillRect(var3 + 2, var4 + 2, 8, 8);
            var2.setColor(UIManager.getColor("RadioButton.shadow"));
            var2.drawLine(var3 + 4, var4 + 0, var3 + 7, var4 + 0);
            var2.drawLine(var3 + 2, var4 + 1, var3 + 3, var4 + 1);
            var2.drawLine(var3 + 8, var4 + 1, var3 + 9, var4 + 1);
            var2.drawLine(var3 + 1, var4 + 2, var3 + 1, var4 + 3);
            var2.drawLine(var3 + 0, var4 + 4, var3 + 0, var4 + 7);
            var2.drawLine(var3 + 1, var4 + 8, var3 + 1, var4 + 9);
            var2.setColor(UIManager.getColor("RadioButton.highlight"));
            var2.drawLine(var3 + 2, var4 + 10, var3 + 3, var4 + 10);
            var2.drawLine(var3 + 4, var4 + 11, var3 + 7, var4 + 11);
            var2.drawLine(var3 + 8, var4 + 10, var3 + 9, var4 + 10);
            var2.drawLine(var3 + 10, var4 + 9, var3 + 10, var4 + 8);
            var2.drawLine(var3 + 11, var4 + 7, var3 + 11, var4 + 4);
            var2.drawLine(var3 + 10, var4 + 3, var3 + 10, var4 + 2);
            var2.setColor(UIManager.getColor("RadioButton.darkShadow"));
            var2.drawLine(var3 + 4, var4 + 1, var3 + 7, var4 + 1);
            var2.drawLine(var3 + 2, var4 + 2, var3 + 3, var4 + 2);
            var2.drawLine(var3 + 8, var4 + 2, var3 + 9, var4 + 2);
            var2.drawLine(var3 + 2, var4 + 3, var3 + 2, var4 + 3);
            var2.drawLine(var3 + 1, var4 + 4, var3 + 1, var4 + 7);
            var2.drawLine(var3 + 2, var4 + 8, var3 + 2, var4 + 8);
            var2.setColor(UIManager.getColor("RadioButton.light"));
            var2.drawLine(var3 + 2, var4 + 9, var3 + 3, var4 + 9);
            var2.drawLine(var3 + 4, var4 + 10, var3 + 7, var4 + 10);
            var2.drawLine(var3 + 8, var4 + 9, var3 + 9, var4 + 9);
            var2.drawLine(var3 + 9, var4 + 8, var3 + 9, var4 + 8);
            var2.drawLine(var3 + 10, var4 + 7, var3 + 10, var4 + 4);
            var2.drawLine(var3 + 9, var4 + 3, var3 + 9, var4 + 3);
            if (var6.isSelected()) {
               if (var6.isEnabled()) {
                  var2.setColor(UIManager.getColor("RadioButton.foreground"));
               } else {
                  var2.setColor(UIManager.getColor("RadioButton.shadow"));
               }

               var2.fillRect(var3 + 4, var4 + 5, 4, 2);
               var2.fillRect(var3 + 5, var4 + 4, 2, 4);
            }
         }

      }

      public int getIconWidth() {
         XPStyle var1 = XPStyle.getXP();
         return var1 != null ? var1.getSkin((Component)null, TMSchema.Part.BP_RADIOBUTTON).getWidth() : 13;
      }

      public int getIconHeight() {
         XPStyle var1 = XPStyle.getXP();
         return var1 != null ? var1.getSkin((Component)null, TMSchema.Part.BP_RADIOBUTTON).getHeight() : 13;
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
         JCheckBox var5 = (JCheckBox)var1;
         ButtonModel var6 = var5.getModel();
         XPStyle var7 = XPStyle.getXP();
         if (var7 != null) {
            TMSchema.State var8;
            if (var6.isSelected()) {
               var8 = TMSchema.State.CHECKEDNORMAL;
               if (!var6.isEnabled()) {
                  var8 = TMSchema.State.CHECKEDDISABLED;
               } else if (var6.isPressed() && var6.isArmed()) {
                  var8 = TMSchema.State.CHECKEDPRESSED;
               } else if (var6.isRollover()) {
                  var8 = TMSchema.State.CHECKEDHOT;
               }
            } else {
               var8 = TMSchema.State.UNCHECKEDNORMAL;
               if (!var6.isEnabled()) {
                  var8 = TMSchema.State.UNCHECKEDDISABLED;
               } else if (var6.isPressed() && var6.isArmed()) {
                  var8 = TMSchema.State.UNCHECKEDPRESSED;
               } else if (var6.isRollover()) {
                  var8 = TMSchema.State.UNCHECKEDHOT;
               }
            }

            TMSchema.Part var9 = TMSchema.Part.BP_CHECKBOX;
            var7.getSkin(var1, var9).paintSkin(var2, var3, var4, var8);
         } else {
            if (!var5.isBorderPaintedFlat()) {
               var2.setColor(UIManager.getColor("CheckBox.shadow"));
               var2.drawLine(var3, var4, var3 + 11, var4);
               var2.drawLine(var3, var4 + 1, var3, var4 + 11);
               var2.setColor(UIManager.getColor("CheckBox.highlight"));
               var2.drawLine(var3 + 12, var4, var3 + 12, var4 + 12);
               var2.drawLine(var3, var4 + 12, var3 + 11, var4 + 12);
               var2.setColor(UIManager.getColor("CheckBox.darkShadow"));
               var2.drawLine(var3 + 1, var4 + 1, var3 + 10, var4 + 1);
               var2.drawLine(var3 + 1, var4 + 2, var3 + 1, var4 + 10);
               var2.setColor(UIManager.getColor("CheckBox.light"));
               var2.drawLine(var3 + 1, var4 + 11, var3 + 11, var4 + 11);
               var2.drawLine(var3 + 11, var4 + 1, var3 + 11, var4 + 10);
               if ((!var6.isPressed() || !var6.isArmed()) && var6.isEnabled()) {
                  var2.setColor(UIManager.getColor("CheckBox.interiorBackground"));
               } else {
                  var2.setColor(UIManager.getColor("CheckBox.background"));
               }

               var2.fillRect(var3 + 2, var4 + 2, 9, 9);
            } else {
               var2.setColor(UIManager.getColor("CheckBox.shadow"));
               var2.drawRect(var3 + 1, var4 + 1, 10, 10);
               if ((!var6.isPressed() || !var6.isArmed()) && var6.isEnabled()) {
                  var2.setColor(UIManager.getColor("CheckBox.interiorBackground"));
               } else {
                  var2.setColor(UIManager.getColor("CheckBox.background"));
               }

               var2.fillRect(var3 + 2, var4 + 2, 9, 9);
            }

            if (var6.isEnabled()) {
               var2.setColor(UIManager.getColor("CheckBox.foreground"));
            } else {
               var2.setColor(UIManager.getColor("CheckBox.shadow"));
            }

            if (var6.isSelected()) {
               var2.drawLine(var3 + 9, var4 + 3, var3 + 9, var4 + 3);
               var2.drawLine(var3 + 8, var4 + 4, var3 + 9, var4 + 4);
               var2.drawLine(var3 + 7, var4 + 5, var3 + 9, var4 + 5);
               var2.drawLine(var3 + 6, var4 + 6, var3 + 8, var4 + 6);
               var2.drawLine(var3 + 3, var4 + 7, var3 + 7, var4 + 7);
               var2.drawLine(var3 + 4, var4 + 8, var3 + 6, var4 + 8);
               var2.drawLine(var3 + 5, var4 + 9, var3 + 5, var4 + 9);
               var2.drawLine(var3 + 3, var4 + 5, var3 + 3, var4 + 5);
               var2.drawLine(var3 + 3, var4 + 6, var3 + 4, var4 + 6);
            }
         }

      }

      public int getIconWidth() {
         XPStyle var1 = XPStyle.getXP();
         return var1 != null ? var1.getSkin((Component)null, TMSchema.Part.BP_CHECKBOX).getWidth() : 13;
      }

      public int getIconHeight() {
         XPStyle var1 = XPStyle.getXP();
         return var1 != null ? var1.getSkin((Component)null, TMSchema.Part.BP_CHECKBOX).getHeight() : 13;
      }

      // $FF: synthetic method
      CheckBoxIcon(Object var1) {
         this();
      }
   }

   private static class ResizeIcon implements Icon, Serializable {
      private ResizeIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.setColor(UIManager.getColor("InternalFrame.resizeIconHighlight"));
         var2.drawLine(0, 11, 11, 0);
         var2.drawLine(4, 11, 11, 4);
         var2.drawLine(8, 11, 11, 8);
         var2.setColor(UIManager.getColor("InternalFrame.resizeIconShadow"));
         var2.drawLine(1, 11, 11, 1);
         var2.drawLine(2, 11, 11, 2);
         var2.drawLine(5, 11, 11, 5);
         var2.drawLine(6, 11, 11, 6);
         var2.drawLine(9, 11, 11, 9);
         var2.drawLine(10, 11, 11, 10);
      }

      public int getIconWidth() {
         return 13;
      }

      public int getIconHeight() {
         return 13;
      }

      // $FF: synthetic method
      ResizeIcon(Object var1) {
         this();
      }
   }

   private static class FrameButtonIcon implements Icon, Serializable {
      private TMSchema.Part part;

      private FrameButtonIcon(TMSchema.Part var1) {
         this.part = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         int var5 = this.getIconWidth();
         int var6 = this.getIconHeight();
         XPStyle var7 = XPStyle.getXP();
         if (var7 != null) {
            XPStyle.Skin var8 = var7.getSkin(var1, this.part);
            AbstractButton var9 = (AbstractButton)var1;
            ButtonModel var10 = var9.getModel();
            JInternalFrame var11 = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, var9);
            boolean var12 = var11 != null && var11.isSelected();
            TMSchema.State var13;
            if (var12) {
               if (!var10.isEnabled()) {
                  var13 = TMSchema.State.DISABLED;
               } else if (var10.isArmed() && var10.isPressed()) {
                  var13 = TMSchema.State.PUSHED;
               } else if (var10.isRollover()) {
                  var13 = TMSchema.State.HOT;
               } else {
                  var13 = TMSchema.State.NORMAL;
               }
            } else if (!var10.isEnabled()) {
               var13 = TMSchema.State.INACTIVEDISABLED;
            } else if (var10.isArmed() && var10.isPressed()) {
               var13 = TMSchema.State.INACTIVEPUSHED;
            } else if (var10.isRollover()) {
               var13 = TMSchema.State.INACTIVEHOT;
            } else {
               var13 = TMSchema.State.INACTIVENORMAL;
            }

            var8.paintSkin(var2, 0, 0, var5, var6, var13);
         } else {
            var2.setColor(Color.black);
            int var16 = var5 / 12 + 2;
            int var17 = var6 / 5;
            int var18 = var6 - var17 * 2 - 1;
            int var19 = var5 * 3 / 4 - 3;
            int var20 = Math.max(var6 / 8, 2);
            int var21 = Math.max(var5 / 15, 1);
            if (this.part == TMSchema.Part.WP_CLOSEBUTTON) {
               byte var14;
               if (var5 > 47) {
                  var14 = 6;
               } else if (var5 > 37) {
                  var14 = 5;
               } else if (var5 > 26) {
                  var14 = 4;
               } else if (var5 > 16) {
                  var14 = 3;
               } else if (var5 > 12) {
                  var14 = 2;
               } else {
                  var14 = 1;
               }

               var17 = var6 / 12 + 2;
               if (var14 == 1) {
                  if (var19 % 2 == 1) {
                     ++var16;
                     ++var19;
                  }

                  var2.drawLine(var16, var17, var16 + var19 - 2, var17 + var19 - 2);
                  var2.drawLine(var16 + var19 - 2, var17, var16, var17 + var19 - 2);
               } else if (var14 == 2) {
                  if (var19 > 6) {
                     ++var16;
                     --var19;
                  }

                  var2.drawLine(var16, var17, var16 + var19 - 2, var17 + var19 - 2);
                  var2.drawLine(var16 + var19 - 2, var17, var16, var17 + var19 - 2);
                  var2.drawLine(var16 + 1, var17, var16 + var19 - 1, var17 + var19 - 2);
                  var2.drawLine(var16 + var19 - 1, var17, var16 + 1, var17 + var19 - 2);
               } else {
                  var16 += 2;
                  ++var17;
                  var19 -= 2;
                  var2.drawLine(var16, var17, var16 + var19 - 1, var17 + var19 - 1);
                  var2.drawLine(var16 + var19 - 1, var17, var16, var17 + var19 - 1);
                  var2.drawLine(var16 + 1, var17, var16 + var19 - 1, var17 + var19 - 2);
                  var2.drawLine(var16 + var19 - 2, var17, var16, var17 + var19 - 2);
                  var2.drawLine(var16, var17 + 1, var16 + var19 - 2, var17 + var19 - 1);
                  var2.drawLine(var16 + var19 - 1, var17 + 1, var16 + 1, var17 + var19 - 1);

                  for(int var15 = 4; var15 <= var14; ++var15) {
                     var2.drawLine(var16 + var15 - 2, var17, var16 + var19 - 1, var17 + var19 - var15 + 1);
                     var2.drawLine(var16, var17 + var15 - 2, var16 + var19 - var15 + 1, var17 + var19 - 1);
                     var2.drawLine(var16 + var19 - var15 + 1, var17, var16, var17 + var19 - var15 + 1);
                     var2.drawLine(var16 + var19 - 1, var17 + var15 - 2, var16 + var15 - 2, var17 + var19 - 1);
                  }
               }
            } else if (this.part == TMSchema.Part.WP_MINBUTTON) {
               var2.fillRect(var16, var17 + var18 - var20, var19 - var19 / 3, var20);
            } else if (this.part == TMSchema.Part.WP_MAXBUTTON) {
               var2.fillRect(var16, var17, var19, var20);
               var2.fillRect(var16, var17, var21, var18);
               var2.fillRect(var16 + var19 - var21, var17, var21, var18);
               var2.fillRect(var16, var17 + var18 - var21, var19, var21);
            } else if (this.part == TMSchema.Part.WP_RESTOREBUTTON) {
               var2.fillRect(var16 + var19 / 3, var17, var19 - var19 / 3, var20);
               var2.fillRect(var16 + var19 / 3, var17, var21, var18 / 3);
               var2.fillRect(var16 + var19 - var21, var17, var21, var18 - var18 / 3);
               var2.fillRect(var16 + var19 - var19 / 3, var17 + var18 - var18 / 3 - var21, var19 / 3, var21);
               var2.fillRect(var16, var17 + var18 / 3, var19 - var19 / 3, var20);
               var2.fillRect(var16, var17 + var18 / 3, var21, var18 - var18 / 3);
               var2.fillRect(var16 + var19 - var19 / 3 - var21, var17 + var18 / 3, var21, var18 - var18 / 3);
               var2.fillRect(var16, var17 + var18 - var21, var19 - var19 / 3, var21);
            }
         }

      }

      public int getIconWidth() {
         int var1;
         if (XPStyle.getXP() != null) {
            var1 = UIManager.getInt("InternalFrame.titleButtonHeight") - 2;
            Dimension var2 = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
            if (var2 != null && var2.width != 0 && var2.height != 0) {
               var1 = (int)((float)var1 * (float)var2.width / (float)var2.height);
            }
         } else {
            var1 = UIManager.getInt("InternalFrame.titleButtonWidth") - 2;
         }

         if (XPStyle.getXP() != null) {
            var1 -= 2;
         }

         return var1;
      }

      public int getIconHeight() {
         int var1 = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
         return var1;
      }

      // $FF: synthetic method
      FrameButtonIcon(TMSchema.Part var1, Object var2) {
         this(var1);
      }
   }
}
