/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 * XMediaStream is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * XMediaStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.util;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.Element;
import java.io.IOException;
import java.util.*;
import org.themonkeysdidit.datastructures.TreeNode;

/**
******************************************************************************
**
** The XMLConfigReader provides a simple way to read an XML file to be used
** as a config file.
**
** As an example, if we have the following XML document:
** <code><br />
** &lt;shoutcast_client&gt<br />
** &lt;mysql&gt<br />
** &lt;hostname&gt xorn42 &lt;/hostname&gt<br />
** &lt;/mysql&gt<br />
** &lt;/shoutcast_client&gt<br />
** </code>
** 
** We would extract the hostname by calling:<br />
** <code>
** enterNode(mysql)<br />
** enterNode(hostname)<br />
** getValue()
** </code>
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XML_ConfigReader implements ConfigReader {

    /**
     * Construct a config file object from the specified xml file.
     *
     * @param configFile The config file to create the config object from.
     */
    public XML_ConfigReader(String configFile) {
        
        //Logger.log("INFO", "Reading config from: " + configFile);
        
        NODE_STORE = new Stack<TreeNode>();
        
        try {
            SAXBuilder builder = new SAXBuilder();
            CONFIG_DOC = builder.build(configFile);
        }
        catch(IOException io) {
            Logger.log("WARN", io.getMessage());
        }
        catch(JDOMException jdome) {
            Logger.log("WARN", jdome.getMessage());
        }
        
        // Now we have the Document, need to grab all the elements
        // and store them in a useful way.
        storeConfig();
        
        // Now lets sneakily update the system property to what ever logging
        // level was there in the config
        updateLogLevel();

    }
    
    /**
     * Enter the XML node in the config file.
     *
     * @param name The name of the node to enter.
     * @return True if the node exists at this level and can be entered, false
     * otherwise.
     */
    public boolean enterNode(String name) {
        TreeNode current = NODE_STORE.peek();
        if(current.getChildNode(name) != null) {
            NODE_STORE.push(current.getChildNode(name));
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Exit the current node.
     */
    public void exitNode() {
        NODE_STORE.pop();
    }
    
    /**
     * Get the value of the current node.
     *
     * @return The value of the current node.
     */
    public String getValue() {
        return NODE_STORE.peek().getValue();
    }
    
    private void storeConfig() {
        NODE_STORE.push(processDocument(CONFIG_DOC.getRootElement()));
    }
    
    private TreeNode processDocument(Element e) {
        TreeNode t = new TreeNode(e.getName());
        if(e.getText().trim().compareTo("") != 0) {
            t.setValue(e.getText().trim());
        }
                
        List children = e.getChildren();
        Iterator i = children.iterator();
        
        while(i.hasNext()) {
            Object o = (Element)i.next();
            if(o instanceof Element) {
                t.addChildNode(processDocument((Element) o));
            }
        }
        
        return t;
    }
    
    private void updateLogLevel() {
        // If it has been defined in config:
        if(enterNode("logging")) {
            if(enterNode("enabled_levels")) {
                Logger.setLevel(getValue());
                exitNode();
            }
            exitNode();
        }
    }
    
    private Document CONFIG_DOC;
    private Stack<TreeNode> NODE_STORE;

}
