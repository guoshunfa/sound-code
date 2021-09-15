package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class AquaInternalFrameBorder implements Border, UIResource {
   private static final int kCloseButton = 0;
   private static final int kIconButton = 1;
   private static final int kGrowButton = 2;
   private static final int sMaxIconWidth = 15;
   private static final int sMaxIconHeight = 15;
   private static final int sAfterButtonPad = 11;
   private static final int sAfterIconPad = 5;
   private static final int sRightSideTitleClip = 0;
   private static final int kContentTester = 100;
   static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorder> documentWindowFrame = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorder>() {
      protected AquaInternalFrameBorder getInstance() {
         return new AquaInternalFrameBorder(JRSUIConstants.WindowType.DOCUMENT);
      }
   };
   static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorder> utilityWindowFrame = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorder>() {
      protected AquaInternalFrameBorder getInstance() {
         return new AquaInternalFrameBorder(JRSUIConstants.WindowType.UTILITY);
      }
   };
   static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorder> dialogWindowFrame = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorder>() {
      protected AquaInternalFrameBorder getInstance() {
         return new AquaInternalFrameBorder(JRSUIConstants.WindowType.DOCUMENT);
      }
   };
   private final AquaInternalFrameBorderMetrics metrics;
   private final int fThisButtonSpan;
   private final int fThisLeftSideTotal;
   private final boolean fIsUtility;
   private final JRSUIConstants.WindowType fWindowKind;
   private Insets fBorderInsets;
   private Color selectedTextColor;
   private Color notSelectedTextColor;
   private Rectangle fInBounds;
   protected final AquaPainter<JRSUIState.TitleBarHeightState> titleBarPainter = AquaPainter.create(JRSUIStateFactory.getTitleBar());
   protected final AquaPainter<JRSUIState> widgetPainter = AquaPainter.create(JRSUIState.getInstance());

   protected static AquaInternalFrameBorder window() {
      return (AquaInternalFrameBorder)documentWindowFrame.get();
   }

   protected static AquaInternalFrameBorder utility() {
      return (AquaInternalFrameBorder)utilityWindowFrame.get();
   }

   protected static AquaInternalFrameBorder dialog() {
      return (AquaInternalFrameBorder)dialogWindowFrame.get();
   }

   protected AquaInternalFrameBorder(JRSUIConstants.WindowType var1) {
      this.fWindowKind = var1;
      ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).set(JRSUIConstants.WindowClipCorners.YES);
      if (this.fWindowKind == JRSUIConstants.WindowType.UTILITY) {
         this.fIsUtility = true;
         this.metrics = AquaInternalFrameBorderMetrics.getMetrics(true);
         this.widgetPainter.state.set(JRSUIConstants.WindowType.UTILITY);
         ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).set(JRSUIConstants.WindowType.UTILITY);
      } else {
         this.fIsUtility = false;
         this.metrics = AquaInternalFrameBorderMetrics.getMetrics(false);
         this.widgetPainter.state.set(JRSUIConstants.WindowType.DOCUMENT);
         ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).set(JRSUIConstants.WindowType.DOCUMENT);
      }

      ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).setValue((double)this.metrics.titleBarHeight);
      ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).set(JRSUIConstants.WindowTitleBarSeparator.YES);
      this.widgetPainter.state.set(JRSUIConstants.AlignmentVertical.CENTER);
      this.fThisButtonSpan = this.metrics.buttonWidth * 3 + this.metrics.buttonPadding * 2;
      this.fThisLeftSideTotal = this.metrics.leftSidePadding + this.fThisButtonSpan + 11;
   }

   public void setColors(Color var1, Color var2) {
      this.selectedTextColor = var1;
      this.notSelectedTextColor = var2;
   }

   protected void setInBounds(int var1, int var2, int var3, int var4) {
      if (this.fInBounds == null) {
         this.fInBounds = new Rectangle();
      }

      this.fInBounds.x = var1;
      this.fInBounds.y = var2;
      this.fInBounds.width = var3;
      this.fInBounds.height = var4;
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder((JInternalFrame)var1, var1, var2, var3, var4, var5, var6);
   }

   protected void paintTitleContents(Graphics var1, JInternalFrame var2, int var3, int var4, int var5, int var6) {
      boolean var7 = var2.isSelected();
      Font var8 = var1.getFont();
      var1.setFont(this.metrics.font);
      FontMetrics var9 = var1.getFontMetrics();
      int var10 = (this.metrics.titleBarHeight + var9.getAscent() - var9.getLeading() - var9.getDescent()) / 2;
      int var11 = this.fThisLeftSideTotal + 0;
      int var12 = this.getIconWidth(var2);
      if (var12 > 0) {
         var12 += 5;
      }

      int var14 = var5 - var11 - var12 - 11;
      String var15 = var2.getTitle();
      String var16 = var15;
      boolean var17 = false;
      int var18 = this.fThisLeftSideTotal;
      boolean var19 = false;
      if (var15 != null && !var15.equals("")) {
         int var23 = SwingUtilities.computeStringWidth(var9, var15);
         if (var23 > var14) {
            var19 = true;
            var23 = SwingUtilities.computeStringWidth(var9, "…");

            int var21;
            for(var21 = 0; var21 < var16.length(); ++var21) {
               int var22 = var9.charWidth(var16.charAt(var21));
               if (var23 + var22 > var14) {
                  break;
               }

               var23 += var22;
            }

            var16 = var16.substring(0, var21) + "…";
         }

         if (!var19) {
            var18 = (var5 - (var23 + var12)) / 2;
            if (var18 < this.fThisLeftSideTotal) {
               var18 = this.fThisLeftSideTotal;
            }
         }

         if (!var7 && !this.fIsUtility) {
            var1.setColor(Color.white);
         } else {
            var1.setColor(Color.lightGray);
         }

         SwingUtilities2.drawString(var2, var1, (String)var16, var3 + var18 + var12, var4 + var10 + 1);
         if (!var7 && !this.fIsUtility) {
            var1.setColor(this.notSelectedTextColor);
         } else {
            var1.setColor(this.selectedTextColor);
         }

         SwingUtilities2.drawString(var2, var1, (String)var16, var3 + var18 + var12, var4 + var10);
         var1.setFont(var8);
      }

      int var20 = (this.metrics.titleBarHeight - this.getIconHeight(var2)) / 2;
      this.paintTitleIcon(var1, var2, var3 + var18, var4 + var20);
   }

   public int getWhichButtonHit(JInternalFrame var1, int var2, int var3) {
      byte var4 = -1;
      Insets var5 = var1.getInsets();
      int var6 = var5.left + this.metrics.leftSidePadding - 1;
      if (this.isInsideYButtonArea(var5, var3) && var2 >= var6) {
         if (var2 <= var6 + this.metrics.buttonWidth) {
            if (var1.isClosable()) {
               var4 = 0;
            }
         } else {
            var6 += this.metrics.buttonWidth + this.metrics.buttonPadding;
            if (var2 >= var6 && var2 <= var6 + this.metrics.buttonWidth) {
               if (var1.isIconifiable()) {
                  var4 = 1;
               }
            } else {
               var6 += this.metrics.buttonWidth + this.metrics.buttonPadding;
               if (var2 >= var6 && var2 <= var6 + this.metrics.buttonWidth && var1.isMaximizable()) {
                  var4 = 2;
               }
            }
         }
      }

      return var4;
   }

   public void doButtonAction(JInternalFrame var1, int var2) {
      switch(var2) {
      case 0:
         var1.doDefaultCloseAction();
         break;
      case 1:
         if (var1.isIconifiable()) {
            if (!var1.isIcon()) {
               try {
                  var1.setIcon(true);
               } catch (PropertyVetoException var7) {
               }
            } else {
               try {
                  var1.setIcon(false);
               } catch (PropertyVetoException var6) {
               }
            }
         }
         break;
      case 2:
         if (var1.isMaximizable()) {
            if (!var1.isMaximum()) {
               try {
                  var1.setMaximum(true);
               } catch (PropertyVetoException var5) {
               }
            } else {
               try {
                  var1.setMaximum(false);
               } catch (PropertyVetoException var4) {
               }
            }
         }
         break;
      default:
         System.err.println("AquaInternalFrameBorder should never get here!!!!");
         Thread.dumpStack();
      }

   }

   public boolean isInsideYButtonArea(Insets var1, int var2) {
      int var3 = var1.top - this.metrics.titleBarHeight / 2 - this.metrics.buttonHeight / 2 - 1;
      int var4 = var3 + this.metrics.buttonHeight;
      return var2 >= var3 && var2 <= var4;
   }

   public boolean getWithinRolloverArea(Insets var1, int var2, int var3) {
      int var4 = var1.left + this.metrics.leftSidePadding;
      int var5 = var4 + this.fThisButtonSpan;
      return this.isInsideYButtonArea(var1, var3) && var2 >= var4 && var2 <= var5;
   }

   protected void paintTitleIcon(Graphics var1, JInternalFrame var2, int var3, int var4) {
      Icon var5 = var2.getFrameIcon();
      if (var5 == null) {
         var5 = UIManager.getIcon("InternalFrame.icon");
      }

      if (var5 != null) {
         if (var5 instanceof ImageIcon && (var5.getIconWidth() > 15 || var5.getIconHeight() > 15)) {
            Image var6 = ((ImageIcon)var5).getImage();
            ((ImageIcon)var5).setImage(var6.getScaledInstance(15, 15, 4));
         }

         var5.paintIcon(var2, var1, var3, var4);
      }
   }

   protected int getIconWidth(JInternalFrame var1) {
      int var2 = 0;
      Icon var3 = var1.getFrameIcon();
      if (var3 == null) {
         var3 = UIManager.getIcon("InternalFrame.icon");
      }

      if (var3 != null && var3 instanceof ImageIcon) {
         var2 = Math.min(var3.getIconWidth(), 15);
      }

      return var2;
   }

   protected int getIconHeight(JInternalFrame var1) {
      int var2 = 0;
      Icon var3 = var1.getFrameIcon();
      if (var3 == null) {
         var3 = UIManager.getIcon("InternalFrame.icon");
      }

      if (var3 != null && var3 instanceof ImageIcon) {
         var2 = Math.min(var3.getIconHeight(), 15);
      }

      return var2;
   }

   public void drawWindowTitle(Graphics var1, JInternalFrame var2, int var3, int var4, int var5, int var6) {
      int var10 = this.metrics.titleBarHeight + var6;
      ((JRSUIState.TitleBarHeightState)this.titleBarPainter.state).set(var2.isSelected() ? JRSUIConstants.State.ACTIVE : JRSUIConstants.State.INACTIVE);
      this.titleBarPainter.paint(var1, var2, var3, var4, var5, var10);
      this.paintTitleContents(var1, var2, var3, var4, var5, var10);
      this.drawAllWidgets(var1, var2);
   }

   void paintBorder(JInternalFrame var1, Component var2, Graphics var3, int var4, int var5, int var6, int var7) {
      if (this.fBorderInsets == null) {
         this.getBorderInsets(var2);
      }

      this.setInBounds(var4 + this.fBorderInsets.left, var5 + this.fBorderInsets.top, var6 - (this.fBorderInsets.right + this.fBorderInsets.left), var7 - (this.fBorderInsets.top + this.fBorderInsets.bottom));
      this.setMetrics(var1, var2);
      this.drawWindowTitle(var3, var1, var4, var5, var6, var7);
   }

   boolean isDirty(JInternalFrame var1) {
      Object var2 = var1.getClientProperty("windowModified");
      return var2 != null && var2 != Boolean.FALSE;
   }

   public Insets getBorderInsets(Component var1) {
      if (this.fBorderInsets == null) {
         this.fBorderInsets = new Insets(0, 0, 0, 0);
      }

      if (!(var1 instanceof JInternalFrame)) {
         return this.fBorderInsets;
      } else {
         JInternalFrame var2 = (JInternalFrame)var1;
         this.setInBounds(0, 0, 100, 100);
         this.setMetrics(var2, var1);
         this.fBorderInsets.left = 0;
         this.fBorderInsets.top = this.metrics.titleBarHeight;
         this.fBorderInsets.right = 0;
         this.fBorderInsets.bottom = 0;
         return this.fBorderInsets;
      }
   }

   public void repaintButtonArea(JInternalFrame var1) {
      Insets var2 = var1.getInsets();
      int var3 = var2.left + this.metrics.leftSidePadding;
      int var4 = var2.top - this.metrics.titleBarHeight + 1;
      var1.repaint(var3, var4, this.fThisButtonSpan, this.metrics.titleBarHeight - 2);
   }

   void drawAllWidgets(Graphics var1, JInternalFrame var2) {
      int var3 = this.metrics.leftSidePadding;
      int var4 = (this.metrics.titleBarHeight - this.metrics.buttonHeight) / 2 - this.metrics.titleBarHeight;
      Insets var5 = var2.getInsets();
      var3 += var5.left;
      var4 += var5.top + this.metrics.downShift;
      AquaInternalFrameUI var6 = (AquaInternalFrameUI)var2.getUI();
      int var7 = var6.getWhichButtonPressed();
      boolean var8 = var6.getMouseOverPressedButton();
      boolean var9 = var6.getRollover();
      boolean var10 = var2.isSelected() || this.fIsUtility;
      boolean var11 = var9 || var10;
      boolean var12 = this.isDirty(var2);
      this.paintButton(var1, var2, var3, var4, 0, var7, var8, var2.isClosable(), var11, var9, var12);
      var3 += this.metrics.buttonPadding + this.metrics.buttonWidth;
      this.paintButton(var1, var2, var3, var4, 1, var7, var8, var2.isIconifiable(), var11, var9, false);
      var3 += this.metrics.buttonPadding + this.metrics.buttonWidth;
      this.paintButton(var1, var2, var3, var4, 2, var7, var8, var2.isMaximizable(), var11, var9, false);
   }

   public void paintButton(Graphics var1, JInternalFrame var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11) {
      this.widgetPainter.state.set(getWidget(var2, var5));
      this.widgetPainter.state.set(getState(var6 == var5 && var7, var10, var9, var8));
      this.widgetPainter.state.set(var11 ? JRSUIConstants.BooleanValue.YES : JRSUIConstants.BooleanValue.NO);
      this.widgetPainter.paint(var1, var2, var3, var4, this.metrics.buttonWidth, this.metrics.buttonHeight);
   }

   static JRSUIConstants.Widget getWidget(JInternalFrame var0, int var1) {
      switch(var1) {
      case 1:
         return JRSUIConstants.Widget.TITLE_BAR_COLLAPSE_BOX;
      case 2:
         return JRSUIConstants.Widget.TITLE_BAR_ZOOM_BOX;
      default:
         return JRSUIConstants.Widget.TITLE_BAR_CLOSE_BOX;
      }
   }

   static JRSUIConstants.State getState(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (!var3) {
         return JRSUIConstants.State.DISABLED;
      } else if (!var2) {
         return JRSUIConstants.State.INACTIVE;
      } else if (var0) {
         return JRSUIConstants.State.PRESSED;
      } else {
         return var1 ? JRSUIConstants.State.ROLLOVER : JRSUIConstants.State.ACTIVE;
      }
   }

   protected void setMetrics(JInternalFrame var1, Component var2) {
      String var3 = var1.getTitle();
      FontMetrics var4 = var1.getFontMetrics(UIManager.getFont("InternalFrame.titleFont"));
      int var5 = 0;
      int var6 = var4.getAscent();
      if (var3 != null) {
         var5 = SwingUtilities.computeStringWidth(var4, var3);
      }

      Icon var7 = var1.getFrameIcon();
      if (var7 != null) {
         int var10000 = var5 + var7.getIconWidth();
         Math.max(var6, var7.getIconHeight());
      }

   }

   protected int getTitleHeight() {
      return this.metrics.titleBarHeight;
   }
}
