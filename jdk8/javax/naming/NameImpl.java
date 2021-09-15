package javax.naming;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

class NameImpl {
   private static final byte LEFT_TO_RIGHT = 1;
   private static final byte RIGHT_TO_LEFT = 2;
   private static final byte FLAT = 0;
   private Vector<String> components;
   private byte syntaxDirection;
   private String syntaxSeparator;
   private String syntaxSeparator2;
   private boolean syntaxCaseInsensitive;
   private boolean syntaxTrimBlanks;
   private String syntaxEscape;
   private String syntaxBeginQuote1;
   private String syntaxEndQuote1;
   private String syntaxBeginQuote2;
   private String syntaxEndQuote2;
   private String syntaxAvaSeparator;
   private String syntaxTypevalSeparator;
   private static final int STYLE_NONE = 0;
   private static final int STYLE_QUOTE1 = 1;
   private static final int STYLE_QUOTE2 = 2;
   private static final int STYLE_ESCAPE = 3;
   private int escapingStyle;

   private final boolean isA(String var1, int var2, String var3) {
      return var3 != null && var1.startsWith(var3, var2);
   }

   private final boolean isMeta(String var1, int var2) {
      return this.isA(var1, var2, this.syntaxEscape) || this.isA(var1, var2, this.syntaxBeginQuote1) || this.isA(var1, var2, this.syntaxBeginQuote2) || this.isSeparator(var1, var2);
   }

   private final boolean isSeparator(String var1, int var2) {
      return this.isA(var1, var2, this.syntaxSeparator) || this.isA(var1, var2, this.syntaxSeparator2);
   }

   private final int skipSeparator(String var1, int var2) {
      if (this.isA(var1, var2, this.syntaxSeparator)) {
         var2 += this.syntaxSeparator.length();
      } else if (this.isA(var1, var2, this.syntaxSeparator2)) {
         var2 += this.syntaxSeparator2.length();
      }

      return var2;
   }

   private final int extractComp(String var1, int var2, int var3, Vector<String> var4) throws InvalidNameException {
      boolean var7 = true;
      boolean var8 = false;

      StringBuffer var9;
      for(var9 = new StringBuffer(var3); var2 < var3; var7 = false) {
         String var5;
         String var6;
         if (var7 && ((var8 = this.isA(var1, var2, this.syntaxBeginQuote1)) || this.isA(var1, var2, this.syntaxBeginQuote2))) {
            var5 = var8 ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
            var6 = var8 ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
            if (this.escapingStyle == 0) {
               this.escapingStyle = var8 ? 1 : 2;
            }

            for(var2 += var5.length(); var2 < var3 && !var1.startsWith(var6, var2); ++var2) {
               if (this.isA(var1, var2, this.syntaxEscape) && this.isA(var1, var2 + this.syntaxEscape.length(), var6)) {
                  var2 += this.syntaxEscape.length();
               }

               var9.append(var1.charAt(var2));
            }

            if (var2 >= var3) {
               throw new InvalidNameException(var1 + ": no close quote");
            }

            var2 += var6.length();
            if (var2 != var3 && !this.isSeparator(var1, var2)) {
               throw new InvalidNameException(var1 + ": close quote appears before end of component");
            }
            break;
         }

         if (this.isSeparator(var1, var2)) {
            break;
         }

         if (this.isA(var1, var2, this.syntaxEscape)) {
            if (this.isMeta(var1, var2 + this.syntaxEscape.length())) {
               var2 += this.syntaxEscape.length();
               if (this.escapingStyle == 0) {
                  this.escapingStyle = 3;
               }
            } else if (var2 + this.syntaxEscape.length() >= var3) {
               throw new InvalidNameException(var1 + ": unescaped " + this.syntaxEscape + " at end of component");
            }
         } else if (this.isA(var1, var2, this.syntaxTypevalSeparator) && ((var8 = this.isA(var1, var2 + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote1)) || this.isA(var1, var2 + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote2))) {
            var5 = var8 ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
            var6 = var8 ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
            var2 += this.syntaxTypevalSeparator.length();
            var9.append(this.syntaxTypevalSeparator + var5);

            for(var2 += var5.length(); var2 < var3 && !var1.startsWith(var6, var2); ++var2) {
               if (this.isA(var1, var2, this.syntaxEscape) && this.isA(var1, var2 + this.syntaxEscape.length(), var6)) {
                  var2 += this.syntaxEscape.length();
               }

               var9.append(var1.charAt(var2));
            }

            if (var2 >= var3) {
               throw new InvalidNameException(var1 + ": typeval no close quote");
            }

            var2 += var6.length();
            var9.append(var6);
            if (var2 != var3 && !this.isSeparator(var1, var2)) {
               throw new InvalidNameException(var1.substring(var2) + ": typeval close quote appears before end of component");
            }
            break;
         }

         var9.append(var1.charAt(var2++));
      }

      if (this.syntaxDirection == 2) {
         var4.insertElementAt(var9.toString(), 0);
      } else {
         var4.addElement(var9.toString());
      }

      return var2;
   }

   private static boolean getBoolean(Properties var0, String var1) {
      return toBoolean(var0.getProperty(var1));
   }

   private static boolean toBoolean(String var0) {
      return var0 != null && var0.toLowerCase(Locale.ENGLISH).equals("true");
   }

   private final void recordNamingConvention(Properties var1) {
      String var2 = var1.getProperty("jndi.syntax.direction", "flat");
      if (var2.equals("left_to_right")) {
         this.syntaxDirection = 1;
      } else if (var2.equals("right_to_left")) {
         this.syntaxDirection = 2;
      } else {
         if (!var2.equals("flat")) {
            throw new IllegalArgumentException(var2 + "is not a valid value for the jndi.syntax.direction property");
         }

         this.syntaxDirection = 0;
      }

      if (this.syntaxDirection != 0) {
         this.syntaxSeparator = var1.getProperty("jndi.syntax.separator");
         this.syntaxSeparator2 = var1.getProperty("jndi.syntax.separator2");
         if (this.syntaxSeparator == null) {
            throw new IllegalArgumentException("jndi.syntax.separator property required for non-flat syntax");
         }
      } else {
         this.syntaxSeparator = null;
      }

      this.syntaxEscape = var1.getProperty("jndi.syntax.escape");
      this.syntaxCaseInsensitive = getBoolean(var1, "jndi.syntax.ignorecase");
      this.syntaxTrimBlanks = getBoolean(var1, "jndi.syntax.trimblanks");
      this.syntaxBeginQuote1 = var1.getProperty("jndi.syntax.beginquote");
      this.syntaxEndQuote1 = var1.getProperty("jndi.syntax.endquote");
      if (this.syntaxEndQuote1 == null && this.syntaxBeginQuote1 != null) {
         this.syntaxEndQuote1 = this.syntaxBeginQuote1;
      } else if (this.syntaxBeginQuote1 == null && this.syntaxEndQuote1 != null) {
         this.syntaxBeginQuote1 = this.syntaxEndQuote1;
      }

      this.syntaxBeginQuote2 = var1.getProperty("jndi.syntax.beginquote2");
      this.syntaxEndQuote2 = var1.getProperty("jndi.syntax.endquote2");
      if (this.syntaxEndQuote2 == null && this.syntaxBeginQuote2 != null) {
         this.syntaxEndQuote2 = this.syntaxBeginQuote2;
      } else if (this.syntaxBeginQuote2 == null && this.syntaxEndQuote2 != null) {
         this.syntaxBeginQuote2 = this.syntaxEndQuote2;
      }

      this.syntaxAvaSeparator = var1.getProperty("jndi.syntax.separator.ava");
      this.syntaxTypevalSeparator = var1.getProperty("jndi.syntax.separator.typeval");
   }

   NameImpl(Properties var1) {
      this.syntaxDirection = 1;
      this.syntaxSeparator = "/";
      this.syntaxSeparator2 = null;
      this.syntaxCaseInsensitive = false;
      this.syntaxTrimBlanks = false;
      this.syntaxEscape = "\\";
      this.syntaxBeginQuote1 = "\"";
      this.syntaxEndQuote1 = "\"";
      this.syntaxBeginQuote2 = "'";
      this.syntaxEndQuote2 = "'";
      this.syntaxAvaSeparator = null;
      this.syntaxTypevalSeparator = null;
      this.escapingStyle = 0;
      if (var1 != null) {
         this.recordNamingConvention(var1);
      }

      this.components = new Vector();
   }

   NameImpl(Properties var1, String var2) throws InvalidNameException {
      this(var1);
      boolean var3 = this.syntaxDirection == 2;
      boolean var4 = true;
      int var5 = var2.length();
      int var6 = 0;

      while(var6 < var5) {
         var6 = this.extractComp(var2, var6, var5, this.components);
         String var7 = var3 ? (String)this.components.firstElement() : (String)this.components.lastElement();
         if (var7.length() >= 1) {
            var4 = false;
         }

         if (var6 < var5) {
            var6 = this.skipSeparator(var2, var6);
            if (var6 == var5 && !var4) {
               if (var3) {
                  this.components.insertElementAt("", 0);
               } else {
                  this.components.addElement("");
               }
            }
         }
      }

   }

   NameImpl(Properties var1, Enumeration<String> var2) {
      this(var1);

      while(var2.hasMoreElements()) {
         this.components.addElement(var2.nextElement());
      }

   }

   private final String stringifyComp(String var1) {
      int var2 = var1.length();
      boolean var3 = false;
      boolean var4 = false;
      String var5 = null;
      String var6 = null;
      StringBuffer var7 = new StringBuffer(var2);
      if (this.syntaxSeparator != null && var1.indexOf(this.syntaxSeparator) >= 0) {
         if (this.syntaxBeginQuote1 != null) {
            var5 = this.syntaxBeginQuote1;
            var6 = this.syntaxEndQuote1;
         } else if (this.syntaxBeginQuote2 != null) {
            var5 = this.syntaxBeginQuote2;
            var6 = this.syntaxEndQuote2;
         } else if (this.syntaxEscape != null) {
            var3 = true;
         }
      }

      if (this.syntaxSeparator2 != null && var1.indexOf(this.syntaxSeparator2) >= 0) {
         if (this.syntaxBeginQuote1 != null) {
            if (var5 == null) {
               var5 = this.syntaxBeginQuote1;
               var6 = this.syntaxEndQuote1;
            }
         } else if (this.syntaxBeginQuote2 != null) {
            if (var5 == null) {
               var5 = this.syntaxBeginQuote2;
               var6 = this.syntaxEndQuote2;
            }
         } else if (this.syntaxEscape != null) {
            var4 = true;
         }
      }

      if (var5 != null) {
         var7 = var7.append(var5);
         int var8 = 0;

         while(var8 < var2) {
            if (var1.startsWith(var6, var8)) {
               var7.append(this.syntaxEscape).append(var6);
               var8 += var6.length();
            } else {
               var7.append(var1.charAt(var8++));
            }
         }

         var7.append(var6);
      } else {
         boolean var10 = true;

         for(int var9 = 0; var9 < var2; var10 = false) {
            if (var10 && this.isA(var1, var9, this.syntaxBeginQuote1)) {
               var7.append(this.syntaxEscape).append(this.syntaxBeginQuote1);
               var9 += this.syntaxBeginQuote1.length();
            } else if (var10 && this.isA(var1, var9, this.syntaxBeginQuote2)) {
               var7.append(this.syntaxEscape).append(this.syntaxBeginQuote2);
               var9 += this.syntaxBeginQuote2.length();
            } else if (this.isA(var1, var9, this.syntaxEscape)) {
               if (var9 + this.syntaxEscape.length() >= var2) {
                  var7.append(this.syntaxEscape);
               } else if (this.isMeta(var1, var9 + this.syntaxEscape.length())) {
                  var7.append(this.syntaxEscape);
               }

               var7.append(this.syntaxEscape);
               var9 += this.syntaxEscape.length();
            } else if (var3 && var1.startsWith(this.syntaxSeparator, var9)) {
               var7.append(this.syntaxEscape).append(this.syntaxSeparator);
               var9 += this.syntaxSeparator.length();
            } else if (var4 && var1.startsWith(this.syntaxSeparator2, var9)) {
               var7.append(this.syntaxEscape).append(this.syntaxSeparator2);
               var9 += this.syntaxSeparator2.length();
            } else {
               var7.append(var1.charAt(var9++));
            }
         }
      }

      return var7.toString();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      boolean var3 = true;
      int var4 = this.components.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         String var2;
         if (this.syntaxDirection == 2) {
            var2 = this.stringifyComp((String)this.components.elementAt(var4 - 1 - var5));
         } else {
            var2 = this.stringifyComp((String)this.components.elementAt(var5));
         }

         if (var5 != 0 && this.syntaxSeparator != null) {
            var1.append(this.syntaxSeparator);
         }

         if (var2.length() >= 1) {
            var3 = false;
         }

         var1 = var1.append(var2);
      }

      if (var3 && var4 >= 1 && this.syntaxSeparator != null) {
         var1 = var1.append(this.syntaxSeparator);
      }

      return var1.toString();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof NameImpl) {
         NameImpl var2 = (NameImpl)var1;
         if (var2.size() == this.size()) {
            Enumeration var3 = this.getAll();
            Enumeration var4 = var2.getAll();

            while(var3.hasMoreElements()) {
               String var5 = (String)var3.nextElement();
               String var6 = (String)var4.nextElement();
               if (this.syntaxTrimBlanks) {
                  var5 = var5.trim();
                  var6 = var6.trim();
               }

               if (this.syntaxCaseInsensitive) {
                  if (!var5.equalsIgnoreCase(var6)) {
                     return false;
                  }
               } else if (!var5.equals(var6)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   public int compareTo(NameImpl var1) {
      if (this == var1) {
         return 0;
      } else {
         int var2 = this.size();
         int var3 = var1.size();
         int var4 = Math.min(var2, var3);
         int var5 = 0;
         int var6 = 0;

         int var9;
         do {
            if (var4-- == 0) {
               return var2 - var3;
            }

            String var7 = this.get(var5++);
            String var8 = var1.get(var6++);
            if (this.syntaxTrimBlanks) {
               var7 = var7.trim();
               var8 = var8.trim();
            }

            if (this.syntaxCaseInsensitive) {
               var9 = var7.compareToIgnoreCase(var8);
            } else {
               var9 = var7.compareTo(var8);
            }
         } while(var9 == 0);

         return var9;
      }
   }

   public int size() {
      return this.components.size();
   }

   public Enumeration<String> getAll() {
      return this.components.elements();
   }

   public String get(int var1) {
      return (String)this.components.elementAt(var1);
   }

   public Enumeration<String> getPrefix(int var1) {
      if (var1 >= 0 && var1 <= this.size()) {
         return new NameImplEnumerator(this.components, 0, var1);
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public Enumeration<String> getSuffix(int var1) {
      int var2 = this.size();
      if (var1 >= 0 && var1 <= var2) {
         return new NameImplEnumerator(this.components, var1, var2);
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public boolean isEmpty() {
      return this.components.isEmpty();
   }

   public boolean startsWith(int var1, Enumeration<String> var2) {
      if (var1 >= 0 && var1 <= this.size()) {
         try {
            Enumeration var3 = this.getPrefix(var1);

            while(var3.hasMoreElements()) {
               String var4 = (String)var3.nextElement();
               String var5 = (String)var2.nextElement();
               if (this.syntaxTrimBlanks) {
                  var4 = var4.trim();
                  var5 = var5.trim();
               }

               if (this.syntaxCaseInsensitive) {
                  if (!var4.equalsIgnoreCase(var5)) {
                     return false;
                  }
               } else if (!var4.equals(var5)) {
                  return false;
               }
            }

            return true;
         } catch (NoSuchElementException var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean endsWith(int var1, Enumeration<String> var2) {
      int var3 = this.size() - var1;
      if (var3 >= 0 && var3 <= this.size()) {
         try {
            Enumeration var4 = this.getSuffix(var3);

            while(var4.hasMoreElements()) {
               String var5 = (String)var4.nextElement();
               String var6 = (String)var2.nextElement();
               if (this.syntaxTrimBlanks) {
                  var5 = var5.trim();
                  var6 = var6.trim();
               }

               if (this.syntaxCaseInsensitive) {
                  if (!var5.equalsIgnoreCase(var6)) {
                     return false;
                  }
               } else if (!var5.equals(var6)) {
                  return false;
               }
            }

            return true;
         } catch (NoSuchElementException var7) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean addAll(Enumeration<String> var1) throws InvalidNameException {
      boolean var2 = false;

      while(var1.hasMoreElements()) {
         try {
            String var3 = (String)var1.nextElement();
            if (this.size() > 0 && this.syntaxDirection == 0) {
               throw new InvalidNameException("A flat name can only have a single component");
            }

            this.components.addElement(var3);
            var2 = true;
         } catch (NoSuchElementException var4) {
            break;
         }
      }

      return var2;
   }

   public boolean addAll(int var1, Enumeration<String> var2) throws InvalidNameException {
      boolean var3 = false;

      for(int var4 = var1; var2.hasMoreElements(); ++var4) {
         try {
            String var5 = (String)var2.nextElement();
            if (this.size() > 0 && this.syntaxDirection == 0) {
               throw new InvalidNameException("A flat name can only have a single component");
            }

            this.components.insertElementAt(var5, var4);
            var3 = true;
         } catch (NoSuchElementException var6) {
            break;
         }
      }

      return var3;
   }

   public void add(String var1) throws InvalidNameException {
      if (this.size() > 0 && this.syntaxDirection == 0) {
         throw new InvalidNameException("A flat name can only have a single component");
      } else {
         this.components.addElement(var1);
      }
   }

   public void add(int var1, String var2) throws InvalidNameException {
      if (this.size() > 0 && this.syntaxDirection == 0) {
         throw new InvalidNameException("A flat name can only zero or one component");
      } else {
         this.components.insertElementAt(var2, var1);
      }
   }

   public Object remove(int var1) {
      Object var2 = this.components.elementAt(var1);
      this.components.removeElementAt(var1);
      return var2;
   }

   public int hashCode() {
      int var1 = 0;

      String var3;
      for(Enumeration var2 = this.getAll(); var2.hasMoreElements(); var1 += var3.hashCode()) {
         var3 = (String)var2.nextElement();
         if (this.syntaxTrimBlanks) {
            var3 = var3.trim();
         }

         if (this.syntaxCaseInsensitive) {
            var3 = var3.toLowerCase(Locale.ENGLISH);
         }
      }

      return var1;
   }
}
