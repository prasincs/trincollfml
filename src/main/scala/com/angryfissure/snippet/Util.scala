package com.angryfissure.snippet

import scala.xml.{NodeSeq}
import com.angryfissure._
import com.angryfissure.model._

class Util {

  def in(html : NodeSeq) =
    if(User.loggedIn_?) html else NodeSeq.Empty
  
  def out(html : NodeSeq) = 
    if(!User.loggedIn_?) html else NodeSeq.Empty
}
