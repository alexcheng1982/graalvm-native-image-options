package io.vividcode.nativeimageoptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

object Formatter {
    private val objectMapper = ObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
        .registerModule(KotlinModule.Builder().build())

    fun format(options: List<Option>): String {
        return objectMapper.writeValueAsString(options)
    }
}