package de.softwareforge.eyewiki.forms;

import java.util.Map;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * Closes a WikiForm.
 *
 * @author ebu
 */
public class FormClose
        extends FormElement
        implements WikiPlugin
{
    /**
     * Builds a Form close tag. Removes any information on the form from the WikiContext.
     *
     * @param ctx DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext ctx, Map params)
            throws PluginException
    {
        StringBuffer tags = new StringBuffer();
        tags.append("</form>\n");
        tags.append("</div>");

        // Don't render if no error and error-only-rendering is on.
        FormInfo info = getFormInfo(ctx);

        if (info != null)
        {
            if (info.hide())
            {
                return ("<p>(no need to show close now)</p>");
            }
        }

        // Get rid of remaining form data, so it doesn't mess up other forms.
        // After this, it is safe to add other Forms.
        storeFormInfo(ctx, null);

        return (tags.toString());
    }
}
