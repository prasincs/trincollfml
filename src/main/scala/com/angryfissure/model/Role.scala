package com.angryfissure.model

import net.liftweb._
import mapper._
import http._
import SHtml._
import util._
import model._
import Helpers._
import scala.xml._



class Role extends LongKeyedMapper[Role] with IdPK {
    def getSingleton = Role

    object name extends MappedString(this, 255)
}

object Role extends Role with KeyedMetaMapper[Long, Role] with CRUDify[Long, Role] {
    override def dbTableName = "roles"
}
