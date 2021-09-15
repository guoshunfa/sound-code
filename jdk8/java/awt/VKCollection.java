package java.awt;

import java.util.HashMap;
import java.util.Map;

class VKCollection {
   Map<Integer, String> code2name = new HashMap();
   Map<String, Integer> name2code = new HashMap();

   public VKCollection() {
   }

   public synchronized void put(String var1, Integer var2) {
      assert var1 != null && var2 != null;

      assert this.findName(var2) == null;

      assert this.findCode(var1) == null;

      this.code2name.put(var2, var1);
      this.name2code.put(var1, var2);
   }

   public synchronized Integer findCode(String var1) {
      assert var1 != null;

      return (Integer)this.name2code.get(var1);
   }

   public synchronized String findName(Integer var1) {
      assert var1 != null;

      return (String)this.code2name.get(var1);
   }
}
