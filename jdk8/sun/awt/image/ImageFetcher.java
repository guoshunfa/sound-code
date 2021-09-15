package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;

class ImageFetcher extends Thread {
   static final int HIGH_PRIORITY = 8;
   static final int LOW_PRIORITY = 3;
   static final int ANIM_PRIORITY = 2;
   static final int TIMEOUT = 5000;

   private ImageFetcher(ThreadGroup var1, int var2) {
      super(var1, "Image Fetcher " + var2);
      this.setDaemon(true);
   }

   public static boolean add(ImageFetchable var0) {
      FetcherInfo var1 = FetcherInfo.getFetcherInfo();
      synchronized(var1.waitList) {
         if (!var1.waitList.contains(var0)) {
            var1.waitList.addElement(var0);
            if (var1.numWaiting == 0 && var1.numFetchers < var1.fetchers.length) {
               createFetchers(var1);
            }

            if (var1.numFetchers <= 0) {
               var1.waitList.removeElement(var0);
               return false;
            }

            var1.waitList.notify();
         }

         return true;
      }
   }

   public static void remove(ImageFetchable var0) {
      FetcherInfo var1 = FetcherInfo.getFetcherInfo();
      synchronized(var1.waitList) {
         if (var1.waitList.contains(var0)) {
            var1.waitList.removeElement(var0);
         }

      }
   }

   public static boolean isFetcher(Thread var0) {
      FetcherInfo var1 = FetcherInfo.getFetcherInfo();
      synchronized(var1.waitList) {
         for(int var3 = 0; var3 < var1.fetchers.length; ++var3) {
            if (var1.fetchers[var3] == var0) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean amFetcher() {
      return isFetcher(Thread.currentThread());
   }

   private static ImageFetchable nextImage() {
      FetcherInfo var0 = FetcherInfo.getFetcherInfo();
      synchronized(var0.waitList) {
         ImageFetchable var2 = null;
         long var3 = System.currentTimeMillis() + 5000L;

         while(var2 == null) {
            while(var0.waitList.size() == 0) {
               long var5 = System.currentTimeMillis();
               if (var5 >= var3) {
                  return null;
               }

               Object var8;
               try {
                  ++var0.numWaiting;
                  var0.waitList.wait(var3 - var5);
                  continue;
               } catch (InterruptedException var14) {
                  var8 = null;
               } finally {
                  --var0.numWaiting;
               }

               return (ImageFetchable)var8;
            }

            var2 = (ImageFetchable)var0.waitList.elementAt(0);
            var0.waitList.removeElement(var2);
         }

         return var2;
      }
   }

   public void run() {
      FetcherInfo var1 = FetcherInfo.getFetcherInfo();
      boolean var17 = false;

      Thread var3;
      int var4;
      label172: {
         try {
            var17 = true;
            this.fetchloop();
            var17 = false;
            break label172;
         } catch (Exception var21) {
            var21.printStackTrace();
            var17 = false;
         } finally {
            if (var17) {
               synchronized(var1.waitList) {
                  Thread var9 = Thread.currentThread();
                  int var10 = 0;

                  while(true) {
                     if (var10 >= var1.fetchers.length) {
                        ;
                     } else {
                        if (var1.fetchers[var10] == var9) {
                           var1.fetchers[var10] = null;
                           --var1.numFetchers;
                        }

                        ++var10;
                     }
                  }
               }
            }
         }

         synchronized(var1.waitList) {
            var3 = Thread.currentThread();

            for(var4 = 0; var4 < var1.fetchers.length; ++var4) {
               if (var1.fetchers[var4] == var3) {
                  var1.fetchers[var4] = null;
                  --var1.numFetchers;
               }
            }

            return;
         }
      }

      synchronized(var1.waitList) {
         var3 = Thread.currentThread();

         for(var4 = 0; var4 < var1.fetchers.length; ++var4) {
            if (var1.fetchers[var4] == var3) {
               var1.fetchers[var4] = null;
               --var1.numFetchers;
            }
         }
      }

   }

   private void fetchloop() {
      for(Thread var1 = Thread.currentThread(); isFetcher(var1); stoppingAnimation(var1)) {
         Thread.interrupted();
         var1.setPriority(8);
         ImageFetchable var2 = nextImage();
         if (var2 == null) {
            return;
         }

         try {
            var2.doFetch();
         } catch (Exception var4) {
            System.err.println("Uncaught error fetching image:");
            var4.printStackTrace();
         }
      }

   }

   static void startingAnimation() {
      FetcherInfo var0 = FetcherInfo.getFetcherInfo();
      Thread var1 = Thread.currentThread();
      synchronized(var0.waitList) {
         int var3 = 0;

         while(true) {
            if (var3 >= var0.fetchers.length) {
               break;
            }

            if (var0.fetchers[var3] == var1) {
               var0.fetchers[var3] = null;
               --var0.numFetchers;
               var1.setName("Image Animator " + var3);
               if (var0.waitList.size() > var0.numWaiting) {
                  createFetchers(var0);
               }

               return;
            }

            ++var3;
         }
      }

      var1.setPriority(2);
      var1.setName("Image Animator");
   }

   private static void stoppingAnimation(Thread var0) {
      FetcherInfo var1 = FetcherInfo.getFetcherInfo();
      synchronized(var1.waitList) {
         int var3 = -1;

         for(int var4 = 0; var4 < var1.fetchers.length; ++var4) {
            if (var1.fetchers[var4] == var0) {
               return;
            }

            if (var1.fetchers[var4] == null) {
               var3 = var4;
            }
         }

         if (var3 >= 0) {
            var1.fetchers[var3] = var0;
            ++var1.numFetchers;
            var0.setName("Image Fetcher " + var3);
         }
      }
   }

   private static void createFetchers(final FetcherInfo var0) {
      AppContext var1 = AppContext.getAppContext();
      ThreadGroup var2 = var1.getThreadGroup();

      final ThreadGroup var3;
      try {
         if (var2.getParent() != null) {
            var3 = var2;
         } else {
            var2 = Thread.currentThread().getThreadGroup();

            for(ThreadGroup var4 = var2.getParent(); var4 != null && var4.getParent() != null; var4 = var4.getParent()) {
               var2 = var4;
            }

            var3 = var2;
         }
      } catch (SecurityException var5) {
         var3 = var1.getThreadGroup();
      }

      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            for(int var1 = 0; var1 < var0.fetchers.length; ++var1) {
               if (var0.fetchers[var1] == null) {
                  ImageFetcher var2 = new ImageFetcher(var3, var1);

                  try {
                     var2.start();
                     var0.fetchers[var1] = var2;
                     ++var0.numFetchers;
                     break;
                  } catch (Error var4) {
                  }
               }
            }

            return null;
         }
      });
   }

   // $FF: synthetic method
   ImageFetcher(ThreadGroup var1, int var2, Object var3) {
      this(var1, var2);
   }
}
