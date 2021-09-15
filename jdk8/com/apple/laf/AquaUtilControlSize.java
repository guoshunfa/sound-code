package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import sun.security.action.GetPropertyAction;

public class AquaUtilControlSize {
   protected static final String CLIENT_PROPERTY_KEY = "JComponent.sizeVariant";
   protected static final String SYSTEM_PROPERTY_KEY = "swing.component.sizevariant";
   protected static final AquaUtils.RecyclableSingleton<AquaUtilControlSize.PropertySizeListener> sizeListener = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaUtilControlSize.PropertySizeListener.class);
   protected static final JRSUIConstants.Size defaultSize = getDefaultSize();

   protected static AquaUtilControlSize.PropertySizeListener getSizeListener() {
      return (AquaUtilControlSize.PropertySizeListener)sizeListener.get();
   }

   protected static void addSizePropertyListener(JComponent var0) {
      var0.addPropertyChangeListener("JComponent.sizeVariant", getSizeListener());
      AquaUtilControlSize.PropertySizeListener.applyComponentSize(var0, var0.getClientProperty("JComponent.sizeVariant"));
   }

   protected static void removeSizePropertyListener(JComponent var0) {
      var0.removePropertyChangeListener("JComponent.sizeVariant", getSizeListener());
   }

   private static JRSUIConstants.Size getSizeFromString(String var0) {
      if ("regular".equalsIgnoreCase(var0)) {
         return JRSUIConstants.Size.REGULAR;
      } else if ("small".equalsIgnoreCase(var0)) {
         return JRSUIConstants.Size.SMALL;
      } else if ("mini".equalsIgnoreCase(var0)) {
         return JRSUIConstants.Size.MINI;
      } else {
         return "large".equalsIgnoreCase(var0) ? JRSUIConstants.Size.LARGE : null;
      }
   }

   private static JRSUIConstants.Size getDefaultSize() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.component.sizevariant")));
      JRSUIConstants.Size var1 = getSizeFromString(var0);
      return var1 != null ? var1 : JRSUIConstants.Size.REGULAR;
   }

   protected static JRSUIConstants.Size getUserSizeFrom(JComponent var0) {
      Object var1 = var0.getClientProperty("JComponent.sizeVariant");
      if (var1 == null) {
         return defaultSize;
      } else {
         JRSUIConstants.Size var2 = getSizeFromString(var1.toString());
         return var2 == null ? JRSUIConstants.Size.REGULAR : var2;
      }
   }

   protected static JRSUIConstants.Size applySizeForControl(JComponent var0, AquaPainter<? extends JRSUIState> var1) {
      JRSUIConstants.Size var2 = getUserSizeFrom(var0);
      JRSUIConstants.Size var3 = var2 == null ? JRSUIConstants.Size.REGULAR : var2;
      var1.state.set(var3);
      return var3;
   }

   protected static Font getFontForSize(Component var0, JRSUIConstants.Size var1) {
      Font var2 = var0.getFont();
      if (var1 != null && var2 instanceof UIResource) {
         if (var1 == JRSUIConstants.Size.MINI) {
            return var2.deriveFont(AquaFonts.getMiniControlTextFont().getSize2D());
         } else {
            return var1 == JRSUIConstants.Size.SMALL ? var2.deriveFont(AquaFonts.getSmallControlTextFont().getSize2D()) : var2.deriveFont(AquaFonts.getControlTextFont().getSize2D());
         }
      } else {
         return var2;
      }
   }

   private static void applyBorderForSize(JComponent var0, JRSUIConstants.Size var1) {
      Border var2 = var0.getBorder();
      if (var2 instanceof AquaBorder) {
         AquaBorder var3 = (AquaBorder)var2;
         if (var3.sizeVariant.size != var1) {
            AquaBorder var4 = var3.deriveBorderForSize(var1);
            if (var4 != null) {
               var0.setBorder(var4);
            }
         }
      }
   }

   private static void applyUISizing(JComponent var0, JRSUIConstants.Size var1) {
      try {
         Class var2 = var0.getClass();
         Method var3 = var2.getMethod("getUI");
         Object var4 = var3.invoke(var0);
         if (var4 instanceof AquaUtilControlSize.Sizeable) {
            AquaUtilControlSize.Sizeable var5 = (AquaUtilControlSize.Sizeable)var4;
            var5.applySizeFor(var0, var1);
         }
      } catch (Throwable var6) {
      }
   }

   public static class SizeVariant {
      JRSUIConstants.Size size;
      Insets insets;
      Insets margins;
      Float fontSize;
      int w;
      int h;

      public SizeVariant() {
         this.size = JRSUIConstants.Size.REGULAR;
         this.insets = new InsetsUIResource(0, 0, 0, 0);
         this.margins = new InsetsUIResource(0, 0, 0, 0);
         this.w = 0;
         this.h = 0;
      }

      public SizeVariant(int var1, int var2) {
         this.size = JRSUIConstants.Size.REGULAR;
         this.insets = new InsetsUIResource(0, 0, 0, 0);
         this.margins = new InsetsUIResource(0, 0, 0, 0);
         this.w = 0;
         this.h = 0;
         this.w = var1;
         this.h = var2;
      }

      public SizeVariant(AquaUtilControlSize.SizeVariant var1) {
         this.size = JRSUIConstants.Size.REGULAR;
         this.insets = new InsetsUIResource(0, 0, 0, 0);
         this.margins = new InsetsUIResource(0, 0, 0, 0);
         this.w = 0;
         this.h = 0;
         this.size = var1.size;
         this.insets = new InsetsUIResource(var1.insets.top, var1.insets.left, var1.insets.bottom, var1.insets.right);
         this.margins = new InsetsUIResource(var1.margins.top, var1.margins.left, var1.margins.bottom, var1.margins.right);
         this.fontSize = var1.fontSize;
         this.w = var1.w;
         this.h = var1.h;
      }

      public AquaUtilControlSize.SizeVariant replaceInsets(String var1) {
         this.insets = UIManager.getInsets(var1);
         return this;
      }

      public AquaUtilControlSize.SizeVariant replaceInsets(Insets var1) {
         this.insets = new InsetsUIResource(var1.top, var1.left, var1.bottom, var1.right);
         return this;
      }

      public AquaUtilControlSize.SizeVariant alterInsets(int var1, int var2, int var3, int var4) {
         this.insets = generateInsets(this.insets, var1, var2, var3, var4);
         return this;
      }

      public AquaUtilControlSize.SizeVariant replaceMargins(String var1) {
         this.margins = UIManager.getInsets(var1);
         return this;
      }

      public AquaUtilControlSize.SizeVariant alterMargins(int var1, int var2, int var3, int var4) {
         this.margins = generateInsets(this.margins, var1, var2, var3, var4);
         return this;
      }

      public AquaUtilControlSize.SizeVariant alterFontSize(float var1) {
         float var2 = this.fontSize == null ? 0.0F : this.fontSize;
         this.fontSize = new Float(var1 + var2);
         return this;
      }

      public AquaUtilControlSize.SizeVariant alterMinSize(int var1, int var2) {
         this.w += var1;
         this.h += var2;
         return this;
      }

      static Insets generateInsets(Insets var0, int var1, int var2, int var3, int var4) {
         if (var0 == null) {
            return new InsetsUIResource(var1, var2, var3, var4);
         } else {
            var0.top += var1;
            var0.left += var2;
            var0.bottom += var3;
            var0.right += var4;
            return var0;
         }
      }

      public String toString() {
         return "insets:" + this.insets + ", margins:" + this.margins + ", fontSize:" + this.fontSize;
      }
   }

   public static class SizeDescriptor {
      AquaUtilControlSize.SizeVariant regular;
      AquaUtilControlSize.SizeVariant small;
      AquaUtilControlSize.SizeVariant mini;

      public SizeDescriptor(AquaUtilControlSize.SizeVariant var1) {
         this.regular = this.deriveRegular(var1);
         this.small = this.deriveSmall(new AquaUtilControlSize.SizeVariant(this.regular));
         this.mini = this.deriveMini(new AquaUtilControlSize.SizeVariant(this.small));
      }

      public AquaUtilControlSize.SizeVariant deriveRegular(AquaUtilControlSize.SizeVariant var1) {
         var1.size = JRSUIConstants.Size.REGULAR;
         return var1;
      }

      public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
         var1.size = JRSUIConstants.Size.SMALL;
         return var1;
      }

      public AquaUtilControlSize.SizeVariant deriveMini(AquaUtilControlSize.SizeVariant var1) {
         var1.size = JRSUIConstants.Size.MINI;
         return var1;
      }

      public AquaUtilControlSize.SizeVariant get(JComponent var1) {
         return var1 == null ? this.regular : this.get(AquaUtilControlSize.getUserSizeFrom(var1));
      }

      public AquaUtilControlSize.SizeVariant get(JRSUIConstants.Size var1) {
         if (var1 == JRSUIConstants.Size.REGULAR) {
            return this.regular;
         } else if (var1 == JRSUIConstants.Size.SMALL) {
            return this.small;
         } else {
            return var1 == JRSUIConstants.Size.MINI ? this.mini : this.regular;
         }
      }

      public String toString() {
         return "regular[" + this.regular + "] small[" + this.small + "] mini[" + this.mini + "]";
      }
   }

   protected static class PropertySizeListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("JComponent.sizeVariant".equalsIgnoreCase(var2)) {
            Object var3 = var1.getSource();
            if (var3 instanceof JComponent) {
               JComponent var4 = (JComponent)var3;
               applyComponentSize(var4, var1.getNewValue());
            }
         }
      }

      protected static void applyComponentSize(JComponent var0, Object var1) {
         JRSUIConstants.Size var2 = AquaUtilControlSize.getSizeFromString(var1 == null ? null : var1.toString());
         if (var2 == null) {
            var2 = AquaUtilControlSize.getUserSizeFrom(var0);
            if (var2 == JRSUIConstants.Size.REGULAR) {
               return;
            }
         }

         AquaUtilControlSize.applyBorderForSize(var0, var2);
         AquaUtilControlSize.applyUISizing(var0, var2);
         Font var3 = var0.getFont();
         if (var3 instanceof FontUIResource) {
            var0.setFont(AquaUtilControlSize.getFontForSize(var0, var2));
         }
      }
   }

   interface Sizeable {
      void applySizeFor(JComponent var1, JRSUIConstants.Size var2);
   }
}
