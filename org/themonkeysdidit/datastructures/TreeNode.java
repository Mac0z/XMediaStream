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
 
package org.themonkeysdidit.datastructures;

import java.util.Vector;
import java.util.Enumeration;

/**
******************************************************************************
**
** The TreeNode class is used to model the node of a tree data structure
** in the simplest way possible. It has a name (the name of this node)
** and an optional value (the value of this node) and an optional
** Vector of child nodes (each of which is a TreeNode). It is worth noting
** a TreeNode can have botha  value and a set of child TreeNodes.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class TreeNode {
    
    /**
    * Creates an instance of the TreeNode class. The mandatory name is the
    * name of this node.
    *
    * @param name The name of this TreeNode.
    */
    public TreeNode(String name) {
        NAME = name;
        VALUE = new String();
        CHILD_NODES = new Vector<TreeNode>();
    }
    
    /**
    * Add another TreeNode as a child node of this one.
    *
    * @param node The TreeNode to add as a child.
    */
    public void addChildNode(TreeNode node) {
        CHILD_NODES.addElement(node);
    }
    
    /**
    * Set the value of this TreeNode.
    *
    * @param value The value to assign to this node (always a String).
    */
    public void setValue(String value) {
        VALUE = value;
    }
    
    /**
    * Get the name of this TreeNode.
    *
    * @return Returns name of this TreeNode instance.
    */
    public String getName() {
        return NAME;
    }
    
    /**
    * Get the value of this TreeNode.
    *
    * @return Returns the value of this TreeNode if it was set with setValue(),
    *         null otherwise.
    */
    public String getValue() {
        return VALUE;
    }
    
    /**
    * Get a specified child node of the TreeNode.
    *
    * @param name The name of the child TreeNode to return.
    * @return Returns the names child TreeNode if it exists as a child of this
    *         node, null otherwise.
    */
    public TreeNode getChildNode(String name) {
        Enumeration enumer = CHILD_NODES.elements();
        while(enumer.hasMoreElements()) {
            TreeNode t = (TreeNode)enumer.nextElement();
            if(t.getName().compareTo(name) == 0) {
                return t;
            }
        }
        
        // Node doesn't exist as a child of this node, return null.
        return null;
    }
    
    private Vector<TreeNode> CHILD_NODES;
    private String NAME;
    private String VALUE;
}
