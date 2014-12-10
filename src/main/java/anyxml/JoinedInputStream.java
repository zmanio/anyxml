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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class allows to join several <code>InputStream</code>'s into one.
 * 
 * <p>Reading from an instance of <code>JoinedInputStream</code> will
 * read each underlying <code>InputStream</code> until it is
 * depleted and then continue with the next one.
 * 
 * <p>Depleted <code>InputStream</code>'s will be closed as soon
 * as possible.
 * 
 * @author digulla
 *
 */
public class JoinedInputStream extends InputStream
{
    private List<InputStream> streams = new ArrayList<InputStream> ();
    
    public JoinedInputStream ()
    {
        // Do nothing
    }
    
    /** Add another <code>InputStream</code>.
     * 
     *  <p>It is allowed to add more <code>InputStream</code>'s
     *  even after reading has started.
     *  
     *  <p>It is illegal to add more streams after the joined
     *  stream has been closed.
     */
    public JoinedInputStream add (InputStream in)
    {
        streams.add (in);
        return this;
    }

    @Override
    public int read () throws IOException
    {
        while (!streams.isEmpty ())
        {
            int result = streams.get (0).read ();
            if (result != -1)
                return result;
            
            popStream ();
        }
        
        return -1;
    }

    /**
     * Remove the current <code>InputStream</code> and close it.
     * 
     * @throws IOException
     */
    private void popStream () throws IOException
    {
        streams.remove (0).close ();
    }

    @Override
    public int read (byte[] b, int off, int len) throws IOException
    {
        while (!streams.isEmpty ())
        {
            int result = streams.get (0).read (b, off, len);
            if (result != -1)
                return result;
            
            popStream ();
        }
        
        return -1;
    }
    
    @Override
    public void close () throws IOException
    {
        while (!streams.isEmpty ())
        {
            popStream ();
        }
        
        // Make this class thow NPEs when being used after closing
        streams = null;
    }
}
