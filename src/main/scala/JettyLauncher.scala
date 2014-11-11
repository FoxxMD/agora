import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

//not sure how this all works yet
object JettyLauncher { // this is my entry object as specified in sbt project definition
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080 //set the port to listen on

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    //context.setResourceBase("src/main/webapp/") //set base for serving static files
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    //context.setVirtualHosts(Array("gtgamefest.com"))

    server.setHandler(context)

    server.start
    server.join
  }
}
