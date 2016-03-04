sticky-session
===========================

Simple Java web app that prints Cloud Foundry environment variables and cookies and is useful to showcase sticky sessions.

When JSESSIONID and VCAP_ID cookies are set, session affinity is on.

When a Redis service instance with a name containing the words "session-replication" is bound to the sticky-session application, session replication is enabled.

HealthManager can be tested by sending a "http://host/?shutdown=true" request.

Cookies can be cleared by sending a "http://host/?clearcookies=true" request.

To build, please use Maven from the root directory with the pom.xml file

    mvn package

which should generate a WAR file in the "target" directory.

If the app builds successfully, you should be able to push it to a Cloud Foundry environment by using `cf push` from the root directory that has the `manifest.yml` file.

This version of Sticky-Session was developed based on James Bayer's CloudFoundry-Sticky-Session and Google's HSR-Test code.
