/*  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.pluto.container.om.portlet.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.pluto.container.om.portlet.Description;
import org.apache.pluto.container.om.portlet.PublicRenderParameter;

/**
 * Public render parameters
 * 
 * @author Scott Nicklous
 *
 */
public class PublicRenderParameterImpl implements PublicRenderParameter {
   
   private QName qname;
   private String id;
   private final List<QName> aliases = new ArrayList<QName>();
   private final List<Description> descs = new ArrayList<Description>();

   /**
    * Copy constructor
    * @param edi
    */
   public PublicRenderParameterImpl(PublicRenderParameter pri) {
      QName priqn = pri.getQName();
      this.qname = new QName(priqn.getNamespaceURI(), priqn.getLocalPart());
      this.id = pri.getIdentifier();
      for (QName qn : pri.getAliases()) {
         this.aliases.add(new QName(qn.getNamespaceURI(), qn.getLocalPart()));
      }
      for (Description desc : pri.getDescriptions()) {
         descs.add(new DescriptionImpl(desc));
      }
   }
   
   /**
    * Basic constructor
    */
   public PublicRenderParameterImpl(QName qn, String id) {
      this.qname = qn;
      this.id = id;
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#getQName()
    */
   @Override
   public QName getQName() {
      return qname;
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#getIdentifier()
    */
   @Override
   public String getIdentifier() {
      return id;
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#getDescription(java.util.Locale)
    */
   @Override
   public Description getDescription(Locale locale) {
      Description ret = null;
      for (Description desc : descs) {
         if (desc.getLocale().equals(locale)) {
            ret = desc;
         }
      }
      return ret;
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#getDescriptions()
    */
   @Override
   public List<Description> getDescriptions() {
      return new ArrayList<Description>(descs);
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#addDescription(org.apache.pluto.container.om.portlet.Description)
    */
   @Override
   public void addDescription(Description desc) {
      descs.add(desc);
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#getAliases()
    */
   @Override
   public List<QName> getAliases() {
      return new ArrayList<QName>(aliases);
   }

   /* (non-Javadoc)
    * @see org.apache.pluto.container.om.portlet.PublicRenderParameter#addAlias(javax.xml.namespace.QName)
    */
   @Override
   public void addAlias(QName qName) {
      aliases.add(qName);
   }

}
