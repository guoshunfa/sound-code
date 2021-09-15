package sun.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CoreResourceBundleControl extends ResourceBundle.Control {
   private final Collection<Locale> excludedJDKLocales;
   private static CoreResourceBundleControl resourceBundleControlInstance = new CoreResourceBundleControl();

   protected CoreResourceBundleControl() {
      this.excludedJDKLocales = Arrays.asList(Locale.GERMANY, Locale.ENGLISH, Locale.US, new Locale("es", "ES"), Locale.FRANCE, Locale.ITALY, Locale.JAPAN, Locale.KOREA, new Locale("sv", "SE"), Locale.CHINESE);
   }

   public static CoreResourceBundleControl getRBControlInstance() {
      return resourceBundleControlInstance;
   }

   public static CoreResourceBundleControl getRBControlInstance(String var0) {
      return !var0.startsWith("com.sun.") && !var0.startsWith("java.") && !var0.startsWith("javax.") && !var0.startsWith("sun.") ? null : resourceBundleControlInstance;
   }

   public List<Locale> getCandidateLocales(String var1, Locale var2) {
      List var3 = super.getCandidateLocales(var1, var2);
      var3.removeAll(this.excludedJDKLocales);
      return var3;
   }

   public long getTimeToLive(String var1, Locale var2) {
      return -1L;
   }
}
