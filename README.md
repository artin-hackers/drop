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

* `createLobby`
* `startMatch`
* `endMatch`
* `showScore`
* `clearArea`
* `dropInventory`
* `setLevel`

### Weapons and Mana

* Black
  * "Sword of the Damned" - Summon the dead to aid you
  * ???
* Blue
  * "Axe of Frost" - Freeze the enemy and the ground around them
  * "Zireael's Sword" - Blink forward
* Green
  * "Zdenek's Wand" - Shape the ground around you
  * ???
* Red
  * "Filip's Axe" - Launch fireballs
  * "Bow of Fire" - Set the ground ablaze
* White
  * "Trident" - Heal and make shock-waves
  * ???

### Structure

```text
Drop
├─ Arena
├─ DropPlayer
├─ Effect
└─ Item
   ├─ Resource
   │  └─ Mana
   └─ Weapon
      ├─ Bow
      ├─ DebugStick
      ├─ FilipAxe
      ├─ InvulnerabilityTrident
      ├─ MagicWand
      ├─ SwordOfTheDamned
      ├─ ZdenekWand
      └─ ZireaelSword
```

## Redesign 2021 - To Do

### Features

* [ ] New weapon "Trident"
  * [x] New item
  * [ ] Rename to "Trident", "TridentOfHealh"
  * [ ] One of following
    * [ ] OnInteract(LMB), if mob/player is hit, use mana, add health
    * [ ] OnInteract(RMB), if mob/player is hit, use mana, health to max
    * [ ] If equipped, regenerate health, rate 1/10s, use mana
    * [ ] If ??? make player invulnerable
* [ ] New weapon "Axe of Frost"
* [ ] Levelling
* [ ] Random class upon respawn

### Bugs

* [ ] ...

### Refactoring

* [ ] Update weapon names
* [ ] Every class has its own logger

#### Classes and Methods

* [ ] Arena
  * [x] Properties
  * [x] Arena
  * [x] getArenaLocation
  * [x] getLobbyLocation
  * [x] setArenaLocation
  * [x] buildLobby
  * [ ] buildArena
  * [x] buildPatch
  * [x] getRandomInt
* [ ] Bow
* [ ] DebugStick
* [ ] Drop
  * [ ] Properties
  * [ ] OnEnable
  * [ ] OnCommand
  * [ ] ...
  * [x] clearArea
  * [x] buildLobby
* [ ] DropPlayer
* [ ] Effect
* [ ] FilipAxe
* [ ] InvulrneabilityTrident
* [ ] Item
* [ ] Mana
* [ ] SwordOfTheDamned
* [ ] ZdenekWand
* [ ] ZireaelSword

### Notes

* Armour?
* Capture the Flag mode?
* King of the Hill mode?
* Crossbow weapon?
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

Dead Code

```java
//    @EventHandler
//    public void onHit(EntityDamageByEntityEvent event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
//                event.setDamage(0);
//            }
//        }
//    }

    // @EventHandler
    // public void onDamage(EntityDamageByEntityEvent event) {
    //     LOGGER.warning("Drop.onDamage");
    //     if (event.getEntity() instanceof Player) {
    //         if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
    //             event.setDamage(0);
    //         }
    //     }
    // }

//    @EventHandler
//    public void onPlayerDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
//                event.setCancelled(true);
//            }
//        }
//    }
/*
public Location putInView(CommandSender sender, int distance) {
        if (sender instanceof Player) {
        Player player = (Player) sender;
        Location location = player.getLocation().clone();
        directions direction = getDirection(sender);
        if (direction == directions.NORTH) {
        location.add(0, 0, -distance);
        } else if (direction == directions.EAST) {
        location.add(distance, 0, 0);
        } else if (direction == directions.SOUTH) {
        location.add(0, 0, distance);
        } else if (direction == directions.WEST) {
        location.add(-distance, 0, 0);
        } else {
        getLogger().info("Error: putInView()");
        return null;
        }
        return location;
        }
        return null;
        }

private boolean Hulkazivota2(CommandSender sender) {
        if (sender instanceof Player) {
        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        for (int x = -2; x <= 2; x++) {
        for (int y = 0; y <= 2; y++) {
        for (int z = -2; z <= 2; z++) {
final Location wallBlock = new Location(
        player.getWorld(),
        playerLocation.getX() + x,
        playerLocation.getY() + y,
        playerLocation.getZ() + z);
        wallBlock.getBlock().setType(Material.AIR);
        }
        }
        }
        }
        return true;
        }

public directions getDirection(CommandSender sender) {
        if (sender instanceof Player) {
        Player player = (Player) sender;
        int rotation = Math.round(player.getLocation().getYaw() + 270) % 360;
        if (rotation >= 45 && rotation < 135) {
        return directions.NORTH;
        } else if (rotation >= 135 && rotation < 225) {
        return directions.EAST;
        } else if (rotation >= 225 && rotation < 315) {
        return directions.SOUTH;
        } else {
        return directions.WEST;
        }
        }
        return null;
        }

private enum directions {
  NORTH,
  EAST,
  SOUTH,
  WEST
}
*/
```
