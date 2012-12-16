package com.sixnothings.twitter.json

case class TwitterConfiguration(
  characters_reserved_per_media : Int = 21,
  max_media_per_upload          : Int = 1,
  non_username_paths            : List[String],
  photo_size_limit              : Int,
  photo_sizes                   : Map[String, PhotoSize],
  short_url_length              : Int = 20,
  short_url_length_https        : Int = 21
)

case class PhotoSize(h: Int, resize: String, w: Int)
