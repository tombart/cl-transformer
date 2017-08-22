package com.conmissio.compiler

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

object TransformerClassFactory {

   def create(classBody: String, accountId: String): Function[String, String] = {
     val javaMirror: universe.Mirror = universe.runtimeMirror(getClass.getClassLoader)
     val toolBox: ToolBox[universe.type] = javaMirror.mkToolBox()
     val clazz = toolBox.compile(toolBox.parse(wrapInClass(classBody)))().asInstanceOf[Class[_]]
     val clazzConstructor = clazz.getDeclaredConstructors()(0)
     val instance = clazzConstructor.newInstance(accountId)
     instance.asInstanceOf[Function[String, String]]
  }

  private def wrapInClass(classBody: String): String = {
    "class ClassFunction(val accountId: String) extends Function[String, String] {\n "+
      classBody + "\n}\n scala.reflect.classTag[ClassFunction].runtimeClass"
  }
}

