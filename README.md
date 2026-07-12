# 🏛️ Hollow Knight - Pure MVC Implementation (Java & LibGDX)

A high-performance recreation of the *Hollow Knight* universe, engineered with a strict **Pure Model-View-Controller (MVC) Architecture** for the Advanced Programming course at Sharif University. [cite_start]This project enforces complete separation of concerns, ensuring core game physics are entirely independent of the presentation framework[cite: 364].

---

## 🏗️ Architecture Overview

[cite_start]The core paradigm relies on the absolute decoupling of state mathematics from the LibGDX rendering framework[cite: 365].

```text
 ┌──────────────────────────┐
 │        Controller        │ ── Intercepts Inputs
 └────────────┬─────────────┘
              │ Mutates State
              ▼
 ┌──────────────────────────┐                  ┌──────────────────────────┐
 │          Model           │ ◄─────────────── │           View           │
 │ (Pure Mathematical Engine)│   Pulls State    │ (LibGDX Render & Audio)  │
 └──────────────────────────┘                  └──────────────────────────┘

```

*
**Model (`src.main.model`):** Houses spatial reality (`Rectangle` bounds), rigid body physics, velocities, and numerical attributes. It contains **0% graphical imports**—making it entirely framework-agnostic.


*
**View (`src.main.view`):** Contains dedicated polling engines (`KnightRenderer`, `EnemyRenderer`) that extract atomic model data and coordinate rendering frames, particles, and multi-zone dynamic BGM transitions.


*
**Controller (`src.main.controller`):** Inherits LibGDX's `InputProcessor`. It maps raw keystrokes into context-aware semantic calls, preventing architectural leaks.



---

## 🎮 Core Features


**⚔️ Advanced Combat & Physics:** Pixel-perfect pogo jumping (downward hazard slashes reset dash/jump cooldowns with explosive recoil) , smooth wall-sliding, and a robust axis-separated collision resolution layout.



**👾 Tailored Boss & Enemy AI:** * *False Knight:* Comprehensive 5-state automation lifecycle featuring transition recovery timers, structural shockwaves, and exposed stagger phases.


**🎒 Comprehensive Gameplay Loop:** Real-time inventory slots with notch tracking (`Dashmaster`, `Soul Catcher`, `Void Heart`) , spellcasting projectiles , and dynamic UI management.



**💾 Infrastructure:** Full JSON serialization supporting 4 separate gameplay state tracking save slots , along with dynamic multi-language localization.
---

## 🚀 Run & Start

### Prerequisites
**Java JDK 17** or higher
**Gradle** (Wrapper included)
### Execution
1. Clone the repository:
```bash
git clone [https://github.com/your-username/hollow-knight-mvc.git](https://github.com/your-username/hollow-knight-mvc.git)
```
2. Run the application:
```bash
./gradlew run
