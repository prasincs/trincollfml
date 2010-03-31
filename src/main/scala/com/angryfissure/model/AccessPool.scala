package com.angryfissure.model 
import net.liftweb._
import mapper._
import http._
import util._
import common._

import scala.xml.Text

import java.util.Date

object AccessPool extends AccessPool with LongKeyedMetaMapper[AccessPool] {
  val Native = "Native"

  def findPool(name: String, realm: String): Box[AccessPool] = 
    AccessPool.find(By(AccessPool.name,  name),
                    By(AccessPool.realm, realm))

  //set createdDate and creator when instance AccessPool
  override def create: AccessPool = {
    val ap = super.create
    ap.createdDate(new Date())
    ap.creator(User.currentUser)
    ap
  }
}

class AccessPool extends LongKeyedMapper[AccessPool] {
  def getSingleton = AccessPool
  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  // is it worth having foreign key to another table?
  object realm extends MappedString(this, 256)

  private[model] object name extends MappedString(this, 256) {
    
    override def validations = checkDuplicate _ :: super.validations
    
    def checkDuplicate(in: String): List[FieldError] = 
      sameName(in).map(p =>
        FieldError(this, Text(S.?("base_pool_err_dup_name_in_realm", in, p.realm.is)))
      )
    
  }
  
  // set modify information when setName
  def setName(in: String) = sameName(in) match {
    case Nil => {
      Full(this.name(in))
      Full(this.lastModifyDate(new Date()))
      Full(this.modifier(User.currentUser))
    }
    case List(_,_*) => Failure(S.?("base_pool_err_dup_name"))
  }
  
  def getName() = name.is

  private def sameName(name: String) = 
    AccessPool.findAll(By(AccessPool.name, name)).
      filter(_.realm.is.equalsIgnoreCase(this.realm.is))
  
  //define create and modify fields
  object createdDate extends MappedDateTime(this) 
  object creator extends MappedLongForeignKey(this,User)
  object lastModifyDate extends MappedDateTime(this) 
  object modifier extends MappedLongForeignKey(this,User)
  
}
