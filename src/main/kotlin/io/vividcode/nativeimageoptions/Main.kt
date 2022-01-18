package io.vividcode.nativeimageoptions

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.isRegularFile
import kotlin.io.path.nameWithoutExtension

private fun writeFile(options: List<Option>, targetFile: Path) {
    val output = Formatter.format(options)
    Files.writeString(
        targetFile,
        output,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    )
}

fun main() {
    val output = Paths.get(".", "options-output")
    if (!Files.exists(output)) {
        Files.createDirectories(output)
    }
    Files.walk(Paths.get(".", "options-input"), 1)
        .filter { it.isRegularFile() }
        .forEach { path ->
            val version = path.nameWithoutExtension
            val allOptions = Parser.parse(path)
            writeFile(allOptions, output.resolve("$version.json"))
        }
}