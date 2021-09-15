package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.List;

abstract class FileLockTable {
   protected FileLockTable() {
   }

   public static FileLockTable newSharedFileLockTable(Channel var0, FileDescriptor var1) throws IOException {
      return new SharedFileLockTable(var0, var1);
   }

   public abstract void add(FileLock var1) throws OverlappingFileLockException;

   public abstract void remove(FileLock var1);

   public abstract List<FileLock> removeAll();

   public abstract void replace(FileLock var1, FileLock var2);
}
