package uk.gov.hmcts.reform.draftstore.actions.setup

import java.util.Base64

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._


import scala.util.Random

object Idam {

  private val idamUrl = ConfigFactory.load().getString("auth.idam.url")

  private val idamClientId = ConfigFactory.load().getString("auth.idam.clientId")
  private val idamClientSecret = ConfigFactory.load().getString("auth.idam.clientSecret")
  private val idamRedirectUri = ConfigFactory.load().getString("auth.idam.redirectUri")

  private val basicHeader = "Basic"
  private val authType = "code"
  private val grantType = "authorization_code"

  private val randomEmailFeeder =
    Iterator.continually(
      Map("email" -> (Random.alphanumeric.take(20).mkString + "@example.com"))
    )

  val registerAndSignIn: ChainBuilder =
    feed(randomEmailFeeder)
      .exec(
        http("Create IDAM account")
          .post(idamUrl + "/testing-support/accounts")
          .header(ContentType, ApplicationJson)
          .body(StringBody(
            """
              {
                "email": "${email}",
                "forename": "John",
                "surname": "Smith",
                "password": "password"
              }
            """
          ))
      )
      .exec(session => {
        session.set("loginHeader", buildLoginHeader(session("email").as[String], "password"))
      })
      .exec(
        http("Sign in to IDAM")
          .post(idamUrl + "/oauth2/authorize")
          .header(basicHeader, "${loginHeader}")
          .formParam("response_type", authType)
          .formParam("redirect_uri", idamRedirectUri)
          .formParam("client_id", idamClientId)
          //.check(jsonPath("$['access-token']").saveAs("user_token"))
          .check(jsonPath("$['code']").saveAs("auth_code"))
      )
      .exec(
        http("Exchange code")
          .post(idamUrl + "/oauth2/token")
          .formParam("code", "${auth_code}")
          .formParam("grant_type", grantType)
          .formParam("redirect_uri", idamRedirectUri)
          .formParam("client_id", idamClientId)
          .formParam("client_secret", idamClientSecret)
          .check(jsonPath("$['access_token']").saveAs("user_token"))
      )

  val deleteAccount: ChainBuilder =
    exec(
      http("Delete IDAM account")
          .delete(idamUrl + "/testing-support/accounts/${email}")
    )

  def buildLoginHeader(email: String, password: String) : String = {
    "Basic " + Base64.getEncoder.encodeToString(s"$email:$password".getBytes)
  }
}
