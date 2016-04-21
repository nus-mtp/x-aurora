using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;
using System.Xml.Linq;
using System.Drawing;
using System.Runtime.InteropServices;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

using XAuroraWordPlugin;

namespace XAuroraWordPlugin
{
    public partial class ThisAddIn
    {
        

        public static Word.Document getCurrDocument()
        {
            return Globals.ThisAddIn.Application.ActiveDocument;
        }

        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            System.Timers.Timer myTimer = new System.Timers.Timer();
            myTimer.Elapsed += new ElapsedEventHandler(makeConnection);
            myTimer.Interval = 3000;
            myTimer.Enabled = true;

            InputDetector.startHook();

            GUIManager.guiManagerStart();
        }

        private void makeConnection(object source, System.Timers.ElapsedEventArgs e)
        {
            Communicator.connect();
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {
            InputDetector.endHook();
        }

        #region VSTO generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }
        
        #endregion

        
    }
}
