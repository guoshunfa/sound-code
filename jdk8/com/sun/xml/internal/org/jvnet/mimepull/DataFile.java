package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;

final class DataFile {
   private WeakDataFile weak;
   private long writePointer = 0L;

   DataFile(File file) {
      this.weak = new WeakDataFile(this, file);
   }

   void close() {
      this.weak.close();
   }

   synchronized void read(long pointer, byte[] buf, int offset, int length) {
      this.weak.read(pointer, buf, offset, length);
   }

   void renameTo(File f) {
      this.weak.renameTo(f);
   }

   synchronized long writeTo(byte[] data, int offset, int length) {
      long temp = this.writePointer;
      this.writePointer = this.weak.writeTo(this.writePointer, data, offset, length);
      return temp;
   }
}
