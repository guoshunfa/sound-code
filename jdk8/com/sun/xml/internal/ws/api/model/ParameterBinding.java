package com.sun.xml.internal.ws.api.model;

public final class ParameterBinding {
   public static final ParameterBinding BODY;
   public static final ParameterBinding HEADER;
   public static final ParameterBinding UNBOUND;
   public final ParameterBinding.Kind kind;
   private String mimeType;

   public static ParameterBinding createAttachment(String mimeType) {
      return new ParameterBinding(ParameterBinding.Kind.ATTACHMENT, mimeType);
   }

   private ParameterBinding(ParameterBinding.Kind kind, String mimeType) {
      this.kind = kind;
      this.mimeType = mimeType;
   }

   public String toString() {
      return this.kind.toString();
   }

   public String getMimeType() {
      if (!this.isAttachment()) {
         throw new IllegalStateException();
      } else {
         return this.mimeType;
      }
   }

   public boolean isBody() {
      return this == BODY;
   }

   public boolean isHeader() {
      return this == HEADER;
   }

   public boolean isUnbound() {
      return this == UNBOUND;
   }

   public boolean isAttachment() {
      return this.kind == ParameterBinding.Kind.ATTACHMENT;
   }

   static {
      BODY = new ParameterBinding(ParameterBinding.Kind.BODY, (String)null);
      HEADER = new ParameterBinding(ParameterBinding.Kind.HEADER, (String)null);
      UNBOUND = new ParameterBinding(ParameterBinding.Kind.UNBOUND, (String)null);
   }

   public static enum Kind {
      BODY,
      HEADER,
      UNBOUND,
      ATTACHMENT;
   }
}
