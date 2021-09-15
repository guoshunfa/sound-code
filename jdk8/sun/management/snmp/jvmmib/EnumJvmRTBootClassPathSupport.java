package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmRTBootClassPathSupport extends Enumerated implements Serializable {
   static final long serialVersionUID = -5957542680437939894L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmRTBootClassPathSupport(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmRTBootClassPathSupport(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmRTBootClassPathSupport() throws IllegalArgumentException {
   }

   public EnumJvmRTBootClassPathSupport(String var1) throws IllegalArgumentException {
      super(var1);
   }

   protected Hashtable<Integer, String> getIntTable() {
      return intTable;
   }

   protected Hashtable<String, Integer> getStringTable() {
      return stringTable;
   }

   static {
      intTable.put(new Integer(2), "supported");
      intTable.put(new Integer(1), "unsupported");
      stringTable.put("supported", new Integer(2));
      stringTable.put("unsupported", new Integer(1));
   }
}
