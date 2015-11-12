using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;
using System.Timers;
using Test_Word_Plugin;

public class Messenger
{
    public static void message(String msg)
    {
        Word.Document doc = ThisAddIn.getCurrDocument();
        Word.Range rng = doc.Range(0,0);
        rng.Text = msg;
    }
}

namespace Test_Word_Plugin
{
    public partial class ThisAddIn
    {
        Communicator comm = new Communicator();
        static Word.Document curr;

        public static Word.Document getCurrDocument()
        {
            return curr;
        }

        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            Timer myTimer = new Timer();
            myTimer.Elapsed += new ElapsedEventHandler(makeConnection);
            myTimer.Interval = 3000;
            myTimer.Enabled = true;

            curr = this.Application.ActiveDocument;
        }

        private void makeConnection(object source, System.Timers.ElapsedEventArgs e)
        {
            comm.connect();
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {

        }

        #region VSTO 生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }
        
        #endregion
    }
}
