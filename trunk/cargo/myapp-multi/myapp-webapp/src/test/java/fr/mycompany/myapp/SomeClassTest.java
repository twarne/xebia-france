package fr.mycompany.myapp;

import fr.mycompany.myapp.SomeClass;
import junit.framework.TestCase;

public class SomeClassTest extends TestCase
{
    public void testDoSomething()
    {
        SomeClass sc = new SomeClass();
        assertEquals("Hello World!", sc.saySomething());
    }
}
