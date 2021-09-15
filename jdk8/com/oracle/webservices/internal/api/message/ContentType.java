package com.oracle.webservices.internal.api.message;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;

public interface ContentType {
   String getContentType();

   String getSOAPActionHeader();

   String getAcceptHeader();

   public static class Builder {
      private String contentType;
      private String soapAction;
      private String accept;
      private String charset;

      public ContentType.Builder contentType(String s) {
         this.contentType = s;
         return this;
      }

      public ContentType.Builder soapAction(String s) {
         this.soapAction = s;
         return this;
      }

      public ContentType.Builder accept(String s) {
         this.accept = s;
         return this;
      }

      public ContentType.Builder charset(String s) {
         this.charset = s;
         return this;
      }

      public ContentType build() {
         return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
      }
   }
}
