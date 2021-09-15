package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.omg.CORBA.Object;

public class FileResolverImpl implements Resolver {
   private ORB orb;
   private File file;
   private Properties savedProps;
   private long fileModified = 0L;

   public FileResolverImpl(ORB var1, File var2) {
      this.orb = var1;
      this.file = var2;
      this.savedProps = new Properties();
   }

   public Object resolve(String var1) {
      this.check();
      String var2 = this.savedProps.getProperty(var1);
      return var2 == null ? null : this.orb.string_to_object(var2);
   }

   public Set list() {
      this.check();
      HashSet var1 = new HashSet();
      Enumeration var2 = this.savedProps.propertyNames();

      while(var2.hasMoreElements()) {
         var1.add(var2.nextElement());
      }

      return var1;
   }

   private void check() {
      if (this.file != null) {
         long var1 = this.file.lastModified();
         if (var1 > this.fileModified) {
            try {
               FileInputStream var3 = new FileInputStream(this.file);
               this.savedProps.clear();
               this.savedProps.load((InputStream)var3);
               var3.close();
               this.fileModified = var1;
            } catch (FileNotFoundException var4) {
               System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", this.file.getAbsolutePath()));
            } catch (IOException var5) {
               System.err.println(CorbaResourceUtil.getText("bootstrap.exception", this.file.getAbsolutePath(), var5.toString()));
            }
         }

      }
   }
}
