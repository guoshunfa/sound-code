package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class PageRanges extends SetOfIntegerSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 8639895197656148392L;

   public PageRanges(int[][] var1) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException("members is null");
      } else {
         this.myPageRanges();
      }
   }

   public PageRanges(String var1) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException("members is null");
      } else {
         this.myPageRanges();
      }
   }

   private void myPageRanges() {
      int[][] var1 = this.getMembers();
      int var2 = var1.length;
      if (var2 == 0) {
         throw new IllegalArgumentException("members is zero-length");
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            if (var1[var3][0] < 1) {
               throw new IllegalArgumentException("Page value < 1 specified");
            }
         }

      }
   }

   public PageRanges(int var1) {
      super(var1);
      if (var1 < 1) {
         throw new IllegalArgumentException("Page value < 1 specified");
      }
   }

   public PageRanges(int var1, int var2) {
      super(var1, var2);
      if (var1 > var2) {
         throw new IllegalArgumentException("Null range specified");
      } else if (var1 < 1) {
         throw new IllegalArgumentException("Page value < 1 specified");
      }
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PageRanges;
   }

   public final Class<? extends Attribute> getCategory() {
      return PageRanges.class;
   }

   public final String getName() {
      return "page-ranges";
   }
}
