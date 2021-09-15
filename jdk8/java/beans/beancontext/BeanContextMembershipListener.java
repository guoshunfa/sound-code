package java.beans.beancontext;

import java.util.EventListener;

public interface BeanContextMembershipListener extends EventListener {
   void childrenAdded(BeanContextMembershipEvent var1);

   void childrenRemoved(BeanContextMembershipEvent var1);
}
