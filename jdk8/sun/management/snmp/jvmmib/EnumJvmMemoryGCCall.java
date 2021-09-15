package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemoryGCCall extends Enumerated implements Serializable {
   static final long serialVersionUID = -2869147994287351375L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmMemoryGCCall(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemoryGCCall(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemoryGCCall() throws IllegalArgumentException {
   }

   public EnumJvmMemoryGCCall(String var1) throws IllegalArgumentException {
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
      intTable.put(new Integer(5), "failed");
      intTable.put(new Integer(4), "started");
      intTable.put(new Integer(1), "unsupported");
      intTable.put(new Integer(3), "start");
      stringTable.put("supported", new Integer(2));
      stringTable.put("failed", new Integer(5));
      stringTable.put("started", new Integer(4));
      stringTable.put("unsupported", new Integer(1));
      stringTable.put("start", new Integer(3));
   }
}
