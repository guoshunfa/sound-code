package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

class UnmodifiableHeaders extends Headers {
   Headers map;

   UnmodifiableHeaders(Headers var1) {
      this.map = var1;
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.map.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   public List<String> get(Object var1) {
      return this.map.get(var1);
   }

   public String getFirst(String var1) {
      return this.map.getFirst(var1);
   }

   public List<String> put(String var1, List<String> var2) {
      return this.map.put(var1, var2);
   }

   public void add(String var1, String var2) {
      throw new UnsupportedOperationException("unsupported operation");
   }

   public void set(String var1, String var2) {
      throw new UnsupportedOperationException("unsupported operation");
   }

   public List<String> remove(Object var1) {
      throw new UnsupportedOperationException("unsupported operation");
   }

   public void putAll(Map<? extends String, ? extends List<String>> var1) {
      throw new UnsupportedOperationException("unsupported operation");
   }

   public void clear() {
      throw new UnsupportedOperationException("unsupported operation");
   }

   public Set<String> keySet() {
      return Collections.unmodifiableSet(this.map.keySet());
   }

   public Collection<List<String>> values() {
      return Collections.unmodifiableCollection(this.map.values());
   }

   public Set<Map.Entry<String, List<String>>> entrySet() {
      return Collections.unmodifiableSet(this.map.entrySet());
   }

   public boolean equals(Object var1) {
      return this.map.equals(var1);
   }

   public int hashCode() {
      return this.map.hashCode();
   }
}
