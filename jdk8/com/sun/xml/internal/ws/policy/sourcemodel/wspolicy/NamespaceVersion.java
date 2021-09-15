package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public enum NamespaceVersion {
   v1_2("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp1_2", new XmlToken[]{XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm}),
   v1_5("http://www.w3.org/ns/ws-policy", "wsp", new XmlToken[]{XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm});

   private final String nsUri;
   private final String defaultNsPrefix;
   private final Map<XmlToken, QName> tokenToQNameCache;

   public static NamespaceVersion resolveVersion(String uri) {
      NamespaceVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NamespaceVersion namespaceVersion = var1[var3];
         if (namespaceVersion.toString().equalsIgnoreCase(uri)) {
            return namespaceVersion;
         }
      }

      return null;
   }

   public static NamespaceVersion resolveVersion(QName name) {
      return resolveVersion(name.getNamespaceURI());
   }

   public static NamespaceVersion getLatestVersion() {
      return v1_5;
   }

   public static XmlToken resolveAsToken(QName name) {
      NamespaceVersion nsVersion = resolveVersion(name);
      if (nsVersion != null) {
         XmlToken token = XmlToken.resolveToken(name.getLocalPart());
         if (nsVersion.tokenToQNameCache.containsKey(token)) {
            return token;
         }
      }

      return XmlToken.UNKNOWN;
   }

   private NamespaceVersion(String uri, String prefix, XmlToken... supportedTokens) {
      this.nsUri = uri;
      this.defaultNsPrefix = prefix;
      Map<XmlToken, QName> temp = new HashMap();
      XmlToken[] var7 = supportedTokens;
      int var8 = supportedTokens.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         XmlToken token = var7[var9];
         temp.put(token, new QName(this.nsUri, token.toString()));
      }

      this.tokenToQNameCache = Collections.unmodifiableMap(temp);
   }

   public String getDefaultNamespacePrefix() {
      return this.defaultNsPrefix;
   }

   public QName asQName(XmlToken token) throws IllegalArgumentException {
      return (QName)this.tokenToQNameCache.get(token);
   }

   public String toString() {
      return this.nsUri;
   }
}
