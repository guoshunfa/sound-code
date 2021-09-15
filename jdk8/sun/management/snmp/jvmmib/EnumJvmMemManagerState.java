package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmMemManagerState extends Enumerated implements Serializable {
   static final long serialVersionUID = 8249515157795166343L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmMemManagerState(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemManagerState(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmMemManagerState() throws IllegalArgumentException {
   }

   public EnumJvmMemManagerState(String var1) throws IllegalArgumentException {
      super(var1);
   }

   protected Hashtable<Integer, String> getIntTable() {
      return intTable;
   }

   protected Hashtable<String, Integer> getStringTable() {
      return stringTable;
   }

   static {
      intTable.put(new Integer(2), "valid");
      intTable.put(new Integer(1), "invalid");
      stringTable.put("valid", new Integer(2));
      stringTable.put("invalid", new Integer(1));
   }
}
