package java.awt;

abstract class MediaEntry {
   MediaTracker tracker;
   int ID;
   MediaEntry next;
   int status;
   boolean cancelled;
   static final int LOADING = 1;
   static final int ABORTED = 2;
   static final int ERRORED = 4;
   static final int COMPLETE = 8;
   static final int LOADSTARTED = 13;
   static final int DONE = 14;

   MediaEntry(MediaTracker var1, int var2) {
      this.tracker = var1;
      this.ID = var2;
   }

   abstract Object getMedia();

   static MediaEntry insert(MediaEntry var0, MediaEntry var1) {
      MediaEntry var2 = var0;

      MediaEntry var3;
      for(var3 = null; var2 != null && var2.ID <= var1.ID; var2 = var2.next) {
         var3 = var2;
      }

      var1.next = var2;
      if (var3 == null) {
         var0 = var1;
      } else {
         var3.next = var1;
      }

      return var0;
   }

   int getID() {
      return this.ID;
   }

   abstract void startLoad();

   void cancel() {
      this.cancelled = true;
   }

   synchronized int getStatus(boolean var1, boolean var2) {
      if (var1 && (this.status & 13) == 0) {
         this.status = this.status & -3 | 1;
         this.startLoad();
      }

      return this.status;
   }

   void setStatus(int var1) {
      synchronized(this) {
         this.status = var1;
      }

      this.tracker.setDone();
   }
}
