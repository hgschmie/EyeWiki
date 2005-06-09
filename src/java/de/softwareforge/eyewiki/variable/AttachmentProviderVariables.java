package de.softwareforge.eyewiki.variable;

import org.picocontainer.Startable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiProvider;
import de.softwareforge.eyewiki.attachment.AttachmentManager;
import de.softwareforge.eyewiki.manager.VariableManager;

public class AttachmentProviderVariables
        implements Startable
{
    private final AttachmentManager attachmentManager;
    private final VariableManager variableManager;

    public AttachmentProviderVariables(final VariableManager variableManager, final AttachmentManager attachmentManager)
    {
        this.attachmentManager = attachmentManager;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("attachmentprovider", new AttachmentProvider());
        variableManager.registerVariable("attachmentproviderdescription", new AttachmentProviderDescription());
    }

    public synchronized void stop()
    {
        // GNDN
    }

    private class AttachmentProvider
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            WikiProvider p = attachmentManager.getCurrentProvider();

            return (p != null)
                    ? p.getClass().getName()
                    : "-";
        }
    }

    private class AttachmentProviderDescription
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            WikiProvider p = attachmentManager.getCurrentProvider();

            return (p != null)
                    ? p.getProviderInfo()
                    : "-";
        }
    }
}

