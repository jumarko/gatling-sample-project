
object Config {
  val host: String = if (System.getProperty("cluster.url") != null) {
    System.getProperty("cluster.url")
  } else {
    // default address, this is a fake -  use your own server!
    "https://performance.gooddata.com"
  }
}

/**
 * Perform concurrent requests to resource with different users from user_credentials.csv.
 * For each user the requests are repeated with given pause and count.
 */
class ConcurrentUsersSimulation extends Simulation {
  val hostUrl = Config.host

  // how many requests per user
  val requestsNumber = 20

  // pause between two subsequent requests for the same user
  val requestPauseSeconds = 15

  // basic HTTP configuration - base url for our server and HTTP headers
  val httpConf = httpConfig
    .baseURL(hostUrl)
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:20.0) Gecko/20100101 Firefox/20.0")



  // load users' (name, password) combination
  val usersDataSource: Array[Map[String, String]] = csv("user_credentials.csv")

  // perform requests for each user from credentials files in parallel
  private val usersCount: Int = usersDataSource.length
  val scn = {
    scenario("resource test: host=" + hostUrl + ", users_count=" + usersCount)
      .feed(usersDataSource)

        // at the beginning we need to find user id - note that "trick" with saving the result into the "userId" variable
        .exec(http("getUserId")
          .get("/gdc/app/account/bootstrap")
          .basicAuth("${username}", "${password}")
          .check(jsonPath("$.bootstrapResource.current.loginMD5").find.saveAs("userId"))
        )

        /* 
         * the performance testing itself - perform multiple requests for each user
         * - requests for one user are not concurrent, there is only one request per user at time
         * - there are multiple concurrent requests for different users
         */
      .repeat(requestsNumber) {
      exec(http("processesView GET")
        .get("/gdc/app/account/profile/${userId}/dataload/processesView").basicAuth("${username}", "${password}"))
        .pause(requestPauseSeconds)
    }
  }

  setUp(scn.users(usersCount).ramp(20).protocolConfig(httpConf))

}
