package javax.swing.plaf.synth;

import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.plaf.synth.SynthIcon;

class SynthMenuItemLayoutHelper extends MenuItemLayoutHelper {
   public static final StringUIClientPropertyKey MAX_ACC_OR_ARROW_WIDTH = new StringUIClientPropertyKey("maxAccOrArrowWidth");
   public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 4, 4);
   public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 2, 4);
   public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 2, 2);
   public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 4, 2);
   private SynthContext context;
   private SynthContext accContext;
   private SynthStyle style;
   private SynthStyle accStyle;
   private SynthGraphicsUtils gu;
   private SynthGraphicsUtils accGu;
   private boolean alignAcceleratorText;
   private int maxAccOrArrowWidth;

   public SynthMenuItemLayoutHelper(SynthContext var1, SynthContext var2, JMenuItem var3, Icon var4, Icon var5, Rectangle var6, int var7, String var8, boolean var9, boolean var10, String var11) {
      this.context = var1;
      this.accContext = var2;
      this.style = var1.getStyle();
      this.accStyle = var2.getStyle();
      this.gu = this.style.getGraphicsUtils(var1);
      this.accGu = this.accStyle.getGraphicsUtils(var2);
      this.alignAcceleratorText = this.getAlignAcceleratorText(var11);
      this.reset(var3, var4, var5, var6, var7, var8, var9, this.style.getFont(var1), this.accStyle.getFont(var2), var10, var11);
      this.setLeadingGap(0);
   }

   private boolean getAlignAcceleratorText(String var1) {
      return this.style.getBoolean(this.context, var1 + ".alignAcceleratorText", true);
   }

   protected void calcWidthsAndHeights() {
      if (this.getIcon() != null) {
         this.getIconSize().setWidth(SynthIcon.getIconWidth(this.getIcon(), this.context));
         this.getIconSize().setHeight(SynthIcon.getIconHeight(this.getIcon(), this.context));
      }

      if (!this.getAccText().equals("")) {
         this.getAccSize().setWidth(this.accGu.computeStringWidth(this.getAccContext(), this.getAccFontMetrics().getFont(), this.getAccFontMetrics(), this.getAccText()));
         this.getAccSize().setHeight(this.getAccFontMetrics().getHeight());
      }

      if (this.getText() == null) {
         this.setText("");
      } else if (!this.getText().equals("")) {
         if (this.getHtmlView() != null) {
            this.getTextSize().setWidth((int)this.getHtmlView().getPreferredSpan(0));
            this.getTextSize().setHeight((int)this.getHtmlView().getPreferredSpan(1));
         } else {
            this.getTextSize().setWidth(this.gu.computeStringWidth(this.context, this.getFontMetrics().getFont(), this.getFontMetrics(), this.getText()));
            this.getTextSize().setHeight(this.getFontMetrics().getHeight());
         }
      }

      if (this.useCheckAndArrow()) {
         if (this.getCheckIcon() != null) {
            this.getCheckSize().setWidth(SynthIcon.getIconWidth(this.getCheckIcon(), this.context));
            this.getCheckSize().setHeight(SynthIcon.getIconHeight(this.getCheckIcon(), this.context));
         }

         if (this.getArrowIcon() != null) {
            this.getArrowSize().setWidth(SynthIcon.getIconWidth(this.getArrowIcon(), this.context));
            this.getArrowSize().setHeight(SynthIcon.getIconHeight(this.getArrowIcon(), this.context));
         }
      }

      if (this.isColumnLayout()) {
         this.getLabelSize().setWidth(this.getIconSize().getWidth() + this.getTextSize().getWidth() + this.getGap());
         this.getLabelSize().setHeight(MenuItemLayoutHelper.max(this.getCheckSize().getHeight(), this.getIconSize().getHeight(), this.getTextSize().getHeight(), this.getAccSize().getHeight(), this.getArrowSize().getHeight()));
      } else {
         Rectangle var1 = new Rectangle();
         Rectangle var2 = new Rectangle();
         this.gu.layoutText(this.context, this.getFontMetrics(), this.getText(), this.getIcon(), this.getHorizontalAlignment(), this.getVerticalAlignment(), this.getHorizontalTextPosition(), this.getVerticalTextPosition(), this.getViewRect(), var2, var1, this.getGap());
         var1.width += this.getLeftTextExtraWidth();
         Rectangle var3 = var2.union(var1);
         this.getLabelSize().setHeight(var3.height);
         this.getLabelSize().setWidth(var3.width);
      }

   }

   protected void calcMaxWidths() {
      this.calcMaxWidth(this.getCheckSize(), MAX_CHECK_WIDTH);
      this.maxAccOrArrowWidth = this.calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, this.getArrowSize().getWidth());
      this.maxAccOrArrowWidth = this.calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, this.getAccSize().getWidth());
      int var1;
      if (this.isColumnLayout()) {
         this.calcMaxWidth(this.getIconSize(), MAX_ICON_WIDTH);
         this.calcMaxWidth(this.getTextSize(), MAX_TEXT_WIDTH);
         var1 = this.getGap();
         if (this.getIconSize().getMaxWidth() == 0 || this.getTextSize().getMaxWidth() == 0) {
            var1 = 0;
         }

         this.getLabelSize().setMaxWidth(this.calcMaxValue(MAX_LABEL_WIDTH, this.getIconSize().getMaxWidth() + this.getTextSize().getMaxWidth() + var1));
      } else {
         this.getIconSize().setMaxWidth(this.getParentIntProperty(MAX_ICON_WIDTH));
         this.calcMaxWidth(this.getLabelSize(), MAX_LABEL_WIDTH);
         var1 = this.getLabelSize().getMaxWidth() - this.getIconSize().getMaxWidth();
         if (this.getIconSize().getMaxWidth() > 0) {
            var1 -= this.getGap();
         }

         this.getTextSize().setMaxWidth(this.calcMaxValue(MAX_TEXT_WIDTH, var1));
      }

   }

   public SynthContext getContext() {
      return this.context;
   }

   public SynthContext getAccContext() {
      return this.accContext;
   }

   public SynthStyle getStyle() {
      return this.style;
   }

   public SynthStyle getAccStyle() {
      return this.accStyle;
   }

   public SynthGraphicsUtils getGraphicsUtils() {
      return this.gu;
   }

   public SynthGraphicsUtils getAccGraphicsUtils() {
      return this.accGu;
   }

   public boolean alignAcceleratorText() {
      return this.alignAcceleratorText;
   }

   public int getMaxAccOrArrowWidth() {
      return this.maxAccOrArrowWidth;
   }

   protected void prepareForLayout(MenuItemLayoutHelper.LayoutResult var1) {
      var1.getCheckRect().width = this.getCheckSize().getMaxWidth();
      if (this.useCheckAndArrow() && !"".equals(this.getAccText())) {
         var1.getAccRect().width = this.maxAccOrArrowWidth;
      } else {
         var1.getArrowRect().width = this.maxAccOrArrowWidth;
      }

   }

   public MenuItemLayoutHelper.ColumnAlignment getLTRColumnAlignment() {
      return this.alignAcceleratorText() ? LTR_ALIGNMENT_2 : LTR_ALIGNMENT_1;
   }

   public MenuItemLayoutHelper.ColumnAlignment getRTLColumnAlignment() {
      return this.alignAcceleratorText() ? RTL_ALIGNMENT_2 : RTL_ALIGNMENT_1;
   }

   protected void layoutIconAndTextInLabelRect(MenuItemLayoutHelper.LayoutResult var1) {
      var1.setTextRect(new Rectangle());
      var1.setIconRect(new Rectangle());
      this.gu.layoutText(this.context, this.getFontMetrics(), this.getText(), this.getIcon(), this.getHorizontalAlignment(), this.getVerticalAlignment(), this.getHorizontalTextPosition(), this.getVerticalTextPosition(), var1.getLabelRect(), var1.getIconRect(), var1.getTextRect(), this.getGap());
   }
}
