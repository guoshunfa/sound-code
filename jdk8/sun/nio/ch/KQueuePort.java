package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

final class KQueuePort extends Port {
   private static final int MAX_KEVENTS_TO_POLL = 512;
   private final int kqfd = KQueue.kqueue();
   private boolean closed;
   private final int[] sp;
   private final AtomicInteger wakeupCount = new AtomicInteger();
   private final long address;
   private final ArrayBlockingQueue<KQueuePort.Event> queue;
   private final KQueuePort.Event NEED_TO_POLL = new KQueuePort.Event((Port.PollableChannel)null, 0);
   private final KQueuePort.Event EXECUTE_TASK_OR_SHUTDOWN = new KQueuePort.Event((Port.PollableChannel)null, 0);

   KQueuePort(AsynchronousChannelProvider var1, ThreadPool var2) throws IOException {
      super(var1, var2);
      int[] var3 = new int[2];

      try {
         socketpair(var3);
         KQueue.keventRegister(this.kqfd, var3[0], -1, 1);
      } catch (IOException var5) {
         close0(this.kqfd);
         throw var5;
      }

      this.sp = var3;
      this.address = KQueue.allocatePollArray(512);
      this.queue = new ArrayBlockingQueue(512);
      this.queue.offer(this.NEED_TO_POLL);
   }

   KQueuePort start() {
      this.startThreads(new KQueuePort.EventHandlerTask());
      return this;
   }

   private void implClose() {
      synchronized(this) {
         if (this.closed) {
            return;
         }

         this.closed = true;
      }

      KQueue.freePollArray(this.address);
      close0(this.sp[0]);
      close0(this.sp[1]);
      close0(this.kqfd);
   }

   private void wakeup() {
      if (this.wakeupCount.incrementAndGet() == 1) {
         try {
            interrupt(this.sp[1]);
         } catch (IOException var2) {
            throw new AssertionError(var2);
         }
      }

   }

   void executeOnHandlerTask(Runnable var1) {
      synchronized(this) {
         if (this.closed) {
            throw new RejectedExecutionException();
         } else {
            this.offerTask(var1);
            this.wakeup();
         }
      }
   }

   void shutdownHandlerTasks() {
      int var1 = this.threadCount();
      if (var1 == 0) {
         this.implClose();
      } else {
         while(var1-- > 0) {
            this.wakeup();
         }
      }

   }

   void startPoll(int var1, int var2) {
      int var3 = 0;
      byte var4 = 17;
      if ((var2 & Net.POLLIN) > 0) {
         var3 = KQueue.keventRegister(this.kqfd, var1, -1, var4);
      }

      if (var3 == 0 && (var2 & Net.POLLOUT) > 0) {
         var3 = KQueue.keventRegister(this.kqfd, var1, -2, var4);
      }

      if (var3 != 0) {
         throw new InternalError("kevent failed: " + var3);
      }
   }

   private static native void socketpair(int[] var0) throws IOException;

   private static native void interrupt(int var0) throws IOException;

   private static native void drain1(int var0) throws IOException;

   private static native void close0(int var0);

   static {
      IOUtil.load();
   }

   private class EventHandlerTask implements Runnable {
      private EventHandlerTask() {
      }

      private KQueuePort.Event poll() throws IOException {
         try {
            while(true) {
               int var1 = KQueue.keventPoll(KQueuePort.this.kqfd, KQueuePort.this.address, 512);
               KQueuePort.this.fdToChannelLock.readLock().lock();

               try {
                  while(var1-- > 0) {
                     long var2 = KQueue.getEvent(KQueuePort.this.address, var1);
                     int var4 = KQueue.getDescriptor(var2);
                     Object var5;
                     if (var4 == KQueuePort.this.sp[0]) {
                        if (KQueuePort.this.wakeupCount.decrementAndGet() == 0) {
                           KQueuePort.drain1(KQueuePort.this.sp[0]);
                        }

                        if (var1 <= 0) {
                           var5 = KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN;
                           return (KQueuePort.Event)var5;
                        }

                        KQueuePort.this.queue.offer(KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN);
                     } else {
                        var5 = (Port.PollableChannel)KQueuePort.this.fdToChannel.get(var4);
                        if (var5 != null) {
                           int var6 = KQueue.getFilter(var2);
                           short var7 = 0;
                           if (var6 == -1) {
                              var7 = Net.POLLIN;
                           } else if (var6 == -2) {
                              var7 = Net.POLLOUT;
                           }

                           KQueuePort.Event var8 = new KQueuePort.Event((Port.PollableChannel)var5, var7);
                           if (var1 <= 0) {
                              KQueuePort.Event var9 = var8;
                              return var9;
                           }

                           KQueuePort.this.queue.offer(var8);
                        }
                     }
                  }
               } finally {
                  KQueuePort.this.fdToChannelLock.readLock().unlock();
               }
            }
         } finally {
            KQueuePort.this.queue.offer(KQueuePort.this.NEED_TO_POLL);
         }
      }

      public void run() {
         Invoker.GroupAndInvokeCount var1 = Invoker.getGroupAndInvokeCount();
         boolean var2 = var1 != null;
         boolean var3 = false;

         int var6;
         while(true) {
            boolean var14 = false;

            try {
               label151: {
                  var14 = true;
                  if (var2) {
                     var1.resetInvokeCount();
                  }

                  KQueuePort.Event var4;
                  try {
                     var3 = false;
                     var4 = (KQueuePort.Event)KQueuePort.this.queue.take();
                     if (var4 == KQueuePort.this.NEED_TO_POLL) {
                        try {
                           var4 = this.poll();
                        } catch (IOException var17) {
                           var17.printStackTrace();
                           var14 = false;
                           break label151;
                        }
                     }
                  } catch (InterruptedException var18) {
                     continue;
                  }

                  if (var4 == KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN) {
                     Runnable var5 = KQueuePort.this.pollTask();
                     if (var5 == null) {
                        var14 = false;
                        break;
                     }

                     var3 = true;
                     var5.run();
                     continue;
                  }

                  try {
                     var4.channel().onEvent(var4.events(), var2);
                     continue;
                  } catch (Error var15) {
                     var3 = true;
                     throw var15;
                  } catch (RuntimeException var16) {
                     var3 = true;
                     throw var16;
                  }
               }
            } finally {
               if (var14) {
                  int var8 = KQueuePort.this.threadExit(this, var3);
                  if (var8 == 0 && KQueuePort.this.isShutdown()) {
                     KQueuePort.this.implClose();
                  }

               }
            }

            var6 = KQueuePort.this.threadExit(this, var3);
            if (var6 == 0 && KQueuePort.this.isShutdown()) {
               KQueuePort.this.implClose();
            }

            return;
         }

         var6 = KQueuePort.this.threadExit(this, var3);
         if (var6 == 0 && KQueuePort.this.isShutdown()) {
            KQueuePort.this.implClose();
         }

      }

      // $FF: synthetic method
      EventHandlerTask(Object var2) {
         this();
      }
   }

   static class Event {
      final Port.PollableChannel channel;
      final int events;

      Event(Port.PollableChannel var1, int var2) {
         this.channel = var1;
         this.events = var2;
      }

      Port.PollableChannel channel() {
         return this.channel;
      }

      int events() {
         return this.events;
      }
   }
}
