using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Threading;
using System.Threading;

namespace XAuroraWordPlugin
{
    public static class GUIManager
    {
        public static PreferenceOutline prefOutline;
        public static IntPtr GUIHandle;

        private static Dispatcher _dispatcher;
        public static Dispatcher Dispatcher
        {
            get { return _dispatcher; }
        }

        public struct RECT
        {
            public int left;
            public int top;
            public int right;
            public int bottom;
        }

        public struct GUITHREADINFO
        {
            public uint cbSize;
            public uint flags;
            public IntPtr hwndActive;
            public IntPtr hwndFocus;
            public IntPtr hwndCapture;
            public IntPtr hwndMenuOwner;
            public IntPtr hwndMoveSize;
            public IntPtr hwndCaret;
            public RECT rcCaret;
        }

        public static void guiManagerStart()
        {
            //Thread oThread = new Thread(new ThreadStart(initialize));
            initialize();
        }

        public static void initialize()
        {
            _dispatcher = Dispatcher.CurrentDispatcher;
            prefOutline = new PreferenceOutline();
            GUIHandle = prefOutline.Handle;
        }

        public static Point getCaretPosition()
        {
            Point caretPos = new Point();

            GUITHREADINFO tempInfo = new GUITHREADINFO();
            tempInfo.cbSize = (uint)Marshal.SizeOf(tempInfo);
            GetGUIThreadInfo(0, out tempInfo);

            if (tempInfo.rcCaret.left != -1)
            {
            caretPos.X = (int)tempInfo.rcCaret.left;
            caretPos.Y = (int)tempInfo.rcCaret.bottom;

            ClientToScreen(tempInfo.hwndCaret, out caretPos);
            }

            return caretPos;
        }


        public static void display()
        {
            GUIManager.Dispatcher.Invoke(new showSuggestionDelegate(showSuggestion), new Object[] {});
        }
        public static void disappear()
        {
            GUIManager.Dispatcher.Invoke(new hideSuggestionDelegate(hideSuggestion), new Object[] { });
        }
        public static void passList(List<Preference> res)
        {
            GUIManager.Dispatcher.Invoke(new setPrefListDelegate(setPrefList), new Object[] { res });
        }

        public static void chooseItem(int no, int page)
        {
            GUIManager.Dispatcher.Invoke(new selectItemDelegate(selectItem), new Object[] { no, page });
        }


        private delegate void setPrefListDelegate(List<Preference> res);
        private static void setPrefList(List<Preference> res)
        {
            prefOutline.PrefList.Items.Clear();
            prefOutline.prefList = res;
        }
        private delegate void selectItemDelegate(int no, int page);
        private static void selectItem(int no, int page)
        {
            prefOutline.selectItem(no, page);
            prefOutline.updateDrawList();
        }

        private delegate void showSuggestionDelegate();
        private static void showSuggestion()
        {
            Point px = getCaretPosition();
            prefOutline.SetDesktopLocation(px.X, px.Y);
            prefOutline.Show();
        }
        private delegate void hideSuggestionDelegate();
        private static void hideSuggestion()
        {
            prefOutline.Hide();
        }

        [DllImport("user32.dll", EntryPoint = "GetGUIThreadInfo")]
        public static extern bool GetGUIThreadInfo(uint tId, out GUITHREADINFO threadInfo);

        [DllImport("user32.dll")]
        public static extern bool ClientToScreen(IntPtr hWnd, out Point position);
    }
}
