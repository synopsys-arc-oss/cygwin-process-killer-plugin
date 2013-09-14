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
package com.synopsys.arc.jenkinsci.plugins.cygwinprocesskiller.util;

import com.cloudbees.jenkins.plugins.customtools.CustomTool;
import com.synopsys.arc.jenkinsci.plugins.cygwinprocesskiller.CygwinProcessKillerPlugin;
import hudson.FilePath;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.util.NullStream;
import hudson.util.ProcessTree;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.SystemUtils;

/**
 * Class provides basic Cygwin operations.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinKillHelper {
    private final TaskListener log;
    private final Node node;
    private final CustomTool tool;
    
    
    //
    private final CygwinProcessKillerPlugin plugin = CygwinProcessKillerPlugin.Instance();
    private FilePath tmpDir;
    
    private static final String CYGWIN_START_PREFIX="CYGWIN_";  
    private static final String CYGWIN_BINARY_PATH="\\bin\\";
    private static final int WAIT_TIMEOUT_SEC=500;
    
    public CygwinKillHelper(TaskListener log, Node node, CustomTool tool) {
        this.log = log;
        this.node = node;
        this.tool = tool;
    }
    
    /**
     * Checks that Cygwin is available at the host.
     * @throws IOException
     * @throws InterruptedException 
     */
    public boolean isCygwin() throws IOException, InterruptedException {
        //
        if (!SystemUtils.IS_OS_WINDOWS) {
            return false;
        }
        
        OutputStream str = new ByteArrayOutputStream();
        execCommand("uname", str, "-a");
 
        return str.toString().startsWith(CYGWIN_START_PREFIX);
    }

    public FilePath getTmpDir() throws IOException, InterruptedException {
        if (tmpDir == null) {
            tmpDir = findTmpDir(node);
        }
        return tmpDir;
    }
    
    public int execScript(String script, OutputStream out, String ... args) 
            throws IOException, InterruptedException {
        
        // Prepare a temp file
        FilePath tmpFile = getTmpDir().createTempFile("cygwin_process_killer_", ".sh");       
        tmpFile.write(script, null);
  
        String[] cmd = new String[1+args.length];
        cmd[0] = tmpFile.getRemote();
        System.arraycopy(args, 0, cmd, 1, args.length);
    
        return execCommand("sh", out, cmd);     
    }
    
    public int execCommand(String command, OutputStream stdout, String ... args) throws IOException, InterruptedException {
        String[] cmd = new String[1+args.length];
        cmd[0] = getCygwinBinaryCommand(command);
        System.arraycopy(args, 0, cmd, 1, args.length);
    
        ProcStarter starter = node.createLauncher(log).launch().cmds(cmd).stdout(stdout).pwd(getTmpDir());
        Proc proc = starter.start();
        int resultCode = proc.joinWithTimeout(WAIT_TIMEOUT_SEC, TimeUnit.SECONDS, log);
        starter.readStdout();
        return resultCode;
    }
    
    public boolean kill(ProcessTree.OSProcess process) throws IOException, InterruptedException {
        OutputStream str = new ByteArrayOutputStream();
        int res = execScript(plugin.getKillScript(), str, Integer.toString(process.getPid()));
        
        if (res != 0) {
            log.error("CygwinKiller cannot kill the process tree (parent pid="+process.getPid()+")");
        }
        return res != 0;
    }
    
    public String getCygwinBinaryCommand(String commandName) {
        return tool != null ? tool.getHome()+CYGWIN_BINARY_PATH+commandName+".exe" : commandName+".exe"; 
    }
    
    public static FilePath findTmpDir(Node node) throws IOException, InterruptedException {
        if (node == null) {
            throw new IllegalArgumentException("must pass non-null node");
        }
        
        FilePath root = node.getRootPath();
        if (root == null) {
            throw new IllegalArgumentException("Node " + node.getDisplayName() + " seems to be offline");
        }
        
        FilePath tmpDir = root.child("cygwin_process_killer").child("tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return tmpDir;
    }
    
    
}
