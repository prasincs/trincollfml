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
import javax.servlet.http.{Cookie}

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
 
 private def fmlStr(fml:FML) = 
    <span class="fml-string">{fml.fmlStr}</span>

    def fmlDeservedFunc(str: String) : JsCmd = {
        //val fmlId = JSONParser.parse(str).open_!
        //println ("" + fmlId)
        //var fmlMap = JSONParser.parse(str).toList.map( i => (i.toString, i.toString)d
        println("Received "+ JSONParser.parse(str))
        JsRaw("alert ('deserved clicked')")
    }


    def fmlId(fml:FML) = 
         <a class="fml-link" href={"/fmls/view/"+fml.id}>#{fml.id}</a>

   
    private def sucksA (fml:FML, reDraw:() => JsCmd) = 
        a(()=> {
                if (User.loggedIn_?){
                    println(findCookie("trincollfml_fml_"+fml.id));
                    if (findCookie("trincollfml_fml_"+fml.id).isEmpty){
                        var cookie = new Cookie("trincollfml_fml_"+fml.id, "true")
                        cookie.setMaxAge(300);
                        addCookie(cookie);
                        fml.sucks(fml.sucks+1).save; 
                        reDraw()
                    }else {
                      JsRaw("alert('you can\'t comment twice')");
                    }
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

    private def bindFML(fml:FML, html: NodeSeq, reDraw: () => JsCmd) =
        bind("fml", html,
            "id" -> fmlId(fml),
		    "fmlStr"->fmlStr(fml),
    		"sucks"->
                sucksA (fml,reDraw),
		    "deserved"-> 
                deservesA(fml,reDraw),
            "userName" -> <a href={"/user/"+fml.user+"/view"}>{fml.user.obj.open_!.name}</a>
        )


  private def doList(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq =
	 toShow.
	 flatMap(fml =>
        bindFML(fml,html, reDraw)    
	)
  
  def list(html: NodeSeq) = {
	  val id = S.attr("all_id").open_!
      println(id)
	  def inner(): NodeSeq = {
	    def reDraw() = SetHtml(id, inner())
	    bind("fml", html,
	        "list" -> doList(reDraw) _)
	  }
	  inner()
  }

 def view(html: NodeSeq) = {
    var id = S.param("id") openOr ""
    var fml = try {
			FMLMetaData.findByKey(id.toLong)
		} catch {
			case e:NumberFormatException => Empty
		}
        var l = fml.open_!
        def reDraw() = SetHtml(id, Text("test"))
    bindFML(l, html,reDraw)
 }
}
