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

import hudson.Extension;
import hudson.util.ProcessKiller;
import hudson.util.ProcessTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import org.apache.commons.lang.SystemUtils;

/**
 * Extension, which kills Cygwin processes.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
@Extension
public class CygwinProcessKiller extends ProcessKiller {
    private static final String CYGWIN_START_PREFIX="CYGWIN_";    
    
    @Override
    public boolean kill(ProcessTree.OSProcess process) throws IOException, InterruptedException {       
       return isCygwin() ? doKill(process) : false;
    }
    
    /**
     * Check that Cygwin is available at the host
     * @throws IOException
     * @throws InterruptedException 
     */
    public boolean isCygwin() throws IOException, InterruptedException {
        //
        if (!SystemUtils.IS_OS_WINDOWS) {
            return false;
        }
        
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("uname -a");
        p.waitFor();
    
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));

        if (b.ready()) {
            String output = b.readLine();
            return output.startsWith(CYGWIN_START_PREFIX);
        }
        return false;
    }
    
    public boolean doKill(ProcessTree.OSProcess process) throws IOException, InterruptedException {
        // Prepare kill script
        File tmpFile = File.createTempFile("cygwin_process_killer_", ".sh");
        Writer w = new FileWriter(tmpFile);
        w.write(CygwinProcessKillerPlugin.Instance().getKillScript());
        w.close();

        // Execute
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("sh " + tmpFile.getAbsolutePath());
        p.waitFor();
    
        return true;
    }
}
