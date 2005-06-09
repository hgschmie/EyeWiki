/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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

