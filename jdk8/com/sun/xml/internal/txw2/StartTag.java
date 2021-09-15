package com.sun.xml.internal.txw2;

class StartTag extends Content implements NamespaceResolver {
   private String uri;
   private final String localName;
   private Attribute firstAtt;
   private Attribute lastAtt;
   private ContainerElement owner;
   private NamespaceDecl firstNs;
   private NamespaceDecl lastNs;
   final Document document;

   public StartTag(ContainerElement owner, String uri, String localName) {
      this(owner.document, uri, localName);
      this.owner = owner;
   }

   public StartTag(Document document, String uri, String localName) {
      assert uri != null;

      assert localName != null;

      this.uri = uri;
      this.localName = localName;
      this.document = document;
      this.addNamespaceDecl(uri, (String)null, false);
   }

   public void addAttribute(String nsUri, String localName, Object arg) {
      this.checkWritable();

      Attribute a;
      for(a = this.firstAtt; a != null && !a.hasName(nsUri, localName); a = a.next) {
      }

      if (a == null) {
         a = new Attribute(nsUri, localName);
         if (this.lastAtt == null) {
            assert this.firstAtt == null;

            this.firstAtt = this.lastAtt = a;
         } else {
            assert this.firstAtt != null;

            this.lastAtt.next = a;
            this.lastAtt = a;
         }

         if (nsUri.length() > 0) {
            this.addNamespaceDecl(nsUri, (String)null, true);
         }
      }

      this.document.writeValue(arg, this, a.value);
   }

   public NamespaceDecl addNamespaceDecl(String uri, String prefix, boolean requirePrefix) {
      this.checkWritable();
      if (uri == null) {
         throw new IllegalArgumentException();
      } else {
         if (uri.length() == 0) {
            if (requirePrefix) {
               throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix");
            }

            if (prefix != null && prefix.length() > 0) {
               throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix");
            }

            prefix = "";
         }

         NamespaceDecl n;
         for(n = this.firstNs; n != null; n = n.next) {
            if (uri.equals(n.uri)) {
               if (prefix == null) {
                  n.requirePrefix |= requirePrefix;
                  return n;
               }

               if (n.prefix == null) {
                  n.prefix = prefix;
                  n.requirePrefix |= requirePrefix;
                  return n;
               }

               if (prefix.equals(n.prefix)) {
                  n.requirePrefix |= requirePrefix;
                  return n;
               }
            }

            if (prefix != null && n.prefix != null && n.prefix.equals(prefix)) {
               throw new IllegalArgumentException("Prefix '" + prefix + "' is already bound to '" + n.uri + '\'');
            }
         }

         n = new NamespaceDecl(this.document.assignNewId(), uri, prefix, requirePrefix);
         if (this.lastNs == null) {
            assert this.firstNs == null;

            this.firstNs = this.lastNs = n;
         } else {
            assert this.firstNs != null;

            this.lastNs.next = n;
            this.lastNs = n;
         }

         return n;
      }
   }

   private void checkWritable() {
      if (this.isWritten()) {
         throw new IllegalStateException("The start tag of " + this.localName + " has already been written. If you need out of order writing, see the TypedXmlWriter.block method");
      }
   }

   boolean isWritten() {
      return this.uri == null;
   }

   boolean isReadyToCommit() {
      if (this.owner != null && this.owner.isBlocked()) {
         return false;
      } else {
         for(Content c = this.getNext(); c != null; c = c.getNext()) {
            if (c.concludesPendingStartTag()) {
               return true;
            }
         }

         return false;
      }
   }

   public void written() {
      this.firstAtt = this.lastAtt = null;
      this.uri = null;
      if (this.owner != null) {
         assert this.owner.startTag == this;

         this.owner.startTag = null;
      }

   }

   boolean concludesPendingStartTag() {
      return true;
   }

   void accept(ContentVisitor visitor) {
      visitor.onStartTag(this.uri, this.localName, this.firstAtt, this.firstNs);
   }

   public String getPrefix(String nsUri) {
      NamespaceDecl ns = this.addNamespaceDecl(nsUri, (String)null, false);
      return ns.prefix != null ? ns.prefix : ns.dummyPrefix;
   }
}
