package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

final class CGIForwardCommand implements CGICommandHandler {
   public String getName() {
      return "forward";
   }

   private String getLine(DataInputStream var1) throws IOException {
      return var1.readLine();
   }

   public void execute(String var1) throws CGIClientException, CGIServerException {
      if (!CGIHandler.RequestMethod.equals("POST")) {
         throw new CGIClientException("can only forward POST requests");
      } else {
         int var2;
         try {
            var2 = Integer.parseInt(var1);
         } catch (NumberFormatException var21) {
            throw new CGIClientException("invalid port number.", var21);
         }

         if (var2 > 0 && var2 <= 65535) {
            if (var2 < 1024) {
               throw new CGIClientException("permission denied for port: " + var2);
            } else {
               Socket var4;
               try {
                  var4 = new Socket(InetAddress.getLocalHost(), var2);
               } catch (IOException var20) {
                  throw new CGIServerException("could not connect to local port", var20);
               }

               DataInputStream var5 = new DataInputStream(System.in);
               byte[] var3 = new byte[CGIHandler.ContentLength];

               try {
                  var5.readFully(var3);
               } catch (EOFException var18) {
                  throw new CGIClientException("unexpected EOF reading request body", var18);
               } catch (IOException var19) {
                  throw new CGIClientException("error reading request body", var19);
               }

               try {
                  DataOutputStream var6 = new DataOutputStream(var4.getOutputStream());
                  var6.writeBytes("POST / HTTP/1.0\r\n");
                  var6.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
                  var6.write(var3);
                  var6.flush();
               } catch (IOException var17) {
                  throw new CGIServerException("error writing to server", var17);
               }

               DataInputStream var22;
               try {
                  var22 = new DataInputStream(var4.getInputStream());
               } catch (IOException var16) {
                  throw new CGIServerException("error reading from server", var16);
               }

               String var7 = "Content-length:".toLowerCase();
               boolean var8 = false;
               int var10 = -1;

               String var9;
               do {
                  try {
                     var9 = this.getLine(var22);
                  } catch (IOException var15) {
                     throw new CGIServerException("error reading from server", var15);
                  }

                  if (var9 == null) {
                     throw new CGIServerException("unexpected EOF reading server response");
                  }

                  if (var9.toLowerCase().startsWith(var7)) {
                     if (var8) {
                        throw new CGIServerException("Multiple Content-length entries found.");
                     }

                     var10 = Integer.parseInt(var9.substring(var7.length()).trim());
                     var8 = true;
                  }
               } while(var9.length() != 0 && var9.charAt(0) != '\r' && var9.charAt(0) != '\n');

               if (var8 && var10 >= 0) {
                  var3 = new byte[var10];

                  try {
                     var22.readFully(var3);
                  } catch (EOFException var13) {
                     throw new CGIServerException("unexpected EOF reading server response", var13);
                  } catch (IOException var14) {
                     throw new CGIServerException("error reading from server", var14);
                  }

                  System.out.println("Status: 200 OK");
                  System.out.println("Content-type: application/octet-stream");
                  System.out.println("");

                  try {
                     System.out.write((byte[])var3);
                  } catch (IOException var12) {
                     throw new CGIServerException("error writing response", var12);
                  }

                  System.out.flush();
               } else {
                  throw new CGIServerException("missing or invalid content length in server response");
               }
            }
         } else {
            throw new CGIClientException("invalid port: " + var2);
         }
      }
   }
}
