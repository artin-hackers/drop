# DROP

Diablo Rip-Off Project

Purpose of this project is to implement 1st/3rd-person RPG within Minecraft game
engine. The original inspiration are the loot and ability game mechanics in the
iconic Diablo series. During the development, the focus switched in favour of
PvP genre.

Features:

* Different weapons which grant special abilities
* Versus mode
* Co-op mode

## To Do

* [ ] Code Cleanup
    * [ ] DebugStick
    * [ ] Drop
    * [ ] DropPlayer
    * [ ] Effect - In progress: addMana and removeMana share code, maybe handle mana colours as an array
    * [x] Item
    * [ ] MagicWand
    * [ ] ZireaelSword
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

* Item
    * Superclass/Template for game items (weapons, armour, resources)
    * Abstract class for game plugin which allows equipping various items

## Notes

* The method Effect.removeMana() considers meta data when checking the amount of mana but removal works with the Material only. It may happen that using mana removes actual material not mana (material with specific meta data).
* The methods for checking the item like Item.isItemInMainHand() should be checking material, name and lore
