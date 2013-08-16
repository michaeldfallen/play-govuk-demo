import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-govuk-demo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )


  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    templateKey in Compile <<= baseDirectory(_ / "govuk_template_play"),
    sourceGenerators in Compile <+= (state, templateKey in Compile, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
    playAssetsDirectories <+= baseDirectory { _ / "govuk_template_play" / "assets" },
    updateTemplateTask,
    compile <<= (compile in Compile) dependsOn updateTemplate
  )

  val templateKey = SettingKey[File]("template-dir", "Template directory for govuk_template_play")
  val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
  val updateTemplateTask = updateTemplate := {
    "./update-template.sh".!
  }

}
