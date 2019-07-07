package main

import io.ebean.annotation.Platform
import io.ebean.dbmigration.DbMigration

fun main(args : Array<String>) {

  // requires jvmTarget 1.8
  val dbMigration = DbMigration.create()
  dbMigration.setPlatform(Platform.H2)

  dbMigration.generateMigration()
}
