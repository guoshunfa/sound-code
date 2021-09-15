package java.util.stream;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final class Collectors {
   static final Set<Collector.Characteristics> CH_CONCURRENT_ID;
   static final Set<Collector.Characteristics> CH_CONCURRENT_NOID;
   static final Set<Collector.Characteristics> CH_ID;
   static final Set<Collector.Characteristics> CH_UNORDERED_ID;
   static final Set<Collector.Characteristics> CH_NOID;

   private Collectors() {
   }

   private static <T> BinaryOperator<T> throwingMerger() {
      return (var0, var1) -> {
         throw new IllegalStateException(String.format("Duplicate key %s", var0));
      };
   }

   private static <I, R> Function<I, R> castingIdentity() {
      return (var0) -> {
         return var0;
      };
   }

   public static <T, C extends Collection<T>> Collector<T, ?, C> toCollection(Supplier<C> var0) {
      return new Collectors.CollectorImpl(var0, Collection::add, (var0x, var1) -> {
         var0x.addAll(var1);
         return var0x;
      }, CH_ID);
   }

   public static <T> Collector<T, ?, List<T>> toList() {
      return new Collectors.CollectorImpl(ArrayList::new, List::add, (var0, var1) -> {
         var0.addAll(var1);
         return var0;
      }, CH_ID);
   }

   public static <T> Collector<T, ?, Set<T>> toSet() {
      return new Collectors.CollectorImpl(HashSet::new, Set::add, (var0, var1) -> {
         var0.addAll(var1);
         return var0;
      }, CH_UNORDERED_ID);
   }

   public static Collector<CharSequence, ?, String> joining() {
      return new Collectors.CollectorImpl(StringBuilder::new, StringBuilder::append, (var0, var1) -> {
         var0.append((CharSequence)var1);
         return var0;
      }, StringBuilder::toString, CH_NOID);
   }

   public static Collector<CharSequence, ?, String> joining(CharSequence var0) {
      return joining(var0, "", "");
   }

   public static Collector<CharSequence, ?, String> joining(CharSequence var0, CharSequence var1, CharSequence var2) {
      return new Collectors.CollectorImpl(() -> {
         return new StringJoiner(var0, var1, var2);
      }, StringJoiner::add, StringJoiner::merge, StringJoiner::toString, CH_NOID);
   }

   private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> var0) {
      return (var1, var2) -> {
         Iterator var3 = var2.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var1.merge(var4.getKey(), var4.getValue(), var0);
         }

         return var1;
      };
   }

   public static <T, U, A, R> Collector<T, ?, R> mapping(Function<? super T, ? extends U> var0, Collector<? super U, A, R> var1) {
      BiConsumer var2 = var1.accumulator();
      return new Collectors.CollectorImpl(var1.supplier(), (var2x, var3) -> {
         var2.accept(var2x, var0.apply(var3));
      }, var1.combiner(), var1.finisher(), var1.characteristics());
   }

   public static <T, A, R, RR> Collector<T, A, RR> collectingAndThen(Collector<T, A, R> var0, Function<R, RR> var1) {
      Set var2 = var0.characteristics();
      if (var2.contains(Collector.Characteristics.IDENTITY_FINISH)) {
         if (var2.size() == 1) {
            var2 = CH_NOID;
         } else {
            EnumSet var3 = EnumSet.copyOf((Collection)var2);
            var3.remove(Collector.Characteristics.IDENTITY_FINISH);
            var2 = Collections.unmodifiableSet(var3);
         }
      }

      return new Collectors.CollectorImpl(var0.supplier(), var0.accumulator(), var0.combiner(), var0.finisher().andThen(var1), var2);
   }

   public static <T> Collector<T, ?, Long> counting() {
      return reducing(0L, (var0) -> {
         return 1L;
      }, Long::sum);
   }

   public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> var0) {
      return reducing(BinaryOperator.minBy(var0));
   }

   public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> var0) {
      return reducing(BinaryOperator.maxBy(var0));
   }

   public static <T> Collector<T, ?, Integer> summingInt(ToIntFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new int[1];
      }, (var1, var2) -> {
         var1[0] += var0.applyAsInt(var2);
      }, (var0x, var1) -> {
         var0x[0] += var1[0];
         return var0x;
      }, (var0x) -> {
         return var0x[0];
      }, CH_NOID);
   }

   public static <T> Collector<T, ?, Long> summingLong(ToLongFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new long[1];
      }, (var1, var2) -> {
         var1[0] += var0.applyAsLong(var2);
      }, (var0x, var1) -> {
         var0x[0] += var1[0];
         return var0x;
      }, (var0x) -> {
         return var0x[0];
      }, CH_NOID);
   }

   public static <T> Collector<T, ?, Double> summingDouble(ToDoubleFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new double[3];
      }, (var1, var2) -> {
         sumWithCompensation(var1, var0.applyAsDouble(var2));
         var1[2] += var0.applyAsDouble(var2);
      }, (var0x, var1) -> {
         sumWithCompensation(var0x, var1[0]);
         var0x[2] += var1[2];
         return sumWithCompensation(var0x, var1[1]);
      }, (var0x) -> {
         return computeFinalSum(var0x);
      }, CH_NOID);
   }

   static double[] sumWithCompensation(double[] var0, double var1) {
      double var3 = var1 - var0[1];
      double var5 = var0[0];
      double var7 = var5 + var3;
      var0[1] = var7 - var5 - var3;
      var0[0] = var7;
      return var0;
   }

   static double computeFinalSum(double[] var0) {
      double var1 = var0[0] + var0[1];
      double var3 = var0[var0.length - 1];
      return Double.isNaN(var1) && Double.isInfinite(var3) ? var3 : var1;
   }

   public static <T> Collector<T, ?, Double> averagingInt(ToIntFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new long[2];
      }, (var1, var2) -> {
         var1[0] += (long)var0.applyAsInt(var2);
         int var10002 = var1[1]++;
      }, (var0x, var1) -> {
         var0x[0] += var1[0];
         var0x[1] += var1[1];
         return var0x;
      }, (var0x) -> {
         return var0x[1] == 0L ? 0.0D : (double)var0x[0] / (double)var0x[1];
      }, CH_NOID);
   }

   public static <T> Collector<T, ?, Double> averagingLong(ToLongFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new long[2];
      }, (var1, var2) -> {
         var1[0] += var0.applyAsLong(var2);
         int var10002 = var1[1]++;
      }, (var0x, var1) -> {
         var0x[0] += var1[0];
         var0x[1] += var1[1];
         return var0x;
      }, (var0x) -> {
         return var0x[1] == 0L ? 0.0D : (double)var0x[0] / (double)var0x[1];
      }, CH_NOID);
   }

   public static <T> Collector<T, ?, Double> averagingDouble(ToDoubleFunction<? super T> var0) {
      return new Collectors.CollectorImpl(() -> {
         return new double[4];
      }, (var1, var2) -> {
         sumWithCompensation(var1, var0.applyAsDouble(var2));
         int var10002 = var1[2]++;
         var1[3] += var0.applyAsDouble(var2);
      }, (var0x, var1) -> {
         sumWithCompensation(var0x, var1[0]);
         sumWithCompensation(var0x, var1[1]);
         var0x[2] += var1[2];
         var0x[3] += var1[3];
         return var0x;
      }, (var0x) -> {
         return var0x[2] == 0.0D ? 0.0D : computeFinalSum(var0x) / var0x[2];
      }, CH_NOID);
   }

   public static <T> Collector<T, ?, T> reducing(T var0, BinaryOperator<T> var1) {
      return new Collectors.CollectorImpl(boxSupplier(var0), (var1x, var2) -> {
         var1x[0] = var1.apply(var1x[0], var2);
      }, (var1x, var2) -> {
         var1x[0] = var1.apply(var1x[0], var2[0]);
         return var1x;
      }, (var0x) -> {
         return var0x[0];
      }, CH_NOID);
   }

   private static <T> Supplier<T[]> boxSupplier(T var0) {
      return () -> {
         return (Object[])(new Object[]{var0});
      };
   }

   public static <T> Collector<T, ?, Optional<T>> reducing(BinaryOperator<T> var0) {
      return new Collectors.CollectorImpl(() -> {
         class OptionalBox implements Consumer<T> {
            T value = null;
            boolean present = false;

            public void accept(T var1) {
               if (this.present) {
                  this.value = var0.apply(this.value, var1);
               } else {
                  this.value = var1;
                  this.present = true;
               }

            }
         }

         return new OptionalBox();
      }, OptionalBox::accept, (var0x, var1) -> {
         if (var1.present) {
            var0x.accept(var1.value);
         }

         return var0x;
      }, (var0x) -> {
         return Optional.ofNullable(var0x.value);
      }, CH_NOID);
   }

   public static <T, U> Collector<T, ?, U> reducing(U var0, Function<? super T, ? extends U> var1, BinaryOperator<U> var2) {
      return new Collectors.CollectorImpl(boxSupplier(var0), (var2x, var3) -> {
         var2x[0] = var2.apply(var2x[0], var1.apply(var3));
      }, (var1x, var2x) -> {
         var1x[0] = var2.apply(var1x[0], var2x[0]);
         return var1x;
      }, (var0x) -> {
         return var0x[0];
      }, CH_NOID);
   }

   public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> var0) {
      return groupingBy(var0, toList());
   }

   public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> var0, Collector<? super T, A, D> var1) {
      return groupingBy(var0, HashMap::new, var1);
   }

   public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> var0, Supplier<M> var1, Collector<? super T, A, D> var2) {
      Supplier var3 = var2.supplier();
      BiConsumer var4 = var2.accumulator();
      BiConsumer var5 = (var3x, var4x) -> {
         Object var5 = Objects.requireNonNull(var0.apply(var4x), "element cannot be mapped to a null key");
         Object var6 = var3x.computeIfAbsent(var5, (var1) -> {
            return var3.get();
         });
         var4.accept(var6, var4x);
      };
      BinaryOperator var6 = mapMerger(var2.combiner());
      if (var2.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
         return new Collectors.CollectorImpl(var1, var5, var6, CH_ID);
      } else {
         Function var8 = var2.finisher();
         Function var9 = (var1x) -> {
            var1x.replaceAll((var1, var2) -> {
               return var8.apply(var2);
            });
            return var1x;
         };
         return new Collectors.CollectorImpl(var1, var5, var6, var9, CH_NOID);
      }
   }

   public static <T, K> Collector<T, ?, ConcurrentMap<K, List<T>>> groupingByConcurrent(Function<? super T, ? extends K> var0) {
      return groupingByConcurrent(var0, ConcurrentHashMap::new, toList());
   }

   public static <T, K, A, D> Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(Function<? super T, ? extends K> var0, Collector<? super T, A, D> var1) {
      return groupingByConcurrent(var0, ConcurrentHashMap::new, var1);
   }

   public static <T, K, A, D, M extends ConcurrentMap<K, D>> Collector<T, ?, M> groupingByConcurrent(Function<? super T, ? extends K> var0, Supplier<M> var1, Collector<? super T, A, D> var2) {
      Supplier var3 = var2.supplier();
      BiConsumer var4 = var2.accumulator();
      BinaryOperator var5 = mapMerger(var2.combiner());
      BiConsumer var7;
      if (var2.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
         var7 = (var3x, var4x) -> {
            Object var5 = Objects.requireNonNull(var0.apply(var4x), "element cannot be mapped to a null key");
            Object var6 = var3x.computeIfAbsent(var5, (var1) -> {
               return var3.get();
            });
            var4.accept(var6, var4x);
         };
      } else {
         var7 = (var3x, var4x) -> {
            Object var5 = Objects.requireNonNull(var0.apply(var4x), "element cannot be mapped to a null key");
            Object var6 = var3x.computeIfAbsent(var5, (var1) -> {
               return var3.get();
            });
            synchronized(var6) {
               var4.accept(var6, var4x);
            }
         };
      }

      if (var2.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
         return new Collectors.CollectorImpl(var1, var7, var5, CH_CONCURRENT_ID);
      } else {
         Function var8 = var2.finisher();
         Function var9 = (var1x) -> {
            var1x.replaceAll((var1, var2) -> {
               return var8.apply(var2);
            });
            return var1x;
         };
         return new Collectors.CollectorImpl(var1, var7, var5, var9, CH_CONCURRENT_NOID);
      }
   }

   public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> var0) {
      return partitioningBy(var0, toList());
   }

   public static <T, D, A> Collector<T, ?, Map<Boolean, D>> partitioningBy(Predicate<? super T> var0, Collector<? super T, A, D> var1) {
      BiConsumer var2 = var1.accumulator();
      BiConsumer var3 = (var2x, var3x) -> {
         var2.accept(var0.test(var3x) ? var2x.forTrue : var2x.forFalse, var3x);
      };
      BinaryOperator var4 = var1.combiner();
      BinaryOperator var5 = (var1x, var2x) -> {
         return new Collectors.Partition(var4.apply(var1x.forTrue, var2x.forTrue), var4.apply(var1x.forFalse, var2x.forFalse));
      };
      Supplier var6 = () -> {
         return new Collectors.Partition(var1.supplier().get(), var1.supplier().get());
      };
      if (var1.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
         return new Collectors.CollectorImpl(var6, var3, var5, CH_ID);
      } else {
         Function var7 = (var1x) -> {
            return new Collectors.Partition(var1.finisher().apply(var1x.forTrue), var1.finisher().apply(var1x.forFalse));
         };
         return new Collectors.CollectorImpl(var6, var3, var5, var7, CH_NOID);
      }
   }

   public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1) {
      return toMap(var0, var1, throwingMerger(), HashMap::new);
   }

   public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1, BinaryOperator<U> var2) {
      return toMap(var0, var1, var2, HashMap::new);
   }

   public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1, BinaryOperator<U> var2, Supplier<M> var3) {
      BiConsumer var4 = (var3x, var4x) -> {
         var3x.merge(var0.apply(var4x), var1.apply(var4x), var2);
      };
      return new Collectors.CollectorImpl(var3, var4, mapMerger(var2), CH_ID);
   }

   public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1) {
      return toConcurrentMap(var0, var1, throwingMerger(), ConcurrentHashMap::new);
   }

   public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1, BinaryOperator<U> var2) {
      return toConcurrentMap(var0, var1, var2, ConcurrentHashMap::new);
   }

   public static <T, K, U, M extends ConcurrentMap<K, U>> Collector<T, ?, M> toConcurrentMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends U> var1, BinaryOperator<U> var2, Supplier<M> var3) {
      BiConsumer var4 = (var3x, var4x) -> {
         var3x.merge(var0.apply(var4x), var1.apply(var4x), var2);
      };
      return new Collectors.CollectorImpl(var3, var4, mapMerger(var2), CH_CONCURRENT_ID);
   }

   public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(ToIntFunction<? super T> var0) {
      return new Collectors.CollectorImpl(IntSummaryStatistics::new, (var1, var2) -> {
         var1.accept(var0.applyAsInt(var2));
      }, (var0x, var1) -> {
         var0x.combine(var1);
         return var0x;
      }, CH_ID);
   }

   public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(ToLongFunction<? super T> var0) {
      return new Collectors.CollectorImpl(LongSummaryStatistics::new, (var1, var2) -> {
         var1.accept(var0.applyAsLong(var2));
      }, (var0x, var1) -> {
         var0x.combine(var1);
         return var0x;
      }, CH_ID);
   }

   public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(ToDoubleFunction<? super T> var0) {
      return new Collectors.CollectorImpl(DoubleSummaryStatistics::new, (var1, var2) -> {
         var1.accept(var0.applyAsDouble(var2));
      }, (var0x, var1) -> {
         var0x.combine(var1);
         return var0x;
      }, CH_ID);
   }

   static {
      CH_CONCURRENT_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
      CH_CONCURRENT_NOID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, (Enum)Collector.Characteristics.UNORDERED));
      CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
      CH_UNORDERED_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, (Enum)Collector.Characteristics.IDENTITY_FINISH));
      CH_NOID = Collections.emptySet();
   }

   private static final class Partition<T> extends AbstractMap<Boolean, T> implements Map<Boolean, T> {
      final T forTrue;
      final T forFalse;

      Partition(T var1, T var2) {
         this.forTrue = var1;
         this.forFalse = var2;
      }

      public Set<Map.Entry<Boolean, T>> entrySet() {
         return new AbstractSet<Map.Entry<Boolean, T>>() {
            public Iterator<Map.Entry<Boolean, T>> iterator() {
               AbstractMap.SimpleImmutableEntry var1 = new AbstractMap.SimpleImmutableEntry(false, Partition.this.forFalse);
               AbstractMap.SimpleImmutableEntry var2 = new AbstractMap.SimpleImmutableEntry(true, Partition.this.forTrue);
               return Arrays.asList(var1, var2).iterator();
            }

            public int size() {
               return 2;
            }
         };
      }
   }

   static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
      private final Supplier<A> supplier;
      private final BiConsumer<A, T> accumulator;
      private final BinaryOperator<A> combiner;
      private final Function<A, R> finisher;
      private final Set<Collector.Characteristics> characteristics;

      CollectorImpl(Supplier<A> var1, BiConsumer<A, T> var2, BinaryOperator<A> var3, Function<A, R> var4, Set<Collector.Characteristics> var5) {
         this.supplier = var1;
         this.accumulator = var2;
         this.combiner = var3;
         this.finisher = var4;
         this.characteristics = var5;
      }

      CollectorImpl(Supplier<A> var1, BiConsumer<A, T> var2, BinaryOperator<A> var3, Set<Collector.Characteristics> var4) {
         this(var1, var2, var3, Collectors.castingIdentity(), var4);
      }

      public BiConsumer<A, T> accumulator() {
         return this.accumulator;
      }

      public Supplier<A> supplier() {
         return this.supplier;
      }

      public BinaryOperator<A> combiner() {
         return this.combiner;
      }

      public Function<A, R> finisher() {
         return this.finisher;
      }

      public Set<Collector.Characteristics> characteristics() {
         return this.characteristics;
      }
   }
}
