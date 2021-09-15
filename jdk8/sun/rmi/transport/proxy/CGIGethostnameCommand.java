package sun.rmi.transport.proxy;

final class CGIGethostnameCommand implements CGICommandHandler {
   public String getName() {
      return "gethostname";
   }

   public void execute(String var1) {
      System.out.println("Status: 200 OK");
      System.out.println("Content-type: application/octet-stream");
      System.out.println("Content-length: " + CGIHandler.ServerName.length());
      System.out.println("");
      System.out.print(CGIHandler.ServerName);
      System.out.flush();
   }
}
