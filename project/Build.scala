import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-govuk-demo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
  )


  lazy val main = play.Project(appName, appVersion, appDependencies)
    .settings(GovukTemplatePlay.playSettings:_*)

}

object GovukTemplatePlay extends Plugin {

  lazy val templateKey = SettingKey[Seq[File]]("template-dir", "Template directory for govuk_template_play")
  lazy val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
  lazy val updateTemplateTask = updateTemplate := {
    "./update-template.sh".!
  }

  val playSettings = Seq(
    templateKey <<= baseDirectory(_ / "app" / "assets" / "govuk_template_play")(Seq(_)),
    sourceGenerators in Compile <+= (state, templateKey, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
    playAssetsDirectories <+= baseDirectory { _ / "app" / "assets" / "govuk_template_play" / "assets" },
    updateTemplateTask
  )
}

