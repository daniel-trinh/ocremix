package com.sixnothings.twitter.json

import com.codahale.jerkson.AST.JValue

// Parameters with type JValue are JValues because
// they are potentially null, or haven't been broken down
// into other case classes yet. Also, can't declare this
// as a case class since Tweets have more than 22 parameters.
class Tweet(
  contributors: List[JValue],
  coordinates: JValue,
  created_at: String,
  current_user_retweet: JValue,
  entities: JValue,
  favorited: Boolean,
  geo: JValue,
  id: BigInt,
  id_str: String,
  in_reply_to_screen_name: JValue,
  in_reply_to_status_id: JValue,
  in_reply_to_status_id_str: JValue,
  in_reply_to_user_id: JValue,
  place: JValue,
  possibly_sensitive: Boolean,
  scopes: Map[String, Any],
  retweet_count: Int,
  retweeted: Boolean,
  source: String,
  text: String,
  truncated: Boolean,
  user: JValue,
  withheld_copyright: Boolean,
  withheld_in_countries: List[String],
  withheld_scope: String
)
