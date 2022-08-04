package com.coveritas.heracles.json

import groovy.transform.CompileStatic

import javax.xml.bind.DatatypeConverter
import java.nio.ByteBuffer
import java.security.Timestamp
import java.time.Instant

/**
 * Company is a domain object. We want the view of a organization as a twinned Entity
 */
@CompileStatic
class Article implements Serializable {
  Long id
  String uuid
  Long dateCreated
  Long contentTs
  String source
  String title
  String author
  String contentType
  String content
  String desc
  Integer referenceCount
  Float strength

  Article() {  }

  Date getDate() {
    new Date(dateCreated)
  }

  String toString() { "$title" }
}
