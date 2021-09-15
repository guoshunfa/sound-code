package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import sun.awt.UNIXToolkit;
import sun.awt.image.SunWritableRaster;
import sun.swing.ImageCache;

class GTKEngine {
   static final GTKEngine INSTANCE = new GTKEngine();
   private static final int CACHE_SIZE = 50;
   private static HashMap<Region, Object> regionToWidgetTypeMap;
   private ImageCache cache = new ImageCache(50);
   private int x0;
   private int y0;
   private int w0;
   private int h0;
   private Graphics graphics;
   private Object[] cacheArgs;
   private static final ColorModel[] COLOR_MODELS;
   private static final int[][] BAND_OFFSETS;

   private native void native_paint_arrow(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9);

   private native void native_paint_box(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9, int var10);

   private native void native_paint_box_gap(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11);

   private native void native_paint_check(int var1, int var2, String var3, int var4, int var5, int var6, int var7);

   private native void native_paint_expander(int var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8);

   private native void native_paint_extension(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9);

   private native void native_paint_flat_box(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, boolean var9);

   private native void native_paint_focus(int var1, int var2, String var3, int var4, int var5, int var6, int var7);

   private native void native_paint_handle(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9);

   private native void native_paint_hline(int var1, int var2, String var3, int var4, int var5, int var6, int var7);

   private native void native_paint_option(int var1, int var2, String var3, int var4, int var5, int var6, int var7);

   private native void native_paint_shadow(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9, int var10);

   private native void native_paint_slider(int var1, int var2, int var3, String var4, int var5, int var6, int var7, int var8, int var9);

   private native void native_paint_vline(int var1, int var2, String var3, int var4, int var5, int var6, int var7);

   private native void native_paint_background(int var1, int var2, int var3, int var4, int var5, int var6);

   private native Object native_get_gtk_setting(int var1);

   private native void nativeSetRangeValue(int var1, double var2, double var4, double var6, double var8);

   private native void nativeStartPainting(int var1, int var2);

   private native int nativeFinishPainting(int[] var1, int var2, int var3);

   private native void native_switch_theme();

   static GTKEngine.WidgetType getWidgetType(JComponent var0, Region var1) {
      Object var2 = regionToWidgetTypeMap.get(var1);
      if (var2 instanceof GTKEngine.WidgetType) {
         return (GTKEngine.WidgetType)var2;
      } else {
         GTKEngine.WidgetType[] var3 = (GTKEngine.WidgetType[])((GTKEngine.WidgetType[])var2);
         if (var0 == null) {
            return var3[0];
         } else if (var0 instanceof JScrollBar) {
            return ((JScrollBar)var0).getOrientation() == 0 ? var3[0] : var3[1];
         } else if (var0 instanceof JSeparator) {
            JSeparator var7 = (JSeparator)var0;
            if (var7.getParent() instanceof JPopupMenu) {
               return GTKEngine.WidgetType.POPUP_MENU_SEPARATOR;
            } else if (var7.getParent() instanceof JToolBar) {
               return GTKEngine.WidgetType.TOOL_BAR_SEPARATOR;
            } else {
               return var7.getOrientation() == 0 ? var3[0] : var3[1];
            }
         } else if (var0 instanceof JSlider) {
            return ((JSlider)var0).getOrientation() == 0 ? var3[0] : var3[1];
         } else if (var0 instanceof JProgressBar) {
            return ((JProgressBar)var0).getOrientation() == 0 ? var3[0] : var3[1];
         } else if (var0 instanceof JSplitPane) {
            return ((JSplitPane)var0).getOrientation() == 1 ? var3[1] : var3[0];
         } else if (var1 == Region.LABEL) {
            return var0 instanceof ListCellRenderer ? var3[1] : var3[0];
         } else {
            String var6;
            if (var1 == Region.TEXT_FIELD) {
               var6 = var0.getName();
               return var6 != null && var6.startsWith("ComboBox") ? var3[1] : var3[0];
            } else if (var1 == Region.FORMATTED_TEXT_FIELD) {
               var6 = var0.getName();
               return var6 != null && var6.startsWith("Spinner") ? var3[1] : var3[0];
            } else if (var1 == Region.ARROW_BUTTON) {
               if (var0.getParent() instanceof JScrollBar) {
                  Integer var4 = (Integer)var0.getClientProperty("__arrow_direction__");
                  int var5 = var4 != null ? var4 : 7;
                  switch(var5) {
                  case 1:
                     return GTKEngine.WidgetType.VSCROLL_BAR_BUTTON_UP;
                  case 2:
                  case 4:
                  case 6:
                  default:
                     return null;
                  case 3:
                     return GTKEngine.WidgetType.HSCROLL_BAR_BUTTON_RIGHT;
                  case 5:
                     return GTKEngine.WidgetType.VSCROLL_BAR_BUTTON_DOWN;
                  case 7:
                     return GTKEngine.WidgetType.HSCROLL_BAR_BUTTON_LEFT;
                  }
               } else {
                  return var0.getParent() instanceof JComboBox ? GTKEngine.WidgetType.COMBO_BOX_ARROW_BUTTON : GTKEngine.WidgetType.SPINNER_ARROW_BUTTON;
               }
            } else {
               return null;
            }
         }
      }
   }

   private static int getTextDirection(SynthContext var0) {
      GTKConstants.TextDirection var1 = GTKConstants.TextDirection.NONE;
      JComponent var2 = var0.getComponent();
      if (var2 != null) {
         ComponentOrientation var3 = var2.getComponentOrientation();
         if (var3 != null) {
            var1 = var3.isLeftToRight() ? GTKConstants.TextDirection.LTR : GTKConstants.TextDirection.RTL;
         }
      }

      return var1.ordinal();
   }

   public void paintArrow(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, GTKConstants.ArrowType var6, String var7, int var8, int var9, int var10, int var11) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_arrow(var12, var4, var5.ordinal(), var7, var8 - this.x0, var9 - this.y0, var10, var11, var6.ordinal());
   }

   public void paintBox(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10) {
      int var11 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = var2.getComponentState();
      int var13 = getTextDirection(var2);
      int var14 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_box(var14, var11, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var12, var13);
   }

   public void paintBoxGap(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10, GTKConstants.PositionType var11, int var12, int var13) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var14 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_box_gap(var14, var4, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var11.ordinal(), var12, var13);
   }

   public void paintCheck(Graphics var1, SynthContext var2, Region var3, String var4, int var5, int var6, int var7, int var8) {
      int var9 = var2.getComponentState();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_check(var10, var9, var4, var5 - this.x0, var6 - this.y0, var7, var8);
   }

   public void paintExpander(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ExpanderStyle var5, String var6, int var7, int var8, int var9, int var10) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var11 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_expander(var11, var4, var6, var7 - this.x0, var8 - this.y0, var9, var10, var5.ordinal());
   }

   public void paintExtension(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10, GTKConstants.PositionType var11, int var12) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var13 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_extension(var13, var4, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var11.ordinal());
   }

   public void paintFlatBox(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10, ColorType var11) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_flat_box(var12, var4, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var2.getComponent().hasFocus());
   }

   public void paintFocus(Graphics var1, SynthContext var2, Region var3, int var4, String var5, int var6, int var7, int var8, int var9) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_focus(var10, var4, var5, var6 - this.x0, var7 - this.y0, var8, var9);
   }

   public void paintHandle(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10, GTKConstants.Orientation var11) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_handle(var12, var4, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var11.ordinal());
   }

   public void paintHline(Graphics var1, SynthContext var2, Region var3, int var4, String var5, int var6, int var7, int var8, int var9) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_hline(var10, var4, var5, var6 - this.x0, var7 - this.y0, var8, var9);
   }

   public void paintOption(Graphics var1, SynthContext var2, Region var3, String var4, int var5, int var6, int var7, int var8) {
      int var9 = var2.getComponentState();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_option(var10, var9, var4, var5 - this.x0, var6 - this.y0, var7, var8);
   }

   public void paintShadow(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10) {
      int var11 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = var2.getComponentState();
      int var13 = getTextDirection(var2);
      int var14 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_shadow(var14, var11, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var12, var13);
   }

   public void paintSlider(Graphics var1, SynthContext var2, Region var3, int var4, GTKConstants.ShadowType var5, String var6, int var7, int var8, int var9, int var10, GTKConstants.Orientation var11) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var12 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_slider(var12, var4, var5.ordinal(), var6, var7 - this.x0, var8 - this.y0, var9, var10, var11.ordinal());
   }

   public void paintVline(Graphics var1, SynthContext var2, Region var3, int var4, String var5, int var6, int var7, int var8, int var9) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_vline(var10, var4, var5, var6 - this.x0, var7 - this.y0, var8, var9);
   }

   public void paintBackground(Graphics var1, SynthContext var2, Region var3, int var4, Color var5, int var6, int var7, int var8, int var9) {
      var4 = GTKLookAndFeel.synthStateToGTKStateType(var4).ordinal();
      int var10 = getWidgetType(var2.getComponent(), var3).ordinal();
      this.native_paint_background(var10, var4, var6 - this.x0, var7 - this.y0, var8, var9);
   }

   public boolean paintCachedImage(Graphics var1, int var2, int var3, int var4, int var5, Object... var6) {
      if (var4 > 0 && var5 > 0) {
         Image var7 = this.cache.getImage(this.getClass(), (GraphicsConfiguration)null, var4, var5, var6);
         if (var7 != null) {
            var1.drawImage(var7, var2, var3, (ImageObserver)null);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void startPainting(Graphics var1, int var2, int var3, int var4, int var5, Object... var6) {
      this.nativeStartPainting(var4, var5);
      this.x0 = var2;
      this.y0 = var3;
      this.w0 = var4;
      this.h0 = var5;
      this.graphics = var1;
      this.cacheArgs = var6;
   }

   public void finishPainting() {
      this.finishPainting(true);
   }

   public void finishPainting(boolean var1) {
      DataBufferInt var2 = new DataBufferInt(this.w0 * this.h0);
      int var3 = this.nativeFinishPainting(SunWritableRaster.stealData((DataBufferInt)var2, 0), this.w0, this.h0);
      SunWritableRaster.markDirty((DataBuffer)var2);
      int[] var4 = BAND_OFFSETS[var3 - 1];
      WritableRaster var5 = Raster.createPackedRaster(var2, this.w0, this.h0, this.w0, var4, (Point)null);
      ColorModel var6 = COLOR_MODELS[var3 - 1];
      BufferedImage var7 = new BufferedImage(var6, var5, false, (Hashtable)null);
      if (var1) {
         this.cache.setImage(this.getClass(), (GraphicsConfiguration)null, this.w0, this.h0, this.cacheArgs, var7);
      }

      this.graphics.drawImage(var7, this.x0, this.y0, (ImageObserver)null);
   }

   public void themeChanged() {
      synchronized(UNIXToolkit.GTK_LOCK) {
         this.native_switch_theme();
      }

      this.cache.flush();
   }

   public Object getSetting(GTKEngine.Settings var1) {
      synchronized(UNIXToolkit.GTK_LOCK) {
         return this.native_get_gtk_setting(var1.ordinal());
      }
   }

   void setRangeValue(SynthContext var1, Region var2, double var3, double var5, double var7, double var9) {
      int var11 = getWidgetType(var1.getComponent(), var2).ordinal();
      this.nativeSetRangeValue(var11, var3, var5, var7, var9);
   }

   static {
      Toolkit.getDefaultToolkit();
      regionToWidgetTypeMap = new HashMap(50);
      regionToWidgetTypeMap.put(Region.ARROW_BUTTON, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.SPINNER_ARROW_BUTTON, GTKEngine.WidgetType.COMBO_BOX_ARROW_BUTTON, GTKEngine.WidgetType.HSCROLL_BAR_BUTTON_LEFT, GTKEngine.WidgetType.HSCROLL_BAR_BUTTON_RIGHT, GTKEngine.WidgetType.VSCROLL_BAR_BUTTON_UP, GTKEngine.WidgetType.VSCROLL_BAR_BUTTON_DOWN});
      regionToWidgetTypeMap.put(Region.BUTTON, GTKEngine.WidgetType.BUTTON);
      regionToWidgetTypeMap.put(Region.CHECK_BOX, GTKEngine.WidgetType.CHECK_BOX);
      regionToWidgetTypeMap.put(Region.CHECK_BOX_MENU_ITEM, GTKEngine.WidgetType.CHECK_BOX_MENU_ITEM);
      regionToWidgetTypeMap.put(Region.COLOR_CHOOSER, GTKEngine.WidgetType.COLOR_CHOOSER);
      regionToWidgetTypeMap.put(Region.FILE_CHOOSER, GTKEngine.WidgetType.OPTION_PANE);
      regionToWidgetTypeMap.put(Region.COMBO_BOX, GTKEngine.WidgetType.COMBO_BOX);
      regionToWidgetTypeMap.put(Region.DESKTOP_ICON, GTKEngine.WidgetType.DESKTOP_ICON);
      regionToWidgetTypeMap.put(Region.DESKTOP_PANE, GTKEngine.WidgetType.DESKTOP_PANE);
      regionToWidgetTypeMap.put(Region.EDITOR_PANE, GTKEngine.WidgetType.EDITOR_PANE);
      regionToWidgetTypeMap.put(Region.FORMATTED_TEXT_FIELD, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.FORMATTED_TEXT_FIELD, GTKEngine.WidgetType.SPINNER_TEXT_FIELD});
      regionToWidgetTypeMap.put(GTKRegion.HANDLE_BOX, GTKEngine.WidgetType.HANDLE_BOX);
      regionToWidgetTypeMap.put(Region.INTERNAL_FRAME, GTKEngine.WidgetType.INTERNAL_FRAME);
      regionToWidgetTypeMap.put(Region.INTERNAL_FRAME_TITLE_PANE, GTKEngine.WidgetType.INTERNAL_FRAME_TITLE_PANE);
      regionToWidgetTypeMap.put(Region.LABEL, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.LABEL, GTKEngine.WidgetType.COMBO_BOX_TEXT_FIELD});
      regionToWidgetTypeMap.put(Region.LIST, GTKEngine.WidgetType.LIST);
      regionToWidgetTypeMap.put(Region.MENU, GTKEngine.WidgetType.MENU);
      regionToWidgetTypeMap.put(Region.MENU_BAR, GTKEngine.WidgetType.MENU_BAR);
      regionToWidgetTypeMap.put(Region.MENU_ITEM, GTKEngine.WidgetType.MENU_ITEM);
      regionToWidgetTypeMap.put(Region.MENU_ITEM_ACCELERATOR, GTKEngine.WidgetType.MENU_ITEM_ACCELERATOR);
      regionToWidgetTypeMap.put(Region.OPTION_PANE, GTKEngine.WidgetType.OPTION_PANE);
      regionToWidgetTypeMap.put(Region.PANEL, GTKEngine.WidgetType.PANEL);
      regionToWidgetTypeMap.put(Region.PASSWORD_FIELD, GTKEngine.WidgetType.PASSWORD_FIELD);
      regionToWidgetTypeMap.put(Region.POPUP_MENU, GTKEngine.WidgetType.POPUP_MENU);
      regionToWidgetTypeMap.put(Region.POPUP_MENU_SEPARATOR, GTKEngine.WidgetType.POPUP_MENU_SEPARATOR);
      regionToWidgetTypeMap.put(Region.PROGRESS_BAR, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HPROGRESS_BAR, GTKEngine.WidgetType.VPROGRESS_BAR});
      regionToWidgetTypeMap.put(Region.RADIO_BUTTON, GTKEngine.WidgetType.RADIO_BUTTON);
      regionToWidgetTypeMap.put(Region.RADIO_BUTTON_MENU_ITEM, GTKEngine.WidgetType.RADIO_BUTTON_MENU_ITEM);
      regionToWidgetTypeMap.put(Region.ROOT_PANE, GTKEngine.WidgetType.ROOT_PANE);
      regionToWidgetTypeMap.put(Region.SCROLL_BAR, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSCROLL_BAR, GTKEngine.WidgetType.VSCROLL_BAR});
      regionToWidgetTypeMap.put(Region.SCROLL_BAR_THUMB, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSCROLL_BAR_THUMB, GTKEngine.WidgetType.VSCROLL_BAR_THUMB});
      regionToWidgetTypeMap.put(Region.SCROLL_BAR_TRACK, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSCROLL_BAR_TRACK, GTKEngine.WidgetType.VSCROLL_BAR_TRACK});
      regionToWidgetTypeMap.put(Region.SCROLL_PANE, GTKEngine.WidgetType.SCROLL_PANE);
      regionToWidgetTypeMap.put(Region.SEPARATOR, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSEPARATOR, GTKEngine.WidgetType.VSEPARATOR});
      regionToWidgetTypeMap.put(Region.SLIDER, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSLIDER, GTKEngine.WidgetType.VSLIDER});
      regionToWidgetTypeMap.put(Region.SLIDER_THUMB, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSLIDER_THUMB, GTKEngine.WidgetType.VSLIDER_THUMB});
      regionToWidgetTypeMap.put(Region.SLIDER_TRACK, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSLIDER_TRACK, GTKEngine.WidgetType.VSLIDER_TRACK});
      regionToWidgetTypeMap.put(Region.SPINNER, GTKEngine.WidgetType.SPINNER);
      regionToWidgetTypeMap.put(Region.SPLIT_PANE, GTKEngine.WidgetType.SPLIT_PANE);
      regionToWidgetTypeMap.put(Region.SPLIT_PANE_DIVIDER, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.HSPLIT_PANE_DIVIDER, GTKEngine.WidgetType.VSPLIT_PANE_DIVIDER});
      regionToWidgetTypeMap.put(Region.TABBED_PANE, GTKEngine.WidgetType.TABBED_PANE);
      regionToWidgetTypeMap.put(Region.TABBED_PANE_CONTENT, GTKEngine.WidgetType.TABBED_PANE_CONTENT);
      regionToWidgetTypeMap.put(Region.TABBED_PANE_TAB, GTKEngine.WidgetType.TABBED_PANE_TAB);
      regionToWidgetTypeMap.put(Region.TABBED_PANE_TAB_AREA, GTKEngine.WidgetType.TABBED_PANE_TAB_AREA);
      regionToWidgetTypeMap.put(Region.TABLE, GTKEngine.WidgetType.TABLE);
      regionToWidgetTypeMap.put(Region.TABLE_HEADER, GTKEngine.WidgetType.TABLE_HEADER);
      regionToWidgetTypeMap.put(Region.TEXT_AREA, GTKEngine.WidgetType.TEXT_AREA);
      regionToWidgetTypeMap.put(Region.TEXT_FIELD, new GTKEngine.WidgetType[]{GTKEngine.WidgetType.TEXT_FIELD, GTKEngine.WidgetType.COMBO_BOX_TEXT_FIELD});
      regionToWidgetTypeMap.put(Region.TEXT_PANE, GTKEngine.WidgetType.TEXT_PANE);
      regionToWidgetTypeMap.put(GTKEngine.CustomRegion.TITLED_BORDER, GTKEngine.WidgetType.TITLED_BORDER);
      regionToWidgetTypeMap.put(Region.TOGGLE_BUTTON, GTKEngine.WidgetType.TOGGLE_BUTTON);
      regionToWidgetTypeMap.put(Region.TOOL_BAR, GTKEngine.WidgetType.TOOL_BAR);
      regionToWidgetTypeMap.put(Region.TOOL_BAR_CONTENT, GTKEngine.WidgetType.TOOL_BAR);
      regionToWidgetTypeMap.put(Region.TOOL_BAR_DRAG_WINDOW, GTKEngine.WidgetType.TOOL_BAR_DRAG_WINDOW);
      regionToWidgetTypeMap.put(Region.TOOL_BAR_SEPARATOR, GTKEngine.WidgetType.TOOL_BAR_SEPARATOR);
      regionToWidgetTypeMap.put(Region.TOOL_TIP, GTKEngine.WidgetType.TOOL_TIP);
      regionToWidgetTypeMap.put(Region.TREE, GTKEngine.WidgetType.TREE);
      regionToWidgetTypeMap.put(Region.TREE_CELL, GTKEngine.WidgetType.TREE_CELL);
      regionToWidgetTypeMap.put(Region.VIEWPORT, GTKEngine.WidgetType.VIEWPORT);
      COLOR_MODELS = new ColorModel[]{new DirectColorModel(24, 16711680, 65280, 255, 0), new DirectColorModel(25, 16711680, 65280, 255, 16777216), ColorModel.getRGBdefault()};
      BAND_OFFSETS = new int[][]{{16711680, 65280, 255}, {16711680, 65280, 255, 16777216}, {16711680, 65280, 255, -16777216}};
   }

   static class CustomRegion extends Region {
      static Region TITLED_BORDER = new GTKEngine.CustomRegion("TitledBorder");

      private CustomRegion(String var1) {
         super(var1, (String)null, false);
      }
   }

   static enum Settings {
      GTK_FONT_NAME,
      GTK_ICON_SIZES;
   }

   static enum WidgetType {
      BUTTON,
      CHECK_BOX,
      CHECK_BOX_MENU_ITEM,
      COLOR_CHOOSER,
      COMBO_BOX,
      COMBO_BOX_ARROW_BUTTON,
      COMBO_BOX_TEXT_FIELD,
      DESKTOP_ICON,
      DESKTOP_PANE,
      EDITOR_PANE,
      FORMATTED_TEXT_FIELD,
      HANDLE_BOX,
      HPROGRESS_BAR,
      HSCROLL_BAR,
      HSCROLL_BAR_BUTTON_LEFT,
      HSCROLL_BAR_BUTTON_RIGHT,
      HSCROLL_BAR_TRACK,
      HSCROLL_BAR_THUMB,
      HSEPARATOR,
      HSLIDER,
      HSLIDER_TRACK,
      HSLIDER_THUMB,
      HSPLIT_PANE_DIVIDER,
      INTERNAL_FRAME,
      INTERNAL_FRAME_TITLE_PANE,
      IMAGE,
      LABEL,
      LIST,
      MENU,
      MENU_BAR,
      MENU_ITEM,
      MENU_ITEM_ACCELERATOR,
      OPTION_PANE,
      PANEL,
      PASSWORD_FIELD,
      POPUP_MENU,
      POPUP_MENU_SEPARATOR,
      RADIO_BUTTON,
      RADIO_BUTTON_MENU_ITEM,
      ROOT_PANE,
      SCROLL_PANE,
      SPINNER,
      SPINNER_ARROW_BUTTON,
      SPINNER_TEXT_FIELD,
      SPLIT_PANE,
      TABBED_PANE,
      TABBED_PANE_TAB_AREA,
      TABBED_PANE_CONTENT,
      TABBED_PANE_TAB,
      TABLE,
      TABLE_HEADER,
      TEXT_AREA,
      TEXT_FIELD,
      TEXT_PANE,
      TITLED_BORDER,
      TOGGLE_BUTTON,
      TOOL_BAR,
      TOOL_BAR_DRAG_WINDOW,
      TOOL_BAR_SEPARATOR,
      TOOL_TIP,
      TREE,
      TREE_CELL,
      VIEWPORT,
      VPROGRESS_BAR,
      VSCROLL_BAR,
      VSCROLL_BAR_BUTTON_UP,
      VSCROLL_BAR_BUTTON_DOWN,
      VSCROLL_BAR_TRACK,
      VSCROLL_BAR_THUMB,
      VSEPARATOR,
      VSLIDER,
      VSLIDER_TRACK,
      VSLIDER_THUMB,
      VSPLIT_PANE_DIVIDER;
   }
}
