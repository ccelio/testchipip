package testchipip

import chisel3.{Module, Driver}
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.util.{HasGeneratorUtilities, ParsedInputNames}
import java.io.File

trait GeneratorApp extends App with HasGeneratorUtilities {
  lazy val names = ParsedInputNames(
    targetDir = args(0),
    topModuleProject = args(1),
    topModuleClass = args(2),
    configProject = args(3),
    configs = args(4))

  lazy val config = getConfig(names)
  lazy val world = config.toInstance
  lazy val params = Parameters.root(world)
  lazy val circuit = Driver.elaborate(() =>
      Class.forName(names.fullTopModuleClass)
        .getConstructor(classOf[Parameters])
        .newInstance(params)
        .asInstanceOf[Module])

  lazy val longName = names.topModuleProject + "." +
                 names.topModuleClass + "." +
                 names.configs

  def generateFirrtl =
    Driver.dumpFirrtl(circuit,
      Some(new File(names.targetDir, s"$longName.fir")))
}

object Generator extends GeneratorApp {
  generateFirrtl
}
