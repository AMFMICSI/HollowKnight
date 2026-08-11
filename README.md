# Hollow Knight Clone

A 2D action-platformer inspired by *Hollow Knight*, built with **Java & LibGDX**
and following strict **Model-View-Controller (MVC) architecture**.

**Sharif University of Technology** — Faculty of Computer Engineering
Advanced Programming Course (Spring 2025)
**Author:** Fatemeh Mostafavi
- Instructor: Dr. Mohammad Amin Fazli
- Teaching Assistant: Amirhossein Mirzaei
- Graphics TA: Hamed Alinzhad
- Graphics Designers: Hamed Alinzhad, Shahab Ahmadloo, Sepehr Kardel

## Prerequisites
- Java JDK 17+
- Gradle (wrapper included)

## Run
```bash
git clone <repo-url>
cd HollowKnight
./gradlew run
```

## Architecture
Pure MVC with complete separation of concerns — the Model layer has **zero graphical imports**:

```
┌──────────────────────┐
│     Controller       │  ← Intercepts raw input
│   (InputProcessor)   │
└──────────┬───────────┘
│ Mutates state
▼
┌──────────────────────┐                  ┌──────────────────────┐
│       Model          │ ◄──── Pulls ──── │        View          │
│  (Physics, State,    │       State      │  (Render, Audio,     │
│   Game Logic)        │                  │   UI, Particles)     │
└──────────────────────┘                  └──────────────────────┘
```

## Controls
| Key | Action |
|-----|--------|
| Arrow Keys | Move left / right |
| Z | Jump / Double Jump |
| X | Attack with Nail |
| C | Dash |
| A (hold) | Focus — heal HP |
| Down + X (mid-air) | Pogo bounce |
| I | Open Inventory |
| ESC | Pause |

## Features

### Movement & Platforming
- Variable jump height (hold longer to jump higher)
- Double jump with dedicated animation
- Wall slide (Mantis Claw) on vertical surfaces
- Pogo bouncing — downward slash on spikes/enemies resets dash & jump
- Smooth collision resolution with no edge-snagging

### Combat & Spells
- **Nail** — melee slash in 4 directions with slash VFX
- **Vengeful Spirit** — horizontal magic projectile (pierces enemies)
- **Howling Wraiths** — aerial AoE burst dealing 3 rapid ticks
- **Soul System** — gain 11 soul per Nail hit, spend 33 to cast spells or heal
- **Knockback** on both player and enemies

### Enemies & AI
| Enemy | Type | Behavior |
|-------|------|----------|
| Crawlid | Ground | Walks, turns at walls/cliffs |
| HuskHornhead | Ground | Patrols, detects knight, charges at high speed |
| CrystalHunter | Flying | Flies through solids, shoots crystal projectile |
| CrystalGuardian | Stationary | Fires laser, then enrages and charges |

- Dead enemies leave visible corpses on the ground
- Enemies respawn when knight moves far enough away

### Boss: False Knight
- 5 attack patterns: Mace Slam, Charge Run, Offensive Leap, Defensive Leap, Power Slam
- Distance-based AI with randomization and anti-spam
- Stun phase at 50% HP — armor breaks, inner creature exposed
- Phase 2: increased speed, new Power Slam shockwave attack

### Charms & Inventory
| Charm | Effect | Notch Cost |
|-------|--------|------------|
| Soul Catcher | More soul per hit | 1 |
| Dashmaster | Shorter dash cooldown | 1 |
| Unbreakable Strength | Increased nail damage | 1 |
| Quick Slash | Faster attack speed | 1 |
| Quick Focus | Faster healing | 1 |
| Heavy Blow | Increased knockback | 1 |
| Sharp Shadow | Dash through enemies + 20% longer dash | 2 |
| Void Heart | +50% spell damage, black spell VFX | 2 |

- 3 notch limit — manage your loadout in the Inventory menu

### Menus
- **Main Menu** — New Game, Load Game (4 slots), Settings, Guide, Achievements
- **Pause Menu** — Continue, Save & Quit, Settings, Cheat Codes
- **Inventory** — Equip/unequip charms with notch tracking
- **Guide** — Dynamic keybindings, ability descriptions, cheat code list
- **Settings** — Volume, SFX, brightness, key rebinding, language switch

### Cheat Codes
| Code | Effect |
|------|--------|
| Ctrl + B | Teleport to boss arena |
| Ctrl + N | Noclip / fly mode |
| Ctrl + H | Emergency heal |
| Ctrl + R | Refill soul |
| Ctrl + G | God mode |

### Save System
- 4 save slots with JSON serialization
- SQLite database as secure backup
- Saves: position, HP, soul, equipped charms, achievements, play time

### Audio & Visuals
- Dynamic area-based BGM with crossfade transitions
- Event SFX: nail slash, damage, soul gain, focus
- Screen shake on hits and boss attacks
- Invincibility blink effect after taking damage
- Particle effects (butterflies, etc.)

### Localization
- English (default) & French
- Dynamic switching from Settings menu

## Achievements
| Achievement | Condition |
|-------------|-----------|
| Completion | Finish the game |
| Speedrun | Finish in under 5 minutes |
| True Hunter | Kill every enemy type |
| Defeat False Knight | Beat the boss |
| Airborne | 3 consecutive pogo bounces without landing |
