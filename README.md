# DROP

Diablo Rip-Off Project

Purpose of this project is to implement 1st/3rd-person RPG within Minecraft game
engine. The original inspiration are the loot and ability game mechanics in the
iconic Diablo series. During the development, the focus switched in favour of
the PvP genre.

## Documentation

### Features

* Versus mode
* Co-op mode
* Hero classes (colours)
* Different weapons which grant special abilities
* Levelling

### Commands

* `startMatch`
* `endMatch`
* `showScore`

### Weapons

* [ ] "Axe of Frost" (blue) - Freezes the enemy and the ground around them
* [x] "Filip's Axe" `FilipAxe` (red) - Melee weapon that can shoot fireballs
* [x] "Fire Bow" `Bow` (red) - Shoots arrows that set the ground ablaze
* [ ] Crossbow
* [x] "The Trident" `InvulnerabilityTrident` (white) - Heals the wielder and produces shock-waves
* [ ] "Sword of the Damned" (black) - Summons the dead to aid you
* [x] "Zdenek's Wand" "`ZdenekWand` (green) - Shapes the ground around you
* [x] "Zireael's Sword" `ZireaelSword` (blue) - Grants you the blink ability

### Mana

* Black
* Blue
* Green
* Red
* White

### Structure

```text
Drop
├─ DropPlayer
├─ Effect
└─ Item
   ├─ DebugStick
   ├─ MagicWand
   └─ ZireaelSword
```

## Redesign 2021 - To Do

### Features

* [ ] Implement the "Axe of Frost"

### Bugs

* [ ] ...

### Refactoring

* [ ] Update weapon names

### Notes

* Implement sword of the dead that summons zombies and skeletons, consumes black mana
* Random respawn position, safe area
* Callback trident, (consumes white mana?)
* Trident heals over time
* Trident pushes enemy back (consumes mana) and explosion when thrown
* Cooldown for red axe fireballs
* Consume red mana with bow
* Implement levelling (based on colour)
* Implement all weapon mode - it is a mess
* Implement single random weapon mode
* Implement hero classes based on the colour
* Implement proper coop mode
* Implement proper versus mode with result screen at the end
* Consider class Match which will take care of starting, running and ending of the match
* When starting a match, length in minutes can be specified via argument, e.g. `startMatch MINUTES`
* Player looses the whole inventory upon start/death, his items cannot be picked up
* Player's health is reset upon match start
* Hunger is disabled
* Player's level is reset upon match start (levels are not implemented yet) 
* Command `showScore` shows current score of the players to the requestor
* When new player connects, he is teleported to the arena
* Player equips with a random weapon upon respawn, maybe two
* Levels, based on colours
* Code Cleanup
* Reimplement DebugStick - Current version is ugly and not optimal
* Review logging
* Drop
    * Export items and effects to other classes
* Add points to the player if opponent dies from burning, suffocating, etc.
* Display resources (mana, arrows, etc.) on the belt
* Add resources (mana, soil, arrows)
* Use resources while casting a spell
* Regenerate resources
* Implement cool-down
* Earth Wand generates resource
* Rename items in game
* Each class has a constructor with logger message
* Logger
    * Fine - Debug information
    * Finer - Method entry points
    * Finest - Cycle-iteration
* Item
    * Superclass/Template for game items (weapons, armour, resources)
    * Abstract class for game plugin which allows equipping various items
* The methods for checking the item like Item.isItemInMainHand should be checking material, name and lore
* Why Mana cannot be static? Why Item cannot be static? Item is abstract, so requires override from subclases.
* Mana.equip and Mana.interact might not be necessary if Drop interface and Item.interact are defined differently.
* Mana and items will be properties of DropPlayer. They will have limits based on the played.
* Bug: When charging green mana, if using ground beneath the player, 3 green are added.
* One of the following:
    * ~~Register listener in Item, not in ZireaelSword, ...? onInteract will be available for all items~~
    * Remove onInteract from Item, it will have only isItemInMain hand, event handler will be in specific class if needed
* Implement Mana add/remove methods better
* There cannot be constructor for Drop class. Plugin would stop working.
* Mana could kill player if interacted with
* No idea how `Item.removeItems` can work with following code. It should be negated. - It is always false.
```text
if (itemStack.getType().equals(material)) {
    continue;
}
```
Minecraft messes up materials (Material.BLUE_DYE, Material.GREEN_DYE)
```text
[10:28:24] [Server thread/INFO]: Item.removeItems: material = AIR, displayName = Blue Mana
[10:28:24] [Server thread/INFO]: Item.removeItems: material = INK_SACK, displayName = Green Mana
```
* Some weapons grant speed boost, might be permanent or onRequest
* More actions, catch keyboard presses of [Q], [E], [Shift], ... Might not be possible
