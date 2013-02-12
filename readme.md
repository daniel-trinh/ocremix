# Overview

This project convert's the official ocremix.org RSS feed of new songs into tweets. If a failure
occurs anywhere in the process, the exception is logged.

Minimalistic instructions on using and deploying this project follows:

## Talking to Twitter's REST API

The Databinder Dispatch Reboot library is used to authenticate and call Twitter's REST API. Not a
fun library to use due to the severe lack of documentation, but it works.

## Scheduling

Akka scheduling is used for the following things:

1) Periodically polling the ocremix.org rss website, and posting new songs to Twitter (every 30 min).

2) Updating the t.co length from Twitter's official configuration settings (every 24 hours).

## Logging and Exception Handling

Logging is done using Akka's logging. Default behavior logs to stdout -- this is done
to be able to use the `heroku logs` command.

Exceptions are by default sent to the `panic` Twitter handle configured in the application.conf file.

## Configuration

OAuth keys and Twitter Handles are stored in this file. The application_template.conf file has
a few other things filled out already, but the OAuth keys and Twitter handles are intentionally
missing.

The 'ocremix' handle is the user where new tweets will be posted to.

The 'panic' handle is the user where exceptions will be sent to, as a direct message.

The OAuth keys are the keys for the ocremix user. A Twitter application needs to be created first.

## Deployment

This project is intended to deployed on Heroku's Cedar stack, on a worker dyno. Trying to deploy
this on a web dyno will result in heroku barfing every 60 seconds and restarting the application.

Three things are required to deploy this on heroku (and have it work), besides having a free tier
working heroku account:

1) A Procfile in the root directory, with these contents:

`
worker: target/start Worker
`

2) The sbt-start-script plugin installed as a project plugin -- heroku needs to be able to run
sbt clean compile stage. The sbt plugin adds the stage command.

3) OAuth credentials in application.conf (see Configuration section)