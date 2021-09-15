package sun.net.www.http;

class KeepAliveCleanerEntry {
   KeepAliveStream kas;
   HttpClient hc;

   public KeepAliveCleanerEntry(KeepAliveStream var1, HttpClient var2) {
      this.kas = var1;
      this.hc = var2;
   }

   protected KeepAliveStream getKeepAliveStream() {
      return this.kas;
   }

   protected HttpClient getHttpClient() {
      return this.hc;
   }

   protected void setQueuedForCleanup() {
      this.kas.queuedForCleanup = true;
   }

   protected boolean getQueuedForCleanup() {
      return this.kas.queuedForCleanup;
   }
}
