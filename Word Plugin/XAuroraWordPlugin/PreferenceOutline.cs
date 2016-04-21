using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace XAuroraWordPlugin
{
    public partial class PreferenceOutline : Form
    {
        private int padding = 3;
        private int currPage = -1;
        private int selected = -1;
        public List<Preference> prefList;

        public PreferenceOutline()
        {
            InitializeComponent();

            PrefList.DrawMode = DrawMode.OwnerDrawFixed;
            PrefList.DrawItem += new DrawItemEventHandler(PrefList_DrawItem);
            PrefList.ItemHeight = 35;

            PrefList.SelectedIndexChanged += new System.EventHandler(PrefList_SelectedIndexChanged);

            BackColor = Color.Lime;
            panel1.BackColor = Color.Lime;
            TransparencyKey = Color.Lime;
        }

        public void updateDrawList()
        {
            PrefList.Items.Clear();
            for (int i = 0; i < Math.Min(5,(prefList.Count-currPage*5)); i++)
            {
                PrefList.Items.Add(prefList[currPage*5+i]);
            }

            PrefList.SelectedIndex = selected;
        }

        public void selectItem(int no, int page)
        {
            selected = no;
            currPage = page;
        }

        private void PrefList_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void PrefList_DrawItem(object sender, DrawItemEventArgs e)
        {
            bool selected = false;

            if ((e.State & DrawItemState.Selected) == DrawItemState.Selected)
            {
                e = new DrawItemEventArgs(e.Graphics,
                                          e.Font,
                                          e.Bounds,
                                          e.Index,
                                          e.State ^ DrawItemState.Selected,
                                          e.ForeColor,
                                          Color.FromArgb(51, 153, 255));//Choose the color
                selected = true;
                // Draw the selected background
                e.DrawBackground();
            }
            else
            {
                if (e.Index % 2 == 1)
                {
                    e.Graphics.FillRectangle(new SolidBrush(Color.FromArgb(239, 248, 253)), e.Bounds);
                }
                else
                {
                    // Draw the default background
                    e.DrawBackground();
                }
            }

            // If the ListBox has focus, draw a focus rectangle around the selected item.
            e.DrawFocusRectangle();

            if (e.Index >= 0)
            {
                Preference suggestion = (Preference)PrefList.Items[e.Index];
                String content = suggestion.getContent(), sourceName = suggestion.getSource();

                content = content.Replace("\n", " ");

                Rectangle drawBound = e.Bounds;
                drawBound.X += padding;
                drawBound.Y += padding;
                drawBound.Width -= padding * 2;
                drawBound.Height -= padding * 2;

                StringFormat stringFormat = StringFormat.GenericDefault;
                stringFormat.Alignment = StringAlignment.Near;

                // Draw Index String
                Brush myBrush = new SolidBrush(Color.FromArgb(102, 106, 114));
                if (selected)
                {
                    myBrush = Brushes.White;
                }
                Font font = new Font(e.Font.FontFamily, e.Font.Size + 6, e.Font.Style);
                drawBound.Y += 2;
                e.Graphics.DrawString((e.Index + 1) + "", font, myBrush, drawBound, stringFormat);
                drawBound.Y -= 2;

                // Draw Content String
                myBrush = Brushes.Black;
                if (selected)
                {
                    myBrush = Brushes.White;
                }
                font = new Font(e.Font.FontFamily, e.Font.Size + 1, e.Font.Style);
                content = getSuggestionWrapping(content, font, false);
                drawBound.X += 18;
                drawBound.Width -= 18;

                e.Graphics.DrawString(content, font, myBrush, drawBound, stringFormat);

                drawBound.Y += getRenderTextHeight(content, PrefList, font);
                drawBound.Height -= getRenderTextHeight(content, PrefList, font);

                myBrush = new SolidBrush(Color.FromArgb(54, 96, 137));
                font = new Font(e.Font.FontFamily, e.Font.Size - 1, e.Font.Style);
                sourceName = getSourceNameWrapping(sourceName, font);
                if (selected)
                {
                    myBrush = Brushes.White;
                }

                e.Graphics.DrawString(sourceName, font, myBrush, drawBound, StringFormat.GenericDefault);

                // Draw bottom line if it is not the last entry
                if (e.Index != PrefList.Items.Count - 1)
                {
                    Pen pen = new Pen(Color.FromArgb(185, 187, 189), 1);
                    Point point1 = new Point(0, e.Bounds.Y + e.Bounds.Height - 1);
                    Point point2 = new Point(e.Bounds.X + e.Bounds.Width - 1, e.Bounds.Y + e.Bounds.Height - 1);
                    e.Graphics.DrawLine(pen, point1, point2);
                }
            }

            e.DrawFocusRectangle();
        }

        private void PreferenceOutline_Load(object sender, EventArgs e)
        {

        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        #region Text Wrapping
        public String getSourceNameWrapping(String sourceName, Font font)
        {
            int gap = 25;
            String[] words = sourceName.Split(' ');
            int width = getRenderTextWidth(sourceName, PrefList, font);
            String result = "";

            if (width > PrefList.Width - gap)
            {
                result += words[0];
                width = getRenderTextWidth(result, PrefList, font);

                // check if the first word width is larger than the PrefList width
                if (width > PrefList.Width - gap)
                {
                    while (getRenderTextWidth(result + " ...", PrefList, font) > PrefList.Width - gap)
                    {
                        result = result.Remove(result.Length - 1, 1);
                    }
                }
                else
                {
                    for (int i = 1; i < words.Count(); i++)
                    {
                        String tempResult = result + " " + words[i];
                        width = getRenderTextWidth(tempResult + " ...", PrefList, font);

                        if (width > PrefList.Width - gap)
                        {
                            break;
                        }
                        else
                        {
                            result = tempResult;
                        }
                    }
                }

                result = result + " ...";
            }
            else
            {
                result = sourceName;
            }

            return result;
        }

        public String getSuggestionWrapping(String suggestion, Font font, bool entityMode)
        {
            int gap = 30;
            if (entityMode)
            {
                gap = 100;
            }
            String[] words = suggestion.Split(' ');
            String result = "";

            int width = getRenderTextWidth(suggestion, PrefList, font);

            if (width > PrefList.Width - gap)
            {

                ArrayList wordIndexCollection = new ArrayList();

                // The check sequence define what kind of substring pattern we wish to have
                // So by having the following value {0, words.length, 1, 2}
                // It will check the following substring accordingly:
                // Example: "This is an example of substring sequence check."
                // "This ... "
                // "This ... check."
                // "This is ... check."
                // "This is an ... check."
                // This is an absolute lame and stupid check.
                int[] firstCheckSequence = { 0, words.Count() - 1, 1, 2 };

                for (int i = 0; i < firstCheckSequence.Count(); i++)
                {
                    wordIndexCollection.Add(firstCheckSequence[i]);
                    result = generateSubstring(words, wordIndexCollection);
                    width = getRenderTextWidth(result, PrefList, font);

                    // check if the width is wider
                    if (width > PrefList.Width - gap)
                    {
                        if (i == 0)
                        {
                            // If the first word is longer than the width list, we need to substring by characters
                            while (getRenderTextWidth(
                                generateSubstring(words, wordIndexCollection),
                                PrefList, font) > PrefList.Width - gap)
                            {
                                words[0] = words[0].Remove(words[0].Length - 1, 1);
                            }
                            result = generateSubstring(words, wordIndexCollection);
                        }
                        break;
                    }
                }

                // After having the initial pattern check, we will try to take as many words into 
                // the substring as possible based on the length
                // cStart and cEnd define the start and end indexes to perform this operation.
                int cStart = 3, cEnd = words.Count() - 2;

                while (width < PrefList.Width - gap)
                {
                    String cStartWord = words[cStart];
                    String cEndWord = words[cEnd];
                    String tempResult;

                    if (cStartWord.Length < cEndWord.Length)
                    {
                        wordIndexCollection.Add(cStart);
                        cStart++;
                    }
                    else
                    {
                        wordIndexCollection.Add(cEnd);
                        cEnd--;
                    }

                    tempResult = generateSubstring(words, wordIndexCollection);
                    width = getRenderTextWidth(tempResult, PrefList, font);

                    // check if the width is wider
                    if (width > PrefList.Width - gap)
                    {
                        break;
                    }
                    else
                    {
                        result = tempResult;
                    }
                }
            }
            else
            {
                result = suggestion;
            }

            return result;
        }

        private int getRenderTextWidth(String text, Control control, Font font)
        {
            Graphics g = control.CreateGraphics();
            int width = (int)g.MeasureString(text, font).Width;

            g.Dispose();
            return width;
        }

        private int getRenderTextHeight(String text, Control control, Font font)
        {
            Graphics g = control.CreateGraphics();
            int height = (int)g.MeasureString(text, font).Height;

            g.Dispose();
            return height;
        }

        // Generate the substring from the list of choosen words
        private String generateSubstring(String[] words, ArrayList wordIndexes)
        {
            wordIndexes.Sort();
            object[] arrayWordIndexes = wordIndexes.ToArray();

            // Special case
            if (wordIndexes.Count == 1)
            {
                return words[0] + " ...";
            }

            String result = "";

            try
            {
                for (int i = 0; i < arrayWordIndexes.Count(); i++)
                {
                    int wordIndex = (int)arrayWordIndexes[i];

                    if (i == 0)
                    {
                        // first word
                        result += words[wordIndex];
                    }
                    else
                    {
                        result += " ";
                        int prevWordIndex = (int)arrayWordIndexes[i - 1];

                        if (wordIndex - prevWordIndex != 1)
                        {
                            result += "... " + words[wordIndex];
                        }
                        else
                        {
                            result += words[wordIndex];
                        }
                    }
                }
            }
            catch (Exception e)
            {
                return "";
            }

            return result;
        }
        #endregion
    }
}
