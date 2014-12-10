/*
 * Copyright (c) 2008, Aaron Digulla
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the name of Aaron Digulla nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package anyxml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Create Java code for HTML entities. Download the source for the Wikipedia page
 * http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
 * and run it through this code (filename is the first argument).
 * 
 * @author DIGULAA
 *
 */
public class CreateHtmlEntities
{
    public static void main (String[] args)
    {
        try
        {
            File f = new File (args[0]);
            BufferedReader r = new BufferedReader (new InputStreamReader (new FileInputStream (f), "iso-8859-1"));
            String line;
            
            while ((line = r.readLine ()) != null)
            {
                line = line.trim ();
                if (!"|-".equals (line))
                    continue;
                
                line = r.readLine (); // Name
                String name = line.substring (2).trim ();
                line = r.readLine (); // Character
                line = r.readLine (); // Unicode code point
                String unicode = line.substring (4, 8);
                if ("00A0".compareTo (unicode) > 0)
                    continue;
                line = r.readLine (); // Standard
                line = r.readLine (); // DTD
                line = r.readLine (); // Old ISO subset
                line = r.readLine (); // Description
                String desc = line.substring (2).trim ();
                desc = desc
                .replaceAll ("''", "")
                .replaceAll ("\\{\\{[^}]+\\}\\}", "")
                .replaceAll ("\\[\\[", "")
                .replaceAll ("\\]\\]", "")
                ;
                System.out.println ("        add (\""+name+"\", \"\\u"+unicode.toLowerCase ()+"\"); // "+desc);
            }
            
            r.close ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }
    }
}
