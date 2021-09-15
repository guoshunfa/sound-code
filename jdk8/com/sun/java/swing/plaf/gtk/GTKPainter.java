package com.sun.java.swing.plaf.gtk;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;
import sun.awt.UNIXToolkit;

class GTKPainter extends SynthPainter {
   private static final GTKConstants.PositionType[] POSITIONS;
   private static final GTKConstants.ShadowType[] SHADOWS;
   private static final GTKEngine ENGINE;
   static final GTKPainter INSTANCE;

   private GTKPainter() {
   }

   private String getName(SynthContext var1) {
      return var1.getRegion().isSubregion() ? null : var1.getComponent().getName();
   }

   public void paintCheckBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintRadioButtonBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintCheckBoxMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintRadioButtonMenuItemBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintFormattedTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintTextBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintToolBarBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = var1.getComponentState();
      int var9 = GTKLookAndFeel.synthStateToGTKState(var7, var8);
      int var10 = ((JToolBar)var1.getComponent()).getOrientation();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8, var10)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8, var10);
            ENGINE.paintBox(var2, var1, var7, var9, GTKConstants.ShadowType.OUT, "handlebox_bin", var3, var4, var5, var6);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = ((JToolBar)var1.getComponent()).getOrientation();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8);
            ENGINE.paintBox(var2, var1, var7, 1, GTKConstants.ShadowType.OUT, "toolbar", var3, var4, var5, var6);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintPasswordFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintTextBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (this.getName(var1) == "Tree.cellEditor") {
         this.paintTreeCellEditorBackground(var1, var2, var3, var4, var5, var6);
      } else {
         this.paintTextBackground(var1, var2, var3, var4, var5, var6);
      }

   }

   public void paintRadioButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
      if (var8 == 2) {
         synchronized(UNIXToolkit.GTK_LOCK) {
            if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
               ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
               ENGINE.paintFlatBox(var2, var1, var7, 2, GTKConstants.ShadowType.ETCHED_OUT, "checkbutton", var3, var4, var5, var6, ColorType.BACKGROUND);
               ENGINE.finishPainting();
            }
         }
      }

   }

   public void paintRadioButtonMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
      if (var8 == 2) {
         synchronized(UNIXToolkit.GTK_LOCK) {
            if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
               GTKConstants.ShadowType var10 = GTKLookAndFeel.is2_2() ? GTKConstants.ShadowType.NONE : GTKConstants.ShadowType.OUT;
               ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
               ENGINE.paintBox(var2, var1, var7, var8, var10, "menuitem", var3, var4, var5, var6);
               ENGINE.finishPainting();
            }
         }
      }

   }

   public void paintLabelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      String var7 = this.getName(var1);
      JComponent var8 = var1.getComponent();
      Container var9 = var8.getParent();
      if (var7 != "TableHeader.renderer" && var7 != "GTKFileChooser.directoryListLabel" && var7 != "GTKFileChooser.fileListLabel") {
         if (var8 instanceof ListCellRenderer && var9 != null && var9.getParent() instanceof JComboBox) {
            this.paintTextBackground(var1, var2, var3, var4, var5, var6);
         }
      } else {
         this.paintButtonBackgroundImpl(var1, var2, Region.BUTTON, "button", var3, var4, var5, var6, true, false, false, false);
      }

   }

   public void paintInternalFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Metacity.INSTANCE.paintFrameBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paintDesktopPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, ColorType.BACKGROUND);
   }

   public void paintDesktopIconBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Metacity.INSTANCE.paintFrameBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      String var7 = this.getName(var1);
      if (var7 != null && var7.startsWith("InternalFrameTitlePane.")) {
         Metacity.INSTANCE.paintButtonBackground(var1, var2, var3, var4, var5, var6);
      } else {
         AbstractButton var8 = (AbstractButton)var1.getComponent();
         boolean var9 = var8.isContentAreaFilled() && var8.isBorderPainted();
         boolean var10 = var8.isFocusPainted();
         boolean var11 = var8 instanceof JButton && ((JButton)var8).isDefaultCapable();
         boolean var12 = var8.getParent() instanceof JToolBar;
         this.paintButtonBackgroundImpl(var1, var2, Region.BUTTON, "button", var3, var4, var5, var6, var9, var10, var11, var12);
      }

   }

   private void paintButtonBackgroundImpl(SynthContext var1, Graphics var2, Region var3, String var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11, boolean var12) {
      int var13 = var1.getComponentState();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var5, var6, var7, var8, var3, var13, var4, var9, var10, var11, var12)) {
            ENGINE.startPainting(var2, var5, var6, var7, var8, var3, var13, var4, var9, var10, var11, var12);
            GTKStyle var15 = (GTKStyle)var1.getStyle();
            if (var11 && !var12) {
               Insets var16 = var15.getClassSpecificInsetsValue(var1, "default-border", GTKStyle.BUTTON_DEFAULT_BORDER_INSETS);
               if (var9 && (var13 & 1024) != 0) {
                  ENGINE.paintBox(var2, var1, var3, 1, GTKConstants.ShadowType.IN, "buttondefault", var5, var6, var7, var8);
               }

               var5 += var16.left;
               var6 += var16.top;
               var7 -= var16.left + var16.right;
               var8 -= var16.top + var16.bottom;
            }

            boolean var27 = var15.getClassSpecificBoolValue(var1, "interior-focus", true);
            int var17 = var15.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
            int var18 = var15.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
            int var19 = var17 + var18;
            int var20 = var15.getXThickness();
            int var21 = var15.getYThickness();
            if (!var27 && (var13 & 256) == 256) {
               var5 += var19;
               var6 += var19;
               var7 -= 2 * var19;
               var8 -= 2 * var19;
            }

            int var22 = GTKLookAndFeel.synthStateToGTKState(var3, var13);
            boolean var23;
            if (var12) {
               var23 = var22 != 1 && var22 != 8;
            } else {
               var23 = var9 || var22 != 1;
            }

            if (var23) {
               GTKConstants.ShadowType var24 = GTKConstants.ShadowType.OUT;
               if ((var13 & 516) != 0) {
                  var24 = GTKConstants.ShadowType.IN;
               }

               ENGINE.paintBox(var2, var1, var3, var22, var24, var4, var5, var6, var7, var8);
            }

            if (var10 && (var13 & 256) != 0) {
               if (var27) {
                  var5 += var20 + var18;
                  var6 += var21 + var18;
                  var7 -= 2 * (var20 + var18);
                  var8 -= 2 * (var21 + var18);
               } else {
                  var5 -= var19;
                  var6 -= var19;
                  var7 += 2 * var19;
                  var8 += 2 * var19;
               }

               ENGINE.paintFocus(var2, var1, var3, var22, var4, var5, var6, var7, var8);
            }

            ENGINE.finishPainting();
         }
      }
   }

   public void paintArrowButtonForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      JComponent var9 = var1.getComponent();
      String var10 = var9.getName();
      GTKConstants.ArrowType var11 = null;
      switch(var7) {
      case 1:
         var11 = GTKConstants.ArrowType.UP;
      case 2:
      case 4:
      case 6:
      default:
         break;
      case 3:
         var11 = GTKConstants.ArrowType.RIGHT;
         break;
      case 5:
         var11 = GTKConstants.ArrowType.DOWN;
         break;
      case 7:
         var11 = GTKConstants.ArrowType.LEFT;
      }

      String var12 = "arrow";
      if (var10 != "ScrollBar.button" && var10 != "TabbedPane.button") {
         if (var10 != "Spinner.nextButton" && var10 != "Spinner.previousButton") {
            assert var10 == "ComboBox.arrowButton" : "unexpected name: " + var10;
         } else {
            var12 = "spinbutton";
         }
      } else if (var11 != GTKConstants.ArrowType.UP && var11 != GTKConstants.ArrowType.DOWN) {
         var12 = "hscrollbar";
      } else {
         var12 = "vscrollbar";
      }

      int var13 = GTKLookAndFeel.synthStateToGTKState(var8, var1.getComponentState());
      GTKConstants.ShadowType var14 = var13 == 4 ? GTKConstants.ShadowType.IN : GTKConstants.ShadowType.OUT;
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var13, var10, var7)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var13, var10, var7);
            ENGINE.paintArrow(var2, var1, var8, var13, var14, var11, var12, var3, var4, var5, var6);
            ENGINE.finishPainting();
         }
      }
   }

   public void paintArrowButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      AbstractButton var8 = (AbstractButton)var1.getComponent();
      String var9 = var8.getName();
      String var10 = "button";
      int var11 = 0;
      if (var9 != "ScrollBar.button" && var9 != "TabbedPane.button") {
         if (var9 == "Spinner.previousButton") {
            var10 = "spinbutton_down";
         } else if (var9 == "Spinner.nextButton") {
            var10 = "spinbutton_up";
         } else {
            assert var9 == "ComboBox.arrowButton" : "unexpected name: " + var9;
         }
      } else {
         Integer var12 = (Integer)var8.getClientProperty("__arrow_direction__");
         var11 = var12 != null ? var12 : 7;
         switch(var11) {
         case 1:
         case 5:
            var10 = "vscrollbar";
            break;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         default:
            var10 = "hscrollbar";
         }
      }

      int var19 = var1.getComponentState();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var19, var10, var11)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var19, var10, var11);
            int var14;
            if (var10.startsWith("spin")) {
               var14 = var8.getParent().isEnabled() ? 1 : 8;
               int var15 = var10 == "spinbutton_up" ? var4 : var4 - var6;
               int var16 = var6 * 2;
               ENGINE.paintBox(var2, var1, var7, var14, GTKConstants.ShadowType.IN, "spinbutton", var3, var15, var5, var16);
            }

            var14 = GTKLookAndFeel.synthStateToGTKState(var7, var19);
            GTKConstants.ShadowType var20 = GTKConstants.ShadowType.OUT;
            if ((var14 & 516) != 0) {
               var20 = GTKConstants.ShadowType.IN;
            }

            ENGINE.paintBox(var2, var1, var7, var14, var20, var10, var3, var4, var5, var6);
            ENGINE.finishPainting();
         }
      }
   }

   public void paintListBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, GTKColorType.TEXT_BACKGROUND);
   }

   public void paintMenuBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
            GTKStyle var9 = (GTKStyle)var1.getStyle();
            int var10 = var9.getClassSpecificIntValue((SynthContext)var1, "shadow-type", 2);
            GTKConstants.ShadowType var11 = SHADOWS[var10];
            int var12 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
            ENGINE.paintBox(var2, var1, var7, var12, var11, "menubar", var3, var4, var5, var6);
            ENGINE.finishPainting();
         }
      }
   }

   public void paintMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintMenuItemBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      int var7 = GTKLookAndFeel.synthStateToGTKState(var1.getRegion(), var1.getComponentState());
      if (var7 == 2) {
         Region var8 = Region.MENU_ITEM;
         synchronized(UNIXToolkit.GTK_LOCK) {
            if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8)) {
               GTKConstants.ShadowType var10 = GTKLookAndFeel.is2_2() ? GTKConstants.ShadowType.NONE : GTKConstants.ShadowType.OUT;
               ENGINE.startPainting(var2, var3, var4, var5, var6, var8);
               ENGINE.paintBox(var2, var1, var8, var7, var10, "menuitem", var3, var4, var5, var6);
               ENGINE.finishPainting();
            }
         }
      }

   }

   public void paintPopupMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8);
            ENGINE.paintBox(var2, var1, var7, var8, GTKConstants.ShadowType.OUT, "menu", var3, var4, var5, var6);
            GTKStyle var10 = (GTKStyle)var1.getStyle();
            int var11 = var10.getXThickness();
            int var12 = var10.getYThickness();
            ENGINE.paintBackground(var2, var1, var7, var8, var10.getGTKColor(var1, var8, GTKColorType.BACKGROUND), var3 + var11, var4 + var12, var5 - var11 - var11, var6 - var12 - var12);
            ENGINE.finishPainting();
         }
      }
   }

   public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
            ENGINE.paintBox(var2, var1, var7, 1, GTKConstants.ShadowType.IN, "trough", var3, var4, var5, var6);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintProgressBarForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (var5 > 0 && var6 > 0) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var8, "fg");
            ENGINE.paintBox(var2, var1, var8, 2, GTKConstants.ShadowType.OUT, "bar", var3, var4, var5, var6);
            ENGINE.finishPainting(false);
         }
      }
   }

   public void paintViewportBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
            ENGINE.paintShadow(var2, var1, var7, 1, GTKConstants.ShadowType.IN, "scrolled_window", var3, var4, var5, var6);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      int var9 = var1.getComponentState();
      JComponent var10 = var1.getComponent();
      String var11;
      if (var10 instanceof JToolBar.Separator) {
         var11 = "toolbar";
         float var12 = 0.2F;
         JToolBar.Separator var13 = (JToolBar.Separator)var10;
         Dimension var14 = var13.getSeparatorSize();
         GTKStyle var15 = (GTKStyle)var1.getStyle();
         if (var7 == 0) {
            var3 += (int)((float)var5 * var12);
            var5 -= (int)((float)var5 * var12 * 2.0F);
            var4 += (var14.height - var15.getYThickness()) / 2;
         } else {
            var4 += (int)((float)var6 * var12);
            var6 -= (int)((float)var6 * var12 * 2.0F);
            var3 += (var14.width - var15.getXThickness()) / 2;
         }
      } else {
         var11 = "separator";
         Insets var18 = var10.getInsets();
         var3 += var18.left;
         var4 += var18.top;
         if (var7 == 0) {
            var5 -= var18.left + var18.right;
         } else {
            var6 -= var18.top + var18.bottom;
         }
      }

      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8, var9, var11, var7)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var8, var9, var11, var7);
            if (var7 == 0) {
               ENGINE.paintHline(var2, var1, var8, var9, var11, var3, var4, var5, var6);
            } else {
               ENGINE.paintVline(var2, var1, var8, var9, var11, var3, var4, var5, var6);
            }

            ENGINE.finishPainting();
         }

      }
   }

   public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = var1.getComponentState();
      boolean var9 = (var8 & 256) != 0;
      int var10 = 0;
      if (var9) {
         GTKStyle var11 = (GTKStyle)var1.getStyle();
         var10 = var11.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1) + var11.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
         var3 -= var10;
         var4 -= var10;
         var5 += var10 * 2;
         var6 += var10 * 2;
      }

      JSlider var24 = (JSlider)var1.getComponent();
      double var12 = (double)var24.getValue();
      double var14 = (double)var24.getMinimum();
      double var16 = (double)var24.getMaximum();
      double var18 = 20.0D;
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (var5 > 0 && var6 > 0) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8, var12);
            int var21 = GTKLookAndFeel.synthStateToGTKState(var7, var8);
            ENGINE.setRangeValue(var1, var7, var12, var14, var16, var18);
            ENGINE.paintBox(var2, var1, var7, var21, GTKConstants.ShadowType.IN, "trough", var3 + var10, var4 + var10, var5 - 2 * var10, var6 - 2 * var10);
            if (var9) {
               ENGINE.paintFocus(var2, var1, var7, 1, "trough", var3, var4, var5, var6);
            }

            ENGINE.finishPainting(false);
         }
      }
   }

   public void paintSliderThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      int var9 = GTKLookAndFeel.synthStateToGTKState(var8, var1.getComponentState());
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8, var9, var7)) {
            GTKConstants.Orientation var11 = var7 == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
            String var12 = var7 == 0 ? "hscale" : "vscale";
            ENGINE.startPainting(var2, var3, var4, var5, var6, var8, var9, var7);
            ENGINE.paintSlider(var2, var1, var8, var9, GTKConstants.ShadowType.OUT, var12, var3, var4, var5, var6, var11);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintSpinnerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
      JSplitPane var9 = (JSplitPane)var1.getComponent();
      GTKConstants.Orientation var10 = var9.getOrientation() == 1 ? GTKConstants.Orientation.VERTICAL : GTKConstants.Orientation.HORIZONTAL;
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8, var10)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8, var10);
            ENGINE.paintHandle(var2, var1, var7, var8, GTKConstants.ShadowType.OUT, "paned", var3, var4, var5, var6, var10);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintSplitPaneDragDivider(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintSplitPaneDividerForeground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintTabbedPaneContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JTabbedPane var7 = (JTabbedPane)var1.getComponent();
      int var8 = var7.getSelectedIndex();
      GTKConstants.PositionType var9 = GTKLookAndFeel.SwingOrientationConstantToGTK(var7.getTabPlacement());
      int var10 = 0;
      int var11 = 0;
      if (var8 != -1) {
         Rectangle var12 = var7.getBoundsAt(var8);
         if (var9 != GTKConstants.PositionType.TOP && var9 != GTKConstants.PositionType.BOTTOM) {
            var10 = var12.y - var4;
            var11 = var12.height;
         } else {
            var10 = var12.x - var3;
            var11 = var12.width;
         }
      }

      Region var17 = var1.getRegion();
      int var13 = GTKLookAndFeel.synthStateToGTKState(var17, var1.getComponentState());
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var17, var13, var9, var10, var11)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var17, var13, var9, var10, var11);
            ENGINE.paintBoxGap(var2, var1, var17, var13, GTKConstants.ShadowType.OUT, "notebook", var3, var4, var5, var6, var9, var10, var11);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      int var9 = var1.getComponentState();
      int var10 = (var9 & 512) != 0 ? 1 : 4;
      JTabbedPane var11 = (JTabbedPane)var1.getComponent();
      int var12 = var11.getTabPlacement();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8, var10, var12, var7)) {
            GTKConstants.PositionType var14 = POSITIONS[var12 - 1];
            ENGINE.startPainting(var2, var3, var4, var5, var6, var8, var10, var12, var7);
            ENGINE.paintExtension(var2, var1, var8, var10, GTKConstants.ShadowType.OUT, "tab", var3, var4, var5, var6, var14, var7);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintTextPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintTextAreaBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintEditorPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintTextAreaBackground(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, GTKColorType.TEXT_BACKGROUND);
   }

   private void paintTextBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JComponent var7 = var1.getComponent();
      Container var8 = var7.getParent();
      Container var9 = null;
      GTKStyle var10 = (GTKStyle)var1.getStyle();
      Region var11 = var1.getRegion();
      int var12 = var1.getComponentState();
      if (var7 instanceof ListCellRenderer && var8 != null) {
         var9 = var8.getParent();
         if (var9 instanceof JComboBox && var9.hasFocus()) {
            var12 |= 256;
         }
      }

      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var11, var12)) {
            int var14 = GTKLookAndFeel.synthStateToGTKState(var11, var12);
            boolean var15 = false;
            boolean var16 = var10.getClassSpecificBoolValue(var1, "interior-focus", true);
            int var21 = var10.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
            if (!var16 && (var12 & 256) != 0) {
               var3 += var21;
               var4 += var21;
               var5 -= 2 * var21;
               var6 -= 2 * var21;
            }

            int var17 = var10.getXThickness();
            int var18 = var10.getYThickness();
            ENGINE.startPainting(var2, var3, var4, var5, var6, var11, var12);
            ENGINE.paintShadow(var2, var1, var11, var14, GTKConstants.ShadowType.IN, "entry", var3, var4, var5, var6);
            ENGINE.paintFlatBox(var2, var1, var11, var14, GTKConstants.ShadowType.NONE, "entry_bg", var3 + var17, var4 + var18, var5 - 2 * var17, var6 - 2 * var18, ColorType.TEXT_BACKGROUND);
            if (var21 > 0 && (var12 & 256) != 0) {
               if (!var16) {
                  var3 -= var21;
                  var4 -= var21;
                  var5 += 2 * var21;
                  var6 += 2 * var21;
               } else if (var9 instanceof JComboBox) {
                  var3 += var21 + 2;
                  var4 += var21 + 1;
                  var5 -= 2 * var21 + 1;
                  var6 -= 2 * var21 + 2;
               } else {
                  var3 += var21;
                  var4 += var21;
                  var5 -= 2 * var21;
                  var6 -= 2 * var21;
               }

               ENGINE.paintFocus(var2, var1, var11, var14, "entry", var3, var4, var5, var6);
            }

            ENGINE.finishPainting();
         }
      }
   }

   private void paintTreeCellEditorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = GTKLookAndFeel.synthStateToGTKState(var7, var1.getComponentState());
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8);
            ENGINE.paintFlatBox(var2, var1, var7, var8, GTKConstants.ShadowType.NONE, "entry_bg", var3, var4, var5, var6, ColorType.TEXT_BACKGROUND);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintRootPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, GTKColorType.BACKGROUND);
   }

   public void paintToggleButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      JToggleButton var8 = (JToggleButton)var1.getComponent();
      boolean var9 = var8.isContentAreaFilled() && var8.isBorderPainted();
      boolean var10 = var8.isFocusPainted();
      boolean var11 = var8.getParent() instanceof JToolBar;
      this.paintButtonBackgroundImpl(var1, var2, var7, "button", var3, var4, var5, var6, var9, var10, false, var11);
   }

   public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      boolean var8 = (var1.getComponentState() & 256) != 0;
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8);
            Insets var10 = var1.getComponent().getInsets();
            GTKStyle var11 = (GTKStyle)var1.getStyle();
            int var12 = var11.getClassSpecificIntValue((SynthContext)var1, "trough-border", 1);
            var10.left -= var12;
            var10.right -= var12;
            var10.top -= var12;
            var10.bottom -= var12;
            ENGINE.paintBox(var2, var1, var7, 4, GTKConstants.ShadowType.IN, "trough", var3 + var10.left, var4 + var10.top, var5 - var10.left - var10.right, var6 - var10.top - var10.bottom);
            if (var8) {
               ENGINE.paintFocus(var2, var1, var7, 1, "trough", var3, var4, var5, var6);
            }

            ENGINE.finishPainting();
         }
      }
   }

   public void paintScrollBarThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      Region var8 = var1.getRegion();
      int var9 = GTKLookAndFeel.synthStateToGTKState(var8, var1.getComponentState());
      JScrollBar var10 = (JScrollBar)var1.getComponent();
      boolean var11 = var10.getOrientation() == 0 && !var10.getComponentOrientation().isLeftToRight();
      double var12 = 0.0D;
      double var14 = 100.0D;
      double var16 = 20.0D;
      double var18;
      if (var10.getMaximum() - var10.getMinimum() == var10.getVisibleAmount()) {
         var18 = 0.0D;
         var16 = 100.0D;
      } else if (var10.getValue() == var10.getMinimum()) {
         var18 = var11 ? 100.0D : 0.0D;
      } else if (var10.getValue() >= var10.getMaximum() - var10.getVisibleAmount()) {
         var18 = var11 ? 0.0D : 100.0D;
      } else {
         var18 = 50.0D;
      }

      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8, var9, var7, var18, var16, var11)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var8, var9, var7, var18, var16, var11);
            GTKConstants.Orientation var21 = var7 == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
            ENGINE.setRangeValue(var1, var8, var18, var12, var14, var16);
            ENGINE.paintSlider(var2, var1, var8, var9, GTKConstants.ShadowType.OUT, "slider", var3, var4, var5, var6, var21);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintToolTipBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7);
            ENGINE.paintFlatBox(var2, var1, var7, 1, GTKConstants.ShadowType.OUT, "tooltip", var3, var4, var5, var6, ColorType.BACKGROUND);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintTreeCellBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = var1.getRegion();
      int var8 = var1.getComponentState();
      int var9 = GTKLookAndFeel.synthStateToGTKState(var7, var8);
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var7, var8)) {
            ENGINE.startPainting(var2, var3, var4, var5, var6, var7, var8);
            ENGINE.paintFlatBox(var2, var1, var7, var9, GTKConstants.ShadowType.NONE, "cell_odd", var3, var4, var5, var6, ColorType.TEXT_BACKGROUND);
            ENGINE.finishPainting();
         }

      }
   }

   public void paintTreeCellFocus(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Region var7 = Region.TREE_CELL;
      int var8 = var1.getComponentState();
      this.paintFocus(var1, var2, var7, var8, "treeview", var3, var4, var5, var6);
   }

   public void paintTreeBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, GTKColorType.TEXT_BACKGROUND);
   }

   public void paintViewportBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.fillArea(var1, var2, var3, var4, var5, var6, GTKColorType.TEXT_BACKGROUND);
   }

   void paintFocus(SynthContext var1, Graphics var2, Region var3, int var4, String var5, int var6, int var7, int var8, int var9) {
      int var10 = GTKLookAndFeel.synthStateToGTKState(var3, var4);
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var6, var7, var8, var9, var3, var10, "focus")) {
            ENGINE.startPainting(var2, var6, var7, var8, var9, var3, var10, "focus");
            ENGINE.paintFocus(var2, var1, var3, var10, var5, var6, var7, var8, var9);
            ENGINE.finishPainting();
         }

      }
   }

   void paintMetacityElement(SynthContext var1, Graphics var2, int var3, String var4, int var5, int var6, int var7, int var8, GTKConstants.ShadowType var9, GTKConstants.ArrowType var10) {
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var5, var6, var7, var8, var3, var4, var9, var10)) {
            ENGINE.startPainting(var2, var5, var6, var7, var8, var3, var4, var9, var10);
            if (var4 == "metacity-arrow") {
               ENGINE.paintArrow(var2, var1, Region.INTERNAL_FRAME_TITLE_PANE, var3, var9, var10, "", var5, var6, var7, var8);
            } else if (var4 == "metacity-box") {
               ENGINE.paintBox(var2, var1, Region.INTERNAL_FRAME_TITLE_PANE, var3, var9, "", var5, var6, var7, var8);
            } else if (var4 == "metacity-vline") {
               ENGINE.paintVline(var2, var1, Region.INTERNAL_FRAME_TITLE_PANE, var3, "", var5, var6, var7, var8);
            }

            ENGINE.finishPainting();
         }

      }
   }

   void paintIcon(SynthContext var1, Graphics var2, Method var3, int var4, int var5, int var6, int var7) {
      int var8 = var1.getComponentState();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var4, var5, var6, var7, var8, var3)) {
            ENGINE.startPainting(var2, var4, var5, var6, var7, var8, var3);

            try {
               var3.invoke(this, var1, var2, var8, var4, var5, var6, var7);
            } catch (IllegalAccessException var12) {
               assert false;
            } catch (InvocationTargetException var13) {
               assert false;
            }

            ENGINE.finishPainting();
         }

      }
   }

   void paintIcon(SynthContext var1, Graphics var2, Method var3, int var4, int var5, int var6, int var7, Object var8) {
      int var9 = var1.getComponentState();
      synchronized(UNIXToolkit.GTK_LOCK) {
         if (!ENGINE.paintCachedImage(var2, var4, var5, var6, var7, var9, var3, var8)) {
            ENGINE.startPainting(var2, var4, var5, var6, var7, var9, var3, var8);

            try {
               var3.invoke(this, var1, var2, var9, var4, var5, var6, var7, var8);
            } catch (IllegalAccessException var13) {
               assert false;
            } catch (InvocationTargetException var14) {
               assert false;
            }

            ENGINE.finishPainting();
         }

      }
   }

   public void paintTreeExpandedIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      ENGINE.paintExpander(var2, var1, Region.TREE, GTKLookAndFeel.synthStateToGTKState(var1.getRegion(), var3), GTKConstants.ExpanderStyle.EXPANDED, "treeview", var4, var5, var6, var7);
   }

   public void paintTreeCollapsedIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      ENGINE.paintExpander(var2, var1, Region.TREE, GTKLookAndFeel.synthStateToGTKState(var1.getRegion(), var3), GTKConstants.ExpanderStyle.COLLAPSED, "treeview", var4, var5, var6, var7);
   }

   public void paintCheckBoxIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      GTKStyle var8 = (GTKStyle)var1.getStyle();
      int var9 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-size", 13);
      int var10 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-spacing", 2);
      ENGINE.paintCheck(var2, var1, Region.CHECK_BOX, "checkbutton", var4 + var10, var5 + var10, var9, var9);
   }

   public void paintRadioButtonIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      GTKStyle var8 = (GTKStyle)var1.getStyle();
      int var9 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-size", 13);
      int var10 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-spacing", 2);
      ENGINE.paintOption(var2, var1, Region.RADIO_BUTTON, "radiobutton", var4 + var10, var5 + var10, var9, var9);
   }

   public void paintMenuArrowIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, GTKConstants.ArrowType var8) {
      int var9 = GTKLookAndFeel.synthStateToGTKState(var1.getRegion(), var3);
      GTKConstants.ShadowType var10 = GTKConstants.ShadowType.OUT;
      if (var9 == 2) {
         var10 = GTKConstants.ShadowType.IN;
      }

      ENGINE.paintArrow(var2, var1, Region.MENU_ITEM, var9, var10, var8, "menuitem", var4 + 3, var5 + 3, 7, 7);
   }

   public void paintCheckBoxMenuItemCheckIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      GTKStyle var8 = (GTKStyle)var1.getStyle();
      int var9 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-size", 12);
      ENGINE.paintCheck(var2, var1, Region.CHECK_BOX_MENU_ITEM, "check", var4 + 1, var5 + 1, var9, var9);
   }

   public void paintRadioButtonMenuItemCheckIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      GTKStyle var8 = (GTKStyle)var1.getStyle();
      int var9 = var8.getClassSpecificIntValue((SynthContext)var1, "indicator-size", 12);
      ENGINE.paintOption(var2, var1, Region.RADIO_BUTTON_MENU_ITEM, "option", var4 + 1, var5 + 1, var9, var9);
   }

   public void paintToolBarHandleIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, GTKConstants.Orientation var8) {
      int var9 = GTKLookAndFeel.synthStateToGTKState(var1.getRegion(), var3);
      var8 = var8 == GTKConstants.Orientation.HORIZONTAL ? GTKConstants.Orientation.VERTICAL : GTKConstants.Orientation.HORIZONTAL;
      ENGINE.paintHandle(var2, var1, Region.TOOL_BAR, var9, GTKConstants.ShadowType.OUT, "handlebox", var4, var5, var6, var7, var8);
   }

   public void paintAscendingSortIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      ENGINE.paintArrow(var2, var1, Region.TABLE, 1, GTKConstants.ShadowType.IN, GTKConstants.ArrowType.UP, "arrow", var4, var5, var6, var7);
   }

   public void paintDescendingSortIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      ENGINE.paintArrow(var2, var1, Region.TABLE, 1, GTKConstants.ShadowType.IN, GTKConstants.ArrowType.DOWN, "arrow", var4, var5, var6, var7);
   }

   private void fillArea(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, ColorType var7) {
      if (var1.getComponent().isOpaque()) {
         Region var8 = var1.getRegion();
         int var9 = GTKLookAndFeel.synthStateToGTKState(var8, var1.getComponentState());
         GTKStyle var10 = (GTKStyle)var1.getStyle();
         var2.setColor(var10.getGTKColor(var1, var9, var7));
         var2.fillRect(var3, var4, var5, var6);
      }

   }

   static {
      POSITIONS = new GTKConstants.PositionType[]{GTKConstants.PositionType.BOTTOM, GTKConstants.PositionType.RIGHT, GTKConstants.PositionType.TOP, GTKConstants.PositionType.LEFT};
      SHADOWS = new GTKConstants.ShadowType[]{GTKConstants.ShadowType.NONE, GTKConstants.ShadowType.IN, GTKConstants.ShadowType.OUT, GTKConstants.ShadowType.ETCHED_IN, GTKConstants.ShadowType.OUT};
      ENGINE = GTKEngine.INSTANCE;
      INSTANCE = new GTKPainter();
   }

   static class TitledBorder extends AbstractBorder implements UIResource {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         SynthContext var7 = this.getContext((JComponent)var1);
         Region var8 = var7.getRegion();
         int var9 = var7.getComponentState();
         int var10 = GTKLookAndFeel.synthStateToGTKState(var8, var9);
         synchronized(UNIXToolkit.GTK_LOCK) {
            if (!GTKPainter.ENGINE.paintCachedImage(var2, var3, var4, var5, var6, var8)) {
               GTKPainter.ENGINE.startPainting(var2, var3, var4, var5, var6, var8);
               GTKPainter.ENGINE.paintShadow(var2, var7, var8, var10, GTKConstants.ShadowType.ETCHED_IN, "frame", var3, var4, var5, var6);
               GTKPainter.ENGINE.finishPainting();
            }

         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         SynthContext var3 = this.getContext((JComponent)var1);
         return var3.getStyle().getInsets(var3, var2);
      }

      public boolean isBorderOpaque() {
         return true;
      }

      private SynthStyle getStyle(JComponent var1) {
         return SynthLookAndFeel.getStyle(var1, GTKEngine.CustomRegion.TITLED_BORDER);
      }

      private SynthContext getContext(JComponent var1) {
         short var2 = 1024;
         return new SynthContext(var1, GTKEngine.CustomRegion.TITLED_BORDER, this.getStyle(var1), var2);
      }
   }

   static class ListTableFocusBorder extends AbstractBorder implements UIResource {
      private boolean selectedCell;
      private boolean focusedCell;

      public static GTKPainter.ListTableFocusBorder getSelectedCellBorder() {
         return new GTKPainter.ListTableFocusBorder(true, true);
      }

      public static GTKPainter.ListTableFocusBorder getUnselectedCellBorder() {
         return new GTKPainter.ListTableFocusBorder(false, true);
      }

      public static GTKPainter.ListTableFocusBorder getNoFocusCellBorder() {
         return new GTKPainter.ListTableFocusBorder(false, false);
      }

      public ListTableFocusBorder(boolean var1, boolean var2) {
         this.selectedCell = var1;
         this.focusedCell = var2;
      }

      private SynthContext getContext(Component var1) {
         SynthContext var2 = null;
         LabelUI var3 = null;
         if (var1 instanceof JLabel) {
            var3 = ((JLabel)var1).getUI();
         }

         if (var3 instanceof SynthUI) {
            var2 = ((SynthUI)var3).getContext((JComponent)var1);
         }

         return var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (this.focusedCell) {
            SynthContext var7 = this.getContext(var1);
            int var8 = this.selectedCell ? 512 : 257;
            if (var7 != null) {
               GTKPainter.INSTANCE.paintFocus(var7, var2, Region.TABLE, var8, "", var3, var4, var5, var6);
            }
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         SynthContext var3 = this.getContext(var1);
         if (var3 != null) {
            var2 = var3.getStyle().getInsets(var3, var2);
         }

         return var2;
      }

      public boolean isBorderOpaque() {
         return true;
      }
   }
}
