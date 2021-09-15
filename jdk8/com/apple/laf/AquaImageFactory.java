package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIUtils;
import com.apple.eawt.Application;
import com.apple.eio.FileManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;
import sun.awt.image.MultiResolutionCachedImage;
import sun.lwawt.macosx.LWCToolkit;

public class AquaImageFactory {
   private static final int kAlertIconSize = 64;
   protected static final AquaImageFactory.NamedImageSingleton northArrow = new AquaImageFactory.NamedImageSingleton("NSMenuScrollUp");
   protected static final AquaImageFactory.IconUIResourceSingleton northArrowIcon;
   protected static final AquaImageFactory.NamedImageSingleton southArrow;
   protected static final AquaImageFactory.IconUIResourceSingleton southArrowIcon;
   protected static final AquaImageFactory.NamedImageSingleton westArrow;
   protected static final AquaImageFactory.IconUIResourceSingleton westArrowIcon;
   protected static final AquaImageFactory.NamedImageSingleton eastArrow;
   protected static final AquaImageFactory.IconUIResourceSingleton eastArrowIcon;

   public static IconUIResource getConfirmImageIcon() {
      return new IconUIResource(new AquaIcon.CachingScalingIcon(64, 64) {
         Image createImage() {
            return AquaImageFactory.getGenericJavaIcon();
         }
      });
   }

   public static IconUIResource getCautionImageIcon() {
      return getAppIconCompositedOn(AquaIcon.SystemIcon.getCautionIcon());
   }

   public static IconUIResource getStopImageIcon() {
      return getAppIconCompositedOn(AquaIcon.SystemIcon.getStopIcon());
   }

   public static IconUIResource getLockImageIcon() {
      Image var0;
      if (JRSUIUtils.Images.shouldUseLegacySecurityUIPath()) {
         var0 = AquaUtils.getCImageCreator().createImageFromFile("/System/Library/CoreServices/SecurityAgent.app/Contents/Resources/Security.icns", 64.0D, 64.0D);
         return getAppIconCompositedOn(var0);
      } else {
         var0 = Toolkit.getDefaultToolkit().getImage("NSImage://NSSecurity");
         return getAppIconCompositedOn(var0);
      }
   }

   static Image getGenericJavaIcon() {
      return (Image)AccessController.doPrivileged(new PrivilegedAction<Image>() {
         public Image run() {
            return Application.getApplication().getDockIconImage();
         }
      });
   }

   static String getPathToThisApplication() {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return FileManager.getPathToApplicationBundle();
         }
      });
   }

   static IconUIResource getAppIconCompositedOn(AquaIcon.SystemIcon var0) {
      var0.setSize(64, 64);
      return getAppIconCompositedOn(var0.createImage());
   }

   static IconUIResource getAppIconCompositedOn(Image var0) {
      if (var0 instanceof MultiResolutionCachedImage) {
         int var3 = var0.getWidth((ImageObserver)null);
         MultiResolutionCachedImage var2 = ((MultiResolutionCachedImage)var0).map((var1x) -> {
            return getAppIconImageCompositedOn(var1x, var1x.getWidth((ImageObserver)null) / var3);
         });
         return new IconUIResource(new ImageIcon(var2));
      } else {
         BufferedImage var1 = getAppIconImageCompositedOn(var0, 1);
         return new IconUIResource(new ImageIcon(var1));
      }
   }

   static BufferedImage getAppIconImageCompositedOn(Image var0, int var1) {
      int var2 = 64 * var1;
      int var3 = (int)((double)var2 * 0.5D);
      int var4 = var2 - var3;
      AquaIcon.CachingScalingIcon var5 = new AquaIcon.CachingScalingIcon(var3, var3) {
         Image createImage() {
            return AquaImageFactory.getGenericJavaIcon();
         }
      };
      BufferedImage var6 = new BufferedImage(var2, var2, 3);
      Graphics var7 = var6.getGraphics();
      var7.drawImage(var0, 0, 0, var2, var2, (ImageObserver)null);
      if (var7 instanceof Graphics2D) {
         ((Graphics2D)var7).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      }

      var5.paintIcon((Component)null, var7, var4, var4);
      var7.dispose();
      return var6;
   }

   public static IconUIResource getTreeFolderIcon() {
      return AquaIcon.SystemIcon.getFolderIconUIResource();
   }

   public static IconUIResource getTreeOpenFolderIcon() {
      return AquaIcon.SystemIcon.getOpenFolderIconUIResource();
   }

   public static IconUIResource getTreeDocumentIcon() {
      return AquaIcon.SystemIcon.getDocumentIconUIResource();
   }

   public static UIResource getTreeExpandedIcon() {
      return AquaIcon.getIconFor(new AquaIcon.JRSUIControlSpec() {
         public void initIconPainter(AquaPainter<? extends JRSUIState> var1) {
            var1.state.set(JRSUIConstants.Widget.DISCLOSURE_TRIANGLE);
            var1.state.set(JRSUIConstants.State.ACTIVE);
            var1.state.set(JRSUIConstants.Direction.DOWN);
            var1.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
            var1.state.set(JRSUIConstants.AlignmentVertical.CENTER);
         }
      }, 20, 20);
   }

   public static UIResource getTreeCollapsedIcon() {
      return AquaIcon.getIconFor(new AquaIcon.JRSUIControlSpec() {
         public void initIconPainter(AquaPainter<? extends JRSUIState> var1) {
            var1.state.set(JRSUIConstants.Widget.DISCLOSURE_TRIANGLE);
            var1.state.set(JRSUIConstants.State.ACTIVE);
            var1.state.set(JRSUIConstants.Direction.RIGHT);
            var1.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
            var1.state.set(JRSUIConstants.AlignmentVertical.CENTER);
         }
      }, 20, 20);
   }

   public static UIResource getTreeRightToLeftCollapsedIcon() {
      return AquaIcon.getIconFor(new AquaIcon.JRSUIControlSpec() {
         public void initIconPainter(AquaPainter<? extends JRSUIState> var1) {
            var1.state.set(JRSUIConstants.Widget.DISCLOSURE_TRIANGLE);
            var1.state.set(JRSUIConstants.State.ACTIVE);
            var1.state.set(JRSUIConstants.Direction.LEFT);
            var1.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
            var1.state.set(JRSUIConstants.AlignmentVertical.CENTER);
         }
      }, 20, 20);
   }

   static Image getArrowImageForDirection(int var0) {
      switch(var0) {
      case 1:
         return (Image)northArrow.get();
      case 2:
      case 4:
      case 6:
      default:
         return null;
      case 3:
         return (Image)eastArrow.get();
      case 5:
         return (Image)southArrow.get();
      case 7:
         return (Image)westArrow.get();
      }
   }

   static Icon getArrowIconForDirection(int var0) {
      switch(var0) {
      case 1:
         return (Icon)northArrowIcon.get();
      case 2:
      case 4:
      case 6:
      default:
         return null;
      case 3:
         return (Icon)eastArrowIcon.get();
      case 5:
         return (Icon)southArrowIcon.get();
      case 7:
         return (Icon)westArrowIcon.get();
      }
   }

   public static Icon getMenuArrowIcon() {
      return new AquaImageFactory.InvertableImageIcon(AquaUtils.generateLightenedImage((Image)eastArrow.get(), 25));
   }

   public static Icon getMenuItemCheckIcon() {
      return new AquaImageFactory.InvertableImageIcon(AquaUtils.generateLightenedImage(getNSIcon("NSMenuItemSelection"), 25));
   }

   public static Icon getMenuItemDashIcon() {
      return new AquaImageFactory.InvertableImageIcon(AquaUtils.generateLightenedImage(getNSIcon("NSMenuMixedState"), 25));
   }

   private static Image getNSIcon(String var0) {
      Image var1 = Toolkit.getDefaultToolkit().getImage("NSImage://" + var0);
      return var1;
   }

   public static Color getWindowBackgroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(SystemColor.window);
   }

   public static Color getTextSelectionBackgroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(SystemColor.textHighlight);
   }

   public static Color getTextSelectionForegroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(SystemColor.textHighlightText);
   }

   public static Color getSelectionBackgroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(SystemColor.controlHighlight);
   }

   public static Color getSelectionForegroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(SystemColor.controlLtHighlight);
   }

   public static Color getFocusRingColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(LWCToolkit.getAppleColor(0));
   }

   public static Color getSelectionInactiveBackgroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(LWCToolkit.getAppleColor(1));
   }

   public static Color getSelectionInactiveForegroundColorUIResource() {
      return new AquaImageFactory.SystemColorProxy(LWCToolkit.getAppleColor(2));
   }

   static {
      northArrowIcon = new AquaImageFactory.IconUIResourceSingleton(northArrow);
      southArrow = new AquaImageFactory.NamedImageSingleton("NSMenuScrollDown");
      southArrowIcon = new AquaImageFactory.IconUIResourceSingleton(southArrow);
      westArrow = new AquaImageFactory.NamedImageSingleton("NSMenuSubmenuLeft");
      westArrowIcon = new AquaImageFactory.IconUIResourceSingleton(westArrow);
      eastArrow = new AquaImageFactory.NamedImageSingleton("NSMenuSubmenu");
      eastArrowIcon = new AquaImageFactory.IconUIResourceSingleton(eastArrow);
   }

   private static class SystemColorProxy extends Color implements UIResource {
      final Color color;

      public SystemColorProxy(Color var1) {
         super(var1.getRGB());
         this.color = var1;
      }

      public int getRGB() {
         return this.color.getRGB();
      }
   }

   public abstract static class RecyclableSlicedImageControl extends AquaUtils.RecyclableObject<AquaImageFactory.SlicedImageControl> {
      final AquaImageFactory.NineSliceMetrics metrics;

      public RecyclableSlicedImageControl(AquaImageFactory.NineSliceMetrics var1) {
         this.metrics = var1;
      }

      protected AquaImageFactory.SlicedImageControl create() {
         return new AquaImageFactory.SlicedImageControl(this.createTemplateImage(this.metrics.minW, this.metrics.minH), this.metrics);
      }

      protected abstract Image createTemplateImage(int var1, int var2);
   }

   public static class SlicedImageControl {
      final BufferedImage NW;
      final BufferedImage N;
      final BufferedImage NE;
      final BufferedImage W;
      final BufferedImage C;
      final BufferedImage E;
      final BufferedImage SW;
      final BufferedImage S;
      final BufferedImage SE;
      final AquaImageFactory.NineSliceMetrics metrics;
      final int totalWidth;
      final int totalHeight;
      final int centerColWidth;
      final int centerRowHeight;

      public SlicedImageControl(Image var1, int var2, int var3, int var4, int var5) {
         this(var1, var2, var3, var4, var5, true);
      }

      public SlicedImageControl(Image var1, int var2, int var3, int var4, int var5, boolean var6) {
         this(var1, var2, var3, var4, var5, var6, true, true);
      }

      public SlicedImageControl(Image var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7, boolean var8) {
         this(var1, new AquaImageFactory.NineSliceMetrics(var1.getWidth((ImageObserver)null), var1.getHeight((ImageObserver)null), var2, var3, var4, var5, var6, var7, var8));
      }

      public SlicedImageControl(Image var1, AquaImageFactory.NineSliceMetrics var2) {
         this.metrics = var2;
         if (var1.getWidth((ImageObserver)null) == var2.minW && var1.getHeight((ImageObserver)null) == var2.minH) {
            this.totalWidth = var2.minW;
            this.totalHeight = var2.minH;
            this.centerColWidth = this.totalWidth - var2.wCut - var2.eCut;
            this.centerRowHeight = this.totalHeight - var2.nCut - var2.sCut;
            this.NW = createSlice(var1, 0, 0, var2.wCut, var2.nCut);
            this.N = createSlice(var1, var2.wCut, 0, this.centerColWidth, var2.nCut);
            this.NE = createSlice(var1, this.totalWidth - var2.eCut, 0, var2.eCut, var2.nCut);
            this.W = createSlice(var1, 0, var2.nCut, var2.wCut, this.centerRowHeight);
            this.C = var2.showMiddle ? createSlice(var1, var2.wCut, var2.nCut, this.centerColWidth, this.centerRowHeight) : null;
            this.E = createSlice(var1, this.totalWidth - var2.eCut, var2.nCut, var2.eCut, this.centerRowHeight);
            this.SW = createSlice(var1, 0, this.totalHeight - var2.sCut, var2.wCut, var2.sCut);
            this.S = createSlice(var1, var2.wCut, this.totalHeight - var2.sCut, this.centerColWidth, var2.sCut);
            this.SE = createSlice(var1, this.totalWidth - var2.eCut, this.totalHeight - var2.sCut, var2.eCut, var2.sCut);
         } else {
            throw new IllegalArgumentException("SlicedImageControl: template image and NineSliceMetrics don't agree on minimum dimensions");
         }
      }

      static BufferedImage createSlice(Image var0, int var1, int var2, int var3, int var4) {
         if (var3 != 0 && var4 != 0) {
            BufferedImage var5 = new BufferedImage(var3, var4, 3);
            Graphics2D var6 = var5.createGraphics();
            var6.drawImage(var0, 0, 0, var3, var4, var1, var2, var1 + var3, var2 + var4, (ImageObserver)null);
            var6.dispose();
            return var5;
         } else {
            return null;
         }
      }

      public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
         var1.translate(var2, var3);
         if (var4 >= this.totalWidth && var5 >= this.totalHeight) {
            this.paintStretchedMiddles(var1, var4, var5);
         } else {
            this.paintCompressed(var1, var4, var5);
         }

         var1.translate(-var2, -var3);
      }

      void paintStretchedMiddles(Graphics var1, int var2, int var3) {
         int var4 = this.metrics.stretchH ? 0 : var2 / 2 - this.totalWidth / 2;
         int var5 = this.metrics.stretchV ? 0 : var3 / 2 - this.totalHeight / 2;
         int var6 = this.metrics.stretchH ? var2 : this.totalWidth;
         int var7 = this.metrics.stretchV ? var3 : this.totalHeight;
         if (this.NW != null) {
            var1.drawImage(this.NW, var4, var5, (ImageObserver)null);
         }

         if (this.N != null) {
            var1.drawImage(this.N, var4 + this.metrics.wCut, var5, var6 - this.metrics.eCut - this.metrics.wCut, this.metrics.nCut, (ImageObserver)null);
         }

         if (this.NE != null) {
            var1.drawImage(this.NE, var4 + var6 - this.metrics.eCut, var5, (ImageObserver)null);
         }

         if (this.W != null) {
            var1.drawImage(this.W, var4, var5 + this.metrics.nCut, this.metrics.wCut, var7 - this.metrics.nCut - this.metrics.sCut, (ImageObserver)null);
         }

         if (this.C != null) {
            var1.drawImage(this.C, var4 + this.metrics.wCut, var5 + this.metrics.nCut, var6 - this.metrics.eCut - this.metrics.wCut, var7 - this.metrics.nCut - this.metrics.sCut, (ImageObserver)null);
         }

         if (this.E != null) {
            var1.drawImage(this.E, var4 + var6 - this.metrics.eCut, var5 + this.metrics.nCut, this.metrics.eCut, var7 - this.metrics.nCut - this.metrics.sCut, (ImageObserver)null);
         }

         if (this.SW != null) {
            var1.drawImage(this.SW, var4, var5 + var7 - this.metrics.sCut, (ImageObserver)null);
         }

         if (this.S != null) {
            var1.drawImage(this.S, var4 + this.metrics.wCut, var5 + var7 - this.metrics.sCut, var6 - this.metrics.eCut - this.metrics.wCut, this.metrics.sCut, (ImageObserver)null);
         }

         if (this.SE != null) {
            var1.drawImage(this.SE, var4 + var6 - this.metrics.eCut, var5 + var7 - this.metrics.sCut, (ImageObserver)null);
         }

      }

      void paintCompressed(Graphics var1, int var2, int var3) {
         double var4 = var3 > this.totalHeight ? 1.0D : (double)var3 / (double)this.totalHeight;
         double var6 = var2 > this.totalWidth ? 1.0D : (double)var2 / (double)this.totalWidth;
         int var8 = (int)((double)this.metrics.nCut * var4);
         int var9 = (int)((double)this.metrics.sCut * var4);
         int var10 = var3 - var8 - var9;
         int var11 = (int)((double)this.metrics.wCut * var6);
         int var12 = (int)((double)this.metrics.eCut * var6);
         int var13 = var2 - var11 - var12;
         if (this.NW != null) {
            var1.drawImage(this.NW, 0, 0, var11, var8, (ImageObserver)null);
         }

         if (this.N != null) {
            var1.drawImage(this.N, var11, 0, var13, var8, (ImageObserver)null);
         }

         if (this.NE != null) {
            var1.drawImage(this.NE, var2 - var12, 0, var12, var8, (ImageObserver)null);
         }

         if (this.W != null) {
            var1.drawImage(this.W, 0, var8, var11, var10, (ImageObserver)null);
         }

         if (this.C != null) {
            var1.drawImage(this.C, var11, var8, var13, var10, (ImageObserver)null);
         }

         if (this.E != null) {
            var1.drawImage(this.E, var2 - var12, var8, var12, var10, (ImageObserver)null);
         }

         if (this.SW != null) {
            var1.drawImage(this.SW, 0, var3 - var9, var11, var9, (ImageObserver)null);
         }

         if (this.S != null) {
            var1.drawImage(this.S, var11, var3 - var9, var13, var9, (ImageObserver)null);
         }

         if (this.SE != null) {
            var1.drawImage(this.SE, var2 - var12, var3 - var9, var12, var9, (ImageObserver)null);
         }

      }
   }

   public static class NineSliceMetrics {
      public final int wCut;
      public final int eCut;
      public final int nCut;
      public final int sCut;
      public final int minW;
      public final int minH;
      public final boolean showMiddle;
      public final boolean stretchH;
      public final boolean stretchV;

      public NineSliceMetrics(int var1, int var2, int var3, int var4, int var5, int var6) {
         this(var1, var2, var3, var4, var5, var6, true);
      }

      public NineSliceMetrics(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
         this(var1, var2, var3, var4, var5, var6, var7, true, true);
      }

      public NineSliceMetrics(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9) {
         this.wCut = var3;
         this.eCut = var4;
         this.nCut = var5;
         this.sCut = var6;
         this.minW = var1;
         this.minH = var2;
         this.showMiddle = var7;
         this.stretchH = var8;
         this.stretchV = var9;
      }
   }

   static class InvertableImageIcon extends ImageIcon implements AquaIcon.InvertableIcon, UIResource {
      Icon invertedImage;

      public InvertableImageIcon(Image var1) {
         super(var1);
      }

      public Icon getInvertedIcon() {
         return this.invertedImage != null ? this.invertedImage : (this.invertedImage = new IconUIResource(new ImageIcon(AquaUtils.generateLightenedImage(this.getImage(), 100))));
      }
   }

   static class IconUIResourceSingleton extends AquaUtils.RecyclableSingleton<IconUIResource> {
      final AquaImageFactory.NamedImageSingleton holder;

      public IconUIResourceSingleton(AquaImageFactory.NamedImageSingleton var1) {
         this.holder = var1;
      }

      protected IconUIResource getInstance() {
         return new IconUIResource(new ImageIcon((Image)this.holder.get()));
      }
   }

   static class NamedImageSingleton extends AquaUtils.RecyclableSingleton<Image> {
      final String namedImage;

      NamedImageSingleton(String var1) {
         this.namedImage = var1;
      }

      protected Image getInstance() {
         return AquaImageFactory.getNSIcon(this.namedImage);
      }
   }
}
