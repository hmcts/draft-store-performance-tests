package uk.gov.hmcts.reform.draftstore.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.setup.Draft

object Create {

  val create: ChainBuilder =
    feed(Draft.draftsFeeder)
      .exec { session =>
        println(session("draft_file").as[String])
        session
      }
      .exec(
        http("Create draft")
          .post(url = "")
          .headers(Map(
            "ServiceAuthorization" -> "Bearer ${service_token}",
            "Authorization" -> "Bearer ${user_token}",
            "Secret" -> "${secret}"
          ))
          .body(
            RawFileBody("${draft_file}")
          ).asJson
          .check(headerRegex("Location", """/(\d+)$""").saveAs("id"))
    )
}
