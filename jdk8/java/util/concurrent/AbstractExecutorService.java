package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractExecutorService implements ExecutorService {
   protected <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return new FutureTask(var1, var2);
   }

   protected <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return new FutureTask(var1);
   }

   public Future<?> submit(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RunnableFuture var2 = this.newTaskFor(var1, (Object)null);
         this.execute(var2);
         return var2;
      }
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RunnableFuture var3 = this.newTaskFor(var1, var2);
         this.execute(var3);
         return var3;
      }
   }

   public <T> Future<T> submit(Callable<T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RunnableFuture var2 = this.newTaskFor(var1);
         this.execute(var2);
         return var2;
      }
   }

   private <T> T doInvokeAny(Collection<? extends Callable<T>> var1, boolean var2, long var3) throws InterruptedException, ExecutionException, TimeoutException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var5 = var1.size();
         if (var5 == 0) {
            throw new IllegalArgumentException();
         } else {
            ArrayList var6 = new ArrayList(var5);
            ExecutorCompletionService var7 = new ExecutorCompletionService(this);
            boolean var23 = false;

            Object var14;
            try {
               var23 = true;
               ExecutionException var8 = null;
               long var9 = var2 ? System.nanoTime() + var3 : 0L;
               Iterator var11 = var1.iterator();
               var6.add(var7.submit((Callable)var11.next()));
               --var5;
               int var12 = 1;

               while(true) {
                  Future var13 = var7.poll();
                  if (var13 == null) {
                     if (var5 > 0) {
                        --var5;
                        var6.add(var7.submit((Callable)var11.next()));
                        ++var12;
                     } else {
                        if (var12 == 0) {
                           if (var8 == null) {
                              var8 = new ExecutionException();
                           }

                           throw var8;
                        }

                        if (var2) {
                           var13 = var7.poll(var3, TimeUnit.NANOSECONDS);
                           if (var13 == null) {
                              throw new TimeoutException();
                           }

                           var3 = var9 - System.nanoTime();
                        } else {
                           var13 = var7.take();
                        }
                     }
                  }

                  if (var13 != null) {
                     --var12;

                     try {
                        var14 = var13.get();
                        var23 = false;
                        break;
                     } catch (ExecutionException var24) {
                        var8 = var24;
                     } catch (RuntimeException var25) {
                        var8 = new ExecutionException(var25);
                     }
                  }
               }
            } finally {
               if (var23) {
                  int var18 = 0;

                  for(int var19 = var6.size(); var18 < var19; ++var18) {
                     ((Future)var6.get(var18)).cancel(true);
                  }

               }
            }

            int var15 = 0;

            for(int var16 = var6.size(); var15 < var16; ++var15) {
               ((Future)var6.get(var15)).cancel(true);
            }

            return var14;
         }
      }
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      try {
         return this.doInvokeAny(var1, false, 0L);
      } catch (TimeoutException var3) {
         assert false;

         return null;
      }
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.doInvokeAny(var1, true, var4.toNanos(var2));
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ArrayList var2 = new ArrayList(var1.size());
         boolean var3 = false;
         boolean var14 = false;

         ArrayList var19;
         int var20;
         try {
            var14 = true;
            Iterator var4 = var1.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  int var18 = 0;

                  for(var20 = var2.size(); var18 < var20; ++var18) {
                     Future var21 = (Future)var2.get(var18);
                     if (!var21.isDone()) {
                        try {
                           var21.get();
                        } catch (CancellationException var15) {
                        } catch (ExecutionException var16) {
                        }
                     }
                  }

                  var3 = true;
                  var19 = var2;
                  var14 = false;
                  break;
               }

               Callable var5 = (Callable)var4.next();
               RunnableFuture var6 = this.newTaskFor(var5);
               var2.add(var6);
               this.execute(var6);
            }
         } finally {
            if (var14) {
               if (!var3) {
                  int var9 = 0;

                  for(int var10 = var2.size(); var9 < var10; ++var9) {
                     ((Future)var2.get(var9)).cancel(true);
                  }
               }

            }
         }

         if (!var3) {
            var20 = 0;

            for(int var22 = var2.size(); var20 < var22; ++var20) {
               ((Future)var2.get(var20)).cancel(true);
            }
         }

         return var19;
      }
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         long var5 = var4.toNanos(var2);
         ArrayList var7 = new ArrayList(var1.size());
         boolean var8 = false;
         boolean var25 = false;

         ArrayList var14;
         int var16;
         int var35;
         label270: {
            ArrayList var31;
            int var34;
            label271: {
               ArrayList var15;
               label272: {
                  ArrayList var13;
                  try {
                     var25 = true;
                     Iterator var9 = var1.iterator();

                     while(var9.hasNext()) {
                        Callable var10 = (Callable)var9.next();
                        var7.add(this.newTaskFor(var10));
                     }

                     long var30 = System.nanoTime() + var5;
                     int var11 = var7.size();
                     int var12 = 0;

                     while(true) {
                        if (var12 >= var11) {
                           for(var12 = 0; var12 < var11; ++var12) {
                              Future var32 = (Future)var7.get(var12);
                              if (!var32.isDone()) {
                                 if (var5 <= 0L) {
                                    var14 = var7;
                                    var25 = false;
                                    break label270;
                                 }

                                 try {
                                    var32.get(var5, TimeUnit.NANOSECONDS);
                                 } catch (CancellationException var26) {
                                 } catch (ExecutionException var27) {
                                 } catch (TimeoutException var28) {
                                    var15 = var7;
                                    var25 = false;
                                    break label272;
                                 }

                                 var5 = var30 - System.nanoTime();
                              }
                           }

                           var8 = true;
                           var31 = var7;
                           var25 = false;
                           break label271;
                        }

                        this.execute((Runnable)var7.get(var12));
                        var5 = var30 - System.nanoTime();
                        if (var5 <= 0L) {
                           var13 = var7;
                           var25 = false;
                           break;
                        }

                        ++var12;
                     }
                  } finally {
                     if (var25) {
                        if (!var8) {
                           int var19 = 0;

                           for(int var20 = var7.size(); var19 < var20; ++var19) {
                              ((Future)var7.get(var19)).cancel(true);
                           }
                        }

                     }
                  }

                  if (!var8) {
                     var34 = 0;

                     for(var35 = var7.size(); var34 < var35; ++var34) {
                        ((Future)var7.get(var34)).cancel(true);
                     }
                  }

                  return var13;
               }

               if (!var8) {
                  var16 = 0;

                  for(int var17 = var7.size(); var16 < var17; ++var16) {
                     ((Future)var7.get(var16)).cancel(true);
                  }
               }

               return var15;
            }

            if (!var8) {
               int var33 = 0;

               for(var34 = var7.size(); var33 < var34; ++var33) {
                  ((Future)var7.get(var33)).cancel(true);
               }
            }

            return var31;
         }

         if (!var8) {
            var35 = 0;

            for(var16 = var7.size(); var35 < var16; ++var35) {
               ((Future)var7.get(var35)).cancel(true);
            }
         }

         return var14;
      }
   }
}
