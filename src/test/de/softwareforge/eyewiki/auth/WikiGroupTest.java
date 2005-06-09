package de.softwareforge.eyewiki.auth;

import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.auth.WikiGroup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class WikiGroupTest
        extends TestCase
{
    /** DOCUMENT ME! */
    WikiGroup m_group;

    /**
     * Creates a new WikiGroupTest object.
     *
     * @param s DOCUMENT ME!
     */
    public WikiGroupTest(String s)
    {
        super(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void setUp()
            throws Exception
    {
        m_group = new WikiGroup();
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown()
    {
    }

    /**
     * DOCUMENT ME!
     */
    public void testAdd1()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        m_group.addMember(u1);

        assertTrue(m_group.isMember(u1));
    }

    /**
     * DOCUMENT ME!
     */
    public void testAdd2()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        assertTrue("adding alice", m_group.addMember(u1));
        assertTrue("adding bob", m_group.addMember(u2));

        assertTrue("Alice", m_group.isMember(u1));
        assertTrue("Bob", m_group.isMember(u2));
    }

    /**
     * Check that different objects match as well.
     */
    public void testAdd3()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        UserProfile u3 = new UserProfile();
        u3.setName("Bob");

        assertTrue("adding alice", m_group.addMember(u1));
        assertTrue("adding bob", m_group.addMember(u2));

        assertTrue("Alice", m_group.isMember(u1));
        assertTrue("Bob", m_group.isMember(u3));
    }

    /**
     * DOCUMENT ME!
     */
    public void testRemove()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        UserProfile u3 = new UserProfile();
        u3.setName("Bob");

        m_group.addMember(u1);
        m_group.addMember(u2);

        m_group.removeMember(u3);

        assertTrue("Alice", m_group.isMember(u1));
        assertFalse("Bob", m_group.isMember(u2));
        assertFalse("Bob 2", m_group.isMember(u3));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEquals1()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        m_group.addMember(u1);
        m_group.addMember(u2);

        WikiGroup group2 = new WikiGroup();
        UserProfile u3 = new UserProfile();
        u3.setName("Alice");

        UserProfile u4 = new UserProfile();
        u4.setName("Bob");

        group2.addMember(u3);
        group2.addMember(u4);

        assertTrue(m_group.equals(group2));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEquals2()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        m_group.addMember(u1);
        m_group.addMember(u2);

        WikiGroup group2 = new WikiGroup();
        UserProfile u3 = new UserProfile();
        u3.setName("Alice");

        UserProfile u4 = new UserProfile();
        u4.setName("Charlie");

        group2.addMember(u3);
        group2.addMember(u4);

        assertFalse(m_group.equals(group2));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEquals3()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        m_group.addMember(u1);
        m_group.addMember(u2);
        m_group.setName("Blib");

        WikiGroup group2 = new WikiGroup();
        UserProfile u3 = new UserProfile();
        u3.setName("Alice");

        UserProfile u4 = new UserProfile();
        u4.setName("Bob");

        group2.addMember(u3);
        group2.addMember(u4);
        group2.setName("Blib");

        assertTrue(m_group.equals(group2));
    }

    /**
     * DOCUMENT ME!
     */
    public void testEquals4()
    {
        UserProfile u1 = new UserProfile();
        u1.setName("Alice");

        UserProfile u2 = new UserProfile();
        u2.setName("Bob");

        m_group.addMember(u1);
        m_group.addMember(u2);
        m_group.setName("BlibBlab");

        WikiGroup group2 = new WikiGroup();
        UserProfile u3 = new UserProfile();
        u3.setName("Alice");

        UserProfile u4 = new UserProfile();
        u4.setName("Bob");

        group2.addMember(u3);
        group2.addMember(u4);
        group2.setName("Blib");

        assertFalse(m_group.equals(group2));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Test suite()
    {
        return new TestSuite(WikiGroupTest.class);
    }
}
