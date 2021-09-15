package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MetadataUtil {
   public static Map<String, SDDocument> getMetadataClosure(@NotNull String systemId, @NotNull SDDocumentResolver resolver, boolean onlyTopLevelSchemas) {
      Map<String, SDDocument> closureDocs = new HashMap();
      Set<String> remaining = new HashSet();
      remaining.add(systemId);

      while(true) {
         SDDocument currentDoc;
         Set imports;
         do {
            if (remaining.isEmpty()) {
               return closureDocs;
            }

            Iterator<String> it = remaining.iterator();
            String current = (String)it.next();
            remaining.remove(current);
            currentDoc = resolver.resolve(current);
            SDDocument old = (SDDocument)closureDocs.put(currentDoc.getURL().toExternalForm(), currentDoc);

            assert old == null;

            imports = currentDoc.getImports();
         } while(currentDoc.isSchema() && onlyTopLevelSchemas);

         Iterator var10 = imports.iterator();

         while(var10.hasNext()) {
            String importedDoc = (String)var10.next();
            if (closureDocs.get(importedDoc) == null) {
               remaining.add(importedDoc);
            }
         }
      }
   }
}
