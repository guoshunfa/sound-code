package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class TempFiles {
   private static final Logger LOGGER = Logger.getLogger(TempFiles.class.getName());
   private static final Class<?> CLASS_FILES = safeGetClass("java.nio.file.Files");
   private static final Class<?> CLASS_PATH = safeGetClass("java.nio.file.Path");
   private static final Class<?> CLASS_FILE_ATTRIBUTE = safeGetClass("java.nio.file.attribute.FileAttribute");
   private static final Class<?> CLASS_FILE_ATTRIBUTES = safeGetClass("[Ljava.nio.file.attribute.FileAttribute;");
   private static final Method METHOD_FILE_TO_PATH = safeGetMethod(File.class, "toPath");
   private static final Method METHOD_FILES_CREATE_TEMP_FILE;
   private static final Method METHOD_FILES_CREATE_TEMP_FILE_WITHPATH;
   private static final Method METHOD_PATH_TO_FILE;
   private static boolean useJdk6API = isJdk6();

   private static boolean isJdk6() {
      String javaVersion = System.getProperty("java.version");
      LOGGER.log(Level.FINEST, (String)"Detected java version = {0}", (Object)javaVersion);
      return javaVersion.startsWith("1.6.");
   }

   private static Class<?> safeGetClass(String className) {
      if (useJdk6API) {
         return null;
      } else {
         try {
            return Class.forName(className);
         } catch (ClassNotFoundException var2) {
            LOGGER.log(Level.SEVERE, (String)"Exception cought", (Throwable)var2);
            LOGGER.log(Level.WARNING, (String)"Class {0} not found. Temp files will be created using old java.io API.", (Object)className);
            useJdk6API = true;
            return null;
         }
      }
   }

   private static Method safeGetMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
      if (useJdk6API) {
         return null;
      } else {
         try {
            return clazz.getMethod(methodName, parameterTypes);
         } catch (NoSuchMethodException var4) {
            LOGGER.log(Level.SEVERE, (String)"Exception cought", (Throwable)var4);
            LOGGER.log(Level.WARNING, (String)"Method {0} not found. Temp files will be created using old java.io API.", (Object)methodName);
            useJdk6API = true;
            return null;
         }
      }
   }

   static Object toPath(File f) throws InvocationTargetException, IllegalAccessException {
      return METHOD_FILE_TO_PATH.invoke(f);
   }

   static File toFile(Object path) throws InvocationTargetException, IllegalAccessException {
      return (File)METHOD_PATH_TO_FILE.invoke(path);
   }

   static File createTempFile(String prefix, String suffix, File dir) throws IOException {
      if (useJdk6API) {
         LOGGER.log(Level.FINEST, "Jdk6 detected, temp file (prefix:{0}, suffix:{1}) being created using old java.io API.", new Object[]{prefix, suffix});
         return File.createTempFile(prefix, suffix, dir);
      } else {
         try {
            if (dir != null) {
               Object path = toPath(dir);
               LOGGER.log(Level.FINEST, "Temp file (path: {0}, prefix:{1}, suffix:{2}) being created using NIO API.", new Object[]{dir.getAbsolutePath(), prefix, suffix});
               return toFile(METHOD_FILES_CREATE_TEMP_FILE_WITHPATH.invoke((Object)null, path, prefix, suffix, Array.newInstance(CLASS_FILE_ATTRIBUTE, 0)));
            } else {
               LOGGER.log(Level.FINEST, "Temp file (prefix:{0}, suffix:{1}) being created using NIO API.", new Object[]{prefix, suffix});
               return toFile(METHOD_FILES_CREATE_TEMP_FILE.invoke((Object)null, prefix, suffix, Array.newInstance(CLASS_FILE_ATTRIBUTE, 0)));
            }
         } catch (IllegalAccessException var4) {
            LOGGER.log(Level.SEVERE, (String)"Exception caught", (Throwable)var4);
            LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[]{dir != null ? dir.getAbsolutePath() : null, prefix, suffix});
            return File.createTempFile(prefix, suffix, dir);
         } catch (InvocationTargetException var5) {
            LOGGER.log(Level.SEVERE, (String)"Exception caught", (Throwable)var5);
            LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[]{dir != null ? dir.getAbsolutePath() : null, prefix, suffix});
            return File.createTempFile(prefix, suffix, dir);
         }
      }
   }

   static {
      METHOD_FILES_CREATE_TEMP_FILE = safeGetMethod(CLASS_FILES, "createTempFile", String.class, String.class, CLASS_FILE_ATTRIBUTES);
      METHOD_FILES_CREATE_TEMP_FILE_WITHPATH = safeGetMethod(CLASS_FILES, "createTempFile", CLASS_PATH, String.class, String.class, CLASS_FILE_ATTRIBUTES);
      METHOD_PATH_TO_FILE = safeGetMethod(CLASS_PATH, "toFile");
   }
}
