/*
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package de.softwareforge.eyewiki.auth.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.WikiException;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.WikiAuthenticator;
import de.softwareforge.eyewiki.exception.NoRequiredPropertyException;


/**
 * Provides a simple file-based authenticator.  This is really simple, as it does not even provide
 * encryption for the passwords.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.29.
 */
public class FileAuthenticator
        implements WikiAuthenticator
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(FileAuthenticator.class);

    /** DOCUMENT ME! */
    private String m_fileName;

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     * @param conf DOCUMENT ME!
     *
     * @throws NoRequiredPropertyException DOCUMENT ME!
     * @throws WikiException DOCUMENT ME!
     */
    public FileAuthenticator(WikiEngine engine, Configuration conf)
            throws NoRequiredPropertyException, WikiException
    {
        // No default, you _must_ configure this
        m_fileName = engine.getValidPath(conf.getString(WikiProperties.PROP_AUTH_FILENAME));

        if (log.isInfoEnabled())
        {
            log.info("Authenticator file is at " + m_fileName);
        }
    }

    private Properties readPasswords(String filename)
            throws IOException
    {
        Properties props = new Properties();
        InputStream in = null;

        try
        {
            File file = new File(filename);

            if ((file != null) && file.exists())
            {
                in = new FileInputStream(file);

                props.load(in);

                if (log.isDebugEnabled())
                {
                    log.debug("Loaded " + props.size() + " usernames.");
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        return props;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wup DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean authenticate(UserProfile wup)
    {
        if ((wup == null) || (wup.getName() == null))
        {
            return (false);
        }

        try
        {
            Properties props = readPasswords(m_fileName);

            String userName = wup.getName();
            String password = wup.getPassword();

            String storedPassword = props.getProperty(userName);

            if ((storedPassword != null) && storedPassword.equals(password))
            {
                return true;
            }
        }
        catch (IOException e)
        {
            log.error("Unable to read passwords, disallowing login.", e);
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canChangePasswords()
    {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wup DOCUMENT ME!
     * @param password DOCUMENT ME!
     */
    public void setPassword(UserProfile wup, String password)
    {
    }
}
