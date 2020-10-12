# DROP

Diablo Rip-Off Project

Purpose of this project is to implement 1st/3rd-person RPG within Minecraft game
engine. The original idea was inspired by the loot and ability game mechanics in
the iconic Diablo series. During the development, the focus switched in favour
of PvP genre.

Features:

* Different weapons which grant special abilities
* Versus mode
* Co-op mode

## To Do

* [ ] Code Cleanup
    * [x] DebugStick
    * [ ] Drop
    * [ ] DropPlayer
    * [x] Effect
    * [x] Item
    * [ ] MagicWand
    * [x] ZireaelSword
* [ ] Reimplement DebugStick - Current version is ugly and not optimal
* [ ] Review logging
    * Fine - Method entry points
    * Finer - Method debug information
    * Finest - Method cycle-iteration debug information

### Code Cleanup

* [ ] Drop
    * Export items and effects to other classes 
* [ ] Add points to the player if opponent dies from burning, suffocating, etc.
* [ ] Display resources (mana, arrows, etc.) on the belt
* [ ] Add resources (mana, soil, arrows)
* [ ] Use resources while casting a spell
* [ ] Regenerate resources
* [ ] Implement cooldown
* [ ] Earth Wand generates resource
* [ ] Rename items in game

## Structure

```text
Drop
├─ DropPlayer
├─ Effect
└─ Item
   ├─ DebugStick
   ├─ MagicWand
   └─ ZireaelSword
```
