package java.sql;

class DriverInfo {
   final Driver driver;
   DriverAction da;

   DriverInfo(Driver var1, DriverAction var2) {
      this.driver = var1;
      this.da = var2;
   }

   public boolean equals(Object var1) {
      return var1 instanceof DriverInfo && this.driver == ((DriverInfo)var1).driver;
   }

   public int hashCode() {
      return this.driver.hashCode();
   }

   public String toString() {
      return "driver[className=" + this.driver + "]";
   }

   DriverAction action() {
      return this.da;
   }
}
