package de.softwareforge.eyewiki.dav;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * DOCUMENT ME!
 *
 * @author jalkanen
 *
 * @since
 */
public class WebdavServlet
        extends HttpServlet
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(WebdavServlet.class);

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_PROPPATCH = "PROPPATCH";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_PROPFIND = "PROPFIND";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_MKCOL = "MKCOL";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_COPY = "COPY";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_MOVE = "MOVE";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_LOCK = "LOCK";

    /**
     * DOCUMENT ME!
     */
    private static final String METHOD_UNLOCK = "UNLOCK";

    /**
     * DOCUMENT ME!
     */
    public static final int SC_PROCESSING = 102;

    /**
     * DOCUMENT ME!
     */
    public static final int SC_MULTISTATUS = 207;

    /**
     * DOCUMENT ME!
     */
    public static final int SC_UNPROCESSABLE = 422;

    /**
     * DOCUMENT ME!
     */
    public static final int SC_LOCKED = 423;

    /**
     * DOCUMENT ME!
     */
    public static final int SC_FAILED_DEPENDENCY = 424;

    /**
     * DOCUMENT ME!
     */
    public static final int SC_INSUFFICIENT_STORAGE = 507;

    /**
         *
         */
    public WebdavServlet()
    {
        super();

        // TODO Auto-generated constructor stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doPropFind(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doPropPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doMkCol(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doCopy(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doMove(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * The default implementation of this class just returns an error code.
     *
     * @param request
     * @param response
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doLock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        try
        {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Sorry");
        }
        catch (IOException e)
        {
        }
    }

    /**
     * The default implementation of this class just returns an error code.
     *
     * @param request
     * @param response
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doUnlock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        try
        {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Sorry");
        }
        catch (IOException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String method = request.getMethod();

        if (log.isDebugEnabled())
        {
            log.debug("METHOD=" + method + "; request=" + request.getPathInfo());
        }

        try
        {
            if (METHOD_PROPPATCH.equals(method))
            {
                doPropPatch(request, response);
            }
            else if (METHOD_PROPFIND.equals(method))
            {
                doPropFind(request, response);
            }
            else if (METHOD_MKCOL.equals(method))
            {
                doMkCol(request, response);
            }
            else if (METHOD_COPY.equals(method))
            {
                doCopy(request, response);
            }
            else if (METHOD_MOVE.equals(method))
            {
                doMove(request, response);
            }
            else if (METHOD_LOCK.equals(method))
            {
                doLock(request, response);
            }
            else if (METHOD_UNLOCK.equals(method))
            {
                doUnlock(request, response);
            }
            else if ("OPTIONS".equals(method))
            {
                doOptions(request, response);
            }
            else
            {
                super.service(request, response);
            }
        }
        catch (Throwable t)
        {
            throw new ServletException(t);
        }
    }
}
