package com.angryfissure.model

import net.liftweb._
import mapper._
import http._
import SHtml._
import util._
import model._
import Helpers._
import scala.xml._

object CommentMetaData extends  Comment with KeyedMetaMapper[Long, Comment] with CRUDify[Long, Comment] {
    override def dbTableName = "comments"
}

class Comment extends LongKeyedMapper[Comment] with IdPK {
    def getSingleton = CommentMetaData
    object user extends MappedLongForeignKey(this, User)
    object fml extends MappedLongForeignKey(this, FMLMetaData)
    object commentStr extends MappedString(this,255)
    object timeSubmitted extends MappedDateTime(this)
    object up extends MappedInt(this)
    object down extends MappedInt(this)
    object flagged extends MappedBoolean(this)
}
