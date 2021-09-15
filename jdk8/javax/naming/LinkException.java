package javax.naming;

public class LinkException extends NamingException {
   protected Name linkResolvedName = null;
   protected Object linkResolvedObj = null;
   protected Name linkRemainingName = null;
   protected String linkExplanation = null;
   private static final long serialVersionUID = -7967662604076777712L;

   public LinkException(String var1) {
      super(var1);
   }

   public LinkException() {
   }

   public Name getLinkResolvedName() {
      return this.linkResolvedName;
   }

   public Name getLinkRemainingName() {
      return this.linkRemainingName;
   }

   public Object getLinkResolvedObj() {
      return this.linkResolvedObj;
   }

   public String getLinkExplanation() {
      return this.linkExplanation;
   }

   public void setLinkExplanation(String var1) {
      this.linkExplanation = var1;
   }

   public void setLinkResolvedName(Name var1) {
      if (var1 != null) {
         this.linkResolvedName = (Name)((Name)var1.clone());
      } else {
         this.linkResolvedName = null;
      }

   }

   public void setLinkRemainingName(Name var1) {
      if (var1 != null) {
         this.linkRemainingName = (Name)((Name)var1.clone());
      } else {
         this.linkRemainingName = null;
      }

   }

   public void setLinkResolvedObj(Object var1) {
      this.linkResolvedObj = var1;
   }

   public String toString() {
      return super.toString() + "; Link Remaining Name: '" + this.linkRemainingName + "'";
   }

   public String toString(boolean var1) {
      return var1 && this.linkResolvedObj != null ? this.toString() + "; Link Resolved Object: " + this.linkResolvedObj : this.toString();
   }
}
