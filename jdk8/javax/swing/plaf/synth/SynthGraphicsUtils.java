package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.text.View;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthIcon;

public class SynthGraphicsUtils {
   private Rectangle paintIconR = new Rectangle();
   private Rectangle paintTextR = new Rectangle();
   private Rectangle paintViewR = new Rectangle();
   private Insets paintInsets = new Insets(0, 0, 0, 0);
   private Rectangle iconR = new Rectangle();
   private Rectangle textR = new Rectangle();
   private Rectangle viewR = new Rectangle();
   private Insets viewSizingInsets = new Insets(0, 0, 0, 0);

   public void drawLine(SynthContext var1, Object var2, Graphics var3, int var4, int var5, int var6, int var7) {
      var3.drawLine(var4, var5, var6, var7);
   }

   public void drawLine(SynthContext var1, Object var2, Graphics var3, int var4, int var5, int var6, int var7, Object var8) {
      if ("dashed".equals(var8)) {
         int var9;
         if (var4 == var6) {
            var5 += var5 % 2;

            for(var9 = var5; var9 <= var7; var9 += 2) {
               var3.drawLine(var4, var9, var6, var9);
            }
         } else if (var5 == var7) {
            var4 += var4 % 2;

            for(var9 = var4; var9 <= var6; var9 += 2) {
               var3.drawLine(var9, var5, var9, var7);
            }
         }
      } else {
         this.drawLine(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public String layoutText(SynthContext var1, FontMetrics var2, String var3, Icon var4, int var5, int var6, int var7, int var8, Rectangle var9, Rectangle var10, Rectangle var11, int var12) {
      if (var4 instanceof SynthIcon) {
         SynthGraphicsUtils.SynthIconWrapper var13 = SynthGraphicsUtils.SynthIconWrapper.get((SynthIcon)var4, var1);
         String var14 = SwingUtilities.layoutCompoundLabel(var1.getComponent(), var2, var3, var13, var6, var5, var8, var7, var9, var10, var11, var12);
         SynthGraphicsUtils.SynthIconWrapper.release(var13);
         return var14;
      } else {
         return SwingUtilities.layoutCompoundLabel(var1.getComponent(), var2, var3, var4, var6, var5, var8, var7, var9, var10, var11, var12);
      }
   }

   public int computeStringWidth(SynthContext var1, Font var2, FontMetrics var3, String var4) {
      return SwingUtilities2.stringWidth(var1.getComponent(), var3, var4);
   }

   public Dimension getMinimumSize(SynthContext var1, Font var2, String var3, Icon var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      JComponent var11 = var1.getComponent();
      Dimension var12 = this.getPreferredSize(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      View var13 = (View)var11.getClientProperty("html");
      if (var13 != null) {
         var12.width = (int)((float)var12.width - (var13.getPreferredSpan(0) - var13.getMinimumSpan(0)));
      }

      return var12;
   }

   public Dimension getMaximumSize(SynthContext var1, Font var2, String var3, Icon var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      JComponent var11 = var1.getComponent();
      Dimension var12 = this.getPreferredSize(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      View var13 = (View)var11.getClientProperty("html");
      if (var13 != null) {
         var12.width = (int)((float)var12.width + (var13.getMaximumSpan(0) - var13.getPreferredSpan(0)));
      }

      return var12;
   }

   public int getMaximumCharHeight(SynthContext var1) {
      FontMetrics var2 = var1.getComponent().getFontMetrics(var1.getStyle().getFont(var1));
      return var2.getAscent() + var2.getDescent();
   }

   public Dimension getPreferredSize(SynthContext var1, Font var2, String var3, Icon var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      JComponent var11 = var1.getComponent();
      Insets var12 = var11.getInsets(this.viewSizingInsets);
      int var13 = var12.left + var12.right;
      int var14 = var12.top + var12.bottom;
      if (var4 != null || var3 != null && var2 != null) {
         if (var3 != null && (var4 == null || var2 != null)) {
            FontMetrics var15 = var11.getFontMetrics(var2);
            this.iconR.x = this.iconR.y = this.iconR.width = this.iconR.height = 0;
            this.textR.x = this.textR.y = this.textR.width = this.textR.height = 0;
            this.viewR.x = var13;
            this.viewR.y = var14;
            this.viewR.width = this.viewR.height = 32767;
            this.layoutText(var1, var15, var3, var4, var5, var6, var7, var8, this.viewR, this.iconR, this.textR, var9);
            int var16 = Math.min(this.iconR.x, this.textR.x);
            int var17 = Math.max(this.iconR.x + this.iconR.width, this.textR.x + this.textR.width);
            int var18 = Math.min(this.iconR.y, this.textR.y);
            int var19 = Math.max(this.iconR.y + this.iconR.height, this.textR.y + this.textR.height);
            Dimension var20 = new Dimension(var17 - var16, var19 - var18);
            var20.width += var13;
            var20.height += var14;
            return var20;
         } else {
            return new Dimension(SynthIcon.getIconWidth(var4, var1) + var13, SynthIcon.getIconHeight(var4, var1) + var14);
         }
      } else {
         return new Dimension(var13, var14);
      }
   }

   public void paintText(SynthContext var1, Graphics var2, String var3, Rectangle var4, int var5) {
      this.paintText(var1, var2, var3, var4.x, var4.y, var5);
   }

   public void paintText(SynthContext var1, Graphics var2, String var3, int var4, int var5, int var6) {
      if (var3 != null) {
         JComponent var7 = var1.getComponent();
         FontMetrics var8 = SwingUtilities2.getFontMetrics(var7, var2);
         var5 += var8.getAscent();
         SwingUtilities2.drawStringUnderlineCharAt(var7, var2, var3, var6, var4, var5);
      }

   }

   public void paintText(SynthContext var1, Graphics var2, String var3, Icon var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      if (var4 != null || var3 != null) {
         JComponent var12 = var1.getComponent();
         FontMetrics var13 = SwingUtilities2.getFontMetrics(var12, var2);
         Insets var14 = SynthLookAndFeel.getPaintingInsets(var1, this.paintInsets);
         this.paintViewR.x = var14.left;
         this.paintViewR.y = var14.top;
         this.paintViewR.width = var12.getWidth() - (var14.left + var14.right);
         this.paintViewR.height = var12.getHeight() - (var14.top + var14.bottom);
         this.paintIconR.x = this.paintIconR.y = this.paintIconR.width = this.paintIconR.height = 0;
         this.paintTextR.x = this.paintTextR.y = this.paintTextR.width = this.paintTextR.height = 0;
         String var15 = this.layoutText(var1, var13, var3, var4, var5, var6, var7, var8, this.paintViewR, this.paintIconR, this.paintTextR, var9);
         Rectangle var10000;
         if (var4 != null) {
            Color var16 = var2.getColor();
            if (var1.getStyle().getBoolean(var1, "TableHeader.alignSorterArrow", false) && "TableHeader.renderer".equals(var12.getName())) {
               this.paintIconR.x = this.paintViewR.width - this.paintIconR.width;
            } else {
               var10000 = this.paintIconR;
               var10000.x += var11;
            }

            var10000 = this.paintIconR;
            var10000.y += var11;
            SynthIcon.paintIcon(var4, var1, var2, this.paintIconR.x, this.paintIconR.y, this.paintIconR.width, this.paintIconR.height);
            var2.setColor(var16);
         }

         if (var3 != null) {
            View var17 = (View)var12.getClientProperty("html");
            if (var17 != null) {
               var17.paint(var2, this.paintTextR);
            } else {
               var10000 = this.paintTextR;
               var10000.x += var11;
               var10000 = this.paintTextR;
               var10000.y += var11;
               this.paintText(var1, var2, var15, this.paintTextR, var10);
            }
         }

      }
   }

   static Dimension getPreferredMenuItemSize(SynthContext var0, SynthContext var1, JComponent var2, Icon var3, Icon var4, int var5, String var6, boolean var7, String var8) {
      JMenuItem var9 = (JMenuItem)var2;
      SynthMenuItemLayoutHelper var10 = new SynthMenuItemLayoutHelper(var0, var1, var9, var3, var4, MenuItemLayoutHelper.createMaxRect(), var5, var6, SynthLookAndFeel.isLeftToRight(var9), var7, var8);
      Dimension var11 = new Dimension();
      int var12 = var10.getGap();
      var11.width = 0;
      MenuItemLayoutHelper.addMaxWidth(var10.getCheckSize(), var12, var11);
      MenuItemLayoutHelper.addMaxWidth(var10.getLabelSize(), var12, var11);
      MenuItemLayoutHelper.addWidth(var10.getMaxAccOrArrowWidth(), 5 * var12, var11);
      var11.width -= var12;
      var11.height = MenuItemLayoutHelper.max(var10.getCheckSize().getHeight(), var10.getLabelSize().getHeight(), var10.getAccSize().getHeight(), var10.getArrowSize().getHeight());
      Insets var13 = var10.getMenuItem().getInsets();
      if (var13 != null) {
         var11.width += var13.left + var13.right;
         var11.height += var13.top + var13.bottom;
      }

      if (var11.width % 2 == 0) {
         ++var11.width;
      }

      if (var11.height % 2 == 0) {
         ++var11.height;
      }

      return var11;
   }

   static void applyInsets(Rectangle var0, Insets var1, boolean var2) {
      if (var1 != null) {
         var0.x += var2 ? var1.left : var1.right;
         var0.y += var1.top;
         var0.width -= (var2 ? var1.right : var1.left) + var0.x;
         var0.height -= var1.bottom + var0.y;
      }

   }

   static void paint(SynthContext var0, SynthContext var1, Graphics var2, Icon var3, Icon var4, String var5, int var6, String var7) {
      JMenuItem var8 = (JMenuItem)var0.getComponent();
      SynthStyle var9 = var0.getStyle();
      var2.setFont(var9.getFont(var0));
      Rectangle var10 = new Rectangle(0, 0, var8.getWidth(), var8.getHeight());
      boolean var11 = SynthLookAndFeel.isLeftToRight(var8);
      applyInsets(var10, var8.getInsets(), var11);
      SynthMenuItemLayoutHelper var12 = new SynthMenuItemLayoutHelper(var0, var1, var8, var3, var4, var10, var6, var5, var11, MenuItemLayoutHelper.useCheckAndArrow(var8), var7);
      MenuItemLayoutHelper.LayoutResult var13 = var12.layoutMenuItem();
      paintMenuItem(var2, var12, var13);
   }

   static void paintMenuItem(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      Font var3 = var0.getFont();
      Color var4 = var0.getColor();
      paintCheckIcon(var0, var1, var2);
      paintIcon(var0, var1, var2);
      paintText(var0, var1, var2);
      paintAccText(var0, var1, var2);
      paintArrowIcon(var0, var1, var2);
      var0.setColor(var4);
      var0.setFont(var3);
   }

   static void paintBackground(Graphics var0, SynthMenuItemLayoutHelper var1) {
      paintBackground(var1.getContext(), var0, var1.getMenuItem());
   }

   static void paintBackground(SynthContext var0, Graphics var1, JComponent var2) {
      var0.getPainter().paintMenuItemBackground(var0, var1, 0, 0, var2.getWidth(), var2.getHeight());
   }

   static void paintIcon(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      if (var1.getIcon() != null) {
         JMenuItem var4 = var1.getMenuItem();
         ButtonModel var5 = var4.getModel();
         Icon var3;
         if (!var5.isEnabled()) {
            var3 = var4.getDisabledIcon();
         } else if (var5.isPressed() && var5.isArmed()) {
            var3 = var4.getPressedIcon();
            if (var3 == null) {
               var3 = var4.getIcon();
            }
         } else {
            var3 = var4.getIcon();
         }

         if (var3 != null) {
            Rectangle var6 = var2.getIconRect();
            SynthIcon.paintIcon(var3, var1.getContext(), var0, var6.x, var6.y, var6.width, var6.height);
         }
      }

   }

   static void paintCheckIcon(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      if (var1.getCheckIcon() != null) {
         Rectangle var3 = var2.getCheckRect();
         SynthIcon.paintIcon(var1.getCheckIcon(), var1.getContext(), var0, var3.x, var3.y, var3.width, var3.height);
      }

   }

   static void paintAccText(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      String var3 = var1.getAccText();
      if (var3 != null && !var3.equals("")) {
         var0.setColor(var1.getAccStyle().getColor(var1.getAccContext(), ColorType.TEXT_FOREGROUND));
         var0.setFont(var1.getAccStyle().getFont(var1.getAccContext()));
         var1.getAccGraphicsUtils().paintText(var1.getAccContext(), var0, var3, var2.getAccRect().x, var2.getAccRect().y, -1);
      }

   }

   static void paintText(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      if (!var1.getText().equals("")) {
         if (var1.getHtmlView() != null) {
            var1.getHtmlView().paint(var0, var2.getTextRect());
         } else {
            var0.setColor(var1.getStyle().getColor(var1.getContext(), ColorType.TEXT_FOREGROUND));
            var0.setFont(var1.getStyle().getFont(var1.getContext()));
            var1.getGraphicsUtils().paintText(var1.getContext(), var0, var1.getText(), var2.getTextRect().x, var2.getTextRect().y, var1.getMenuItem().getDisplayedMnemonicIndex());
         }
      }

   }

   static void paintArrowIcon(Graphics var0, SynthMenuItemLayoutHelper var1, MenuItemLayoutHelper.LayoutResult var2) {
      if (var1.getArrowIcon() != null) {
         Rectangle var3 = var2.getArrowRect();
         SynthIcon.paintIcon(var1.getArrowIcon(), var1.getContext(), var0, var3.x, var3.y, var3.width, var3.height);
      }

   }

   private static class SynthIconWrapper implements Icon {
      private static final List<SynthGraphicsUtils.SynthIconWrapper> CACHE = new ArrayList(1);
      private SynthIcon synthIcon;
      private SynthContext context;

      static SynthGraphicsUtils.SynthIconWrapper get(SynthIcon var0, SynthContext var1) {
         synchronized(CACHE) {
            int var3 = CACHE.size();
            if (var3 > 0) {
               SynthGraphicsUtils.SynthIconWrapper var4 = (SynthGraphicsUtils.SynthIconWrapper)CACHE.remove(var3 - 1);
               var4.reset(var0, var1);
               return var4;
            }
         }

         return new SynthGraphicsUtils.SynthIconWrapper(var0, var1);
      }

      static void release(SynthGraphicsUtils.SynthIconWrapper var0) {
         var0.reset((SynthIcon)null, (SynthContext)null);
         synchronized(CACHE) {
            CACHE.add(var0);
         }
      }

      SynthIconWrapper(SynthIcon var1, SynthContext var2) {
         this.reset(var1, var2);
      }

      void reset(SynthIcon var1, SynthContext var2) {
         this.synthIcon = var1;
         this.context = var2;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return this.synthIcon.getIconWidth(this.context);
      }

      public int getIconHeight() {
         return this.synthIcon.getIconHeight(this.context);
      }
   }
}
