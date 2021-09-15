package javax.print;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

class MimeType implements Serializable, Cloneable {
   private static final long serialVersionUID = -2785720609362367683L;
   private String[] myPieces;
   private transient String myStringValue = null;
   private transient MimeType.ParameterMapEntrySet myEntrySet = null;
   private transient MimeType.ParameterMap myParameterMap = null;
   private static final int TOKEN_LEXEME = 0;
   private static final int QUOTED_STRING_LEXEME = 1;
   private static final int TSPECIAL_LEXEME = 2;
   private static final int EOF_LEXEME = 3;
   private static final int ILLEGAL_LEXEME = 4;

   public MimeType(String var1) {
      this.parse(var1);
   }

   public String getMimeType() {
      return this.getStringValue();
   }

   public String getMediaType() {
      return this.myPieces[0];
   }

   public String getMediaSubtype() {
      return this.myPieces[1];
   }

   public Map getParameterMap() {
      if (this.myParameterMap == null) {
         this.myParameterMap = new MimeType.ParameterMap();
      }

      return this.myParameterMap;
   }

   public String toString() {
      return this.getStringValue();
   }

   public int hashCode() {
      return this.getStringValue().hashCode();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof MimeType && this.getStringValue().equals(((MimeType)var1).getStringValue());
   }

   private String getStringValue() {
      if (this.myStringValue == null) {
         StringBuffer var1 = new StringBuffer();
         var1.append(this.myPieces[0]);
         var1.append('/');
         var1.append(this.myPieces[1]);
         int var2 = this.myPieces.length;

         for(int var3 = 2; var3 < var2; var3 += 2) {
            var1.append(';');
            var1.append(' ');
            var1.append(this.myPieces[var3]);
            var1.append('=');
            var1.append(addQuotes(this.myPieces[var3 + 1]));
         }

         this.myStringValue = var1.toString();
      }

      return this.myStringValue;
   }

   private static String toUnicodeLowerCase(String var0) {
      int var1 = var0.length();
      char[] var2 = new char[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = Character.toLowerCase(var0.charAt(var3));
      }

      return new String(var2);
   }

   private static String removeBackslashes(String var0) {
      int var1 = var0.length();
      char[] var2 = new char[var1];
      int var4 = 0;

      for(int var3 = 0; var3 < var1; ++var3) {
         char var5 = var0.charAt(var3);
         if (var5 == '\\') {
            ++var3;
            var5 = var0.charAt(var3);
         }

         var2[var4++] = var5;
      }

      return new String(var2, 0, var4);
   }

   private static String addQuotes(String var0) {
      int var1 = var0.length();
      StringBuffer var4 = new StringBuffer(var1 + 2);
      var4.append('"');

      for(int var2 = 0; var2 < var1; ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '"') {
            var4.append('\\');
         }

         var4.append(var3);
      }

      var4.append('"');
      return var4.toString();
   }

   private void parse(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         MimeType.LexicalAnalyzer var2 = new MimeType.LexicalAnalyzer(var1);
         Vector var4 = new Vector();
         boolean var5 = false;
         boolean var6 = false;
         if (var2.getLexemeType() != 0) {
            throw new IllegalArgumentException();
         } else {
            String var7 = toUnicodeLowerCase(var2.getLexeme());
            var4.add(var7);
            var2.nextLexeme();
            var5 = var7.equals("text");
            if (var2.getLexemeType() == 2 && var2.getLexemeFirstCharacter() == '/') {
               var2.nextLexeme();
               if (var2.getLexemeType() != 0) {
                  throw new IllegalArgumentException();
               } else {
                  var4.add(toUnicodeLowerCase(var2.getLexeme()));
                  var2.nextLexeme();

                  while(true) {
                     if (var2.getLexemeType() == 2 && var2.getLexemeFirstCharacter() == ';') {
                        var2.nextLexeme();
                        if (var2.getLexemeType() != 0) {
                           throw new IllegalArgumentException();
                        }

                        var7 = toUnicodeLowerCase(var2.getLexeme());
                        var4.add(var7);
                        var2.nextLexeme();
                        var6 = var7.equals("charset");
                        if (var2.getLexemeType() == 2 && var2.getLexemeFirstCharacter() == '=') {
                           var2.nextLexeme();
                           if (var2.getLexemeType() == 0) {
                              var7 = var2.getLexeme();
                              var4.add(var5 && var6 ? toUnicodeLowerCase(var7) : var7);
                              var2.nextLexeme();
                              continue;
                           }

                           if (var2.getLexemeType() != 1) {
                              throw new IllegalArgumentException();
                           }

                           var7 = removeBackslashes(var2.getLexeme());
                           var4.add(var5 && var6 ? toUnicodeLowerCase(var7) : var7);
                           var2.nextLexeme();
                           continue;
                        }

                        throw new IllegalArgumentException();
                     }

                     if (var2.getLexemeType() != 3) {
                        throw new IllegalArgumentException();
                     }

                     int var11 = var4.size();
                     this.myPieces = (String[])((String[])var4.toArray(new String[var11]));

                     for(int var8 = 4; var8 < var11; var8 += 2) {
                        int var9;
                        for(var9 = 2; var9 < var8 && this.myPieces[var9].compareTo(this.myPieces[var8]) <= 0; var9 += 2) {
                        }

                        while(var9 < var8) {
                           String var10 = this.myPieces[var9];
                           this.myPieces[var9] = this.myPieces[var8];
                           this.myPieces[var8] = var10;
                           var10 = this.myPieces[var9 + 1];
                           this.myPieces[var9 + 1] = this.myPieces[var8 + 1];
                           this.myPieces[var8 + 1] = var10;
                           var9 += 2;
                        }
                     }

                     return;
                  }
               }
            } else {
               throw new IllegalArgumentException();
            }
         }
      }
   }

   private static class LexicalAnalyzer {
      protected String mySource;
      protected int mySourceLength;
      protected int myCurrentIndex;
      protected int myLexemeType;
      protected int myLexemeBeginIndex;
      protected int myLexemeEndIndex;

      public LexicalAnalyzer(String var1) {
         this.mySource = var1;
         this.mySourceLength = var1.length();
         this.myCurrentIndex = 0;
         this.nextLexeme();
      }

      public int getLexemeType() {
         return this.myLexemeType;
      }

      public String getLexeme() {
         return this.myLexemeBeginIndex >= this.mySourceLength ? null : this.mySource.substring(this.myLexemeBeginIndex, this.myLexemeEndIndex);
      }

      public char getLexemeFirstCharacter() {
         return this.myLexemeBeginIndex >= this.mySourceLength ? '\u0000' : this.mySource.charAt(this.myLexemeBeginIndex);
      }

      public void nextLexeme() {
         int var1 = 0;
         int var2 = 0;

         while(true) {
            while(true) {
               while(var1 >= 0) {
                  char var3;
                  switch(var1) {
                  case 0:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeType = 3;
                        this.myLexemeBeginIndex = this.mySourceLength;
                        this.myLexemeEndIndex = this.mySourceLength;
                        var1 = -1;
                     } else if (Character.isWhitespace(var3 = this.mySource.charAt(this.myCurrentIndex++))) {
                        var1 = 0;
                     } else if (var3 == '"') {
                        this.myLexemeType = 1;
                        this.myLexemeBeginIndex = this.myCurrentIndex;
                        var1 = 1;
                     } else if (var3 == '(') {
                        ++var2;
                        var1 = 3;
                     } else {
                        if (var3 != '/' && var3 != ';' && var3 != '=' && var3 != ')' && var3 != '<' && var3 != '>' && var3 != '@' && var3 != ',' && var3 != ':' && var3 != '\\' && var3 != '[' && var3 != ']' && var3 != '?') {
                           this.myLexemeType = 0;
                           this.myLexemeBeginIndex = this.myCurrentIndex - 1;
                           var1 = 5;
                           continue;
                        }

                        this.myLexemeType = 2;
                        this.myLexemeBeginIndex = this.myCurrentIndex - 1;
                        this.myLexemeEndIndex = this.myCurrentIndex;
                        var1 = -1;
                     }
                     break;
                  case 1:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeType = 4;
                        this.myLexemeBeginIndex = this.mySourceLength;
                        this.myLexemeEndIndex = this.mySourceLength;
                        var1 = -1;
                     } else if ((var3 = this.mySource.charAt(this.myCurrentIndex++)) == '"') {
                        this.myLexemeEndIndex = this.myCurrentIndex - 1;
                        var1 = -1;
                     } else if (var3 == '\\') {
                        var1 = 2;
                     } else {
                        var1 = 1;
                     }
                     break;
                  case 2:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeType = 4;
                        this.myLexemeBeginIndex = this.mySourceLength;
                        this.myLexemeEndIndex = this.mySourceLength;
                        var1 = -1;
                     } else {
                        ++this.myCurrentIndex;
                        var1 = 1;
                     }
                     break;
                  case 3:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeType = 4;
                        this.myLexemeBeginIndex = this.mySourceLength;
                        this.myLexemeEndIndex = this.mySourceLength;
                        var1 = -1;
                     } else if ((var3 = this.mySource.charAt(this.myCurrentIndex++)) == '(') {
                        ++var2;
                        var1 = 3;
                     } else if (var3 == ')') {
                        --var2;
                        var1 = var2 == 0 ? 0 : 3;
                     } else if (var3 == '\\') {
                        var1 = 4;
                     } else {
                        var1 = 3;
                     }
                     break;
                  case 4:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeType = 4;
                        this.myLexemeBeginIndex = this.mySourceLength;
                        this.myLexemeEndIndex = this.mySourceLength;
                        var1 = -1;
                     } else {
                        ++this.myCurrentIndex;
                        var1 = 3;
                     }
                     break;
                  case 5:
                     if (this.myCurrentIndex >= this.mySourceLength) {
                        this.myLexemeEndIndex = this.myCurrentIndex;
                        var1 = -1;
                     } else if (Character.isWhitespace(var3 = this.mySource.charAt(this.myCurrentIndex++))) {
                        this.myLexemeEndIndex = this.myCurrentIndex - 1;
                        var1 = -1;
                     } else if (var3 != '"' && var3 != '(' && var3 != '/' && var3 != ';' && var3 != '=' && var3 != ')' && var3 != '<' && var3 != '>' && var3 != '@' && var3 != ',' && var3 != ':' && var3 != '\\' && var3 != '[' && var3 != ']' && var3 != '?') {
                        var1 = 5;
                     } else {
                        --this.myCurrentIndex;
                        this.myLexemeEndIndex = this.myCurrentIndex;
                        var1 = -1;
                     }
                  }
               }

               return;
            }
         }
      }
   }

   private class ParameterMap extends AbstractMap {
      private ParameterMap() {
      }

      public Set entrySet() {
         if (MimeType.this.myEntrySet == null) {
            MimeType.this.myEntrySet = MimeType.this.new ParameterMapEntrySet();
         }

         return MimeType.this.myEntrySet;
      }

      // $FF: synthetic method
      ParameterMap(Object var2) {
         this();
      }
   }

   private class ParameterMapEntrySet extends AbstractSet {
      private ParameterMapEntrySet() {
      }

      public Iterator iterator() {
         return MimeType.this.new ParameterMapEntrySetIterator();
      }

      public int size() {
         return (MimeType.this.myPieces.length - 2) / 2;
      }

      // $FF: synthetic method
      ParameterMapEntrySet(Object var2) {
         this();
      }
   }

   private class ParameterMapEntrySetIterator implements Iterator {
      private int myIndex;

      private ParameterMapEntrySetIterator() {
         this.myIndex = 2;
      }

      public boolean hasNext() {
         return this.myIndex < MimeType.this.myPieces.length;
      }

      public Object next() {
         if (this.hasNext()) {
            MimeType.ParameterMapEntry var1 = MimeType.this.new ParameterMapEntry(this.myIndex);
            this.myIndex += 2;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      ParameterMapEntrySetIterator(Object var2) {
         this();
      }
   }

   private class ParameterMapEntry implements Map.Entry {
      private int myIndex;

      public ParameterMapEntry(int var2) {
         this.myIndex = var2;
      }

      public Object getKey() {
         return MimeType.this.myPieces[this.myIndex];
      }

      public Object getValue() {
         return MimeType.this.myPieces[this.myIndex + 1];
      }

      public Object setValue(Object var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         return var1 != null && var1 instanceof Map.Entry && this.getKey().equals(((Map.Entry)var1).getKey()) && this.getValue().equals(((Map.Entry)var1).getValue());
      }

      public int hashCode() {
         return this.getKey().hashCode() ^ this.getValue().hashCode();
      }
   }
}
