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
    * [x] DropPlayer
    * [ ] Effect
    * [ ] Item
    * [ ] MagicWand
    * [x] Mana
    * [ ] ZireaelSword
* [ ] Reimplement DebugStick - Current version is ugly and not optimal
* [ ] Review logging

### Code Cleanup

* [ ] Drop
    * Export items and effects to other classes 
* [ ] Add points to the player if opponent dies from burning, suffocating, etc.
* [ ] Display resources (mana, arrows, etc.) on the belt
* [ ] Add resources (mana, soil, arrows)
* [x] Use resources while casting a spell
* [ ] Regenerate resources
* [ ] Implement cool-down
* [x] Earth Wand generates resource
* [ ] Rename items in game

## Rules

* Each class has a constructor with logger message
* Logger
    * Fine - Debug information
    * Finer - Method entry points
    * Finest - Cycle-iteration

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

* The methods for checking the item like Item.isItemInMainHand should be checking material, name and lore
* Why Mana cannot be static? Why Item cannot be static?
* Mana.equip and Mana.interact might not be necessary if Drop interface and Item.interact are defined differently. 
* Mana and items will be properties of DropPlayer. They will have limits based on the played.
* Bug: When charging green mana, if using ground beneath the player, 3 green are added.
