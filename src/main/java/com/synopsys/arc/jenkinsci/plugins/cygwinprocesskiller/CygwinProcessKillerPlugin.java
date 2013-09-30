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
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
    private transient String defaultKillScript;
    
    public static final String PLUGIN_NAME="cygwin-process";
    private static final String KILLSCRIPT_NAME="cygwin_killproc.bash";    
    
    public String getKillScript() {
        return killScript;
    }
    
    public String getDefaultKillScript() {
        return defaultKillScript;
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
    
    @Override
    public void postInitialize() throws Exception {     
        InputStream str = CygwinProcessKillerPlugin.class.getResourceAsStream(KILLSCRIPT_NAME);
        StringWriter writer = new StringWriter();
        IOUtils.copy(str, writer);
        defaultKillScript = writer.toString();
    } 
    
    public CygwinInstallation.DescriptorImpl getCygwinInstallationDescriptor() {
        return CygwinInstallation.DESCRIPTOR;
    }
    
    public CygwinKillerInstallation getToolInstallation() {
        if (cygwinInstallation == null) {
            return null;
        }
        
        CygwinKillerInstallation.DescriptorImpl descriptor = (CygwinKillerInstallation.DescriptorImpl) Hudson.getInstance().getDescriptor(CygwinKillerInstallation.class);
        CygwinKillerInstallation[] installations = descriptor.getInstallations();
        for (CygwinKillerInstallation inst : installations) {
            if (inst.getName().equals(cygwinInstallation.getName())) {
                return inst;
            }
        }
        
        // Installation not found
        return null;
    }
}
