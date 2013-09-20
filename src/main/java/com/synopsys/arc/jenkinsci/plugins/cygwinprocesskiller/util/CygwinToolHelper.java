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

import com.synopsys.arc.jenkinsci.plugins.customtools.CustomToolException;
import com.synopsys.arc.jenkinsci.plugins.customtools.EnvStringParseHelper;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Node;
import hudson.tools.ToolInstallation;
import java.io.File;

/**
 * Provides basic methods for Cygwin handling.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 */
public class CygwinToolHelper {
    public static FilePath getCygwinHome(ToolInstallation tool, Node node, EnvVars additionalVars) 
            throws CustomToolException
    {
        String home = EnvStringParseHelper.resolveExportedPath(tool.getHome(), node);
        if (additionalVars != null && additionalVars.size() != 0) {
            home = additionalVars.expand(home);
        }
        EnvStringParseHelper.checkStringForMacro("CYGWIN_HOME", home);
        
        // Get and check
        File cygwinHome = new File(home);
        if (!cygwinHome.exists()) {
            throw new CustomToolException("Cygwin home directory "+cygwinHome+" does not exist");
        }
        if (!cygwinHome.isDirectory() || !cygwinHome.isAbsolute()) {
            throw new CustomToolException("Cygwin home should be an absolute path to a directory");
        }
        return new FilePath(cygwinHome);
    }           
}
