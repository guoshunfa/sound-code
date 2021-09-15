package sun.rmi.transport;

import java.rmi.server.UID;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.rmi.runtime.RuntimeUtil;
import sun.security.action.GetLongAction;

public class DGCAckHandler {
   private static final long dgcAckTimeout = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.dgc.ackTimeout", 300000L)));
   private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged((PrivilegedAction)(new RuntimeUtil.GetInstanceAction()))).getScheduler();
   private static final Map<UID, DGCAckHandler> idTable = Collections.synchronizedMap(new HashMap());
   private final UID id;
   private List<Object> objList = new ArrayList();
   private Future<?> task = null;

   DGCAckHandler(UID var1) {
      this.id = var1;
      if (var1 != null) {
         assert !idTable.containsKey(var1);

         idTable.put(var1, this);
      }

   }

   synchronized void add(Object var1) {
      if (this.objList != null) {
         this.objList.add(var1);
      }

   }

   synchronized void startTimer() {
      if (this.objList != null && this.task == null) {
         this.task = scheduler.schedule(new Runnable() {
            public void run() {
               if (DGCAckHandler.this.id != null) {
                  DGCAckHandler.idTable.remove(DGCAckHandler.this.id);
               }

               DGCAckHandler.this.release();
            }
         }, dgcAckTimeout, TimeUnit.MILLISECONDS);
      }

   }

   synchronized void release() {
      if (this.task != null) {
         this.task.cancel(false);
         this.task = null;
      }

      this.objList = null;
   }

   public static void received(UID var0) {
      DGCAckHandler var1 = (DGCAckHandler)idTable.remove(var0);
      if (var1 != null) {
         var1.release();
      }

   }
}
