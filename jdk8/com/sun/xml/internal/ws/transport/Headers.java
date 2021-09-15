package com.sun.xml.internal.ws.transport;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Headers extends TreeMap<String, List<String>> {
   private static final Headers.InsensitiveComparator INSTANCE = new Headers.InsensitiveComparator();

   public Headers() {
      super((Comparator)INSTANCE);
   }

   public void add(String key, String value) {
      List<String> list = (List)this.get(key);
      if (list == null) {
         list = new LinkedList();
         this.put(key, list);
      }

      ((List)list).add(value);
   }

   public String getFirst(String key) {
      List<String> l = (List)this.get(key);
      return l == null ? null : (String)l.get(0);
   }

   public void set(String key, String value) {
      LinkedList<String> l = new LinkedList();
      l.add(value);
      this.put(key, l);
   }

   private static final class InsensitiveComparator implements Comparator<String>, Serializable {
      private InsensitiveComparator() {
      }

      public int compare(String o1, String o2) {
         if (o1 == null && o2 == null) {
            return 0;
         } else if (o1 == null) {
            return -1;
         } else {
            return o2 == null ? 1 : o1.compareToIgnoreCase(o2);
         }
      }

      // $FF: synthetic method
      InsensitiveComparator(Object x0) {
         this();
      }
   }
}
