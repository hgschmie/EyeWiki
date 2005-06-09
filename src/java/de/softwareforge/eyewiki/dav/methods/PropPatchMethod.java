package de.softwareforge.eyewiki.dav.methods;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.softwareforge.eyewiki.WikiEngine;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class PropPatchMethod
        extends DavMethod
{
    /**
     * DOCUMENT ME!
     *
     * @param engine
     */
    public PropPatchMethod(WikiEngine engine)
    {
        super(engine);
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "eyeWiki is read-only");
    }
}
