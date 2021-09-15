package com.sun.imageio.stream;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.WeakHashMap;
import javax.imageio.stream.ImageInputStream;

public class StreamCloser {
   private static WeakHashMap<StreamCloser.CloseAction, Object> toCloseQueue;
   private static Thread streamCloser;

   public static void addToQueue(StreamCloser.CloseAction var0) {
      Class var1 = StreamCloser.class;
      synchronized(StreamCloser.class) {
         if (toCloseQueue == null) {
            toCloseQueue = new WeakHashMap();
         }

         toCloseQueue.put(var0, (Object)null);
         if (streamCloser == null) {
            final Runnable var2 = new Runnable() {
               public void run() {
                  if (StreamCloser.toCloseQueue != null) {
                     Class var1 = StreamCloser.class;
                     synchronized(StreamCloser.class) {
                        Set var2 = StreamCloser.toCloseQueue.keySet();
                        StreamCloser.CloseAction[] var3 = new StreamCloser.CloseAction[var2.size()];
                        var3 = (StreamCloser.CloseAction[])var2.toArray(var3);
                        StreamCloser.CloseAction[] var4 = var3;
                        int var5 = var3.length;

                        for(int var6 = 0; var6 < var5; ++var6) {
                           StreamCloser.CloseAction var7 = var4[var6];
                           if (var7 != null) {
                              try {
                                 var7.performAction();
                              } catch (IOException var10) {
                              }
                           }
                        }
                     }
                  }

               }
            };
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  ThreadGroup var1 = Thread.currentThread().getThreadGroup();

                  for(ThreadGroup var2x = var1; var2x != null; var2x = var2x.getParent()) {
                     var1 = var2x;
                  }

                  StreamCloser.streamCloser = new Thread(var1, var2);
                  StreamCloser.streamCloser.setContextClassLoader((ClassLoader)null);
                  Runtime.getRuntime().addShutdownHook(StreamCloser.streamCloser);
                  return null;
               }
            });
         }

      }
   }

   public static void removeFromQueue(StreamCloser.CloseAction var0) {
      Class var1 = StreamCloser.class;
      synchronized(StreamCloser.class) {
         if (toCloseQueue != null) {
            toCloseQueue.remove(var0);
         }

      }
   }

   public static StreamCloser.CloseAction createCloseAction(ImageInputStream var0) {
      return new StreamCloser.CloseAction(var0);
   }

   public static final class CloseAction {
      private ImageInputStream iis;

      private CloseAction(ImageInputStream var1) {
         this.iis = var1;
      }

      public void performAction() throws IOException {
         if (this.iis != null) {
            this.iis.close();
         }

      }

      // $FF: synthetic method
      CloseAction(ImageInputStream var1, Object var2) {
         this(var1);
      }
   }
}
