using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace XAuroraWordPlugin
{
    public class Preference
    {
        private int id;
        private string source;
        private string content;
    
        public Preference()
        {
            id = -1;
            source = "";
            content = "";
        }

        public Preference(int newID, string newSource, string newCont)
        {
            id = newID;
            source = newSource;
            content = newCont;
        }

        public int getID()
        {
            return id;
        }

        public string getSource()
        {
            return source;
        }

        public string getContent()
        {
            return content;
        }

        public void setID(int newID)
        {
            id = newID;
        }

        public void setSource(string newSource)
        {
            source = newSource;
        }

        public void setContent(string newContent)
        {
            content = newContent;
        }

        public string toString()
        {
            return "id:" + id.ToString() + " Source:" + source + " Content:" + content;
        }
    }
}
