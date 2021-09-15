package javax.naming.event;

import java.util.EventObject;
import javax.naming.NamingException;

public class NamingExceptionEvent extends EventObject {
   private NamingException exception;
   private static final long serialVersionUID = -4877678086134736336L;

   public NamingExceptionEvent(EventContext var1, NamingException var2) {
      super(var1);
      this.exception = var2;
   }

   public NamingException getException() {
      return this.exception;
   }

   public EventContext getEventContext() {
      return (EventContext)this.getSource();
   }

   public void dispatch(NamingListener var1) {
      var1.namingExceptionThrown(this);
   }
}
