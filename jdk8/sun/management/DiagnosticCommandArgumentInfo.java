package sun.management;

class DiagnosticCommandArgumentInfo {
   private final String name;
   private final String description;
   private final String type;
   private final String defaultValue;
   private final boolean mandatory;
   private final boolean option;
   private final boolean multiple;
   private final int position;

   String getName() {
      return this.name;
   }

   String getDescription() {
      return this.description;
   }

   String getType() {
      return this.type;
   }

   String getDefault() {
      return this.defaultValue;
   }

   boolean isMandatory() {
      return this.mandatory;
   }

   boolean isOption() {
      return this.option;
   }

   boolean isMultiple() {
      return this.multiple;
   }

   int getPosition() {
      return this.position;
   }

   DiagnosticCommandArgumentInfo(String var1, String var2, String var3, String var4, boolean var5, boolean var6, boolean var7, int var8) {
      this.name = var1;
      this.description = var2;
      this.type = var3;
      this.defaultValue = var4;
      this.mandatory = var5;
      this.option = var6;
      this.multiple = var7;
      this.position = var8;
   }
}
