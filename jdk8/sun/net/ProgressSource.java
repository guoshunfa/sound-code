package sun.net;

import java.net.URL;

public class ProgressSource {
   private URL url;
   private String method;
   private String contentType;
   private long progress;
   private long lastProgress;
   private long expected;
   private ProgressSource.State state;
   private boolean connected;
   private int threshold;
   private ProgressMonitor progressMonitor;

   public ProgressSource(URL var1, String var2) {
      this(var1, var2, -1L);
   }

   public ProgressSource(URL var1, String var2, long var3) {
      this.progress = 0L;
      this.lastProgress = 0L;
      this.expected = -1L;
      this.connected = false;
      this.threshold = 8192;
      this.url = var1;
      this.method = var2;
      this.contentType = "content/unknown";
      this.progress = 0L;
      this.lastProgress = 0L;
      this.expected = var3;
      this.state = ProgressSource.State.NEW;
      this.progressMonitor = ProgressMonitor.getDefault();
      this.threshold = this.progressMonitor.getProgressUpdateThreshold();
   }

   public boolean connected() {
      if (!this.connected) {
         this.connected = true;
         this.state = ProgressSource.State.CONNECTED;
         return false;
      } else {
         return true;
      }
   }

   public void close() {
      this.state = ProgressSource.State.DELETE;
   }

   public URL getURL() {
      return this.url;
   }

   public String getMethod() {
      return this.method;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String var1) {
      this.contentType = var1;
   }

   public long getProgress() {
      return this.progress;
   }

   public long getExpected() {
      return this.expected;
   }

   public ProgressSource.State getState() {
      return this.state;
   }

   public void beginTracking() {
      this.progressMonitor.registerSource(this);
   }

   public void finishTracking() {
      this.progressMonitor.unregisterSource(this);
   }

   public void updateProgress(long var1, long var3) {
      this.lastProgress = this.progress;
      this.progress = var1;
      this.expected = var3;
      if (!this.connected()) {
         this.state = ProgressSource.State.CONNECTED;
      } else {
         this.state = ProgressSource.State.UPDATE;
      }

      if (this.lastProgress / (long)this.threshold != this.progress / (long)this.threshold) {
         this.progressMonitor.updateProgress(this);
      }

      if (this.expected != -1L && this.progress >= this.expected && this.progress != 0L) {
         this.close();
      }

   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public String toString() {
      return this.getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]";
   }

   public static enum State {
      NEW,
      CONNECTED,
      UPDATE,
      DELETE;
   }
}
