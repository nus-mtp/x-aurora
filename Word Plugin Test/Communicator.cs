using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Test_Word_Plugin
{
    public class Communicator
    {
        public void connect()
        {
            // Data buffer for incoming data.
            byte[] bytes = new byte[1024];

            // Connect to a remote device.
            try
            {
                IPEndPoint backgroundSvr = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 23333);

                // Create a TCP/IP  socket.
                Socket sender = new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream, ProtocolType.Tcp);

                // Connect the socket to the remote endpoint. Catch any errors.
                try
                {
                    sender.Connect(backgroundSvr);
                    // Encode the data string into a byte array.
                    string data = "request to connect";
                    data = data.Length + "\n" + data;
                    byte[] msg = Encoding.UTF8.GetBytes(data);

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(bytes);
                    Messenger.message(Encoding.UTF8.GetString(bytes, 0, bytesRec));

                    // Release the socket.
                    sender.Shutdown(SocketShutdown.Both);
                    sender.Close();

                }
                catch (ArgumentNullException ane)
                {
                    Messenger.message("ArgumentNullException :" + ane.ToString());
                }
                catch (SocketException)
                {
                    Messenger.message("SocketException : Cannot Connect to Background");
                }
                catch (Exception e)
                {
                    Messenger.message("Unexpected exception :" + e.ToString());
                }

            }
            catch (Exception e)
            {
                Messenger.message(e.ToString());
            }
        }
    }
}
