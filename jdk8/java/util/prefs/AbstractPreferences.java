package java.util.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeSet;

public abstract class AbstractPreferences extends Preferences {
   private final String name;
   private final String absolutePath;
   final AbstractPreferences parent;
   private final AbstractPreferences root;
   protected boolean newNode = false;
   private Map<String, AbstractPreferences> kidCache = new HashMap();
   private boolean removed = false;
   private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];
   private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];
   protected final Object lock = new Object();
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final AbstractPreferences[] EMPTY_ABSTRACT_PREFS_ARRAY = new AbstractPreferences[0];
   private static final List<EventObject> eventQueue = new LinkedList();
   private static Thread eventDispatchThread = null;

   protected AbstractPreferences(AbstractPreferences var1, String var2) {
      if (var1 == null) {
         if (!var2.equals("")) {
            throw new IllegalArgumentException("Root name '" + var2 + "' must be \"\"");
         }

         this.absolutePath = "/";
         this.root = this;
      } else {
         if (var2.indexOf(47) != -1) {
            throw new IllegalArgumentException("Name '" + var2 + "' contains '/'");
         }

         if (var2.equals("")) {
            throw new IllegalArgumentException("Illegal name: empty string");
         }

         this.root = var1.root;
         this.absolutePath = var1 == this.root ? "/" + var2 : var1.absolutePath() + "/" + var2;
      }

      this.name = var2;
      this.parent = var1;
   }

   public void put(String var1, String var2) {
      if (var1 != null && var2 != null) {
         if (var1.length() > 80) {
            throw new IllegalArgumentException("Key too long: " + var1);
         } else if (var2.length() > 8192) {
            throw new IllegalArgumentException("Value too long: " + var2);
         } else {
            synchronized(this.lock) {
               if (this.removed) {
                  throw new IllegalStateException("Node has been removed.");
               } else {
                  this.putSpi(var1, var2);
                  this.enqueuePreferenceChangeEvent(var1, var2);
               }
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public String get(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("Null key");
      } else {
         synchronized(this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Node has been removed.");
            } else {
               String var4 = null;

               try {
                  var4 = this.getSpi(var1);
               } catch (Exception var7) {
               }

               return var4 == null ? var2 : var4;
            }
         }
      }
   }

   public void remove(String var1) {
      Objects.requireNonNull(var1, (String)"Specified key cannot be null");
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else {
            this.removeSpi(var1);
            this.enqueuePreferenceChangeEvent(var1, (String)null);
         }
      }
   }

   public void clear() throws BackingStoreException {
      synchronized(this.lock) {
         String[] var2 = this.keys();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.remove(var2[var3]);
         }

      }
   }

   public void putInt(String var1, int var2) {
      this.put(var1, Integer.toString(var2));
   }

   public int getInt(String var1, int var2) {
      int var3 = var2;

      try {
         String var4 = this.get(var1, (String)null);
         if (var4 != null) {
            var3 = Integer.parseInt(var4);
         }
      } catch (NumberFormatException var5) {
      }

      return var3;
   }

   public void putLong(String var1, long var2) {
      this.put(var1, Long.toString(var2));
   }

   public long getLong(String var1, long var2) {
      long var4 = var2;

      try {
         String var6 = this.get(var1, (String)null);
         if (var6 != null) {
            var4 = Long.parseLong(var6);
         }
      } catch (NumberFormatException var7) {
      }

      return var4;
   }

   public void putBoolean(String var1, boolean var2) {
      this.put(var1, String.valueOf(var2));
   }

   public boolean getBoolean(String var1, boolean var2) {
      boolean var3 = var2;
      String var4 = this.get(var1, (String)null);
      if (var4 != null) {
         if (var4.equalsIgnoreCase("true")) {
            var3 = true;
         } else if (var4.equalsIgnoreCase("false")) {
            var3 = false;
         }
      }

      return var3;
   }

   public void putFloat(String var1, float var2) {
      this.put(var1, Float.toString(var2));
   }

   public float getFloat(String var1, float var2) {
      float var3 = var2;

      try {
         String var4 = this.get(var1, (String)null);
         if (var4 != null) {
            var3 = Float.parseFloat(var4);
         }
      } catch (NumberFormatException var5) {
      }

      return var3;
   }

   public void putDouble(String var1, double var2) {
      this.put(var1, Double.toString(var2));
   }

   public double getDouble(String var1, double var2) {
      double var4 = var2;

      try {
         String var6 = this.get(var1, (String)null);
         if (var6 != null) {
            var4 = Double.parseDouble(var6);
         }
      } catch (NumberFormatException var7) {
      }

      return var4;
   }

   public void putByteArray(String var1, byte[] var2) {
      this.put(var1, Base64.byteArrayToBase64(var2));
   }

   public byte[] getByteArray(String var1, byte[] var2) {
      byte[] var3 = var2;
      String var4 = this.get(var1, (String)null);

      try {
         if (var4 != null) {
            var3 = Base64.base64ToByteArray(var4);
         }
      } catch (RuntimeException var6) {
      }

      return var3;
   }

   public String[] keys() throws BackingStoreException {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else {
            return this.keysSpi();
         }
      }
   }

   public String[] childrenNames() throws BackingStoreException {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else {
            TreeSet var2 = new TreeSet(this.kidCache.keySet());
            String[] var3 = this.childrenNamesSpi();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];
               var2.add(var6);
            }

            return (String[])var2.toArray(EMPTY_STRING_ARRAY);
         }
      }
   }

   protected final AbstractPreferences[] cachedChildren() {
      return (AbstractPreferences[])this.kidCache.values().toArray(EMPTY_ABSTRACT_PREFS_ARRAY);
   }

   public Preferences parent() {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else {
            return this.parent;
         }
      }
   }

   public Preferences node(String var1) {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         }

         if (var1.equals("")) {
            return this;
         }

         if (var1.equals("/")) {
            return this.root;
         }

         if (var1.charAt(0) != '/') {
            return this.node(new StringTokenizer(var1, "/", true));
         }
      }

      return this.root.node(new StringTokenizer(var1.substring(1), "/", true));
   }

   private Preferences node(StringTokenizer var1) {
      String var2 = var1.nextToken();
      if (var2.equals("/")) {
         throw new IllegalArgumentException("Consecutive slashes in path");
      } else {
         synchronized(this.lock) {
            AbstractPreferences var4 = (AbstractPreferences)this.kidCache.get(var2);
            if (var4 == null) {
               if (var2.length() > 80) {
                  throw new IllegalArgumentException("Node name " + var2 + " too long");
               }

               var4 = this.childSpi(var2);
               if (var4.newNode) {
                  this.enqueueNodeAddedEvent(var4);
               }

               this.kidCache.put(var2, var4);
            }

            if (!var1.hasMoreTokens()) {
               return var4;
            } else {
               var1.nextToken();
               if (!var1.hasMoreTokens()) {
                  throw new IllegalArgumentException("Path ends with slash");
               } else {
                  return var4.node(var1);
               }
            }
         }
      }
   }

   public boolean nodeExists(String var1) throws BackingStoreException {
      synchronized(this.lock) {
         if (var1.equals("")) {
            return !this.removed;
         }

         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         }

         if (var1.equals("/")) {
            return true;
         }

         if (var1.charAt(0) != '/') {
            return this.nodeExists(new StringTokenizer(var1, "/", true));
         }
      }

      return this.root.nodeExists(new StringTokenizer(var1.substring(1), "/", true));
   }

   private boolean nodeExists(StringTokenizer var1) throws BackingStoreException {
      String var2 = var1.nextToken();
      if (var2.equals("/")) {
         throw new IllegalArgumentException("Consecutive slashes in path");
      } else {
         synchronized(this.lock) {
            AbstractPreferences var4 = (AbstractPreferences)this.kidCache.get(var2);
            if (var4 == null) {
               var4 = this.getChild(var2);
            }

            if (var4 == null) {
               return false;
            } else if (!var1.hasMoreTokens()) {
               return true;
            } else {
               var1.nextToken();
               if (!var1.hasMoreTokens()) {
                  throw new IllegalArgumentException("Path ends with slash");
               } else {
                  return var4.nodeExists(var1);
               }
            }
         }
      }
   }

   public void removeNode() throws BackingStoreException {
      if (this == this.root) {
         throw new UnsupportedOperationException("Can't remove the root!");
      } else {
         synchronized(this.parent.lock) {
            this.removeNode2();
            this.parent.kidCache.remove(this.name);
         }
      }
   }

   private void removeNode2() throws BackingStoreException {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node already removed.");
         } else {
            String[] var2 = this.childrenNamesSpi();

            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (!this.kidCache.containsKey(var2[var3])) {
                  this.kidCache.put(var2[var3], this.childSpi(var2[var3]));
               }
            }

            Iterator var8 = this.kidCache.values().iterator();

            while(var8.hasNext()) {
               try {
                  ((AbstractPreferences)var8.next()).removeNode2();
                  var8.remove();
               } catch (BackingStoreException var6) {
               }
            }

            this.removeNodeSpi();
            this.removed = true;
            this.parent.enqueueNodeRemovedEvent(this);
         }
      }
   }

   public String name() {
      return this.name;
   }

   public String absolutePath() {
      return this.absolutePath;
   }

   public boolean isUserNode() {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return AbstractPreferences.this.root == Preferences.userRoot();
         }
      });
   }

   public void addPreferenceChangeListener(PreferenceChangeListener var1) {
      if (var1 == null) {
         throw new NullPointerException("Change listener is null.");
      } else {
         synchronized(this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Node has been removed.");
            }

            PreferenceChangeListener[] var3 = this.prefListeners;
            this.prefListeners = new PreferenceChangeListener[var3.length + 1];
            System.arraycopy(var3, 0, this.prefListeners, 0, var3.length);
            this.prefListeners[var3.length] = var1;
         }

         startEventDispatchThreadIfNecessary();
      }
   }

   public void removePreferenceChangeListener(PreferenceChangeListener var1) {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else if (this.prefListeners != null && this.prefListeners.length != 0) {
            PreferenceChangeListener[] var3 = new PreferenceChangeListener[this.prefListeners.length - 1];

            int var4;
            for(var4 = 0; var4 < var3.length && this.prefListeners[var4] != var1; var3[var4] = this.prefListeners[var4++]) {
            }

            if (var4 == var3.length && this.prefListeners[var4] != var1) {
               throw new IllegalArgumentException("Listener not registered.");
            } else {
               while(var4 < var3.length) {
                  var3[var4++] = this.prefListeners[var4];
               }

               this.prefListeners = var3;
            }
         } else {
            throw new IllegalArgumentException("Listener not registered.");
         }
      }
   }

   public void addNodeChangeListener(NodeChangeListener var1) {
      if (var1 == null) {
         throw new NullPointerException("Change listener is null.");
      } else {
         synchronized(this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Node has been removed.");
            }

            if (this.nodeListeners == null) {
               this.nodeListeners = new NodeChangeListener[1];
               this.nodeListeners[0] = var1;
            } else {
               NodeChangeListener[] var3 = this.nodeListeners;
               this.nodeListeners = new NodeChangeListener[var3.length + 1];
               System.arraycopy(var3, 0, this.nodeListeners, 0, var3.length);
               this.nodeListeners[var3.length] = var1;
            }
         }

         startEventDispatchThreadIfNecessary();
      }
   }

   public void removeNodeChangeListener(NodeChangeListener var1) {
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed.");
         } else if (this.nodeListeners != null && this.nodeListeners.length != 0) {
            int var3;
            for(var3 = 0; var3 < this.nodeListeners.length && this.nodeListeners[var3] != var1; ++var3) {
            }

            if (var3 == this.nodeListeners.length) {
               throw new IllegalArgumentException("Listener not registered.");
            } else {
               NodeChangeListener[] var4 = new NodeChangeListener[this.nodeListeners.length - 1];
               if (var3 != 0) {
                  System.arraycopy(this.nodeListeners, 0, var4, 0, var3);
               }

               if (var3 != var4.length) {
                  System.arraycopy(this.nodeListeners, var3 + 1, var4, var3, var4.length - var3);
               }

               this.nodeListeners = var4;
            }
         } else {
            throw new IllegalArgumentException("Listener not registered.");
         }
      }
   }

   protected abstract void putSpi(String var1, String var2);

   protected abstract String getSpi(String var1);

   protected abstract void removeSpi(String var1);

   protected abstract void removeNodeSpi() throws BackingStoreException;

   protected abstract String[] keysSpi() throws BackingStoreException;

   protected abstract String[] childrenNamesSpi() throws BackingStoreException;

   protected AbstractPreferences getChild(String var1) throws BackingStoreException {
      synchronized(this.lock) {
         String[] var3 = this.childrenNames();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4].equals(var1)) {
               return this.childSpi(var3[var4]);
            }
         }

         return null;
      }
   }

   protected abstract AbstractPreferences childSpi(String var1);

   public String toString() {
      return (this.isUserNode() ? "User" : "System") + " Preference Node: " + this.absolutePath();
   }

   public void sync() throws BackingStoreException {
      this.sync2();
   }

   private void sync2() throws BackingStoreException {
      AbstractPreferences[] var1;
      synchronized(this.lock) {
         if (this.removed) {
            throw new IllegalStateException("Node has been removed");
         }

         this.syncSpi();
         var1 = this.cachedChildren();
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].sync2();
      }

   }

   protected abstract void syncSpi() throws BackingStoreException;

   public void flush() throws BackingStoreException {
      this.flush2();
   }

   private void flush2() throws BackingStoreException {
      AbstractPreferences[] var1;
      synchronized(this.lock) {
         this.flushSpi();
         if (this.removed) {
            return;
         }

         var1 = this.cachedChildren();
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].flush2();
      }

   }

   protected abstract void flushSpi() throws BackingStoreException;

   protected boolean isRemoved() {
      synchronized(this.lock) {
         return this.removed;
      }
   }

   private static synchronized void startEventDispatchThreadIfNecessary() {
      if (eventDispatchThread == null) {
         eventDispatchThread = new AbstractPreferences.EventDispatchThread();
         eventDispatchThread.setDaemon(true);
         eventDispatchThread.start();
      }

   }

   PreferenceChangeListener[] prefListeners() {
      synchronized(this.lock) {
         return this.prefListeners;
      }
   }

   NodeChangeListener[] nodeListeners() {
      synchronized(this.lock) {
         return this.nodeListeners;
      }
   }

   private void enqueuePreferenceChangeEvent(String var1, String var2) {
      if (this.prefListeners.length != 0) {
         synchronized(eventQueue) {
            eventQueue.add(new PreferenceChangeEvent(this, var1, var2));
            eventQueue.notify();
         }
      }

   }

   private void enqueueNodeAddedEvent(Preferences var1) {
      if (this.nodeListeners.length != 0) {
         synchronized(eventQueue) {
            eventQueue.add(new AbstractPreferences.NodeAddedEvent(this, var1));
            eventQueue.notify();
         }
      }

   }

   private void enqueueNodeRemovedEvent(Preferences var1) {
      if (this.nodeListeners.length != 0) {
         synchronized(eventQueue) {
            eventQueue.add(new AbstractPreferences.NodeRemovedEvent(this, var1));
            eventQueue.notify();
         }
      }

   }

   public void exportNode(OutputStream var1) throws IOException, BackingStoreException {
      XmlSupport.export(var1, this, false);
   }

   public void exportSubtree(OutputStream var1) throws IOException, BackingStoreException {
      XmlSupport.export(var1, this, true);
   }

   private static class EventDispatchThread extends Thread {
      private EventDispatchThread() {
      }

      public void run() {
         while(true) {
            EventObject var1 = null;
            synchronized(AbstractPreferences.eventQueue) {
               try {
                  while(AbstractPreferences.eventQueue.isEmpty()) {
                     AbstractPreferences.eventQueue.wait();
                  }

                  var1 = (EventObject)AbstractPreferences.eventQueue.remove(0);
               } catch (InterruptedException var6) {
                  return;
               }
            }

            AbstractPreferences var2 = (AbstractPreferences)var1.getSource();
            int var5;
            if (var1 instanceof PreferenceChangeEvent) {
               PreferenceChangeEvent var8 = (PreferenceChangeEvent)var1;
               PreferenceChangeListener[] var9 = var2.prefListeners();

               for(var5 = 0; var5 < var9.length; ++var5) {
                  var9[var5].preferenceChange(var8);
               }
            } else {
               NodeChangeEvent var3 = (NodeChangeEvent)var1;
               NodeChangeListener[] var4 = var2.nodeListeners();
               if (var3 instanceof AbstractPreferences.NodeAddedEvent) {
                  for(var5 = 0; var5 < var4.length; ++var5) {
                     var4[var5].childAdded(var3);
                  }
               } else {
                  for(var5 = 0; var5 < var4.length; ++var5) {
                     var4[var5].childRemoved(var3);
                  }
               }
            }
         }
      }

      // $FF: synthetic method
      EventDispatchThread(Object var1) {
         this();
      }
   }

   private class NodeRemovedEvent extends NodeChangeEvent {
      private static final long serialVersionUID = 8735497392918824837L;

      NodeRemovedEvent(Preferences var2, Preferences var3) {
         super(var2, var3);
      }
   }

   private class NodeAddedEvent extends NodeChangeEvent {
      private static final long serialVersionUID = -6743557530157328528L;

      NodeAddedEvent(Preferences var2, Preferences var3) {
         super(var2, var3);
      }
   }
}
