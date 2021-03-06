package com.angryfissure.snippet

import com.angryfissure._
import com.angryfissure.model._
import net.liftweb._
import http._
import SHtml._
import S._
import js._
import JsCmds._
import JE._
import mapper._
import util._
import Helpers._
import scala.xml.{NodeSeq, Text}
import java.util.Date;
import java.text.SimpleDateFormat
import net.liftweb.http.provider.HTTPCookie
import common._

class AdminSnip {
  object QueryNotDone extends SessionVar(false)

  private def getFmls =
	  FMLMetaData.findAll(OrderBy(FMLMetaData.timeSubmitted, Descending))
  
  private def getUsers =
	  User.findAll(OrderBy(User.id, Ascending))

 private def approvedCheckBox (fml:FML) = 
        ajaxCheckbox (fml.approved, (approved: Boolean)=> { fml.approved(approved).save; JsRaw("alert('fml updated')");}, ("test","test") )

  private def bindFMLAll(fml:FML, html: NodeSeq, reDraw: () => JsCmd) =
            bind("fml", html,
                     "fmlStr" -> fml.fmlStr,
                     "user" -> fml.user,
                     "timesubmitted" -> fml.timeSubmitted,
                     "sucks" -> fml.sucks,
                     "deserved" -> fml.deserved,
                     "approved" -> approvedCheckBox(fml),
                     FuncAttrBindParam("view_href", _ =>
                       Text("/fmls/view/"+ (fml.id)),"href"),
                     FuncAttrBindParam("edit_href", _ =>
                   Text("/fmls/edit/"+ (fml.id)),"href"),
                    FuncAttrBindParam("delete_href", _ =>
                        Text("/fmls/delete/"+ (fml.id)),"href"))

  private def doListFmls(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq =
	 getFmls.
	 flatMap(fml =>
        bindFMLAll(fml,html, reDraw)    
	)

 def showFmls(html:NodeSeq) = {
   if (User.superUser_?){
	  def inner(): NodeSeq = {
	    def reDraw() = SetHtml("all_fmls", inner())
	    bind("fml", html,
	        "list" -> doListFmls(reDraw) _)
	  }
	  inner()
    }else {
        redirectTo("/")
    }
  }


  private def superUserCheckBox(user:User) =
    ajaxCheckbox (user.superUser, (su: Boolean)=> { user.superUser(su).save; JsRaw("alert('user updated')");}, ("test","test") )



private def bindUser(user:User, html: NodeSeq, reDraw: () => JsCmd) ={
  bind ("user", html,
	"id"-> user.id,
	"name" -> user.name,
    "superuser" -> superUserCheckBox(user),
    "role" -> ajaxSelectObj(Role.elements.toList.map(v=>(v, v.toString)) , Full(user.role), (selected:ScalaObject) => {
        println(selected+""); 
        println(user.role+"");
        println (user.role.getClass+"");
        //user.role(Role.valueOf(selected.toString) getOrElse).save; 
        Run("alert('test')")}),
	FuncAttrBindParam("view_href", _ =>
          Text("/user_mgmt/view/"+ (user.id)),"href"),
        FuncAttrBindParam("edit_href", _ =>
          Text("/user_mgmt/edit/"+ (user.id)),"href"),
        FuncAttrBindParam("delete_href", _ =>
          Text("/user_mgmt/delete/"+ (user.id)),"href"))
      }

private def doListAllUsers(reDraw: ()=> JsCmd)(html:NodeSeq): NodeSeq = 
  getUsers.
    flatMap(user =>
      bindUser(user, html, reDraw))

def showUsers(html:NodeSeq) = {
 if (User.superUser_?){
	  def inner(): NodeSeq = {
	    def reDraw() = SetHtml("all_users", inner())
	    bind("user", html,
	        "list" -> doListAllUsers(reDraw) _)
	  }
	  inner()
    }else {
        redirectTo("/")
    }
  }
 }    

