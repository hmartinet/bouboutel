/*
 * StringTokenizer.java
 *
 * Created on October 25, 2005, 8:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package key;

import java.util.*;


/**
 *
 * @author sdperret
 */
public class StringTokenizerStrict extends StringTokenizer
{
  protected String delim  = "\t\n\r";
  protected String storedToken = null;
  protected boolean lastWasToken = true;
  protected ArrayList<String> tokens = null;
  protected int currentToken = 0;
  protected boolean returnTokens = false;
  protected boolean internal = false;
  protected synchronized void buildInternal()
  {
    tokens = new ArrayList<String>();
    try {
      while ( true ) {      
        tokens.add(nextTokenInternal());
      }
    } catch ( NoSuchElementException e ) {
      //tokens.trimToSize();
      currentToken = 0;
      lastWasToken = true;
      storedToken = null;
      return;
    }
  }
  public StringTokenizerStrict(String s)
  {
    this(s,"\t\n\r");
  }
  public StringTokenizerStrict(String s,String delim)
  {
    this(s,delim,false);
  }
  public StringTokenizerStrict(String s,String delim,boolean returnTokens)
  {
    super (s,delim,true);
    this.delim = delim;    
    this.returnTokens = returnTokens;
    buildInternal();
  }
  public int countTokens()
  {
    return tokens.size() - currentToken;
  }
  public boolean hasMoreElements()
  {
    return ( countTokens() != 0 );
  }
  public Object nextElement()
  {
    return nextToken();
  }

  public String nextToken(String delim)
  {
    if ( delim.equals(this.delim) )
      return nextToken();
    this.delim = delim;
    buildInternal();
    return nextToken();
  }
  public String nextToken()
  {
    if ( internal )
      return super.nextToken();
    
    if( currentToken+1 > tokens.size() ){
        return "";
    }else{ 
        return (String) tokens.get(currentToken++);
    }
  }
  protected String nextTokenInternal()
  {
    try {
      internal = true;
      if ( storedToken != null ) {
String s = storedToken;
storedToken = null;
return s;
      }
      String s = super.nextToken(delim);
      if ( s.length() == 1 )
if ( delim.indexOf(s) != -1 )
  if ( lastWasToken ) {
    if ( returnTokens )
      storedToken = s;
    return "";
  } else {
    lastWasToken = true;
    if ( returnTokens )
      return s;
    else
      return nextTokenInternal();
  }
      lastWasToken = false;
      return s;
    } finally {
      internal = false;
    }
  }
  public boolean hasMoreTokens()
  {
    return hasMoreElements();
  }
}