package com.esports.gtplatform.data


import org.scalatra._

object DatabaseSessionSupport {
  val key = {
    val n = getClass.getName
    if (n.endsWith("$")) n.dropRight(1) else n
  }
}

trait DatabaseSessionSupport { this: ScalatraBase =>

 // def dbSession = request.get(key).orNull.asInstanceOf[Session]
  
  before() { 
    //request(key) = SessionFactory.newSession
   // dbSession.bindToCurrentThread
  }

  after() {
    //dbSession.close
    //dbSession.unbindFromCurrentThread
  }

}
