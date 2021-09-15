package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.model.ExternalMetadataReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ExternalMetadataFeature extends WebServiceFeature {
   private static final String ID = "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
   private boolean enabled = true;
   private List<String> resourceNames;
   private List<File> files;
   private MetadataReader reader;

   private ExternalMetadataFeature() {
   }

   public void addResources(String... resourceNames) {
      if (this.resourceNames == null) {
         this.resourceNames = new ArrayList();
      }

      Collections.addAll(this.resourceNames, resourceNames);
   }

   public List<String> getResourceNames() {
      return this.resourceNames;
   }

   public void addFiles(File... files) {
      if (this.files == null) {
         this.files = new ArrayList();
      }

      Collections.addAll(this.files, files);
   }

   public List<File> getFiles() {
      return this.files;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   private void setEnabled(boolean x) {
      this.enabled = x;
   }

   public String getID() {
      return "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
   }

   public MetadataReader getMetadataReader(ClassLoader classLoader, boolean disableXmlSecurity) {
      if (this.reader != null && this.enabled) {
         return this.reader;
      } else {
         return this.enabled ? new ExternalMetadataReader(this.files, this.resourceNames, classLoader, true, disableXmlSecurity) : null;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ExternalMetadataFeature that = (ExternalMetadataFeature)o;
         if (this.enabled != that.enabled) {
            return false;
         } else {
            if (this.files != null) {
               if (!this.files.equals(that.files)) {
                  return false;
               }
            } else if (that.files != null) {
               return false;
            }

            if (this.resourceNames != null) {
               if (!this.resourceNames.equals(that.resourceNames)) {
                  return false;
               }
            } else if (that.resourceNames != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.enabled ? 1 : 0;
      result = 31 * result + (this.resourceNames != null ? this.resourceNames.hashCode() : 0);
      result = 31 * result + (this.files != null ? this.files.hashCode() : 0);
      return result;
   }

   public String toString() {
      return "[" + this.getID() + ", enabled=" + this.enabled + ", resourceNames=" + this.resourceNames + ", files=" + this.files + ']';
   }

   public static ExternalMetadataFeature.Builder builder() {
      return new ExternalMetadataFeature.Builder(new ExternalMetadataFeature());
   }

   public static final class Builder {
      private final ExternalMetadataFeature o;

      Builder(ExternalMetadataFeature x) {
         this.o = x;
      }

      public ExternalMetadataFeature build() {
         return this.o;
      }

      public ExternalMetadataFeature.Builder addResources(String... res) {
         this.o.addResources(res);
         return this;
      }

      public ExternalMetadataFeature.Builder addFiles(File... files) {
         this.o.addFiles(files);
         return this;
      }

      public ExternalMetadataFeature.Builder setEnabled(boolean enabled) {
         this.o.setEnabled(enabled);
         return this;
      }

      public ExternalMetadataFeature.Builder setReader(MetadataReader r) {
         this.o.reader = r;
         return this;
      }
   }
}
