package java.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.security.action.GetBooleanAction;

public class Arrays {
   private static final int MIN_ARRAY_SORT_GRAN = 8192;
   private static final int INSERTIONSORT_THRESHOLD = 7;

   private Arrays() {
   }

   private static void rangeCheck(int var0, int var1, int var2) {
      if (var1 > var2) {
         throw new IllegalArgumentException("fromIndex(" + var1 + ") > toIndex(" + var2 + ")");
      } else if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var2 > var0) {
         throw new ArrayIndexOutOfBoundsException(var2);
      }
   }

   public static void sort(int[] var0) {
      DualPivotQuicksort.sort((int[])var0, 0, var0.length - 1, (int[])null, 0, 0);
   }

   public static void sort(int[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((int[])var0, var1, var2 - 1, (int[])null, 0, 0);
   }

   public static void sort(long[] var0) {
      DualPivotQuicksort.sort((long[])var0, 0, var0.length - 1, (long[])null, 0, 0);
   }

   public static void sort(long[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((long[])var0, var1, var2 - 1, (long[])null, 0, 0);
   }

   public static void sort(short[] var0) {
      DualPivotQuicksort.sort((short[])var0, 0, var0.length - 1, (short[])null, 0, 0);
   }

   public static void sort(short[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((short[])var0, var1, var2 - 1, (short[])null, 0, 0);
   }

   public static void sort(char[] var0) {
      DualPivotQuicksort.sort((char[])var0, 0, var0.length - 1, (char[])null, 0, 0);
   }

   public static void sort(char[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((char[])var0, var1, var2 - 1, (char[])null, 0, 0);
   }

   public static void sort(byte[] var0) {
      DualPivotQuicksort.sort(var0, 0, var0.length - 1);
   }

   public static void sort(byte[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort(var0, var1, var2 - 1);
   }

   public static void sort(float[] var0) {
      DualPivotQuicksort.sort((float[])var0, 0, var0.length - 1, (float[])null, 0, 0);
   }

   public static void sort(float[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((float[])var0, var1, var2 - 1, (float[])null, 0, 0);
   }

   public static void sort(double[] var0) {
      DualPivotQuicksort.sort((double[])var0, 0, var0.length - 1, (double[])null, 0, 0);
   }

   public static void sort(double[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      DualPivotQuicksort.sort((double[])var0, var1, var2 - 1, (double[])null, 0, 0);
   }

   public static void parallelSort(byte[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJByte.Sorter((CountedCompleter)null, var0, new byte[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort(var0, 0, var1 - 1);
      }

   }

   public static void parallelSort(byte[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJByte.Sorter((CountedCompleter)null, var0, new byte[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort(var0, var1, var2 - 1);
      }

   }

   public static void parallelSort(char[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJChar.Sorter((CountedCompleter)null, var0, new char[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((char[])var0, 0, var1 - 1, (char[])null, 0, 0);
      }

   }

   public static void parallelSort(char[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJChar.Sorter((CountedCompleter)null, var0, new char[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((char[])var0, var1, var2 - 1, (char[])null, 0, 0);
      }

   }

   public static void parallelSort(short[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJShort.Sorter((CountedCompleter)null, var0, new short[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((short[])var0, 0, var1 - 1, (short[])null, 0, 0);
      }

   }

   public static void parallelSort(short[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJShort.Sorter((CountedCompleter)null, var0, new short[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((short[])var0, var1, var2 - 1, (short[])null, 0, 0);
      }

   }

   public static void parallelSort(int[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJInt.Sorter((CountedCompleter)null, var0, new int[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((int[])var0, 0, var1 - 1, (int[])null, 0, 0);
      }

   }

   public static void parallelSort(int[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJInt.Sorter((CountedCompleter)null, var0, new int[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((int[])var0, var1, var2 - 1, (int[])null, 0, 0);
      }

   }

   public static void parallelSort(long[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJLong.Sorter((CountedCompleter)null, var0, new long[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((long[])var0, 0, var1 - 1, (long[])null, 0, 0);
      }

   }

   public static void parallelSort(long[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJLong.Sorter((CountedCompleter)null, var0, new long[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((long[])var0, var1, var2 - 1, (long[])null, 0, 0);
      }

   }

   public static void parallelSort(float[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJFloat.Sorter((CountedCompleter)null, var0, new float[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((float[])var0, 0, var1 - 1, (float[])null, 0, 0);
      }

   }

   public static void parallelSort(float[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJFloat.Sorter((CountedCompleter)null, var0, new float[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((float[])var0, var1, var2 - 1, (float[])null, 0, 0);
      }

   }

   public static void parallelSort(double[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJDouble.Sorter((CountedCompleter)null, var0, new double[var1], 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3)).invoke();
      } else {
         DualPivotQuicksort.sort((double[])var0, 0, var1 - 1, (double[])null, 0, 0);
      }

   }

   public static void parallelSort(double[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJDouble.Sorter((CountedCompleter)null, var0, new double[var3], var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5)).invoke();
      } else {
         DualPivotQuicksort.sort((double[])var0, var1, var2 - 1, (double[])null, 0, 0);
      }

   }

   public static <T extends Comparable<? super T>> void parallelSort(T[] var0) {
      int var1 = var0.length;
      int var2;
      if (var1 > 8192 && (var2 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var3;
         (new ArraysParallelSortHelpers.FJObject.Sorter((CountedCompleter)null, var0, (Comparable[])((Comparable[])Array.newInstance(var0.getClass().getComponentType(), var1)), 0, var1, 0, (var3 = var1 / (var2 << 2)) <= 8192 ? 8192 : var3, Arrays.NaturalOrder.INSTANCE)).invoke();
      } else {
         TimSort.sort(var0, 0, var1, Arrays.NaturalOrder.INSTANCE, (Object[])null, 0, 0);
      }

   }

   public static <T extends Comparable<? super T>> void parallelSort(T[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      int var3 = var2 - var1;
      int var4;
      if (var3 > 8192 && (var4 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var5;
         (new ArraysParallelSortHelpers.FJObject.Sorter((CountedCompleter)null, var0, (Comparable[])((Comparable[])Array.newInstance(var0.getClass().getComponentType(), var3)), var1, var3, 0, (var5 = var3 / (var4 << 2)) <= 8192 ? 8192 : var5, Arrays.NaturalOrder.INSTANCE)).invoke();
      } else {
         TimSort.sort(var0, var1, var2, Arrays.NaturalOrder.INSTANCE, (Object[])null, 0, 0);
      }

   }

   public static <T> void parallelSort(T[] var0, Comparator<? super T> var1) {
      if (var1 == null) {
         var1 = Arrays.NaturalOrder.INSTANCE;
      }

      int var2 = var0.length;
      int var3;
      if (var2 > 8192 && (var3 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var4;
         (new ArraysParallelSortHelpers.FJObject.Sorter((CountedCompleter)null, var0, (Object[])((Object[])Array.newInstance(var0.getClass().getComponentType(), var2)), 0, var2, 0, (var4 = var2 / (var3 << 2)) <= 8192 ? 8192 : var4, (Comparator)var1)).invoke();
      } else {
         TimSort.sort(var0, 0, var2, (Comparator)var1, (Object[])null, 0, 0);
      }

   }

   public static <T> void parallelSort(T[] var0, int var1, int var2, Comparator<? super T> var3) {
      rangeCheck(var0.length, var1, var2);
      if (var3 == null) {
         var3 = Arrays.NaturalOrder.INSTANCE;
      }

      int var4 = var2 - var1;
      int var5;
      if (var4 > 8192 && (var5 = ForkJoinPool.getCommonPoolParallelism()) != 1) {
         int var6;
         (new ArraysParallelSortHelpers.FJObject.Sorter((CountedCompleter)null, var0, (Object[])((Object[])Array.newInstance(var0.getClass().getComponentType(), var4)), var1, var4, 0, (var6 = var4 / (var5 << 2)) <= 8192 ? 8192 : var6, (Comparator)var3)).invoke();
      } else {
         TimSort.sort(var0, var1, var2, (Comparator)var3, (Object[])null, 0, 0);
      }

   }

   public static void sort(Object[] var0) {
      if (Arrays.LegacyMergeSort.userRequested) {
         legacyMergeSort(var0);
      } else {
         ComparableTimSort.sort(var0, 0, var0.length, (Object[])null, 0, 0);
      }

   }

   private static void legacyMergeSort(Object[] var0) {
      Object[] var1 = (Object[])var0.clone();
      mergeSort(var1, var0, 0, var0.length, 0);
   }

   public static void sort(Object[] var0, int var1, int var2) {
      rangeCheck(var0.length, var1, var2);
      if (Arrays.LegacyMergeSort.userRequested) {
         legacyMergeSort(var0, var1, var2);
      } else {
         ComparableTimSort.sort(var0, var1, var2, (Object[])null, 0, 0);
      }

   }

   private static void legacyMergeSort(Object[] var0, int var1, int var2) {
      Object[] var3 = copyOfRange(var0, var1, var2);
      mergeSort(var3, var0, var1, var2, -var1);
   }

   private static void mergeSort(Object[] var0, Object[] var1, int var2, int var3, int var4) {
      int var5 = var3 - var2;
      int var6;
      int var7;
      if (var5 < 7) {
         for(var6 = var2; var6 < var3; ++var6) {
            for(var7 = var6; var7 > var2 && ((Comparable)var1[var7 - 1]).compareTo(var1[var7]) > 0; --var7) {
               swap(var1, var7, var7 - 1);
            }
         }

      } else {
         var6 = var2;
         var7 = var3;
         var2 += var4;
         var3 += var4;
         int var8 = var2 + var3 >>> 1;
         mergeSort(var1, var0, var2, var8, -var4);
         mergeSort(var1, var0, var8, var3, -var4);
         if (((Comparable)var0[var8 - 1]).compareTo(var0[var8]) <= 0) {
            System.arraycopy(var0, var2, var1, var6, var5);
         } else {
            int var9 = var6;
            int var10 = var2;

            for(int var11 = var8; var9 < var7; ++var9) {
               if (var11 < var3 && (var10 >= var8 || ((Comparable)var0[var10]).compareTo(var0[var11]) > 0)) {
                  var1[var9] = var0[var11++];
               } else {
                  var1[var9] = var0[var10++];
               }
            }

         }
      }
   }

   private static void swap(Object[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static <T> void sort(T[] var0, Comparator<? super T> var1) {
      if (var1 == null) {
         sort(var0);
      } else if (Arrays.LegacyMergeSort.userRequested) {
         legacyMergeSort(var0, var1);
      } else {
         TimSort.sort(var0, 0, var0.length, var1, (Object[])null, 0, 0);
      }

   }

   private static <T> void legacyMergeSort(T[] var0, Comparator<? super T> var1) {
      Object[] var2 = (Object[])var0.clone();
      if (var1 == null) {
         mergeSort(var2, var0, 0, var0.length, 0);
      } else {
         mergeSort(var2, var0, 0, var0.length, 0, var1);
      }

   }

   public static <T> void sort(T[] var0, int var1, int var2, Comparator<? super T> var3) {
      if (var3 == null) {
         sort(var0, var1, var2);
      } else {
         rangeCheck(var0.length, var1, var2);
         if (Arrays.LegacyMergeSort.userRequested) {
            legacyMergeSort(var0, var1, var2, var3);
         } else {
            TimSort.sort(var0, var1, var2, var3, (Object[])null, 0, 0);
         }
      }

   }

   private static <T> void legacyMergeSort(T[] var0, int var1, int var2, Comparator<? super T> var3) {
      Object[] var4 = copyOfRange(var0, var1, var2);
      if (var3 == null) {
         mergeSort(var4, var0, var1, var2, -var1);
      } else {
         mergeSort(var4, var0, var1, var2, -var1, var3);
      }

   }

   private static void mergeSort(Object[] var0, Object[] var1, int var2, int var3, int var4, Comparator var5) {
      int var6 = var3 - var2;
      int var7;
      int var8;
      if (var6 < 7) {
         for(var7 = var2; var7 < var3; ++var7) {
            for(var8 = var7; var8 > var2 && var5.compare(var1[var8 - 1], var1[var8]) > 0; --var8) {
               swap(var1, var8, var8 - 1);
            }
         }

      } else {
         var7 = var2;
         var8 = var3;
         var2 += var4;
         var3 += var4;
         int var9 = var2 + var3 >>> 1;
         mergeSort(var1, var0, var2, var9, -var4, var5);
         mergeSort(var1, var0, var9, var3, -var4, var5);
         if (var5.compare(var0[var9 - 1], var0[var9]) <= 0) {
            System.arraycopy(var0, var2, var1, var7, var6);
         } else {
            int var10 = var7;
            int var11 = var2;

            for(int var12 = var9; var10 < var8; ++var10) {
               if (var12 < var3 && (var11 >= var9 || var5.compare(var0[var11], var0[var12]) > 0)) {
                  var1[var10] = var0[var12++];
               } else {
                  var1[var10] = var0[var11++];
               }
            }

         }
      }
   }

   public static <T> void parallelPrefix(T[] var0, BinaryOperator<T> var1) {
      Objects.requireNonNull(var1);
      if (var0.length > 0) {
         (new ArrayPrefixHelpers.CumulateTask((ArrayPrefixHelpers.CumulateTask)null, var1, var0, 0, var0.length)).invoke();
      }

   }

   public static <T> void parallelPrefix(T[] var0, int var1, int var2, BinaryOperator<T> var3) {
      Objects.requireNonNull(var3);
      rangeCheck(var0.length, var1, var2);
      if (var1 < var2) {
         (new ArrayPrefixHelpers.CumulateTask((ArrayPrefixHelpers.CumulateTask)null, var3, var0, var1, var2)).invoke();
      }

   }

   public static void parallelPrefix(long[] var0, LongBinaryOperator var1) {
      Objects.requireNonNull(var1);
      if (var0.length > 0) {
         (new ArrayPrefixHelpers.LongCumulateTask((ArrayPrefixHelpers.LongCumulateTask)null, var1, var0, 0, var0.length)).invoke();
      }

   }

   public static void parallelPrefix(long[] var0, int var1, int var2, LongBinaryOperator var3) {
      Objects.requireNonNull(var3);
      rangeCheck(var0.length, var1, var2);
      if (var1 < var2) {
         (new ArrayPrefixHelpers.LongCumulateTask((ArrayPrefixHelpers.LongCumulateTask)null, var3, var0, var1, var2)).invoke();
      }

   }

   public static void parallelPrefix(double[] var0, DoubleBinaryOperator var1) {
      Objects.requireNonNull(var1);
      if (var0.length > 0) {
         (new ArrayPrefixHelpers.DoubleCumulateTask((ArrayPrefixHelpers.DoubleCumulateTask)null, var1, var0, 0, var0.length)).invoke();
      }

   }

   public static void parallelPrefix(double[] var0, int var1, int var2, DoubleBinaryOperator var3) {
      Objects.requireNonNull(var3);
      rangeCheck(var0.length, var1, var2);
      if (var1 < var2) {
         (new ArrayPrefixHelpers.DoubleCumulateTask((ArrayPrefixHelpers.DoubleCumulateTask)null, var3, var0, var1, var2)).invoke();
      }

   }

   public static void parallelPrefix(int[] var0, IntBinaryOperator var1) {
      Objects.requireNonNull(var1);
      if (var0.length > 0) {
         (new ArrayPrefixHelpers.IntCumulateTask((ArrayPrefixHelpers.IntCumulateTask)null, var1, var0, 0, var0.length)).invoke();
      }

   }

   public static void parallelPrefix(int[] var0, int var1, int var2, IntBinaryOperator var3) {
      Objects.requireNonNull(var3);
      rangeCheck(var0.length, var1, var2);
      if (var1 < var2) {
         (new ArrayPrefixHelpers.IntCumulateTask((ArrayPrefixHelpers.IntCumulateTask)null, var3, var0, var1, var2)).invoke();
      }

   }

   public static int binarySearch(long[] var0, long var1) {
      return binarySearch0(var0, 0, var0.length, var1);
   }

   public static int binarySearch(long[] var0, int var1, int var2, long var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(long[] var0, int var1, int var2, long var3) {
      int var5 = var1;
      int var6 = var2 - 1;

      while(var5 <= var6) {
         int var7 = var5 + var6 >>> 1;
         long var8 = var0[var7];
         if (var8 < var3) {
            var5 = var7 + 1;
         } else {
            if (var8 <= var3) {
               return var7;
            }

            var6 = var7 - 1;
         }
      }

      return -(var5 + 1);
   }

   public static int binarySearch(int[] var0, int var1) {
      return binarySearch0((int[])var0, 0, var0.length, (int)var1);
   }

   public static int binarySearch(int[] var0, int var1, int var2, int var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(int[] var0, int var1, int var2, int var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         int var7 = var0[var6];
         if (var7 < var3) {
            var4 = var6 + 1;
         } else {
            if (var7 <= var3) {
               return var6;
            }

            var5 = var6 - 1;
         }
      }

      return -(var4 + 1);
   }

   public static int binarySearch(short[] var0, short var1) {
      return binarySearch0((short[])var0, 0, var0.length, (short)var1);
   }

   public static int binarySearch(short[] var0, int var1, int var2, short var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(short[] var0, int var1, int var2, short var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         short var7 = var0[var6];
         if (var7 < var3) {
            var4 = var6 + 1;
         } else {
            if (var7 <= var3) {
               return var6;
            }

            var5 = var6 - 1;
         }
      }

      return -(var4 + 1);
   }

   public static int binarySearch(char[] var0, char var1) {
      return binarySearch0((char[])var0, 0, var0.length, (char)var1);
   }

   public static int binarySearch(char[] var0, int var1, int var2, char var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(char[] var0, int var1, int var2, char var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         char var7 = var0[var6];
         if (var7 < var3) {
            var4 = var6 + 1;
         } else {
            if (var7 <= var3) {
               return var6;
            }

            var5 = var6 - 1;
         }
      }

      return -(var4 + 1);
   }

   public static int binarySearch(byte[] var0, byte var1) {
      return binarySearch0((byte[])var0, 0, var0.length, (byte)var1);
   }

   public static int binarySearch(byte[] var0, int var1, int var2, byte var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(byte[] var0, int var1, int var2, byte var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         byte var7 = var0[var6];
         if (var7 < var3) {
            var4 = var6 + 1;
         } else {
            if (var7 <= var3) {
               return var6;
            }

            var5 = var6 - 1;
         }
      }

      return -(var4 + 1);
   }

   public static int binarySearch(double[] var0, double var1) {
      return binarySearch0(var0, 0, var0.length, var1);
   }

   public static int binarySearch(double[] var0, int var1, int var2, double var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(double[] var0, int var1, int var2, double var3) {
      int var5 = var1;
      int var6 = var2 - 1;

      while(var5 <= var6) {
         int var7 = var5 + var6 >>> 1;
         double var8 = var0[var7];
         if (var8 < var3) {
            var5 = var7 + 1;
         } else if (var8 > var3) {
            var6 = var7 - 1;
         } else {
            long var10 = Double.doubleToLongBits(var8);
            long var12 = Double.doubleToLongBits(var3);
            if (var10 == var12) {
               return var7;
            }

            if (var10 < var12) {
               var5 = var7 + 1;
            } else {
               var6 = var7 - 1;
            }
         }
      }

      return -(var5 + 1);
   }

   public static int binarySearch(float[] var0, float var1) {
      return binarySearch0(var0, 0, var0.length, var1);
   }

   public static int binarySearch(float[] var0, int var1, int var2, float var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(float[] var0, int var1, int var2, float var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         float var7 = var0[var6];
         if (var7 < var3) {
            var4 = var6 + 1;
         } else if (var7 > var3) {
            var5 = var6 - 1;
         } else {
            int var8 = Float.floatToIntBits(var7);
            int var9 = Float.floatToIntBits(var3);
            if (var8 == var9) {
               return var6;
            }

            if (var8 < var9) {
               var4 = var6 + 1;
            } else {
               var5 = var6 - 1;
            }
         }
      }

      return -(var4 + 1);
   }

   public static int binarySearch(Object[] var0, Object var1) {
      return binarySearch0(var0, 0, var0.length, var1);
   }

   public static int binarySearch(Object[] var0, int var1, int var2, Object var3) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3);
   }

   private static int binarySearch0(Object[] var0, int var1, int var2, Object var3) {
      int var4 = var1;
      int var5 = var2 - 1;

      while(var4 <= var5) {
         int var6 = var4 + var5 >>> 1;
         Comparable var7 = (Comparable)var0[var6];
         int var8 = var7.compareTo(var3);
         if (var8 < 0) {
            var4 = var6 + 1;
         } else {
            if (var8 <= 0) {
               return var6;
            }

            var5 = var6 - 1;
         }
      }

      return -(var4 + 1);
   }

   public static <T> int binarySearch(T[] var0, T var1, Comparator<? super T> var2) {
      return binarySearch0(var0, 0, var0.length, var1, var2);
   }

   public static <T> int binarySearch(T[] var0, int var1, int var2, T var3, Comparator<? super T> var4) {
      rangeCheck(var0.length, var1, var2);
      return binarySearch0(var0, var1, var2, var3, var4);
   }

   private static <T> int binarySearch0(T[] var0, int var1, int var2, T var3, Comparator<? super T> var4) {
      if (var4 == null) {
         return binarySearch0(var0, var1, var2, var3);
      } else {
         int var5 = var1;
         int var6 = var2 - 1;

         while(var5 <= var6) {
            int var7 = var5 + var6 >>> 1;
            Object var8 = var0[var7];
            int var9 = var4.compare(var8, var3);
            if (var9 < 0) {
               var5 = var7 + 1;
            } else {
               if (var9 <= 0) {
                  return var7;
               }

               var6 = var7 - 1;
            }
         }

         return -(var5 + 1);
      }
   }

   public static boolean equals(long[] var0, long[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(int[] var0, int[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(short[] var0, short[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(char[] var0, char[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(byte[] var0, byte[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(boolean[] var0, boolean[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var0[var3] != var1[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(double[] var0, double[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (Double.doubleToLongBits(var0[var3]) != Double.doubleToLongBits(var1[var3])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(float[] var0, float[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (Float.floatToIntBits(var0[var3]) != Float.floatToIntBits(var1[var3])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(Object[] var0, Object[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            int var3 = 0;

            while(true) {
               if (var3 >= var2) {
                  return true;
               }

               Object var4 = var0[var3];
               Object var5 = var1[var3];
               if (var4 == null) {
                  if (var5 != null) {
                     break;
                  }
               } else if (!var4.equals(var5)) {
                  break;
               }

               ++var3;
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static void fill(long[] var0, long var1) {
      int var3 = 0;

      for(int var4 = var0.length; var3 < var4; ++var3) {
         var0[var3] = var1;
      }

   }

   public static void fill(long[] var0, int var1, int var2, long var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var5 = var1; var5 < var2; ++var5) {
         var0[var5] = var3;
      }

   }

   public static void fill(int[] var0, int var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(int[] var0, int var1, int var2, int var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(short[] var0, short var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(short[] var0, int var1, int var2, short var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(char[] var0, char var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(char[] var0, int var1, int var2, char var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(byte[] var0, byte var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(byte[] var0, int var1, int var2, byte var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(boolean[] var0, boolean var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(boolean[] var0, int var1, int var2, boolean var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(double[] var0, double var1) {
      int var3 = 0;

      for(int var4 = var0.length; var3 < var4; ++var3) {
         var0[var3] = var1;
      }

   }

   public static void fill(double[] var0, int var1, int var2, double var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var5 = var1; var5 < var2; ++var5) {
         var0[var5] = var3;
      }

   }

   public static void fill(float[] var0, float var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(float[] var0, int var1, int var2, float var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static void fill(Object[] var0, Object var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         var0[var2] = var1;
      }

   }

   public static void fill(Object[] var0, int var1, int var2, Object var3) {
      rangeCheck(var0.length, var1, var2);

      for(int var4 = var1; var4 < var2; ++var4) {
         var0[var4] = var3;
      }

   }

   public static <T> T[] copyOf(T[] var0, int var1) {
      return (Object[])copyOf(var0, var1, var0.getClass());
   }

   public static <T, U> T[] copyOf(U[] var0, int var1, Class<? extends T[]> var2) {
      Object[] var3 = var2 == Object[].class ? (Object[])(new Object[var1]) : (Object[])((Object[])Array.newInstance(var2.getComponentType(), var1));
      System.arraycopy(var0, 0, var3, 0, Math.min(var0.length, var1));
      return var3;
   }

   public static byte[] copyOf(byte[] var0, int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static short[] copyOf(short[] var0, int var1) {
      short[] var2 = new short[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static int[] copyOf(int[] var0, int var1) {
      int[] var2 = new int[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static long[] copyOf(long[] var0, int var1) {
      long[] var2 = new long[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static char[] copyOf(char[] var0, int var1) {
      char[] var2 = new char[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static float[] copyOf(float[] var0, int var1) {
      float[] var2 = new float[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static double[] copyOf(double[] var0, int var1) {
      double[] var2 = new double[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static boolean[] copyOf(boolean[] var0, int var1) {
      boolean[] var2 = new boolean[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static <T> T[] copyOfRange(T[] var0, int var1, int var2) {
      return copyOfRange(var0, var1, var2, var0.getClass());
   }

   public static <T, U> T[] copyOfRange(U[] var0, int var1, int var2, Class<? extends T[]> var3) {
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         Object[] var5 = var3 == Object[].class ? (Object[])(new Object[var4]) : (Object[])((Object[])Array.newInstance(var3.getComponentType(), var4));
         System.arraycopy(var0, var1, var5, 0, Math.min(var0.length - var1, var4));
         return var5;
      }
   }

   public static byte[] copyOfRange(byte[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         byte[] var4 = new byte[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static short[] copyOfRange(short[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         short[] var4 = new short[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static int[] copyOfRange(int[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         int[] var4 = new int[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static long[] copyOfRange(long[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         long[] var4 = new long[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static char[] copyOfRange(char[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         char[] var4 = new char[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static float[] copyOfRange(float[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         float[] var4 = new float[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static double[] copyOfRange(double[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         double[] var4 = new double[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   public static boolean[] copyOfRange(boolean[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 0) {
         throw new IllegalArgumentException(var1 + " > " + var2);
      } else {
         boolean[] var4 = new boolean[var3];
         System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
         return var4;
      }
   }

   @SafeVarargs
   public static <T> List<T> asList(T... var0) {
      return new Arrays.ArrayList(var0);
   }

   public static int hashCode(long[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         long[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            long var5 = var2[var4];
            int var7 = (int)(var5 ^ var5 >>> 32);
            var1 = 31 * var1 + var7;
         }

         return var1;
      }
   }

   public static int hashCode(int[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         int[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            var1 = 31 * var1 + var5;
         }

         return var1;
      }
   }

   public static int hashCode(short[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         short[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            short var5 = var2[var4];
            var1 = 31 * var1 + var5;
         }

         return var1;
      }
   }

   public static int hashCode(char[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         char[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2[var4];
            var1 = 31 * var1 + var5;
         }

         return var1;
      }
   }

   public static int hashCode(byte[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         byte[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            var1 = 31 * var1 + var5;
         }

         return var1;
      }
   }

   public static int hashCode(boolean[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         boolean[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            boolean var5 = var2[var4];
            var1 = 31 * var1 + (var5 ? 1231 : 1237);
         }

         return var1;
      }
   }

   public static int hashCode(float[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         float[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            float var5 = var2[var4];
            var1 = 31 * var1 + Float.floatToIntBits(var5);
         }

         return var1;
      }
   }

   public static int hashCode(double[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         double[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            double var5 = var2[var4];
            long var7 = Double.doubleToLongBits(var5);
            var1 = 31 * var1 + (int)(var7 ^ var7 >>> 32);
         }

         return var1;
      }
   }

   public static int hashCode(Object[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         Object[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            var1 = 31 * var1 + (var5 == null ? 0 : var5.hashCode());
         }

         return var1;
      }
   }

   public static int deepHashCode(Object[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 1;
         Object[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            int var6 = 0;
            if (var5 instanceof Object[]) {
               var6 = deepHashCode((Object[])((Object[])var5));
            } else if (var5 instanceof byte[]) {
               var6 = hashCode((byte[])((byte[])var5));
            } else if (var5 instanceof short[]) {
               var6 = hashCode((short[])((short[])var5));
            } else if (var5 instanceof int[]) {
               var6 = hashCode((int[])((int[])var5));
            } else if (var5 instanceof long[]) {
               var6 = hashCode((long[])((long[])var5));
            } else if (var5 instanceof char[]) {
               var6 = hashCode((char[])((char[])var5));
            } else if (var5 instanceof float[]) {
               var6 = hashCode((float[])((float[])var5));
            } else if (var5 instanceof double[]) {
               var6 = hashCode((double[])((double[])var5));
            } else if (var5 instanceof boolean[]) {
               var6 = hashCode((boolean[])((boolean[])var5));
            } else if (var5 != null) {
               var6 = var5.hashCode();
            }

            var1 = 31 * var1 + var6;
         }

         return var1;
      }
   }

   public static boolean deepEquals(Object[] var0, Object[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var1.length != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               Object var4 = var0[var3];
               Object var5 = var1[var3];
               if (var4 != var5) {
                  if (var4 == null) {
                     return false;
                  }

                  boolean var6 = deepEquals0(var4, var5);
                  if (!var6) {
                     return false;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   static boolean deepEquals0(Object var0, Object var1) {
      assert var0 != null;

      boolean var2;
      if (var0 instanceof Object[] && var1 instanceof Object[]) {
         var2 = deepEquals((Object[])((Object[])var0), (Object[])((Object[])var1));
      } else if (var0 instanceof byte[] && var1 instanceof byte[]) {
         var2 = equals((byte[])((byte[])var0), (byte[])((byte[])var1));
      } else if (var0 instanceof short[] && var1 instanceof short[]) {
         var2 = equals((short[])((short[])var0), (short[])((short[])var1));
      } else if (var0 instanceof int[] && var1 instanceof int[]) {
         var2 = equals((int[])((int[])var0), (int[])((int[])var1));
      } else if (var0 instanceof long[] && var1 instanceof long[]) {
         var2 = equals((long[])((long[])var0), (long[])((long[])var1));
      } else if (var0 instanceof char[] && var1 instanceof char[]) {
         var2 = equals((char[])((char[])var0), (char[])((char[])var1));
      } else if (var0 instanceof float[] && var1 instanceof float[]) {
         var2 = equals((float[])((float[])var0), (float[])((float[])var1));
      } else if (var0 instanceof double[] && var1 instanceof double[]) {
         var2 = equals((double[])((double[])var0), (double[])((double[])var1));
      } else if (var0 instanceof boolean[] && var1 instanceof boolean[]) {
         var2 = equals((boolean[])((boolean[])var0), (boolean[])((boolean[])var1));
      } else {
         var2 = var0.equals(var1);
      }

      return var2;
   }

   public static String toString(long[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(int[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(short[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append((int)var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(char[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(byte[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append((int)var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(boolean[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(float[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(double[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(var0[var3]);
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String toString(Object[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length - 1;
         if (var1 == -1) {
            return "[]";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');
            int var3 = 0;

            while(true) {
               var2.append(String.valueOf(var0[var3]));
               if (var3 == var1) {
                  return var2.append(']').toString();
               }

               var2.append(", ");
               ++var3;
            }
         }
      }
   }

   public static String deepToString(Object[] var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = 20 * var0.length;
         if (var0.length != 0 && var1 <= 0) {
            var1 = Integer.MAX_VALUE;
         }

         StringBuilder var2 = new StringBuilder(var1);
         deepToString(var0, var2, new HashSet());
         return var2.toString();
      }
   }

   private static void deepToString(Object[] var0, StringBuilder var1, Set<Object[]> var2) {
      if (var0 == null) {
         var1.append("null");
      } else {
         int var3 = var0.length - 1;
         if (var3 == -1) {
            var1.append("[]");
         } else {
            var2.add(var0);
            var1.append('[');
            int var4 = 0;

            while(true) {
               Object var5 = var0[var4];
               if (var5 == null) {
                  var1.append("null");
               } else {
                  Class var6 = var5.getClass();
                  if (var6.isArray()) {
                     if (var6 == byte[].class) {
                        var1.append(toString((byte[])((byte[])var5)));
                     } else if (var6 == short[].class) {
                        var1.append(toString((short[])((short[])var5)));
                     } else if (var6 == int[].class) {
                        var1.append(toString((int[])((int[])var5)));
                     } else if (var6 == long[].class) {
                        var1.append(toString((long[])((long[])var5)));
                     } else if (var6 == char[].class) {
                        var1.append(toString((char[])((char[])var5)));
                     } else if (var6 == float[].class) {
                        var1.append(toString((float[])((float[])var5)));
                     } else if (var6 == double[].class) {
                        var1.append(toString((double[])((double[])var5)));
                     } else if (var6 == boolean[].class) {
                        var1.append(toString((boolean[])((boolean[])var5)));
                     } else if (var2.contains(var5)) {
                        var1.append("[...]");
                     } else {
                        deepToString((Object[])((Object[])var5), var1, var2);
                     }
                  } else {
                     var1.append(var5.toString());
                  }
               }

               if (var4 == var3) {
                  var1.append(']');
                  var2.remove(var0);
                  return;
               }

               var1.append(", ");
               ++var4;
            }
         }
      }
   }

   public static <T> void setAll(T[] var0, IntFunction<? extends T> var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var0[var2] = var1.apply(var2);
      }

   }

   public static <T> void parallelSetAll(T[] var0, IntFunction<? extends T> var1) {
      Objects.requireNonNull(var1);
      IntStream.range(0, var0.length).parallel().forEach((var2) -> {
         var0[var2] = var1.apply(var2);
      });
   }

   public static void setAll(int[] var0, IntUnaryOperator var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var0[var2] = var1.applyAsInt(var2);
      }

   }

   public static void parallelSetAll(int[] var0, IntUnaryOperator var1) {
      Objects.requireNonNull(var1);
      IntStream.range(0, var0.length).parallel().forEach((var2) -> {
         var0[var2] = var1.applyAsInt(var2);
      });
   }

   public static void setAll(long[] var0, IntToLongFunction var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var0[var2] = var1.applyAsLong(var2);
      }

   }

   public static void parallelSetAll(long[] var0, IntToLongFunction var1) {
      Objects.requireNonNull(var1);
      IntStream.range(0, var0.length).parallel().forEach((var2) -> {
         var0[var2] = var1.applyAsLong(var2);
      });
   }

   public static void setAll(double[] var0, IntToDoubleFunction var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var0[var2] = var1.applyAsDouble(var2);
      }

   }

   public static void parallelSetAll(double[] var0, IntToDoubleFunction var1) {
      Objects.requireNonNull(var1);
      IntStream.range(0, var0.length).parallel().forEach((var2) -> {
         var0[var2] = var1.applyAsDouble(var2);
      });
   }

   public static <T> Spliterator<T> spliterator(T[] var0) {
      return Spliterators.spliterator((Object[])var0, 1040);
   }

   public static <T> Spliterator<T> spliterator(T[] var0, int var1, int var2) {
      return Spliterators.spliterator((Object[])var0, var1, var2, 1040);
   }

   public static Spliterator.OfInt spliterator(int[] var0) {
      return Spliterators.spliterator((int[])var0, 1040);
   }

   public static Spliterator.OfInt spliterator(int[] var0, int var1, int var2) {
      return Spliterators.spliterator((int[])var0, var1, var2, 1040);
   }

   public static Spliterator.OfLong spliterator(long[] var0) {
      return Spliterators.spliterator((long[])var0, 1040);
   }

   public static Spliterator.OfLong spliterator(long[] var0, int var1, int var2) {
      return Spliterators.spliterator((long[])var0, var1, var2, 1040);
   }

   public static Spliterator.OfDouble spliterator(double[] var0) {
      return Spliterators.spliterator((double[])var0, 1040);
   }

   public static Spliterator.OfDouble spliterator(double[] var0, int var1, int var2) {
      return Spliterators.spliterator((double[])var0, var1, var2, 1040);
   }

   public static <T> Stream<T> stream(T[] var0) {
      return stream((Object[])var0, 0, var0.length);
   }

   public static <T> Stream<T> stream(T[] var0, int var1, int var2) {
      return StreamSupport.stream(spliterator(var0, var1, var2), false);
   }

   public static IntStream stream(int[] var0) {
      return stream((int[])var0, 0, var0.length);
   }

   public static IntStream stream(int[] var0, int var1, int var2) {
      return StreamSupport.intStream(spliterator(var0, var1, var2), false);
   }

   public static LongStream stream(long[] var0) {
      return stream((long[])var0, 0, var0.length);
   }

   public static LongStream stream(long[] var0, int var1, int var2) {
      return StreamSupport.longStream(spliterator(var0, var1, var2), false);
   }

   public static DoubleStream stream(double[] var0) {
      return stream((double[])var0, 0, var0.length);
   }

   public static DoubleStream stream(double[] var0, int var1, int var2) {
      return StreamSupport.doubleStream(spliterator(var0, var1, var2), false);
   }

   private static class ArrayList<E> extends AbstractList<E> implements RandomAccess, Serializable {
      private static final long serialVersionUID = -2764017481108945198L;
      private final E[] a;

      ArrayList(E[] var1) {
         this.a = (Object[])Objects.requireNonNull(var1);
      }

      public int size() {
         return this.a.length;
      }

      public Object[] toArray() {
         return (Object[])this.a.clone();
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = this.size();
         if (var1.length < var2) {
            return Arrays.copyOf(this.a, var2, var1.getClass());
         } else {
            System.arraycopy(this.a, 0, var1, 0, var2);
            if (var1.length > var2) {
               var1[var2] = null;
            }

            return var1;
         }
      }

      public E get(int var1) {
         return this.a[var1];
      }

      public E set(int var1, E var2) {
         Object var3 = this.a[var1];
         this.a[var1] = var2;
         return var3;
      }

      public int indexOf(Object var1) {
         Object[] var2 = this.a;
         int var3;
         if (var1 == null) {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] == null) {
                  return var3;
               }
            }
         } else {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var1.equals(var2[var3])) {
                  return var3;
               }
            }
         }

         return -1;
      }

      public boolean contains(Object var1) {
         return this.indexOf(var1) != -1;
      }

      public Spliterator<E> spliterator() {
         return Spliterators.spliterator((Object[])this.a, 16);
      }

      public void forEach(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
         Object[] var2 = this.a;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            var1.accept(var5);
         }

      }

      public void replaceAll(UnaryOperator<E> var1) {
         Objects.requireNonNull(var1);
         Object[] var2 = this.a;

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = var1.apply(var2[var3]);
         }

      }

      public void sort(Comparator<? super E> var1) {
         Arrays.sort(this.a, var1);
      }
   }

   static final class LegacyMergeSort {
      private static final boolean userRequested = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("java.util.Arrays.useLegacyMergeSort")));
   }

   static final class NaturalOrder implements Comparator<Object> {
      static final Arrays.NaturalOrder INSTANCE = new Arrays.NaturalOrder();

      public int compare(Object var1, Object var2) {
         return ((Comparable)var1).compareTo(var2);
      }
   }
}
