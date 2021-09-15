package com.apple.laf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Kernel;
import java.awt.image.RGBImageFilter;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GrayFilter;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.awt.image.MultiResolutionCachedImage;
import sun.lwawt.macosx.CImage;
import sun.misc.Launcher;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;

final class AquaUtils {
   private static final String ANIMATIONS_PROPERTY = "swing.enableAnimations";
   private static final AquaUtils.RecyclableSingleton<CImage.Creator> cImageCreator = new AquaUtils.RecyclableSingleton<CImage.Creator>() {
      protected CImage.Creator getInstance() {
         return AquaUtils.getCImageCreatorInternal();
      }
   };
   private static final AquaUtils.RecyclableSingleton<Boolean> enableAnimations = new AquaUtils.RecyclableSingleton<Boolean>() {
      protected Boolean getInstance() {
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.enableAnimations")));
         return !"false".equals(var1);
      }
   };
   private static final int MENU_BLINK_DELAY = 50;
   private static final AquaUtils.RecyclableSingleton<Method> getJComponentGetFlagMethod = new AquaUtils.RecyclableSingleton<Method>() {
      protected Method getInstance() {
         return (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
            public Method run() {
               try {
                  Method var1 = JComponent.class.getDeclaredMethod("getFlag", Integer.TYPE);
                  var1.setAccessible(true);
                  return var1;
               } catch (Throwable var2) {
                  return null;
               }
            }
         });
      }
   };
   private static final Integer OPAQUE_SET_FLAG = 24;

   private AquaUtils() {
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static void enforceComponentOrientation(Component var0, ComponentOrientation var1) {
      var0.setComponentOrientation(var1);
      if (var0 instanceof Container) {
         Component[] var2 = ((Container)var0).getComponents();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            enforceComponentOrientation(var5, var1);
         }
      }

   }

   private static CImage.Creator getCImageCreatorInternal() {
      return (CImage.Creator)AccessController.doPrivileged(new PrivilegedAction<CImage.Creator>() {
         public CImage.Creator run() {
            try {
               Method var1 = CImage.class.getDeclaredMethod("getCreator");
               var1.setAccessible(true);
               return (CImage.Creator)var1.invoke((Object)null);
            } catch (Exception var2) {
               return null;
            }
         }
      });
   }

   static CImage.Creator getCImageCreator() {
      return (CImage.Creator)cImageCreator.get();
   }

   static Image generateSelectedDarkImage(Image var0) {
      FilteredImageSource var1 = new FilteredImageSource(var0.getSource(), new AquaUtils.IconImageFilter() {
         int getGreyFor(int var1) {
            return var1 * 75 / 100;
         }
      });
      return Toolkit.getDefaultToolkit().createImage((ImageProducer)var1);
   }

   static Image generateDisabledImage(Image var0) {
      FilteredImageSource var1 = new FilteredImageSource(var0.getSource(), new AquaUtils.IconImageFilter() {
         int getGreyFor(int var1) {
            return 255 - (255 - var1) * 65 / 100;
         }
      });
      return Toolkit.getDefaultToolkit().createImage((ImageProducer)var1);
   }

   static Image generateLightenedImage(Image var0, int var1) {
      GrayFilter var2 = new GrayFilter(true, var1);
      return (Image)(var0 instanceof MultiResolutionCachedImage ? ((MultiResolutionCachedImage)var0).map((var1x) -> {
         return generateLightenedImage(var1x, var2);
      }) : generateLightenedImage(var0, var2));
   }

   static Image generateLightenedImage(Image var0, ImageFilter var1) {
      FilteredImageSource var2 = new FilteredImageSource(var0.getSource(), var1);
      return Toolkit.getDefaultToolkit().createImage((ImageProducer)var2);
   }

   private static boolean animationsEnabled() {
      return (Boolean)enableAnimations.get();
   }

   static void blinkMenu(AquaUtils.Selectable var0) {
      if (animationsEnabled()) {
         try {
            var0.paintSelected(false);
            Thread.sleep(50L);
            var0.paintSelected(true);
            Thread.sleep(50L);
         } catch (InterruptedException var2) {
         }

      }
   }

   static void paintDropShadowText(Graphics var0, JComponent var1, Font var2, FontMetrics var3, int var4, int var5, int var6, int var7, Color var8, Color var9, String var10) {
      var0.setFont(var2);
      var0.setColor(var9);
      SwingUtilities2.drawString(var1, var0, var10, var4 + var6, var5 + var7 + var3.getAscent());
      var0.setColor(var8);
      SwingUtilities2.drawString(var1, var0, var10, var4, var5 + var3.getAscent());
   }

   static boolean shouldUseOpaqueButtons() {
      ClassLoader var0 = Launcher.getLauncher().getClassLoader();
      return classExists(var0, "com.installshield.wizard.platform.macosx.MacOSXUtils");
   }

   private static boolean classExists(ClassLoader var0, String var1) {
      try {
         return Class.forName(var1, false, var0) != null;
      } catch (Throwable var3) {
         return false;
      }
   }

   static boolean hasOpaqueBeenExplicitlySet(JComponent var0) {
      Method var1 = (Method)getJComponentGetFlagMethod.get();
      if (var1 == null) {
         return false;
      } else {
         try {
            return Boolean.TRUE.equals(var1.invoke(var0, OPAQUE_SET_FLAG));
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   private static boolean isWindowTextured(Component var0) {
      if (!(var0 instanceof JComponent)) {
         return false;
      } else {
         JRootPane var1 = ((JComponent)var0).getRootPane();
         if (var1 == null) {
            return false;
         } else {
            Object var2 = var1.getClientProperty("apple.awt.brushMetalLook");
            if (var2 != null) {
               return Boolean.parseBoolean(var2.toString());
            } else {
               var2 = var1.getClientProperty("Window.style");
               return var2 != null && "textured".equals(var2);
            }
         }
      }
   }

   private static Color resetAlpha(Color var0) {
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), 0);
   }

   static void fillRect(Graphics var0, Component var1) {
      fillRect(var0, var1, var1.getBackground(), 0, 0, var1.getWidth(), var1.getHeight());
   }

   static void fillRect(Graphics var0, Component var1, Color var2, int var3, int var4, int var5, int var6) {
      if (var0 instanceof Graphics2D) {
         Graphics2D var7 = (Graphics2D)var0.create();

         try {
            if (var2 instanceof UIResource && isWindowTextured(var1) && var2.equals(SystemColor.window)) {
               var7.setComposite(AlphaComposite.Src);
               var7.setColor(resetAlpha(var2));
            } else {
               var7.setColor(var2);
            }

            var7.fillRect(var3, var4, var5, var6);
         } finally {
            var7.dispose();
         }

      }
   }

   static class SlicedShadowBorder extends AquaUtils.ShadowBorder {
      private final AquaImageFactory.SlicedImageControl slices;

      SlicedShadowBorder(AquaUtils.Painter var1, AquaUtils.Painter var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
         super(var1, var2, var3, var4, var5, var6, var7);
         BufferedImage var14 = new BufferedImage(var8, var9, 3);
         super.paintBorder((Component)null, var14.getGraphics(), 0, 0, var8, var9);
         this.slices = new AquaImageFactory.SlicedImageControl(var14, var10, var11, var12, var13, false);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         this.slices.paint(var2, var3, var4, var5, var6);
      }
   }

   static class ShadowBorder implements Border {
      private final AquaUtils.Painter prePainter;
      private final AquaUtils.Painter postPainter;
      private final int offsetX;
      private final int offsetY;
      private final float distance;
      private final int blur;
      private final Insets insets;
      private final ConvolveOp blurOp;

      ShadowBorder(AquaUtils.Painter var1, AquaUtils.Painter var2, int var3, int var4, float var5, float var6, int var7) {
         this.prePainter = var1;
         this.postPainter = var2;
         this.offsetX = var3;
         this.offsetY = var4;
         this.distance = var5;
         this.blur = var7;
         int var8 = var7 / 2;
         this.insets = new Insets(var8 - var4, var8 - var3, var8 + var4, var8 + var3);
         float var9 = var6 / (float)(var7 * var7);
         float[] var10 = new float[var7 * var7];

         for(int var11 = 0; var11 < var10.length; ++var11) {
            var10[var11] = var9;
         }

         this.blurOp = new ConvolveOp(new Kernel(var7, var7, var10));
      }

      public final boolean isBorderOpaque() {
         return false;
      }

      public final Insets getBorderInsets(Component var1) {
         return this.insets;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         BufferedImage var7 = new BufferedImage(var5 + this.blur * 2, var6 + this.blur * 2, 3);
         this.paintToImage(var7, var3, var4, var5, var6);
         var2.drawImage(var7, -this.blur, -this.blur, (ImageObserver)null);
      }

      private void paintToImage(BufferedImage var1, int var2, int var3, int var4, int var5) {
         Graphics2D var6 = (Graphics2D)var1.getGraphics();
         var6.setComposite(AlphaComposite.Clear);
         var6.setColor(Color.black);
         var6.fillRect(0, 0, var4 + this.blur * 2, var5 + this.blur * 2);
         int var7 = (int)((float)(var2 + this.blur + this.offsetX) + (float)this.insets.left * this.distance);
         int var8 = (int)((float)(var3 + this.blur + this.offsetY) + (float)this.insets.top * this.distance);
         int var9 = (int)((float)var4 - (float)(this.insets.left + this.insets.right) * this.distance);
         int var10 = (int)((float)var5 - (float)(this.insets.top + this.insets.bottom) * this.distance);
         var6.setComposite(AlphaComposite.DstAtop);
         if (this.prePainter != null) {
            this.prePainter.paint(var6, var7, var8, var9, var10);
         }

         var6.dispose();
         var6 = (Graphics2D)var1.getGraphics();
         var6.setComposite(AlphaComposite.DstAtop);
         var6.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         var6.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
         var6.drawImage(var1, this.blurOp, 0, 0);
         if (this.postPainter != null) {
            this.postPainter.paint(var6, var7, var8, var9, var10);
         }

         var6.dispose();
      }
   }

   interface Painter {
      void paint(Graphics var1, int var2, int var3, int var4, int var5);
   }

   interface JComponentPainter {
      void paint(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6);
   }

   interface Selectable {
      void paintSelected(boolean var1);
   }

   abstract static class LazyKeyedSingleton<K, V> {
      private Map<K, V> refs;

      V get(K var1) {
         if (this.refs == null) {
            this.refs = new HashMap();
         }

         Object var2 = this.refs.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            Object var3 = this.getInstance(var1);
            this.refs.put(var1, var3);
            return var3;
         }
      }

      protected abstract V getInstance(K var1);
   }

   static class RecyclableSingletonFromDefaultConstructor<T> extends AquaUtils.RecyclableSingleton<T> {
      private final Class<T> clazz;

      RecyclableSingletonFromDefaultConstructor(Class<T> var1) {
         this.clazz = var1;
      }

      T getInstance() {
         try {
            ReflectUtil.checkPackageAccess(this.clazz);
            return this.clazz.newInstance();
         } catch (IllegalAccessException | InstantiationException var2) {
            return null;
         }
      }
   }

   abstract static class RecyclableSingleton<T> {
      final T get() {
         return AppContext.getSoftReferenceValue(this, () -> {
            return this.getInstance();
         });
      }

      void reset() {
         AppContext.getAppContext().remove(this);
      }

      abstract T getInstance();
   }

   abstract static class RecyclableObject<T> {
      private SoftReference<T> objectRef;

      T get() {
         Object var1;
         if (this.objectRef != null && (var1 = this.objectRef.get()) != null) {
            return var1;
         } else {
            var1 = this.create();
            this.objectRef = new SoftReference(var1);
            return var1;
         }
      }

      protected abstract T create();
   }

   private abstract static class IconImageFilter extends RGBImageFilter {
      IconImageFilter() {
         this.canFilterIndexColorModel = true;
      }

      public final int filterRGB(int var1, int var2, int var3) {
         int var4 = var3 >> 16 & 255;
         int var5 = var3 >> 8 & 255;
         int var6 = var3 & 255;
         int var7 = this.getGreyFor((int)((0.3D * (double)var4 + 0.59D * (double)var5 + 0.11D * (double)var6) / 3.0D));
         return var3 & -16777216 | grayTransform(var4, var7) << 16 | grayTransform(var5, var7) << 8 | grayTransform(var6, var7) << 0;
      }

      private static int grayTransform(int var0, int var1) {
         int var2 = var0 - var1;
         if (var2 < 0) {
            var2 = 0;
         }

         if (var2 > 255) {
            var2 = 255;
         }

         return var2;
      }

      abstract int getGreyFor(int var1);
   }
}
