package io.github.susimsek.springbootgraalvmnativeexample.config

import io.github.susimsek.springbootgraalvmnativeexample.dto.Violation
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeHint
import org.springframework.lang.Nullable

class NativeConfig {

  class AppNativeRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, @Nullable classLoader: ClassLoader?) {
      hints.reflection().registerType(Violation::class.java) { builder: TypeHint.Builder ->
        builder.withMembers()
      }
    }
  }
}
