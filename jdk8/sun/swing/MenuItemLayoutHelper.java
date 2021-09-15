package sun.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;

public class MenuItemLayoutHelper {
   public static final StringUIClientPropertyKey MAX_ARROW_WIDTH = new StringUIClientPropertyKey("maxArrowWidth");
   public static final StringUIClientPropertyKey MAX_CHECK_WIDTH = new StringUIClientPropertyKey("maxCheckWidth");
   public static final StringUIClientPropertyKey MAX_ICON_WIDTH = new StringUIClientPropertyKey("maxIconWidth");
   public static final StringUIClientPropertyKey MAX_TEXT_WIDTH = new StringUIClientPropertyKey("maxTextWidth");
   public static final StringUIClientPropertyKey MAX_ACC_WIDTH = new StringUIClientPropertyKey("maxAccWidth");
   public static final StringUIClientPropertyKey MAX_LABEL_WIDTH = new StringUIClientPropertyKey("maxLabelWidth");
   private JMenuItem mi;
   private JComponent miParent;
   private Font font;
   private Font accFont;
   private FontMetrics fm;
   private FontMetrics accFm;
   private Icon icon;
   private Icon checkIcon;
   private Icon arrowIcon;
   private String text;
   private String accText;
   private boolean isColumnLayout;
   private boolean useCheckAndArrow;
   private boolean isLeftToRight;
   private boolean isTopLevelMenu;
   private View htmlView;
   private int verticalAlignment;
   private int horizontalAlignment;
   private int verticalTextPosition;
   private int horizontalTextPosition;
   private int gap;
   private int leadingGap;
   private int afterCheckIconGap;
   private int minTextOffset;
   private int leftTextExtraWidth;
   private Rectangle viewRect;
   private MenuItemLayoutHelper.RectSize iconSize;
   private MenuItemLayoutHelper.RectSize textSize;
   private MenuItemLayoutHelper.RectSize accSize;
   private MenuItemLayoutHelper.RectSize checkSize;
   private MenuItemLayoutHelper.RectSize arrowSize;
   private MenuItemLayoutHelper.RectSize labelSize;

   protected MenuItemLayoutHelper() {
   }

   public MenuItemLayoutHelper(JMenuItem var1, Icon var2, Icon var3, Rectangle var4, int var5, String var6, boolean var7, Font var8, Font var9, boolean var10, String var11) {
      this.reset(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   protected void reset(JMenuItem var1, Icon var2, Icon var3, Rectangle var4, int var5, String var6, boolean var7, Font var8, Font var9, boolean var10, String var11) {
      this.mi = var1;
      this.miParent = getMenuItemParent(var1);
      this.accText = this.getAccText(var6);
      this.verticalAlignment = var1.getVerticalAlignment();
      this.horizontalAlignment = var1.getHorizontalAlignment();
      this.verticalTextPosition = var1.getVerticalTextPosition();
      this.horizontalTextPosition = var1.getHorizontalTextPosition();
      this.useCheckAndArrow = var10;
      this.font = var8;
      this.accFont = var9;
      this.fm = var1.getFontMetrics(var8);
      this.accFm = var1.getFontMetrics(var9);
      this.isLeftToRight = var7;
      this.isColumnLayout = isColumnLayout(var7, this.horizontalAlignment, this.horizontalTextPosition, this.verticalTextPosition);
      this.isTopLevelMenu = this.miParent == null;
      this.checkIcon = var2;
      this.icon = this.getIcon(var11);
      this.arrowIcon = var3;
      this.text = var1.getText();
      this.gap = var5;
      this.afterCheckIconGap = this.getAfterCheckIconGap(var11);
      this.minTextOffset = this.getMinTextOffset(var11);
      this.htmlView = (View)var1.getClientProperty("html");
      this.viewRect = var4;
      this.iconSize = new MenuItemLayoutHelper.RectSize();
      this.textSize = new MenuItemLayoutHelper.RectSize();
      this.accSize = new MenuItemLayoutHelper.RectSize();
      this.checkSize = new MenuItemLayoutHelper.RectSize();
      this.arrowSize = new MenuItemLayoutHelper.RectSize();
      this.labelSize = new MenuItemLayoutHelper.RectSize();
      this.calcExtraWidths();
      this.calcWidthsAndHeights();
      this.setOriginalWidths();
      this.calcMaxWidths();
      this.leadingGap = this.getLeadingGap(var11);
      this.calcMaxTextOffset(var4);
   }

   private void calcExtraWidths() {
      this.leftTextExtraWidth = this.getLeftExtraWidth(this.text);
   }

   private int getLeftExtraWidth(String var1) {
      int var2 = SwingUtilities2.getLeftSideBearing(this.mi, this.fm, var1);
      return var2 < 0 ? -var2 : 0;
   }

   private void setOriginalWidths() {
      this.iconSize.origWidth = this.iconSize.width;
      this.textSize.origWidth = this.textSize.width;
      this.accSize.origWidth = this.accSize.width;
      this.checkSize.origWidth = this.checkSize.width;
      this.arrowSize.origWidth = this.arrowSize.width;
   }

   private String getAccText(String var1) {
      String var2 = "";
      KeyStroke var3 = this.mi.getAccelerator();
      if (var3 != null) {
         int var4 = var3.getModifiers();
         if (var4 > 0) {
            var2 = KeyEvent.getKeyModifiersText(var4);
            var2 = var2 + var1;
         }

         int var5 = var3.getKeyCode();
         if (var5 != 0) {
            var2 = var2 + KeyEvent.getKeyText(var5);
         } else {
            var2 = var2 + var3.getKeyChar();
         }
      }

      return var2;
   }

   private Icon getIcon(String var1) {
      Icon var2 = null;
      MenuItemCheckIconFactory var3 = (MenuItemCheckIconFactory)UIManager.get(var1 + ".checkIconFactory");
      if (!this.isColumnLayout || !this.useCheckAndArrow || var3 == null || !var3.isCompatible(this.checkIcon, var1)) {
         var2 = this.mi.getIcon();
      }

      return var2;
   }

   private int getMinTextOffset(String var1) {
      int var2 = 0;
      Object var3 = UIManager.get(var1 + ".minimumTextOffset");
      if (var3 instanceof Integer) {
         var2 = (Integer)var3;
      }

      return var2;
   }

   private int getAfterCheckIconGap(String var1) {
      int var2 = this.gap;
      Object var3 = UIManager.get(var1 + ".afterCheckIconGap");
      if (var3 instanceof Integer) {
         var2 = (Integer)var3;
      }

      return var2;
   }

   private int getLeadingGap(String var1) {
      return this.checkSize.getMaxWidth() > 0 ? this.getCheckOffset(var1) : this.gap;
   }

   private int getCheckOffset(String var1) {
      int var2 = this.gap;
      Object var3 = UIManager.get(var1 + ".checkIconOffset");
      if (var3 instanceof Integer) {
         var2 = (Integer)var3;
      }

      return var2;
   }

   protected void calcWidthsAndHeights() {
      if (this.icon != null) {
         this.iconSize.width = this.icon.getIconWidth();
         this.iconSize.height = this.icon.getIconHeight();
      }

      if (!this.accText.equals("")) {
         this.accSize.width = SwingUtilities2.stringWidth(this.mi, this.accFm, this.accText);
         this.accSize.height = this.accFm.getHeight();
      }

      if (this.text == null) {
         this.text = "";
      } else if (!this.text.equals("")) {
         if (this.htmlView != null) {
            this.textSize.width = (int)this.htmlView.getPreferredSpan(0);
            this.textSize.height = (int)this.htmlView.getPreferredSpan(1);
         } else {
            this.textSize.width = SwingUtilities2.stringWidth(this.mi, this.fm, this.text);
            this.textSize.height = this.fm.getHeight();
         }
      }

      if (this.useCheckAndArrow) {
         if (this.checkIcon != null) {
            this.checkSize.width = this.checkIcon.getIconWidth();
            this.checkSize.height = this.checkIcon.getIconHeight();
         }

         if (this.arrowIcon != null) {
            this.arrowSize.width = this.arrowIcon.getIconWidth();
            this.arrowSize.height = this.arrowIcon.getIconHeight();
         }
      }

      if (this.isColumnLayout) {
         this.labelSize.width = this.iconSize.width + this.textSize.width + this.gap;
         this.labelSize.height = max(this.checkSize.height, this.iconSize.height, this.textSize.height, this.accSize.height, this.arrowSize.height);
      } else {
         Rectangle var1 = new Rectangle();
         Rectangle var2 = new Rectangle();
         SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, this.viewRect, var2, var1, this.gap);
         var1.width += this.leftTextExtraWidth;
         Rectangle var3 = var2.union(var1);
         this.labelSize.height = var3.height;
         this.labelSize.width = var3.width;
      }

   }

   protected void calcMaxWidths() {
      this.calcMaxWidth(this.checkSize, MAX_CHECK_WIDTH);
      this.calcMaxWidth(this.arrowSize, MAX_ARROW_WIDTH);
      this.calcMaxWidth(this.accSize, MAX_ACC_WIDTH);
      int var1;
      if (this.isColumnLayout) {
         this.calcMaxWidth(this.iconSize, MAX_ICON_WIDTH);
         this.calcMaxWidth(this.textSize, MAX_TEXT_WIDTH);
         var1 = this.gap;
         if (this.iconSize.getMaxWidth() == 0 || this.textSize.getMaxWidth() == 0) {
            var1 = 0;
         }

         this.labelSize.maxWidth = this.calcMaxValue(MAX_LABEL_WIDTH, this.iconSize.maxWidth + this.textSize.maxWidth + var1);
      } else {
         this.iconSize.maxWidth = this.getParentIntProperty(MAX_ICON_WIDTH);
         this.calcMaxWidth(this.labelSize, MAX_LABEL_WIDTH);
         var1 = this.labelSize.maxWidth - this.iconSize.maxWidth;
         if (this.iconSize.maxWidth > 0) {
            var1 -= this.gap;
         }

         this.textSize.maxWidth = this.calcMaxValue(MAX_TEXT_WIDTH, var1);
      }

   }

   protected void calcMaxWidth(MenuItemLayoutHelper.RectSize var1, Object var2) {
      var1.maxWidth = this.calcMaxValue(var2, var1.width);
   }

   protected int calcMaxValue(Object var1, int var2) {
      int var3 = this.getParentIntProperty(var1);
      if (var2 > var3) {
         if (this.miParent != null) {
            this.miParent.putClientProperty(var1, var2);
         }

         return var2;
      } else {
         return var3;
      }
   }

   protected int getParentIntProperty(Object var1) {
      Object var2 = null;
      if (this.miParent != null) {
         var2 = this.miParent.getClientProperty(var1);
      }

      if (var2 == null || !(var2 instanceof Integer)) {
         var2 = 0;
      }

      return (Integer)var2;
   }

   public static boolean isColumnLayout(boolean var0, JMenuItem var1) {
      assert var1 != null;

      return isColumnLayout(var0, var1.getHorizontalAlignment(), var1.getHorizontalTextPosition(), var1.getVerticalTextPosition());
   }

   public static boolean isColumnLayout(boolean var0, int var1, int var2, int var3) {
      if (var3 != 0) {
         return false;
      } else {
         if (var0) {
            if (var1 != 10 && var1 != 2) {
               return false;
            }

            if (var2 != 11 && var2 != 4) {
               return false;
            }
         } else {
            if (var1 != 10 && var1 != 4) {
               return false;
            }

            if (var2 != 11 && var2 != 2) {
               return false;
            }
         }

         return true;
      }
   }

   private void calcMaxTextOffset(Rectangle var1) {
      if (this.isColumnLayout && this.isLeftToRight) {
         int var2 = var1.x + this.leadingGap + this.checkSize.maxWidth + this.afterCheckIconGap + this.iconSize.maxWidth + this.gap;
         if (this.checkSize.maxWidth == 0) {
            var2 -= this.afterCheckIconGap;
         }

         if (this.iconSize.maxWidth == 0) {
            var2 -= this.gap;
         }

         if (var2 < this.minTextOffset) {
            var2 = this.minTextOffset;
         }

         this.calcMaxValue(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, var2);
      }
   }

   public MenuItemLayoutHelper.LayoutResult layoutMenuItem() {
      MenuItemLayoutHelper.LayoutResult var1 = this.createLayoutResult();
      this.prepareForLayout(var1);
      if (this.isColumnLayout()) {
         if (this.isLeftToRight()) {
            this.doLTRColumnLayout(var1, this.getLTRColumnAlignment());
         } else {
            this.doRTLColumnLayout(var1, this.getRTLColumnAlignment());
         }
      } else if (this.isLeftToRight()) {
         this.doLTRComplexLayout(var1, this.getLTRColumnAlignment());
      } else {
         this.doRTLComplexLayout(var1, this.getRTLColumnAlignment());
      }

      this.alignAccCheckAndArrowVertically(var1);
      return var1;
   }

   private MenuItemLayoutHelper.LayoutResult createLayoutResult() {
      return new MenuItemLayoutHelper.LayoutResult(new Rectangle(this.iconSize.width, this.iconSize.height), new Rectangle(this.textSize.width, this.textSize.height), new Rectangle(this.accSize.width, this.accSize.height), new Rectangle(this.checkSize.width, this.checkSize.height), new Rectangle(this.arrowSize.width, this.arrowSize.height), new Rectangle(this.labelSize.width, this.labelSize.height));
   }

   public MenuItemLayoutHelper.ColumnAlignment getLTRColumnAlignment() {
      return MenuItemLayoutHelper.ColumnAlignment.LEFT_ALIGNMENT;
   }

   public MenuItemLayoutHelper.ColumnAlignment getRTLColumnAlignment() {
      return MenuItemLayoutHelper.ColumnAlignment.RIGHT_ALIGNMENT;
   }

   protected void prepareForLayout(MenuItemLayoutHelper.LayoutResult var1) {
      var1.checkRect.width = this.checkSize.maxWidth;
      var1.accRect.width = this.accSize.maxWidth;
      var1.arrowRect.width = this.arrowSize.maxWidth;
   }

   private void alignAccCheckAndArrowVertically(MenuItemLayoutHelper.LayoutResult var1) {
      var1.accRect.y = (int)((float)var1.labelRect.y + (float)var1.labelRect.height / 2.0F - (float)var1.accRect.height / 2.0F);
      this.fixVerticalAlignment(var1, var1.accRect);
      if (this.useCheckAndArrow) {
         var1.arrowRect.y = (int)((float)var1.labelRect.y + (float)var1.labelRect.height / 2.0F - (float)var1.arrowRect.height / 2.0F);
         var1.checkRect.y = (int)((float)var1.labelRect.y + (float)var1.labelRect.height / 2.0F - (float)var1.checkRect.height / 2.0F);
         this.fixVerticalAlignment(var1, var1.arrowRect);
         this.fixVerticalAlignment(var1, var1.checkRect);
      }

   }

   private void fixVerticalAlignment(MenuItemLayoutHelper.LayoutResult var1, Rectangle var2) {
      int var3 = 0;
      if (var2.y < this.viewRect.y) {
         var3 = this.viewRect.y - var2.y;
      } else if (var2.y + var2.height > this.viewRect.y + this.viewRect.height) {
         var3 = this.viewRect.y + this.viewRect.height - var2.y - var2.height;
      }

      if (var3 != 0) {
         Rectangle var10000 = var1.checkRect;
         var10000.y += var3;
         var10000 = var1.iconRect;
         var10000.y += var3;
         var10000 = var1.textRect;
         var10000.y += var3;
         var10000 = var1.accRect;
         var10000.y += var3;
         var10000 = var1.arrowRect;
         var10000.y += var3;
         var10000 = var1.labelRect;
         var10000.y += var3;
      }

   }

   private void doLTRColumnLayout(MenuItemLayoutHelper.LayoutResult var1, MenuItemLayoutHelper.ColumnAlignment var2) {
      var1.iconRect.width = this.iconSize.maxWidth;
      var1.textRect.width = this.textSize.maxWidth;
      this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, var1.checkRect, var1.iconRect, var1.textRect);
      Rectangle var10000;
      if (var1.checkRect.width > 0) {
         var10000 = var1.iconRect;
         var10000.x += this.afterCheckIconGap - this.gap;
         var10000 = var1.textRect;
         var10000.x += this.afterCheckIconGap - this.gap;
      }

      this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, var1.arrowRect, var1.accRect);
      int var3 = var1.textRect.x - this.viewRect.x;
      if (!this.isTopLevelMenu && var3 < this.minTextOffset) {
         var10000 = var1.textRect;
         var10000.x += this.minTextOffset - var3;
      }

      this.alignRects(var1, var2);
      this.calcTextAndIconYPositions(var1);
      var1.setLabelRect(var1.textRect.union(var1.iconRect));
   }

   private void doLTRComplexLayout(MenuItemLayoutHelper.LayoutResult var1, MenuItemLayoutHelper.ColumnAlignment var2) {
      var1.labelRect.width = this.labelSize.maxWidth;
      this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, var1.checkRect, var1.labelRect);
      Rectangle var10000;
      if (var1.checkRect.width > 0) {
         var10000 = var1.labelRect;
         var10000.x += this.afterCheckIconGap - this.gap;
      }

      this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, var1.arrowRect, var1.accRect);
      int var3 = var1.labelRect.x - this.viewRect.x;
      if (!this.isTopLevelMenu && var3 < this.minTextOffset) {
         var10000 = var1.labelRect;
         var10000.x += this.minTextOffset - var3;
      }

      this.alignRects(var1, var2);
      this.calcLabelYPosition(var1);
      this.layoutIconAndTextInLabelRect(var1);
   }

   private void doRTLColumnLayout(MenuItemLayoutHelper.LayoutResult var1, MenuItemLayoutHelper.ColumnAlignment var2) {
      var1.iconRect.width = this.iconSize.maxWidth;
      var1.textRect.width = this.textSize.maxWidth;
      this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, var1.checkRect, var1.iconRect, var1.textRect);
      Rectangle var10000;
      if (var1.checkRect.width > 0) {
         var10000 = var1.iconRect;
         var10000.x -= this.afterCheckIconGap - this.gap;
         var10000 = var1.textRect;
         var10000.x -= this.afterCheckIconGap - this.gap;
      }

      this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, var1.arrowRect, var1.accRect);
      int var3 = this.viewRect.x + this.viewRect.width - (var1.textRect.x + var1.textRect.width);
      if (!this.isTopLevelMenu && var3 < this.minTextOffset) {
         var10000 = var1.textRect;
         var10000.x -= this.minTextOffset - var3;
      }

      this.alignRects(var1, var2);
      this.calcTextAndIconYPositions(var1);
      var1.setLabelRect(var1.textRect.union(var1.iconRect));
   }

   private void doRTLComplexLayout(MenuItemLayoutHelper.LayoutResult var1, MenuItemLayoutHelper.ColumnAlignment var2) {
      var1.labelRect.width = this.labelSize.maxWidth;
      this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, var1.checkRect, var1.labelRect);
      Rectangle var10000;
      if (var1.checkRect.width > 0) {
         var10000 = var1.labelRect;
         var10000.x -= this.afterCheckIconGap - this.gap;
      }

      this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, var1.arrowRect, var1.accRect);
      int var3 = this.viewRect.x + this.viewRect.width - (var1.labelRect.x + var1.labelRect.width);
      if (!this.isTopLevelMenu && var3 < this.minTextOffset) {
         var10000 = var1.labelRect;
         var10000.x -= this.minTextOffset - var3;
      }

      this.alignRects(var1, var2);
      this.calcLabelYPosition(var1);
      this.layoutIconAndTextInLabelRect(var1);
   }

   private void alignRects(MenuItemLayoutHelper.LayoutResult var1, MenuItemLayoutHelper.ColumnAlignment var2) {
      this.alignRect(var1.checkRect, var2.getCheckAlignment(), this.checkSize.getOrigWidth());
      this.alignRect(var1.iconRect, var2.getIconAlignment(), this.iconSize.getOrigWidth());
      this.alignRect(var1.textRect, var2.getTextAlignment(), this.textSize.getOrigWidth());
      this.alignRect(var1.accRect, var2.getAccAlignment(), this.accSize.getOrigWidth());
      this.alignRect(var1.arrowRect, var2.getArrowAlignment(), this.arrowSize.getOrigWidth());
   }

   private void alignRect(Rectangle var1, int var2, int var3) {
      if (var2 == 4) {
         var1.x = var1.x + var1.width - var3;
      }

      var1.width = var3;
   }

   protected void layoutIconAndTextInLabelRect(MenuItemLayoutHelper.LayoutResult var1) {
      var1.setTextRect(new Rectangle());
      var1.setIconRect(new Rectangle());
      SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, var1.labelRect, var1.iconRect, var1.textRect, this.gap);
   }

   private void calcXPositionsLTR(int var1, int var2, int var3, Rectangle... var4) {
      int var5 = var1 + var2;
      Rectangle[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Rectangle var9 = var6[var8];
         var9.x = var5;
         if (var9.width > 0) {
            var5 += var9.width + var3;
         }
      }

   }

   private void calcXPositionsRTL(int var1, int var2, int var3, Rectangle... var4) {
      int var5 = var1 - var2;
      Rectangle[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Rectangle var9 = var6[var8];
         var9.x = var5 - var9.width;
         if (var9.width > 0) {
            var5 -= var9.width + var3;
         }
      }

   }

   private void calcTextAndIconYPositions(MenuItemLayoutHelper.LayoutResult var1) {
      if (this.verticalAlignment == 1) {
         var1.textRect.y = (int)((float)this.viewRect.y + (float)var1.labelRect.height / 2.0F - (float)var1.textRect.height / 2.0F);
         var1.iconRect.y = (int)((float)this.viewRect.y + (float)var1.labelRect.height / 2.0F - (float)var1.iconRect.height / 2.0F);
      } else if (this.verticalAlignment == 0) {
         var1.textRect.y = (int)((float)this.viewRect.y + (float)this.viewRect.height / 2.0F - (float)var1.textRect.height / 2.0F);
         var1.iconRect.y = (int)((float)this.viewRect.y + (float)this.viewRect.height / 2.0F - (float)var1.iconRect.height / 2.0F);
      } else if (this.verticalAlignment == 3) {
         var1.textRect.y = (int)((float)(this.viewRect.y + this.viewRect.height) - (float)var1.labelRect.height / 2.0F - (float)var1.textRect.height / 2.0F);
         var1.iconRect.y = (int)((float)(this.viewRect.y + this.viewRect.height) - (float)var1.labelRect.height / 2.0F - (float)var1.iconRect.height / 2.0F);
      }

   }

   private void calcLabelYPosition(MenuItemLayoutHelper.LayoutResult var1) {
      if (this.verticalAlignment == 1) {
         var1.labelRect.y = this.viewRect.y;
      } else if (this.verticalAlignment == 0) {
         var1.labelRect.y = (int)((float)this.viewRect.y + (float)this.viewRect.height / 2.0F - (float)var1.labelRect.height / 2.0F);
      } else if (this.verticalAlignment == 3) {
         var1.labelRect.y = this.viewRect.y + this.viewRect.height - var1.labelRect.height;
      }

   }

   public static JComponent getMenuItemParent(JMenuItem var0) {
      Container var1 = var0.getParent();
      return !(var1 instanceof JComponent) || var0 instanceof JMenu && ((JMenu)var0).isTopLevelMenu() ? null : (JComponent)var1;
   }

   public static void clearUsedParentClientProperties(JMenuItem var0) {
      clearUsedClientProperties(getMenuItemParent(var0));
   }

   public static void clearUsedClientProperties(JComponent var0) {
      if (var0 != null) {
         var0.putClientProperty(MAX_ARROW_WIDTH, (Object)null);
         var0.putClientProperty(MAX_CHECK_WIDTH, (Object)null);
         var0.putClientProperty(MAX_ACC_WIDTH, (Object)null);
         var0.putClientProperty(MAX_TEXT_WIDTH, (Object)null);
         var0.putClientProperty(MAX_ICON_WIDTH, (Object)null);
         var0.putClientProperty(MAX_LABEL_WIDTH, (Object)null);
         var0.putClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, (Object)null);
      }

   }

   public static int max(int... var0) {
      int var1 = Integer.MIN_VALUE;
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if (var5 > var1) {
            var1 = var5;
         }
      }

      return var1;
   }

   public static Rectangle createMaxRect() {
      return new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public static void addMaxWidth(MenuItemLayoutHelper.RectSize var0, int var1, Dimension var2) {
      if (var0.maxWidth > 0) {
         var2.width += var0.maxWidth + var1;
      }

   }

   public static void addWidth(int var0, int var1, Dimension var2) {
      if (var0 > 0) {
         var2.width += var0 + var1;
      }

   }

   public JMenuItem getMenuItem() {
      return this.mi;
   }

   public JComponent getMenuItemParent() {
      return this.miParent;
   }

   public Font getFont() {
      return this.font;
   }

   public Font getAccFont() {
      return this.accFont;
   }

   public FontMetrics getFontMetrics() {
      return this.fm;
   }

   public FontMetrics getAccFontMetrics() {
      return this.accFm;
   }

   public Icon getIcon() {
      return this.icon;
   }

   public Icon getCheckIcon() {
      return this.checkIcon;
   }

   public Icon getArrowIcon() {
      return this.arrowIcon;
   }

   public String getText() {
      return this.text;
   }

   public String getAccText() {
      return this.accText;
   }

   public boolean isColumnLayout() {
      return this.isColumnLayout;
   }

   public boolean useCheckAndArrow() {
      return this.useCheckAndArrow;
   }

   public boolean isLeftToRight() {
      return this.isLeftToRight;
   }

   public boolean isTopLevelMenu() {
      return this.isTopLevelMenu;
   }

   public View getHtmlView() {
      return this.htmlView;
   }

   public int getVerticalAlignment() {
      return this.verticalAlignment;
   }

   public int getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public int getVerticalTextPosition() {
      return this.verticalTextPosition;
   }

   public int getHorizontalTextPosition() {
      return this.horizontalTextPosition;
   }

   public int getGap() {
      return this.gap;
   }

   public int getLeadingGap() {
      return this.leadingGap;
   }

   public int getAfterCheckIconGap() {
      return this.afterCheckIconGap;
   }

   public int getMinTextOffset() {
      return this.minTextOffset;
   }

   public Rectangle getViewRect() {
      return this.viewRect;
   }

   public MenuItemLayoutHelper.RectSize getIconSize() {
      return this.iconSize;
   }

   public MenuItemLayoutHelper.RectSize getTextSize() {
      return this.textSize;
   }

   public MenuItemLayoutHelper.RectSize getAccSize() {
      return this.accSize;
   }

   public MenuItemLayoutHelper.RectSize getCheckSize() {
      return this.checkSize;
   }

   public MenuItemLayoutHelper.RectSize getArrowSize() {
      return this.arrowSize;
   }

   public MenuItemLayoutHelper.RectSize getLabelSize() {
      return this.labelSize;
   }

   protected void setMenuItem(JMenuItem var1) {
      this.mi = var1;
   }

   protected void setMenuItemParent(JComponent var1) {
      this.miParent = var1;
   }

   protected void setFont(Font var1) {
      this.font = var1;
   }

   protected void setAccFont(Font var1) {
      this.accFont = var1;
   }

   protected void setFontMetrics(FontMetrics var1) {
      this.fm = var1;
   }

   protected void setAccFontMetrics(FontMetrics var1) {
      this.accFm = var1;
   }

   protected void setIcon(Icon var1) {
      this.icon = var1;
   }

   protected void setCheckIcon(Icon var1) {
      this.checkIcon = var1;
   }

   protected void setArrowIcon(Icon var1) {
      this.arrowIcon = var1;
   }

   protected void setText(String var1) {
      this.text = var1;
   }

   protected void setAccText(String var1) {
      this.accText = var1;
   }

   protected void setColumnLayout(boolean var1) {
      this.isColumnLayout = var1;
   }

   protected void setUseCheckAndArrow(boolean var1) {
      this.useCheckAndArrow = var1;
   }

   protected void setLeftToRight(boolean var1) {
      this.isLeftToRight = var1;
   }

   protected void setTopLevelMenu(boolean var1) {
      this.isTopLevelMenu = var1;
   }

   protected void setHtmlView(View var1) {
      this.htmlView = var1;
   }

   protected void setVerticalAlignment(int var1) {
      this.verticalAlignment = var1;
   }

   protected void setHorizontalAlignment(int var1) {
      this.horizontalAlignment = var1;
   }

   protected void setVerticalTextPosition(int var1) {
      this.verticalTextPosition = var1;
   }

   protected void setHorizontalTextPosition(int var1) {
      this.horizontalTextPosition = var1;
   }

   protected void setGap(int var1) {
      this.gap = var1;
   }

   protected void setLeadingGap(int var1) {
      this.leadingGap = var1;
   }

   protected void setAfterCheckIconGap(int var1) {
      this.afterCheckIconGap = var1;
   }

   protected void setMinTextOffset(int var1) {
      this.minTextOffset = var1;
   }

   protected void setViewRect(Rectangle var1) {
      this.viewRect = var1;
   }

   protected void setIconSize(MenuItemLayoutHelper.RectSize var1) {
      this.iconSize = var1;
   }

   protected void setTextSize(MenuItemLayoutHelper.RectSize var1) {
      this.textSize = var1;
   }

   protected void setAccSize(MenuItemLayoutHelper.RectSize var1) {
      this.accSize = var1;
   }

   protected void setCheckSize(MenuItemLayoutHelper.RectSize var1) {
      this.checkSize = var1;
   }

   protected void setArrowSize(MenuItemLayoutHelper.RectSize var1) {
      this.arrowSize = var1;
   }

   protected void setLabelSize(MenuItemLayoutHelper.RectSize var1) {
      this.labelSize = var1;
   }

   public int getLeftTextExtraWidth() {
      return this.leftTextExtraWidth;
   }

   public static boolean useCheckAndArrow(JMenuItem var0) {
      boolean var1 = true;
      if (var0 instanceof JMenu && ((JMenu)var0).isTopLevelMenu()) {
         var1 = false;
      }

      return var1;
   }

   public static class RectSize {
      private int width;
      private int height;
      private int origWidth;
      private int maxWidth;

      public RectSize() {
      }

      public RectSize(int var1, int var2, int var3, int var4) {
         this.width = var1;
         this.height = var2;
         this.origWidth = var3;
         this.maxWidth = var4;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public int getOrigWidth() {
         return this.origWidth;
      }

      public int getMaxWidth() {
         return this.maxWidth;
      }

      public void setWidth(int var1) {
         this.width = var1;
      }

      public void setHeight(int var1) {
         this.height = var1;
      }

      public void setOrigWidth(int var1) {
         this.origWidth = var1;
      }

      public void setMaxWidth(int var1) {
         this.maxWidth = var1;
      }

      public String toString() {
         return "[w=" + this.width + ",h=" + this.height + ",ow=" + this.origWidth + ",mw=" + this.maxWidth + "]";
      }
   }

   public static class ColumnAlignment {
      private int checkAlignment;
      private int iconAlignment;
      private int textAlignment;
      private int accAlignment;
      private int arrowAlignment;
      public static final MenuItemLayoutHelper.ColumnAlignment LEFT_ALIGNMENT = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 2, 2);
      public static final MenuItemLayoutHelper.ColumnAlignment RIGHT_ALIGNMENT = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 4, 4);

      public ColumnAlignment(int var1, int var2, int var3, int var4, int var5) {
         this.checkAlignment = var1;
         this.iconAlignment = var2;
         this.textAlignment = var3;
         this.accAlignment = var4;
         this.arrowAlignment = var5;
      }

      public int getCheckAlignment() {
         return this.checkAlignment;
      }

      public int getIconAlignment() {
         return this.iconAlignment;
      }

      public int getTextAlignment() {
         return this.textAlignment;
      }

      public int getAccAlignment() {
         return this.accAlignment;
      }

      public int getArrowAlignment() {
         return this.arrowAlignment;
      }
   }

   public static class LayoutResult {
      private Rectangle iconRect;
      private Rectangle textRect;
      private Rectangle accRect;
      private Rectangle checkRect;
      private Rectangle arrowRect;
      private Rectangle labelRect;

      public LayoutResult() {
         this.iconRect = new Rectangle();
         this.textRect = new Rectangle();
         this.accRect = new Rectangle();
         this.checkRect = new Rectangle();
         this.arrowRect = new Rectangle();
         this.labelRect = new Rectangle();
      }

      public LayoutResult(Rectangle var1, Rectangle var2, Rectangle var3, Rectangle var4, Rectangle var5, Rectangle var6) {
         this.iconRect = var1;
         this.textRect = var2;
         this.accRect = var3;
         this.checkRect = var4;
         this.arrowRect = var5;
         this.labelRect = var6;
      }

      public Rectangle getIconRect() {
         return this.iconRect;
      }

      public void setIconRect(Rectangle var1) {
         this.iconRect = var1;
      }

      public Rectangle getTextRect() {
         return this.textRect;
      }

      public void setTextRect(Rectangle var1) {
         this.textRect = var1;
      }

      public Rectangle getAccRect() {
         return this.accRect;
      }

      public void setAccRect(Rectangle var1) {
         this.accRect = var1;
      }

      public Rectangle getCheckRect() {
         return this.checkRect;
      }

      public void setCheckRect(Rectangle var1) {
         this.checkRect = var1;
      }

      public Rectangle getArrowRect() {
         return this.arrowRect;
      }

      public void setArrowRect(Rectangle var1) {
         this.arrowRect = var1;
      }

      public Rectangle getLabelRect() {
         return this.labelRect;
      }

      public void setLabelRect(Rectangle var1) {
         this.labelRect = var1;
      }

      public Map<String, Rectangle> getAllRects() {
         HashMap var1 = new HashMap();
         var1.put("checkRect", this.checkRect);
         var1.put("iconRect", this.iconRect);
         var1.put("textRect", this.textRect);
         var1.put("accRect", this.accRect);
         var1.put("arrowRect", this.arrowRect);
         var1.put("labelRect", this.labelRect);
         return var1;
      }
   }
}
