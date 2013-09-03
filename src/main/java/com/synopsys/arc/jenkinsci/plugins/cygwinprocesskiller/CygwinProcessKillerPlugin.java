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

import hudson.Plugin;
import hudson.model.Hudson;
import java.io.File;
import java.io.InputStream;
import jenkins.model.Jenkins;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinProcessKillerPlugin extends Plugin {

    private static final String PLUGIN_NAME="cygwin-process";
    transient String killScript;

    public String getKillScript() {
        return killScript;
    }
    
    public static CygwinProcessKillerPlugin Instance() {
        Plugin plugin = Jenkins.getInstance().getPlugin(CygwinProcessKillerPlugin.class);
        return plugin != null ? (CygwinProcessKillerPlugin)plugin : null;
    }
    
    @Override
    public void postInitialize() throws Exception {
        File file = new File(Hudson.getInstance().getRootUrl()+"plugin/"+PLUGIN_NAME+"/scripts/cygwin_killproc.sh");
        if (file.exists()) {
            
        }
        //File rd = new File(g)
        //super.start();
    }
    
}
