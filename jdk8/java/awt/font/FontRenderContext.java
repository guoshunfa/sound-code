package java.awt.font;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FontRenderContext {
   private transient AffineTransform tx;
   private transient Object aaHintValue;
   private transient Object fmHintValue;
   private transient boolean defaulting;

   protected FontRenderContext() {
      this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
      this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
      this.defaulting = true;
   }

   public FontRenderContext(AffineTransform var1, boolean var2, boolean var3) {
      if (var1 != null && !var1.isIdentity()) {
         this.tx = new AffineTransform(var1);
      }

      if (var2) {
         this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
      } else {
         this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      }

      if (var3) {
         this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
      } else {
         this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
      }

   }

   public FontRenderContext(AffineTransform var1, Object var2, Object var3) {
      if (var1 != null && !var1.isIdentity()) {
         this.tx = new AffineTransform(var1);
      }

      try {
         if (!RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(var2)) {
            throw new IllegalArgumentException("AA hint:" + var2);
         }

         this.aaHintValue = var2;
      } catch (Exception var6) {
         throw new IllegalArgumentException("AA hint:" + var2);
      }

      try {
         if (RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(var3)) {
            this.fmHintValue = var3;
         } else {
            throw new IllegalArgumentException("FM hint:" + var3);
         }
      } catch (Exception var5) {
         throw new IllegalArgumentException("FM hint:" + var3);
      }
   }

   public boolean isTransformed() {
      if (!this.defaulting) {
         return this.tx != null;
      } else {
         return !this.getTransform().isIdentity();
      }
   }

   public int getTransformType() {
      if (!this.defaulting) {
         return this.tx == null ? 0 : this.tx.getType();
      } else {
         return this.getTransform().getType();
      }
   }

   public AffineTransform getTransform() {
      return this.tx == null ? new AffineTransform() : new AffineTransform(this.tx);
   }

   public boolean isAntiAliased() {
      return this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
   }

   public boolean usesFractionalMetrics() {
      return this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_OFF && this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
   }

   public Object getAntiAliasingHint() {
      if (this.defaulting) {
         return this.isAntiAliased() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      } else {
         return this.aaHintValue;
      }
   }

   public Object getFractionalMetricsHint() {
      if (this.defaulting) {
         return this.usesFractionalMetrics() ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
      } else {
         return this.fmHintValue;
      }
   }

   public boolean equals(Object var1) {
      try {
         return this.equals((FontRenderContext)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean equals(FontRenderContext var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!var1.defaulting && !this.defaulting) {
         if (var1.aaHintValue == this.aaHintValue && var1.fmHintValue == this.fmHintValue) {
            return this.tx == null ? var1.tx == null : this.tx.equals(var1.tx);
         } else {
            return false;
         }
      } else {
         return var1.getAntiAliasingHint() == this.getAntiAliasingHint() && var1.getFractionalMetricsHint() == this.getFractionalMetricsHint() && var1.getTransform().equals(this.getTransform());
      }
   }

   public int hashCode() {
      int var1 = this.tx == null ? 0 : this.tx.hashCode();
      if (this.defaulting) {
         var1 += this.getAntiAliasingHint().hashCode();
         var1 += this.getFractionalMetricsHint().hashCode();
      } else {
         var1 += this.aaHintValue.hashCode();
         var1 += this.fmHintValue.hashCode();
      }

      return var1;
   }
}
