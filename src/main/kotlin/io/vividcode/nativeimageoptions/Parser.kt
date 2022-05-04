package io.vividcode.nativeimageoptions

import java.nio.file.Files
import java.nio.file.Path

enum class OptionType {
    HOSTED,
    RUNTIME,
}

enum class OptionDataType(private val converter: (String) -> Any) {
    BOOLEAN({ it.toBoolean() }),
    LONG({ it.toLong() }),
    DOUBLE({ it.toDouble() }),
    STRING({ it });

    fun convert(input: String) = converter(input)
}

data class Option(
    val type: OptionType,
    val name: String,
    val dataType: OptionDataType,
    val defaultValue: Any,
    val description: String = ""
)

object Parser {
    fun parse(path: Path): List<Option> {
        val input = Files.readString(path)
        val size = input.length
        var index = 0
        var optionType = OptionType.HOSTED
        var optionDataType = OptionDataType.STRING
        var optionName = ""
        var optionDefaultValue = ""
        var optionDescription = ""
        var inOptionName = false
        var inOptionDefaultValue = false
        var inOptionDescription = false
        val buffer = StringBuilder()
        val options = mutableListOf<Option>()

        fun finishOptionName() {
            inOptionName = false
            optionName = buffer.toString().trim()
            buffer.clear()
        }

        fun finishOptionDefaultValue() {
            inOptionDefaultValue = false
            optionDefaultValue = buffer.toString().trim()
            optionDefaultValue = optionDefaultValue.removeSurrounding("\"")
            buffer.clear()
        }

        fun finishOptionDescription() {
            inOptionDescription = false
            optionDescription = buffer.toString()
                .replace("\r", " ")
                .replace("\n", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            buffer.clear()
        }

        fun finishOption() {
            if (inOptionDescription) {
                finishOptionDescription()
                val defaultPattern = "Default: "
                val defaultIndex = optionDescription.lastIndexOf(defaultPattern)
                if (defaultIndex != -1) {
                    val defaultValueStart = defaultIndex + defaultPattern.length
                    val defaultValueEnd = optionDescription.indexOf(" ", defaultValueStart).let {
                        if (it != -1) it else optionDescription.length
                    }
                    val defaultValue =
                        optionDescription.substring(defaultValueStart, defaultValueEnd).trim()
                    optionDefaultValue = if (optionDataType == OptionDataType.BOOLEAN) {
                        when (defaultValue) {
                            "+" -> "true"
                            "-" -> "false"
                            else -> ""
                        }
                    } else {
                        defaultValue
                    }
                    optionDescription = optionDescription.substring(0, defaultIndex)
                }
                optionDescription = optionDescription.replace("[Extra help available]", "")
                if (optionDefaultValue == "..." || optionDefaultValue == "\"\"") {
                    optionDefaultValue = ""
                }
                optionDescription = optionDescription.replace(Regex("""\s{2,}"""), " ")
                optionDefaultValue.toLongOrNull()?.run {
                    optionDataType = OptionDataType.LONG
                } ?: optionDefaultValue.toDoubleOrNull()?.run {
                    optionDataType = OptionDataType.DOUBLE
                }
                options.add(
                    Option(
                        optionType,
                        optionName,
                        optionDataType,
                        optionDataType.convert(optionDefaultValue),
                        optionDescription.trim()
                    )
                )
            }
        }

        while (index < size) {
            when {
                input[index] == '-' -> {
                    when (input[index + 1]) {
                        'H', 'R' -> {
                            finishOption()
                            inOptionName = true
                            if (input[index + 1] == 'R') {
                                optionType = OptionType.RUNTIME
                            }
                            if (input[index + 3] == '?') {
                                optionDataType = OptionDataType.BOOLEAN
                                index++
                            } else {
                                optionDataType = OptionDataType.STRING
                            }
                            index += 2
                        }
                        else -> {
                            buffer.append(input[index])
                        }
                    }
                }
                input[index] == '=' -> {
                    if (inOptionName) {
                        inOptionDefaultValue = true
                        inOptionName = false
                        optionName = buffer.toString()
                        buffer.clear()
                    }
                }
                input[index].isWhitespace() -> {
                    if (inOptionName || inOptionDefaultValue || inOptionDescription) {
                        buffer.append(input[index])
                    }
                    if (inOptionName) {
                        finishOptionName()
                        inOptionDescription = true
                    } else if (inOptionDefaultValue) {
                        finishOptionDefaultValue()
                        inOptionDescription = true
                    }
                }
                else -> {
                    if (inOptionName || inOptionDefaultValue || inOptionDescription) {
                        buffer.append(input[index])
                    }
                }
            }
            index++
        }

        finishOption()

        return options
    }
}
