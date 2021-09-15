package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class StyleSheet extends StyleContext {
   static final Border noBorder = new EmptyBorder(0, 0, 0, 0);
   static final int DEFAULT_FONT_SIZE = 3;
   private CSS css;
   private StyleSheet.SelectorMapping selectorMapping;
   private Hashtable<String, StyleSheet.ResolvedStyle> resolvedStyles;
   private Vector<StyleSheet> linkedStyleSheets;
   private URL base;
   static final int[] sizeMapDefault = new int[]{8, 10, 12, 14, 18, 24, 36};
   private int[] sizeMap;
   private boolean w3cLengthUnits;

   public StyleSheet() {
      this.sizeMap = sizeMapDefault;
      this.w3cLengthUnits = false;
      this.selectorMapping = new StyleSheet.SelectorMapping(0);
      this.resolvedStyles = new Hashtable();
      if (this.css == null) {
         this.css = new CSS();
      }

   }

   public Style getRule(HTML.Tag var1, Element var2) {
      StyleSheet.SearchBuffer var3 = StyleSheet.SearchBuffer.obtainSearchBuffer();

      Style var11;
      try {
         Vector var4 = var3.getVector();

         for(Element var5 = var2; var5 != null; var5 = var5.getParentElement()) {
            var4.addElement(var5);
         }

         int var15 = var4.size();
         StringBuffer var6 = var3.getStringBuffer();

         AttributeSet var7;
         for(int var10 = var15 - 1; var10 >= 1; --var10) {
            var2 = (Element)var4.elementAt(var10);
            var7 = var2.getAttributes();
            Object var9 = var7.getAttribute(StyleConstants.NameAttribute);
            String var8 = var9.toString();
            var6.append(var8);
            if (var7 != null) {
               if (var7.isDefined(HTML.Attribute.ID)) {
                  var6.append('#');
                  var6.append(var7.getAttribute(HTML.Attribute.ID));
               } else if (var7.isDefined(HTML.Attribute.CLASS)) {
                  var6.append('.');
                  var6.append(var7.getAttribute(HTML.Attribute.CLASS));
               }
            }

            var6.append(' ');
         }

         var6.append(var1.toString());
         var2 = (Element)var4.elementAt(0);
         var7 = var2.getAttributes();
         if (var2.isLeaf()) {
            Object var16 = var7.getAttribute(var1);
            if (var16 instanceof AttributeSet) {
               var7 = (AttributeSet)var16;
            } else {
               var7 = null;
            }
         }

         if (var7 != null) {
            if (var7.isDefined(HTML.Attribute.ID)) {
               var6.append('#');
               var6.append(var7.getAttribute(HTML.Attribute.ID));
            } else if (var7.isDefined(HTML.Attribute.CLASS)) {
               var6.append('.');
               var6.append(var7.getAttribute(HTML.Attribute.CLASS));
            }
         }

         Style var17 = this.getResolvedStyle(var6.toString(), var4, var1);
         var11 = var17;
      } finally {
         StyleSheet.SearchBuffer.releaseSearchBuffer(var3);
      }

      return var11;
   }

   public Style getRule(String var1) {
      var1 = this.cleanSelectorString(var1);
      if (var1 != null) {
         Style var2 = this.getResolvedStyle(var1);
         return var2;
      } else {
         return null;
      }
   }

   public void addRule(String var1) {
      if (var1 != null) {
         if (var1 == "BASE_SIZE_DISABLE") {
            this.sizeMap = sizeMapDefault;
         } else if (var1.startsWith("BASE_SIZE ")) {
            this.rebaseSizeMap(Integer.parseInt(var1.substring("BASE_SIZE ".length())));
         } else if (var1 == "W3C_LENGTH_UNITS_ENABLE") {
            this.w3cLengthUnits = true;
         } else if (var1 == "W3C_LENGTH_UNITS_DISABLE") {
            this.w3cLengthUnits = false;
         } else {
            StyleSheet.CssParser var6 = new StyleSheet.CssParser();

            try {
               var6.parse(this.getBase(), new StringReader(var1), false, false);
            } catch (IOException var8) {
            }
         }
      }

   }

   public AttributeSet getDeclaration(String var1) {
      if (var1 == null) {
         return SimpleAttributeSet.EMPTY;
      } else {
         StyleSheet.CssParser var2 = new StyleSheet.CssParser();
         return var2.parseDeclaration(var1);
      }
   }

   public void loadRules(Reader var1, URL var2) throws IOException {
      StyleSheet.CssParser var3 = new StyleSheet.CssParser();
      var3.parse(var2, var1, false, false);
   }

   public AttributeSet getViewAttributes(View var1) {
      return new StyleSheet.ViewAttributeSet(var1);
   }

   public void removeStyle(String var1) {
      Style var2 = this.getStyle(var1);
      if (var2 != null) {
         String var3 = this.cleanSelectorString(var1);
         String[] var4 = this.getSimpleSelectors(var3);
         synchronized(this) {
            StyleSheet.SelectorMapping var6 = this.getRootSelectorMapping();

            for(int var7 = var4.length - 1; var7 >= 0; --var7) {
               var6 = var6.getChildSelectorMapping(var4[var7], true);
            }

            Style var12 = var6.getStyle();
            if (var12 != null) {
               var6.setStyle((Style)null);
               if (this.resolvedStyles.size() > 0) {
                  Enumeration var8 = this.resolvedStyles.elements();

                  while(var8.hasMoreElements()) {
                     StyleSheet.ResolvedStyle var9 = (StyleSheet.ResolvedStyle)var8.nextElement();
                     var9.removeStyle(var12);
                  }
               }
            }
         }
      }

      super.removeStyle(var1);
   }

   public void addStyleSheet(StyleSheet var1) {
      synchronized(this) {
         if (this.linkedStyleSheets == null) {
            this.linkedStyleSheets = new Vector();
         }

         if (!this.linkedStyleSheets.contains(var1)) {
            int var3 = 0;
            if (var1 instanceof UIResource && this.linkedStyleSheets.size() > 1) {
               var3 = this.linkedStyleSheets.size() - 1;
            }

            this.linkedStyleSheets.insertElementAt(var1, var3);
            this.linkStyleSheetAt(var1, var3);
         }

      }
   }

   public void removeStyleSheet(StyleSheet var1) {
      synchronized(this) {
         if (this.linkedStyleSheets != null) {
            int var3 = this.linkedStyleSheets.indexOf(var1);
            if (var3 != -1) {
               this.linkedStyleSheets.removeElementAt(var3);
               this.unlinkStyleSheet(var1, var3);
               if (var3 == 0 && this.linkedStyleSheets.size() == 0) {
                  this.linkedStyleSheets = null;
               }
            }
         }

      }
   }

   public StyleSheet[] getStyleSheets() {
      synchronized(this) {
         StyleSheet[] var1;
         if (this.linkedStyleSheets != null) {
            var1 = new StyleSheet[this.linkedStyleSheets.size()];
            this.linkedStyleSheets.copyInto(var1);
         } else {
            var1 = null;
         }

         return var1;
      }
   }

   public void importStyleSheet(URL var1) {
      try {
         InputStream var2 = var1.openStream();
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var2));
         StyleSheet.CssParser var4 = new StyleSheet.CssParser();
         var4.parse(var1, var3, false, true);
         var3.close();
         var2.close();
      } catch (Throwable var5) {
      }

   }

   public void setBase(URL var1) {
      this.base = var1;
   }

   public URL getBase() {
      return this.base;
   }

   public void addCSSAttribute(MutableAttributeSet var1, CSS.Attribute var2, String var3) {
      this.css.addInternalCSSValue(var1, var2, var3);
   }

   public boolean addCSSAttributeFromHTML(MutableAttributeSet var1, CSS.Attribute var2, String var3) {
      Object var4 = this.css.getCssValue(var2, var3);
      if (var4 != null) {
         var1.addAttribute(var2, var4);
         return true;
      } else {
         return false;
      }
   }

   public AttributeSet translateHTMLToCSS(AttributeSet var1) {
      AttributeSet var2 = this.css.translateHTMLToCSS(var1);
      Style var3 = this.addStyle((String)null, (Style)null);
      var3.addAttributes(var2);
      return var3;
   }

   public AttributeSet addAttribute(AttributeSet var1, Object var2, Object var3) {
      if (this.css == null) {
         this.css = new CSS();
      }

      if (var2 instanceof StyleConstants) {
         HTML.Tag var4 = HTML.getTagForStyleConstantsKey((StyleConstants)var2);
         if (var4 != null && var1.isDefined(var4)) {
            var1 = this.removeAttribute(var1, var4);
         }

         Object var5 = this.css.styleConstantsValueToCSSValue((StyleConstants)var2, var3);
         if (var5 != null) {
            CSS.Attribute var6 = this.css.styleConstantsKeyToCSSKey((StyleConstants)var2);
            if (var6 != null) {
               return super.addAttribute(var1, var6, var5);
            }
         }
      }

      return super.addAttribute(var1, var2, var3);
   }

   public AttributeSet addAttributes(AttributeSet var1, AttributeSet var2) {
      if (!(var2 instanceof HTMLDocument.TaggedAttributeSet)) {
         var1 = this.removeHTMLTags(var1, var2);
      }

      return super.addAttributes(var1, this.convertAttributeSet(var2));
   }

   public AttributeSet removeAttribute(AttributeSet var1, Object var2) {
      if (var2 instanceof StyleConstants) {
         HTML.Tag var3 = HTML.getTagForStyleConstantsKey((StyleConstants)var2);
         if (var3 != null) {
            var1 = super.removeAttribute(var1, var3);
         }

         CSS.Attribute var4 = this.css.styleConstantsKeyToCSSKey((StyleConstants)var2);
         if (var4 != null) {
            return super.removeAttribute(var1, var4);
         }
      }

      return super.removeAttribute(var1, var2);
   }

   public AttributeSet removeAttributes(AttributeSet var1, Enumeration<?> var2) {
      return super.removeAttributes(var1, var2);
   }

   public AttributeSet removeAttributes(AttributeSet var1, AttributeSet var2) {
      if (var1 != var2) {
         var1 = this.removeHTMLTags(var1, var2);
      }

      return super.removeAttributes(var1, this.convertAttributeSet(var2));
   }

   protected StyleContext.SmallAttributeSet createSmallAttributeSet(AttributeSet var1) {
      return new StyleSheet.SmallConversionSet(var1);
   }

   protected MutableAttributeSet createLargeAttributeSet(AttributeSet var1) {
      return new StyleSheet.LargeConversionSet(var1);
   }

   private AttributeSet removeHTMLTags(AttributeSet var1, AttributeSet var2) {
      if (!(var2 instanceof StyleSheet.LargeConversionSet) && !(var2 instanceof StyleSheet.SmallConversionSet)) {
         Enumeration var3 = var2.getAttributeNames();

         while(var3.hasMoreElements()) {
            Object var4 = var3.nextElement();
            if (var4 instanceof StyleConstants) {
               HTML.Tag var5 = HTML.getTagForStyleConstantsKey((StyleConstants)var4);
               if (var5 != null && var1.isDefined(var5)) {
                  var1 = super.removeAttribute(var1, var5);
               }
            }
         }
      }

      return var1;
   }

   AttributeSet convertAttributeSet(AttributeSet var1) {
      if (!(var1 instanceof StyleSheet.LargeConversionSet) && !(var1 instanceof StyleSheet.SmallConversionSet)) {
         Enumeration var2 = var1.getAttributeNames();

         Object var3;
         do {
            if (!var2.hasMoreElements()) {
               return var1;
            }

            var3 = var2.nextElement();
         } while(!(var3 instanceof StyleConstants));

         StyleSheet.LargeConversionSet var4 = new StyleSheet.LargeConversionSet();
         Enumeration var5 = var1.getAttributeNames();

         while(var5.hasMoreElements()) {
            Object var6 = var5.nextElement();
            Object var7 = null;
            if (var6 instanceof StyleConstants) {
               CSS.Attribute var8 = this.css.styleConstantsKeyToCSSKey((StyleConstants)var6);
               if (var8 != null) {
                  Object var9 = var1.getAttribute(var6);
                  var7 = this.css.styleConstantsValueToCSSValue((StyleConstants)var6, var9);
                  if (var7 != null) {
                     var4.addAttribute(var8, var7);
                  }
               }
            }

            if (var7 == null) {
               var4.addAttribute(var6, var1.getAttribute(var6));
            }
         }

         return var4;
      } else {
         return var1;
      }
   }

   public Font getFont(AttributeSet var1) {
      return this.css.getFont(this, var1, 12, this);
   }

   public Color getForeground(AttributeSet var1) {
      Color var2 = this.css.getColor(var1, CSS.Attribute.COLOR);
      return var2 == null ? Color.black : var2;
   }

   public Color getBackground(AttributeSet var1) {
      return this.css.getColor(var1, CSS.Attribute.BACKGROUND_COLOR);
   }

   public StyleSheet.BoxPainter getBoxPainter(AttributeSet var1) {
      return new StyleSheet.BoxPainter(var1, this.css, this);
   }

   public StyleSheet.ListPainter getListPainter(AttributeSet var1) {
      return new StyleSheet.ListPainter(var1, this);
   }

   public void setBaseFontSize(int var1) {
      this.css.setBaseFontSize(var1);
   }

   public void setBaseFontSize(String var1) {
      this.css.setBaseFontSize(var1);
   }

   public static int getIndexOfSize(float var0) {
      return CSS.getIndexOfSize(var0, sizeMapDefault);
   }

   public float getPointSize(int var1) {
      return this.css.getPointSize(var1, this);
   }

   public float getPointSize(String var1) {
      return this.css.getPointSize(var1, this);
   }

   public Color stringToColor(String var1) {
      return CSS.stringToColor(var1);
   }

   ImageIcon getBackgroundImage(AttributeSet var1) {
      Object var2 = var1.getAttribute(CSS.Attribute.BACKGROUND_IMAGE);
      return var2 != null ? ((CSS.BackgroundImage)var2).getImage(this.getBase()) : null;
   }

   void addRule(String[] var1, AttributeSet var2, boolean var3) {
      int var4 = var1.length;
      StringBuilder var5 = new StringBuilder();
      var5.append(var1[0]);

      for(int var6 = 1; var6 < var4; ++var6) {
         var5.append(' ');
         var5.append(var1[var6]);
      }

      String var14 = var5.toString();
      Style var7 = this.getStyle(var14);
      if (var7 == null) {
         Style var8 = this.addStyle(var14, (Style)null);
         synchronized(this) {
            StyleSheet.SelectorMapping var10 = this.getRootSelectorMapping();

            for(int var11 = var4 - 1; var11 >= 0; --var11) {
               var10 = var10.getChildSelectorMapping(var1[var11], true);
            }

            var7 = var10.getStyle();
            if (var7 == null) {
               var7 = var8;
               var10.setStyle(var8);
               this.refreshResolvedRules(var14, var1, var8, var10.getSpecificity());
            }
         }
      }

      if (var3) {
         var7 = this.getLinkedStyle(var7);
      }

      var7.addAttributes(var2);
   }

   private synchronized void linkStyleSheetAt(StyleSheet var1, int var2) {
      if (this.resolvedStyles.size() > 0) {
         Enumeration var3 = this.resolvedStyles.elements();

         while(var3.hasMoreElements()) {
            StyleSheet.ResolvedStyle var4 = (StyleSheet.ResolvedStyle)var3.nextElement();
            var4.insertExtendedStyleAt(var1.getRule(var4.getName()), var2);
         }
      }

   }

   private synchronized void unlinkStyleSheet(StyleSheet var1, int var2) {
      if (this.resolvedStyles.size() > 0) {
         Enumeration var3 = this.resolvedStyles.elements();

         while(var3.hasMoreElements()) {
            StyleSheet.ResolvedStyle var4 = (StyleSheet.ResolvedStyle)var3.nextElement();
            var4.removeExtendedStyleAt(var2);
         }
      }

   }

   String[] getSimpleSelectors(String var1) {
      var1 = this.cleanSelectorString(var1);
      StyleSheet.SearchBuffer var2 = StyleSheet.SearchBuffer.obtainSearchBuffer();
      Vector var3 = var2.getVector();
      int var4 = 0;
      int var5 = var1.length();

      while(var4 != -1) {
         int var6 = var1.indexOf(32, var4);
         if (var6 != -1) {
            var3.addElement(var1.substring(var4, var6));
            ++var6;
            if (var6 == var5) {
               var4 = -1;
            } else {
               var4 = var6;
            }
         } else {
            var3.addElement(var1.substring(var4));
            var4 = -1;
         }
      }

      String[] var7 = new String[var3.size()];
      var3.copyInto(var7);
      StyleSheet.SearchBuffer.releaseSearchBuffer(var2);
      return var7;
   }

   String cleanSelectorString(String var1) {
      boolean var2 = true;
      int var3 = 0;

      for(int var4 = var1.length(); var3 < var4; ++var3) {
         switch(var1.charAt(var3)) {
         case '\t':
         case '\n':
         case '\r':
            return this._cleanSelectorString(var1);
         case ' ':
            if (var2) {
               return this._cleanSelectorString(var1);
            }

            var2 = true;
            break;
         default:
            var2 = false;
         }
      }

      if (var2) {
         return this._cleanSelectorString(var1);
      } else {
         return var1;
      }
   }

   private String _cleanSelectorString(String var1) {
      StyleSheet.SearchBuffer var2 = StyleSheet.SearchBuffer.obtainSearchBuffer();
      StringBuffer var3 = var2.getStringBuffer();
      boolean var4 = true;
      int var5 = 0;
      char[] var6 = var1.toCharArray();
      int var7 = var6.length;
      String var8 = null;

      try {
         for(int var9 = 0; var9 < var7; ++var9) {
            switch(var6[var9]) {
            case '\t':
            case '\n':
            case '\r':
               if (!var4) {
                  var4 = true;
                  if (var5 < var9) {
                     var3.append(var6, var5, var9 - var5);
                     var3.append(' ');
                  }
               }

               var5 = var9 + 1;
               break;
            case ' ':
               if (!var4) {
                  var4 = true;
                  if (var5 < var9) {
                     var3.append(var6, var5, 1 + var9 - var5);
                  }
               }

               var5 = var9 + 1;
               break;
            default:
               var4 = false;
            }
         }

         if (var4 && var3.length() > 0) {
            var3.setLength(var3.length() - 1);
         } else if (var5 < var7) {
            var3.append(var6, var5, var7 - var5);
         }

         var8 = var3.toString();
         return var8;
      } finally {
         StyleSheet.SearchBuffer.releaseSearchBuffer(var2);
      }
   }

   private StyleSheet.SelectorMapping getRootSelectorMapping() {
      return this.selectorMapping;
   }

   static int getSpecificity(String var0) {
      int var1 = 0;
      boolean var2 = true;
      int var3 = 0;

      for(int var4 = var0.length(); var3 < var4; ++var3) {
         switch(var0.charAt(var3)) {
         case ' ':
            var2 = true;
            break;
         case '#':
            var1 += 10000;
            break;
         case '.':
            var1 += 100;
            break;
         default:
            if (var2) {
               var2 = false;
               ++var1;
            }
         }
      }

      return var1;
   }

   private Style getLinkedStyle(Style var1) {
      Style var2 = (Style)var1.getResolveParent();
      if (var2 == null) {
         var2 = this.addStyle((String)null, (Style)null);
         var1.setResolveParent(var2);
      }

      return var2;
   }

   private synchronized Style getResolvedStyle(String var1, Vector var2, HTML.Tag var3) {
      Style var4 = (Style)this.resolvedStyles.get(var1);
      if (var4 == null) {
         var4 = this.createResolvedStyle(var1, var2, var3);
      }

      return var4;
   }

   private synchronized Style getResolvedStyle(String var1) {
      Style var2 = (Style)this.resolvedStyles.get(var1);
      if (var2 == null) {
         var2 = this.createResolvedStyle(var1);
      }

      return var2;
   }

   private void addSortedStyle(StyleSheet.SelectorMapping var1, Vector<StyleSheet.SelectorMapping> var2) {
      int var3 = var2.size();
      if (var3 > 0) {
         int var4 = var1.getSpecificity();

         for(int var5 = 0; var5 < var3; ++var5) {
            if (var4 >= ((StyleSheet.SelectorMapping)var2.elementAt(var5)).getSpecificity()) {
               var2.insertElementAt(var1, var5);
               return;
            }
         }
      }

      var2.addElement(var1);
   }

   private synchronized void getStyles(StyleSheet.SelectorMapping var1, Vector<StyleSheet.SelectorMapping> var2, String[] var3, String[] var4, String[] var5, int var6, int var7, Hashtable<StyleSheet.SelectorMapping, StyleSheet.SelectorMapping> var8) {
      if (!var8.contains(var1)) {
         var8.put(var1, var1);
         Style var9 = var1.getStyle();
         if (var9 != null) {
            this.addSortedStyle(var1, var2);
         }

         for(int var10 = var6; var10 < var7; ++var10) {
            String var11 = var3[var10];
            if (var11 != null) {
               StyleSheet.SelectorMapping var12 = var1.getChildSelectorMapping(var11, false);
               if (var12 != null) {
                  this.getStyles(var12, var2, var3, var4, var5, var10 + 1, var7, var8);
               }

               String var13;
               if (var5[var10] != null) {
                  var13 = var5[var10];
                  var12 = var1.getChildSelectorMapping(var11 + "." + var13, false);
                  if (var12 != null) {
                     this.getStyles(var12, var2, var3, var4, var5, var10 + 1, var7, var8);
                  }

                  var12 = var1.getChildSelectorMapping("." + var13, false);
                  if (var12 != null) {
                     this.getStyles(var12, var2, var3, var4, var5, var10 + 1, var7, var8);
                  }
               }

               if (var4[var10] != null) {
                  var13 = var4[var10];
                  var12 = var1.getChildSelectorMapping(var11 + "#" + var13, false);
                  if (var12 != null) {
                     this.getStyles(var12, var2, var3, var4, var5, var10 + 1, var7, var8);
                  }

                  var12 = var1.getChildSelectorMapping("#" + var13, false);
                  if (var12 != null) {
                     this.getStyles(var12, var2, var3, var4, var5, var10 + 1, var7, var8);
                  }
               }
            }
         }

      }
   }

   private synchronized Style createResolvedStyle(String var1, String[] var2, String[] var3, String[] var4) {
      StyleSheet.SearchBuffer var5 = StyleSheet.SearchBuffer.obtainSearchBuffer();
      Vector var6 = var5.getVector();
      Hashtable var7 = var5.getHashtable();

      try {
         StyleSheet.SelectorMapping var8 = this.getRootSelectorMapping();
         int var9 = var2.length;
         String var10 = var2[0];
         StyleSheet.SelectorMapping var11 = var8.getChildSelectorMapping(var10, false);
         if (var11 != null) {
            this.getStyles(var11, var6, var2, var3, var4, 1, var9, var7);
         }

         String var12;
         if (var4[0] != null) {
            var12 = var4[0];
            var11 = var8.getChildSelectorMapping(var10 + "." + var12, false);
            if (var11 != null) {
               this.getStyles(var11, var6, var2, var3, var4, 1, var9, var7);
            }

            var11 = var8.getChildSelectorMapping("." + var12, false);
            if (var11 != null) {
               this.getStyles(var11, var6, var2, var3, var4, 1, var9, var7);
            }
         }

         if (var3[0] != null) {
            var12 = var3[0];
            var11 = var8.getChildSelectorMapping(var10 + "#" + var12, false);
            if (var11 != null) {
               this.getStyles(var11, var6, var2, var3, var4, 1, var9, var7);
            }

            var11 = var8.getChildSelectorMapping("#" + var12, false);
            if (var11 != null) {
               this.getStyles(var11, var6, var2, var3, var4, 1, var9, var7);
            }
         }

         int var20 = this.linkedStyleSheets != null ? this.linkedStyleSheets.size() : 0;
         int var13 = var6.size();
         AttributeSet[] var14 = new AttributeSet[var13 + var20];

         int var15;
         for(var15 = 0; var15 < var13; ++var15) {
            var14[var15] = ((StyleSheet.SelectorMapping)var6.elementAt(var15)).getStyle();
         }

         for(var15 = 0; var15 < var20; ++var15) {
            Style var16 = ((StyleSheet)this.linkedStyleSheets.elementAt(var15)).getRule(var1);
            if (var16 == null) {
               var14[var15 + var13] = SimpleAttributeSet.EMPTY;
            } else {
               var14[var15 + var13] = var16;
            }
         }

         StyleSheet.ResolvedStyle var22 = new StyleSheet.ResolvedStyle(var1, var14, var13);
         this.resolvedStyles.put(var1, var22);
         StyleSheet.ResolvedStyle var21 = var22;
         return var21;
      } finally {
         StyleSheet.SearchBuffer.releaseSearchBuffer(var5);
      }
   }

   private Style createResolvedStyle(String var1, Vector var2, HTML.Tag var3) {
      int var4 = var2.size();
      String[] var5 = new String[var4];
      String[] var6 = new String[var4];
      String[] var7 = new String[var4];

      for(int var8 = 0; var8 < var4; ++var8) {
         Element var9 = (Element)var2.elementAt(var8);
         AttributeSet var10 = var9.getAttributes();
         if (var8 == 0 && var9.isLeaf()) {
            Object var11 = var10.getAttribute(var3);
            if (var11 instanceof AttributeSet) {
               var10 = (AttributeSet)var11;
            } else {
               var10 = null;
            }
         }

         if (var10 != null) {
            HTML.Tag var12 = (HTML.Tag)var10.getAttribute(StyleConstants.NameAttribute);
            if (var12 != null) {
               var5[var8] = var12.toString();
            } else {
               var5[var8] = null;
            }

            if (var10.isDefined(HTML.Attribute.CLASS)) {
               var7[var8] = var10.getAttribute(HTML.Attribute.CLASS).toString();
            } else {
               var7[var8] = null;
            }

            if (var10.isDefined(HTML.Attribute.ID)) {
               var6[var8] = var10.getAttribute(HTML.Attribute.ID).toString();
            } else {
               var6[var8] = null;
            }
         } else {
            var5[var8] = var6[var8] = var7[var8] = null;
         }
      }

      var5[0] = var3.toString();
      return this.createResolvedStyle(var1, var5, var6, var7);
   }

   private Style createResolvedStyle(String var1) {
      StyleSheet.SearchBuffer var2 = StyleSheet.SearchBuffer.obtainSearchBuffer();
      Vector var3 = var2.getVector();

      try {
         int var5 = 0;
         int var7 = 0;
         int var8 = 0;

         int var6;
         for(int var9 = var1.length(); var8 < var9; var8 = var6 + 1) {
            if (var5 == var8) {
               var5 = var1.indexOf(46, var8);
            }

            if (var7 == var8) {
               var7 = var1.indexOf(35, var8);
            }

            var6 = var1.indexOf(32, var8);
            if (var6 == -1) {
               var6 = var9;
            }

            if (var5 != -1 && var7 != -1 && var5 < var6 && var7 < var6) {
               if (var7 < var5) {
                  if (var8 == var7) {
                     var3.addElement("");
                  } else {
                     var3.addElement(var1.substring(var8, var7));
                  }

                  if (var5 + 1 < var6) {
                     var3.addElement(var1.substring(var5 + 1, var6));
                  } else {
                     var3.addElement((Object)null);
                  }

                  if (var7 + 1 == var5) {
                     var3.addElement((Object)null);
                  } else {
                     var3.addElement(var1.substring(var7 + 1, var5));
                  }
               } else if (var7 < var6) {
                  if (var8 == var5) {
                     var3.addElement("");
                  } else {
                     var3.addElement(var1.substring(var8, var5));
                  }

                  if (var5 + 1 < var7) {
                     var3.addElement(var1.substring(var5 + 1, var7));
                  } else {
                     var3.addElement((Object)null);
                  }

                  if (var7 + 1 == var6) {
                     var3.addElement((Object)null);
                  } else {
                     var3.addElement(var1.substring(var7 + 1, var6));
                  }
               }

               var5 = var7 = var6 + 1;
            } else if (var5 != -1 && var5 < var6) {
               if (var5 == var8) {
                  var3.addElement("");
               } else {
                  var3.addElement(var1.substring(var8, var5));
               }

               if (var5 + 1 == var6) {
                  var3.addElement((Object)null);
               } else {
                  var3.addElement(var1.substring(var5 + 1, var6));
               }

               var3.addElement((Object)null);
               var5 = var6 + 1;
            } else if (var7 != -1 && var7 < var6) {
               if (var7 == var8) {
                  var3.addElement("");
               } else {
                  var3.addElement(var1.substring(var8, var7));
               }

               var3.addElement((Object)null);
               if (var7 + 1 == var6) {
                  var3.addElement((Object)null);
               } else {
                  var3.addElement(var1.substring(var7 + 1, var6));
               }

               var7 = var6 + 1;
            } else {
               var3.addElement(var1.substring(var8, var6));
               var3.addElement((Object)null);
               var3.addElement((Object)null);
            }
         }

         int var10 = var3.size();
         int var11 = var10 / 3;
         String[] var12 = new String[var11];
         String[] var13 = new String[var11];
         String[] var14 = new String[var11];
         int var15 = 0;

         for(int var16 = var10 - 3; var15 < var11; var16 -= 3) {
            var12[var15] = (String)var3.elementAt(var16);
            var14[var15] = (String)var3.elementAt(var16 + 1);
            var13[var15] = (String)var3.elementAt(var16 + 2);
            ++var15;
         }

         Style var20 = this.createResolvedStyle(var1, var12, var13, var14);
         return var20;
      } finally {
         StyleSheet.SearchBuffer.releaseSearchBuffer(var2);
      }
   }

   private synchronized void refreshResolvedRules(String var1, String[] var2, Style var3, int var4) {
      if (this.resolvedStyles.size() > 0) {
         Enumeration var5 = this.resolvedStyles.elements();

         while(var5.hasMoreElements()) {
            StyleSheet.ResolvedStyle var6 = (StyleSheet.ResolvedStyle)var5.nextElement();
            if (var6.matches(var1)) {
               var6.insertStyle(var3, var4);
            }
         }
      }

   }

   void rebaseSizeMap(int var1) {
      this.sizeMap = new int[sizeMapDefault.length];

      for(int var3 = 0; var3 < sizeMapDefault.length; ++var3) {
         this.sizeMap[var3] = Math.max(var1 * sizeMapDefault[var3] / sizeMapDefault[CSS.baseFontSizeIndex], 4);
      }

   }

   int[] getSizeMap() {
      return this.sizeMap;
   }

   boolean isW3CLengthUnits() {
      return this.w3cLengthUnits;
   }

   class CssParser implements CSSParser.CSSParserCallback {
      Vector<String[]> selectors = new Vector();
      Vector<String> selectorTokens = new Vector();
      String propertyName;
      MutableAttributeSet declaration = new SimpleAttributeSet();
      boolean parsingDeclaration;
      boolean isLink;
      URL base;
      CSSParser parser = new CSSParser();

      public AttributeSet parseDeclaration(String var1) {
         try {
            return this.parseDeclaration((Reader)(new StringReader(var1)));
         } catch (IOException var3) {
            return null;
         }
      }

      public AttributeSet parseDeclaration(Reader var1) throws IOException {
         this.parse(this.base, var1, true, false);
         return this.declaration.copyAttributes();
      }

      public void parse(URL var1, Reader var2, boolean var3, boolean var4) throws IOException {
         this.base = var1;
         this.isLink = var4;
         this.parsingDeclaration = var3;
         this.declaration.removeAttributes((AttributeSet)this.declaration);
         this.selectorTokens.removeAllElements();
         this.selectors.removeAllElements();
         this.propertyName = null;
         this.parser.parse(var2, this, var3);
      }

      public void handleImport(String var1) {
         URL var2 = CSS.getURL(this.base, var1);
         if (var2 != null) {
            StyleSheet.this.importStyleSheet(var2);
         }

      }

      public void handleSelector(String var1) {
         if (!var1.startsWith(".") && !var1.startsWith("#")) {
            var1 = var1.toLowerCase();
         }

         int var2 = var1.length();
         if (var1.endsWith(",")) {
            if (var2 > 1) {
               var1 = var1.substring(0, var2 - 1);
               this.selectorTokens.addElement(var1);
            }

            this.addSelector();
         } else if (var2 > 0) {
            this.selectorTokens.addElement(var1);
         }

      }

      public void startRule() {
         if (this.selectorTokens.size() > 0) {
            this.addSelector();
         }

         this.propertyName = null;
      }

      public void handleProperty(String var1) {
         this.propertyName = var1;
      }

      public void handleValue(String var1) {
         if (this.propertyName != null && var1 != null && var1.length() > 0) {
            CSS.Attribute var2 = CSS.getAttribute(this.propertyName);
            if (var2 != null) {
               if (var2 == CSS.Attribute.LIST_STYLE_IMAGE && var1 != null && !var1.equals("none")) {
                  URL var3 = CSS.getURL(this.base, var1);
                  if (var3 != null) {
                     var1 = var3.toString();
                  }
               }

               StyleSheet.this.addCSSAttribute(this.declaration, var2, var1);
            }

            this.propertyName = null;
         }

      }

      public void endRule() {
         int var1 = this.selectors.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            String[] var3 = (String[])this.selectors.elementAt(var2);
            if (var3.length > 0) {
               StyleSheet.this.addRule(var3, this.declaration, this.isLink);
            }
         }

         this.declaration.removeAttributes((AttributeSet)this.declaration);
         this.selectors.removeAllElements();
      }

      private void addSelector() {
         String[] var1 = new String[this.selectorTokens.size()];
         this.selectorTokens.copyInto(var1);
         this.selectors.addElement(var1);
         this.selectorTokens.removeAllElements();
      }
   }

   static class SelectorMapping implements Serializable {
      private int specificity;
      private Style style;
      private HashMap<String, StyleSheet.SelectorMapping> children;

      public SelectorMapping(int var1) {
         this.specificity = var1;
      }

      public int getSpecificity() {
         return this.specificity;
      }

      public void setStyle(Style var1) {
         this.style = var1;
      }

      public Style getStyle() {
         return this.style;
      }

      public StyleSheet.SelectorMapping getChildSelectorMapping(String var1, boolean var2) {
         StyleSheet.SelectorMapping var3 = null;
         if (this.children != null) {
            var3 = (StyleSheet.SelectorMapping)this.children.get(var1);
         } else if (var2) {
            this.children = new HashMap(7);
         }

         if (var3 == null && var2) {
            int var4 = this.getChildSpecificity(var1);
            var3 = this.createChildSelectorMapping(var4);
            this.children.put(var1, var3);
         }

         return var3;
      }

      protected StyleSheet.SelectorMapping createChildSelectorMapping(int var1) {
         return new StyleSheet.SelectorMapping(var1);
      }

      protected int getChildSpecificity(String var1) {
         char var2 = var1.charAt(0);
         int var3 = this.getSpecificity();
         if (var2 == '.') {
            var3 += 100;
         } else if (var2 == '#') {
            var3 += 10000;
         } else {
            ++var3;
            if (var1.indexOf(46) != -1) {
               var3 += 100;
            }

            if (var1.indexOf(35) != -1) {
               var3 += 10000;
            }
         }

         return var3;
      }
   }

   static class ResolvedStyle extends MuxingAttributeSet implements Serializable, Style {
      String name;
      private int extendedIndex;

      ResolvedStyle(String var1, AttributeSet[] var2, int var3) {
         super(var2);
         this.name = var1;
         this.extendedIndex = var3;
      }

      synchronized void insertStyle(Style var1, int var2) {
         AttributeSet[] var3 = this.getAttributes();
         int var4 = var3.length;

         int var5;
         for(var5 = 0; var5 < this.extendedIndex && var2 <= StyleSheet.getSpecificity(((Style)var3[var5]).getName()); ++var5) {
         }

         this.insertAttributeSetAt(var1, var5);
         ++this.extendedIndex;
      }

      synchronized void removeStyle(Style var1) {
         AttributeSet[] var2 = this.getAttributes();

         for(int var3 = var2.length - 1; var3 >= 0; --var3) {
            if (var2[var3] == var1) {
               this.removeAttributeSetAt(var3);
               if (var3 < this.extendedIndex) {
                  --this.extendedIndex;
               }
               break;
            }
         }

      }

      synchronized void insertExtendedStyleAt(Style var1, int var2) {
         this.insertAttributeSetAt(var1, this.extendedIndex + var2);
      }

      synchronized void addExtendedStyle(Style var1) {
         this.insertAttributeSetAt(var1, this.getAttributes().length);
      }

      synchronized void removeExtendedStyleAt(int var1) {
         this.removeAttributeSetAt(this.extendedIndex + var1);
      }

      protected boolean matches(String var1) {
         int var2 = var1.length();
         if (var2 == 0) {
            return false;
         } else {
            int var3 = this.name.length();
            int var4 = var1.lastIndexOf(32);
            int var5 = this.name.lastIndexOf(32);
            if (var4 >= 0) {
               ++var4;
            }

            if (var5 >= 0) {
               ++var5;
            }

            if (!this.matches(var1, var4, var2, var5, var3)) {
               return false;
            } else {
               boolean var6;
               do {
                  if (var4 == -1) {
                     return true;
                  }

                  var2 = var4 - 1;
                  var4 = var1.lastIndexOf(32, var2 - 1);
                  if (var4 >= 0) {
                     ++var4;
                  }

                  for(var6 = false; !var6 && var5 != -1; var6 = this.matches(var1, var4, var2, var5, var3)) {
                     var3 = var5 - 1;
                     var5 = this.name.lastIndexOf(32, var3 - 1);
                     if (var5 >= 0) {
                        ++var5;
                     }
                  }
               } while(var6);

               return false;
            }
         }
      }

      boolean matches(String var1, int var2, int var3, int var4, int var5) {
         var2 = Math.max(var2, 0);
         var4 = Math.max(var4, 0);
         int var6 = this.boundedIndexOf(this.name, '.', var4, var5);
         int var7 = this.boundedIndexOf(this.name, '#', var4, var5);
         int var8 = this.boundedIndexOf(var1, '.', var2, var3);
         int var9 = this.boundedIndexOf(var1, '#', var2, var3);
         if (var8 != -1) {
            if (var6 == -1) {
               return false;
            } else {
               if (var2 == var8) {
                  if (var5 - var6 != var3 - var8 || !var1.regionMatches(var2, this.name, var6, var5 - var6)) {
                     return false;
                  }
               } else if (var3 - var2 != var5 - var4 || !var1.regionMatches(var2, this.name, var4, var5 - var4)) {
                  return false;
               }

               return true;
            }
         } else if (var9 != -1) {
            if (var7 == -1) {
               return false;
            } else {
               if (var2 == var9) {
                  if (var5 - var7 != var3 - var9 || !var1.regionMatches(var2, this.name, var7, var5 - var7)) {
                     return false;
                  }
               } else if (var3 - var2 != var5 - var4 || !var1.regionMatches(var2, this.name, var4, var5 - var4)) {
                  return false;
               }

               return true;
            }
         } else if (var6 != -1) {
            return var6 - var4 == var3 - var2 && var1.regionMatches(var2, this.name, var4, var6 - var4);
         } else if (var7 != -1) {
            return var7 - var4 == var3 - var2 && var1.regionMatches(var2, this.name, var4, var7 - var4);
         } else {
            return var5 - var4 == var3 - var2 && var1.regionMatches(var2, this.name, var4, var5 - var4);
         }
      }

      int boundedIndexOf(String var1, char var2, int var3, int var4) {
         int var5 = var1.indexOf(var2, var3);
         return var5 >= var4 ? -1 : var5;
      }

      public void addAttribute(Object var1, Object var2) {
      }

      public void addAttributes(AttributeSet var1) {
      }

      public void removeAttribute(Object var1) {
      }

      public void removeAttributes(Enumeration<?> var1) {
      }

      public void removeAttributes(AttributeSet var1) {
      }

      public void setResolveParent(AttributeSet var1) {
      }

      public String getName() {
         return this.name;
      }

      public void addChangeListener(ChangeListener var1) {
      }

      public void removeChangeListener(ChangeListener var1) {
      }

      public ChangeListener[] getChangeListeners() {
         return new ChangeListener[0];
      }
   }

   class ViewAttributeSet extends MuxingAttributeSet {
      View host;

      ViewAttributeSet(View var2) {
         this.host = var2;
         Document var3 = var2.getDocument();
         StyleSheet.SearchBuffer var4 = StyleSheet.SearchBuffer.obtainSearchBuffer();
         Vector var5 = var4.getVector();

         try {
            if (var3 instanceof HTMLDocument) {
               StyleSheet var6 = StyleSheet.this;
               Element var7 = var2.getElement();
               AttributeSet var8 = var7.getAttributes();
               AttributeSet var9 = StyleSheet.this.translateHTMLToCSS(var8);
               if (var9.getAttributeCount() != 0) {
                  var5.addElement(var9);
               }

               if (!var7.isLeaf()) {
                  HTML.Tag var18 = (HTML.Tag)var8.getAttribute(StyleConstants.NameAttribute);
                  Style var19 = StyleSheet.this.getRule(var18, var7);
                  if (var19 != null) {
                     var5.addElement(var19);
                  }
               } else {
                  Enumeration var10 = var8.getAttributeNames();

                  label111:
                  while(true) {
                     Object var11;
                     AttributeSet var13;
                     do {
                        do {
                           if (!var10.hasMoreElements()) {
                              break label111;
                           }

                           var11 = var10.nextElement();
                        } while(!(var11 instanceof HTML.Tag));

                        if (var11 != HTML.Tag.A) {
                           break;
                        }

                        Object var12 = var8.getAttribute(var11);
                        if (var12 == null || !(var12 instanceof AttributeSet)) {
                           break;
                        }

                        var13 = (AttributeSet)var12;
                     } while(var13.getAttribute(HTML.Attribute.HREF) == null);

                     Style var20 = var6.getRule((HTML.Tag)var11, var7);
                     if (var20 != null) {
                        var5.addElement(var20);
                     }
                  }
               }
            }

            AttributeSet[] var17 = new AttributeSet[var5.size()];
            var5.copyInto(var17);
            this.setAttributes(var17);
         } finally {
            StyleSheet.SearchBuffer.releaseSearchBuffer(var4);
         }

      }

      public boolean isDefined(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               var1 = var2;
            }
         }

         return super.isDefined(var1);
      }

      public Object getAttribute(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               Object var3 = this.doGetAttribute(var2);
               if (var3 instanceof CSS.CssValue) {
                  return ((CSS.CssValue)var3).toStyleConstants((StyleConstants)var1, this.host);
               }
            }
         }

         return this.doGetAttribute(var1);
      }

      Object doGetAttribute(Object var1) {
         Object var2 = super.getAttribute(var1);
         if (var2 != null) {
            return var2;
         } else {
            if (var1 instanceof CSS.Attribute) {
               CSS.Attribute var3 = (CSS.Attribute)var1;
               if (var3.isInherited()) {
                  AttributeSet var4 = this.getResolveParent();
                  if (var4 != null) {
                     return var4.getAttribute(var1);
                  }
               }
            }

            return null;
         }
      }

      public AttributeSet getResolveParent() {
         if (this.host == null) {
            return null;
         } else {
            View var1 = this.host.getParent();
            return var1 != null ? var1.getAttributes() : null;
         }
      }
   }

   static class BackgroundImagePainter implements Serializable {
      ImageIcon backgroundImage;
      float hPosition;
      float vPosition;
      short flags;
      private int paintX;
      private int paintY;
      private int paintMaxX;
      private int paintMaxY;

      BackgroundImagePainter(AttributeSet var1, CSS var2, StyleSheet var3) {
         this.backgroundImage = var3.getBackgroundImage(var1);
         CSS.BackgroundPosition var4 = (CSS.BackgroundPosition)var1.getAttribute(CSS.Attribute.BACKGROUND_POSITION);
         if (var4 != null) {
            this.hPosition = var4.getHorizontalPosition();
            this.vPosition = var4.getVerticalPosition();
            if (var4.isHorizontalPositionRelativeToSize()) {
               this.flags = (short)(this.flags | 4);
            } else if (var4.isHorizontalPositionRelativeToSize()) {
               this.hPosition *= (float)CSS.getFontSize(var1, 12, var3);
            }

            if (var4.isVerticalPositionRelativeToSize()) {
               this.flags = (short)(this.flags | 8);
            } else if (var4.isVerticalPositionRelativeToFontSize()) {
               this.vPosition *= (float)CSS.getFontSize(var1, 12, var3);
            }
         }

         CSS.Value var5 = (CSS.Value)var1.getAttribute(CSS.Attribute.BACKGROUND_REPEAT);
         if (var5 != null && var5 != CSS.Value.BACKGROUND_REPEAT) {
            if (var5 == CSS.Value.BACKGROUND_REPEAT_X) {
               this.flags = (short)(this.flags | 1);
            } else if (var5 == CSS.Value.BACKGROUND_REPEAT_Y) {
               this.flags = (short)(this.flags | 2);
            }
         } else {
            this.flags = (short)(this.flags | 3);
         }

      }

      void paint(Graphics var1, float var2, float var3, float var4, float var5, View var6) {
         Rectangle var7 = var1.getClipRect();
         if (var7 != null) {
            var1.clipRect((int)var2, (int)var3, (int)var4, (int)var5);
         }

         int var8;
         int var9;
         if ((this.flags & 3) == 0) {
            var8 = this.backgroundImage.getIconWidth();
            var9 = this.backgroundImage.getIconWidth();
            if ((this.flags & 4) == 4) {
               this.paintX = (int)(var2 + var4 * this.hPosition - (float)var8 * this.hPosition);
            } else {
               this.paintX = (int)var2 + (int)this.hPosition;
            }

            if ((this.flags & 8) == 8) {
               this.paintY = (int)(var3 + var5 * this.vPosition - (float)var9 * this.vPosition);
            } else {
               this.paintY = (int)var3 + (int)this.vPosition;
            }

            if (var7 == null || this.paintX + var8 > var7.x && this.paintY + var9 > var7.y && this.paintX < var7.x + var7.width && this.paintY < var7.y + var7.height) {
               this.backgroundImage.paintIcon((Component)null, var1, this.paintX, this.paintY);
            }
         } else {
            var8 = this.backgroundImage.getIconWidth();
            var9 = this.backgroundImage.getIconHeight();
            if (var8 > 0 && var9 > 0) {
               this.paintX = (int)var2;
               this.paintY = (int)var3;
               this.paintMaxX = (int)(var2 + var4);
               this.paintMaxY = (int)(var3 + var5);
               if (this.updatePaintCoordinates(var7, var8, var9)) {
                  while(this.paintX < this.paintMaxX) {
                     for(int var10 = this.paintY; var10 < this.paintMaxY; var10 += var9) {
                        this.backgroundImage.paintIcon((Component)null, var1, this.paintX, var10);
                     }

                     this.paintX += var8;
                  }
               }
            }
         }

         if (var7 != null) {
            var1.setClip(var7.x, var7.y, var7.width, var7.height);
         }

      }

      private boolean updatePaintCoordinates(Rectangle var1, int var2, int var3) {
         if ((this.flags & 3) == 1) {
            this.paintMaxY = this.paintY + 1;
         } else if ((this.flags & 3) == 2) {
            this.paintMaxX = this.paintX + 1;
         }

         if (var1 != null) {
            if ((this.flags & 3) == 1 && (this.paintY + var3 <= var1.y || this.paintY > var1.y + var1.height)) {
               return false;
            }

            if ((this.flags & 3) == 2 && (this.paintX + var2 <= var1.x || this.paintX > var1.x + var1.width)) {
               return false;
            }

            if ((this.flags & 1) == 1) {
               if (var1.x + var1.width < this.paintMaxX) {
                  if ((var1.x + var1.width - this.paintX) % var2 == 0) {
                     this.paintMaxX = var1.x + var1.width;
                  } else {
                     this.paintMaxX = ((var1.x + var1.width - this.paintX) / var2 + 1) * var2 + this.paintX;
                  }
               }

               if (var1.x > this.paintX) {
                  this.paintX += (var1.x - this.paintX) / var2 * var2;
               }
            }

            if ((this.flags & 2) == 2) {
               if (var1.y + var1.height < this.paintMaxY) {
                  if ((var1.y + var1.height - this.paintY) % var3 == 0) {
                     this.paintMaxY = var1.y + var1.height;
                  } else {
                     this.paintMaxY = ((var1.y + var1.height - this.paintY) / var3 + 1) * var3 + this.paintY;
                  }
               }

               if (var1.y > this.paintY) {
                  this.paintY += (var1.y - this.paintY) / var3 * var3;
               }
            }
         }

         return true;
      }
   }

   public static class ListPainter implements Serializable {
      static final char[][] romanChars = new char[][]{{'i', 'v'}, {'x', 'l'}, {'c', 'd'}, {'m', '?'}};
      private Rectangle paintRect;
      private boolean checkedForStart;
      private int start;
      private CSS.Value type;
      URL imageurl;
      private StyleSheet ss = null;
      Icon img = null;
      private int bulletgap = 5;
      private boolean isLeftToRight;

      ListPainter(AttributeSet var1, StyleSheet var2) {
         this.ss = var2;
         String var3 = (String)var1.getAttribute(CSS.Attribute.LIST_STYLE_IMAGE);
         this.type = null;
         if (var3 != null && !var3.equals("none")) {
            String var4 = null;

            URL var6;
            try {
               StringTokenizer var5 = new StringTokenizer(var3, "()");
               if (var5.hasMoreTokens()) {
                  var4 = var5.nextToken();
               }

               if (var5.hasMoreTokens()) {
                  var4 = var5.nextToken();
               }

               var6 = new URL(var4);
               this.img = new ImageIcon(var6);
            } catch (MalformedURLException var8) {
               if (var4 != null && var2 != null && var2.getBase() != null) {
                  try {
                     var6 = new URL(var2.getBase(), var4);
                     this.img = new ImageIcon(var6);
                  } catch (MalformedURLException var7) {
                     this.img = null;
                  }
               } else {
                  this.img = null;
               }
            }
         }

         if (this.img == null) {
            this.type = (CSS.Value)var1.getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
         }

         this.start = 1;
         this.paintRect = new Rectangle();
      }

      private CSS.Value getChildType(View var1) {
         CSS.Value var2 = (CSS.Value)var1.getAttributes().getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
         if (var2 == null) {
            if (this.type == null) {
               View var3 = var1.getParent();
               HTMLDocument var4 = (HTMLDocument)var3.getDocument();
               if (HTMLDocument.matchNameAttribute(var3.getElement().getAttributes(), HTML.Tag.OL)) {
                  var2 = CSS.Value.DECIMAL;
               } else {
                  var2 = CSS.Value.DISC;
               }
            } else {
               var2 = this.type;
            }
         }

         return var2;
      }

      private void getStart(View var1) {
         this.checkedForStart = true;
         Element var2 = var1.getElement();
         if (var2 != null) {
            AttributeSet var3 = var2.getAttributes();
            Object var4;
            if (var3 != null && var3.isDefined(HTML.Attribute.START) && (var4 = var3.getAttribute(HTML.Attribute.START)) != null && var4 instanceof String) {
               try {
                  this.start = Integer.parseInt((String)var4);
               } catch (NumberFormatException var6) {
               }
            }
         }

      }

      private int getRenderIndex(View var1, int var2) {
         if (!this.checkedForStart) {
            this.getStart(var1);
         }

         int var3 = var2;

         for(int var4 = var2; var4 >= 0; --var4) {
            AttributeSet var5 = var1.getElement().getElement(var4).getAttributes();
            if (var5.getAttribute(StyleConstants.NameAttribute) != HTML.Tag.LI) {
               --var3;
            } else if (var5.isDefined(HTML.Attribute.VALUE)) {
               Object var6 = var5.getAttribute(HTML.Attribute.VALUE);
               if (var6 != null && var6 instanceof String) {
                  try {
                     int var7 = Integer.parseInt((String)var6);
                     return var3 - var4 + var7;
                  } catch (NumberFormatException var8) {
                  }
               }
            }
         }

         return var3 + this.start;
      }

      public void paint(Graphics var1, float var2, float var3, float var4, float var5, View var6, int var7) {
         View var8 = var6.getView(var7);
         Container var9 = var6.getContainer();
         Object var10 = var8.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
         if (var10 instanceof HTML.Tag && var10 == HTML.Tag.LI) {
            this.isLeftToRight = var9.getComponentOrientation().isLeftToRight();
            float var11 = 0.0F;
            if (var8.getViewCount() > 0) {
               View var12 = var8.getView(0);
               Object var13 = var12.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
               if ((var13 == HTML.Tag.P || var13 == HTML.Tag.IMPLIED) && var12.getViewCount() > 0) {
                  this.paintRect.setBounds((int)var2, (int)var3, (int)var4, (int)var5);
                  Shape var14 = var8.getChildAllocation(0, this.paintRect);
                  if (var14 != null && (var14 = var12.getView(0).getChildAllocation(0, var14)) != null) {
                     Rectangle var15 = var14 instanceof Rectangle ? (Rectangle)var14 : var14.getBounds();
                     var11 = var12.getView(0).getAlignment(1);
                     var3 = (float)var15.y;
                     var5 = (float)var15.height;
                  }
               }
            }

            Color var16 = var9.isEnabled() ? (this.ss != null ? this.ss.getForeground(var8.getAttributes()) : var9.getForeground()) : UIManager.getColor("textInactiveText");
            var1.setColor(var16);
            if (this.img != null) {
               this.drawIcon(var1, (int)var2, (int)var3, (int)var4, (int)var5, var11, var9);
            } else {
               CSS.Value var17 = this.getChildType(var8);
               Font var18 = ((StyledDocument)var8.getDocument()).getFont(var8.getAttributes());
               if (var18 != null) {
                  var1.setFont(var18);
               }

               if (var17 != CSS.Value.SQUARE && var17 != CSS.Value.CIRCLE && var17 != CSS.Value.DISC) {
                  if (var17 == CSS.Value.DECIMAL) {
                     this.drawLetter(var1, '1', (int)var2, (int)var3, (int)var4, (int)var5, var11, this.getRenderIndex(var6, var7));
                  } else if (var17 == CSS.Value.LOWER_ALPHA) {
                     this.drawLetter(var1, 'a', (int)var2, (int)var3, (int)var4, (int)var5, var11, this.getRenderIndex(var6, var7));
                  } else if (var17 == CSS.Value.UPPER_ALPHA) {
                     this.drawLetter(var1, 'A', (int)var2, (int)var3, (int)var4, (int)var5, var11, this.getRenderIndex(var6, var7));
                  } else if (var17 == CSS.Value.LOWER_ROMAN) {
                     this.drawLetter(var1, 'i', (int)var2, (int)var3, (int)var4, (int)var5, var11, this.getRenderIndex(var6, var7));
                  } else if (var17 == CSS.Value.UPPER_ROMAN) {
                     this.drawLetter(var1, 'I', (int)var2, (int)var3, (int)var4, (int)var5, var11, this.getRenderIndex(var6, var7));
                  }
               } else {
                  this.drawShape(var1, var17, (int)var2, (int)var3, (int)var4, (int)var5, var11);
               }

            }
         }
      }

      void drawIcon(Graphics var1, int var2, int var3, int var4, int var5, float var6, Component var7) {
         int var8 = this.isLeftToRight ? -(this.img.getIconWidth() + this.bulletgap) : var4 + this.bulletgap;
         int var9 = var2 + var8;
         int var10 = Math.max(var3, var3 + (int)(var6 * (float)var5) - this.img.getIconHeight());
         this.img.paintIcon(var7, var1, var9, var10);
      }

      void drawShape(Graphics var1, CSS.Value var2, int var3, int var4, int var5, int var6, float var7) {
         int var8 = this.isLeftToRight ? -(this.bulletgap + 8) : var5 + this.bulletgap;
         int var9 = var3 + var8;
         int var10 = Math.max(var4, var4 + (int)(var7 * (float)var6) - 8);
         if (var2 == CSS.Value.SQUARE) {
            var1.drawRect(var9, var10, 8, 8);
         } else if (var2 == CSS.Value.CIRCLE) {
            var1.drawOval(var9, var10, 8, 8);
         } else {
            var1.fillOval(var9, var10, 8, 8);
         }

      }

      void drawLetter(Graphics var1, char var2, int var3, int var4, int var5, int var6, float var7, int var8) {
         String var9 = this.formatItemNum(var8, var2);
         var9 = this.isLeftToRight ? var9 + "." : "." + var9;
         FontMetrics var10 = SwingUtilities2.getFontMetrics((JComponent)null, (Graphics)var1);
         int var11 = SwingUtilities2.stringWidth((JComponent)null, var10, var9);
         int var12 = this.isLeftToRight ? -(var11 + this.bulletgap) : var5 + this.bulletgap;
         int var13 = var3 + var12;
         int var14 = Math.max(var4 + var10.getAscent(), var4 + (int)((float)var6 * var7));
         SwingUtilities2.drawString((JComponent)null, var1, (String)var9, var13, var14);
      }

      String formatItemNum(int var1, char var2) {
         String var3 = "1";
         boolean var4 = false;
         String var5;
         switch(var2) {
         case '1':
         default:
            var5 = String.valueOf(var1);
            break;
         case 'A':
            var4 = true;
         case 'a':
            var5 = this.formatAlphaNumerals(var1);
            break;
         case 'I':
            var4 = true;
         case 'i':
            var5 = this.formatRomanNumerals(var1);
         }

         if (var4) {
            var5 = var5.toUpperCase();
         }

         return var5;
      }

      String formatAlphaNumerals(int var1) {
         String var2;
         if (var1 > 26) {
            var2 = this.formatAlphaNumerals(var1 / 26) + this.formatAlphaNumerals(var1 % 26);
         } else {
            var2 = String.valueOf((char)(97 + var1 - 1));
         }

         return var2;
      }

      String formatRomanNumerals(int var1) {
         return this.formatRomanNumerals(0, var1);
      }

      String formatRomanNumerals(int var1, int var2) {
         return var2 < 10 ? this.formatRomanDigit(var1, var2) : this.formatRomanNumerals(var1 + 1, var2 / 10) + this.formatRomanDigit(var1, var2 % 10);
      }

      String formatRomanDigit(int var1, int var2) {
         String var3 = "";
         if (var2 == 9) {
            var3 = var3 + romanChars[var1][0];
            var3 = var3 + romanChars[var1 + 1][0];
            return var3;
         } else if (var2 == 4) {
            var3 = var3 + romanChars[var1][0];
            var3 = var3 + romanChars[var1][1];
            return var3;
         } else {
            if (var2 >= 5) {
               var3 = var3 + romanChars[var1][1];
               var2 -= 5;
            }

            for(int var4 = 0; var4 < var2; ++var4) {
               var3 = var3 + romanChars[var1][0];
            }

            return var3;
         }
      }
   }

   public static class BoxPainter implements Serializable {
      float topMargin;
      float bottomMargin;
      float leftMargin;
      float rightMargin;
      short marginFlags;
      Border border;
      Insets binsets;
      CSS css;
      StyleSheet ss;
      Color bg;
      StyleSheet.BackgroundImagePainter bgPainter;

      BoxPainter(AttributeSet var1, CSS var2, StyleSheet var3) {
         this.ss = var3;
         this.css = var2;
         this.border = this.getBorder(var1);
         this.binsets = this.border.getBorderInsets((Component)null);
         this.topMargin = this.getLength(CSS.Attribute.MARGIN_TOP, var1);
         this.bottomMargin = this.getLength(CSS.Attribute.MARGIN_BOTTOM, var1);
         this.leftMargin = this.getLength(CSS.Attribute.MARGIN_LEFT, var1);
         this.rightMargin = this.getLength(CSS.Attribute.MARGIN_RIGHT, var1);
         this.bg = var3.getBackground(var1);
         if (var3.getBackgroundImage(var1) != null) {
            this.bgPainter = new StyleSheet.BackgroundImagePainter(var1, var2, var3);
         }

      }

      Border getBorder(AttributeSet var1) {
         return new CSSBorder(var1);
      }

      Color getBorderColor(AttributeSet var1) {
         Color var2 = this.css.getColor(var1, CSS.Attribute.BORDER_COLOR);
         if (var2 == null) {
            var2 = this.css.getColor(var1, CSS.Attribute.COLOR);
            if (var2 == null) {
               return Color.black;
            }
         }

         return var2;
      }

      public float getInset(int var1, View var2) {
         AttributeSet var3 = var2.getAttributes();
         float var4 = 0.0F;
         switch(var1) {
         case 1:
            var4 += this.topMargin;
            var4 += (float)this.binsets.top;
            var4 += this.getLength(CSS.Attribute.PADDING_TOP, var3);
            break;
         case 2:
            var4 += this.getOrientationMargin(StyleSheet.BoxPainter.HorizontalMargin.LEFT, this.leftMargin, var3, isLeftToRight(var2));
            var4 += (float)this.binsets.left;
            var4 += this.getLength(CSS.Attribute.PADDING_LEFT, var3);
            break;
         case 3:
            var4 += this.bottomMargin;
            var4 += (float)this.binsets.bottom;
            var4 += this.getLength(CSS.Attribute.PADDING_BOTTOM, var3);
            break;
         case 4:
            var4 += this.getOrientationMargin(StyleSheet.BoxPainter.HorizontalMargin.RIGHT, this.rightMargin, var3, isLeftToRight(var2));
            var4 += (float)this.binsets.right;
            var4 += this.getLength(CSS.Attribute.PADDING_RIGHT, var3);
            break;
         default:
            throw new IllegalArgumentException("Invalid side: " + var1);
         }

         return var4;
      }

      public void paint(Graphics var1, float var2, float var3, float var4, float var5, View var6) {
         float var7 = 0.0F;
         float var8 = 0.0F;
         float var9 = 0.0F;
         float var10 = 0.0F;
         AttributeSet var11 = var6.getAttributes();
         boolean var12 = isLeftToRight(var6);
         float var13 = this.getOrientationMargin(StyleSheet.BoxPainter.HorizontalMargin.LEFT, this.leftMargin, var11, var12);
         float var14 = this.getOrientationMargin(StyleSheet.BoxPainter.HorizontalMargin.RIGHT, this.rightMargin, var11, var12);
         if (!(var6 instanceof HTMLEditorKit.HTMLFactory.BodyBlockView)) {
            var7 = var13;
            var8 = this.topMargin;
            var9 = -(var13 + var14);
            var10 = -(this.topMargin + this.bottomMargin);
         }

         if (this.bg != null) {
            var1.setColor(this.bg);
            var1.fillRect((int)(var2 + var7), (int)(var3 + var8), (int)(var4 + var9), (int)(var5 + var10));
         }

         if (this.bgPainter != null) {
            this.bgPainter.paint(var1, var2 + var7, var3 + var8, var4 + var9, var5 + var10, var6);
         }

         var2 += var13;
         var3 += this.topMargin;
         var4 -= var13 + var14;
         var5 -= this.topMargin + this.bottomMargin;
         if (this.border instanceof BevelBorder) {
            int var15 = (int)this.getLength(CSS.Attribute.BORDER_TOP_WIDTH, var11);

            for(int var16 = var15 - 1; var16 >= 0; --var16) {
               this.border.paintBorder((Component)null, var1, (int)var2 + var16, (int)var3 + var16, (int)var4 - 2 * var16, (int)var5 - 2 * var16);
            }
         } else {
            this.border.paintBorder((Component)null, var1, (int)var2, (int)var3, (int)var4, (int)var5);
         }

      }

      float getLength(CSS.Attribute var1, AttributeSet var2) {
         return this.css.getLength(var2, var1, this.ss);
      }

      static boolean isLeftToRight(View var0) {
         boolean var1 = true;
         Container var2;
         if (isOrientationAware(var0) && var0 != null && (var2 = var0.getContainer()) != null) {
            var1 = var2.getComponentOrientation().isLeftToRight();
         }

         return var1;
      }

      static boolean isOrientationAware(View var0) {
         boolean var1 = false;
         AttributeSet var2;
         Object var3;
         if (var0 != null && (var2 = var0.getElement().getAttributes()) != null && (var3 = var2.getAttribute(StyleConstants.NameAttribute)) instanceof HTML.Tag && (var3 == HTML.Tag.DIR || var3 == HTML.Tag.MENU || var3 == HTML.Tag.UL || var3 == HTML.Tag.OL)) {
            var1 = true;
         }

         return var1;
      }

      float getOrientationMargin(StyleSheet.BoxPainter.HorizontalMargin var1, float var2, AttributeSet var3, boolean var4) {
         float var5 = var2;
         float var6 = var2;
         Object var7 = null;
         switch(var1) {
         case RIGHT:
            var6 = var4 ? this.getLength(CSS.Attribute.MARGIN_RIGHT_LTR, var3) : this.getLength(CSS.Attribute.MARGIN_RIGHT_RTL, var3);
            var7 = var3.getAttribute(CSS.Attribute.MARGIN_RIGHT);
            break;
         case LEFT:
            var6 = var4 ? this.getLength(CSS.Attribute.MARGIN_LEFT_LTR, var3) : this.getLength(CSS.Attribute.MARGIN_LEFT_RTL, var3);
            var7 = var3.getAttribute(CSS.Attribute.MARGIN_LEFT);
         }

         if (var7 == null && var6 != -2.14748365E9F) {
            var5 = var6;
         }

         return var5;
      }

      static enum HorizontalMargin {
         LEFT,
         RIGHT;
      }
   }

   private static class SearchBuffer {
      static Stack<StyleSheet.SearchBuffer> searchBuffers = new Stack();
      Vector vector = null;
      StringBuffer stringBuffer = null;
      Hashtable hashtable = null;

      static StyleSheet.SearchBuffer obtainSearchBuffer() {
         StyleSheet.SearchBuffer var0;
         try {
            if (!searchBuffers.empty()) {
               var0 = (StyleSheet.SearchBuffer)searchBuffers.pop();
            } else {
               var0 = new StyleSheet.SearchBuffer();
            }
         } catch (EmptyStackException var2) {
            var0 = new StyleSheet.SearchBuffer();
         }

         return var0;
      }

      static void releaseSearchBuffer(StyleSheet.SearchBuffer var0) {
         var0.empty();
         searchBuffers.push(var0);
      }

      StringBuffer getStringBuffer() {
         if (this.stringBuffer == null) {
            this.stringBuffer = new StringBuffer();
         }

         return this.stringBuffer;
      }

      Vector getVector() {
         if (this.vector == null) {
            this.vector = new Vector();
         }

         return this.vector;
      }

      Hashtable getHashtable() {
         if (this.hashtable == null) {
            this.hashtable = new Hashtable();
         }

         return this.hashtable;
      }

      void empty() {
         if (this.stringBuffer != null) {
            this.stringBuffer.setLength(0);
         }

         if (this.vector != null) {
            this.vector.removeAllElements();
         }

         if (this.hashtable != null) {
            this.hashtable.clear();
         }

      }
   }

   class SmallConversionSet extends StyleContext.SmallAttributeSet {
      public SmallConversionSet(AttributeSet var2) {
         super((AttributeSet)var2);
      }

      public boolean isDefined(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               return super.isDefined(var2);
            }
         }

         return super.isDefined(var1);
      }

      public Object getAttribute(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               Object var3 = super.getAttribute(var2);
               if (var3 != null) {
                  return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)var1, var3);
               }
            }
         }

         return super.getAttribute(var1);
      }
   }

   class LargeConversionSet extends SimpleAttributeSet {
      public LargeConversionSet(AttributeSet var2) {
         super(var2);
      }

      public LargeConversionSet() {
      }

      public boolean isDefined(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               return super.isDefined(var2);
            }
         }

         return super.isDefined(var1);
      }

      public Object getAttribute(Object var1) {
         if (var1 instanceof StyleConstants) {
            CSS.Attribute var2 = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)var1);
            if (var2 != null) {
               Object var3 = super.getAttribute(var2);
               if (var3 != null) {
                  return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)var1, var3);
               }
            }
         }

         return super.getAttribute(var1);
      }
   }
}
