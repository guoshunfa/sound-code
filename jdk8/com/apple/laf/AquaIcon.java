package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;

public class AquaIcon {
   static UIResource getIconFor(final AquaIcon.JRSUIControlSpec var0, int var1, int var2) {
      return new AquaIcon.ScalingJRSUIIcon(var1, var2) {
         public void initIconPainter(AquaPainter<JRSUIState> var1) {
            var0.initIconPainter(var1);
         }
      };
   }

   public static Image getImageForIcon(Icon var0) {
      if (var0 instanceof ImageIcon) {
         return ((ImageIcon)var0).getImage();
      } else {
         int var1 = var0.getIconWidth();
         int var2 = var0.getIconHeight();
         if (var1 > 0 && var2 > 0) {
            BufferedImage var3 = new BufferedImage(var1, var2, 3);
            Graphics var4 = var3.getGraphics();
            var0.paintIcon((Component)null, var4, 0, 0);
            var4.dispose();
            return var3;
         } else {
            return null;
         }
      }
   }

   static class SystemIcon extends AquaIcon.CachingScalingIcon {
      private static final AquaIcon.SystemIconUIResourceSingleton folderIcon = new AquaIcon.SystemIconUIResourceSingleton("fldr");
      private static final AquaIcon.SystemIconUIResourceSingleton openFolderIcon = new AquaIcon.SystemIconUIResourceSingleton("ofld");
      private static final AquaIcon.SystemIconUIResourceSingleton desktopIcon = new AquaIcon.SystemIconUIResourceSingleton("desk");
      private static final AquaIcon.SystemIconUIResourceSingleton computerIcon = new AquaIcon.SystemIconUIResourceSingleton("FNDR");
      private static final AquaIcon.SystemIconUIResourceSingleton documentIcon = new AquaIcon.SystemIconUIResourceSingleton("docu");
      private static final AquaIcon.SystemIconUIResourceSingleton hardDriveIcon = new AquaIcon.SystemIconUIResourceSingleton("hdsk");
      private static final AquaIcon.SystemIconUIResourceSingleton floppyIcon = new AquaIcon.SystemIconUIResourceSingleton("flpy");
      private static final AquaIcon.SystemIconSingleton caut = new AquaIcon.SystemIconSingleton("caut");
      private static final AquaIcon.SystemIconSingleton stop = new AquaIcon.SystemIconSingleton("stop");
      final String selector;

      static IconUIResource getFolderIconUIResource() {
         return (IconUIResource)folderIcon.get();
      }

      static IconUIResource getOpenFolderIconUIResource() {
         return (IconUIResource)openFolderIcon.get();
      }

      static IconUIResource getDesktopIconUIResource() {
         return (IconUIResource)desktopIcon.get();
      }

      static IconUIResource getComputerIconUIResource() {
         return (IconUIResource)computerIcon.get();
      }

      static IconUIResource getDocumentIconUIResource() {
         return (IconUIResource)documentIcon.get();
      }

      static IconUIResource getHardDriveIconUIResource() {
         return (IconUIResource)hardDriveIcon.get();
      }

      static IconUIResource getFloppyIconUIResource() {
         return (IconUIResource)floppyIcon.get();
      }

      static AquaIcon.SystemIcon getCautionIcon() {
         return (AquaIcon.SystemIcon)caut.get();
      }

      static AquaIcon.SystemIcon getStopIcon() {
         return (AquaIcon.SystemIcon)stop.get();
      }

      public SystemIcon(String var1, int var2, int var3) {
         super(var2, var3);
         this.selector = var1;
      }

      public SystemIcon(String var1) {
         this(var1, 16, 16);
      }

      Image createImage() {
         return AquaUtils.getCImageCreator().createSystemImageFromSelector(this.selector, this.getIconWidth(), this.getIconHeight());
      }
   }

   static class SystemIconUIResourceSingleton extends AquaUtils.RecyclableSingleton<IconUIResource> {
      final String selector;

      public SystemIconUIResourceSingleton(String var1) {
         this.selector = var1;
      }

      protected IconUIResource getInstance() {
         return new IconUIResource(new AquaIcon.SystemIcon(this.selector));
      }
   }

   static class SystemIconSingleton extends AquaUtils.RecyclableSingleton<AquaIcon.SystemIcon> {
      final String selector;

      public SystemIconSingleton(String var1) {
         this.selector = var1;
      }

      protected AquaIcon.SystemIcon getInstance() {
         return new AquaIcon.SystemIcon(this.selector);
      }
   }

   static class FileIcon extends AquaIcon.CachingScalingIcon {
      final File file;

      public FileIcon(File var1, int var2, int var3) {
         super(var2, var3);
         this.file = var1;
      }

      public FileIcon(File var1) {
         this(var1, 16, 16);
      }

      Image createImage() {
         return AquaUtils.getCImageCreator().createImageOfFile(this.file.getAbsolutePath(), this.getIconWidth(), this.getIconHeight());
      }
   }

   abstract static class ScalingJRSUIIcon implements Icon, UIResource {
      final int width;
      final int height;

      public ScalingJRSUIIcon(int var1, int var2) {
         this.width = var1;
         this.height = var2;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (!GraphicsEnvironment.isHeadless()) {
            var2 = var2.create();
            if (var2 instanceof Graphics2D) {
               ((Graphics2D)var2).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            }

            AquaPainter var5 = AquaPainter.create(JRSUIState.getInstance());
            this.initIconPainter(var5);
            var2.clipRect(var3, var4, this.width, this.height);
            var5.paint(var2, var1, var3, var4, this.width, this.height);
            var2.dispose();
         }
      }

      public abstract void initIconPainter(AquaPainter<JRSUIState> var1);

      public int getIconWidth() {
         return this.width;
      }

      public int getIconHeight() {
         return this.height;
      }
   }

   abstract static class CachingScalingIcon implements Icon, UIResource {
      int width;
      int height;
      Image image;

      public CachingScalingIcon(int var1, int var2) {
         this.width = var1;
         this.height = var2;
      }

      void setSize(int var1, int var2) {
         this.width = var1;
         this.height = var2;
         this.image = null;
      }

      Image getImage() {
         if (this.image != null) {
            return this.image;
         } else {
            if (!GraphicsEnvironment.isHeadless()) {
               this.image = this.createImage();
            }

            return this.image;
         }
      }

      abstract Image createImage();

      public boolean hasIconRef() {
         return this.getImage() != null;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2 = var2.create();
         if (var2 instanceof Graphics2D) {
            ((Graphics2D)var2).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         }

         Image var5 = this.getImage();
         if (var5 != null) {
            var2.drawImage(var5, var3, var4, this.getIconWidth(), this.getIconHeight(), (ImageObserver)null);
         }

         var2.dispose();
      }

      public int getIconWidth() {
         return this.width;
      }

      public int getIconHeight() {
         return this.height;
      }
   }

   abstract static class DynamicallySizingJRSUIIcon extends AquaIcon.JRSUIIcon {
      protected final AquaUtilControlSize.SizeDescriptor sizeDescriptor;
      protected AquaUtilControlSize.SizeVariant sizeVariant;

      public DynamicallySizingJRSUIIcon(AquaUtilControlSize.SizeDescriptor var1) {
         this.sizeDescriptor = var1;
         this.sizeVariant = var1.regular;
         this.initJRSUIState();
      }

      public abstract void initJRSUIState();

      public int getIconHeight() {
         return this.sizeVariant == null ? 0 : this.sizeVariant.h;
      }

      public int getIconWidth() {
         return this.sizeVariant == null ? 0 : this.sizeVariant.w;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         JRSUIConstants.Size var5 = var1 instanceof JComponent ? AquaUtilControlSize.getUserSizeFrom((JComponent)var1) : JRSUIConstants.Size.REGULAR;
         this.sizeVariant = this.sizeDescriptor.get(var5);
         this.painter.state.set(var5);
         super.paintIcon(var1, var2, var3, var4);
      }
   }

   abstract static class JRSUIIcon implements Icon, UIResource {
      protected final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIState.getInstance());

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         this.painter.paint(var2, var1, var3, var4, this.getIconWidth(), this.getIconHeight());
      }
   }

   public interface JRSUIControlSpec {
      void initIconPainter(AquaPainter<? extends JRSUIState> var1);
   }

   interface InvertableIcon extends Icon {
      Icon getInvertedIcon();
   }
}
