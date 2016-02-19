using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XAuroraWordPlugin
{
    public static class Communicator
    {
        const int CONNECTION_REQUEST = 101;
        const int CONNECTION_REQUEST_WITH_HOT_KEY = 131;
        const int REQUEST_FOR_PREFERENCE = 132;

        const int ALL_OK = 200;
        const int RECEIVED = 151;
        const int HOT_KEY = 171;
        const int PREFERENCE_LIST = 172;

        const int UNCONNECTED = 0;
        const int REQUESTING_HOT_KEY = 1;
        const int CONNECTED = 2;

        const int MAX_LENGTH = 200000;

        static Boolean isHotkeyGet = false;
        static int serverStat = REQUESTING_HOT_KEY;

        public static void connect()
        {
            // Data buffer for incoming data.
            byte[] bytes = new byte[MAX_LENGTH];

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
                    string data = "";
                    switch (serverStat){
                        case UNCONNECTED:
                            {
                                if (isHotkeyGet)
                                    data = CONNECTION_REQUEST.ToString();
                                else data = CONNECTION_REQUEST_WITH_HOT_KEY.ToString();
                                break;
                            }
                        case REQUESTING_HOT_KEY:
                            {
                                data = CONNECTION_REQUEST_WITH_HOT_KEY.ToString();
                                break;
                            }
                        case CONNECTED:
                            {
                                data = CONNECTION_REQUEST.ToString();
                                break;
                            }
                        default:
                            {
                                break;
                            }
                    };
                    data = data.Length + "\n" + data;
                    byte[] msg = Encoding.UTF8.GetBytes(data);

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(bytes);
                    string res = Encoding.UTF8.GetString(bytes, 0, bytesRec).Trim();
                    string result = processResponse(res);
                    if (result!="") Messenger.message(result);

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
                    serverStat = REQUESTING_HOT_KEY;
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

        public static void pushText(String content)
        {
            // Data buffer for incoming data.
            byte[] bytes = new byte[MAX_LENGTH];

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
                    string data = "";
                    switch (serverStat)
                    {
                        //case UNCONNECTED:
                        //    {
                        //        if (isHotkeyGet)
                        //            data = CONNECTION_REQUEST.ToString();
                        //        else data = CONNECTION_REQUEST_WITH_HOT_KEY.ToString();
                        //        break;
                        //    }
                        //case REQUESTING_HOT_KEY:
                        //    {
                        //        data = CONNECTION_REQUEST_WITH_HOT_KEY.ToString();
                        //        break;
                        //    }
                        case CONNECTED:
                            {
                                data = REQUEST_FOR_PREFERENCE.ToString() + content;
                                break;
                            }
                        default:
                            {
                                break;
                            }
                    };
                    data = data.Length + "\n" + data;
                    byte[] msg = Encoding.UTF8.GetBytes(data);

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(bytes);
                    string res = Encoding.UTF8.GetString(bytes, 0, bytesRec).Trim();
                    string result = processResponse(res);
                    if (result != "") Messenger.message(result);

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
                    serverStat = REQUESTING_HOT_KEY;
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

        private static string processResponse(string res)
        {
            string[] seperator = new string[] {"\n"};
            string[] parts = res.Split(seperator, StringSplitOptions.None);
            int commCode = Int32.Parse(parts[0]);
            string result = "";
            switch (commCode)
            {
                case ALL_OK:
                    {
                        break;
                    }
                case RECEIVED:
                    {
                        break;
                    }
                case HOT_KEY: 
                    {
                        serverStat = CONNECTED;
                        isHotkeyGet = true;
                        for (int i = 0; i < parts.Length; i++)
                        {
                            Messenger.message(parts[i]+"\n");
                        }
                        // update hotkey;
                        break;
                    }
                case PREFERENCE_LIST:
                    {
                        for (int i = 1;i<parts.Length;i++){
                            result = result + parts[i] + "\n";
                        }
                        Messenger.message(result);
                        break;
                    }
                default:
                    {
                        serverStat = UNCONNECTED;
                        break;
                    }
            };
            return result;
        }
    }
}
