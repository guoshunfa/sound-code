package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.lang.ref.WeakReference;
import java.net.URL;
import javax.swing.ImageIcon;
import sun.awt.AppContext;
import sun.swing.plaf.synth.Paint9Painter;

class ImagePainter extends SynthPainter {
   private static final StringBuffer CACHE_KEY = new StringBuffer("SynthCacheKey");
   private Image image;
   private Insets sInsets;
   private Insets dInsets;
   private URL path;
   private boolean tiles;
   private boolean paintCenter;
   private Paint9Painter imageCache;
   private boolean center;

   private static Paint9Painter getPaint9Painter() {
      synchronized(CACHE_KEY) {
         WeakReference var1 = (WeakReference)AppContext.getAppContext().get(CACHE_KEY);
         Paint9Painter var2;
         if (var1 == null || (var2 = (Paint9Painter)var1.get()) == null) {
            var2 = new Paint9Painter(30);
            var1 = new WeakReference(var2);
            AppContext.getAppContext().put(CACHE_KEY, var1);
         }

         return var2;
      }
   }

   ImagePainter(boolean var1, boolean var2, Insets var3, Insets var4, URL var5, boolean var6) {
      if (var3 != null) {
         this.sInsets = (Insets)var3.clone();
      }

      if (var4 == null) {
         this.dInsets = this.sInsets;
      } else {
         this.dInsets = (Insets)var4.clone();
      }

      this.tiles = var1;
      this.paintCenter = var2;
      this.imageCache = getPaint9Painter();
      this.path = var5;
      this.center = var6;
   }

   public boolean getTiles() {
      return this.tiles;
   }

   public boolean getPaintsCenter() {
      return this.paintCenter;
   }

   public boolean getCenter() {
      return this.center;
   }

   public Insets getInsets(Insets var1) {
      if (var1 == null) {
         return (Insets)this.dInsets.clone();
      } else {
         var1.left = this.dInsets.left;
         var1.right = this.dInsets.right;
         var1.top = this.dInsets.top;
         var1.bottom = this.dInsets.bottom;
         return var1;
      }
   }

   public Image getImage() {
      if (this.image == null) {
         this.image = (new ImageIcon(this.path, (String)null)).getImage();
      }

      return this.image;
   }

   private void paint(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Image var7 = this.getImage();
      if (Paint9Painter.validImage(var7)) {
         Paint9Painter.PaintType var8;
         if (this.getCenter()) {
            var8 = Paint9Painter.PaintType.CENTER;
         } else if (!this.getTiles()) {
            var8 = Paint9Painter.PaintType.PAINT9_STRETCH;
         } else {
            var8 = Paint9Painter.PaintType.PAINT9_TILE;
         }

         int var9 = 512;
         if (!this.getCenter() && !this.getPaintsCenter()) {
            var9 |= 16;
         }

         this.imageCache.paint(var1.getComponent(), var2, var3, var4, var5, var6, var7, this.sInsets, this.dInsets, var8, var9);
      }

   }

   public void paintArrowButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintArrowButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintArrowButtonForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintCheckBoxMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintCheckBoxMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintCheckBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintCheckBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintColorChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintColorChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintComboBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintComboBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintDesktopIconBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintDesktopIconBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintDesktopPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintDesktopPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintEditorPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintEditorPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintFileChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintFileChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintFormattedTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintFormattedTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintInternalFrameTitlePaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintInternalFrameTitlePaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintInternalFrameBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintInternalFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintLabelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintLabelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintListBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintListBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintOptionPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintOptionPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPanelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPanelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPasswordFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPasswordFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPopupMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintPopupMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintProgressBarForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRadioButtonMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRadioButtonMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRadioButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRadioButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRootPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintRootPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintScrollPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSeparatorForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSpinnerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSpinnerBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneDividerForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneDragDivider(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintSplitPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTabbedPaneContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTableHeaderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTableHeaderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTableBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTableBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToggleButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToggleButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolTipBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintToolTipBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTreeBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTreeBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTreeCellBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTreeCellBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintTreeCellFocus(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintViewportBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }

   public void paintViewportBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.paint(var1, var2, var3, var4, var5, var6);
   }
}
