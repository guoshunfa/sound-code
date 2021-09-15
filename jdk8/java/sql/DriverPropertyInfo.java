package java.sql;

public class DriverPropertyInfo {
   public String name;
   public String description = null;
   public boolean required = false;
   public String value = null;
   public String[] choices = null;

   public DriverPropertyInfo(String var1, String var2) {
      this.name = var1;
      this.value = var2;
   }
}
