/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.jsf.cfg;

import com.caucho.config.types.DescriptionGroupConfig;
import java.util.*;

import javax.annotation.*;

import javax.el.*;

import javax.faces.*;
import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.component.html.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.faces.el.*;
import javax.faces.event.*;
import javax.faces.render.*;
import javax.faces.validator.*;

import java.lang.reflect.*;

import com.caucho.config.*;
import com.caucho.config.j2ee.*;
import com.caucho.jsf.el.*;
import com.caucho.util.*;

public class RenderKitConfig extends DescriptionGroupConfig
{
  private static final L10N L = new L10N(RenderKitConfig.class);

  private String _name = RenderKitFactory.HTML_BASIC_RENDER_KIT;
  private Class _class;

  private RenderKit _renderKit;
  
  private ArrayList<RendererConfig> _rendererList
    = new ArrayList<RendererConfig>();

  public void setName(String name)
  {
    _name = name;
  }

  public void setRenderKitId(String name)
  {
    setName(name);
  }

  public void setClass(Class cl)
  {
    Config.validate(cl, RenderKit.class);
    
    _class = cl;
  }

  public void setRenderKitClass(Class cl)
  {
    setClass(cl);
  }

  public void addRenderer(RendererConfig renderer)
  {
    _rendererList.add(renderer);
  }

  @PostConstruct
  public void init()
    throws InstantiationException,
	   IllegalAccessException
  {
    if (_class != null)
      _renderKit = (RenderKit) _class.newInstance();
  }

  public void configure()
  {
    RenderKitFactory factory
      = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

    FacesContext context = new DummyFacesContext();
    
    RenderKit oldRenderKit = factory.getRenderKit(context, _name);
    RenderKit renderKit = oldRenderKit;

    if (_class != null) {
      if (oldRenderKit != null) {
	try {
	  Constructor ctor
	    = _class.getConstructor(new Class[] { RenderKit.class });

	  renderKit = (RenderKit) ctor.newInstance(oldRenderKit);
	} catch (NoSuchMethodException e) {
	} catch (RuntimeException e) {
	  throw e;
	} catch (Exception e) {
	  throw ConfigException.create(e);
	}
      }

      try {
	renderKit = (RenderKit) _class.newInstance();
      } catch (RuntimeException e) {
	throw e;
      } catch (Exception e) {
	throw ConfigException.create(e);
      }

      if (_name == null)
	throw new ConfigException(L.l("'{0}' is an unknown render-kit-id.",
				      _name));
	
      factory.addRenderKit(_name, renderKit);
    }

    if (renderKit == null)
      throw new ConfigException(L.l("'{0}' is an unknown render-kit-id.",
				    _name));
    
    for (RendererConfig renderer : _rendererList)
      renderer.configure(renderKit);
  }
}
