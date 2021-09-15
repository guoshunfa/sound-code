package javax.naming;

public class LinkRef extends Reference {
   static final String linkClassName = LinkRef.class.getName();
   static final String linkAddrType = "LinkAddress";
   private static final long serialVersionUID = -5386290613498931298L;

   public LinkRef(Name var1) {
      super(linkClassName, new StringRefAddr("LinkAddress", var1.toString()));
   }

   public LinkRef(String var1) {
      super(linkClassName, new StringRefAddr("LinkAddress", var1));
   }

   public String getLinkName() throws NamingException {
      if (this.className != null && this.className.equals(linkClassName)) {
         RefAddr var1 = this.get("LinkAddress");
         if (var1 != null && var1 instanceof StringRefAddr) {
            return (String)((StringRefAddr)var1).getContent();
         }
      }

      throw new MalformedLinkException();
   }
}
