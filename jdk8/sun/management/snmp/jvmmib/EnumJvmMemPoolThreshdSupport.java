package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemPoolThreshdSupport extends Enumerated implements Serializable {
   static final long serialVersionUID = 7014693561120661029L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmMemPoolThreshdSupport(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemPoolThreshdSupport(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemPoolThreshdSupport() throws IllegalArgumentException {
   }

   public EnumJvmMemPoolThreshdSupport(String var1) throws IllegalArgumentException {
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
