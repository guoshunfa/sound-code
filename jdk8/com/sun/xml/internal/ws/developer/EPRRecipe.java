package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.Source;

public final class EPRRecipe {
   private final List<Header> referenceParameters = new ArrayList();
   private final List<Source> metadata = new ArrayList();

   @NotNull
   public List<Header> getReferenceParameters() {
      return this.referenceParameters;
   }

   @NotNull
   public List<Source> getMetadata() {
      return this.metadata;
   }

   public EPRRecipe addReferenceParameter(Header h) {
      if (h == null) {
         throw new IllegalArgumentException();
      } else {
         this.referenceParameters.add(h);
         return this;
      }
   }

   public EPRRecipe addReferenceParameters(Header... headers) {
      Header[] var2 = headers;
      int var3 = headers.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Header h = var2[var4];
         this.addReferenceParameter(h);
      }

      return this;
   }

   public EPRRecipe addReferenceParameters(Iterable<? extends Header> headers) {
      Iterator var2 = headers.iterator();

      while(var2.hasNext()) {
         Header h = (Header)var2.next();
         this.addReferenceParameter(h);
      }

      return this;
   }

   public EPRRecipe addMetadata(Source source) {
      if (source == null) {
         throw new IllegalArgumentException();
      } else {
         this.metadata.add(source);
         return this;
      }
   }

   public EPRRecipe addMetadata(Source... sources) {
      Source[] var2 = sources;
      int var3 = sources.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Source s = var2[var4];
         this.addMetadata(s);
      }

      return this;
   }

   public EPRRecipe addMetadata(Iterable<? extends Source> sources) {
      Iterator var2 = sources.iterator();

      while(var2.hasNext()) {
         Source s = (Source)var2.next();
         this.addMetadata(s);
      }

      return this;
   }
}
