package javax.xml.bind;

public interface ValidationEvent {
   int WARNING = 0;
   int ERROR = 1;
   int FATAL_ERROR = 2;

   int getSeverity();

   String getMessage();

   Throwable getLinkedException();

   ValidationEventLocator getLocator();
}
