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
 *
 */
package anyxml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import anyxml.Document;
import anyxml.Element;
import anyxml.XMLIOSource;
import anyxml.XMLParser;
import anyxml.XMLWriter;

/**
 * A little search'n'replace utility for Maven 2 pom.xml files.
 * 
 * @author DIGULAA
 *
 */
public class MavenSNR
{
    public static void main (String[] args)
    {
        try
        {
            MavenSNR obj = new MavenSNR ();
            obj.run (args);
        }
        catch (Throwable t)
        {
            while (t != null)
            {
                t.printStackTrace ();
                t = t.getCause ();
            }
            System.exit (1);
        }
    }
    
    private List<POMFile> files = new ArrayList<POMFile> ();
    private List<String> searchConditions = new ArrayList<String> ();
    private List<String> checkPaths = new ArrayList<String> ();
    private List<String> printPaths = new ArrayList<String> ();
    private List<String> replacePaths = new ArrayList<String> ();
    
    public void run (String[] args)
    {
        if (args.length == 0)
            throw new IllegalArgumentException ("Usage: $0 path-to-pom.xml search1...searchN [ --check check1...checkN ] [ --print path1...pathN ] [ --replace replace1..replaceN ]");
        
        readFile (new File (args[0]));
        
        collectArgs (args);
        
        checkFiles ();
    }

    /**
     * @param args
     */
    private void collectArgs (String[] args)
    {
        int pos = 1;
        while (pos < args.length)
        {
            if (args[pos].startsWith ("--"))
                break;
            
            searchConditions.add (args[pos ++]);
        }
        
        if (pos < args.length && "--check".equals (args[pos]))
        {
            pos ++;
            while (pos < args.length)
            {
                if (args[pos].startsWith ("--"))
                    break;
                
                checkPaths.add (args[pos ++]);
            }
        }
        
        if (pos < args.length && "--print".equals (args[pos]))
        {
            pos ++;
            while (pos < args.length)
            {
                if (args[pos].startsWith ("--"))
                    break;
                
                printPaths.add (args[pos ++]);
            }
        }
        
        if (pos < args.length && "--replace".equals (args[pos]))
        {
            pos ++;
            while (pos < args.length)
            {
                replacePaths.add (args[pos ++]);
            }
        }
    }

    private void checkFiles ()
    {
        for (POMFile pom: files)
        {
            if (pom.matches (searchConditions))
            {
                System.out.println ("--- "+pom.getFile ());
                boolean hasErrors = false;
                for (String path: checkPaths)
                {
                    boolean check = pom.check (path);
                    hasErrors = hasErrors || !check;
                }
                if (hasErrors)
                    System.out.println ("FAILED");
                else
                    System.out.println ("OKAY");
                
                for (String path: printPaths)
                    pom.print (path);
                
                for (String path: replacePaths)
                {
                    try
                    {
                        pom.replace (path);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException ("Can't replace in file "+pom.getFile ().getAbsolutePath (), e);
                    }
                }
            }
        }
    }
    
    private void readFile (File file)
    {
        System.out.println ("Reading "+file);
        POMFile pom = new POMFile (file);
        files.add (pom);
        
        File pomDir = file.getParentFile ();
        for (String moduleName: pom.getModules ())
        {
            File moduleDir = new File (pomDir, moduleName);
            File modulePom = new File (moduleDir, "pom.xml");
            readFile (modulePom);
        }
    }

    public static class POMFile
    {
        private File file;
        private Document doc;
        
        public POMFile (File file)
        {
            if (!file.exists ())
                throw new IllegalArgumentException ("File "+file.getAbsolutePath ()+" doesn't exist");
            
            this.file = file;
            getDoc ();
        }
        
        public boolean replace (String condition) throws IOException
        {
            int pos = condition.indexOf ('=');
            if (pos == -1)
            {
                System.out.println ("ERROR: No replacement text found");
                return false;
            }
            String elementPath = condition.substring (0, pos);
            String expected = condition.substring (pos+1);
            
            Element e = doc.getChild (elementPath);
            if (e == null)
            {
                System.out.println ("WARNING: Element "+elementPath+" not found.");
                return false;
            }
            
            String content = e.getTrimmedText ();
            if (expected.equals (content))
            {
                System.out.println ("INFO: Element already contains replacement text");
                return false;
            }

            e.setText (expected);
            
            File bak = new File (file.getAbsolutePath () + ".bak");
            File tmp = new File (file.getAbsolutePath () + ".tmp");
            
            if (tmp.exists ())
                if (!tmp.delete ())
                    throw new IOException ("Can't delete "+tmp.getAbsolutePath ());
            
	    String encoding = doc.getEncoding ();
	    if (encoding == null)
	    {
		encoding = "utf-8";
	    }
            XMLWriter writer = new XMLWriter (new OutputStreamWriter (new FileOutputStream (tmp), encoding));
            try
            {
                doc.toXML (writer);
            }
            finally
            {
                writer.close ();
            }
            
            if (bak.exists ())
                if (!bak.delete ())
                    throw new IOException ("Can't delete "+bak.getAbsolutePath ());
            if (!file.renameTo (bak))
                throw new IOException ("Can't rename "+file.getAbsolutePath ()+" to "+bak.getAbsolutePath ());
            if (!tmp.renameTo (file))
                throw new IOException ("Can't rename "+tmp.getAbsolutePath ()+" to "+file.getAbsolutePath ());
            
            System.out.println ("INFO: Element "+elementPath+" updated.");
            return true;
        }

        public File getFile ()
        {
            return file;
        }
        
        public void print (String path)
        {
            boolean printContent = false;
            if (path.endsWith ("/*"))
            {
                printContent = true;
                path = path.substring (0, path.length () - 2);
            }
            
            Element e = doc.getChild (path);
            if (e == null)
                System.out.println ("Element "+path+" not found");
            
            if (printContent)
                System.out.println (e.getTrimmedText ());
            else
                System.out.println (e.toXML ());
        }

        public boolean matches (List<String> searchConditions)
        {
            for (String searchCondition: searchConditions)
                if (!matches (searchCondition))
                    return false;
            
            return true;
        }

        public boolean matches (String condition)
        {
            int pos = condition.indexOf ('=');
            String elementPath = condition;
            String expected = null;
            if (pos != -1)
            {
                elementPath = condition.substring (0, pos);
                expected = condition.substring (pos+1);
            }
            
            Element e = doc.getChild (elementPath);
            if (e == null)
                return false;
            if (expected == null)
                return true;
            
            String content = e.getTrimmedText ();
            return expected.equals (content);
        }

        public boolean check (String condition)
        {
            int pos = condition.indexOf ('=');
            String elementPath = condition;
            String expected = "";
            if (pos != -1)
            {
                elementPath = condition.substring (0, pos);
                expected = condition.substring (pos+1);
            }
//            System.out.println (condition);
//            System.out.println ("expected="+expected);
            
            Element e = doc.getChild (elementPath);
            if (e == null)
            {
                System.out.println ("ERROR: Can't find element "+elementPath);
                return false;
            }
            
            String content = e.getTrimmedText ();
            if (!expected.equals (content))
            {
                System.out.println ("ERROR: "+elementPath+": expected '"+expected+"', found '"+content+"'");
                return false;
            }
            
            return true;
        }
        
        public List<String> getModules ()
        {
            Element modules = doc.getRootElement ().getChild ("modules");
            if (modules == null || !modules.hasChildren ())
                return Collections.emptyList ();
            
            List<String> result = new ArrayList<String> ();
            for (Element module: modules.getChildren ("module"))
            {
                result.add (module.getTrimmedText ());
            }
            
            return result;
        }

        public Document getDoc ()
        {
            if (doc == null)
            {
                XMLParser parser = new XMLParser ();
                try
                {
                    XMLIOSource source = new XMLIOSource (file);
                    doc = parser.parse (source);
                }
                catch (IOException e)
                {
                    throw new RuntimeException ("Error opening file "+file.getAbsolutePath ()+" for reading", e);
                }
            }
            
            return doc;
        }
    }
}
