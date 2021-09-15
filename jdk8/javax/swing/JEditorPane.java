package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Caret;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.CompositeView;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class JEditorPane extends JTextComponent {
   private SwingWorker<URL, Object> pageLoader;
   private EditorKit kit;
   private boolean isUserSetEditorKit;
   private Hashtable<String, Object> pageProperties;
   static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
   private Hashtable<String, EditorKit> typeHandlers;
   private static final Object kitRegistryKey = new StringBuffer("JEditorPane.kitRegistry");
   private static final Object kitTypeRegistryKey = new StringBuffer("JEditorPane.kitTypeRegistry");
   private static final Object kitLoaderRegistryKey = new StringBuffer("JEditorPane.kitLoaderRegistry");
   private static final String uiClassID = "EditorPaneUI";
   public static final String W3C_LENGTH_UNITS = "JEditorPane.w3cLengthUnits";
   public static final String HONOR_DISPLAY_PROPERTIES = "JEditorPane.honorDisplayProperties";
   static final Map<String, String> defaultEditorKitMap = new HashMap(0);

   public JEditorPane() {
      this.setFocusCycleRoot(true);
      this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
         public Component getComponentAfter(Container var1, Component var2) {
            if (var1 == JEditorPane.this && (JEditorPane.this.isEditable() || JEditorPane.this.getComponentCount() <= 0)) {
               Container var3 = JEditorPane.this.getFocusCycleRootAncestor();
               return var3 != null ? var3.getFocusTraversalPolicy().getComponentAfter(var3, JEditorPane.this) : null;
            } else {
               return super.getComponentAfter(var1, var2);
            }
         }

         public Component getComponentBefore(Container var1, Component var2) {
            if (var1 == JEditorPane.this && (JEditorPane.this.isEditable() || JEditorPane.this.getComponentCount() <= 0)) {
               Container var3 = JEditorPane.this.getFocusCycleRootAncestor();
               return var3 != null ? var3.getFocusTraversalPolicy().getComponentBefore(var3, JEditorPane.this) : null;
            } else {
               return super.getComponentBefore(var1, var2);
            }
         }

         public Component getDefaultComponent(Container var1) {
            return var1 == JEditorPane.this && (JEditorPane.this.isEditable() || JEditorPane.this.getComponentCount() <= 0) ? null : super.getDefaultComponent(var1);
         }

         protected boolean accept(Component var1) {
            return var1 != JEditorPane.this ? super.accept(var1) : false;
         }
      });
      LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
      LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
   }

   public JEditorPane(URL var1) throws IOException {
      this();
      this.setPage(var1);
   }

   public JEditorPane(String var1) throws IOException {
      this();
      this.setPage(var1);
   }

   public JEditorPane(String var1, String var2) {
      this();
      this.setContentType(var1);
      this.setText(var2);
   }

   public synchronized void addHyperlinkListener(HyperlinkListener var1) {
      this.listenerList.add(HyperlinkListener.class, var1);
   }

   public synchronized void removeHyperlinkListener(HyperlinkListener var1) {
      this.listenerList.remove(HyperlinkListener.class, var1);
   }

   public synchronized HyperlinkListener[] getHyperlinkListeners() {
      return (HyperlinkListener[])this.listenerList.getListeners(HyperlinkListener.class);
   }

   public void fireHyperlinkUpdate(HyperlinkEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == HyperlinkListener.class) {
            ((HyperlinkListener)var2[var3 + 1]).hyperlinkUpdate(var1);
         }
      }

   }

   public void setPage(URL var1) throws IOException {
      if (var1 == null) {
         throw new IOException("invalid url");
      } else {
         URL var2 = this.getPage();
         if (!var1.equals(var2) && var1.getRef() == null) {
            this.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
         }

         boolean var3 = false;
         Object var4 = this.getPostData();
         if (var2 == null || !var2.sameFile(var1) || var4 != null) {
            int var5 = this.getAsynchronousLoadPriority(this.getDocument());
            if (var5 >= 0) {
               if (this.pageLoader != null) {
                  this.pageLoader.cancel(true);
               }

               this.pageLoader = new JEditorPane.PageLoader((Document)null, (InputStream)null, var2, var1);
               this.pageLoader.execute();
               return;
            }

            InputStream var6 = this.getStream(var1);
            if (this.kit != null) {
               Document var7 = this.initializeModel(this.kit, var1);
               var5 = this.getAsynchronousLoadPriority(var7);
               if (var5 >= 0) {
                  this.setDocument(var7);
                  synchronized(this) {
                     this.pageLoader = new JEditorPane.PageLoader(var7, var6, var2, var1);
                     this.pageLoader.execute();
                     return;
                  }
               }

               this.read(var6, var7);
               this.setDocument(var7);
               var3 = true;
            }
         }

         final String var11 = var1.getRef();
         if (var11 != null) {
            if (!var3) {
               this.scrollToReference(var11);
            } else {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     JEditorPane.this.scrollToReference(var11);
                  }
               });
            }

            this.getDocument().putProperty("stream", var1);
         }

         this.firePropertyChange("page", var2, var1);
      }
   }

   private Document initializeModel(EditorKit var1, URL var2) {
      Document var3 = var1.createDefaultDocument();
      if (this.pageProperties != null) {
         Enumeration var4 = this.pageProperties.keys();

         while(var4.hasMoreElements()) {
            String var5 = (String)var4.nextElement();
            var3.putProperty(var5, this.pageProperties.get(var5));
         }

         this.pageProperties.clear();
      }

      if (var3.getProperty("stream") == null) {
         var3.putProperty("stream", var2);
      }

      return var3;
   }

   private int getAsynchronousLoadPriority(Document var1) {
      return var1 instanceof AbstractDocument ? ((AbstractDocument)var1).getAsynchronousLoadPriority() : -1;
   }

   public void read(InputStream var1, Object var2) throws IOException {
      if (var2 instanceof HTMLDocument && this.kit instanceof HTMLEditorKit) {
         HTMLDocument var5 = (HTMLDocument)var2;
         this.setDocument(var5);
         this.read(var1, (Document)var5);
      } else {
         String var3 = (String)this.getClientProperty("charset");
         InputStreamReader var4 = var3 != null ? new InputStreamReader(var1, var3) : new InputStreamReader(var1);
         super.read(var4, var2);
      }

   }

   void read(InputStream var1, Document var2) throws IOException {
      if (!Boolean.TRUE.equals(var2.getProperty("IgnoreCharsetDirective"))) {
         var1 = new BufferedInputStream((InputStream)var1, 10240);
         ((InputStream)var1).mark(10240);
      }

      try {
         String var3 = (String)this.getClientProperty("charset");
         InputStreamReader var12 = var3 != null ? new InputStreamReader((InputStream)var1, var3) : new InputStreamReader((InputStream)var1);
         this.kit.read((Reader)var12, var2, 0);
      } catch (BadLocationException var10) {
         throw new IOException(var10.getMessage());
      } catch (ChangedCharSetException var11) {
         String var4 = var11.getCharSetSpec();
         if (var11.keyEqualsCharSet()) {
            this.putClientProperty("charset", var4);
         } else {
            this.setCharsetFromContentTypeParameters(var4);
         }

         try {
            ((InputStream)var1).reset();
         } catch (IOException var9) {
            ((InputStream)var1).close();
            URL var6 = (URL)var2.getProperty("stream");
            if (var6 == null) {
               throw var11;
            }

            URLConnection var7 = var6.openConnection();
            var1 = var7.getInputStream();
         }

         try {
            var2.remove(0, var2.getLength());
         } catch (BadLocationException var8) {
         }

         var2.putProperty("IgnoreCharsetDirective", true);
         this.read((InputStream)var1, (Document)var2);
      }

   }

   protected InputStream getStream(URL var1) throws IOException {
      final URLConnection var2 = var1.openConnection();
      if (var2 instanceof HttpURLConnection) {
         HttpURLConnection var3 = (HttpURLConnection)var2;
         var3.setInstanceFollowRedirects(false);
         Object var4 = this.getPostData();
         if (var4 != null) {
            this.handlePostData(var3, var4);
         }

         int var5 = var3.getResponseCode();
         boolean var6 = var5 >= 300 && var5 <= 399;
         if (var6) {
            String var7 = var2.getHeaderField("Location");
            if (var7.startsWith("http", 0)) {
               var1 = new URL(var7);
            } else {
               var1 = new URL(var1, var7);
            }

            return this.getStream(var1);
         }
      }

      if (SwingUtilities.isEventDispatchThread()) {
         this.handleConnectionProperties(var2);
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  JEditorPane.this.handleConnectionProperties(var2);
               }
            });
         } catch (InterruptedException var8) {
            throw new RuntimeException(var8);
         } catch (InvocationTargetException var9) {
            throw new RuntimeException(var9);
         }
      }

      return var2.getInputStream();
   }

   private void handleConnectionProperties(URLConnection var1) {
      if (this.pageProperties == null) {
         this.pageProperties = new Hashtable();
      }

      String var2 = var1.getContentType();
      if (var2 != null) {
         this.setContentType(var2);
         this.pageProperties.put("content-type", var2);
      }

      this.pageProperties.put("stream", var1.getURL());
      String var3 = var1.getContentEncoding();
      if (var3 != null) {
         this.pageProperties.put("content-encoding", var3);
      }

   }

   private Object getPostData() {
      return this.getDocument().getProperty("javax.swing.JEditorPane.postdata");
   }

   private void handlePostData(HttpURLConnection var1, Object var2) throws IOException {
      var1.setDoOutput(true);
      DataOutputStream var3 = null;

      try {
         var1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var3 = new DataOutputStream(var1.getOutputStream());
         var3.writeBytes((String)var2);
      } finally {
         if (var3 != null) {
            var3.close();
         }

      }

   }

   public void scrollToReference(String var1) {
      Document var2 = this.getDocument();
      if (var2 instanceof HTMLDocument) {
         HTMLDocument var3 = (HTMLDocument)var2;

         for(HTMLDocument.Iterator var4 = var3.getIterator(HTML.Tag.A); var4.isValid(); var4.next()) {
            AttributeSet var5 = var4.getAttributes();
            String var6 = (String)var5.getAttribute(HTML.Attribute.NAME);
            if (var6 != null && var6.equals(var1)) {
               try {
                  int var7 = var4.getStartOffset();
                  Rectangle var8 = this.modelToView(var7);
                  if (var8 != null) {
                     Rectangle var9 = this.getVisibleRect();
                     var8.height = var9.height;
                     this.scrollRectToVisible(var8);
                     this.setCaretPosition(var7);
                  }
               } catch (BadLocationException var10) {
                  UIManager.getLookAndFeel().provideErrorFeedback(this);
               }
            }
         }
      }

   }

   public URL getPage() {
      return (URL)this.getDocument().getProperty("stream");
   }

   public void setPage(String var1) throws IOException {
      if (var1 == null) {
         throw new IOException("invalid url");
      } else {
         URL var2 = new URL(var1);
         this.setPage(var2);
      }
   }

   public String getUIClassID() {
      return "EditorPaneUI";
   }

   protected EditorKit createDefaultEditorKit() {
      return new JEditorPane.PlainEditorKit();
   }

   public EditorKit getEditorKit() {
      if (this.kit == null) {
         this.kit = this.createDefaultEditorKit();
         this.isUserSetEditorKit = false;
      }

      return this.kit;
   }

   public final String getContentType() {
      return this.kit != null ? this.kit.getContentType() : null;
   }

   public final void setContentType(String var1) {
      int var2 = var1.indexOf(";");
      if (var2 > -1) {
         String var3 = var1.substring(var2);
         var1 = var1.substring(0, var2).trim();
         if (var1.toLowerCase().startsWith("text/")) {
            this.setCharsetFromContentTypeParameters(var3);
         }
      }

      if (this.kit == null || !var1.equals(this.kit.getContentType()) || !this.isUserSetEditorKit) {
         EditorKit var4 = this.getEditorKitForContentType(var1);
         if (var4 != null && var4 != this.kit) {
            this.setEditorKit(var4);
            this.isUserSetEditorKit = false;
         }
      }

   }

   private void setCharsetFromContentTypeParameters(String var1) {
      try {
         int var3 = var1.indexOf(59);
         if (var3 > -1 && var3 < var1.length() - 1) {
            var1 = var1.substring(var3 + 1);
         }

         if (var1.length() > 0) {
            JEditorPane.HeaderParser var4 = new JEditorPane.HeaderParser(var1);
            String var2 = var4.findValue("charset");
            if (var2 != null) {
               this.putClientProperty("charset", var2);
            }
         }
      } catch (IndexOutOfBoundsException var5) {
      } catch (NullPointerException var6) {
      } catch (Exception var7) {
         System.err.println("JEditorPane.getCharsetFromContentTypeParameters failed on: " + var1);
         var7.printStackTrace();
      }

   }

   public void setEditorKit(EditorKit var1) {
      EditorKit var2 = this.kit;
      this.isUserSetEditorKit = true;
      if (var2 != null) {
         var2.deinstall(this);
      }

      this.kit = var1;
      if (this.kit != null) {
         this.kit.install(this);
         this.setDocument(this.kit.createDefaultDocument());
      }

      this.firePropertyChange("editorKit", var2, var1);
   }

   public EditorKit getEditorKitForContentType(String var1) {
      if (this.typeHandlers == null) {
         this.typeHandlers = new Hashtable(3);
      }

      EditorKit var2 = (EditorKit)this.typeHandlers.get(var1);
      if (var2 == null) {
         var2 = createEditorKitForContentType(var1);
         if (var2 != null) {
            this.setEditorKitForContentType(var1, var2);
         }
      }

      if (var2 == null) {
         var2 = this.createDefaultEditorKit();
      }

      return var2;
   }

   public void setEditorKitForContentType(String var1, EditorKit var2) {
      if (this.typeHandlers == null) {
         this.typeHandlers = new Hashtable(3);
      }

      this.typeHandlers.put(var1, var2);
   }

   public void replaceSelection(String var1) {
      if (!this.isEditable()) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      } else {
         EditorKit var2 = this.getEditorKit();
         if (var2 instanceof StyledEditorKit) {
            try {
               Document var3 = this.getDocument();
               Caret var4 = this.getCaret();
               boolean var5 = this.saveComposedText(var4.getDot());
               int var6 = Math.min(var4.getDot(), var4.getMark());
               int var7 = Math.max(var4.getDot(), var4.getMark());
               if (var3 instanceof AbstractDocument) {
                  ((AbstractDocument)var3).replace(var6, var7 - var6, var1, ((StyledEditorKit)var2).getInputAttributes());
               } else {
                  if (var6 != var7) {
                     var3.remove(var6, var7 - var6);
                  }

                  if (var1 != null && var1.length() > 0) {
                     var3.insertString(var6, var1, ((StyledEditorKit)var2).getInputAttributes());
                  }
               }

               if (var5) {
                  this.restoreComposedText();
               }
            } catch (BadLocationException var8) {
               UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
         } else {
            super.replaceSelection(var1);
         }

      }
   }

   public static EditorKit createEditorKitForContentType(String var0) {
      Hashtable var1 = getKitRegisty();
      EditorKit var2 = (EditorKit)var1.get(var0);
      if (var2 == null) {
         String var3 = (String)getKitTypeRegistry().get(var0);
         ClassLoader var4 = (ClassLoader)getKitLoaderRegistry().get(var0);

         try {
            Class var5;
            if (var4 != null) {
               var5 = var4.loadClass(var3);
            } else {
               var5 = Class.forName(var3, true, Thread.currentThread().getContextClassLoader());
            }

            var2 = (EditorKit)var5.newInstance();
            var1.put(var0, var2);
         } catch (Throwable var6) {
            var2 = null;
         }
      }

      return var2 != null ? (EditorKit)var2.clone() : null;
   }

   public static void registerEditorKitForContentType(String var0, String var1) {
      registerEditorKitForContentType(var0, var1, Thread.currentThread().getContextClassLoader());
   }

   public static void registerEditorKitForContentType(String var0, String var1, ClassLoader var2) {
      getKitTypeRegistry().put(var0, var1);
      if (var2 != null) {
         getKitLoaderRegistry().put(var0, var2);
      } else {
         getKitLoaderRegistry().remove(var0);
      }

      getKitRegisty().remove(var0);
   }

   public static String getEditorKitClassNameForContentType(String var0) {
      return (String)getKitTypeRegistry().get(var0);
   }

   private static Hashtable<String, String> getKitTypeRegistry() {
      loadDefaultKitsIfNecessary();
      return (Hashtable)SwingUtilities.appContextGet(kitTypeRegistryKey);
   }

   private static Hashtable<String, ClassLoader> getKitLoaderRegistry() {
      loadDefaultKitsIfNecessary();
      return (Hashtable)SwingUtilities.appContextGet(kitLoaderRegistryKey);
   }

   private static Hashtable<String, EditorKit> getKitRegisty() {
      Hashtable var0 = (Hashtable)SwingUtilities.appContextGet(kitRegistryKey);
      if (var0 == null) {
         var0 = new Hashtable(3);
         SwingUtilities.appContextPut(kitRegistryKey, var0);
      }

      return var0;
   }

   private static void loadDefaultKitsIfNecessary() {
      if (SwingUtilities.appContextGet(kitTypeRegistryKey) == null) {
         synchronized(defaultEditorKitMap) {
            if (defaultEditorKitMap.size() == 0) {
               defaultEditorKitMap.put("text/plain", "javax.swing.JEditorPane$PlainEditorKit");
               defaultEditorKitMap.put("text/html", "javax.swing.text.html.HTMLEditorKit");
               defaultEditorKitMap.put("text/rtf", "javax.swing.text.rtf.RTFEditorKit");
               defaultEditorKitMap.put("application/rtf", "javax.swing.text.rtf.RTFEditorKit");
            }
         }

         Hashtable var0 = new Hashtable();
         SwingUtilities.appContextPut(kitTypeRegistryKey, var0);
         var0 = new Hashtable();
         SwingUtilities.appContextPut(kitLoaderRegistryKey, var0);
         Iterator var1 = defaultEditorKitMap.keySet().iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            registerEditorKitForContentType(var2, (String)defaultEditorKitMap.get(var2));
         }
      }

   }

   public Dimension getPreferredSize() {
      Dimension var1 = super.getPreferredSize();
      Container var2 = SwingUtilities.getUnwrappedParent(this);
      if (var2 instanceof JViewport) {
         JViewport var3 = (JViewport)var2;
         TextUI var4 = this.getUI();
         int var5 = var1.width;
         int var6 = var1.height;
         int var7;
         Dimension var8;
         if (!this.getScrollableTracksViewportWidth()) {
            var7 = var3.getWidth();
            var8 = var4.getMinimumSize(this);
            if (var7 != 0 && var7 < var8.width) {
               var5 = var8.width;
            }
         }

         if (!this.getScrollableTracksViewportHeight()) {
            var7 = var3.getHeight();
            var8 = var4.getMinimumSize(this);
            if (var7 != 0 && var7 < var8.height) {
               var6 = var8.height;
            }
         }

         if (var5 != var1.width || var6 != var1.height) {
            var1 = new Dimension(var5, var6);
         }
      }

      return var1;
   }

   public void setText(String var1) {
      try {
         Document var2 = this.getDocument();
         var2.remove(0, var2.getLength());
         if (var1 == null || var1.equals("")) {
            return;
         }

         StringReader var3 = new StringReader(var1);
         EditorKit var4 = this.getEditorKit();
         var4.read((Reader)var3, var2, 0);
      } catch (IOException var5) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      } catch (BadLocationException var6) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      }

   }

   public String getText() {
      String var1;
      try {
         StringWriter var2 = new StringWriter();
         this.write(var2);
         var1 = var2.toString();
      } catch (IOException var3) {
         var1 = null;
      }

      return var1;
   }

   public boolean getScrollableTracksViewportWidth() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         JViewport var2 = (JViewport)var1;
         TextUI var3 = this.getUI();
         int var4 = var2.getWidth();
         Dimension var5 = var3.getMinimumSize(this);
         Dimension var6 = var3.getMaximumSize(this);
         if (var4 >= var5.width && var4 <= var6.width) {
            return true;
         }
      }

      return false;
   }

   public boolean getScrollableTracksViewportHeight() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         JViewport var2 = (JViewport)var1;
         TextUI var3 = this.getUI();
         int var4 = var2.getHeight();
         Dimension var5 = var3.getMinimumSize(this);
         if (var4 >= var5.height) {
            Dimension var6 = var3.getMaximumSize(this);
            if (var4 <= var6.height) {
               return true;
            }
         }
      }

      return false;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("EditorPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.kit != null ? this.kit.toString() : "";
      String var2 = this.typeHandlers != null ? this.typeHandlers.toString() : "";
      return super.paramString() + ",kit=" + var1 + ",typeHandlers=" + var2;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.getEditorKit() instanceof HTMLEditorKit) {
         if (this.accessibleContext == null || this.accessibleContext.getClass() != JEditorPane.AccessibleJEditorPaneHTML.class) {
            this.accessibleContext = new JEditorPane.AccessibleJEditorPaneHTML();
         }
      } else if (this.accessibleContext == null || this.accessibleContext.getClass() != JEditorPane.AccessibleJEditorPane.class) {
         this.accessibleContext = new JEditorPane.AccessibleJEditorPane();
      }

      return this.accessibleContext;
   }

   static class HeaderParser {
      String raw;
      String[][] tab;

      public HeaderParser(String var1) {
         this.raw = var1;
         this.tab = new String[10][2];
         this.parse();
      }

      private void parse() {
         if (this.raw != null) {
            this.raw = this.raw.trim();
            char[] var1 = this.raw.toCharArray();
            int var2 = 0;
            int var3 = 0;
            int var4 = 0;
            boolean var5 = true;
            boolean var6 = false;
            int var7 = var1.length;

            while(true) {
               while(var3 < var7) {
                  char var8 = var1[var3];
                  if (var8 == '=') {
                     this.tab[var4][0] = (new String(var1, var2, var3 - var2)).toLowerCase();
                     var5 = false;
                     ++var3;
                     var2 = var3;
                  } else if (var8 == '"') {
                     if (!var6) {
                        var6 = true;
                        ++var3;
                        var2 = var3;
                     } else {
                        this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                        var6 = false;

                        do {
                           ++var3;
                        } while(var3 < var7 && (var1[var3] == ' ' || var1[var3] == ','));

                        var5 = true;
                        var2 = var3;
                     }
                  } else if (var8 != ' ' && var8 != ',') {
                     ++var3;
                  } else if (var6) {
                     ++var3;
                  } else {
                     if (var5) {
                        this.tab[var4++][0] = (new String(var1, var2, var3 - var2)).toLowerCase();
                     } else {
                        this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                     }

                     while(var3 < var7 && (var1[var3] == ' ' || var1[var3] == ',')) {
                        ++var3;
                     }

                     var5 = true;
                     var2 = var3;
                  }
               }

               --var3;
               if (var3 > var2) {
                  if (!var5) {
                     if (var1[var3] == '"') {
                        this.tab[var4++][1] = new String(var1, var2, var3 - var2);
                     } else {
                        this.tab[var4++][1] = new String(var1, var2, var3 - var2 + 1);
                     }
                  } else {
                     this.tab[var4][0] = (new String(var1, var2, var3 - var2 + 1)).toLowerCase();
                  }
               } else if (var3 == var2) {
                  if (!var5) {
                     if (var1[var3] == '"') {
                        this.tab[var4++][1] = String.valueOf(var1[var3 - 1]);
                     } else {
                        this.tab[var4++][1] = String.valueOf(var1[var3]);
                     }
                  } else {
                     this.tab[var4][0] = String.valueOf(var1[var3]).toLowerCase();
                  }
               }
               break;
            }
         }

      }

      public String findKey(int var1) {
         return var1 >= 0 && var1 <= 10 ? this.tab[var1][0] : null;
      }

      public String findValue(int var1) {
         return var1 >= 0 && var1 <= 10 ? this.tab[var1][1] : null;
      }

      public String findValue(String var1) {
         return this.findValue(var1, (String)null);
      }

      public String findValue(String var1, String var2) {
         if (var1 == null) {
            return var2;
         } else {
            var1 = var1.toLowerCase();

            for(int var3 = 0; var3 < 10; ++var3) {
               if (this.tab[var3][0] == null) {
                  return var2;
               }

               if (var1.equals(this.tab[var3][0])) {
                  return this.tab[var3][1];
               }
            }

            return var2;
         }
      }

      public int findInt(String var1, int var2) {
         try {
            return Integer.parseInt(this.findValue(var1, String.valueOf(var2)));
         } catch (Throwable var4) {
            return var2;
         }
      }
   }

   static class PlainEditorKit extends DefaultEditorKit implements ViewFactory {
      public ViewFactory getViewFactory() {
         return this;
      }

      public View create(Element var1) {
         Document var2 = var1.getDocument();
         Object var3 = var2.getProperty("i18n");
         return (View)(var3 != null && var3.equals(Boolean.TRUE) ? this.createI18N(var1) : new WrappedPlainView(var1));
      }

      View createI18N(Element var1) {
         String var2 = var1.getName();
         if (var2 != null) {
            if (var2.equals("content")) {
               return new JEditorPane.PlainEditorKit.PlainParagraph(var1);
            }

            if (var2.equals("paragraph")) {
               return new BoxView(var1, 1);
            }
         }

         return null;
      }

      static class PlainParagraph extends ParagraphView {
         PlainParagraph(Element var1) {
            super(var1);
            this.layoutPool = new JEditorPane.PlainEditorKit.PlainParagraph.LogicalView(var1);
            this.layoutPool.setParent(this);
         }

         protected void setPropertiesFromAttributes() {
            Container var1 = this.getContainer();
            if (var1 != null && !var1.getComponentOrientation().isLeftToRight()) {
               this.setJustification(2);
            } else {
               this.setJustification(0);
            }

         }

         public int getFlowSpan(int var1) {
            Container var2 = this.getContainer();
            if (var2 instanceof JTextArea) {
               JTextArea var3 = (JTextArea)var2;
               if (!var3.getLineWrap()) {
                  return Integer.MAX_VALUE;
               }
            }

            return super.getFlowSpan(var1);
         }

         protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
            SizeRequirements var3 = super.calculateMinorAxisRequirements(var1, var2);
            Container var4 = this.getContainer();
            if (var4 instanceof JTextArea) {
               JTextArea var5 = (JTextArea)var4;
               if (!var5.getLineWrap()) {
                  var3.minimum = var3.preferred;
               }
            }

            return var3;
         }

         static class LogicalView extends CompositeView {
            LogicalView(Element var1) {
               super(var1);
            }

            protected int getViewIndexAtPosition(int var1) {
               Element var2 = this.getElement();
               return var2.getElementCount() > 0 ? var2.getElementIndex(var1) : 0;
            }

            protected boolean updateChildren(DocumentEvent.ElementChange var1, DocumentEvent var2, ViewFactory var3) {
               return false;
            }

            protected void loadChildren(ViewFactory var1) {
               Element var2 = this.getElement();
               if (var2.getElementCount() > 0) {
                  super.loadChildren(var1);
               } else {
                  GlyphView var3 = new GlyphView(var2);
                  this.append(var3);
               }

            }

            public float getPreferredSpan(int var1) {
               if (this.getViewCount() != 1) {
                  throw new Error("One child view is assumed.");
               } else {
                  View var2 = this.getView(0);
                  return var2.getPreferredSpan(var1);
               }
            }

            protected void forwardUpdateToView(View var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
               var1.setParent(this);
               super.forwardUpdateToView(var1, var2, var3, var4);
            }

            public void paint(Graphics var1, Shape var2) {
            }

            protected boolean isBefore(int var1, int var2, Rectangle var3) {
               return false;
            }

            protected boolean isAfter(int var1, int var2, Rectangle var3) {
               return false;
            }

            protected View getViewAtPoint(int var1, int var2, Rectangle var3) {
               return null;
            }

            protected void childAllocation(int var1, Rectangle var2) {
            }
         }
      }
   }

   protected class JEditorPaneAccessibleHypertextSupport extends JEditorPane.AccessibleJEditorPane implements AccessibleHypertext {
      JEditorPane.JEditorPaneAccessibleHypertextSupport.LinkVector hyperlinks = new JEditorPane.JEditorPaneAccessibleHypertextSupport.LinkVector();
      boolean linksValid = false;

      private void buildLinkTable() {
         this.hyperlinks.removeAllElements();
         Document var1 = JEditorPane.this.getDocument();
         if (var1 != null) {
            ElementIterator var2 = new ElementIterator(var1);

            Element var3;
            while((var3 = var2.next()) != null) {
               if (var3.isLeaf()) {
                  AttributeSet var4 = var3.getAttributes();
                  AttributeSet var5 = (AttributeSet)var4.getAttribute(HTML.Tag.A);
                  String var6 = var5 != null ? (String)var5.getAttribute(HTML.Attribute.HREF) : null;
                  if (var6 != null) {
                     this.hyperlinks.addElement(new JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink(var3));
                  }
               }
            }
         }

         this.linksValid = true;
      }

      public JEditorPaneAccessibleHypertextSupport() {
         super();
         Document var2 = JEditorPane.this.getDocument();
         if (var2 != null) {
            var2.addDocumentListener(new DocumentListener() {
               public void changedUpdate(DocumentEvent var1) {
                  JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
               }

               public void insertUpdate(DocumentEvent var1) {
                  JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
               }

               public void removeUpdate(DocumentEvent var1) {
                  JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
               }
            });
         }

      }

      public int getLinkCount() {
         if (!this.linksValid) {
            this.buildLinkTable();
         }

         return this.hyperlinks.size();
      }

      public int getLinkIndex(int var1) {
         if (!this.linksValid) {
            this.buildLinkTable();
         }

         Element var2 = null;
         Document var3 = JEditorPane.this.getDocument();
         int var4;
         if (var3 != null) {
            for(var2 = var3.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var4)) {
               var4 = var2.getElementIndex(var1);
            }
         }

         return this.hyperlinks.baseElementIndex(var2);
      }

      public AccessibleHyperlink getLink(int var1) {
         if (!this.linksValid) {
            this.buildLinkTable();
         }

         return var1 >= 0 && var1 < this.hyperlinks.size() ? (AccessibleHyperlink)this.hyperlinks.elementAt(var1) : null;
      }

      public String getLinkText(int var1) {
         if (!this.linksValid) {
            this.buildLinkTable();
         }

         Element var2 = (Element)this.hyperlinks.elementAt(var1);
         if (var2 != null) {
            Document var3 = JEditorPane.this.getDocument();
            if (var3 != null) {
               try {
                  return var3.getText(var2.getStartOffset(), var2.getEndOffset() - var2.getStartOffset());
               } catch (BadLocationException var5) {
                  return null;
               }
            }
         }

         return null;
      }

      private class LinkVector extends Vector<JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink> {
         private LinkVector() {
         }

         public int baseElementIndex(Element var1) {
            for(int var3 = 0; var3 < this.elementCount; ++var3) {
               JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink var2 = (JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink)this.elementAt(var3);
               if (var2.element == var1) {
                  return var3;
               }
            }

            return -1;
         }

         // $FF: synthetic method
         LinkVector(Object var2) {
            this();
         }
      }

      public class HTMLLink extends AccessibleHyperlink {
         Element element;

         public HTMLLink(Element var2) {
            this.element = var2;
         }

         public boolean isValid() {
            return JEditorPaneAccessibleHypertextSupport.this.linksValid;
         }

         public int getAccessibleActionCount() {
            return 1;
         }

         public boolean doAccessibleAction(int var1) {
            if (var1 == 0 && this.isValid()) {
               URL var2 = (URL)this.getAccessibleActionObject(var1);
               if (var2 != null) {
                  HyperlinkEvent var3 = new HyperlinkEvent(JEditorPane.this, HyperlinkEvent.EventType.ACTIVATED, var2);
                  JEditorPane.this.fireHyperlinkUpdate(var3);
                  return true;
               }
            }

            return false;
         }

         public String getAccessibleActionDescription(int var1) {
            if (var1 == 0 && this.isValid()) {
               Document var2 = JEditorPane.this.getDocument();
               if (var2 != null) {
                  try {
                     return var2.getText(this.getStartIndex(), this.getEndIndex() - this.getStartIndex());
                  } catch (BadLocationException var4) {
                     return null;
                  }
               }
            }

            return null;
         }

         public Object getAccessibleActionObject(int var1) {
            if (var1 == 0 && this.isValid()) {
               AttributeSet var2 = this.element.getAttributes();
               AttributeSet var3 = (AttributeSet)var2.getAttribute(HTML.Tag.A);
               String var4 = var3 != null ? (String)var3.getAttribute(HTML.Attribute.HREF) : null;
               if (var4 != null) {
                  URL var5;
                  try {
                     var5 = new URL(JEditorPane.this.getPage(), var4);
                  } catch (MalformedURLException var7) {
                     var5 = null;
                  }

                  return var5;
               }
            }

            return null;
         }

         public Object getAccessibleActionAnchor(int var1) {
            return this.getAccessibleActionDescription(var1);
         }

         public int getStartIndex() {
            return this.element.getStartOffset();
         }

         public int getEndIndex() {
            return this.element.getEndOffset();
         }
      }
   }

   protected class AccessibleJEditorPaneHTML extends JEditorPane.AccessibleJEditorPane {
      private AccessibleContext accessibleContext;

      public AccessibleText getAccessibleText() {
         return JEditorPane.this.new JEditorPaneAccessibleHypertextSupport();
      }

      protected AccessibleJEditorPaneHTML() {
         super();
         HTMLEditorKit var2 = (HTMLEditorKit)JEditorPane.this.getEditorKit();
         this.accessibleContext = var2.getAccessibleContext();
      }

      public int getAccessibleChildrenCount() {
         return this.accessibleContext != null ? this.accessibleContext.getAccessibleChildrenCount() : 0;
      }

      public Accessible getAccessibleChild(int var1) {
         return this.accessibleContext != null ? this.accessibleContext.getAccessibleChild(var1) : null;
      }

      public Accessible getAccessibleAt(Point var1) {
         if (this.accessibleContext != null && var1 != null) {
            try {
               AccessibleComponent var2 = this.accessibleContext.getAccessibleComponent();
               return var2 != null ? var2.getAccessibleAt(var1) : null;
            } catch (IllegalComponentStateException var3) {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   protected class AccessibleJEditorPane extends JTextComponent.AccessibleJTextComponent {
      protected AccessibleJEditorPane() {
         super();
      }

      public String getAccessibleDescription() {
         String var1 = this.accessibleDescription;
         if (var1 == null) {
            var1 = (String)JEditorPane.this.getClientProperty("AccessibleDescription");
         }

         if (var1 == null) {
            var1 = JEditorPane.this.getContentType();
         }

         return var1;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.MULTI_LINE);
         return var1;
      }
   }

   class PageLoader extends SwingWorker<URL, Object> {
      InputStream in;
      URL old;
      URL page;
      Document doc;

      PageLoader(Document var2, InputStream var3, URL var4, URL var5) {
         this.in = var3;
         this.old = var4;
         this.page = var5;
         this.doc = var2;
      }

      protected URL doInBackground() {
         boolean var1 = false;

         try {
            label160: {
               URL var2;
               if (this.in == null) {
                  this.in = JEditorPane.this.getStream(this.page);
                  if (JEditorPane.this.kit == null) {
                     UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
                     var2 = this.old;
                  }
               }

               if (this.doc == null) {
                  URL var3;
                  try {
                     SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                           PageLoader.this.doc = JEditorPane.this.initializeModel(JEditorPane.this.kit, PageLoader.this.page);
                           JEditorPane.this.setDocument(PageLoader.this.doc);
                        }
                     });
                  } catch (InvocationTargetException var10) {
                     UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
                     var3 = this.old;
                  } catch (InterruptedException var11) {
                     UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
                     var3 = this.old;
                  }
               }

               JEditorPane.this.read(this.in, this.doc);
               var2 = (URL)this.doc.getProperty("stream");
               String var14 = var2.getRef();
               if (var14 != null) {
                  Runnable var4 = new Runnable() {
                     public void run() {
                        URL var1 = (URL)JEditorPane.this.getDocument().getProperty("stream");
                        String var2 = var1.getRef();
                        JEditorPane.this.scrollToReference(var2);
                     }
                  };
                  SwingUtilities.invokeLater(var4);
               }

               var1 = true;
            }
         } catch (IOException var12) {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
         } finally {
            if (var1) {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     JEditorPane.this.firePropertyChange("page", PageLoader.this.old, PageLoader.this.page);
                  }
               });
            }

            return var1 ? this.page : this.old;
         }
      }
   }
}
