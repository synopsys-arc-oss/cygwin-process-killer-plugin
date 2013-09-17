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
import com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper;
import com.synopsys.arc.jenkinsci.plugins.customtools.multiconfig.MulticonfigWrapperOptions;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Provides integration with Custom-Tools plug-in.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinInstallation implements Serializable, Describable<CygwinInstallation> {
    private String name;

    @DataBoundConstructor
    public CygwinInstallation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
  
    @Override
    public Descriptor<CygwinInstallation> getDescriptor() {
        return DESCRIPTOR;
    }
     
    static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    
    @Extension
    public static class DescriptorImpl extends Descriptor<CygwinInstallation> {

        @Override
        public String getDisplayName() {
            return "Cygwin Installation";
        }
        
        public static CustomTool[] getCustomToolInstallations() {
            return Hudson.getInstance().getDescriptorByType(CustomTool.DescriptorImpl.class).getInstallations();      
        }
    }
}
