package sun.management;

import com.sun.management.VMOption;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class VMOptionCompositeData extends LazyCompositeData {
   private final VMOption option;
   private static final CompositeType vmOptionCompositeType;
   private static final String NAME = "name";
   private static final String VALUE = "value";
   private static final String WRITEABLE = "writeable";
   private static final String ORIGIN = "origin";
   private static final String[] vmOptionItemNames;
   private static final long serialVersionUID = -2395573975093578470L;

   private VMOptionCompositeData(VMOption var1) {
      this.option = var1;
   }

   public VMOption getVMOption() {
      return this.option;
   }

   public static CompositeData toCompositeData(VMOption var0) {
      VMOptionCompositeData var1 = new VMOptionCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{this.option.getName(), this.option.getValue(), new Boolean(this.option.isWriteable()), this.option.getOrigin().toString()};

      try {
         return new CompositeDataSupport(vmOptionCompositeType, vmOptionItemNames, var1);
      } catch (OpenDataException var3) {
         throw new AssertionError(var3);
      }
   }

   static CompositeType getVMOptionCompositeType() {
      return vmOptionCompositeType;
   }

   public static String getName(CompositeData var0) {
      return getString(var0, "name");
   }

   public static String getValue(CompositeData var0) {
      return getString(var0, "value");
   }

   public static VMOption.Origin getOrigin(CompositeData var0) {
      String var1 = getString(var0, "origin");
      return (VMOption.Origin)Enum.valueOf(VMOption.Origin.class, var1);
   }

   public static boolean isWriteable(CompositeData var0) {
      return getBoolean(var0, "writeable");
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(vmOptionCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for VMOption");
      }
   }

   static {
      try {
         vmOptionCompositeType = (CompositeType)MappedMXBeanType.toOpenType(VMOption.class);
      } catch (OpenDataException var1) {
         throw new AssertionError(var1);
      }

      vmOptionItemNames = new String[]{"name", "value", "writeable", "origin"};
   }
}
