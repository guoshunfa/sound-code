package com.sun.media.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import sun.reflect.misc.ReflectUtil;

public final class JARSoundbankReader extends SoundbankReader {
   private static boolean isZIP(URL var0) {
      boolean var1 = false;

      try {
         InputStream var2 = var0.openStream();

         try {
            byte[] var3 = new byte[4];
            var1 = var2.read(var3) == 4;
            if (var1) {
               var1 = var3[0] == 80 && var3[1] == 75 && var3[2] == 3 && var3[3] == 4;
            }
         } finally {
            var2.close();
         }
      } catch (IOException var8) {
      }

      return var1;
   }

   public Soundbank getSoundbank(URL var1) throws InvalidMidiDataException, IOException {
      if (!isZIP(var1)) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         URLClassLoader var3 = URLClassLoader.newInstance(new URL[]{var1});
         InputStream var4 = var3.getResourceAsStream("META-INF/services/javax.sound.midi.Soundbank");
         if (var4 == null) {
            return null;
         } else {
            try {
               BufferedReader var5 = new BufferedReader(new InputStreamReader(var4));

               for(String var6 = var5.readLine(); var6 != null; var6 = var5.readLine()) {
                  if (!var6.startsWith("#")) {
                     try {
                        Class var7 = Class.forName(var6.trim(), false, var3);
                        if (Soundbank.class.isAssignableFrom(var7)) {
                           Object var8 = ReflectUtil.newInstance(var7);
                           var2.add((Soundbank)var8);
                        }
                     } catch (ClassNotFoundException var14) {
                     } catch (InstantiationException var15) {
                     } catch (IllegalAccessException var16) {
                     }
                  }
               }
            } finally {
               var4.close();
            }

            if (var2.size() == 0) {
               return null;
            } else if (var2.size() == 1) {
               return (Soundbank)var2.get(0);
            } else {
               SimpleSoundbank var18 = new SimpleSoundbank();
               Iterator var19 = var2.iterator();

               while(var19.hasNext()) {
                  Soundbank var20 = (Soundbank)var19.next();
                  var18.addAllInstruments(var20);
               }

               return var18;
            }
         }
      }
   }

   public Soundbank getSoundbank(InputStream var1) throws InvalidMidiDataException, IOException {
      return null;
   }

   public Soundbank getSoundbank(File var1) throws InvalidMidiDataException, IOException {
      return this.getSoundbank(var1.toURI().toURL());
   }
}
