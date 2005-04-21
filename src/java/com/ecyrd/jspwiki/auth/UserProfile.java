/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.auth;

import java.util.StringTokenizer;

import com.ecyrd.jspwiki.util.TextUtil;


/**
 * Contains user profile information.
 *
 * @author Janne Jalkanen
 *
 * @since 1.7.2
 */

// FIXME: contains magic strings.
public class UserProfile
        extends WikiPrincipal
{
    /** DOCUMENT ME! */
    public static final int NONE = 0;

    /** DOCUMENT ME! */
    public static final int COOKIE = 1;

    /** DOCUMENT ME! */
    public static final int CONTAINER = 2; // Container has done auth for us.

    /** DOCUMENT ME! */
    public static final int PASSWORD = 3;

    /** DOCUMENT ME! */
    private int m_loginStatus = NONE;

    /** DOCUMENT ME! */
    private String m_password = null;

    /** DOCUMENT ME! */
    private String m_loginName = null;

    /**
     * Creates a new UserProfile object.
     */
    public UserProfile()
    {
    }

    /**
     * The login name may be different from your WikiName.  The WikiName is typically of type
     * FirstnameLastName (like JanneJalkanen), whereas the login name is typically a shorter one,
     * such as "jannej" or something similar.
     *
     * @param name DOCUMENT ME!
     */
    public void setLoginName(String name)
    {
        m_loginName = name;
    }

    /**
     * Returns the login name.
     *
     * @return DOCUMENT ME!
     */
    public String getLoginName()
    {
        return m_loginName;
    }

    /**
     * Returns true, if the user has been authenticated properly.
     *
     * @return DOCUMENT ME!
     */
    public boolean isAuthenticated()
    {
        return m_loginStatus >= CONTAINER;
    }

    /*
    public UserProfile( String representation )
    {
        parseStringRepresentation( representation );
    }
    */
    public String getStringRepresentation()
    {
        String res = "username=" + TextUtil.urlEncodeUTF8(getName());

        return res;
    }

    /**
     * DOCUMENT ME!
     *
     * @param res DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static UserProfile parseStringRepresentation(String res)
    {
        UserProfile prof = new UserProfile();

        if ((res != null) && (res.length() > 0))
        {
            //
            //  Not all browsers or containers do proper cookie
            //  decoding, which is why we can suddenly get stuff
            //  like "username=3DJanneJalkanen", so we have to
            //  do the conversion here.
            //
            res = TextUtil.urlDecodeUTF8(res);

            StringTokenizer tok = new StringTokenizer(res, " ,=");

            while (tok.hasMoreTokens())
            {
                String param = tok.nextToken();
                String value = tok.nextToken();

                if (param.equals("username"))
                {
                    prof.setName(value);
                }
            }
        }

        return prof;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object o)
    {
        if ((o != null) && (o instanceof UserProfile))
        {
            String name = getName();

            if ((name != null) && name.equals(((UserProfile) o).getName()))
            {
                return true;
            }
        }

        return false;
    }

    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLoginStatus()
    {
        return m_loginStatus;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setLoginStatus(int arg)
    {
        m_loginStatus = arg;
    }

    /**
     * Returns the password that the user gave.  We store the password because some authenticators
     * may need to reissue it at periodical intervals; or possibly use the same password to
     * multiple services.
     *
     * @return DOCUMENT ME!
     */
    public String getPassword()
    {
        return m_password;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg DOCUMENT ME!
     */
    public void setPassword(String arg)
    {
        m_password = arg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "[UserProfile: '" + getName() + "']";
    }
}
