package javax.naming.event;

import java.util.EventObject;
import javax.naming.Binding;

public class NamingEvent extends EventObject {
   public static final int OBJECT_ADDED = 0;
   public static final int OBJECT_REMOVED = 1;
   public static final int OBJECT_RENAMED = 2;
   public static final int OBJECT_CHANGED = 3;
   protected Object changeInfo;
   protected int type;
   protected Binding oldBinding;
   protected Binding newBinding;
   private static final long serialVersionUID = -7126752885365133499L;

   public NamingEvent(EventContext var1, int var2, Binding var3, Binding var4, Object var5) {
      super(var1);
      this.type = var2;
      this.oldBinding = var4;
      this.newBinding = var3;
      this.changeInfo = var5;
   }

   public int getType() {
      return this.type;
   }

   public EventContext getEventContext() {
      return (EventContext)this.getSource();
   }

   public Binding getOldBinding() {
      return this.oldBinding;
   }

   public Binding getNewBinding() {
      return this.newBinding;
   }

   public Object getChangeInfo() {
      return this.changeInfo;
   }

   public void dispatch(NamingListener var1) {
      switch(this.type) {
      case 0:
         ((NamespaceChangeListener)var1).objectAdded(this);
         break;
      case 1:
         ((NamespaceChangeListener)var1).objectRemoved(this);
         break;
      case 2:
         ((NamespaceChangeListener)var1).objectRenamed(this);
         break;
      case 3:
         ((ObjectChangeListener)var1).objectChanged(this);
      }

   }
}
