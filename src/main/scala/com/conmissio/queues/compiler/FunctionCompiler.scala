package com.conmissio.queues.compiler

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

object FunctionCompiler {

   def toClass(classBody: String, id: String): Either[Function[String, String], FunctionCompilerError] = {
     try {
       val javaMirror: universe.Mirror = universe.runtimeMirror(getClass.getClassLoader)
       val toolBox: ToolBox[universe.type] = javaMirror.mkToolBox()
       val clazz = toolBox.compile(toolBox.parse(wrapInClass(classBody)))().asInstanceOf[Class[_]]
       val clazzConstructor = clazz.getDeclaredConstructors()(0)
       val instance = clazzConstructor.newInstance(id)
       Left (instance.asInstanceOf[Function[String, String]])
     } catch {
       case e: Exception => Right(new FunctionCompilerError("Couldn't compile Function for id: " + id, e))
     }
  }

  private def wrapInClass(classBody: String): String = {
    "class ClassFunction(val accountId: String) extends Function[String, String] {\n "+
      classBody + "\n}\n scala.reflect.classTag[ClassFunction].runtimeClass"
  }
}

