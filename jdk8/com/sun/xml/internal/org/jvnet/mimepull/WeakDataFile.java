package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WeakDataFile extends WeakReference<DataFile> {
   private static final Logger LOGGER = Logger.getLogger(WeakDataFile.class.getName());
   private static ReferenceQueue<DataFile> refQueue = new ReferenceQueue();
   private static List<WeakDataFile> refList = new ArrayList();
   private final File file;
   private final RandomAccessFile raf;
   private static boolean hasCleanUpExecutor = false;

   WeakDataFile(DataFile df, File file) {
      super(df, refQueue);
      refList.add(this);
      this.file = file;

      try {
         this.raf = new RandomAccessFile(file, "rw");
      } catch (IOException var4) {
         throw new MIMEParsingException(var4);
      }

      if (!hasCleanUpExecutor) {
         drainRefQueueBounded();
      }

   }

   synchronized void read(long pointer, byte[] buf, int offset, int length) {
      try {
         this.raf.seek(pointer);
         this.raf.readFully(buf, offset, length);
      } catch (IOException var7) {
         throw new MIMEParsingException(var7);
      }
   }

   synchronized long writeTo(long pointer, byte[] data, int offset, int length) {
      try {
         this.raf.seek(pointer);
         this.raf.write(data, offset, length);
         return this.raf.getFilePointer();
      } catch (IOException var7) {
         throw new MIMEParsingException(var7);
      }
   }

   void close() {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, (String)"Deleting file = {0}", (Object)this.file.getName());
      }

      refList.remove(this);

      try {
         this.raf.close();
         boolean deleted = this.file.delete();
         if (!deleted && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, (String)"File {0} was not deleted", (Object)this.file.getAbsolutePath());
         }

      } catch (IOException var2) {
         throw new MIMEParsingException(var2);
      }
   }

   void renameTo(File f) {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, "Moving file={0} to={1}", new Object[]{this.file, f});
      }

      refList.remove(this);

      try {
         this.raf.close();
         boolean renamed = this.file.renameTo(f);
         if (!renamed && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "File {0} was not moved to {1}", new Object[]{this.file.getAbsolutePath(), f.getAbsolutePath()});
         }

      } catch (IOException var3) {
         throw new MIMEParsingException(var3);
      }
   }

   static void drainRefQueueBounded() {
      WeakDataFile weak;
      for(; (weak = (WeakDataFile)refQueue.poll()) != null; weak.close()) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"Cleaning file = {0} from reference queue.", (Object)weak.file);
         }
      }

   }

   static {
      CleanUpExecutorFactory executorFactory = CleanUpExecutorFactory.newInstance();
      if (executorFactory != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"Initializing clean up executor for MIMEPULL: {0}", (Object)executorFactory.getClass().getName());
         }

         Executor executor = executorFactory.getExecutor();
         executor.execute(new Runnable() {
            public void run() {
               while(true) {
                  try {
                     WeakDataFile weak = (WeakDataFile)WeakDataFile.refQueue.remove();
                     if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                        WeakDataFile.LOGGER.log(Level.FINE, (String)"Cleaning file = {0} from reference queue.", (Object)weak.file);
                     }

                     weak.close();
                  } catch (InterruptedException var3) {
                  }
               }
            }
         });
         hasCleanUpExecutor = true;
      }

   }
}
