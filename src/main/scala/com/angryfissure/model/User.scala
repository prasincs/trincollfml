package com.angryfissure.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import net.liftweb._
import mapper._
import Helpers._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, name, firstName, lastName, email,
  locale, timezone, password, textArea)

  override def signupFields = List(name, firstName, lastName, email, locale, timezone, password, textArea)
  // comment this line out to require email validations
  override def skipEmailValidation = true
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server
  object name extends MappedString(this, 255)
  object role extends MappedLongForeignKey(this, Role)
  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }

}
