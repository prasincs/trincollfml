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

class CommentSnip {
  object QueryNotDone extends SessionVar(false)
  
  def add(form:NodeSeq): NodeSeq = {
        val fmlId = S.param("id").open_!.toLong 

        Console.println("here " + fmlId)
        var comment = CommentMetaData.create.user(User.currentUser).timeSubmitted(new Date()).fml(FMLMetaData.findByKey(fmlId))

        def checkAndSave(): Unit = {
            println(comment)
            comment.save
            S.notice("Added "+ comment.commentStr)
        }

        def doBind(form: NodeSeq): NodeSeq = {
            bind ("comment", form,
                "commentStr" -> comment.commentStr.toForm,
                "submit" -> submit("AddComment", checkAndSave))
        }

        doBind(form)
    }

    private def toShow(fmlId:Long) = 
        //CommentMetaData.findAll(OrderBy(CommentMetaData.timeSubmitted, Descending))
        CommentMetaData.findAll(By(CommentMetaData.fml, fmlId))

    private def bindComment(comment:Comment, html: NodeSeq, reDraw: () => JsCmd) = 
        bind("comment", html,
            "commentStr" -> Text(comment.commentStr),
            "userName" -> <a href={"/user/"+comment.user+"/view"}>{comment.user.obj.open_!.name}</a>
        )

    private def doList( reDraw: () => JsCmd, fmlId : Long) ( html: NodeSeq): NodeSeq = {
        println(toShow(fmlId))
        toShow(fmlId).
            flatMap(comment =>
                bindComment(comment, html, reDraw)
            )
        }

    def list(html: NodeSeq) = {
        val fmlId = S.param("id").open_!.toLong
        println(fmlId)
        def inner(): NodeSeq = {
            def reDraw() = SetHtml("comments", inner())
            bind("comment", html,
                "list" -> doList(reDraw, fmlId) _)
        }
        inner()
    }
}
