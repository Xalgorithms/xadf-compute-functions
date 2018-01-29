package org.xalgorithms.rule_interpreter

import org.xalgorithms.rule_interpreter.common.setKey
import play.api.libs.json._


object utils {
  def documentToContext(d: String): String = {
    val parsedDoc = Json.parse(d).as[JsObject]
    val res = setKey(parsedDoc, "tables", Json.obj("items" -> (parsedDoc \ "items").get))

    Json.stringify(res)
  }

  def jsonToString(v: JsValue): String = {
    Json.stringify(v)
  }

  def extractStep(r: String): String = {
    val parsedRule = Json.parse(r)
    val step = (parsedRule \ "steps" \ 0).get

    Json.stringify(step)
  }

  def extractRevision(r: JsValue): String = {
    val res = (r \ "revision").get
    Json.stringify(res)
  }
}
