package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemPoolCollectThreshdSupport extends Enumerated implements Serializable {
   static final long serialVersionUID = 8610091819732806282L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmMemPoolCollectThreshdSupport(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemPoolCollectThreshdSupport(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemPoolCollectThreshdSupport() throws IllegalArgumentException {
   }

   public EnumJvmMemPoolCollectThreshdSupport(String var1) throws IllegalArgumentException {
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
