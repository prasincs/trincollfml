package com.angryfissure.model

import net.liftweb._
import net.liftweb.mapper._
import mapper._
import http._
import SHtml._
import util._
import model._
import Helpers._
import scala.xml._
import common._ //For Full and Empty to work
import java.util.Date

object Tracking extends Tracking with LongKeyedMetaMapper[Tracking] 
{
  override def dbTableName = "tracking"
}

class Tracking extends LongKeyedMapper[Tracking] 
{
  def getSingleton = Tracking
  def primaryKeyField = id
  
  object id extends MappedLongIndex(this)
  object ipAddress extends MappedString(this, 15)
  object timeStamp extends MappedDateTime(this){
    override def defaultValue = new Date()
  }
  
}
