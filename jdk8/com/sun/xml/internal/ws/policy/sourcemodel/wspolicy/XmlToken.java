package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

public enum XmlToken {
   Policy("Policy", true),
   ExactlyOne("ExactlyOne", true),
   All("All", true),
   PolicyReference("PolicyReference", true),
   UsingPolicy("UsingPolicy", true),
   Name("Name", false),
   Optional("Optional", false),
   Ignorable("Ignorable", false),
   PolicyUris("PolicyURIs", false),
   Uri("URI", false),
   Digest("Digest", false),
   DigestAlgorithm("DigestAlgorithm", false),
   UNKNOWN("", true);

   private String tokenName;
   private boolean element;

   public static XmlToken resolveToken(String name) {
      XmlToken[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         XmlToken token = var1[var3];
         if (token.toString().equals(name)) {
            return token;
         }
      }

      return UNKNOWN;
   }

   private XmlToken(String name, boolean element) {
      this.tokenName = name;
      this.element = element;
   }

   public boolean isElement() {
      return this.element;
   }

   public String toString() {
      return this.tokenName;
   }
}
