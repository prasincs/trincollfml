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
  
  var cnt = 0

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

    def fmlDeservedFunc(str: String) : JsCmd = {
        //val fmlId = JSONParser.parse(str).open_!
        //println ("" + fmlId)
        //var fmlMap = JSONParser.parse(str).toList.map( i => (i.toString, i.toString)d
        println("Received "+ JSONParser.parse(str))
        JsRaw("alert ('deserved clicked')")
    }


    def fmlSucksFunc(str: String) : JsCmd = {
        println("Received "+ str)
        //JsRaw("alert ('sucks clicked')")
        SetHtml("5-sucks", Text(1.toString))
    }

    def doClicker(text: NodeSeq): NodeSeq = 
        a(()=> {
            cnt = cnt +1
            SetHtml ("cnt_id", Text(cnt.toString))}, text)

    private def sucksA (fml:FML, reDraw:() => JsCmd) = 
        a(()=> {
                if (User.loggedIn_?){
                    fml.sucks(fml.sucks+1).save; reDraw()
                } else {
                    JsRaw("alert('Login first')")
                }
                }, Text(fml.sucks.toString))

    private def deservesA (fml:FML, reDraw:() => JsCmd) = 
        a(()=> { 
            if (User.loggedIn_?){
                fml.deserved(fml.deserved+1).save; reDraw()
            } else {
                JsRaw("alert('Login first')")
            }
            }
            , Text(fml.deserved.toString))

 // A super Lame version  
 //private def sucksA (fml:FML, reDraw: () => JsCmd) = 
 //       swappable(<a>{fml.sucks}</a>,
 //       <a>{fml.sucks+1}
 //           </a>)

  private def doList(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq =
	 toShow.
	 flatMap(fml =>
	  bind("fml", html,
		"fmlStr"->fmlStr(fml, reDraw),
		"sucks"->
        sucksA (fml,reDraw),
		"deserved"-> 
        deservesA(fml,reDraw),
        "userName" -> <a href={"/user/"+fml.user+"/view"}>{fml.user.obj.open_!.name}</a>
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
