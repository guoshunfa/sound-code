package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEConfig {
   private static final int DEFAULT_CHUNK_SIZE = 8192;
   private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
   private static final String DEFAULT_FILE_PREFIX = "MIME";
   private static final Logger LOGGER = Logger.getLogger(MIMEConfig.class.getName());
   boolean parseEagerly;
   int chunkSize;
   long memoryThreshold;
   File tempDir;
   String prefix;
   String suffix;

   private MIMEConfig(boolean parseEagerly, int chunkSize, long inMemoryThreshold, String dir, String prefix, String suffix) {
      this.parseEagerly = parseEagerly;
      this.chunkSize = chunkSize;
      this.memoryThreshold = inMemoryThreshold;
      this.prefix = prefix;
      this.suffix = suffix;
      this.setDir(dir);
   }

   public MIMEConfig() {
      this(false, 8192, 1048576L, (String)null, "MIME", (String)null);
   }

   boolean isParseEagerly() {
      return this.parseEagerly;
   }

   public void setParseEagerly(boolean parseEagerly) {
      this.parseEagerly = parseEagerly;
   }

   int getChunkSize() {
      return this.chunkSize;
   }

   void setChunkSize(int chunkSize) {
      this.chunkSize = chunkSize;
   }

   long getMemoryThreshold() {
      return this.memoryThreshold;
   }

   public void setMemoryThreshold(long memoryThreshold) {
      this.memoryThreshold = memoryThreshold;
   }

   boolean isOnlyMemory() {
      return this.memoryThreshold == -1L;
   }

   File getTempDir() {
      return this.tempDir;
   }

   String getTempFilePrefix() {
      return this.prefix;
   }

   String getTempFileSuffix() {
      return this.suffix;
   }

   public final void setDir(String dir) {
      if (this.tempDir == null && dir != null && !dir.equals("")) {
         this.tempDir = new File(dir);
      }

   }

   public void validate() {
      if (!this.isOnlyMemory()) {
         try {
            File tempFile = this.tempDir == null ? File.createTempFile(this.prefix, this.suffix) : File.createTempFile(this.prefix, this.suffix, this.tempDir);
            boolean deleted = tempFile.delete();
            if (!deleted && LOGGER.isLoggable(Level.INFO)) {
               LOGGER.log(Level.INFO, (String)"File {0} was not deleted", (Object)tempFile.getAbsolutePath());
            }
         } catch (Exception var3) {
            this.memoryThreshold = -1L;
         }
      }

   }
}
