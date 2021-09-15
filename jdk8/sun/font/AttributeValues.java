package sun.font;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.font.GraphicAttribute;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.im.InputMethodHighlight;
import java.io.Serializable;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public final class AttributeValues implements Cloneable {
   private int defined;
   private int nondefault;
   private String family = "Default";
   private float weight = 1.0F;
   private float width = 1.0F;
   private float posture;
   private float size = 12.0F;
   private float tracking;
   private NumericShaper numericShaping;
   private AffineTransform transform;
   private GraphicAttribute charReplacement;
   private Paint foreground;
   private Paint background;
   private float justification = 1.0F;
   private Object imHighlight;
   private Font font;
   private byte imUnderline = -1;
   private byte superscript;
   private byte underline = -1;
   private byte runDirection = -2;
   private byte bidiEmbedding;
   private byte kerning;
   private byte ligatures;
   private boolean strikethrough;
   private boolean swapColors;
   private AffineTransform baselineTransform;
   private AffineTransform charTransform;
   private static final AttributeValues DEFAULT = new AttributeValues();
   public static final int MASK_ALL = getMask((EAttribute[])EAttribute.class.getEnumConstants());
   private static final String DEFINED_KEY = "sun.font.attributevalues.defined_key";

   public String getFamily() {
      return this.family;
   }

   public void setFamily(String var1) {
      this.family = var1;
      this.update(EAttribute.EFAMILY);
   }

   public float getWeight() {
      return this.weight;
   }

   public void setWeight(float var1) {
      this.weight = var1;
      this.update(EAttribute.EWEIGHT);
   }

   public float getWidth() {
      return this.width;
   }

   public void setWidth(float var1) {
      this.width = var1;
      this.update(EAttribute.EWIDTH);
   }

   public float getPosture() {
      return this.posture;
   }

   public void setPosture(float var1) {
      this.posture = var1;
      this.update(EAttribute.EPOSTURE);
   }

   public float getSize() {
      return this.size;
   }

   public void setSize(float var1) {
      this.size = var1;
      this.update(EAttribute.ESIZE);
   }

   public AffineTransform getTransform() {
      return this.transform;
   }

   public void setTransform(AffineTransform var1) {
      this.transform = var1 != null && !var1.isIdentity() ? new AffineTransform(var1) : DEFAULT.transform;
      this.updateDerivedTransforms();
      this.update(EAttribute.ETRANSFORM);
   }

   public void setTransform(TransformAttribute var1) {
      this.transform = var1 != null && !var1.isIdentity() ? var1.getTransform() : DEFAULT.transform;
      this.updateDerivedTransforms();
      this.update(EAttribute.ETRANSFORM);
   }

   public int getSuperscript() {
      return this.superscript;
   }

   public void setSuperscript(int var1) {
      this.superscript = (byte)var1;
      this.update(EAttribute.ESUPERSCRIPT);
   }

   public Font getFont() {
      return this.font;
   }

   public void setFont(Font var1) {
      this.font = var1;
      this.update(EAttribute.EFONT);
   }

   public GraphicAttribute getCharReplacement() {
      return this.charReplacement;
   }

   public void setCharReplacement(GraphicAttribute var1) {
      this.charReplacement = var1;
      this.update(EAttribute.ECHAR_REPLACEMENT);
   }

   public Paint getForeground() {
      return this.foreground;
   }

   public void setForeground(Paint var1) {
      this.foreground = var1;
      this.update(EAttribute.EFOREGROUND);
   }

   public Paint getBackground() {
      return this.background;
   }

   public void setBackground(Paint var1) {
      this.background = var1;
      this.update(EAttribute.EBACKGROUND);
   }

   public int getUnderline() {
      return this.underline;
   }

   public void setUnderline(int var1) {
      this.underline = (byte)var1;
      this.update(EAttribute.EUNDERLINE);
   }

   public boolean getStrikethrough() {
      return this.strikethrough;
   }

   public void setStrikethrough(boolean var1) {
      this.strikethrough = var1;
      this.update(EAttribute.ESTRIKETHROUGH);
   }

   public int getRunDirection() {
      return this.runDirection;
   }

   public void setRunDirection(int var1) {
      this.runDirection = (byte)var1;
      this.update(EAttribute.ERUN_DIRECTION);
   }

   public int getBidiEmbedding() {
      return this.bidiEmbedding;
   }

   public void setBidiEmbedding(int var1) {
      this.bidiEmbedding = (byte)var1;
      this.update(EAttribute.EBIDI_EMBEDDING);
   }

   public float getJustification() {
      return this.justification;
   }

   public void setJustification(float var1) {
      this.justification = var1;
      this.update(EAttribute.EJUSTIFICATION);
   }

   public Object getInputMethodHighlight() {
      return this.imHighlight;
   }

   public void setInputMethodHighlight(Annotation var1) {
      this.imHighlight = var1;
      this.update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
   }

   public void setInputMethodHighlight(InputMethodHighlight var1) {
      this.imHighlight = var1;
      this.update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
   }

   public int getInputMethodUnderline() {
      return this.imUnderline;
   }

   public void setInputMethodUnderline(int var1) {
      this.imUnderline = (byte)var1;
      this.update(EAttribute.EINPUT_METHOD_UNDERLINE);
   }

   public boolean getSwapColors() {
      return this.swapColors;
   }

   public void setSwapColors(boolean var1) {
      this.swapColors = var1;
      this.update(EAttribute.ESWAP_COLORS);
   }

   public NumericShaper getNumericShaping() {
      return this.numericShaping;
   }

   public void setNumericShaping(NumericShaper var1) {
      this.numericShaping = var1;
      this.update(EAttribute.ENUMERIC_SHAPING);
   }

   public int getKerning() {
      return this.kerning;
   }

   public void setKerning(int var1) {
      this.kerning = (byte)var1;
      this.update(EAttribute.EKERNING);
   }

   public float getTracking() {
      return this.tracking;
   }

   public void setTracking(float var1) {
      this.tracking = (float)((byte)((int)var1));
      this.update(EAttribute.ETRACKING);
   }

   public int getLigatures() {
      return this.ligatures;
   }

   public void setLigatures(int var1) {
      this.ligatures = (byte)var1;
      this.update(EAttribute.ELIGATURES);
   }

   public AffineTransform getBaselineTransform() {
      return this.baselineTransform;
   }

   public AffineTransform getCharTransform() {
      return this.charTransform;
   }

   public static int getMask(EAttribute var0) {
      return var0.mask;
   }

   public static int getMask(EAttribute... var0) {
      int var1 = 0;
      EAttribute[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EAttribute var5 = var2[var4];
         var1 |= var5.mask;
      }

      return var1;
   }

   public void unsetDefault() {
      this.defined &= this.nondefault;
   }

   public void defineAll(int var1) {
      this.defined |= var1;
      if ((this.defined & EAttribute.EBASELINE_TRANSFORM.mask) != 0) {
         throw new InternalError("can't define derived attribute");
      }
   }

   public boolean allDefined(int var1) {
      return (this.defined & var1) == var1;
   }

   public boolean anyDefined(int var1) {
      return (this.defined & var1) != 0;
   }

   public boolean anyNonDefault(int var1) {
      return (this.nondefault & var1) != 0;
   }

   public boolean isDefined(EAttribute var1) {
      return (this.defined & var1.mask) != 0;
   }

   public boolean isNonDefault(EAttribute var1) {
      return (this.nondefault & var1.mask) != 0;
   }

   public void setDefault(EAttribute var1) {
      if (var1.att == null) {
         throw new InternalError("can't set default derived attribute: " + var1);
      } else {
         this.i_set(var1, DEFAULT);
         this.defined |= var1.mask;
         this.nondefault &= ~var1.mask;
      }
   }

   public void unset(EAttribute var1) {
      if (var1.att == null) {
         throw new InternalError("can't unset derived attribute: " + var1);
      } else {
         this.i_set(var1, DEFAULT);
         this.defined &= ~var1.mask;
         this.nondefault &= ~var1.mask;
      }
   }

   public void set(EAttribute var1, AttributeValues var2) {
      if (var1.att == null) {
         throw new InternalError("can't set derived attribute: " + var1);
      } else {
         if (var2 != null && var2 != DEFAULT) {
            if ((var2.defined & var1.mask) != 0) {
               this.i_set(var1, var2);
               this.update(var1);
            }
         } else {
            this.setDefault(var1);
         }

      }
   }

   public void set(EAttribute var1, Object var2) {
      if (var1.att == null) {
         throw new InternalError("can't set derived attribute: " + var1);
      } else {
         if (var2 != null) {
            try {
               this.i_set(var1, var2);
               this.update(var1);
               return;
            } catch (Exception var4) {
            }
         }

         this.setDefault(var1);
      }
   }

   public Object get(EAttribute var1) {
      if (var1.att == null) {
         throw new InternalError("can't get derived attribute: " + var1);
      } else {
         return (this.nondefault & var1.mask) != 0 ? this.i_get(var1) : null;
      }
   }

   public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> var1) {
      return this.merge(var1, MASK_ALL);
   }

   public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> var1, int var2) {
      if (var1 instanceof AttributeMap && ((AttributeMap)var1).getValues() != null) {
         this.merge(((AttributeMap)var1).getValues(), var2);
      } else if (var1 != null && !var1.isEmpty()) {
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();

            try {
               EAttribute var5 = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)var4.getKey());
               if (var5 != null && (var2 & var5.mask) != 0) {
                  this.set(var5, var4.getValue());
               }
            } catch (ClassCastException var6) {
            }
         }
      }

      return this;
   }

   public AttributeValues merge(AttributeValues var1) {
      return this.merge(var1, MASK_ALL);
   }

   public AttributeValues merge(AttributeValues var1, int var2) {
      int var3 = var2 & var1.defined;
      EAttribute[] var4 = EAttribute.atts;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EAttribute var7 = var4[var6];
         if (var3 == 0) {
            break;
         }

         if ((var3 & var7.mask) != 0) {
            var3 &= ~var7.mask;
            this.i_set(var7, var1);
            this.update(var7);
         }
      }

      return this;
   }

   public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> var0) {
      return fromMap(var0, MASK_ALL);
   }

   public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> var0, int var1) {
      return (new AttributeValues()).merge(var0, var1);
   }

   public Map<TextAttribute, Object> toMap(Map<TextAttribute, Object> var1) {
      if (var1 == null) {
         var1 = new HashMap();
      }

      int var2 = this.defined;

      for(int var3 = 0; var2 != 0; ++var3) {
         EAttribute var4 = EAttribute.atts[var3];
         if ((var2 & var4.mask) != 0) {
            var2 &= ~var4.mask;
            ((Map)var1).put(var4.att, this.get(var4));
         }
      }

      return (Map)var1;
   }

   public static boolean is16Hashtable(Hashtable<Object, Object> var0) {
      return var0.containsKey("sun.font.attributevalues.defined_key");
   }

   public static AttributeValues fromSerializableHashtable(Hashtable<Object, Object> var0) {
      AttributeValues var1 = new AttributeValues();
      if (var0 != null && !var0.isEmpty()) {
         Iterator var2 = var0.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            Object var4 = var3.getKey();
            Object var5 = var3.getValue();
            if (var4.equals("sun.font.attributevalues.defined_key")) {
               var1.defineAll((Integer)var5);
            } else {
               try {
                  EAttribute var6 = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)var4);
                  if (var6 != null) {
                     var1.set(var6, var5);
                  }
               } catch (ClassCastException var7) {
               }
            }
         }
      }

      return var1;
   }

   public Hashtable<Object, Object> toSerializableHashtable() {
      Hashtable var1 = new Hashtable();
      int var2 = this.defined;
      int var3 = this.defined;

      for(int var4 = 0; var3 != 0; ++var4) {
         EAttribute var5 = EAttribute.atts[var4];
         if ((var3 & var5.mask) != 0) {
            var3 &= ~var5.mask;
            Object var6 = this.get(var5);
            if (var6 != null) {
               if (var6 instanceof Serializable) {
                  var1.put(var5.att, var6);
               } else {
                  var2 &= ~var5.mask;
               }
            }
         }
      }

      var1.put("sun.font.attributevalues.defined_key", var2);
      return var1;
   }

   public int hashCode() {
      return this.defined << 8 ^ this.nondefault;
   }

   public boolean equals(Object var1) {
      try {
         return this.equals((AttributeValues)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean equals(AttributeValues var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         return this.defined == var1.defined && this.nondefault == var1.nondefault && this.underline == var1.underline && this.strikethrough == var1.strikethrough && this.superscript == var1.superscript && this.width == var1.width && this.kerning == var1.kerning && this.tracking == var1.tracking && this.ligatures == var1.ligatures && this.runDirection == var1.runDirection && this.bidiEmbedding == var1.bidiEmbedding && this.swapColors == var1.swapColors && equals(this.transform, var1.transform) && equals(this.foreground, var1.foreground) && equals(this.background, var1.background) && equals(this.numericShaping, var1.numericShaping) && equals(this.justification, var1.justification) && equals(this.charReplacement, var1.charReplacement) && this.size == var1.size && this.weight == var1.weight && this.posture == var1.posture && equals(this.family, var1.family) && equals(this.font, var1.font) && this.imUnderline == var1.imUnderline && equals(this.imHighlight, var1.imHighlight);
      }
   }

   public AttributeValues clone() {
      try {
         AttributeValues var1 = (AttributeValues)super.clone();
         if (this.transform != null) {
            var1.transform = new AffineTransform(this.transform);
            var1.updateDerivedTransforms();
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('{');
      int var2 = this.defined;

      for(int var3 = 0; var2 != 0; ++var3) {
         EAttribute var4 = EAttribute.atts[var3];
         if ((var2 & var4.mask) != 0) {
            var2 &= ~var4.mask;
            if (var1.length() > 1) {
               var1.append(", ");
            }

            var1.append((Object)var4);
            var1.append('=');
            switch(var4) {
            case EFAMILY:
               var1.append('"');
               var1.append(this.family);
               var1.append('"');
               break;
            case EWEIGHT:
               var1.append(this.weight);
               break;
            case EWIDTH:
               var1.append(this.width);
               break;
            case EPOSTURE:
               var1.append(this.posture);
               break;
            case ESIZE:
               var1.append(this.size);
               break;
            case ETRANSFORM:
               var1.append((Object)this.transform);
               break;
            case ESUPERSCRIPT:
               var1.append((int)this.superscript);
               break;
            case EFONT:
               var1.append((Object)this.font);
               break;
            case ECHAR_REPLACEMENT:
               var1.append((Object)this.charReplacement);
               break;
            case EFOREGROUND:
               var1.append((Object)this.foreground);
               break;
            case EBACKGROUND:
               var1.append((Object)this.background);
               break;
            case EUNDERLINE:
               var1.append((int)this.underline);
               break;
            case ESTRIKETHROUGH:
               var1.append(this.strikethrough);
               break;
            case ERUN_DIRECTION:
               var1.append((int)this.runDirection);
               break;
            case EBIDI_EMBEDDING:
               var1.append((int)this.bidiEmbedding);
               break;
            case EJUSTIFICATION:
               var1.append(this.justification);
               break;
            case EINPUT_METHOD_HIGHLIGHT:
               var1.append(this.imHighlight);
               break;
            case EINPUT_METHOD_UNDERLINE:
               var1.append((int)this.imUnderline);
               break;
            case ESWAP_COLORS:
               var1.append(this.swapColors);
               break;
            case ENUMERIC_SHAPING:
               var1.append((Object)this.numericShaping);
               break;
            case EKERNING:
               var1.append((int)this.kerning);
               break;
            case ELIGATURES:
               var1.append((int)this.ligatures);
               break;
            case ETRACKING:
               var1.append(this.tracking);
               break;
            default:
               throw new InternalError();
            }

            if ((this.nondefault & var4.mask) == 0) {
               var1.append('*');
            }
         }
      }

      var1.append("[btx=" + this.baselineTransform + ", ctx=" + this.charTransform + "]");
      var1.append('}');
      return var1.toString();
   }

   private static boolean equals(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   private void update(EAttribute var1) {
      this.defined |= var1.mask;
      if (this.i_validate(var1)) {
         if (this.i_equals(var1, DEFAULT)) {
            this.nondefault &= ~var1.mask;
         } else {
            this.nondefault |= var1.mask;
         }
      } else {
         this.setDefault(var1);
      }

   }

   private void i_set(EAttribute var1, AttributeValues var2) {
      switch(var1) {
      case EFAMILY:
         this.family = var2.family;
         break;
      case EWEIGHT:
         this.weight = var2.weight;
         break;
      case EWIDTH:
         this.width = var2.width;
         break;
      case EPOSTURE:
         this.posture = var2.posture;
         break;
      case ESIZE:
         this.size = var2.size;
         break;
      case ETRANSFORM:
         this.transform = var2.transform;
         this.updateDerivedTransforms();
         break;
      case ESUPERSCRIPT:
         this.superscript = var2.superscript;
         break;
      case EFONT:
         this.font = var2.font;
         break;
      case ECHAR_REPLACEMENT:
         this.charReplacement = var2.charReplacement;
         break;
      case EFOREGROUND:
         this.foreground = var2.foreground;
         break;
      case EBACKGROUND:
         this.background = var2.background;
         break;
      case EUNDERLINE:
         this.underline = var2.underline;
         break;
      case ESTRIKETHROUGH:
         this.strikethrough = var2.strikethrough;
         break;
      case ERUN_DIRECTION:
         this.runDirection = var2.runDirection;
         break;
      case EBIDI_EMBEDDING:
         this.bidiEmbedding = var2.bidiEmbedding;
         break;
      case EJUSTIFICATION:
         this.justification = var2.justification;
         break;
      case EINPUT_METHOD_HIGHLIGHT:
         this.imHighlight = var2.imHighlight;
         break;
      case EINPUT_METHOD_UNDERLINE:
         this.imUnderline = var2.imUnderline;
         break;
      case ESWAP_COLORS:
         this.swapColors = var2.swapColors;
         break;
      case ENUMERIC_SHAPING:
         this.numericShaping = var2.numericShaping;
         break;
      case EKERNING:
         this.kerning = var2.kerning;
         break;
      case ELIGATURES:
         this.ligatures = var2.ligatures;
         break;
      case ETRACKING:
         this.tracking = var2.tracking;
         break;
      default:
         throw new InternalError();
      }

   }

   private boolean i_equals(EAttribute var1, AttributeValues var2) {
      switch(var1) {
      case EFAMILY:
         return equals(this.family, var2.family);
      case EWEIGHT:
         return this.weight == var2.weight;
      case EWIDTH:
         return this.width == var2.width;
      case EPOSTURE:
         return this.posture == var2.posture;
      case ESIZE:
         return this.size == var2.size;
      case ETRANSFORM:
         return equals(this.transform, var2.transform);
      case ESUPERSCRIPT:
         return this.superscript == var2.superscript;
      case EFONT:
         return equals(this.font, var2.font);
      case ECHAR_REPLACEMENT:
         return equals(this.charReplacement, var2.charReplacement);
      case EFOREGROUND:
         return equals(this.foreground, var2.foreground);
      case EBACKGROUND:
         return equals(this.background, var2.background);
      case EUNDERLINE:
         return this.underline == var2.underline;
      case ESTRIKETHROUGH:
         return this.strikethrough == var2.strikethrough;
      case ERUN_DIRECTION:
         return this.runDirection == var2.runDirection;
      case EBIDI_EMBEDDING:
         return this.bidiEmbedding == var2.bidiEmbedding;
      case EJUSTIFICATION:
         return this.justification == var2.justification;
      case EINPUT_METHOD_HIGHLIGHT:
         return equals(this.imHighlight, var2.imHighlight);
      case EINPUT_METHOD_UNDERLINE:
         return this.imUnderline == var2.imUnderline;
      case ESWAP_COLORS:
         return this.swapColors == var2.swapColors;
      case ENUMERIC_SHAPING:
         return equals(this.numericShaping, var2.numericShaping);
      case EKERNING:
         return this.kerning == var2.kerning;
      case ELIGATURES:
         return this.ligatures == var2.ligatures;
      case ETRACKING:
         return this.tracking == var2.tracking;
      default:
         throw new InternalError();
      }
   }

   private void i_set(EAttribute var1, Object var2) {
      switch(var1) {
      case EFAMILY:
         this.family = ((String)var2).trim();
         break;
      case EWEIGHT:
         this.weight = ((Number)var2).floatValue();
         break;
      case EWIDTH:
         this.width = ((Number)var2).floatValue();
         break;
      case EPOSTURE:
         this.posture = ((Number)var2).floatValue();
         break;
      case ESIZE:
         this.size = ((Number)var2).floatValue();
         break;
      case ETRANSFORM:
         if (var2 instanceof TransformAttribute) {
            TransformAttribute var4 = (TransformAttribute)var2;
            if (var4.isIdentity()) {
               this.transform = null;
            } else {
               this.transform = var4.getTransform();
            }
         } else {
            this.transform = new AffineTransform((AffineTransform)var2);
         }

         this.updateDerivedTransforms();
         break;
      case ESUPERSCRIPT:
         this.superscript = (byte)(Integer)var2;
         break;
      case EFONT:
         this.font = (Font)var2;
         break;
      case ECHAR_REPLACEMENT:
         this.charReplacement = (GraphicAttribute)var2;
         break;
      case EFOREGROUND:
         this.foreground = (Paint)var2;
         break;
      case EBACKGROUND:
         this.background = (Paint)var2;
         break;
      case EUNDERLINE:
         this.underline = (byte)(Integer)var2;
         break;
      case ESTRIKETHROUGH:
         this.strikethrough = (Boolean)var2;
         break;
      case ERUN_DIRECTION:
         if (var2 instanceof Boolean) {
            this.runDirection = (byte)(TextAttribute.RUN_DIRECTION_LTR.equals(var2) ? 0 : 1);
         } else {
            this.runDirection = (byte)(Integer)var2;
         }
         break;
      case EBIDI_EMBEDDING:
         this.bidiEmbedding = (byte)(Integer)var2;
         break;
      case EJUSTIFICATION:
         this.justification = ((Number)var2).floatValue();
         break;
      case EINPUT_METHOD_HIGHLIGHT:
         if (var2 instanceof Annotation) {
            Annotation var3 = (Annotation)var2;
            this.imHighlight = (InputMethodHighlight)var3.getValue();
         } else {
            this.imHighlight = (InputMethodHighlight)var2;
         }
         break;
      case EINPUT_METHOD_UNDERLINE:
         this.imUnderline = (byte)(Integer)var2;
         break;
      case ESWAP_COLORS:
         this.swapColors = (Boolean)var2;
         break;
      case ENUMERIC_SHAPING:
         this.numericShaping = (NumericShaper)var2;
         break;
      case EKERNING:
         this.kerning = (byte)(Integer)var2;
         break;
      case ELIGATURES:
         this.ligatures = (byte)(Integer)var2;
         break;
      case ETRACKING:
         this.tracking = ((Number)var2).floatValue();
         break;
      default:
         throw new InternalError();
      }

   }

   private Object i_get(EAttribute var1) {
      switch(var1) {
      case EFAMILY:
         return this.family;
      case EWEIGHT:
         return this.weight;
      case EWIDTH:
         return this.width;
      case EPOSTURE:
         return this.posture;
      case ESIZE:
         return this.size;
      case ETRANSFORM:
         return this.transform == null ? TransformAttribute.IDENTITY : new TransformAttribute(this.transform);
      case ESUPERSCRIPT:
         return Integer.valueOf(this.superscript);
      case EFONT:
         return this.font;
      case ECHAR_REPLACEMENT:
         return this.charReplacement;
      case EFOREGROUND:
         return this.foreground;
      case EBACKGROUND:
         return this.background;
      case EUNDERLINE:
         return Integer.valueOf(this.underline);
      case ESTRIKETHROUGH:
         return this.strikethrough;
      case ERUN_DIRECTION:
         switch(this.runDirection) {
         case 0:
            return TextAttribute.RUN_DIRECTION_LTR;
         case 1:
            return TextAttribute.RUN_DIRECTION_RTL;
         default:
            return null;
         }
      case EBIDI_EMBEDDING:
         return Integer.valueOf(this.bidiEmbedding);
      case EJUSTIFICATION:
         return this.justification;
      case EINPUT_METHOD_HIGHLIGHT:
         return this.imHighlight;
      case EINPUT_METHOD_UNDERLINE:
         return Integer.valueOf(this.imUnderline);
      case ESWAP_COLORS:
         return this.swapColors;
      case ENUMERIC_SHAPING:
         return this.numericShaping;
      case EKERNING:
         return Integer.valueOf(this.kerning);
      case ELIGATURES:
         return Integer.valueOf(this.ligatures);
      case ETRACKING:
         return this.tracking;
      default:
         throw new InternalError();
      }
   }

   private boolean i_validate(EAttribute var1) {
      switch(var1) {
      case EFAMILY:
         if (this.family == null || this.family.length() == 0) {
            this.family = DEFAULT.family;
         }

         return true;
      case EWEIGHT:
         return this.weight > 0.0F && this.weight < 10.0F;
      case EWIDTH:
         return this.width >= 0.5F && this.width < 10.0F;
      case EPOSTURE:
         return this.posture >= -1.0F && this.posture <= 1.0F;
      case ESIZE:
         return this.size >= 0.0F;
      case ETRANSFORM:
         if (this.transform != null && this.transform.isIdentity()) {
            this.transform = DEFAULT.transform;
         }

         return true;
      case ESUPERSCRIPT:
         return this.superscript >= -7 && this.superscript <= 7;
      case EFONT:
         return true;
      case ECHAR_REPLACEMENT:
         return true;
      case EFOREGROUND:
         return true;
      case EBACKGROUND:
         return true;
      case EUNDERLINE:
         return this.underline >= -1 && this.underline < 6;
      case ESTRIKETHROUGH:
         return true;
      case ERUN_DIRECTION:
         return this.runDirection >= -2 && this.runDirection <= 1;
      case EBIDI_EMBEDDING:
         return this.bidiEmbedding >= -61 && this.bidiEmbedding < 62;
      case EJUSTIFICATION:
         this.justification = Math.max(0.0F, Math.min(this.justification, 1.0F));
         return true;
      case EINPUT_METHOD_HIGHLIGHT:
         return true;
      case EINPUT_METHOD_UNDERLINE:
         return this.imUnderline >= -1 && this.imUnderline < 6;
      case ESWAP_COLORS:
         return true;
      case ENUMERIC_SHAPING:
         return true;
      case EKERNING:
         return this.kerning >= 0 && this.kerning <= 1;
      case ELIGATURES:
         return this.ligatures >= 0 && this.ligatures <= 1;
      case ETRACKING:
         return this.tracking >= -1.0F && this.tracking <= 10.0F;
      default:
         throw new InternalError("unknown attribute: " + var1);
      }
   }

   public static float getJustification(Map<?, ?> var0) {
      if (var0 != null) {
         if (var0 instanceof AttributeMap && ((AttributeMap)var0).getValues() != null) {
            return ((AttributeMap)var0).getValues().justification;
         }

         Object var1 = var0.get(TextAttribute.JUSTIFICATION);
         if (var1 != null && var1 instanceof Number) {
            return Math.max(0.0F, Math.min(1.0F, ((Number)var1).floatValue()));
         }
      }

      return DEFAULT.justification;
   }

   public static NumericShaper getNumericShaping(Map<?, ?> var0) {
      if (var0 != null) {
         if (var0 instanceof AttributeMap && ((AttributeMap)var0).getValues() != null) {
            return ((AttributeMap)var0).getValues().numericShaping;
         }

         Object var1 = var0.get(TextAttribute.NUMERIC_SHAPING);
         if (var1 != null && var1 instanceof NumericShaper) {
            return (NumericShaper)var1;
         }
      }

      return DEFAULT.numericShaping;
   }

   public AttributeValues applyIMHighlight() {
      if (this.imHighlight != null) {
         InputMethodHighlight var1 = null;
         if (this.imHighlight instanceof InputMethodHighlight) {
            var1 = (InputMethodHighlight)this.imHighlight;
         } else {
            var1 = (InputMethodHighlight)((Annotation)this.imHighlight).getValue();
         }

         Map var2 = var1.getStyle();
         if (var2 == null) {
            Toolkit var3 = Toolkit.getDefaultToolkit();
            var2 = var3.mapInputMethodHighlight(var1);
         }

         if (var2 != null) {
            return this.clone().merge(var2);
         }
      }

      return this;
   }

   public static AffineTransform getBaselineTransform(Map<?, ?> var0) {
      if (var0 != null) {
         AttributeValues var1 = null;
         if (var0 instanceof AttributeMap && ((AttributeMap)var0).getValues() != null) {
            var1 = ((AttributeMap)var0).getValues();
         } else if (var0.get(TextAttribute.TRANSFORM) != null) {
            var1 = fromMap(var0);
         }

         if (var1 != null) {
            return var1.baselineTransform;
         }
      }

      return null;
   }

   public static AffineTransform getCharTransform(Map<?, ?> var0) {
      if (var0 != null) {
         AttributeValues var1 = null;
         if (var0 instanceof AttributeMap && ((AttributeMap)var0).getValues() != null) {
            var1 = ((AttributeMap)var0).getValues();
         } else if (var0.get(TextAttribute.TRANSFORM) != null) {
            var1 = fromMap(var0);
         }

         if (var1 != null) {
            return var1.charTransform;
         }
      }

      return null;
   }

   public void updateDerivedTransforms() {
      if (this.transform == null) {
         this.baselineTransform = null;
         this.charTransform = null;
      } else {
         this.charTransform = new AffineTransform(this.transform);
         this.baselineTransform = extractXRotation(this.charTransform, true);
         if (this.charTransform.isIdentity()) {
            this.charTransform = null;
         }

         if (this.baselineTransform.isIdentity()) {
            this.baselineTransform = null;
         }
      }

      if (this.baselineTransform == null) {
         this.nondefault &= ~EAttribute.EBASELINE_TRANSFORM.mask;
      } else {
         this.nondefault |= EAttribute.EBASELINE_TRANSFORM.mask;
      }

   }

   public static AffineTransform extractXRotation(AffineTransform var0, boolean var1) {
      return extractRotation(new Point2D.Double(1.0D, 0.0D), var0, var1);
   }

   public static AffineTransform extractYRotation(AffineTransform var0, boolean var1) {
      return extractRotation(new Point2D.Double(0.0D, 1.0D), var0, var1);
   }

   private static AffineTransform extractRotation(Point2D.Double var0, AffineTransform var1, boolean var2) {
      var1.deltaTransform(var0, var0);
      AffineTransform var3 = AffineTransform.getRotateInstance(var0.x, var0.y);

      try {
         AffineTransform var4 = var3.createInverse();
         double var5 = var1.getTranslateX();
         double var7 = var1.getTranslateY();
         var1.preConcatenate(var4);
         if (var2 && (var5 != 0.0D || var7 != 0.0D)) {
            var1.setTransform(var1.getScaleX(), var1.getShearY(), var1.getShearX(), var1.getScaleY(), 0.0D, 0.0D);
            var3.setTransform(var3.getScaleX(), var3.getShearY(), var3.getShearX(), var3.getScaleY(), var5, var7);
         }

         return var3;
      } catch (NoninvertibleTransformException var9) {
         return null;
      }
   }
}
