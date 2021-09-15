package sun.misc;

import java.io.FileDescriptor;

public interface JavaIOFileDescriptorAccess {
   void set(FileDescriptor var1, int var2);

   int get(FileDescriptor var1);

   void setHandle(FileDescriptor var1, long var2);

   long getHandle(FileDescriptor var1);
}
