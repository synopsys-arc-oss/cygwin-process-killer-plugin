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

import com.synopsys.arc.jenkinsci.plugins.cygwinprocesskiller.util.CygwinKillHelper;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import hudson.util.ProcessKiller;
import hudson.util.ProcessTree;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extension, which kills Cygwin process trees.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
@Extension
public class CygwinProcessKiller extends ProcessKiller {
    private static final Level KILLER_LOGGING_LEVEL = Level.WARNING;
    private static final String KILLER_LOGGER_NAME = "global";

    @Override
    public boolean kill(ProcessTree.OSProcess process) throws IOException, InterruptedException {       
        CygwinProcessKillerPlugin plugin = CygwinProcessKillerPlugin.Instance();
        if (!plugin.isEnableProcessKiller()) {
            return false;
        }
        
        // Init variables
        TaskListener listener = new LogTaskListener(Logger.getLogger(KILLER_LOGGER_NAME), KILLER_LOGGING_LEVEL);
        Node currentNode = Computer.currentComputer().getNode();
        CygwinKillerInstallation tool = plugin.getToolInstallation();
        
        // Run helper, which checks platform and then runs kill script
        CygwinKillHelper helper = new CygwinKillHelper(listener, currentNode, tool, process);
        return helper.isCygwin() ? helper.kill() : false;
    }    
}
