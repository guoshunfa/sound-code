package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;

public class StyleConstants {
   public static final String ComponentElementName = "component";
   public static final String IconElementName = "icon";
   public static final Object NameAttribute = new StyleConstants("name");
   public static final Object ResolveAttribute = new StyleConstants("resolver");
   public static final Object ModelAttribute = new StyleConstants("model");
   public static final Object BidiLevel = new StyleConstants.CharacterConstants("bidiLevel");
   public static final Object FontFamily = new StyleConstants.FontConstants("family");
   public static final Object Family;
   public static final Object FontSize;
   public static final Object Size;
   public static final Object Bold;
   public static final Object Italic;
   public static final Object Underline;
   public static final Object StrikeThrough;
   public static final Object Superscript;
   public static final Object Subscript;
   public static final Object Foreground;
   public static final Object Background;
   public static final Object ComponentAttribute;
   public static final Object IconAttribute;
   public static final Object ComposedTextAttribute;
   public static final Object FirstLineIndent;
   public static final Object LeftIndent;
   public static final Object RightIndent;
   public static final Object LineSpacing;
   public static final Object SpaceAbove;
   public static final Object SpaceBelow;
   public static final Object Alignment;
   public static final Object TabSet;
   public static final Object Orientation;
   public static final int ALIGN_LEFT = 0;
   public static final int ALIGN_CENTER = 1;
   public static final int ALIGN_RIGHT = 2;
   public static final int ALIGN_JUSTIFIED = 3;
   static Object[] keys;
   private String representation;

   public String toString() {
      return this.representation;
   }

   public static int getBidiLevel(AttributeSet var0) {
      Integer var1 = (Integer)var0.getAttribute(BidiLevel);
      return var1 != null ? var1 : 0;
   }

   public static void setBidiLevel(MutableAttributeSet var0, int var1) {
      var0.addAttribute(BidiLevel, var1);
   }

   public static Component getComponent(AttributeSet var0) {
      return (Component)var0.getAttribute(ComponentAttribute);
   }

   public static void setComponent(MutableAttributeSet var0, Component var1) {
      var0.addAttribute("$ename", "component");
      var0.addAttribute(ComponentAttribute, var1);
   }

   public static Icon getIcon(AttributeSet var0) {
      return (Icon)var0.getAttribute(IconAttribute);
   }

   public static void setIcon(MutableAttributeSet var0, Icon var1) {
      var0.addAttribute("$ename", "icon");
      var0.addAttribute(IconAttribute, var1);
   }

   public static String getFontFamily(AttributeSet var0) {
      String var1 = (String)var0.getAttribute(FontFamily);
      if (var1 == null) {
         var1 = "Monospaced";
      }

      return var1;
   }

   public static void setFontFamily(MutableAttributeSet var0, String var1) {
      var0.addAttribute(FontFamily, var1);
   }

   public static int getFontSize(AttributeSet var0) {
      Integer var1 = (Integer)var0.getAttribute(FontSize);
      return var1 != null ? var1 : 12;
   }

   public static void setFontSize(MutableAttributeSet var0, int var1) {
      var0.addAttribute(FontSize, var1);
   }

   public static boolean isBold(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(Bold);
      return var1 != null ? var1 : false;
   }

   public static void setBold(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(Bold, var1);
   }

   public static boolean isItalic(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(Italic);
      return var1 != null ? var1 : false;
   }

   public static void setItalic(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(Italic, var1);
   }

   public static boolean isUnderline(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(Underline);
      return var1 != null ? var1 : false;
   }

   public static boolean isStrikeThrough(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(StrikeThrough);
      return var1 != null ? var1 : false;
   }

   public static boolean isSuperscript(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(Superscript);
      return var1 != null ? var1 : false;
   }

   public static boolean isSubscript(AttributeSet var0) {
      Boolean var1 = (Boolean)var0.getAttribute(Subscript);
      return var1 != null ? var1 : false;
   }

   public static void setUnderline(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(Underline, var1);
   }

   public static void setStrikeThrough(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(StrikeThrough, var1);
   }

   public static void setSuperscript(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(Superscript, var1);
   }

   public static void setSubscript(MutableAttributeSet var0, boolean var1) {
      var0.addAttribute(Subscript, var1);
   }

   public static Color getForeground(AttributeSet var0) {
      Color var1 = (Color)var0.getAttribute(Foreground);
      if (var1 == null) {
         var1 = Color.black;
      }

      return var1;
   }

   public static void setForeground(MutableAttributeSet var0, Color var1) {
      var0.addAttribute(Foreground, var1);
   }

   public static Color getBackground(AttributeSet var0) {
      Color var1 = (Color)var0.getAttribute(Background);
      if (var1 == null) {
         var1 = Color.black;
      }

      return var1;
   }

   public static void setBackground(MutableAttributeSet var0, Color var1) {
      var0.addAttribute(Background, var1);
   }

   public static float getFirstLineIndent(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(FirstLineIndent);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setFirstLineIndent(MutableAttributeSet var0, float var1) {
      var0.addAttribute(FirstLineIndent, new Float(var1));
   }

   public static float getRightIndent(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(RightIndent);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setRightIndent(MutableAttributeSet var0, float var1) {
      var0.addAttribute(RightIndent, new Float(var1));
   }

   public static float getLeftIndent(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(LeftIndent);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setLeftIndent(MutableAttributeSet var0, float var1) {
      var0.addAttribute(LeftIndent, new Float(var1));
   }

   public static float getLineSpacing(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(LineSpacing);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setLineSpacing(MutableAttributeSet var0, float var1) {
      var0.addAttribute(LineSpacing, new Float(var1));
   }

   public static float getSpaceAbove(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(SpaceAbove);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setSpaceAbove(MutableAttributeSet var0, float var1) {
      var0.addAttribute(SpaceAbove, new Float(var1));
   }

   public static float getSpaceBelow(AttributeSet var0) {
      Float var1 = (Float)var0.getAttribute(SpaceBelow);
      return var1 != null ? var1 : 0.0F;
   }

   public static void setSpaceBelow(MutableAttributeSet var0, float var1) {
      var0.addAttribute(SpaceBelow, new Float(var1));
   }

   public static int getAlignment(AttributeSet var0) {
      Integer var1 = (Integer)var0.getAttribute(Alignment);
      return var1 != null ? var1 : 0;
   }

   public static void setAlignment(MutableAttributeSet var0, int var1) {
      var0.addAttribute(Alignment, var1);
   }

   public static TabSet getTabSet(AttributeSet var0) {
      TabSet var1 = (TabSet)var0.getAttribute(TabSet);
      return var1;
   }

   public static void setTabSet(MutableAttributeSet var0, TabSet var1) {
      var0.addAttribute(TabSet, var1);
   }

   StyleConstants(String var1) {
      this.representation = var1;
   }

   static {
      Family = FontFamily;
      FontSize = new StyleConstants.FontConstants("size");
      Size = FontSize;
      Bold = new StyleConstants.FontConstants("bold");
      Italic = new StyleConstants.FontConstants("italic");
      Underline = new StyleConstants.CharacterConstants("underline");
      StrikeThrough = new StyleConstants.CharacterConstants("strikethrough");
      Superscript = new StyleConstants.CharacterConstants("superscript");
      Subscript = new StyleConstants.CharacterConstants("subscript");
      Foreground = new StyleConstants.ColorConstants("foreground");
      Background = new StyleConstants.ColorConstants("background");
      ComponentAttribute = new StyleConstants.CharacterConstants("component");
      IconAttribute = new StyleConstants.CharacterConstants("icon");
      ComposedTextAttribute = new StyleConstants("composed text");
      FirstLineIndent = new StyleConstants.ParagraphConstants("FirstLineIndent");
      LeftIndent = new StyleConstants.ParagraphConstants("LeftIndent");
      RightIndent = new StyleConstants.ParagraphConstants("RightIndent");
      LineSpacing = new StyleConstants.ParagraphConstants("LineSpacing");
      SpaceAbove = new StyleConstants.ParagraphConstants("SpaceAbove");
      SpaceBelow = new StyleConstants.ParagraphConstants("SpaceBelow");
      Alignment = new StyleConstants.ParagraphConstants("Alignment");
      TabSet = new StyleConstants.ParagraphConstants("TabSet");
      Orientation = new StyleConstants.ParagraphConstants("Orientation");
      keys = new Object[]{NameAttribute, ResolveAttribute, BidiLevel, FontFamily, FontSize, Bold, Italic, Underline, StrikeThrough, Superscript, Subscript, Foreground, Background, ComponentAttribute, IconAttribute, FirstLineIndent, LeftIndent, RightIndent, LineSpacing, SpaceAbove, SpaceBelow, Alignment, TabSet, Orientation, ModelAttribute, ComposedTextAttribute};
   }

   public static class FontConstants extends StyleConstants implements AttributeSet.FontAttribute, AttributeSet.CharacterAttribute {
      private FontConstants(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      FontConstants(String var1, Object var2) {
         this(var1);
      }
   }

   public static class ColorConstants extends StyleConstants implements AttributeSet.ColorAttribute, AttributeSet.CharacterAttribute {
      private ColorConstants(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      ColorConstants(String var1, Object var2) {
         this(var1);
      }
   }

   public static class CharacterConstants extends StyleConstants implements AttributeSet.CharacterAttribute {
      private CharacterConstants(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      CharacterConstants(String var1, Object var2) {
         this(var1);
      }
   }

   public static class ParagraphConstants extends StyleConstants implements AttributeSet.ParagraphAttribute {
      private ParagraphConstants(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      ParagraphConstants(String var1, Object var2) {
         this(var1);
      }
   }
}
