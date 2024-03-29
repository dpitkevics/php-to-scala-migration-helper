/*
 * Copyright (c) 1998-2000 Caucho Technology -- all rights reserved
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
 * @author Emil Ong
 */

package com.caucho.xtpdoc;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class ContainerNode implements ContentItem {
  private static Logger log = Logger.getLogger(ContainerNode.class.getName());

  private Document _document;
  private ArrayList<ContentItem> _items = new ArrayList<ContentItem>();
  private boolean _isWebOnly;

  public ContainerNode(Document document)
  {
    _document = document;
  }

  public Document getDocument()
  {
    return _document;
  }

  public void setWebOnly(boolean isWebOnly)
  {
    _isWebOnly = true;
  }

  public boolean isWebOnly()
  {
    return _isWebOnly;
  }

  public boolean isArticle()
  {
    return _document.isArticle();
  }

  protected void addItem(ContentItem item)
  {
    _items.add(item);
  }

  protected ArrayList<ContentItem> getItems()
  {
    return _items;
  }

  public void writeHtml(XMLStreamWriter out)
    throws XMLStreamException
  {
    for (ContentItem item : _items)
      item.writeHtml(out);
  }

  public void writeLaTeXTop(PrintWriter out)
    throws IOException
  {
    for (ContentItem item : _items)
      item.writeLaTeXTop(out);
  }

  public void writeLaTeX(PrintWriter out)
    throws IOException
  {
    for (ContentItem item : _items)
      item.writeLaTeX(out);
  }

  public void writeLaTeXEnclosed(PrintWriter out)
    throws IOException
  {
    for (ContentItem item : _items)
      item.writeLaTeXEnclosed(out);
  }

  public void writeLaTeXArticle(PrintWriter out)
    throws IOException
  {
    for (ContentItem item : _items)
      item.writeLaTeXTop(out);
  }
}
