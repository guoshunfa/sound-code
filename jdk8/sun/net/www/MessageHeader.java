package sun.net.www;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

public class MessageHeader {
   private String[] keys;
   private String[] values;
   private int nkeys;

   public MessageHeader() {
      this.grow();
   }

   public MessageHeader(InputStream var1) throws IOException {
      this.parseHeader(var1);
   }

   public synchronized String getHeaderNamesInList() {
      StringJoiner var1 = new StringJoiner(",");

      for(int var2 = 0; var2 < this.nkeys; ++var2) {
         var1.add(this.keys[var2]);
      }

      return var1.toString();
   }

   public synchronized void reset() {
      this.keys = null;
      this.values = null;
      this.nkeys = 0;
      this.grow();
   }

   public synchronized String findValue(String var1) {
      int var2;
      if (var1 == null) {
         var2 = this.nkeys;

         while(true) {
            --var2;
            if (var2 < 0) {
               break;
            }

            if (this.keys[var2] == null) {
               return this.values[var2];
            }
         }
      } else {
         var2 = this.nkeys;

         while(true) {
            --var2;
            if (var2 < 0) {
               break;
            }

            if (var1.equalsIgnoreCase(this.keys[var2])) {
               return this.values[var2];
            }
         }
      }

      return null;
   }

   public synchronized int getKey(String var1) {
      int var2 = this.nkeys;

      do {
         --var2;
         if (var2 < 0) {
            return -1;
         }
      } while(this.keys[var2] != var1 && (var1 == null || !var1.equalsIgnoreCase(this.keys[var2])));

      return var2;
   }

   public synchronized String getKey(int var1) {
      return var1 >= 0 && var1 < this.nkeys ? this.keys[var1] : null;
   }

   public synchronized String getValue(int var1) {
      return var1 >= 0 && var1 < this.nkeys ? this.values[var1] : null;
   }

   public synchronized String findNextValue(String var1, String var2) {
      boolean var3 = false;
      int var4;
      if (var1 == null) {
         var4 = this.nkeys;

         while(true) {
            --var4;
            if (var4 < 0) {
               break;
            }

            if (this.keys[var4] == null) {
               if (var3) {
                  return this.values[var4];
               }

               if (this.values[var4] == var2) {
                  var3 = true;
               }
            }
         }
      } else {
         var4 = this.nkeys;

         while(true) {
            --var4;
            if (var4 < 0) {
               break;
            }

            if (var1.equalsIgnoreCase(this.keys[var4])) {
               if (var3) {
                  return this.values[var4];
               }

               if (this.values[var4] == var2) {
                  var3 = true;
               }
            }
         }
      }

      return null;
   }

   public boolean filterNTLMResponses(String var1) {
      boolean var2 = false;

      int var3;
      for(var3 = 0; var3 < this.nkeys; ++var3) {
         if (var1.equalsIgnoreCase(this.keys[var3]) && this.values[var3] != null && this.values[var3].length() > 5 && this.values[var3].substring(0, 5).equalsIgnoreCase("NTLM ")) {
            var2 = true;
            break;
         }
      }

      if (var2) {
         var3 = 0;

         for(int var4 = 0; var4 < this.nkeys; ++var4) {
            if (!var1.equalsIgnoreCase(this.keys[var4]) || !"Negotiate".equalsIgnoreCase(this.values[var4]) && !"Kerberos".equalsIgnoreCase(this.values[var4])) {
               if (var4 != var3) {
                  this.keys[var3] = this.keys[var4];
                  this.values[var3] = this.values[var4];
               }

               ++var3;
            }
         }

         if (var3 != this.nkeys) {
            this.nkeys = var3;
            return true;
         }
      }

      return false;
   }

   public Iterator<String> multiValueIterator(String var1) {
      return new MessageHeader.HeaderIterator(var1, this);
   }

   public synchronized Map<String, List<String>> getHeaders() {
      return this.getHeaders((String[])null);
   }

   public synchronized Map<String, List<String>> getHeaders(String[] var1) {
      return this.filterAndAddHeaders(var1, (Map)null);
   }

   public synchronized Map<String, List<String>> filterAndAddHeaders(String[] var1, Map<String, List<String>> var2) {
      boolean var3 = false;
      HashMap var4 = new HashMap();
      int var5 = this.nkeys;

      while(true) {
         --var5;
         if (var5 < 0) {
            Object var7;
            Iterator var8;
            Map.Entry var10;
            if (var2 != null) {
               for(var8 = var2.entrySet().iterator(); var8.hasNext(); ((List)var7).addAll((Collection)var10.getValue())) {
                  var10 = (Map.Entry)var8.next();
                  var7 = (List)var4.get(var10.getKey());
                  if (var7 == null) {
                     var7 = new ArrayList();
                     var4.put(var10.getKey(), var7);
                  }
               }
            }

            var8 = var4.keySet().iterator();

            while(var8.hasNext()) {
               String var11 = (String)var8.next();
               var4.put(var11, Collections.unmodifiableList((List)var4.get(var11)));
            }

            return Collections.unmodifiableMap(var4);
         }

         if (var1 != null) {
            for(int var6 = 0; var6 < var1.length; ++var6) {
               if (var1[var6] != null && var1[var6].equalsIgnoreCase(this.keys[var5])) {
                  var3 = true;
                  break;
               }
            }
         }

         if (!var3) {
            Object var9 = (List)var4.get(this.keys[var5]);
            if (var9 == null) {
               var9 = new ArrayList();
               var4.put(this.keys[var5], var9);
            }

            ((List)var9).add(this.values[var5]);
         } else {
            var3 = false;
         }
      }
   }

   public synchronized void print(PrintStream var1) {
      for(int var2 = 0; var2 < this.nkeys; ++var2) {
         if (this.keys[var2] != null) {
            var1.print(this.keys[var2] + (this.values[var2] != null ? ": " + this.values[var2] : "") + "\r\n");
         }
      }

      var1.print("\r\n");
      var1.flush();
   }

   public synchronized void add(String var1, String var2) {
      this.grow();
      this.keys[this.nkeys] = var1;
      this.values[this.nkeys] = var2;
      ++this.nkeys;
   }

   public synchronized void prepend(String var1, String var2) {
      this.grow();

      for(int var3 = this.nkeys; var3 > 0; --var3) {
         this.keys[var3] = this.keys[var3 - 1];
         this.values[var3] = this.values[var3 - 1];
      }

      this.keys[0] = var1;
      this.values[0] = var2;
      ++this.nkeys;
   }

   public synchronized void set(int var1, String var2, String var3) {
      this.grow();
      if (var1 >= 0) {
         if (var1 >= this.nkeys) {
            this.add(var2, var3);
         } else {
            this.keys[var1] = var2;
            this.values[var1] = var3;
         }

      }
   }

   private void grow() {
      if (this.keys == null || this.nkeys >= this.keys.length) {
         String[] var1 = new String[this.nkeys + 4];
         String[] var2 = new String[this.nkeys + 4];
         if (this.keys != null) {
            System.arraycopy(this.keys, 0, var1, 0, this.nkeys);
         }

         if (this.values != null) {
            System.arraycopy(this.values, 0, var2, 0, this.nkeys);
         }

         this.keys = var1;
         this.values = var2;
      }

   }

   public synchronized void remove(String var1) {
      int var2;
      int var3;
      if (var1 == null) {
         for(var2 = 0; var2 < this.nkeys; ++var2) {
            while(this.keys[var2] == null && var2 < this.nkeys) {
               for(var3 = var2; var3 < this.nkeys - 1; ++var3) {
                  this.keys[var3] = this.keys[var3 + 1];
                  this.values[var3] = this.values[var3 + 1];
               }

               --this.nkeys;
            }
         }
      } else {
         for(var2 = 0; var2 < this.nkeys; ++var2) {
            while(var1.equalsIgnoreCase(this.keys[var2]) && var2 < this.nkeys) {
               for(var3 = var2; var3 < this.nkeys - 1; ++var3) {
                  this.keys[var3] = this.keys[var3 + 1];
                  this.values[var3] = this.values[var3 + 1];
               }

               --this.nkeys;
            }
         }
      }

   }

   public synchronized void set(String var1, String var2) {
      int var3 = this.nkeys;

      do {
         --var3;
         if (var3 < 0) {
            this.add(var1, var2);
            return;
         }
      } while(!var1.equalsIgnoreCase(this.keys[var3]));

      this.values[var3] = var2;
   }

   public synchronized void setIfNotSet(String var1, String var2) {
      if (this.findValue(var1) == null) {
         this.add(var1, var2);
      }

   }

   public static String canonicalID(String var0) {
      if (var0 == null) {
         return "";
      } else {
         int var1 = 0;
         int var2 = var0.length();

         boolean var3;
         char var4;
         for(var3 = false; var1 < var2 && ((var4 = var0.charAt(var1)) == '<' || var4 <= ' '); var3 = true) {
            ++var1;
         }

         while(var1 < var2 && ((var4 = var0.charAt(var2 - 1)) == '>' || var4 <= ' ')) {
            --var2;
            var3 = true;
         }

         return var3 ? var0.substring(var1, var2) : var0;
      }
   }

   public void parseHeader(InputStream var1) throws IOException {
      synchronized(this) {
         this.nkeys = 0;
      }

      this.mergeHeader(var1);
   }

   public void mergeHeader(InputStream var1) throws IOException {
      if (var1 != null) {
         char[] var2 = new char[10];

         String var9;
         String var11;
         for(int var3 = var1.read(); var3 != 10 && var3 != 13 && var3 >= 0; this.add(var11, var9)) {
            byte var4 = 0;
            int var5 = -1;
            boolean var7 = var3 > 32;
            int var10 = var4 + 1;
            var2[var4] = (char)var3;

            label104:
            while(true) {
               int var6;
               if ((var6 = var1.read()) < 0) {
                  var3 = -1;
                  break;
               }

               switch(var6) {
               case 9:
                  var6 = 32;
               case 32:
                  var7 = false;
                  break;
               case 10:
               case 13:
                  var3 = var1.read();
                  if (var6 == 13 && var3 == 10) {
                     var3 = var1.read();
                     if (var3 == 13) {
                        var3 = var1.read();
                     }
                  }

                  if (var3 == 10 || var3 == 13 || var3 > 32) {
                     break label104;
                  }

                  var6 = 32;
                  break;
               case 58:
                  if (var7 && var10 > 0) {
                     var5 = var10;
                  }

                  var7 = false;
               }

               if (var10 >= var2.length) {
                  char[] var8 = new char[var2.length * 2];
                  System.arraycopy(var2, 0, var8, 0, var10);
                  var2 = var8;
               }

               var2[var10++] = (char)var6;
            }

            while(var10 > 0 && var2[var10 - 1] <= ' ') {
               --var10;
            }

            if (var5 <= 0) {
               var11 = null;
               var5 = 0;
            } else {
               var11 = String.copyValueOf(var2, 0, var5);
               if (var5 < var10 && var2[var5] == ':') {
                  ++var5;
               }

               while(var5 < var10 && var2[var5] <= ' ') {
                  ++var5;
               }
            }

            if (var5 >= var10) {
               var9 = new String();
            } else {
               var9 = String.copyValueOf(var2, var5, var10 - var5);
            }
         }

      }
   }

   public synchronized String toString() {
      String var1 = super.toString() + this.nkeys + " pairs: ";

      for(int var2 = 0; var2 < this.keys.length && var2 < this.nkeys; ++var2) {
         var1 = var1 + "{" + this.keys[var2] + ": " + this.values[var2] + "}";
      }

      return var1;
   }

   class HeaderIterator implements Iterator<String> {
      int index = 0;
      int next = -1;
      String key;
      boolean haveNext = false;
      Object lock;

      public HeaderIterator(String var2, Object var3) {
         this.key = var2;
         this.lock = var3;
      }

      public boolean hasNext() {
         synchronized(this.lock) {
            if (this.haveNext) {
               return true;
            } else {
               while(this.index < MessageHeader.this.nkeys) {
                  if (this.key.equalsIgnoreCase(MessageHeader.this.keys[this.index])) {
                     this.haveNext = true;
                     this.next = this.index++;
                     return true;
                  }

                  ++this.index;
               }

               return false;
            }
         }
      }

      public String next() {
         synchronized(this.lock) {
            if (this.haveNext) {
               this.haveNext = false;
               return MessageHeader.this.values[this.next];
            } else if (this.hasNext()) {
               return this.next();
            } else {
               throw new NoSuchElementException("No more elements");
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("remove not allowed");
      }
   }
}
