package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

final class DefaultIDResolver extends IDResolver {
   private HashMap<String, Object> idmap = null;

   public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
      if (this.idmap != null) {
         this.idmap.clear();
      }

   }

   public void bind(String id, Object obj) {
      if (this.idmap == null) {
         this.idmap = new HashMap();
      }

      this.idmap.put(id, obj);
   }

   public Callable resolve(final String id, Class targetType) {
      return new Callable() {
         public Object call() throws Exception {
            return DefaultIDResolver.this.idmap == null ? null : DefaultIDResolver.this.idmap.get(id);
         }
      };
   }
}
