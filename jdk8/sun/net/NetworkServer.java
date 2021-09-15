package sun.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer implements Runnable, Cloneable {
   public Socket clientSocket = null;
   private Thread serverInstance;
   private ServerSocket serverSocket;
   public PrintStream clientOutput;
   public InputStream clientInput;

   public void close() throws IOException {
      this.clientSocket.close();
      this.clientSocket = null;
      this.clientInput = null;
      this.clientOutput = null;
   }

   public boolean clientIsOpen() {
      return this.clientSocket != null;
   }

   public final void run() {
      if (this.serverSocket != null) {
         Thread.currentThread().setPriority(10);

         while(true) {
            try {
               Socket var1 = this.serverSocket.accept();
               NetworkServer var2 = (NetworkServer)this.clone();
               var2.serverSocket = null;
               var2.clientSocket = var1;
               (new Thread(var2)).start();
            } catch (Exception var6) {
               System.out.print("Server failure\n");
               var6.printStackTrace();

               try {
                  this.serverSocket.close();
               } catch (IOException var5) {
               }

               System.out.print("cs=" + this.serverSocket + "\n");
               break;
            }
         }
      } else {
         try {
            this.clientOutput = new PrintStream(new BufferedOutputStream(this.clientSocket.getOutputStream()), false, "ISO8859_1");
            this.clientInput = new BufferedInputStream(this.clientSocket.getInputStream());
            this.serviceRequest();
         } catch (Exception var4) {
         }

         try {
            this.close();
         } catch (IOException var3) {
         }
      }

   }

   public final void startServer(int var1) throws IOException {
      this.serverSocket = new ServerSocket(var1, 50);
      this.serverInstance = new Thread(this);
      this.serverInstance.start();
   }

   public void serviceRequest() throws IOException {
      byte[] var1 = new byte[300];
      this.clientOutput.print("Echo server " + this.getClass().getName() + "\n");
      this.clientOutput.flush();

      int var2;
      while((var2 = this.clientInput.read(var1, 0, var1.length)) >= 0) {
         this.clientOutput.write(var1, 0, var2);
      }

   }

   public static void main(String[] var0) {
      try {
         (new NetworkServer()).startServer(8888);
      } catch (IOException var2) {
         System.out.print("Server failed: " + var2 + "\n");
      }

   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
