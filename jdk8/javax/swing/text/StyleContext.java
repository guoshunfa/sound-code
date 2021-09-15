package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.font.FontUtilities;

public class StyleContext implements Serializable, AbstractDocument.AttributeContext {
   private static StyleContext defaultContext;
   public static final String DEFAULT_STYLE = "default";
   private static Hashtable<Object, String> freezeKeyMap;
   private static Hashtable<String, Object> thawKeyMap;
   private Style styles = new StyleContext.NamedStyle((Style)null);
   private transient StyleContext.FontKey fontSearch = new StyleContext.FontKey((String)null, 0, 0);
   private transient Hashtable<StyleContext.FontKey, Font> fontTable = new Hashtable();
   private transient Map<StyleContext.SmallAttributeSet, WeakReference<StyleContext.SmallAttributeSet>> attributesPool = Collections.synchronizedMap(new WeakHashMap());
   private transient MutableAttributeSet search = new SimpleAttributeSet();
   private int unusedSets;
   static final int THRESHOLD = 9;

   public static final StyleContext getDefaultStyleContext() {
      if (defaultContext == null) {
         defaultContext = new StyleContext();
      }

      return defaultContext;
   }

   public StyleContext() {
      this.addStyle("default", (Style)null);
   }

   public Style addStyle(String var1, Style var2) {
      StyleContext.NamedStyle var3 = new StyleContext.NamedStyle(var1, var2);
      if (var1 != null) {
         this.styles.addAttribute(var1, var3);
      }

      return var3;
   }

   public void removeStyle(String var1) {
      this.styles.removeAttribute(var1);
   }

   public Style getStyle(String var1) {
      return (Style)this.styles.getAttribute(var1);
   }

   public Enumeration<?> getStyleNames() {
      return this.styles.getAttributeNames();
   }

   public void addChangeListener(ChangeListener var1) {
      this.styles.addChangeListener(var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.styles.removeChangeListener(var1);
   }

   public ChangeListener[] getChangeListeners() {
      return ((StyleContext.NamedStyle)this.styles).getChangeListeners();
   }

   public Font getFont(AttributeSet var1) {
      int var2 = 0;
      if (StyleConstants.isBold(var1)) {
         var2 |= 1;
      }

      if (StyleConstants.isItalic(var1)) {
         var2 |= 2;
      }

      String var3 = StyleConstants.getFontFamily(var1);
      int var4 = StyleConstants.getFontSize(var1);
      if (StyleConstants.isSuperscript(var1) || StyleConstants.isSubscript(var1)) {
         var4 -= 2;
      }

      return this.getFont(var3, var2, var4);
   }

   public Color getForeground(AttributeSet var1) {
      return StyleConstants.getForeground(var1);
   }

   public Color getBackground(AttributeSet var1) {
      return StyleConstants.getBackground(var1);
   }

   public Font getFont(String var1, int var2, int var3) {
      this.fontSearch.setValue(var1, var2, var3);
      Object var4 = (Font)this.fontTable.get(this.fontSearch);
      if (var4 == null) {
         Style var5 = this.getStyle("default");
         if (var5 != null) {
            Font var7 = (Font)var5.getAttribute("FONT_ATTRIBUTE_KEY");
            if (var7 != null && var7.getFamily().equalsIgnoreCase(var1)) {
               var4 = var7.deriveFont(var2, (float)var3);
            }
         }

         if (var4 == null) {
            var4 = new Font(var1, var2, var3);
         }

         if (!FontUtilities.fontSupportsDefaultEncoding((Font)var4)) {
            var4 = FontUtilities.getCompositeFontUIResource((Font)var4);
         }

         StyleContext.FontKey var6 = new StyleContext.FontKey(var1, var2, var3);
         this.fontTable.put(var6, var4);
      }

      return (Font)var4;
   }

   public FontMetrics getFontMetrics(Font var1) {
      return Toolkit.getDefaultToolkit().getFontMetrics(var1);
   }

   public synchronized AttributeSet addAttribute(AttributeSet var1, Object var2, Object var3) {
      if (var1.getAttributeCount() + 1 <= this.getCompressionThreshold()) {
         this.search.removeAttributes((AttributeSet)this.search);
         this.search.addAttributes(var1);
         this.search.addAttribute(var2, var3);
         this.reclaim(var1);
         return this.getImmutableUniqueSet();
      } else {
         MutableAttributeSet var4 = this.getMutableAttributeSet(var1);
         var4.addAttribute(var2, var3);
         return var4;
      }
   }

   public synchronized AttributeSet addAttributes(AttributeSet var1, AttributeSet var2) {
      if (var1.getAttributeCount() + var2.getAttributeCount() <= this.getCompressionThreshold()) {
         this.search.removeAttributes((AttributeSet)this.search);
         this.search.addAttributes(var1);
         this.search.addAttributes(var2);
         this.reclaim(var1);
         return this.getImmutableUniqueSet();
      } else {
         MutableAttributeSet var3 = this.getMutableAttributeSet(var1);
         var3.addAttributes(var2);
         return var3;
      }
   }

   public synchronized AttributeSet removeAttribute(AttributeSet var1, Object var2) {
      if (var1.getAttributeCount() - 1 <= this.getCompressionThreshold()) {
         this.search.removeAttributes((AttributeSet)this.search);
         this.search.addAttributes(var1);
         this.search.removeAttribute(var2);
         this.reclaim(var1);
         return this.getImmutableUniqueSet();
      } else {
         MutableAttributeSet var3 = this.getMutableAttributeSet(var1);
         var3.removeAttribute(var2);
         return var3;
      }
   }

   public synchronized AttributeSet removeAttributes(AttributeSet var1, Enumeration<?> var2) {
      if (var1.getAttributeCount() <= this.getCompressionThreshold()) {
         this.search.removeAttributes((AttributeSet)this.search);
         this.search.addAttributes(var1);
         this.search.removeAttributes(var2);
         this.reclaim(var1);
         return this.getImmutableUniqueSet();
      } else {
         MutableAttributeSet var3 = this.getMutableAttributeSet(var1);
         var3.removeAttributes(var2);
         return var3;
      }
   }

   public synchronized AttributeSet removeAttributes(AttributeSet var1, AttributeSet var2) {
      if (var1.getAttributeCount() <= this.getCompressionThreshold()) {
         this.search.removeAttributes((AttributeSet)this.search);
         this.search.addAttributes(var1);
         this.search.removeAttributes(var2);
         this.reclaim(var1);
         return this.getImmutableUniqueSet();
      } else {
         MutableAttributeSet var3 = this.getMutableAttributeSet(var1);
         var3.removeAttributes(var2);
         return var3;
      }
   }

   public AttributeSet getEmptySet() {
      return SimpleAttributeSet.EMPTY;
   }

   public void reclaim(AttributeSet var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.attributesPool.size();
      }

   }

   protected int getCompressionThreshold() {
      return 9;
   }

   protected StyleContext.SmallAttributeSet createSmallAttributeSet(AttributeSet var1) {
      return new StyleContext.SmallAttributeSet(var1);
   }

   protected MutableAttributeSet createLargeAttributeSet(AttributeSet var1) {
      return new SimpleAttributeSet(var1);
   }

   synchronized void removeUnusedSets() {
      this.attributesPool.size();
   }

   AttributeSet getImmutableUniqueSet() {
      StyleContext.SmallAttributeSet var1 = this.createSmallAttributeSet(this.search);
      WeakReference var2 = (WeakReference)this.attributesPool.get(var1);
      StyleContext.SmallAttributeSet var3;
      if (var2 == null || (var3 = (StyleContext.SmallAttributeSet)var2.get()) == null) {
         var3 = var1;
         this.attributesPool.put(var1, new WeakReference(var1));
      }

      return var3;
   }

   MutableAttributeSet getMutableAttributeSet(AttributeSet var1) {
      return var1 instanceof MutableAttributeSet && var1 != SimpleAttributeSet.EMPTY ? (MutableAttributeSet)var1 : this.createLargeAttributeSet(var1);
   }

   public String toString() {
      this.removeUnusedSets();
      String var1 = "";

      StyleContext.SmallAttributeSet var3;
      for(Iterator var2 = this.attributesPool.keySet().iterator(); var2.hasNext(); var1 = var1 + var3 + "\n") {
         var3 = (StyleContext.SmallAttributeSet)var2.next();
      }

      return var1;
   }

   public void writeAttributes(ObjectOutputStream var1, AttributeSet var2) throws IOException {
      writeAttributeSet(var1, var2);
   }

   public void readAttributes(ObjectInputStream var1, MutableAttributeSet var2) throws ClassNotFoundException, IOException {
      readAttributeSet(var1, var2);
   }

   public static void writeAttributeSet(ObjectOutputStream var0, AttributeSet var1) throws IOException {
      int var2 = var1.getAttributeCount();
      var0.writeInt(var2);
      Enumeration var3 = var1.getAttributeNames();

      while(var3.hasMoreElements()) {
         Object var4 = var3.nextElement();
         Object var5;
         if (var4 instanceof Serializable) {
            var0.writeObject(var4);
         } else {
            var5 = freezeKeyMap.get(var4);
            if (var5 == null) {
               throw new NotSerializableException(var4.getClass().getName() + " is not serializable as a key in an AttributeSet");
            }

            var0.writeObject(var5);
         }

         var5 = var1.getAttribute(var4);
         Object var6 = freezeKeyMap.get(var5);
         if (var5 instanceof Serializable) {
            var0.writeObject(var6 != null ? var6 : var5);
         } else {
            if (var6 == null) {
               throw new NotSerializableException(var5.getClass().getName() + " is not serializable as a value in an AttributeSet");
            }

            var0.writeObject(var6);
         }
      }

   }

   public static void readAttributeSet(ObjectInputStream var0, MutableAttributeSet var1) throws ClassNotFoundException, IOException {
      int var2 = var0.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var0.readObject();
         Object var5 = var0.readObject();
         if (thawKeyMap != null) {
            Object var6 = thawKeyMap.get(var4);
            if (var6 != null) {
               var4 = var6;
            }

            Object var7 = thawKeyMap.get(var5);
            if (var7 != null) {
               var5 = var7;
            }
         }

         var1.addAttribute(var4, var5);
      }

   }

   public static void registerStaticAttributeKey(Object var0) {
      String var1 = var0.getClass().getName() + "." + var0.toString();
      if (freezeKeyMap == null) {
         freezeKeyMap = new Hashtable();
         thawKeyMap = new Hashtable();
      }

      freezeKeyMap.put(var0, var1);
      thawKeyMap.put(var1, var0);
   }

   public static Object getStaticAttribute(Object var0) {
      return thawKeyMap != null && var0 != null ? thawKeyMap.get(var0) : null;
   }

   public static Object getStaticAttributeKey(Object var0) {
      return var0.getClass().getName() + "." + var0.toString();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.removeUnusedSets();
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.fontSearch = new StyleContext.FontKey((String)null, 0, 0);
      this.fontTable = new Hashtable();
      this.search = new SimpleAttributeSet();
      this.attributesPool = Collections.synchronizedMap(new WeakHashMap());
      var1.defaultReadObject();
   }

   static {
      try {
         int var0 = StyleConstants.keys.length;

         for(int var1 = 0; var1 < var0; ++var1) {
            registerStaticAttributeKey(StyleConstants.keys[var1]);
         }
      } catch (Throwable var2) {
         var2.printStackTrace();
      }

   }

   public class NamedStyle implements Style, Serializable {
      protected EventListenerList listenerList;
      protected transient ChangeEvent changeEvent;
      private transient AttributeSet attributes;

      public NamedStyle(String var2, Style var3) {
         this.listenerList = new EventListenerList();
         this.changeEvent = null;
         this.attributes = StyleContext.this.getEmptySet();
         if (var2 != null) {
            this.setName(var2);
         }

         if (var3 != null) {
            this.setResolveParent(var3);
         }

      }

      public NamedStyle(Style var2) {
         this((String)null, var2);
      }

      public NamedStyle() {
         this.listenerList = new EventListenerList();
         this.changeEvent = null;
         this.attributes = StyleContext.this.getEmptySet();
      }

      public String toString() {
         return "NamedStyle:" + this.getName() + " " + this.attributes;
      }

      public String getName() {
         return this.isDefined(StyleConstants.NameAttribute) ? this.getAttribute(StyleConstants.NameAttribute).toString() : null;
      }

      public void setName(String var1) {
         if (var1 != null) {
            this.addAttribute(StyleConstants.NameAttribute, var1);
         }

      }

      public void addChangeListener(ChangeListener var1) {
         this.listenerList.add(ChangeListener.class, var1);
      }

      public void removeChangeListener(ChangeListener var1) {
         this.listenerList.remove(ChangeListener.class, var1);
      }

      public ChangeListener[] getChangeListeners() {
         return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
      }

      protected void fireStateChanged() {
         Object[] var1 = this.listenerList.getListenerList();

         for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
            if (var1[var2] == ChangeListener.class) {
               if (this.changeEvent == null) {
                  this.changeEvent = new ChangeEvent(this);
               }

               ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
            }
         }

      }

      public <T extends EventListener> T[] getListeners(Class<T> var1) {
         return this.listenerList.getListeners(var1);
      }

      public int getAttributeCount() {
         return this.attributes.getAttributeCount();
      }

      public boolean isDefined(Object var1) {
         return this.attributes.isDefined(var1);
      }

      public boolean isEqual(AttributeSet var1) {
         return this.attributes.isEqual(var1);
      }

      public AttributeSet copyAttributes() {
         StyleContext.NamedStyle var1 = StyleContext.this.new NamedStyle();
         var1.attributes = this.attributes.copyAttributes();
         return var1;
      }

      public Object getAttribute(Object var1) {
         return this.attributes.getAttribute(var1);
      }

      public Enumeration<?> getAttributeNames() {
         return this.attributes.getAttributeNames();
      }

      public boolean containsAttribute(Object var1, Object var2) {
         return this.attributes.containsAttribute(var1, var2);
      }

      public boolean containsAttributes(AttributeSet var1) {
         return this.attributes.containsAttributes(var1);
      }

      public AttributeSet getResolveParent() {
         return this.attributes.getResolveParent();
      }

      public void addAttribute(Object var1, Object var2) {
         StyleContext var3 = StyleContext.this;
         this.attributes = var3.addAttribute(this.attributes, var1, var2);
         this.fireStateChanged();
      }

      public void addAttributes(AttributeSet var1) {
         StyleContext var2 = StyleContext.this;
         this.attributes = var2.addAttributes(this.attributes, var1);
         this.fireStateChanged();
      }

      public void removeAttribute(Object var1) {
         StyleContext var2 = StyleContext.this;
         this.attributes = var2.removeAttribute(this.attributes, var1);
         this.fireStateChanged();
      }

      public void removeAttributes(Enumeration<?> var1) {
         StyleContext var2 = StyleContext.this;
         this.attributes = var2.removeAttributes(this.attributes, var1);
         this.fireStateChanged();
      }

      public void removeAttributes(AttributeSet var1) {
         StyleContext var2 = StyleContext.this;
         if (var1 == this) {
            this.attributes = var2.getEmptySet();
         } else {
            this.attributes = var2.removeAttributes(this.attributes, var1);
         }

         this.fireStateChanged();
      }

      public void setResolveParent(AttributeSet var1) {
         if (var1 != null) {
            this.addAttribute(StyleConstants.ResolveAttribute, var1);
         } else {
            this.removeAttribute(StyleConstants.ResolveAttribute);
         }

      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         StyleContext.writeAttributeSet(var1, this.attributes);
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
         this.attributes = SimpleAttributeSet.EMPTY;
         StyleContext.readAttributeSet(var1, this);
      }
   }

   static class FontKey {
      private String family;
      private int style;
      private int size;

      public FontKey(String var1, int var2, int var3) {
         this.setValue(var1, var2, var3);
      }

      public void setValue(String var1, int var2, int var3) {
         this.family = var1 != null ? var1.intern() : null;
         this.style = var2;
         this.size = var3;
      }

      public int hashCode() {
         int var1 = this.family != null ? this.family.hashCode() : 0;
         return var1 ^ this.style ^ this.size;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof StyleContext.FontKey)) {
            return false;
         } else {
            StyleContext.FontKey var2 = (StyleContext.FontKey)var1;
            return this.size == var2.size && this.style == var2.style && this.family == var2.family;
         }
      }
   }

   class KeyBuilder {
      private Vector<Object> keys = new Vector();
      private Vector<Object> data = new Vector();

      public void initialize(AttributeSet var1) {
         if (var1 instanceof StyleContext.SmallAttributeSet) {
            this.initialize(((StyleContext.SmallAttributeSet)var1).attributes);
         } else {
            this.keys.removeAllElements();
            this.data.removeAllElements();
            Enumeration var2 = var1.getAttributeNames();

            while(var2.hasMoreElements()) {
               Object var3 = var2.nextElement();
               this.addAttribute(var3, var1.getAttribute(var3));
            }
         }

      }

      private void initialize(Object[] var1) {
         this.keys.removeAllElements();
         this.data.removeAllElements();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; var3 += 2) {
            this.keys.addElement(var1[var3]);
            this.data.addElement(var1[var3 + 1]);
         }

      }

      public Object[] createTable() {
         int var1 = this.keys.size();
         Object[] var2 = new Object[2 * var1];

         for(int var3 = 0; var3 < var1; ++var3) {
            int var4 = 2 * var3;
            var2[var4] = this.keys.elementAt(var3);
            var2[var4 + 1] = this.data.elementAt(var3);
         }

         return var2;
      }

      int getCount() {
         return this.keys.size();
      }

      public void addAttribute(Object var1, Object var2) {
         this.keys.addElement(var1);
         this.data.addElement(var2);
      }

      public void addAttributes(AttributeSet var1) {
         if (var1 instanceof StyleContext.SmallAttributeSet) {
            Object[] var2 = ((StyleContext.SmallAttributeSet)var1).attributes;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; var4 += 2) {
               this.addAttribute(var2[var4], var2[var4 + 1]);
            }
         } else {
            Enumeration var5 = var1.getAttributeNames();

            while(var5.hasMoreElements()) {
               Object var6 = var5.nextElement();
               this.addAttribute(var6, var1.getAttribute(var6));
            }
         }

      }

      public void removeAttribute(Object var1) {
         int var2 = this.keys.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (this.keys.elementAt(var3).equals(var1)) {
               this.keys.removeElementAt(var3);
               this.data.removeElementAt(var3);
               return;
            }
         }

      }

      public void removeAttributes(Enumeration var1) {
         while(var1.hasMoreElements()) {
            Object var2 = var1.nextElement();
            this.removeAttribute(var2);
         }

      }

      public void removeAttributes(AttributeSet var1) {
         Enumeration var2 = var1.getAttributeNames();

         while(var2.hasMoreElements()) {
            Object var3 = var2.nextElement();
            Object var4 = var1.getAttribute(var3);
            this.removeSearchAttribute(var3, var4);
         }

      }

      private void removeSearchAttribute(Object var1, Object var2) {
         int var3 = this.keys.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            if (this.keys.elementAt(var4).equals(var1)) {
               if (this.data.elementAt(var4).equals(var2)) {
                  this.keys.removeElementAt(var4);
                  this.data.removeElementAt(var4);
               }

               return;
            }
         }

      }
   }

   class KeyEnumeration implements Enumeration<Object> {
      Object[] attr;
      int i;

      KeyEnumeration(Object[] var2) {
         this.attr = var2;
         this.i = 0;
      }

      public boolean hasMoreElements() {
         return this.i < this.attr.length;
      }

      public Object nextElement() {
         if (this.i < this.attr.length) {
            Object var1 = this.attr[this.i];
            this.i += 2;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }
   }

   public class SmallAttributeSet implements AttributeSet {
      Object[] attributes;
      AttributeSet resolveParent;

      public SmallAttributeSet(Object[] var2) {
         this.attributes = var2;
         this.updateResolveParent();
      }

      public SmallAttributeSet(AttributeSet var2) {
         int var3 = var2.getAttributeCount();
         Object[] var4 = new Object[2 * var3];
         Enumeration var5 = var2.getAttributeNames();

         for(int var6 = 0; var5.hasMoreElements(); var6 += 2) {
            var4[var6] = var5.nextElement();
            var4[var6 + 1] = var2.getAttribute(var4[var6]);
         }

         this.attributes = var4;
         this.updateResolveParent();
      }

      private void updateResolveParent() {
         this.resolveParent = null;
         Object[] var1 = this.attributes;

         for(int var2 = 0; var2 < var1.length; var2 += 2) {
            if (var1[var2] == StyleConstants.ResolveAttribute) {
               this.resolveParent = (AttributeSet)var1[var2 + 1];
               break;
            }
         }

      }

      Object getLocalAttribute(Object var1) {
         if (var1 == StyleConstants.ResolveAttribute) {
            return this.resolveParent;
         } else {
            Object[] var2 = this.attributes;

            for(int var3 = 0; var3 < var2.length; var3 += 2) {
               if (var1.equals(var2[var3])) {
                  return var2[var3 + 1];
               }
            }

            return null;
         }
      }

      public String toString() {
         String var1 = "{";
         Object[] var2 = this.attributes;

         for(int var3 = 0; var3 < var2.length; var3 += 2) {
            if (var2[var3 + 1] instanceof AttributeSet) {
               var1 = var1 + var2[var3] + "=AttributeSet,";
            } else {
               var1 = var1 + var2[var3] + "=" + var2[var3 + 1] + ",";
            }
         }

         var1 = var1 + "}";
         return var1;
      }

      public int hashCode() {
         int var1 = 0;
         Object[] var2 = this.attributes;

         for(int var3 = 1; var3 < var2.length; var3 += 2) {
            var1 ^= var2[var3].hashCode();
         }

         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof AttributeSet)) {
            return false;
         } else {
            AttributeSet var2 = (AttributeSet)var1;
            return this.getAttributeCount() == var2.getAttributeCount() && this.containsAttributes(var2);
         }
      }

      public Object clone() {
         return this;
      }

      public int getAttributeCount() {
         return this.attributes.length / 2;
      }

      public boolean isDefined(Object var1) {
         Object[] var2 = this.attributes;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; var4 += 2) {
            if (var1.equals(var2[var4])) {
               return true;
            }
         }

         return false;
      }

      public boolean isEqual(AttributeSet var1) {
         if (var1 instanceof StyleContext.SmallAttributeSet) {
            return var1 == this;
         } else {
            return this.getAttributeCount() == var1.getAttributeCount() && this.containsAttributes(var1);
         }
      }

      public AttributeSet copyAttributes() {
         return this;
      }

      public Object getAttribute(Object var1) {
         Object var2 = this.getLocalAttribute(var1);
         if (var2 == null) {
            AttributeSet var3 = this.getResolveParent();
            if (var3 != null) {
               var2 = var3.getAttribute(var1);
            }
         }

         return var2;
      }

      public Enumeration<?> getAttributeNames() {
         return StyleContext.this.new KeyEnumeration(this.attributes);
      }

      public boolean containsAttribute(Object var1, Object var2) {
         return var2.equals(this.getAttribute(var1));
      }

      public boolean containsAttributes(AttributeSet var1) {
         boolean var2 = true;

         Object var4;
         for(Enumeration var3 = var1.getAttributeNames(); var2 && var3.hasMoreElements(); var2 = var1.getAttribute(var4).equals(this.getAttribute(var4))) {
            var4 = var3.nextElement();
         }

         return var2;
      }

      public AttributeSet getResolveParent() {
         return this.resolveParent;
      }
   }
}
