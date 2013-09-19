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

import com.cloudbees.jenkins.plugins.customtools.CustomTool;
import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.tools.ToolInstallation;
import java.io.IOException;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Cygwin Process Killer plugin.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinProcessKillerPlugin extends Plugin {

    private String killScript;
    private boolean enableProcessKiller;
    private CygwinInstallation cygwinInstallation;
    
    private static final String PLUGIN_NAME="cygwin-process";
    private static final String SCRIPT_PATH="/plugin/"+PLUGIN_NAME+"/scripts/cygwin_killproc.bash";
    private static final int MAX_SCRIPT_SIZE=8192;
    
    
    public String getKillScript() {
        return killScript;
    }

    public boolean isEnableProcessKiller() {
        return enableProcessKiller;
    }

    public CygwinInstallation getCygwinInstallation() {
        return cygwinInstallation;
    }
    
    public static CygwinProcessKillerPlugin Instance() {
        Plugin plugin = Jenkins.getInstance().getPlugin(CygwinProcessKillerPlugin.class);
        return plugin != null ? (CygwinProcessKillerPlugin)plugin : null;
    }

    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, Descriptor.FormException {
        this.enableProcessKiller = formData.getBoolean("enableProcessKiller");
        this.killScript = formData.getString("killScript");
        this.cygwinInstallation = req.bindJSON(CygwinInstallation.class, formData.getJSONObject("cygwinInstallation"));
        save();
    }
    
    @Override 
    public void start() throws Exception {
        super.load();
    }
    
    /*@Override
    public void postInitialize() throws Exception {     
        if (killScript != null) {
            return;
        }
        
        InputStream istream = ClassLoader.getSystemResourceAsStream(Functions.getResourcePath()+SCRIPT_PATH);
        if (istream != null) {
            Reader reader = new InputStreamReader(istream);
            CharBuffer buf = CharBuffer.allocate(MAX_SCRIPT_SIZE);
            reader.read(buf);
            killScript =  buf.toString();  
        } 
    } */
    
    public CygwinInstallation.DescriptorImpl getCygwinInstallationDescriptor() {
        return CygwinInstallation.DESCRIPTOR;
    }
    
    public ToolInstallation getToolInstallation() {
        if (cygwinInstallation == null) {
            return null;
        }
        
        CustomTool.DescriptorImpl descriptor = (CustomTool.DescriptorImpl) Hudson.getInstance().getDescriptor(CustomTool.class);
        CustomTool[] installations = descriptor.getInstallations();
        for (CustomTool inst : installations) {
            if (inst.getName().equals(cygwinInstallation.getName())) {
                return inst;
            }
        }
        
        // Installation not found
        return null;
    }
}
