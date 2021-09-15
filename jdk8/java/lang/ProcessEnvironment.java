package java.lang;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class ProcessEnvironment {
   private static final HashMap<ProcessEnvironment.Variable, ProcessEnvironment.Value> theEnvironment;
   private static final Map<String, String> theUnmodifiableEnvironment;
   static final int MIN_NAME_LENGTH = 0;

   static String getenv(String var0) {
      return (String)theUnmodifiableEnvironment.get(var0);
   }

   static Map<String, String> getenv() {
      return theUnmodifiableEnvironment;
   }

   static Map<String, String> environment() {
      return new ProcessEnvironment.StringEnvironment((Map)((Map)theEnvironment.clone()));
   }

   static Map<String, String> emptyEnvironment(int var0) {
      return new ProcessEnvironment.StringEnvironment(new HashMap(var0));
   }

   private static native byte[][] environ();

   private ProcessEnvironment() {
   }

   private static void validateVariable(String var0) {
      if (var0.indexOf(61) != -1 || var0.indexOf(0) != -1) {
         throw new IllegalArgumentException("Invalid environment variable name: \"" + var0 + "\"");
      }
   }

   private static void validateValue(String var0) {
      if (var0.indexOf(0) != -1) {
         throw new IllegalArgumentException("Invalid environment variable value: \"" + var0 + "\"");
      }
   }

   static byte[] toEnvironmentBlock(Map<String, String> var0, int[] var1) {
      return var0 == null ? null : ((ProcessEnvironment.StringEnvironment)var0).toEnvironmentBlock(var1);
   }

   private static int arrayCompare(byte[] var0, byte[] var1) {
      int var2 = var0.length < var1.length ? var0.length : var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var0[var3] != var1[var3]) {
            return var0[var3] - var1[var3];
         }
      }

      return var0.length - var1.length;
   }

   private static boolean arrayEquals(byte[] var0, byte[] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var0[var2] != var1[var2]) {
               return false;
            }
         }

         return true;
      }
   }

   private static int arrayHash(byte[] var0) {
      int var1 = 0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1 = 31 * var1 + var0[var2];
      }

      return var1;
   }

   static {
      byte[][] var0 = environ();
      theEnvironment = new HashMap(var0.length / 2 + 3);

      for(int var1 = var0.length - 1; var1 > 0; var1 -= 2) {
         theEnvironment.put(ProcessEnvironment.Variable.valueOf(var0[var1 - 1]), ProcessEnvironment.Value.valueOf(var0[var1]));
      }

      theUnmodifiableEnvironment = Collections.unmodifiableMap(new ProcessEnvironment.StringEnvironment(theEnvironment));
   }

   private static class StringKeySet extends AbstractSet<String> {
      private final Set<ProcessEnvironment.Variable> s;

      public StringKeySet(Set<ProcessEnvironment.Variable> var1) {
         this.s = var1;
      }

      public int size() {
         return this.s.size();
      }

      public boolean isEmpty() {
         return this.s.isEmpty();
      }

      public void clear() {
         this.s.clear();
      }

      public Iterator<String> iterator() {
         return new Iterator<String>() {
            Iterator<ProcessEnvironment.Variable> i;

            {
               this.i = StringKeySet.this.s.iterator();
            }

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public String next() {
               return ((ProcessEnvironment.Variable)this.i.next()).toString();
            }

            public void remove() {
               this.i.remove();
            }
         };
      }

      public boolean contains(Object var1) {
         return this.s.contains(ProcessEnvironment.Variable.valueOfQueryOnly(var1));
      }

      public boolean remove(Object var1) {
         return this.s.remove(ProcessEnvironment.Variable.valueOfQueryOnly(var1));
      }
   }

   private static class StringValues extends AbstractCollection<String> {
      private final Collection<ProcessEnvironment.Value> c;

      public StringValues(Collection<ProcessEnvironment.Value> var1) {
         this.c = var1;
      }

      public int size() {
         return this.c.size();
      }

      public boolean isEmpty() {
         return this.c.isEmpty();
      }

      public void clear() {
         this.c.clear();
      }

      public Iterator<String> iterator() {
         return new Iterator<String>() {
            Iterator<ProcessEnvironment.Value> i;

            {
               this.i = StringValues.this.c.iterator();
            }

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public String next() {
               return ((ProcessEnvironment.Value)this.i.next()).toString();
            }

            public void remove() {
               this.i.remove();
            }
         };
      }

      public boolean contains(Object var1) {
         return this.c.contains(ProcessEnvironment.Value.valueOfQueryOnly(var1));
      }

      public boolean remove(Object var1) {
         return this.c.remove(ProcessEnvironment.Value.valueOfQueryOnly(var1));
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.StringValues && this.c.equals(((ProcessEnvironment.StringValues)var1).c);
      }

      public int hashCode() {
         return this.c.hashCode();
      }
   }

   private static class StringEntrySet extends AbstractSet<Map.Entry<String, String>> {
      private final Set<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> s;

      public StringEntrySet(Set<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> var1) {
         this.s = var1;
      }

      public int size() {
         return this.s.size();
      }

      public boolean isEmpty() {
         return this.s.isEmpty();
      }

      public void clear() {
         this.s.clear();
      }

      public Iterator<Map.Entry<String, String>> iterator() {
         return new Iterator<Map.Entry<String, String>>() {
            Iterator<Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>> i;

            {
               this.i = StringEntrySet.this.s.iterator();
            }

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public Map.Entry<String, String> next() {
               return new ProcessEnvironment.StringEntry((Map.Entry)this.i.next());
            }

            public void remove() {
               this.i.remove();
            }
         };
      }

      private static Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> vvEntry(final Object var0) {
         return var0 instanceof ProcessEnvironment.StringEntry ? ((ProcessEnvironment.StringEntry)var0).e : new Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value>() {
            public ProcessEnvironment.Variable getKey() {
               return ProcessEnvironment.Variable.valueOfQueryOnly(((Map.Entry)var0).getKey());
            }

            public ProcessEnvironment.Value getValue() {
               return ProcessEnvironment.Value.valueOfQueryOnly(((Map.Entry)var0).getValue());
            }

            public ProcessEnvironment.Value setValue(ProcessEnvironment.Value var1) {
               throw new UnsupportedOperationException();
            }
         };
      }

      public boolean contains(Object var1) {
         return this.s.contains(vvEntry(var1));
      }

      public boolean remove(Object var1) {
         return this.s.remove(vvEntry(var1));
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.StringEntrySet && this.s.equals(((ProcessEnvironment.StringEntrySet)var1).s);
      }

      public int hashCode() {
         return this.s.hashCode();
      }
   }

   private static class StringEntry implements Map.Entry<String, String> {
      private final Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> e;

      public StringEntry(Map.Entry<ProcessEnvironment.Variable, ProcessEnvironment.Value> var1) {
         this.e = var1;
      }

      public String getKey() {
         return ((ProcessEnvironment.Variable)this.e.getKey()).toString();
      }

      public String getValue() {
         return ((ProcessEnvironment.Value)this.e.getValue()).toString();
      }

      public String setValue(String var1) {
         return ((ProcessEnvironment.Value)this.e.setValue(ProcessEnvironment.Value.valueOf(var1))).toString();
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.StringEntry && this.e.equals(((ProcessEnvironment.StringEntry)var1).e);
      }

      public int hashCode() {
         return this.e.hashCode();
      }
   }

   private static class StringEnvironment extends AbstractMap<String, String> {
      private Map<ProcessEnvironment.Variable, ProcessEnvironment.Value> m;

      private static String toString(ProcessEnvironment.Value var0) {
         return var0 == null ? null : var0.toString();
      }

      public StringEnvironment(Map<ProcessEnvironment.Variable, ProcessEnvironment.Value> var1) {
         this.m = var1;
      }

      public int size() {
         return this.m.size();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public void clear() {
         this.m.clear();
      }

      public boolean containsKey(Object var1) {
         return this.m.containsKey(ProcessEnvironment.Variable.valueOfQueryOnly(var1));
      }

      public boolean containsValue(Object var1) {
         return this.m.containsValue(ProcessEnvironment.Value.valueOfQueryOnly(var1));
      }

      public String get(Object var1) {
         return toString((ProcessEnvironment.Value)this.m.get(ProcessEnvironment.Variable.valueOfQueryOnly(var1)));
      }

      public String put(String var1, String var2) {
         return toString((ProcessEnvironment.Value)this.m.put(ProcessEnvironment.Variable.valueOf(var1), ProcessEnvironment.Value.valueOf(var2)));
      }

      public String remove(Object var1) {
         return toString((ProcessEnvironment.Value)this.m.remove(ProcessEnvironment.Variable.valueOfQueryOnly(var1)));
      }

      public Set<String> keySet() {
         return new ProcessEnvironment.StringKeySet(this.m.keySet());
      }

      public Set<Map.Entry<String, String>> entrySet() {
         return new ProcessEnvironment.StringEntrySet(this.m.entrySet());
      }

      public Collection<String> values() {
         return new ProcessEnvironment.StringValues(this.m.values());
      }

      public byte[] toEnvironmentBlock(int[] var1) {
         int var2 = this.m.size() * 2;

         Map.Entry var4;
         for(Iterator var3 = this.m.entrySet().iterator(); var3.hasNext(); var2 += ((ProcessEnvironment.Value)var4.getValue()).getBytes().length) {
            var4 = (Map.Entry)var3.next();
            var2 += ((ProcessEnvironment.Variable)var4.getKey()).getBytes().length;
         }

         byte[] var9 = new byte[var2];
         int var10 = 0;

         byte[] var8;
         for(Iterator var5 = this.m.entrySet().iterator(); var5.hasNext(); var10 += var8.length + 1) {
            Map.Entry var6 = (Map.Entry)var5.next();
            byte[] var7 = ((ProcessEnvironment.Variable)var6.getKey()).getBytes();
            var8 = ((ProcessEnvironment.Value)var6.getValue()).getBytes();
            System.arraycopy(var7, 0, var9, var10, var7.length);
            var10 += var7.length;
            var9[var10++] = 61;
            System.arraycopy(var8, 0, var9, var10, var8.length);
         }

         var1[0] = this.m.size();
         return var9;
      }
   }

   private static class Value extends ProcessEnvironment.ExternalData implements Comparable<ProcessEnvironment.Value> {
      protected Value(String var1, byte[] var2) {
         super(var1, var2);
      }

      public static ProcessEnvironment.Value valueOfQueryOnly(Object var0) {
         return valueOfQueryOnly((String)var0);
      }

      public static ProcessEnvironment.Value valueOfQueryOnly(String var0) {
         return new ProcessEnvironment.Value(var0, var0.getBytes());
      }

      public static ProcessEnvironment.Value valueOf(String var0) {
         ProcessEnvironment.validateValue(var0);
         return valueOfQueryOnly(var0);
      }

      public static ProcessEnvironment.Value valueOf(byte[] var0) {
         return new ProcessEnvironment.Value(new String(var0), var0);
      }

      public int compareTo(ProcessEnvironment.Value var1) {
         return ProcessEnvironment.arrayCompare(this.getBytes(), var1.getBytes());
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.Value && super.equals(var1);
      }
   }

   private static class Variable extends ProcessEnvironment.ExternalData implements Comparable<ProcessEnvironment.Variable> {
      protected Variable(String var1, byte[] var2) {
         super(var1, var2);
      }

      public static ProcessEnvironment.Variable valueOfQueryOnly(Object var0) {
         return valueOfQueryOnly((String)var0);
      }

      public static ProcessEnvironment.Variable valueOfQueryOnly(String var0) {
         return new ProcessEnvironment.Variable(var0, var0.getBytes());
      }

      public static ProcessEnvironment.Variable valueOf(String var0) {
         ProcessEnvironment.validateVariable(var0);
         return valueOfQueryOnly(var0);
      }

      public static ProcessEnvironment.Variable valueOf(byte[] var0) {
         return new ProcessEnvironment.Variable(new String(var0), var0);
      }

      public int compareTo(ProcessEnvironment.Variable var1) {
         return ProcessEnvironment.arrayCompare(this.getBytes(), var1.getBytes());
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.Variable && super.equals(var1);
      }
   }

   private abstract static class ExternalData {
      protected final String str;
      protected final byte[] bytes;

      protected ExternalData(String var1, byte[] var2) {
         this.str = var1;
         this.bytes = var2;
      }

      public byte[] getBytes() {
         return this.bytes;
      }

      public String toString() {
         return this.str;
      }

      public boolean equals(Object var1) {
         return var1 instanceof ProcessEnvironment.ExternalData && ProcessEnvironment.arrayEquals(this.getBytes(), ((ProcessEnvironment.ExternalData)var1).getBytes());
      }

      public int hashCode() {
         return ProcessEnvironment.arrayHash(this.getBytes());
      }
   }
}
