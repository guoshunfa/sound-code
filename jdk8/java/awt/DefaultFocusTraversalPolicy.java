package java.awt;

import java.awt.peer.ComponentPeer;

public class DefaultFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy {
   private static final long serialVersionUID = 8876966522510157497L;

   protected boolean accept(Component var1) {
      if (var1.isVisible() && var1.isDisplayable() && var1.isEnabled()) {
         if (!(var1 instanceof Window)) {
            for(Container var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
               if (!var2.isEnabled() && !var2.isLightweight()) {
                  return false;
               }

               if (var2 instanceof Window) {
                  break;
               }
            }
         }

         boolean var4 = var1.isFocusable();
         if (var1.isFocusTraversableOverridden()) {
            return var4;
         } else {
            ComponentPeer var3 = var1.getPeer();
            return var3 != null && var3.isFocusable();
         }
      } else {
         return false;
      }
   }
}
