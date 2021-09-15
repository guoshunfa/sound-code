package sun.util.resources;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class TimeZoneNamesBundle extends OpenListResourceBundle {
   public Object handleGetObject(String var1) {
      String[] var2 = (String[])((String[])super.handleGetObject(var1));
      if (Objects.isNull(var2)) {
         return null;
      } else {
         int var3 = var2.length;
         String[] var4 = new String[7];
         var4[0] = var1;
         System.arraycopy(var2, 0, var4, 1, var3);
         return var4;
      }
   }

   protected <K, V> Map<K, V> createMap(int var1) {
      return new LinkedHashMap(var1);
   }

   protected <E> Set<E> createSet() {
      return new LinkedHashSet();
   }

   protected abstract Object[][] getContents();
}
