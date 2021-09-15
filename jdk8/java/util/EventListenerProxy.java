package java.util;

public abstract class EventListenerProxy<T extends EventListener> implements EventListener {
   private final T listener;

   public EventListenerProxy(T var1) {
      this.listener = var1;
   }

   public T getListener() {
      return this.listener;
   }
}
