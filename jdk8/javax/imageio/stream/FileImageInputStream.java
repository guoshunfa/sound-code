package javax.imageio.stream;

import com.sun.imageio.stream.CloseableDisposerRecord;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import sun.java2d.Disposer;

public class FileImageInputStream extends ImageInputStreamImpl {
   private RandomAccessFile raf;
   private final Object disposerReferent;
   private final CloseableDisposerRecord disposerRecord;

   public FileImageInputStream(File var1) throws FileNotFoundException, IOException {
      this(var1 == null ? null : new RandomAccessFile(var1, "r"));
   }

   public FileImageInputStream(RandomAccessFile var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("raf == null!");
      } else {
         this.raf = var1;
         this.disposerRecord = new CloseableDisposerRecord(var1);
         if (this.getClass() == FileImageInputStream.class) {
            this.disposerReferent = new Object();
            Disposer.addRecord(this.disposerReferent, this.disposerRecord);
         } else {
            this.disposerReferent = new StreamFinalizer(this);
         }

      }
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      int var1 = this.raf.read();
      if (var1 != -1) {
         ++this.streamPos;
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      int var4 = this.raf.read(var1, var2, var3);
      if (var4 != -1) {
         this.streamPos += (long)var4;
      }

      return var4;
   }

   public long length() {
      try {
         this.checkClosed();
         return this.raf.length();
      } catch (IOException var2) {
         return -1L;
      }
   }

   public void seek(long var1) throws IOException {
      this.checkClosed();
      if (var1 < this.flushedPos) {
         throw new IndexOutOfBoundsException("pos < flushedPos!");
      } else {
         this.bitOffset = 0;
         this.raf.seek(var1);
         this.streamPos = this.raf.getFilePointer();
      }
   }

   public void close() throws IOException {
      super.close();
      this.disposerRecord.dispose();
      this.raf = null;
   }

   protected void finalize() throws Throwable {
   }
}
