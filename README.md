# Minesweeper

A graphical implementation of the classic **Minesweeper** game built in **Java** using the **Processing** library and **Gradle**.

This project was originally developed as part of the **INFO1113 / COMP9003 Programming Technologies** coursework at the University of Sydney.

## Overview

The game recreates the core mechanics of Minesweeper on an **18 × 27 grid**. Mines are randomly distributed across the board, and the player must reveal all safe tiles without triggering a mine.

The implementation includes interactive tile revealing, mine detection, flagging, recursive empty-area expansion, a game timer, win/loss conditions, and animated mine explosions.

## Features

- **Random Mine Generation**
  - Mines are randomly placed when a new game starts.
  - The number of mines can be provided through a command-line argument.
  - The game defaults to **100 mines** when no valid argument is supplied.

- **Tile Interaction**
  - Left-click to reveal a hidden tile.
  - Hovering over a hidden tile highlights it.
  - Revealed tiles display the number of adjacent mines.
  - Empty tiles automatically reveal neighbouring tiles.

- **Flagging System**
  - Right-click a hidden tile to place a flag.
  - Right-click again to remove the flag.
  - Flagged tiles cannot be revealed until the flag is removed.

- **Mine Counter Logic**
  - Revealed tiles display the number of adjacent mines from **1–8**.
  - Numbers are displayed using different colours for easier identification.

- **Game Timer**
  - Tracks the number of seconds elapsed since the game started.
  - Stops automatically when the game ends.

- **Win and Loss Conditions**
  - The player wins after revealing all non-mine tiles.
  - Clicking a mine ends the game and triggers the mine explosion sequence.

- **Animated Mine Explosions**
  - Mines use a multi-frame sprite animation.
  - Explosions occur progressively across the board rather than simultaneously.
  - Each explosion begins several frames after the previous explosion.

- **Restart**
  - Press **`R`** to restart the game with a newly generated mine layout.

## Technologies

- **Java**
- **Processing**
- **Gradle**

## Project Structure

```text
Minesweeper/
├── build.gradle
└── src/
    └── main/
        ├── java/
        │   └── minesweeper/
        │       └── App.java
        └── resources/
            └── minesweeper/
                ├── flag.png
                ├── mine0.png
                ├── mine1.png
                ├── ...
                ├── mine9.png
                ├── tile.png
                ├── tile1.png
                ├── tile2.png
                └── wall0.png
```

## Controls

| Input | Action |
|---|---|
| **Left Click** | Reveal a tile |
| **Right Click** | Flag or unflag a tile |
| **R** | Restart the game |

## Running the Project

### Prerequisites

Make sure you have Java installed on your system.

You can check your Java installation with:

```bash
java -version
```

### Run with Gradle

From the project root directory:

```bash
gradle run
```

The number of mines can also be supplied as a command-line argument if supported by the local Gradle configuration.

If no valid mine count is provided, the game uses the default value of **100 mines**.

## Game Logic

Each tile keeps track of whether it:

- contains a mine,
- has been revealed,
- has been flagged, and
- is adjacent to one or more mines.

When a safe tile is revealed, the game calculates the number of mines in its neighbouring cells. If no neighbouring mines are present, adjacent tiles are progressively revealed.

The game finishes when either all non-mine tiles have been revealed or the player reveals a mine.

## What I Practised

This project provided practical experience with:

- Object-oriented programming in Java
- Event-driven programming
- Mouse and keyboard input handling
- 2D grid and coordinate management
- Recursive/iterative game-state propagation
- Sprite-based animation
- Timing and frame-based logic
- Randomised game generation
- Dependency management with Gradle
- Building a complete interactive application using Processing

## Academic Context

Originally developed for the **INFO1113 / COMP9003 Assignment Warmup** at the **University of Sydney**.

The repository is maintained as part of my programming portfolio and documents my implementation and experience with Java application development.
