package java.util.concurrent;

public enum TimeUnit {
   NANOSECONDS {
      public long toNanos(long var1) {
         return var1;
      }

      public long toMicros(long var1) {
         return var1 / 1000L;
      }

      public long toMillis(long var1) {
         return var1 / 1000000L;
      }

      public long toSeconds(long var1) {
         return var1 / 1000000000L;
      }

      public long toMinutes(long var1) {
         return var1 / 60000000000L;
      }

      public long toHours(long var1) {
         return var1 / 3600000000000L;
      }

      public long toDays(long var1) {
         return var1 / 86400000000000L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toNanos(var1);
      }

      int excessNanos(long var1, long var3) {
         return (int)(var1 - var3 * 1000000L);
      }
   },
   MICROSECONDS {
      public long toNanos(long var1) {
         return x(var1, 1000L, 9223372036854775L);
      }

      public long toMicros(long var1) {
         return var1;
      }

      public long toMillis(long var1) {
         return var1 / 1000L;
      }

      public long toSeconds(long var1) {
         return var1 / 1000000L;
      }

      public long toMinutes(long var1) {
         return var1 / 60000000L;
      }

      public long toHours(long var1) {
         return var1 / 3600000000L;
      }

      public long toDays(long var1) {
         return var1 / 86400000000L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toMicros(var1);
      }

      int excessNanos(long var1, long var3) {
         return (int)(var1 * 1000L - var3 * 1000000L);
      }
   },
   MILLISECONDS {
      public long toNanos(long var1) {
         return x(var1, 1000000L, 9223372036854L);
      }

      public long toMicros(long var1) {
         return x(var1, 1000L, 9223372036854775L);
      }

      public long toMillis(long var1) {
         return var1;
      }

      public long toSeconds(long var1) {
         return var1 / 1000L;
      }

      public long toMinutes(long var1) {
         return var1 / 60000L;
      }

      public long toHours(long var1) {
         return var1 / 3600000L;
      }

      public long toDays(long var1) {
         return var1 / 86400000L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toMillis(var1);
      }

      int excessNanos(long var1, long var3) {
         return 0;
      }
   },
   SECONDS {
      public long toNanos(long var1) {
         return x(var1, 1000000000L, 9223372036L);
      }

      public long toMicros(long var1) {
         return x(var1, 1000000L, 9223372036854L);
      }

      public long toMillis(long var1) {
         return x(var1, 1000L, 9223372036854775L);
      }

      public long toSeconds(long var1) {
         return var1;
      }

      public long toMinutes(long var1) {
         return var1 / 60L;
      }

      public long toHours(long var1) {
         return var1 / 3600L;
      }

      public long toDays(long var1) {
         return var1 / 86400L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toSeconds(var1);
      }

      int excessNanos(long var1, long var3) {
         return 0;
      }
   },
   MINUTES {
      public long toNanos(long var1) {
         return x(var1, 60000000000L, 153722867L);
      }

      public long toMicros(long var1) {
         return x(var1, 60000000L, 153722867280L);
      }

      public long toMillis(long var1) {
         return x(var1, 60000L, 153722867280912L);
      }

      public long toSeconds(long var1) {
         return x(var1, 60L, 153722867280912930L);
      }

      public long toMinutes(long var1) {
         return var1;
      }

      public long toHours(long var1) {
         return var1 / 60L;
      }

      public long toDays(long var1) {
         return var1 / 1440L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toMinutes(var1);
      }

      int excessNanos(long var1, long var3) {
         return 0;
      }
   },
   HOURS {
      public long toNanos(long var1) {
         return x(var1, 3600000000000L, 2562047L);
      }

      public long toMicros(long var1) {
         return x(var1, 3600000000L, 2562047788L);
      }

      public long toMillis(long var1) {
         return x(var1, 3600000L, 2562047788015L);
      }

      public long toSeconds(long var1) {
         return x(var1, 3600L, 2562047788015215L);
      }

      public long toMinutes(long var1) {
         return x(var1, 60L, 153722867280912930L);
      }

      public long toHours(long var1) {
         return var1;
      }

      public long toDays(long var1) {
         return var1 / 24L;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toHours(var1);
      }

      int excessNanos(long var1, long var3) {
         return 0;
      }
   },
   DAYS {
      public long toNanos(long var1) {
         return x(var1, 86400000000000L, 106751L);
      }

      public long toMicros(long var1) {
         return x(var1, 86400000000L, 106751991L);
      }

      public long toMillis(long var1) {
         return x(var1, 86400000L, 106751991167L);
      }

      public long toSeconds(long var1) {
         return x(var1, 86400L, 106751991167300L);
      }

      public long toMinutes(long var1) {
         return x(var1, 1440L, 6405119470038038L);
      }

      public long toHours(long var1) {
         return x(var1, 24L, 384307168202282325L);
      }

      public long toDays(long var1) {
         return var1;
      }

      public long convert(long var1, TimeUnit var3) {
         return var3.toDays(var1);
      }

      int excessNanos(long var1, long var3) {
         return 0;
      }
   };

   static final long C0 = 1L;
   static final long C1 = 1000L;
   static final long C2 = 1000000L;
   static final long C3 = 1000000000L;
   static final long C4 = 60000000000L;
   static final long C5 = 3600000000000L;
   static final long C6 = 86400000000000L;
   static final long MAX = Long.MAX_VALUE;

   private TimeUnit() {
   }

   static long x(long var0, long var2, long var4) {
      if (var0 > var4) {
         return Long.MAX_VALUE;
      } else {
         return var0 < -var4 ? Long.MIN_VALUE : var0 * var2;
      }
   }

   public long convert(long var1, TimeUnit var3) {
      throw new AbstractMethodError();
   }

   public long toNanos(long var1) {
      throw new AbstractMethodError();
   }

   public long toMicros(long var1) {
      throw new AbstractMethodError();
   }

   public long toMillis(long var1) {
      throw new AbstractMethodError();
   }

   public long toSeconds(long var1) {
      throw new AbstractMethodError();
   }

   public long toMinutes(long var1) {
      throw new AbstractMethodError();
   }

   public long toHours(long var1) {
      throw new AbstractMethodError();
   }

   public long toDays(long var1) {
      throw new AbstractMethodError();
   }

   abstract int excessNanos(long var1, long var3);

   public void timedWait(Object var1, long var2) throws InterruptedException {
      if (var2 > 0L) {
         long var4 = this.toMillis(var2);
         int var6 = this.excessNanos(var2, var4);
         var1.wait(var4, var6);
      }

   }

   public void timedJoin(Thread var1, long var2) throws InterruptedException {
      if (var2 > 0L) {
         long var4 = this.toMillis(var2);
         int var6 = this.excessNanos(var2, var4);
         var1.join(var4, var6);
      }

   }

   public void sleep(long var1) throws InterruptedException {
      if (var1 > 0L) {
         long var3 = this.toMillis(var1);
         int var5 = this.excessNanos(var1, var3);
         Thread.sleep(var3, var5);
      }

   }

   // $FF: synthetic method
   TimeUnit(Object var3) {
      this();
   }
}
