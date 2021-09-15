package javax.naming.event;

public interface NamespaceChangeListener extends NamingListener {
   void objectAdded(NamingEvent var1);

   void objectRemoved(NamingEvent var1);

   void objectRenamed(NamingEvent var1);
}
