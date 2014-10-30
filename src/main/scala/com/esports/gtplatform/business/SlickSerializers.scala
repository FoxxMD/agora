package com.esports.gtplatform.business

import models.Game
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.{Extraction, DefaultFormats, Formats, CustomSerializer}

/**
 * Created by Matthew on 10/30/2014.
 */
class SlickSerializers extends CustomSerializer[Game](formats => ( {
    PartialFunction.empty
}, {
    case gr: Game =>
        implicit val formats: Formats = DefaultFormats
        val a = Extraction.decompose(gr) merge
            render("tournamentTypes" -> Extraction.decompose(gr.tournamentTypes))
        a
}))
