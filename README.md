# GraalVM `native-image` Options

Parse options of GraalVM `native-image` to JSON data.

Visit [build-native-java-apps.cc](https://build-native-java-apps.cc/references/expert-options/) to see these options.
 
GraalVM versions supported:

* `22.2.0`
* `22.1.0`
* `21.3.0`
* `20.3.4`

Options are retrieved using `native-image --expert-options-all`.

Output content is saved into `options-input` directory.

JSON output is saved into `options-output` directory.

## Option Format

For each option:

| Property       | Description                                     |
|----------------|-------------------------------------------------|
| `type`         | Can be `HOSTED` or `RUNTIME`                    |
| `name`         | Name                                            |
| `dataType`     | Can be `BOOLEAN`, `LONG`, `DOUBLE`, or `STRING` |
| `defaultValue` | Default value                                   |
| `description`  | Description                                     |

See the example below.

```json
{
  "type": "HOSTED",
  "name": "AOTInline",
  "dataType": "BOOLEAN",
  "defaultValue": true,
  "description": "Perform method inlining in the AOT compiled native image."
}
```

## Use Options

Hosted options are specified using the prefix `-H:`, while runtimes options are specified using the
prefix `-R:`.

Boolean options are toggled using `+` or `-`, while other options are specified using `=`.

Examples:

| Option                         | Description             |
|--------------------------------|-------------------------|
| `-H:+AOTInline`                | Enabled hosted option   |
| `-R:-Inline`                   | Disabled runtime option |
| `-H:Class=Main`                | Hosted option           |
| `-R:MaximumHeapSizePercent=80` | Runtime option          |

