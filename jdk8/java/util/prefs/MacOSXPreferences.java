package java.util.prefs;

import java.util.Objects;

class MacOSXPreferences extends AbstractPreferences {
   private static final String defaultAppName = "com.apple.java.util.prefs";
   private final boolean isUser;
   private final boolean isRoot;
   private final MacOSXPreferencesFile file;
   private final String path;
   private static MacOSXPreferences userRoot = null;
   private static MacOSXPreferences systemRoot = null;

   static synchronized Preferences getUserRoot() {
      if (userRoot == null) {
         userRoot = new MacOSXPreferences(true);
      }

      return userRoot;
   }

   static synchronized Preferences getSystemRoot() {
      if (systemRoot == null) {
         systemRoot = new MacOSXPreferences(false);
      }

      return systemRoot;
   }

   private MacOSXPreferences(boolean var1) {
      this((MacOSXPreferences)null, "", false, true, var1);
   }

   private MacOSXPreferences(MacOSXPreferences var1, String var2) {
      this(var1, var2, false, false, false);
   }

   private MacOSXPreferences(MacOSXPreferences var1, String var2, boolean var3) {
      this(var1, var2, var3, false, false);
   }

   private MacOSXPreferences(MacOSXPreferences var1, String var2, boolean var3, boolean var4, boolean var5) {
      super(var1, var2);
      this.isRoot = var4;
      if (var4) {
         this.isUser = var5;
      } else {
         this.isUser = this.isUserNode();
      }

      this.path = var4 ? this.absolutePath() : this.absolutePath() + "/";
      this.file = this.cfFileForNode(this.isUser);
      if (var3) {
         this.newNode = var3;
      } else {
         this.newNode = this.file.addNode(this.path);
      }

   }

   private MacOSXPreferencesFile cfFileForNode(boolean var1) {
      String var2 = this.path;
      boolean var3 = false;
      int var4 = -1;

      for(int var5 = 0; var5 < 4; ++var5) {
         var4 = var2.indexOf(47, var4 + 1);
         if (var4 == -1) {
            break;
         }
      }

      if (var4 == -1) {
         var2 = "com.apple.java.util.prefs";
      } else {
         var2 = var2.substring(1, var4);
         var2 = var2.replace('/', '.');
         var2 = var2.toLowerCase();
      }

      return MacOSXPreferencesFile.getFile(var2, var1);
   }

   protected void putSpi(String var1, String var2) {
      this.file.addKeyToNode(this.path, var1, var2);
   }

   protected String getSpi(String var1) {
      return this.file.getKeyFromNode(this.path, var1);
   }

   protected void removeSpi(String var1) {
      Objects.requireNonNull(var1, (String)"Specified key cannot be null");
      this.file.removeKeyFromNode(this.path, var1);
   }

   protected void removeNodeSpi() throws BackingStoreException {
      Class var1 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         ((MacOSXPreferences)this.parent()).removeChild(this.name());
         this.file.removeNode(this.path);
      }
   }

   private void removeChild(String var1) {
      this.file.removeChildFromNode(this.path, var1);
   }

   protected String[] childrenNamesSpi() throws BackingStoreException {
      String[] var1 = this.file.getChildrenForNode(this.path);
      if (var1 == null) {
         throw new BackingStoreException("Couldn't get list of children for node '" + this.path + "'");
      } else {
         return var1;
      }
   }

   protected String[] keysSpi() throws BackingStoreException {
      String[] var1 = this.file.getKeysForNode(this.path);
      if (var1 == null) {
         throw new BackingStoreException("Couldn't get list of keys for node '" + this.path + "'");
      } else {
         return var1;
      }
   }

   protected AbstractPreferences childSpi(String var1) {
      Class var2 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         boolean var3 = this.file.addChildToNode(this.path, var1);
         return new MacOSXPreferences(this, var1, var3);
      }
   }

   public void flush() throws BackingStoreException {
      synchronized(this.lock) {
         if (this.isUser) {
            if (!MacOSXPreferencesFile.flushUser()) {
               throw new BackingStoreException("Synchronization failed for node '" + this.path + "'");
            }
         } else if (!MacOSXPreferencesFile.flushWorld()) {
            throw new BackingStoreException("Synchronization failed for node '" + this.path + "'");
         }

      }
   }

   protected void flushSpi() throws BackingStoreException {
   }

   public void sync() throws BackingStoreException {
      synchronized(this.lock) {
         if (this.isRemoved()) {
            throw new IllegalStateException("Node has been removed");
         } else {
            if (this.isUser) {
               if (!MacOSXPreferencesFile.syncUser()) {
                  throw new BackingStoreException("Synchronization failed for node '" + this.path + "'");
               }
            } else if (!MacOSXPreferencesFile.syncWorld()) {
               throw new BackingStoreException("Synchronization failed for node '" + this.path + "'");
            }

         }
      }
   }

   protected void syncSpi() throws BackingStoreException {
   }
}
