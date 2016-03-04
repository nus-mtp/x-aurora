using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Word = Microsoft.Office.Interop.Word;

namespace XAuroraWordPlugin
{
    // Debug Logger
    public static class Messenger
    {
        public static void message(String msg)
        {
            Word.Document doc = ThisAddIn.getCurrDocument();
            Word.Range rng = doc.Range(0, 0);
            rng.Text = msg;
        }
    }
}
