/*******************************************************************************
 * Copyright (c) 2017 Gilles Querret
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gilles Querret - initial API and implementation and/or initial documentation
 *******************************************************************************/ 
package org.prorefactor.proparse.antlr4;

import java.io.FileNotFoundException;

public class IncludeFileNotFoundException extends FileNotFoundException {
  private static final long serialVersionUID = -6437738654876482735L;

  private final String sourceFileName;
  private final String includeName;

  public IncludeFileNotFoundException(String fileName, String incName) {
    super(fileName + " - Unable to find include file '" + incName + "'");
    this.sourceFileName = fileName;
    this.includeName = incName;
  }

  public String getFileName() {
    return sourceFileName;
  }

  public String getIncludeName() {
    return includeName;
  }
}