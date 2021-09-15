package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.image.SunWritableRaster;
import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

class XPStyle {
   private static XPStyle xp;
   private static XPStyle.SkinPainter skinPainter = new XPStyle.SkinPainter();
   private static Boolean themeActive = null;
   private HashMap<String, Border> borderMap;
   private HashMap<String, Color> colorMap;
   private boolean flatMenus;

   static synchronized void invalidateStyle() {
      xp = null;
      themeActive = null;
      skinPainter.flush();
   }

   static synchronized XPStyle getXP() {
      if (themeActive == null) {
         Toolkit var0 = Toolkit.getDefaultToolkit();
         themeActive = (Boolean)var0.getDesktopProperty("win.xpstyle.themeActive");
         if (themeActive == null) {
            themeActive = Boolean.FALSE;
         }

         if (themeActive) {
            GetPropertyAction var1 = new GetPropertyAction("swing.noxp");
            if (AccessController.doPrivileged((PrivilegedAction)var1) == null && ThemeReader.isThemed() && !(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel)) {
               xp = new XPStyle();
            }
         }
      }

      return ThemeReader.isXPStyleEnabled() ? xp : null;
   }

   static boolean isVista() {
      XPStyle var0 = getXP();
      return var0 != null && var0.isSkinDefined((Component)null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
   }

   String getString(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      return getTypeEnumName(var1, var2, var3, var4);
   }

   TMSchema.TypeEnum getTypeEnum(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      int var5 = ThemeReader.getEnum(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
      return TMSchema.TypeEnum.getTypeEnum(var4, var5);
   }

   private static String getTypeEnumName(Component var0, TMSchema.Part var1, TMSchema.State var2, TMSchema.Prop var3) {
      int var4 = ThemeReader.getEnum(var1.getControlName(var0), var1.getValue(), TMSchema.State.getValue(var1, var2), var3.getValue());
      return var4 == -1 ? null : TMSchema.TypeEnum.getTypeEnum(var3, var4).getName();
   }

   int getInt(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4, int var5) {
      return ThemeReader.getInt(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
   }

   Dimension getDimension(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      Dimension var5 = ThemeReader.getPosition(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
      return var5 != null ? var5 : new Dimension();
   }

   Point getPoint(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      Dimension var5 = ThemeReader.getPosition(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
      return var5 != null ? new Point(var5.width, var5.height) : new Point();
   }

   Insets getMargin(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      Insets var5 = ThemeReader.getThemeMargins(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
      return var5 != null ? var5 : new Insets(0, 0, 0, 0);
   }

   synchronized Color getColor(XPStyle.Skin var1, TMSchema.Prop var2, Color var3) {
      String var4 = var1.toString() + "." + var2.name();
      TMSchema.Part var5 = var1.part;
      Object var6 = (Color)this.colorMap.get(var4);
      if (var6 == null) {
         var6 = ThemeReader.getColor(var5.getControlName((Component)null), var5.getValue(), TMSchema.State.getValue(var5, var1.state), var2.getValue());
         if (var6 != null) {
            var6 = new ColorUIResource((Color)var6);
            this.colorMap.put(var4, var6);
         }
      }

      return (Color)(var6 != null ? var6 : var3);
   }

   Color getColor(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4, Color var5) {
      return this.getColor(new XPStyle.Skin(var1, var2, var3), var4, var5);
   }

   synchronized Border getBorder(Component var1, TMSchema.Part var2) {
      if (var2 == TMSchema.Part.MENU) {
         return this.flatMenus ? new XPStyle.XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1) : null;
      } else {
         XPStyle.Skin var3 = new XPStyle.Skin(var1, var2, (TMSchema.State)null);
         Object var4 = (Border)this.borderMap.get(var3.string);
         if (var4 == null) {
            String var5 = getTypeEnumName(var1, var2, (TMSchema.State)null, TMSchema.Prop.BGTYPE);
            if ("borderfill".equalsIgnoreCase(var5)) {
               int var6 = this.getInt(var1, var2, (TMSchema.State)null, TMSchema.Prop.BORDERSIZE, 1);
               Color var7 = this.getColor(var3, TMSchema.Prop.BORDERCOLOR, Color.black);
               var4 = new XPStyle.XPFillBorder(var7, var6);
               if (var2 == TMSchema.Part.CP_COMBOBOX) {
                  var4 = new XPStyle.XPStatefulFillBorder(var7, var6, var2, TMSchema.Prop.BORDERCOLOR);
               }
            } else if ("imagefile".equalsIgnoreCase(var5)) {
               Insets var8 = this.getMargin(var1, var2, (TMSchema.State)null, TMSchema.Prop.SIZINGMARGINS);
               if (var8 != null) {
                  if (this.getBoolean(var1, var2, (TMSchema.State)null, TMSchema.Prop.BORDERONLY)) {
                     var4 = new XPStyle.XPImageBorder(var1, var2);
                  } else if (var2 == TMSchema.Part.CP_COMBOBOX) {
                     var4 = new EmptyBorder(1, 1, 1, 1);
                  } else if (var2 == TMSchema.Part.TP_BUTTON) {
                     var4 = new XPStyle.XPEmptyBorder(new Insets(3, 3, 3, 3));
                  } else {
                     var4 = new XPStyle.XPEmptyBorder(var8);
                  }
               }
            }

            if (var4 != null) {
               this.borderMap.put(var3.string, var4);
            }
         }

         return (Border)var4;
      }
   }

   boolean isSkinDefined(Component var1, TMSchema.Part var2) {
      return var2.getValue() == 0 || ThemeReader.isThemePartDefined(var2.getControlName(var1), var2.getValue(), 0);
   }

   synchronized XPStyle.Skin getSkin(Component var1, TMSchema.Part var2) {
      assert this.isSkinDefined(var1, var2) : "part " + var2 + " is not defined";

      return new XPStyle.Skin(var1, var2, (TMSchema.State)null);
   }

   long getThemeTransitionDuration(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.State var4, TMSchema.Prop var5) {
      return ThemeReader.getThemeTransitionDuration(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), TMSchema.State.getValue(var2, var4), var5 != null ? var5.getValue() : 0);
   }

   private XPStyle() {
      this.flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
      this.colorMap = new HashMap();
      this.borderMap = new HashMap();
   }

   private boolean getBoolean(Component var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.Prop var4) {
      return ThemeReader.getBoolean(var2.getControlName(var1), var2.getValue(), TMSchema.State.getValue(var2, var3), var4.getValue());
   }

   static Dimension getPartSize(TMSchema.Part var0, TMSchema.State var1) {
      return ThemeReader.getPartSize(var0.getControlName((Component)null), var0.getValue(), TMSchema.State.getValue(var0, var1));
   }

   private static boolean getSysBoolean(TMSchema.Prop var0) {
      return ThemeReader.getSysBoolean("window", var0.getValue());
   }

   static {
      invalidateStyle();
   }

   static class GlyphButton extends JButton {
      private XPStyle.Skin skin;

      public GlyphButton(Component var1, TMSchema.Part var2) {
         XPStyle var3 = XPStyle.getXP();
         this.skin = var3 != null ? var3.getSkin(var1, var2) : null;
         this.setBorder((Border)null);
         this.setContentAreaFilled(false);
         this.setMinimumSize(new Dimension(5, 5));
         this.setPreferredSize(new Dimension(16, 16));
         this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
      }

      public boolean isFocusTraversable() {
         return false;
      }

      protected TMSchema.State getState() {
         TMSchema.State var1 = TMSchema.State.NORMAL;
         if (!this.isEnabled()) {
            var1 = TMSchema.State.DISABLED;
         } else if (this.getModel().isPressed()) {
            var1 = TMSchema.State.PRESSED;
         } else if (this.getModel().isRollover()) {
            var1 = TMSchema.State.HOT;
         }

         return var1;
      }

      public void paintComponent(Graphics var1) {
         if (XPStyle.getXP() != null && this.skin != null) {
            Dimension var2 = this.getSize();
            this.skin.paintSkin(var1, 0, 0, var2.width, var2.height, this.getState());
         }
      }

      public void setPart(Component var1, TMSchema.Part var2) {
         XPStyle var3 = XPStyle.getXP();
         this.skin = var3 != null ? var3.getSkin(var1, var2) : null;
         this.revalidate();
         this.repaint();
      }

      protected void paintBorder(Graphics var1) {
      }
   }

   private static class SkinPainter extends CachedPainter {
      SkinPainter() {
         super(30);
         this.flush();
      }

      public void flush() {
         super.flush();
      }

      protected void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6) {
         boolean var7 = false;
         XPStyle.Skin var8 = (XPStyle.Skin)var6[0];
         TMSchema.Part var9 = var8.part;
         TMSchema.State var10 = (TMSchema.State)var6[1];
         if (var10 == null) {
            var10 = var8.state;
         }

         if (var1 == null) {
            var1 = var8.component;
         }

         BufferedImage var11 = (BufferedImage)var2;
         WritableRaster var12 = var11.getRaster();
         DataBufferInt var13 = (DataBufferInt)var12.getDataBuffer();
         ThemeReader.paintBackground(SunWritableRaster.stealData((DataBufferInt)var13, 0), var9.getControlName(var1), var9.getValue(), TMSchema.State.getValue(var9, var10), 0, 0, var4, var5, var4);
         SunWritableRaster.markDirty((DataBuffer)var13);
      }

      protected Image createImage(Component var1, int var2, int var3, GraphicsConfiguration var4, Object[] var5) {
         return new BufferedImage(var2, var3, 2);
      }
   }

   static class Skin {
      final Component component;
      final TMSchema.Part part;
      final TMSchema.State state;
      private final String string;
      private Dimension size;

      Skin(Component var1, TMSchema.Part var2) {
         this(var1, var2, (TMSchema.State)null);
      }

      Skin(TMSchema.Part var1, TMSchema.State var2) {
         this((Component)null, var1, var2);
      }

      Skin(Component var1, TMSchema.Part var2, TMSchema.State var3) {
         this.size = null;
         this.component = var1;
         this.part = var2;
         this.state = var3;
         String var4 = var2.getControlName(var1) + "." + var2.name();
         if (var3 != null) {
            var4 = var4 + "(" + var3.name() + ")";
         }

         this.string = var4;
      }

      Insets getContentMargin() {
         byte var1 = 100;
         byte var2 = 100;
         Insets var3 = ThemeReader.getThemeBackgroundContentMargins(this.part.getControlName((Component)null), this.part.getValue(), 0, var1, var2);
         return var3 != null ? var3 : new Insets(0, 0, 0, 0);
      }

      private int getWidth(TMSchema.State var1) {
         if (this.size == null) {
            this.size = XPStyle.getPartSize(this.part, var1);
         }

         return this.size != null ? this.size.width : 0;
      }

      int getWidth() {
         return this.getWidth(this.state != null ? this.state : TMSchema.State.NORMAL);
      }

      private int getHeight(TMSchema.State var1) {
         if (this.size == null) {
            this.size = XPStyle.getPartSize(this.part, var1);
         }

         return this.size != null ? this.size.height : 0;
      }

      int getHeight() {
         return this.getHeight(this.state != null ? this.state : TMSchema.State.NORMAL);
      }

      public String toString() {
         return this.string;
      }

      public boolean equals(Object var1) {
         return var1 instanceof XPStyle.Skin && ((XPStyle.Skin)var1).string.equals(this.string);
      }

      public int hashCode() {
         return this.string.hashCode();
      }

      void paintSkin(Graphics var1, int var2, int var3, TMSchema.State var4) {
         if (var4 == null) {
            var4 = this.state;
         }

         this.paintSkin(var1, var2, var3, this.getWidth(var4), this.getHeight(var4), var4);
      }

      void paintSkin(Graphics var1, Rectangle var2, TMSchema.State var3) {
         this.paintSkin(var1, var2.x, var2.y, var2.width, var2.height, var3);
      }

      void paintSkin(Graphics var1, int var2, int var3, int var4, int var5, TMSchema.State var6) {
         if (XPStyle.getXP() != null) {
            if (ThemeReader.isGetThemeTransitionDurationDefined() && this.component instanceof JComponent && SwingUtilities.getAncestorOfClass(CellRendererPane.class, this.component) == null) {
               AnimationController.paintSkin((JComponent)this.component, this, var1, var2, var3, var4, var5, var6);
            } else {
               this.paintSkinRaw(var1, var2, var3, var4, var5, var6);
            }

         }
      }

      void paintSkinRaw(Graphics var1, int var2, int var3, int var4, int var5, TMSchema.State var6) {
         if (XPStyle.getXP() != null) {
            XPStyle.skinPainter.paint((Component)null, var1, var2, var3, var4, var5, new Object[]{this, var6});
         }
      }

      void paintSkin(Graphics var1, int var2, int var3, int var4, int var5, TMSchema.State var6, boolean var7) {
         if (XPStyle.getXP() != null) {
            if (!var7 || !"borderfill".equals(XPStyle.getTypeEnumName(this.component, this.part, var6, TMSchema.Prop.BGTYPE))) {
               XPStyle.skinPainter.paint((Component)null, var1, var2, var3, var4, var5, new Object[]{this, var6});
            }
         }
      }
   }

   private class XPEmptyBorder extends EmptyBorder implements UIResource {
      XPEmptyBorder(Insets var2) {
         super(var2.top + 2, var2.left + 2, var2.bottom + 2, var2.right + 2);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2 = super.getBorderInsets(var1, var2);
         Insets var3 = null;
         if (var1 instanceof AbstractButton) {
            Insets var4 = ((AbstractButton)var1).getMargin();
            if (var1.getParent() instanceof JToolBar && !(var1 instanceof JRadioButton) && !(var1 instanceof JCheckBox) && var4 instanceof InsetsUIResource) {
               var2.top -= 2;
               var2.left -= 2;
               var2.bottom -= 2;
               var2.right -= 2;
            } else {
               var3 = var4;
            }
         } else if (var1 instanceof JToolBar) {
            var3 = ((JToolBar)var1).getMargin();
         } else if (var1 instanceof JTextComponent) {
            var3 = ((JTextComponent)var1).getMargin();
         }

         if (var3 != null) {
            var2.top = var3.top + 2;
            var2.left = var3.left + 2;
            var2.bottom = var3.bottom + 2;
            var2.right = var3.right + 2;
         }

         return var2;
      }
   }

   private class XPImageBorder extends AbstractBorder implements UIResource {
      XPStyle.Skin skin;

      XPImageBorder(Component var2, TMSchema.Part var3) {
         this.skin = XPStyle.this.getSkin(var2, var3);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         this.skin.paintSkin(var2, var3, var4, var5, var6, (TMSchema.State)null);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = null;
         Insets var4 = this.skin.getContentMargin();
         if (var4 == null) {
            var4 = new Insets(0, 0, 0, 0);
         }

         if (var1 instanceof AbstractButton) {
            var3 = ((AbstractButton)var1).getMargin();
         } else if (var1 instanceof JToolBar) {
            var3 = ((JToolBar)var1).getMargin();
         } else if (var1 instanceof JTextComponent) {
            var3 = ((JTextComponent)var1).getMargin();
         }

         var2.top = (var3 != null ? var3.top : 0) + var4.top;
         var2.left = (var3 != null ? var3.left : 0) + var4.left;
         var2.bottom = (var3 != null ? var3.bottom : 0) + var4.bottom;
         var2.right = (var3 != null ? var3.right : 0) + var4.right;
         return var2;
      }
   }

   private class XPStatefulFillBorder extends XPStyle.XPFillBorder {
      private final TMSchema.Part part;
      private final TMSchema.Prop prop;

      XPStatefulFillBorder(Color var2, int var3, TMSchema.Part var4, TMSchema.Prop var5) {
         super(var2, var3);
         this.part = var4;
         this.prop = var5;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         TMSchema.State var7 = TMSchema.State.NORMAL;
         if (var1 instanceof JComboBox) {
            JComboBox var8 = (JComboBox)var1;
            if (var8.getUI() instanceof WindowsComboBoxUI) {
               WindowsComboBoxUI var9 = (WindowsComboBoxUI)var8.getUI();
               var7 = var9.getXPComboBoxState(var8);
            }
         }

         this.lineColor = XPStyle.this.getColor(var1, this.part, var7, this.prop, Color.black);
         super.paintBorder(var1, var2, var3, var4, var5, var6);
      }
   }

   private class XPFillBorder extends LineBorder implements UIResource {
      XPFillBorder(Color var2, int var3) {
         super(var2, var3);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = null;
         if (var1 instanceof AbstractButton) {
            var3 = ((AbstractButton)var1).getMargin();
         } else if (var1 instanceof JToolBar) {
            var3 = ((JToolBar)var1).getMargin();
         } else if (var1 instanceof JTextComponent) {
            var3 = ((JTextComponent)var1).getMargin();
         }

         var2.top = (var3 != null ? var3.top : 0) + this.thickness;
         var2.left = (var3 != null ? var3.left : 0) + this.thickness;
         var2.bottom = (var3 != null ? var3.bottom : 0) + this.thickness;
         var2.right = (var3 != null ? var3.right : 0) + this.thickness;
         return var2;
      }
   }
}
