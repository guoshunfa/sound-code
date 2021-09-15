package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import sun.awt.AppContext;
import sun.swing.AccumulativeRunnable;

public abstract class SwingWorker<T, V> implements RunnableFuture<T> {
   private static final int MAX_WORKER_THREADS = 10;
   private volatile int progress;
   private volatile SwingWorker.StateValue state;
   private final FutureTask<T> future;
   private final PropertyChangeSupport propertyChangeSupport;
   private AccumulativeRunnable<V> doProcess;
   private AccumulativeRunnable<Integer> doNotifyProgressChange;
   private final AccumulativeRunnable<Runnable> doSubmit = getDoSubmit();
   private static final Object DO_SUBMIT_KEY = new StringBuilder("doSubmit");

   public SwingWorker() {
      Callable var1 = new Callable<T>() {
         public T call() throws Exception {
            SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
            return SwingWorker.this.doInBackground();
         }
      };
      this.future = new FutureTask<T>(var1) {
         protected void done() {
            SwingWorker.this.doneEDT();
            SwingWorker.this.setState(SwingWorker.StateValue.DONE);
         }
      };
      this.state = SwingWorker.StateValue.PENDING;
      this.propertyChangeSupport = new SwingWorker.SwingWorkerPropertyChangeSupport(this);
      this.doProcess = null;
      this.doNotifyProgressChange = null;
   }

   protected abstract T doInBackground() throws Exception;

   public final void run() {
      this.future.run();
   }

   @SafeVarargs
   protected final void publish(V... var1) {
      synchronized(this) {
         if (this.doProcess == null) {
            this.doProcess = new AccumulativeRunnable<V>() {
               public void run(List<V> var1) {
                  SwingWorker.this.process(var1);
               }

               protected void submit() {
                  SwingWorker.this.doSubmit.add(this);
               }
            };
         }
      }

      this.doProcess.add(var1);
   }

   protected void process(List<V> var1) {
   }

   protected void done() {
   }

   protected final void setProgress(int var1) {
      if (var1 >= 0 && var1 <= 100) {
         if (this.progress != var1) {
            int var2 = this.progress;
            this.progress = var1;
            if (this.getPropertyChangeSupport().hasListeners("progress")) {
               synchronized(this) {
                  if (this.doNotifyProgressChange == null) {
                     this.doNotifyProgressChange = new AccumulativeRunnable<Integer>() {
                        public void run(List<Integer> var1) {
                           SwingWorker.this.firePropertyChange("progress", var1.get(0), var1.get(var1.size() - 1));
                        }

                        protected void submit() {
                           SwingWorker.this.doSubmit.add(this);
                        }
                     };
                  }
               }

               this.doNotifyProgressChange.add(var2, var1);
            }
         }
      } else {
         throw new IllegalArgumentException("the value should be from 0 to 100");
      }
   }

   public final int getProgress() {
      return this.progress;
   }

   public final void execute() {
      getWorkersExecutorService().execute(this);
   }

   public final boolean cancel(boolean var1) {
      return this.future.cancel(var1);
   }

   public final boolean isCancelled() {
      return this.future.isCancelled();
   }

   public final boolean isDone() {
      return this.future.isDone();
   }

   public final T get() throws InterruptedException, ExecutionException {
      return this.future.get();
   }

   public final T get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      return this.future.get(var1, var3);
   }

   public final void addPropertyChangeListener(PropertyChangeListener var1) {
      this.getPropertyChangeSupport().addPropertyChangeListener(var1);
   }

   public final void removePropertyChangeListener(PropertyChangeListener var1) {
      this.getPropertyChangeSupport().removePropertyChangeListener(var1);
   }

   public final void firePropertyChange(String var1, Object var2, Object var3) {
      this.getPropertyChangeSupport().firePropertyChange(var1, var2, var3);
   }

   public final PropertyChangeSupport getPropertyChangeSupport() {
      return this.propertyChangeSupport;
   }

   public final SwingWorker.StateValue getState() {
      return this.isDone() ? SwingWorker.StateValue.DONE : this.state;
   }

   private void setState(SwingWorker.StateValue var1) {
      SwingWorker.StateValue var2 = this.state;
      this.state = var1;
      this.firePropertyChange("state", var2, var1);
   }

   private void doneEDT() {
      Runnable var1 = new Runnable() {
         public void run() {
            SwingWorker.this.done();
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         var1.run();
      } else {
         this.doSubmit.add(var1);
      }

   }

   private static synchronized ExecutorService getWorkersExecutorService() {
      AppContext var0 = AppContext.getAppContext();
      final Object var1 = (ExecutorService)var0.get(SwingWorker.class);
      if (var1 == null) {
         ThreadFactory var2 = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(Runnable var1) {
               Thread var2 = this.defaultFactory.newThread(var1);
               var2.setName("SwingWorker-" + var2.getName());
               var2.setDaemon(true);
               return var2;
            }
         };
         var1 = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MINUTES, new LinkedBlockingQueue(), var2);
         var0.put(SwingWorker.class, var1);
         var0.addPropertyChangeListener("disposed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent var1x) {
               boolean var2 = (Boolean)var1x.getNewValue();
               if (var2) {
                  WeakReference var3 = new WeakReference(var1);
                  final ExecutorService var4 = (ExecutorService)var3.get();
                  if (var4 != null) {
                     AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                           var4.shutdown();
                           return null;
                        }
                     });
                  }
               }

            }
         });
      }

      return (ExecutorService)var1;
   }

   private static AccumulativeRunnable<Runnable> getDoSubmit() {
      synchronized(DO_SUBMIT_KEY) {
         AppContext var1 = AppContext.getAppContext();
         Object var2 = var1.get(DO_SUBMIT_KEY);
         if (var2 == null) {
            var2 = new SwingWorker.DoSubmitAccumulativeRunnable();
            var1.put(DO_SUBMIT_KEY, var2);
         }

         return (AccumulativeRunnable)var2;
      }
   }

   private class SwingWorkerPropertyChangeSupport extends PropertyChangeSupport {
      SwingWorkerPropertyChangeSupport(Object var2) {
         super(var2);
      }

      public void firePropertyChange(final PropertyChangeEvent var1) {
         if (SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(var1);
         } else {
            SwingWorker.this.doSubmit.add(new Runnable() {
               public void run() {
                  SwingWorkerPropertyChangeSupport.this.firePropertyChange(var1);
               }
            });
         }

      }
   }

   private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> implements ActionListener {
      private static final int DELAY = 33;

      private DoSubmitAccumulativeRunnable() {
      }

      protected void run(List<Runnable> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Runnable var3 = (Runnable)var2.next();
            var3.run();
         }

      }

      protected void submit() {
         Timer var1 = new Timer(33, this);
         var1.setRepeats(false);
         var1.start();
      }

      public void actionPerformed(ActionEvent var1) {
         this.run();
      }

      // $FF: synthetic method
      DoSubmitAccumulativeRunnable(Object var1) {
         this();
      }
   }

   public static enum StateValue {
      PENDING,
      STARTED,
      DONE;
   }
}
