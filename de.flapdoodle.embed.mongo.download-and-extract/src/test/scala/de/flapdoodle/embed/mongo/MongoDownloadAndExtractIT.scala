package de.flapdoodle.embed.mongo

import java.io.File
import java.nio.file.{Files, Path, Paths => NioPaths}

import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.distribution.Distribution
import org.apache.commons.io.FileUtils.deleteDirectory
import org.specs2.matcher.{FileMatchers, Matchers}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class MongoDownloadAndExtractIT extends SpecWithJUnit
                                   with Matchers
                                   with FileMatchers {

    "MongoDownloadAndExtract" should {

        "store download-and-extract cache in custom location according to input db version" in new PackagedCtx {

            val dbVersion = "3.3.1"
            val osPrefix = getOsPrefixFrom(dbVersion)

            withTempDir { tempDir =>

                runJava(mongoDownloadAndExtractDeployable, tempDir, s"$dbVersion") == Success

                findDbVersionDir(tempDir, dbVersion, osPrefix) must beRight[File]
            }
        }

        "download-and-extract artifact - check executable" in new ExecutableCtx {

            val dbVersion = "3.2.0"

            withTempDir { tempDir =>

                runJava(mongoDownloadAndExtractDeployable, tempDir, s"$dbVersion") == Success

                val extractedExecutableFolderName = getExtractedExecutableFolderName(dbVersion)

                val extractedExecutable = findExecutable(tempDir,
                    dbVersion,
                    extractedExecutableFolderName)

                run(extractedExecutable.right.get.getPath, "--version") == Success
            }
        }
    }

    trait ExecutableCtx extends PackagedCtx {

        def findExecutable(basedir: String,
                           dbVersion: String,
                           extractedExecutableFolderName: String): Either[String, File] = {

            installersContainerFolder(basedir, extractedExecutableFolderName)

        }

        def getExtractedExecutableFolderName(dbVersion: String): String = {
            val versionEnum = Version.valueOf( s"V${dbVersion.replace('.', '_')}" )
            val distribution = Distribution.detectFor(versionEnum)
            distribution.getPlatform.name + "-" + distribution.getBitsize.name + "--" + distribution.getVersion.asInDownloadPath
        }
    }

    trait PackagedCtx extends Ctx {

        def findDbVersionDir(basedir: String,
                             dbVersion: String,
                             osPrefix: String): Either[String, File] = {

            installersContainerFolder(basedir, osPrefix)
                    .right
                    .map(retrieveInstallationFile(_, dbVersion))
                    .joinRight
        }

        private def retrieveInstallationFile(installationFile: File,
                                             dbVersion: String): Either[String, File] = {

            if(installationFile.getName.contains(dbVersion)) {
                Right(installationFile)
            }else {
                Left(s"Installation file (${installationFile.getAbsolutePath}) does not match version ($dbVersion). ")
            }
        }

        def installersContainerFolder(basedir: String,
                                      osPrefix: String): Either[String, File] = {

            osSpecificChildFolder(extractedInstallersFolder(basedir, osPrefix))
        }

        private def osSpecificChildFolder(downloadFolder: File): Either[String, File] = {

            downloadFolder
                    .listFiles()
                    .toList
                    .headOption
                    .toRight(s"expected a file under ${downloadFolder.getAbsolutePath} but got none")
        }
    }

    trait Ctx extends Scope {

        val Success = 0

        def withTempDir[T](f: String => T): T = {

            val tempDir = Files.createTempDirectory("embed-mongo-test").toFile

            try {

                f(tempDir.getAbsolutePath)
            } finally {
                deleteDirectory(tempDir)
            }
        }

        def runJava(runnable: Path,
                    args: String*): Int = {

            run(executableName = "java",
                "-jar" +: runnable.toAbsolutePath.toString +: args :_*)
        }

        def run(executableName: String,
                args: String*): Int = {

            import sys.process._
            (executableName +: args).mkString(" ").!
        }

        def extractedInstallersFolder(basedir: String,
                                      osPrefix: String): File = {
            new File(basedir, osPrefix)
        }

        def getOsPrefixFrom(version: String): String = {

            val paths = new Paths(Command.MongoD)
            val versionEnum = Version.valueOf( s"V${version.replace('.', '_')}" )
            new File(paths.getPath(Distribution.detectFor(versionEnum))).getParent
        }

        def mongoDownloadAndExtractDeployable: Path = {

            getMaybePath.getOrElse( throw new RuntimeException (
                """
                  |!!! ERROR !!! jar-ball artifact with name wix-embedded-mongo-download-and-extract-some-version-jar-with-dependencies.jar
                  |cannot be found. Please verify the following:
                  |1. The project is built and packaged using Maven: '$$ mvn package' should do the trick
                  |2. Your working directory is set to the module directory ($$MODULE_DIR$$)
                """.stripMargin)
            )
        }

        private def getMaybePath: Option[Path] = {

            val targetDir = NioPaths.get("./target")
            if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {

                Files
                        .newDirectoryStream(targetDir)
                        .asScala
                        .filter(Files.isRegularFile(_))
                        .find(_.toString.endsWith("jar-with-dependencies.jar"))
            } else
                None
        }
    }

}
