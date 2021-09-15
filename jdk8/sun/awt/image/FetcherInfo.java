package sun.awt.image;

import java.util.Vector;
import sun.awt.AppContext;

class FetcherInfo {
   static final int MAX_NUM_FETCHERS_PER_APPCONTEXT = 4;
   Thread[] fetchers = new Thread[4];
   int numFetchers = 0;
   int numWaiting = 0;
   Vector waitList = new Vector();
   private static final Object FETCHER_INFO_KEY = new StringBuffer("FetcherInfo");

   private FetcherInfo() {
   }

   static FetcherInfo getFetcherInfo() {
      AppContext var0 = AppContext.getAppContext();
      synchronized(var0) {
         FetcherInfo var2 = (FetcherInfo)var0.get(FETCHER_INFO_KEY);
         if (var2 == null) {
            var2 = new FetcherInfo();
            var0.put(FETCHER_INFO_KEY, var2);
         }

         return var2;
      }
   }
}
