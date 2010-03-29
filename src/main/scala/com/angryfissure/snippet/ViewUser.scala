package com.angryfissure.snippet

import com.angryfissure._
import com.angryfissure.model._
import net.liftweb._
import http._
import SHtml._
import S._
import js._
import JsCmds._
import mapper._
import util._
import Helpers._
import scala.xml.{NodeSeq, Text}
import java.util.Date;
import java.text.SimpleDateFormat
import common._

class ViewUser {
		

	def view (html: NodeSeq) : NodeSeq = {
		var id = S.param("id") openOr ""
        println(id)
		var user = try {
			User.findByKey(id.toLong)
		} catch {
			case e:NumberFormatException => Empty
		}
        var l = user.open_!
		bind ("user", html,
				"id" -> l.id,
				"name"-> l.name,
				"firstName"-> l.firstName,
				"lastName" -> l.lastName
				)
    }
}
