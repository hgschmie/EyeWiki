package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;


/**
 * Returns the value of an Wiki variable.
 *
 * <P>
 * <B>Attributes</B>
 * </p>
 *
 * <UL>
 * <li>
 * var - Name of the variable.  Required.
 * </li>
 * <li>
 * default - Revert to this value, if the value of "var" is null. If left out, this tag will
 * produce a concise error message if the named variable is not found. Set to empty (default="")
 * to hide the message.
 * </li>
 * </ul>
 *
 * <P>
 * A default value implies <I>failmode='quiet'</I>.
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */
public class VariableTag
        extends WikiTagBase
{
    /** DOCUMENT ME! */
    private String m_var = null;

    /** DOCUMENT ME! */
    private String m_default = null;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVar()
    {
        return m_var;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setVar(String arg)
    {
        m_var = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setDefault(String arg)
    {
        m_default = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws JspException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws JspException, IOException
    {
        WikiEngine engine = m_wikiContext.getEngine();
        JspWriter out = pageContext.getOut();
        String msg = null;
        String value = null;

        try
        {
            value = engine.getVariableManager().getValue(m_wikiContext, getVar());
        }
        catch (NoSuchVariableException e)
        {
            msg = "No such variable: " + e.getMessage();
        }
        catch (IllegalArgumentException e)
        {
            msg = "Incorrect variable name: " + e.getMessage();
        }

        if (value == null)
        {
            value = m_default;
        }

        if (value == null)
        {
            value = msg;
        }

        out.write(value);

        return (SKIP_BODY);
    }
}
