package java.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface Comparator<T> {
   int compare(T var1, T var2);

   boolean equals(Object var1);

   default Comparator<T> reversed() {
      return Collections.reverseOrder(this);
   }

   default Comparator<T> thenComparing(Comparator<? super T> var1) {
      Objects.requireNonNull(var1);
      return (Comparator)((Serializable)((var2x, var3) -> {
         int var4 = this.compare(var2x, var3);
         return var4 != 0 ? var4 : var1.compare(var2x, var3);
      }));
   }

   default <U> Comparator<T> thenComparing(Function<? super T, ? extends U> var1, Comparator<? super U> var2) {
      return this.thenComparing(comparing(var1, var2));
   }

   default <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> var1) {
      return this.thenComparing(comparing(var1));
   }

   default Comparator<T> thenComparingInt(ToIntFunction<? super T> var1) {
      return this.thenComparing(comparingInt(var1));
   }

   default Comparator<T> thenComparingLong(ToLongFunction<? super T> var1) {
      return this.thenComparing(comparingLong(var1));
   }

   default Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> var1) {
      return this.thenComparing(comparingDouble(var1));
   }

   static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
      return Collections.reverseOrder();
   }

   static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
      return Comparators.NaturalOrderComparator.INSTANCE;
   }

   static <T> Comparator<T> nullsFirst(Comparator<? super T> var0) {
      return new Comparators.NullComparator(true, var0);
   }

   static <T> Comparator<T> nullsLast(Comparator<? super T> var0) {
      return new Comparators.NullComparator(false, var0);
   }

   static <T, U> Comparator<T> comparing(Function<? super T, ? extends U> var0, Comparator<? super U> var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return (Comparator)((Serializable)((var2x, var3) -> {
         return var1.compare(var0.apply(var2x), var0.apply(var3));
      }));
   }

   static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> var0) {
      Objects.requireNonNull(var0);
      return (Comparator)((Serializable)((var1x, var2x) -> {
         return ((Comparable)var0.apply(var1x)).compareTo(var0.apply(var2x));
      }));
   }

   static <T> Comparator<T> comparingInt(ToIntFunction<? super T> var0) {
      Objects.requireNonNull(var0);
      return (Comparator)((Serializable)((var1x, var2x) -> {
         return Integer.compare(var0.applyAsInt(var1x), var0.applyAsInt(var2x));
      }));
   }

   static <T> Comparator<T> comparingLong(ToLongFunction<? super T> var0) {
      Objects.requireNonNull(var0);
      return (Comparator)((Serializable)((var1x, var2x) -> {
         return Long.compare(var0.applyAsLong(var1x), var0.applyAsLong(var2x));
      }));
   }

   static <T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> var0) {
      Objects.requireNonNull(var0);
      return (Comparator)((Serializable)((var1x, var2x) -> {
         return Double.compare(var0.applyAsDouble(var1x), var0.applyAsDouble(var2x));
      }));
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda var0) {
      String var1 = var0.getImplMethodName();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1669453324:
         if (var1.equals("lambda$comparing$77a9974f$1")) {
            var2 = 0;
         }
         break;
      case -902634255:
         if (var1.equals("lambda$comparingLong$6043328a$1")) {
            var2 = 4;
         }
         break;
      case -860761860:
         if (var1.equals("lambda$thenComparing$36697e65$1")) {
            var2 = 3;
         }
         break;
      case -564296216:
         if (var1.equals("lambda$comparing$ea9a8b3a$1")) {
            var2 = 1;
         }
         break;
      case 826214802:
         if (var1.equals("lambda$comparingInt$7b0bb60$1")) {
            var2 = 2;
         }
         break;
      case 1964526692:
         if (var1.equals("lambda$comparingDouble$8dcf42ea$1")) {
            var2 = 5;
         }
      }

      switch(var2) {
      case 0:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/function/Function;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            return (var1x, var2x) -> {
               return ((Comparable)var0.apply(var1x)).compareTo(var0.apply(var2x));
            };
         }
         break;
      case 1:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/Comparator;Ljava/util/function/Function;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            return (var2x, var3) -> {
               return var1.compare(var0.apply(var2x), var0.apply(var3));
            };
         }
         break;
      case 2:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/function/ToIntFunction;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            return (var1x, var2x) -> {
               return Integer.compare(var0.applyAsInt(var1x), var0.applyAsInt(var2x));
            };
         }
         break;
      case 3:
         if (var0.getImplMethodKind() == 7 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/Comparator;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            Comparator var10000 = (Comparator)var0.getCapturedArg(0);
            return (var2x, var3) -> {
               int var4 = this.compare(var2x, var3);
               return var4 != 0 ? var4 : var1.compare(var2x, var3);
            };
         }
         break;
      case 4:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/function/ToLongFunction;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            return (var1x, var2x) -> {
               return Long.compare(var0.applyAsLong(var1x), var0.applyAsLong(var2x));
            };
         }
         break;
      case 5:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Comparator") && var0.getImplMethodSignature().equals("(Ljava/util/function/ToDoubleFunction;Ljava/lang/Object;Ljava/lang/Object;)I")) {
            return (var1x, var2x) -> {
               return Double.compare(var0.applyAsDouble(var1x), var0.applyAsDouble(var2x));
            };
         }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
