package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.Painter;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

class SynthPainterImpl extends SynthPainter {
   private NimbusStyle style;

   SynthPainterImpl(NimbusStyle var1) {
      this.style = var1;
   }

   private void paint(Painter var1, SynthContext var2, Graphics var3, int var4, int var5, int var6, int var7, AffineTransform var8) {
      if (var1 != null) {
         Graphics2D var9;
         if (var3 instanceof Graphics2D) {
            var9 = (Graphics2D)var3;
            if (var8 != null) {
               var9.transform(var8);
            }

            var9.translate(var4, var5);
            var1.paint(var9, var2.getComponent(), var6, var7);
            var9.translate(-var4, -var5);
            if (var8 != null) {
               try {
                  var9.transform(var8.createInverse());
               } catch (NoninvertibleTransformException var11) {
                  var11.printStackTrace();
               }
            }
         } else {
            BufferedImage var12 = new BufferedImage(var6, var7, 2);
            Graphics2D var10 = var12.createGraphics();
            if (var8 != null) {
               var10.transform(var8);
            }

            var1.paint(var10, var2.getComponent(), var6, var7);
            var10.dispose();
            var3.drawImage(var12, var4, var5, (ImageObserver)null);
            var9 = null;
         }
      }

   }

   private void paintBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, AffineTransform var7) {
      JComponent var8 = var1.getComponent();
      Color var9 = var8 != null ? var8.getBackground() : null;
      if (var9 == null || var9.getAlpha() > 0) {
         Painter var10 = this.style.getBackgroundPainter(var1);
         if (var10 != null) {
            this.paint(var10, var1, var2, var3, var4, var5, var6, var7);
         }
      }

   }

   private void paintForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, AffineTransform var7) {
      Painter var8 = this.style.getForegroundPainter(var1);
      if (var8 != null) {
         this.paint(var8, var1, var2, var3, var4, var5, var6, var7);
      }

   }

   private void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, AffineTransform var7) {
      Painter var8 = this.style.getBorderPainter(var1);
      if (var8 != null) {
         this.paint(var8, var1, var2, var3, var4, var5, var6, var7);
      }

   }

   private void paintBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      JComponent var8 = var1.getComponent();
      boolean var9 = var8.getComponentOrientation().isLeftToRight();
      if (var1.getComponent() instanceof JSlider) {
         var9 = true;
      }

      AffineTransform var10;
      if (var7 == 1 && var9) {
         var10 = new AffineTransform();
         var10.scale(-1.0D, 1.0D);
         var10.rotate(Math.toRadians(90.0D));
         this.paintBackground(var1, var2, var4, var3, var6, var5, var10);
      } else if (var7 == 1) {
         var10 = new AffineTransform();
         var10.rotate(Math.toRadians(90.0D));
         var10.translate(0.0D, (double)(-(var3 + var5)));
         this.paintBackground(var1, var2, var4, var3, var6, var5, var10);
      } else if (var7 == 0 && var9) {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         var10 = new AffineTransform();
         var10.translate((double)var3, (double)var4);
         var10.scale(-1.0D, 1.0D);
         var10.translate((double)(-var5), 0.0D);
         this.paintBackground(var1, var2, 0, 0, var5, var6, var10);
      }

   }

   private void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      JComponent var8 = var1.getComponent();
      boolean var9 = var8.getComponentOrientation().isLeftToRight();
      AffineTransform var10;
      if (var7 == 1 && var9) {
         var10 = new AffineTransform();
         var10.scale(-1.0D, 1.0D);
         var10.rotate(Math.toRadians(90.0D));
         this.paintBorder(var1, var2, var4, var3, var6, var5, var10);
      } else if (var7 == 1) {
         var10 = new AffineTransform();
         var10.rotate(Math.toRadians(90.0D));
         var10.translate(0.0D, (double)(-(var3 + var5)));
         this.paintBorder(var1, var2, var4, 0, var6, var5, var10);
      } else if (var7 == 0 && var9) {
         this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      }

   }

   private void paintForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      JComponent var8 = var1.getComponent();
      boolean var9 = var8.getComponentOrientation().isLeftToRight();
      AffineTransform var10;
      if (var7 == 1 && var9) {
         var10 = new AffineTransform();
         var10.scale(-1.0D, 1.0D);
         var10.rotate(Math.toRadians(90.0D));
         this.paintForeground(var1, var2, var4, var3, var6, var5, var10);
      } else if (var7 == 1) {
         var10 = new AffineTransform();
         var10.rotate(Math.toRadians(90.0D));
         var10.translate(0.0D, (double)(-(var3 + var5)));
         this.paintForeground(var1, var2, var4, 0, var6, var5, var10);
      } else if (var7 == 0 && var9) {
         this.paintForeground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         this.paintForeground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      }

   }

   public void paintArrowButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBackground(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintArrowButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintArrowButtonForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      String var8 = var1.getComponent().getName();
      boolean var9 = var1.getComponent().getComponentOrientation().isLeftToRight();
      AffineTransform var10;
      if (!"Spinner.nextButton".equals(var8) && !"Spinner.previousButton".equals(var8)) {
         if (var7 == 7) {
            this.paintForeground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
         } else if (var7 == 1) {
            if (var9) {
               var10 = new AffineTransform();
               var10.scale(-1.0D, 1.0D);
               var10.rotate(Math.toRadians(90.0D));
               this.paintForeground(var1, var2, var4, 0, var6, var5, var10);
            } else {
               var10 = new AffineTransform();
               var10.rotate(Math.toRadians(90.0D));
               var10.translate(0.0D, (double)(-(var3 + var5)));
               this.paintForeground(var1, var2, var4, 0, var6, var5, var10);
            }
         } else if (var7 == 3) {
            var10 = new AffineTransform();
            var10.translate((double)var5, 0.0D);
            var10.scale(-1.0D, 1.0D);
            this.paintForeground(var1, var2, var3, var4, var5, var6, var10);
         } else if (var7 == 5) {
            if (var9) {
               var10 = new AffineTransform();
               var10.rotate(Math.toRadians(-90.0D));
               var10.translate((double)(-var6), 0.0D);
               this.paintForeground(var1, var2, var4, var3, var6, var5, var10);
            } else {
               var10 = new AffineTransform();
               var10.scale(-1.0D, 1.0D);
               var10.rotate(Math.toRadians(-90.0D));
               var10.translate((double)(-(var6 + var4)), (double)(-(var5 + var3)));
               this.paintForeground(var1, var2, var4, var3, var6, var5, var10);
            }
         }
      } else if (var9) {
         this.paintForeground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         var10 = new AffineTransform();
         var10.translate((double)var5, 0.0D);
         var10.scale(-1.0D, 1.0D);
         this.paintForeground(var1, var2, var3, var4, var5, var6, var10);
      }

   }

   public void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintCheckBoxMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintCheckBoxMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintCheckBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintCheckBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintColorChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintColorChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintComboBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBackground(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintComboBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintDesktopIconBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintDesktopIconBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintDesktopPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintDesktopPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintEditorPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintEditorPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintFileChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintFileChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintFormattedTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBackground(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintFormattedTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBorder(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintInternalFrameTitlePaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintInternalFrameTitlePaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintInternalFrameBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintInternalFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintLabelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintLabelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintListBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintListBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintOptionPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintOptionPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPanelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPanelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPasswordFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPasswordFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPopupMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintPopupMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintProgressBarForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintForeground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintRadioButtonMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintRadioButtonMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintRadioButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintRadioButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintRootPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintRootPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollBarThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollBarThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintScrollPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintScrollPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSeparatorForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintForeground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSliderThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1.getComponent().getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE) {
         byte var8;
         if (var7 == 0) {
            var8 = 1;
         } else {
            var8 = 0;
         }

         this.paintBackground(var1, var2, var3, var4, var5, var6, var8);
      } else {
         this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public void paintSliderThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintSpinnerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSpinnerBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      if (var7 == 1) {
         AffineTransform var8 = new AffineTransform();
         var8.scale(-1.0D, 1.0D);
         var8.rotate(Math.toRadians(90.0D));
         this.paintBackground(var1, var2, var4, var3, var6, var5, var8);
      } else {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      }

   }

   public void paintSplitPaneDividerForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintForeground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSplitPaneDragDivider(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSplitPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintSplitPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      AffineTransform var8;
      if (var7 == 2) {
         var8 = new AffineTransform();
         var8.scale(-1.0D, 1.0D);
         var8.rotate(Math.toRadians(90.0D));
         this.paintBackground(var1, var2, var4, var3, var6, var5, var8);
      } else if (var7 == 4) {
         var8 = new AffineTransform();
         var8.rotate(Math.toRadians(90.0D));
         var8.translate(0.0D, (double)(-(var3 + var5)));
         this.paintBackground(var1, var2, var4, 0, var6, var5, var8);
      } else if (var7 == 3) {
         var8 = new AffineTransform();
         var8.translate((double)var3, (double)var4);
         var8.scale(1.0D, -1.0D);
         var8.translate(0.0D, (double)(-var6));
         this.paintBackground(var1, var2, 0, 0, var5, var6, var8);
      } else {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      }

   }

   public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      AffineTransform var9;
      if (var8 == 2) {
         var9 = new AffineTransform();
         var9.scale(-1.0D, 1.0D);
         var9.rotate(Math.toRadians(90.0D));
         this.paintBackground(var1, var2, var4, var3, var6, var5, var9);
      } else if (var8 == 4) {
         var9 = new AffineTransform();
         var9.rotate(Math.toRadians(90.0D));
         var9.translate(0.0D, (double)(-(var3 + var5)));
         this.paintBackground(var1, var2, var4, 0, var6, var5, var9);
      } else if (var8 == 3) {
         var9 = new AffineTransform();
         var9.translate((double)var3, (double)var4);
         var9.scale(1.0D, -1.0D);
         var9.translate(0.0D, (double)(-var6));
         this.paintBackground(var1, var2, 0, 0, var5, var6, var9);
      } else {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      }

   }

   public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTabbedPaneContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTableHeaderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTableHeaderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTableBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTableBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTextAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTextAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTextPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTextPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBackground(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var1.getComponent().getComponentOrientation().isLeftToRight()) {
         this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
      } else {
         AffineTransform var7 = new AffineTransform();
         var7.translate((double)var3, (double)var4);
         var7.scale(-1.0D, 1.0D);
         var7.translate((double)(-var5), 0.0D);
         this.paintBorder(var1, var2, 0, 0, var5, var6, var7);
      }

   }

   public void paintToggleButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToggleButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, var7);
   }

   public void paintToolTipBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintToolTipBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTreeBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTreeBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTreeCellBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTreeCellBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintTreeCellFocus(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public void paintViewportBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBackground(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }

   public void paintViewportBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paintBorder(var1, var2, var3, var4, var5, var6, (AffineTransform)null);
   }
}
