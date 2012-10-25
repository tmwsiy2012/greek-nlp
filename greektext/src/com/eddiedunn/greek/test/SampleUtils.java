package com.eddiedunn.greek.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A collection of useful stuff for the samples' use.
 */
public class SampleUtils {

    private static final FontRenderContext DEFAULT_FRC = 
                                new FontRenderContext(null, false, false);
                
    /**
     * Return the default FontRenderContext from the graphics environment.
     */
    public static FontRenderContext getDefaultFontRenderContext() {

        // fake right now - figure out where to get real default info
        return DEFAULT_FRC;
    }

    /**
     * Create a Frame containing the given panel.
     */
    public static void showComponentInFrame(Component component,
                                            String frameTitle) {

        Frame sampleFrame = new Frame(frameTitle);
        sampleFrame.add(component);
	sampleFrame.setBackground(Color.white);
        sampleFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        
        sampleFrame.setSize(400, 250);
        sampleFrame.show();

	component.requestFocus();
    }

    private static final Hashtable map = new Hashtable();
    static {
        map.put(TextAttribute.SIZE, new Float(18.0));
    }

    private static final String TEXT_ARG = "-text";

    // text to use in samples:
    private static AttributedString plainEnglish = new AttributedString("Hello world.", map);

    private static AttributedString longEnglish = new AttributedString(
	"Many people believe that Vincent van Gogh painted his best works " +
	"during the two-year period he spent in Provence. Here is where he " +
	"painted The Starry Night--which some consider to be his greatest " +
	"work of all. However, as his artistic brilliance reached new heights " +
	"in Provence, his physical and mental health plummeted. ", map);

    private static AttributedString plainArabic = new AttributedString(
                        "\u0647\u0630\u0627 \u0639\u0631\u0636 " +
                        "\u0644\u0645\u062C\u0645\u0648\u0639\u0629 TextLayout", map);

    private static AttributedString plainHebrew = new AttributedString(
                        "\u05D0\u05E0\u05D9 \u05DC\u05D0 \u05DE\u05D1\u05D9\u05DF " +
                        "\u05E2\u05D1\u05E8\u05D9\u05EA", map);

    private static AttributedString mixed = new AttributedString(
                        "\u05D0\u05E0\u05D9 Hello \u05DC\u05D0 \u05DE\u05D1\u05D9\u05DF " +
                        "\u05E2\u05D1\u05E8\u05D9\u05EA Arabic \u0644\u0645\u062C\u0645\u0648\u0639\u0629", map);

    private static String oneTwoThree = 
        "\u05D0\u05D7\u05EA  \u05E9\u05EA\u05D9\u05DD  \u05E9\u05DC\u05D5\u05E9  " +
        "\u05D0\u05E8\u05D1\u05E2  \u05EA\u05DE\u05E9  \u05E9\u05E9  \u05E9\u05D1\u05E2  " +
        "\u05E9\u05DE\u05D5\u05E0\u05D4  \u05EA\u05E9\u05E2  \u05E2\u05E9\u05E8  ";

    private static AttributedString longHebrew = new AttributedString(
                                        oneTwoThree + oneTwoThree + oneTwoThree, map);

    private static Hashtable sampleText = new Hashtable(5);
    static {
        sampleText.put("english", plainEnglish);
        sampleText.put("longenglish", longEnglish);
        sampleText.put("arabic", plainArabic);
        sampleText.put("hebrew", plainHebrew);
        sampleText.put("longhebrew", longHebrew);
	sampleText.put("mixed", mixed);
    }

    /**
     * Return the index in args of the String argument.  If argument is
     * not found return -1.
     */
    public static int getIndexOfArgument(String[] args, String argument) {

        for (int i=0; i < args.length; i++) {
            
            if (args[i].equals(argument)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Using the argument list, return an AttributedCharacterIterator.
     * Currently supported:
     *     -text english
     *     -text longenglish
     *     -text arabic
     *     -text hebrew
     *     -text longhebrew
     */
    public static AttributedCharacterIterator getText(String[] args) {

        AttributedString string = plainEnglish;
 
        int textIndex = getIndexOfArgument(args, TEXT_ARG);
        if (textIndex != -1) {
            if (textIndex+1 != args.length) {
                Object value = sampleText.get(args[textIndex+1]);

                if (value == null) {
                    textUsage();
                }
                else {
                    string = (AttributedString) value;
                }
            }
            else {
                textUsage();
            }
        }
 
        return string.getIterator();
    }

    public static void textUsage() {

        System.err.println("Valid values of " + TEXT_ARG + " are:");
        Enumeration keys = sampleText.keys();
        while (keys.hasMoreElements()) {
            System.err.println(keys.nextElement());
        }
    }
}

