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

class FMLSnip {
  object QueryNotDone extends SessionVar(false)
  
  def add(form: NodeSeq): NodeSeq = {
    Console.println(new Date())
	val fml = FMLMetaData.create.user(User.currentUser).timeSubmitted(new Date())
   
	def checkAndSave(): Unit =  {
      fml.save
      S.notice("Added "+fml.fmlStr)
    }
    
   
 
	def doBind(form: NodeSeq): NodeSeq= {
		bind("fml", form,
			"fmlStr" -> fml.fmlStr.toForm,
			"submit" -> submit("AddFML", checkAndSave))
    }
 
    doBind(form)
  }
  
  private def toShow =
	  FMLMetaData.findAll(OrderBy(FMLMetaData.timeSubmitted, Descending))
  
  private def fmlStr(fml: FML, reDraw: () => JsCmd) =
	  swappable(<span style="font-size:20px;">{fml.fmlStr}</span>,
         <span>{ajaxText(fml.fmlStr,
                     v => {fml.fmlStr(v).save; reDraw()})}
         </span>)
object showUser extends RequestVar[Long](null)

    def fmlDeservedFunc(str: String) : JsCmd = {
        println("Received "+ str)
        JsRaw("alert ('deserved clicked')")
    }


    def fmlSucksFunc(str: String) : JsCmd = {
        println("Received "+ str)
        JsRaw("alert ('sucks clicked')")
    }


  private def doList(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq =
	 toShow.
	 flatMap(fml =>
	  bind("fml", html,
		"fmlStr"->fmlStr(fml, reDraw),
		"sucks"->
        <a  onclick={ajaxCall(Str("fml-deserved"), fmlSucksFunc _ )._2}>{fml.sucks}</a>,
		"deserved"-> 
        // ajaxCall and ajaxInvoke actually returns a pair (String, JsExp).
        // The String is used for garbage collection, so we only need
        // to use the JsExp element (_2).
            <a  onclick={ajaxCall(Str("fml-deserved"), fmlDeservedFunc _ )._2}>{fml.deserved}</a>,
        "userName" -> link("/user/"+fml.user+"/view",()=> showUser(fml.user.obj.open_!.id), Text(fml.user.obj.open_!.firstName))
        )
	  )
  
  def list(html: NodeSeq) = {
	  val id = S.attr("all_id").open_!
	  def inner(): NodeSeq = {
	    def reDraw() = SetHtml(id, inner())
	    bind("fml", html,
	        "list" -> doList(reDraw) _)
	  }
	  inner()
  }
}
