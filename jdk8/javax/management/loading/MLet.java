package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.ServiceNotFoundException;

public class MLet extends URLClassLoader implements MLetMBean, MBeanRegistration, Externalizable {
   private static final long serialVersionUID = 3636148327800330130L;
   private MBeanServer server;
   private List<MLetContent> mletList;
   private String libraryDirectory;
   private ObjectName mletObjectName;
   private URL[] myUrls;
   private transient ClassLoaderRepository currentClr;
   private transient boolean delegateToCLR;
   private Map<String, Class<?>> primitiveClasses;

   public MLet() {
      this(new URL[0]);
   }

   public MLet(URL[] var1) {
      this(var1, true);
   }

   public MLet(URL[] var1, ClassLoader var2) {
      this(var1, var2, true);
   }

   public MLet(URL[] var1, ClassLoader var2, URLStreamHandlerFactory var3) {
      this(var1, var2, var3, true);
   }

   public MLet(URL[] var1, boolean var2) {
      super(var1);
      this.server = null;
      this.mletList = new ArrayList();
      this.mletObjectName = null;
      this.myUrls = null;
      this.primitiveClasses = new HashMap(8);
      this.primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
      this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
      this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
      this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
      this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
      this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
      this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
      this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
      this.init(var2);
   }

   public MLet(URL[] var1, ClassLoader var2, boolean var3) {
      super(var1, var2);
      this.server = null;
      this.mletList = new ArrayList();
      this.mletObjectName = null;
      this.myUrls = null;
      this.primitiveClasses = new HashMap(8);
      this.primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
      this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
      this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
      this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
      this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
      this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
      this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
      this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
      this.init(var3);
   }

   public MLet(URL[] var1, ClassLoader var2, URLStreamHandlerFactory var3, boolean var4) {
      super(var1, var2, var3);
      this.server = null;
      this.mletList = new ArrayList();
      this.mletObjectName = null;
      this.myUrls = null;
      this.primitiveClasses = new HashMap(8);
      this.primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
      this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
      this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
      this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
      this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
      this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
      this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
      this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
      this.init(var4);
   }

   private void init(boolean var1) {
      this.delegateToCLR = var1;

      try {
         this.libraryDirectory = System.getProperty("jmx.mlet.library.dir");
         if (this.libraryDirectory == null) {
            this.libraryDirectory = this.getTmpDir();
         }
      } catch (SecurityException var3) {
      }

   }

   public void addURL(URL var1) {
      if (!Arrays.asList(this.getURLs()).contains(var1)) {
         super.addURL(var1);
      }

   }

   public void addURL(String var1) throws ServiceNotFoundException {
      try {
         URL var2 = new URL(var1);
         if (!Arrays.asList(this.getURLs()).contains(var2)) {
            super.addURL(var2);
         }

      } catch (MalformedURLException var3) {
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "addUrl", (String)("Malformed URL: " + var1), (Throwable)var3);
         }

         throw new ServiceNotFoundException("The specified URL is malformed");
      }
   }

   public URL[] getURLs() {
      return super.getURLs();
   }

   public Set<Object> getMBeansFromURL(URL var1) throws ServiceNotFoundException {
      if (var1 == null) {
         throw new ServiceNotFoundException("The specified URL is null");
      } else {
         return this.getMBeansFromURL(var1.toString());
      }
   }

   public Set<Object> getMBeansFromURL(String var1) throws ServiceNotFoundException {
      String var2 = "getMBeansFromURL";
      if (this.server == null) {
         throw new IllegalStateException("This MLet MBean is not registered with an MBeanServer.");
      } else if (var1 == null) {
         JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, "URL is null");
         throw new ServiceNotFoundException("The specified URL is null");
      } else {
         var1 = var1.replace(File.separatorChar, '/');
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, "<URL = " + var1 + ">");
         }

         try {
            MLetParser var3 = new MLetParser();
            this.mletList = var3.parseURL(var1);
         } catch (Exception var24) {
            String var4 = "Problems while parsing URL [" + var1 + "], got exception [" + var24.toString() + "]";
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, var4);
            throw (ServiceNotFoundException)EnvHelp.initCause(new ServiceNotFoundException(var4), var24);
         }

         if (this.mletList.size() == 0) {
            String var37 = "File " + var1 + " not found or MLET tag not defined in file";
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, var37);
            throw new ServiceNotFoundException(var37);
         } else {
            HashSet var36 = new HashSet();
            Iterator var38 = this.mletList.iterator();

            while(true) {
               while(var38.hasNext()) {
                  MLetContent var5 = (MLetContent)var38.next();
                  String var6 = var5.getCode();
                  if (var6 != null && var6.endsWith(".class")) {
                     var6 = var6.substring(0, var6.length() - 6);
                  }

                  String var7 = var5.getName();
                  URL var8 = var5.getCodeBase();
                  String var9 = var5.getVersion();
                  String var10 = var5.getSerializedObject();
                  String var11 = var5.getJarFiles();
                  URL var12 = var5.getDocumentBase();
                  if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                     StringBuilder var13 = (new StringBuilder()).append("\n\tMLET TAG     = ").append((Object)var5.getAttributes()).append("\n\tCODEBASE     = ").append((Object)var8).append("\n\tARCHIVE      = ").append(var11).append("\n\tCODE         = ").append(var6).append("\n\tOBJECT       = ").append(var10).append("\n\tNAME         = ").append(var7).append("\n\tVERSION      = ").append(var9).append("\n\tDOCUMENT URL = ").append((Object)var12);
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, var13.toString());
                  }

                  StringTokenizer var39 = new StringTokenizer(var11, ",", false);

                  while(var39.hasMoreTokens()) {
                     String var14 = var39.nextToken().trim();
                     if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, "Load archive for codebase <" + var8 + ">, file <" + var14 + ">");
                     }

                     try {
                        var8 = this.check(var9, var8, var14, var5);
                     } catch (Exception var25) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), var2, (String)"Got unexpected exception", (Throwable)var25);
                        var36.add(var25);
                        continue;
                     }

                     try {
                        if (!Arrays.asList(this.getURLs()).contains(new URL(var8.toString() + var14))) {
                           this.addURL(var8 + var14);
                        }
                     } catch (MalformedURLException var23) {
                     }
                  }

                  if (var6 != null && var10 != null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, "CODE and OBJECT parameters cannot be specified at the same time in tag MLET");
                     var36.add(new Error("CODE and OBJECT parameters cannot be specified at the same time in tag MLET"));
                  } else if (var6 == null && var10 == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, "Either CODE or OBJECT parameter must be specified in tag MLET");
                     var36.add(new Error("Either CODE or OBJECT parameter must be specified in tag MLET"));
                  } else {
                     ObjectInstance var15;
                     try {
                        if (var6 != null) {
                           List var16 = var5.getParameterTypes();
                           List var17 = var5.getParameterValues();
                           ArrayList var18 = new ArrayList();

                           for(int var19 = 0; var19 < var16.size(); ++var19) {
                              var18.add(this.constructParameter((String)var17.get(var19), (String)var16.get(var19)));
                           }

                           if (var16.isEmpty()) {
                              if (var7 == null) {
                                 var15 = this.server.createMBean(var6, (ObjectName)null, this.mletObjectName);
                              } else {
                                 var15 = this.server.createMBean(var6, new ObjectName(var7), this.mletObjectName);
                              }
                           } else {
                              Object[] var41 = var18.toArray();
                              String[] var20 = new String[var16.size()];
                              var16.toArray(var20);
                              if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                                 StringBuilder var21 = new StringBuilder();

                                 for(int var22 = 0; var22 < var20.length; ++var22) {
                                    var21.append("\n\tSignature     = ").append(var20[var22]).append("\t\nParams        = ").append(var41[var22]);
                                 }

                                 JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), var2, var21.toString());
                              }

                              if (var7 == null) {
                                 var15 = this.server.createMBean(var6, (ObjectName)null, this.mletObjectName, var41, var20);
                              } else {
                                 var15 = this.server.createMBean(var6, new ObjectName(var7), this.mletObjectName, var41, var20);
                              }
                           }
                        } else {
                           Object var40 = this.loadSerializedObject(var8, var10);
                           if (var7 == null) {
                              this.server.registerMBean(var40, (ObjectName)null);
                           } else {
                              this.server.registerMBean(var40, new ObjectName(var7));
                           }

                           var15 = new ObjectInstance(var7, var40.getClass().getName());
                        }
                     } catch (ReflectionException var26) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"ReflectionException", (Throwable)var26);
                        var36.add(var26);
                        continue;
                     } catch (InstanceAlreadyExistsException var27) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"InstanceAlreadyExistsException", (Throwable)var27);
                        var36.add(var27);
                        continue;
                     } catch (MBeanRegistrationException var28) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"MBeanRegistrationException", (Throwable)var28);
                        var36.add(var28);
                        continue;
                     } catch (MBeanException var29) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"MBeanException", (Throwable)var29);
                        var36.add(var29);
                        continue;
                     } catch (NotCompliantMBeanException var30) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"NotCompliantMBeanException", (Throwable)var30);
                        var36.add(var30);
                        continue;
                     } catch (InstanceNotFoundException var31) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"InstanceNotFoundException", (Throwable)var31);
                        var36.add(var31);
                        continue;
                     } catch (IOException var32) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"IOException", (Throwable)var32);
                        var36.add(var32);
                        continue;
                     } catch (SecurityException var33) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"SecurityException", (Throwable)var33);
                        var36.add(var33);
                        continue;
                     } catch (Exception var34) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"Exception", (Throwable)var34);
                        var36.add(var34);
                        continue;
                     } catch (Error var35) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var2, (String)"Error", (Throwable)var35);
                        var36.add(var35);
                        continue;
                     }

                     var36.add(var15);
                  }
               }

               return var36;
            }
         }
      }
   }

   public synchronized String getLibraryDirectory() {
      return this.libraryDirectory;
   }

   public synchronized void setLibraryDirectory(String var1) {
      this.libraryDirectory = var1;
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.setMBeanServer(var1);
      if (var2 == null) {
         var2 = new ObjectName(var1.getDefaultDomain() + ":" + "type=MLet");
      }

      this.mletObjectName = var2;
      return this.mletObjectName;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
   }

   public void writeExternal(ObjectOutput var1) throws IOException, UnsupportedOperationException {
      throw new UnsupportedOperationException("MLet.writeExternal");
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException, UnsupportedOperationException {
      throw new UnsupportedOperationException("MLet.readExternal");
   }

   public synchronized Class<?> loadClass(String var1, ClassLoaderRepository var2) throws ClassNotFoundException {
      ClassLoaderRepository var3 = this.currentClr;

      Class var4;
      try {
         this.currentClr = var2;
         var4 = this.loadClass(var1);
      } finally {
         this.currentClr = var3;
      }

      return var4;
   }

   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      return this.findClass(var1, this.currentClr);
   }

   Class<?> findClass(String var1, ClassLoaderRepository var2) throws ClassNotFoundException {
      Class var3 = null;
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", var1);

      try {
         var3 = super.findClass(var1);
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + var1 + " loaded through MLet classloader");
         }
      } catch (ClassNotFoundException var6) {
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + var1 + " not found locally");
         }
      }

      if (var3 == null && this.delegateToCLR && var2 != null) {
         try {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + var1 + " : looking in CLR");
            }

            var3 = var2.loadClassBefore(this, var1);
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + var1 + " loaded through the default classloader repository");
            }
         } catch (ClassNotFoundException var5) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + var1 + " not found in CLR");
            }
         }
      }

      if (var3 == null) {
         JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Failed to load class " + var1);
         throw new ClassNotFoundException(var1);
      } else {
         return var3;
      }
   }

   protected String findLibrary(String var1) {
      String var3 = "findLibrary";
      String var4 = System.mapLibraryName(var1);
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, "Search " + var1 + " in all JAR files");
      }

      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, "loadLibraryAsResource(" + var4 + ")");
      }

      String var2 = this.loadLibraryAsResource(var4);
      if (var2 != null) {
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, var4 + " loaded, absolute path = " + var2);
         }

         return var2;
      } else {
         var4 = removeSpace(System.getProperty("os.name")) + File.separator + removeSpace(System.getProperty("os.arch")) + File.separator + removeSpace(System.getProperty("os.version")) + File.separator + "lib" + File.separator + var4;
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, "loadLibraryAsResource(" + var4 + ")");
         }

         var2 = this.loadLibraryAsResource(var4);
         if (var2 != null) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, var4 + " loaded, absolute path = " + var2);
            }

            return var2;
         } else {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, var1 + " not found in any JAR file");
               JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), var3, "Search " + var1 + " along the path specified as the java.library.path property");
            }

            return null;
         }
      }
   }

   private String getTmpDir() {
      String var1 = System.getProperty("java.io.tmpdir");
      if (var1 != null) {
         return var1;
      } else {
         File var2 = null;
         boolean var15 = false;

         String var4;
         boolean var5;
         label183: {
            File var3;
            label184: {
               label185: {
                  try {
                     var15 = true;
                     var2 = File.createTempFile("tmp", "jmx");
                     if (var2 == null) {
                        var3 = null;
                        var15 = false;
                        break label184;
                     }

                     var3 = var2.getParentFile();
                     if (var3 == null) {
                        var4 = null;
                        var15 = false;
                        break label185;
                     }

                     var4 = var3.getAbsolutePath();
                     var15 = false;
                  } catch (Exception var21) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to determine system temporary dir");
                     var4 = null;
                     var15 = false;
                     break label183;
                  } finally {
                     if (var15) {
                        if (var2 != null) {
                           try {
                              boolean var7 = var2.delete();
                              if (!var7) {
                                 JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
                              }
                           } catch (Exception var17) {
                              JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", (String)"Failed to delete temporary file", (Throwable)var17);
                           }
                        }

                     }
                  }

                  if (var2 != null) {
                     try {
                        var5 = var2.delete();
                        if (!var5) {
                           JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
                        }
                     } catch (Exception var20) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", (String)"Failed to delete temporary file", (Throwable)var20);
                     }
                  }

                  return var4;
               }

               if (var2 != null) {
                  try {
                     var5 = var2.delete();
                     if (!var5) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
                     }
                  } catch (Exception var19) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", (String)"Failed to delete temporary file", (Throwable)var19);
                  }
               }

               return var4;
            }

            if (var2 != null) {
               try {
                  boolean var23 = var2.delete();
                  if (!var23) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
                  }
               } catch (Exception var18) {
                  JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", (String)"Failed to delete temporary file", (Throwable)var18);
               }
            }

            return var3;
         }

         if (var2 != null) {
            try {
               var5 = var2.delete();
               if (!var5) {
                  JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
               }
            } catch (Exception var16) {
               JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", (String)"Failed to delete temporary file", (Throwable)var16);
            }
         }

         return var4;
      }
   }

   private synchronized String loadLibraryAsResource(String var1) {
      try {
         InputStream var2 = this.getResourceAsStream(var1.replace(File.separatorChar, '/'));
         if (var2 != null) {
            String var19;
            try {
               File var3 = new File(this.libraryDirectory);
               var3.mkdirs();
               File var4 = Files.createTempFile(var3.toPath(), var1 + ".", (String)null).toFile();
               var4.deleteOnExit();
               FileOutputStream var5 = new FileOutputStream(var4);

               try {
                  byte[] var6 = new byte[4096];

                  int var7;
                  while((var7 = var2.read(var6)) >= 0) {
                     var5.write(var6, 0, var7);
                  }
               } finally {
                  var5.close();
               }

               if (!var4.exists()) {
                  return null;
               }

               var19 = var4.getAbsolutePath();
            } finally {
               var2.close();
            }

            return var19;
         } else {
            return null;
         }
      } catch (Exception var18) {
         JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadLibraryAsResource", (String)("Failed to load library : " + var1), (Throwable)var18);
         return null;
      }
   }

   private static String removeSpace(String var0) {
      return var0.trim().replace(" ", "");
   }

   protected URL check(String var1, URL var2, String var3, MLetContent var4) throws Exception {
      return var2;
   }

   private Object loadSerializedObject(URL var1, String var2) throws IOException, ClassNotFoundException {
      if (var2 != null) {
         var2 = var2.replace(File.separatorChar, '/');
      }

      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "loadSerializedObject", var1.toString() + var2);
      }

      InputStream var3 = this.getResourceAsStream(var2);
      if (var3 != null) {
         try {
            MLetObjectInputStream var4 = new MLetObjectInputStream(var3, this);
            Object var5 = var4.readObject();
            var4.close();
            return var5;
         } catch (IOException var6) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", (String)("Exception while deserializing " + var2), (Throwable)var6);
            }

            throw var6;
         } catch (ClassNotFoundException var7) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", (String)("Exception while deserializing " + var2), (Throwable)var7);
            }

            throw var7;
         }
      } else {
         if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Error: File " + var2 + " containing serialized object not found");
         }

         throw new Error("File " + var2 + " containing serialized object not found");
      }
   }

   private Object constructParameter(String var1, String var2) {
      Class var3 = (Class)this.primitiveClasses.get(var2);
      if (var3 != null) {
         try {
            Constructor var4 = var3.getConstructor(String.class);
            Object[] var5 = new Object[]{var1};
            return var4.newInstance(var5);
         } catch (Exception var6) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "constructParameter", (String)"Got unexpected exception", (Throwable)var6);
         }
      }

      if (var2.compareTo("java.lang.Boolean") == 0) {
         return Boolean.valueOf(var1);
      } else if (var2.compareTo("java.lang.Byte") == 0) {
         return new Byte(var1);
      } else if (var2.compareTo("java.lang.Short") == 0) {
         return new Short(var1);
      } else if (var2.compareTo("java.lang.Long") == 0) {
         return new Long(var1);
      } else if (var2.compareTo("java.lang.Integer") == 0) {
         return new Integer(var1);
      } else if (var2.compareTo("java.lang.Float") == 0) {
         return new Float(var1);
      } else if (var2.compareTo("java.lang.Double") == 0) {
         return new Double(var1);
      } else {
         return var2.compareTo("java.lang.String") == 0 ? var1 : var1;
      }
   }

   private synchronized void setMBeanServer(final MBeanServer var1) {
      this.server = var1;
      PrivilegedAction var2 = new PrivilegedAction<ClassLoaderRepository>() {
         public ClassLoaderRepository run() {
            return var1.getClassLoaderRepository();
         }
      };
      this.currentClr = (ClassLoaderRepository)AccessController.doPrivileged(var2);
   }
}
