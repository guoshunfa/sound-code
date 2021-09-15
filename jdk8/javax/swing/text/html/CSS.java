package javax.swing.text.html;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.View;

public class CSS implements Serializable {
   private static final Hashtable<String, CSS.Attribute> attributeMap = new Hashtable();
   private static final Hashtable<String, CSS.Value> valueMap = new Hashtable();
   private static final Hashtable<HTML.Attribute, CSS.Attribute[]> htmlAttrToCssAttrMap = new Hashtable(20);
   private static final Hashtable<Object, CSS.Attribute> styleConstantToCssMap = new Hashtable(17);
   private static final Hashtable<String, CSS.Value> htmlValueToCssValueMap = new Hashtable(8);
   private static final Hashtable<String, CSS.Value> cssValueToInternalValueMap = new Hashtable(13);
   private transient Hashtable<Object, Object> valueConvertor;
   private int baseFontSize;
   private transient StyleSheet styleSheet = null;
   static int baseFontSizeIndex;

   public CSS() {
      this.baseFontSize = baseFontSizeIndex + 1;
      this.valueConvertor = new Hashtable();
      this.valueConvertor.put(CSS.Attribute.FONT_SIZE, new CSS.FontSize());
      this.valueConvertor.put(CSS.Attribute.FONT_FAMILY, new CSS.FontFamily());
      this.valueConvertor.put(CSS.Attribute.FONT_WEIGHT, new CSS.FontWeight());
      CSS.BorderStyle var1 = new CSS.BorderStyle();
      this.valueConvertor.put(CSS.Attribute.BORDER_TOP_STYLE, var1);
      this.valueConvertor.put(CSS.Attribute.BORDER_RIGHT_STYLE, var1);
      this.valueConvertor.put(CSS.Attribute.BORDER_BOTTOM_STYLE, var1);
      this.valueConvertor.put(CSS.Attribute.BORDER_LEFT_STYLE, var1);
      CSS.ColorValue var2 = new CSS.ColorValue();
      this.valueConvertor.put(CSS.Attribute.COLOR, var2);
      this.valueConvertor.put(CSS.Attribute.BACKGROUND_COLOR, var2);
      this.valueConvertor.put(CSS.Attribute.BORDER_TOP_COLOR, var2);
      this.valueConvertor.put(CSS.Attribute.BORDER_RIGHT_COLOR, var2);
      this.valueConvertor.put(CSS.Attribute.BORDER_BOTTOM_COLOR, var2);
      this.valueConvertor.put(CSS.Attribute.BORDER_LEFT_COLOR, var2);
      CSS.LengthValue var3 = new CSS.LengthValue();
      this.valueConvertor.put(CSS.Attribute.MARGIN_TOP, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_BOTTOM, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_LEFT, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_LEFT_LTR, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_LEFT_RTL, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_RIGHT, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_RIGHT_LTR, var3);
      this.valueConvertor.put(CSS.Attribute.MARGIN_RIGHT_RTL, var3);
      this.valueConvertor.put(CSS.Attribute.PADDING_TOP, var3);
      this.valueConvertor.put(CSS.Attribute.PADDING_BOTTOM, var3);
      this.valueConvertor.put(CSS.Attribute.PADDING_LEFT, var3);
      this.valueConvertor.put(CSS.Attribute.PADDING_RIGHT, var3);
      CSS.BorderWidthValue var4 = new CSS.BorderWidthValue((String)null, 0);
      this.valueConvertor.put(CSS.Attribute.BORDER_TOP_WIDTH, var4);
      this.valueConvertor.put(CSS.Attribute.BORDER_BOTTOM_WIDTH, var4);
      this.valueConvertor.put(CSS.Attribute.BORDER_LEFT_WIDTH, var4);
      this.valueConvertor.put(CSS.Attribute.BORDER_RIGHT_WIDTH, var4);
      CSS.LengthValue var5 = new CSS.LengthValue(true);
      this.valueConvertor.put(CSS.Attribute.TEXT_INDENT, var5);
      this.valueConvertor.put(CSS.Attribute.WIDTH, var3);
      this.valueConvertor.put(CSS.Attribute.HEIGHT, var3);
      this.valueConvertor.put(CSS.Attribute.BORDER_SPACING, var3);
      CSS.StringValue var6 = new CSS.StringValue();
      this.valueConvertor.put(CSS.Attribute.FONT_STYLE, var6);
      this.valueConvertor.put(CSS.Attribute.TEXT_DECORATION, var6);
      this.valueConvertor.put(CSS.Attribute.TEXT_ALIGN, var6);
      this.valueConvertor.put(CSS.Attribute.VERTICAL_ALIGN, var6);
      CSS.CssValueMapper var7 = new CSS.CssValueMapper();
      this.valueConvertor.put(CSS.Attribute.LIST_STYLE_TYPE, var7);
      this.valueConvertor.put(CSS.Attribute.BACKGROUND_IMAGE, new CSS.BackgroundImage());
      this.valueConvertor.put(CSS.Attribute.BACKGROUND_POSITION, new CSS.BackgroundPosition());
      this.valueConvertor.put(CSS.Attribute.BACKGROUND_REPEAT, var7);
      this.valueConvertor.put(CSS.Attribute.BACKGROUND_ATTACHMENT, var7);
      CSS.CssValue var8 = new CSS.CssValue();
      int var9 = CSS.Attribute.allAttributes.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         CSS.Attribute var11 = CSS.Attribute.allAttributes[var10];
         if (this.valueConvertor.get(var11) == null) {
            this.valueConvertor.put(var11, var8);
         }
      }

   }

   void setBaseFontSize(int var1) {
      if (var1 < 1) {
         this.baseFontSize = 0;
      } else if (var1 > 7) {
         this.baseFontSize = 7;
      } else {
         this.baseFontSize = var1;
      }

   }

   void setBaseFontSize(String var1) {
      if (var1 != null) {
         int var2;
         if (var1.startsWith("+")) {
            var2 = Integer.valueOf(var1.substring(1));
            this.setBaseFontSize(this.baseFontSize + var2);
         } else if (var1.startsWith("-")) {
            var2 = -Integer.valueOf(var1.substring(1));
            this.setBaseFontSize(this.baseFontSize + var2);
         } else {
            this.setBaseFontSize(Integer.valueOf(var1));
         }
      }

   }

   int getBaseFontSize() {
      return this.baseFontSize;
   }

   void addInternalCSSValue(MutableAttributeSet var1, CSS.Attribute var2, String var3) {
      if (var2 == CSS.Attribute.FONT) {
         CSS.ShorthandFontParser.parseShorthandFont(this, var3, var1);
      } else if (var2 == CSS.Attribute.BACKGROUND) {
         CSS.ShorthandBackgroundParser.parseShorthandBackground(this, var3, var1);
      } else if (var2 == CSS.Attribute.MARGIN) {
         CSS.ShorthandMarginParser.parseShorthandMargin(this, var3, var1, CSS.Attribute.ALL_MARGINS);
      } else if (var2 == CSS.Attribute.PADDING) {
         CSS.ShorthandMarginParser.parseShorthandMargin(this, var3, var1, CSS.Attribute.ALL_PADDING);
      } else if (var2 == CSS.Attribute.BORDER_WIDTH) {
         CSS.ShorthandMarginParser.parseShorthandMargin(this, var3, var1, CSS.Attribute.ALL_BORDER_WIDTHS);
      } else if (var2 == CSS.Attribute.BORDER_COLOR) {
         CSS.ShorthandMarginParser.parseShorthandMargin(this, var3, var1, CSS.Attribute.ALL_BORDER_COLORS);
      } else if (var2 == CSS.Attribute.BORDER_STYLE) {
         CSS.ShorthandMarginParser.parseShorthandMargin(this, var3, var1, CSS.Attribute.ALL_BORDER_STYLES);
      } else if (var2 != CSS.Attribute.BORDER && var2 != CSS.Attribute.BORDER_TOP && var2 != CSS.Attribute.BORDER_RIGHT && var2 != CSS.Attribute.BORDER_BOTTOM && var2 != CSS.Attribute.BORDER_LEFT) {
         Object var4 = this.getInternalCSSValue(var2, var3);
         if (var4 != null) {
            var1.addAttribute(var2, var4);
         }
      } else {
         CSS.ShorthandBorderParser.parseShorthandBorder(var1, var2, var3);
      }

   }

   Object getInternalCSSValue(CSS.Attribute var1, String var2) {
      CSS.CssValue var3 = (CSS.CssValue)this.valueConvertor.get(var1);
      Object var4 = var3.parseCssValue(var2);
      return var4 != null ? var4 : var3.parseCssValue(var1.getDefaultValue());
   }

   CSS.Attribute styleConstantsKeyToCSSKey(StyleConstants var1) {
      return (CSS.Attribute)styleConstantToCssMap.get(var1);
   }

   Object styleConstantsValueToCSSValue(StyleConstants var1, Object var2) {
      CSS.Attribute var3 = this.styleConstantsKeyToCSSKey(var1);
      if (var3 != null) {
         CSS.CssValue var4 = (CSS.CssValue)this.valueConvertor.get(var3);
         return var4.fromStyleConstants(var1, var2);
      } else {
         return null;
      }
   }

   Object cssValueToStyleConstantsValue(StyleConstants var1, Object var2) {
      return var2 instanceof CSS.CssValue ? ((CSS.CssValue)var2).toStyleConstants(var1, (View)null) : null;
   }

   Font getFont(StyleContext var1, AttributeSet var2, int var3, StyleSheet var4) {
      var4 = this.getStyleSheet(var4);
      int var5 = getFontSize(var2, var3, var4);
      CSS.StringValue var6 = (CSS.StringValue)var2.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
      if (var6 != null) {
         String var7 = var6.toString();
         if (var7.indexOf("sup") >= 0 || var7.indexOf("sub") >= 0) {
            var5 -= 2;
         }
      }

      CSS.FontFamily var13 = (CSS.FontFamily)var2.getAttribute(CSS.Attribute.FONT_FAMILY);
      String var8 = var13 != null ? var13.getValue() : "SansSerif";
      int var9 = 0;
      CSS.FontWeight var10 = (CSS.FontWeight)var2.getAttribute(CSS.Attribute.FONT_WEIGHT);
      if (var10 != null && var10.getValue() > 400) {
         var9 |= 1;
      }

      Object var11 = var2.getAttribute(CSS.Attribute.FONT_STYLE);
      if (var11 != null && var11.toString().indexOf("italic") >= 0) {
         var9 |= 2;
      }

      if (var8.equalsIgnoreCase("monospace")) {
         var8 = "Monospaced";
      }

      Font var12 = var1.getFont(var8, var9, var5);
      if (var12 == null || var12.getFamily().equals("Dialog") && !var8.equalsIgnoreCase("Dialog")) {
         var8 = "SansSerif";
         var12 = var1.getFont(var8, var9, var5);
      }

      return var12;
   }

   static int getFontSize(AttributeSet var0, int var1, StyleSheet var2) {
      CSS.FontSize var3 = (CSS.FontSize)var0.getAttribute(CSS.Attribute.FONT_SIZE);
      return var3 != null ? var3.getValue(var0, var2) : var1;
   }

   Color getColor(AttributeSet var1, CSS.Attribute var2) {
      CSS.ColorValue var3 = (CSS.ColorValue)var1.getAttribute(var2);
      return var3 != null ? var3.getValue() : null;
   }

   float getPointSize(String var1, StyleSheet var2) {
      var2 = this.getStyleSheet(var2);
      if (var1 != null) {
         int var3;
         if (var1.startsWith("+")) {
            var3 = Integer.valueOf(var1.substring(1));
            return this.getPointSize(this.baseFontSize + var3, var2);
         } else if (var1.startsWith("-")) {
            var3 = -Integer.valueOf(var1.substring(1));
            return this.getPointSize(this.baseFontSize + var3, var2);
         } else {
            int var4 = Integer.valueOf(var1);
            return this.getPointSize(var4, var2);
         }
      } else {
         return 0.0F;
      }
   }

   float getLength(AttributeSet var1, CSS.Attribute var2, StyleSheet var3) {
      var3 = this.getStyleSheet(var3);
      CSS.LengthValue var4 = (CSS.LengthValue)var1.getAttribute(var2);
      boolean var5 = var3 == null ? false : var3.isW3CLengthUnits();
      float var6 = var4 != null ? var4.getValue(var5) : 0.0F;
      return var6;
   }

   AttributeSet translateHTMLToCSS(AttributeSet var1) {
      SimpleAttributeSet var2 = new SimpleAttributeSet();
      Element var3 = (Element)var1;
      HTML.Tag var4 = this.getHTMLTag(var1);
      if (var4 == HTML.Tag.TD || var4 == HTML.Tag.TH) {
         AttributeSet var5 = var3.getParentElement().getParentElement().getAttributes();
         int var6 = getTableBorder(var5);
         if (var6 > 0) {
            this.translateAttribute(HTML.Attribute.BORDER, "1", var2);
         }

         String var7 = (String)var5.getAttribute(HTML.Attribute.CELLPADDING);
         if (var7 != null) {
            CSS.LengthValue var8 = (CSS.LengthValue)this.getInternalCSSValue(CSS.Attribute.PADDING_TOP, var7);
            var8.span = var8.span < 0.0F ? 0.0F : var8.span;
            var2.addAttribute(CSS.Attribute.PADDING_TOP, var8);
            var2.addAttribute(CSS.Attribute.PADDING_BOTTOM, var8);
            var2.addAttribute(CSS.Attribute.PADDING_LEFT, var8);
            var2.addAttribute(CSS.Attribute.PADDING_RIGHT, var8);
         }
      }

      if (var3.isLeaf()) {
         this.translateEmbeddedAttributes(var1, var2);
      } else {
         this.translateAttributes(var4, var1, var2);
      }

      if (var4 == HTML.Tag.CAPTION) {
         Object var9 = var1.getAttribute(HTML.Attribute.ALIGN);
         if (var9 == null || !var9.equals("top") && !var9.equals("bottom")) {
            var9 = var1.getAttribute(HTML.Attribute.VALIGN);
            if (var9 != null) {
               var2.addAttribute(CSS.Attribute.CAPTION_SIDE, var9);
            }
         } else {
            var2.addAttribute(CSS.Attribute.CAPTION_SIDE, var9);
            var2.removeAttribute(CSS.Attribute.TEXT_ALIGN);
         }
      }

      return var2;
   }

   private static int getTableBorder(AttributeSet var0) {
      String var1 = (String)var0.getAttribute(HTML.Attribute.BORDER);
      if (var1 != "#DEFAULT" && !"".equals(var1)) {
         try {
            return Integer.parseInt(var1);
         } catch (NumberFormatException var3) {
            return 0;
         }
      } else {
         return 1;
      }
   }

   public static CSS.Attribute[] getAllAttributeKeys() {
      CSS.Attribute[] var0 = new CSS.Attribute[CSS.Attribute.allAttributes.length];
      System.arraycopy(CSS.Attribute.allAttributes, 0, var0, 0, CSS.Attribute.allAttributes.length);
      return var0;
   }

   public static final CSS.Attribute getAttribute(String var0) {
      return (CSS.Attribute)attributeMap.get(var0);
   }

   static final CSS.Value getValue(String var0) {
      return (CSS.Value)valueMap.get(var0);
   }

   static URL getURL(URL var0, String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (var1.startsWith("url(") && var1.endsWith(")")) {
            var1 = var1.substring(4, var1.length() - 1);
         }

         URL var2;
         try {
            var2 = new URL(var1);
            if (var2 != null) {
               return var2;
            }
         } catch (MalformedURLException var3) {
         }

         if (var0 != null) {
            try {
               var2 = new URL(var0, var1);
               return var2;
            } catch (MalformedURLException var4) {
            }
         }

         return null;
      }
   }

   static String colorToHex(Color var0) {
      String var1 = "#";
      String var2 = Integer.toHexString(var0.getRed());
      if (var2.length() > 2) {
         var2 = var2.substring(0, 2);
      } else if (var2.length() < 2) {
         var1 = var1 + "0" + var2;
      } else {
         var1 = var1 + var2;
      }

      var2 = Integer.toHexString(var0.getGreen());
      if (var2.length() > 2) {
         var2 = var2.substring(0, 2);
      } else if (var2.length() < 2) {
         var1 = var1 + "0" + var2;
      } else {
         var1 = var1 + var2;
      }

      var2 = Integer.toHexString(var0.getBlue());
      if (var2.length() > 2) {
         var2 = var2.substring(0, 2);
      } else if (var2.length() < 2) {
         var1 = var1 + "0" + var2;
      } else {
         var1 = var1 + var2;
      }

      return var1;
   }

   static final Color hexToColor(String var0) {
      int var2 = var0.length();
      String var1;
      if (var0.startsWith("#")) {
         var1 = var0.substring(1, Math.min(var0.length(), 7));
      } else {
         var1 = var0;
      }

      String var3 = "0x" + var1;

      Color var4;
      try {
         var4 = Color.decode(var3);
      } catch (NumberFormatException var6) {
         var4 = null;
      }

      return var4;
   }

   static Color stringToColor(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Color var1;
         if (var0.length() == 0) {
            var1 = Color.black;
         } else if (var0.startsWith("rgb(")) {
            var1 = parseRGB(var0);
         } else if (var0.charAt(0) == '#') {
            var1 = hexToColor(var0);
         } else if (var0.equalsIgnoreCase("Black")) {
            var1 = hexToColor("#000000");
         } else if (var0.equalsIgnoreCase("Silver")) {
            var1 = hexToColor("#C0C0C0");
         } else if (var0.equalsIgnoreCase("Gray")) {
            var1 = hexToColor("#808080");
         } else if (var0.equalsIgnoreCase("White")) {
            var1 = hexToColor("#FFFFFF");
         } else if (var0.equalsIgnoreCase("Maroon")) {
            var1 = hexToColor("#800000");
         } else if (var0.equalsIgnoreCase("Red")) {
            var1 = hexToColor("#FF0000");
         } else if (var0.equalsIgnoreCase("Purple")) {
            var1 = hexToColor("#800080");
         } else if (var0.equalsIgnoreCase("Fuchsia")) {
            var1 = hexToColor("#FF00FF");
         } else if (var0.equalsIgnoreCase("Green")) {
            var1 = hexToColor("#008000");
         } else if (var0.equalsIgnoreCase("Lime")) {
            var1 = hexToColor("#00FF00");
         } else if (var0.equalsIgnoreCase("Olive")) {
            var1 = hexToColor("#808000");
         } else if (var0.equalsIgnoreCase("Yellow")) {
            var1 = hexToColor("#FFFF00");
         } else if (var0.equalsIgnoreCase("Navy")) {
            var1 = hexToColor("#000080");
         } else if (var0.equalsIgnoreCase("Blue")) {
            var1 = hexToColor("#0000FF");
         } else if (var0.equalsIgnoreCase("Teal")) {
            var1 = hexToColor("#008080");
         } else if (var0.equalsIgnoreCase("Aqua")) {
            var1 = hexToColor("#00FFFF");
         } else if (var0.equalsIgnoreCase("Orange")) {
            var1 = hexToColor("#FF8000");
         } else {
            var1 = hexToColor(var0);
         }

         return var1;
      }
   }

   private static Color parseRGB(String var0) {
      int[] var1 = new int[]{4};
      int var2 = getColorComponent(var0, var1);
      int var3 = getColorComponent(var0, var1);
      int var4 = getColorComponent(var0, var1);
      return new Color(var2, var3, var4);
   }

   private static int getColorComponent(String var0, int[] var1) {
      int var2;
      int var10002;
      char var3;
      for(var2 = var0.length(); var1[0] < var2 && (var3 = var0.charAt(var1[0])) != '-' && !Character.isDigit(var3) && var3 != '.'; var10002 = var1[0]++) {
      }

      int var4 = var1[0];
      if (var4 < var2 && var0.charAt(var1[0]) == '-') {
         var10002 = var1[0]++;
      }

      while(var1[0] < var2 && Character.isDigit(var0.charAt(var1[0]))) {
         var10002 = var1[0]++;
      }

      if (var1[0] < var2 && var0.charAt(var1[0]) == '.') {
         for(var10002 = var1[0]++; var1[0] < var2 && Character.isDigit(var0.charAt(var1[0])); var10002 = var1[0]++) {
         }
      }

      if (var4 != var1[0]) {
         try {
            float var5 = Float.parseFloat(var0.substring(var4, var1[0]));
            if (var1[0] < var2 && var0.charAt(var1[0]) == '%') {
               var10002 = var1[0]++;
               var5 = var5 * 255.0F / 100.0F;
            }

            return Math.min(255, Math.max(0, (int)var5));
         } catch (NumberFormatException var6) {
         }
      }

      return 0;
   }

   static int getIndexOfSize(float var0, int[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var0 <= (float)var1[var2]) {
            return var2 + 1;
         }
      }

      return var1.length;
   }

   static int getIndexOfSize(float var0, StyleSheet var1) {
      int[] var2 = var1 != null ? var1.getSizeMap() : StyleSheet.sizeMapDefault;
      return getIndexOfSize(var0, var2);
   }

   static String[] parseStrings(String var0) {
      int var3 = var0 == null ? 0 : var0.length();
      Vector var4 = new Vector(4);

      for(int var1 = 0; var1 < var3; ++var1) {
         while(var1 < var3 && Character.isWhitespace(var0.charAt(var1))) {
            ++var1;
         }

         int var2;
         for(var2 = var1; var1 < var3 && !Character.isWhitespace(var0.charAt(var1)); ++var1) {
         }

         if (var2 != var1) {
            var4.addElement(var0.substring(var2, var1));
         }
      }

      String[] var5 = new String[var4.size()];
      var4.copyInto(var5);
      return var5;
   }

   float getPointSize(int var1, StyleSheet var2) {
      var2 = this.getStyleSheet(var2);
      int[] var3 = var2 != null ? var2.getSizeMap() : StyleSheet.sizeMapDefault;
      --var1;
      if (var1 < 0) {
         return (float)var3[0];
      } else {
         return var1 > var3.length - 1 ? (float)var3[var3.length - 1] : (float)var3[var1];
      }
   }

   private void translateEmbeddedAttributes(AttributeSet var1, MutableAttributeSet var2) {
      Enumeration var3 = var1.getAttributeNames();
      if (var1.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.HR) {
         this.translateAttributes(HTML.Tag.HR, var1, var2);
      }

      while(var3.hasMoreElements()) {
         Object var4 = var3.nextElement();
         if (var4 instanceof HTML.Tag) {
            HTML.Tag var5 = (HTML.Tag)var4;
            Object var6 = var1.getAttribute(var5);
            if (var6 != null && var6 instanceof AttributeSet) {
               this.translateAttributes(var5, (AttributeSet)var6, var2);
            }
         } else if (var4 instanceof CSS.Attribute) {
            var2.addAttribute(var4, var1.getAttribute(var4));
         }
      }

   }

   private void translateAttributes(HTML.Tag var1, AttributeSet var2, MutableAttributeSet var3) {
      Enumeration var4 = var2.getAttributeNames();

      while(true) {
         while(true) {
            while(var4.hasMoreElements()) {
               Object var5 = var4.nextElement();
               if (var5 instanceof HTML.Attribute) {
                  HTML.Attribute var6 = (HTML.Attribute)var5;
                  if (var6 == HTML.Attribute.ALIGN) {
                     String var10 = (String)var2.getAttribute(HTML.Attribute.ALIGN);
                     if (var10 != null) {
                        CSS.Attribute var8 = this.getCssAlignAttribute(var1, var2);
                        if (var8 != null) {
                           Object var9 = this.getCssValue(var8, var10);
                           if (var9 != null) {
                              var3.addAttribute(var8, var9);
                           }
                        }
                     }
                  } else if (var6 != HTML.Attribute.SIZE || this.isHTMLFontTag(var1)) {
                     if (var1 == HTML.Tag.TABLE && var6 == HTML.Attribute.BORDER) {
                        int var7 = getTableBorder(var2);
                        if (var7 > 0) {
                           this.translateAttribute(HTML.Attribute.BORDER, Integer.toString(var7), var3);
                        }
                     } else {
                        this.translateAttribute(var6, (String)var2.getAttribute(var6), var3);
                     }
                  }
               } else if (var5 instanceof CSS.Attribute) {
                  var3.addAttribute(var5, var2.getAttribute(var5));
               }
            }

            return;
         }
      }
   }

   private void translateAttribute(HTML.Attribute var1, String var2, MutableAttributeSet var3) {
      CSS.Attribute[] var4 = this.getCssAttribute(var1);
      if (var4 != null && var2 != null) {
         CSS.Attribute[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            CSS.Attribute var8 = var5[var7];
            Object var9 = this.getCssValue(var8, var2);
            if (var9 != null) {
               var3.addAttribute(var8, var9);
            }
         }

      }
   }

   Object getCssValue(CSS.Attribute var1, String var2) {
      CSS.CssValue var3 = (CSS.CssValue)this.valueConvertor.get(var1);
      Object var4 = var3.parseHtmlValue(var2);
      return var4;
   }

   private CSS.Attribute[] getCssAttribute(HTML.Attribute var1) {
      return (CSS.Attribute[])htmlAttrToCssAttrMap.get(var1);
   }

   private CSS.Attribute getCssAlignAttribute(HTML.Tag var1, AttributeSet var2) {
      return CSS.Attribute.TEXT_ALIGN;
   }

   private HTML.Tag getHTMLTag(AttributeSet var1) {
      Object var2 = var1.getAttribute(StyleConstants.NameAttribute);
      if (var2 instanceof HTML.Tag) {
         HTML.Tag var3 = (HTML.Tag)var2;
         return var3;
      } else {
         return null;
      }
   }

   private boolean isHTMLFontTag(HTML.Tag var1) {
      return var1 != null && (var1 == HTML.Tag.FONT || var1 == HTML.Tag.BASEFONT);
   }

   private boolean isFloater(String var1) {
      return var1.equals("left") || var1.equals("right");
   }

   private boolean validTextAlignValue(String var1) {
      return this.isFloater(var1) || var1.equals("center");
   }

   static SizeRequirements calculateTiledRequirements(CSS.LayoutIterator var0, SizeRequirements var1) {
      long var2 = 0L;
      long var4 = 0L;
      long var6 = 0L;
      int var8 = 0;
      int var9 = 0;
      int var10 = var0.getCount();

      for(int var11 = 0; var11 < var10; ++var11) {
         var0.setIndex(var11);
         int var13 = (int)var0.getLeadingCollapseSpan();
         var9 += Math.max(var8, var13);
         var6 += (long)((int)var0.getPreferredSpan(0.0F));
         var2 = (long)((float)var2 + var0.getMinimumSpan(0.0F));
         var4 = (long)((float)var4 + var0.getMaximumSpan(0.0F));
         var8 = (int)var0.getTrailingCollapseSpan();
      }

      var9 += var8;
      var9 = (int)((float)var9 + 2.0F * var0.getBorderWidth());
      var2 += (long)var9;
      var6 += (long)var9;
      var4 += (long)var9;
      if (var1 == null) {
         var1 = new SizeRequirements();
      }

      var1.minimum = var2 > 2147483647L ? Integer.MAX_VALUE : (int)var2;
      var1.preferred = var6 > 2147483647L ? Integer.MAX_VALUE : (int)var6;
      var1.maximum = var4 > 2147483647L ? Integer.MAX_VALUE : (int)var4;
      return var1;
   }

   static void calculateTiledLayout(CSS.LayoutIterator var0, int var1) {
      long var2 = 0L;
      int var6 = 0;
      int var7 = 0;
      int var8 = var0.getCount();
      byte var9 = 3;
      long[] var10 = new long[var9];
      long[] var11 = new long[var9];

      int var12;
      for(var12 = 0; var12 < var9; ++var12) {
         var10[var12] = var11[var12] = 0L;
      }

      for(var12 = 0; var12 < var8; ++var12) {
         var0.setIndex(var12);
         int var14 = (int)var0.getLeadingCollapseSpan();
         var0.setOffset(Math.max(var6, var14));
         var7 += var0.getOffset();
         long var4 = (long)var0.getPreferredSpan((float)var1);
         var0.setSpan((int)var4);
         var2 += var4;
         int var10001 = var0.getAdjustmentWeight();
         var10[var10001] += (long)var0.getMaximumSpan((float)var1) - var4;
         var10001 = var0.getAdjustmentWeight();
         var11[var10001] += var4 - (long)var0.getMinimumSpan((float)var1);
         var6 = (int)var0.getTrailingCollapseSpan();
      }

      var7 += var6;
      var7 = (int)((float)var7 + 2.0F * var0.getBorderWidth());

      for(var12 = 1; var12 < var9; ++var12) {
         var10[var12] += var10[var12 - 1];
         var11[var12] += var11[var12 - 1];
      }

      var12 = var1 - var7;
      long var13 = (long)var12 - var2;
      long[] var15 = var13 > 0L ? var10 : var11;
      var13 = Math.abs(var13);

      int var16;
      for(var16 = 0; var16 <= 2 && var15[var16] < var13; ++var16) {
      }

      float var17 = 0.0F;
      if (var16 <= 2) {
         var13 -= var16 > 0 ? var15[var16 - 1] : 0L;
         if (var13 != 0L) {
            float var18 = (float)(var15[var16] - (var16 > 0 ? var15[var16 - 1] : 0L));
            var17 = (float)var13 / var18;
         }
      }

      int var26 = (int)var0.getBorderWidth();

      int var19;
      int var20;
      for(var19 = 0; var19 < var8; ++var19) {
         var0.setIndex(var19);
         var0.setOffset(var0.getOffset() + var26);
         if (var0.getAdjustmentWeight() < var16) {
            var0.setSpan((int)((long)var12 > var2 ? Math.floor((double)var0.getMaximumSpan((float)var1)) : Math.ceil((double)var0.getMinimumSpan((float)var1))));
         } else if (var0.getAdjustmentWeight() == var16) {
            var20 = (long)var12 > var2 ? (int)var0.getMaximumSpan((float)var1) - var0.getSpan() : var0.getSpan() - (int)var0.getMinimumSpan((float)var1);
            int var21 = (int)Math.floor((double)(var17 * (float)var20));
            var0.setSpan(var0.getSpan() + ((long)var12 > var2 ? var21 : -var21));
         }

         var26 = (int)Math.min((long)var0.getOffset() + (long)var0.getSpan(), 2147483647L);
      }

      var19 = var1 - var26 - (int)var0.getTrailingCollapseSpan() - (int)var0.getBorderWidth();
      var20 = var19 > 0 ? 1 : -1;
      var19 *= var20;
      boolean var27 = true;

      while(var19 > 0 && var27) {
         var27 = false;
         int var22 = 0;

         for(int var23 = 0; var23 < var8; ++var23) {
            var0.setIndex(var23);
            var0.setOffset(var0.getOffset() + var22);
            int var24 = var0.getSpan();
            if (var19 > 0) {
               int var25 = var20 > 0 ? (int)Math.floor((double)var0.getMaximumSpan((float)var1)) - var24 : var24 - (int)Math.ceil((double)var0.getMinimumSpan((float)var1));
               if (var25 >= 1) {
                  var27 = true;
                  var0.setSpan(var24 + var20);
                  var22 += var20;
                  --var19;
               }
            }
         }
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Enumeration var2 = this.valueConvertor.keys();
      var1.writeInt(this.valueConvertor.size());
      if (var2 != null) {
         while(var2.hasMoreElements()) {
            Object var3 = var2.nextElement();
            Object var4 = this.valueConvertor.get(var3);
            if (!(var3 instanceof Serializable) && (var3 = StyleContext.getStaticAttributeKey(var3)) == null) {
               var3 = null;
               var4 = null;
            } else if (!(var4 instanceof Serializable) && (var4 = StyleContext.getStaticAttributeKey(var4)) == null) {
               var3 = null;
               var4 = null;
            }

            var1.writeObject(var3);
            var1.writeObject(var4);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      this.valueConvertor = new Hashtable();

      while(var2-- > 0) {
         Object var3 = var1.readObject();
         Object var4 = var1.readObject();
         Object var5 = StyleContext.getStaticAttribute(var3);
         if (var5 != null) {
            var3 = var5;
         }

         Object var6 = StyleContext.getStaticAttribute(var4);
         if (var6 != null) {
            var4 = var6;
         }

         if (var3 != null && var4 != null) {
            this.valueConvertor.put(var3, var4);
         }
      }

   }

   private StyleSheet getStyleSheet(StyleSheet var1) {
      if (var1 != null) {
         this.styleSheet = var1;
      }

      return this.styleSheet;
   }

   static {
      int var0;
      for(var0 = 0; var0 < CSS.Attribute.allAttributes.length; ++var0) {
         attributeMap.put(CSS.Attribute.allAttributes[var0].toString(), CSS.Attribute.allAttributes[var0]);
      }

      for(var0 = 0; var0 < CSS.Value.allValues.length; ++var0) {
         valueMap.put(CSS.Value.allValues[var0].toString(), CSS.Value.allValues[var0]);
      }

      htmlAttrToCssAttrMap.put(HTML.Attribute.COLOR, new CSS.Attribute[]{CSS.Attribute.COLOR});
      htmlAttrToCssAttrMap.put(HTML.Attribute.TEXT, new CSS.Attribute[]{CSS.Attribute.COLOR});
      htmlAttrToCssAttrMap.put(HTML.Attribute.CLEAR, new CSS.Attribute[]{CSS.Attribute.CLEAR});
      htmlAttrToCssAttrMap.put(HTML.Attribute.BACKGROUND, new CSS.Attribute[]{CSS.Attribute.BACKGROUND_IMAGE});
      htmlAttrToCssAttrMap.put(HTML.Attribute.BGCOLOR, new CSS.Attribute[]{CSS.Attribute.BACKGROUND_COLOR});
      htmlAttrToCssAttrMap.put(HTML.Attribute.WIDTH, new CSS.Attribute[]{CSS.Attribute.WIDTH});
      htmlAttrToCssAttrMap.put(HTML.Attribute.HEIGHT, new CSS.Attribute[]{CSS.Attribute.HEIGHT});
      htmlAttrToCssAttrMap.put(HTML.Attribute.BORDER, new CSS.Attribute[]{CSS.Attribute.BORDER_TOP_WIDTH, CSS.Attribute.BORDER_RIGHT_WIDTH, CSS.Attribute.BORDER_BOTTOM_WIDTH, CSS.Attribute.BORDER_LEFT_WIDTH});
      htmlAttrToCssAttrMap.put(HTML.Attribute.CELLPADDING, new CSS.Attribute[]{CSS.Attribute.PADDING});
      htmlAttrToCssAttrMap.put(HTML.Attribute.CELLSPACING, new CSS.Attribute[]{CSS.Attribute.BORDER_SPACING});
      htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINWIDTH, new CSS.Attribute[]{CSS.Attribute.MARGIN_LEFT, CSS.Attribute.MARGIN_RIGHT});
      htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINHEIGHT, new CSS.Attribute[]{CSS.Attribute.MARGIN_TOP, CSS.Attribute.MARGIN_BOTTOM});
      htmlAttrToCssAttrMap.put(HTML.Attribute.HSPACE, new CSS.Attribute[]{CSS.Attribute.PADDING_LEFT, CSS.Attribute.PADDING_RIGHT});
      htmlAttrToCssAttrMap.put(HTML.Attribute.VSPACE, new CSS.Attribute[]{CSS.Attribute.PADDING_BOTTOM, CSS.Attribute.PADDING_TOP});
      htmlAttrToCssAttrMap.put(HTML.Attribute.FACE, new CSS.Attribute[]{CSS.Attribute.FONT_FAMILY});
      htmlAttrToCssAttrMap.put(HTML.Attribute.SIZE, new CSS.Attribute[]{CSS.Attribute.FONT_SIZE});
      htmlAttrToCssAttrMap.put(HTML.Attribute.VALIGN, new CSS.Attribute[]{CSS.Attribute.VERTICAL_ALIGN});
      htmlAttrToCssAttrMap.put(HTML.Attribute.ALIGN, new CSS.Attribute[]{CSS.Attribute.VERTICAL_ALIGN, CSS.Attribute.TEXT_ALIGN, CSS.Attribute.FLOAT});
      htmlAttrToCssAttrMap.put(HTML.Attribute.TYPE, new CSS.Attribute[]{CSS.Attribute.LIST_STYLE_TYPE});
      htmlAttrToCssAttrMap.put(HTML.Attribute.NOWRAP, new CSS.Attribute[]{CSS.Attribute.WHITE_SPACE});
      styleConstantToCssMap.put(StyleConstants.FontFamily, CSS.Attribute.FONT_FAMILY);
      styleConstantToCssMap.put(StyleConstants.FontSize, CSS.Attribute.FONT_SIZE);
      styleConstantToCssMap.put(StyleConstants.Bold, CSS.Attribute.FONT_WEIGHT);
      styleConstantToCssMap.put(StyleConstants.Italic, CSS.Attribute.FONT_STYLE);
      styleConstantToCssMap.put(StyleConstants.Underline, CSS.Attribute.TEXT_DECORATION);
      styleConstantToCssMap.put(StyleConstants.StrikeThrough, CSS.Attribute.TEXT_DECORATION);
      styleConstantToCssMap.put(StyleConstants.Superscript, CSS.Attribute.VERTICAL_ALIGN);
      styleConstantToCssMap.put(StyleConstants.Subscript, CSS.Attribute.VERTICAL_ALIGN);
      styleConstantToCssMap.put(StyleConstants.Foreground, CSS.Attribute.COLOR);
      styleConstantToCssMap.put(StyleConstants.Background, CSS.Attribute.BACKGROUND_COLOR);
      styleConstantToCssMap.put(StyleConstants.FirstLineIndent, CSS.Attribute.TEXT_INDENT);
      styleConstantToCssMap.put(StyleConstants.LeftIndent, CSS.Attribute.MARGIN_LEFT);
      styleConstantToCssMap.put(StyleConstants.RightIndent, CSS.Attribute.MARGIN_RIGHT);
      styleConstantToCssMap.put(StyleConstants.SpaceAbove, CSS.Attribute.MARGIN_TOP);
      styleConstantToCssMap.put(StyleConstants.SpaceBelow, CSS.Attribute.MARGIN_BOTTOM);
      styleConstantToCssMap.put(StyleConstants.Alignment, CSS.Attribute.TEXT_ALIGN);
      htmlValueToCssValueMap.put("disc", CSS.Value.DISC);
      htmlValueToCssValueMap.put("square", CSS.Value.SQUARE);
      htmlValueToCssValueMap.put("circle", CSS.Value.CIRCLE);
      htmlValueToCssValueMap.put("1", CSS.Value.DECIMAL);
      htmlValueToCssValueMap.put("a", CSS.Value.LOWER_ALPHA);
      htmlValueToCssValueMap.put("A", CSS.Value.UPPER_ALPHA);
      htmlValueToCssValueMap.put("i", CSS.Value.LOWER_ROMAN);
      htmlValueToCssValueMap.put("I", CSS.Value.UPPER_ROMAN);
      cssValueToInternalValueMap.put("none", CSS.Value.NONE);
      cssValueToInternalValueMap.put("disc", CSS.Value.DISC);
      cssValueToInternalValueMap.put("square", CSS.Value.SQUARE);
      cssValueToInternalValueMap.put("circle", CSS.Value.CIRCLE);
      cssValueToInternalValueMap.put("decimal", CSS.Value.DECIMAL);
      cssValueToInternalValueMap.put("lower-roman", CSS.Value.LOWER_ROMAN);
      cssValueToInternalValueMap.put("upper-roman", CSS.Value.UPPER_ROMAN);
      cssValueToInternalValueMap.put("lower-alpha", CSS.Value.LOWER_ALPHA);
      cssValueToInternalValueMap.put("upper-alpha", CSS.Value.UPPER_ALPHA);
      cssValueToInternalValueMap.put("repeat", CSS.Value.BACKGROUND_REPEAT);
      cssValueToInternalValueMap.put("no-repeat", CSS.Value.BACKGROUND_NO_REPEAT);
      cssValueToInternalValueMap.put("repeat-x", CSS.Value.BACKGROUND_REPEAT_X);
      cssValueToInternalValueMap.put("repeat-y", CSS.Value.BACKGROUND_REPEAT_Y);
      cssValueToInternalValueMap.put("scroll", CSS.Value.BACKGROUND_SCROLL);
      cssValueToInternalValueMap.put("fixed", CSS.Value.BACKGROUND_FIXED);
      CSS.Attribute[] var7 = CSS.Attribute.allAttributes;

      int var2;
      int var3;
      try {
         CSS.Attribute[] var1 = var7;
         var2 = var7.length;

         for(var3 = 0; var3 < var2; ++var3) {
            CSS.Attribute var4 = var1[var3];
            StyleContext.registerStaticAttributeKey(var4);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

      CSS.Value[] var9 = CSS.Value.allValues;

      try {
         CSS.Value[] var8 = var9;
         var2 = var9.length;

         for(var3 = 0; var3 < var2; ++var3) {
            CSS.Value var10 = var8[var3];
            StyleContext.registerStaticAttributeKey(var10);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      baseFontSizeIndex = 3;
   }

   interface LayoutIterator {
      int WorstAdjustmentWeight = 2;

      void setOffset(int var1);

      int getOffset();

      void setSpan(int var1);

      int getSpan();

      int getCount();

      void setIndex(int var1);

      float getMinimumSpan(float var1);

      float getPreferredSpan(float var1);

      float getMaximumSpan(float var1);

      int getAdjustmentWeight();

      float getBorderWidth();

      float getLeadingCollapseSpan();

      float getTrailingCollapseSpan();
   }

   static class ShorthandBorderParser {
      static CSS.Attribute[] keys;

      static void parseShorthandBorder(MutableAttributeSet var0, CSS.Attribute var1, String var2) {
         Object[] var3 = new Object[CSSBorder.PARSERS.length];
         String[] var4 = CSS.parseStrings(var2);
         String[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            boolean var9 = false;

            for(int var10 = 0; var10 < var3.length; ++var10) {
               Object var11 = CSSBorder.PARSERS[var10].parseCssValue(var8);
               if (var11 != null) {
                  if (var3[var10] == null) {
                     var3[var10] = var11;
                     var9 = true;
                  }
                  break;
               }
            }

            if (!var9) {
               return;
            }
         }

         int var12;
         for(var12 = 0; var12 < var3.length; ++var12) {
            if (var3[var12] == null) {
               var3[var12] = CSSBorder.DEFAULTS[var12];
            }
         }

         for(var12 = 0; var12 < keys.length; ++var12) {
            if (var1 == CSS.Attribute.BORDER || var1 == keys[var12]) {
               for(var6 = 0; var6 < var3.length; ++var6) {
                  var0.addAttribute(CSSBorder.ATTRIBUTES[var6][var12], var3[var6]);
               }
            }
         }

      }

      static {
         keys = new CSS.Attribute[]{CSS.Attribute.BORDER_TOP, CSS.Attribute.BORDER_RIGHT, CSS.Attribute.BORDER_BOTTOM, CSS.Attribute.BORDER_LEFT};
      }
   }

   static class ShorthandMarginParser {
      static void parseShorthandMargin(CSS var0, String var1, MutableAttributeSet var2, CSS.Attribute[] var3) {
         String[] var4 = CSS.parseStrings(var1);
         int var5 = var4.length;
         boolean var6 = false;
         int var7;
         switch(var5) {
         case 0:
            return;
         case 1:
            for(var7 = 0; var7 < 4; ++var7) {
               var0.addInternalCSSValue(var2, var3[var7], var4[0]);
            }

            return;
         case 2:
            var0.addInternalCSSValue(var2, var3[0], var4[0]);
            var0.addInternalCSSValue(var2, var3[2], var4[0]);
            var0.addInternalCSSValue(var2, var3[1], var4[1]);
            var0.addInternalCSSValue(var2, var3[3], var4[1]);
            break;
         case 3:
            var0.addInternalCSSValue(var2, var3[0], var4[0]);
            var0.addInternalCSSValue(var2, var3[1], var4[1]);
            var0.addInternalCSSValue(var2, var3[2], var4[2]);
            var0.addInternalCSSValue(var2, var3[3], var4[1]);
            break;
         default:
            for(var7 = 0; var7 < 4; ++var7) {
               var0.addInternalCSSValue(var2, var3[var7], var4[var7]);
            }
         }

      }
   }

   static class ShorthandBackgroundParser {
      static void parseShorthandBackground(CSS var0, String var1, MutableAttributeSet var2) {
         String[] var3 = CSS.parseStrings(var1);
         int var4 = var3.length;
         int var5 = 0;
         short var6 = 0;

         while(true) {
            while(var5 < var4) {
               String var7 = var3[var5++];
               if ((var6 & 1) == 0 && isImage(var7)) {
                  var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_IMAGE, var7);
                  var6 = (short)(var6 | 1);
               } else if ((var6 & 2) == 0 && isRepeat(var7)) {
                  var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_REPEAT, var7);
                  var6 = (short)(var6 | 2);
               } else if ((var6 & 4) == 0 && isAttachment(var7)) {
                  var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_ATTACHMENT, var7);
                  var6 = (short)(var6 | 4);
               } else if ((var6 & 8) == 0 && isPosition(var7)) {
                  if (var5 < var4 && isPosition(var3[var5])) {
                     var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_POSITION, var7 + " " + var3[var5++]);
                  } else {
                     var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_POSITION, var7);
                  }

                  var6 = (short)(var6 | 8);
               } else if ((var6 & 16) == 0 && isColor(var7)) {
                  var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_COLOR, var7);
                  var6 = (short)(var6 | 16);
               }
            }

            if ((var6 & 1) == 0) {
               var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_IMAGE, (String)null);
            }

            if ((var6 & 2) == 0) {
               var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_REPEAT, "repeat");
            }

            if ((var6 & 4) == 0) {
               var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_ATTACHMENT, "scroll");
            }

            if ((var6 & 8) == 0) {
               var0.addInternalCSSValue(var2, CSS.Attribute.BACKGROUND_POSITION, (String)null);
            }

            return;
         }
      }

      static boolean isImage(String var0) {
         return var0.startsWith("url(") && var0.endsWith(")");
      }

      static boolean isRepeat(String var0) {
         return var0.equals("repeat-x") || var0.equals("repeat-y") || var0.equals("repeat") || var0.equals("no-repeat");
      }

      static boolean isAttachment(String var0) {
         return var0.equals("fixed") || var0.equals("scroll");
      }

      static boolean isPosition(String var0) {
         return var0.equals("top") || var0.equals("bottom") || var0.equals("left") || var0.equals("right") || var0.equals("center") || var0.length() > 0 && Character.isDigit(var0.charAt(0));
      }

      static boolean isColor(String var0) {
         return CSS.stringToColor(var0) != null;
      }
   }

   static class ShorthandFontParser {
      static void parseShorthandFont(CSS var0, String var1, MutableAttributeSet var2) {
         String[] var3 = CSS.parseStrings(var1);
         int var4 = var3.length;
         int var5 = 0;
         short var6 = 0;
         int var7 = Math.min(3, var4);

         while(var5 < var7) {
            if ((var6 & 1) == 0 && isFontStyle(var3[var5])) {
               var0.addInternalCSSValue(var2, CSS.Attribute.FONT_STYLE, var3[var5++]);
               var6 = (short)(var6 | 1);
            } else if ((var6 & 2) == 0 && isFontVariant(var3[var5])) {
               var0.addInternalCSSValue(var2, CSS.Attribute.FONT_VARIANT, var3[var5++]);
               var6 = (short)(var6 | 2);
            } else if ((var6 & 4) == 0 && isFontWeight(var3[var5])) {
               var0.addInternalCSSValue(var2, CSS.Attribute.FONT_WEIGHT, var3[var5++]);
               var6 = (short)(var6 | 4);
            } else {
               if (!var3[var5].equals("normal")) {
                  break;
               }

               ++var5;
            }
         }

         if ((var6 & 1) == 0) {
            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_STYLE, "normal");
         }

         if ((var6 & 2) == 0) {
            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_VARIANT, "normal");
         }

         if ((var6 & 4) == 0) {
            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_WEIGHT, "normal");
         }

         String var8;
         if (var5 < var4) {
            var8 = var3[var5];
            int var9 = var8.indexOf(47);
            if (var9 != -1) {
               var8 = var8.substring(0, var9);
               var3[var5] = var3[var5].substring(var9);
            } else {
               ++var5;
            }

            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_SIZE, var8);
         } else {
            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_SIZE, "medium");
         }

         if (var5 < var4 && var3[var5].startsWith("/")) {
            var8 = null;
            if (var3[var5].equals("/")) {
               ++var5;
               if (var5 < var4) {
                  var8 = var3[var5++];
               }
            } else {
               var8 = var3[var5++].substring(1);
            }

            if (var8 != null) {
               var0.addInternalCSSValue(var2, CSS.Attribute.LINE_HEIGHT, var8);
            } else {
               var0.addInternalCSSValue(var2, CSS.Attribute.LINE_HEIGHT, "normal");
            }
         } else {
            var0.addInternalCSSValue(var2, CSS.Attribute.LINE_HEIGHT, "normal");
         }

         if (var5 < var4) {
            for(var8 = var3[var5++]; var5 < var4; var8 = var8 + " " + var3[var5++]) {
            }

            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_FAMILY, var8);
         } else {
            var0.addInternalCSSValue(var2, CSS.Attribute.FONT_FAMILY, "SansSerif");
         }

      }

      private static boolean isFontStyle(String var0) {
         return var0.equals("italic") || var0.equals("oblique");
      }

      private static boolean isFontVariant(String var0) {
         return var0.equals("small-caps");
      }

      private static boolean isFontWeight(String var0) {
         if (!var0.equals("bold") && !var0.equals("bolder") && !var0.equals("italic") && !var0.equals("lighter")) {
            return var0.length() == 3 && var0.charAt(0) >= '1' && var0.charAt(0) <= '9' && var0.charAt(1) == '0' && var0.charAt(2) == '0';
         } else {
            return true;
         }
      }
   }

   static class LengthUnit implements Serializable {
      static Hashtable<String, Float> lengthMapping = new Hashtable(6);
      static Hashtable<String, Float> w3cLengthMapping = new Hashtable(6);
      short type;
      float value;
      String units = null;
      static final short UNINITALIZED_LENGTH = 10;

      LengthUnit(String var1, short var2, float var3) {
         this.parse(var1, var2, var3);
      }

      void parse(String var1, short var2, float var3) {
         this.type = var2;
         this.value = var3;
         int var4 = var1.length();
         if (var4 > 0 && var1.charAt(var4 - 1) == '%') {
            try {
               this.value = Float.valueOf(var1.substring(0, var4 - 1)) / 100.0F;
               this.type = 1;
            } catch (NumberFormatException var11) {
            }
         }

         if (var4 >= 2) {
            this.units = var1.substring(var4 - 2, var4);
            Float var5 = (Float)lengthMapping.get(this.units);
            if (var5 != null) {
               try {
                  this.value = Float.valueOf(var1.substring(0, var4 - 2));
                  this.type = 0;
               } catch (NumberFormatException var10) {
               }
            } else if (!this.units.equals("em") && !this.units.equals("ex")) {
               if (var1.equals("larger")) {
                  this.value = 2.0F;
                  this.type = 2;
               } else if (var1.equals("smaller")) {
                  this.value = -2.0F;
                  this.type = 2;
               } else {
                  try {
                     this.value = Float.valueOf(var1);
                     this.type = 0;
                  } catch (NumberFormatException var8) {
                  }
               }
            } else {
               try {
                  this.value = Float.valueOf(var1.substring(0, var4 - 2));
                  this.type = 3;
               } catch (NumberFormatException var9) {
               }
            }
         } else if (var4 > 0) {
            try {
               this.value = Float.valueOf(var1);
               this.type = 0;
            } catch (NumberFormatException var7) {
            }
         }

      }

      float getValue(boolean var1) {
         Hashtable var2 = var1 ? w3cLengthMapping : lengthMapping;
         float var3 = 1.0F;
         if (this.units != null) {
            Float var4 = (Float)var2.get(this.units);
            if (var4 != null) {
               var3 = var4;
            }
         }

         return this.value * var3;
      }

      static float getValue(float var0, String var1, Boolean var2) {
         Hashtable var3 = var2 ? w3cLengthMapping : lengthMapping;
         float var4 = 1.0F;
         if (var1 != null) {
            Float var5 = (Float)var3.get(var1);
            if (var5 != null) {
               var4 = var5;
            }
         }

         return var0 * var4;
      }

      public String toString() {
         return this.type + " " + this.value;
      }

      static {
         lengthMapping.put("pt", new Float(1.0F));
         lengthMapping.put("px", new Float(1.3F));
         lengthMapping.put("mm", new Float(2.83464F));
         lengthMapping.put("cm", new Float(28.3464F));
         lengthMapping.put("pc", new Float(12.0F));
         lengthMapping.put("in", new Float(72.0F));
         int var0 = 72;

         try {
            var0 = Toolkit.getDefaultToolkit().getScreenResolution();
         } catch (HeadlessException var2) {
         }

         w3cLengthMapping.put("pt", new Float((float)var0 / 72.0F));
         w3cLengthMapping.put("px", new Float(1.0F));
         w3cLengthMapping.put("mm", new Float((float)var0 / 25.4F));
         w3cLengthMapping.put("cm", new Float((float)var0 / 2.54F));
         w3cLengthMapping.put("pc", new Float((float)var0 / 6.0F));
         w3cLengthMapping.put("in", new Float((float)var0));
      }
   }

   static class BackgroundImage extends CSS.CssValue {
      private boolean loadedImage;
      private ImageIcon image;

      Object parseCssValue(String var1) {
         CSS.BackgroundImage var2 = new CSS.BackgroundImage();
         var2.svalue = var1;
         return var2;
      }

      Object parseHtmlValue(String var1) {
         return this.parseCssValue(var1);
      }

      ImageIcon getImage(URL var1) {
         if (!this.loadedImage) {
            synchronized(this) {
               if (!this.loadedImage) {
                  URL var3 = CSS.getURL(var1, this.svalue);
                  this.loadedImage = true;
                  if (var3 != null) {
                     this.image = new ImageIcon();
                     Image var4 = Toolkit.getDefaultToolkit().createImage(var3);
                     if (var4 != null) {
                        this.image.setImage(var4);
                     }
                  }
               }
            }
         }

         return this.image;
      }
   }

   static class BackgroundPosition extends CSS.CssValue {
      float horizontalPosition;
      float verticalPosition;
      short relative;

      Object parseCssValue(String var1) {
         String[] var2 = CSS.parseStrings(var1);
         int var3 = var2.length;
         CSS.BackgroundPosition var4 = new CSS.BackgroundPosition();
         var4.relative = 5;
         var4.svalue = var1;
         if (var3 > 0) {
            short var5 = 0;
            int var6 = 0;

            while(true) {
               while(var6 < var3) {
                  String var7 = var2[var6++];
                  if (var7.equals("center")) {
                     var5 = (short)(var5 | 4);
                  } else {
                     if ((var5 & 1) == 0) {
                        if (var7.equals("top")) {
                           var5 = (short)(var5 | 1);
                        } else if (var7.equals("bottom")) {
                           var5 = (short)(var5 | 1);
                           var4.verticalPosition = 1.0F;
                           continue;
                        }
                     }

                     if ((var5 & 2) == 0) {
                        if (var7.equals("left")) {
                           var5 = (short)(var5 | 2);
                           var4.horizontalPosition = 0.0F;
                        } else if (var7.equals("right")) {
                           var5 = (short)(var5 | 2);
                           var4.horizontalPosition = 1.0F;
                        }
                     }
                  }
               }

               if (var5 != 0) {
                  if ((var5 & 1) == 1) {
                     if ((var5 & 2) == 0) {
                        var4.horizontalPosition = 0.5F;
                     }
                  } else if ((var5 & 2) == 2) {
                     var4.verticalPosition = 0.5F;
                  } else {
                     var4.horizontalPosition = var4.verticalPosition = 0.5F;
                  }
               } else {
                  CSS.LengthUnit var8 = new CSS.LengthUnit(var2[0], (short)0, 0.0F);
                  if (var8.type == 0) {
                     var4.horizontalPosition = var8.value;
                     var4.relative = (short)(1 ^ var4.relative);
                  } else if (var8.type == 1) {
                     var4.horizontalPosition = var8.value;
                  } else if (var8.type == 3) {
                     var4.horizontalPosition = var8.value;
                     var4.relative = (short)(1 ^ var4.relative | 2);
                  }

                  if (var3 > 1) {
                     var8 = new CSS.LengthUnit(var2[1], (short)0, 0.0F);
                     if (var8.type == 0) {
                        var4.verticalPosition = var8.value;
                        var4.relative = (short)(4 ^ var4.relative);
                     } else if (var8.type == 1) {
                        var4.verticalPosition = var8.value;
                     } else if (var8.type == 3) {
                        var4.verticalPosition = var8.value;
                        var4.relative = (short)(4 ^ var4.relative | 8);
                     }
                  } else {
                     var4.verticalPosition = 0.5F;
                  }
               }
               break;
            }
         }

         return var4;
      }

      boolean isHorizontalPositionRelativeToSize() {
         return (this.relative & 1) == 1;
      }

      boolean isHorizontalPositionRelativeToFontSize() {
         return (this.relative & 2) == 2;
      }

      float getHorizontalPosition() {
         return this.horizontalPosition;
      }

      boolean isVerticalPositionRelativeToSize() {
         return (this.relative & 4) == 4;
      }

      boolean isVerticalPositionRelativeToFontSize() {
         return (this.relative & 8) == 8;
      }

      float getVerticalPosition() {
         return this.verticalPosition;
      }
   }

   static class CssValueMapper extends CSS.CssValue {
      Object parseCssValue(String var1) {
         Object var2 = CSS.cssValueToInternalValueMap.get(var1);
         if (var2 == null) {
            var2 = CSS.cssValueToInternalValueMap.get(var1.toLowerCase());
         }

         return var2;
      }

      Object parseHtmlValue(String var1) {
         Object var2 = CSS.htmlValueToCssValueMap.get(var1);
         if (var2 == null) {
            var2 = CSS.htmlValueToCssValueMap.get(var1.toLowerCase());
         }

         return var2;
      }
   }

   static class BorderWidthValue extends CSS.LengthValue {
      private static final float[] values = new float[]{1.0F, 2.0F, 4.0F};

      BorderWidthValue(String var1, int var2) {
         this.svalue = var1;
         this.span = values[var2];
         this.percentage = false;
      }

      Object parseCssValue(String var1) {
         if (var1 != null) {
            if (var1.equals("thick")) {
               return new CSS.BorderWidthValue(var1, 2);
            }

            if (var1.equals("medium")) {
               return new CSS.BorderWidthValue(var1, 1);
            }

            if (var1.equals("thin")) {
               return new CSS.BorderWidthValue(var1, 0);
            }
         }

         return super.parseCssValue(var1);
      }

      Object parseHtmlValue(String var1) {
         return var1 == "#DEFAULT" ? this.parseCssValue("medium") : this.parseCssValue(var1);
      }
   }

   static class LengthValue extends CSS.CssValue {
      boolean mayBeNegative;
      boolean percentage;
      float span;
      String units;

      LengthValue() {
         this(false);
      }

      LengthValue(boolean var1) {
         this.units = null;
         this.mayBeNegative = var1;
      }

      float getValue() {
         return this.getValue(false);
      }

      float getValue(boolean var1) {
         return this.getValue(0.0F, var1);
      }

      float getValue(float var1) {
         return this.getValue(var1, false);
      }

      float getValue(float var1, boolean var2) {
         return this.percentage ? this.span * var1 : CSS.LengthUnit.getValue(this.span, this.units, var2);
      }

      boolean isPercentage() {
         return this.percentage;
      }

      Object parseCssValue(String var1) {
         CSS.LengthValue var2;
         try {
            float var3 = Float.valueOf(var1);
            var2 = new CSS.LengthValue();
            var2.span = var3;
         } catch (NumberFormatException var5) {
            CSS.LengthUnit var4 = new CSS.LengthUnit(var1, (short)10, 0.0F);
            switch(var4.type) {
            case 0:
               var2 = new CSS.LengthValue();
               var2.span = this.mayBeNegative ? var4.value : Math.max(0.0F, var4.value);
               var2.units = var4.units;
               break;
            case 1:
               var2 = new CSS.LengthValue();
               var2.span = Math.max(0.0F, Math.min(1.0F, var4.value));
               var2.percentage = true;
               break;
            default:
               return null;
            }
         }

         var2.svalue = var1;
         return var2;
      }

      Object parseHtmlValue(String var1) {
         if (var1.equals("#DEFAULT")) {
            var1 = "1";
         }

         return this.parseCssValue(var1);
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         CSS.LengthValue var3 = new CSS.LengthValue();
         var3.svalue = var2.toString();
         var3.span = (Float)var2;
         return var3;
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return new Float(this.getValue(false));
      }
   }

   static class BorderStyle extends CSS.CssValue {
      private transient CSS.Value style;

      CSS.Value getValue() {
         return this.style;
      }

      Object parseCssValue(String var1) {
         CSS.Value var2 = CSS.getValue(var1);
         if (var2 == null || var2 != CSS.Value.INSET && var2 != CSS.Value.OUTSET && var2 != CSS.Value.NONE && var2 != CSS.Value.DOTTED && var2 != CSS.Value.DASHED && var2 != CSS.Value.SOLID && var2 != CSS.Value.DOUBLE && var2 != CSS.Value.GROOVE && var2 != CSS.Value.RIDGE) {
            return null;
         } else {
            CSS.BorderStyle var3 = new CSS.BorderStyle();
            var3.svalue = var1;
            var3.style = var2;
            return var3;
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         if (this.style == null) {
            var1.writeObject((Object)null);
         } else {
            var1.writeObject(this.style.toString());
         }

      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
         Object var2 = var1.readObject();
         if (var2 != null) {
            this.style = CSS.getValue((String)var2);
         }

      }
   }

   static class ColorValue extends CSS.CssValue {
      Color c;

      Color getValue() {
         return this.c;
      }

      Object parseCssValue(String var1) {
         Color var2 = CSS.stringToColor(var1);
         if (var2 != null) {
            CSS.ColorValue var3 = new CSS.ColorValue();
            var3.svalue = var1;
            var3.c = var2;
            return var3;
         } else {
            return null;
         }
      }

      Object parseHtmlValue(String var1) {
         return this.parseCssValue(var1);
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         CSS.ColorValue var3 = new CSS.ColorValue();
         var3.c = (Color)var2;
         var3.svalue = CSS.colorToHex(var3.c);
         return var3;
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return this.c;
      }
   }

   static class FontWeight extends CSS.CssValue {
      int weight;

      int getValue() {
         return this.weight;
      }

      Object parseCssValue(String var1) {
         CSS.FontWeight var2 = new CSS.FontWeight();
         var2.svalue = var1;
         if (var1.equals("bold")) {
            var2.weight = 700;
         } else if (var1.equals("normal")) {
            var2.weight = 400;
         } else {
            try {
               var2.weight = Integer.parseInt(var1);
            } catch (NumberFormatException var4) {
               var2 = null;
            }
         }

         return var2;
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         return var2.equals(Boolean.TRUE) ? this.parseCssValue("bold") : this.parseCssValue("normal");
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return this.weight > 500 ? Boolean.TRUE : Boolean.FALSE;
      }

      boolean isBold() {
         return this.weight > 500;
      }
   }

   static class FontFamily extends CSS.CssValue {
      String family;

      String getValue() {
         return this.family;
      }

      Object parseCssValue(String var1) {
         int var2 = var1.indexOf(44);
         CSS.FontFamily var3 = new CSS.FontFamily();
         var3.svalue = var1;
         var3.family = null;
         if (var2 == -1) {
            this.setFontName(var3, var1);
         } else {
            boolean var4 = false;
            int var6 = var1.length();
            var2 = 0;

            while(!var4) {
               while(var2 < var6 && Character.isWhitespace(var1.charAt(var2))) {
                  ++var2;
               }

               int var5 = var2;
               var2 = var1.indexOf(44, var2);
               if (var2 == -1) {
                  var2 = var6;
               }

               if (var5 < var6) {
                  if (var5 != var2) {
                     int var7 = var2;
                     if (var2 > 0 && var1.charAt(var2 - 1) == ' ') {
                        var7 = var2 - 1;
                     }

                     this.setFontName(var3, var1.substring(var5, var7));
                     var4 = var3.family != null;
                  }

                  ++var2;
               } else {
                  var4 = true;
               }
            }
         }

         if (var3.family == null) {
            var3.family = "SansSerif";
         }

         return var3;
      }

      private void setFontName(CSS.FontFamily var1, String var2) {
         var1.family = var2;
      }

      Object parseHtmlValue(String var1) {
         return this.parseCssValue(var1);
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         return this.parseCssValue(var2.toString());
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return this.family;
      }
   }

   class FontSize extends CSS.CssValue {
      float value;
      boolean index;
      CSS.LengthUnit lu;

      int getValue(AttributeSet var1, StyleSheet var2) {
         var2 = CSS.this.getStyleSheet(var2);
         if (this.index) {
            return Math.round(CSS.this.getPointSize((int)this.value, var2));
         } else if (this.lu == null) {
            return Math.round(this.value);
         } else if (this.lu.type == 0) {
            boolean var6 = var2 == null ? false : var2.isW3CLengthUnits();
            return Math.round(this.lu.getValue(var6));
         } else {
            if (var1 != null) {
               AttributeSet var3 = var1.getResolveParent();
               if (var3 != null) {
                  int var4 = StyleConstants.getFontSize(var3);
                  float var5;
                  if (this.lu.type != 1 && this.lu.type != 3) {
                     var5 = this.lu.value + (float)var4;
                  } else {
                     var5 = this.lu.value * (float)var4;
                  }

                  return Math.round(var5);
               }
            }

            return 12;
         }
      }

      Object parseCssValue(String var1) {
         CSS.FontSize var2 = CSS.this.new FontSize();
         var2.svalue = var1;

         try {
            if (var1.equals("xx-small")) {
               var2.value = 1.0F;
               var2.index = true;
            } else if (var1.equals("x-small")) {
               var2.value = 2.0F;
               var2.index = true;
            } else if (var1.equals("small")) {
               var2.value = 3.0F;
               var2.index = true;
            } else if (var1.equals("medium")) {
               var2.value = 4.0F;
               var2.index = true;
            } else if (var1.equals("large")) {
               var2.value = 5.0F;
               var2.index = true;
            } else if (var1.equals("x-large")) {
               var2.value = 6.0F;
               var2.index = true;
            } else if (var1.equals("xx-large")) {
               var2.value = 7.0F;
               var2.index = true;
            } else {
               var2.lu = new CSS.LengthUnit(var1, (short)1, 1.0F);
            }
         } catch (NumberFormatException var4) {
            var2 = null;
         }

         return var2;
      }

      Object parseHtmlValue(String var1) {
         if (var1 != null && var1.length() != 0) {
            CSS.FontSize var2 = CSS.this.new FontSize();
            var2.svalue = var1;

            try {
               int var3 = CSS.this.getBaseFontSize();
               int var4;
               if (var1.charAt(0) == '+') {
                  var4 = Integer.valueOf(var1.substring(1));
                  var2.value = (float)(var3 + var4);
                  var2.index = true;
               } else if (var1.charAt(0) == '-') {
                  var4 = -Integer.valueOf(var1.substring(1));
                  var2.value = (float)(var3 + var4);
                  var2.index = true;
               } else {
                  var2.value = (float)Integer.parseInt(var1);
                  if (var2.value > 7.0F) {
                     var2.value = 7.0F;
                  } else if (var2.value < 0.0F) {
                     var2.value = 0.0F;
                  }

                  var2.index = true;
               }
            } catch (NumberFormatException var5) {
               var2 = null;
            }

            return var2;
         } else {
            return null;
         }
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         if (var2 instanceof Number) {
            CSS.FontSize var3 = CSS.this.new FontSize();
            var3.value = (float)CSS.getIndexOfSize(((Number)var2).floatValue(), StyleSheet.sizeMapDefault);
            var3.svalue = Integer.toString((int)var3.value);
            var3.index = true;
            return var3;
         } else {
            return this.parseCssValue(var2.toString());
         }
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return var2 != null ? this.getValue(var2.getAttributes(), (StyleSheet)null) : this.getValue((AttributeSet)null, (StyleSheet)null);
      }
   }

   static class StringValue extends CSS.CssValue {
      Object parseCssValue(String var1) {
         CSS.StringValue var2 = new CSS.StringValue();
         var2.svalue = var1;
         return var2;
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         if (var1 == StyleConstants.Italic) {
            return var2.equals(Boolean.TRUE) ? this.parseCssValue("italic") : this.parseCssValue("");
         } else if (var1 == StyleConstants.Underline) {
            return var2.equals(Boolean.TRUE) ? this.parseCssValue("underline") : this.parseCssValue("");
         } else if (var1 == StyleConstants.Alignment) {
            int var3 = (Integer)var2;
            String var4;
            switch(var3) {
            case 0:
               var4 = "left";
               break;
            case 1:
               var4 = "center";
               break;
            case 2:
               var4 = "right";
               break;
            case 3:
               var4 = "justify";
               break;
            default:
               var4 = "left";
            }

            return this.parseCssValue(var4);
         } else if (var1 == StyleConstants.StrikeThrough) {
            return var2.equals(Boolean.TRUE) ? this.parseCssValue("line-through") : this.parseCssValue("");
         } else if (var1 == StyleConstants.Superscript) {
            return var2.equals(Boolean.TRUE) ? this.parseCssValue("super") : this.parseCssValue("");
         } else if (var1 == StyleConstants.Subscript) {
            return var2.equals(Boolean.TRUE) ? this.parseCssValue("sub") : this.parseCssValue("");
         } else {
            return null;
         }
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         if (var1 == StyleConstants.Italic) {
            return this.svalue.indexOf("italic") >= 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (var1 == StyleConstants.Underline) {
            return this.svalue.indexOf("underline") >= 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (var1 == StyleConstants.Alignment) {
            if (this.svalue.equals("right")) {
               return new Integer(2);
            } else if (this.svalue.equals("center")) {
               return new Integer(1);
            } else {
               return this.svalue.equals("justify") ? new Integer(3) : new Integer(0);
            }
         } else if (var1 == StyleConstants.StrikeThrough) {
            return this.svalue.indexOf("line-through") >= 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (var1 == StyleConstants.Superscript) {
            return this.svalue.indexOf("super") >= 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (var1 == StyleConstants.Subscript) {
            return this.svalue.indexOf("sub") >= 0 ? Boolean.TRUE : Boolean.FALSE;
         } else {
            return null;
         }
      }

      boolean isItalic() {
         return this.svalue.indexOf("italic") != -1;
      }

      boolean isStrike() {
         return this.svalue.indexOf("line-through") != -1;
      }

      boolean isUnderline() {
         return this.svalue.indexOf("underline") != -1;
      }

      boolean isSub() {
         return this.svalue.indexOf("sub") != -1;
      }

      boolean isSup() {
         return this.svalue.indexOf("sup") != -1;
      }
   }

   static class CssValue implements Serializable {
      String svalue;

      Object parseCssValue(String var1) {
         return var1;
      }

      Object parseHtmlValue(String var1) {
         return this.parseCssValue(var1);
      }

      Object fromStyleConstants(StyleConstants var1, Object var2) {
         return null;
      }

      Object toStyleConstants(StyleConstants var1, View var2) {
         return null;
      }

      public String toString() {
         return this.svalue;
      }
   }

   static final class Value {
      static final CSS.Value INHERITED = new CSS.Value("inherited");
      static final CSS.Value NONE = new CSS.Value("none");
      static final CSS.Value HIDDEN = new CSS.Value("hidden");
      static final CSS.Value DOTTED = new CSS.Value("dotted");
      static final CSS.Value DASHED = new CSS.Value("dashed");
      static final CSS.Value SOLID = new CSS.Value("solid");
      static final CSS.Value DOUBLE = new CSS.Value("double");
      static final CSS.Value GROOVE = new CSS.Value("groove");
      static final CSS.Value RIDGE = new CSS.Value("ridge");
      static final CSS.Value INSET = new CSS.Value("inset");
      static final CSS.Value OUTSET = new CSS.Value("outset");
      static final CSS.Value DISC = new CSS.Value("disc");
      static final CSS.Value CIRCLE = new CSS.Value("circle");
      static final CSS.Value SQUARE = new CSS.Value("square");
      static final CSS.Value DECIMAL = new CSS.Value("decimal");
      static final CSS.Value LOWER_ROMAN = new CSS.Value("lower-roman");
      static final CSS.Value UPPER_ROMAN = new CSS.Value("upper-roman");
      static final CSS.Value LOWER_ALPHA = new CSS.Value("lower-alpha");
      static final CSS.Value UPPER_ALPHA = new CSS.Value("upper-alpha");
      static final CSS.Value BACKGROUND_NO_REPEAT = new CSS.Value("no-repeat");
      static final CSS.Value BACKGROUND_REPEAT = new CSS.Value("repeat");
      static final CSS.Value BACKGROUND_REPEAT_X = new CSS.Value("repeat-x");
      static final CSS.Value BACKGROUND_REPEAT_Y = new CSS.Value("repeat-y");
      static final CSS.Value BACKGROUND_SCROLL = new CSS.Value("scroll");
      static final CSS.Value BACKGROUND_FIXED = new CSS.Value("fixed");
      private String name;
      static final CSS.Value[] allValues;

      private Value(String var1) {
         this.name = var1;
      }

      public String toString() {
         return this.name;
      }

      static {
         allValues = new CSS.Value[]{INHERITED, NONE, DOTTED, DASHED, SOLID, DOUBLE, GROOVE, RIDGE, INSET, OUTSET, DISC, CIRCLE, SQUARE, DECIMAL, LOWER_ROMAN, UPPER_ROMAN, LOWER_ALPHA, UPPER_ALPHA, BACKGROUND_NO_REPEAT, BACKGROUND_REPEAT, BACKGROUND_REPEAT_X, BACKGROUND_REPEAT_Y, BACKGROUND_FIXED, BACKGROUND_FIXED};
      }
   }

   public static final class Attribute {
      private String name;
      private String defaultValue;
      private boolean inherited;
      public static final CSS.Attribute BACKGROUND = new CSS.Attribute("background", (String)null, false);
      public static final CSS.Attribute BACKGROUND_ATTACHMENT = new CSS.Attribute("background-attachment", "scroll", false);
      public static final CSS.Attribute BACKGROUND_COLOR = new CSS.Attribute("background-color", "transparent", false);
      public static final CSS.Attribute BACKGROUND_IMAGE = new CSS.Attribute("background-image", "none", false);
      public static final CSS.Attribute BACKGROUND_POSITION = new CSS.Attribute("background-position", (String)null, false);
      public static final CSS.Attribute BACKGROUND_REPEAT = new CSS.Attribute("background-repeat", "repeat", false);
      public static final CSS.Attribute BORDER = new CSS.Attribute("border", (String)null, false);
      public static final CSS.Attribute BORDER_BOTTOM = new CSS.Attribute("border-bottom", (String)null, false);
      public static final CSS.Attribute BORDER_BOTTOM_COLOR = new CSS.Attribute("border-bottom-color", (String)null, false);
      public static final CSS.Attribute BORDER_BOTTOM_STYLE = new CSS.Attribute("border-bottom-style", "none", false);
      public static final CSS.Attribute BORDER_BOTTOM_WIDTH = new CSS.Attribute("border-bottom-width", "medium", false);
      public static final CSS.Attribute BORDER_COLOR = new CSS.Attribute("border-color", (String)null, false);
      public static final CSS.Attribute BORDER_LEFT = new CSS.Attribute("border-left", (String)null, false);
      public static final CSS.Attribute BORDER_LEFT_COLOR = new CSS.Attribute("border-left-color", (String)null, false);
      public static final CSS.Attribute BORDER_LEFT_STYLE = new CSS.Attribute("border-left-style", "none", false);
      public static final CSS.Attribute BORDER_LEFT_WIDTH = new CSS.Attribute("border-left-width", "medium", false);
      public static final CSS.Attribute BORDER_RIGHT = new CSS.Attribute("border-right", (String)null, false);
      public static final CSS.Attribute BORDER_RIGHT_COLOR = new CSS.Attribute("border-right-color", (String)null, false);
      public static final CSS.Attribute BORDER_RIGHT_STYLE = new CSS.Attribute("border-right-style", "none", false);
      public static final CSS.Attribute BORDER_RIGHT_WIDTH = new CSS.Attribute("border-right-width", "medium", false);
      public static final CSS.Attribute BORDER_STYLE = new CSS.Attribute("border-style", "none", false);
      public static final CSS.Attribute BORDER_TOP = new CSS.Attribute("border-top", (String)null, false);
      public static final CSS.Attribute BORDER_TOP_COLOR = new CSS.Attribute("border-top-color", (String)null, false);
      public static final CSS.Attribute BORDER_TOP_STYLE = new CSS.Attribute("border-top-style", "none", false);
      public static final CSS.Attribute BORDER_TOP_WIDTH = new CSS.Attribute("border-top-width", "medium", false);
      public static final CSS.Attribute BORDER_WIDTH = new CSS.Attribute("border-width", "medium", false);
      public static final CSS.Attribute CLEAR = new CSS.Attribute("clear", "none", false);
      public static final CSS.Attribute COLOR = new CSS.Attribute("color", "black", true);
      public static final CSS.Attribute DISPLAY = new CSS.Attribute("display", "block", false);
      public static final CSS.Attribute FLOAT = new CSS.Attribute("float", "none", false);
      public static final CSS.Attribute FONT = new CSS.Attribute("font", (String)null, true);
      public static final CSS.Attribute FONT_FAMILY = new CSS.Attribute("font-family", (String)null, true);
      public static final CSS.Attribute FONT_SIZE = new CSS.Attribute("font-size", "medium", true);
      public static final CSS.Attribute FONT_STYLE = new CSS.Attribute("font-style", "normal", true);
      public static final CSS.Attribute FONT_VARIANT = new CSS.Attribute("font-variant", "normal", true);
      public static final CSS.Attribute FONT_WEIGHT = new CSS.Attribute("font-weight", "normal", true);
      public static final CSS.Attribute HEIGHT = new CSS.Attribute("height", "auto", false);
      public static final CSS.Attribute LETTER_SPACING = new CSS.Attribute("letter-spacing", "normal", true);
      public static final CSS.Attribute LINE_HEIGHT = new CSS.Attribute("line-height", "normal", true);
      public static final CSS.Attribute LIST_STYLE = new CSS.Attribute("list-style", (String)null, true);
      public static final CSS.Attribute LIST_STYLE_IMAGE = new CSS.Attribute("list-style-image", "none", true);
      public static final CSS.Attribute LIST_STYLE_POSITION = new CSS.Attribute("list-style-position", "outside", true);
      public static final CSS.Attribute LIST_STYLE_TYPE = new CSS.Attribute("list-style-type", "disc", true);
      public static final CSS.Attribute MARGIN = new CSS.Attribute("margin", (String)null, false);
      public static final CSS.Attribute MARGIN_BOTTOM = new CSS.Attribute("margin-bottom", "0", false);
      public static final CSS.Attribute MARGIN_LEFT = new CSS.Attribute("margin-left", "0", false);
      public static final CSS.Attribute MARGIN_RIGHT = new CSS.Attribute("margin-right", "0", false);
      static final CSS.Attribute MARGIN_LEFT_LTR = new CSS.Attribute("margin-left-ltr", Integer.toString(Integer.MIN_VALUE), false);
      static final CSS.Attribute MARGIN_LEFT_RTL = new CSS.Attribute("margin-left-rtl", Integer.toString(Integer.MIN_VALUE), false);
      static final CSS.Attribute MARGIN_RIGHT_LTR = new CSS.Attribute("margin-right-ltr", Integer.toString(Integer.MIN_VALUE), false);
      static final CSS.Attribute MARGIN_RIGHT_RTL = new CSS.Attribute("margin-right-rtl", Integer.toString(Integer.MIN_VALUE), false);
      public static final CSS.Attribute MARGIN_TOP = new CSS.Attribute("margin-top", "0", false);
      public static final CSS.Attribute PADDING = new CSS.Attribute("padding", (String)null, false);
      public static final CSS.Attribute PADDING_BOTTOM = new CSS.Attribute("padding-bottom", "0", false);
      public static final CSS.Attribute PADDING_LEFT = new CSS.Attribute("padding-left", "0", false);
      public static final CSS.Attribute PADDING_RIGHT = new CSS.Attribute("padding-right", "0", false);
      public static final CSS.Attribute PADDING_TOP = new CSS.Attribute("padding-top", "0", false);
      public static final CSS.Attribute TEXT_ALIGN = new CSS.Attribute("text-align", (String)null, true);
      public static final CSS.Attribute TEXT_DECORATION = new CSS.Attribute("text-decoration", "none", true);
      public static final CSS.Attribute TEXT_INDENT = new CSS.Attribute("text-indent", "0", true);
      public static final CSS.Attribute TEXT_TRANSFORM = new CSS.Attribute("text-transform", "none", true);
      public static final CSS.Attribute VERTICAL_ALIGN = new CSS.Attribute("vertical-align", "baseline", false);
      public static final CSS.Attribute WORD_SPACING = new CSS.Attribute("word-spacing", "normal", true);
      public static final CSS.Attribute WHITE_SPACE = new CSS.Attribute("white-space", "normal", true);
      public static final CSS.Attribute WIDTH = new CSS.Attribute("width", "auto", false);
      static final CSS.Attribute BORDER_SPACING = new CSS.Attribute("border-spacing", "0", true);
      static final CSS.Attribute CAPTION_SIDE = new CSS.Attribute("caption-side", "left", true);
      static final CSS.Attribute[] allAttributes;
      private static final CSS.Attribute[] ALL_MARGINS;
      private static final CSS.Attribute[] ALL_PADDING;
      private static final CSS.Attribute[] ALL_BORDER_WIDTHS;
      private static final CSS.Attribute[] ALL_BORDER_STYLES;
      private static final CSS.Attribute[] ALL_BORDER_COLORS;

      private Attribute(String var1, String var2, boolean var3) {
         this.name = var1;
         this.defaultValue = var2;
         this.inherited = var3;
      }

      public String toString() {
         return this.name;
      }

      public String getDefaultValue() {
         return this.defaultValue;
      }

      public boolean isInherited() {
         return this.inherited;
      }

      static {
         allAttributes = new CSS.Attribute[]{BACKGROUND, BACKGROUND_ATTACHMENT, BACKGROUND_COLOR, BACKGROUND_IMAGE, BACKGROUND_POSITION, BACKGROUND_REPEAT, BORDER, BORDER_BOTTOM, BORDER_BOTTOM_WIDTH, BORDER_COLOR, BORDER_LEFT, BORDER_LEFT_WIDTH, BORDER_RIGHT, BORDER_RIGHT_WIDTH, BORDER_STYLE, BORDER_TOP, BORDER_TOP_WIDTH, BORDER_WIDTH, BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE, BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR, CLEAR, COLOR, DISPLAY, FLOAT, FONT, FONT_FAMILY, FONT_SIZE, FONT_STYLE, FONT_VARIANT, FONT_WEIGHT, HEIGHT, LETTER_SPACING, LINE_HEIGHT, LIST_STYLE, LIST_STYLE_IMAGE, LIST_STYLE_POSITION, LIST_STYLE_TYPE, MARGIN, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, PADDING, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT, PADDING_TOP, TEXT_ALIGN, TEXT_DECORATION, TEXT_INDENT, TEXT_TRANSFORM, VERTICAL_ALIGN, WORD_SPACING, WHITE_SPACE, WIDTH, BORDER_SPACING, CAPTION_SIDE, MARGIN_LEFT_LTR, MARGIN_LEFT_RTL, MARGIN_RIGHT_LTR, MARGIN_RIGHT_RTL};
         ALL_MARGINS = new CSS.Attribute[]{MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT};
         ALL_PADDING = new CSS.Attribute[]{PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT};
         ALL_BORDER_WIDTHS = new CSS.Attribute[]{BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH};
         ALL_BORDER_STYLES = new CSS.Attribute[]{BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE};
         ALL_BORDER_COLORS = new CSS.Attribute[]{BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR};
      }
   }
}
