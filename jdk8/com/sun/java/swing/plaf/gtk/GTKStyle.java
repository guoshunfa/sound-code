package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;
import sun.awt.AppContext;
import sun.awt.UNIXToolkit;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthIcon;

class GTKStyle extends SynthStyle implements GTKConstants {
   private static final String ICON_PROPERTY_PREFIX = "gtk.icon.";
   static final Color BLACK_COLOR;
   static final Color WHITE_COLOR;
   static final Font DEFAULT_FONT;
   static final Insets BUTTON_DEFAULT_BORDER_INSETS;
   private static final GTKGraphicsUtils GTK_GRAPHICS;
   private static final Map<String, String> CLASS_SPECIFIC_MAP;
   private static final Map<String, GTKStyle.GTKStockIcon> ICONS_MAP;
   private final Font font;
   private final int widgetType;
   private final int xThickness;
   private final int yThickness;

   private static native int nativeGetXThickness(int var0);

   private static native int nativeGetYThickness(int var0);

   private static native int nativeGetColorForState(int var0, int var1, int var2);

   private static native Object nativeGetClassValue(int var0, String var1);

   private static native String nativeGetPangoFontName(int var0);

   GTKStyle(Font var1, GTKEngine.WidgetType var2) {
      this.widgetType = var2.ordinal();
      String var3;
      synchronized(UNIXToolkit.GTK_LOCK) {
         this.xThickness = nativeGetXThickness(this.widgetType);
         this.yThickness = nativeGetYThickness(this.widgetType);
         var3 = nativeGetPangoFontName(this.widgetType);
      }

      Font var4 = null;
      if (var3 != null) {
         var4 = PangoFonts.lookupFont(var3);
      }

      if (var4 != null) {
         this.font = var4;
      } else if (var1 != null) {
         this.font = var1;
      } else {
         this.font = DEFAULT_FONT;
      }

   }

   public void installDefaults(SynthContext var1) {
      super.installDefaults(var1);
      if (!var1.getRegion().isSubregion()) {
         var1.getComponent().putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, GTKLookAndFeel.aaTextInfo);
      }

   }

   public SynthGraphicsUtils getGraphicsUtils(SynthContext var1) {
      return GTK_GRAPHICS;
   }

   public SynthPainter getPainter(SynthContext var1) {
      return GTKPainter.INSTANCE;
   }

   protected Color getColorForState(SynthContext var1, ColorType var2) {
      if (var2 != ColorType.FOCUS && var2 != GTKColorType.BLACK) {
         if (var2 == GTKColorType.WHITE) {
            return WHITE_COLOR;
         } else {
            Region var3 = var1.getRegion();
            int var4 = var1.getComponentState();
            var4 = GTKLookAndFeel.synthStateToGTKState(var3, var4);
            if (var2 != ColorType.TEXT_FOREGROUND || var3 != Region.BUTTON && var3 != Region.CHECK_BOX && var3 != Region.CHECK_BOX_MENU_ITEM && var3 != Region.MENU && var3 != Region.MENU_ITEM && var3 != Region.RADIO_BUTTON && var3 != Region.RADIO_BUTTON_MENU_ITEM && var3 != Region.TABBED_PANE_TAB && var3 != Region.TOGGLE_BUTTON && var3 != Region.TOOL_TIP && var3 != Region.MENU_ITEM_ACCELERATOR && var3 != Region.TABBED_PANE_TAB) {
               if (var3 == Region.TABLE || var3 == Region.LIST || var3 == Region.TREE || var3 == Region.TREE_CELL) {
                  if (var2 == ColorType.FOREGROUND) {
                     var2 = ColorType.TEXT_FOREGROUND;
                     if (var4 == 4) {
                        var4 = 512;
                     }
                  } else if (var2 == ColorType.BACKGROUND) {
                     var2 = ColorType.TEXT_BACKGROUND;
                  }
               }
            } else {
               var2 = ColorType.FOREGROUND;
            }

            return this.getStyleSpecificColor(var1, var4, var2);
         }
      } else {
         return BLACK_COLOR;
      }
   }

   private Color getStyleSpecificColor(SynthContext var1, int var2, ColorType var3) {
      var2 = GTKLookAndFeel.synthStateToGTKStateType(var2).ordinal();
      synchronized(UNIXToolkit.GTK_LOCK) {
         int var5 = nativeGetColorForState(this.widgetType, var2, var3.getID());
         return new ColorUIResource(var5);
      }
   }

   Color getGTKColor(int var1, ColorType var2) {
      return this.getGTKColor((SynthContext)null, var1, var2);
   }

   Color getGTKColor(SynthContext var1, int var2, ColorType var3) {
      if (var1 != null) {
         JComponent var4 = var1.getComponent();
         Region var5 = var1.getRegion();
         var2 = GTKLookAndFeel.synthStateToGTKState(var5, var2);
         if (!var5.isSubregion() && (var2 & 1) != 0) {
            Color var6;
            if (var3 != ColorType.BACKGROUND && var3 != ColorType.TEXT_BACKGROUND) {
               if (var3 == ColorType.FOREGROUND || var3 == ColorType.TEXT_FOREGROUND) {
                  var6 = var4.getForeground();
                  if (!(var6 instanceof UIResource)) {
                     return var6;
                  }
               }
            } else {
               var6 = var4.getBackground();
               if (!(var6 instanceof UIResource)) {
                  return var6;
               }
            }
         }
      }

      return this.getStyleSpecificColor(var1, var2, var3);
   }

   public Color getColor(SynthContext var1, ColorType var2) {
      JComponent var3 = var1.getComponent();
      Region var4 = var1.getRegion();
      int var5 = var1.getComponentState();
      if (var3.getName() == "Table.cellRenderer") {
         if (var2 == ColorType.BACKGROUND) {
            return var3.getBackground();
         }

         if (var2 == ColorType.FOREGROUND) {
            return var3.getForeground();
         }
      }

      if (var4 == Region.LABEL && var2 == ColorType.TEXT_FOREGROUND) {
         var2 = ColorType.FOREGROUND;
      }

      if (!var4.isSubregion() && (var5 & 1) != 0) {
         if (var2 == ColorType.BACKGROUND) {
            return var3.getBackground();
         }

         if (var2 == ColorType.FOREGROUND) {
            return var3.getForeground();
         }

         if (var2 == ColorType.TEXT_FOREGROUND) {
            Color var6 = var3.getForeground();
            if (var6 != null && !(var6 instanceof UIResource)) {
               return var6;
            }
         }
      }

      return this.getColorForState(var1, var2);
   }

   protected Font getFontForState(SynthContext var1) {
      return this.font;
   }

   int getXThickness() {
      return this.xThickness;
   }

   int getYThickness() {
      return this.yThickness;
   }

   public Insets getInsets(SynthContext var1, Insets var2) {
      Region var3 = var1.getRegion();
      JComponent var4 = var1.getComponent();
      String var5 = var3.isSubregion() ? null : var4.getName();
      if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      } else {
         var2.top = var2.bottom = var2.left = var2.right = 0;
      }

      if (var3 != Region.ARROW_BUTTON && var3 != Region.BUTTON && var3 != Region.TOGGLE_BUTTON) {
         if (var3 != Region.CHECK_BOX && var3 != Region.RADIO_BUTTON) {
            if (var3 == Region.MENU_BAR) {
               return this.getMenuBarInsets(var1, var2);
            } else if (var3 != Region.MENU && var3 != Region.MENU_ITEM && var3 != Region.CHECK_BOX_MENU_ITEM && var3 != Region.RADIO_BUTTON_MENU_ITEM) {
               if (var3 == Region.FORMATTED_TEXT_FIELD) {
                  return this.getTextFieldInsets(var1, var2);
               } else {
                  if (var3 == Region.INTERNAL_FRAME) {
                     var2 = Metacity.INSTANCE.getBorderInsets(var1, var2);
                  } else if (var3 == Region.LABEL) {
                     if ("TableHeader.renderer" == var5) {
                        return this.getButtonInsets(var1, var2);
                     }

                     if (var4 instanceof ListCellRenderer) {
                        return this.getTextFieldInsets(var1, var2);
                     }

                     if ("Tree.cellRenderer" == var5) {
                        return this.getSimpleInsets(var1, var2, 1);
                     }
                  } else {
                     if (var3 == Region.OPTION_PANE) {
                        return this.getSimpleInsets(var1, var2, 6);
                     }

                     if (var3 == Region.POPUP_MENU) {
                        return this.getSimpleInsets(var1, var2, 2);
                     }

                     if (var3 != Region.PROGRESS_BAR && var3 != Region.SLIDER && var3 != Region.TABBED_PANE && var3 != Region.TABBED_PANE_CONTENT && var3 != Region.TOOL_BAR && var3 != Region.TOOL_BAR_DRAG_WINDOW && var3 != Region.TOOL_TIP) {
                        if (var3 == Region.SCROLL_BAR) {
                           return this.getScrollBarInsets(var1, var2);
                        }

                        if (var3 == Region.SLIDER_TRACK) {
                           return this.getSliderTrackInsets(var1, var2);
                        }

                        if (var3 == Region.TABBED_PANE_TAB) {
                           return this.getTabbedPaneTabInsets(var1, var2);
                        }

                        if (var3 != Region.TEXT_FIELD && var3 != Region.PASSWORD_FIELD) {
                           if (var3 != Region.SEPARATOR && var3 != Region.POPUP_MENU_SEPARATOR && var3 != Region.TOOL_BAR_SEPARATOR) {
                              if (var3 == GTKEngine.CustomRegion.TITLED_BORDER) {
                                 return this.getThicknessInsets(var1, var2);
                              }

                              return var2;
                           }

                           return this.getSeparatorInsets(var1, var2);
                        }

                        if (var5 == "Tree.cellEditor") {
                           return this.getSimpleInsets(var1, var2, 1);
                        }

                        return this.getTextFieldInsets(var1, var2);
                     }

                     return this.getThicknessInsets(var1, var2);
                  }

                  return var2;
               }
            } else {
               return this.getMenuItemInsets(var1, var2);
            }
         } else {
            return this.getRadioInsets(var1, var2);
         }
      } else {
         return "Spinner.previousButton" != var5 && "Spinner.nextButton" != var5 ? this.getButtonInsets(var1, var2) : this.getSimpleInsets(var1, var2, 1);
      }
   }

   private Insets getButtonInsets(SynthContext var1, Insets var2) {
      byte var3 = 1;
      int var4 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
      int var5 = this.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
      int var6 = this.getXThickness();
      int var7 = this.getYThickness();
      int var8 = var4 + var5 + var6 + var3;
      int var9 = var4 + var5 + var7 + var3;
      var2.left = var2.right = var8;
      var2.top = var2.bottom = var9;
      JComponent var10 = var1.getComponent();
      if (var10 instanceof JButton && !(var10.getParent() instanceof JToolBar) && ((JButton)var10).isDefaultCapable()) {
         Insets var11 = this.getClassSpecificInsetsValue(var1, "default-border", BUTTON_DEFAULT_BORDER_INSETS);
         var2.left += var11.left;
         var2.right += var11.right;
         var2.top += var11.top;
         var2.bottom += var11.bottom;
      }

      return var2;
   }

   private Insets getRadioInsets(SynthContext var1, Insets var2) {
      int var3 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
      int var4 = this.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
      int var5 = var3 + var4;
      var2.top = var5;
      var2.bottom = var5;
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         var2.left = 0;
         var2.right = var5;
      } else {
         var2.left = var5;
         var2.right = 0;
      }

      return var2;
   }

   private Insets getMenuBarInsets(SynthContext var1, Insets var2) {
      int var3 = this.getClassSpecificIntValue((SynthContext)var1, "internal-padding", 1);
      int var4 = this.getXThickness();
      int var5 = this.getYThickness();
      var2.left = var2.right = var4 + var3;
      var2.top = var2.bottom = var5 + var3;
      return var2;
   }

   private Insets getMenuItemInsets(SynthContext var1, Insets var2) {
      int var3 = this.getClassSpecificIntValue((SynthContext)var1, "horizontal-padding", 3);
      int var4 = this.getXThickness();
      int var5 = this.getYThickness();
      var2.left = var2.right = var4 + var3;
      var2.top = var2.bottom = var5;
      return var2;
   }

   private Insets getThicknessInsets(SynthContext var1, Insets var2) {
      var2.left = var2.right = this.getXThickness();
      var2.top = var2.bottom = this.getYThickness();
      return var2;
   }

   private Insets getSeparatorInsets(SynthContext var1, Insets var2) {
      int var3 = 0;
      if (var1.getRegion() == Region.POPUP_MENU_SEPARATOR) {
         var3 = this.getClassSpecificIntValue((SynthContext)var1, "horizontal-padding", 3);
      }

      var2.right = var2.left = this.getXThickness() + var3;
      var2.top = var2.bottom = this.getYThickness();
      return var2;
   }

   private Insets getSliderTrackInsets(SynthContext var1, Insets var2) {
      int var3 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
      int var4 = this.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
      var2.top = var2.bottom = var2.left = var2.right = var3 + var4;
      return var2;
   }

   private Insets getSimpleInsets(SynthContext var1, Insets var2, int var3) {
      var2.top = var2.bottom = var2.right = var2.left = var3;
      return var2;
   }

   private Insets getTabbedPaneTabInsets(SynthContext var1, Insets var2) {
      int var3 = this.getXThickness();
      int var4 = this.getYThickness();
      int var5 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
      byte var6 = 2;
      var2.left = var2.right = var5 + var6 + var3;
      var2.top = var2.bottom = var5 + var6 + var4;
      return var2;
   }

   private Insets getTextFieldInsets(SynthContext var1, Insets var2) {
      var2 = this.getClassSpecificInsetsValue(var1, "inner-border", this.getSimpleInsets(var1, var2, 2));
      int var3 = this.getXThickness();
      int var4 = this.getYThickness();
      boolean var5 = this.getClassSpecificBoolValue(var1, "interior-focus", true);
      int var6 = 0;
      if (!var5) {
         var6 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
      }

      var2.left += var6 + var3;
      var2.right += var6 + var3;
      var2.top += var6 + var4;
      var2.bottom += var6 + var4;
      return var2;
   }

   private Insets getScrollBarInsets(SynthContext var1, Insets var2) {
      int var3 = this.getClassSpecificIntValue((SynthContext)var1, "trough-border", 1);
      var2.left = var2.right = var2.top = var2.bottom = var3;
      JComponent var4 = var1.getComponent();
      int var5;
      if (var4.getParent() instanceof JScrollPane) {
         var5 = getClassSpecificIntValue((GTKEngine.WidgetType)GTKEngine.WidgetType.SCROLL_PANE, "scrollbar-spacing", 3);
         if (((JScrollBar)var4).getOrientation() == 0) {
            var2.top += var5;
         } else if (var4.getComponentOrientation().isLeftToRight()) {
            var2.left += var5;
         } else {
            var2.right += var5;
         }
      } else if (var4.isFocusable()) {
         var5 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
         int var6 = this.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
         int var7 = var5 + var6;
         var2.left += var7;
         var2.right += var7;
         var2.top += var7;
         var2.bottom += var7;
      }

      return var2;
   }

   private static Object getClassSpecificValue(GTKEngine.WidgetType var0, String var1) {
      synchronized(UNIXToolkit.GTK_LOCK) {
         return nativeGetClassValue(var0.ordinal(), var1);
      }
   }

   private static int getClassSpecificIntValue(GTKEngine.WidgetType var0, String var1, int var2) {
      Object var3 = getClassSpecificValue(var0, var1);
      return var3 instanceof Number ? ((Number)var3).intValue() : var2;
   }

   Object getClassSpecificValue(String var1) {
      synchronized(UNIXToolkit.GTK_LOCK) {
         return nativeGetClassValue(this.widgetType, var1);
      }
   }

   int getClassSpecificIntValue(SynthContext var1, String var2, int var3) {
      Object var4 = this.getClassSpecificValue(var2);
      return var4 instanceof Number ? ((Number)var4).intValue() : var3;
   }

   Insets getClassSpecificInsetsValue(SynthContext var1, String var2, Insets var3) {
      Object var4 = this.getClassSpecificValue(var2);
      return var4 instanceof Insets ? (Insets)var4 : var3;
   }

   boolean getClassSpecificBoolValue(SynthContext var1, String var2, boolean var3) {
      Object var4 = this.getClassSpecificValue(var2);
      return var4 instanceof Boolean ? (Boolean)var4 : var3;
   }

   public boolean isOpaque(SynthContext var1) {
      Region var2 = var1.getRegion();
      if (var2 != Region.COMBO_BOX && var2 != Region.DESKTOP_PANE && var2 != Region.DESKTOP_ICON && var2 != Region.EDITOR_PANE && var2 != Region.FORMATTED_TEXT_FIELD && var2 != Region.INTERNAL_FRAME && var2 != Region.LIST && var2 != Region.MENU_BAR && var2 != Region.PANEL && var2 != Region.PASSWORD_FIELD && var2 != Region.POPUP_MENU && var2 != Region.PROGRESS_BAR && var2 != Region.ROOT_PANE && var2 != Region.SCROLL_PANE && var2 != Region.SPINNER && var2 != Region.SPLIT_PANE_DIVIDER && var2 != Region.TABLE && var2 != Region.TEXT_AREA && var2 != Region.TEXT_FIELD && var2 != Region.TEXT_PANE && var2 != Region.TOOL_BAR_DRAG_WINDOW && var2 != Region.TOOL_TIP && var2 != Region.TREE && var2 != Region.VIEWPORT) {
         JComponent var3 = var1.getComponent();
         String var4 = var3.getName();
         return var4 == "ComboBox.renderer" || var4 == "ComboBox.listRenderer";
      } else {
         return true;
      }
   }

   public Object get(SynthContext var1, Object var2) {
      String var3 = (String)CLASS_SPECIFIC_MAP.get(var2);
      Object var4;
      if (var3 != null) {
         var4 = this.getClassSpecificValue(var3);
         if (var4 != null) {
            return var4;
         }
      }

      if (var2 == "ScrollPane.viewportBorderInsets") {
         return this.getThicknessInsets(var1, new Insets(0, 0, 0, 0));
      } else if (var2 == "Slider.tickColor") {
         return this.getColorForState(var1, ColorType.FOREGROUND);
      } else {
         int var13;
         if (var2 == "ScrollBar.minimumThumbSize") {
            var13 = this.getClassSpecificIntValue((SynthContext)var1, "min-slider-length", 21);
            JScrollBar var21 = (JScrollBar)var1.getComponent();
            return var21.getOrientation() == 0 ? new DimensionUIResource(var13, 0) : new DimensionUIResource(0, var13);
         } else if (var2 == "Separator.thickness") {
            JSeparator var18 = (JSeparator)var1.getComponent();
            return var18.getOrientation() == 0 ? this.getYThickness() : this.getXThickness();
         } else if (var2 == "ToolBar.separatorSize") {
            var13 = getClassSpecificIntValue((GTKEngine.WidgetType)GTKEngine.WidgetType.TOOL_BAR, "space-size", 12);
            return new DimensionUIResource(var13, var13);
         } else if (var2 == "ScrollBar.buttonSize") {
            JScrollBar var17 = (JScrollBar)var1.getComponent().getParent();
            boolean var20 = var17.getOrientation() == 0;
            GTKEngine.WidgetType var22 = var20 ? GTKEngine.WidgetType.HSCROLL_BAR : GTKEngine.WidgetType.VSCROLL_BAR;
            int var7 = getClassSpecificIntValue((GTKEngine.WidgetType)var22, "slider-width", 14);
            int var8 = getClassSpecificIntValue((GTKEngine.WidgetType)var22, "stepper-size", 14);
            return var20 ? new DimensionUIResource(var8, var7) : new DimensionUIResource(var7, var8);
         } else {
            int var6;
            if (var2 == "ArrowButton.size") {
               String var14 = var1.getComponent().getName();
               if (var14 != null && var14.startsWith("Spinner")) {
                  String var19;
                  synchronized(UNIXToolkit.GTK_LOCK) {
                     var19 = nativeGetPangoFontName(GTKEngine.WidgetType.SPINNER.ordinal());
                  }

                  var6 = var19 != null ? PangoFonts.getFontSize(var19) : 10;
                  return var6 + this.getXThickness() * 2;
               }
            } else if ("CheckBox.iconTextGap".equals(var2) || "RadioButton.iconTextGap".equals(var2)) {
               var13 = this.getClassSpecificIntValue((SynthContext)var1, "indicator-spacing", 2);
               int var5 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
               var6 = this.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
               return var13 + var5 + var6;
            }

            var4 = null;
            GTKStyle.GTKStockIcon var15;
            synchronized(ICONS_MAP) {
               var15 = (GTKStyle.GTKStockIcon)ICONS_MAP.get(var2);
            }

            if (var15 != null) {
               return var15;
            } else if (var2 != "engine") {
               Object var16 = UIManager.get(var2);
               if (var2 == "Table.rowHeight") {
                  var6 = this.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 0);
                  if (var16 == null && var6 > 0) {
                     var16 = 16 + 2 * var6;
                  }
               }

               return var16;
            } else {
               return null;
            }
         }
      }
   }

   private Icon getStockIcon(SynthContext var1, String var2, int var3) {
      GTKConstants.TextDirection var4 = GTKConstants.TextDirection.LTR;
      if (var1 != null) {
         ComponentOrientation var5 = var1.getComponent().getComponentOrientation();
         if (var5 != null && !var5.isLeftToRight()) {
            var4 = GTKConstants.TextDirection.RTL;
         }
      }

      Icon var8 = this.getStyleSpecificIcon(var2, var4, var3);
      if (var8 != null) {
         return var8;
      } else {
         String var6 = "gtk.icon." + var2 + '.' + var3 + '.' + (var4 == GTKConstants.TextDirection.RTL ? "rtl" : "ltr");
         Image var7 = (Image)Toolkit.getDefaultToolkit().getDesktopProperty(var6);
         return var7 != null ? new ImageIcon(var7) : null;
      }
   }

   private Icon getStyleSpecificIcon(String var1, GTKConstants.TextDirection var2, int var3) {
      UNIXToolkit var4 = (UNIXToolkit)Toolkit.getDefaultToolkit();
      BufferedImage var5 = var4.getStockIcon(this.widgetType, var1, var3, var2.ordinal(), (String)null);
      return var5 != null ? new ImageIcon(var5) : null;
   }

   static {
      BLACK_COLOR = new ColorUIResource(Color.BLACK);
      WHITE_COLOR = new ColorUIResource(Color.WHITE);
      DEFAULT_FONT = new FontUIResource("sansserif", 0, 10);
      BUTTON_DEFAULT_BORDER_INSETS = new Insets(1, 1, 1, 1);
      GTK_GRAPHICS = new GTKGraphicsUtils();
      CLASS_SPECIFIC_MAP = new HashMap();
      CLASS_SPECIFIC_MAP.put("Slider.thumbHeight", "slider-width");
      CLASS_SPECIFIC_MAP.put("Slider.trackBorder", "trough-border");
      CLASS_SPECIFIC_MAP.put("SplitPane.size", "handle-size");
      CLASS_SPECIFIC_MAP.put("Tree.expanderSize", "expander-size");
      CLASS_SPECIFIC_MAP.put("ScrollBar.thumbHeight", "slider-width");
      CLASS_SPECIFIC_MAP.put("ScrollBar.width", "slider-width");
      CLASS_SPECIFIC_MAP.put("TextArea.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("TextArea.caretAspectRatio", "cursor-aspect-ratio");
      CLASS_SPECIFIC_MAP.put("TextField.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("TextField.caretAspectRatio", "cursor-aspect-ratio");
      CLASS_SPECIFIC_MAP.put("PasswordField.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("PasswordField.caretAspectRatio", "cursor-aspect-ratio");
      CLASS_SPECIFIC_MAP.put("FormattedTextField.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("FormattedTextField.caretAspectRatio", "cursor-aspect-");
      CLASS_SPECIFIC_MAP.put("TextPane.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("TextPane.caretAspectRatio", "cursor-aspect-ratio");
      CLASS_SPECIFIC_MAP.put("EditorPane.caretForeground", "cursor-color");
      CLASS_SPECIFIC_MAP.put("EditorPane.caretAspectRatio", "cursor-aspect-ratio");
      ICONS_MAP = new HashMap();
      ICONS_MAP.put("FileChooser.cancelIcon", new GTKStyle.GTKStockIcon("gtk-cancel", 4));
      ICONS_MAP.put("FileChooser.okIcon", new GTKStyle.GTKStockIcon("gtk-ok", 4));
      ICONS_MAP.put("OptionPane.errorIcon", new GTKStyle.GTKStockIcon("gtk-dialog-error", 6));
      ICONS_MAP.put("OptionPane.informationIcon", new GTKStyle.GTKStockIcon("gtk-dialog-info", 6));
      ICONS_MAP.put("OptionPane.warningIcon", new GTKStyle.GTKStockIcon("gtk-dialog-warning", 6));
      ICONS_MAP.put("OptionPane.questionIcon", new GTKStyle.GTKStockIcon("gtk-dialog-question", 6));
      ICONS_MAP.put("OptionPane.yesIcon", new GTKStyle.GTKStockIcon("gtk-yes", 4));
      ICONS_MAP.put("OptionPane.noIcon", new GTKStyle.GTKStockIcon("gtk-no", 4));
      ICONS_MAP.put("OptionPane.cancelIcon", new GTKStyle.GTKStockIcon("gtk-cancel", 4));
      ICONS_MAP.put("OptionPane.okIcon", new GTKStyle.GTKStockIcon("gtk-ok", 4));
   }

   static class GTKLazyValue implements UIDefaults.LazyValue {
      private String className;
      private String methodName;

      GTKLazyValue(String var1) {
         this(var1, (String)null);
      }

      GTKLazyValue(String var1, String var2) {
         this.className = var1;
         this.methodName = var2;
      }

      public Object createValue(UIDefaults var1) {
         try {
            Class var2 = Class.forName(this.className, true, Thread.currentThread().getContextClassLoader());
            if (this.methodName == null) {
               return var2.newInstance();
            }

            Method var3 = var2.getMethod(this.methodName, (Class[])null);
            return var3.invoke(var2, (Object[])null);
         } catch (ClassNotFoundException var4) {
         } catch (IllegalAccessException var5) {
         } catch (InvocationTargetException var6) {
         } catch (NoSuchMethodException var7) {
         } catch (InstantiationException var8) {
         }

         return null;
      }
   }

   private static class GTKStockIcon extends SynthIcon {
      private String key;
      private int size;
      private boolean loadedLTR;
      private boolean loadedRTL;
      private Icon ltrIcon;
      private Icon rtlIcon;
      private SynthStyle style;

      GTKStockIcon(String var1, int var2) {
         this.key = var1;
         this.size = var2;
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Icon var7 = this.getIcon(var1);
         if (var7 != null) {
            if (var1 == null) {
               var7.paintIcon((Component)null, var2, var3, var4);
            } else {
               var7.paintIcon(var1.getComponent(), var2, var3, var4);
            }
         }

      }

      public int getIconWidth(SynthContext var1) {
         Icon var2 = this.getIcon(var1);
         return var2 != null ? var2.getIconWidth() : 0;
      }

      public int getIconHeight(SynthContext var1) {
         Icon var2 = this.getIcon(var1);
         return var2 != null ? var2.getIconHeight() : 0;
      }

      private Icon getIcon(SynthContext var1) {
         if (var1 != null) {
            ComponentOrientation var2 = var1.getComponent().getComponentOrientation();
            SynthStyle var3 = var1.getStyle();
            if (var3 != this.style) {
               this.style = var3;
               this.loadedLTR = this.loadedRTL = false;
            }

            if (var2 != null && !var2.isLeftToRight()) {
               if (!this.loadedRTL) {
                  this.loadedRTL = true;
                  this.rtlIcon = ((GTKStyle)var1.getStyle()).getStockIcon(var1, this.key, this.size);
               }

               return this.rtlIcon;
            } else {
               if (!this.loadedLTR) {
                  this.loadedLTR = true;
                  this.ltrIcon = ((GTKStyle)var1.getStyle()).getStockIcon(var1, this.key, this.size);
               }

               return this.ltrIcon;
            }
         } else {
            return this.ltrIcon;
         }
      }
   }

   static class GTKStockIconInfo {
      private static Map<String, Integer> ICON_TYPE_MAP;
      private static final Object ICON_SIZE_KEY = new StringBuffer("IconSize");

      private static Dimension[] getIconSizesMap() {
         AppContext var0 = AppContext.getAppContext();
         Dimension[] var1 = (Dimension[])((Dimension[])var0.get(ICON_SIZE_KEY));
         if (var1 == null) {
            var1 = new Dimension[]{null, new Dimension(16, 16), new Dimension(18, 18), new Dimension(24, 24), new Dimension(20, 20), new Dimension(32, 32), new Dimension(48, 48)};
            var0.put(ICON_SIZE_KEY, var1);
         }

         return var1;
      }

      public static Dimension getIconSize(int var0) {
         Dimension[] var1 = getIconSizesMap();
         return var0 >= 0 && var0 < var1.length ? var1[var0] : null;
      }

      public static void setIconSize(int var0, int var1, int var2) {
         Dimension[] var3 = getIconSizesMap();
         if (var0 >= 0 && var0 < var3.length) {
            var3[var0] = new Dimension(var1, var2);
         }

      }

      public static int getIconType(String var0) {
         if (var0 == null) {
            return -100;
         } else {
            if (ICON_TYPE_MAP == null) {
               initIconTypeMap();
            }

            Integer var1 = (Integer)ICON_TYPE_MAP.get(var0);
            return var1 != null ? var1 : -100;
         }
      }

      private static void initIconTypeMap() {
         ICON_TYPE_MAP = new HashMap();
         ICON_TYPE_MAP.put("gtk-menu", 1);
         ICON_TYPE_MAP.put("gtk-small-toolbar", 2);
         ICON_TYPE_MAP.put("gtk-large-toolbar", 3);
         ICON_TYPE_MAP.put("gtk-button", 4);
         ICON_TYPE_MAP.put("gtk-dnd", 5);
         ICON_TYPE_MAP.put("gtk-dialog", 6);
      }
   }
}
