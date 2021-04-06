# Play ScalaJS sample projects and published mini-libraries

A collection of small published libraries with bits of boilerplate code for Play ScalaJS projects that be cherry-picked for specific infrastructure needs.

Also includes some samples projects showing how to use the libraries.

Generally, scalatags pages and scripts are used instead of Play view templates.

## Projects

Authentication with [Silhouette](https://www.silhouette.rocks):

- `play-scalajs-auth-silhouette` sample

[Rpc](https://en.wikipedia.org/wiki/Remote_procedure_call) with [autowire](https://github.com/lihaoyi/autowire):

- `play-scalajs-rpc-autowire` published at `"com.marcgrue" %%% "playing-rpc-autowire" % "0.2.0"`
- `play-scalajs-rpc-autowire-test-dep` - Sample using dependency
- `play-scalajs-rpc-autowire-test-inlcude` - Sample including library files

Rpc with [Sloth](https://github.com/cornerman/sloth):

- `play-scalajs-rpc-sloth` published at `"com.marcgrue" %%% "playing-rpc-sloth" % "0.2.1"`
- `play-scalajs-rpc-sloth-test-dep` - Sample using dependency
- `play-scalajs-rpc-sloth-test-inlcude` - Sample including library files

                                                                                     

Various utilities (for [scala.rx](https://github.com/lihaoyi/scala.rx), [scalatags](https://github.com/com-lihaoyi/scalatags)):

- `play-scalajs-utils` published at `"com.marcgrue" %%% "playing-utils" % "0.1.0"`


## Comments

Code of all published libraries are collected under the `playing` namespace:

- `playing.sloth`
- `playing.autowire`
- `playing.utils`


General stack:

- sbt 1.5.0
- Play 2.8.7
- Scala 2.13.5 + 2.12.13
- ScalaJS 1.5.1

