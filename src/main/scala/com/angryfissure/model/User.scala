package com.angryfissure.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import net.liftweb._
import mapper._
import Helpers._
import common._ // for Full and Empty to work

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, name, firstName, lastName, email, password)

  override def signupFields = List(name, firstName, lastName, email, locale, timezone, password)
  // comment this line out to require email validations
  override def skipEmailValidation = true
  
  override def loginXhtml = 
    <div id="formBox">
      { super.loginXhtml }
    </div>
        
  override def signupXhtml(user: User) = 
	<div id="formBox">
      { super.signupXhtml(user) }
    </div>
  
  override def editXhtml(user: User) = 
	<div id="formBox">
      { super.editXhtml(user) }
    </div>
  
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server
  object name extends MappedString(this, 255) {
  	override def displayName = "Username"
  }
  object role extends MappedEnum(this, Role){
    override def defaultValue = Role.User
  }

}

object Role extends Enumeration {
    val User = Value(0, "User")
    val Admin = Value(1, "Admin")
}
