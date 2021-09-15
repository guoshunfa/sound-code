package java.awt;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.awt.SunHints;

public class RenderingHints implements Map<Object, Object>, Cloneable {
   HashMap<Object, Object> hintmap = new HashMap(7);
   public static final RenderingHints.Key KEY_ANTIALIASING;
   public static final Object VALUE_ANTIALIAS_ON;
   public static final Object VALUE_ANTIALIAS_OFF;
   public static final Object VALUE_ANTIALIAS_DEFAULT;
   public static final RenderingHints.Key KEY_RENDERING;
   public static final Object VALUE_RENDER_SPEED;
   public static final Object VALUE_RENDER_QUALITY;
   public static final Object VALUE_RENDER_DEFAULT;
   public static final RenderingHints.Key KEY_DITHERING;
   public static final Object VALUE_DITHER_DISABLE;
   public static final Object VALUE_DITHER_ENABLE;
   public static final Object VALUE_DITHER_DEFAULT;
   public static final RenderingHints.Key KEY_TEXT_ANTIALIASING;
   public static final Object VALUE_TEXT_ANTIALIAS_ON;
   public static final Object VALUE_TEXT_ANTIALIAS_OFF;
   public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT;
   public static final Object VALUE_TEXT_ANTIALIAS_GASP;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR;
   public static final RenderingHints.Key KEY_TEXT_LCD_CONTRAST;
   public static final RenderingHints.Key KEY_FRACTIONALMETRICS;
   public static final Object VALUE_FRACTIONALMETRICS_OFF;
   public static final Object VALUE_FRACTIONALMETRICS_ON;
   public static final Object VALUE_FRACTIONALMETRICS_DEFAULT;
   public static final RenderingHints.Key KEY_INTERPOLATION;
   public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
   public static final Object VALUE_INTERPOLATION_BILINEAR;
   public static final Object VALUE_INTERPOLATION_BICUBIC;
   public static final RenderingHints.Key KEY_ALPHA_INTERPOLATION;
   public static final Object VALUE_ALPHA_INTERPOLATION_SPEED;
   public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY;
   public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT;
   public static final RenderingHints.Key KEY_COLOR_RENDERING;
   public static final Object VALUE_COLOR_RENDER_SPEED;
   public static final Object VALUE_COLOR_RENDER_QUALITY;
   public static final Object VALUE_COLOR_RENDER_DEFAULT;
   public static final RenderingHints.Key KEY_STROKE_CONTROL;
   public static final Object VALUE_STROKE_DEFAULT;
   public static final Object VALUE_STROKE_NORMALIZE;
   public static final Object VALUE_STROKE_PURE;

   public RenderingHints(Map<RenderingHints.Key, ?> var1) {
      if (var1 != null) {
         this.hintmap.putAll(var1);
      }

   }

   public RenderingHints(RenderingHints.Key var1, Object var2) {
      this.hintmap.put(var1, var2);
   }

   public int size() {
      return this.hintmap.size();
   }

   public boolean isEmpty() {
      return this.hintmap.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.hintmap.containsKey((RenderingHints.Key)var1);
   }

   public boolean containsValue(Object var1) {
      return this.hintmap.containsValue(var1);
   }

   public Object get(Object var1) {
      return this.hintmap.get((RenderingHints.Key)var1);
   }

   public Object put(Object var1, Object var2) {
      if (!((RenderingHints.Key)var1).isCompatibleValue(var2)) {
         throw new IllegalArgumentException(var2 + " incompatible with " + var1);
      } else {
         return this.hintmap.put((RenderingHints.Key)var1, var2);
      }
   }

   public void add(RenderingHints var1) {
      this.hintmap.putAll(var1.hintmap);
   }

   public void clear() {
      this.hintmap.clear();
   }

   public Object remove(Object var1) {
      return this.hintmap.remove((RenderingHints.Key)var1);
   }

   public void putAll(Map<?, ?> var1) {
      Iterator var2;
      Map.Entry var3;
      if (RenderingHints.class.isInstance(var1)) {
         var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            this.hintmap.put(var3.getKey(), var3.getValue());
         }
      } else {
         var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            this.put(var3.getKey(), var3.getValue());
         }
      }

   }

   public Set<Object> keySet() {
      return this.hintmap.keySet();
   }

   public Collection<Object> values() {
      return this.hintmap.values();
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return Collections.unmodifiableMap(this.hintmap).entrySet();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof RenderingHints) {
         return this.hintmap.equals(((RenderingHints)var1).hintmap);
      } else {
         return var1 instanceof Map ? this.hintmap.equals(var1) : false;
      }
   }

   public int hashCode() {
      return this.hintmap.hashCode();
   }

   public Object clone() {
      try {
         RenderingHints var1 = (RenderingHints)super.clone();
         if (this.hintmap != null) {
            var1.hintmap = (HashMap)this.hintmap.clone();
         }

         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public String toString() {
      return this.hintmap == null ? this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + " (0 hints)" : this.hintmap.toString();
   }

   static {
      KEY_ANTIALIASING = SunHints.KEY_ANTIALIASING;
      VALUE_ANTIALIAS_ON = SunHints.VALUE_ANTIALIAS_ON;
      VALUE_ANTIALIAS_OFF = SunHints.VALUE_ANTIALIAS_OFF;
      VALUE_ANTIALIAS_DEFAULT = SunHints.VALUE_ANTIALIAS_DEFAULT;
      KEY_RENDERING = SunHints.KEY_RENDERING;
      VALUE_RENDER_SPEED = SunHints.VALUE_RENDER_SPEED;
      VALUE_RENDER_QUALITY = SunHints.VALUE_RENDER_QUALITY;
      VALUE_RENDER_DEFAULT = SunHints.VALUE_RENDER_DEFAULT;
      KEY_DITHERING = SunHints.KEY_DITHERING;
      VALUE_DITHER_DISABLE = SunHints.VALUE_DITHER_DISABLE;
      VALUE_DITHER_ENABLE = SunHints.VALUE_DITHER_ENABLE;
      VALUE_DITHER_DEFAULT = SunHints.VALUE_DITHER_DEFAULT;
      KEY_TEXT_ANTIALIASING = SunHints.KEY_TEXT_ANTIALIASING;
      VALUE_TEXT_ANTIALIAS_ON = SunHints.VALUE_TEXT_ANTIALIAS_ON;
      VALUE_TEXT_ANTIALIAS_OFF = SunHints.VALUE_TEXT_ANTIALIAS_OFF;
      VALUE_TEXT_ANTIALIAS_DEFAULT = SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
      VALUE_TEXT_ANTIALIAS_GASP = SunHints.VALUE_TEXT_ANTIALIAS_GASP;
      VALUE_TEXT_ANTIALIAS_LCD_HRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
      VALUE_TEXT_ANTIALIAS_LCD_HBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
      VALUE_TEXT_ANTIALIAS_LCD_VRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
      VALUE_TEXT_ANTIALIAS_LCD_VBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
      KEY_TEXT_LCD_CONTRAST = SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST;
      KEY_FRACTIONALMETRICS = SunHints.KEY_FRACTIONALMETRICS;
      VALUE_FRACTIONALMETRICS_OFF = SunHints.VALUE_FRACTIONALMETRICS_OFF;
      VALUE_FRACTIONALMETRICS_ON = SunHints.VALUE_FRACTIONALMETRICS_ON;
      VALUE_FRACTIONALMETRICS_DEFAULT = SunHints.VALUE_FRACTIONALMETRICS_DEFAULT;
      KEY_INTERPOLATION = SunHints.KEY_INTERPOLATION;
      VALUE_INTERPOLATION_NEAREST_NEIGHBOR = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      VALUE_INTERPOLATION_BILINEAR = SunHints.VALUE_INTERPOLATION_BILINEAR;
      VALUE_INTERPOLATION_BICUBIC = SunHints.VALUE_INTERPOLATION_BICUBIC;
      KEY_ALPHA_INTERPOLATION = SunHints.KEY_ALPHA_INTERPOLATION;
      VALUE_ALPHA_INTERPOLATION_SPEED = SunHints.VALUE_ALPHA_INTERPOLATION_SPEED;
      VALUE_ALPHA_INTERPOLATION_QUALITY = SunHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
      VALUE_ALPHA_INTERPOLATION_DEFAULT = SunHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
      KEY_COLOR_RENDERING = SunHints.KEY_COLOR_RENDERING;
      VALUE_COLOR_RENDER_SPEED = SunHints.VALUE_COLOR_RENDER_SPEED;
      VALUE_COLOR_RENDER_QUALITY = SunHints.VALUE_COLOR_RENDER_QUALITY;
      VALUE_COLOR_RENDER_DEFAULT = SunHints.VALUE_COLOR_RENDER_DEFAULT;
      KEY_STROKE_CONTROL = SunHints.KEY_STROKE_CONTROL;
      VALUE_STROKE_DEFAULT = SunHints.VALUE_STROKE_DEFAULT;
      VALUE_STROKE_NORMALIZE = SunHints.VALUE_STROKE_NORMALIZE;
      VALUE_STROKE_PURE = SunHints.VALUE_STROKE_PURE;
   }

   public abstract static class Key {
      private static HashMap<Object, Object> identitymap = new HashMap(17);
      private int privatekey;

      private String getIdentity() {
         return this.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this.getClass())) + ":" + Integer.toHexString(this.privatekey);
      }

      private static synchronized void recordIdentity(RenderingHints.Key var0) {
         String var1 = var0.getIdentity();
         Object var2 = identitymap.get(var1);
         if (var2 != null) {
            RenderingHints.Key var3 = (RenderingHints.Key)((WeakReference)var2).get();
            if (var3 != null && var3.getClass() == var0.getClass()) {
               throw new IllegalArgumentException(var1 + " already registered");
            }
         }

         identitymap.put(var1, new WeakReference(var0));
      }

      protected Key(int var1) {
         this.privatekey = var1;
         recordIdentity(this);
      }

      public abstract boolean isCompatibleValue(Object var1);

      protected final int intKey() {
         return this.privatekey;
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final boolean equals(Object var1) {
         return this == var1;
      }
   }
}
