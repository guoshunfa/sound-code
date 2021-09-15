package sun.management;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class StackTraceElementCompositeData extends LazyCompositeData {
   private final StackTraceElement ste;
   private static final CompositeType stackTraceElementCompositeType;
   private static final String CLASS_NAME = "className";
   private static final String METHOD_NAME = "methodName";
   private static final String FILE_NAME = "fileName";
   private static final String LINE_NUMBER = "lineNumber";
   private static final String NATIVE_METHOD = "nativeMethod";
   private static final String[] stackTraceElementItemNames;
   private static final long serialVersionUID = -2704607706598396827L;

   private StackTraceElementCompositeData(StackTraceElement var1) {
      this.ste = var1;
   }

   public StackTraceElement getStackTraceElement() {
      return this.ste;
   }

   public static StackTraceElement from(CompositeData var0) {
      validateCompositeData(var0);
      return new StackTraceElement(getString(var0, "className"), getString(var0, "methodName"), getString(var0, "fileName"), getInt(var0, "lineNumber"));
   }

   public static CompositeData toCompositeData(StackTraceElement var0) {
      StackTraceElementCompositeData var1 = new StackTraceElementCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{this.ste.getClassName(), this.ste.getMethodName(), this.ste.getFileName(), new Integer(this.ste.getLineNumber()), new Boolean(this.ste.isNativeMethod())};

      try {
         return new CompositeDataSupport(stackTraceElementCompositeType, stackTraceElementItemNames, var1);
      } catch (OpenDataException var3) {
         throw new AssertionError(var3);
      }
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(stackTraceElementCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for StackTraceElement");
      }
   }

   static {
      try {
         stackTraceElementCompositeType = (CompositeType)MappedMXBeanType.toOpenType(StackTraceElement.class);
      } catch (OpenDataException var1) {
         throw new AssertionError(var1);
      }

      stackTraceElementItemNames = new String[]{"className", "methodName", "fileName", "lineNumber", "nativeMethod"};
   }
}
