package de.softwareforge.eyewiki.forms;

import java.util.HashMap;
import java.util.Map;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.html.TextArea;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * DOCUMENT ME!
 *
 * @author ebu
 */
public class FormTextarea
        extends FormElement
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    public static final String PARAM_ROWS = "rows";

    /** DOCUMENT ME! */
    public static final String PARAM_COLS = "cols";

    /**
     * DOCUMENT ME!
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
        // Don't render if no error and error-only-rendering is on.
        FormInfo info = getFormInfo(ctx);

        if (info == null)
        {
            return "";
        }

        if (info.hide())
        {
            return "<p>(no need to show textarea field now)</p>";
        }

        Map previousValues = info.getSubmission();

        if (previousValues == null)
        {
            previousValues = new HashMap();
        }

        ConcreteElement field = null;

        field = buildTextArea(params, previousValues);

        // We should look for extra params, e.g. width, ..., here.

        return (field == null) ? "" : field.toString();
    }

    private TextArea buildTextArea(Map params, Map previousValues)
            throws PluginException
    {
        String inputName = (String) params.get(PARAM_INPUTNAME);
        String rows = (String) params.get(PARAM_ROWS);
        String cols = (String) params.get(PARAM_COLS);

        if (inputName == null)
        {
            throw new PluginException("Textarea element is missing " + "parameter 'name'.");
        }

        // In order to isolate posted form elements into their own
        // map, prefix the variable name here. It will be stripped
        // when the handler plugin is executed.
        TextArea field = new TextArea(HANDLERPARAM_PREFIX + inputName, rows, cols);

        if (previousValues != null)
        {
            String oldValue = (String) previousValues.get(inputName);

            if (oldValue != null)
            {
                field.addElement(oldValue);
            }
            else
            {
                oldValue = (String) params.get(PARAM_VALUE);

                if (oldValue != null)
                {
                    field.addElement(oldValue);
                }
            }
        }

        return field;
    }
}
