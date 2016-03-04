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

        private static LowLevelKeyboardProc _proc = HookCallback;
        private static IntPtr _hookID = IntPtr.Zero;
        private static Process self;
        private static System.Timers.Timer countDown = new System.Timers.Timer();
        private static bool typing = false;
        private static Word.Document currDoc;
        private static int selStart = 0;
        private static int selEnd = 0;
        
        // Method for starting hooking keyboard
        public static void startHook()
        {
            _hookID = SetHook(_proc);
            timerInit();
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

        // Callback function for Keyboard hook.
        private static IntPtr HookCallback(
            int nCode, IntPtr wParam, IntPtr lParam)
        {
            if (nCode >= 0 && wParam == (IntPtr)WM_KEYDOWN)
            {
                IntPtr active = GetForegroundWindow();
                if (active.Equals(self.MainWindowHandle))
                {
                    currDoc = ThisAddIn.getCurrDocument();
                    if (!typing)
                    {
                        // Register place for starting typing.
                        typing = true;
                        selStart = currDoc.Application.Selection.Start;
                    }
                    int vkCode = Marshal.ReadInt32(lParam);
                    //Messenger.message(((Keys)vkCode).ToString());
                    resetTimer();
                }
            }
            return CallNextHookEx(_hookID, nCode, wParam, lParam);
        }

        // Timer for checking whether the input is idle.
        private static void timerInit()
        {
            countDown.Elapsed += new ElapsedEventHandler(elapsedAction);
            countDown.Interval = REACTION_TIME;
            countDown.Stop();
            countDown.AutoReset = false;
        }

        // The Action this plugin will take after Reaction Time elapsed.
        private static void elapsedAction(object source, System.Timers.ElapsedEventArgs e)
        {
            typing = false;
            // Check whether is connected to background.
            if (Communicator.isConnected())
            {
                selEnd = currDoc.Application.Selection.End;
                Word.Range rng = currDoc.Range(selStart, selEnd);
                rng.Select();
                Communicator.pushText(rng.Text.ToString(), 0);
            }
            // Stop the timer.
            countDown.Stop();
        }

        // Reset Reaction Time for input detection.
        private static void resetTimer()
        {
            countDown.Interval = REACTION_TIME;
            countDown.Start();
        }

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
        #endregion

    }
}
