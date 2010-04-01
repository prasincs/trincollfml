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

object FMLMetaData extends FML with KeyedMetaMapper[Long, FML] with CRUDify[Long, FML]{
     override def dbTableName = "fmls"
     override def fieldOrder = List(fmlStr, user, timeSubmitted, sucks, deserved, approved) 
     override def viewMenuLoc = Empty
     override def createMenuLoc = Empty
     override def showAllMenuLoc = Empty

     }

class FML extends KeyedMapper[Long, FML] {
	def getSingleton = FMLMetaData   
    def primaryKeyField = id
    
    object id extends MappedLongIndex(this)
    object fmlStr extends MappedTextarea(this,2048) {
      override def textareaRows  = 10
      override def textareaCols = 50
  	}
    object user extends MappedLongForeignKey(this, User)
    object timeSubmitted extends MappedDateTime(this)
    //    {
//	    val dateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm a")
//	    override def defaultValue = time(millis + days(5))
//	    override def asHtml = Text(toString)
//	    override def toString = dateFormat.format(is)
//	}
    object tracking extends MappedLongForeignKey(this, Tracking)
    object sucks extends MappedInt(this)
    object deserved extends MappedInt(this)
    object approved extends MappedBoolean(this)


}
