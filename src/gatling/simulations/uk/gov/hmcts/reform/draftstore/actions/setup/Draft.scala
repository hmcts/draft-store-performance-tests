package uk.gov.hmcts.reform.draftstore.actions.setup

import io.gatling.core.Predef._

import scala.util.Random

object Draft {

  val drafts = List("os-copyleft.json", "os-licenses.json", "os-miscellaneous.json",
    "os-obsolete.json", "os-osi-approved.json", "os-permissive.json",
    "os-popular.json", "os-redundant.json", "os-retired.json",
    "os-discouraged.json")
    
  val draftsFeeder = Iterator.continually(Map("draft_file" -> "drafts/".concat(drafts(Random.nextInt(10)))))

}
