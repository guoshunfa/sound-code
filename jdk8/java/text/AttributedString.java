package java.text;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class AttributedString {
   private static final int ARRAY_SIZE_INCREMENT = 10;
   String text;
   int runArraySize;
   int runCount;
   int[] runStarts;
   Vector<AttributedCharacterIterator.Attribute>[] runAttributes;
   Vector<Object>[] runAttributeValues;

   AttributedString(AttributedCharacterIterator[] var1) {
      if (var1 == null) {
         throw new NullPointerException("Iterators must not be null");
      } else {
         if (var1.length == 0) {
            this.text = "";
         } else {
            StringBuffer var2 = new StringBuffer();

            int var3;
            for(var3 = 0; var3 < var1.length; ++var3) {
               this.appendContents(var2, var1[var3]);
            }

            this.text = var2.toString();
            if (this.text.length() > 0) {
               var3 = 0;
               Map var4 = null;

               for(int var5 = 0; var5 < var1.length; ++var5) {
                  AttributedCharacterIterator var6 = var1[var5];
                  int var7 = var6.getBeginIndex();
                  int var8 = var6.getEndIndex();

                  for(int var9 = var7; var9 < var8; var9 = var6.getRunLimit()) {
                     var6.setIndex(var9);
                     Map var10 = var6.getAttributes();
                     if (mapsDiffer(var4, var10)) {
                        this.setAttributes(var10, var9 - var7 + var3);
                     }

                     var4 = var10;
                  }

                  var3 += var8 - var7;
               }
            }
         }

      }
   }

   public AttributedString(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.text = var1;
      }
   }

   public AttributedString(String var1, Map<? extends AttributedCharacterIterator.Attribute, ?> var2) {
      if (var1 != null && var2 != null) {
         this.text = var1;
         if (var1.length() == 0) {
            if (!var2.isEmpty()) {
               throw new IllegalArgumentException("Can't add attribute to 0-length text");
            }
         } else {
            int var3 = var2.size();
            if (var3 > 0) {
               this.createRunAttributeDataVectors();
               Vector var4 = new Vector(var3);
               Vector var5 = new Vector(var3);
               this.runAttributes[0] = var4;
               this.runAttributeValues[0] = var5;
               Iterator var6 = var2.entrySet().iterator();

               while(var6.hasNext()) {
                  Map.Entry var7 = (Map.Entry)var6.next();
                  var4.addElement(var7.getKey());
                  var5.addElement(var7.getValue());
               }
            }

         }
      } else {
         throw new NullPointerException();
      }
   }

   public AttributedString(AttributedCharacterIterator var1) {
      this(var1, var1.getBeginIndex(), var1.getEndIndex(), (AttributedCharacterIterator.Attribute[])null);
   }

   public AttributedString(AttributedCharacterIterator var1, int var2, int var3) {
      this(var1, var2, var3, (AttributedCharacterIterator.Attribute[])null);
   }

   public AttributedString(AttributedCharacterIterator var1, int var2, int var3, AttributedCharacterIterator.Attribute[] var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var5 = var1.getBeginIndex();
         int var6 = var1.getEndIndex();
         if (var2 >= var5 && var3 <= var6 && var2 <= var3) {
            StringBuffer var7 = new StringBuffer();
            var1.setIndex(var2);

            for(char var8 = var1.current(); var1.getIndex() < var3; var8 = var1.next()) {
               var7.append(var8);
            }

            this.text = var7.toString();
            if (var2 != var3) {
               HashSet var14 = new HashSet();
               if (var4 == null) {
                  var14.addAll(var1.getAllAttributeKeys());
               } else {
                  for(int var9 = 0; var9 < var4.length; ++var9) {
                     var14.add(var4[var9]);
                  }

                  var14.retainAll(var1.getAllAttributeKeys());
               }

               if (!var14.isEmpty()) {
                  Iterator var15 = var14.iterator();

                  while(var15.hasNext()) {
                     AttributedCharacterIterator.Attribute var10 = (AttributedCharacterIterator.Attribute)var15.next();
                     var1.setIndex(var5);

                     int var12;
                     for(; var1.getIndex() < var3; var1.setIndex(var12)) {
                        int var11 = var1.getRunStart(var10);
                        var12 = var1.getRunLimit(var10);
                        Object var13 = var1.getAttribute(var10);
                        if (var13 != null) {
                           if (var13 instanceof Annotation) {
                              if (var11 >= var2 && var12 <= var3) {
                                 this.addAttribute(var10, var13, var11 - var2, var12 - var2);
                              } else if (var12 > var3) {
                                 break;
                              }
                           } else {
                              if (var11 >= var3) {
                                 break;
                              }

                              if (var12 > var2) {
                                 if (var11 < var2) {
                                    var11 = var2;
                                 }

                                 if (var12 > var3) {
                                    var12 = var3;
                                 }

                                 if (var11 != var12) {
                                    this.addAttribute(var10, var13, var11 - var2, var12 - var2);
                                 }
                              }
                           }
                        }
                     }
                  }

               }
            }
         } else {
            throw new IllegalArgumentException("Invalid substring range");
         }
      }
   }

   public void addAttribute(AttributedCharacterIterator.Attribute var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var3 = this.length();
         if (var3 == 0) {
            throw new IllegalArgumentException("Can't add attribute to 0-length text");
         } else {
            this.addAttributeImpl(var1, var2, 0, var3);
         }
      }
   }

   public void addAttribute(AttributedCharacterIterator.Attribute var1, Object var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var3 >= 0 && var4 <= this.length() && var3 < var4) {
         this.addAttributeImpl(var1, var2, var3, var4);
      } else {
         throw new IllegalArgumentException("Invalid substring range");
      }
   }

   public void addAttributes(Map<? extends AttributedCharacterIterator.Attribute, ?> var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 <= this.length() && var2 <= var3) {
         if (var2 == var3) {
            if (!var1.isEmpty()) {
               throw new IllegalArgumentException("Can't add attribute to 0-length text");
            }
         } else {
            if (this.runCount == 0) {
               this.createRunAttributeDataVectors();
            }

            int var4 = this.ensureRunBreak(var2);
            int var5 = this.ensureRunBreak(var3);
            Iterator var6 = var1.entrySet().iterator();

            while(var6.hasNext()) {
               Map.Entry var7 = (Map.Entry)var6.next();
               this.addAttributeRunData((AttributedCharacterIterator.Attribute)var7.getKey(), var7.getValue(), var4, var5);
            }

         }
      } else {
         throw new IllegalArgumentException("Invalid substring range");
      }
   }

   private synchronized void addAttributeImpl(AttributedCharacterIterator.Attribute var1, Object var2, int var3, int var4) {
      if (this.runCount == 0) {
         this.createRunAttributeDataVectors();
      }

      int var5 = this.ensureRunBreak(var3);
      int var6 = this.ensureRunBreak(var4);
      this.addAttributeRunData(var1, var2, var5, var6);
   }

   private final void createRunAttributeDataVectors() {
      int[] var1 = new int[10];
      Vector[] var2 = (Vector[])(new Vector[10]);
      Vector[] var3 = (Vector[])(new Vector[10]);
      this.runStarts = var1;
      this.runAttributes = var2;
      this.runAttributeValues = var3;
      this.runArraySize = 10;
      this.runCount = 1;
   }

   private final int ensureRunBreak(int var1) {
      return this.ensureRunBreak(var1, true);
   }

   private final int ensureRunBreak(int var1, boolean var2) {
      if (var1 == this.length()) {
         return this.runCount;
      } else {
         int var3;
         for(var3 = 0; var3 < this.runCount && this.runStarts[var3] < var1; ++var3) {
         }

         if (var3 < this.runCount && this.runStarts[var3] == var1) {
            return var3;
         } else {
            if (this.runCount == this.runArraySize) {
               int var4 = this.runArraySize + 10;
               int[] var5 = new int[var4];
               Vector[] var6 = (Vector[])(new Vector[var4]);
               Vector[] var7 = (Vector[])(new Vector[var4]);

               for(int var8 = 0; var8 < this.runArraySize; ++var8) {
                  var5[var8] = this.runStarts[var8];
                  var6[var8] = this.runAttributes[var8];
                  var7[var8] = this.runAttributeValues[var8];
               }

               this.runStarts = var5;
               this.runAttributes = var6;
               this.runAttributeValues = var7;
               this.runArraySize = var4;
            }

            Vector var9 = null;
            Vector var10 = null;
            if (var2) {
               Vector var11 = this.runAttributes[var3 - 1];
               Vector var13 = this.runAttributeValues[var3 - 1];
               if (var11 != null) {
                  var9 = new Vector(var11);
               }

               if (var13 != null) {
                  var10 = new Vector(var13);
               }
            }

            ++this.runCount;

            for(int var12 = this.runCount - 1; var12 > var3; --var12) {
               this.runStarts[var12] = this.runStarts[var12 - 1];
               this.runAttributes[var12] = this.runAttributes[var12 - 1];
               this.runAttributeValues[var12] = this.runAttributeValues[var12 - 1];
            }

            this.runStarts[var3] = var1;
            this.runAttributes[var3] = var9;
            this.runAttributeValues[var3] = var10;
            return var3;
         }
      }
   }

   private void addAttributeRunData(AttributedCharacterIterator.Attribute var1, Object var2, int var3, int var4) {
      for(int var5 = var3; var5 < var4; ++var5) {
         int var6 = -1;
         if (this.runAttributes[var5] == null) {
            Vector var7 = new Vector();
            Vector var8 = new Vector();
            this.runAttributes[var5] = var7;
            this.runAttributeValues[var5] = var8;
         } else {
            var6 = this.runAttributes[var5].indexOf(var1);
         }

         if (var6 == -1) {
            int var10 = this.runAttributes[var5].size();
            this.runAttributes[var5].addElement(var1);

            try {
               this.runAttributeValues[var5].addElement(var2);
            } catch (Exception var9) {
               this.runAttributes[var5].setSize(var10);
               this.runAttributeValues[var5].setSize(var10);
            }
         } else {
            this.runAttributeValues[var5].set(var6, var2);
         }
      }

   }

   public AttributedCharacterIterator getIterator() {
      return this.getIterator((AttributedCharacterIterator.Attribute[])null, 0, this.length());
   }

   public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] var1) {
      return this.getIterator(var1, 0, this.length());
   }

   public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] var1, int var2, int var3) {
      return new AttributedString.AttributedStringIterator(var1, var2, var3);
   }

   int length() {
      return this.text.length();
   }

   private char charAt(int var1) {
      return this.text.charAt(var1);
   }

   private synchronized Object getAttribute(AttributedCharacterIterator.Attribute var1, int var2) {
      Vector var3 = this.runAttributes[var2];
      Vector var4 = this.runAttributeValues[var2];
      if (var3 == null) {
         return null;
      } else {
         int var5 = var3.indexOf(var1);
         return var5 != -1 ? var4.elementAt(var5) : null;
      }
   }

   private Object getAttributeCheckRange(AttributedCharacterIterator.Attribute var1, int var2, int var3, int var4) {
      Object var5 = this.getAttribute(var1, var2);
      if (var5 instanceof Annotation) {
         int var6;
         int var7;
         if (var3 > 0) {
            var6 = var2;

            for(var7 = this.runStarts[var2]; var7 >= var3 && valuesMatch(var5, this.getAttribute(var1, var6 - 1)); var7 = this.runStarts[var6]) {
               --var6;
            }

            if (var7 < var3) {
               return null;
            }
         }

         var6 = this.length();
         if (var4 < var6) {
            var7 = var2;

            int var8;
            for(var8 = var2 < this.runCount - 1 ? this.runStarts[var2 + 1] : var6; var8 <= var4 && valuesMatch(var5, this.getAttribute(var1, var7 + 1)); var8 = var7 < this.runCount - 1 ? this.runStarts[var7 + 1] : var6) {
               ++var7;
            }

            if (var8 > var4) {
               return null;
            }
         }
      }

      return var5;
   }

   private boolean attributeValuesMatch(Set<? extends AttributedCharacterIterator.Attribute> var1, int var2, int var3) {
      Iterator var4 = var1.iterator();

      AttributedCharacterIterator.Attribute var5;
      do {
         if (!var4.hasNext()) {
            return true;
         }

         var5 = (AttributedCharacterIterator.Attribute)var4.next();
      } while(valuesMatch(this.getAttribute(var5, var2), this.getAttribute(var5, var3)));

      return false;
   }

   private static final boolean valuesMatch(Object var0, Object var1) {
      if (var0 == null) {
         return var1 == null;
      } else {
         return var0.equals(var1);
      }
   }

   private final void appendContents(StringBuffer var1, CharacterIterator var2) {
      int var3 = var2.getBeginIndex();
      int var4 = var2.getEndIndex();

      while(var3 < var4) {
         var2.setIndex(var3++);
         var1.append(var2.current());
      }

   }

   private void setAttributes(Map<AttributedCharacterIterator.Attribute, Object> var1, int var2) {
      if (this.runCount == 0) {
         this.createRunAttributeDataVectors();
      }

      int var3 = this.ensureRunBreak(var2, false);
      int var4;
      if (var1 != null && (var4 = var1.size()) > 0) {
         Vector var5 = new Vector(var4);
         Vector var6 = new Vector(var4);
         Iterator var7 = var1.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry var8 = (Map.Entry)var7.next();
            var5.add(var8.getKey());
            var6.add(var8.getValue());
         }

         this.runAttributes[var3] = var5;
         this.runAttributeValues[var3] = var6;
      }

   }

   private static <K, V> boolean mapsDiffer(Map<K, V> var0, Map<K, V> var1) {
      if (var0 != null) {
         return !var0.equals(var1);
      } else {
         return var1 != null && var1.size() > 0;
      }
   }

   private final class AttributeMap extends AbstractMap<AttributedCharacterIterator.Attribute, Object> {
      int runIndex;
      int beginIndex;
      int endIndex;

      AttributeMap(int var2, int var3, int var4) {
         this.runIndex = var2;
         this.beginIndex = var3;
         this.endIndex = var4;
      }

      public Set<Map.Entry<AttributedCharacterIterator.Attribute, Object>> entrySet() {
         HashSet var1 = new HashSet();
         synchronized(AttributedString.this) {
            int var3 = AttributedString.this.runAttributes[this.runIndex].size();

            for(int var4 = 0; var4 < var3; ++var4) {
               AttributedCharacterIterator.Attribute var5 = (AttributedCharacterIterator.Attribute)AttributedString.this.runAttributes[this.runIndex].get(var4);
               Object var6 = AttributedString.this.runAttributeValues[this.runIndex].get(var4);
               if (var6 instanceof Annotation) {
                  var6 = AttributedString.this.getAttributeCheckRange(var5, this.runIndex, this.beginIndex, this.endIndex);
                  if (var6 == null) {
                     continue;
                  }
               }

               AttributeEntry var7 = new AttributeEntry(var5, var6);
               var1.add(var7);
            }

            return var1;
         }
      }

      public Object get(Object var1) {
         return AttributedString.this.getAttributeCheckRange((AttributedCharacterIterator.Attribute)var1, this.runIndex, this.beginIndex, this.endIndex);
      }
   }

   private final class AttributedStringIterator implements AttributedCharacterIterator {
      private int beginIndex;
      private int endIndex;
      private AttributedCharacterIterator.Attribute[] relevantAttributes;
      private int currentIndex;
      private int currentRunIndex;
      private int currentRunStart;
      private int currentRunLimit;

      AttributedStringIterator(AttributedCharacterIterator.Attribute[] var2, int var3, int var4) {
         if (var3 >= 0 && var3 <= var4 && var4 <= AttributedString.this.length()) {
            this.beginIndex = var3;
            this.endIndex = var4;
            this.currentIndex = var3;
            this.updateRunInfo();
            if (var2 != null) {
               this.relevantAttributes = (AttributedCharacterIterator.Attribute[])var2.clone();
            }

         } else {
            throw new IllegalArgumentException("Invalid substring range");
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof AttributedString.AttributedStringIterator)) {
            return false;
         } else {
            AttributedString.AttributedStringIterator var2 = (AttributedString.AttributedStringIterator)var1;
            if (AttributedString.this != var2.getString()) {
               return false;
            } else {
               return this.currentIndex == var2.currentIndex && this.beginIndex == var2.beginIndex && this.endIndex == var2.endIndex;
            }
         }
      }

      public int hashCode() {
         return AttributedString.this.text.hashCode() ^ this.currentIndex ^ this.beginIndex ^ this.endIndex;
      }

      public Object clone() {
         try {
            AttributedString.AttributedStringIterator var1 = (AttributedString.AttributedStringIterator)super.clone();
            return var1;
         } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
         }
      }

      public char first() {
         return this.internalSetIndex(this.beginIndex);
      }

      public char last() {
         return this.endIndex == this.beginIndex ? this.internalSetIndex(this.endIndex) : this.internalSetIndex(this.endIndex - 1);
      }

      public char current() {
         return this.currentIndex == this.endIndex ? '\uffff' : AttributedString.this.charAt(this.currentIndex);
      }

      public char next() {
         return this.currentIndex < this.endIndex ? this.internalSetIndex(this.currentIndex + 1) : '\uffff';
      }

      public char previous() {
         return this.currentIndex > this.beginIndex ? this.internalSetIndex(this.currentIndex - 1) : '\uffff';
      }

      public char setIndex(int var1) {
         if (var1 >= this.beginIndex && var1 <= this.endIndex) {
            return this.internalSetIndex(var1);
         } else {
            throw new IllegalArgumentException("Invalid index");
         }
      }

      public int getBeginIndex() {
         return this.beginIndex;
      }

      public int getEndIndex() {
         return this.endIndex;
      }

      public int getIndex() {
         return this.currentIndex;
      }

      public int getRunStart() {
         return this.currentRunStart;
      }

      public int getRunStart(AttributedCharacterIterator.Attribute var1) {
         if (this.currentRunStart != this.beginIndex && this.currentRunIndex != -1) {
            Object var2 = this.getAttribute(var1);
            int var3 = this.currentRunStart;

            for(int var4 = this.currentRunIndex; var3 > this.beginIndex && AttributedString.valuesMatch(var2, AttributedString.this.getAttribute(var1, var4 - 1)); var3 = AttributedString.this.runStarts[var4]) {
               --var4;
            }

            if (var3 < this.beginIndex) {
               var3 = this.beginIndex;
            }

            return var3;
         } else {
            return this.currentRunStart;
         }
      }

      public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> var1) {
         if (this.currentRunStart != this.beginIndex && this.currentRunIndex != -1) {
            int var2 = this.currentRunStart;

            for(int var3 = this.currentRunIndex; var2 > this.beginIndex && AttributedString.this.attributeValuesMatch(var1, this.currentRunIndex, var3 - 1); var2 = AttributedString.this.runStarts[var3]) {
               --var3;
            }

            if (var2 < this.beginIndex) {
               var2 = this.beginIndex;
            }

            return var2;
         } else {
            return this.currentRunStart;
         }
      }

      public int getRunLimit() {
         return this.currentRunLimit;
      }

      public int getRunLimit(AttributedCharacterIterator.Attribute var1) {
         if (this.currentRunLimit != this.endIndex && this.currentRunIndex != -1) {
            Object var2 = this.getAttribute(var1);
            int var3 = this.currentRunLimit;

            for(int var4 = this.currentRunIndex; var3 < this.endIndex && AttributedString.valuesMatch(var2, AttributedString.this.getAttribute(var1, var4 + 1)); var3 = var4 < AttributedString.this.runCount - 1 ? AttributedString.this.runStarts[var4 + 1] : this.endIndex) {
               ++var4;
            }

            if (var3 > this.endIndex) {
               var3 = this.endIndex;
            }

            return var3;
         } else {
            return this.currentRunLimit;
         }
      }

      public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> var1) {
         if (this.currentRunLimit != this.endIndex && this.currentRunIndex != -1) {
            int var2 = this.currentRunLimit;

            for(int var3 = this.currentRunIndex; var2 < this.endIndex && AttributedString.this.attributeValuesMatch(var1, this.currentRunIndex, var3 + 1); var2 = var3 < AttributedString.this.runCount - 1 ? AttributedString.this.runStarts[var3 + 1] : this.endIndex) {
               ++var3;
            }

            if (var2 > this.endIndex) {
               var2 = this.endIndex;
            }

            return var2;
         } else {
            return this.currentRunLimit;
         }
      }

      public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
         return (Map)(AttributedString.this.runAttributes != null && this.currentRunIndex != -1 && AttributedString.this.runAttributes[this.currentRunIndex] != null ? AttributedString.this.new AttributeMap(this.currentRunIndex, this.beginIndex, this.endIndex) : new Hashtable());
      }

      public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() {
         if (AttributedString.this.runAttributes == null) {
            return new HashSet();
         } else {
            synchronized(AttributedString.this) {
               HashSet var2 = new HashSet();

               for(int var3 = 0; var3 < AttributedString.this.runCount; ++var3) {
                  if (AttributedString.this.runStarts[var3] < this.endIndex && (var3 == AttributedString.this.runCount - 1 || AttributedString.this.runStarts[var3 + 1] > this.beginIndex)) {
                     Vector var4 = AttributedString.this.runAttributes[var3];
                     if (var4 != null) {
                        int var5 = var4.size();

                        while(var5-- > 0) {
                           var2.add(var4.get(var5));
                        }
                     }
                  }
               }

               return var2;
            }
         }
      }

      public Object getAttribute(AttributedCharacterIterator.Attribute var1) {
         int var2 = this.currentRunIndex;
         return var2 < 0 ? null : AttributedString.this.getAttributeCheckRange(var1, var2, this.beginIndex, this.endIndex);
      }

      private AttributedString getString() {
         return AttributedString.this;
      }

      private char internalSetIndex(int var1) {
         this.currentIndex = var1;
         if (var1 < this.currentRunStart || var1 >= this.currentRunLimit) {
            this.updateRunInfo();
         }

         return this.currentIndex == this.endIndex ? '\uffff' : AttributedString.this.charAt(var1);
      }

      private void updateRunInfo() {
         if (this.currentIndex == this.endIndex) {
            this.currentRunStart = this.currentRunLimit = this.endIndex;
            this.currentRunIndex = -1;
         } else {
            synchronized(AttributedString.this) {
               int var2;
               for(var2 = -1; var2 < AttributedString.this.runCount - 1 && AttributedString.this.runStarts[var2 + 1] <= this.currentIndex; ++var2) {
               }

               this.currentRunIndex = var2;
               if (var2 >= 0) {
                  this.currentRunStart = AttributedString.this.runStarts[var2];
                  if (this.currentRunStart < this.beginIndex) {
                     this.currentRunStart = this.beginIndex;
                  }
               } else {
                  this.currentRunStart = this.beginIndex;
               }

               if (var2 < AttributedString.this.runCount - 1) {
                  this.currentRunLimit = AttributedString.this.runStarts[var2 + 1];
                  if (this.currentRunLimit > this.endIndex) {
                     this.currentRunLimit = this.endIndex;
                  }
               } else {
                  this.currentRunLimit = this.endIndex;
               }
            }
         }

      }
   }
}
