/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pluto.driver.services.container;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.pluto.container.PortletURLProvider;
import org.apache.pluto.container.PortletWindow;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.driver.url.PortalURL.URLType;
import org.apache.pluto.driver.url.PortalURLParameter;
import org.apache.pluto.driver.url.PortalURLPublicParameter;

/**
 *
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private final Logger LOGGER = LoggerFactory.getLogger(PortletURLProviderImpl.class);

    private final PortalURL url;
    private final TYPE type;
    private final String window;
    private PortletMode portletMode;
    private WindowState windowState;
    private String cacheLevel;
    private String resourceID;
    private Map<String, String[]> renderParameters;
    private Map<String, List<String>> properties;
    
    private final Set<PortalURLPublicParameter> prpSet = new HashSet<PortalURLPublicParameter>();
    
    /**
     * Returns <code>true</code> if given parameter name is a public render parameter
     * 
     * @param name
     */
    private boolean isPRP(String name) {
       boolean found = false;
       for (PortalURLPublicParameter prp : prpSet) {
          if (name.equals(prp.getName())) {
             found = true;
             break;
          }
       }
       return found;
    }

    public PortletURLProviderImpl(PortalURL url, TYPE type, PortletWindow portletWindow)
    {
        this.url = url;
        this.type = type;
        this.window = portletWindow.getId().getStringId();
    }
    
    public PortalURL apply()
    {
        PortalURL url = this.url.clone();
        if (PortletURLProvider.TYPE.ACTION == type)
        {
           url.setTargetWindow(window);
           url.setType(URLType.Action);
        }
        else if (PortletURLProvider.TYPE.RESOURCE == type)
        {
           url.setTargetWindow(window);
           url.setType(URLType.Resource);
            if (!ResourceURL.FULL.equals(cacheLevel))
            {
                for (PortalURLParameter parm : url.getParameters())
                {
                    if (window.equals(parm.getWindowId()))
                    {
                        url.getPrivateRenderParameters().put(parm.getName(), parm.getValues());
                    }                            
                }
            }
            url.clearParameters(window);
        }
        else
        {
            url.setTargetWindow(window);
            url.setType(URLType.Render);
            url.clearParameters(window);
        }
        if (portletMode != null)
        {
            url.setPortletMode(window, portletMode);
        }
        if (windowState != null)
        {
            url.setWindowState(window, windowState);
        }
        if (renderParameters != null)
        {
            for (Map.Entry<String,String[]> entry : renderParameters.entrySet())
            {
                if (!isPRP(entry.getKey()))
                {
                    url.addParameter(new PortalURLParameter(window, entry.getKey(), entry.getValue()));
                }
            }
        }
        for (PortalURLPublicParameter prp : prpSet) {
           url.addPublicRenderParameter(prp);
        }
        url.setResourceID(resourceID);
        url.setCacheability(cacheLevel);
        return url;
    }
    
    public TYPE getType()
    {
        return type;
    }
    
    public void setPortletMode(PortletMode mode)
    {
        this.portletMode = mode;
    }
    
    public PortletMode getPortletMode()
    {
        return portletMode;
    }

    public void setWindowState(WindowState state)
    {
        this.windowState = state;
    }

    public WindowState getWindowState()
    {
        return windowState;
    }

    public void setSecure(boolean secure) throws PortletSecurityException {
        // ignore: not supported
    }
    
    public boolean isSecure()
    {
        return false;
    }
    
    public Map<String,String[]> getRenderParameters()
    {
        if (renderParameters == null)
        {
            renderParameters = new HashMap<String,String[]>();
        }
        return renderParameters;
    }
    
    public String getCacheability()
    {
        return cacheLevel;
    }

    public void setCacheability(String cacheLevel)
    {
        this.cacheLevel = cacheLevel;
    }

    public String getResourceID()
    {
        return resourceID;
    }

    public void setResourceID(String resourceID)
    {
        this.resourceID = resourceID;
    }
    
    public String toURL()
    {
        return toURL(false);
    }

    public String toURL(boolean absolute)
    {
        return apply().toURL(absolute);
    }

    public void write(Writer out, boolean escapeXML) throws IOException
    {
        String result = apply().toURL(false);
        if (escapeXML)
        {
            result = result.replaceAll("&", "&amp;");
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
            result = result.replaceAll("\'", "&#039;");
            result = result.replaceAll("\"", "&#034;");
        }
        out.write(result);
    }

    public Map<String, List<String>> getProperties()
    {
        if (properties == null)
        {
            properties = new HashMap<String, List<String>>();
        }
        return properties;
    }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.PortletURLProvider#addPublicRenderParameter(javax.xml.namespace.QName, java.lang.String, java.lang.String[])
    */
   public void addPublicRenderParameter(QName qn, String identifier, String[] values) 
   {
       LOGGER.debug("Add PRP. QName = " + qn.toString() + ", ID = " + identifier
             + ", values = " + Arrays.toString(values));
       PortalURLPublicParameter pupp = new PortalURLPublicParameter(window, identifier, qn, values);
       prpSet.add(pupp);
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.PortletURLProvider#removePublicRenderParameter(javax.xml.namespace.QName)
    */
   public void removePublicRenderParameter(QName qn, String identifier) 
   {
       LOGGER.debug("Remove PRP. QName = " + qn.toString());
       PortalURLPublicParameter pupp = new PortalURLPublicParameter(window, identifier, qn);
       pupp.setRemoved(true);
       prpSet.add(pupp);
   }

   /**
    * Clear public render parameters except those marked for removal 
    */
   public void clearPublicRenderParameters() {
      for (PortalURLPublicParameter prp : prpSet) {
         if (!prp.isRemoved()) {
            prpSet.remove(prp);
         }
      }
   }
}
