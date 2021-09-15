package com.sun.corba.se.impl.encoding;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.WeakHashMap;

class CodeSetCache {
   private ThreadLocal converterCaches = new ThreadLocal() {
      public Object initialValue() {
         return new Map[]{new WeakHashMap(), new WeakHashMap()};
      }
   };
   private static final int BTC_CACHE_MAP = 0;
   private static final int CTB_CACHE_MAP = 1;

   CharsetDecoder getByteToCharConverter(Object var1) {
      Map var2 = ((Map[])((Map[])this.converterCaches.get()))[0];
      return (CharsetDecoder)var2.get(var1);
   }

   CharsetEncoder getCharToByteConverter(Object var1) {
      Map var2 = ((Map[])((Map[])this.converterCaches.get()))[1];
      return (CharsetEncoder)var2.get(var1);
   }

   CharsetDecoder setConverter(Object var1, CharsetDecoder var2) {
      Map var3 = ((Map[])((Map[])this.converterCaches.get()))[0];
      var3.put(var1, var2);
      return var2;
   }

   CharsetEncoder setConverter(Object var1, CharsetEncoder var2) {
      Map var3 = ((Map[])((Map[])this.converterCaches.get()))[1];
      var3.put(var1, var2);
      return var2;
   }
}
