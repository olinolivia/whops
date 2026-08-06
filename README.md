# whops
This mod aims to serve as a dependency for maps and servers,
covering the most essential facets of parkour on the client
for responsiveness and accuracy out of reach for server-side
tools like plugins and datapacks.

## Features

- Backwards compatibility options for movement, aka.
  changing mechanics to work as they did in 1.8 and 1.12
- A per-player checkpoint system, controllable with
  commands and with an instant, client-authoritative
  return keybind.

## Usage

### Legacy movement
While legacy movement options are accessible normally as
standard gamerules, it is often more convenient to use the
provided version presets, found as functions under
`/function whops:legacy_preset/<version>`.
Only 1.8.9 and 1.12.2 are currently covered, with latest
serving as a quick reset to default mechanics.

### Checkpoint system
The checkpoint system can be accessed from the `/checkpoint`
command, with `/checkpoint save` storing a position and
rotation (leave blank for current) to the calling player's
checkpoint slot. You may return to this checkpoint by
either calling `/checkpoint load` or by pressing the `return`
keybind (configurable in controls)

---

More features are planned, and the mod is always open to
suggestions in the form of issues!

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
