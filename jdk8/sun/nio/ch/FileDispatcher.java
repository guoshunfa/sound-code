package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

abstract class FileDispatcher extends NativeDispatcher {
   public static final int NO_LOCK = -1;
   public static final int LOCKED = 0;
   public static final int RET_EX_LOCK = 1;
   public static final int INTERRUPTED = 2;

   abstract int force(FileDescriptor var1, boolean var2) throws IOException;

   abstract int truncate(FileDescriptor var1, long var2) throws IOException;

   abstract long size(FileDescriptor var1) throws IOException;

   abstract int lock(FileDescriptor var1, boolean var2, long var3, long var5, boolean var7) throws IOException;

   abstract void release(FileDescriptor var1, long var2, long var4) throws IOException;

   abstract FileDescriptor duplicateForMapping(FileDescriptor var1) throws IOException;

   abstract boolean canTransferToDirectly(SelectableChannel var1);

   abstract boolean transferToDirectlyNeedsPositionLock();
}
