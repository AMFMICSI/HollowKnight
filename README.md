
# ⚔️ Hollow Knight: Project Hallownest

A pure Java and LibGDX recreation of the critically acclaimed game **Hollow Knight**, architected strictly on the **MVC (Model-View-Controller) Design Pattern**. This project was developed as part of the Advanced Programming course at **Sharif University of Technology**.

---

## 🗺️ Table of Contents
- [Architecture & Design Patterns](#-architecture--design-patterns)
- [Core Gameplay Features](#-core-gameplay-features)
- [Advanced AI & Boss Battles](#-advanced-ai--boss-battles)
- [Technical Systems](#-technical-systems)
- [Project Directory Tree](#-project-directory-tree)
- [How to Run](#-how-to-run)

---

## 🏛️ Architecture & Design Patterns

The hallmark of this project is its uncompromising adherence to a **Pure MVC Architecture**, ensuring absolute separation of concerns.

```text
                  ┌───────────────────────────────┐
                  │          Controller           │
                  │  (InputProcessor & Cheats)    │
                  └───────────────┬───────────────┘
                                  │
                                  │ Updates State
                                  ▼
┌───────────────────────────────┐           Polling State          ┌───────────────────────────────┐
│             Model             │◄─────────────────────────────────┤             View              │
│ (Pure Math, Physics & States) │                                  │ (LibGDX Batch, BGM & FX)      │
└───────────────────────────────┘                                  └───────────────────────────────┘


* **The Model (`src.main.model`)**: Contains the absolute mathematical, spatial, and physical reality of the game. It is **100% agnostic of graphics or frameworks**. Class models like `Knight`, `Enemy`, and `Zote` track ticks, velocities, bounds (`Rectangle`), and state machines (`Enum`) without importing a single rendering tool or asset.
* **The View (`src.main.view`)**: Governs spatial asset processing, audio systems, and active screen lifecycles. Utilizing dedicated rendering pipelines under `view.renderer` (e.g., `KnightRenderer`, `ZoteRenderer`), it safely monitors the Model layer via clean polling interfaces, drawing frames based on current ticks.
* **The Controller (`src.main.controller`)**: Implements LibGDX's `InputProcessor`. It intercepts raw hardware interrupts (keyboard/mouse) and translates player intent into direct mutation calls on the Model layer, preserving architectural encapsulation.



## 🕹️ Core Gameplay Features

* **Vessel Physics**: Advanced platforming mechanics with strict gravity models, jump height decay, wall sliding, wall jumping, and custom-tuned dash actions.
* **Pogo Combat Mechanics**: Allows the player to strike downwards on enemies or environmental hazards (`Spikes`), triggering a vertical bounce effect (`doPogoBounce`) and recharging spent abilities.
* **Soul & Health Infrastructure**: Comprehensive resources monitoring system. Striking opponents rewards Soul, which can be spent dynamically to initiate a channeled Focus/Heal circle or to cast potent magic spells.
* **Spell Framework**: Includes two iconic high-damage abilities:
    * `Vengeful Spirit`: An horizontal projectile launch.
    * `Howling Wraiths`: A powerful vertical, upwards area-of-effect (AoE) blast.
* **Charm Integration System**: Fully operational Inventory layout where players manage Charms under strict notch constraints (`maxNotches = 3`). Real-time computational adjustments handled by `CharmEffectCalculator` for:
    * *Soul Catcher* (Increases soul-per-hit yield).
    * *Dashmaster* (Reduces dash cooldown).
    * *Strength* (Multiplies raw nail damage output).

---

## 🧠 Advanced AI & Boss Battles

* **Basic Threats**: Diverse entity loops ranging from standard ground patrollers (`Crawlid`, `Husk Hornhead`) to airborne projectile snipers (`Crystal Hunter`).
* **Crystal Guardian**: An advanced threat matrix using raycast laser calculations, long-range targeting, and a dynamic "Enraged" speed phase when falling below health thresholds.
* **Zote the Mighty (NPC Interaction)**: A proximity-aware entity with custom multi-layered dialog trees. Striking Zote triggers an integrated event flag causing him to aggressively retailiate.
* **The False Knight (Epic Boss Fight)**: A robust multi-phase combat cycle incorporating dynamic jump strikes (`LEAP`), shockwave generation, and an automated structural **Stun Phase** where his armor breaks down, exposing his weak point for strategic hits.

---

## ⚙️ Technical Systems

* **State Teleportation Physics**: Solves infinite-damage lock issues on hazard blocks. Colliding with `Spike` elements applies immediate base damage and safely teleports the player back to their `lastSafePosition` recorded on a solid floor block.
* **JSON Persistence (Save/Load)**: Implements standard file I/O operations via `DatabaseManager`. Players can choose multiple distinct game slots. Serialized parameters precisely snapshot Knight coordinates, current health masks, inventory arrays, active charms, and unlocked progress.
* **Dynamic Sound Synthesis (Area BGM)**: Features cross-faded audio blending algorithms. Moving between spatial grid sectors smoothly shifts background soundtracks (e.g., from *Greenpath* to *Crystal Peaks*) without jarring sound overlapping.
* **Dynamic Localization**: Real-time multi-lingual support powered by `TranslationManager`. Seamlessly switches fonts, labels, and text maps between English and alternative languages directly from the runtime settings dashboard.
* **Developer Cheat System**: Integrated sandbox toggles featuring:
    * *God Mode*: Complete damage mitigation.
    * *Noclip / Flight Mode*: Completely ignores physical block collisions, enabling rapid traversal across map coordinates.
    * Instant Soul & Health restoration macro strings.

---

## 📁 Project Directory Tree

```text
src/main/
├── Main.java                        # Primary LibGDX Lifecycle Delegate
├── controller/
│   └── GameController.java          # Hardware Input Processing & Decoupling
├── model/
│   ├── Game.java                    # Heart of the Simulation Universe
│   ├── data/                        # JSON Serialization & Keyboard Mapping Layouts
│   ├── physics/                     # Rigid Body Collision Resolution Pipelines
│   ├── enviroment/                  # Map Loading Models & Hazards
│   └── entity/                      # Structural Math & Game Logic Entities
│       ├── knight/                  # Player Attributes, Health, and Cheat Systems
│       ├── spell/                   # Magic & AoE Spells Logic
│       ├── charm/                   # Modifier Calculus Systems
│       └── enemy/                   # Basic AI Loops & Concrete Boss Matrices
└── view/
    ├── screens/                     # Menu UI Screens & Active Gameplay Views
    ├── manager/                     # Cross-Layer Asset, Sound, & Achievement Registries
    ├── config/                      # Localization Databases & Game Context Settings
    └── renderer/                    # Pure Visual Render Modules (LibGDX Context Only)

```

---

## 🚀 How to Run

1. Make sure you have Java JDK 17 or higher installed.
2. Clone the repository:
3. Open the project in your favorite IDE (IntelliJ IDEA / Eclipse).
4. Let Gradle resolve the **LibGDX dependencies**.
5. Locate `src/main/Main.java` and hit **Run**!
