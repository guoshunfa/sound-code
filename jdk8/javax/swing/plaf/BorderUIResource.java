package javax.swing.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class BorderUIResource implements Border, UIResource, Serializable {
   static Border etched;
   static Border loweredBevel;
   static Border raisedBevel;
   static Border blackLine;
   private Border delegate;

   public static Border getEtchedBorderUIResource() {
      if (etched == null) {
         etched = new BorderUIResource.EtchedBorderUIResource();
      }

      return etched;
   }

   public static Border getLoweredBevelBorderUIResource() {
      if (loweredBevel == null) {
         loweredBevel = new BorderUIResource.BevelBorderUIResource(1);
      }

      return loweredBevel;
   }

   public static Border getRaisedBevelBorderUIResource() {
      if (raisedBevel == null) {
         raisedBevel = new BorderUIResource.BevelBorderUIResource(0);
      }

      return raisedBevel;
   }

   public static Border getBlackLineBorderUIResource() {
      if (blackLine == null) {
         blackLine = new BorderUIResource.LineBorderUIResource(Color.black);
      }

      return blackLine;
   }

   public BorderUIResource(Border var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null border delegate argument");
      } else {
         this.delegate = var1;
      }
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.delegate.paintBorder(var1, var2, var3, var4, var5, var6);
   }

   public Insets getBorderInsets(Component var1) {
      return this.delegate.getBorderInsets(var1);
   }

   public boolean isBorderOpaque() {
      return this.delegate.isBorderOpaque();
   }

   public static class TitledBorderUIResource extends TitledBorder implements UIResource {
      public TitledBorderUIResource(String var1) {
         super(var1);
      }

      public TitledBorderUIResource(Border var1) {
         super(var1);
      }

      public TitledBorderUIResource(Border var1, String var2) {
         super(var1, var2);
      }

      public TitledBorderUIResource(Border var1, String var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public TitledBorderUIResource(Border var1, String var2, int var3, int var4, Font var5) {
         super(var1, var2, var3, var4, var5);
      }

      @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
      public TitledBorderUIResource(Border var1, String var2, int var3, int var4, Font var5, Color var6) {
         super(var1, var2, var3, var4, var5, var6);
      }
   }

   public static class MatteBorderUIResource extends MatteBorder implements UIResource {
      public MatteBorderUIResource(int var1, int var2, int var3, int var4, Color var5) {
         super(var1, var2, var3, var4, var5);
      }

      public MatteBorderUIResource(int var1, int var2, int var3, int var4, Icon var5) {
         super(var1, var2, var3, var4, var5);
      }

      public MatteBorderUIResource(Icon var1) {
         super(var1);
      }
   }

   public static class EtchedBorderUIResource extends EtchedBorder implements UIResource {
      public EtchedBorderUIResource() {
      }

      public EtchedBorderUIResource(int var1) {
         super(var1);
      }

      public EtchedBorderUIResource(Color var1, Color var2) {
         super(var1, var2);
      }

      @ConstructorProperties({"etchType", "highlightColor", "shadowColor"})
      public EtchedBorderUIResource(int var1, Color var2, Color var3) {
         super(var1, var2, var3);
      }
   }

   public static class BevelBorderUIResource extends BevelBorder implements UIResource {
      public BevelBorderUIResource(int var1) {
         super(var1);
      }

      public BevelBorderUIResource(int var1, Color var2, Color var3) {
         super(var1, var2, var3);
      }

      @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
      public BevelBorderUIResource(int var1, Color var2, Color var3, Color var4, Color var5) {
         super(var1, var2, var3, var4, var5);
      }
   }

   public static class LineBorderUIResource extends LineBorder implements UIResource {
      public LineBorderUIResource(Color var1) {
         super(var1);
      }

      @ConstructorProperties({"lineColor", "thickness"})
      public LineBorderUIResource(Color var1, int var2) {
         super(var1, var2);
      }
   }

   public static class EmptyBorderUIResource extends EmptyBorder implements UIResource {
      public EmptyBorderUIResource(int var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      @ConstructorProperties({"borderInsets"})
      public EmptyBorderUIResource(Insets var1) {
         super(var1);
      }
   }

   public static class CompoundBorderUIResource extends CompoundBorder implements UIResource {
      @ConstructorProperties({"outsideBorder", "insideBorder"})
      public CompoundBorderUIResource(Border var1, Border var2) {
         super(var1, var2);
      }
   }
}
