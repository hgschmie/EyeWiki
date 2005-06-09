package de.softwareforge.eyewiki.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;


/**
 * Generic utilities related to file and stream handling, JDK1.4 version. Do not call this directly
 * - go through FileUtil, since it is smart enough to decide which version you want to call.
 *
 * <p>
 * This class contains only JDK1.4 -specific methods.
 * </p>
 */

// FIXME: It would be so much neater to do a clean subclassing here
// but since this is a static class, we'd need to do some kind of
// redirection.  For later.
public final class FileUtil14
{
    private FileUtil14()
    {
    }


    /**
     * JDK 1.4 version of FileUtil.readContents.  This version circumvents all kinds of problems
     * just by gulping in the entire inputstream to a ByteArray.
     *
     * @param input DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static String readContents(InputStream input, String encoding)
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileUtil.copyContents(input, out);

        ByteBuffer bbuf = ByteBuffer.wrap(out.toByteArray());

        Charset cset = Charset.forName(encoding);
        CharsetDecoder csetdecoder = cset.newDecoder();

        csetdecoder.onMalformedInput(CodingErrorAction.REPORT);
        csetdecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        try
        {
            CharBuffer cbuf = csetdecoder.decode(bbuf);

            return cbuf.toString();
        }
        catch (CharacterCodingException e)
        {
            Charset latin1 = Charset.forName("ISO-8859-1");
            CharsetDecoder l1decoder = latin1.newDecoder();

            l1decoder.onMalformedInput(CodingErrorAction.REPORT);
            l1decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

            try
            {
                bbuf = ByteBuffer.wrap(out.toByteArray());

                CharBuffer cbuf = l1decoder.decode(bbuf);

                return cbuf.toString();
            }
            catch (CharacterCodingException ex)
            {
                throw (CharacterCodingException) ex.fillInStackTrace();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getThrowingMethod(Throwable t)
    {
        StackTraceElement [] trace = t.getStackTrace();
        StringBuffer sb = new StringBuffer();

        if ((trace == null) || (trace.length == 0))
        {
            sb.append("[Stack trace not available]");
        }
        else
        {
            sb.append(trace[0].isNativeMethod()
                ? "native method"
                : "");
            sb.append(trace[0].getClassName());
            sb.append(".");
            sb.append(trace[0].getMethodName() + "(), line " + trace[0].getLineNumber());
        }

        return sb.toString();
    }
}
