package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class AquaMenuPainter {
   static final byte kShiftGlyph = 5;
   static final byte kOptionGlyph = 7;
   static final byte kControlGlyph = 6;
   static final byte kPencilGlyph = 15;
   static final byte kCommandMark = 17;
   static final char kUBlackDiamond = '◆';
   static final char kUCheckMark = '✓';
   static final char kUControlGlyph = '⌃';
   static final char kUOptionGlyph = '⌥';
   static final char kUEnterGlyph = '⌤';
   static final char kUCommandGlyph = '⌘';
   static final char kULeftDeleteGlyph = '⌫';
   static final char kURightDeleteGlyph = '⌦';
   static final char kUShiftGlyph = '⇧';
   static final char kUCapsLockGlyph = '⇪';
   static final int ALT_GRAPH_MASK = 32;
   static final int sUnsupportedModifiersMask = -48;
   static final AquaUtils.RecyclableSingleton<AquaMenuPainter> sPainter = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaMenuPainter.class);
   static final int defaultMenuItemGap = 2;
   static final int kAcceleratorArrowSpace = 16;
   protected final AquaMenuPainter.RecyclableBorder menuBarPainter = new AquaMenuPainter.RecyclableBorder("MenuBar.backgroundPainter");
   protected final AquaMenuPainter.RecyclableBorder selectedMenuBarItemPainter = new AquaMenuPainter.RecyclableBorder("MenuBar.selectedBackgroundPainter");
   protected final AquaMenuPainter.RecyclableBorder selectedMenuItemPainter = new AquaMenuPainter.RecyclableBorder("MenuItem.selectedBackgroundPainter");

   static String getKeyModifiersText(int var0, boolean var1) {
      return getKeyModifiersUnicode(var0, var1);
   }

   private static String getKeyModifiersUnicode(int var0, boolean var1) {
      StringBuilder var2 = new StringBuilder(2);
      if (var1) {
         if ((var0 & 2) != 0) {
            var2.append('⌃');
         }

         if ((var0 & 40) != 0) {
            var2.append('⌥');
         }

         if ((var0 & 1) != 0) {
            var2.append('⇧');
         }

         if ((var0 & 4) != 0) {
            var2.append('⌘');
         }
      } else {
         if ((var0 & 4) != 0) {
            var2.append('⌘');
         }

         if ((var0 & 1) != 0) {
            var2.append('⇧');
         }

         if ((var0 & 40) != 0) {
            var2.append('⌥');
         }

         if ((var0 & 2) != 0) {
            var2.append('⌃');
         }
      }

      return var2.toString();
   }

   static AquaMenuPainter instance() {
      return (AquaMenuPainter)sPainter.get();
   }

   public void paintMenuBarBackground(Graphics var1, int var2, int var3, JComponent var4) {
      var1.setColor(var4 == null ? Color.white : var4.getBackground());
      var1.fillRect(0, 0, var2, var3);
      ((Border)this.menuBarPainter.get()).paintBorder((Component)null, var1, 0, 0, var2, var3);
   }

   public void paintSelectedMenuTitleBackground(Graphics var1, int var2, int var3) {
      ((Border)this.selectedMenuBarItemPainter.get()).paintBorder((Component)null, var1, -1, 0, var2 + 2, var3);
   }

   public void paintSelectedMenuItemBackground(Graphics var1, int var2, int var3) {
      ((Border)this.selectedMenuItemPainter.get()).paintBorder((Component)null, var1, 0, 0, var2, var3);
   }

   protected void paintMenuItem(AquaMenuPainter.Client var1, Graphics var2, JComponent var3, Icon var4, Icon var5, Color var6, Color var7, Color var8, Color var9, int var10, Font var11) {
      JMenuItem var12 = (JMenuItem)var3;
      ButtonModel var13 = var12.getModel();
      int var14 = var12.getWidth();
      int var15 = var12.getHeight();
      Insets var16 = var3.getInsets();
      Rectangle var17 = new Rectangle(0, 0, var14, var15);
      var17.x += var16.left;
      var17.y += var16.top;
      var17.width -= var16.right + var17.x;
      var17.height -= var16.bottom + var17.y;
      Font var18 = var2.getFont();
      Color var19 = var2.getColor();
      Font var20 = var3.getFont();
      var2.setFont(var20);
      FontMetrics var21 = var2.getFontMetrics(var20);
      FontMetrics var22 = var2.getFontMetrics(var11);
      if (var3.isOpaque()) {
         var1.paintBackground(var2, var3, var14, var15);
      }

      KeyStroke var23 = var12.getAccelerator();
      String var24 = "";
      String var25 = "";
      boolean var26 = AquaUtils.isLeftToRight(var3);
      if (var23 != null) {
         int var27 = var23.getModifiers();
         if (var27 > 0) {
            var24 = getKeyModifiersText(var27, var26);
         }

         int var28 = var23.getKeyCode();
         if (var28 != 0) {
            var25 = KeyEvent.getKeyText(var28);
         } else {
            var25 = var25 + var23.getKeyChar();
         }
      }

      Rectangle var43 = new Rectangle();
      Rectangle var44 = new Rectangle();
      Rectangle var29 = new Rectangle();
      Rectangle var30 = new Rectangle();
      Rectangle var31 = new Rectangle();
      String var32 = this.layoutMenuItem(var12, var21, var12.getText(), var22, var25, var24, var12.getIcon(), var4, var5, var12.getVerticalAlignment(), var12.getHorizontalAlignment(), var12.getVerticalTextPosition(), var12.getHorizontalTextPosition(), var17, var43, var44, var29, var30, var31, var12.getText() == null ? 0 : var10, var10);
      Container var33 = var12.getParent();
      boolean var34 = var33 instanceof JMenuBar;

      Container var35;
      for(var35 = var33; var35 != null && !(var35 instanceof JPopupMenu); var35 = var35.getParent()) {
      }

      boolean var36 = var13.isEnabled() && (var35 == null || var35.isVisible());
      boolean var37 = false;
      if (!var36) {
         var2.setColor(var8);
      } else if (!var13.isArmed() && (!(var3 instanceof JMenu) || !var13.isSelected())) {
         var2.setColor(var34 ? var33.getForeground() : var12.getForeground());
      } else {
         var2.setColor(var9);
         var37 = true;
      }

      if (var12.getIcon() != null) {
         this.paintIcon(var2, var12, var43, var36);
      }

      if (var4 != null) {
         this.paintCheck(var2, var12, var4, var30);
      }

      int var39;
      if (var25 != null && !var25.equals("")) {
         int var38 = var29.y + var21.getAscent();
         if (var24.equals("")) {
            SwingUtilities2.drawString(var3, var2, var25, var29.x, var38);
         } else {
            var39 = var23.getModifiers();
            short var40 = 0;
            if ((var39 & 32) > 0) {
               var40 = 8997;
            }

            int var41 = Math.max(var21.charWidth('M'), SwingUtilities.computeStringWidth(var21, var25));
            if (var26) {
               var2.setFont(var11);
               this.drawString(var2, var3, var24, var40, var29.x, var38, var36, var37);
               var2.setFont(var20);
               SwingUtilities2.drawString(var3, var2, var25, var29.x + var29.width - var41, var38);
            } else {
               int var42 = var29.x + var41;
               var2.setFont(var11);
               this.drawString(var2, var3, var24, var40, var42, var38, var36, var37);
               var2.setFont(var20);
               SwingUtilities2.drawString(var3, var2, var25, var42 - var21.stringWidth(var25), var38);
            }
         }
      }

      if (var32 != null && !var32.equals("")) {
         View var45 = (View)var3.getClientProperty("html");
         if (var45 != null) {
            var45.paint(var2, var44);
         } else {
            var39 = AquaMnemonicHandler.isMnemonicHidden() ? -1 : var13.getMnemonic();
            this.drawString(var2, var3, var32, var39, var44.x, var44.y + var21.getAscent(), var36, var37);
         }
      }

      if (var5 != null) {
         this.paintArrow(var2, var12, var13, var5, var31);
      }

      var2.setColor(var19);
      var2.setFont(var18);
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4, Font var5) {
      JMenuItem var6 = (JMenuItem)var1;
      Icon var7 = var6.getIcon();
      String var8 = var6.getText();
      KeyStroke var9 = var6.getAccelerator();
      String var10 = "";
      String var11 = "";
      if (var9 != null) {
         int var12 = var9.getModifiers();
         if (var12 > 0) {
            var11 = getKeyModifiersText(var12, true);
         }

         int var13 = var9.getKeyCode();
         if (var13 != 0) {
            var10 = KeyEvent.getKeyText(var13);
         } else {
            var10 = var10 + var9.getKeyChar();
         }
      }

      Font var24 = var6.getFont();
      FontMetrics var25 = var6.getFontMetrics(var24);
      FontMetrics var14 = var6.getFontMetrics(var5);
      Rectangle var15 = new Rectangle();
      Rectangle var16 = new Rectangle();
      Rectangle var17 = new Rectangle();
      Rectangle var18 = new Rectangle();
      Rectangle var19 = new Rectangle();
      Rectangle var20 = new Rectangle(32767, 32767);
      this.layoutMenuItem(var6, var25, var8, var14, var10, var11, var7, var2, var3, var6.getVerticalAlignment(), var6.getHorizontalAlignment(), var6.getVerticalTextPosition(), var6.getHorizontalTextPosition(), var20, var15, var16, var17, var18, var19, var8 == null ? 0 : var4, var4);
      Rectangle var21 = new Rectangle();
      var21.setBounds(var16);
      var21 = SwingUtilities.computeUnion(var15.x, var15.y, var15.width, var15.height, var21);
      boolean var22 = var10 == null || var10.equals("");
      if (!var22) {
         var21.width += var17.width;
      }

      if (!isTopLevelMenu(var6)) {
         var21.width += var18.width;
         var21.width += var4;
         var21.width += var4;
         var21.width += var19.width;
      }

      Insets var23 = var6.getInsets();
      if (var23 != null) {
         var21.width += var23.left + var23.right;
         var21.height += var23.top + var23.bottom;
      }

      var21.width += 4 + var4;
      var21.height = Math.max(var21.height, 18);
      return var21.getSize();
   }

   protected void paintCheck(Graphics var1, JMenuItem var2, Icon var3, Rectangle var4) {
      if (!isTopLevelMenu(var2) && var2.isSelected()) {
         if (var2.isArmed() && var3 instanceof AquaIcon.InvertableIcon) {
            ((AquaIcon.InvertableIcon)var3).getInvertedIcon().paintIcon(var2, var1, var4.x, var4.y);
         } else {
            var3.paintIcon(var2, var1, var4.x, var4.y);
         }

      }
   }

   protected void paintIcon(Graphics var1, JMenuItem var2, Rectangle var3, boolean var4) {
      ButtonModel var5 = var2.getModel();
      Icon var6;
      if (!var4) {
         var6 = var2.getDisabledIcon();
      } else if (var5.isPressed() && var5.isArmed()) {
         var6 = var2.getPressedIcon();
         if (var6 == null) {
            var6 = var2.getIcon();
         }
      } else {
         var6 = var2.getIcon();
      }

      if (var6 != null) {
         var6.paintIcon(var2, var1, var3.x, var3.y);
      }

   }

   protected void paintArrow(Graphics var1, JMenuItem var2, ButtonModel var3, Icon var4, Rectangle var5) {
      if (!isTopLevelMenu(var2)) {
         if (var2 instanceof JMenu && (var3.isArmed() || var3.isSelected()) && var4 instanceof AquaIcon.InvertableIcon) {
            ((AquaIcon.InvertableIcon)var4).getInvertedIcon().paintIcon(var2, var1, var5.x, var5.y);
         } else {
            var4.paintIcon(var2, var1, var5.x, var5.y);
         }

      }
   }

   public void drawString(Graphics var1, JComponent var2, String var3, int var4, int var5, int var6, boolean var7, boolean var8) {
      int var11 = -1;
      if (var4 != 0) {
         char var10 = Character.toUpperCase((char)var4);
         char var9 = Character.toLowerCase((char)var4);
         int var13 = var3.indexOf(var10);
         int var12 = var3.indexOf(var9);
         if (var13 == -1) {
            var11 = var12;
         } else if (var12 == -1) {
            var11 = var13;
         } else {
            var11 = var12 < var13 ? var12 : var13;
         }
      }

      SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var3, var11, var5, var6);
   }

   private static boolean isTopLevelMenu(JMenuItem var0) {
      return var0 instanceof JMenu && ((JMenu)var0).isTopLevelMenu();
   }

   private String layoutMenuItem(JMenuItem var1, FontMetrics var2, String var3, FontMetrics var4, String var5, String var6, Icon var7, Icon var8, Icon var9, int var10, int var11, int var12, int var13, Rectangle var14, Rectangle var15, Rectangle var16, Rectangle var17, Rectangle var18, Rectangle var19, int var20, int var21) {
      SwingUtilities.layoutCompoundLabel(var1, var2, var3, var7, var10, 2, var12, var13, var14, var15, var16, var20);
      boolean var22 = var5 == null || var5.equals("");
      if (var22) {
         var17.width = var17.height = 0;
         var5 = "";
      } else {
         var17.width = SwingUtilities.computeStringWidth(var4, var6);
         var17.width += Math.max(var2.charWidth('M'), SwingUtilities.computeStringWidth(var2, var5));
         var17.height = var4.getHeight();
      }

      boolean var23 = isTopLevelMenu(var1);
      if (!var23) {
         if (var8 != null) {
            var18.width = var8.getIconWidth();
            var18.height = var8.getIconHeight();
         } else {
            var18.width = var18.height = 16;
         }

         if (var9 != null) {
            var19.width = var9.getIconWidth();
            var19.height = var9.getIconHeight();
         } else {
            var19.width = var19.height = 16;
         }

         var16.x += 12;
         var15.x += 12;
      }

      Rectangle var24 = var15.union(var16);
      var17.x += var14.width - var19.width - var17.width;
      var17.y = var14.y + var14.height / 2 - var17.height / 2;
      if (!var23) {
         var19.x = var14.width - var19.width + 1;
         var19.y = var14.y + var24.height / 2 - var19.height / 2 + 1;
         var18.y = var14.y + var24.height / 2 - var18.height / 2;
         var18.x = 5;
         var16.width += 8;
      }

      if (!AquaUtils.isLeftToRight(var1)) {
         int var25 = var14.width;
         var18.x = var25 - (var18.x + var18.width);
         var15.x = var25 - (var15.x + var15.width);
         var16.x = var25 - (var16.x + var16.width);
         var17.x = var25 - (var17.x + var17.width);
         var19.x = var25 - (var19.x + var19.width);
      }

      var16.x += var21;
      var15.x += var21;
      return var3;
   }

   public static Border getMenuBarPainter() {
      AquaBorder.Default var0 = new AquaBorder.Default();
      var0.painter.state.set(JRSUIConstants.Widget.MENU_BAR);
      return var0;
   }

   public static Border getSelectedMenuBarItemPainter() {
      AquaBorder.Default var0 = new AquaBorder.Default();
      var0.painter.state.set(JRSUIConstants.Widget.MENU_TITLE);
      var0.painter.state.set(JRSUIConstants.State.PRESSED);
      return var0;
   }

   public static Border getSelectedMenuItemPainter() {
      AquaBorder.Default var0 = new AquaBorder.Default();
      var0.painter.state.set(JRSUIConstants.Widget.MENU_ITEM);
      var0.painter.state.set(JRSUIConstants.State.PRESSED);
      return var0;
   }

   static class RecyclableBorder extends AquaUtils.RecyclableSingleton<Border> {
      final String borderName;

      RecyclableBorder(String var1) {
         this.borderName = var1;
      }

      protected Border getInstance() {
         return UIManager.getBorder(this.borderName);
      }
   }

   interface Client {
      void paintBackground(Graphics var1, JComponent var2, int var3, int var4);
   }
}
