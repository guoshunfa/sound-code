package javax.swing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RowFilter<M, I> {
   private static void checkIndices(int[] var0) {
      for(int var1 = var0.length - 1; var1 >= 0; --var1) {
         if (var0[var1] < 0) {
            throw new IllegalArgumentException("Index must be >= 0");
         }
      }

   }

   public static <M, I> RowFilter<M, I> regexFilter(String var0, int... var1) {
      return new RowFilter.RegexFilter(Pattern.compile(var0), var1);
   }

   public static <M, I> RowFilter<M, I> dateFilter(RowFilter.ComparisonType var0, Date var1, int... var2) {
      return new RowFilter.DateFilter(var0, var1.getTime(), var2);
   }

   public static <M, I> RowFilter<M, I> numberFilter(RowFilter.ComparisonType var0, Number var1, int... var2) {
      return new RowFilter.NumberFilter(var0, var1, var2);
   }

   public static <M, I> RowFilter<M, I> orFilter(Iterable<? extends RowFilter<? super M, ? super I>> var0) {
      return new RowFilter.OrFilter(var0);
   }

   public static <M, I> RowFilter<M, I> andFilter(Iterable<? extends RowFilter<? super M, ? super I>> var0) {
      return new RowFilter.AndFilter(var0);
   }

   public static <M, I> RowFilter<M, I> notFilter(RowFilter<M, I> var0) {
      return new RowFilter.NotFilter(var0);
   }

   public abstract boolean include(RowFilter.Entry<? extends M, ? extends I> var1);

   private static class NotFilter<M, I> extends RowFilter<M, I> {
      private RowFilter<M, I> filter;

      NotFilter(RowFilter<M, I> var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("filter must be non-null");
         } else {
            this.filter = var1;
         }
      }

      public boolean include(RowFilter.Entry<? extends M, ? extends I> var1) {
         return !this.filter.include(var1);
      }
   }

   private static class AndFilter<M, I> extends RowFilter.OrFilter<M, I> {
      AndFilter(Iterable<? extends RowFilter<? super M, ? super I>> var1) {
         super(var1);
      }

      public boolean include(RowFilter.Entry<? extends M, ? extends I> var1) {
         Iterator var2 = this.filters.iterator();

         RowFilter var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (RowFilter)var2.next();
         } while(var3.include(var1));

         return false;
      }
   }

   private static class OrFilter<M, I> extends RowFilter<M, I> {
      List<RowFilter<? super M, ? super I>> filters = new ArrayList();

      OrFilter(Iterable<? extends RowFilter<? super M, ? super I>> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            RowFilter var3 = (RowFilter)var2.next();
            if (var3 == null) {
               throw new IllegalArgumentException("Filter must be non-null");
            }

            this.filters.add(var3);
         }

      }

      public boolean include(RowFilter.Entry<? extends M, ? extends I> var1) {
         Iterator var2 = this.filters.iterator();

         RowFilter var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (RowFilter)var2.next();
         } while(!var3.include(var1));

         return true;
      }
   }

   private static class NumberFilter extends RowFilter.GeneralFilter {
      private boolean isComparable;
      private Number number;
      private RowFilter.ComparisonType type;

      NumberFilter(RowFilter.ComparisonType var1, Number var2, int[] var3) {
         super(var3);
         if (var1 != null && var2 != null) {
            this.type = var1;
            this.number = var2;
            this.isComparable = var2 instanceof Comparable;
         } else {
            throw new IllegalArgumentException("type and number must be non-null");
         }
      }

      protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> var1, int var2) {
         Object var3 = var1.getValue(var2);
         if (var3 instanceof Number) {
            boolean var4 = true;
            Class var6 = var3.getClass();
            int var5;
            if (this.number.getClass() == var6 && this.isComparable) {
               var5 = ((Comparable)this.number).compareTo(var3);
            } else {
               var5 = this.longCompare((Number)var3);
            }

            switch(this.type) {
            case BEFORE:
               return var5 > 0;
            case AFTER:
               return var5 < 0;
            case EQUAL:
               return var5 == 0;
            case NOT_EQUAL:
               return var5 != 0;
            }
         }

         return false;
      }

      private int longCompare(Number var1) {
         long var2 = this.number.longValue() - var1.longValue();
         if (var2 < 0L) {
            return -1;
         } else {
            return var2 > 0L ? 1 : 0;
         }
      }
   }

   private static class DateFilter extends RowFilter.GeneralFilter {
      private long date;
      private RowFilter.ComparisonType type;

      DateFilter(RowFilter.ComparisonType var1, long var2, int[] var4) {
         super(var4);
         if (var1 == null) {
            throw new IllegalArgumentException("type must be non-null");
         } else {
            this.type = var1;
            this.date = var2;
         }
      }

      protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> var1, int var2) {
         Object var3 = var1.getValue(var2);
         if (var3 instanceof Date) {
            long var4 = ((Date)var3).getTime();
            switch(this.type) {
            case BEFORE:
               return var4 < this.date;
            case AFTER:
               return var4 > this.date;
            case EQUAL:
               return var4 == this.date;
            case NOT_EQUAL:
               return var4 != this.date;
            }
         }

         return false;
      }
   }

   private static class RegexFilter extends RowFilter.GeneralFilter {
      private Matcher matcher;

      RegexFilter(Pattern var1, int[] var2) {
         super(var2);
         if (var1 == null) {
            throw new IllegalArgumentException("Pattern must be non-null");
         } else {
            this.matcher = var1.matcher("");
         }
      }

      protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> var1, int var2) {
         this.matcher.reset(var1.getStringValue(var2));
         return this.matcher.find();
      }
   }

   private abstract static class GeneralFilter extends RowFilter<Object, Object> {
      private int[] columns;

      GeneralFilter(int[] var1) {
         RowFilter.checkIndices(var1);
         this.columns = var1;
      }

      public boolean include(RowFilter.Entry<? extends Object, ? extends Object> var1) {
         int var2 = var1.getValueCount();
         if (this.columns.length > 0) {
            for(int var3 = this.columns.length - 1; var3 >= 0; --var3) {
               int var4 = this.columns[var3];
               if (var4 < var2 && this.include(var1, var4)) {
                  return true;
               }
            }
         } else {
            while(true) {
               --var2;
               if (var2 < 0) {
                  break;
               }

               if (this.include(var1, var2)) {
                  return true;
               }
            }
         }

         return false;
      }

      protected abstract boolean include(RowFilter.Entry<? extends Object, ? extends Object> var1, int var2);
   }

   public abstract static class Entry<M, I> {
      public abstract M getModel();

      public abstract int getValueCount();

      public abstract Object getValue(int var1);

      public String getStringValue(int var1) {
         Object var2 = this.getValue(var1);
         return var2 == null ? "" : var2.toString();
      }

      public abstract I getIdentifier();
   }

   public static enum ComparisonType {
      BEFORE,
      AFTER,
      EQUAL,
      NOT_EQUAL;
   }
}
