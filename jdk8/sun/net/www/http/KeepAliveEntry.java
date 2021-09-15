package sun.net.www.http;

class KeepAliveEntry {
   HttpClient hc;
   long idleStartTime;

   KeepAliveEntry(HttpClient var1, long var2) {
      this.hc = var1;
      this.idleStartTime = var2;
   }
}
