package javax.swing.text.rtf;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

class RTFAttributes {
   static RTFAttribute[] attributes;

   static Dictionary<String, RTFAttribute> attributesByKeyword() {
      Hashtable var0 = new Hashtable(attributes.length);
      RTFAttribute[] var1 = attributes;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         RTFAttribute var4 = var1[var3];
         var0.put(var4.rtfName(), var4);
      }

      return var0;
   }

   static {
      Vector var0 = new Vector();
      byte var1 = 0;
      byte var2 = 1;
      boolean var3 = true;
      byte var4 = 3;
      byte var5 = 4;
      Boolean var6 = true;
      Boolean var7 = false;
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, StyleConstants.Italic, "i"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, StyleConstants.Bold, "b"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, StyleConstants.Underline, "ul"));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var2, StyleConstants.LeftIndent, "li", 0.0F, 0));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var2, StyleConstants.RightIndent, "ri", 0.0F, 0));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var2, StyleConstants.FirstLineIndent, "fi", 0.0F, 0));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, StyleConstants.Alignment, "ql", 0));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, StyleConstants.Alignment, "qr", 2));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, StyleConstants.Alignment, "qc", 1));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, StyleConstants.Alignment, "qj", 3));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var2, StyleConstants.SpaceAbove, "sa", 0));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var2, StyleConstants.SpaceBelow, "sb", 0));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_alignment", "tqr", 1));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_alignment", "tqc", 2));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_alignment", "tqdec", 4));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_leader", "tldot", 1));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_leader", "tlhyph", 2));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_leader", "tlul", 3));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_leader", "tlth", 4));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var5, "tab_leader", "tleq", 5));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "caps", "caps"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "outl", "outl"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "scaps", "scaps"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "shad", "shad"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "v", "v"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "strike", "strike"));
      var0.addElement(new RTFAttributes.BooleanAttribute(var1, "deleted", "deleted"));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var4, "saveformat", "defformat", "RTF"));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var4, "landscape", "landscape"));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "paperw", "paperw", 12240));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "paperh", "paperh", 15840));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "margl", "margl", 1800));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "margr", "margr", 1800));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "margt", "margt", 1440));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "margb", "margb", 1440));
      var0.addElement(RTFAttributes.NumericAttribute.NewTwips(var4, "gutter", "gutter", 0));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, "widowctrl", "nowidctlpar", var7));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var2, "widowctrl", "widctlpar", var6));
      var0.addElement(new RTFAttributes.AssertiveAttribute(var4, "widowctrl", "widowctrl", var6));
      RTFAttribute[] var8 = new RTFAttribute[var0.size()];
      var0.copyInto(var8);
      attributes = var8;
   }

   static class NumericAttribute extends RTFAttributes.GenericAttribute implements RTFAttribute {
      int rtfDefault;
      Number swingDefault;
      float scale;

      protected NumericAttribute(int var1, Object var2, String var3) {
         super(var1, var2, var3);
         this.rtfDefault = 0;
         this.swingDefault = null;
         this.scale = 1.0F;
      }

      public NumericAttribute(int var1, Object var2, String var3, int var4, int var5) {
         this(var1, var2, var3, var4, var5, 1.0F);
      }

      public NumericAttribute(int var1, Object var2, String var3, Number var4, int var5, float var6) {
         super(var1, var2, var3);
         this.swingDefault = var4;
         this.rtfDefault = var5;
         this.scale = var6;
      }

      public static RTFAttributes.NumericAttribute NewTwips(int var0, Object var1, String var2, float var3, int var4) {
         return new RTFAttributes.NumericAttribute(var0, var1, var2, new Float(var3), var4, 20.0F);
      }

      public static RTFAttributes.NumericAttribute NewTwips(int var0, Object var1, String var2, int var3) {
         return new RTFAttributes.NumericAttribute(var0, var1, var2, (Number)null, var3, 20.0F);
      }

      public boolean set(MutableAttributeSet var1) {
         return false;
      }

      public boolean set(MutableAttributeSet var1, int var2) {
         Object var3;
         if (this.scale == 1.0F) {
            var3 = var2;
         } else {
            var3 = new Float((float)var2 / this.scale);
         }

         var1.addAttribute(this.swingName, var3);
         return true;
      }

      public boolean setDefault(MutableAttributeSet var1) {
         Number var2 = (Number)var1.getAttribute(this.swingName);
         if (var2 == null) {
            var2 = this.swingDefault;
         }

         if (var2 == null || (this.scale != 1.0F || var2.intValue() != this.rtfDefault) && Math.round(var2.floatValue() * this.scale) != this.rtfDefault) {
            this.set(var1, this.rtfDefault);
            return true;
         } else {
            return true;
         }
      }

      public boolean writeValue(Object var1, RTFGenerator var2, boolean var3) throws IOException {
         Number var4 = (Number)var1;
         if (var4 == null) {
            var4 = this.swingDefault;
         }

         if (var4 == null) {
            return true;
         } else {
            int var5 = Math.round(var4.floatValue() * this.scale);
            if (var3 || var5 != this.rtfDefault) {
               var2.writeControlWord(this.rtfName, var5);
            }

            return true;
         }
      }
   }

   static class AssertiveAttribute extends RTFAttributes.GenericAttribute implements RTFAttribute {
      Object swingValue;

      public AssertiveAttribute(int var1, Object var2, String var3) {
         super(var1, var2, var3);
         this.swingValue = true;
      }

      public AssertiveAttribute(int var1, Object var2, String var3, Object var4) {
         super(var1, var2, var3);
         this.swingValue = var4;
      }

      public AssertiveAttribute(int var1, Object var2, String var3, int var4) {
         super(var1, var2, var3);
         this.swingValue = var4;
      }

      public boolean set(MutableAttributeSet var1) {
         if (this.swingValue == null) {
            var1.removeAttribute(this.swingName);
         } else {
            var1.addAttribute(this.swingName, this.swingValue);
         }

         return true;
      }

      public boolean set(MutableAttributeSet var1, int var2) {
         return false;
      }

      public boolean setDefault(MutableAttributeSet var1) {
         var1.removeAttribute(this.swingName);
         return true;
      }

      public boolean writeValue(Object var1, RTFGenerator var2, boolean var3) throws IOException {
         if (var1 == null) {
            return !var3;
         } else if (var1.equals(this.swingValue)) {
            var2.writeControlWord(this.rtfName);
            return true;
         } else {
            return !var3;
         }
      }
   }

   static class BooleanAttribute extends RTFAttributes.GenericAttribute implements RTFAttribute {
      boolean rtfDefault;
      boolean swingDefault;
      protected static final Boolean True = true;
      protected static final Boolean False = false;

      public BooleanAttribute(int var1, Object var2, String var3, boolean var4, boolean var5) {
         super(var1, var2, var3);
         this.swingDefault = var4;
         this.rtfDefault = var5;
      }

      public BooleanAttribute(int var1, Object var2, String var3) {
         super(var1, var2, var3);
         this.swingDefault = false;
         this.rtfDefault = false;
      }

      public boolean set(MutableAttributeSet var1) {
         var1.addAttribute(this.swingName, True);
         return true;
      }

      public boolean set(MutableAttributeSet var1, int var2) {
         Boolean var3 = var2 != 0 ? True : False;
         var1.addAttribute(this.swingName, var3);
         return true;
      }

      public boolean setDefault(MutableAttributeSet var1) {
         if (this.swingDefault != this.rtfDefault || var1.getAttribute(this.swingName) != null) {
            var1.addAttribute(this.swingName, this.rtfDefault);
         }

         return true;
      }

      public boolean writeValue(Object var1, RTFGenerator var2, boolean var3) throws IOException {
         Boolean var4;
         if (var1 == null) {
            var4 = this.swingDefault;
         } else {
            var4 = (Boolean)var1;
         }

         if (var3 || var4 != this.rtfDefault) {
            if (var4) {
               var2.writeControlWord(this.rtfName);
            } else {
               var2.writeControlWord(this.rtfName, 0);
            }
         }

         return true;
      }
   }

   abstract static class GenericAttribute {
      int domain;
      Object swingName;
      String rtfName;

      protected GenericAttribute(int var1, Object var2, String var3) {
         this.domain = var1;
         this.swingName = var2;
         this.rtfName = var3;
      }

      public int domain() {
         return this.domain;
      }

      public Object swingName() {
         return this.swingName;
      }

      public String rtfName() {
         return this.rtfName;
      }

      abstract boolean set(MutableAttributeSet var1);

      abstract boolean set(MutableAttributeSet var1, int var2);

      abstract boolean setDefault(MutableAttributeSet var1);

      public boolean write(AttributeSet var1, RTFGenerator var2, boolean var3) throws IOException {
         return this.writeValue(var1.getAttribute(this.swingName), var2, var3);
      }

      public boolean writeValue(Object var1, RTFGenerator var2, boolean var3) throws IOException {
         return false;
      }
   }
}
