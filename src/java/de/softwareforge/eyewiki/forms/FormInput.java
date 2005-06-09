package de.softwareforge.eyewiki.forms;

import java.util.HashMap;
import java.util.Map;

import org.apache.ecs.html.Input;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.plugin.PluginException;
import de.softwareforge.eyewiki.plugin.WikiPlugin;


/**
 * Creates a simple input text field.
 */
public class FormInput
        extends FormElement
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    public static final String PARAM_TYPE = "type";

    /** DOCUMENT ME! */
    public static final String PARAM_SIZE = "size";

    /**
     * Generates a dynamic form element on the WikiPage.
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
        String inputName = (String) params.get(PARAM_INPUTNAME);
        String inputValue = (String) params.get(PARAM_VALUE);
        String inputType = (String) params.get(PARAM_TYPE);
        String size = (String) params.get(PARAM_SIZE);

        if (inputName == null)
        {
            throw new PluginException("Input element is missing parameter 'name'.");
        }

        if (inputValue == null)
        {
            inputValue = "";
        }

        // Don't render if no error and error-only-rendering is on.
        FormInfo info = getFormInfo(ctx);
        Map previousValues = new HashMap();

        if (info != null)
        {
            if (info.hide())
            {
                return ("<p>(no need to show input field now)</p>");
            }

            previousValues = info.getSubmission();
        }

        // In order to isolate posted form elements into their own
        // map, prefix the variable name here. It will be stripped
        // when the handler plugin is executed.
        Input field = new Input(inputType, HANDLERPARAM_PREFIX + inputName, inputValue);

        if (previousValues != null)
        {
            String oldValue = (String) previousValues.get(inputName);

            if (oldValue != null)
            {
                field.setValue(oldValue);
            }
        }

        if (size != null)
        {
            field.setSize(size);
        }

        return (field.toString());
    }
}
