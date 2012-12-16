# TODO

## main
+ figure out how to unwrap a promised either
+ implement email functionality

### rss parsing
+ figure out how to periodically schedule polling of rss feed
+ parse top 10 rss feed into RemixEntry case class list

### twitter api
+ twitter helpConfig api call
+ twitter directMessage api call
+ twitter tweet api call
+ json string to TwitterConfig object parsing
+ json string to Tweet object parsing
+ implement trimMethod
## test

### unit tests
+ make sure json parser for twitter config and tweet objects are working
+ make sure rss feed parser works on sample xml
+ make sure trimMethod works with ocremix posts

### manual tests
+ test out each twitter api call at least once
+ scheduling code, make sure it actually polls periodically
+ investigate if having rigid case classes will be too brittle for parsing json

# DONE