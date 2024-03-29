/*
 * Copyright (c) 1998-2003 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package javax.el;

import java.util.HashMap;
import java.util.Locale;

/**
 * ELContext a context for EL resolution.
 */
public abstract class ELContext {
  private HashMap<Class,Object> _contextMap;

  private Locale _locale;

  private boolean _isPropertyResolved;
  
  public ELContext()
  {
  }

  public Object getContext(Class key)
  {
    if (key == null)
      throw new NullPointerException();
    else if (_contextMap != null)
      return _contextMap.get(key);
    else
      return null;
  }

  public void putContext(Class key, Object contextObject)
  {
    if (key == null)
      throw new NullPointerException();

    if (_contextMap == null)
      _contextMap = new HashMap<Class,Object>(8);
    
    _contextMap.put(key, contextObject);
  }

  abstract public ELResolver getELResolver();

  abstract public FunctionMapper getFunctionMapper();

  public Locale getLocale()
  {
    return _locale;
  }

  abstract public VariableMapper getVariableMapper();

  public boolean isPropertyResolved()
  {
    return _isPropertyResolved;
  }

  public void setLocale(Locale locale)
  {
    _locale = locale;
  }

  public void setPropertyResolved(boolean resolved)
  {
    _isPropertyResolved = resolved;
  }
}
