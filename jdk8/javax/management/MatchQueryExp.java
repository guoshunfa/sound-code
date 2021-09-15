package javax.management;

class MatchQueryExp extends QueryEval implements QueryExp {
   private static final long serialVersionUID = -7156603696948215014L;
   private AttributeValueExp exp;
   private String pattern;

   public MatchQueryExp() {
   }

   public MatchQueryExp(AttributeValueExp var1, StringValueExp var2) {
      this.exp = var1;
      this.pattern = var2.getValue();
   }

   public AttributeValueExp getAttribute() {
      return this.exp;
   }

   public String getPattern() {
      return this.pattern;
   }

   public boolean apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      ValueExp var2 = this.exp.apply(var1);
      return !(var2 instanceof StringValueExp) ? false : wildmatch(((StringValueExp)var2).getValue(), this.pattern);
   }

   public String toString() {
      return this.exp + " like " + new StringValueExp(this.pattern);
   }

   private static boolean wildmatch(String var0, String var1) {
      int var3 = 0;
      int var4 = 0;
      int var5 = var0.length();
      int var6 = var1.length();

      while(true) {
         while(var4 < var6) {
            char var2 = var1.charAt(var4++);
            if (var2 != '?') {
               if (var2 != '[') {
                  if (var2 == '*') {
                     if (var4 >= var6) {
                        return true;
                     }

                     while(!wildmatch(var0.substring(var3), var1.substring(var4))) {
                        ++var3;
                        if (var3 >= var5) {
                           return false;
                        }
                     }

                     return true;
                  }

                  if (var2 == '\\') {
                     if (var4 >= var6 || var3 >= var5 || var1.charAt(var4++) != var0.charAt(var3++)) {
                        return false;
                     }
                  } else if (var3 >= var5 || var2 != var0.charAt(var3++)) {
                     return false;
                  }
               } else {
                  if (var3 >= var5) {
                     return false;
                  }

                  boolean var7 = true;
                  boolean var8 = false;
                  if (var1.charAt(var4) == '!') {
                     var7 = false;
                     ++var4;
                  }

                  while((var2 = var1.charAt(var4)) != ']') {
                     ++var4;
                     if (var4 >= var6) {
                        break;
                     }

                     if (var1.charAt(var4) == '-' && var4 + 1 < var6 && var1.charAt(var4 + 1) != ']') {
                        if (var0.charAt(var3) >= var1.charAt(var4 - 1) && var0.charAt(var3) <= var1.charAt(var4 + 1)) {
                           var8 = true;
                        }

                        ++var4;
                     } else if (var2 == var0.charAt(var3)) {
                        var8 = true;
                     }
                  }

                  if (var4 >= var6 || var7 != var8) {
                     return false;
                  }

                  ++var4;
                  ++var3;
               }
            } else {
               ++var3;
               if (var3 > var5) {
                  return false;
               }
            }
         }

         return var3 == var5;
      }
   }
}
