/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.synopsys.arc.jenkinsci.plugins.cygwinprocesskiller;

import hudson.Functions;
import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinProcessKillerPlugin extends Plugin {

    private boolean enableProcessKiller;
    private static final String PLUGIN_NAME="cygwin-process";
    private static final String SCRIPT_PATH="/plugin/"+PLUGIN_NAME+"/scripts/cygwin_killproc.bash";
    private static final int MAX_SCRIPT_SIZE=8192;
    
    transient String killScript;

    public String getKillScript() {
        return killScript;
    }

    public boolean isEnableProcessKiller() {
        return enableProcessKiller;
    }
    
    public static CygwinProcessKillerPlugin Instance() {
        Plugin plugin = Jenkins.getInstance().getPlugin(CygwinProcessKillerPlugin.class);
        return plugin != null ? (CygwinProcessKillerPlugin)plugin : null;
    }

    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, Descriptor.FormException {
        super.configure(req, formData); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void postInitialize() throws Exception {
        
        InputStream istream = ClassLoader.getSystemResourceAsStream(Functions.getResourcePath()+SCRIPT_PATH);
        if (istream != null) {
            Reader reader = new InputStreamReader(istream);
            CharBuffer buf = CharBuffer.allocate(MAX_SCRIPT_SIZE);
            reader.read(buf);
            killScript =  buf.toString();  
        } else {
            throw new IOException("Cannot find Cygwin process killer script");
        }
        //File rd = new File(g)
        //super.start();
    }
    
}
