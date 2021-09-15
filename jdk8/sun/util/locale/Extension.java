package sun.util.locale;

class Extension {
   private final char key;
   private String value;
   private String id;

   protected Extension(char var1) {
      this.key = var1;
   }

   Extension(char var1, String var2) {
      this.key = var1;
      this.setValue(var2);
   }

   protected void setValue(String var1) {
      this.value = var1;
      this.id = this.key + "-" + var1;
   }

   public char getKey() {
      return this.key;
   }

   public String getValue() {
      return this.value;
   }

   public String getID() {
      return this.id;
   }

   public String toString() {
      return this.getID();
   }
}
