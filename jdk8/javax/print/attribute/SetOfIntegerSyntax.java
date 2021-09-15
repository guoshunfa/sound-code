package javax.print.attribute;

import java.io.Serializable;
import java.util.Vector;

public abstract class SetOfIntegerSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = 3666874174847632203L;
   private int[][] members;

   protected SetOfIntegerSyntax(String var1) {
      this.members = parse(var1);
   }

   private static int[][] parse(String var0) {
      Vector var1 = new Vector();
      int var2 = var0 == null ? 0 : var0.length();
      int var3 = 0;
      byte var4 = 0;
      int var5 = 0;
      int var6 = 0;

      while(true) {
         while(var3 < var2) {
            char var7 = var0.charAt(var3++);
            int var8;
            switch(var4) {
            case 0:
               if (Character.isWhitespace(var7)) {
                  var4 = 0;
               } else {
                  if ((var8 = Character.digit((char)var7, 10)) == -1) {
                     throw new IllegalArgumentException();
                  }

                  var5 = var8;
                  var4 = 1;
               }
               break;
            case 1:
               if (Character.isWhitespace(var7)) {
                  var4 = 2;
               } else if ((var8 = Character.digit((char)var7, 10)) != -1) {
                  var5 = 10 * var5 + var8;
                  var4 = 1;
               } else {
                  if (var7 != '-' && var7 != ':') {
                     if (var7 != ',') {
                        throw new IllegalArgumentException();
                     }

                     accumulate(var1, var5, var5);
                     var4 = 6;
                     continue;
                  }

                  var4 = 3;
               }
               break;
            case 2:
               if (Character.isWhitespace(var7)) {
                  var4 = 2;
               } else {
                  if (var7 != '-' && var7 != ':') {
                     if (var7 != ',') {
                        throw new IllegalArgumentException();
                     }

                     accumulate(var1, var5, var5);
                     var4 = 6;
                     continue;
                  }

                  var4 = 3;
               }
               break;
            case 3:
               if (Character.isWhitespace(var7)) {
                  var4 = 3;
               } else {
                  if ((var8 = Character.digit((char)var7, 10)) == -1) {
                     throw new IllegalArgumentException();
                  }

                  var6 = var8;
                  var4 = 4;
               }
               break;
            case 4:
               if (Character.isWhitespace(var7)) {
                  var4 = 5;
               } else if ((var8 = Character.digit((char)var7, 10)) != -1) {
                  var6 = 10 * var6 + var8;
                  var4 = 4;
               } else {
                  if (var7 != ',') {
                     throw new IllegalArgumentException();
                  }

                  accumulate(var1, var5, var6);
                  var4 = 6;
               }
               break;
            case 5:
               if (Character.isWhitespace(var7)) {
                  var4 = 5;
               } else {
                  if (var7 != ',') {
                     throw new IllegalArgumentException();
                  }

                  accumulate(var1, var5, var6);
                  var4 = 6;
               }
               break;
            case 6:
               if (Character.isWhitespace(var7)) {
                  var4 = 6;
               } else {
                  if ((var8 = Character.digit((char)var7, 10)) == -1) {
                     throw new IllegalArgumentException();
                  }

                  var5 = var8;
                  var4 = 1;
               }
            }
         }

         switch(var4) {
         case 0:
         default:
            break;
         case 1:
         case 2:
            accumulate(var1, var5, var5);
            break;
         case 3:
         case 6:
            throw new IllegalArgumentException();
         case 4:
         case 5:
            accumulate(var1, var5, var6);
         }

         return canonicalArrayForm(var1);
      }
   }

   private static void accumulate(Vector var0, int var1, int var2) {
      if (var1 <= var2) {
         var0.add(new int[]{var1, var2});

         for(int var3 = var0.size() - 2; var3 >= 0; --var3) {
            int[] var4 = (int[])((int[])var0.elementAt(var3));
            int var5 = var4[0];
            int var6 = var4[1];
            int[] var7 = (int[])((int[])var0.elementAt(var3 + 1));
            int var8 = var7[0];
            int var9 = var7[1];
            if (Math.max(var5, var8) - Math.min(var6, var9) <= 1) {
               var0.setElementAt(new int[]{Math.min(var5, var8), Math.max(var6, var9)}, var3);
               var0.remove(var3 + 1);
            } else {
               if (var5 <= var8) {
                  break;
               }

               var0.setElementAt(var7, var3);
               var0.setElementAt(var4, var3 + 1);
            }
         }
      }

   }

   private static int[][] canonicalArrayForm(Vector var0) {
      return (int[][])((int[][])var0.toArray(new int[var0.size()][]));
   }

   protected SetOfIntegerSyntax(int[][] var1) {
      this.members = parse(var1);
   }

   private static int[][] parse(int[][] var0) {
      Vector var1 = new Vector();
      int var2 = var0 == null ? 0 : var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4;
         int var5;
         if (var0[var3].length == 1) {
            var4 = var5 = var0[var3][0];
         } else {
            if (var0[var3].length != 2) {
               throw new IllegalArgumentException();
            }

            var4 = var0[var3][0];
            var5 = var0[var3][1];
         }

         if (var4 <= var5 && var4 < 0) {
            throw new IllegalArgumentException();
         }

         accumulate(var1, var4, var5);
      }

      return canonicalArrayForm(var1);
   }

   protected SetOfIntegerSyntax(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.members = new int[][]{{var1, var1}};
      }
   }

   protected SetOfIntegerSyntax(int var1, int var2) {
      if (var1 <= var2 && var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.members = var1 <= var2 ? new int[][]{{var1, var2}} : new int[0][];
      }
   }

   public int[][] getMembers() {
      int var1 = this.members.length;
      int[][] var2 = new int[var1][];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = new int[]{this.members[var3][0], this.members[var3][1]};
      }

      return var2;
   }

   public boolean contains(int var1) {
      int var2 = this.members.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var1 < this.members[var3][0]) {
            return false;
         }

         if (var1 <= this.members[var3][1]) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(IntegerSyntax var1) {
      return this.contains(var1.getValue());
   }

   public int next(int var1) {
      int var2 = this.members.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var1 < this.members[var3][0]) {
            return this.members[var3][0];
         }

         if (var1 < this.members[var3][1]) {
            return var1 + 1;
         }
      }

      return -1;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof SetOfIntegerSyntax) {
         int[][] var2 = this.members;
         int[][] var3 = ((SetOfIntegerSyntax)var1).members;
         int var4 = var2.length;
         int var5 = var3.length;
         if (var4 != var5) {
            return false;
         } else {
            for(int var6 = 0; var6 < var4; ++var6) {
               if (var2[var6][0] != var3[var6][0] || var2[var6][1] != var3[var6][1]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.members.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1 += this.members[var3][0] + this.members[var3][1];
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      int var2 = this.members.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var3 > 0) {
            var1.append(',');
         }

         var1.append(this.members[var3][0]);
         if (this.members[var3][0] != this.members[var3][1]) {
            var1.append('-');
            var1.append(this.members[var3][1]);
         }
      }

      return var1.toString();
   }
}
