package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXParseException2;
import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class UnmarshallingContext extends Coordinator implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, XmlVisitor.TextPredictor {
   private static final Logger logger = Logger.getLogger(UnmarshallingContext.class.getName());
   private final UnmarshallingContext.State root;
   private UnmarshallingContext.State current;
   private static final LocatorEx DUMMY_INSTANCE;
   @NotNull
   private LocatorEx locator;
   private Object result;
   private JaxBeanInfo expectedType;
   private IDResolver idResolver;
   private boolean isUnmarshalInProgress;
   private boolean aborted;
   public final UnmarshallerImpl parent;
   private final AssociationMap assoc;
   private boolean isInplaceMode;
   private InfosetScanner scanner;
   private Object currentElement;
   private NamespaceContext environmentNamespaceContext;
   @Nullable
   public ClassResolver classResolver;
   @Nullable
   public ClassLoader classLoader;
   private static volatile int errorsCounter;
   private final Map<Class, UnmarshallingContext.Factory> factories;
   private Patcher[] patchers;
   private int patchersLen;
   private String[] nsBind;
   private int nsLen;
   private Scope[] scopes;
   private int scopeTop;
   private static final Loader DEFAULT_ROOT_LOADER;
   private static final Loader EXPECTED_TYPE_ROOT_LOADER;

   public UnmarshallingContext(UnmarshallerImpl _parent, AssociationMap assoc) {
      this.locator = DUMMY_INSTANCE;
      this.isUnmarshalInProgress = true;
      this.aborted = false;
      this.factories = new HashMap();
      this.patchers = null;
      this.patchersLen = 0;
      this.nsBind = new String[16];
      this.nsLen = 0;
      this.scopes = new Scope[16];
      this.scopeTop = 0;

      for(int i = 0; i < this.scopes.length; ++i) {
         this.scopes[i] = new Scope(this);
      }

      this.parent = _parent;
      this.assoc = assoc;
      this.root = this.current = new UnmarshallingContext.State((UnmarshallingContext.State)null);
   }

   public void reset(InfosetScanner scanner, boolean isInplaceMode, JaxBeanInfo expectedType, IDResolver idResolver) {
      this.scanner = scanner;
      this.isInplaceMode = isInplaceMode;
      this.expectedType = expectedType;
      this.idResolver = idResolver;
   }

   public JAXBContextImpl getJAXBContext() {
      return this.parent.context;
   }

   public UnmarshallingContext.State getCurrentState() {
      return this.current;
   }

   public Loader selectRootLoader(UnmarshallingContext.State state, TagName tag) throws SAXException {
      try {
         Loader l = this.getJAXBContext().selectRootLoader(state, tag);
         if (l != null) {
            return l;
         }

         if (this.classResolver != null) {
            Class<?> clazz = this.classResolver.resolveElementName(tag.uri, tag.local);
            if (clazz != null) {
               JAXBContextImpl enhanced = this.getJAXBContext().createAugmented(clazz);
               JaxBeanInfo<?> bi = enhanced.getBeanInfo(clazz);
               return bi.getLoader(enhanced, true);
            }
         }
      } catch (RuntimeException var7) {
         throw var7;
      } catch (Exception var8) {
         this.handleError(var8);
      }

      return null;
   }

   public void clearStates() {
      UnmarshallingContext.State last;
      for(last = this.current; last.next != null; last = last.next) {
      }

      while(last.prev != null) {
         last.loader = null;
         last.nil = false;
         last.receiver = null;
         last.intercepter = null;
         last.elementDefaultValue = null;
         last.target = null;
         last = last.prev;
         last.next.prev = null;
         last.next = null;
      }

      this.current = last;
   }

   public void setFactories(Object factoryInstances) {
      this.factories.clear();
      if (factoryInstances != null) {
         if (factoryInstances instanceof Object[]) {
            Object[] var2 = (Object[])((Object[])factoryInstances);
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Object factory = var2[var4];
               this.addFactory(factory);
            }
         } else {
            this.addFactory(factoryInstances);
         }

      }
   }

   private void addFactory(Object factory) {
      Method[] var2 = factory.getClass().getMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method m = var2[var4];
         if (m.getName().startsWith("create") && m.getParameterTypes().length <= 0) {
            Class type = m.getReturnType();
            this.factories.put(type, new UnmarshallingContext.Factory(factory, m));
         }
      }

   }

   public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException {
      if (locator != null) {
         this.locator = locator;
      }

      this.environmentNamespaceContext = nsContext;
      this.result = null;
      this.current = this.root;
      this.patchersLen = 0;
      this.aborted = false;
      this.isUnmarshalInProgress = true;
      this.nsLen = 0;
      if (this.expectedType != null) {
         this.root.loader = EXPECTED_TYPE_ROOT_LOADER;
      } else {
         this.root.loader = DEFAULT_ROOT_LOADER;
      }

      this.idResolver.startDocument(this);
   }

   public void startElement(TagName tagName) throws SAXException {
      this.pushCoordinator();

      try {
         this._startElement(tagName);
      } finally {
         this.popCoordinator();
      }

   }

   private void _startElement(TagName tagName) throws SAXException {
      if (this.assoc != null) {
         this.currentElement = this.scanner.getCurrentElement();
      }

      Loader h = this.current.loader;
      this.current.push();
      h.childElement(this.current, tagName);

      assert this.current.loader != null;

      this.current.loader.startElement(this.current, tagName);
   }

   public void text(CharSequence pcdata) throws SAXException {
      this.pushCoordinator();

      try {
         if (this.current.elementDefaultValue != null && ((CharSequence)pcdata).length() == 0) {
            pcdata = this.current.elementDefaultValue;
         }

         this.current.loader.text(this.current, (CharSequence)pcdata);
      } finally {
         this.popCoordinator();
      }

   }

   public final void endElement(TagName tagName) throws SAXException {
      this.pushCoordinator();

      try {
         UnmarshallingContext.State child = this.current;
         child.loader.leaveElement(child, tagName);
         Object target = child.target;
         Receiver recv = child.receiver;
         Intercepter intercepter = child.intercepter;
         child.pop();
         if (intercepter != null) {
            target = intercepter.intercept(this.current, target);
         }

         if (recv != null) {
            recv.receive(this.current, target);
         }
      } finally {
         this.popCoordinator();
      }

   }

   public void endDocument() throws SAXException {
      this.runPatchers();
      this.idResolver.endDocument();
      this.isUnmarshalInProgress = false;
      this.currentElement = null;
      this.locator = DUMMY_INSTANCE;
      this.environmentNamespaceContext = null;

      assert this.root == this.current;

   }

   /** @deprecated */
   @Deprecated
   public boolean expectText() {
      return this.current.loader.expectText;
   }

   /** @deprecated */
   @Deprecated
   public XmlVisitor.TextPredictor getPredictor() {
      return this;
   }

   public UnmarshallingContext getContext() {
      return this;
   }

   public Object getResult() throws UnmarshalException {
      if (this.isUnmarshalInProgress) {
         throw new IllegalStateException();
      } else if (!this.aborted) {
         return this.result;
      } else {
         throw new UnmarshalException((String)null);
      }
   }

   void clearResult() {
      if (this.isUnmarshalInProgress) {
         throw new IllegalStateException();
      } else {
         this.result = null;
      }
   }

   public Object createInstance(Class<?> clazz) throws SAXException {
      if (!this.factories.isEmpty()) {
         UnmarshallingContext.Factory factory = (UnmarshallingContext.Factory)this.factories.get(clazz);
         if (factory != null) {
            return factory.createInstance();
         }
      }

      return ClassFactory.create(clazz);
   }

   public Object createInstance(JaxBeanInfo beanInfo) throws SAXException {
      if (!this.factories.isEmpty()) {
         UnmarshallingContext.Factory factory = (UnmarshallingContext.Factory)this.factories.get(beanInfo.jaxbType);
         if (factory != null) {
            return factory.createInstance();
         }
      }

      try {
         return beanInfo.createInstance(this);
      } catch (IllegalAccessException var3) {
         Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), var3, false);
      } catch (InvocationTargetException var4) {
         Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), var4, false);
      } catch (InstantiationException var5) {
         Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), var5, false);
      }

      return null;
   }

   public void handleEvent(ValidationEvent event, boolean canRecover) throws SAXException {
      ValidationEventHandler eventHandler = this.parent.getEventHandler();
      boolean recover = eventHandler.handleEvent(event);
      if (!recover) {
         this.aborted = true;
      }

      if (!canRecover || !recover) {
         throw new SAXParseException2(event.getMessage(), this.locator, new UnmarshalException(event.getMessage(), event.getLinkedException()));
      }
   }

   public boolean handleEvent(ValidationEvent event) {
      try {
         boolean recover = this.parent.getEventHandler().handleEvent(event);
         if (!recover) {
            this.aborted = true;
         }

         return recover;
      } catch (RuntimeException var3) {
         return false;
      }
   }

   public void handleError(Exception e) throws SAXException {
      this.handleError(e, true);
   }

   public void handleError(Exception e, boolean canRecover) throws SAXException {
      this.handleEvent(new ValidationEventImpl(1, e.getMessage(), this.locator.getLocation(), e), canRecover);
   }

   public void handleError(String msg) {
      this.handleEvent(new ValidationEventImpl(1, msg, this.locator.getLocation()));
   }

   protected ValidationEventLocator getLocation() {
      return this.locator.getLocation();
   }

   public LocatorEx getLocator() {
      return this.locator;
   }

   public void errorUnresolvedIDREF(Object bean, String idref, LocatorEx loc) throws SAXException {
      this.handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(idref), loc.getLocation()), true);
   }

   public void addPatcher(Patcher job) {
      if (this.patchers == null) {
         this.patchers = new Patcher[32];
      }

      if (this.patchers.length == this.patchersLen) {
         Patcher[] buf = new Patcher[this.patchersLen * 2];
         System.arraycopy(this.patchers, 0, buf, 0, this.patchersLen);
         this.patchers = buf;
      }

      this.patchers[this.patchersLen++] = job;
   }

   private void runPatchers() throws SAXException {
      if (this.patchers != null) {
         for(int i = 0; i < this.patchersLen; ++i) {
            this.patchers[i].run();
            this.patchers[i] = null;
         }
      }

   }

   public String addToIdTable(String id) throws SAXException {
      Object o = this.current.target;
      if (o == null) {
         o = this.current.prev.target;
      }

      this.idResolver.bind(id, o);
      return id;
   }

   public Callable getObjectFromId(String id, Class targetType) throws SAXException {
      return this.idResolver.resolve(id, targetType);
   }

   public void startPrefixMapping(String prefix, String uri) {
      if (this.nsBind.length == this.nsLen) {
         String[] n = new String[this.nsLen * 2];
         System.arraycopy(this.nsBind, 0, n, 0, this.nsLen);
         this.nsBind = n;
      }

      this.nsBind[this.nsLen++] = prefix;
      this.nsBind[this.nsLen++] = uri;
   }

   public void endPrefixMapping(String prefix) {
      this.nsLen -= 2;
   }

   private String resolveNamespacePrefix(String prefix) {
      if (prefix.equals("xml")) {
         return "http://www.w3.org/XML/1998/namespace";
      } else {
         for(int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (prefix.equals(this.nsBind[i])) {
               return this.nsBind[i + 1];
            }
         }

         if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getNamespaceURI(prefix.intern());
         } else if (prefix.equals("")) {
            return "";
         } else {
            return null;
         }
      }
   }

   public String[] getNewlyDeclaredPrefixes() {
      return this.getPrefixList(this.current.prev.numNsDecl);
   }

   public String[] getAllDeclaredPrefixes() {
      return this.getPrefixList(0);
   }

   private String[] getPrefixList(int startIndex) {
      int size = (this.current.numNsDecl - startIndex) / 2;
      String[] r = new String[size];

      for(int i = 0; i < r.length; ++i) {
         r[i] = this.nsBind[startIndex + i * 2];
      }

      return r;
   }

   public Iterator<String> getPrefixes(String uri) {
      return Collections.unmodifiableList(this.getAllPrefixesInList(uri)).iterator();
   }

   private List<String> getAllPrefixesInList(String uri) {
      List<String> a = new ArrayList();
      if (uri == null) {
         throw new IllegalArgumentException();
      } else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
         a.add("xml");
         return a;
      } else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
         a.add("xmlns");
         return a;
      } else {
         for(int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
               a.add(this.nsBind[i]);
            }
         }

         return a;
      }
   }

   public String getPrefix(String uri) {
      if (uri == null) {
         throw new IllegalArgumentException();
      } else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
         return "xml";
      } else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
         return "xmlns";
      } else {
         for(int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
               return this.nsBind[i];
            }
         }

         if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getPrefix(uri);
         } else {
            return null;
         }
      }
   }

   public String getNamespaceURI(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException();
      } else {
         return prefix.equals("xmlns") ? "http://www.w3.org/2000/xmlns/" : this.resolveNamespacePrefix(prefix);
      }
   }

   public void startScope(int frameSize) {
      this.scopeTop += frameSize;
      if (this.scopeTop >= this.scopes.length) {
         Scope[] s = new Scope[Math.max(this.scopeTop + 1, this.scopes.length * 2)];
         System.arraycopy(this.scopes, 0, s, 0, this.scopes.length);

         for(int i = this.scopes.length; i < s.length; ++i) {
            s[i] = new Scope(this);
         }

         this.scopes = s;
      }

   }

   public void endScope(int frameSize) throws SAXException {
      while(true) {
         try {
            if (frameSize > 0) {
               this.scopes[this.scopeTop].finish();
               --frameSize;
               --this.scopeTop;
               continue;
            }
         } catch (AccessorException var3) {
            this.handleError((Exception)var3);

            while(frameSize > 0) {
               this.scopes[this.scopeTop--] = new Scope(this);
               --frameSize;
            }
         }

         return;
      }
   }

   public Scope getScope(int offset) {
      return this.scopes[this.scopeTop - offset];
   }

   public void recordInnerPeer(Object innerPeer) {
      if (this.assoc != null) {
         this.assoc.addInner(this.currentElement, innerPeer);
      }

   }

   public Object getInnerPeer() {
      return this.assoc != null && this.isInplaceMode ? this.assoc.getInnerPeer(this.currentElement) : null;
   }

   public void recordOuterPeer(Object outerPeer) {
      if (this.assoc != null) {
         this.assoc.addOuter(this.currentElement, outerPeer);
      }

   }

   public Object getOuterPeer() {
      return this.assoc != null && this.isInplaceMode ? this.assoc.getOuterPeer(this.currentElement) : null;
   }

   public String getXMIMEContentType() {
      Object t = this.current.target;
      return t == null ? null : this.getJAXBContext().getXMIMEContentType(t);
   }

   public static UnmarshallingContext getInstance() {
      return (UnmarshallingContext)Coordinator._getInstance();
   }

   public Collection<QName> getCurrentExpectedElements() {
      this.pushCoordinator();

      Collection var3;
      try {
         UnmarshallingContext.State s = this.getCurrentState();
         Loader l = s.loader;
         var3 = l != null ? l.getExpectedChildElements() : null;
      } finally {
         this.popCoordinator();
      }

      return var3;
   }

   public Collection<QName> getCurrentExpectedAttributes() {
      this.pushCoordinator();

      Collection var3;
      try {
         UnmarshallingContext.State s = this.getCurrentState();
         Loader l = s.loader;
         var3 = l != null ? l.getExpectedAttributes() : null;
      } finally {
         this.popCoordinator();
      }

      return var3;
   }

   public StructureLoader getStructureLoader() {
      return this.current.loader instanceof StructureLoader ? (StructureLoader)this.current.loader : null;
   }

   public boolean shouldErrorBeReported() throws SAXException {
      if (logger.isLoggable(Level.FINEST)) {
         return true;
      } else {
         if (errorsCounter >= 0) {
            --errorsCounter;
            if (errorsCounter == 0) {
               this.handleEvent(new ValidationEventImpl(0, Messages.ERRORS_LIMIT_EXCEEDED.format(), this.getLocator().getLocation(), (Throwable)null), true);
            }
         }

         return errorsCounter >= 0;
      }
   }

   static {
      LocatorImpl loc = new LocatorImpl();
      loc.setPublicId((String)null);
      loc.setSystemId((String)null);
      loc.setLineNumber(-1);
      loc.setColumnNumber(-1);
      DUMMY_INSTANCE = new LocatorExWrapper(loc);
      errorsCounter = 10;
      DEFAULT_ROOT_LOADER = new UnmarshallingContext.DefaultRootLoader();
      EXPECTED_TYPE_ROOT_LOADER = new UnmarshallingContext.ExpectedTypeRootLoader();
   }

   private static final class ExpectedTypeRootLoader extends Loader implements Receiver {
      private ExpectedTypeRootLoader() {
      }

      public void childElement(UnmarshallingContext.State state, TagName ea) {
         UnmarshallingContext context = state.getContext();
         QName qn = new QName(ea.uri, ea.local);
         state.prev.target = new JAXBElement(qn, context.expectedType.jaxbType, (Class)null, (Object)null);
         state.receiver = this;
         state.loader = new XsiNilLoader(context.expectedType.getLoader((JAXBContextImpl)null, true));
      }

      public void receive(UnmarshallingContext.State state, Object o) {
         JAXBElement e = (JAXBElement)state.target;
         e.setValue(o);
         state.getContext().recordOuterPeer(e);
         state.getContext().result = e;
      }

      // $FF: synthetic method
      ExpectedTypeRootLoader(Object x0) {
         this();
      }
   }

   private static final class DefaultRootLoader extends Loader implements Receiver {
      private DefaultRootLoader() {
      }

      public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         Loader loader = state.getContext().selectRootLoader(state, ea);
         if (loader != null) {
            state.loader = loader;
            state.receiver = this;
         } else {
            JaxBeanInfo beanInfo = XsiTypeLoader.parseXsiType(state, ea, (JaxBeanInfo)null);
            if (beanInfo == null) {
               this.reportUnexpectedChildElement(ea, false);
            } else {
               state.loader = beanInfo.getLoader((JAXBContextImpl)null, false);
               state.prev.backup = new JAXBElement(ea.createQName(), Object.class, (Object)null);
               state.receiver = this;
            }
         }
      }

      public Collection<QName> getExpectedChildElements() {
         return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames();
      }

      public void receive(UnmarshallingContext.State state, Object o) {
         if (state.backup != null) {
            ((JAXBElement)state.backup).setValue(o);
            o = state.backup;
         }

         if (state.nil) {
            ((JAXBElement)o).setNil(true);
         }

         state.getContext().result = o;
      }

      // $FF: synthetic method
      DefaultRootLoader(Object x0) {
         this();
      }
   }

   private static class Factory {
      private final Object factorInstance;
      private final Method method;

      public Factory(Object factorInstance, Method method) {
         this.factorInstance = factorInstance;
         this.method = method;
      }

      public Object createInstance() throws SAXException {
         try {
            return this.method.invoke(this.factorInstance);
         } catch (IllegalAccessException var2) {
            UnmarshallingContext.getInstance().handleError(var2, false);
         } catch (InvocationTargetException var3) {
            UnmarshallingContext.getInstance().handleError(var3, false);
         }

         return null;
      }
   }

   public final class State {
      private Loader loader;
      private Receiver receiver;
      private Intercepter intercepter;
      private Object target;
      private Object backup;
      private int numNsDecl;
      private String elementDefaultValue;
      private UnmarshallingContext.State prev;
      private UnmarshallingContext.State next;
      private boolean nil;
      private boolean mixed;

      public UnmarshallingContext getContext() {
         return UnmarshallingContext.this;
      }

      private State(UnmarshallingContext.State prev) {
         this.nil = false;
         this.mixed = false;
         this.prev = prev;
         if (prev != null) {
            prev.next = this;
            if (prev.mixed) {
               this.mixed = true;
            }
         }

      }

      private void push() {
         if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
            UnmarshallingContext.logger.log(Level.FINEST, "State.push");
         }

         if (this.next == null) {
            assert UnmarshallingContext.this.current == this;

            this.next = UnmarshallingContext.this.new State(this);
         }

         this.nil = false;
         UnmarshallingContext.State n = this.next;
         n.numNsDecl = UnmarshallingContext.this.nsLen;
         UnmarshallingContext.this.current = n;
      }

      private void pop() {
         if (UnmarshallingContext.logger.isLoggable(Level.FINEST)) {
            UnmarshallingContext.logger.log(Level.FINEST, "State.pop");
         }

         assert this.prev != null;

         this.loader = null;
         this.nil = false;
         this.mixed = false;
         this.receiver = null;
         this.intercepter = null;
         this.elementDefaultValue = null;
         this.target = null;
         UnmarshallingContext.this.current = this.prev;
         this.next = null;
      }

      public boolean isMixed() {
         return this.mixed;
      }

      public Object getTarget() {
         return this.target;
      }

      public void setLoader(Loader loader) {
         if (loader instanceof StructureLoader) {
            this.mixed = !((StructureLoader)loader).getBeanInfo().hasElementOnlyContentModel();
         }

         this.loader = loader;
      }

      public void setReceiver(Receiver receiver) {
         this.receiver = receiver;
      }

      public UnmarshallingContext.State getPrev() {
         return this.prev;
      }

      public void setIntercepter(Intercepter intercepter) {
         this.intercepter = intercepter;
      }

      public void setBackup(Object backup) {
         this.backup = backup;
      }

      public void setTarget(Object target) {
         this.target = target;
      }

      public Object getBackup() {
         return this.backup;
      }

      public boolean isNil() {
         return this.nil;
      }

      public void setNil(boolean nil) {
         this.nil = nil;
      }

      public Loader getLoader() {
         return this.loader;
      }

      public String getElementDefaultValue() {
         return this.elementDefaultValue;
      }

      public void setElementDefaultValue(String elementDefaultValue) {
         this.elementDefaultValue = elementDefaultValue;
      }

      // $FF: synthetic method
      State(UnmarshallingContext.State x1, Object x2) {
         this(x1);
      }
   }
}
