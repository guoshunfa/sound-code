package java.awt.font;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import sun.font.Decoration;
import sun.font.FontResolver;
import sun.text.CodePointIterator;

final class StyledParagraph {
   private int length;
   private Decoration decoration;
   private Object font;
   private Vector<Decoration> decorations;
   int[] decorationStarts;
   private Vector<Object> fonts;
   int[] fontStarts;
   private static int INITIAL_SIZE = 8;

   public StyledParagraph(AttributedCharacterIterator var1, char[] var2) {
      int var3 = var1.getBeginIndex();
      int var4 = var1.getEndIndex();
      this.length = var4 - var3;
      int var5 = var3;
      var1.first();

      int var6;
      do {
         var6 = var1.getRunLimit();
         int var7 = var5 - var3;
         Map var8 = var1.getAttributes();
         var8 = addInputMethodAttrs(var8);
         Decoration var9 = Decoration.getDecoration(var8);
         this.addDecoration(var9, var7);
         Object var10 = getGraphicOrFont(var8);
         if (var10 == null) {
            this.addFonts(var2, var8, var7, var6 - var3);
         } else {
            this.addFont(var10, var7);
         }

         var1.setIndex(var6);
         var5 = var6;
      } while(var6 < var4);

      if (this.decorations != null) {
         this.decorationStarts = addToVector(this, this.length, this.decorations, this.decorationStarts);
      }

      if (this.fonts != null) {
         this.fontStarts = addToVector(this, this.length, this.fonts, this.fontStarts);
      }

   }

   private static void insertInto(int var0, int[] var1, int var2) {
      while(true) {
         --var2;
         if (var1[var2] <= var0) {
            return;
         }

         int var10002 = var1[var2]++;
      }
   }

   public static StyledParagraph insertChar(AttributedCharacterIterator var0, char[] var1, int var2, StyledParagraph var3) {
      char var4 = var0.setIndex(var2);
      int var5 = Math.max(var2 - var0.getBeginIndex() - 1, 0);
      Map var6 = addInputMethodAttrs(var0.getAttributes());
      Decoration var7 = Decoration.getDecoration(var6);
      if (!var3.getDecorationAt(var5).equals(var7)) {
         return new StyledParagraph(var0, var1);
      } else {
         Object var8 = getGraphicOrFont(var6);
         if (var8 == null) {
            FontResolver var9 = FontResolver.getInstance();
            int var10 = var9.getFontIndex(var4);
            var8 = var9.getFont(var10, var6);
         }

         if (!var3.getFontOrGraphicAt(var5).equals(var8)) {
            return new StyledParagraph(var0, var1);
         } else {
            ++var3.length;
            if (var3.decorations != null) {
               insertInto(var5, var3.decorationStarts, var3.decorations.size());
            }

            if (var3.fonts != null) {
               insertInto(var5, var3.fontStarts, var3.fonts.size());
            }

            return var3;
         }
      }
   }

   private static void deleteFrom(int var0, int[] var1, int var2) {
      while(true) {
         --var2;
         if (var1[var2] <= var0) {
            return;
         }

         int var10002 = var1[var2]--;
      }
   }

   public static StyledParagraph deleteChar(AttributedCharacterIterator var0, char[] var1, int var2, StyledParagraph var3) {
      var2 -= var0.getBeginIndex();
      if (var3.decorations == null && var3.fonts == null) {
         --var3.length;
         return var3;
      } else if (var3.getRunLimit(var2) == var2 + 1 && (var2 == 0 || var3.getRunLimit(var2 - 1) == var2)) {
         return new StyledParagraph(var0, var1);
      } else {
         --var3.length;
         if (var3.decorations != null) {
            deleteFrom(var2, var3.decorationStarts, var3.decorations.size());
         }

         if (var3.fonts != null) {
            deleteFrom(var2, var3.fontStarts, var3.fonts.size());
         }

         return var3;
      }
   }

   public int getRunLimit(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         int var2 = this.length;
         int var3;
         if (this.decorations != null) {
            var3 = findRunContaining(var1, this.decorationStarts);
            var2 = this.decorationStarts[var3 + 1];
         }

         var3 = this.length;
         if (this.fonts != null) {
            int var4 = findRunContaining(var1, this.fontStarts);
            var3 = this.fontStarts[var4 + 1];
         }

         return Math.min(var2, var3);
      } else {
         throw new IllegalArgumentException("index out of range");
      }
   }

   public Decoration getDecorationAt(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         if (this.decorations == null) {
            return this.decoration;
         } else {
            int var2 = findRunContaining(var1, this.decorationStarts);
            return (Decoration)this.decorations.elementAt(var2);
         }
      } else {
         throw new IllegalArgumentException("index out of range");
      }
   }

   public Object getFontOrGraphicAt(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         if (this.fonts == null) {
            return this.font;
         } else {
            int var2 = findRunContaining(var1, this.fontStarts);
            return this.fonts.elementAt(var2);
         }
      } else {
         throw new IllegalArgumentException("index out of range");
      }
   }

   private static int findRunContaining(int var0, int[] var1) {
      int var2;
      for(var2 = 1; var1[var2] <= var0; ++var2) {
      }

      return var2 - 1;
   }

   private static int[] addToVector(Object var0, int var1, Vector var2, int[] var3) {
      if (!var2.lastElement().equals(var0)) {
         var2.addElement(var0);
         int var4 = var2.size();
         if (var3.length == var4) {
            int[] var5 = new int[var3.length * 2];
            System.arraycopy(var3, 0, var5, 0, var3.length);
            var3 = var5;
         }

         var3[var4 - 1] = var1;
      }

      return var3;
   }

   private void addDecoration(Decoration var1, int var2) {
      if (this.decorations != null) {
         this.decorationStarts = addToVector(var1, var2, this.decorations, this.decorationStarts);
      } else if (this.decoration == null) {
         this.decoration = var1;
      } else if (!this.decoration.equals(var1)) {
         this.decorations = new Vector(INITIAL_SIZE);
         this.decorations.addElement(this.decoration);
         this.decorations.addElement(var1);
         this.decorationStarts = new int[INITIAL_SIZE];
         this.decorationStarts[0] = 0;
         this.decorationStarts[1] = var2;
      }

   }

   private void addFont(Object var1, int var2) {
      if (this.fonts != null) {
         this.fontStarts = addToVector(var1, var2, this.fonts, this.fontStarts);
      } else if (this.font == null) {
         this.font = var1;
      } else if (!this.font.equals(var1)) {
         this.fonts = new Vector(INITIAL_SIZE);
         this.fonts.addElement(this.font);
         this.fonts.addElement(var1);
         this.fontStarts = new int[INITIAL_SIZE];
         this.fontStarts[0] = 0;
         this.fontStarts[1] = var2;
      }

   }

   private void addFonts(char[] var1, Map<? extends AttributedCharacterIterator.Attribute, ?> var2, int var3, int var4) {
      FontResolver var5 = FontResolver.getInstance();
      CodePointIterator var6 = CodePointIterator.create(var1, var3, var4);

      for(int var7 = var6.charIndex(); var7 < var4; var7 = var6.charIndex()) {
         int var8 = var5.nextFontRunIndex(var6);
         this.addFont(var5.getFont(var8, var2), var7);
      }

   }

   static Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs(Map<? extends AttributedCharacterIterator.Attribute, ?> var0) {
      Object var1 = var0.get(TextAttribute.INPUT_METHOD_HIGHLIGHT);

      try {
         if (var1 != null) {
            if (var1 instanceof Annotation) {
               var1 = ((Annotation)var1).getValue();
            }

            InputMethodHighlight var2 = (InputMethodHighlight)var1;
            Map var3 = null;

            try {
               var3 = var2.getStyle();
            } catch (NoSuchMethodError var5) {
            }

            if (var3 == null) {
               Toolkit var4 = Toolkit.getDefaultToolkit();
               var3 = var4.mapInputMethodHighlight(var2);
            }

            if (var3 != null) {
               HashMap var7 = new HashMap(5, 0.9F);
               var7.putAll(var0);
               var7.putAll(var3);
               return var7;
            }
         }
      } catch (ClassCastException var6) {
      }

      return var0;
   }

   private static Object getGraphicOrFont(Map<? extends AttributedCharacterIterator.Attribute, ?> var0) {
      Object var1 = var0.get(TextAttribute.CHAR_REPLACEMENT);
      if (var1 != null) {
         return var1;
      } else {
         var1 = var0.get(TextAttribute.FONT);
         if (var1 != null) {
            return var1;
         } else {
            return var0.get(TextAttribute.FAMILY) != null ? Font.getFont(var0) : null;
         }
      }
   }
}
