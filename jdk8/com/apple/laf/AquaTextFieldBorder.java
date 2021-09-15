package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.text.JTextComponent;

public class AquaTextFieldBorder extends AquaBorder {
   protected static final AquaUtils.RecyclableSingleton<AquaTextFieldBorder> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaTextFieldBorder.class);

   public static AquaTextFieldBorder getTextFieldBorder() {
      return (AquaTextFieldBorder)instance.get();
   }

   public AquaTextFieldBorder() {
      this(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(6, 7, 6, 7).alterInsets(3, 3, 3, 3)));
      this.painter.state.set(JRSUIConstants.Widget.FRAME_TEXT_FIELD);
      this.painter.state.set(JRSUIConstants.FrameOnly.YES);
      this.painter.state.set(JRSUIConstants.Size.LARGE);
   }

   public AquaTextFieldBorder(AquaUtilControlSize.SizeDescriptor var1) {
      super(var1);
   }

   public AquaTextFieldBorder(AquaTextFieldBorder var1) {
      super((AquaBorder)var1);
   }

   protected void setSize(JRSUIConstants.Size var1) {
      super.setSize(var1);
      this.painter.state.set(JRSUIConstants.Size.LARGE);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (!(var1 instanceof JTextComponent)) {
         this.painter.state.set(JRSUIConstants.State.ACTIVE);
         this.painter.state.set(JRSUIConstants.Focused.NO);
         this.painter.paint(var2, var1, var3, var4, var5, var6);
      } else {
         JTextComponent var7 = (JTextComponent)var1;
         JRSUIConstants.State var8 = getStateFor(var7);
         this.painter.state.set(var8);
         this.painter.state.set(JRSUIConstants.State.ACTIVE == var8 && var7.hasFocus() ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
         if (var7.isOpaque()) {
            this.painter.paint(var2, var1, var3, var4, var5, var6);
         } else {
            int var9 = getShrinkageFor(var7, var6);
            Insets var10 = this.getSubInsets(var9);
            var3 += var10.left;
            var4 += var10.top;
            var5 -= var10.left + var10.right;
            var6 -= var10.top + var10.bottom;
            if (var9 > 0) {
               Rectangle var11 = var2.getClipBounds();
               var11.x += var9;
               var11.width -= var9 * 2;
               var2.setClip(var11);
            }

            this.painter.paint(var2, var1, var3, var4, var5, var6);
         }
      }
   }

   static int getShrinkageFor(JTextComponent var0, int var1) {
      if (var0 == null) {
         return 0;
      } else {
         TextUI var2 = var0.getUI();
         if (var2 == null) {
            return 0;
         } else {
            Dimension var3 = var2.getPreferredSize(var0);
            if (var3 == null) {
               return 0;
            } else {
               int var4 = var3.height - var1;
               return var4 < 0 ? 0 : (var4 > 3 ? 3 : var4);
            }
         }
      }
   }

   protected Insets getSubInsets(int var1) {
      Insets var2 = this.sizeVariant.insets;
      return (Insets)(var1 > 0 ? new InsetsUIResource(var2.top - var1, var2.left, var2.bottom - var1, var2.right) : var2);
   }

   public Insets getBorderInsets(Component var1) {
      return var1 instanceof JTextComponent && !var1.isOpaque() ? new InsetsUIResource(5, 5, 5, 5) : new InsetsUIResource(3, 7, 3, 7);
   }

   protected static JRSUIConstants.State getStateFor(JTextComponent var0) {
      if (!AquaFocusHandler.isActive(var0)) {
         return JRSUIConstants.State.INACTIVE;
      } else if (!var0.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else {
         return !var0.isEditable() ? JRSUIConstants.State.DISABLED : JRSUIConstants.State.ACTIVE;
      }
   }
}
