﻿using System;
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
        const int REQUEST_FOR_CONTINUE = 133;

        const int ALL_OK = 200;
        const int RECEIVED = 151;
        const int HOT_KEY = 171;
        const int PREFERENCE_LIST = 172;
        const int CONTINUE_LIST = 173;

        const int UNCONNECTED = 0;
        const int REQUESTING_HOT_KEY = 1;
        const int CONNECTED = 2;

        const int MAX_LENGTH = 200000;
        const int MAX_RETRY_TIME = 3;

        static Boolean isHotkeyGet = false;
        static int serverStat = REQUESTING_HOT_KEY;

        private static List<Preference> result = new List<Preference>();

        // Make Connection with background system.
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
                    // According to serverStat, generate message to send.
                    switch (serverStat)
                    {
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
                    // Encode with UTF8.
                    byte[] msg = Encoding.UTF8.GetBytes(data);

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(bytes);
                    string res = Encoding.UTF8.GetString(bytes, 0, bytesRec).Trim();
                    processResponse(res);
                    //if (result != "") Messenger.message(result);

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

        // Send text for Preference List.
        public static void pushText(String content, int retry)
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
                    data = REQUEST_FOR_PREFERENCE.ToString() + "\n" + content;
                    data = data.Length + "\n" + data;
                    byte[] msg = Encoding.UTF8.GetBytes(data);

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msg);

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(bytes);
                    string res = Encoding.UTF8.GetString(bytes, 0, bytesRec).Trim();
                    processResponse(res);

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
                    if (retry < MAX_RETRY_TIME) pushText(content, retry + 1);
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

        // Process response by background system.
        private static void processResponse(string res)
        {
            string[] seperator = new string[] { "\n" };
            string[] parts = res.Split(seperator, StringSplitOptions.None);
            // Extract Communication code.
            int commCode = Int32.Parse(parts[0]);
            // According to Communication code, do actions.
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
                        string[] newSep = new string[] {"%"};
                        for (int i = 1; i < parts.Length; i++)
                        {
                            String[] temp = parts[i].Split(newSep, StringSplitOptions.None);
                            int[] intRes = new int[3];
                            for (int j = 0; j < temp.Length; j++)
                            {
                                temp[j] = temp[j].Substring(2);
                                intRes[j] = Convert.ToInt32(temp[j], 16);
                            }
                            if (temp.Length < 3) intRes[2] = -1;

                            switch (i)
                            {
                                case 1:
                                    {
                                        InputDetector.extendSentenceHotKey = intRes;
                                        break;
                                    }
                                case 2:
                                    {
                                        InputDetector.deleteSentenceHotKey = intRes;
                                        break;
                                    }
                                case 3:
                                    {
                                        InputDetector.extendParaHotKey = intRes;
                                        break;
                                    }
                                case 4:
                                    {
                                        InputDetector.deleteParaHotKey = intRes;
                                        break;
                                    }
                                default:
                                    {
                                        break;
                                    }
                            }

                            Messenger.message(intRes[0].ToString() + " " + intRes[1].ToString() + " " + intRes[2].ToString());
                        }
                        break;
                    }
                case PREFERENCE_LIST:
                    {
                        result = new List<Preference>();
                        for (int i = 1; i < parts.Length; i++)
                        {
                            Preference tempPref = new Preference();
                            tempPref.setID(i);
                            tempPref.setContent(parts[i]);
                            result.Add(tempPref) ;
                        }
                        break;
                    }
                default:
                    {
                        serverStat = UNCONNECTED;
                        break;
                    }
            };
        }

        // Check for whether the plugin is connected to background system.
        public static bool isConnected()
        {
            return serverStat.Equals(CONNECTED);
        }  

        public static List<Preference> getResult(){
            return result;
        }
    }
}
