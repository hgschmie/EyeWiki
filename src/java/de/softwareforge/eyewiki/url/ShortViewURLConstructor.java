package de.softwareforge.eyewiki.url;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;

/**
 *  A specific URL constructor that returns easy-to-grok URLs for
 *  VIEW and ATTACH contexts, but goes through JSP pages otherwise.
 *
 *  @author jalkanen
 *
 *  @since
 */
public class ShortViewURLConstructor
        extends ShortURLConstructor
{
    public ShortViewURLConstructor(final WikiEngine engine, final Configuration conf)
    {
        super(engine, conf);
    }

    private String makeURL(String context, String name, boolean absolute)
    {
        String viewurl = m_urlPrefix+"%n";

        if (absolute)
        {
            viewurl = "%uwiki/%n";
        }

        if (context.equals(WikiContext.VIEW))
        {
            if (name == null)
            {
                return makeURL("%u","",absolute); // FIXME
            }
            return doReplacement(viewurl, name, absolute);
        }

        return doReplacement(
                DefaultURLConstructor.getURLPattern(context,name),
                name,
                true);
    }

    public String makeURL(String context, String name, boolean absolute, String parameters)
    {
        if (StringUtils.isNotEmpty(parameters))
        {
            if (context.equals(WikiContext.ATTACH) || context.equals(WikiContext.VIEW) || name == null)
            {
                parameters = "?" + parameters;
            }
            else
            {
                parameters = "&amp;"+parameters;
            }
        }
        else
        {
            parameters = "";
        }
        return makeURL(context, name, absolute) + parameters;
    }

    /**
     *   Since we're only called from WikiServlet, where we get the VIEW requests,
     *   we can safely return this.
     */
    public String getForwardPage(HttpServletRequest req)
    {
        return "Wiki.jsp";
    }
}
