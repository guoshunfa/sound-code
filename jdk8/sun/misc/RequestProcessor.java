package sun.misc;

public class RequestProcessor implements Runnable {
   private static Queue<Request> requestQueue;
   private static Thread dispatcher;

   public static void postRequest(Request var0) {
      lazyInitialize();
      requestQueue.enqueue(var0);
   }

   public void run() {
      lazyInitialize();

      while(true) {
         while(true) {
            try {
               Request var1 = (Request)requestQueue.dequeue();

               try {
                  var1.execute();
               } catch (Throwable var3) {
               }
            } catch (InterruptedException var4) {
            }
         }
      }
   }

   public static synchronized void startProcessing() {
      if (dispatcher == null) {
         dispatcher = new Thread(new RequestProcessor(), "Request Processor");
         dispatcher.setPriority(7);
         dispatcher.start();
      }

   }

   private static synchronized void lazyInitialize() {
      if (requestQueue == null) {
         requestQueue = new Queue();
      }

   }
}
