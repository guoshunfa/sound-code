package java.util.prefs;

class MacOSXPreferencesFactory implements PreferencesFactory {
   public Preferences userRoot() {
      return MacOSXPreferences.getUserRoot();
   }

   public Preferences systemRoot() {
      return MacOSXPreferences.getSystemRoot();
   }
}
