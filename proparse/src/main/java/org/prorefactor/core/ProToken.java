/********************************************************************************
 * Copyright (c) 2015-2018 Riverside Software
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU Lesser General Public License v3.0
 * which is available at https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-3.0
 ********************************************************************************/
package org.prorefactor.core;

import java.util.List;

import javax.annotation.Nonnull;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.WritableToken;
import org.prorefactor.core.ABLNodeType;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;

public class ProToken implements WritableToken {
  private static final String INVALID_TYPE = "Invalid type number ";

  // Fields coming from WritableToken
  private ABLNodeType type;
  private int line;
  private int charPositionInLine = -1; // set to invalid position
  private int channel = DEFAULT_CHANNEL;
  private String text;
  private int index = -1;

  private int fileIndex;
  private int endFileIndex;
  private String fileName;
  private int endLine;
  private int endCharPositionInLine;
  private int macroSourceNum;

  private String analyzeSuspend = null;
  private ProToken hiddenBefore = null;
  private boolean macroExpansion;
  private boolean synthetic = false;

  public ProToken(ABLNodeType type, String text) {
    this.type = type;
    this.channel = DEFAULT_CHANNEL;
    this.text = text;
    this.fileIndex = 0;
    this.charPositionInLine = 0;
  }

  @Override
  public int getType() {
    return type.getType();
  }

  @Override
  public void setType(int type) {
    this.type = ABLNodeType.getNodeType(type);
    if (this.type == null)
      throw new IllegalArgumentException(INVALID_TYPE + type);
  }

  public ABLNodeType getNodeType() {
    return type;
  }

  public int getFileIndex() {
    return fileIndex;
  }

  public int getMacroSourceNum() {
    return macroSourceNum;
  }

  public String getFileName() {
    return fileName;
  }

  public ProToken getNext() {
    throw new UnsupportedOperationException();
  }

  public ProToken getPrev() {
    return getHiddenBefore();
  }

  public int getEndLine() {
    return endLine;
  }

  public int getEndCharPositionInLine() {
    return endCharPositionInLine;
  }

  @Deprecated
  public int getEndColumn() {
    return endCharPositionInLine;
  }

  public int getEndFileIndex() {
    return endFileIndex;
  }

  /**
   * @return Comma-separated list of &amp;ANALYZE-SUSPEND options. Null for code not managed by AppBuilder.
   */
  public String getAnalyzeSuspend() {
    return analyzeSuspend;
  }

  /**
   * Returns true if last character of token was generated from a macro expansion, i.e. {&amp;SOMETHING}.
   * This doesn't mean that all characters were generated from a macro, e.g. {&prefix}VarName will return false 
   */
  public boolean isMacroExpansion() {
    return macroExpansion;
  }

  /**
   * @return True if token is part of an editable section in AppBuilder managed code
   */
  public boolean isEditableInAB() {
    return (analyzeSuspend == null) || isTokenEditableInAB(analyzeSuspend);
  }

  /**
   * @return True if token has been generated by ProParser and not by the lexer
   */
  public boolean isSynthetic() {
    return synthetic;
  }

  /**
   * @return True if token has been generated by the lexer and not by ProParser
   */
  public boolean isNatural() {
    return !synthetic;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ProToken) {
      ProToken tok = (ProToken) obj;
      return ((tok.type == this.type) && (tok.text.equals(this.text)) && (tok.line == this.line)
          && (tok.charPositionInLine == this.charPositionInLine) && (tok.fileIndex == this.fileIndex) && (tok.endFileIndex == this.endFileIndex)
          && (tok.endLine == this.endLine) && (tok.endCharPositionInLine == this.endCharPositionInLine)
          && (tok.macroSourceNum == this.macroSourceNum));
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type.toString(), text, line, charPositionInLine, fileIndex, endFileIndex, endLine, endCharPositionInLine, macroSourceNum);
  }

  @Override
  public String toString() {
    return "[\"" + text.replace('\r', ' ').replace('\n', ' ') + "\",<" + type + ">,macro=" + macroSourceNum + ",start="
        + fileIndex + ":" + line + ":" + charPositionInLine + ",end=" + endFileIndex + ":" + endLine + ":"
        + endCharPositionInLine + "]";
  }

  /**
   * @return True if token is part of an editable section in AppBuilder managed code
   */
  public static boolean isTokenEditableInAB(@Nonnull String str) {
    List<String> attrs = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(str);
    if (attrs.isEmpty() || !"_UIB-CODE-BLOCK".equalsIgnoreCase(attrs.get(0)))
      return false;

    if ((attrs.size() >= 3) && "_CUSTOM".equalsIgnoreCase(attrs.get(1))
        && "_DEFINITIONS".equalsIgnoreCase(attrs.get(2)))
      return true;
    else if ((attrs.size() >= 2) && "_CONTROL".equalsIgnoreCase(attrs.get(1)))
      return true;
    else if ((attrs.size() == 4) && "_PROCEDURE".equals(attrs.get(1)))
      return true;
    else if ((attrs.size() == 5) && "_PROCEDURE".equals(attrs.get(1)) && "_FREEFORM".equals(attrs.get(4)))
      return true;
    else if ((attrs.size() >= 2) && "_FUNCTION".equals(attrs.get(1)))
      return true;

    return false;
  }

  public void setEndFileIndex(int endFileIndex) {
    this.endFileIndex = endFileIndex;
  }

  public void setEndLine(int endLine) {
    this.endLine = endLine;
  }

  public void setEndCharPositionInLine(int endCharPositionInLine) {
    this.endCharPositionInLine = endCharPositionInLine;
  }

  public void setMacroSourceNum(int macroSourceNum) {
    this.macroSourceNum = macroSourceNum;
  }

  public void setFileIndex(int fileIndex) {
    this.fileIndex = fileIndex;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setAnalyzeSuspend(String analyzeSuspend) {
    this.analyzeSuspend = analyzeSuspend;
  }

  public void setMacroExpansion(boolean macroExpansion) {
    this.macroExpansion = macroExpansion;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Deprecated
  public int getColumn() {
    return charPositionInLine;
  }

  @Override
  public int getChannel() {
    return channel;
  }

  @Override
  public int getTokenIndex() {
    return index;
  }

  @Override
  public int getStartIndex() {
    return -1;
  }

  @Override
  public int getStopIndex() {
    return -1;
  }

  @Override
  public TokenSource getTokenSource() {
    return null;
  }

  @Override
  public CharStream getInputStream() {
    return null;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  public void setSynthetic(boolean synthetic) {
    this.synthetic = synthetic;
  }

  public void setNodeType(ABLNodeType type) {
    if (type == null)
      throw new IllegalArgumentException(INVALID_TYPE + type);
    this.type = type;
  }

  @Override
  public void setLine(int line) {
    this.line = line;
  }

  @Override
  public void setCharPositionInLine(int pos) {
    this.charPositionInLine = pos;
  }

  @Override
  public void setChannel(int channel) {
    this.channel = channel;
  }

  @Override
  public void setTokenIndex(int index) {
    this.index = index;
  }

  public void setHiddenBefore(ProToken hiddenBefore) {
    this.hiddenBefore = hiddenBefore;
  }

  public ProToken getHiddenBefore() {
    return hiddenBefore;
  }
}
