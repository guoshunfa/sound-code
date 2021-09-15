package javax.naming.ldap;

public class BasicControl implements Control {
   protected String id;
   protected boolean criticality = false;
   protected byte[] value = null;
   private static final long serialVersionUID = -4233907508771791687L;

   public BasicControl(String var1) {
      this.id = var1;
   }

   public BasicControl(String var1, boolean var2, byte[] var3) {
      this.id = var1;
      this.criticality = var2;
      this.value = var3;
   }

   public String getID() {
      return this.id;
   }

   public boolean isCritical() {
      return this.criticality;
   }

   public byte[] getEncodedValue() {
      return this.value;
   }
}
