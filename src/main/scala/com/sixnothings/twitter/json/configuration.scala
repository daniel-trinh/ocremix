package com.sixnothings.twitter.json

/**
 * A Scala case class to be used with Jerkson JSON parsing.
 *
 * The underscore casing is due to how jerkson requires constructor attributes
 * to match key names.
 *
 * @param characters_reserved_per_media ???
 * @param max_media_per_upload ???
 * @param non_username_paths ???
 * @param photo_size_limit ???
 * @param photo_sizes ???
 * @param short_url_length When URLs get t.co shortened, http urls will be this length
 * @param short_url_length_https When URLs get t.co shortened, https urls will be this length
 */
case class TwitterConfiguration(
  characters_reserved_per_media : Int = 21,
  max_media_per_upload          : Int = 1,
  non_username_paths            : List[String],
  photo_size_limit              : Int,
  photo_sizes                   : Map[String, PhotoSize],
  short_url_length              : Int = 20,
  short_url_length_https        : Int = 21
)

/**
 * A sub-json resource to be used with TwitterConfiguration.
 */
case class PhotoSize(h: Int, resize: String, w: Int)
