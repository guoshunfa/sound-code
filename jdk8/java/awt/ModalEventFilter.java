package java.awt;

import sun.awt.AppContext;
import sun.awt.ModalExclude;

abstract class ModalEventFilter implements EventFilter {
   protected Dialog modalDialog;
   protected boolean disabled;

   protected ModalEventFilter(Dialog var1) {
      this.modalDialog = var1;
      this.disabled = false;
   }

   Dialog getModalDialog() {
      return this.modalDialog;
   }

   public EventFilter.FilterAction acceptEvent(AWTEvent var1) {
      if (!this.disabled && this.modalDialog.isVisible()) {
         int var2 = var1.getID();
         if (var2 >= 500 && var2 <= 507 || var2 >= 1001 && var2 <= 1001 || var2 == 201) {
            Object var3 = var1.getSource();
            if (!(var3 instanceof ModalExclude) && var3 instanceof Component) {
               Object var4;
               for(var4 = (Component)var3; var4 != null && !(var4 instanceof Window); var4 = ((Component)var4).getParent_NoClientCode()) {
               }

               if (var4 != null) {
                  return this.acceptWindow((Window)var4);
               }
            }
         }

         return EventFilter.FilterAction.ACCEPT;
      } else {
         return EventFilter.FilterAction.ACCEPT;
      }
   }

   protected abstract EventFilter.FilterAction acceptWindow(Window var1);

   void disable() {
      this.disabled = true;
   }

   int compareTo(ModalEventFilter var1) {
      Dialog var2 = var1.getModalDialog();

      Object var3;
      for(var3 = this.modalDialog; var3 != null; var3 = ((Component)var3).getParent_NoClientCode()) {
         if (var3 == var2) {
            return 1;
         }
      }

      for(var3 = var2; var3 != null; var3 = ((Component)var3).getParent_NoClientCode()) {
         if (var3 == this.modalDialog) {
            return -1;
         }
      }

      Dialog var4;
      for(var4 = this.modalDialog.getModalBlocker(); var4 != null; var4 = var4.getModalBlocker()) {
         if (var4 == var2) {
            return -1;
         }
      }

      for(var4 = var2.getModalBlocker(); var4 != null; var4 = var4.getModalBlocker()) {
         if (var4 == this.modalDialog) {
            return 1;
         }
      }

      return this.modalDialog.getModalityType().compareTo(var2.getModalityType());
   }

   static ModalEventFilter createFilterForDialog(Dialog var0) {
      switch(var0.getModalityType()) {
      case DOCUMENT_MODAL:
         return new ModalEventFilter.DocumentModalEventFilter(var0);
      case APPLICATION_MODAL:
         return new ModalEventFilter.ApplicationModalEventFilter(var0);
      case TOOLKIT_MODAL:
         return new ModalEventFilter.ToolkitModalEventFilter(var0);
      default:
         return null;
      }
   }

   private static class DocumentModalEventFilter extends ModalEventFilter {
      private Window documentRoot;

      DocumentModalEventFilter(Dialog var1) {
         super(var1);
         this.documentRoot = var1.getDocumentRoot();
      }

      protected EventFilter.FilterAction acceptWindow(Window var1) {
         if (var1.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
            for(Window var2 = this.modalDialog.getOwner(); var2 != null; var2 = var2.getOwner()) {
               if (var2 == var1) {
                  return EventFilter.FilterAction.REJECT;
               }
            }

            return EventFilter.FilterAction.ACCEPT;
         } else {
            while(var1 != null) {
               if (var1 == this.modalDialog) {
                  return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
               }

               if (var1 == this.documentRoot) {
                  return EventFilter.FilterAction.REJECT;
               }

               var1 = var1.getOwner();
            }

            return EventFilter.FilterAction.ACCEPT;
         }
      }
   }

   private static class ApplicationModalEventFilter extends ModalEventFilter {
      private AppContext appContext;

      ApplicationModalEventFilter(Dialog var1) {
         super(var1);
         this.appContext = var1.appContext;
      }

      protected EventFilter.FilterAction acceptWindow(Window var1) {
         if (var1.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
            return EventFilter.FilterAction.ACCEPT;
         } else if (var1.appContext == this.appContext) {
            while(var1 != null) {
               if (var1 == this.modalDialog) {
                  return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
               }

               var1 = var1.getOwner();
            }

            return EventFilter.FilterAction.REJECT;
         } else {
            return EventFilter.FilterAction.ACCEPT;
         }
      }
   }

   private static class ToolkitModalEventFilter extends ModalEventFilter {
      private AppContext appContext;

      ToolkitModalEventFilter(Dialog var1) {
         super(var1);
         this.appContext = var1.appContext;
      }

      protected EventFilter.FilterAction acceptWindow(Window var1) {
         if (var1.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)) {
            return EventFilter.FilterAction.ACCEPT;
         } else if (var1.appContext != this.appContext) {
            return EventFilter.FilterAction.REJECT;
         } else {
            while(var1 != null) {
               if (var1 == this.modalDialog) {
                  return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
               }

               var1 = var1.getOwner();
            }

            return EventFilter.FilterAction.REJECT;
         }
      }
   }
}
