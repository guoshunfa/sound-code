package javax.naming.ldap;

public class SortKey {
   private String attrID;
   private boolean reverseOrder = false;
   private String matchingRuleID = null;

   public SortKey(String var1) {
      this.attrID = var1;
   }

   public SortKey(String var1, boolean var2, String var3) {
      this.attrID = var1;
      this.reverseOrder = !var2;
      this.matchingRuleID = var3;
   }

   public String getAttributeID() {
      return this.attrID;
   }

   public boolean isAscending() {
      return !this.reverseOrder;
   }

   public String getMatchingRuleID() {
      return this.matchingRuleID;
   }
}
