package java.util.concurrent;

public interface RejectedExecutionHandler {
   void rejectedExecution(Runnable var1, ThreadPoolExecutor var2);
}
