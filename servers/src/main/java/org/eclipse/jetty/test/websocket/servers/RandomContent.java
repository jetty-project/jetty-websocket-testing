package org.eclipse.jetty.test.websocket.servers;

import java.util.Random;

public final class RandomContent
{
    private static final char ALPHANUMSYM[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-_: ".toCharArray();
    private static final char TEXT[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char LOWERCASE[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char HEX[] = "0123456789abcdef".toCharArray();

    private final Random rand;

    public RandomContent()
    {
        this.rand = new Random();
    }

    public void setSeed(long seed)
    {
        this.rand.setSeed(seed);
    }

    private void appendRandomChars(StringBuilder str, char[] chars, int minlen, int maxlen)
    {
        int len = rand.nextInt(maxlen - minlen) + minlen;
        int charsLen = chars.length;
        for (int i = 0; i < len; i++)
        {
            str.append(chars[rand.nextInt(charsLen)]);
        }
    }

    public void appendRandomAlphaNumSym(StringBuilder str, int minlen, int maxlen)
    {
        appendRandomChars(str,ALPHANUMSYM,minlen,maxlen);
    }

    public void appendRandomText(StringBuilder str, int minlen, int maxlen)
    {
        appendRandomChars(str,TEXT,minlen,maxlen);
    }

    private void appendRandomLowercase(StringBuilder str, int minlen, int maxlen)
    {
        appendRandomChars(str,LOWERCASE,minlen,maxlen);
    }

    public void appendRandomNumber(StringBuilder str, int minValue, int maxValue)
    {
        int val = rand.nextInt(maxValue - minValue) + minValue;
        str.append(Integer.toString(val));
    }

    public void appendRandomPath(StringBuilder str, int minSegments, int maxSegments, String extension)
    {
        int segments = rand.nextInt(maxSegments - minSegments) + minSegments;
        for (int i = 0; i < segments; i++)
        {
            str.append('/');
            appendRandomLowercase(str,2,8);
        }
        str.append(extension);
    }

    public void appendRandomHexBytes(StringBuilder str, int numBytes)
    {
        int hexLen = HEX.length;
        for (int i = 0; i < numBytes; i++)
        {
            str.append(HEX[rand.nextInt(hexLen)]);
            str.append(HEX[rand.nextInt(hexLen)]);
        }
    }
}
