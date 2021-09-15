package sun.security.jgss;

import java.util.LinkedList;
import org.ietf.jgss.MessageProp;

public class TokenTracker {
   static final int MAX_INTERVALS = 5;
   private int initNumber;
   private int windowStart;
   private int expectedNumber;
   private int windowStartIndex = 0;
   private LinkedList<TokenTracker.Entry> list = new LinkedList();

   public TokenTracker(int var1) {
      this.initNumber = var1;
      this.windowStart = var1;
      this.expectedNumber = var1;
      TokenTracker.Entry var2 = new TokenTracker.Entry(var1 - 1);
      this.list.add(var2);
   }

   private int getIntervalIndex(int var1) {
      TokenTracker.Entry var2 = null;

      int var3;
      for(var3 = this.list.size() - 1; var3 >= 0; --var3) {
         var2 = (TokenTracker.Entry)this.list.get(var3);
         if (var2.compareTo(var1) <= 0) {
            break;
         }
      }

      return var3;
   }

   public final synchronized void getProps(int var1, MessageProp var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      int var7 = this.getIntervalIndex(var1);
      TokenTracker.Entry var8 = null;
      if (var7 != -1) {
         var8 = (TokenTracker.Entry)this.list.get(var7);
      }

      if (var1 == this.expectedNumber) {
         ++this.expectedNumber;
      } else if (var8 != null && var8.contains(var1)) {
         var6 = true;
      } else if (this.expectedNumber >= this.initNumber) {
         if (var1 > this.expectedNumber) {
            var3 = true;
         } else if (var1 >= this.windowStart) {
            var5 = true;
         } else if (var1 >= this.initNumber) {
            var4 = true;
         } else {
            var3 = true;
         }
      } else if (var1 > this.expectedNumber) {
         if (var1 < this.initNumber) {
            var3 = true;
         } else if (this.windowStart >= this.initNumber) {
            if (var1 >= this.windowStart) {
               var5 = true;
            } else {
               var4 = true;
            }
         } else {
            var4 = true;
         }
      } else if (this.windowStart > this.expectedNumber) {
         var5 = true;
      } else if (var1 < this.windowStart) {
         var4 = true;
      } else {
         var5 = true;
      }

      if (!var6 && !var4) {
         this.add(var1, var7);
      }

      if (var3) {
         this.expectedNumber = var1 + 1;
      }

      var2.setSupplementaryStates(var6, var4, var5, var3, 0, (String)null);
   }

   private void add(int var1, int var2) {
      TokenTracker.Entry var4 = null;
      TokenTracker.Entry var5 = null;
      boolean var6 = false;
      boolean var7 = false;
      if (var2 != -1) {
         var4 = (TokenTracker.Entry)this.list.get(var2);
         if (var1 == var4.getEnd() + 1) {
            var4.setEnd(var1);
            var6 = true;
         }
      }

      int var8 = var2 + 1;
      if (var8 < this.list.size()) {
         var5 = (TokenTracker.Entry)this.list.get(var8);
         if (var1 == var5.getStart() - 1) {
            if (!var6) {
               var5.setStart(var1);
            } else {
               var5.setStart(var4.getStart());
               this.list.remove(var2);
               if (this.windowStartIndex > var2) {
                  --this.windowStartIndex;
               }
            }

            var7 = true;
         }
      }

      if (!var7 && !var6) {
         TokenTracker.Entry var3;
         if (this.list.size() < 5) {
            var3 = new TokenTracker.Entry(var1);
            if (var2 < this.windowStartIndex) {
               ++this.windowStartIndex;
            }
         } else {
            int var9 = this.windowStartIndex;
            if (this.windowStartIndex == this.list.size() - 1) {
               this.windowStartIndex = 0;
            }

            var3 = (TokenTracker.Entry)this.list.remove(var9);
            this.windowStart = ((TokenTracker.Entry)this.list.get(this.windowStartIndex)).getStart();
            var3.setStart(var1);
            var3.setEnd(var1);
            if (var2 >= var9) {
               --var2;
            } else if (var9 != this.windowStartIndex) {
               if (var2 == -1) {
                  this.windowStart = var1;
               }
            } else {
               ++this.windowStartIndex;
            }
         }

         this.list.add(var2 + 1, var3);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("TokenTracker: ");
      var1.append(" initNumber=").append(this.initNumber);
      var1.append(" windowStart=").append(this.windowStart);
      var1.append(" expectedNumber=").append(this.expectedNumber);
      var1.append(" windowStartIndex=").append(this.windowStartIndex);
      var1.append("\n\tIntervals are: {");

      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         if (var2 != 0) {
            var1.append(", ");
         }

         var1.append(((TokenTracker.Entry)this.list.get(var2)).toString());
      }

      var1.append('}');
      return var1.toString();
   }

   class Entry {
      private int start;
      private int end;

      Entry(int var2) {
         this.start = var2;
         this.end = var2;
      }

      final int compareTo(int var1) {
         if (this.start > var1) {
            return 1;
         } else {
            return this.end < var1 ? -1 : 0;
         }
      }

      final boolean contains(int var1) {
         return var1 >= this.start && var1 <= this.end;
      }

      final void append(int var1) {
         if (var1 == this.end + 1) {
            this.end = var1;
         }

      }

      final void setInterval(int var1, int var2) {
         this.start = var1;
         this.end = var2;
      }

      final void setEnd(int var1) {
         this.end = var1;
      }

      final void setStart(int var1) {
         this.start = var1;
      }

      final int getStart() {
         return this.start;
      }

      final int getEnd() {
         return this.end;
      }

      public String toString() {
         return "[" + this.start + ", " + this.end + "]";
      }
   }
}
