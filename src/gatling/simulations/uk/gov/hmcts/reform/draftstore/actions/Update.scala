package uk.gov.hmcts.reform.draftstore.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.setup.Draft

object Update {

  val update: ChainBuilder =
    feed(Draft.draftsFeeder)
      .exec(
        http("Update draft")
          .put(url = "/${id}")
          .headers(Map(
            "ServiceAuthorization" -> "Bearer ${service_token}",
            "Authorization" -> "Bearer ${user_token}",
            "Secret" -> "${secret}"
          ))
          .body(
            RawFileBody("${draft_file}")
          ).asJson
          .check(status.is(204))
    )
}
