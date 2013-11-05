#Play-govuk-demo

This is a simple example of how to make use of [govuk_template_play](https://github.com/alphagov/govuk_template_play) in your play applications. 

##Get the templates

First step, download the templates. Here we use git submodules to link to the [govuk_template_play](https://github.com/alphagov/govuk_template_play) repo directly.

```sh
git submodule add git@github.com:alphagov/govuk_template_play.git
git submodule init
git submodule update
```

You can also download the compiled sources as a tar ball from [govuk_template_play/releases](https://github.com/alphagov/govuk_template_play/releases)

##Add the templates to Plays Template Generator

Play uses the property `sourceGenerators` to know what files should be compiled as templates. To make use of the govuk\_template.scala.html file we need to include it in the `sourceGenerators` property.

Make the following changes to your project definition. This will add a new SettingsKey that refers to our template directory and includes it in the `sourceGenerators` property.


#####[project/Build.scala](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/project/Build.scala)
```scala 
val templateKey = SettingKey[File]("template-dir", "Template directory for govuk_template_play")

lazy val main = play.Project(appName, appVersion, appDependencies).settings(
  templateKey in Compile <<= baseDirectory(_ / "govuk_template_play"),
  sourceGenerators in Compile <+= (state, templateKey in Compile, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates)
```

##Serve the govuk_template_play assets to the template

In order for the govuk_template.scala.html template to access assets we need to add them to the `playAssetsDirectories` and serve them off the url `/template/*file`. 

To do this we will create our own `AssetBuilder` which will keep reverse routing in our own templates cleaner than using the existing `route.Assets` to serve template assets.

Create the following file:
#####[app/controllers/Template.scala](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/app/controllers/Template.scala)
```scala
package controllers
object Template extends AssetBuilder
```

Add the following definition to your routes file:
#####[conf/routes](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/conf/routes)
```
GET    /template/*file    controllers.Template.at("/govuk_template_play/assets", file)
```

And finally we need to add the `govuk_template_play/assets` folder to the `playAssetsDirectories` property in our Build definition

#####[project/Build.scala](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/project/Build.scala)
```scala
...
lazy val main = play.Project(appName, appVersion, appDependencies).settings(
  playAssetsDirectories <+= baseDirectory { _ / "govuk_template_play" / "assets" },
...
```

##Init the submodule

Final thing we need to do is ensure the submodule is initialised. To do this we will create a script that can be hooked in as part of your upstart script.

Create the following sh script
#####[update-template.sh](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/update-template.sh)
```sh
echo "Updating govuk_template_play"
git submodule init
git submodule update
```

Now we will execute this script anytime play tries to compile the app. Make the following changes to your Build definition:

#####[project/Build.scala](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/project/Build.scala)
```scala
...
val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
val updateTemplateTask = updateTemplate := {
  "./update-template.sh".!
}

lazy val main = play.Project(appName, appVersion, appDependencies).settings(
  updateTemplateTask,
  compile <<= (compile in Compile) dependsOn updateTemplate,
...
```

Done! 

##Using the templates

A simple example of how to use the template can be found in [main.scala](https://github.com/michaeldfallen/play-govuk-demo/blob/play-212/app/views/main.scala.html)
