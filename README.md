# Gatling performance tests sample

Performance tests use [gatling](http://gatling-tool.org/) for execution.


## Configuration
Typical test uses multiple user account for simulation of concurrent users.
User accounts are defined in [user_credentials.csv](src/test/resources/data/user_credentials.csv)

Proper testing data (user accounts, etc.) have to be created on all environments used for testing.

## Running

Performance tests are executed via [gatling maven plugin](https://github.com/excilys/gatling/wiki/Maven-plugin).
Running performance tests is as simple as following:

```mvn clean test```

You can pass custom cluster url via system property:

```mvn clean test -Dcluster.url=https://myserver.gooddata.com```

### Jenkins
There is a [jenkins job](https://ci.intgdc.com/job/MSF-Performance-Tests/) for automatic execution.
[ConvertGatlingToXunit.groovy](ConvertGatlingToXunit.groovy) is used for generating XUnit xml report from gatling.log



