package com.sun.java.swing.plaf.gtk;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;

class GTKStyleFactory extends SynthStyleFactory {
   private final Map<Object, GTKStyle> stylesCache = new HashMap();
   private Font defaultFont;

   public synchronized SynthStyle getStyle(JComponent var1, Region var2) {
      GTKEngine.WidgetType var3 = GTKEngine.getWidgetType(var1, var2);
      Object var4 = null;
      boolean var6;
      boolean var7;
      if (var2 == Region.SCROLL_BAR) {
         if (var1 != null) {
            JScrollBar var5 = (JScrollBar)var1;
            var6 = var5.getParent() instanceof JScrollPane;
            var7 = var5.getOrientation() == 0;
            boolean var8 = var5.getComponentOrientation().isLeftToRight();
            boolean var9 = var5.isFocusable();
            var4 = new GTKStyleFactory.ComplexKey(var3, new Object[]{var6, var7, var8, var9});
         }
      } else if (var2 != Region.CHECK_BOX && var2 != Region.RADIO_BUTTON) {
         if (var2 == Region.BUTTON) {
            if (var1 != null) {
               JButton var11 = (JButton)var1;
               var6 = var11.getParent() instanceof JToolBar;
               var7 = var11.isDefaultCapable();
               var4 = new GTKStyleFactory.ComplexKey(var3, new Object[]{var6, var7});
            }
         } else if (var2 == Region.MENU && var1 instanceof JMenu && ((JMenu)var1).isTopLevelMenu() && UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")) {
            var3 = GTKEngine.WidgetType.MENU_BAR;
         }
      } else if (var1 != null) {
         boolean var10 = var1.getComponentOrientation().isLeftToRight();
         var4 = new GTKStyleFactory.ComplexKey(var3, new Object[]{var10});
      }

      if (var4 == null) {
         var4 = var3;
      }

      GTKStyle var12 = (GTKStyle)this.stylesCache.get(var4);
      if (var12 == null) {
         var12 = new GTKStyle(this.defaultFont, var3);
         this.stylesCache.put(var4, var12);
      }

      return var12;
   }

   void initStyles(Font var1) {
      this.defaultFont = var1;
      this.stylesCache.clear();
   }

   private static class ComplexKey {
      private final GTKEngine.WidgetType wt;
      private final Object[] args;

      ComplexKey(GTKEngine.WidgetType var1, Object... var2) {
         this.wt = var1;
         this.args = var2;
      }

      public int hashCode() {
         int var1 = this.wt.hashCode();
         if (this.args != null) {
            Object[] var2 = this.args;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Object var5 = var2[var4];
               var1 = var1 * 29 + (var5 == null ? 0 : var5.hashCode());
            }
         }

         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof GTKStyleFactory.ComplexKey)) {
            return false;
         } else {
            GTKStyleFactory.ComplexKey var2 = (GTKStyleFactory.ComplexKey)var1;
            if (this.wt == var2.wt) {
               if (this.args == null && var2.args == null) {
                  return true;
               }

               if (this.args != null && var2.args != null && this.args.length == var2.args.length) {
                  int var3 = 0;

                  while(true) {
                     if (var3 >= this.args.length) {
                        return true;
                     }

                     Object var4 = this.args[var3];
                     Object var5 = var2.args[var3];
                     if (var4 == null) {
                        if (var5 != null) {
                           break;
                        }
                     } else if (!var4.equals(var5)) {
                        break;
                     }

                     ++var3;
                  }

                  return false;
               }
            }

            return false;
         }
      }

      public String toString() {
         String var1 = "ComplexKey[wt=" + this.wt;
         if (this.args != null) {
            var1 = var1 + ",args=[";

            for(int var2 = 0; var2 < this.args.length; ++var2) {
               var1 = var1 + this.args[var2];
               if (var2 < this.args.length - 1) {
                  var1 = var1 + ",";
               }
            }

            var1 = var1 + "]";
         }

         var1 = var1 + "]";
         return var1;
      }
   }
}
