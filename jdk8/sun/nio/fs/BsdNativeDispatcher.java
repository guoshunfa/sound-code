package sun.nio.fs;

class BsdNativeDispatcher extends UnixNativeDispatcher {
   protected BsdNativeDispatcher() {
   }

   static native long getfsstat() throws UnixException;

   static native int fsstatEntry(long var0, UnixMountEntry var2) throws UnixException;

   static native void endfsstat(long var0) throws UnixException;

   private static native void initIDs();

   static {
      initIDs();
   }
}
