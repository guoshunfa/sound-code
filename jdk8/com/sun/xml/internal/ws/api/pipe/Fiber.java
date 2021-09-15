package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public final class Fiber implements Runnable, Cancelable, ComponentRegistry {
   private final List<Fiber.Listener> _listeners = new ArrayList();
   private Tube[] conts = new Tube[16];
   private int contsSize;
   private Tube next;
   private Packet packet;
   private Throwable throwable;
   public final Engine owner;
   private volatile int suspendedCount = 0;
   private volatile boolean isInsideSuspendCallbacks = false;
   private boolean synchronous;
   private boolean interrupted;
   private final int id;
   private List<FiberContextSwitchInterceptor> interceptors;
   @Nullable
   private ClassLoader contextClassLoader;
   @Nullable
   private Fiber.CompletionCallback completionCallback;
   private boolean isDeliverThrowableInPacket = false;
   private Thread currentThread;
   private final ReentrantLock lock = new ReentrantLock();
   private final Condition condition;
   private volatile boolean isCanceled;
   private boolean started;
   private boolean startedSync;
   private static final Fiber.PlaceholderTube PLACEHOLDER = new Fiber.PlaceholderTube();
   private static final ThreadLocal<Fiber> CURRENT_FIBER = new ThreadLocal();
   private static final AtomicInteger iotaGen = new AtomicInteger();
   private static final Logger LOGGER = Logger.getLogger(Fiber.class.getName());
   private static final ReentrantLock serializedExecutionLock = new ReentrantLock();
   public static volatile boolean serializeExecution = Boolean.getBoolean(Fiber.class.getName() + ".serialize");
   private final Set<Component> components;

   /** @deprecated */
   public void addListener(Fiber.Listener listener) {
      synchronized(this._listeners) {
         if (!this._listeners.contains(listener)) {
            this._listeners.add(listener);
         }

      }
   }

   /** @deprecated */
   public void removeListener(Fiber.Listener listener) {
      synchronized(this._listeners) {
         this._listeners.remove(listener);
      }
   }

   List<Fiber.Listener> getCurrentListeners() {
      synchronized(this._listeners) {
         return new ArrayList(this._listeners);
      }
   }

   private void clearListeners() {
      synchronized(this._listeners) {
         this._listeners.clear();
      }
   }

   public void setDeliverThrowableInPacket(boolean isDeliverThrowableInPacket) {
      this.isDeliverThrowableInPacket = isDeliverThrowableInPacket;
   }

   Fiber(Engine engine) {
      this.condition = this.lock.newCondition();
      this.components = new CopyOnWriteArraySet();
      this.owner = engine;
      this.id = iotaGen.incrementAndGet();
      if (isTraceEnabled()) {
         LOGGER.log(Level.FINE, (String)"{0} created", (Object)this.getName());
      }

      this.contextClassLoader = Thread.currentThread().getContextClassLoader();
   }

   public void start(@NotNull Tube tubeline, @NotNull Packet request, @Nullable Fiber.CompletionCallback completionCallback) {
      this.start(tubeline, request, completionCallback, false);
   }

   private void dumpFiberContext(String desc) {
      if (isTraceEnabled()) {
         String action = null;
         String msgId = null;
         if (this.packet != null) {
            SOAPVersion[] var4 = SOAPVersion.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               SOAPVersion sv = var4[var6];
               AddressingVersion[] var8 = AddressingVersion.values();
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  AddressingVersion av = var8[var10];
                  action = this.packet.getMessage() != null ? AddressingUtils.getAction(this.packet.getMessage().getHeaders(), av, sv) : null;
                  msgId = this.packet.getMessage() != null ? AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), av, sv) : null;
                  if (action != null || msgId != null) {
                     break;
                  }
               }

               if (action != null || msgId != null) {
                  break;
               }
            }
         }

         String actionAndMsgDesc;
         if (action == null && msgId == null) {
            actionAndMsgDesc = "NO ACTION or MSG ID";
         } else {
            actionAndMsgDesc = "'" + action + "' and msgId '" + msgId + "'";
         }

         String tubeDesc;
         if (this.next != null) {
            tubeDesc = this.next.toString() + ".processRequest()";
         } else {
            tubeDesc = this.peekCont() + ".processResponse()";
         }

         LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[]{this.getName(), desc, actionAndMsgDesc, tubeDesc, Thread.currentThread().getName(), this.packet != null ? this.packet.toShortString() : null});
      }

   }

   public void start(@NotNull Tube tubeline, @NotNull Packet request, @Nullable Fiber.CompletionCallback completionCallback, boolean forceSync) {
      this.next = tubeline;
      this.packet = request;
      this.completionCallback = completionCallback;
      if (forceSync) {
         this.startedSync = true;
         this.dumpFiberContext("starting (sync)");
         this.run();
      } else {
         this.started = true;
         this.dumpFiberContext("starting (async)");
         this.owner.addRunnable(this);
      }

   }

   public void resume(@NotNull Packet resumePacket) {
      this.resume(resumePacket, false);
   }

   public void resume(@NotNull Packet resumePacket, boolean forceSync) {
      this.resume(resumePacket, forceSync, (Fiber.CompletionCallback)null);
   }

   public void resume(@NotNull Packet resumePacket, boolean forceSync, Fiber.CompletionCallback callback) {
      this.lock.lock();

      try {
         if (callback != null) {
            this.setCompletionCallback(callback);
         }

         if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[]{this.getName(), this.suspendedCount - 1});
         }

         this.packet = resumePacket;
         if (--this.suspendedCount == 0) {
            if (!this.isInsideSuspendCallbacks) {
               List<Fiber.Listener> listeners = this.getCurrentListeners();
               Iterator var5 = listeners.iterator();

               while(var5.hasNext()) {
                  Fiber.Listener listener = (Fiber.Listener)var5.next();

                  try {
                     listener.fiberResumed(this);
                  } catch (Throwable var11) {
                     if (isTraceEnabled()) {
                        LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, var11.getMessage()});
                     }
                  }
               }

               if (this.synchronous) {
                  this.condition.signalAll();
               } else if (!forceSync && !this.startedSync) {
                  this.dumpFiberContext("resuming (async)");
                  this.owner.addRunnable(this);
               } else {
                  this.run();
               }
            }
         } else if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[]{this.getName(), this.suspendedCount});
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void resumeAndReturn(@NotNull Packet resumePacket, boolean forceSync) {
      if (isTraceEnabled()) {
         LOGGER.log(Level.FINE, (String)"{0} resumed with Return Packet", (Object)this.getName());
      }

      this.next = null;
      this.resume(resumePacket, forceSync);
   }

   public void resume(@NotNull Throwable throwable) {
      this.resume(throwable, this.packet, false);
   }

   public void resume(@NotNull Throwable throwable, @NotNull Packet packet) {
      this.resume(throwable, packet, false);
   }

   public void resume(@NotNull Throwable error, boolean forceSync) {
      this.resume(error, this.packet, forceSync);
   }

   public void resume(@NotNull Throwable error, @NotNull Packet packet, boolean forceSync) {
      if (isTraceEnabled()) {
         LOGGER.log(Level.FINE, (String)"{0} resumed with Return Throwable", (Object)this.getName());
      }

      this.next = null;
      this.throwable = error;
      this.resume(packet, forceSync);
   }

   public void cancel(boolean mayInterrupt) {
      this.isCanceled = true;
      if (mayInterrupt) {
         synchronized(this) {
            if (this.currentThread != null) {
               this.currentThread.interrupt();
            }
         }
      }

   }

   private boolean suspend(Holder<Boolean> isRequireUnlock, Runnable onExitRunnable) {
      if (isTraceEnabled()) {
         LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[]{this.getName(), this.suspendedCount + 1});
         if (this.suspendedCount > 0) {
            LOGGER.log(Level.FINE, (String)"WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", (Object)this.getName());
         }
      }

      List<Fiber.Listener> listeners = this.getCurrentListeners();
      Iterator var4;
      Fiber.Listener listener;
      if (++this.suspendedCount == 1) {
         this.isInsideSuspendCallbacks = true;

         try {
            var4 = listeners.iterator();

            while(var4.hasNext()) {
               listener = (Fiber.Listener)var4.next();

               try {
                  listener.fiberSuspended(this);
               } catch (Throwable var17) {
                  if (isTraceEnabled()) {
                     LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, var17.getMessage()});
                  }
               }
            }
         } finally {
            this.isInsideSuspendCallbacks = false;
         }
      }

      if (this.suspendedCount <= 0) {
         var4 = listeners.iterator();

         while(var4.hasNext()) {
            listener = (Fiber.Listener)var4.next();

            try {
               listener.fiberResumed(this);
            } catch (Throwable var16) {
               if (isTraceEnabled()) {
                  LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, var16.getMessage()});
               }
            }
         }
      } else if (onExitRunnable != null) {
         if (!this.synchronous) {
            synchronized(this) {
               this.currentThread = null;
            }

            this.lock.unlock();

            assert !this.lock.isHeldByCurrentThread();

            isRequireUnlock.value = Boolean.FALSE;

            try {
               onExitRunnable.run();
               return true;
            } catch (Throwable var14) {
               throw new Fiber.OnExitRunnableException(var14);
            }
         }

         if (isTraceEnabled()) {
            LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread");
         }

         onExitRunnable.run();
      }

      return false;
   }

   public synchronized void addInterceptor(@NotNull FiberContextSwitchInterceptor interceptor) {
      if (this.interceptors == null) {
         this.interceptors = new ArrayList();
      } else {
         List<FiberContextSwitchInterceptor> l = new ArrayList();
         l.addAll(this.interceptors);
         this.interceptors = l;
      }

      this.interceptors.add(interceptor);
   }

   public synchronized boolean removeInterceptor(@NotNull FiberContextSwitchInterceptor interceptor) {
      if (this.interceptors != null) {
         boolean result = this.interceptors.remove(interceptor);
         if (this.interceptors.isEmpty()) {
            this.interceptors = null;
         } else {
            List<FiberContextSwitchInterceptor> l = new ArrayList();
            l.addAll(this.interceptors);
            this.interceptors = l;
         }

         return result;
      } else {
         return false;
      }
   }

   @Nullable
   public ClassLoader getContextClassLoader() {
      return this.contextClassLoader;
   }

   public ClassLoader setContextClassLoader(@Nullable ClassLoader contextClassLoader) {
      ClassLoader r = this.contextClassLoader;
      this.contextClassLoader = contextClassLoader;
      return r;
   }

   /** @deprecated */
   @Deprecated
   public void run() {
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      try {
         assert !this.synchronous;

         if (!this.doRun()) {
            if (!this.startedSync || this.suspendedCount != 0 || this.next == null && this.contsSize <= 0) {
               this.completionCheck();
            } else {
               this.startedSync = false;
               this.dumpFiberContext("restarting (async) after startSync");
               this.owner.addRunnable(this);
            }
         }
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

   }

   @NotNull
   public Packet runSync(@NotNull Tube tubeline, @NotNull Packet request) {
      this.lock.lock();

      Packet var7;
      try {
         Tube[] oldCont = this.conts;
         int oldContSize = this.contsSize;
         boolean oldSynchronous = this.synchronous;
         Tube oldNext = this.next;
         if (oldContSize > 0) {
            this.conts = new Tube[16];
            this.contsSize = 0;
         }

         try {
            this.synchronous = true;
            this.packet = request;
            this.next = tubeline;
            this.doRun();
            if (this.throwable != null) {
               if (!this.isDeliverThrowableInPacket) {
                  if (this.throwable instanceof RuntimeException) {
                     throw (RuntimeException)this.throwable;
                  }

                  if (this.throwable instanceof Error) {
                     throw (Error)this.throwable;
                  }

                  throw new AssertionError(this.throwable);
               }

               this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
            }

            var7 = this.packet;
         } finally {
            this.conts = oldCont;
            this.contsSize = oldContSize;
            this.synchronous = oldSynchronous;
            this.next = oldNext;
            if (this.interrupted) {
               Thread.currentThread().interrupt();
               this.interrupted = false;
            }

            if (!this.started && !this.startedSync) {
               this.completionCheck();
            }

         }
      } finally {
         this.lock.unlock();
      }

      return var7;
   }

   private void completionCheck() {
      this.lock.lock();

      try {
         if (!this.isCanceled && this.contsSize == 0 && this.suspendedCount == 0) {
            if (isTraceEnabled()) {
               LOGGER.log(Level.FINE, (String)"{0} completed", (Object)this.getName());
            }

            this.clearListeners();
            this.condition.signalAll();
            if (this.completionCallback != null) {
               if (this.throwable != null) {
                  if (this.isDeliverThrowableInPacket) {
                     this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
                     this.completionCallback.onCompletion(this.packet);
                  } else {
                     this.completionCallback.onCompletion(this.throwable);
                  }
               } else {
                  this.completionCallback.onCompletion(this.packet);
               }
            }
         }
      } finally {
         this.lock.unlock();
      }

   }

   private boolean doRun() {
      this.dumpFiberContext("running");
      if (serializeExecution) {
         serializedExecutionLock.lock();

         boolean var1;
         try {
            var1 = this._doRun(this.next);
         } finally {
            serializedExecutionLock.unlock();
         }

         return var1;
      } else {
         return this._doRun(this.next);
      }
   }

   private boolean _doRun(Tube next) {
      Holder<Boolean> isRequireUnlock = new Holder(Boolean.TRUE);
      this.lock.lock();
      boolean var26 = false;

      boolean var47;
      label469: {
         label470: {
            boolean needsToReenter;
            try {
               label461: {
                  ClassLoader old;
                  Thread thread;
                  label471: {
                     label472: {
                        var26 = true;
                        List ints;
                        synchronized(this) {
                           ints = this.interceptors;
                           this.currentThread = Thread.currentThread();
                           if (isTraceEnabled()) {
                              LOGGER.log(Level.FINE, (String)"Thread entering _doRun(): {0}", (Object)this.currentThread);
                           }

                           old = this.currentThread.getContextClassLoader();
                           this.currentThread.setContextClassLoader(this.contextClassLoader);
                        }

                        while(true) {
                           boolean var36 = false;

                           try {
                              var36 = true;
                              if (ints == null) {
                                 this.next = next;
                                 if (this.__doRun(isRequireUnlock, (List)null)) {
                                    var47 = true;
                                    var36 = false;
                                    break label471;
                                 }
                              } else {
                                 next = (new Fiber.InterceptorHandler(isRequireUnlock, ints)).invoke(next);
                                 if (next == PLACEHOLDER) {
                                    var47 = true;
                                    var36 = false;
                                    break;
                                 }
                              }

                              synchronized(this) {
                                 needsToReenter = ints != this.interceptors;
                                 if (needsToReenter) {
                                    ints = this.interceptors;
                                 }
                              }

                              if (!needsToReenter) {
                                 var36 = false;
                                 break label472;
                              }
                           } catch (Fiber.OnExitRunnableException var43) {
                              Throwable t = var43.target;
                              if (t instanceof WebServiceException) {
                                 throw (WebServiceException)t;
                              }

                              throw new WebServiceException(t);
                           } finally {
                              if (var36) {
                                 Thread thread = Thread.currentThread();
                                 thread.setContextClassLoader(old);
                                 if (isTraceEnabled()) {
                                    LOGGER.log(Level.FINE, (String)"Thread leaving _doRun(): {0}", (Object)thread);
                                 }

                              }
                           }
                        }

                        thread = Thread.currentThread();
                        thread.setContextClassLoader(old);
                        if (isTraceEnabled()) {
                           LOGGER.log(Level.FINE, (String)"Thread leaving _doRun(): {0}", (Object)thread);
                           var26 = false;
                        } else {
                           var26 = false;
                        }
                        break label470;
                     }

                     Thread thread = Thread.currentThread();
                     thread.setContextClassLoader(old);
                     if (isTraceEnabled()) {
                        LOGGER.log(Level.FINE, (String)"Thread leaving _doRun(): {0}", (Object)thread);
                     }
                     break label461;
                  }

                  thread = Thread.currentThread();
                  thread.setContextClassLoader(old);
                  if (isTraceEnabled()) {
                     LOGGER.log(Level.FINE, (String)"Thread leaving _doRun(): {0}", (Object)thread);
                     var26 = false;
                  } else {
                     var26 = false;
                  }
                  break label469;
               }

               needsToReenter = false;
               var26 = false;
            } finally {
               if (var26) {
                  if ((Boolean)isRequireUnlock.value) {
                     synchronized(this) {
                        this.currentThread = null;
                     }

                     this.lock.unlock();
                  }

               }
            }

            if ((Boolean)isRequireUnlock.value) {
               synchronized(this) {
                  this.currentThread = null;
               }

               this.lock.unlock();
            }

            return needsToReenter;
         }

         if ((Boolean)isRequireUnlock.value) {
            synchronized(this) {
               this.currentThread = null;
            }

            this.lock.unlock();
         }

         return var47;
      }

      if ((Boolean)isRequireUnlock.value) {
         synchronized(this) {
            this.currentThread = null;
         }

         this.lock.unlock();
      }

      return var47;
   }

   private boolean __doRun(Holder<Boolean> isRequireUnlock, List<FiberContextSwitchInterceptor> originalInterceptors) {
      assert this.lock.isHeldByCurrentThread();

      Fiber old = (Fiber)CURRENT_FIBER.get();
      CURRENT_FIBER.set(this);
      boolean traceEnabled = LOGGER.isLoggable(Level.FINER);

      try {
         for(boolean abortResponse = false; this.isReady(originalInterceptors); this.dumpFiberContext("After tube execution")) {
            if (this.isCanceled) {
               this.next = null;
               this.throwable = null;
               this.contsSize = 0;
               return false;
            }

            try {
               NextAction na;
               Tube last;
               boolean var8;
               if (this.throwable != null) {
                  if (this.contsSize == 0 || abortResponse) {
                     this.contsSize = 0;
                     var8 = false;
                     return var8;
                  }

                  last = this.popCont();
                  if (traceEnabled) {
                     LOGGER.log(Level.FINER, "{0} {1}.processException({2})", new Object[]{this.getName(), last, this.throwable});
                  }

                  na = last.processException(this.throwable);
               } else if (this.next != null) {
                  if (traceEnabled) {
                     LOGGER.log(Level.FINER, "{0} {1}.processRequest({2})", new Object[]{this.getName(), this.next, this.packet != null ? "Packet@" + Integer.toHexString(this.packet.hashCode()) : "null"});
                  }

                  na = this.next.processRequest(this.packet);
                  last = this.next;
               } else {
                  if (this.contsSize == 0 || abortResponse) {
                     this.contsSize = 0;
                     var8 = false;
                     return var8;
                  }

                  last = this.popCont();
                  if (traceEnabled) {
                     LOGGER.log(Level.FINER, "{0} {1}.processResponse({2})", new Object[]{this.getName(), last, this.packet != null ? "Packet@" + Integer.toHexString(this.packet.hashCode()) : "null"});
                  }

                  na = last.processResponse(this.packet);
               }

               if (traceEnabled) {
                  LOGGER.log(Level.FINER, "{0} {1} returned with {2}", new Object[]{this.getName(), last, na});
               }

               if (na.kind != 4) {
                  if (na.kind != 3 && na.kind != 5) {
                     this.packet = na.packet;
                  }

                  this.throwable = na.throwable;
               }

               switch(na.kind) {
               case 0:
               case 7:
                  this.pushCont(last);
               case 1:
                  this.next = na.next;
                  if (na.kind == 7 && this.startedSync) {
                     var8 = false;
                     return var8;
                  }
                  break;
               case 4:
                  if (this.next != null) {
                     this.pushCont(last);
                  }

                  this.next = na.next;
                  if (this.suspend(isRequireUnlock, na.onExitRunnable)) {
                     var8 = true;
                     return var8;
                  }
                  break;
               case 5:
               case 6:
                  abortResponse = true;
                  if (isTraceEnabled()) {
                     LOGGER.log(Level.FINE, "Fiber {0} is aborting a response due to exception: {1}", new Object[]{this, na.throwable});
                  }
               case 2:
               case 3:
                  this.next = null;
                  break;
               default:
                  throw new AssertionError();
               }
            } catch (RuntimeException var13) {
               if (traceEnabled) {
                  LOGGER.log(Level.FINER, (String)(this.getName() + " Caught " + var13 + ". Start stack unwinding"), (Throwable)var13);
               }

               this.throwable = var13;
            } catch (Error var14) {
               if (traceEnabled) {
                  LOGGER.log(Level.FINER, (String)(this.getName() + " Caught " + var14 + ". Start stack unwinding"), (Throwable)var14);
               }

               this.throwable = var14;
            }
         }

         return false;
      } finally {
         CURRENT_FIBER.set(old);
      }
   }

   private void pushCont(Tube tube) {
      this.conts[this.contsSize++] = tube;
      int len = this.conts.length;
      if (this.contsSize == len) {
         Tube[] newBuf = new Tube[len * 2];
         System.arraycopy(this.conts, 0, newBuf, 0, len);
         this.conts = newBuf;
      }

   }

   private Tube popCont() {
      return this.conts[--this.contsSize];
   }

   private Tube peekCont() {
      int index = this.contsSize - 1;
      return index >= 0 && index < this.conts.length ? this.conts[index] : null;
   }

   public void resetCont(Tube[] conts, int contsSize) {
      this.conts = conts;
      this.contsSize = contsSize;
   }

   private boolean isReady(List<FiberContextSwitchInterceptor> originalInterceptors) {
      if (this.synchronous) {
         while(this.suspendedCount == 1) {
            try {
               if (isTraceEnabled()) {
                  LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[]{this.getName(), Thread.currentThread().getName()});
               }

               this.condition.await();
            } catch (InterruptedException var6) {
               this.interrupted = true;
            }
         }

         synchronized(this) {
            return this.interceptors == originalInterceptors;
         }
      } else if (this.suspendedCount > 0) {
         return false;
      } else {
         synchronized(this) {
            return this.interceptors == originalInterceptors;
         }
      }
   }

   private String getName() {
      return "engine-" + this.owner.id + "fiber-" + this.id;
   }

   public String toString() {
      return this.getName();
   }

   @Nullable
   public Packet getPacket() {
      return this.packet;
   }

   public Fiber.CompletionCallback getCompletionCallback() {
      return this.completionCallback;
   }

   public void setCompletionCallback(Fiber.CompletionCallback completionCallback) {
      this.completionCallback = completionCallback;
   }

   public static boolean isSynchronous() {
      return current().synchronous;
   }

   public boolean isStartedSync() {
      return this.startedSync;
   }

   @NotNull
   public static Fiber current() {
      Fiber fiber = (Fiber)CURRENT_FIBER.get();
      if (fiber == null) {
         throw new IllegalStateException("Can be only used from fibers");
      } else {
         return fiber;
      }
   }

   public static Fiber getCurrentIfSet() {
      return (Fiber)CURRENT_FIBER.get();
   }

   private static boolean isTraceEnabled() {
      return LOGGER.isLoggable(Level.FINE);
   }

   public <S> S getSPI(Class<S> spiType) {
      Iterator var2 = this.components.iterator();

      Object spi;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         Component c = (Component)var2.next();
         spi = c.getSPI(spiType);
      } while(spi == null);

      return spi;
   }

   public Set<Component> getComponents() {
      return this.components;
   }

   private static class PlaceholderTube extends AbstractTubeImpl {
      private PlaceholderTube() {
      }

      public NextAction processRequest(Packet request) {
         throw new UnsupportedOperationException();
      }

      public NextAction processResponse(Packet response) {
         throw new UnsupportedOperationException();
      }

      public NextAction processException(Throwable t) {
         return this.doThrow(t);
      }

      public void preDestroy() {
      }

      public Fiber.PlaceholderTube copy(TubeCloner cloner) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      PlaceholderTube(Object x0) {
         this();
      }
   }

   private class InterceptorHandler implements FiberContextSwitchInterceptor.Work<Tube, Tube> {
      private final Holder<Boolean> isUnlockRequired;
      private final List<FiberContextSwitchInterceptor> ints;
      private int idx;

      public InterceptorHandler(Holder<Boolean> isUnlockRequired, List<FiberContextSwitchInterceptor> ints) {
         this.isUnlockRequired = isUnlockRequired;
         this.ints = ints;
      }

      Tube invoke(Tube next) {
         this.idx = 0;
         return this.execute(next);
      }

      public Tube execute(Tube next) {
         if (this.idx == this.ints.size()) {
            Fiber.this.next = next;
            return (Tube)(Fiber.this.__doRun(this.isUnlockRequired, this.ints) ? Fiber.PLACEHOLDER : Fiber.this.next);
         } else {
            FiberContextSwitchInterceptor interceptor = (FiberContextSwitchInterceptor)this.ints.get(this.idx++);
            return (Tube)interceptor.execute(Fiber.this, next, this);
         }
      }
   }

   private static final class OnExitRunnableException extends RuntimeException {
      private static final long serialVersionUID = 1L;
      Throwable target;

      public OnExitRunnableException(Throwable target) {
         super((Throwable)null);
         this.target = target;
      }
   }

   public interface CompletionCallback {
      void onCompletion(@NotNull Packet var1);

      void onCompletion(@NotNull Throwable var1);
   }

   /** @deprecated */
   public interface Listener {
      void fiberSuspended(Fiber var1);

      void fiberResumed(Fiber var1);
   }
}
