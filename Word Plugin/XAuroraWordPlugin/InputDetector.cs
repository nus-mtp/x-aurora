using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.Timers;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;
using WordTool = Microsoft.Office.Tools.Word;

namespace XAuroraWordPlugin
{
    public static class InputDetector
    {
        private const int WH_KEYBOARD_LL = 13;
        private const int WM_KEYDOWN = 0x0100;
        private const int REACTION_TIME = 2000;
        private const int HOTKEY_DELAY = 500;
        private const string KEYBOARD_TYPING = "typing";
        private const string KEYBOARD_PREFLISTSHOWED = "prefshowed";
        private const string KEYBOARD_IDLE = "idle";
        private const int REFERENCE_PER_PAGE = 5;

        private static LowLevelKeyboardProc _proc = HookCallback;
        private static IntPtr _hookID = IntPtr.Zero;
        private static Process self;
        private static System.Timers.Timer countDown = new System.Timers.Timer();
        private static System.Timers.Timer hotkeyTimer = new System.Timers.Timer();
        private static Word.Document currDoc;
        private static Word.Range rng;
        private static int selStart = 0;
        private static int selEnd = 0;

        private static int selectedItem = 0;
        private static int wrappedPage = 0;
        private static int maxPage = -1;
        private static List<Preference> tempList;
        private static List<int> sentenceLength = new List<int>();

        private static bool[,] hotkeyFlags;

        private static string inputStatus = KEYBOARD_IDLE;

        public static int[] extendSentenceHotKey;
        public static int[] deleteSentenceHotKey;
        public static int[] extendParaHotKey;
        public static int[] deleteParaHotKey;

        #region keyboard hook
        // Method for starting hooking keyboard
        public static void startHook()
        {
            _hookID = SetHook(_proc);
            timerInit();
            hotkeyTimerInti();
        }

        // Release keyboard hook
        public static void endHook()
        {
            UnhookWindowsHookEx(_hookID);
        }

        // Set hook to current Process.
        private static IntPtr SetHook(LowLevelKeyboardProc proc)
        {
            self = Process.GetCurrentProcess();
            using (ProcessModule curModule = self.MainModule)
            {
                return SetWindowsHookEx(WH_KEYBOARD_LL, proc,
                    GetModuleHandle(curModule.ModuleName), 0);
            }
        }

        // Low-Level System Integer Pointer
        private delegate IntPtr LowLevelKeyboardProc(
            int nCode, IntPtr wParam, IntPtr lParam);
        #endregion

        // Callback function for Keyboard hook.
        private static IntPtr HookCallback(
            int nCode, IntPtr wParam, IntPtr lParam)
        {
            if (nCode >= 0 && wParam == (IntPtr)WM_KEYDOWN)
            {
                IntPtr active = GetForegroundWindow();
                if (!(active.Equals(self.MainWindowHandle) || active.Equals(GUIManager.GUIHandle)))
                {
                    inputStatus = KEYBOARD_IDLE;
                    GUIManager.disappear();
                }

                #region KeyCheck
                else
                {
                    currDoc = ThisAddIn.getCurrDocument();
                    int vkCode = Marshal.ReadInt32(lParam);

                    if (active.Equals(GUIManager.GUIHandle))
                    {
                        hotkeyTimer.Start();

                        int maxCount = tempList.Count - wrappedPage * REFERENCE_PER_PAGE - 1;

                        if (vkCode == KeyboardMapping.Keys["1"])
                        {
                            selectedItem = Math.Min(0, maxCount);
                        }
                        else if (vkCode == KeyboardMapping.Keys["2"])
                        {
                            selectedItem = Math.Min(1, maxCount);
                        }
                        else if (vkCode == KeyboardMapping.Keys["3"])
                        {
                            selectedItem = Math.Min(2, maxCount);
                        }
                        else if (vkCode == KeyboardMapping.Keys["4"])
                        {
                            selectedItem = Math.Min(3, maxCount);
                        }
                        else if (vkCode == KeyboardMapping.Keys["5"])
                        {
                            selectedItem = Math.Min(4, maxCount);
                        }
                        else if (vkCode == KeyboardMapping.Keys["VK_LEFT"])
                        {
                            if (wrappedPage > 0) wrappedPage -= 1;
                            selectedItem = 0;
                        }
                        else if (vkCode == KeyboardMapping.Keys["VK_RIGHT"])
                        {
                            if (wrappedPage < maxPage)
                            {
                                wrappedPage += 1;
                                selectedItem = 0;
                            }
                            else selectedItem = maxCount;
                        }

                        GUIManager.chooseItem(selectedItem, wrappedPage);
                    }
                    

                    if (inputStatus == KEYBOARD_IDLE)
                    {
                        // Register place for starting typing.
                        inputStatus = KEYBOARD_TYPING;
                        Messenger.message("User is Typing!");
                        selStart = currDoc.Application.Selection.End;
                        
                        //start Timer
                        resetTimer();
                    }
                    else if (inputStatus == KEYBOARD_TYPING)
                    {
                        resetTimer();
                    }
                    else if (inputStatus == KEYBOARD_PREFLISTSHOWED)
                    {
                        if (checkHotKey(vkCode, extendSentenceHotKey, 1))
                        {
                            string extending = tempList[wrappedPage * REFERENCE_PER_PAGE + selectedItem].getContent();

                            sentenceLength.Add(extending.Length);
                            int tempPos = selEnd;

                            // Print string
                            currDoc.Application.Selection.Start = selEnd;
                            currDoc.Application.Selection.End = selEnd;
                            currDoc.Application.Selection.TypeText(extending);

                            // Move cursor
                            selEnd = tempPos + extending.Length;
                            selStart = tempPos;
                            rng = currDoc.Range(selStart, selEnd);
                            rng.Select();

                            Messenger.message("Sentence Extended!");
                        }
                        else if (checkHotKey(vkCode, deleteSentenceHotKey, 4))
                        {
                            if (sentenceLength.Any())
                            {
                                int lastAdded = sentenceLength.Count - 1;
                                selStart = selEnd - sentenceLength[lastAdded];
                                rng = currDoc.Range(selStart, selEnd);
                                rng.Delete();
                                sentenceLength.RemoveAt(lastAdded);

                                if (sentenceLength.Any())
                                {
                                    lastAdded = sentenceLength.Count - 1;
                                    selEnd = selStart;
                                    selStart = selEnd - sentenceLength[lastAdded];
                                    rng = currDoc.Range(selStart, selEnd);
                                    rng.Select();
                                }
                                else
                                {
                                    selEnd = selStart;
                                    rng = currDoc.Range(selStart, selEnd);
                                    rng.Select();
                                }
                                
                                Messenger.message("Sentence Reverted!");
                            }
                            else Messenger.message("Nothing extended yet!");
                        }
                        else if (vkCode == KeyboardMapping.Keys["SPACE"])
                        {
                            Messenger.message("KeyboardStatRestored!\n");

                            selStart = selEnd;
                            rng = currDoc.Range(selStart, selEnd);
                            rng.Select();
                            
                            GUIManager.disappear();
                            inputStatus = KEYBOARD_TYPING;
                            sentenceLength = new List<int>();
                        }
                    }
                }
                #endregion
            }
            return CallNextHookEx(_hookID, nCode, wParam, lParam);
        }
        

        #region User type timer
        // Timer for checking whether the input is idle.
        private static void resetHotkeyFlags()
        {
            hotkeyFlags = new bool[6,3];
        }

        private static void timerInit()
        {
            countDown.Elapsed += new ElapsedEventHandler(stoppedType);
            countDown.Interval = REACTION_TIME;
            countDown.Stop();
            countDown.AutoReset = false;
            resetHotkeyFlags();
        }

        // The Action this plugin will take after Reaction Time elapsed.
        private static void stoppedType(object source, System.Timers.ElapsedEventArgs e)
        {
            // Check whether is connected to background.
            if (Communicator.isConnected() && (inputStatus == KEYBOARD_TYPING))
            {
                selEnd = currDoc.Application.Selection.End;
                if (selEnd > selStart)
                {
                    rng = currDoc.Range(selStart, selEnd);
                    rng.Select();
                    GUIManager.display();
                    inputStatus = KEYBOARD_PREFLISTSHOWED;
                    Communicator.pushText(rng.Text.ToString(), 0);
                    tempList = Communicator.getResult();
                    GUIManager.passList(tempList);
                    maxPage = tempList.Count / REFERENCE_PER_PAGE;
                    GUIManager.chooseItem(0, 0);
                    countDown.Stop();
                }
                else
                {
                    selStart = selEnd;
                    resetTimer();
                }
            }
        }

        // Reset Reaction Time for input detection.
        private static void resetTimer()
        {
            countDown.Interval = REACTION_TIME;
            countDown.Start();
        }
        #endregion

        #region Hotkey timer and check
        private static void hotkeyTimerInti()
        {
            hotkeyTimer.Elapsed += new ElapsedEventHandler(hotkeyExpire);
            hotkeyTimer.Interval = HOTKEY_DELAY;
            hotkeyTimer.Stop();
            hotkeyTimer.AutoReset = false;
        }

        private static void resetHotkeyTimer()
        {
            hotkeyTimer.Interval = HOTKEY_DELAY;
            hotkeyTimer.Start();
        }

        private static void hotkeyExpire(object source, System.Timers.ElapsedEventArgs e)
        {
            resetHotkeyFlags();
            hotkeyTimer.Interval = HOTKEY_DELAY;
            hotkeyTimer.Stop();
        }

        // Check simultaneous pressed keys.
        private static Boolean checkHotKey(int keyPressed, int[] hotKey, int order)
        {
            int param1 = hotKey[0];
            int param2 = hotKey[1];
            int param3 = hotKey[2];

            if (hotkeyTimer.Enabled)
            {
                if (keyPressed == param1)
                {
                    hotkeyFlags[order, 0] = true;
                    resetHotkeyTimer();
                }
                else if (keyPressed == param2)
                {
                    hotkeyFlags[order, 1] = true;
                    resetHotkeyTimer();
                }
                else if (keyPressed == param3)
                {
                    hotkeyFlags[order, 2] = true;
                    resetHotkeyTimer();
                }
            }


            if (param3 == -1)
            {
                if (hotkeyFlags[order,0] && hotkeyFlags[order,1])
                {
                    resetHotkeyFlags();
                    return true;
                }
                else return false;
            }
            else if (hotkeyFlags[order, 0] && hotkeyFlags[order, 1] && hotkeyFlags[order,2])
            {
                resetHotkeyFlags();
                return true;
            }
            else return false;
        }

        #endregion

        // DLLs import required for low-level keyboard detection.
        #region DLLs
        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr SetWindowsHookEx(int idHook,
            LowLevelKeyboardProc lpfn, IntPtr hMod, uint dwThreadId);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool UnhookWindowsHookEx(IntPtr hhk);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr CallNextHookEx(IntPtr hhk, int nCode,
            IntPtr wParam, IntPtr lParam);

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr GetModuleHandle(string lpModuleName);

        [DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll", CharSet = CharSet.Auto)]
        private static extern ushort GetAsyncKeyState(int vKey);
        #endregion

    }
}
