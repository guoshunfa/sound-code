package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

public class AquaTableHeaderBorder extends AbstractBorder {
   protected static final int SORT_NONE = 0;
   protected static final int SORT_ASCENDING = 1;
   protected static final int SORT_DECENDING = -1;
   protected final Insets editorBorderInsets = new Insets(1, 3, 1, 3);
   protected final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIState.getInstance());
   protected boolean doPaint = true;
   static final AquaUtils.RecyclableSingleton<Border> alternateBorder = new AquaUtils.RecyclableSingleton<Border>() {
      protected Border getInstance() {
         return BorderFactory.createRaisedBevelBorder();
      }
   };
   private boolean selected = false;
   private int fHorizontalShift = 0;
   private int sortOrder = 0;

   protected static AquaTableHeaderBorder getListHeaderBorder() {
      return new AquaTableHeaderBorder();
   }

   protected AquaTableHeaderBorder() {
      this.painter.state.set(JRSUIConstants.AlignmentHorizontal.LEFT);
      this.painter.state.set(JRSUIConstants.AlignmentVertical.TOP);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (this.doPaint) {
         JComponent var7 = (JComponent)var1;
         Color var8 = var7.getBackground();
         if (!(var8 instanceof UIResource)) {
            this.doPaint = false;
            var7.paint(var2);
            getAlternateBorder().paintBorder(var7, var2, var3, var4, var5, var6);
            this.doPaint = true;
         } else {
            JRSUIConstants.State var9 = this.getState(var7);
            this.painter.state.set(var9);
            this.painter.state.set(var7.hasFocus() ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
            this.painter.state.set(var6 > 16 ? JRSUIConstants.Widget.BUTTON_BEVEL : JRSUIConstants.Widget.BUTTON_LIST_HEADER);
            this.painter.state.set(this.selected ? JRSUIConstants.BooleanValue.YES : JRSUIConstants.BooleanValue.NO);
            switch(this.sortOrder) {
            case -1:
               this.painter.state.set(JRSUIConstants.Direction.DOWN);
               break;
            case 1:
               this.painter.state.set(JRSUIConstants.Direction.UP);
               break;
            default:
               this.painter.state.set(JRSUIConstants.Direction.NONE);
            }

            this.painter.paint(var2, var1, var3 - 1, var4 - 1, var5 + 1, var6);
            var2.clipRect(var3, var4, var5, var6);
            var2.translate(this.fHorizontalShift, -1);
            this.doPaint = false;
            var7.paint(var2);
            this.doPaint = true;
         }
      }
   }

   protected JRSUIConstants.State getState(JComponent var1) {
      if (!var1.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else {
         JRootPane var2 = var1.getRootPane();
         if (var2 == null) {
            return JRSUIConstants.State.ACTIVE;
         } else {
            return !AquaFocusHandler.isActive(var2) ? JRSUIConstants.State.INACTIVE : JRSUIConstants.State.ACTIVE;
         }
      }
   }

   protected static Border getAlternateBorder() {
      return (Border)alternateBorder.get();
   }

   public Insets getBorderInsets(Component var1) {
      return this.editorBorderInsets;
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.left = this.editorBorderInsets.left;
      var2.top = this.editorBorderInsets.top;
      var2.right = this.editorBorderInsets.right;
      var2.bottom = this.editorBorderInsets.bottom;
      return var2;
   }

   public boolean isBorderOpaque() {
      return false;
   }

   protected void setSelected(boolean var1) {
      this.selected = var1;
   }

   protected void setHorizontalShift(int var1) {
      this.fHorizontalShift = var1;
   }

   protected void setSortOrder(int var1) {
      if (var1 >= -1 && var1 <= 1) {
         this.sortOrder = var1;
      } else {
         throw new IllegalArgumentException("Invalid sort order constant: " + var1);
      }
   }
}
