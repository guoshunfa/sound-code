package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;

class GTKGraphicsUtils extends SynthGraphicsUtils {
   public void paintText(SynthContext var1, Graphics var2, String var3, int var4, int var5, int var6) {
      if (var3 != null && var3.length() > 0) {
         if (var1.getRegion() != Region.INTERNAL_FRAME_TITLE_PANE) {
            int var7 = var1.getComponentState();
            if ((var7 & 8) == 8) {
               Color var8 = var2.getColor();
               var2.setColor(var1.getStyle().getColor(var1, GTKColorType.WHITE));
               ++var4;
               ++var5;
               super.paintText(var1, var2, var3, var4, var5, var6);
               var2.setColor(var8);
               --var4;
               --var5;
               super.paintText(var1, var2, var3, var4, var5, var6);
            } else {
               String var9 = GTKLookAndFeel.getGtkThemeName();
               if (var9 != null && var9.startsWith("blueprint") && shouldShadowText(var1.getRegion(), var7)) {
                  var2.setColor(Color.BLACK);
                  super.paintText(var1, var2, var3, var4 + 1, var5 + 1, var6);
                  var2.setColor(Color.WHITE);
               }

               super.paintText(var1, var2, var3, var4, var5, var6);
            }

         }
      }
   }

   public void paintText(SynthContext var1, Graphics var2, String var3, Rectangle var4, int var5) {
      if (var3 != null && var3.length() > 0) {
         Region var6 = var1.getRegion();
         if ((var6 == Region.RADIO_BUTTON || var6 == Region.CHECK_BOX || var6 == Region.TABBED_PANE_TAB) && (var1.getComponentState() & 256) != 0) {
            JComponent var7 = var1.getComponent();
            if (!(var7 instanceof AbstractButton) || ((AbstractButton)var7).isFocusPainted()) {
               int var8 = var1.getComponentState();
               GTKStyle var9 = (GTKStyle)var1.getStyle();
               int var10 = var9.getClassSpecificIntValue((SynthContext)var1, "focus-line-width", 1);
               int var11 = var9.getClassSpecificIntValue((SynthContext)var1, "focus-padding", 1);
               int var12 = var10 + var11;
               int var13 = var4.x - var12;
               int var14 = var4.y - var12;
               int var15 = var4.width + 2 * var12;
               int var16 = var4.height + 2 * var12;
               Color var17 = var2.getColor();
               GTKPainter.INSTANCE.paintFocus(var1, var2, var6, var8, "checkbutton", var13, var14, var15, var16);
               var2.setColor(var17);
            }
         }

         super.paintText(var1, var2, var3, var4, var5);
      }
   }

   private static boolean shouldShadowText(Region var0, int var1) {
      int var2 = GTKLookAndFeel.synthStateToGTKState(var0, var1);
      return var2 == 2 && (var0 == Region.MENU || var0 == Region.MENU_ITEM || var0 == Region.CHECK_BOX_MENU_ITEM || var0 == Region.RADIO_BUTTON_MENU_ITEM);
   }
}
