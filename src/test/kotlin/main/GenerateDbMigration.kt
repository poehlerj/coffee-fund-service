package main

import io.ebean.annotation.Platform
import io.ebean.dbmigration.DbMigration

fun main(args : Array<String>) {

  // requires jvmTarget 1.8
  val dbMigration = DbMigration.create()
  dbMigration.addPlatform(Platform.H2, "h2")
  dbMigration.addPlatform(Platform.POSTGRES, "postgres")
  dbMigration.addPlatform(Platform.MYSQL, "mysql")

  dbMigration.generateMigration()
}
