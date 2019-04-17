package uk.gov.hmcts.reform.draftstore

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.Create.create
import uk.gov.hmcts.reform.draftstore.actions.DeleteAll.deleteAll
import uk.gov.hmcts.reform.draftstore.actions.ReadOne.readOne
import uk.gov.hmcts.reform.draftstore.actions.setup.Idam
import uk.gov.hmcts.reform.draftstore.actions.setup.LeaseServiceToken.leaseServiceToken

import scala.concurrent.duration._

class CreateMultipleDrafts extends Simulation {

  val config = ConfigFactory.load()

  val httpProtocol =
    http
      .baseURL(config.getString("baseUrl"))
      .contentTypeHeader("application/json")


  val createAndReadDrafts =
    scenario("Register and create multiple drafts")
      .exec(Idam.registerAndSignIn)
      .pause(2 second, 5 seconds)
      .exec(session => {
        Map(
          "email" -> session("email").as[String],
          "user_token" -> session("user_token").as[String]
        )
        session
      })
      .exec(leaseServiceToken)
      .during(1.minute)(
        exec(
          create,
          readOne,
          pause(2.seconds)
        )
      )
      .exec(deleteAll)
      .exec(Idam.deleteAccount)

  setUp(
    // Load test over 1 hour - settings
    createAndReadDrafts.inject(rampUsers(3000).over(60.minutes))
    // Regression (pipeline) - settings
    //createAndReadDrafts.inject(rampUsers(300).over(360.seconds)),
    // Single user
    //createAndReadDrafts.inject(rampUsers(1).over(1.seconds)),
  ).protocols(httpProtocol)
}
