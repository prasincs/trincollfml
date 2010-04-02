package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.com.angryfissure.model._


/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier,
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
  Props.get("db.url") openOr "jdbc:h2:lift_proto.db",
Props.get("db.user"), Props.get("db.password")))

    // where to search snippet
    LiftRules.addToPackages("com.angryfissure")
    Schemifier.schemify(true, Log.infoF _, User, FMLMetaData, CommentMetaData, Tracking)
  LiftRules.siteMapFailRedirectLocation = List("index")

    // Build SiteMap
    val entries =  Menu(Loc("Home", List("index"), "Home")) ::
User.sitemap  ++ FMLMetaData.menus


    LiftRules.setSiteMap(SiteMap(entries:_*))

 LiftRules.setSiteMap(SiteMap(entries:_*))
    LiftRules.rewrite.append {
	    case RewriteRequest(
		    ParsePath ("user" :: id :: "view" :: Nil , _, _, _), _ , _) =>
		    RewriteResponse ("viewUser"::Nil, Map("id" -> id))
	    
        case RewriteRequest(
            ParsePath ("fmls" :: "view" :: id :: Nil, _, _, _), _, _) =>
            RewriteResponse ("viewFml"::Nil, Map("id" -> id))
        
         case RewriteRequest(
            ParsePath ("fmls" :: "list" :: Nil, _, _, _), _, _) =>
            RewriteResponse ("listFml"::Nil)


        //effectively rewrites /admin to /admin/
         case RewriteRequest(
            ParsePath ("admin" :: Nil, _, _, _), _, _) =>
            RewriteResponse ("admin"::"index" :: Nil)

    }

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)



    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }

}



