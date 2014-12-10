import SiteKeys._
import GhReadmeKeys._
import GhPagesKeys.ghpagesNoJekyll
import SonatypeKeys._

//
// Basic project information.
//

name := "anyxml"

version := "0.1"

description := "A forgiving, literal XML parser and serializer."

homepage := Some(url("http://zman.io/anyxml/"))

startYear := Some(2014)

organization := "io.zman"

organizationName := "zman.io"

organizationHomepage := Some(url("http://zman.io/"))

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "junit" % "junit-dep" % "4.10" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

licenses := Seq("The BSD 3-Clause License" -> url("http://opensource.org/licenses/BSD-3-Clause"))

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.coverallsSettings

//
// Documentation site generation.
//

site.settings

includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.md" | "*.yml"

site.includeScaladoc("api")

ghreadme.settings

readmeMappings ++= Seq(
  "." --- Seq(
    "title"    -> "about",
    "headline" -> "a forgiving, literal XML parser and serializer",
    "layout"   -> "home"
  ),
  "changelog" --- Seq(
    "title"    -> "changelog",
    "headline" -> "a look back at the great XML struggles of yore",
    "layout"   -> "page"
  )
)

ghpages.settings

ghpagesNoJekyll := false

git.remoteRepo := (sys.env get "GH_TOKEN" map (t => s"https://$t:@github.com/") getOrElse "git@github.com:") + "zmanio/anyxml.git"

//
// Publishing to Sonatype
//

sonatypeSettings
  
pomExtra := (
  <scm>
    <url>git@github.com:zmanio/anyxml.git</url>
    <connection>scm:git:git@github.com:zmanio/anyxml.git</connection>
    <developerConnection>scm:git:git@github.com:zmanio/anyxml.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>lonnie</id>
      <name>Lonnie Pryor</name>
      <url>http://zman.io</url>
    </developer>
  </developers>
)
