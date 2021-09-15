package com.sun.java.swing.plaf.gtk;

import java.awt.Graphics;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import sun.swing.plaf.synth.SynthIcon;

class GTKIconFactory {
   static final int CHECK_ICON_EXTRA_INSET = 1;
   static final int DEFAULT_ICON_SPACING = 2;
   static final int DEFAULT_ICON_SIZE = 13;
   static final int DEFAULT_TOGGLE_MENU_ITEM_SIZE = 12;
   private static final String RADIO_BUTTON_ICON = "paintRadioButtonIcon";
   private static final String CHECK_BOX_ICON = "paintCheckBoxIcon";
   private static final String MENU_ARROW_ICON = "paintMenuArrowIcon";
   private static final String CHECK_BOX_MENU_ITEM_CHECK_ICON = "paintCheckBoxMenuItemCheckIcon";
   private static final String RADIO_BUTTON_MENU_ITEM_CHECK_ICON = "paintRadioButtonMenuItemCheckIcon";
   private static final String TREE_EXPANDED_ICON = "paintTreeExpandedIcon";
   private static final String TREE_COLLAPSED_ICON = "paintTreeCollapsedIcon";
   private static final String ASCENDING_SORT_ICON = "paintAscendingSortIcon";
   private static final String DESCENDING_SORT_ICON = "paintDescendingSortIcon";
   private static final String TOOL_BAR_HANDLE_ICON = "paintToolBarHandleIcon";
   private static Map<String, GTKIconFactory.DelegatingIcon> iconsPool = Collections.synchronizedMap(new HashMap());

   private static GTKIconFactory.DelegatingIcon getIcon(String var0) {
      Object var1 = (GTKIconFactory.DelegatingIcon)iconsPool.get(var0);
      if (var1 == null) {
         if (var0 != "paintTreeCollapsedIcon" && var0 != "paintTreeExpandedIcon") {
            if (var0 == "paintToolBarHandleIcon") {
               var1 = new GTKIconFactory.ToolBarHandleIcon();
            } else if (var0 == "paintMenuArrowIcon") {
               var1 = new GTKIconFactory.MenuArrowIcon();
            } else {
               var1 = new GTKIconFactory.DelegatingIcon(var0);
            }
         } else {
            var1 = new GTKIconFactory.SynthExpanderIcon(var0);
         }

         iconsPool.put(var0, var1);
      }

      return (GTKIconFactory.DelegatingIcon)var1;
   }

   public static Icon getAscendingSortIcon() {
      return getIcon("paintAscendingSortIcon");
   }

   public static Icon getDescendingSortIcon() {
      return getIcon("paintDescendingSortIcon");
   }

   public static SynthIcon getTreeExpandedIcon() {
      return getIcon("paintTreeExpandedIcon");
   }

   public static SynthIcon getTreeCollapsedIcon() {
      return getIcon("paintTreeCollapsedIcon");
   }

   public static SynthIcon getRadioButtonIcon() {
      return getIcon("paintRadioButtonIcon");
   }

   public static SynthIcon getCheckBoxIcon() {
      return getIcon("paintCheckBoxIcon");
   }

   public static SynthIcon getMenuArrowIcon() {
      return getIcon("paintMenuArrowIcon");
   }

   public static SynthIcon getCheckBoxMenuItemCheckIcon() {
      return getIcon("paintCheckBoxMenuItemCheckIcon");
   }

   public static SynthIcon getRadioButtonMenuItemCheckIcon() {
      return getIcon("paintRadioButtonMenuItemCheckIcon");
   }

   public static SynthIcon getToolBarHandleIcon() {
      return getIcon("paintToolBarHandleIcon");
   }

   static void resetIcons() {
      synchronized(iconsPool) {
         Iterator var1 = iconsPool.values().iterator();

         while(var1.hasNext()) {
            GTKIconFactory.DelegatingIcon var2 = (GTKIconFactory.DelegatingIcon)var1.next();
            var2.resetIconDimensions();
         }

      }
   }

   private static class MenuArrowIcon extends GTKIconFactory.DelegatingIcon {
      private static final Class[] PARAM_TYPES;

      public MenuArrowIcon() {
         super("paintMenuArrowIcon");
      }

      protected Class[] getMethodParamTypes() {
         return PARAM_TYPES;
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 != null) {
            GTKConstants.ArrowType var7 = GTKConstants.ArrowType.RIGHT;
            if (!var1.getComponent().getComponentOrientation().isLeftToRight()) {
               var7 = GTKConstants.ArrowType.LEFT;
            }

            GTKPainter.INSTANCE.paintIcon(var1, var2, this.getMethod(), var3, var4, var5, var6, var7);
         }

      }

      static {
         PARAM_TYPES = new Class[]{SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, GTKConstants.ArrowType.class};
      }
   }

   private static class ToolBarHandleIcon extends GTKIconFactory.DelegatingIcon {
      private static final Class[] PARAM_TYPES;
      private SynthStyle style;

      public ToolBarHandleIcon() {
         super("paintToolBarHandleIcon");
      }

      protected Class[] getMethodParamTypes() {
         return PARAM_TYPES;
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 != null) {
            JToolBar var7 = (JToolBar)var1.getComponent();
            GTKConstants.Orientation var8 = var7.getOrientation() == 0 ? GTKConstants.Orientation.HORIZONTAL : GTKConstants.Orientation.VERTICAL;
            if (this.style == null) {
               this.style = SynthLookAndFeel.getStyleFactory().getStyle(var1.getComponent(), GTKRegion.HANDLE_BOX);
            }

            var1 = new SynthContext(var7, GTKRegion.HANDLE_BOX, this.style, 1);
            GTKPainter.INSTANCE.paintIcon(var1, var2, this.getMethod(), var3, var4, var5, var6, var8);
         }

      }

      public int getIconWidth(SynthContext var1) {
         if (var1 == null) {
            return 10;
         } else {
            return ((JToolBar)var1.getComponent()).getOrientation() == 0 ? 10 : var1.getComponent().getWidth();
         }
      }

      public int getIconHeight(SynthContext var1) {
         if (var1 == null) {
            return 10;
         } else {
            return ((JToolBar)var1.getComponent()).getOrientation() == 0 ? var1.getComponent().getHeight() : 10;
         }
      }

      static {
         PARAM_TYPES = new Class[]{SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, GTKConstants.Orientation.class};
      }
   }

   private static class SynthExpanderIcon extends GTKIconFactory.DelegatingIcon {
      SynthExpanderIcon(String var1) {
         super(var1);
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 != null) {
            super.paintIcon(var1, var2, var3, var4, var5, var6);
            this.updateSizeIfNecessary(var1);
         }

      }

      int getIconDimension(SynthContext var1) {
         this.updateSizeIfNecessary(var1);
         return this.iconDimension == -1 ? 13 : this.iconDimension;
      }

      private void updateSizeIfNecessary(SynthContext var1) {
         if (this.iconDimension == -1 && var1 != null) {
            this.iconDimension = var1.getStyle().getInt(var1, "Tree.expanderSize", 10);
         }

      }
   }

   private static class DelegatingIcon extends SynthIcon implements UIResource {
      private static final Class[] PARAM_TYPES;
      private Object method;
      int iconDimension = -1;

      DelegatingIcon(String var1) {
         this.method = var1;
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 != null) {
            GTKPainter.INSTANCE.paintIcon(var1, var2, this.getMethod(), var3, var4, var5, var6);
         }

      }

      public int getIconWidth(SynthContext var1) {
         return this.getIconDimension(var1);
      }

      public int getIconHeight(SynthContext var1) {
         return this.getIconDimension(var1);
      }

      void resetIconDimensions() {
         this.iconDimension = -1;
      }

      protected Method getMethod() {
         if (this.method instanceof String) {
            this.method = this.resolveMethod((String)this.method);
         }

         return (Method)this.method;
      }

      protected Class[] getMethodParamTypes() {
         return PARAM_TYPES;
      }

      private Method resolveMethod(String var1) {
         try {
            return GTKPainter.class.getMethod(var1, this.getMethodParamTypes());
         } catch (NoSuchMethodException var3) {
            assert false;

            return null;
         }
      }

      int getIconDimension(SynthContext var1) {
         if (this.iconDimension >= 0) {
            return this.iconDimension;
         } else if (var1 == null) {
            return 13;
         } else {
            Region var2 = var1.getRegion();
            GTKStyle var3 = (GTKStyle)var1.getStyle();
            this.iconDimension = var3.getClassSpecificIntValue(var1, "indicator-size", var2 != Region.CHECK_BOX_MENU_ITEM && var2 != Region.RADIO_BUTTON_MENU_ITEM ? 13 : 12);
            if (var2 != Region.CHECK_BOX && var2 != Region.RADIO_BUTTON) {
               if (var2 == Region.CHECK_BOX_MENU_ITEM || var2 == Region.RADIO_BUTTON_MENU_ITEM) {
                  this.iconDimension += 2;
               }
            } else {
               this.iconDimension += 2 * var3.getClassSpecificIntValue((SynthContext)var1, "indicator-spacing", 2);
            }

            return this.iconDimension;
         }
      }

      static {
         PARAM_TYPES = new Class[]{SynthContext.class, Graphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE};
      }
   }
}
