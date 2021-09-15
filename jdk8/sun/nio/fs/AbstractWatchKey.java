package sun.nio.fs;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract class AbstractWatchKey implements WatchKey {
   static final int MAX_EVENT_LIST_SIZE = 512;
   static final AbstractWatchKey.Event<Object> OVERFLOW_EVENT;
   private final AbstractWatchService watcher;
   private final Path dir;
   private AbstractWatchKey.State state;
   private List<WatchEvent<?>> events;
   private Map<Object, WatchEvent<?>> lastModifyEvents;

   protected AbstractWatchKey(Path var1, AbstractWatchService var2) {
      this.watcher = var2;
      this.dir = var1;
      this.state = AbstractWatchKey.State.READY;
      this.events = new ArrayList();
      this.lastModifyEvents = new HashMap();
   }

   final AbstractWatchService watcher() {
      return this.watcher;
   }

   public Path watchable() {
      return this.dir;
   }

   final void signal() {
      synchronized(this) {
         if (this.state == AbstractWatchKey.State.READY) {
            this.state = AbstractWatchKey.State.SIGNALLED;
            this.watcher.enqueueKey(this);
         }

      }
   }

   final void signalEvent(WatchEvent.Kind<?> var1, Object var2) {
      boolean var3 = var1 == StandardWatchEventKinds.ENTRY_MODIFY;
      synchronized(this) {
         int var5 = this.events.size();
         if (var5 > 0) {
            WatchEvent var6 = (WatchEvent)this.events.get(var5 - 1);
            if (var6.kind() == StandardWatchEventKinds.OVERFLOW || var1 == var6.kind() && Objects.equals(var2, var6.context())) {
               ((AbstractWatchKey.Event)var6).increment();
               return;
            }

            if (!this.lastModifyEvents.isEmpty()) {
               if (var3) {
                  WatchEvent var7 = (WatchEvent)this.lastModifyEvents.get(var2);
                  if (var7 != null) {
                     assert var7.kind() == StandardWatchEventKinds.ENTRY_MODIFY;

                     ((AbstractWatchKey.Event)var7).increment();
                     return;
                  }
               } else {
                  this.lastModifyEvents.remove(var2);
               }
            }

            if (var5 >= 512) {
               var1 = StandardWatchEventKinds.OVERFLOW;
               var3 = false;
               var2 = null;
            }
         }

         AbstractWatchKey.Event var10 = new AbstractWatchKey.Event(var1, var2);
         if (var3) {
            this.lastModifyEvents.put(var2, var10);
         } else if (var1 == StandardWatchEventKinds.OVERFLOW) {
            this.events.clear();
            this.lastModifyEvents.clear();
         }

         this.events.add(var10);
         this.signal();
      }
   }

   public final List<WatchEvent<?>> pollEvents() {
      synchronized(this) {
         List var2 = this.events;
         this.events = new ArrayList();
         this.lastModifyEvents.clear();
         return var2;
      }
   }

   public final boolean reset() {
      synchronized(this) {
         if (this.state == AbstractWatchKey.State.SIGNALLED && this.isValid()) {
            if (this.events.isEmpty()) {
               this.state = AbstractWatchKey.State.READY;
            } else {
               this.watcher.enqueueKey(this);
            }
         }

         return this.isValid();
      }
   }

   static {
      OVERFLOW_EVENT = new AbstractWatchKey.Event(StandardWatchEventKinds.OVERFLOW, (Object)null);
   }

   private static class Event<T> implements WatchEvent<T> {
      private final WatchEvent.Kind<T> kind;
      private final T context;
      private int count;

      Event(WatchEvent.Kind<T> var1, T var2) {
         this.kind = var1;
         this.context = var2;
         this.count = 1;
      }

      public WatchEvent.Kind<T> kind() {
         return this.kind;
      }

      public T context() {
         return this.context;
      }

      public int count() {
         return this.count;
      }

      void increment() {
         ++this.count;
      }
   }

   private static enum State {
      READY,
      SIGNALLED;
   }
}
