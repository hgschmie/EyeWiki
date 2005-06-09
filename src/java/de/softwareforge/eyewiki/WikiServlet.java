package de.softwareforge.eyewiki;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.url.DefaultURLConstructor;


/**
 */
public class WikiServlet
        extends HttpServlet
{
    /** DOCUMENT ME! */
    private WikiEngine m_engine = null;

    /** DOCUMENT ME! */
    protected Logger log = Logger.getLogger(this.getClass());

    /**
     * DOCUMENT ME!
     *
     * @param config DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void init(ServletConfig config)
            throws ServletException
    {
        super.init(config);
        m_engine = WikiEngine.getInstance(config);
        log.info("WikiServlet initialized.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        doGet(req, res);
    }

    /**
     * DOCUMENT ME!
     *
     * @param req DOCUMENT ME!
     * @param res DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        String pageName =
            DefaultURLConstructor.parsePageFromURL(req, m_engine.getContentEncoding());

        if (log.isInfoEnabled())
        {
            log.info("Request for page: " + pageName);
        }

        if (pageName == null)
        {
            pageName = m_engine.getFrontPage(); // FIXME: Add special pages as well
        }

        String jspPage = m_engine.getURLConstructor().getForwardPage(req);

        StringBuffer sb = new StringBuffer("/")
                .append(jspPage)
                .append("?page=")
                .append(m_engine.encodeName(pageName))
                .append("&")
                .append(req.getQueryString());

        RequestDispatcher dispatcher = req.getRequestDispatcher(sb.toString());

        dispatcher.forward(req, res);
    }
}
