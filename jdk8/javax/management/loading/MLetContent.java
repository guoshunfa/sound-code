package javax.management.loading;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MLetContent {
   private Map<String, String> attributes;
   private List<String> types;
   private List<String> values;
   private URL documentURL;
   private URL baseURL;

   public MLetContent(URL var1, Map<String, String> var2, List<String> var3, List<String> var4) {
      this.documentURL = var1;
      this.attributes = Collections.unmodifiableMap(var2);
      this.types = Collections.unmodifiableList(var3);
      this.values = Collections.unmodifiableList(var4);
      String var5 = this.getParameter("codebase");
      if (var5 != null) {
         if (!var5.endsWith("/")) {
            var5 = var5 + "/";
         }

         try {
            this.baseURL = new URL(this.documentURL, var5);
         } catch (MalformedURLException var10) {
         }
      }

      if (this.baseURL == null) {
         String var6 = this.documentURL.getFile();
         int var7 = var6.lastIndexOf(47);
         if (var7 >= 0 && var7 < var6.length() - 1) {
            try {
               this.baseURL = new URL(this.documentURL, var6.substring(0, var7 + 1));
            } catch (MalformedURLException var9) {
            }
         }
      }

      if (this.baseURL == null) {
         this.baseURL = this.documentURL;
      }

   }

   public Map<String, String> getAttributes() {
      return this.attributes;
   }

   public URL getDocumentBase() {
      return this.documentURL;
   }

   public URL getCodeBase() {
      return this.baseURL;
   }

   public String getJarFiles() {
      return this.getParameter("archive");
   }

   public String getCode() {
      return this.getParameter("code");
   }

   public String getSerializedObject() {
      return this.getParameter("object");
   }

   public String getName() {
      return this.getParameter("name");
   }

   public String getVersion() {
      return this.getParameter("version");
   }

   public List<String> getParameterTypes() {
      return this.types;
   }

   public List<String> getParameterValues() {
      return this.values;
   }

   private String getParameter(String var1) {
      return (String)this.attributes.get(var1.toLowerCase());
   }
}
