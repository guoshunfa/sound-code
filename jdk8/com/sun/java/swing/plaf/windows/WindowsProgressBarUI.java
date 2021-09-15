package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class WindowsProgressBarUI extends BasicProgressBarUI {
   private Rectangle previousFullBox;
   private Insets indeterminateInsets;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsProgressBarUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      if (XPStyle.getXP() != null) {
         LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.FALSE);
         this.progressBar.setBorder((Border)null);
         this.indeterminateInsets = UIManager.getInsets("ProgressBar.indeterminateInsets");
      }

   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      int var4 = super.getBaseline(var1, var2, var3);
      if (XPStyle.getXP() != null && this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
         FontMetrics var5 = this.progressBar.getFontMetrics(this.progressBar.getFont());
         int var6 = this.progressBar.getInsets().top;
         byte var7;
         if (this.progressBar.isIndeterminate()) {
            var7 = -1;
            --var3;
         } else {
            var7 = 0;
            var3 -= 3;
         }

         var4 = var7 + (var3 + var5.getAscent() - var5.getLeading() - var5.getDescent()) / 2;
      }

      return var4;
   }

   protected Dimension getPreferredInnerHorizontal() {
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null) {
         XPStyle.Skin var2 = var1.getSkin(this.progressBar, TMSchema.Part.PP_BAR);
         return new Dimension((int)super.getPreferredInnerHorizontal().getWidth(), var2.getHeight());
      } else {
         return super.getPreferredInnerHorizontal();
      }
   }

   protected Dimension getPreferredInnerVertical() {
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null) {
         XPStyle.Skin var2 = var1.getSkin(this.progressBar, TMSchema.Part.PP_BARVERT);
         return new Dimension(var2.getWidth(), (int)super.getPreferredInnerVertical().getHeight());
      } else {
         return super.getPreferredInnerVertical();
      }
   }

   protected void paintDeterminate(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         boolean var4 = this.progressBar.getOrientation() == 1;
         boolean var5 = WindowsGraphicsUtils.isLeftToRight(var2);
         int var6 = this.progressBar.getWidth();
         int var7 = this.progressBar.getHeight() - 1;
         int var8 = this.getAmountFull((Insets)null, var6, var7);
         this.paintXPBackground(var1, var4, var6, var7);
         if (this.progressBar.isStringPainted()) {
            var1.setColor(this.progressBar.getForeground());
            var7 -= 2;
            var6 -= 2;
            if (var6 <= 0 || var7 <= 0) {
               return;
            }

            Graphics2D var15 = (Graphics2D)var1;
            var15.setStroke(new BasicStroke((float)(var4 ? var6 : var7), 0, 2));
            if (!var4) {
               if (var5) {
                  var15.drawLine(2, var7 / 2 + 1, var8 - 2, var7 / 2 + 1);
               } else {
                  var15.drawLine(2 + var6, var7 / 2 + 1, 2 + var6 - (var8 - 2), var7 / 2 + 1);
               }

               this.paintString(var1, 0, 0, var6, var7, var8, (Insets)null);
            } else {
               var15.drawLine(var6 / 2 + 1, var7 + 1, var6 / 2 + 1, var7 + 1 - var8 + 2);
               this.paintString(var1, 2, 2, var6, var7, var8, (Insets)null);
            }
         } else {
            XPStyle.Skin var9 = var3.getSkin(this.progressBar, var4 ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK);
            int var10;
            if (var4) {
               var10 = var6 - 5;
            } else {
               var10 = var7 - 5;
            }

            int var11 = var3.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, (TMSchema.State)null, TMSchema.Prop.PROGRESSCHUNKSIZE, 2);
            int var12 = var3.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, (TMSchema.State)null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
            int var13 = (var8 - 4) / (var11 + var12);
            if (var12 > 0 && var13 * (var11 + var12) + var11 < var8 - 4) {
               ++var13;
            }

            for(int var14 = 0; var14 < var13; ++var14) {
               if (var4) {
                  var9.paintSkin(var1, 3, var7 - var14 * (var11 + var12) - var11 - 2, var10, var11, (TMSchema.State)null);
               } else if (var5) {
                  var9.paintSkin(var1, 4 + var14 * (var11 + var12), 2, var11, var10, (TMSchema.State)null);
               } else {
                  var9.paintSkin(var1, var6 - (2 + (var14 + 1) * (var11 + var12)), 2, var11, var10, (TMSchema.State)null);
               }
            }
         }
      } else {
         super.paintDeterminate(var1, var2);
      }

   }

   protected void setAnimationIndex(int var1) {
      super.setAnimationIndex(var1);
      XPStyle var2 = XPStyle.getXP();
      if (var2 != null) {
         if (this.boxRect != null) {
            Rectangle var3 = this.getFullChunkBounds(this.boxRect);
            if (this.previousFullBox != null) {
               var3.add(this.previousFullBox);
            }

            this.progressBar.repaint(var3);
         } else {
            this.progressBar.repaint();
         }
      }

   }

   protected int getBoxLength(int var1, int var2) {
      XPStyle var3 = XPStyle.getXP();
      return var3 != null ? 6 : super.getBoxLength(var1, var2);
   }

   protected Rectangle getBox(Rectangle var1) {
      Rectangle var2 = super.getBox(var1);
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         boolean var4 = this.progressBar.getOrientation() == 1;
         TMSchema.Part var5 = var4 ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
         Insets var6 = this.indeterminateInsets;
         int var7 = this.getAnimationIndex();
         int var8 = this.getFrameCount() / 2;
         int var9 = var3.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, (TMSchema.State)null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
         var7 %= var8;
         int var10;
         double var11;
         if (!var4) {
            var2.y += var6.top;
            var2.height = this.progressBar.getHeight() - var6.top - var6.bottom;
            var10 = this.progressBar.getWidth() - var6.left - var6.right;
            var10 += (var2.width + var9) * 2;
            var11 = (double)var10 / (double)var8;
            var2.x = (int)(var11 * (double)var7) + var6.left;
         } else {
            var2.x += var6.left;
            var2.width = this.progressBar.getWidth() - var6.left - var6.right;
            var10 = this.progressBar.getHeight() - var6.top - var6.bottom;
            var10 += (var2.height + var9) * 2;
            var11 = (double)var10 / (double)var8;
            var2.y = (int)(var11 * (double)var7) + var6.top;
         }
      }

      return var2;
   }

   protected void paintIndeterminate(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         boolean var4 = this.progressBar.getOrientation() == 1;
         int var5 = this.progressBar.getWidth();
         int var6 = this.progressBar.getHeight();
         this.paintXPBackground(var1, var4, var5, var6);
         this.boxRect = this.getBox(this.boxRect);
         if (this.boxRect != null) {
            var1.setColor(this.progressBar.getForeground());
            if (!(var1 instanceof Graphics2D)) {
               return;
            }

            this.paintIndeterminateFrame(this.boxRect, (Graphics2D)var1, var4, var5, var6);
            if (this.progressBar.isStringPainted()) {
               if (!var4) {
                  this.paintString(var1, -1, -1, var5, var6, 0, (Insets)null);
               } else {
                  this.paintString(var1, 1, 1, var5, var6, 0, (Insets)null);
               }
            }
         }
      } else {
         super.paintIndeterminate(var1, var2);
      }

   }

   private Rectangle getFullChunkBounds(Rectangle var1) {
      boolean var2 = this.progressBar.getOrientation() == 1;
      XPStyle var3 = XPStyle.getXP();
      int var4 = var3 != null ? var3.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, (TMSchema.State)null, TMSchema.Prop.PROGRESSSPACESIZE, 0) : 0;
      int var5;
      if (!var2) {
         var5 = var1.width + var4;
         return new Rectangle(var1.x - var5 * 2, var1.y, var5 * 3, var1.height);
      } else {
         var5 = var1.height + var4;
         return new Rectangle(var1.x, var1.y - var5 * 2, var1.width, var5 * 3);
      }
   }

   private void paintIndeterminateFrame(Rectangle var1, Graphics2D var2, boolean var3, int var4, int var5) {
      XPStyle var6 = XPStyle.getXP();
      if (var6 != null) {
         Graphics2D var7 = (Graphics2D)var2.create();
         TMSchema.Part var8 = var3 ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
         TMSchema.Part var9 = var3 ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK;
         int var10 = var6.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, (TMSchema.State)null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
         boolean var11 = false;
         boolean var12 = false;
         int var18;
         int var19;
         if (!var3) {
            var18 = -var1.width - var10;
            var19 = 0;
         } else {
            var18 = 0;
            var19 = -var1.height - var10;
         }

         Rectangle var13 = this.getFullChunkBounds(var1);
         this.previousFullBox = var13;
         Insets var14 = this.indeterminateInsets;
         Rectangle var15 = new Rectangle(var14.left, var14.top, var4 - var14.left - var14.right, var5 - var14.top - var14.bottom);
         Rectangle var16 = var15.intersection(var13);
         var7.clip(var16);
         XPStyle.Skin var17 = var6.getSkin(this.progressBar, var9);
         var7.setComposite(AlphaComposite.getInstance(3, 0.8F));
         var17.paintSkin(var7, var1.x, var1.y, var1.width, var1.height, (TMSchema.State)null);
         var1.translate(var18, var19);
         var7.setComposite(AlphaComposite.getInstance(3, 0.5F));
         var17.paintSkin(var7, var1.x, var1.y, var1.width, var1.height, (TMSchema.State)null);
         var1.translate(var18, var19);
         var7.setComposite(AlphaComposite.getInstance(3, 0.2F));
         var17.paintSkin(var7, var1.x, var1.y, var1.width, var1.height, (TMSchema.State)null);
         var7.dispose();
      }
   }

   private void paintXPBackground(Graphics var1, boolean var2, int var3, int var4) {
      XPStyle var5 = XPStyle.getXP();
      if (var5 != null) {
         TMSchema.Part var6 = var2 ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
         XPStyle.Skin var7 = var5.getSkin(this.progressBar, var6);
         var7.paintSkin(var1, 0, 0, var3, var4, (TMSchema.State)null);
      }
   }
}
